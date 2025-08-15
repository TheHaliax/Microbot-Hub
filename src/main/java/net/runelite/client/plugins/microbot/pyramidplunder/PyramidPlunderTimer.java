package net.runelite.client.plugins.microbot.pyramidplunder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import net.runelite.api.Client;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Timer;

final class PyramidPlunderTimer extends Timer
{
    private final PyramidPlunderConfig config;
    private final Client client;

    PyramidPlunderTimer(Duration duration,
                        BufferedImage image,
                        Plugin owner,                 // <- real @PluginDescriptor plugin
                        PyramidPlunderConfig config,
                        Client client)
    {
        super(duration.toMillis(), ChronoUnit.MILLIS, image, owner);
        assert config != null;
        assert client != null;
        this.config = config;
        this.client = client;
    }

    @Override
    public Color getTextColor()
    {
        long secs = Math.max(0, Duration.between(Instant.now(), getEndTime()).getSeconds());
        return secs < config.timerLowWarning() ? Color.RED.brighter() : Color.WHITE;
    }

    @Override
    public String getTooltip()
    {
        int floor = client.getVarbitValue(VarbitID.NTK_ROOM_NUMBER);
        int lvl   = client.getVarbitValue(VarbitID.NTK_THIEVING_REQUIRED);
        return "Time remaining. Floor: " + floor + ". Thieving level: " + lvl;
    }

    @Override
    public boolean render()
    {
        return config.showExactTimer();
    }
}
