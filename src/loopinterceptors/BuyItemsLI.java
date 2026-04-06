package loopinterceptors;

import data.PersistedScriptInfo;
import framework.LoopInterceptor;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.items.Item;

import java.util.Arrays;

/**
 *
 */
public class BuyItemsLI extends LoopInterceptor {

    private int[] buyID = new int[10];
    private int[] buyQty = new int[10];
    private int[] buyPrice = new int[10];
    private byte buySize;

    private int[] missedID = new int[10];
    private int[] missedQty = new int[10];
    private int[] missedPrice = new int[10];
    private byte missedSize;

    private byte buyStage;
    private byte currentBuyI;

    private final Condition GE_CONTAINS_CURRENT_BUY_I = () -> GrandExchange.contains(buyID[currentBuyI]);
    private final Condition COLLECTED_ITEM = () -> !GrandExchange.isBuyOpen() || ScriptData.currentWidgetChild == null || ScriptData.currentWidgetChild.getActions() == null;

    public BuyItemsLI() {
        super(() -> true);
    }

    public void exportToPersistedScriptInfo(PersistedScriptInfo p) {
        p.buyStage = buyStage;
        p.currentBuyI = currentBuyI;
        p.buyID = buyID;
        p.buyQty = buyQty;
        p.buyPrice = buyPrice;
        p.buySize = buySize;
        p.missedID = missedID;
        p.missedQty = missedQty;
        p.missedPrice = missedPrice;
        p.missedSize = missedSize;
    }
    public void importFromPersistedScriptInfo(PersistedScriptInfo p) {
        buyStage = p.buyStage;
        currentBuyI = p.currentBuyI;
        buyID = p.buyID;
        buyQty = p.buyQty;
        buyPrice = p.buyPrice;
        buySize = p.buySize;
        missedID = p.missedID;
        missedQty = p.missedQty;
        missedPrice = p.missedPrice;
        missedSize = p.missedSize;
    }

    public void addBuyItem(int id, int qty) {
        buyID[buySize] = id;
        buyQty[buySize] = qty;

        int livePrice = LivePrices.get(id);
        if (livePrice == 0) {
            buyPrice[buySize] = 1;
        }
        else {
            int min = livePrice - (int) Math.max(1, Math.floor(ScriptData.SECURE_RANDOM.nextInt(21) / 100.0 * livePrice));
            int max = livePrice + (int) Math.ceil(ScriptData.SECURE_RANDOM.nextInt(21) / 100.0 * livePrice);
            buyPrice[buySize] = ScriptData.SECURE_RANDOM.nextInt(max - min + 1) + min;
        }
        buySize++;
    }
    public void addBuyItem(int id, int qty, int prc) {
        buyID[buySize] = id;
        buyQty[buySize] = qty;
        buyPrice[buySize++] = prc;
    }

    public void reset() {
        buySize = 0;
        buyStage = 0;
    }

    private void transferMissedToBuy() {
        for (byte i = 0; i < missedSize; i++) { // Add back items that failed to buy/couldn't because lack of gp
            addBuyItem(missedID[i], missedQty[i], missedPrice[i]);
        }
        missedSize = 0;
    }

    private void addMissed(byte buyIdx) {
        missedID[missedSize] = buyID[buyIdx];
        missedQty[missedSize] = buyQty[buyIdx];
        missedPrice[missedSize++] = buyPrice[buyIdx];
    }

    private void shuffleBuy() {
        for (int i = buySize - 1; i > 0; i--) {
            int j = ScriptData.SECURE_RANDOM.nextInt(i + 1);
            int tempID = buyID[i];
            int tempQty = buyQty[i];
            int tempPrice = buyPrice[i];
            buyID[i] = buyID[j];
            buyQty[i] = buyQty[j];
            buyPrice[i] = buyPrice[j];
            buyID[j] = tempID;
            buyQty[j] = tempQty;
            buyPrice[j] = tempPrice;
        }
    }

    private void removeBuy(byte index) {
        int last = buySize - 1;
        if (index != last) {
            buyID[index] = buyID[last];
            buyQty[index] = buyQty[last];
            buyPrice[index] = buyPrice[last];
        }
        buyID[last] = 0;
        buyQty[last] = 0;
        buyPrice[last] = 0;
        buySize--;
    }

    private int getBuyTotalPrice(byte index) {
        return buyPrice[index] * buyQty[index];
    }

    public int getBuyTotalPrice() {
        int totalPrice = 0;
        for (byte i = 0; i < buySize; i++) {
            totalPrice += getBuyTotalPrice(i);
        }
        return totalPrice;
    }

    private void reduceBuyQuantity(byte index, int qty) {
        buyQty[index] -= qty;
    }

    private void increaseBuyPrice(byte index) {
        int price = buyPrice[index];
        double percent = (ScriptData.SECURE_RANDOM.nextInt(20 - 3 + 1) + 3) / 100.0 + 1;
        price = (int) Math.ceil(price * percent);
        buyPrice[index] = price;
    }

    public byte getBuySize() {
        return buySize;
    }

    @Override
    public int handle() {
        if (buyStage > 0 && buySize > 0 && !GrandExchange.isOpen()) { // Guard in case log out, must open GrandExchange again
            if (ScriptData.GRAND_EXCHANGE.contains(Players.getLocal())) {
                GrandExchange.open();
                Sleep.sleepUntil(ScriptData.GRAND_EXCHANGE_IS_OPEN, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            }
            else if (!ScriptData.walkToArea(ScriptData.GRAND_EXCHANGE)) {
                return ScriptData.returnMSFast();
            }
        }
        else {
            switch (buyStage) {
                case 0:
                    if (Inventory.onlyContains(995) && !Bank.contains(995)) {
                        Logger.log("Buy information:");
                        Logger.log("buyID: " + Arrays.toString(buyID));
                        Logger.log("BuyPrice: " + Arrays.toString(buyPrice));
                        Logger.log("BuyQty: " + Arrays.toString(buyQty));
                        Logger.log("BuySize: " + buySize);
                        buyStage = 1;
                        shuffleBuy();
                        return ScriptData.returnMSFast();
                    }
                    else if (ScriptData.progressionTaskTimer != null && !ScriptData.progressionTaskTimer.isPaused()) {
                        ScriptData.progressionTaskTimer.pause();
                        ScriptData.unPauseTimer = 1;
                    }
                    else {
                        if (!Equipment.isEmpty()) {
                            ScriptData.depositAllEqpLI.setDepositAllEqp(true);
                        }
                        if (!Inventory.isEmpty()) {
                            if (Inventory.contains(995)) {
                                for (Item item : Inventory.toArray()) {
                                    if (item != null) {
                                        int id = item.getId();
                                        if (id != 995 && ScriptData.depositLI.depositNotContainsID(id)) {
                                            ScriptData.depositLI.addItemToDeposit(id, Inventory.count(id));
                                        }
                                    }
                                }
                            }
                            else {
                                ScriptData.depositAllInvLI.setDepositAllInv(true);
                            }
                        }
                        if (Bank.contains(995)) {
                            ScriptData.withdrawLI.addItemToWithdraw(995, Bank.count(995) + Inventory.count(995));
                            Logger.log("Added Coins to items to withdraw, withdrawSize: " + ScriptData.withdrawLI.getWithdrawSize());
                        }
                        if (ScriptData.withdrawLI.getWithdrawSize() > 0 || ScriptData.depositLI.getDepositSize() > 0 || ScriptData.depositAllEqpLI.shouldHandle() || ScriptData.depositAllInvLI.shouldHandle()) {
                            // Set pipeline to bank pipeline, return pipeline to pipeline using currentPipelineI after
                            ScriptData.bankingPipeline.setBankingReturnToI((byte) 35);
                            ScriptData.currentPipelineI = 34;
                        }
                    }
                    return ScriptData.returnMSFast();
                case 1: // Place offers
                    if (buySize == 0) { // No buy offers left
                        if (GrandExchange.isOpen()) {
                            GrandExchange.close();
                            Sleep.sleepUntil(ScriptData.GRAND_EXCHANGE_CLOSED, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            return ScriptData.returnMSFast();
                        }
                        else { // Finished
                            Logger.log("Finished toBuy");
                            transferMissedToBuy();
                            if (ScriptData.unPauseTimer == 1) {
                                ScriptData.unPauseTimer = 0;
                                ScriptData.progressionTaskTimer.resume();
                            }
                            buySize = 0;
                            buyStage = 0;
                            currentBuyI = 0;
                            ScriptData.currentPipelineI = ScriptData.currentProgressionTaskI;
                        }
                    }
                    else if (GrandExchange.getUsedSlots() == Math.min(3, buySize)) {
                        Sleep.sleepUntil(ScriptData.GRAND_EXCHANGE_READY_TO_COLLECT, ScriptData.SECURE_RANDOM.nextInt(120000 - 3000 + 1) + 30000, 500); // 3s - 2m
                        Logger.log("Done placing offers, going back to buyStage: 2, currentBuyI: 0");
                        buyStage = 2;
                        currentBuyI = 0;
                        return ScriptData.returnMSFast();
                    }
                    else if (Inventory.count(995) < getBuyTotalPrice(currentBuyI)) {
                        addMissed(currentBuyI);
                        removeBuy(currentBuyI);
                        return ScriptData.returnMSFast();
                    }
                    else if (GrandExchange.buyItem(buyID[currentBuyI], buyQty[currentBuyI], buyPrice[currentBuyI])) {
                        Logger.log("Placed buy offer for " + buyID[currentBuyI]);
                        Sleep.sleepUntil(GE_CONTAINS_CURRENT_BUY_I, ScriptData.SECURE_RANDOM.nextInt(20000 - 10000 + 1) + 10000, 300);
                        if (GE_CONTAINS_CURRENT_BUY_I.verify()) {
                            currentBuyI++;
                            Logger.log("Incrementing currentBuyI, next item: " + buyID[currentBuyI]);
                            return ScriptData.returnMSFast();
                        }
                    }
                    break;
                case 2: // Cancel and collect
                    if (GrandExchange.getUsedSlots() == 0) {
                        buyStage = 1; // Place more offers
                        currentBuyI = 0;
                        shuffleBuy();
                        Logger.log("usedSlots is 0, buyStage set to 1");
                        return ScriptData.returnMSFast();
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
                                    Sleep.sleepUntil(COLLECTED_ITEM, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                                    if (COLLECTED_ITEM.verify()) { // Verify interaction went through
                                        if (name.contains("Coins")) { // Didn't completely buy
                                            increaseBuyPrice(currentBuyI);
                                            Logger.log("Didn't completely buy, collected Coins, and increased price");
                                        }
                                        else {
                                            reduceBuyQuantity(currentBuyI, stackSize);
                                            if (buyQty[currentBuyI] <= 0) { // Completely bought
                                                removeBuy(currentBuyI);
                                                Logger.log("Completely bought, buySize now: " + getBuySize());
                                            }
                                            else {
                                                Logger.log("Didn't completely buy");
                                                increaseBuyPrice(currentBuyI);
                                            }
                                        }
                                    }
                                }
                            }
                            else if ((ScriptData.currentWidgetChild = Widgets.get(465, 24, 3)) != null && ScriptData.currentWidgetChild.getActions() != null) { // Item in second/right slot (Coins)
                                if (ScriptData.currentWidgetChild.interact()) { // Verify interaction
                                    Sleep.sleepUntil(COLLECTED_ITEM, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300); // Verify interaction went through
                                }
                                Logger.log("Collected second slot");
                            }
                        }
                        else {
                            GrandExchange.openSlotInterface(currentBuyI);
                        }
                    }
                    else {
                        currentBuyI++;
                        return ScriptData.returnMSFast();
                    }
                    break;
            }
        }

        return ScriptData.returnMSNormal();
    }

}
