package net.runelite.client.plugins.microbot.hunterrumors.logic.handlers;

import net.runelite.api.ItemID;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.hunterrumors.HunterRumorsAreaCatalog;
import net.runelite.client.plugins.microbot.hunterrumors.RumourMajorArea;
import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;
import net.runelite.client.plugins.microbot.hunterrumors.logic.TrapOwnershipTracker;
import net.runelite.client.plugins.microbot.hunterrumors.logic.RarePartDetector;
import net.runelite.client.plugins.microbot.hunterrumors.logic.RumourHandler;
import net.runelite.client.plugins.microbot.hunterrumors.model.AreaPolygon;
import net.runelite.client.plugins.microbot.hunterrumors.model.GearTag;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shared handler logic for all "Spiked pit" (pitfall) rumours.
 *
 * v0: setup + completion check only.
 * v1: implement pit build + lure loop once we capture in-game interactions.
 */
public abstract class SpikedPitRumourHandler implements RumourHandler
{
    protected static final String TEASING_STICK = "Teasing stick";
    protected static final String HUNTERS_SPEAR = "Hunter's spear";
    protected static final String LOGS = "Logs";

    // Prefer fletching knife, then regular knife.
    private static final int[] KNIFE_IDS = new int[]{ItemID.FLETCHING_KNIFE, ItemID.KNIFE};

    // Used only for "do we have an axe" sanity; global loadout will pick best.
    private static final int[] AXE_IDS = new int[]{
            ItemID.CRYSTAL_AXE,
            ItemID.DRAGON_AXE,
            ItemID.RUNE_AXE,
            ItemID.ADAMANT_AXE,
            ItemID.MITHRIL_AXE,
            ItemID.BLACK_AXE,
            ItemID.STEEL_AXE,
            ItemID.IRON_AXE,
            ItemID.BRONZE_AXE
    };

    protected abstract RumourMajorArea[] majorAreas();

    /**
     * Pitfall-specific IDs and local rules. Each concrete handler provides its own.
     */
    protected interface PitfallProfile
    {
        WorldArea area();
        int pitObjectId();             // action "Trap" on this
        int spikedPitObjectId();       // action "Jump"
        int collapsedTrapObjectId();   // action "Dismantle"
        int lureNpcId();               // action "Tease"
        int maxOtherPlayersInArea();   // e.g. 2 means "ok if <=2 others"
        int pitExclusionDistance();    // don't contest pits near other players
        int lowHpThreshold();          // walk to bank if hp <= threshold
    }

    /**
     * Provide pitfall profile when implemented. If null, handler stays in "TODO" mode.
     */
    protected PitfallProfile pitfall()
    {
        return null;
    }

    @Override
    public List<AreaPolygon> getCandidateAreas()
    {
        RumourMajorArea[] areas = majorAreas();
        if (areas == null || areas.length == 0)
        {
            return Collections.emptyList();
        }

        ArrayList<AreaPolygon> out = new ArrayList<>(areas.length);
        for (RumourMajorArea a : areas)
        {
            AreaPolygon poly = HunterRumorsAreaCatalog.get(a);
            if (poly != null)
            {
                out.add(poly);
            }
        }
        return out;
    }

    @Override
    public List<String> getRequiredGear()
    {
        // Wiki says: Knife + any logs + (teasing stick OR hunter's spear).
        // We require teasing stick for now (spear support can come later once we wire equipment selection).
        return List.of(TEASING_STICK, "Knife", "Axe", LOGS);
    }

    @Override
    public List<GearTag> getOptionalGearTags()
    {
        return Collections.emptyList();
    }

    @Override
    public boolean ensureSetup()
    {
        if (!hasKnife() || !Rs2Inventory.hasItem(LOGS))
        {
            Microbot.status = "Need: knife + logs";
            return false;
        }

        // v0: require teasing stick. Later: allow hunter's spear as alternative.
        if (!Rs2Inventory.hasItem(TEASING_STICK))
        {
            Microbot.status = "Need: teasing stick";
            return false;
        }

        if (!hasAxe())
        {
            Microbot.status = "Need: woodcutting axe";
            return false;
        }

        return true;
    }

    @Override
    public boolean step()
    {
        PitfallProfile p = pitfall();
        if (p == null || p.area() == null)
        {
            Microbot.status = getTarget().name() + ": no pitfall profile";
            return false;
        }

        // Safety: bail to bank if low HP.
        int hp = Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS);
        if (hp > 0 && hp <= Math.max(1, p.lowHpThreshold()))
        {
            Microbot.status = getTarget().name() + ": low HP -> bank";
            Rs2Bank.walkToBank();
            return false;
        }

        WorldPoint me = Rs2Player.getWorldLocation();
        if (me == null)
        {
            return false;
        }

        if (!p.area().contains(me))
        {
            Microbot.status = getTarget().name() + ": walk to area";
            Rs2Walker.walkTo(center(p.area()), 6);
            return false;
        }

        int others = countOtherPlayersInArea(p.area());
        if (others > Math.max(0, p.maxOtherPlayersInArea()))
        {
            Microbot.status = getTarget().name() + ": crowded (" + others + ")";
            return false;
        }

        // Dismantle owned collapsed trap if present.
        if (tryDismantleCollapsedTrap(p))
        {
            localStep = LocalStep.FIND_OR_BUILD_TRAP;
            activePitTile = null;
            return true;
        }

        if (localStep == LocalStep.FIND_OR_BUILD_TRAP)
        {
            if (activePitTile == null)
            {
                activePitTile = findFreePitTile(p, me);
                if (activePitTile == null)
                {
                    Microbot.status = getTarget().name() + ": no free pits";
                    Rs2Walker.walkTo(center(p.area()), 6);
                    return false;
                }
            }

            if (isSpikedPitAt(p, activePitTile))
            {
                localStep = LocalStep.TEASE;
                return false;
            }

            Microbot.status = getTarget().name() + ": trap pit";
            var pit = Microbot.getRs2TileObjectCache().query().withId(p.pitObjectId()).nearest(activePitTile, 0);
            if (pit == null)
            {
                Rs2Walker.walkTo(activePitTile, 2);
                return false;
            }
            pit.click("Trap");
            return false;
        }

        if (localStep == LocalStep.TEASE)
        {
            if (activePitTile == null || !isSpikedPitAt(p, activePitTile))
            {
                localStep = LocalStep.FIND_OR_BUILD_TRAP;
                return false;
            }

            if (otherTrapNearby(p, activePitTile))
            {
                Microbot.status = getTarget().name() + ": pit contested";
                activePitTile = null;
                localStep = LocalStep.FIND_OR_BUILD_TRAP;
                return false;
            }

            var npc = Microbot.getRs2NpcCache().query()
                    .withId(p.lureNpcId())
                    .within(activePitTile, 10)
                    .first();
            if (npc == null)
            {
                Microbot.status = getTarget().name() + ": find target";
                return false;
            }
            Microbot.status = getTarget().name() + ": tease";
            npc.click("Tease");
            lastTeaseAtMs = System.currentTimeMillis();
            localStep = LocalStep.JUMP;
            return false;
        }

        if (localStep == LocalStep.JUMP)
        {
            if (activePitTile == null || !isSpikedPitAt(p, activePitTile))
            {
                localStep = LocalStep.FIND_OR_BUILD_TRAP;
                return false;
            }

            // Jump soon after tease (tune later).
            if (System.currentTimeMillis() - lastTeaseAtMs < 6_000L)
            {
                var spiked = Microbot.getRs2TileObjectCache().query().withId(p.spikedPitObjectId()).nearest(activePitTile, 0);
                if (spiked == null)
                {
                    Rs2Walker.walkTo(activePitTile, 2);
                    return false;
                }
                Microbot.status = getTarget().name() + ": jump pit";
                spiked.click("Jump");
                localStep = LocalStep.DISMANTLE;
                return false;
            }

            localStep = LocalStep.TEASE;
            return false;
        }

        if (localStep == LocalStep.DISMANTLE)
        {
            if (tryDismantleCollapsedTrap(p))
            {
                activePitTile = null;
                localStep = LocalStep.FIND_OR_BUILD_TRAP;
                return true;
            }
            Microbot.status = getTarget().name() + ": waiting trap";
            return false;
        }

        return false;
    }

    @Override
    public boolean isRumourComplete()
    {
        return RarePartDetector.hasRarePart(getTarget().rarePart());
    }

    @Override
    public WorldPoint preferredAnchor()
    {
        List<AreaPolygon> cands = getCandidateAreas();
        if (cands.isEmpty())
        {
            return null;
        }

        AreaPolygon a = cands.get(0);
        if (a == null || a.getBounds() == null)
        {
            return null;
        }

        return new WorldPoint(
                a.getBounds().getX() + (a.getBounds().getWidth() / 2),
                a.getBounds().getY() + (a.getBounds().getHeight() / 2),
                a.getPlane()
        );
    }

    private static boolean hasKnife()
    {
        for (int id : KNIFE_IDS)
        {
            if (Rs2Inventory.hasItem(id))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean hasAxe()
    {
        for (int id : AXE_IDS)
        {
            if (id <= 0)
            {
                continue;
            }
            if (Rs2Equipment.isWearing(id) || Rs2Inventory.hasItem(id))
            {
                return true;
            }
        }
        return false;
    }

    // ---- pitfall loop state (bounded, no recursion) ----
    private enum LocalStep
    {
        FIND_OR_BUILD_TRAP,
        TEASE,
        JUMP,
        DISMANTLE
    }

    private LocalStep localStep = LocalStep.FIND_OR_BUILD_TRAP;
    private WorldPoint activePitTile = null;
    private long lastTeaseAtMs = 0L;

    private static WorldPoint center(WorldArea a)
    {
        return new WorldPoint(a.getX() + (a.getWidth() / 2), a.getY() + (a.getHeight() / 2), a.getPlane());
    }

    private static int countOtherPlayersInArea(WorldArea area)
    {
        if (area == null)
        {
            return 0;
        }

        Player self = Microbot.getClient().getLocalPlayer();
        int count = 0;
        for (Player p : Microbot.getClient().getPlayers())
        {
            if (p == null || p == self)
            {
                continue;
            }
            WorldPoint wp = p.getWorldLocation();
            if (wp == null)
            {
                continue;
            }
            if (area.contains(wp))
            {
                count++;
            }
        }
        return count;
    }

    private static WorldPoint findFreePitTile(PitfallProfile p, WorldPoint me)
    {
        var pits = Microbot.getRs2TileObjectCache().query()
                .withId(p.pitObjectId())
                .toList();
        WorldPoint best = null;
        int bestDist = Integer.MAX_VALUE;
        for (var pit : pits)
        {
            if (pit == null)
            {
                continue;
            }
            WorldPoint loc = pit.getWorldLocation();
            if (loc == null || !p.area().contains(loc))
            {
                continue;
            }
            if (anyOtherPlayerNear(loc, Math.max(1, p.pitExclusionDistance())))
            {
                continue;
            }
            if (isAnyTrapAt(p, loc) && !isMyTrapTile(loc))
            {
                continue;
            }
            int d = me.distanceTo(loc);
            if (d < bestDist)
            {
                bestDist = d;
                best = loc;
            }
        }
        return best;
    }

    private static boolean anyOtherPlayerNear(WorldPoint tile, int dist)
    {
        Player self = Microbot.getClient().getLocalPlayer();
        for (Player p : Microbot.getClient().getPlayers())
        {
            if (p == null || p == self)
            {
                continue;
            }
            WorldPoint wp = p.getWorldLocation();
            if (wp == null)
            {
                continue;
            }
            if (wp.getPlane() == tile.getPlane() && wp.distanceTo(tile) <= dist)
            {
                return true;
            }
        }
        return false;
    }

    private static boolean isAnyTrapAt(PitfallProfile p, WorldPoint tile)
    {
        if (tile == null)
        {
            return false;
        }
        return Microbot.getRs2TileObjectCache().query().withId(p.spikedPitObjectId()).nearest(tile, 0) != null
                || Microbot.getRs2TileObjectCache().query().withId(p.collapsedTrapObjectId()).nearest(tile, 0) != null;
    }

    private static boolean isSpikedPitAt(PitfallProfile p, WorldPoint tile)
    {
        return tile != null && Microbot.getRs2TileObjectCache().query().withId(p.spikedPitObjectId()).nearest(tile, 0) != null;
    }

    private static boolean tryDismantleCollapsedTrap(PitfallProfile p)
    {
        TrapOwnershipTracker tracker = null;
        try
        {
            if (Microbot.getInjector() != null)
            {
                tracker = Microbot.getInjector().getInstance(TrapOwnershipTracker.class);
            }
        }
        catch (Exception ignored)
        {
            tracker = null;
        }

        var objs = Microbot.getRs2TileObjectCache().query().withId(p.collapsedTrapObjectId()).toList();
        for (var o : objs)
        {
            if (o == null)
            {
                continue;
            }
            WorldPoint wp = o.getWorldLocation();
            if (wp == null || !p.area().contains(wp))
            {
                continue;
            }
            if (tracker != null && !tracker.isMyTrapTile(wp))
            {
                continue;
            }
            o.click("Dismantle");
            return true;
        }
        return false;
    }

    private static boolean isMyTrapTile(WorldPoint tile)
    {
        try
        {
            if (Microbot.getInjector() == null)
            {
                return false;
            }
            TrapOwnershipTracker tracker = Microbot.getInjector().getInstance(TrapOwnershipTracker.class);
            return tracker != null && tracker.isMyTrapTile(tile);
        }
        catch (Exception ignored)
        {
            return false;
        }
    }

    private static boolean otherTrapNearby(PitfallProfile p, WorldPoint pitTile)
    {
        var objs = Microbot.getRs2TileObjectCache().query()
                .within(pitTile, 4)
                .where(o -> o != null && (o.getId() == p.spikedPitObjectId() || o.getId() == p.collapsedTrapObjectId()))
                .toList();
        for (var o : objs)
        {
            if (o == null)
            {
                continue;
            }
            WorldPoint wp = o.getWorldLocation();
            if (wp == null)
            {
                continue;
            }
            if (!isMyTrapTile(wp))
            {
                return true;
            }
        }
        return false;
    }
}

