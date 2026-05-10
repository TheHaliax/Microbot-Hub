package net.runelite.client.plugins.microbot.hunterrumors.logic.catalog;

import net.runelite.api.coords.WorldPoint;

/**
 * Immutable pair of world points (e.g. deadfall boulder adjacency).
 */
public final class WorldPointPair
{
    private final WorldPoint a;
    private final WorldPoint b;

    public WorldPointPair(WorldPoint a, WorldPoint b)
    {
        if (a == null) { throw new IllegalArgumentException("a"); }
        if (b == null) { throw new IllegalArgumentException("b"); }
        this.a = a;
        this.b = b;
    }

    public WorldPoint getA() { return a; }

    public WorldPoint getB() { return b; }
}

