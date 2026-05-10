package net.runelite.client.plugins.microbot.hunterrumors;

import net.runelite.api.coords.WorldPoint;
import net.runelite.api.coords.WorldArea;
import java.util.Arrays;
import java.util.List;
import net.runelite.client.plugins.microbot.hunterrumors.model.AreaPolygon;
import net.runelite.client.plugins.microbot.rs2leaguetransport.LeaguesRegion;

public final class HunterRumorsLocations
{
    private HunterRumorsLocations()
    {
        throw new IllegalStateException("utility");
    }

    // Closest fairy ring to Hunter's Guild (per Hal).
    public static final WorldPoint HUNTERS_GUILD_FAIRY_RING = new WorldPoint(1651, 3010, 0);

    // Piscatoris Hunter/Falconry area fairy ring (per Hal). (code: AKQ)
    public static final WorldPoint PISCATORIS_HUNTER_AREA_FAIRY_RING = new WorldPoint(2319, 3619, 0);

    // Feldip Hunter area fairy ring (per Hal). (code: AKS)
    public static final int FELDIP_HUNTER_AREA_FAIRY_RING_OBJECT_ID = 29445;
    public static final WorldPoint FELDIP_HUNTER_AREA_FAIRY_RING = new WorldPoint(2571, 2956, 0);

    /**
     * Red salamander — Ourania / ZMI hunter tiles (local). Rumor plugin uses these only;
     * do not depend on {@code net.runelite.client.plugins.microbot.util.walker.enums.Salamanders}.
     */
    public static final WorldPoint RED_SALAMANDER_OURANIA_EAST = new WorldPoint(2450, 3223, 0);
    public static final WorldPoint RED_SALAMANDER_OURANIA_SOUTH = new WorldPoint(2470, 3243, 0);

    // Falconry entrance stile (per Hal).
    public static final int FALCONRY_STILE_OBJECT_ID = 19222;
    public static final WorldPoint FALCONRY_STILE_TILE = new WorldPoint(2371, 3620, 0);
    public static final WorldArea FALCONRY_STILE_AREA = new WorldArea(2371, 3620, 1, 1, 0);

    // Falconry enclosure bounds built from fence IDs (4262, 8969, 8970, 8971) scan.
    public static final WorldArea FALCONRY_AREA = new WorldArea(2364, 3573, 31, 48, 0);

    // Falconry fence polygon corners (ordered), from Dev Shell scan (fence tiles=160, corners=87).
    public static final List<WorldPoint> FALCONRY_POLYGON = Arrays.asList(
            new WorldPoint(2371, 3573, 0),
            new WorldPoint(2378, 3573, 0),
            new WorldPoint(2378, 3574, 0),
            new WorldPoint(2379, 3574, 0),
            new WorldPoint(2379, 3575, 0),
            new WorldPoint(2381, 3575, 0),
            new WorldPoint(2381, 3579, 0),
            new WorldPoint(2382, 3579, 0),
            new WorldPoint(2382, 3580, 0),
            new WorldPoint(2383, 3580, 0),
            new WorldPoint(2383, 3581, 0),
            new WorldPoint(2384, 3581, 0),
            new WorldPoint(2384, 3582, 0),
            new WorldPoint(2385, 3582, 0),
            new WorldPoint(2385, 3586, 0),
            new WorldPoint(2386, 3586, 0),
            new WorldPoint(2386, 3587, 0),
            new WorldPoint(2387, 3587, 0),
            new WorldPoint(2387, 3588, 0),
            new WorldPoint(2390, 3588, 0),
            new WorldPoint(2390, 3587, 0),
            new WorldPoint(2391, 3587, 0),
            new WorldPoint(2391, 3586, 0),
            new WorldPoint(2394, 3586, 0),
            new WorldPoint(2394, 3587, 0),
            new WorldPoint(2395, 3587, 0),
            new WorldPoint(2395, 3590, 0),
            new WorldPoint(2394, 3590, 0),
            new WorldPoint(2394, 3595, 0),
            new WorldPoint(2393, 3595, 0),
            new WorldPoint(2393, 3598, 0),
            new WorldPoint(2391, 3598, 0),
            new WorldPoint(2391, 3602, 0),
            new WorldPoint(2390, 3602, 0),
            new WorldPoint(2390, 3603, 0),
            new WorldPoint(2389, 3603, 0),
            new WorldPoint(2389, 3604, 0),
            new WorldPoint(2388, 3604, 0),
            new WorldPoint(2388, 3605, 0),
            new WorldPoint(2385, 3605, 0),
            new WorldPoint(2385, 3610, 0),
            new WorldPoint(2384, 3610, 0),
            new WorldPoint(2384, 3611, 0),
            new WorldPoint(2383, 3611, 0),
            new WorldPoint(2383, 3612, 0),
            new WorldPoint(2382, 3612, 0),
            new WorldPoint(2382, 3613, 0),
            new WorldPoint(2381, 3613, 0),
            new WorldPoint(2381, 3614, 0),
            new WorldPoint(2380, 3614, 0),
            new WorldPoint(2380, 3615, 0),
            new WorldPoint(2379, 3615, 0),
            new WorldPoint(2379, 3616, 0),
            new WorldPoint(2378, 3616, 0),
            new WorldPoint(2378, 3621, 0),
            new WorldPoint(2372, 3621, 0),
            new WorldPoint(2372, 3620, 0),
            new WorldPoint(2377, 3620, 0),
            new WorldPoint(2377, 3615, 0),
            new WorldPoint(2378, 3615, 0),
            new WorldPoint(2378, 3614, 0),
            new WorldPoint(2379, 3614, 0),
            new WorldPoint(2379, 3613, 0),
            new WorldPoint(2380, 3613, 0),
            new WorldPoint(2380, 3612, 0),
            new WorldPoint(2381, 3612, 0),
            new WorldPoint(2381, 3611, 0),
            new WorldPoint(2382, 3611, 0),
            new WorldPoint(2382, 3610, 0),
            new WorldPoint(2383, 3610, 0),
            new WorldPoint(2383, 3609, 0),
            new WorldPoint(2384, 3609, 0),
            new WorldPoint(2384, 3604, 0),
            new WorldPoint(2387, 3604, 0),
            new WorldPoint(2387, 3603, 0),
            new WorldPoint(2388, 3603, 0),
            new WorldPoint(2388, 3602, 0),
            new WorldPoint(2389, 3602, 0),
            new WorldPoint(2389, 3601, 0),
            new WorldPoint(2390, 3601, 0),
            new WorldPoint(2390, 3597, 0),
            new WorldPoint(2392, 3597, 0),
            new WorldPoint(2392, 3594, 0),
            new WorldPoint(2393, 3594, 0),
            new WorldPoint(2393, 3589, 0),
            new WorldPoint(2394, 3589, 0),
            new WorldPoint(2394, 3587, 0)
    );

    public static final AreaPolygon FALCONRY_AREA_POLYGON =
            new AreaPolygon("Falconry (Piscatoris)", 0, FALCONRY_POLYGON, FALCONRY_AREA, new LeaguesRegion[]{LeaguesRegion.KANDARIN});
}

