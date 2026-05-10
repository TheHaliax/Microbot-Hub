package net.runelite.client.plugins.microbot.hunterrumors.logic.training;

import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;

/**
 * Data-only description of a bird-snare training spot/profile.
 * All bird targets must run through the same runtime pipeline; differences belong here.
 */
public final class BirdTrainingProfile
{
    private final RumourTarget target;
    private final int minHunterLevelInclusive;
    private final int maxHunterLevelInclusive;
    private final int maxSnaresCap;

    public BirdTrainingProfile(
            RumourTarget target,
            int minHunterLevelInclusive,
            int maxHunterLevelInclusive,
            int maxSnaresCap
    )
    {
        if (target == null) { throw new IllegalArgumentException("target"); }
        if (minHunterLevelInclusive < 1 || maxHunterLevelInclusive < minHunterLevelInclusive) { throw new IllegalArgumentException("level range"); }
        if (maxSnaresCap < 1) { throw new IllegalArgumentException("maxSnaresCap"); }

        this.target = target;
        this.minHunterLevelInclusive = minHunterLevelInclusive;
        this.maxHunterLevelInclusive = maxHunterLevelInclusive;
        this.maxSnaresCap = maxSnaresCap;
    }

    public RumourTarget getTarget() { return target; }

    public int getMinHunterLevelInclusive() { return minHunterLevelInclusive; }

    public int getMaxHunterLevelInclusive() { return maxHunterLevelInclusive; }

    public int getMaxSnaresCap() { return maxSnaresCap; }
}

