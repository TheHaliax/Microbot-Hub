package net.runelite.client.plugins.microbot.pyramidplunder;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("pyramidplunder")
public interface PyramidPlunderConfig extends Config {
    enum SarcophagusMode {
        NONE,
        HIGHEST_2,
        HIGHEST_5
    }
    enum ChestMode {
        ALL,
        HIGHEST_2,
        HIGHEST_5
    }
    enum UrnMode {
        NONE,
        HIGHEST_1,
        HIGHEST_2,
        TIMER_BASED
    }
    @ConfigSection(
            name = "General Settings",
            description = "General settings for the Pyramid Plunder module",
            position = 0
    )
    String generalSection = "general";

    @ConfigSection(
            name = "Loot Settings",
            description = "Settings for what to loot and keep",
            position = 1
    )
    String lootSection = "loot";

    @ConfigItem(
            keyName = "keepValuableArtefacts",
            name = "Keep Valuable Artefacts",
            description = "Keep only valuable artefacts (ignore low-value loot)",
            section = lootSection,
            position = 0
    )
    default boolean keepValuableArtefacts() { return true; }

    @ConfigItem(
            keyName = "keepIvoryComb",
            name = "Keep Ivory Comb",
            description = "Keep Ivory Comb artefacts (otherwise drop them)",
            section = lootSection,
            position = 1
    )
    default boolean keepIvoryComb() { return false; }

    @ConfigSection(
            name = "Overlay & Highlight Settings",
            description = "Overlay and highlight settings for Pyramid Plunder",
            position = 2
    )
    String overlaySection = "overlay";

    @ConfigItem(
            keyName = "hideTimer",
            name = "Hide default timer",
            description = "Hides the default pyramid plunder timer.",
            section = overlaySection,
            position = 0
    )
    default boolean hideTimer() { return true; }

    @ConfigItem(
            keyName = "showExactTimer",
            name = "Show exact timer",
            description = "Displays the amount of time remaining as an infobox.",
            section = overlaySection,
            position = 1
    )
    default boolean showExactTimer() { return true; }

    @ConfigItem(
            keyName = "timerLowWarning",
            name = "Timer low warning",
            description = "Determines the time when the timers color will change.",
            section = overlaySection,
            position = 2
    )
    default int timerLowWarning() { return 30; }

    @Alpha
    @ConfigItem(
            keyName = "highlightDoorsColor",
            name = "Highlight doors color",
            description = "Selects the color for highlighting tomb doors.",
            section = overlaySection,
            position = 3
    )
    default Color highlightDoorsColor() { return Color.green; }

    @ConfigItem(
            keyName = "highlightDoors",
            name = "Highlight doors",
            description = "Highlights the four tomb doors in each room.",
            section = overlaySection,
            position = 4
    )
    default boolean highlightDoors() { return true; }

    @Alpha
    @ConfigItem(
            keyName = "highlightSpeartrapColor",
            name = "Highlight speartrap color",
            description = "Selects the color for highlighting speartraps.",
            section = overlaySection,
            position = 5
    )
    default Color highlightSpeartrapsColor() { return Color.orange; }

    @ConfigItem(
            keyName = "highlightSpeartraps",
            name = "Highlight speartraps",
            description = "Highlight the spear traps at the entrance of each room.",
            section = overlaySection,
            position = 6
    )
    default boolean highlightSpeartraps() { return true; }

    @Alpha
    @ConfigItem(
            keyName = "highlightContainersColor",
            name = "Highlight containers color",
            description = "Selects the color for highlighting urns, chests and sarcophagus.",
            section = overlaySection,
            position = 7
    )
    default Color highlightContainersColor() { return Color.yellow; }

    @ConfigItem(
            keyName = "urnMode",
            name = "Urn Logic",
            description = "Urns: Highest floor, highest 2 floors, or timer based",
            section = overlaySection,
            position = 8
    )
    default UrnMode urnMode() { return UrnMode.HIGHEST_1; }



    @ConfigItem(
            keyName = "chestMode",
            name = "Chest Logic",
            description = "Chest: All, highest 2, or 5 highest available rooms",
            section = overlaySection,
            position = 9
    )
    default ChestMode chestMode() { return ChestMode.ALL; }

    @ConfigItem(
            keyName = "sarcophagusMode",
            name = "Sarcophagus Logic",
            description = "Sarcophagus: None, highest 2, or 5 highest available rooms",
            section = overlaySection,
            position = 10
    )
    default SarcophagusMode sarcophagusMode() { return SarcophagusMode.HIGHEST_5; }
}
