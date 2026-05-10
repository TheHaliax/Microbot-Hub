package net.runelite.client.plugins.microbot.hunterrumors.logic.training;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.hunterrumors.HunterRumorsConfig;
import net.runelite.client.plugins.microbot.hunterrumors.logic.handlers.BirdSnareHandler;

import javax.inject.Inject;

/**
 * Pre-46 progressive training dispatcher.
 * Chooses a target profile then dispatches to the method handler.
 */
@Slf4j
public final class TrainingOrchestrator
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

