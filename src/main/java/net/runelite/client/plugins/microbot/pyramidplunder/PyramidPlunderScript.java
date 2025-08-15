package net.runelite.client.plugins.microbot.pyramidplunder;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.api.gameval.VarbitID;

import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.runelite.api.Skill.STRENGTH;
import static net.runelite.api.Skill.THIEVING;
import static net.runelite.client.plugins.microbot.Microbot.getVarbitValue;

@Slf4j
public class PyramidPlunderScript extends Script {
    private final PyramidPlunderPlugin module;
    @Setter
    @Getter
    private PyramidPlunderState state = PyramidPlunderState.INITIALIZING;
    private boolean initialized = false;

    public PyramidPlunderScript(PyramidPlunderPlugin module) {
        this.module = module;
    }
    
    public PyramidPlunderScript() {
        this.module = null;
    }

    // === Item IDs ===
    public static final int[] NTK_TREASURES = {
            ItemID.NTK_IVORY_COMB,
            ItemID.NTK_SCARAB_GOLD,
            ItemID.NTK_SCARAB_STONE,
            ItemID.NTK_SCARAB_POTTERY,
            ItemID.NTK_STATUETTE_GOLD,
            ItemID.NTK_STATUETTE_POTTERY,
            ItemID.NTK_STATUETTE_STONE,
            ItemID.NTK_SEAL_GOLD,
            ItemID.NTK_SEAL_STONE
    };

    public static final int[] NTK_TREASURES_GOLD = {
            ItemID.NTK_SCARAB_GOLD,
            ItemID.NTK_STATUETTE_GOLD,
            ItemID.NTK_SEAL_GOLD
    };

    public static final int[] NTK_TREASURES_MISC = {
            ItemID.NTK_SCARAB_STONE,
            ItemID.NTK_SCARAB_POTTERY,
            ItemID.NTK_STATUETTE_POTTERY,
            ItemID.NTK_STATUETTE_STONE,
            ItemID.NTK_SEAL_STONE
    };

    // === Npc IDs ===
    private static final int PYRAMID_PLUNDER_MUMMY = NpcID.NTK_MUMMY_GUARDIAN;

    // === Object IDs ===
    private static final int NO_MUMMY_EXIT = ObjectID.NTK_ANTECHAMBER_EXIT;
    private static final int EXIT_PYRAMID = ObjectID.NTK_TOMB_DOOR_EXIT;

    // === Locations ===
    public static final WorldArea OUTSIDE_PYRAMID_PLUNDER = new WorldArea(3280, 2750, 20, 20, 0);
    public static final WorldPoint PYRAMID_PLUNDER_ENTRANCE_NORTH = new WorldPoint(3289, 2750, 0);
    public static final WorldPoint PYRAMID_PLUNDER_ENTRANCE_MUMMY = new WorldPoint(3289, 2751, 0);
    public static final WorldArea PYRAMID_PLUNDER_ENTRANCE_MUMMY_AREA = new WorldArea(3285, 2750, 8, 8, 0);

    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!Microbot.isLoggedIn()) return;
            if (!super.run()) return;

            log.info("[PyramidPlunder] State: {}", state);

            switch (state) {
                case INITIALIZING:
                    log.info("[PyramidPlunder] Initializing...");
                    PyramidPlunderPlugin.status = "Initializing...";
                    if (!initialized) {
                        initialize();
                    }
                    Rs2Walker.walkTo(PYRAMID_PLUNDER_ENTRANCE_NORTH);
                    Rs2Player.waitForWalking(2000);
                    state = PyramidPlunderState.SUPPLIES_CHECK;
                    break;

                case SUPPLIES_CHECK:
                    log.info("[PyramidPlunder] Checking supplies...");
                    if (Rs2Inventory.isFull()
                            || !Rs2Inventory.hasItem("antipoison")
                            || !Rs2Inventory.hasItem("prayer")) {
                        state = PyramidPlunderState.BANKING;
                        break;
                    }
                    state = PyramidPlunderState.ENTER_PYRAMID;
                    break;

                case BANKING:
                    log.info("[PyramidPlunder] Handling banking...");
                    PyramidPlunderPlugin.status = "Banking...";
                    handleBanking();
                    break;

                case ENTER_PYRAMID:
                    log.info("[PyramidPlunder] Entering pyramid...");
                    PyramidPlunderPlugin.status = "Entering Pyramid Plunder...";
                    if (PYRAMID_PLUNDER_ENTRANCE_MUMMY_AREA.contains(Rs2Player.getWorldLocation())) {
                        handleMummy();
                    } else {
                        handleEnterPyramid();
                    }
                    break;

                case ROOM_1:
                    if (handleRoom(1)) { state = PyramidPlunderState.ROOM_2; PyramidPlunderPlugin.currentRoom = 2; }
                    break;
                case ROOM_2:
                    if (handleRoom(2)) { state = PyramidPlunderState.ROOM_3; PyramidPlunderPlugin.currentRoom = 3; }
                    break;
                case ROOM_3:
                    if (handleRoom(3)) { state = PyramidPlunderState.ROOM_4; PyramidPlunderPlugin.currentRoom = 4; }
                    break;
                case ROOM_4:
                    if (handleRoom(4)) { state = PyramidPlunderState.ROOM_5; PyramidPlunderPlugin.currentRoom = 5; }
                    break;
                case ROOM_5:
                    if (handleRoom(5)) { state = PyramidPlunderState.ROOM_6; PyramidPlunderPlugin.currentRoom = 6; }
                    break;
                case ROOM_6:
                    if (handleRoom(6)) { state = PyramidPlunderState.ROOM_7; PyramidPlunderPlugin.currentRoom = 7; }
                    break;
                case ROOM_7:
                    if (handleRoom(7)) { state = PyramidPlunderState.ROOM_8; PyramidPlunderPlugin.currentRoom = 8; }
                    break;
                case ROOM_8:
                    state = PyramidPlunderState.EXIT_PYRAMID;
                    PyramidPlunderPlugin.currentRoom = 8;
                    break;

                case EXIT_PYRAMID:
                    log.info("[PyramidPlunder] Exiting pyramid...");
                    PyramidPlunderPlugin.status = "Exiting Pyramid Plunder...";
                    handleExitPyramid();
                    break;

                case LOOT_CHECK:
                    log.info("[PyramidPlunder] Loot check...");
                    handleLootCheck();
                    break;

                case FINISHED:
                    log.info("[PyramidPlunder] Finished.");
                    PyramidPlunderPlugin.status = "Finished for now.";
                    shutdown();
                    break;

                default:
                    log.warn("[PyramidPlunder] Unknown state: {}", state);
                    state = PyramidPlunderState.INITIALIZING;
                    break;
            }

        }, 0, 1200, TimeUnit.MILLISECONDS);

        return true;
    }

    private void initialize() {
        // Initialize starting XP values
        PyramidPlunderPlugin.startingXp = getCurrentXp();
        PyramidPlunderPlugin.xpGained = 0;
        PyramidPlunderPlugin.runsCompleted = 0;
        PyramidPlunderPlugin.currentRoom = 0;
        
        initialized = true;
        state = PyramidPlunderState.INITIALIZE_GEAR;
    }

    private boolean handleRoom(int roomNum) {
        PyramidPlunderPlugin.status = "Looting Room " + roomNum;
        
        // Basic room handling logic
        // This would include looting urns, chests, and sarcophagi based on configuration
        
        // For now, just simulate room completion
        try {
            Thread.sleep(2000); // Simulate room processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return true; // Room completed successfully
    }

    private void handleBanking() {
        // Banking logic would go here
        // For now, just transition to next state
        state = PyramidPlunderState.ENTER_PYRAMID;
    }

    private void handleMummy() {
        // Handle mummy encounter
        // For now, just transition to next state
        state = PyramidPlunderState.ENTER_PYRAMID;
    }

    private void handleEnterPyramid() {
        // Handle entering the pyramid
        // For now, just transition to first room
        state = PyramidPlunderState.ROOM_1;
        PyramidPlunderPlugin.currentRoom = 1;
    }

    private void handleExitPyramid() {
        // Handle exiting the pyramid
        state = PyramidPlunderState.LOOT_CHECK;
    }

    private void handleLootCheck() {
        // Check for valuable loot and handle accordingly
        PyramidPlunderPlugin.runsCompleted++;
        state = PyramidPlunderState.BANKING;
    }

    private int getCurrentXp() {
        return Microbot.getClient().getSkillExperience(THIEVING);
    }

    @Override
    public void shutdown() {
        state = PyramidPlunderState.INITIALIZING;
        initialized = false;
        super.shutdown();
    }
}
