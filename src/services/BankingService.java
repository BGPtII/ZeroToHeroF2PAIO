package services;

import framework.SCScript;
import framework.ScriptState;
import global.PlayerData;

public class BankingService {

    private final int[] WITHDRAW_ID;
    private final int[] WITHDRAW_QTY;
    private byte withdrawSize;

    private final int[] DEPOSIT_ID;
    private final int[] DEPOSIT_QTY;
    private byte depositSize;

    private boolean dpAllEqpInit;
    private boolean dpAllInvInit;

    private boolean withdrawModeIsNoted;

    public BankingService() {
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

    public int getDepositId(byte index) {
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
        return depositSize != 0 || withdrawSize != 0 || dpAllEqpInit;
    }

    public void startBanking() {
        PlayerData.stateToReturnTo = SCScript.scriptState;

        SCScript.scriptState = ScriptState.BANKING;
    }

    public void toggleWithdrawMode(boolean isNoted) {
        withdrawModeIsNoted = isNoted;
    }
    public boolean getWithdrawModeIsNoted() {
        return withdrawModeIsNoted;
    }

}
