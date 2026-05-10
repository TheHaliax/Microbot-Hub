package net.runelite.client.plugins.microbot.hunterrumors.logic;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.microbot.hunterrumors.Huntmaster;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Tracks current + per-huntmaster "saved" rumours, similar to RuneLite plugin-hub Rumour Reminder.
 * Storage is per-RS-profile via {@link ConfigManager#setRSProfileConfiguration}.
 *
 * We keep string targets for now (exact in-game creature name). Mapping to {@link net.runelite.client.plugins.microbot.hunterrumors.RumourTarget}
 * happens later via a normalizer.
 */
@Slf4j
@Singleton
public class AssignmentTracker
{
    // Region IDs from RumourReminder plugin (guild floor/basement). We only parse dialog in basement.
    private static final int HUNTER_GUILD_BASEMENT_REGION_ID = 6291;

    private static final Pattern RUMOUR_COMPLETION_PATTERN = Pattern.compile("(?:Another one done|Thanks for that)");

    private static final String KEY_ACTIVE = "activeRumour";
    private static final String KEY_ACTIVE_MASTER = "activeMaster";

    private static String keyForMaster(Huntmaster master)
    {
        return "rumour_" + master.name().toLowerCase();
    }

    private final Client client;
    private final ConfigManager configManager;

    @Getter
    private @Nullable String activeRumour;

    @Getter
    private @Nullable Huntmaster activeMaster;

    // Saved/pending per master (may be null)
    private final Map<Huntmaster, String> rumourByMaster = new EnumMap<>(Huntmaster.class);

    @Inject
    public AssignmentTracker(Client client, ConfigManager configManager)
    {
        this.client = client;
        this.configManager = configManager;
    }

    public void onChatMessage(ChatMessage event)
    {
        if (event == null)
        {
            return;
        }

        // We only implement dialog parsing for now (matches Rumour Reminder approach).
        if (event.getType() == ChatMessageType.DIALOG)
        {
            handleHunterDialog(event);
        }
    }

    private void handleHunterDialog(ChatMessage message)
    {
        if (client.getLocalPlayer() == null)
        {
            return;
        }

        WorldPoint loc = client.getLocalPlayer().getWorldLocation();
        if (loc == null || loc.getRegionID() != HUNTER_GUILD_BASEMENT_REGION_ID)
        {
            return;
        }

        String raw = message.getMessage();
        if (raw == null || !raw.contains("|"))
        {
            return;
        }

        // Format in RumourReminder: "HunterName|dialog text..."
        String[] parts = raw.split("\\|", 2);
        if (parts.length < 2)
        {
            return;
        }

        String prefix = parts[0];
        String contents = parts[1];

        Huntmaster talking = findTalkingMaster(prefix);
        if (talking == null)
        {
            return;
        }

        loadAll();

        // Completion message clears active + that master's saved rumour
        if (RUMOUR_COMPLETION_PATTERN.matcher(contents).find())
        {
            clearRumour(talking);
            saveAll();
            return;
        }

        // Assignment / remembering / confirmations are handled later once we nail exact dialogue strings in-game.
        // For v0, just leave room for incremental updates.
    }

    private @Nullable Huntmaster findTalkingMaster(String prefix)
    {
        if (prefix == null)
        {
            return null;
        }
        // Prefix contains short names in RumourReminder; we use full in-game names as fallback.
        for (Huntmaster m : Huntmaster.values())
        {
            if (prefix.contains(shortName(m)) || prefix.contains(m.getNpcName()))
            {
                return m;
            }
        }
        return null;
    }

    private static String shortName(Huntmaster m)
    {
        switch (m)
        {
            case GILMAN: return "Gilman";
            case ORNUS: return "Ornus";
            case CERVUS: return "Cervus";
            case ACO: return "Aco";
            case TECO: return "Teco";
            case WOLF: return "Wolf";
            default: return m.name();
        }
    }

    private void clearRumour(Huntmaster master)
    {
        if (master == null)
        {
            return;
        }
        rumourByMaster.put(master, null);
        if (activeMaster == master)
        {
            activeMaster = null;
            activeRumour = null;
        }
    }

    public @Nullable String getRumourFor(Huntmaster master)
    {
        return rumourByMaster.get(master);
    }

    public void loadAll()
    {
        this.activeRumour = configManager.getRSProfileConfiguration("hunterrumors", KEY_ACTIVE);

        String activeMasterName = configManager.getRSProfileConfiguration("hunterrumors", KEY_ACTIVE_MASTER);
        this.activeMaster = null;
        if (activeMasterName != null)
        {
            try
            {
                this.activeMaster = Huntmaster.valueOf(activeMasterName);
            }
            catch (Exception ignored)
            {
                this.activeMaster = null;
            }
        }

        for (Huntmaster m : Huntmaster.values())
        {
            String key = keyForMaster(m);
            String v = configManager.getRSProfileConfiguration("hunterrumors", key);
            rumourByMaster.put(m, v);
        }
    }

    public void saveAll()
    {
        setOrUnset(KEY_ACTIVE, activeRumour);
        setOrUnset(KEY_ACTIVE_MASTER, activeMaster != null ? activeMaster.name() : null);

        for (Huntmaster m : Huntmaster.values())
        {
            setOrUnset(keyForMaster(m), rumourByMaster.get(m));
        }
    }

    private void setOrUnset(String key, @Nullable String value)
    {
        if (value == null || value.isBlank())
        {
            configManager.unsetRSProfileConfiguration("hunterrumors", key);
            return;
        }
        configManager.setRSProfileConfiguration("hunterrumors", key, value);
    }
}

