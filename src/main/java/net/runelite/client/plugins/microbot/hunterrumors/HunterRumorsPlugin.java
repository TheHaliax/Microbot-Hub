package net.runelite.client.plugins.microbot.hunterrumors;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.PluginConstants;
import net.runelite.client.plugins.microbot.hunterrumors.logic.AssignmentTracker;
import net.runelite.client.plugins.microbot.hunterrumors.logic.TrapOwnershipTracker;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.eventbus.EventBus;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = HunterRumorsPlugin.HAL_PREFIX + "Hunter Rumors",
        description = "Automates Hunter Guild Rumours: configurable huntmaster blocks + task routing",
        tags = {"hunter", "rumours", "rumors", "guild", "microbot"},
        authors = {"Hal"},
        version = HunterRumorsPlugin.version,
        minClientVersion = "2.1.0",
        enabledByDefault = PluginConstants.DEFAULT_ENABLED,
        isExternal = PluginConstants.IS_EXTERNAL
)
public class HunterRumorsPlugin extends Plugin
{
    /** Custom list name prefix; same HTML pattern as entries in PluginConstants. */
    static final String HAL_PREFIX = "<html>[<font color=#00E5FF>Hal</font>] ";
    static final String version = "0.1.8";
    static final String CONFIG_GROUP = "hunterrumors";

    @Inject
    private HunterRumorsConfig config;

    @Provides
    HunterRumorsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(HunterRumorsConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private HunterRumorsOverlay overlay;

    @Inject
    private HunterRumorsScript script;

    @Inject
    private AssignmentTracker assignmentTracker;

    @Inject
    private TrapOwnershipTracker trapOwnershipTracker;

    @Inject
    private EventBus eventBus;

    @Override
    protected void startUp() throws AWTException
    {
        if (overlayManager != null && overlay != null)
        {
            overlayManager.add(overlay);
        }
        if (eventBus != null && trapOwnershipTracker != null)
        {
            eventBus.register(trapOwnershipTracker);
        }
        script.run(config);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (assignmentTracker != null)
        {
            assignmentTracker.onChatMessage(event);
        }
    }

    @Override
    protected void shutDown()
    {
        script.shutdown();
        if (eventBus != null && trapOwnershipTracker != null)
        {
            eventBus.unregister(trapOwnershipTracker);
        }
        if (overlayManager != null && overlay != null)
        {
            overlayManager.remove(overlay);
        }
    }
}

