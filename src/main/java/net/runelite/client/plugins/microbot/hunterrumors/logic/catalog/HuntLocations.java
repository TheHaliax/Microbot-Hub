package net.runelite.client.plugins.microbot.hunterrumors.logic.catalog;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.hunterrumors.HunterRumorsConfig;
import net.runelite.client.plugins.microbot.rs2leaguetransport.LeaguesRegion;
import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;

import java.util.Collections;
import java.util.List;

/**
 * Hardcoded, survey-derived start locations.
 *
 * Keying:
 * - type: handler family (box trap / bird snare / etc.)
 * - subtype: {@link RumourTarget} (actual target)
 * - region gating: {@link LeaguesRegion} unlocks when leaguesEnabled
 *
 * NOTE: These are only "start anchors". Runtime logic can pin to one until
 * crowding/contested signals force rotation.
 */
public final class HuntLocations
{
    private HuntLocations() {}

    public static List<WorldPoint> centers(HuntMethodType type, RumourTarget subtype, HunterRumorsConfig config)
    {
        if (type == null || subtype == null)
        {
            return Collections.emptyList();
        }

        // Bird snare anchors
        if (type == HuntMethodType.BIRD_SNARE)
        {
            switch (subtype)
            {
                case TROPICAL_WAGTAIL:
                    return gated(config, LeaguesRegion.KANDARIN, List.of(
                            // Feldip hunter area digest -> suggested_centers (3x3 grid free)
                            new WorldPoint(2520, 2945, 0),
                            new WorldPoint(2505, 2882, 0)
                    ));
                case CRIMSON_SWIFT:
                    return gated(config, LeaguesRegion.KANDARIN, List.of(
                            // Feldip hunter area digest -> suggested_centers
                            new WorldPoint(2607, 2928, 0)
                    ));
                case CERULEAN_TWITCH:
                    // NOTE: not Leagues-gated yet (no ASGARNIA in LeaguesRegion enum).
                    return List.of(
                            // Mons Gratia digest -> suggested_centers
                            new WorldPoint(1447, 3243, 0)
                    );
                case SNOWY_KNIGHT:
                    // NOTE: not Leagues-gated yet (no ASGARNIA in LeaguesRegion enum).
                    return List.of(
                            // Mons Gratia digest -> suggested_centers
                            new WorldPoint(1447, 3239, 0),
                            new WorldPoint(1430, 3256, 0)
                    );
                case SAPPHIRE_GLACIALIS:
                    // NOTE: not Leagues-gated yet (no ASGARNIA in LeaguesRegion enum).
                    return List.of(
                            // Mons Gratia digest -> suggested_centers
                            new WorldPoint(1448, 3239, 0),
                            new WorldPoint(1435, 3261, 0)
                    );
                case BLACK_WARLOCK:
                    return gated(config, LeaguesRegion.VARLAMORE, List.of(
                            // Tlati rainforest digest -> suggested_centers
                            new WorldPoint(1295, 3106, 0)
                    ));
                default:
                    return Collections.emptyList();
            }
        }

        // Box trap anchors (chins)
        if (type == HuntMethodType.BOX_TRAP)
        {
            switch (subtype)
            {
                case CARNIVOROUS_CHINCHOMPA:
                    return gated(config, LeaguesRegion.KANDARIN, List.of(
                            // Feldip hunter area digest -> suggested_centers
                            new WorldPoint(2555, 2934, 0)
                    ));
                default:
                    return Collections.emptyList();
            }
        }

        return Collections.emptyList();
    }

    public static List<WorldPointPair> deadfallPairs(RumourTarget subtype, HunterRumorsConfig config)
    {
        if (subtype == null)
        {
            return Collections.emptyList();
        }

        // Feldip hunter area deadfall boulder pairs (object id 19215) from survey digest.
        // Treat these as candidate anchors; handler chooses which pair to use.
        switch (subtype)
        {
            case BARB_TAILED_KEBBIT:
            case PRICKLY_KEBBIT:
            case SABRE_TOOTHED_KEBBIT:
            case DARK_KEBBIT:
            case DASHING_KEBBIT:
                return gatedPairs(config, LeaguesRegion.KANDARIN, List.of(
                        new WorldPointPair(new WorldPoint(2576, 2925, 0), new WorldPoint(2576, 2926, 0)),
                        new WorldPointPair(new WorldPoint(2566, 2902, 0), new WorldPoint(2566, 2903, 0)),
                        new WorldPointPair(new WorldPoint(2573, 2897, 0), new WorldPoint(2574, 2897, 0)),
                        new WorldPointPair(new WorldPoint(2583, 2913, 0), new WorldPoint(2584, 2913, 0)),
                        new WorldPointPair(new WorldPoint(2579, 2885, 0), new WorldPoint(2580, 2885, 0)),
                        new WorldPointPair(new WorldPoint(2574, 2910, 0), new WorldPoint(2575, 2910, 0)),
                        new WorldPointPair(new WorldPoint(2574, 2916, 0), new WorldPoint(2574, 2917, 0)),
                        new WorldPointPair(new WorldPoint(2572, 2930, 0), new WorldPoint(2573, 2930, 0))
                ));
            default:
                return Collections.emptyList();
        }
    }

    public static List<WorldPoint> pitfallPits(RumourTarget subtype, HunterRumorsConfig config)
    {
        if (subtype == null)
        {
            return Collections.emptyList();
        }

        // Feldip pit tiles from object_index example tiles for pit object ids (19260 etc).
        // Handler will validate pit/spiked/collapsed variants at runtime.
        switch (subtype)
        {
            case SPINED_LARUPIA:
            case HORNED_GRAAHK:
            case SABRE_TOOTHED_KYATT:
            case SUNLIGHT_ANTELOPE:
            case MOONLIGHT_ANTELOPE:
                return gated(config, LeaguesRegion.KANDARIN, List.of(
                        new WorldPoint(2543, 2908, 0),
                        new WorldPoint(2543, 2909, 0),
                        new WorldPoint(2544, 2908, 0),
                        new WorldPoint(2544, 2909, 0)
                ));
            default:
                return Collections.emptyList();
        }
    }

    private static List<WorldPoint> gated(HunterRumorsConfig config, LeaguesRegion region, List<WorldPoint> centers)
    {
        if (centers == null || centers.isEmpty())
        {
            return Collections.emptyList();
        }
        if (config == null || region == null)
        {
            return centers;
        }
        if (!config.leaguesEnabled())
        {
            return centers;
        }
        if (!isRegionUnlocked(config, region))
        {
            return Collections.emptyList();
        }
        return centers;
    }

    private static List<WorldPointPair> gatedPairs(HunterRumorsConfig config, LeaguesRegion region, List<WorldPointPair> pairs)
    {
        if (pairs == null || pairs.isEmpty())
        {
            return Collections.emptyList();
        }
        if (config == null || region == null)
        {
            return pairs;
        }
        if (!config.leaguesEnabled())
        {
            return pairs;
        }
        if (!isRegionUnlocked(config, region))
        {
            return Collections.emptyList();
        }
        return pairs;
    }

    private static boolean isRegionUnlocked(HunterRumorsConfig config, LeaguesRegion region)
    {
        switch (region)
        {
            case MISTHALIN:
                return config.unlockedMisthalin();
            case KANDARIN:
                return config.unlockedKandarin();
            case FREMENNIK:
                return config.unlockedFremennik();
            case KARAMJA:
                return config.unlockedKaramja();
            case DESERT:
                return config.unlockedDesert();
            case KOUREND:
                return config.unlockedKourend();
            case TIRANNWN:
                return config.unlockedTirannwn();
            case MORYTANIA:
                return config.unlockedMorytania();
            case VARLAMORE:
                return config.unlockedVarlamore();
        }
        return true;
    }
}

