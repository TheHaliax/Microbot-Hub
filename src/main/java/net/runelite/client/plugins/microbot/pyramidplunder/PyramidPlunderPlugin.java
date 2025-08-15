package net.runelite.client.plugins.microbot.pyramidplunder;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.api.gameval.*;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.pluginscheduler.api.SchedulablePlugin;
import net.runelite.client.plugins.microbot.pluginscheduler.condition.logical.AndCondition;
import net.runelite.client.plugins.microbot.pluginscheduler.condition.logical.LogicalCondition;
import net.runelite.client.plugins.microbot.pluginscheduler.event.PluginScheduleEntrySoftStopEvent;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.RSTimeUnit;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Pyramid Plunder",
        description = "Automates Pyramid Plunder minigame with full overlay, loot logic, and timer.",
        tags = {"pyramid", "plunder", "thieving", "skilling", "minigame", "microbot", "automation"},
        enabledByDefault = false,
        author = "Hal",
        version = "1.0.0",
        iconUrl = "https://oldschool.runescape.wiki/images/Pyramid_plunder.png",
        isExternal = true,
        minClientVersion = "1.9.6"
)
public class PyramidPlunderPlugin extends Plugin implements SchedulablePlugin {

    // ===== Injected =====
    @Inject private Client client;
    @Inject private OverlayManager overlayManager;
    @Inject private InfoBoxManager infoBoxManager;
    @Inject private ItemManager itemManager;
    @Inject private ClientThread clientThread;
    @Inject private ConfigManager configManager;

    // ===== UI / Script =====
    private PyramidPlunderOverlay overlay;
    private PyramidPlunderOverlayPanel overlayPanel;
    private PyramidPlunderScript script;
    private PyramidPlunderTimer timer;

    // ===== Highlight caches =====
    @Getter private final Map<TileObject, Tile> tilesToHighlight = new HashMap<>();
    @Getter private final List<GameObject> objectsToHighlight = new ArrayList<>();

    // ===== Constants =====
    public static final int PYRAMID_PLUNDER_REGION = 7749;
    public static final Duration PYRAMID_PLUNDER_DURATION = Duration.of(501, RSTimeUnit.GAME_TICKS);
    private static final int[] FLOOR_REQS = {21, 31, 41, 51, 61, 71, 81, 91};

    public static final Set<Integer> TOMB_DOOR_WALL_IDS = Set.of(
            ObjectID.NTK_TOMB_DOOR1, ObjectID.NTK_TOMB_DOOR2, ObjectID.NTK_TOMB_DOOR3, ObjectID.NTK_TOMB_DOOR4);
    public static final int TOMB_DOOR_CLOSED_ID = ObjectID.NTK_TOMB_DOOR_NOANIM;

    public static final int SPEARTRAP_ID = ObjectID.NTK_SPEARTRAP_INMOTION;

    public static final Set<Integer> URN_IDS = Set.of(
            ObjectID.NTK_URN_TYPE1_MULTI_1, ObjectID.NTK_URN_TYPE1_MULTI_2, ObjectID.NTK_URN_TYPE1_MULTI_3,
            ObjectID.NTK_URN_TYPE1_MULTI_4, ObjectID.NTK_URN_TYPE1_MULTI_5,
            ObjectID.NTK_URN_TYPE2_MULTI_6, ObjectID.NTK_URN_TYPE2_MULTI_7, ObjectID.NTK_URN_TYPE2_MULTI_8,
            ObjectID.NTK_URN_TYPE2_MULTI_9, ObjectID.NTK_URN_TYPE2_MULTI_10,
            ObjectID.NTK_URN_TYPE3_MULTI_11, ObjectID.NTK_URN_TYPE3_MULTI_12, ObjectID.NTK_URN_TYPE3_MULTI_13,
            ObjectID.NTK_URN_TYPE3_MULTI_14, ObjectID.NTK_URN_TYPE3_MULTI_15);
    public static final Set<Integer> URN_CLOSED_IDS = Set.of(
            ObjectID.NTK_URN1_CLOSED, ObjectID.NTK_URN2_CLOSED, ObjectID.NTK_URN3_CLOSED);

    public static final int GRAND_GOLD_CHEST_ID = ObjectID.NTK_GOLDEN_CHEST_MULTI;
    public static final int GRAND_GOLD_CHEST_CLOSED_ID = ObjectID.NTK_GOLDEN_CHEST_CLOSED;

    public static final int SARCOPHAGUS_ID = ObjectID.NTK_SARCOPHAGUS_MULTI;
    public static final int SARCOPHAGUS_CLOSED_ID = ObjectID.NTK_SARCOPHAGUS;

    // ===== Stats =====
    public static String status = "Initializing...";
    public static int currentRoom = 0, totalRooms = 0, xpGained = 0, startingXp = 0;
    public static Integer startingXpStrength = null;
    public static int urnsLooted = 0, chestsLooted = 0, sarcophagiLooted = 0;
    public static int runsCompleted = 0;

    // ===== Cached (tick-safe) loot rooms =====
    private volatile int cachedUrnRoom = -1;
    private volatile int cachedChestRoom = -1;
    private volatile int cachedSarcRoom = -1;

    private final LogicalCondition stopCondition = new AndCondition();

    public PyramidPlunderPlugin() {
    }

    @Provides
    PyramidPlunderConfig getConfig(ConfigManager cm) { return cm.getConfig(PyramidPlunderConfig.class); }

    public PyramidPlunderConfig cfg() { return configManager.getConfig(PyramidPlunderConfig.class); }

    public PyramidPlunderConfig getConfig() { return cfg(); }

    @Override
    public LogicalCondition getStartCondition() {
        // Create conditions that determine when your plugin can start
        // Return null if the plugin can start anytime
        return null;
    }

    @Override
    public LogicalCondition getStopCondition() { return this.stopCondition; }

    // ===== Lifecycle =====
    @Override
    protected void startUp() {
        log.info("Pyramid Plunder module started");
        if (startingXpStrength == null && client != null) startingXpStrength = client.getSkillExperience(Skill.STRENGTH);

        overlay = new PyramidPlunderOverlay(client, this, configManager);
        overlayPanel = new PyramidPlunderOverlayPanel(client, configManager);
        if (overlayManager != null) {
            overlayManager.add(overlay);
            overlayManager.add(overlayPanel);
        }

        if (script == null) script = new PyramidPlunderScript(this);
        script.run();
    }

    @Override
    protected void shutDown() {
        log.info("Pyramid Plunder module stopped");
        tilesToHighlight.clear();
        objectsToHighlight.clear();

        if (overlayManager != null && overlay != null) {
            overlayManager.remove(overlay);
            if (overlayPanel != null) overlayManager.remove(overlayPanel);
        }
        overlay = null;
        overlayPanel = null;

        if (infoBoxManager != null) infoBoxManager.removeIf(PyramidPlunderTimer.class::isInstance);
        timer = null;
        status = "Stopped";

        if (script != null) script.shutdown();

        if (clientThread != null) {
            clientThread.invoke(() -> {
                Widget w = client.getWidget(InterfaceID.NtkOverlay.CONTENT);
                if (w != null) w.setHidden(false);
            });
        }
    }

    // ===== Config =====
    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        log.debug("Pyramid Plunder config changed: {} = {}", event.getKey(), event.getNewValue());
    }

    // ===== Event Handlers =====
    @Subscribe
    public void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() == GameState.LOADING) {
            tilesToHighlight.clear();
            objectsToHighlight.clear();
        }
    }

    @Subscribe
    public void onGameTick(GameTick t) {
        if (Rs2Player.isInCombat()) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
        }
        if (!Rs2Player.isInCombat() && Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MELEE)) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE);
        }
        if (Rs2Player.getHealthPercentage() < 100.0) {
            Rs2Prayer.toggle(Rs2PrayerEnum.RAPID_HEAL, true);
        }
        if (Rs2Player.getHealthPercentage() == 100.0 && Rs2Prayer.isPrayerActive(Rs2PrayerEnum.RAPID_HEAL)) {
            Rs2Prayer.toggle(Rs2PrayerEnum.RAPID_HEAL);
        }
        handleTimer();
        if (script != null) adjustScriptStateIfOutside();
        computeCachedRooms();
    }

    private void handleTimer() {
        if (!isInPyramidPlunder()) {
            if (timer != null) {
                infoBoxManager.removeInfoBox(timer);
                timer = null;
            }
            return;
        }
        if (timer != null) return;

        int ppTicks = client.getVarbitValue(VarbitID.NTK_PLAYER_TIMER_COUNT);
        Duration remaining = PYRAMID_PLUNDER_DURATION.minus(ppTicks, RSTimeUnit.GAME_TICKS);

        timer = new PyramidPlunderTimer(
                remaining,
                itemManager.getImage(ItemID.NTK_JEWELLED_SCEPTRE_3),
                this,
                cfg(),
                client
        );
        infoBoxManager.addInfoBox(timer);
    }

    private void adjustScriptStateIfOutside() {
        WorldPoint wp = client.getLocalPlayer().getWorldLocation();
        if (PyramidPlunderScript.OUTSIDE_PYRAMID_PLUNDER.contains(wp)) {
            PyramidPlunderState st = script.getState();
            if (st != PyramidPlunderState.LOOT_CHECK &&
                    st != PyramidPlunderState.BANKING &&
                    st != PyramidPlunderState.ENTER_PYRAMID &&
                    st != PyramidPlunderState.SUPPLIES_CHECK) {
                script.setState(PyramidPlunderState.LOOT_CHECK);
            }
        }
    }

    private boolean isInPyramidPlunder() {
        Player p = client.getLocalPlayer();
        return p != null &&
                p.getWorldLocation().getRegionID() == PYRAMID_PLUNDER_REGION &&
                client.getVarbitValue(VarbitID.NTK_PLAYER_TIMER_COUNT) > 0;
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned e) {
        WallObject o = e.getWallObject();
        if (TOMB_DOOR_WALL_IDS.contains(o.getId())) tilesToHighlight.put(o, e.getTile());
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned e) {
        GameObject o = e.getGameObject();
        if (SPEARTRAP_ID == o.getId()) tilesToHighlight.put(o, e.getTile());
        else if (URN_IDS.contains(o.getId()) || o.getId() == GRAND_GOLD_CHEST_ID || o.getId() == SARCOPHAGUS_ID) {
            objectsToHighlight.add(o);
        }
    }

    // ===== Public getters (script thread safe) =====
    public int getUrnRoom()      { return cachedUrnRoom; }  // -1 means none
    public int getChestRoom()    { return cachedChestRoom; }
    public int getSarcRoom()     { return cachedSarcRoom;  }

    // ===== Cache compute =====
    private void computeCachedRooms() {
        // If outside minigame or varbit unset, kill cache to -1 and bail
        int roomVar = client.getVarbitValue(VarbitID.NTK_ROOM_NUMBER);
        if (roomVar < 1 || roomVar > 8) {
            cachedUrnRoom = cachedChestRoom = cachedSarcRoom = -1;
            return;
        }

        PyramidPlunderConfig c = cfg();
        int urn   = calcRoom(c, LootType.URN, timer, roomVar);
        int chest = calcRoom(c, LootType.CHEST, null, roomVar);
        int sarc  = calcRoom(c, LootType.SARCOPHAGUS, null, roomVar);

        cachedUrnRoom   = urn;
        cachedChestRoom = chest;
        cachedSarcRoom  = sarc;
    }

    // ===== Internal calc (client thread only) =====
    private int calcRoom(PyramidPlunderConfig c, LootType type, PyramidPlunderTimer tmr, int curRoom) {
        int lvl = client.getRealSkillLevel(Skill.THIEVING);
        if (lvl <= 0) return -1;

        int[] eligible = collectEligible(lvl, curRoom);
        if (eligible.length == 0) return -1;

        switch (type) {
            case URN:         return pickUrnRoom(c, eligible, lvl, curRoom, tmr);
            case CHEST:       return pickChestRoom(c, eligible);
            case SARCOPHAGUS: return pickSarcRoom(c, eligible);
            default:          return -1;
        }
    }

    private int[] collectEligible(int lvl, int cur) {
        int count = 0;
        int[] tmp = new int[8];
        for (int i = 0; i < FLOOR_REQS.length; i++) {
            if (lvl >= FLOOR_REQS[i] && (i + 1) >= cur) tmp[count++] = i + 1;
        }
        return Arrays.copyOf(tmp, count);
    }

    private int pickUrnRoom(PyramidPlunderConfig c, int[] eligible, int lvl, int cur, PyramidPlunderTimer tmr) {
        switch (c.urnMode()) {
            case NONE:        return -1;
            case HIGHEST_1:   return eligible[eligible.length - 1];
            case HIGHEST_2:   return eligible.length > 1 ? eligible[eligible.length - 2] : eligible[0];
            case TIMER_BASED:
                if (tmr == null) return -1;
                long sec = Math.max(0, Duration.between(Instant.now(), tmr.getEndTime()).getSeconds());
                Set<Integer> valid = getTimerBasedUrnRooms(lvl, (int) sec);
                int pick = Integer.MAX_VALUE;
                for (int r : valid) if (r >= cur && r < pick) pick = r;
                return pick == Integer.MAX_VALUE ? -1 : pick;
            default:          return -1;
        }
    }

    private int pickChestRoom(PyramidPlunderConfig c, int[] eligible) {
        switch (c.chestMode()) {
            case ALL:        return eligible[0];
            case HIGHEST_2:  return eligible.length > 1 ? eligible[eligible.length - 2] : eligible[0];
            case HIGHEST_5:  return eligible.length > 4 ? eligible[eligible.length - 5] : eligible[0];
            default:         return -1;
        }
    }

    private int pickSarcRoom(PyramidPlunderConfig c, int[] eligible) {
        switch (c.sarcophagusMode()) {
            case NONE:        return -1;
            case HIGHEST_2:   return eligible.length > 1 ? eligible[eligible.length - 2] : eligible[0];
            case HIGHEST_5:   return eligible.length > 4 ? eligible[eligible.length - 5] : eligible[0];
            default:          return -1;
        }
    }

    public enum LootType { URN, CHEST, SARCOPHAGUS }

    public static Set<Integer> getTimerBasedUrnRooms(int level, int seconds) {
        int[] tmp = new int[8];
        int cnt = 0;
        for (int i = 0; i < FLOOR_REQS.length; i++) if (level >= FLOOR_REQS[i]) tmp[cnt++] = i + 1;

        Set<Integer> res = new HashSet<>();
        if (cnt == 0) return res;

        if (seconds > 240 && cnt >= 3) res.add(tmp[cnt - 3]);
        else if (seconds > 120 && cnt >= 2) res.add(tmp[cnt - 2]);
        else if (seconds > 30  && cnt >= 1) res.add(tmp[cnt - 1]);

        return res;
    }

    @Subscribe
    public void onPluginScheduleEntrySoftStopEvent(PluginScheduleEntrySoftStopEvent event) {
        if (event.getPlugin() == this) {
            Microbot.stopPlugin(this);
        }
    }
}
