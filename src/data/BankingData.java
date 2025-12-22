package data;

import framework.SCScript;
import org.dreambot.api.utilities.Logger;

public class BankingData {

    private final int[] WITHDRAW_ID;
    private final int[] WITHDRAW_QTY;
    private byte withdrawSize;

    private final int[] DEPOSIT_ID;
    private final int[] DEPOSIT_QTY;
    private byte depositSize;

    private boolean dpAllEqpInit;
    private boolean dpAllInvInit;

    private boolean withdrawModeIsNoted;

    public BankingData() {
        WITHDRAW_ID = new int[28];
        WITHDRAW_QTY = new int[28];
        DEPOSIT_ID = new int[28];
        DEPOSIT_QTY = new int[28];
    }

    public void addItemToWithdraw(int id, int qty) {
        WITHDRAW_ID[withdrawSize] = id;
        WITHDRAW_QTY[withdrawSize++] = qty;
    }

    public void addItemToDeposit(int id, int qty) {
        DEPOSIT_ID[depositSize] = id;
        DEPOSIT_QTY[depositSize++] = qty;
        Logger.log("Added toDeposit, id: " + id + ", qty: " + qty);
    }

    public byte getWithdrawSize() {
        return withdrawSize;
    }
    public void resetWithdraw() {
        withdrawSize = 0;
    }

    public byte getDepositSize() {
        return depositSize;
    }
    public void resetDeposit() {
        depositSize = 0;
    }

    public int getDepositID(int index) {
        return DEPOSIT_ID[index];
    }
    public int getDepositQty(byte index) {
        return DEPOSIT_QTY[index];
    }

    public boolean depositNotContainsID(int id) {
        for (byte i = 0; i < depositSize; i++) {
            if (DEPOSIT_ID[i] == id) {
                return false;
            }
        }
        return true;
    }

    public void toggleDpAllEqpInit(boolean on) { // Uses depositAllEquipment widget as per Banking#depositAllEquipment
        dpAllEqpInit = on;
    }
    public boolean depositAllEquipment() {
        return dpAllEqpInit;
    }
    public boolean depositAllInventory() {
        return dpAllInvInit;
    }

    public void toggleDpAllInvInit(boolean on) {
        dpAllInvInit = on;
    }

    public boolean bankingShouldBeToggled() { // Has items queued up to be banked
        return depositSize != 0 || withdrawSize != 0 || dpAllEqpInit || dpAllInvInit;
    }

    public void toggleWithdrawMode(boolean isNoted) {
        withdrawModeIsNoted = isNoted;
    }
    public boolean getWithdrawModeIsNoted() {
        return withdrawModeIsNoted;
    }

    public int getWithdrawID(int index) {
        return WITHDRAW_ID[index];
    }
    public int getWithdrawQty(int index) {
        return WITHDRAW_QTY[index];
    }

    public void shuffleDeposit() {
        for (int i = depositSize - 1; i > 0; i--) {
            int j = SCScript.SECURE_RANDOM.nextInt(i + 1);
            int tmpID = DEPOSIT_ID[i];
            int tmpQty = DEPOSIT_QTY[i];
            DEPOSIT_ID[i] = DEPOSIT_ID[j];
            DEPOSIT_QTY[i] = DEPOSIT_QTY[j];
            DEPOSIT_ID[j] = tmpID;
            DEPOSIT_QTY[j] = tmpQty;
        }
    }
    public void shuffleWithdraw() {
        for (int i = withdrawSize - 1; i > 0; i--) {
            int j = SCScript.SECURE_RANDOM.nextInt(i + 1);
            int tmpID = WITHDRAW_ID[i];
            int tmpQty = WITHDRAW_QTY[i];
            WITHDRAW_ID[i] = WITHDRAW_ID[j];
            WITHDRAW_QTY[i] = WITHDRAW_QTY[j];
            WITHDRAW_ID[j] = tmpID;
            WITHDRAW_QTY[j] = tmpQty;
        }
    }

}
