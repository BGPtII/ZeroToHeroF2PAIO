package nodes;

import framework.Node;
import framework.SCScript;
import global.PlayerData;
import global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;

/**
 *
 */
public class SellItems implements Node {

    private byte sellStage;
    private byte currentSellI;
    private final Condition GE_CONTAINS_CURRENT_SELL_I = () -> GrandExchange.contains(SCScript.GRAND_EXCHANGE_SERVICE.getSellName(currentSellI));

    @Override
    public int loop() {
        if (sellStage > 0 && !GrandExchange.isOpen()) { // Guard in case log out, must open GrandExchange again
            if (ScriptData.GRAND_EXCHANGE.contains(Players.getLocal()) || ScriptData.GRAND_EXCHANGE.contains(Walking.getDestination())) {
                GrandExchange.open();
                Sleep.sleepUntil(PlayerData.GRAND_EXCHANGE_IS_OPEN, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            }
            else {
                PlayerData.walkToArea(ScriptData.GRAND_EXCHANGE);
            }
        }
        else {
            switch(sellStage) { // Purge inventory, withdraw itemsToSell (Noted if possible)
                case 0:
                    sellStage++;
                    SCScript.GRAND_EXCHANGE_SERVICE.shuffleSell();
                    if (!Inventory.isEmpty()) {
                        SCScript.BANKING_SERVICE.toggleDpAllInvInit(true);
                    }
                    for (byte i = 0; i < SCScript.GRAND_EXCHANGE_SERVICE.getSellSize(); i++) {
                        SCScript.BANKING_SERVICE.addItemToWithdraw(SCScript.GRAND_EXCHANGE_SERVICE.getSellName(i), Bank.count(SCScript.GRAND_EXCHANGE_SERVICE.getSellName(i)) + Inventory.count(SCScript.GRAND_EXCHANGE_SERVICE.getSellName(i)) - SCScript.GRAND_EXCHANGE_SERVICE.getSellQuantityToKeep(i));

                    }
                    SCScript.BANKING_SERVICE.toggleWithdrawMode(true);
                    SCScript.BANKING_SERVICE.startBanking();
                    break;
                case 1: // Place offers
                    if (SCScript.GRAND_EXCHANGE_SERVICE.getSellSize() == 0) { // No sell offers left
                        if (GrandExchange.isOpen()) {
                            GrandExchange.close();
                            Sleep.sleepUntil(PlayerData.GRAND_EXCHANGE_CLOSED, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            if (!GrandExchange.isOpen()) {
                                sellStage = 0;
                                currentSellI = 0;
                                SCScript.scriptState = PlayerData.stateToReturnTo;
                            }
                        }
                    }
                    else if (GrandExchange.getUsedSlots() == Math.min(3, SCScript.GRAND_EXCHANGE_SERVICE.getBuySize())) {
                        Sleep.sleepUntil(PlayerData.GRAND_EXCHANGE_READY_TO_COLLECT, SCScript.SECURE_RANDOM.nextInt(120000 - 30000 + 1) + 30000, 500);
                        sellStage++;
                        currentSellI = 0;
                    }
                    else if (GrandExchange.sellItem(SCScript.GRAND_EXCHANGE_SERVICE.getSellName(currentSellI), SCScript.GRAND_EXCHANGE_SERVICE.getSellName(currentSellI), SCScript.GRAND_EXCHANGE_SERVICE.getSellPrice(currentSellI))) {
                        Sleep.sleepUntil(GE_CONTAINS_CURRENT_SELL_I, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        if (GrandExchange.contains(SCScript.GRAND_EXCHANGE_SERVICE.getSellName(currentSellI))) {
                            currentSellI++;
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                    }
                    break;
                case 2: // Cancel and collect
                    if (GrandExchange.getUsedSlots() == 0) {
                        sellStage = 2; // Place more offers
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (GrandExchange.slotContainsItem(currentSellI)) {
                        if (GrandExchange.isReadyToCollect(currentSellI)) {
                            GrandExchange.cancelOffer(currentSellI);
                        }
                        else if (GrandExchange.isSellOpen()) {
                            if ((PlayerData.currentWidgetChild = Widgets.get(465, 24, 2)) != null && PlayerData.currentWidgetChild.getActions() != null) { // Item in first/left slot (Coins if aborted & nothing bought)
                                String name = PlayerData.currentWidgetChild.getName();
                                if (PlayerData.currentWidgetChild.interact()) { // Verify interaction for collecting either the currentItem or Coins
                                    Sleep.sleepUntil(PlayerData.COLLECTED_ITEM, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                                    if (!GrandExchange.isSellOpen() || PlayerData.currentWidgetChild == null || PlayerData.currentWidgetChild.getActions() == null) { // Verify interaction went through
                                        if (name.contains("Coins")) { // Didn't completely buy
                                            SCScript.GRAND_EXCHANGE_SERVICE.reduceSellPrice(currentSellI);
                                        }
                                        else {
                                            if (Inventory.count(SCScript.GRAND_EXCHANGE_SERVICE.getSellName(currentSellI)) == 0) { // Completely sold
                                                SCScript.GRAND_EXCHANGE_SERVICE.removeSell(currentSellI);
                                            }
                                            else {
                                                SCScript.GRAND_EXCHANGE_SERVICE.increaseBuyPrice(currentSellI);
                                            }
                                        }
                                    }
                                }
                            }
                            else if ((PlayerData.currentWidgetChild = Widgets.get(465, 24, 3)) != null && PlayerData.currentWidgetChild.getActions() != null) { // Item in second/right slot (Coins)
                                if (PlayerData.currentWidgetChild.interact()) { // Verify interaction
                                    Sleep.sleepUntil(PlayerData.COLLECTED_ITEM, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300); // Verify interaction went through
                                }
                            }
                        }
                        else {
                            GrandExchange.openSlotInterface(currentSellI);
                        }
                    }
                    else {
                        currentSellI++;
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
                    }
                    break;
            }
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
