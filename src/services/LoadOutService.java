package services;

import framework.SCScript;
import framework.ScriptState;
import global.PlayerData;
import global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.wrappers.items.Item;

/**
 * - After initialSetUp in EnsureLoadOut (buy items > withdraw items > (equip) item(s):
 * - Depending on the task, checks will either have to be for:
 *      - reFill (reFill needed items)
 *      - unLoad (Deposit unNeeded items)
 */
public class LoadOutService {

    private final int[] INV_ID;
    private final int[] INV_QTY_MIN; // Threshold for reFill, 0 = noRefill
    private final int[] INV_QTY_MAX; // Max allowed in Inventory
    private final int[] INV_QTY_INIT; // Initial quantity needed upon selection
    private byte invSize;

    private final int[] EQP_ID;
    private final int[] EQP_QTY_MIN;
    private final int[] EQP_QTY_INIT;
    private byte eqpSize;

    private final boolean depositsOnInvFull; // Deposits all inventory except what is in invLoadOut

    public LoadOutService(int invSize, int eqpSize, boolean depositsOnInvFull) {
        INV_ID = new int[invSize];
        INV_QTY_MIN = new int[invSize];
        INV_QTY_MAX = new int[invSize];
        INV_QTY_INIT = new int[invSize];

        EQP_ID = new int[eqpSize];
        EQP_QTY_MIN = new int[eqpSize];
        EQP_QTY_INIT = new int[eqpSize];

        this.depositsOnInvFull = depositsOnInvFull;
    }

    public void addInventoryItem(int id, int min, int max, int init) {
        INV_ID[invSize] = id;
        INV_QTY_MIN[invSize] = min;
        INV_QTY_MAX[invSize] = max;
        INV_QTY_INIT[invSize++] = init;
    }
    public void setInventoryItem(int index, int min, int max, int init) {
        INV_QTY_MIN[index] = min;
        INV_QTY_MAX[index] = max;
        INV_QTY_INIT[index] = init;
    }
    public void addEquipmentItem(int id, int min, int init) {
        EQP_ID[eqpSize] = id;
        EQP_QTY_MIN[eqpSize] = min;
        EQP_QTY_INIT[eqpSize++] = init;
    }

    public byte getEqpSize() {
        return eqpSize;
    }
    public byte getInvSize() {
        return invSize;
    }

    public int getInvItemID(byte index) {
        return INV_ID[index];
    }
    public int getInvItemQtyInit(byte index) {
        return INV_QTY_INIT[index];
    }
    public int getInvItemQtyMax(byte index) {
        return INV_QTY_MAX[index];
    }

    public int getEqpItemID(byte index) {
        return EQP_ID[index];
    }

    public int getEqpItemQtyInit(byte index) {
        return EQP_QTY_INIT[index];
    }

    public boolean setUpNotValid() { // Handles routing to Banking, Grand Exchange
        for (byte i = 0; i < eqpSize; i++) {
            if (Equipment.count(EQP_ID[i]) < EQP_QTY_MIN[i]) {
                if (Inventory.contains(EQP_ID[i])) {
                    Inventory.interact(EQP_ID[i]);
                }
                else if (Bank.contains(EQP_ID[i])) {
                    SCScript.BANKING_SERVICE.addItemToWithdraw(EQP_ID[i], Bank.count(EQP_ID[i]));
                }
                else {
                    SCScript.GRAND_EXCHANGE_SERVICE.addBuyItem(EQP_ID[i], EQP_QTY_INIT[i]);
                }
            }
        }
        for (byte i = 0; i < invSize; i++) {
            if (Inventory.count(INV_ID[i]) < INV_QTY_MIN[i]) {
                int invCount = Inventory.count(INV_ID[i]);
                if (invCount < INV_QTY_MIN[i]) {
                    if (Bank.count(INV_ID[i]) >= INV_QTY_MIN[i] - invCount) {
                        SCScript.BANKING_SERVICE.addItemToWithdraw(INV_ID[i], Math.min(INV_QTY_MAX[i] - invCount, Bank.count(INV_ID[i])));
                    }
                    else {
                        SCScript.GRAND_EXCHANGE_SERVICE.addBuyItem(INV_ID[i], INV_QTY_INIT[i]);
                    }
                }
            }
        }
        if (depositsOnInvFull && Inventory.isFull()) {
            ScriptData.depositInvItemsNotInLoadOut();
        }
        if (SCScript.GRAND_EXCHANGE_SERVICE.getBuySize() != 0) {
            SCScript.scriptState = ScriptState.BUY_ITEMS;
            if (SCScript.BANKING_SERVICE.getWithdrawSize() != 0) { // Reset withdraw
                SCScript.BANKING_SERVICE.resetWithdraw();
            }
            return true;
        }
        if (SCScript.BANKING_SERVICE.getWithdrawSize() != 0) {
            SCScript.scriptState = ScriptState.BANKING;
            return true;
        }
        return false;
    }

}
