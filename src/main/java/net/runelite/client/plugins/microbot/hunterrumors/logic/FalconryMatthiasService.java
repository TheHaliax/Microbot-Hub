package net.runelite.client.plugins.microbot.hunterrumors.logic;

import lombok.Getter;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.api.npc.models.Rs2NpcModel;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import javax.inject.Singleton;
import java.util.EnumMap;
import java.util.Map;

/**
 * Handles the falconry "Quick-falcon" interaction with Matthias (npc id 1341).
 * Prep: unequip gloves/weapon/shield, acquire falconry glove.
 * Cleanup: relinquish falcon, re-equip previous items.
 */
@Singleton
public class FalconryMatthiasService
{
    public static final int MATTHIAS_NPC_ID = 1341;
    public static final String QUICK_FALCON_ACTION = "Quick-falcon";

    @Getter
    private boolean holdingFalcon = false;

    // Snapshot of equipment ids we removed (so we can re-equip later).
    private final Map<EquipmentInventorySlot, Integer> removedEquipIds = new EnumMap<>(EquipmentInventorySlot.class);

    public void reset()
    {
        holdingFalcon = false;
        removedEquipIds.clear();
    }

    public boolean ensureFalconReady()
    {
        // Must be empty: WEAPON/SHIELD/GLOVES
        snapshotIfPresent(EquipmentInventorySlot.WEAPON);
        snapshotIfPresent(EquipmentInventorySlot.SHIELD);
        snapshotIfPresent(EquipmentInventorySlot.GLOVES);

        boolean removedAny = false;
        removedAny |= Rs2Equipment.unEquip(EquipmentInventorySlot.WEAPON);
        removedAny |= Rs2Equipment.unEquip(EquipmentInventorySlot.SHIELD);
        removedAny |= Rs2Equipment.unEquip(EquipmentInventorySlot.GLOVES);

        // If we removed something, let the next tick continue.
        if (removedAny)
        {
            return true;
        }

        // Acquire falcon/glove.
        if (clickMatthias())
        {
            holdingFalcon = true;
            return true;
        }
        return false;
    }

    public boolean relinquishAndReequip()
    {
        // Relinquish first
        if (holdingFalcon)
        {
            if (clickMatthias())
            {
                holdingFalcon = false;
                return true;
            }
            return false;
        }

        // Re-equip previous items from inventory (best effort).
        boolean equippedAny = false;
        equippedAny |= tryEquipSnapshot(EquipmentInventorySlot.WEAPON);
        equippedAny |= tryEquipSnapshot(EquipmentInventorySlot.SHIELD);
        equippedAny |= tryEquipSnapshot(EquipmentInventorySlot.GLOVES);
        return equippedAny;
    }

    private void snapshotIfPresent(EquipmentInventorySlot slot)
    {
        if (slot == null || removedEquipIds.containsKey(slot))
        {
            return;
        }
        var item = Rs2Equipment.get(slot);
        if (item == null || item.getId() <= 0)
        {
            return;
        }
        removedEquipIds.put(slot, item.getId());
    }

    private boolean tryEquipSnapshot(EquipmentInventorySlot slot)
    {
        Integer id = removedEquipIds.get(slot);
        if (id == null || id <= 0)
        {
            return false;
        }
        // If already equipped, clear snapshot.
        if (Rs2Equipment.get(slot) != null && Rs2Equipment.get(slot).getId() == id)
        {
            removedEquipIds.remove(slot);
            return true;
        }

        // Try common actions; item decides which exists.
        if (Rs2Inventory.interact(id, "Wield") ||
                Rs2Inventory.interact(id, "Wear") ||
                Rs2Inventory.interact(id, "Equip"))
        {
            removedEquipIds.remove(slot);
            return true;
        }
        return false;
    }

    private boolean clickMatthias()
    {
        Rs2NpcModel matthias = Microbot.getRs2NpcCache().query()
                .withId(MATTHIAS_NPC_ID)
                .nearestOnClientThread();
        if (matthias == null)
        {
            return false;
        }
        return matthias.click(QUICK_FALCON_ACTION);
    }
}

