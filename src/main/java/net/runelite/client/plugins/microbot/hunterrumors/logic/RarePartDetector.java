package net.runelite.client.plugins.microbot.hunterrumors.logic;

import net.runelite.client.plugins.microbot.hunterrumors.RumourRarePart;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

public final class RarePartDetector
{
    private RarePartDetector()
    {
        throw new IllegalStateException("utility");
    }

    public static boolean hasRarePart(RumourRarePart part)
    {
        if (part == null)
        {
            return false;
        }

        Integer id = part.getItemId();
        if (id != null && id > 0)
        {
            return Rs2Inventory.hasItem(id);
        }

        String name = part.getItemName();
        if (name == null || name.isBlank())
        {
            return false;
        }

        return Rs2Inventory.hasItem(name);
    }
}

