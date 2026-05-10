package net.runelite.client.plugins.microbot.rs2leaguetransport;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.plugins.microbot.Microbot;

/**
 * Leagues area teleport UI driver (static util). Opens Activities → Leagues → View Areas, then
 * Teleport to region using {@link LeaguesRegion#toMenuTarget()} with fixed anchor widget id.
 * <p>
 * Call {@link #teleport(LeaguesRegion)} once, then {@link #tick()} from the script loop until
 * {@link #isActive()} is false — one menu action per tick.
 */
public final class Rs2LeagueTransport
{
    private static final Object LOCK = new Object();

    private static final int W_ACTIVITIES = 10551357;
    private static final int W_LEAGUES = 41222178;
    private static final int W_VIEW_AREAS = 42991656;

    /**
     * Areas list teleport control; destination is selected via {@link LeaguesRegion#toMenuTarget()}.
     */
    private static final int W_TELEPORT_ANCHOR = 33554516;

    private static boolean active;
    private static LeaguesRegion targetRegion;
    private static int step;

    private Rs2LeagueTransport()
    {
    }

    /**
     * Starts a leagues teleport to {@code region}. Drive with {@link #tick()} until inactive.
     */
    public static void teleport(LeaguesRegion region)
    {
        assert region != null;

        synchronized (LOCK)
        {
            targetRegion = region;
            step = 0;
            active = true;
        }
    }

    public static boolean isActive()
    {
        synchronized (LOCK)
        {
            return active;
        }
    }

    public static void stop()
    {
        synchronized (LOCK)
        {
            active = false;
            step = 0;
            targetRegion = null;
        }
    }

    /**
     * Advances one UI step if a teleport is {@linkplain #isActive() active}. At most one {@code menuAction} per call.
     */
    public static void tick()
    {
        Client client = Microbot.getClient();
        if (client == null)
        {
            return;
        }

        synchronized (LOCK)
        {
            if (!active || targetRegion == null)
            {
                return;
            }

            LeaguesRegion region = targetRegion;
            assert region != null;

            if (step == 0)
            {
                Microbot.status = "Leagues: open activities";
                client.menuAction(-1, W_ACTIVITIES, MenuAction.CC_OP, 1, -1, "Leagues", "");
                step = 1;
                return;
            }
            if (step == 1)
            {
                Microbot.status = "Leagues: open leagues";
                client.menuAction(-1, W_LEAGUES, MenuAction.CC_OP, 1, -1, "Leagues", "");
                step = 2;
                return;
            }
            if (step == 2)
            {
                Microbot.status = "Leagues: open areas";
                client.menuAction(-1, W_VIEW_AREAS, MenuAction.CC_OP, 1, -1, "View Areas", "");
                step = 3;
                return;
            }

            Microbot.status = "Leagues: teleport " + region.getDisplayName();
            client.menuAction(-1, W_TELEPORT_ANCHOR, MenuAction.CC_OP, 1, -1, "Teleport to", region.toMenuTarget());
            active = false;
            step = 0;
            targetRegion = null;
        }
    }
}
