package loopinterceptors;

import data.PersistedScriptInfo;
import framework.LoopInterceptor;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;

/**
 *
 */
public class SellItemsLI extends LoopInterceptor {

    private int[] sellID = new int[10];
    private int[] sellQty = new int[10];
    private int[] sellPrice = new int[10];
    private byte sellSize;

    private byte sellStage;
    private byte currentSellI;
    private final Condition GE_CONTAINS_CURRENT_SELL_I = () -> GrandExchange.contains(sellID[currentSellI]);
    private final Condition COLLECTED_ITEM = () -> !GrandExchange.isSellOpen() || ScriptData.currentWidgetChild == null || ScriptData.currentWidgetChild.getActions() == null;

    public SellItemsLI() {
        super(null);
        setShouldHandle(() -> true);
    }

    public void reset() {
        sellSize = 0;
        sellStage = 0;
    }

    public void addSell(int id, int price, int quantity) {
        sellID[sellSize] = id;
        sellQty[sellSize] = quantity;
        sellPrice[sellSize++] = price;
    }

    public byte getSellSize() {
        return sellSize;
    }

    public void exportToPersistedScriptInfo(PersistedScriptInfo p) {
        p.sellStage = sellStage;
        p.currentSellI = currentSellI;
        p.sellID = this.sellID;
        p.sellQty = this.sellQty;
        p.sellPrice = this.sellPrice;
        p.sellSize = this.sellSize;
    }
    public void importFromPersistedScriptInfo(PersistedScriptInfo p) {
        sellStage = p.sellStage;
        currentSellI = p.currentSellI;
        sellID = p.sellID;
        sellQty = p.sellQty;
        sellPrice = p.sellPrice;
        this.sellSize = p.sellSize;
    }

    public int getSellQty(int index) {
        return sellQty[index];
    }

    public void shuffleSell() {
        for (int i = sellSize - 1; i > 0; i--) {
            int j = ScriptData.SECURE_RANDOM.nextInt(i + 1);
            int tempID = sellID[i];
            int tempPrice = sellPrice[i];
            int tempQty = sellQty[i];
            sellID[i] = sellID[j];
            sellPrice[i] = sellPrice[j];
            sellQty[i] = sellQty[j];
            sellID[j] = tempID;
            sellPrice[j] = tempPrice;
            sellQty[j] = tempQty;
        }
    }

    private void removeSell(byte index) {
        int last = sellSize - 1;
        if (index != last) {
            sellID[index] = sellID[last];
            sellPrice[index] = sellPrice[last];
            sellQty[index] = sellQty[last];
        }
        sellID[last] = 0;
        sellPrice[last] = 0;
        sellQty[last] = 0;
        sellSize--;
    }

    private void reduceSellPrice(byte index) {
        int price = sellPrice[index];
        price = (int) (price * 0.95);
        sellPrice[index] = price;
    }

    @Override
    public boolean shouldHandle() {
        return false;
    }

    @Override
    public int handle() {
        if (sellStage > 0 && !GrandExchange.isOpen()) { // Guard in case log out, must open GrandExchange again
            if (ScriptData.GRAND_EXCHANGE.contains(Players.getLocal())) {
                GrandExchange.open(); // Only returns true if GE is open
                Sleep.sleepUntil(ScriptData.GRAND_EXCHANGE_IS_OPEN, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            }
            else {
                ScriptData.walkToArea(ScriptData.GRAND_EXCHANGE);
            }
        }
        else {
            switch(sellStage) { // handleInv
                case 0:
                    ScriptData.bankingPipeline.setReturnToI((byte) 36);
                    ScriptData.currentPipelineI = 34;
                    ScriptData.bankWithdrawModeLI.setBankMode(BankMode.NOTE);
                    sellStage = 1;
                    if (!Inventory.isEmpty()) {
                        ScriptData.depositAllInvLI.setDepositAllInv(true);
                    }
                    for (byte i = 0; i < sellSize; i++) {
                        ScriptData.withdrawLI.addItemToWithdraw(sellID[i], sellQty[i]);
                    }
                    shuffleSell();
                    break;
                case 1: // Place offers
                    if (sellSize == 0) { // No sell offers left
                        if (GrandExchange.isOpen()) {
                            GrandExchange.close();
                            Sleep.sleepUntil(ScriptData.GRAND_EXCHANGE_CLOSED, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            if (!GrandExchange.isOpen()) {
                                sellStage = 0;
                                currentSellI = 0;
                                ScriptData.bankWithdrawModeLI.setBankMode(BankMode.ITEM);
                                ScriptData.currentPipelineI = ScriptData.currentProgressionTaskI;
                                return ScriptData.returnMSFast();
                            }
                        }
                    }
                    else if (GrandExchange.getUsedSlots() == Math.min(3, sellSize)) {
                        Sleep.sleepUntil(ScriptData.GRAND_EXCHANGE_READY_TO_COLLECT, ScriptData.SECURE_RANDOM.nextInt(120000 - 3000 + 1) + 30000, 500); // 3s - 2m
                        sellStage = 2;
                        currentSellI = 0;
                        return ScriptData.returnMSFast();
                    }
                    else if (!Inventory.contains(sellID[currentSellI])) {
                        removeSell(currentSellI);
                        return ScriptData.returnMSFast();
                    }
                    else if (GrandExchange.sellItem(sellID[currentSellI], sellQty[currentSellI], sellPrice[currentSellI])) {
                        Sleep.sleepUntil(GE_CONTAINS_CURRENT_SELL_I, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        if (GrandExchange.contains(sellID[currentSellI])) {
                            currentSellI++;
                            return ScriptData.returnMSFast();
                        }
                    }
                    break;
                case 2: // Cancel and collect
                    if (GrandExchange.getUsedSlots() == 0) { // Need to place more offers
                        sellStage = 1; // Place more offers
                        currentSellI = 0;
                        shuffleSell();
                        return ScriptData.returnMSFast();
                    }
                    else if (GrandExchange.slotContainsItem(currentSellI)) {
                        if (!GrandExchange.isReadyToCollect(currentSellI)) {
                            GrandExchange.cancelOffer(currentSellI);
                        }
                        else if (GrandExchange.isSellOpen()) {
                            if ((ScriptData.currentWidgetChild = Widgets.get(465, 24, 2)) != null && ScriptData.currentWidgetChild.getActions() != null) { // Item in first/left slot (Coins if aborted & nothing bought)
                                String name = ScriptData.currentWidgetChild.getName();
                                if (ScriptData.currentWidgetChild.interact()) { // Verify interaction for collecting either the currentItem or Coins
                                    Sleep.sleepUntil(COLLECTED_ITEM, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                                    if (COLLECTED_ITEM.verify()) { // Verify interaction went through
                                        if (name.contains("Coins")) { // Coins in first slot, sold everything
                                            removeSell(currentSellI);
                                        }
                                        else {
                                            reduceSellPrice(currentSellI);
                                        }
                                    }
                                }
                            }
                            else if ((ScriptData.currentWidgetChild = Widgets.get(465, 24, 3)) != null && ScriptData.currentWidgetChild.getActions() != null) { // Item in second/right slot (Coins)
                                if (ScriptData.currentWidgetChild.interact()) { // Verify interaction
                                    Sleep.sleepUntil(COLLECTED_ITEM, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300); // Verify interaction went through
                                    if (COLLECTED_ITEM.verify()) {
                                        reduceSellPrice(currentSellI);
                                    }
                                }
                            }
                        }
                        else {
                            GrandExchange.openSlotInterface(currentSellI);
                        }
                    }
                    else {
                        currentSellI++;
                        return ScriptData.returnMSFast();
                    }
                    break;
            }
        }

        return ScriptData.returnMSNormal();
    }

}
