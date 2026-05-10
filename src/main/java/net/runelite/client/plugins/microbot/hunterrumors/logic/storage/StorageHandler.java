package net.runelite.client.plugins.microbot.hunterrumors.logic.storage;

/**
 * Generic storage adapter for "container items" (kits, baskets, pouches, etc).
 *
 * v0: framework only. Concrete handlers will implement UI interactions once we capture widgets/actions.
 */
public interface StorageHandler
{
    String name();

    /**
     * Quick availability check (e.g. container item present/unlocked).
     */
    boolean isAvailable();

    /**
     * One bounded step to reconcile desired state (store/withdraw).
     *
     * Return true when this handler is "done" for current request.
     */
    boolean step(StorageRequest request);
}

