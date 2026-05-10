package net.runelite.client.plugins.microbot.hunterrumors.logic.storage.handlers;

import net.runelite.client.plugins.microbot.hunterrumors.logic.storage.StorageHandler;
import net.runelite.client.plugins.microbot.hunterrumors.logic.storage.StorageRequest;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.huntkit.Rs2HuntKit;

/**
 * Huntsman's kit storage handler.
 *
 * Supported:
 * - Fill: store all eligible hunter items from inventory.
 * - View: open the kit interface (bank-like UI) so later logic can withdraw specific tools.
 */
public class HuntsmansKitStorageHandler implements StorageHandler
{
    private static final String KIT = "Huntsman's kit";
    private static final int KIT_ITEM_ID = 29309;

    @Override
    public String name()
    {
        return "Huntsman's kit";
    }

    @Override
    public boolean isAvailable()
    {
        return Rs2Inventory.hasItem(KIT) || Rs2Inventory.hasItem(KIT_ITEM_ID);
    }

    @Override
    public boolean step(StorageRequest request)
    {
        // v0: keep kit filled so future withdraws work. Bounded: one action.
        if (!isAvailable())
        {
            return true;
        }

        if (Rs2HuntKit.isOpen())
        {
            return true;
        }

        return storeAllHunter();
    }

    public boolean storeAllHunter()
    {
        if (!isAvailable())
        {
            return false;
        }
        return Rs2HuntKit.fill();
    }

    public boolean openView()
    {
        return Rs2HuntKit.openView();
    }

    public boolean isViewOpen()
    {
        return Rs2HuntKit.isOpen();
    }

    public boolean closeView()
    {
        return Rs2HuntKit.close();
    }
}

