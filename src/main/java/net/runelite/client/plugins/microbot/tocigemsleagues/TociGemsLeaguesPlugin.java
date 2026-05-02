package net.runelite.client.plugins.microbot.tocigemsleagues;

import com.google.inject.Provides;
import java.awt.AWTException;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.PluginConstants;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = PluginConstants.DEFAULT_PREFIX + "Toci Gems Leagues",
        description = "Buy uncut rubies from Toci, cut with chisel, sell cut rubies (Leagues)",
        tags = {"toci", "ruby", "gem", "crafting", "shop", "leagues"},
        authors = {"Hal"},
        version = TociGemsLeaguesPlugin.version,
        minClientVersion = "2.1.0",
        enabledByDefault = PluginConstants.DEFAULT_ENABLED,
        isExternal = PluginConstants.IS_EXTERNAL
)
@Slf4j
public class TociGemsLeaguesPlugin extends Plugin {
    static final String version = "1.0.1";
    @Inject
    private TociGemsLeaguesConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TociGemsLeaguesOverlay overlay;
    @Inject
    TociGemsLeaguesScript script;
    static String status = "Initializing...";
    static int cycles = 0;

    @Provides
    TociGemsLeaguesConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TociGemsLeaguesConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        if (this.overlayManager != null) {
            this.overlayManager.add(this.overlay);
        }
        this.script.run();
    }

    @Override
    protected void shutDown() {
        this.script.shutdown();
        if (this.overlayManager != null) {
            this.overlayManager.remove(this.overlay);
        }
    }
}
