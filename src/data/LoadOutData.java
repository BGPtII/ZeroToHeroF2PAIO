package data;

import framework.SCScript;
import framework.ScriptState;
import data.global.ScriptData;
import nodes.InitializeTask;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.items.Item;

/**
 *
 */
public class LoadOutData {

    private final int[] INV_ID;
    private final int[] INV_QTY_MIN; // Threshold for reFill, 0 = noRefill
    private final int[] INV_QTY_MAX; // Max allowed in Inventory
    private final int[] INV_QTY_INIT; // Initial quantity needed upon selection
    private byte invSize;

    private final int[] EQP_ID;
    private final int[] EQP_QTY_MIN;
    private final int[] EQP_QTY_INIT;
    private byte eqpSize;

    private Condition shouldBank;

    public LoadOutData(int invSize, int eqpSize, Condition shouldBank) {
        INV_ID = new int[invSize];
        INV_QTY_MIN = new int[invSize];
        INV_QTY_MAX = new int[invSize];
        INV_QTY_INIT = new int[invSize];

        EQP_ID = new int[eqpSize];
        EQP_QTY_MIN = new int[eqpSize];
        EQP_QTY_INIT = new int[eqpSize];

        this.shouldBank = shouldBank;
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
    public void setInventoryItem(int index, int id, int min, int max, int init) {
        INV_ID[index] = id;
        INV_QTY_MIN[index] = min;
        INV_QTY_MAX[index] = max;
        INV_QTY_INIT[index] = init;
    }
    public void setInventoryItemMax(int index, int max) {
        INV_QTY_MAX[index] = max;
    }
    public int getInventoryItemMax(int index) {
        return INV_QTY_MAX[index];
    }
    public void setInventoryItemMin(int index, int min) {
        INV_QTY_MIN[index] = min;
    }
    public void addEquipmentItem(int id, int min, int init) {
        EQP_ID[eqpSize] = id;
        EQP_QTY_MIN[eqpSize] = min;
        EQP_QTY_INIT[eqpSize++] = init;
    }
    public void setEquipmentItem(int index, int min, int init) {
        EQP_QTY_MIN[index] = min;
        EQP_QTY_INIT[index] = init;
    }
    public void setEquipmentItem(int index, int id, int min, int init) {
        EQP_ID[index] = id;
        EQP_QTY_MIN[index] = min;
        EQP_QTY_INIT[index] = init;
    }

    public byte getEqpSize() {
        return eqpSize;
    }
    public byte getInvSize() {
        return invSize;
    }

    public int getInvItemID(int index) {
        return INV_ID[index];
    }
    public int getInvItemQtyInit(byte index) {
        return INV_QTY_INIT[index];
    }
    public int getInvItemQtyMin(int index)  {
        return INV_QTY_MIN[index];
    }
    public int getInvItemQtyMax(int index) {
        return INV_QTY_MAX[index];
    }

    public int getEqpItemID(int index) {
        return EQP_ID[index];
    }

    public int getEqpItemQtyInit(int index) {
        return EQP_QTY_INIT[index];
    }
    public int getEqpItemQtyMin(int index) {
        return EQP_QTY_MIN[index];
    }

    public boolean shouldBank() { // Handles routing to Banking, Grand Exchange
        if (shouldBank == null || !shouldBank.verify()) {
            return false;
        }
        for (byte i = 0; i < eqpSize; i++) {
            if (Equipment.count(EQP_ID[i]) < EQP_QTY_MIN[i]) { // reStock eqp
                SCScript.GE_DATA.addBuyItem(EQP_ID[i], EQP_QTY_INIT[i]);
            }
        }
        for (byte i = 0; i < invSize; i++) { // reStock inv
            if (Inventory.count(INV_ID[i]) < INV_QTY_MIN[i]) {
                if (Bank.count(INV_ID[i]) >= INV_QTY_MIN[i] - Inventory.count(INV_ID[i])) {
                    SCScript.BANKING_DATA.addItemToWithdraw(INV_ID[i], Math.min(INV_QTY_MAX[i] - Inventory.count(INV_ID[i]), Bank.count(INV_ID[i])));
                }
                else {
                    SCScript.GE_DATA.addBuyItem(INV_ID[i], INV_QTY_INIT[i]);
                }
            }
        }
        for (Item item : Inventory.toArray()) { // Deposit items not in invLoadOut
            if (item != null) {
                boolean itemInLoadOut = false;
                int id = item.getId();
                int invCount = Inventory.count(id);
                for (byte j = 0; j < ScriptData.currentLoadOutData.getInvSize(); j++) { // check invLoadOut
                    if (id == ScriptData.currentLoadOutData.getInvItemID(j)) {
                        itemInLoadOut = true;
                        Logger.log("id " + id + " is in invLoadOut");
                        if (invCount > ScriptData.currentLoadOutData.getInvItemQtyMax(j) && SCScript.BANKING_DATA.depositNotContainsID(id)) {
                            SCScript.BANKING_DATA.addItemToDeposit(id, invCount - ScriptData.currentLoadOutData.getInvItemQtyMax(j));
                            Logger.log("Added id " + id + " toDeposit, qty: " + (invCount - ScriptData.currentLoadOutData.getInvItemQtyMax(j)));
                        }
                        break;
                    }
                }
                if (!itemInLoadOut && SCScript.BANKING_DATA.depositNotContainsID(id)) {
                    SCScript.BANKING_DATA.addItemToDeposit(id, invCount);
                    Logger.log("Added id " + id + " toDeposit, qty: " + invCount);
                }
            }
        }

        if (ScriptData.currentTask != ScriptData.currentMoneyMakingTask && SCScript.GE_DATA.getBuySize() != 0) {
            SCScript.scriptState = ScriptState.BUY_ITEMS;
            Logger.log("Triggered buyItems, resetting deposit & withdraw, initializeTaskI set to 1");
            SCScript.BANKING_DATA.resetDeposit();
            SCScript.BANKING_DATA.resetWithdraw();
            InitializeTask.initializeTaskI = 1;
            return true;
        }
        if (SCScript.BANKING_DATA.bankingShouldBeToggled()) {
            SCScript.BANKING_DATA.shuffleDeposit();
            SCScript.BANKING_DATA.shuffleWithdraw();
            SCScript.scriptState = ScriptState.BANKING;
            return true;
        }
        return false;
    }

    public void setShouldBank(Condition shouldBank) {
        this.shouldBank = shouldBank;
    }

}
