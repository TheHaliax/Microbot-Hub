package net.runelite.client.plugins.microbot.hunterrumors;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigInformation;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(HunterRumorsPlugin.CONFIG_GROUP)
@ConfigInformation("Hunter Guild Rumours automation.<br/>" +
        "Configure one huntmaster as SELECTED, optionally configure BLOCK targets per huntmaster.<br/>" +
        "Leagues mode can limit areas + use Leagues area teleports.<br/>" +
        "- Hal")
public interface HunterRumorsConfig extends Config
{
    @ConfigSection(
            name = "General",
            description = "General settings",
            position = 0,
            closedByDefault = false
    )
    String general = "general";

    @ConfigItem(
            keyName = "enabled",
            name = "Enabled",
            description = "Enable Hunter Rumors automation",
            position = 0,
            section = general
    )
    default boolean enabled()
    {
        return true;
    }

    @ConfigItem(
            keyName = "avoidCrowds",
            name = "Avoid crowds (hop)",
            description = "Avoid competition (rotate areas first, then hop)",
            position = 1,
            section = general
    )
    default boolean avoidCrowds()
    {
        return true;
    }

    @ConfigItem(
            keyName = "avoidCrowdsPlayers",
            name = "Max nearby players",
            description = "Hop when nearby player count is >= this (excludes you)",
            position = 2,
            section = general
    )
    default int avoidCrowdsPlayers()
    {
        return 3; // "more than 2 other players"
    }

    @ConfigItem(
            keyName = "avoidCrowdsDistance",
            name = "Nearby distance (tiles)",
            description = "Distance to count nearby players",
            position = 3,
            section = general
    )
    default int avoidCrowdsDistance()
    {
        return 8;
    }

    @ConfigItem(
            keyName = "progressiveMode",
            name = "Progressive mode (<46 train)",
            description = "When Hunter < 46, train with shared bird profiles until 46 then continue rumours",
            position = 4,
            section = general
    )
    default boolean progressiveMode()
    {
        return true;
    }

    @ConfigItem(
            keyName = "trainingMaxNearbyPlayers",
            name = "Training: max nearby players",
            description = "Blacklist a training center when nearby player count is >= this (excludes you)",
            position = 5,
            section = general
    )
    default int trainingMaxNearbyPlayers()
    {
        return 3;
    }

    @ConfigItem(
            keyName = "trainingNearbyDistance",
            name = "Training: nearby distance (tiles)",
            description = "Distance to count nearby players for training blacklists",
            position = 6,
            section = general
    )
    default int trainingNearbyDistance()
    {
        return 10;
    }

    @ConfigItem(
            keyName = "trainingPlacementFailLimit",
            name = "Training: placement fail limit",
            description = "After this many consecutive failures at a center, cooldown and rotate to next center",
            position = 7,
            section = general
    )
    default int trainingPlacementFailLimit()
    {
        return 4;
    }

    @ConfigItem(
            keyName = "trainingCenterCooldownSeconds",
            name = "Training: center cooldown (sec)",
            description = "Cooldown seconds after repeated failures/crowding at a center",
            position = 8,
            section = general
    )
    default int trainingCenterCooldownSeconds()
    {
        return 120;
    }

    @ConfigItem(
            keyName = "trainingMaxSnaresCap",
            name = "Training: max snares cap",
            description = "Hard cap for snares to lay (actual is min(inventory, cap))",
            position = 9,
            section = general
    )
    default int trainingMaxSnaresCap()
    {
        return 5;
    }

    @ConfigSection(
            name = "Debug",
            description = "Developer testing helpers",
            position = 99,
            closedByDefault = true
    )
    String debug = "debug";

    @ConfigItem(
            keyName = "debugDrawArea",
            name = "Draw area polygon",
            description = "Draw the selected area polygon in the game scene",
            position = 0,
            section = debug
    )
    default DebugArea debugDrawArea()
    {
        return DebugArea.NONE;
    }

    @ConfigSection(
            name = "Leagues",
            description = "Leagues options (area locks + teleports)",
            position = 1,
            closedByDefault = true
    )
    String leagues = "leagues";

    @ConfigItem(
            keyName = "leaguesEnabled",
            name = "Leagues mode",
            description = "Enable Leagues region filtering + Leagues area teleports",
            position = 0,
            section = leagues
    )
    default boolean leaguesEnabled()
    {
        return false;
    }

    // Region unlocks (config-driven; no world mapping)
    @ConfigItem(keyName = "unlockedMisthalin", name = "Misthalin", description = "Unlocked area", position = 1, section = leagues)
    default boolean unlockedMisthalin() { return true; }

    @ConfigItem(keyName = "unlockedKandarin", name = "Kandarin", description = "Unlocked area", position = 2, section = leagues)
    default boolean unlockedKandarin() { return true; }

    @ConfigItem(keyName = "unlockedFremennik", name = "Fremennik", description = "Unlocked area", position = 3, section = leagues)
    default boolean unlockedFremennik() { return true; }

    @ConfigItem(keyName = "unlockedKaramja", name = "Karamja", description = "Unlocked area", position = 4, section = leagues)
    default boolean unlockedKaramja() { return true; }

    @ConfigItem(keyName = "unlockedDesert", name = "Desert", description = "Unlocked area", position = 5, section = leagues)
    default boolean unlockedDesert() { return true; }

    @ConfigItem(keyName = "unlockedKourend", name = "Kourend", description = "Unlocked area", position = 6, section = leagues)
    default boolean unlockedKourend() { return true; }

    @ConfigItem(keyName = "unlockedTirannwn", name = "Tirannwn", description = "Unlocked area", position = 7, section = leagues)
    default boolean unlockedTirannwn() { return true; }

    @ConfigItem(keyName = "unlockedMorytania", name = "Morytania", description = "Unlocked area", position = 8, section = leagues)
    default boolean unlockedMorytania() { return true; }

    @ConfigItem(keyName = "unlockedVarlamore", name = "Varlamore", description = "Unlocked area", position = 9, section = leagues)
    default boolean unlockedVarlamore() { return true; }

    @ConfigSection(
            name = "Huntmasters",
            description = "Per-huntmaster modes and block targets",
            position = 2,
            closedByDefault = false
    )
    String huntmasters = "huntmasters";

    @ConfigItem(keyName = "gilmanMode", name = "Gilman (Novice)", description = "Mode for Huntmaster Gilman", position = 0, section = huntmasters)
    default HuntmasterMode gilmanMode() { return HuntmasterMode.DISABLED; }

    @ConfigItem(keyName = "gilmanBlockTarget", name = "Gilman block target", description = "Target to block via Gilman", position = 1, section = huntmasters)
    default RumourTarget gilmanBlockTarget() { return RumourTarget.NONE; }

    @ConfigItem(keyName = "ornusMode", name = "Ornus (Adept)", description = "Mode for Guild Hunter Ornus", position = 2, section = huntmasters)
    default HuntmasterMode ornusMode() { return HuntmasterMode.DISABLED; }

    @ConfigItem(keyName = "ornusBlockTarget", name = "Ornus block target", description = "Target to block via Ornus", position = 3, section = huntmasters)
    default RumourTarget ornusBlockTarget() { return RumourTarget.NONE; }

    @ConfigItem(keyName = "cervusMode", name = "Cervus (Adept)", description = "Mode for Guild Hunter Cervus", position = 4, section = huntmasters)
    default HuntmasterMode cervusMode() { return HuntmasterMode.DISABLED; }

    @ConfigItem(keyName = "cervusBlockTarget", name = "Cervus block target", description = "Target to block via Cervus", position = 5, section = huntmasters)
    default RumourTarget cervusBlockTarget() { return RumourTarget.NONE; }

    @ConfigItem(keyName = "acoMode", name = "Aco (Expert)", description = "Mode for Guild Hunter Aco", position = 6, section = huntmasters)
    default HuntmasterMode acoMode() { return HuntmasterMode.DISABLED; }

    @ConfigItem(keyName = "acoBlockTarget", name = "Aco block target", description = "Target to block via Aco", position = 7, section = huntmasters)
    default RumourTarget acoBlockTarget() { return RumourTarget.NONE; }

    @ConfigItem(keyName = "tecoMode", name = "Teco (Expert)", description = "Mode for Guild Hunter Teco", position = 8, section = huntmasters)
    default HuntmasterMode tecoMode() { return HuntmasterMode.DISABLED; }

    @ConfigItem(keyName = "tecoBlockTarget", name = "Teco block target", description = "Target to block via Teco", position = 9, section = huntmasters)
    default RumourTarget tecoBlockTarget() { return RumourTarget.NONE; }

    @ConfigItem(keyName = "wolfMode", name = "Wolf (Master)", description = "Mode for Guild Hunter Wolf", position = 10, section = huntmasters)
    default HuntmasterMode wolfMode() { return HuntmasterMode.DISABLED; }

    @ConfigItem(keyName = "wolfBlockTarget", name = "Wolf block target", description = "Target to block via Wolf", position = 11, section = huntmasters)
    default RumourTarget wolfBlockTarget() { return RumourTarget.NONE; }

    // Note: this repo's RuneLite config API does not support conditional enable/disable annotations.
    // UI will always show block target dropdowns; script will ignore them unless mode == BLOCK.
}

