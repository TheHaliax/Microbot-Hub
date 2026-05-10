package net.runelite.client.plugins.microbot.hunterrumors;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.hunterrumors.model.AreaPolygon;
import net.runelite.client.plugins.microbot.rs2leaguetransport.LeaguesRegion;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Boilerplate "major areas" for rumours, tagged with Leagues regions.
 *
 * v0: bounds-only areas with coarse anchors.
 * Refinement strategy: when we implement a handler, replace its chosen major area with sub-areas/polygons.
 */
public final class HunterRumorsAreaCatalog
{
    private static final int DEFAULT_SIZE = 40;

    private static final Map<RumourMajorArea, AreaPolygon> AREAS = new EnumMap<>(RumourMajorArea.class);

    static
    {
        // Kandarin / Feldip / Piscatoris
        put(RumourMajorArea.FELDIP_HUNTER_AREA, "Feldip Hunter area",
                HunterRumorsLocations.FELDIP_HUNTER_AREA_FAIRY_RING, DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.KANDARIN);

        put(RumourMajorArea.PISCATORIS_HUNTER_AREA, "Piscatoris Hunter area",
                HunterRumorsLocations.PISCATORIS_HUNTER_AREA_FAIRY_RING, DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.KANDARIN);

        put(RumourMajorArea.PISCATORIS_FALCONRY_AREA, "Piscatoris falconry area",
                HunterRumorsLocations.FALCONRY_STILE_TILE, 70, 70,
                LeaguesRegion.KANDARIN);

        // Fremennik
        put(RumourMajorArea.RELLEKKA_HUNTER_AREA, "Rellekka Hunter area",
                new WorldPoint(2680, 3701, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.FREMENNIK);

        put(RumourMajorArea.WEISS, "Weiss",
                new WorldPoint(2847, 3935, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.FREMENNIK);

        put(RumourMajorArea.MONS_GRATIA, "Mons Gratia",
                new WorldPoint(2872, 3922, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.FREMENNIK);

        // Kourend
        put(RumourMajorArea.FARMING_GUILD, "Farming Guild",
                new WorldPoint(1242, 3720, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.KOUREND);

        put(RumourMajorArea.KOUREND_WOODLAND, "Kourend Woodland",
                new WorldPoint(1632, 3572, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.KOUREND);

        // Morytania
        put(RumourMajorArea.CANIFIS_HUNTER_AREA, "Canifis Hunter area",
                new WorldPoint(3515, 3512, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.MORYTANIA);

        put(RumourMajorArea.NORTH_WEST_OF_SLEPE, "North-west of Slepe",
                new WorldPoint(3650, 3330, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.MORYTANIA);

        // Desert
        put(RumourMajorArea.UZER_HUNTER_AREA, "Uzer Hunter area",
                new WorldPoint(3422, 3159, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.DESERT);

        put(RumourMajorArea.NECROPOLIS_HUNTER_AREA, "Necropolis Hunter area",
                new WorldPoint(3539, 2892, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.DESERT);

        // Ourania / ZMI is Kandarin (anchor: red salamander east; south tile in HunterRumorsLocations)
        put(RumourMajorArea.OURANIA_HUNTER_AREA, "Ourania Hunter area",
                HunterRumorsLocations.RED_SALAMANDER_OURANIA_EAST, DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.KANDARIN);

        // Tirannwn
        put(RumourMajorArea.GWENITH_HUNTER_AREA, "Gwenith Hunter area",
                new WorldPoint(2230, 3160, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.TIRANNWN);

        // Misthalin / Misc
        put(RumourMajorArea.ISLE_OF_SOULS, "Isle of Souls",
                new WorldPoint(2210, 2858, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.MISTHALIN);

        put(RumourMajorArea.MUSHROOM_FOREST_FOSSIL_ISLAND, "Mushroom Forest (Fossil Island)",
                new WorldPoint(3711, 3876, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.MISTHALIN);

        // Karamja
        put(RumourMajorArea.KARAMJA_HUNTER_AREA, "Karamja Hunter area",
                new WorldPoint(2816, 2944, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.KARAMJA);

        // Varlamore
        put(RumourMajorArea.AVIUM_SAVANNAH, "Avium Savannah",
                new WorldPoint(1705, 3120, 0), 80, 80,
                LeaguesRegion.VARLAMORE);

        put(RumourMajorArea.WEST_OF_HUNTER_GUILD, "West of Hunter Guild",
                new WorldPoint(1525, 3060, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.VARLAMORE);

        put(RumourMajorArea.NORTH_WEST_OF_LOCUS_OASIS, "North-west of Locus Oasis",
                new WorldPoint(1675, 3135, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.VARLAMORE);

        put(RumourMajorArea.NEYPOTZLI, "Neypotzli",
                new WorldPoint(1615, 3145, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.VARLAMORE);

        put(RumourMajorArea.RALOS_RISE, "Ralos' Rise",
                new WorldPoint(1460, 3190, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.VARLAMORE);

        put(RumourMajorArea.THE_BURROW, "The Burrow",
                new WorldPoint(1400, 3210, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.VARLAMORE);

        // Specific chinchompas
        put(RumourMajorArea.RED_CHINCHOMPA_HUNTING_GROUND, "Red chinchompa hunting ground",
                new WorldPoint(2528, 2881, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.KANDARIN);

        // Feldip Hills red chinchompa spots (Hal, 4 anchors)
        put(RumourMajorArea.RED_CHINCHOMPA_FELDIP_1, "Red chinchompa (Feldip #1)",
                new WorldPoint(2556, 2934, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.KANDARIN);
        put(RumourMajorArea.RED_CHINCHOMPA_FELDIP_2, "Red chinchompa (Feldip #2)",
                new WorldPoint(2556, 2911, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.KANDARIN);
        put(RumourMajorArea.RED_CHINCHOMPA_FELDIP_3, "Red chinchompa (Feldip #3)",
                new WorldPoint(2497, 2902, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.KANDARIN);
        put(RumourMajorArea.RED_CHINCHOMPA_FELDIP_4, "Red chinchompa (Feldip #4)",
                new WorldPoint(2502, 2889, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.KANDARIN);

        // Tlati Rainforest (Varlamore) red chinchompa spots (Hal, 2 anchors)
        put(RumourMajorArea.RED_CHINCHOMPA_TLATI_1, "Red chinchompa (Tlati #1)",
                new WorldPoint(1314, 3171, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.VARLAMORE);
        put(RumourMajorArea.RED_CHINCHOMPA_TLATI_2, "Red chinchompa (Tlati #2)",
                new WorldPoint(1320, 3164, 0), DEFAULT_SIZE, DEFAULT_SIZE,
                LeaguesRegion.VARLAMORE);
    }

    private HunterRumorsAreaCatalog()
    {
        throw new IllegalStateException("utility");
    }

    public static AreaPolygon get(RumourMajorArea area)
    {
        return Optional.ofNullable(area)
                .map(AREAS::get)
                .orElse(null);
    }

    private static void put(
            RumourMajorArea key,
            String name,
            WorldPoint anchor,
            int width,
            int height,
            LeaguesRegion... regions
    )
    {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(anchor, "anchor");

        int w = Math.max(6, width);
        int h = Math.max(6, height);

        int x = anchor.getX() - (w / 2);
        int y = anchor.getY() - (h / 2);

        AREAS.put(key, new AreaPolygon(
                name,
                anchor.getPlane(),
                java.util.Collections.emptyList(),
                new WorldArea(x, y, w, h, anchor.getPlane()),
                regions
        ));
    }
}

