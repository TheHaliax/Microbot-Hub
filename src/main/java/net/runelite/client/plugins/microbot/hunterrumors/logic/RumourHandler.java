package net.runelite.client.plugins.microbot.hunterrumors.logic;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;
import net.runelite.client.plugins.microbot.hunterrumors.model.AreaPolygon;
import net.runelite.client.plugins.microbot.hunterrumors.model.GearTag;

import java.util.List;

public interface RumourHandler
{
    RumourTarget getTarget();

    List<AreaPolygon> getCandidateAreas();

    List<String> getRequiredGear();

    List<GearTag> getOptionalGearTags();

    /**
     * Called in banking/setup stage; must be bounded.
     */
    boolean ensureSetup();

    /**
     * Called each tick; must do at most one bounded action.
     */
    boolean step();

    /**
     * True when rare part obtained and ready to turn in.
     */
    boolean isRumourComplete();

    /**
     * Optional hint tile for travel planning (e.g. center of chosen polygon).
     */
    default WorldPoint preferredAnchor()
    {
        return null;
    }
}

