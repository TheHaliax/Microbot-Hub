package net.runelite.client.plugins.microbot.hunterrumors.logic.storage.handlers;

import net.runelite.client.plugins.microbot.hunterrumors.logic.storage.StorageHandler;
import net.runelite.client.plugins.microbot.hunterrumors.logic.storage.StorageRequest;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

/**
 * v0 stub for meat/fur pouches.
 *
 * Next: implement open pouch + auto-store meat/fur drops, enforce "one active pouch" rules.
 */
public class PouchStorageHandler implements StorageHandler
{
    private static final String[] POUCH_NAMES = new String[]{
            "Large meat pouch",
            "Medium meat pouch",
            "Small meat pouch",
            "Large fur pouch",
            "Medium fur pouch",
            "Small fur pouch"
    };

    @Override
    public String name()
    {
        return "Pouches";
    }

    @Override
    public boolean isAvailable()
    {
        for (String n : POUCH_NAMES)
        {
            if (Rs2Inventory.hasItem(n))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean step(StorageRequest request)
    {
        // No-op until UI wiring exists.
        return true;
    }
}

