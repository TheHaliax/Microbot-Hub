package net.runelite.client.plugins.microbot.hunterrumors.logic.handlers.spikedpit;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.hunterrumors.RumourMajorArea;
import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;
import net.runelite.client.plugins.microbot.hunterrumors.logic.handlers.SpikedPitRumourHandler;

public class SunlightAntelopeSpikedPitHandler extends SpikedPitRumourHandler
{
    // Bounded box (Hal): between these two points.
    private static final WorldPoint BOX_SW = new WorldPoint(1733, 2994, 0);
    private static final WorldPoint BOX_NE = new WorldPoint(1761, 3021, 0);
    private static final WorldArea AREA = new WorldArea(
            BOX_SW.getX(),
            BOX_SW.getY(),
            (BOX_NE.getX() - BOX_SW.getX()) + 1,
            (BOX_NE.getY() - BOX_SW.getY()) + 1,
            0
    );

    // Observed IDs (Hal screenshots)
    private static final int PIT_OBJECT_ID = 19227; // action: "Trap Pit"
    private static final int SPIKED_PIT_OBJECT_ID = 19228; // actions: "Jump Spiked pit", "Dismantle Spiked pit"
    private static final int COLLAPSED_TRAP_OBJECT_ID = 53024; // actions: "Dismantle Collapsed trap"
    private static final int SUNLIGHT_ANTELOPE_NPC_ID = 13133; // action: "Tease Sunlight antelope"

    private static final int PIT_PLAYER_EXCLUSION_DIST = 2; // don't fight over pits

    @Override
    public RumourTarget getTarget()
    {
        return RumourTarget.SUNLIGHT_ANTELOPE;
    }

    @Override
    protected RumourMajorArea[] majorAreas()
    {
        return new RumourMajorArea[]{RumourMajorArea.AVIUM_SAVANNAH};
    }

    @Override
    protected PitfallProfile pitfall()
    {
        return new PitfallProfile()
        {
            @Override public WorldArea area() { return AREA; }
            @Override public int pitObjectId() { return PIT_OBJECT_ID; }
            @Override public int spikedPitObjectId() { return SPIKED_PIT_OBJECT_ID; }
            @Override public int collapsedTrapObjectId() { return COLLAPSED_TRAP_OBJECT_ID; }
            @Override public int lureNpcId() { return SUNLIGHT_ANTELOPE_NPC_ID; }
            @Override public int maxOtherPlayersInArea() { return 2; }
            @Override public int pitExclusionDistance() { return PIT_PLAYER_EXCLUSION_DIST; }
            @Override public int lowHpThreshold() { return 20; }
        };
    }
}

