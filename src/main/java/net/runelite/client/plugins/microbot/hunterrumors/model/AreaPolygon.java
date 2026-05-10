package net.runelite.client.plugins.microbot.hunterrumors.model;

import lombok.Getter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.rs2leaguetransport.LeaguesRegion;

import java.util.List;

@Getter
public class AreaPolygon
{
    private final String name;
    private final int plane;
    private final List<WorldPoint> vertices;
    private final WorldArea bounds;
    private final LeaguesRegion[] leaguesRegions;

    public AreaPolygon(String name, int plane, List<WorldPoint> vertices, WorldArea bounds, LeaguesRegion[] leaguesRegions)
    {
        this.name = name;
        this.plane = plane;
        this.vertices = vertices;
        this.bounds = bounds;
        this.leaguesRegions = leaguesRegions;
    }

    public boolean contains(WorldPoint p)
    {
        if (p == null || p.getPlane() != plane)
        {
            return false;
        }
        if (bounds != null && !bounds.contains(p))
        {
            return false;
        }
        if (vertices == null || vertices.size() < 3)
        {
            return true; // bounds-only fallback
        }

        // Even-odd ray cast against polygon vertices (in world tile coordinates).
        // Works fine for axis-aligned fence-style polygons; repeated vertices are tolerated.
        final int x = p.getX();
        final int y = p.getY();

        boolean inside = false;
        int n = vertices.size();
        int j = n - 1;
        for (int i = 0; i < n; i++)
        {
            WorldPoint vi = vertices.get(i);
            WorldPoint vj = vertices.get(j);
            if (vi == null || vj == null)
            {
                j = i;
                continue;
            }

            int xi = vi.getX();
            int yi = vi.getY();
            int xj = vj.getX();
            int yj = vj.getY();

            // Check if edge intersects ray to the right of (x,y)
            boolean intersect = ((yi > y) != (yj > y)) &&
                    (x < (long) (xj - xi) * (y - yi) / (long) (yj - yi) + xi);
            if (intersect)
            {
                inside = !inside;
            }
            j = i;
        }

        return inside;
    }
}

