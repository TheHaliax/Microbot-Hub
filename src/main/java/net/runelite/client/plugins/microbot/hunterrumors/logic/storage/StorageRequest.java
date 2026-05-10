package net.runelite.client.plugins.microbot.hunterrumors.logic.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Declarative "what should be stored/available" request.
 *
 * v0: item names only; IDs can be added later.
 */
@Getter
@RequiredArgsConstructor
public class StorageRequest
{
    private final List<String> preferStoredItemNames;
    private final List<String> mustRemainAccessibleItemNames;
}

