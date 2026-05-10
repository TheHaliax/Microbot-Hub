package net.runelite.client.plugins.microbot.hunterrumors;

public enum HunterRumorsState
{
    INIT,
    DISABLED,
    NEEDS_HUNTER_LEVEL,
    TRAINING,
    HOPPING,
    IDLE,

    // Leagues UI chain
    LEAGUES_OPEN_ACTIVITIES,
    LEAGUES_OPEN_LEAGUES,
    LEAGUES_OPEN_AREAS,
    LEAGUES_TELEPORT,

    // Placeholder states for later
    GET_ASSIGNMENT,
    EXECUTE_ASSIGNMENT,
    TURN_IN
}

