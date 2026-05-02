package net.runelite.client.plugins.microbot.tocigemsleagues;

import java.util.concurrent.TimeUnit;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.api.npc.Rs2NpcQueryable;
import net.runelite.client.plugins.microbot.tocigemsleagues.TociGemsLeaguesPlugin;
import net.runelite.client.plugins.microbot.tocigemsleagues.TociGemsLeaguesState;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

public class TociGemsLeaguesScript
extends Script {
    private static final WorldPoint TOCI_LOCATION = new WorldPoint(1428, 2975, 0);
    private static final String TOCI_NPC_NAME = "Toci";
    private static final int TOCI_NPC_ID = 13915;
    private static final String UNCUT_RUBY_SHOP_NAME = "Uncut ruby";
    private static final int RUBY_SELL_PREFERRED_SLOT = 27;
    private static final int MAX_RAPID_BUYS_PER_TICK = 56;
    private static final int MAX_RAPID_SELLS_PER_TICK = 56;
    private static final int RAPID_BUY_SLEEP_MIN_MS = 85;
    private static final int RAPID_BUY_SLEEP_MAX_MS = 160;
    private static final int RAPID_SELL_SLEEP_MIN_MS = 85;
    private static final int RAPID_SELL_SLEEP_MAX_MS = 160;
    private static final int TICK_MS = 400;
    private TociGemsLeaguesState state = TociGemsLeaguesState.INITIALIZING;

    public boolean run() {
        TociGemsLeaguesPlugin.cycles = 0;
        TociGemsLeaguesPlugin.status = "Initializing...";
        this.mainScheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!Microbot.isLoggedIn()) {
                return;
            }
            if (!super.run()) {
                return;
            }
            switch (this.state) {
                case INITIALIZING: {
                    TociGemsLeaguesPlugin.status = "Checking inventory...";
                    if (!Rs2Inventory.hasItem((int[])new int[]{1755})) {
                        TociGemsLeaguesPlugin.status = "Need a chisel in inventory";
                        Microbot.showMessage((String)"Toci Gems Leagues: put a chisel in your inventory.");
                        this.state = TociGemsLeaguesState.FINISHED;
                        break;
                    }
                    this.state = TociGemsLeaguesState.WALK_TO_TOCI;
                    break;
                }
                case WALK_TO_TOCI: {
                    TociGemsLeaguesPlugin.status = "Walking to Toci...";
                    if (Rs2Npc.getNpc((String)TOCI_NPC_NAME) != null) {
                        this.state = TociGemsLeaguesState.OPEN_SHOP_BUY;
                        break;
                    }
                    if (Rs2Player.isMoving()) break;
                    Rs2Walker.walkTo((WorldPoint)TOCI_LOCATION, (int)6);
                    break;
                }
                case OPEN_SHOP_BUY: {
                    TociGemsLeaguesPlugin.status = "Opening shop...";
                    if (Rs2Shop.isOpen()) {
                        this.state = TociGemsLeaguesState.BUY_ONE_UNCUT;
                        break;
                    }
                    if (!TociGemsLeaguesScript.openTociShop()) break;
                    TociGemsLeaguesScript.sleepUntil(Rs2Shop::isOpen, (int)4000);
                    break;
                }
                case BUY_ONE_UNCUT: {
                    TociGemsLeaguesPlugin.status = "Buying uncut rubies until full...";
                    if (!Rs2Shop.isOpen()) {
                        this.state = TociGemsLeaguesState.OPEN_SHOP_BUY;
                        break;
                    }
                    if (Rs2Inventory.emptySlotCount() <= 0) {
                        this.state = TociGemsLeaguesState.CLOSE_SHOP_AFTER_BUY;
                        break;
                    }
                    for (int i = 0; i < 56 && this.isRunning() && Rs2Inventory.emptySlotCount() > 0; ++i) {
                        Rs2Shop.buyItem((String)UNCUT_RUBY_SHOP_NAME, (String)"1");
                        TociGemsLeaguesScript.sleep((int)Rs2Random.between((int)85, (int)160));
                    }
                    if (Rs2Inventory.emptySlotCount() > 0) break;
                    this.state = TociGemsLeaguesState.CLOSE_SHOP_AFTER_BUY;
                    break;
                }
                case CLOSE_SHOP_AFTER_BUY: {
                    TociGemsLeaguesPlugin.status = "Closing shop...";
                    Rs2Shop.closeShop();
                    TociGemsLeaguesScript.sleep((int)Rs2Random.between((int)300, (int)500));
                    this.state = TociGemsLeaguesState.CUT_APPLY;
                    break;
                }
                case CUT_APPLY: {
                    TociGemsLeaguesPlugin.status = "Cutting ruby...";
                    if (!Rs2Inventory.hasItem((int[])new int[]{1619}) && !Rs2Inventory.hasItem((String[])new String[]{UNCUT_RUBY_SHOP_NAME})) {
                        this.state = TociGemsLeaguesState.OPEN_SHOP_SELL;
                        break;
                    }
                    if (Rs2Shop.isOpen()) {
                        Rs2Shop.closeShop();
                        TociGemsLeaguesScript.sleep((int)Rs2Random.between((int)200, (int)400));
                    }
                    for (int attempt = 0; attempt < 4 && Rs2Inventory.hasItem((int[])new int[]{1619}) && !Rs2Player.isAnimating(); ++attempt) {
                        if (attempt == 0 || !Rs2Dialogue.hasQuestion((String)"How many do you wish to make?")) {
                            Rs2Inventory.use((int)1755);
                            TociGemsLeaguesScript.sleep((int)Rs2Random.between((int)250, (int)450));
                            Rs2Inventory.use((int)1619);
                            TociGemsLeaguesScript.sleep((int)Rs2Random.between((int)450, (int)700));
                        }
                        TociGemsLeaguesScript.sleepUntil(() -> Rs2Dialogue.hasQuestion((String)"How many do you wish to make?"), (int)2500);
                        if (Rs2Dialogue.hasQuestion((String)"How many do you wish to make?")) {
                            Rs2Keyboard.keyPress((int)32);
                            TociGemsLeaguesScript.sleep((int)Rs2Random.between((int)350, (int)600));
                        }
                        if (Rs2Player.isAnimating()) break;
                    }
                    this.state = TociGemsLeaguesState.CUT_WAIT;
                    break;
                }
                case CUT_WAIT: {
                    TociGemsLeaguesPlugin.status = "Waiting for cut to finish...";
                    boolean doneCutting = TociGemsLeaguesScript.sleepUntil(() -> !Rs2Inventory.hasItem((int[])new int[]{1619}) || !Rs2Player.isAnimating(), (int)60000);
                    if (!doneCutting) {
                        TociGemsLeaguesPlugin.status = "Cut timed out \u2014 stopping";
                        Microbot.showMessage((String)"Toci Gems Leagues: cutting took too long.");
                        this.state = TociGemsLeaguesState.FINISHED;
                        break;
                    }
                    this.state = TociGemsLeaguesState.OPEN_SHOP_SELL;
                    break;
                }
                case OPEN_SHOP_SELL: {
                    TociGemsLeaguesPlugin.status = "Preparing to sell cut rubies...";
                    if (!Rs2Inventory.hasItem((int[])new int[]{1603})) {
                        ++TociGemsLeaguesPlugin.cycles;
                        this.state = TociGemsLeaguesState.OPEN_SHOP_BUY;
                        break;
                    }
                    if (!Rs2Shop.isOpen()) {
                        if (!TociGemsLeaguesScript.openTociShop()) break;
                        TociGemsLeaguesScript.sleepUntil(Rs2Shop::isOpen, (int)4000);
                        break;
                    }
                    this.state = TociGemsLeaguesState.SELL_ONE_RUBY;
                    break;
                }
                case SELL_ONE_RUBY: {
                    TociGemsLeaguesPlugin.status = "Selling cut rubies...";
                    if (!Rs2Inventory.hasItem((int[])new int[]{1603})) {
                        ++TociGemsLeaguesPlugin.cycles;
                        this.state = TociGemsLeaguesState.OPEN_SHOP_BUY;
                        break;
                    }
                    if (!Rs2Shop.isOpen()) {
                        this.state = TociGemsLeaguesState.OPEN_SHOP_SELL;
                        break;
                    }
                    for (int i = 0; i < 56 && this.isRunning() && Rs2Inventory.hasItem((int[])new int[]{1603}); ++i) {
                        int sellSlot = TociGemsLeaguesScript.rubySellSlot();
                        if (sellSlot < 0) {
                            TociGemsLeaguesPlugin.status = "Rubies not found in inventory \u2014 stopping";
                            this.state = TociGemsLeaguesState.FINISHED;
                            break;
                        }
                        Rs2Inventory.slotInteract((int)sellSlot, (String)"Sell 1");
                        TociGemsLeaguesScript.sleep((int)Rs2Random.between((int)85, (int)160));
                    }
                    if (this.state == TociGemsLeaguesState.FINISHED) break;
                    this.state = TociGemsLeaguesState.OPEN_SHOP_SELL;
                    break;
                }
                case FINISHED: {
                    TociGemsLeaguesPlugin.status = "Stopped.";
                    this.shutdown();
                    break;
                }
                default: {
                    this.state = TociGemsLeaguesState.WALK_TO_TOCI;
                }
            }
        }, 0L, 400L, TimeUnit.MILLISECONDS);
        return true;
    }

    private static int rubySellSlot() {
        if (Rs2Inventory.slotContains((int)27, (int[])new int[]{1603})) {
            return 27;
        }
        return Rs2Inventory.items(item -> item.getId() == 1603).reduce((a, b) -> b).map(Rs2ItemModel::getSlot).orElse(-1);
    }

    private static boolean openTociShop() {
        if (Rs2Shop.openShop((String)TOCI_NPC_NAME)) {
            return true;
        }
        if (((Rs2NpcQueryable)Microbot.getRs2NpcCache().query().withId(13915)).interact("Trade")) {
            return true;
        }
        return ((Rs2NpcQueryable)Microbot.getRs2NpcCache().query().withId(13915)).interact("Exchange");
    }

    public void shutdown() {
        this.state = TociGemsLeaguesState.INITIALIZING;
        super.shutdown();
    }
}
