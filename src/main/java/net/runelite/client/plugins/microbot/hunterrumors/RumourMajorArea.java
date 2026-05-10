package net.runelite.client.plugins.microbot.hunterrumors;

/**
 * "Major area" buckets from the Hunters' Rumours wiki table.
 *
 * These are used to quickly wire handlers to a known region set (Leagues) and a rough travel anchor.
 * Exact sub-spots (e.g. multiple chinchompa locations in Feldip) can be split later while keeping the same bucket.
 */
public enum RumourMajorArea
{
    // Kandarin / Feldip / Piscatoris
    FELDIP_HUNTER_AREA,
    PISCATORIS_HUNTER_AREA,
    PISCATORIS_FALCONRY_AREA,

    // Fremennik
    RELLEKKA_HUNTER_AREA,
    WEISS,
    MONS_GRATIA,

    // Kourend
    FARMING_GUILD,
    KOUREND_WOODLAND,

    // Morytania
    CANIFIS_HUNTER_AREA,
    NORTH_WEST_OF_SLEPE,

    // Desert
    UZER_HUNTER_AREA,
    NECROPOLIS_HUNTER_AREA,

    // Ourania / ZMI (Kandarin)
    OURANIA_HUNTER_AREA,

    // Tirannwn
    GWENITH_HUNTER_AREA,

    // Misthalin
    ISLE_OF_SOULS,
    MUSHROOM_FOREST_FOSSIL_ISLAND,

    // Karamja
    KARAMJA_HUNTER_AREA,

    // Varlamore
    AVIUM_SAVANNAH,
    WEST_OF_HUNTER_GUILD,
    NORTH_WEST_OF_LOCUS_OASIS,
    NEYPOTZLI,
    RALOS_RISE,
    THE_BURROW,

    // Specific chinch areas
    RED_CHINCHOMPA_HUNTING_GROUND,
    RED_CHINCHOMPA_FELDIP_1,
    RED_CHINCHOMPA_FELDIP_2,
    RED_CHINCHOMPA_FELDIP_3,
    RED_CHINCHOMPA_FELDIP_4,
    RED_CHINCHOMPA_TLATI_1,
    RED_CHINCHOMPA_TLATI_2
}

