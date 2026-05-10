package net.runelite.client.plugins.microbot.hunterrumors.logic;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.coords.Angle;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.plugins.microbot.Microbot;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Slf4j
@Singleton
public class TrapOwnershipTracker
{
    private final net.runelite.api.Client client;

    private final Set<WorldPoint> myTrapTiles = new HashSet<>();
    private WorldPoint lastTickLocalPlayerLocation = null;

    @Inject
    public TrapOwnershipTracker(net.runelite.api.Client client)
    {
        this.client = client;
    }

    public Set<WorldPoint> getMyTrapTiles()
    {
        return Collections.unmodifiableSet(myTrapTiles);
    }

    public boolean isMyTrapTile(WorldPoint wp)
    {
        return wp != null && myTrapTiles.contains(wp);
    }

    @net.runelite.client.eventbus.Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        if (event == null)
        {
            return;
        }
        GameObject go = event.getGameObject();
        if (go == null)
        {
            return;
        }
        int id = go.getId();
        if (!CompetitionTrapIds.ALL_TRAPS.contains(id))
        {
            return;
        }

        Player me = client.getLocalPlayer();
        if (me == null)
        {
            return;
        }

        WorldPoint spawn = go.getWorldLocation();
        if (spawn == null)
        {
            return;
        }

        boolean looksLikeMine = false;
        WorldPoint meNow = me.getWorldLocation();
        if (meNow != null && meNow.distanceTo(spawn) <= 2)
        {
            looksLikeMine = true;
        }
        if (!looksLikeMine && lastTickLocalPlayerLocation != null && spawn.distanceTo(lastTickLocalPlayerLocation) == 0)
        {
            looksLikeMine = true;
        }
        if (!looksLikeMine)
        {
            return;
        }

        // Salamander net traps: stored tile is orientation-dependent (see existing SalamanderPlugin behavior).
        WorldPoint ownedTile = translateNetTrapTileIfNeeded(id, go, spawn);

        myTrapTiles.add(ownedTile);
        log.debug("[HunterRumors] Registered owned trap {} at {}", id, ownedTile);
    }

    @net.runelite.client.eventbus.Subscribe
    public void onGameTick(GameTick event)
    {
        Player lp = client.getLocalPlayer();
        if (lp != null)
        {
            lastTickLocalPlayerLocation = lp.getWorldLocation();
        }

        // Cull trap tiles that no longer contain any trap object.
        if (myTrapTiles.isEmpty())
        {
            return;
        }
        Tile[][][] tiles = client.getScene().getTiles();
        Iterator<WorldPoint> it = myTrapTiles.iterator();
        while (it.hasNext())
        {
            WorldPoint wp = it.next();
            LocalPoint local = LocalPoint.fromWorld(client, wp);
            if (local == null)
            {
                continue;
            }
            Tile tile = tiles[wp.getPlane()][local.getSceneX()][local.getSceneY()];
            if (tile == null)
            {
                continue;
            }

            boolean anyTrapObject = false;
            for (GameObject o : tile.getGameObjects())
            {
                if (o == null)
                {
                    continue;
                }
                if (CompetitionTrapIds.ALL_TRAPS.contains(o.getId()))
                {
                    anyTrapObject = true;
                    break;
                }
            }

            if (!anyTrapObject)
            {
                it.remove();
            }
        }
    }

    private static WorldPoint translateNetTrapTileIfNeeded(int objectId, GameObject go, WorldPoint spawnTile)
    {
        if (!CompetitionTrapIds.NET_TRAPS.contains(objectId) || go == null || spawnTile == null)
        {
            return spawnTile;
        }

        Direction trapOrientation = new Angle(go.getOrientation()).getNearestDirection();
        WorldPoint translated = spawnTile;
        switch (trapOrientation)
        {
            case SOUTH:
                translated = spawnTile.dy(-1);
                break;
            case WEST:
                translated = spawnTile.dx(-1);
                break;
            default:
                break;
        }
        return translated;
    }
}

