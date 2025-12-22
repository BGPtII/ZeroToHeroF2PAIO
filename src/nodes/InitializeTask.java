package nodes;

import framework.Node;
import framework.SCScript;
import framework.ScriptState;
import data.global.PlayerData;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;

/**
 * - Buys items it doesn't have
 * - Gets rid of items it doesn't need
 * When rolling a MoneyMakingTask:
 * - taskType set to 1
 */
public class InitializeTask implements Node {

    public static byte initializeTaskI; // Reset all once finished

    private int getTaskLevel() {
        switch (ScriptData.currentTask) {
            case WOODCUTTING_TRAINING: // Progression - Training
            case CHOPPING_LOGS:
                return Math.min(30, Skills.getRealLevel(Skill.WOODCUTTING));
            case MINING_TRAINING:
                return Math.min(30, Skills.getRealLevel(Skill.MINING));
            case MELEE_TRAINING:
                return Math.min(30, Math.min(Skills.getRealLevel(Skill.ATTACK), Math.min(Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE))));
            case RANGED_TRAINING:
                return Math.min(30, Skills.getRealLevel(Skill.RANGED));
            case FISHING_TRAINING:
                return Math.min(30, Skills.getRealLevel(Skill.FISHING));
            case SPINNING_BALLS_OF_WOOL: // MoneyMaking
                return Math.min(30, Skills.getRealLevel(Skill.CRAFTING));
            case SMELTING_BRONZE_BARS:
                return Math.min(30, Skills.getRealLevel(Skill.SMITHING));
            default:
                return -1;
        }
    }

    @Override
    public int loop() {
        switch (initializeTaskI) {
            case 0: // initialAddToBuy
                ScriptData.returnTo = ScriptState.INITIALIZE_TASK;
                initializeTaskI++;
                for (byte i = 0; i < ScriptData.currentLoadOutData.getInvSize(); i++) { // Add inv toBuy
                    int heldCount = PlayerData.getTotalHeldCount(ScriptData.currentLoadOutData.getInvItemID(i));
                    if (heldCount < ScriptData.currentLoadOutData.getInvItemQtyInit(i)) {
                        SCScript.GE_DATA.addBuyItem(ScriptData.currentLoadOutData.getInvItemID(i), ScriptData.currentLoadOutData.getInvItemQtyInit(i) - heldCount);
                        Logger.log("Added ID " + ScriptData.currentLoadOutData.getInvItemID(i) + " to buyList, qty: " + (ScriptData.currentLoadOutData.getInvItemQtyInit(i) - heldCount));
                    }
                }
                for (byte i = 0; i < ScriptData.currentLoadOutData.getEqpSize(); i++) { // Add eqp toBuy
                    int heldCount = PlayerData.getTotalHeldCount(ScriptData.currentLoadOutData.getEqpItemID(i));
                    if (heldCount < ScriptData.currentLoadOutData.getEqpItemQtyInit(i)) {
                        SCScript.GE_DATA.addBuyItem(ScriptData.currentLoadOutData.getEqpItemID(i), ScriptData.currentLoadOutData.getEqpItemQtyInit(i) - heldCount);
                        Logger.log("Added ID " + ScriptData.currentLoadOutData.getEqpItemID(i) + " to buyList, qty: " + (ScriptData.currentLoadOutData.getEqpItemQtyInit(i) - heldCount));
                    }
                }
                Logger.log("Finished 0: initialAddToBuy");
                break;
            case 1: // buy/sell/makeMoney
                if (SCScript.GE_DATA.getBuySize() != 0) { // Needs to buy items
                    int totalCoins = Bank.count("Coins") + Inventory.count("Coins");
                    Logger.log("totalCoins: " + totalCoins);
                    int totalToBuyEstCost = 0;
                    for (byte i = 0; i < SCScript.GE_DATA.getBuySize(); i++) { // Must be able to buy all items in order to trigger
                        totalToBuyEstCost += SCScript.GE_DATA.getBuyTotalPrice(i);
                    }
                    Logger.log("totalToBuyEstCost: " + totalToBuyEstCost);
                    if (totalCoins >= totalToBuyEstCost) { // Trigger toBuy
                        SCScript.GE_DATA.shuffleBuy();
                        SCScript.scriptState = ScriptState.BUY_ITEMS;
                        Logger.log("buyItems triggered");
                    }
                    else { // Check if items can be sold to afford
                        SCScript.GE_DATA.attemptAddSellableItems();
                        if (SCScript.GE_DATA.getSellSize() != 0) {
                            SCScript.scriptState = ScriptState.SELL_ITEMS;
                            Logger.log("sellItems triggered");
                        }
                        else { // Determine a moneyMakingTask
                            SCScript.scriptState = ScriptState.DETERMINE_TASK;
                            DetermineTask.taskType = 1;
                            if (ScriptData.progressionTaskTimer != null) {
                                ScriptData.progressionTaskTimer.pause();
                                Logger.log("Pausing progressionTaskTimer");
                            }
                            Logger.log("Needs to determineMoneyMakingTask");
                        }
                    }
                }
                else {
                    Logger.log("Finished 1: buy/sell/makeMoney");
                    initializeTaskI = 2; // Move to next stage, has all initial items
                }
                break;
            case 2: // purgeInvEqp
                boolean hasNonLoadOutEqp = false;
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    Item item = Equipment.getItemInSlot(slot);
                    if (item == null) {
                        continue;
                    }
                    int id = item.getId();
                    boolean inEqpLoadout = false;
                    for (byte j = 0; j < ScriptData.currentLoadOutData.getEqpSize(); j++) {
                        if (id == ScriptData.currentLoadOutData.getEqpItemID(j)) {
                            inEqpLoadout = true;
                            break;
                        }
                    }
                    if (!inEqpLoadout) {
                        hasNonLoadOutEqp = true;
                        break;
                    }
                }
                if (hasNonLoadOutEqp) {
                    Logger.log("Has an equipment item that isn't in eqpLoadOut, depositAllEqp triggered");
                    SCScript.BANKING_DATA.toggleDpAllEqpInit(true);
                }
                Logger.log("Done purgeEqp");

                boolean invHasLoadout = false;
                boolean invHasNonLoadout = false;
                for (Item item : Inventory.toArray()) {
                    if (item == null) {
                        continue;
                    }
                    int id = item.getId();

                    boolean inLoadout = false;

                    for (byte i = 0; i < ScriptData.currentLoadOutData.getInvSize(); i++) { // Check if item in invLoadOut
                        if (id == ScriptData.currentLoadOutData.getInvItemID(i)) {
                            inLoadout = true;
                            break;
                        }
                    }
                    if (!inLoadout) { // Check if item in eqpLoadOut
                        for (byte i = 0; i < ScriptData.currentLoadOutData.getEqpSize(); i++) {
                            if (id == ScriptData.currentLoadOutData.getEqpItemID(i)) {
                                inLoadout = true;
                                break;
                            }
                        }
                    }

                    if (inLoadout) {
                        invHasLoadout = true;
                    }
                    else  {
                        invHasNonLoadout = true;
                    }
                    if (invHasLoadout && invHasNonLoadout) {
                        break;
                    }
                }

                if (invHasNonLoadout && !invHasLoadout) {
                    Logger.log("invDoesNotHaveLoadOutItem, depositAllInv triggered");
                    SCScript.BANKING_DATA.toggleDpAllInvInit(true);
                }
                else {
                    for (Item item : Inventory.toArray()) {
                        if (item == null) {
                            continue;
                        }
                        int id = item.getId();

                        boolean isInvLoadout = false;
                        for (byte i = 0; i < ScriptData.currentLoadOutData.getInvSize(); i++) {
                            if (id == ScriptData.currentLoadOutData.getInvItemID(i)) { isInvLoadout = true; break; }
                        }
                        boolean isEqpLoadout = false;
                        if (!isInvLoadout) {
                            for (byte i = 0; i < ScriptData.currentLoadOutData.getEqpSize(); i++) {
                                if (id == ScriptData.currentLoadOutData.getEqpItemID(i)) { isEqpLoadout = true; break; }
                            }
                        }

                        if (!isInvLoadout && !isEqpLoadout) { // Not in any loadOut > deposit this whole stack
                            if (SCScript.BANKING_DATA.depositNotContainsID(id)) {
                                SCScript.BANKING_DATA.addItemToDeposit(id, Inventory.count(id));
                            }
                        }
                        else if (isInvLoadout) { // Enforce inv max for loadOut items
                            for (byte i = 0; i < ScriptData.currentLoadOutData.getInvSize(); i++) {
                                if (id == ScriptData.currentLoadOutData.getInvItemID(i)) {
                                    int max = ScriptData.currentLoadOutData.getInvItemQtyMax(i);
                                    if (max > 0) {
                                        int extra = Inventory.count(id) - max;
                                        if (extra > 0 && SCScript.BANKING_DATA.depositNotContainsID(id)) {
                                            SCScript.BANKING_DATA.addItemToDeposit(id, extra);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    Logger.log("Inventory has a loadOutItem, added extras to deposit");
                }

                Logger.log("Done purgeInv");
                if (SCScript.BANKING_DATA.bankingShouldBeToggled()) {
                    SCScript.BANKING_DATA.shuffleDeposit();
                    SCScript.BANKING_DATA.shuffleWithdraw();
                    Logger.log("Banking triggered");
                    SCScript.scriptState = ScriptState.BANKING;
                }
                else {
                    initializeTaskI++;
                    Logger.log("Finished 2: purgeInvEqp");
                }
                break;
            case 3: // WithdrawEqpInv
                for (byte i = 0; i < ScriptData.currentLoadOutData.getEqpSize(); i++) {
                    if (Equipment.count(ScriptData.currentLoadOutData.getEqpItemID(i)) < ScriptData.currentLoadOutData.getEqpItemQtyInit(i)) {
                        if (Inventory.contains(ScriptData.currentLoadOutData.getEqpItemID(i))) {
                            Item invItem = Inventory.get(ScriptData.currentLoadOutData.getEqpItemID(i)); // Gets the item for the corresponding ID
                            if (invItem != null) {
                                if (invItem.hasAction("Wield")) {
                                    if (invItem.interact("Wield")) {
                                        Sleep.sleepUntil(() -> Inventory.getIdForSlot(invItem.getSlot()) == -1, SCScript.SECURE_RANDOM.nextInt(5000 - 2000 + 1) + 2000); // No item in slot
                                    }
                                }
                                else if (invItem.interact("Wear")) {
                                    Sleep.sleepUntil(() -> Inventory.getIdForSlot(invItem.getSlot()) == -1, SCScript.SECURE_RANDOM.nextInt(5000 - 2000 + 1) + 2000);
                                }
                            }
                        }
                        else if (Bank.contains(ScriptData.currentLoadOutData.getEqpItemID(i))) {
                            SCScript.BANKING_DATA.addItemToWithdraw(ScriptData.currentLoadOutData.getEqpItemID(i), Bank.count(ScriptData.currentLoadOutData.getEqpItemID(i)));
                        }
                    }
                }
                for (byte i = 0; i < ScriptData.currentLoadOutData.getInvSize(); i++) {
                    if (Inventory.count(ScriptData.currentLoadOutData.getInvItemID(i)) < ScriptData.currentLoadOutData.getInvItemQtyMax(i)) {
                        int toWithdraw = Math.min(ScriptData.currentLoadOutData.getInvItemQtyMax(i) - Inventory.count(ScriptData.currentLoadOutData.getInvItemID(i)), Bank.count(ScriptData.currentLoadOutData.getInvItemID(i)));
                        if (toWithdraw != 0) {
                            SCScript.BANKING_DATA.addItemToWithdraw(ScriptData.currentLoadOutData.getInvItemID(i), toWithdraw);
                        }
                    }
                }
                if (SCScript.BANKING_DATA.getWithdrawSize() != 0) {
                    SCScript.BANKING_DATA.shuffleWithdraw();
                    SCScript.scriptState = ScriptState.BANKING;
                }
                else {
                    initializeTaskI++;
                    Logger.log("Finished 3: withdraw");
                }
                break;
            case 4: // Start task
                ScriptData.returnTo = ScriptData.currentTask;
                int level = getTaskLevel();
                double linearInterpolationFactor = (level - 1) / 29.0; // level to percentage (linear interpolation = straight line between 2 (x, y) on a graph

                int minMSL1 = 300000; // 5m
                int maxMSL1 = 1800000; // 30m
                int minMSL30 = 1800000;
                int maxMSL30 = 7200000; // 2h

                long minMs = Math.round(minMSL1 + linearInterpolationFactor * (minMSL30 - minMSL1));
                long maxMs = Math.round(maxMSL1 + linearInterpolationFactor * (maxMSL30 - maxMSL1));

                long span = maxMs - minMs;

                if (DetermineTask.taskType == 1) {
                    Logger.log("Starting moneyMakingTask: " + ScriptData.currentMoneyMakingTask);
                    SCScript.scriptState = ScriptData.currentMoneyMakingTask;
                    ScriptData.moneyMakingTaskTimer = new Timer(minMs + SCScript.SECURE_RANDOM.nextInt((int) span + 1));
                }
                else {
                    SCScript.scriptState = ScriptData.currentProgressionTask;
                    Logger.log("Starting progressionTask: " + ScriptData.currentProgressionTask);
                    if (ScriptData.progressionTaskTimer == null) {
                        Logger.log("Initializing new progressionTaskTimer");
                        ScriptData.progressionTaskTimer = new Timer(minMs + SCScript.SECURE_RANDOM.nextInt((int) span + 1));
                    }
                    else {
                        Logger.log("Resuming progressionTaskTimer");
                        ScriptData.progressionTaskTimer.resume();
                    }
                }

                int ticks = 1000000; // Steps you split the range into (ticks = 10, step is 0.5 / 10 = 0.05), (ticks = 1000000, step is 0.5/1000000 = 0.0000005)
                double pathRand = SCScript.SECURE_RANDOM.nextInt(ticks + 1) / (2.0 * ticks); // 0-0.5 inclusive
                Logger.log("pathRand: " + pathRand);
                WebFinder.getWebFinder().setPathRandomization(pathRand);
                break;
        }

        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
    }

}
