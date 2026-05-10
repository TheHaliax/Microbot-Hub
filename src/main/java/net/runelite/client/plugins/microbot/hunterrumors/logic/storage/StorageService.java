package net.runelite.client.plugins.microbot.hunterrumors.logic.storage;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs all available storage handlers against a request.
 *
 * v0: non-invasive; handlers are stubs until we implement container UIs.
 */
@Singleton
public class StorageService
{
    private final List<StorageHandler> handlers = new ArrayList<>();

    public void register(StorageHandler handler)
    {
        if (handler != null)
        {
            handlers.add(handler);
        }
    }

    /**
     * One bounded step across handlers.
     * Returns true when no handler needs further work.
     */
    public boolean step(StorageRequest request)
    {
        if (request == null || handlers.isEmpty())
        {
            return true;
        }

        boolean allDone = true;
        for (StorageHandler h : handlers)
        {
            if (h == null || !h.isAvailable())
            {
                continue;
            }
            boolean done = h.step(request);
            if (!done)
            {
                allDone = false;
                break; // bounded: only drive one handler per tick
            }
        }
        return allDone;
    }
}

