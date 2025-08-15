package net.runelite.client.plugins.microbot.pyramidplunder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public final class PyramidPlunderOverlayPanel extends OverlayPanel
{
    private final Client client;
    private final ConfigManager configManager;

    public PyramidPlunderOverlayPanel(Client client, ConfigManager configManager)
    {
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.configManager = configManager;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        final int startThievingXp  = PyramidPlunderPlugin.startingXp;
        final int curThievingXp    = client.getSkillExperience(Skill.THIEVING);
        final int gainedThievingXp = Math.max(0, curThievingXp - startThievingXp);

        final int startStrengthXp  = PyramidPlunderPlugin.startingXpStrength != null
                ? PyramidPlunderPlugin.startingXpStrength
                : client.getSkillExperience(Skill.STRENGTH);
        final int curStrengthXp    = client.getSkillExperience(Skill.STRENGTH);
        final int gainedStrengthXp = Math.max(0, curStrengthXp - startStrengthXp);

        final int urns  = PyramidPlunderPlugin.urnsLooted;
        final int chsts = PyramidPlunderPlugin.chestsLooted;
        final int sarcs = PyramidPlunderPlugin.sarcophagiLooted;
        final int runs  = PyramidPlunderPlugin.runsCompleted;

        panelComponent.getChildren().clear();
        panelComponent.setPreferredSize(new Dimension(220, 0));

        panelComponent.getChildren().add(
                TitleComponent.builder()
                        .text("Pyramid Plunder Stats")
                        .color(Color.YELLOW)
                        .build()
        );

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Thieving XP:")
                .right(curThievingXp + " (+" + gainedThievingXp + ")")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Strength XP:")
                .right(curStrengthXp + " (+" + gainedStrengthXp + ")")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Urns Looted:")
                .right(Integer.toString(urns))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Chests Looted:")
                .right(Integer.toString(chsts))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Sarcophagi:")
                .right(Integer.toString(sarcs))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Runs Completed:")
                .right(Integer.toString(runs))
                .build());

        return super.render(graphics);
    }

    @SuppressWarnings("unused")
    private PyramidPlunderConfig cfg()
    {
        return configManager.getConfig(PyramidPlunderConfig.class);
    }
}
