package net.runelite.client.plugins.microbot.rs2leaguetransport;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.PluginConstants;

/**
 * Standalone plugin exposing {@link Rs2LeagueTransport} and {@link LeaguesRegion} for Leagues area teleports.
 */
@PluginDescriptor(
        name = PluginConstants.DEFAULT_PREFIX + "Rs2 League Transport",
        description = "Static util for Leagues UI teleports (Activities → areas); use Rs2LeagueTransport.teleport(LeaguesRegion).",
        tags = {"leagues", "teleport", "microbot"},
        authors = {"Hal"},
        version = Rs2LeagueTransportPlugin.version,
        minClientVersion = "2.1.0",
        enabledByDefault = PluginConstants.DEFAULT_ENABLED,
        isExternal = PluginConstants.IS_EXTERNAL
)
public class Rs2LeagueTransportPlugin extends Plugin
{
    static final String version = "1.0.0";

    @Override
    protected void startUp()
    {
    }

    @Override
    protected void shutDown()
    {
        Rs2LeagueTransport.stop();
    }
}
