package nodes.services;

import framework.Node;
import framework.SCScript;
import framework.ScriptState;
import data.global.ScriptData;
import nodes.InitializeTask;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;

/**
 *
 */
public class SellItems implements Node {

    private byte sellStage;
    private byte currentSellI;
    private final Condition GE_CONTAINS_CURRENT_SELL_I = () -> GrandExchange.contains(SCScript.GE_DATA.getSellID(currentSellI));
    private final Condition COLLECTED_ITEM = () -> !GrandExchange.isSellOpen() || ScriptData.currentWidgetChild == null || ScriptData.currentWidgetChild.getActions() == null;

    @Override
    public int loop() {
        if (sellStage > 0 && !GrandExchange.isOpen()) { // Guard in case log out, must open GrandExchange again
            if (ScriptData.GRAND_EXCHANGE.contains(Players.getLocal())) {
                GrandExchange.open(); // Only returns true if GE is open
                Sleep.sleepUntil(ScriptData.GRAND_EXCHANGE_IS_OPEN, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            }
            else {
                ScriptData.walkToArea(ScriptData.GRAND_EXCHANGE);
            }
        }
        else {
            switch(sellStage) { // handleInv
                case 0:
                    ScriptData.returnTo = ScriptState.SELL_ITEMS;
                    sellStage++;
                    if (!Inventory.isEmpty()) {
                        SCScript.BANKING_DATA.toggleDpAllInvInit(true);
                    }
                    for (byte i = 0; i < SCScript.GE_DATA.getSellSize(); i++) {
                        SCScript.BANKING_DATA.addItemToWithdraw(SCScript.GE_DATA.getSellID(i), Bank.count(SCScript.GE_DATA.getSellID(i)) + Inventory.count(SCScript.GE_DATA.getSellID(i)) - SCScript.GE_DATA.getSellQuantityToKeep(i));
                    }
                    SCScript.BANKING_DATA.toggleWithdrawMode(true);
                    SCScript.scriptState = ScriptState.BANKING;
                    SCScript.BANKING_DATA.shuffleWithdraw();
                    SCScript.GE_DATA.shuffleSell();
                    break;
                case 1: // Place offers
                    if (SCScript.GE_DATA.getSellSize() == 0) { // No sell offers left
                        if (GrandExchange.isOpen()) {
                            GrandExchange.close();
                            Sleep.sleepUntil(ScriptData.GRAND_EXCHANGE_CLOSED, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            if (!GrandExchange.isOpen()) {
                                Logger.log("Done selling");
                                sellStage = 0;
                                currentSellI = 0;
                                InitializeTask.initializeTaskI = 1;
                                SCScript.BANKING_DATA.toggleWithdrawMode(false);
                                SCScript.scriptState = ScriptState.INITIALIZE_TASK;
                                ScriptData.returnTo = ScriptState.INITIALIZE_TASK;
                            }
                        }
                    }
                    else if (GrandExchange.getUsedSlots() == Math.min(3, SCScript.GE_DATA.getSellSize())) {
                        Logger.log("Placed all offers, waiting for completion");
                        Sleep.sleepUntil(ScriptData.GRAND_EXCHANGE_READY_TO_COLLECT, SCScript.SECURE_RANDOM.nextInt(120000 - 3000 + 1) + 30000, 500); // 3s - 2m
                        sellStage++;
                        currentSellI = 0;
                        Logger.log("Done waiting for completion, sellStage: 2");
                    }
                    else if (GrandExchange.sellItem(SCScript.GE_DATA.getSellID(currentSellI), SCScript.GE_DATA.getSellID(currentSellI), SCScript.GE_DATA.getSellPrice(currentSellI))) {
                        Sleep.sleepUntil(GE_CONTAINS_CURRENT_SELL_I, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        if (GrandExchange.contains(SCScript.GE_DATA.getSellID(currentSellI))) {
                            currentSellI++;
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                    }
                    break;
                case 2: // Cancel and collect
                    if (GrandExchange.getUsedSlots() == 0) {
                        Logger.log("Needs to place more offers");
                        sellStage = 1; // Place more offers
                        currentSellI = 0;
                        SCScript.GE_DATA.shuffleSell();
                        Logger.log("currentSellI: " + currentSellI);
                        Logger.log("Next item to place offer (ID): " + SCScript.GE_DATA.getSellID(currentSellI));
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (GrandExchange.slotContainsItem(currentSellI)) {
                        if (!GrandExchange.isReadyToCollect(currentSellI)) {
                            GrandExchange.cancelOffer(currentSellI);
                            Logger.log("Attempted to cancel offer");
                        }
                        else if (GrandExchange.isSellOpen()) {
                            if ((ScriptData.currentWidgetChild = Widgets.get(465, 24, 2)) != null && ScriptData.currentWidgetChild.getActions() != null) { // Item in first/left slot (Coins if aborted & nothing bought)
                                String name = ScriptData.currentWidgetChild.getName();
                                if (ScriptData.currentWidgetChild.interact()) { // Verify interaction for collecting either the currentItem or Coins
                                    Sleep.sleepUntil(COLLECTED_ITEM, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                                    if (COLLECTED_ITEM.verify()) { // Verify interaction went through
                                        if (name.contains("Coins")) { // Coins in first slot, sold everything
                                            SCScript.GE_DATA.removeSell(currentSellI);
                                            Logger.log("Completely sold (Coins in 1st slot), removing from sell");
                                        }
                                        else {
                                            SCScript.GE_DATA.reduceSellPrice(currentSellI);
                                            Logger.log("Didn't completely sell (item in 1st slot), reducing price");
                                        }
                                    }
                                }
                            }
                            else if ((ScriptData.currentWidgetChild = Widgets.get(465, 24, 3)) != null && ScriptData.currentWidgetChild.getActions() != null) { // Item in second/right slot (Coins)
                                if (ScriptData.currentWidgetChild.interact()) { // Verify interaction
                                    Sleep.sleepUntil(COLLECTED_ITEM, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300); // Verify interaction went through
                                    if (COLLECTED_ITEM.verify()) {
                                        Logger.log("Collected 2nd slot, reducing sell price");
                                        SCScript.GE_DATA.reduceSellPrice(currentSellI);
                                    }
                                }
                            }
                        }
                        else {
                            GrandExchange.openSlotInterface(currentSellI);
                            Logger.log("Attempting to openSlotInterface for currentSellI: " + currentSellI);
                        }
                    }
                    else {
                        currentSellI++;
                        Logger.log("Incremented currentSellI, now: " + currentSellI);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    break;
            }
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
