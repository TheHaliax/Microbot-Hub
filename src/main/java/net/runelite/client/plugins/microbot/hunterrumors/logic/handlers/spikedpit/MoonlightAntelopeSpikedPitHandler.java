package net.runelite.client.plugins.microbot.hunterrumors.logic.handlers.spikedpit;

import net.runelite.client.plugins.microbot.hunterrumors.RumourMajorArea;
import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;
import net.runelite.client.plugins.microbot.hunterrumors.logic.handlers.SpikedPitRumourHandler;

public class MoonlightAntelopeSpikedPitHandler extends SpikedPitRumourHandler
{
    @Override
    public RumourTarget getTarget()
    {
        return RumourTarget.MOONLIGHT_ANTELOPE;
    }

    @Override
    protected RumourMajorArea[] majorAreas()
    {
        return new RumourMajorArea[]{RumourMajorArea.THE_BURROW};
    }
}

