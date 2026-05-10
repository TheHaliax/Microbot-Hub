package net.runelite.client.plugins.microbot.hunterrumors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.hunterrumors.logic.CrowdAvoidanceService;
import net.runelite.client.plugins.microbot.hunterrumors.logic.TrapOwnershipTracker;
import net.runelite.client.plugins.microbot.hunterrumors.logic.TravelPlanner;
import net.runelite.client.plugins.microbot.hunterrumors.logic.training.TrainingOrchestrator;
import net.runelite.client.plugins.microbot.rs2leaguetransport.LeaguesRegion;
import net.runelite.client.plugins.microbot.rs2leaguetransport.Rs2LeagueTransport;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HunterRumorsScript extends Script
{
    @Getter
    private HunterRumorsState state = HunterRumorsState.INIT;

    private HunterRumorsConfig config;

    @Inject
    private CrowdAvoidanceService crowdAvoidanceService;

    @Inject
    private TrapOwnershipTracker trapOwnershipTracker;

    @Inject
    private TrainingOrchestrator trainingOrchestrator;

    private static final int FAIRY_RING_ID = 29228;

    public boolean run(HunterRumorsConfig config)
    {
        this.config = config;
        shutdown();
        state = HunterRumorsState.INIT;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() ->
        {
            try
            {
                if (!Microbot.isLoggedIn() || !super.run())
                {
                    return;
                }

                tick();
            }
            catch (Exception ex)
            {
                log.error("HunterRumors loop error", ex);
                Microbot.status = "Error: " + ex.getClass().getSimpleName();
            }
        }, 0, 150, TimeUnit.MILLISECONDS);

        return true;
    }

    private void tick()
    {
        if (config == null || !config.enabled())
        {
            state = HunterRumorsState.DISABLED;
            Microbot.status = "Disabled";
            return;
        }

        int hunterLevel = Microbot.getClient().getRealSkillLevel(Skill.HUNTER);
        if (hunterLevel < 46)
        {
            if (config.progressiveMode() && trainingOrchestrator != null)
            {
                state = HunterRumorsState.TRAINING;
                Microbot.status = "Training to 46 (" + hunterLevel + ")";
                trainingOrchestrator.tick(config, hunterLevel);
                return;
            }
            else
            {
                state = HunterRumorsState.NEEDS_HUNTER_LEVEL;
                Microbot.status = "Need Hunter 46+";
                return;
            }
        }

        // Global crowd avoidance (world hop), deterministic by mode.
        if (crowdAvoidanceService != null && trapOwnershipTracker != null)
        {
            // Ensure the competition detector can ignore our own traps even when handlers forget to pass tiles.
            // The handler/assignment sets mode + ids; this just keeps the "mine" set fresh.
            crowdAvoidanceService.setCompetition(
                    null,
                    null,
                    trapOwnershipTracker.getMyTrapTiles()
            );
            if (crowdAvoidanceService.tryResolveCompetition(config))
            {
                state = HunterRumorsState.HOPPING;
                Microbot.status = "Resolving competition";
                return;
            }
        }

        // If we're in the middle of a leagues teleport chain, keep driving it.
        if (Rs2LeagueTransport.isActive())
        {
            state = HunterRumorsState.LEAGUES_TELEPORT;
            Rs2LeagueTransport.tick();
            return;
        }

        // v0: return routing stub (used after completing/turning-in tasks later).
        if (pendingReturn)
        {
            if (tryReturnRouting())
            {
                pendingReturn = false;
            }
            return;
        }

        state = HunterRumorsState.IDLE;
        Microbot.status = "Idle (skeleton)";
    }

    // v0: set by future task handlers when they want to return to guild
    private boolean pendingReturn = false;
    private ReturnState returnState = ReturnState.IDLE;

    private enum ReturnState
    {
        IDLE,
        WALK_TO_FAIRY_RING,
        WEBWALK_TO_GUILD
    }

    public void requestReturnToGuild()
    {
        pendingReturn = true;
        returnState = ReturnState.IDLE;
    }

    /**
     * Return routing per Hal spec:
     * - If leagues enabled and Varlamore unlocked: use Varlamore area teleport.
     * - Else: fairy ring (TODO - no util yet)
     * - Else: WebWalker/bank walker.
     */
    private boolean tryReturnRouting()
    {
        if (config.leaguesEnabled() && config.unlockedVarlamore())
        {
            state = HunterRumorsState.LEAGUES_OPEN_ACTIVITIES;
            Rs2LeagueTransport.teleport(LeaguesRegion.VARLAMORE);
            return true;
        }

        // Else: locate nearby fairy ring and then webwalk (WebWalker will use ring transport if dramen staff available).
        if (returnState == ReturnState.IDLE)
        {
            returnState = ReturnState.WALK_TO_FAIRY_RING;
        }

        if (returnState == ReturnState.WALK_TO_FAIRY_RING)
        {
            // Closest fairy ring to the Hunter's Guild (per Hal): WorldPoint(1651, 3010, 0).
            var ring = Microbot.getRs2TileObjectCache().query().nearest(HunterRumorsLocations.HUNTERS_GUILD_FAIRY_RING, 3);
            if (ring == null)
            {
                // Not in scene yet; walk to the anchor first.
                if (Rs2Player.getWorldLocation() != null && Rs2Player.getWorldLocation().distanceTo(HunterRumorsLocations.HUNTERS_GUILD_FAIRY_RING) > 6)
                {
                    state = HunterRumorsState.IDLE;
                    Microbot.status = "Return: walk to fairy ring";
                    Rs2Walker.walkTo(HunterRumorsLocations.HUNTERS_GUILD_FAIRY_RING, 4);
                    return false;
                }

                // We're near the anchor but the ring still isn't loaded; fall back to webwalker directly.
                returnState = ReturnState.WEBWALK_TO_GUILD;
            }
            else
            {
                var ringLoc = ring.getWorldLocation();
                if (ringLoc != null && Rs2Player.getWorldLocation() != null && Rs2Player.getWorldLocation().distanceTo(ringLoc) > 3)
                {
                    state = HunterRumorsState.IDLE;
                    Microbot.status = "Return: walk to fairy ring";
                    Rs2Walker.walkTo(ringLoc, 3);
                    return false; // keep ticking until at ring
                }
                returnState = ReturnState.WEBWALK_TO_GUILD;
            }
        }

        if (returnState == ReturnState.WEBWALK_TO_GUILD)
        {
            state = HunterRumorsState.IDLE;
            Microbot.status = "Return: WebWalker(guild)";
            // For now, just webwalk to huntmaster basement area center; webwalker will handle transports.
            Rs2Walker.walkTo(new net.runelite.api.coords.WorldPoint(1558, 9457, 0), 6);
            return true;
        }

        // Fallback: WebWalker to Hunters Guild bank
        state = HunterRumorsState.IDLE;
        Microbot.status = "Return: WebWalker(bank)";
        // Rs2Bank.walkToBank will use the configured webwalker bank name when called by handlers; keep as stub here.
        return true;
    }

    @Override
    public void shutdown()
    {
        super.shutdown();
        pendingReturn = false;
        returnState = ReturnState.IDLE;
        Rs2LeagueTransport.stop();
        state = HunterRumorsState.INIT;
    }
}

