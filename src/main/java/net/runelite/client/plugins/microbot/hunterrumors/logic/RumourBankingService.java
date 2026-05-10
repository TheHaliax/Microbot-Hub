package net.runelite.client.plugins.microbot.hunterrumors.logic;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Global banking prep for all rumours.
 *
 * v0: only enforces handler-required gear by name.
 * Future: per-target supplies, keep/trash, meat pouch, shop restock.
 */
@Singleton
public class RumourBankingService
{
    private enum Phase
    {
        IDLE,
        OPEN_BANK,
        ENSURE_GLOBAL_LOADOUT,
        WITHDRAW_REQUIRED,
        CLOSE_BANK
    }

    private Phase phase = Phase.IDLE;
    private int stepBudget = 0;

    // Global "single loadout" template (from wiki Strategies -> Equipment section)
    private static final int[] KNIFE_IDS = new int[]{ItemID.FLETCHING_KNIFE, ItemID.KNIFE};

    private static final String[] CAPE_PRIORITY = new String[]{
            "Max cape",
            "Hunter cape",
            "Ardougne cloak 4",
            "Ardougne cloak 3",
            "Ardougne cloak 2",
            "Ardougne cloak 1"
    };

    // Prefer best available axe (including 2h/felling variants).
    // We avoid referencing other plugin source-sets here.
    private static final int[] AXE_PRIORITY = new int[]{
            ItemID.CRYSTAL_AXE,
            ItemID.DRAGON_AXE,
            ItemID.RUNE_AXE,
            ItemID.ADAMANT_AXE,
            ItemID.MITHRIL_AXE,
            ItemID.BLACK_AXE,
            ItemID.STEEL_AXE,
            ItemID.IRON_AXE,
            ItemID.BRONZE_AXE
    };

    private static final String[] QUETZAL_WHISTLE_PRIORITY = new String[]{
            "Perfected quetzal whistle",
            "Enhanced quetzal whistle",
            "Basic quetzal whistle"
    };

    private static final String[] MEAT_POUCH_PRIORITY = new String[]{
            "Large meat pouch",
            "Medium meat pouch",
            "Small meat pouch"
    };

    private static final String[] FUR_POUCH_PRIORITY = new String[]{
            "Large fur pouch",
            "Medium fur pouch",
            "Small fur pouch"
    };

    private static final String[] OUTFIT_PRIORITY = new String[]{
            "Guild hunter headwear",
            "Guild hunter top",
            "Guild hunter legs",
            "Guild hunter boots"
    };

    private static final String[] GRACEFUL_FALLBACK = new String[]{
            "Graceful hood",
            "Graceful top",
            "Graceful legs",
            "Graceful boots"
    };

    private static final String RING_OF_ENDURANCE = "Ring of endurance";
    private static final String HUNTSMANS_KIT = "Huntsman's kit";
    private static final String LOG_BASKET = "Log basket";
    private static final String BONECRUSHER = "Bonecrusher";
    private static final String BONECRUSHER_NECKLACE = "Bonecrusher necklace";

    public void reset()
    {
        phase = Phase.IDLE;
        stepBudget = 0;
    }

    /**
     * Bounded: does at most one meaningful bank action per call.
     */
    public boolean ensurePrepared(RumourHandler handler)
    {
        if (handler == null)
        {
            return true;
        }

        List<String> required = handler.getRequiredGear();
        if (required == null || required.isEmpty())
        {
            // Still enforce global loadout.
            required = java.util.Collections.emptyList();
        }

        if (phase == Phase.IDLE)
        {
            phase = Phase.OPEN_BANK;
            stepBudget = 40;
        }

        // Fast path: already have everything.
        if (hasGlobalLoadout() && hasAll(required))
        {
            phase = Phase.IDLE;
            return true;
        }

        if (stepBudget-- <= 0)
        {
            Microbot.status = "Bank: budget exceeded (reset)";
            reset();
            return false;
        }

        if (phase == Phase.OPEN_BANK)
        {
            Microbot.status = "Bank: open";
            if (!Rs2Bank.isOpen())
            {
                Rs2Bank.openBank();
                return false;
            }
            phase = Phase.WITHDRAW_REQUIRED;
            return false;
        }

        if (phase == Phase.ENSURE_GLOBAL_LOADOUT)
        {
            Microbot.status = "Bank: global loadout";
            if (!Rs2Bank.isOpen())
            {
                phase = Phase.OPEN_BANK;
                return false;
            }

            // Withdraw/Equip one missing global piece per call (bounded).
            if (!ensureGlobalOneStep())
            {
                return false;
            }

            // If global loadout looks good, proceed.
            phase = Phase.WITHDRAW_REQUIRED;
            return false;
        }

        if (phase == Phase.WITHDRAW_REQUIRED)
        {
            Microbot.status = "Bank: withdraw required";
            if (!Rs2Bank.isOpen())
            {
                phase = Phase.OPEN_BANK;
                return false;
            }

            // Ensure global loadout before per-handler requirements.
            if (!hasGlobalLoadout())
            {
                phase = Phase.ENSURE_GLOBAL_LOADOUT;
                return false;
            }

            // Withdraw one missing item per call (bounded).
            for (String name : required)
            {
                if (name == null || name.isBlank())
                {
                    continue;
                }
                if (!Rs2Inventory.hasItem(name))
                {
                    if (!Rs2Bank.hasItem(name))
                    {
                        Microbot.status = "Missing item in bank: " + name;
                        // Stay in this phase; handler may decide to stop/disable later.
                        return false;
                    }

                    Rs2Bank.withdrawAll(name);
                    return false;
                }
            }

            phase = Phase.CLOSE_BANK;
            return false;
        }

        if (phase == Phase.CLOSE_BANK)
        {
            Microbot.status = "Bank: close";
            Rs2Bank.closeBank();
            phase = Phase.IDLE;
            return hasAll(required);
        }

        return false;
    }

    private static boolean hasAll(List<String> names)
    {
        if (names == null || names.isEmpty())
        {
            return true;
        }

        // Avoid repeated checks if list contains duplicates.
        Set<String> uniq = new HashSet<>(names.size());
        uniq.addAll(names);
        for (String name : uniq)
        {
            if (name == null || name.isBlank())
            {
                continue;
            }
            if (!Rs2Inventory.hasItem(name))
            {
                return false;
            }
        }
        return true;
    }

    private static boolean hasAny(String... names)
    {
        if (names == null || names.length == 0)
        {
            return false;
        }
        for (String n : names)
        {
            if (n == null || n.isBlank())
            {
                continue;
            }
            if (Rs2Inventory.hasItem(n))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean hasKnife()
    {
        for (int id : KNIFE_IDS)
        {
            if (Rs2Inventory.hasItem(id))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean hasAxe()
    {
        // Axe can be worn or in inventory.
        for (int id : AXE_PRIORITY)
        {
            if (id <= 0)
            {
                continue;
            }
            if (Rs2Inventory.hasItem(id) || Rs2Equipment.isWearing(id))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean hasCape()
    {
        for (String cape : CAPE_PRIORITY)
        {
            if (Rs2Equipment.isWearing(cape) || Rs2Inventory.hasItem(cape))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean hasRing()
    {
        return Rs2Equipment.isWearing(RING_OF_ENDURANCE) || Rs2Inventory.hasItem(RING_OF_ENDURANCE);
    }

    private static boolean hasOutfitOrGraceful()
    {
        // Prefer full outfit; otherwise accept graceful.
        boolean outfit = true;
        for (String s : OUTFIT_PRIORITY)
        {
            if (!Rs2Equipment.isWearing(s) && !Rs2Inventory.hasItem(s))
            {
                outfit = false;
                break;
            }
        }
        if (outfit)
        {
            return true;
        }

        for (String s : GRACEFUL_FALLBACK)
        {
            if (Rs2Equipment.isWearing(s) || Rs2Inventory.hasItem(s))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean hasGlobalLoadout()
    {
        // Hard requirements: dramen staff always + axe always + knife always.
        if (!Rs2Inventory.hasItem(ItemID.DRAMEN_STAFF))
        {
            return false;
        }
        if (!hasKnife())
        {
            return false;
        }
        if (!hasAxe())
        {
            return false;
        }

        // Soft requirements (still try to bring): whistle, kit, pouches, basket, bonecrusher, cape, ring, outfit/graceful
        if (!hasAny(QUETZAL_WHISTLE_PRIORITY))
        {
            return false;
        }
        if (!Rs2Inventory.hasItem(HUNTSMANS_KIT))
        {
            return false;
        }
        if (!hasAny(MEAT_POUCH_PRIORITY))
        {
            return false;
        }
        if (!hasAny(FUR_POUCH_PRIORITY))
        {
            return false;
        }
        if (!Rs2Inventory.hasItem(LOG_BASKET))
        {
            return false;
        }
        if (!Rs2Inventory.hasItem(BONECRUSHER) && !Rs2Inventory.hasItem(BONECRUSHER_NECKLACE)
                && !Rs2Equipment.isWearing(BONECRUSHER_NECKLACE))
        {
            return false;
        }
        if (!hasCape())
        {
            return false;
        }
        if (!hasRing())
        {
            return false;
        }
        if (!hasOutfitOrGraceful())
        {
            return false;
        }

        return true;
    }

    /**
     * Returns true when no further global action needed; false when another tick is needed.
     */
    private static boolean ensureGlobalOneStep()
    {
        // 1) Dramen staff (inventory)
        if (!Rs2Inventory.hasItem(ItemID.DRAMEN_STAFF))
        {
            if (!Rs2Bank.hasItem(ItemID.DRAMEN_STAFF))
            {
                Microbot.status = "Missing in bank: Dramen staff";
                return false;
            }
            Rs2Bank.withdrawOne(ItemID.DRAMEN_STAFF);
            return false;
        }

        // 2) Knife (inventory) - prefer fletching knife.
        if (!hasKnife())
        {
            if (Rs2Bank.hasItem(ItemID.FLETCHING_KNIFE))
            {
                Rs2Bank.withdrawOne(ItemID.FLETCHING_KNIFE);
                return false;
            }
            if (Rs2Bank.hasItem(ItemID.KNIFE))
            {
                Rs2Bank.withdrawOne(ItemID.KNIFE);
                return false;
            }
            Microbot.status = "Missing in bank: knife";
            return false;
        }

        // 3) Axe (wielded or inventory). Choose best present in bank if absent.
        if (!hasAxe())
        {
            // Walk best-to-worst; pick first axe that exists in bank.
            for (int id : AXE_PRIORITY)
            {
                if (id <= 0)
                {
                    continue;
                }
                if (Rs2Bank.hasItem(id))
                {
                    Rs2Bank.withdrawOne(id);
                    return false;
                }
            }
            Microbot.status = "Missing in bank: woodcutting axe";
            return false;
        }
        // Try to wield axe if it's in inventory.
        // (wield by id, best-to-worst)
        for (int id : AXE_PRIORITY)
        {
            if (id <= 0)
            {
                continue;
            }
            if (Rs2Inventory.wield(id))
            {
                break;
            }
        }

        // 4) Whistle (inventory)
        if (!hasAny(QUETZAL_WHISTLE_PRIORITY))
        {
            for (String w : QUETZAL_WHISTLE_PRIORITY)
            {
                if (Rs2Bank.hasItem(w))
                {
                    Rs2Bank.withdrawOne(w);
                    return false;
                }
            }
            Microbot.status = "Missing in bank: quetzal whistle";
            return false;
        }

        // 5) Huntsman's kit (inventory)
        if (!Rs2Inventory.hasItem(HUNTSMANS_KIT))
        {
            if (!Rs2Bank.hasItem(HUNTSMANS_KIT))
            {
                Microbot.status = "Missing in bank: huntsman's kit";
                return false;
            }
            Rs2Bank.withdrawOne(HUNTSMANS_KIT);
            return false;
        }

        // 6) Meat pouch (inventory) - largest available
        if (!hasAny(MEAT_POUCH_PRIORITY))
        {
            for (String p : MEAT_POUCH_PRIORITY)
            {
                if (Rs2Bank.hasItem(p))
                {
                    Rs2Bank.withdrawOne(p);
                    return false;
                }
            }
            Microbot.status = "Missing in bank: meat pouch";
            return false;
        }

        // 7) Fur pouch (inventory) - largest available
        if (!hasAny(FUR_POUCH_PRIORITY))
        {
            for (String p : FUR_POUCH_PRIORITY)
            {
                if (Rs2Bank.hasItem(p))
                {
                    Rs2Bank.withdrawOne(p);
                    return false;
                }
            }
            Microbot.status = "Missing in bank: fur pouch";
            return false;
        }

        // 8) Log basket (inventory)
        if (!Rs2Inventory.hasItem(LOG_BASKET))
        {
            if (!Rs2Bank.hasItem(LOG_BASKET))
            {
                Microbot.status = "Missing in bank: log basket";
                return false;
            }
            Rs2Bank.withdrawOne(LOG_BASKET);
            return false;
        }

        // 9) Bonecrusher or necklace (inventory/equip)
        if (!Rs2Inventory.hasItem(BONECRUSHER) && !Rs2Inventory.hasItem(BONECRUSHER_NECKLACE)
                && !Rs2Equipment.isWearing(BONECRUSHER_NECKLACE))
        {
            if (Rs2Bank.hasItem(BONECRUSHER_NECKLACE))
            {
                Rs2Bank.withdrawOne(BONECRUSHER_NECKLACE);
                return false;
            }
            if (Rs2Bank.hasItem(BONECRUSHER))
            {
                Rs2Bank.withdrawOne(BONECRUSHER);
                return false;
            }
            Microbot.status = "Missing in bank: bonecrusher";
            return false;
        }

        // 10) Ring of endurance (equip if possible)
        if (!hasRing())
        {
            if (!Rs2Bank.hasItem(RING_OF_ENDURANCE))
            {
                Microbot.status = "Missing in bank: ring of endurance";
                return false;
            }
            Rs2Bank.withdrawOne(RING_OF_ENDURANCE);
            return false;
        }
        Rs2Inventory.wear(RING_OF_ENDURANCE);

        // 11) Cape priority (equip best available)
        if (!hasCape())
        {
            for (String cape : CAPE_PRIORITY)
            {
                if (Rs2Bank.hasItem(cape))
                {
                    Rs2Bank.withdrawOne(cape);
                    return false;
                }
            }
            Microbot.status = "Missing in bank: cape";
            return false;
        }
        Rs2Inventory.wear(CAPE_PRIORITY);

        // 12) Outfit > graceful fallback (equip if present)
        boolean haveOutfit = Rs2Bank.hasAllItems(java.util.List.of(OUTFIT_PRIORITY), false, 1)
                || (Rs2Inventory.hasItem(OUTFIT_PRIORITY[0]) && Rs2Inventory.hasItem(OUTFIT_PRIORITY[1])
                && Rs2Inventory.hasItem(OUTFIT_PRIORITY[2]) && Rs2Inventory.hasItem(OUTFIT_PRIORITY[3]));
        if (!hasOutfitOrGraceful())
        {
            // Try withdraw outfit pieces first, else graceful.
            for (String p : OUTFIT_PRIORITY)
            {
                if (!Rs2Inventory.hasItem(p) && !Rs2Equipment.isWearing(p) && Rs2Bank.hasItem(p))
                {
                    Rs2Bank.withdrawOne(p);
                    return false;
                }
            }
            for (String p : GRACEFUL_FALLBACK)
            {
                if (!Rs2Inventory.hasItem(p) && !Rs2Equipment.isWearing(p) && Rs2Bank.hasItem(p))
                {
                    Rs2Bank.withdrawOne(p);
                    return false;
                }
            }
            Microbot.status = "Missing in bank: hunter outfit/graceful";
            return false;
        }

        // Equip whichever set we have.
        if (haveOutfit)
        {
            Rs2Inventory.wear(OUTFIT_PRIORITY);
        }
        else
        {
            Rs2Inventory.wear(GRACEFUL_FALLBACK);
        }

        // Good enough for v0.
        return true;
    }
}

