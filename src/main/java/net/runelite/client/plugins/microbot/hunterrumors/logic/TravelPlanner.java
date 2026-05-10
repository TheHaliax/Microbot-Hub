package net.runelite.client.plugins.microbot.hunterrumors.logic;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.hunterrumors.HunterRumorsConfig;
import net.runelite.client.plugins.microbot.rs2leaguetransport.LeaguesRegion;
import net.runelite.client.plugins.microbot.hunterrumors.model.AreaPolygon;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.ArrayList;
import java.util.List;

public class TravelPlanner
{
    public static AreaPolygon chooseArea(HunterRumorsConfig config, List<AreaPolygon> candidates)
    {
        if (candidates == null || candidates.isEmpty())
        {
            return null;
        }

        List<AreaPolygon> filtered = candidates;
        if (config != null && config.leaguesEnabled())
        {
            filtered = filterByLeaguesUnlocks(config, candidates);
        }

        if (filtered.isEmpty())
        {
            return null;
        }

        // v0: pick closest bounds center to player.
        WorldPoint me = Rs2Player.getWorldLocation();
        AreaPolygon best = filtered.get(0);
        int bestDist = Integer.MAX_VALUE;
        for (AreaPolygon a : filtered)
        {
            WorldPoint anchor = center(a);
            if (anchor == null || me == null)
            {
                continue;
            }
            int d = me.distanceTo(anchor);
            if (d < bestDist)
            {
                bestDist = d;
                best = a;
            }
        }
        return best;
    }

    private static List<AreaPolygon> filterByLeaguesUnlocks(HunterRumorsConfig config, List<AreaPolygon> candidates)
    {
        ArrayList<AreaPolygon> out = new ArrayList<>(candidates.size());
        for (AreaPolygon a : candidates)
        {
            if (a == null)
            {
                continue;
            }
            LeaguesRegion[] regions = a.getLeaguesRegions();
            if (regions == null || regions.length == 0)
            {
                out.add(a);
                continue;
            }
            boolean ok = false;
            for (LeaguesRegion r : regions)
            {
                if (isUnlocked(config, r))
                {
                    ok = true;
                    break;
                }
            }
            if (ok)
            {
                out.add(a);
            }
        }
        return out;
    }

    private static boolean isUnlocked(HunterRumorsConfig config, LeaguesRegion r)
    {
        if (config == null || r == null)
        {
            return true;
        }
        switch (r)
        {
            case MISTHALIN: return config.unlockedMisthalin();
            case KANDARIN: return config.unlockedKandarin();
            case FREMENNIK: return config.unlockedFremennik();
            case KARAMJA: return config.unlockedKaramja();
            case DESERT: return config.unlockedDesert();
            case KOUREND: return config.unlockedKourend();
            case TIRANNWN: return config.unlockedTirannwn();
            case MORYTANIA: return config.unlockedMorytania();
            case VARLAMORE: return config.unlockedVarlamore();
            default: return true;
        }
    }

    private static WorldPoint center(AreaPolygon a)
    {
        if (a == null || a.getBounds() == null)
        {
            return null;
        }
        int x = a.getBounds().getX() + (a.getBounds().getWidth() / 2);
        int y = a.getBounds().getY() + (a.getBounds().getHeight() / 2);
        return new WorldPoint(x, y, a.getPlane());
    }
}

