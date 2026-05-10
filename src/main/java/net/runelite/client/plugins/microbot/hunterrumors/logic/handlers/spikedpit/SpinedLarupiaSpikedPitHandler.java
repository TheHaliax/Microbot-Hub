package net.runelite.client.plugins.microbot.hunterrumors.logic.handlers.spikedpit;

import net.runelite.client.plugins.microbot.hunterrumors.RumourMajorArea;
import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;
import net.runelite.client.plugins.microbot.hunterrumors.logic.handlers.SpikedPitRumourHandler;

public class SpinedLarupiaSpikedPitHandler extends SpikedPitRumourHandler
{
    @Override
    public RumourTarget getTarget()
    {
        return RumourTarget.SPINED_LARUPIA;
    }

    @Override
    protected RumourMajorArea[] majorAreas()
    {
        return new RumourMajorArea[]{RumourMajorArea.FELDIP_HUNTER_AREA};
    }
}

