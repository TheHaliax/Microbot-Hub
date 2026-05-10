package net.runelite.client.plugins.microbot.rs2leaguetransport;

public enum LeaguesRegion
{
    MISTHALIN("Misthalin"),
    KANDARIN("Kandarin"),
    FREMENNIK("Fremennik"),
    KARAMJA("Karamja"),
    DESERT("Desert"),
    KOUREND("Kourend"),
    TIRANNWN("Tirannwn"),
    MORYTANIA("Morytania"),
    VARLAMORE("Varlamore");

    private final String displayName;

    LeaguesRegion(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String toMenuTarget()
    {
        return "<col=ff981f>" + displayName + "</col>";
    }
}
