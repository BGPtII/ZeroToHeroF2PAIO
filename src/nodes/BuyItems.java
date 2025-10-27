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
 * - First, shuffle toBuy
 * - Second, withdraw Coins
 * - Third, attempt to buy items
 *      - If player can't buy an item, remove it from toBuy
 */
public class BuyItems implements Node {

    private byte buyStage;
    private byte currentBuyI;
    private final Condition GE_CONTAINS_CURRENT_BUY_I = () -> GrandExchange.contains(SCScript.GRAND_EXCHANGE_SERVICE.getBuyID(currentBuyI));


    @Override
    public int loop() {
        if (buyStage > 0 && !GrandExchange.isOpen()) { // Guard in case log out, must open GrandExchange again
            if (ScriptData.GRAND_EXCHANGE.contains(Players.getLocal()) || ScriptData.GRAND_EXCHANGE.contains(Walking.getDestination())) {
                GrandExchange.open();
                Sleep.sleepUntil(PlayerData.GRAND_EXCHANGE_IS_OPEN, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            }
            else {
                PlayerData.walkToArea(ScriptData.GRAND_EXCHANGE);
            }
        }
        else {
            switch (buyStage) {
                case 0:
                    if (Bank.contains("Coins")) {
                        SCScript.BANKING_SERVICE.addItemToWithdraw(995, Bank.count("Coins"));
                        SCScript.BANKING_SERVICE.startBanking();
                    }
                    buyStage++;
                    break;
                case 1: // Place offers
                    if (SCScript.GRAND_EXCHANGE_SERVICE.getBuySize() == 0) { // No buy offers left
                        if (GrandExchange.isOpen()) {
                            GrandExchange.close();
                            Sleep.sleepUntil(PlayerData.GRAND_EXCHANGE_CLOSED, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            if (!GrandExchange.isOpen()) {
                                buyStage = 0;
                                currentBuyI = 0;
                                SCScript.scriptState = PlayerData.stateToReturnTo;
                            }
                        }
                    }
                    else if (GrandExchange.getUsedSlots() == Math.min(3, SCScript.GRAND_EXCHANGE_SERVICE.getBuySize())) {
                        Sleep.sleepUntil(PlayerData.GRAND_EXCHANGE_READY_TO_COLLECT, SCScript.SECURE_RANDOM.nextInt(120000 - 30000 + 1) + 30000, 500);
                        buyStage++;
                        currentBuyI = 0;
                    }
                    else if (Inventory.count("Coins") < SCScript.GRAND_EXCHANGE_SERVICE.getBuyTotalPrice(currentBuyI)) {
                        SCScript.GRAND_EXCHANGE_SERVICE.removeBuy(currentBuyI);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
                    }
                    else if (GrandExchange.buyItem(SCScript.GRAND_EXCHANGE_SERVICE.getBuyID(currentBuyI), SCScript.GRAND_EXCHANGE_SERVICE.getBuyQty(currentBuyI), SCScript.GRAND_EXCHANGE_SERVICE.getBuyPrice(currentBuyI))) {
                        Sleep.sleepUntil(GE_CONTAINS_CURRENT_BUY_I, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        if (GrandExchange.contains(SCScript.GRAND_EXCHANGE_SERVICE.getBuyID(currentBuyI))) {
                            currentBuyI++;
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                    }
                    break;
                case 2: // Cancel and collect
                    if (GrandExchange.getUsedSlots() == 0) {
                        buyStage = 2; // Place more offers
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (GrandExchange.slotContainsItem(currentBuyI)) {
                        if (GrandExchange.isReadyToCollect(currentBuyI)) {
                            GrandExchange.cancelOffer(currentBuyI);
                        }
                        else if (GrandExchange.isBuyOpen()) {
                            if ((PlayerData.currentWidgetChild = Widgets.get(465, 24, 2)) != null && PlayerData.currentWidgetChild.getActions() != null) { // Item in first/left slot (Coins if aborted & nothing bought)
                                int stackSize = PlayerData.currentWidgetChild.getItemStack();
                                String name = PlayerData.currentWidgetChild.getName();
                                if (PlayerData.currentWidgetChild.interact()) { // Verify interaction for collecting either the currentItem or Coins
                                    Sleep.sleepUntil(PlayerData.COLLECTED_ITEM, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                                    if (!GrandExchange.isBuyOpen() || PlayerData.currentWidgetChild == null || PlayerData.currentWidgetChild.getActions() == null) { // Verify interaction went through
                                        if (name.contains("Coins")) { // Didn't completely buy
                                            SCScript.GRAND_EXCHANGE_SERVICE.increaseBuyPrice(currentBuyI);
                                        }
                                        else {
                                            SCScript.GRAND_EXCHANGE_SERVICE.reduceBuyQuantity(currentBuyI, stackSize);
                                            if (SCScript.GRAND_EXCHANGE_SERVICE.getBuyQty(currentBuyI) == 0) { // Completely bought
                                                SCScript.GRAND_EXCHANGE_SERVICE.removeBuy(currentBuyI);
                                            }
                                            else {
                                                SCScript.GRAND_EXCHANGE_SERVICE.increaseBuyPrice(currentBuyI);
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
                            GrandExchange.openSlotInterface(currentBuyI);
                        }
                    }
                    else {
                        currentBuyI++;
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
                    }
                    break;
            }
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
