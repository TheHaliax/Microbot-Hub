package net.runelite.client.plugins.microbot.hunterrumors.logic.handlers.spikedpit;

import net.runelite.client.plugins.microbot.hunterrumors.RumourMajorArea;
import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;
import net.runelite.client.plugins.microbot.hunterrumors.logic.handlers.SpikedPitRumourHandler;

public class SabreToothedKyattSpikedPitHandler extends SpikedPitRumourHandler
{
    @Override
    public RumourTarget getTarget()
    {
        return RumourTarget.SABRE_TOOTHED_KYATT;
    }

    @Override
    protected RumourMajorArea[] majorAreas()
    {
        return new RumourMajorArea[]{RumourMajorArea.RELLEKKA_HUNTER_AREA};
    }
}

