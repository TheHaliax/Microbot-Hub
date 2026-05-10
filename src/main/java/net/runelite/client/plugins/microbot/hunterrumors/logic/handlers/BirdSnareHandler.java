package net.runelite.client.plugins.microbot.hunterrumors.logic.handlers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.hunterrumors.HunterRumorsConfig;
import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;
import net.runelite.client.plugins.microbot.hunterrumors.logic.CompetitionTrapIds;
import net.runelite.client.plugins.microbot.hunterrumors.logic.TrapOwnershipTracker;
import net.runelite.client.plugins.microbot.hunterrumors.logic.catalog.HuntLocations;
import net.runelite.client.plugins.microbot.hunterrumors.logic.catalog.HuntMethodType;
import net.runelite.client.plugins.microbot.hunterrumors.logic.training.BirdTrainingBlacklistState;
import net.runelite.client.plugins.microbot.hunterrumors.logic.training.TrapPlacementPlanner;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class BirdSnareHandler implements HuntMethodHandler
{
    private static final int TRAP_QUERY_RADIUS_TILES = 20;
    private static final int WALK_RADIUS = 1;

    private static final long DEFAULT_ACTION_COOLDOWN_MS = 600;
    private static final long AFTER_WALK_COOLDOWN_MS = 900;
    private static final long AFTER_LAY_COOLDOWN_MS = 1200;
    private static final long INVENTORY_WAIT_TIMEOUT_MS = 6000;

    @Inject
    private TrapOwnershipTracker trapOwnershipTracker;

    private final BirdTrainingBlacklistState blacklistState = new BirdTrainingBlacklistState();

    @Getter
    private WorldPoint activeCenter;
    private int activePlacementIndex;

    private long nextActionAtMillis;
    private int inventoryCountBeforeInteract;
    private long inventoryWaitDeadlineMillis;
    private boolean waitingForInventoryChange;

    @Override
    public HuntMethodType type()
    {
        return HuntMethodType.BIRD_SNARE;
    }

    @Override
    public boolean step(RumourTarget target, HunterRumorsConfig config)
    {
        if (config == null || target == null)
        {
            return false;
        }

        long now = System.currentTimeMillis();
        if (now < nextActionAtMillis)
        {
            return false;
        }

        if (waitingForInventoryChange)
        {
            if (Rs2Inventory.count() != inventoryCountBeforeInteract)
            {
                waitingForInventoryChange = false;
                nextActionAtMillis = now + DEFAULT_ACTION_COOLDOWN_MS;
                return true;
            }
            if (now >= inventoryWaitDeadlineMillis)
            {
                waitingForInventoryChange = false;
                nextActionAtMillis = now + DEFAULT_ACTION_COOLDOWN_MS;
            }
            return true;
        }

        if (!Rs2Inventory.contains(ItemID.HUNTING_OJIBWAY_BIRD_SNARE))
        {
            Microbot.status = "Bird snare: need snares";
            return false;
        }

        WorldPoint center = resolveCenter(target, config, now);
        if (center == null)
        {
            Microbot.status = "Bird snare: no centers";
            return false;
        }
        activeCenter = center;

        if (tryHandleOwnedTraps(center, now))
        {
            return true;
        }

        if (tryPlaceNextSnare(config, center, now))
        {
            return true;
        }

        nextActionAtMillis = now + DEFAULT_ACTION_COOLDOWN_MS;
        return false;
    }

    private WorldPoint resolveCenter(RumourTarget target, HunterRumorsConfig config, long nowMillis)
    {
        List<WorldPoint> centers = HuntLocations.centers(HuntMethodType.BIRD_SNARE, target, config);
        if (centers == null || centers.isEmpty())
        {
            return null;
        }

        int maxNearbyPlayers = Math.max(0, config.trainingMaxNearbyPlayers());
        int radius = Math.max(1, config.trainingNearbyDistance());

        int start = ThreadLocalRandom.current().nextInt(centers.size());
        for (int probe = 0; probe < centers.size(); probe++)
        {
            int idx = (start + probe) % centers.size();
            WorldPoint c = centers.get(idx);
            if (c == null)
            {
                continue;
            }

            if (!blacklistState.isAvailable(c, nowMillis))
            {
                continue;
            }

            int others = countOtherPlayersNearby(c, radius);
            if (others >= maxNearbyPlayers)
            {
                blacklistState.recordFailure(
                        c,
                        nowMillis,
                        Math.max(1, config.trainingPlacementFailLimit()),
                        Math.max(0, config.trainingCenterCooldownSeconds())
                );
                continue;
            }

            return c;
        }

        return null;
    }

    private int countOtherPlayersNearby(WorldPoint center, int radiusTiles)
    {
        if (center == null || Microbot.getClient() == null)
        {
            return 0;
        }

        Player me = Microbot.getClient().getLocalPlayer();
        List<Player> players = Microbot.getClient().getPlayers();
        if (players == null || players.isEmpty())
        {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < players.size(); i++)
        {
            Player p = players.get(i);
            if (p == null || p == me)
            {
                continue;
            }
            WorldPoint w = p.getWorldLocation();
            if (w != null && w.distanceTo(center) <= radiusTiles)
            {
                count++;
            }
        }
        return count;
    }

    private boolean tryHandleOwnedTraps(WorldPoint center, long nowMillis)
    {
        Set<WorldPoint> ownedTiles = trapOwnershipTracker != null ? trapOwnershipTracker.getMyTrapTiles() : null;
        if (ownedTiles == null || ownedTiles.isEmpty())
        {
            return false;
        }

        var traps = Microbot.getRs2TileObjectCache()
                .query()
                .where(o -> o != null && CompetitionTrapIds.BIRD_SNARES.contains(o.getId()))
                .within(center, TRAP_QUERY_RADIUS_TILES)
                .toList();

        if (traps == null || traps.isEmpty())
        {
            return false;
        }

        final int scanLimit = Math.min(12, traps.size());
        for (int i = 0; i < scanLimit; i++)
        {
            var trap = traps.get(i);
            if (trap == null)
            {
                continue;
            }
            WorldPoint loc = trap.getWorldLocation();
            if (loc == null || !ownedTiles.contains(loc))
            {
                continue;
            }

            inventoryCountBeforeInteract = Rs2Inventory.count();
            trap.click();
            waitingForInventoryChange = true;
            inventoryWaitDeadlineMillis = nowMillis + INVENTORY_WAIT_TIMEOUT_MS;
            nextActionAtMillis = nowMillis + DEFAULT_ACTION_COOLDOWN_MS;
            return true;
        }

        return false;
    }

    private boolean tryPlaceNextSnare(HunterRumorsConfig config, WorldPoint center, long nowMillis)
    {
        List<WorldPoint> plan = TrapPlacementPlanner.centerAndCorners(center);
        if (plan.isEmpty())
        {
            return false;
        }

        // Ensure the whole 3x3 grid plan is placeable up front.
        for (int i = 0; i < plan.size(); i++)
        {
            if (!isPlaceableTile(plan.get(i)))
            {
                blacklistState.recordFailure(
                        center,
                        nowMillis,
                        Math.max(1, config.trainingPlacementFailLimit()),
                        Math.max(0, config.trainingCenterCooldownSeconds())
                );
                nextActionAtMillis = nowMillis + DEFAULT_ACTION_COOLDOWN_MS;
                return true;
            }
        }

        int invSnares = Rs2Inventory.itemQuantity(ItemID.HUNTING_OJIBWAY_BIRD_SNARE);
        int cap = Math.max(1, config.trainingMaxSnaresCap());
        int maxToLay = Math.min(invSnares, cap);
        if (maxToLay <= 0)
        {
            return false;
        }

        for (int probe = 0; probe < plan.size(); probe++)
        {
            int idx = (activePlacementIndex + probe) % plan.size();
            if (idx >= maxToLay)
            {
                return false;
            }

            WorldPoint tile = plan.get(idx);
            WorldPoint me = Rs2Player.getWorldLocation();
            if (me == null || me.distanceTo(tile) > WALK_RADIUS)
            {
                Microbot.status = "Bird snare: walk grid";
                Rs2Walker.walkTo(tile, WALK_RADIUS);
                nextActionAtMillis = nowMillis + AFTER_WALK_COOLDOWN_MS;
                return true;
            }

            if (Rs2Player.isMoving() || Rs2Player.isAnimating())
            {
                nextActionAtMillis = nowMillis + DEFAULT_ACTION_COOLDOWN_MS;
                return true;
            }

            Rs2ItemModel snare = Rs2Inventory.get(ItemID.HUNTING_OJIBWAY_BIRD_SNARE);
            if (snare == null)
            {
                return false;
            }

            boolean laid = Rs2Inventory.interact(snare, "Lay");
            if (laid)
            {
                blacklistState.recordSuccess(center);
                activePlacementIndex = (idx + 1) % plan.size();
                nextActionAtMillis = nowMillis + AFTER_LAY_COOLDOWN_MS;
                Microbot.status = "Bird snare: lay";
                return true;
            }

            blacklistState.recordFailure(
                    center,
                    nowMillis,
                    Math.max(1, config.trainingPlacementFailLimit()),
                    Math.max(0, config.trainingCenterCooldownSeconds())
            );
            nextActionAtMillis = nowMillis + DEFAULT_ACTION_COOLDOWN_MS;
            return true;
        }

        return false;
    }

    private boolean isPlaceableTile(WorldPoint tile)
    {
        if (tile == null || Microbot.getClient() == null)
        {
            return false;
        }

        LocalPoint lp = LocalPoint.fromWorld(Microbot.getClient().getTopLevelWorldView(), tile);
        if (lp == null)
        {
            return false;
        }

        if (!Rs2Tile.isWalkable(lp))
        {
            return false;
        }

        return Microbot.getRs2TileObjectCache().query().within(tile, 0).count() == 0;
    }
}

