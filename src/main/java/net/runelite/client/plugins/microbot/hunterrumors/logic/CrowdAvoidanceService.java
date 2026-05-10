package net.runelite.client.plugins.microbot.hunterrumors.logic;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.hunterrumors.HunterRumorsConfig;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class CrowdAvoidanceService
{
    private final Client client;

    // Current "area rotation" plan (set by handlers). This lets us try nearby areas before hopping.
    private volatile List<WorldPoint> areaAnchors = null;
    private final Set<WorldPoint> triedAnchors = new HashSet<>();
    private long lastAnchorMoveAtMs = 0L;

    // Competition profile (set by the current handler/assignment).
    private volatile CompetitionMode competitionMode = CompetitionMode.NONE;
    private volatile Set<Integer> competitionTrapObjectIds = null;
    private volatile Set<WorldPoint> myTrapTiles = null;

    private static final int MAX_ANCHOR_SWITCH_DISTANCE = 80; // "not very far apart"
    private static final long ANCHOR_SWITCH_COOLDOWN_MS = 15_000L;

    @Inject
    public CrowdAvoidanceService(Client client)
    {
        this.client = client;
    }

    /**
     * Provide a set of candidate "area anchors" for the current assignment. If crowded, we will try
     * each anchor (that is reasonably close) before hopping worlds.
     */
    public void setAreaAnchors(List<WorldPoint> anchors)
    {
        if (anchors == null || anchors.isEmpty())
        {
            areaAnchors = null;
            triedAnchors.clear();
            return;
        }
        areaAnchors = anchors.stream().filter(Objects::nonNull).collect(Collectors.toList());
        triedAnchors.clear();
    }

    public void clearAreaAnchors()
    {
        areaAnchors = null;
        triedAnchors.clear();
    }

    public void setCompetition(CompetitionMode mode, Set<Integer> trapObjectIds, Set<WorldPoint> myTrapTiles)
    {
        if (mode != null)
        {
            this.competitionMode = mode;
        }
        if (trapObjectIds != null)
        {
            this.competitionTrapObjectIds = trapObjectIds;
        }
        if (myTrapTiles != null)
        {
            this.myTrapTiles = myTrapTiles;
        }
    }

    public void clearCompetition()
    {
        this.competitionMode = CompetitionMode.NONE;
        this.competitionTrapObjectIds = null;
        this.myTrapTiles = null;
    }

    public void setAreaAnchorsFromPolygons(List<net.runelite.client.plugins.microbot.hunterrumors.model.AreaPolygon> areas)
    {
        if (areas == null || areas.isEmpty())
        {
            clearAreaAnchors();
            return;
        }
        List<WorldPoint> anchors = areas.stream()
                .filter(Objects::nonNull)
                .map(a -> a.getBounds() == null ? null :
                        new WorldPoint(
                                a.getBounds().getX() + (a.getBounds().getWidth() / 2),
                                a.getBounds().getY() + (a.getBounds().getHeight() / 2),
                                a.getPlane()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        setAreaAnchors(anchors);
    }

    public boolean tryResolveCompetition(HunterRumorsConfig config)
    {
        if (config == null || !config.avoidCrowds())
        {
            return false;
        }
        Player me = client.getLocalPlayer();
        if (me == null || me.getWorldLocation() == null)
        {
            return false;
        }

        CompetitionMode mode = competitionMode;
        if (mode == null || mode == CompetitionMode.NONE)
        {
            return false;
        }

        int dist = Math.max(1, config.avoidCrowdsDistance());
        boolean crowded = false;
        int count = 0;
        if (mode == CompetitionMode.PLAYERS_NEARBY)
        {
            for (Player p : client.getPlayers())
            {
                if (p == null || p == me || p.getWorldLocation() == null)
                {
                    continue;
                }
                if (p.getWorldLocation().distanceTo(me.getWorldLocation()) <= dist)
                {
                    count++;
                }
            }
            crowded = count >= Math.max(1, config.avoidCrowdsPlayers());
        }
        else if (mode == CompetitionMode.TRAPS_NEARBY)
        {
            Set<Integer> trapIds = competitionTrapObjectIds;
            if (trapIds == null || trapIds.isEmpty())
            {
                return false;
            }
            Set<WorldPoint> mine = myTrapTiles;
            var objs = Microbot.getRs2TileObjectCache().query()
                    .within(me.getWorldLocation(), dist)
                    .where(o -> o != null && trapIds.contains(o.getId()))
                    .toList();
            for (var o : objs)
            {
                WorldPoint wp = o.getWorldLocation();
                if (wp == null)
                {
                    continue;
                }
                if (mine != null && mine.contains(wp))
                {
                    continue;
                }
                crowded = true;
                break;
            }
        }

        if (!crowded)
        {
            return false;
        }

        // First: rotate through nearby candidate areas (if provided), before world hopping.
        if (tryMoveToNextAreaAnchor(me.getWorldLocation()))
        {
            Microbot.status = "Crowded: switching area";
            return true;
        }

        int next = pickNextWorld(config.leaguesEnabled());
        if (next <= 0)
        {
            return false;
        }

        Microbot.log("[HunterRumors] Crowded (" + count + "), hopping to " + next);
        Microbot.hopToWorld(next);
        return true;
    }

    private boolean tryMoveToNextAreaAnchor(WorldPoint me)
    {
        List<WorldPoint> anchors = areaAnchors;
        if (anchors == null || anchors.isEmpty() || me == null)
        {
            return false;
        }

        long now = System.currentTimeMillis();
        if (now - lastAnchorMoveAtMs < ANCHOR_SWITCH_COOLDOWN_MS)
        {
            return false;
        }

        // Prefer anchors close enough to "swap spots" quickly.
        for (WorldPoint a : anchors)
        {
            if (a == null || triedAnchors.contains(a))
            {
                continue;
            }
            if (a.distanceTo(me) > MAX_ANCHOR_SWITCH_DISTANCE)
            {
                continue;
            }

            triedAnchors.add(a);
            lastAnchorMoveAtMs = now;
            Microbot.log("[HunterRumors] Crowded: trying alternate area " + a);
            Rs2Walker.walkTo(a, 6);
            return true;
        }

        // If we've tried all anchors, reset the cycle so future crowd events can re-try (e.g. players moved).
        if (triedAnchors.size() >= anchors.size())
        {
            triedAnchors.clear();
        }
        return false;
    }

    private int pickNextWorld(boolean leaguesEnabled)
    {
        WorldResult wr = Microbot.getWorldService().getWorlds();
        if (wr == null || wr.getWorlds() == null)
        {
            return -1;
        }

        int current = client.getWorld();
        List<net.runelite.http.api.worlds.World> worlds = wr.getWorlds();

        // Keep only worlds matching our "mode" and safe-ish.
        List<net.runelite.http.api.worlds.World> candidates = worlds.stream()
                .filter(w -> w != null)
                .filter(w -> isAllowedWorld(w, leaguesEnabled))
                .sorted(Comparator.comparingInt(net.runelite.http.api.worlds.World::getId))
                .collect(Collectors.toList());

        if (candidates.isEmpty())
        {
            return -1;
        }

        // Next world in ascending order, wrap.
        for (net.runelite.http.api.worlds.World w : candidates)
        {
            if (w.getId() > current)
            {
                return w.getId();
            }
        }
        return candidates.get(0).getId();
    }

    private boolean isAllowedWorld(net.runelite.http.api.worlds.World w, boolean leaguesEnabled)
    {
        int id = w.getId();
        if (id <= 0)
        {
            return false;
        }

        Set<WorldType> types = w.getTypes();
        if (types == null)
        {
            return false;
        }

        // Avoid dangerous/special worlds. (WorldType enum varies across forks; use name() checks.)
        if (hasType(types, "PVP") ||
                hasType(types, "HIGH_RISK") ||
                hasType(types, "DEADMAN") ||
                hasType(types, "TOURNAMENT") ||
                hasType(types, "BETA"))
        {
            return false;
        }

        boolean isLeague = hasType(types, "LEAGUE");
        if (leaguesEnabled)
        {
            return isLeague;
        }
        else
        {
            return !isLeague;
        }
    }

    private static boolean hasType(Set<WorldType> types, String needleUpper)
    {
        if (types == null || needleUpper == null || needleUpper.isEmpty())
        {
            return false;
        }
        for (WorldType t : types)
        {
            if (t == null)
            {
                continue;
            }
            String n = t.name();
            if (n != null && n.toUpperCase().contains(needleUpper))
            {
                return true;
            }
        }
        return false;
    }
}

