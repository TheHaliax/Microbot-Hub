package net.runelite.client.plugins.microbot.hunterrumors.logic.training;

import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Deterministic 3x3 placement plan: center + 4 corners.
 * This class is data-only; execution happens in the service.
 */
public final class TrapPlacementPlanner
{
    private TrapPlacementPlanner() {}

    public static List<WorldPoint> centerAndCorners(WorldPoint center)
    {
        if (center == null)
        {
            return Collections.emptyList();
        }

        // Order is stable: center, NW, NE, SW, SE.
        List<WorldPoint> out = new ArrayList<>(5);
        out.add(center);
        out.add(new WorldPoint(center.getX() - 1, center.getY() + 1, center.getPlane()));
        out.add(new WorldPoint(center.getX() + 1, center.getY() + 1, center.getPlane()));
        out.add(new WorldPoint(center.getX() - 1, center.getY() - 1, center.getPlane()));
        out.add(new WorldPoint(center.getX() + 1, center.getY() - 1, center.getPlane()));
        return out;
    }
}

