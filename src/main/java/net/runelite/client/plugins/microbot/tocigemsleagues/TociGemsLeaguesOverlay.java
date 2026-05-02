package net.runelite.client.plugins.microbot.tocigemsleagues;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.plugins.microbot.tocigemsleagues.TociGemsLeaguesPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class TociGemsLeaguesOverlay
extends OverlayPanel {
    private static final String OVERLAY_TITLE = "Toci Gems Leagues";

    @Inject
    TociGemsLeaguesOverlay(TociGemsLeaguesPlugin plugin) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            this.panelComponent.setPreferredSize(new Dimension(220, 120));
            this.panelComponent.getChildren().add(TitleComponent.builder().text(OVERLAY_TITLE).color(Color.MAGENTA).build());
            this.panelComponent.getChildren().add(LineComponent.builder().left("Status:").right(TociGemsLeaguesPlugin.status).build());
            this.panelComponent.getChildren().add(LineComponent.builder().left("Cycles:").right(String.valueOf(TociGemsLeaguesPlugin.cycles)).build());
        }
        catch (Exception ex) {
            System.out.println("TociGemsLeaguesOverlay error: " + ex.getMessage());
        }
        return super.render(graphics);
    }
}
