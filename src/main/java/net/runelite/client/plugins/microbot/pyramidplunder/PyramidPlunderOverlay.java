package net.runelite.client.plugins.microbot.pyramidplunder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

import static net.runelite.client.plugins.microbot.pyramidplunder.PyramidPlunderPlugin.*;
import net.runelite.client.config.ConfigManager;

public class PyramidPlunderOverlay extends Overlay
{
    private static final int MAX_DISTANCE = 2350;

    private final Client client;
    private final PyramidPlunderPlugin module;
    private final ConfigManager configManager;

    public PyramidPlunderOverlay(Client client, PyramidPlunderPlugin module, ConfigManager configManager)
    {
        super(module);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.module = module;
        this.configManager = configManager;
    }

    private PyramidPlunderConfig getConfig() {
        return configManager.getConfig(PyramidPlunderConfig.class);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Widget ppWidget = client.getWidget(InterfaceID.NtkOverlay.CONTENT);
        if (ppWidget == null)
        {
            return null;
        }

        ppWidget.setHidden(getConfig().hideTimer());

        LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();

        // Highlight convex hulls of urns, chests, and sarcophagus
        int currentFloor = client.getVarbitValue(VarbitID.NTK_ROOM_NUMBER);
        for (GameObject object : module.getObjectsToHighlight())
        {
            if (module.getUrnRoom() > currentFloor && URN_IDS.contains(object.getId())
                    || module.getChestRoom() > currentFloor && GRAND_GOLD_CHEST_ID == object.getId()
                    || module.getSarcRoom() > currentFloor && SARCOPHAGUS_ID == object.getId()
                    || object.getLocalLocation().distanceTo(playerLocation) >= MAX_DISTANCE)
            {
                continue;
            }

            ObjectComposition imposter = client.getObjectDefinition(object.getId()).getImpostor();
            if (URN_CLOSED_IDS.contains(imposter.getId())
                    || GRAND_GOLD_CHEST_CLOSED_ID == imposter.getId()
                    || SARCOPHAGUS_CLOSED_ID == imposter.getId())
            {
                Shape shape = object.getConvexHull();

                if (shape != null)
                {
                    OverlayUtil.renderPolygon(graphics, shape, getConfig().highlightContainersColor());
                }
            }
        }

        Point mousePosition = client.getMouseCanvasPosition();

        // Highlight clickboxes of speartraps and tomb doors
        module.getTilesToHighlight().forEach((object, tile) ->
        {
            if (!getConfig().highlightDoors() && TOMB_DOOR_WALL_IDS.contains(object.getId())
                    || !getConfig().highlightSpeartraps() && SPEARTRAP_ID == object.getId()
                    || tile.getPlane() != client.getPlane()
                    || object.getLocalLocation().distanceTo(playerLocation) >= MAX_DISTANCE)
            {
                return;
            }

            Color highlightColor;
            if (SPEARTRAP_ID == object.getId())
            {
                // this varbit is set to 1 when you enter a room and 0 once you get passed the spike traps
                if (client.getVarbitValue(VarbitID.NTK_TRAP_ACTIVE) != 1)
                {
                    return;
                }

                highlightColor = getConfig().highlightSpeartrapsColor();
            }
            else
            {
                ObjectComposition imposter = client.getObjectDefinition(object.getId()).getImpostor();
                if (imposter.getId() != TOMB_DOOR_CLOSED_ID)
                {
                    return;
                }

                highlightColor = getConfig().highlightDoorsColor();
            }

            Shape objectClickbox = object.getClickbox();
            if (objectClickbox != null)
            {
                if (objectClickbox.contains(mousePosition.getX(), mousePosition.getY()))
                {
                    graphics.setColor(highlightColor.darker());
                }
                else
                {
                    graphics.setColor(highlightColor);
                }

                graphics.draw(objectClickbox);
                graphics.setColor(ColorUtil.colorWithAlpha(highlightColor, highlightColor.getAlpha() / 5));
                graphics.fill(objectClickbox);
            }
        });

        return null;
    }
}
