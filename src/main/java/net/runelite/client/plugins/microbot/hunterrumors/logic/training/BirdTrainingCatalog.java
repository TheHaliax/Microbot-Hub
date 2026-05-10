package net.runelite.client.plugins.microbot.hunterrumors.logic.training;

import net.runelite.client.plugins.microbot.hunterrumors.RumourTarget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Static ordered catalog for Hunter < 46 bird training.
 * Adding new birds/regions should be data-only changes here.
 */
public final class BirdTrainingCatalog
{
    private static final List<BirdTrainingProfile> PROFILES = build();

    private BirdTrainingCatalog() {}

    public static List<BirdTrainingProfile> profiles()
    {
        return PROFILES;
    }

    public static BirdTrainingProfile pickForLevel(int hunterLevel)
    {
        for (int i = 0; i < PROFILES.size(); i++)
        {
            BirdTrainingProfile p = PROFILES.get(i);
            if (hunterLevel >= p.getMinHunterLevelInclusive() && hunterLevel <= p.getMaxHunterLevelInclusive())
            {
                return p;
            }
        }
        return null;
    }

    private static List<BirdTrainingProfile> build()
    {
        // Note: using a mutable builder list here is init-time only.
        // Centers are provided by `logic/catalog/HuntLocations` (single source of truth).
        List<BirdTrainingProfile> list = new ArrayList<>();

        final int defaultMaxSnaresCap = 5;

        // Feldip
        list.add(new BirdTrainingProfile(RumourTarget.TROPICAL_WAGTAIL, 1, 45, defaultMaxSnaresCap));
        list.add(new BirdTrainingProfile(RumourTarget.CRIMSON_SWIFT, 1, 45, defaultMaxSnaresCap));

        // Mons Gratia
        list.add(new BirdTrainingProfile(RumourTarget.CERULEAN_TWITCH, 1, 45, defaultMaxSnaresCap));
        list.add(new BirdTrainingProfile(RumourTarget.SNOWY_KNIGHT, 1, 45, defaultMaxSnaresCap));
        list.add(new BirdTrainingProfile(RumourTarget.SAPPHIRE_GLACIALIS, 1, 45, defaultMaxSnaresCap));

        return Collections.unmodifiableList(list);
    }
}

