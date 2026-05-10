package net.runelite.client.plugins.microbot.hunterrumors;

/**
 * Minimal placeholder. We'll expand + attach handlers as we implement rumours in-game.
 */
public enum RumourTarget
{
    NONE,

    // Birds
    TROPICAL_WAGTAIL,
    CRIMSON_SWIFT,
    SAPPHIRE_GLACIALIS,
    SNOWY_KNIGHT,
    BLACK_WARLOCK,
    CERULEAN_TWITCH,

    // Kebbits / deadfall / tracking / falconry
    WILD_KEBBIT,
    BARB_TAILED_KEBBIT,
    PRICKLY_KEBBIT,
    SABRE_TOOTHED_KEBBIT,
    DARK_KEBBIT,
    DASHING_KEBBIT,

    // Salamanders / lizards
    SWAMP_LIZARD,
    ORANGE_SALAMANDER,
    RED_SALAMANDER,
    TECU_SALAMANDER,

    // Antelope
    SUNLIGHT_ANTELOPE,
    MOONLIGHT_ANTELOPE,

    // Chinchompas
    CARNIVOROUS_CHINCHOMPA,
    GREY_CHINCHOMPA,
    RED_CHINCHOMPA,

    // Other
    PYRE_FOX,
    HORNED_GRAAHK,
    SPINED_LARUPIA,
    SABRE_TOOTHED_KYATT,
    EMBERTAILED_JERBOA,
    HERBIBOAR,

    // Moths
    SUNLIGHT_MOTH,
    MOONLIGHT_MOTH
    ;

    public RumourRarePart rarePart()
    {
        switch (this)
        {
            // Birds (snare): wiki lists Tailfeathers for bird rumours
            case TROPICAL_WAGTAIL:
            case CRIMSON_SWIFT:
            case SAPPHIRE_GLACIALIS:
            case SNOWY_KNIGHT:
            case BLACK_WARLOCK:
            case CERULEAN_TWITCH:
                return RumourRarePart.TAILFEATHERS;

            // Kebbits (deadfall/falconry/tracking)
            case WILD_KEBBIT:
            case BARB_TAILED_KEBBIT:
            case PRICKLY_KEBBIT:
            case SABRE_TOOTHED_KEBBIT:
            case DARK_KEBBIT:
            case DASHING_KEBBIT:
                return RumourRarePart.KEBBITY_TUFT;

            // Salamanders / lizards
            case SWAMP_LIZARD:
                return RumourRarePart.SWAMP_LIZARD_CLAW;
            case ORANGE_SALAMANDER:
                return RumourRarePart.ORANGE_SALAMANDER_CLAW;
            case RED_SALAMANDER:
                return RumourRarePart.RED_SALAMANDER_CLAW;
            case TECU_SALAMANDER:
                return RumourRarePart.SALAMANDER_CLAW;

            // Antelope
            case SUNLIGHT_ANTELOPE:
                return RumourRarePart.ANTELOPE_HOOF_SHARD_SUNLIGHT;
            case MOONLIGHT_ANTELOPE:
                return RumourRarePart.ANTELOPE_HOOF_SHARD_MOONLIGHT;

            // Chinchompas
            case CARNIVOROUS_CHINCHOMPA:
            case GREY_CHINCHOMPA:
                return RumourRarePart.CHINCHOMPA_TUFT;
            case RED_CHINCHOMPA:
                return RumourRarePart.RED_CHINCHOMPA_TUFT;

            // Other
            case PYRE_FOX:
                return RumourRarePart.FOX_FLUFF;
            case HORNED_GRAAHK:
                return RumourRarePart.GRAAHK_HORN_SPUR;
            case SPINED_LARUPIA:
                return RumourRarePart.LARUPIA_EAR;
            case SABRE_TOOTHED_KYATT:
                return RumourRarePart.KYATT_TOOTH_CHIP;
            case EMBERTAILED_JERBOA:
                return RumourRarePart.LARGE_JERBOA_TAIL;
            case HERBIBOAR:
                return RumourRarePart.HERBY_TUFT;

            // Moths
            case SUNLIGHT_MOTH:
                return RumourRarePart.SUNLIGHT_MOTH_WING;
            case MOONLIGHT_MOTH:
                return RumourRarePart.MOONLIGHT_MOTH_WING;

            default:
                return null;
        }
    }
}

