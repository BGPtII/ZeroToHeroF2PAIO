package services;

import framework.SCScript;
import org.dreambot.api.methods.grandexchange.LivePrices;

/**
 * - Noted items IDs are unNoted + 1
 */
public class GrandExchangeService {

    private final int[] BUY_ID;
    private final int[] BUY_QTY;
    private final int[] BUY_PRICE;
    private byte buySize;
    private byte currentBuyI;

    private final String[] SELL_NAME; // Name preferred, noted items have separate IDs
    private final int[] SELL_QTY_TO_KEEP;
    private final int[] SELL_PRICE;
    private byte sellSize;

    private final String[] SELLABLE_ITEMS = new String[] {
        "Logs", "Oak logs", "Willow logs",
        "Tin ore", "Copper ore", "Iron ore"
    };
    public void addSellItem(String name, int qtyToKeep) {
        SELL_NAME[sellSize] = name;
        SELL_QTY_TO_KEEP[sellSize] = qtyToKeep;
        SELL_PRICE[sellSize++] = LivePrices.get(name);
    }
    public void attemptAddSellableItems() {
        for (String item : SELLABLE_ITEMS) {
            addSellItem(item, 0);
        }
    }

    public GrandExchangeService() {
        BUY_ID = new int[10];
        BUY_QTY = new int[10];
        BUY_PRICE = new int[10];

        SELL_NAME = new String[20];
        SELL_QTY_TO_KEEP = new int[20];
        SELL_PRICE = new int[20];
    }

    public void addBuyItem(int id, int qty) {
        BUY_ID[buySize] = id;
        BUY_QTY[buySize] = qty;
        BUY_PRICE[buySize++] = LivePrices.get(id);
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
        for (int i = buySize - 1; i > 0; i--) { // Fisher-Yates shuffle
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
        for (int i = sellSize - 1; i > 0; i--) { // Fisher-Yates shuffle
            int j = SCScript.SECURE_RANDOM.nextInt(i + 1);
            String tempNme = SELL_NAME[i];
            int tempQtyToKeep = SELL_QTY_TO_KEEP[i];
            int tempPrice = SELL_PRICE[i];
            SELL_NAME[i] = SELL_NAME[j];
            SELL_QTY_TO_KEEP[i] = SELL_QTY_TO_KEEP[j];
            SELL_PRICE[i] = SELL_PRICE[j];
            SELL_NAME[j] = tempNme;
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
        price = (int) (price * 1.05);
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

    public int getSellName(byte index) {
        return SELL_PRICE[index];
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
            SELL_NAME[index] = SELL_NAME[last];
            SELL_QTY_TO_KEEP[index] = SELL_QTY_TO_KEEP[last];
            SELL_PRICE[index] = SELL_PRICE[last];
        }
        SELL_NAME[last] = null;
        SELL_QTY_TO_KEEP[last] = 0;
        SELL_PRICE[last] = 0;
        buySize--;
    }

}
