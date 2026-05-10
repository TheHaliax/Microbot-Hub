package net.runelite.client.plugins.microbot.hunterrumors.logic.handlers;

import net.runelite.client.plugins.microbot.hunterrumors.HunterRumorsConfig;
import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;
import net.runelite.client.plugins.microbot.hunterrumors.logic.catalog.HuntMethodType;

/**
 * One handler per {@link HuntMethodType}.
 * Implementations should do at most one bounded action per tick.
 */
public interface HuntMethodHandler
{
    HuntMethodType type();

    boolean step(RumourTarget target, HunterRumorsConfig config);
}

