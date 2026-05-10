package net.runelite.client.plugins.microbot.hunterrumors.logic.storage.handlers;

import net.runelite.client.plugins.microbot.hunterrumors.logic.storage.StorageHandler;
import net.runelite.client.plugins.microbot.hunterrumors.logic.storage.StorageRequest;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

/**
 * v0 stub.
 *
 * Next: implement open basket + store logs.
 */
public class LogBasketStorageHandler implements StorageHandler
{
    private static final String LOG_BASKET = "Log basket";

    @Override
    public String name()
    {
        return "Log basket";
    }

    @Override
    public boolean isAvailable()
    {
        return Rs2Inventory.hasItem(LOG_BASKET);
    }

    @Override
    public boolean step(StorageRequest request)
    {
        // No-op until UI wiring exists.
        return true;
    }
}

