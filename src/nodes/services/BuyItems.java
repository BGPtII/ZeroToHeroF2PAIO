package nodes.services;

import framework.Node;
import framework.SCScript;
import framework.ScriptState;
import data.global.ScriptData;
import nodes.DetermineTask;
import nodes.InitializeTask;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.items.Item;

/**
 *
 */
public class BuyItems implements Node {

    private byte buyStage;
    private byte currentBuyI;
    private final Condition GE_CONTAINS_CURRENT_BUY_I = () -> GrandExchange.contains(SCScript.GE_DATA.getBuyID(currentBuyI));
    private final Condition COLLECTED_ITEM = () -> !GrandExchange.isBuyOpen() || ScriptData.currentWidgetChild == null || ScriptData.currentWidgetChild.getActions() == null;
    private final int[] MISSED_ID = new int[10];
    private final int[] MISSED_QTY = new int[10];
    private final int[] MISSED_PRC = new int[10];
    private byte missedSize;

    @Override
    public int loop() {
        if (buyStage > 0 && !GrandExchange.isOpen()) { // Guard in case log out, must open GrandExchange again
            if (ScriptData.GRAND_EXCHANGE.contains(Players.getLocal())) {
                GrandExchange.open();
                Sleep.sleepUntil(ScriptData.GRAND_EXCHANGE_IS_OPEN, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            }
            else if (!ScriptData.walkToArea(ScriptData.GRAND_EXCHANGE)) {
                ScriptData.walkToArea(ScriptData.GRAND_EXCHANGE);
            }
        }
        else {
            switch (buyStage) {
                case 0:
                    if (Inventory.onlyContains(995) && !Bank.contains(995)) {
                        buyStage = 1;
                        Logger.log("Proceeding to buyStage: 1");
                        SCScript.GE_DATA.shuffleBuy();
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (ScriptData.progressionTaskTimer != null && !ScriptData.progressionTaskTimer.isPaused()) {
                        Logger.log("Pausing progressionTaskTimer");
                        ScriptData.progressionTaskTimer.pause();
                    }
                    else {
                        if (!Equipment.isEmpty()) {
                            SCScript.BANKING_DATA.toggleDpAllEqpInit(true);
                        }
                        if (!Inventory.isEmpty()) {
                            if (Inventory.contains(995)) {
                                for (Item item : Inventory.toArray()) {
                                    if (item != null) {
                                        int id = item.getId();
                                        if (id != 995 && SCScript.BANKING_DATA.depositNotContainsID(id)) {
                                            SCScript.BANKING_DATA.addItemToDeposit(id, Inventory.count(id));
                                        }
                                    }
                                }
                            }
                            else {
                                SCScript.BANKING_DATA.toggleDpAllInvInit(true);
                                Logger.log("Needs to depositAllInventory");
                            }
                        }
                        if (Bank.contains(995)) {
                            SCScript.BANKING_DATA.addItemToWithdraw(995, Bank.count(995) + Inventory.count(995));
                        }
                        if (SCScript.BANKING_DATA.bankingShouldBeToggled()) {
                            SCScript.scriptState = ScriptState.BANKING;
                            ScriptData.returnTo = ScriptState.BUY_ITEMS;
                        }
                    }
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                case 1: // Place offers
                    if (SCScript.GE_DATA.getBuySize() == 0) { // No buy offers left
                        if (GrandExchange.isOpen()) {
                            GrandExchange.close();
                            Sleep.sleepUntil(ScriptData.GRAND_EXCHANGE_CLOSED, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            if (ScriptData.GRAND_EXCHANGE_CLOSED.verify()) {
                                buyStage = 0;
                                currentBuyI = 0;
                                for (byte i = 0; i < missedSize; i++) { // Add back items that failed to buy/couldn't because lack of gp
                                    Logger.log("Added toBuy from MISSED - id: " + MISSED_ID[i] + ", qty: " + MISSED_QTY[i] + ", prc: " + MISSED_PRC[i]);
                                    SCScript.GE_DATA.addBuyItem(MISSED_ID[i], MISSED_QTY[i], MISSED_PRC[i]);
                                }
                                missedSize = 0;
                                InitializeTask.initializeTaskI = 1;
                                if (SCScript.GE_DATA.getBuySize() == 0 && ScriptData.currentTask == ScriptData.currentMoneyMakingTask) {
                                    Logger.log("Bought everything needed to buy");
                                    SCScript.scriptState = ScriptState.DETERMINE_TASK;
                                    DetermineTask.taskType = 2;
                                }
                                else {
                                    Logger.log("Failed to buy everything");
                                    ScriptData.returnTo = ScriptState.INITIALIZE_TASK;
                                    SCScript.scriptState = ScriptState.INITIALIZE_TASK;
                                }
                            }
                        }
                    }
                    else if (GrandExchange.getUsedSlots() == Math.min(3, SCScript.GE_DATA.getBuySize())) {
                        Sleep.sleepUntil(ScriptData.GRAND_EXCHANGE_READY_TO_COLLECT, SCScript.SECURE_RANDOM.nextInt(120000 - 3000 + 1) + 30000, 500); // 3s - 2m
                        Logger.log("Done placing offers, going back to buyStage: 2, currentBuyI: 0");
                        buyStage = 2;
                        currentBuyI = 0;
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (Inventory.count(995) < SCScript.GE_DATA.getBuyTotalPrice(currentBuyI)) {
                        Logger.log("currentBuyI id: " + SCScript.GE_DATA.getBuyID(currentBuyI) + "inv gp (" + Inventory.count(995) + ") < totalBuyPrice of currentBuyI: " + SCScript.GE_DATA.getBuyTotalPrice(currentBuyI));
                        Logger.log("Added id back to buy: " + SCScript.GE_DATA.getBuyID(currentBuyI));
                        MISSED_ID[missedSize] = SCScript.GE_DATA.getBuyID(currentBuyI);
                        MISSED_QTY[missedSize] = SCScript.GE_DATA.getBuyQty(currentBuyI);
                        MISSED_PRC[missedSize++] = SCScript.GE_DATA.getBuyPrice(currentBuyI);
                        SCScript.GE_DATA.removeBuy(currentBuyI);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (GrandExchange.buyItem(SCScript.GE_DATA.getBuyID(currentBuyI), SCScript.GE_DATA.getBuyQty(currentBuyI), SCScript.GE_DATA.getBuyPrice(currentBuyI))) {
                        Sleep.sleepUntil(GE_CONTAINS_CURRENT_BUY_I, SCScript.SECURE_RANDOM.nextInt(20000 - 10000 + 1) + 10000, 300);
                        if (GE_CONTAINS_CURRENT_BUY_I.verify()) {
                            currentBuyI++;
                        }
                    }
                    break;
                case 2: // Cancel and collect
                    if (GrandExchange.getUsedSlots() == 0) {
                        buyStage = 1; // Place more offers
                        currentBuyI = 0;
                        Logger.log("Place more offers");
                        SCScript.GE_DATA.shuffleBuy();
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (GrandExchange.slotContainsItem(currentBuyI)) {
                        if (!GrandExchange.isReadyToCollect(currentBuyI)) {
                            GrandExchange.cancelOffer(currentBuyI);
                        }
                        else if (GrandExchange.isBuyOpen()) {
                            if ((ScriptData.currentWidgetChild = Widgets.get(465, 24, 2)) != null && ScriptData.currentWidgetChild.getActions() != null) { // Item in first/left slot (Coins if aborted & nothing bought)
                                int stackSize = ScriptData.currentWidgetChild.getItemStack();
                                String name = ScriptData.currentWidgetChild.getName();
                                if (ScriptData.currentWidgetChild.interact()) { // Verify interaction for collecting either the currentItem or Coins
                                    Sleep.sleepUntil(COLLECTED_ITEM, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                                    if (COLLECTED_ITEM.verify()) { // Verify interaction went through
                                        if (name.contains("Coins")) { // Didn't completely buy
                                            SCScript.GE_DATA.increaseBuyPrice(currentBuyI);
                                        }
                                        else {
                                            SCScript.GE_DATA.reduceBuyQuantity(currentBuyI, stackSize);
                                            if (SCScript.GE_DATA.getBuyQty(currentBuyI) == 0) { // Completely bought
                                                SCScript.GE_DATA.removeBuy(currentBuyI);
                                            }
                                            else {
                                                SCScript.GE_DATA.increaseBuyPrice(currentBuyI);
                                            }
                                        }
                                    }
                                }
                            }
                            else if ((ScriptData.currentWidgetChild = Widgets.get(465, 24, 3)) != null && ScriptData.currentWidgetChild.getActions() != null) { // Item in second/right slot (Coins)
                                if (ScriptData.currentWidgetChild.interact()) { // Verify interaction
                                    Sleep.sleepUntil(COLLECTED_ITEM, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300); // Verify interaction went through
                                }
                            }
                        }
                        else {
                            GrandExchange.openSlotInterface(currentBuyI);
                        }
                    }
                    else {
                        currentBuyI++;
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    break;
            }
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
