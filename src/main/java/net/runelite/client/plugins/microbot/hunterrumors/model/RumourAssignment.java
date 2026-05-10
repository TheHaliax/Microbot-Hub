package net.runelite.client.plugins.microbot.hunterrumors.model;

import lombok.Getter;
import net.runelite.client.plugins.microbot.hunterrumors.Huntmaster;
import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;

import java.util.List;

@Getter
public class RumourAssignment
{
    private final Huntmaster huntmaster;
    private final RumourTarget target;
    private final List<String> requiredGear;
    private final List<GearTag> optionalGear;
    private final List<AreaPolygon> candidateAreas;

    public RumourAssignment(
            Huntmaster huntmaster,
            RumourTarget target,
            List<String> requiredGear,
            List<GearTag> optionalGear,
            List<AreaPolygon> candidateAreas)
    {
        this.huntmaster = huntmaster;
        this.target = target;
        this.requiredGear = requiredGear;
        this.optionalGear = optionalGear;
        this.candidateAreas = candidateAreas;
    }
}

