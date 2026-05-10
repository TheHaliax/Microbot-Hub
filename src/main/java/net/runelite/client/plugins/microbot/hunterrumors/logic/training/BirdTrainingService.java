package net.runelite.client.plugins.microbot.hunterrumors.logic.training;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.hunterrumors.HunterRumorsConfig;
import net.runelite.client.plugins.microbot.hunterrumors.logic.handlers.BirdSnareHandler;

import javax.inject.Inject;

@Slf4j
public final class BirdTrainingService
{
    @Inject
    private BirdSnareHandler birdSnareHandler;

    public void tick(HunterRumorsConfig config, int hunterLevel)
    {
        if (config == null || birdSnareHandler == null)
        {
            return;
        }

        BirdTrainingProfile profile = BirdTrainingCatalog.pickForLevel(hunterLevel);
        if (profile == null)
        {
            Microbot.status = "Training: no profile";
            return;
        }

        birdSnareHandler.step(profile.getTarget(), config);
    }
}

