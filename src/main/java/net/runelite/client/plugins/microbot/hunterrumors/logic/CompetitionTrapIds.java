package net.runelite.client.plugins.microbot.hunterrumors.logic;

import net.runelite.api.ItemID;
import net.runelite.api.gameval.ObjectID;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class CompetitionTrapIds
{
    private CompetitionTrapIds()
    {
        throw new IllegalStateException("utility");
    }

    // Mirrors existing Microbot trap scripts (e.g. `AutoChinScript`, `BirdHunterPlugin`, `DeadFallTrapHunterPlugin`).
    public static final Set<Integer> BOX_TRAPS = new HashSet<>(Arrays.asList(
            ItemID.BOX_TRAP,
            net.runelite.api.ObjectID.BOX_TRAP,
            net.runelite.api.ObjectID.BOX_TRAP_9385,
            net.runelite.api.ObjectID.BOX_TRAP_9380,
            net.runelite.api.ObjectID.SHAKING_BOX_9384,
            net.runelite.api.ObjectID.SHAKING_BOX_9383,
            net.runelite.api.ObjectID.SHAKING_BOX_9382,
            net.runelite.api.ObjectID.SHAKING_BOX
    ));

    public static final Set<Integer> BIRD_SNARES = new HashSet<>(Arrays.asList(
            ObjectID.HUNTING_OJIBWAY_TRAP,
            ObjectID.HUNTING_OJIBWAY_TRAP_FULL_JUNGLE,
            ObjectID.HUNTING_OJIBWAY_TRAP_FULL_POLAR,
            ObjectID.HUNTING_OJIBWAY_TRAP_FULL_DESERT,
            ObjectID.HUNTING_OJIBWAY_TRAP_FULL_WOODLAND,
            ObjectID.HUNTING_OJIBWAY_TRAP_FULL_COLOURED,
            ObjectID.HUNTING_OJIBWAY_TRAP_BROKEN,
            ObjectID.HUNTING_OJIBWAY_TRAP_FAILING,
            ObjectID.HUNTING_OJIBWAY_TRAP_TRAPPING_JUNGLE,
            ObjectID.HUNTING_OJIBWAY_TRAP_TRAPPING_COLOURED,
            ObjectID.HUNTING_OJIBWAY_TRAP_TRAPPING_DESERT,
            ObjectID.HUNTING_OJIBWAY_TRAP_TRAPPING_WOODLAND,
            ObjectID.HUNTING_OJIBWAY_TRAP_TRAPPING_POLAR
    ));

    public static final Set<Integer> DEADFALLS = new HashSet<>(Arrays.asList(
            ObjectID.HUNTING_DEADFALL_TRAP,
            ObjectID.HUNTING_DEADFALL_FULL_SPIKE,
            ObjectID.HUNTING_DEADFALL_FULL_SABRE,
            ObjectID.HUNTING_DEADFALL_FULL_BARBED,
            ObjectID.HUNTING_DEADFALL_FULL_CLAW,
            ObjectID.HUNTING_DEADFALL_FULL_FENNEC,
            ObjectID.HUNTING_DEADFALL_BOULDER,
            ObjectID.HUNTING_DEADFALL_TRAPPING_SPIKE,
            ObjectID.HUNTING_DEADFALL_TRAPPING_SABRE,
            ObjectID.HUNTING_DEADFALL_TRAPPING_SABRE_M,
            ObjectID.HUNTING_DEADFALL_TRAPPING_BARBED,
            ObjectID.HUNTING_DEADFALL_TRAPPING_BARBED_M,
            ObjectID.HUNTING_DEADFALL_TRAPPING_CLAW,
            ObjectID.HUNTING_DEADFALL_TRAPPING_FENNEC,
            ObjectID.HUNTING_DEADFALL_TRAPPING_FENNEC_M
    ));

    public static final Set<Integer> NET_TRAPS = new HashSet<>(Arrays.asList(
            ObjectID.HUNTING_SAPLING_NET_SET_SWAMP,
            ObjectID.HUNTING_SAPLING_NET_SET_ORANGE,
            ObjectID.HUNTING_SAPLING_NET_SET_RED,
            ObjectID.HUNTING_SAPLING_NET_SET_BLACK,
            ObjectID.HUNTING_SAPLING_NET_SET_MOUNTAIN,

            ObjectID.HUNTING_SAPLING_FULL_GREEN,
            ObjectID.HUNTING_SAPLING_FULL_RED,
            ObjectID.HUNTING_SAPLING_FULL_ORANGE,
            ObjectID.HUNTING_SAPLING_FULL_BLACK,
            ObjectID.HUNTING_SAPLING_FULL_MOUNTAIN,

            ObjectID.HUNTING_SAPLING_CATCHING_GREEN,
            ObjectID.HUNTING_SAPLING_FAILING_SWAMP,
            ObjectID.HUNTING_SAPLING_CATCHING_ORANGE,
            ObjectID.HUNTING_SAPLING_FAILING_ORANGE,
            ObjectID.HUNTING_SAPLING_CATCHING_RED,
            ObjectID.HUNTING_SAPLING_FAILING_RED,
            ObjectID.HUNTING_SAPLING_CATCHING_BLACK,
            ObjectID.HUNTING_SAPLING_FAILING_BLACK,
            ObjectID.HUNTING_SAPLING_CATCHING_MOUNTAIN,
            ObjectID.HUNTING_SAPLING_FAILING_MOUNTAIN,

            ObjectID.HUNTING_SAPLING_UP_ORANGE,
            ObjectID.HUNTING_SAPLING_UP_RED,
            ObjectID.HUNTING_SAPLING_UP_BLACK,
            ObjectID.HUNTING_SAPLING_UP_SWAMP,
            ObjectID.HUNTING_SAPLING_UP_MOUNTAIN,
            ObjectID.HUNTING_SAPLING_SETTING_MOUNTAIN,

            ObjectID.HUNTING_SAPLING_FAILED_MOUNTAIN
    ));

    // Spiked pit (pitfall) objects (Sunlight/Moonlight antelope + larupia/graahk/kyatt)
    // IDs observed in-game by Hal:
    // - Pit: 19227 (build action is "Trap" on this)
    // - Spiked pit: 19228 ("Jump" / "Dismantle")
    // - Collapsed trap: 53024 ("Dismantle")
    public static final Set<Integer> PITFALL_TRAPS = new HashSet<>(Arrays.asList(
            19228,
            53024
    ));

    public static final Set<Integer> ALL_TRAPS = union(PITFALL_TRAPS, union(BOX_TRAPS, union(BIRD_SNARES, union(DEADFALLS, NET_TRAPS))));

    private static Set<Integer> union(Set<Integer> a, Set<Integer> b)
    {
        HashSet<Integer> out = new HashSet<>();
        if (a != null) out.addAll(a);
        if (b != null) out.addAll(b);
        return out;
    }
}

