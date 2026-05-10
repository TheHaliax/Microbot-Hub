package net.runelite.client.plugins.microbot.hunterrumors.logic.handlers.spikedpit;

import net.runelite.client.plugins.microbot.hunterrumors.RumourMajorArea;
import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;
import net.runelite.client.plugins.microbot.hunterrumors.logic.handlers.SpikedPitRumourHandler;

public class HornedGraahkSpikedPitHandler extends SpikedPitRumourHandler
{
    @Override
    public RumourTarget getTarget()
    {
        return RumourTarget.HORNED_GRAAHK;
    }

    @Override
    protected RumourMajorArea[] majorAreas()
    {
        return new RumourMajorArea[]{RumourMajorArea.KARAMJA_HUNTER_AREA};
    }
}

