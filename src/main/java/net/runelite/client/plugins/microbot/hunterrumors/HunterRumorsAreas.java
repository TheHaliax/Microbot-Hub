package net.runelite.client.plugins.microbot.hunterrumors;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.hunterrumors.model.AreaPolygon;
import net.runelite.client.plugins.microbot.rs2leaguetransport.LeaguesRegion;

import java.util.Collections;

/**
 * Centralised area definitions for rumour handlers.
 *
 * v0: bounds-only areas (no polygons yet) for quick iteration.
 */
public final class HunterRumorsAreas
{
    private HunterRumorsAreas()
    {
        throw new IllegalStateException("utility");
    }

    // Sunlight antelope (Hunters' Rumours): Avium Savannah, east of Locus Oasis (Varlamore).
    // NOTE: This is a conservative bounds-only approximation; refine via in-game scan when convenient.
    public static final AreaPolygon SUNLIGHT_ANTELOPE_AVIUM_SAVANNAH = new AreaPolygon(
            "Sunlight antelope (Avium Savannah)",
            0,
            Collections.emptyList(),
            new WorldArea(new WorldPoint(1705, 3120, 0), 70, 70),
            new LeaguesRegion[]{LeaguesRegion.VARLAMORE}
    );
}

