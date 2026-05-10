package net.runelite.client.plugins.microbot.hunterrumors;

import lombok.Getter;

/**
 * Rare creature parts returned to complete a rumour.
 * Names taken from OSRS wiki (string match only for now; item IDs can be added later).
 */
@Getter
public enum RumourRarePart
{
    // Tufts
    KEBBITY_TUFT("Kebbity tuft", 29223),
    CHINCHOMPA_TUFT("Chinchompa tuft"),
    RED_CHINCHOMPA_TUFT("Red chinchompa tuft"),
    HERBY_TUFT("Herby tuft"),

    // Wings
    BLUE_BUTTERFLY_WING("Blue butterfly wing"),
    WHITE_BUTTERFLY_WING("White butterfly wing"),
    BLACK_BUTTERFLY_WING("Black butterfly wing"),
    SUNLIGHT_MOTH_WING("Sunlight moth wing"),
    MOONLIGHT_MOTH_WING("Moonlight moth wing"),

    // Claws
    SWAMP_LIZARD_CLAW("Swamp lizard claw"),
    ORANGE_SALAMANDER_CLAW("Orange salamander claw"),
    RED_SALAMANDER_CLAW("Red salamander claw"),
    SALAMANDER_CLAW("Salamander claw"),

    // Other
    TAILFEATHERS("Tailfeathers"),
    LARUPIA_EAR("Larupia ear"),
    LARGE_JERBOA_TAIL("Large jerboa tail"),
    GRAAHK_HORN_SPUR("Graahk horn spur"),
    KYATT_TOOTH_CHIP("Kyatt tooth chip"),
    FOX_FLUFF("Fox fluff"),
    ANTELOPE_HOOF_SHARD_SUNLIGHT("Antelope hoof shard (sunlight)", 29236),
    ANTELOPE_HOOF_SHARD_MOONLIGHT("Antelope hoof shard (moonlight)");

    private final String itemName;
    private final Integer itemId;

    RumourRarePart(String itemName)
    {
        this.itemName = itemName;
        this.itemId = null;
    }

    RumourRarePart(String itemName, int itemId)
    {
        this.itemName = itemName;
        this.itemId = itemId;
    }
}

