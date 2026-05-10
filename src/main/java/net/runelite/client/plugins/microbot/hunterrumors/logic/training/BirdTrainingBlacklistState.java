package net.runelite.client.plugins.microbot.hunterrumors.logic.training;

import net.runelite.api.coords.WorldPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory per-center cooldown + failure tracking.
 * Not persisted by design; resets on plugin restart.
 */
public final class BirdTrainingBlacklistState
{
    private static final class CenterState
    {
        int consecutiveFailures;
        long unavailableUntilMillis;
    }

    private final Map<WorldPoint, CenterState> stateByCenter = new HashMap<>();

    public boolean isAvailable(WorldPoint center, long nowMillis)
    {
        if (center == null) { return false; }
        CenterState st = stateByCenter.get(center);
        if (st == null) { return true; }
        return nowMillis >= st.unavailableUntilMillis;
    }

    public void recordFailure(WorldPoint center, long nowMillis, int failureLimit, int cooldownSeconds)
    {
        if (center == null) { return; }
        if (failureLimit < 1) { return; }
        if (cooldownSeconds < 0) { return; }

        CenterState st = stateByCenter.computeIfAbsent(center, c -> new CenterState());
        st.consecutiveFailures++;

        if (st.consecutiveFailures >= failureLimit)
        {
            st.unavailableUntilMillis = nowMillis + (cooldownSeconds * 1000L);
            st.consecutiveFailures = 0;
        }
    }

    public void recordSuccess(WorldPoint center)
    {
        if (center == null) { return; }
        CenterState st = stateByCenter.get(center);
        if (st != null)
        {
            st.consecutiveFailures = 0;
        }
    }
}

