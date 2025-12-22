package data;

import framework.SCScript;
import data.global.PlayerData;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;

/**
 * - Noted items IDs are unNoted + 1
 */
public class GrandExchangeData {

    private final int[] BUY_ID;
    private final int[] BUY_QTY;
    private final int[] BUY_PRICE;
    private byte buySize;

    private final int[] SELL_ID; // Name preferred, noted items have separate IDs
    private final int[] SELL_QTY_TO_KEEP;
    private final int[] SELL_PRICE;
    private byte sellSize;

    private final int[] SELLABLE_ITEMS_NOT_RESTRICTED = new int[] { // Uses Normal/Un-noted IDs
        1511, // Logs
        1759, 2349, // Ball of wool, Bronze bar
    };
    private final int[] SELLABLE_ITEMS_RESTRICTED = new int[] {
        1521, 1519, // Oak logs, Willow logs
        438, 436, 440 // Tin ore, Copper ore, Iron ore
    };
    public void addSellItem(int id, int qtyToKeep) {
        SELL_ID[sellSize] = id;
        SELL_QTY_TO_KEEP[sellSize] = qtyToKeep;
        int livePrice = LivePrices.get(id);
        if (livePrice == 0) {
            SELL_PRICE[sellSize++] = 1;
        }
        else {
            int min = livePrice - (int) Math.max(1, Math.floor(SCScript.SECURE_RANDOM.nextInt(21) / 100.0 * livePrice));
            int max = livePrice + (int) Math.ceil(SCScript.SECURE_RANDOM.nextInt(21) / 100.0 * livePrice);
            SELL_PRICE[sellSize++] = SCScript.SECURE_RANDOM.nextInt(max - min + 1) + min;
        }
        Logger.log("Added " + id + " toSell, price: " + SELL_PRICE[sellSize - 1]);
    }
    public void attemptAddSellableItems() {
        if (Skills.getTotalLevel() >= 300 && Quests.getQuestPoints() >= 10) { // 100 total level actual threshold
            for (int sellableItem : SELLABLE_ITEMS_RESTRICTED) {
                if (PlayerData.playerHasItemID(sellableItem)) {
                    addSellItem(sellableItem, 0);
                }
            }
        }
        for (int sellableItem : SELLABLE_ITEMS_NOT_RESTRICTED) {
            if (PlayerData.playerHasItemID(sellableItem)) {
                addSellItem(sellableItem, 0);
            }
        }
    }

    public GrandExchangeData() {
        BUY_ID = new int[10];
        BUY_QTY = new int[10];
        BUY_PRICE = new int[10];

        SELL_ID = new int[20];
        SELL_QTY_TO_KEEP = new int[20];
        SELL_PRICE = new int[20];
    }

    public void addBuyItem(int id, int qty) {
        BUY_ID[buySize] = id;
        BUY_QTY[buySize] = qty;

        int livePrice = LivePrices.get(id);
        if (livePrice == 0) {
            BUY_PRICE[buySize] = 1;
        }
        else {
            int min = livePrice - (int) Math.max(1, Math.floor(SCScript.SECURE_RANDOM.nextInt(21) / 100.0 * livePrice));
            int max = livePrice + (int) Math.ceil(SCScript.SECURE_RANDOM.nextInt(21) / 100.0 * livePrice);
            BUY_PRICE[buySize] = SCScript.SECURE_RANDOM.nextInt(max - min + 1) + min;
        }
        buySize++;
    }
    public void addBuyItem(int id, int qty, int prc) {
        BUY_ID[buySize] = id;
        BUY_QTY[buySize] = qty;
        BUY_PRICE[buySize++] = prc;
    }

    public byte getBuySize() {
        return buySize;
    }

    public int getBuyTotalPrice(byte index) {
        return BUY_PRICE[index] * BUY_QTY[index];
    }

    public int getSellSize() {
        return sellSize;
    }

    public void shuffleBuy() {
        for (int i = buySize - 1; i > 0; i--) {
            int j = SCScript.SECURE_RANDOM.nextInt(i + 1);
            int tempID = BUY_ID[i];
            int tempQty = BUY_QTY[i];
            int tempPrice = BUY_PRICE[i];
            BUY_ID[i] = BUY_ID[j];
            BUY_QTY[i] = BUY_QTY[j];
            BUY_PRICE[i] = BUY_PRICE[j];
            BUY_ID[j] = tempID;
            BUY_QTY[j] = tempQty;
            BUY_PRICE[j] = tempPrice;
        }
    }

    public void shuffleSell() {
        for (int i = sellSize - 1; i > 0; i--) {
            int j = SCScript.SECURE_RANDOM.nextInt(i + 1);
            int tempID = SELL_ID[i];
            int tempQtyToKeep = SELL_QTY_TO_KEEP[i];
            int tempPrice = SELL_PRICE[i];
            SELL_ID[i] = SELL_ID[j];
            SELL_QTY_TO_KEEP[i] = SELL_QTY_TO_KEEP[j];
            SELL_PRICE[i] = SELL_PRICE[j];
            SELL_ID[j] = tempID;
            SELL_QTY_TO_KEEP[j] = tempQtyToKeep;
            SELL_PRICE[j] = tempPrice;
        }
    }

    public int getBuyID(byte index) {
        return BUY_ID[index];
    }

    public int getBuyQty(byte index) {
        return BUY_QTY[index];
    }

    public int getBuyPrice(byte index) {
        return BUY_PRICE[index];
    }

    public void increaseBuyPrice(byte index) {
        int price = BUY_PRICE[index];
        double percent = (SCScript.SECURE_RANDOM.nextInt(20 - 3 + 1) + 3) / 100.0 + 1;
        Logger.log("Increase buyPrice by " + percent + "%");
        price = (int) Math.ceil(price * percent);
        BUY_PRICE[index] = price;
    }

    public void reduceBuyQuantity(byte index, int qty) {
        BUY_QTY[index] -= qty;
    }

    public void removeBuy(byte index) {
        int last = buySize - 1;
        if (index != last) {
            BUY_ID[index] = BUY_ID[last];
            BUY_QTY[index] = BUY_QTY[last];
            BUY_PRICE[index] = BUY_PRICE[last];
        }
        BUY_ID[last] = 0;
        BUY_QTY[last] = 0;
        BUY_PRICE[last] = 0;
        buySize--;
    }

    public int getSellID(byte index) {
        return SELL_ID[index];
    }

    public int getSellQuantityToKeep(byte index) {
        return SELL_QTY_TO_KEEP[index];
    }

    public int getSellPrice(byte index) {
        return SELL_PRICE[index];
    }

    public void reduceSellPrice(byte index) {
        int price = SELL_PRICE[index];
        price = (int) (price * 0.95);
        SELL_PRICE[index] = price;
    }

    public void removeSell(byte index) {
        int last = sellSize - 1;
        if (index != last) {
            SELL_ID[index] = SELL_ID[last];
            SELL_QTY_TO_KEEP[index] = SELL_QTY_TO_KEEP[last];
            SELL_PRICE[index] = SELL_PRICE[last];
        }
        SELL_ID[last] = 0;
        SELL_QTY_TO_KEEP[last] = 0;
        SELL_PRICE[last] = 0;
        sellSize--;
    }

}
