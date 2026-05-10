package net.runelite.client.plugins.microbot.hunterrumors;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.plugins.microbot.hunterrumors.model.AreaPolygon;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class HunterRumorsOverlay extends Overlay
{
    private final HunterRumorsScript script;
    private final HunterRumorsConfig config;
    private final Client client;

    private DebugArea lastDebugArea = null;
    private AreaPolygon cachedPolygon = null;
    private List<WorldPoint> cachedVertices = null;

    @Inject
    public HunterRumorsOverlay(HunterRumorsScript script, HunterRumorsConfig config, Client client)
    {
        this.script = script;
        this.config = config;
        this.client = client;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        renderDebugArea(graphics);

        graphics.setColor(Color.WHITE);
        int y = 15;
        graphics.drawString("Hunter Rumors", 10, y);
        y += 15;
        graphics.drawString("State: " + (script != null ? script.getState() : "null"), 10, y);
        y += 15;
        graphics.drawString("Status: " + Microbot.status, 10, y);
        return new Dimension(220, 60);
    }

    private void renderDebugArea(Graphics2D g)
    {
        if (config == null || client == null)
        {
            return;
        }

        DebugArea a = config.debugDrawArea();
        if (a == null || a == DebugArea.NONE)
        {
            lastDebugArea = a;
            cachedPolygon = null;
            cachedVertices = null;
            return;
        }

        if (a != lastDebugArea)
        {
            lastDebugArea = a;
            cachedPolygon = resolveDebugPolygon(a);
            cachedVertices = cachedPolygon != null ? cachedPolygon.getVertices() : null;
        }

        if (cachedVertices == null || cachedVertices.size() < 3)
        {
            return;
        }

        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(0, 255, 255, 220));

        int n = cachedVertices.size();
        for (int i = 0; i < n; i++)
        {
            WorldPoint p1 = cachedVertices.get(i);
            WorldPoint p2 = cachedVertices.get((i + 1) % n);
            drawEdge(g, p1, p2);
        }
    }

    private AreaPolygon resolveDebugPolygon(DebugArea a)
    {
        switch (a)
        {
            case FALCONRY:
                return HunterRumorsLocations.FALCONRY_AREA_POLYGON;
            default:
                return null;
        }
    }

    private void drawEdge(Graphics2D g, WorldPoint a, WorldPoint b)
    {
        if (a == null || b == null)
        {
            return;
        }
        LocalPoint la = LocalPoint.fromWorld(client, a);
        LocalPoint lb = LocalPoint.fromWorld(client, b);
        if (la == null || lb == null)
        {
            return;
        }
        Point pa = Perspective.localToCanvas(client, la, a.getPlane());
        Point pb = Perspective.localToCanvas(client, lb, b.getPlane());
        if (pa == null || pb == null)
        {
            return;
        }
        g.drawLine(pa.getX(), pa.getY(), pb.getX(), pb.getY());
    }
}

