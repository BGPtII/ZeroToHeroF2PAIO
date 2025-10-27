package nodes;

import framework.Node;
import framework.SCScript;
import global.PlayerData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;

public class Banking implements Node {

    private final Condition EQP_EMPTY = Equipment::isEmpty;
    private final Condition INV_EMPTY = Inventory::isEmpty;
    private final Condition BANK_CLOSED = () -> !Bank.isOpen();
    private final Condition WITHDRAW_MODE_NOTE = () -> Bank.getWithdrawMode() == BankMode.NOTE;
    private final Condition WITHDRAW_MODE_ITEM = () -> Bank.getWithdrawMode() == BankMode.ITEM;

    @Override
    public int loop() {
        if (Bank.isOpen()) {
            if (SCScript.BANKING_SERVICE.depositAllEquipment()) {
                Bank.depositAllEquipment();
                Sleep.sleepUntil(EQP_EMPTY, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                if (Equipment.isEmpty()) {
                    SCScript.BANKING_SERVICE.toggleDpAllEqpInit(false);
                }
            }
            else if (SCScript.BANKING_SERVICE.depositAllInventory()) {
                Bank.depositAllItems();
                Sleep.sleepUntil(INV_EMPTY, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                if (Inventory.isEmpty()) {
                    SCScript.BANKING_SERVICE.toggleDpAllInvInit(false);
                }
            }
            else if (SCScript.BANKING_SERVICE.getDepositSize() != 0) {
                for (byte i = 0; i < SCScript.BANKING_SERVICE.getDepositSize(); i++) {
                    int invCount = Inventory.count(SCScript.BANKING_SERVICE.getDepositId(i));
                    int id = SCScript.BANKING_SERVICE.getDepositId(i);
                    Bank.deposit(id, SCScript.BANKING_SERVICE.getDepositQty(i));
                    Sleep.sleepUntil(() -> Inventory.count(id) != invCount, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                }
                SCScript.BANKING_SERVICE.resetDeposit();
            }
            else if (SCScript.BANKING_SERVICE.getWithdrawModeIsNoted() && Bank.getWithdrawMode() == BankMode.ITEM) {
                Bank.setWithdrawMode(BankMode.NOTE);
                Sleep.sleepUntil(WITHDRAW_MODE_NOTE, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            }
            else if (!SCScript.BANKING_SERVICE.getWithdrawModeIsNoted() && Bank.getWithdrawMode() == BankMode.NOTE) {
                Bank.setWithdrawMode(BankMode.ITEM);
                Sleep.sleepUntil(WITHDRAW_MODE_ITEM, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            }
            else {
                Bank.close();
                Sleep.sleepUntil(BANK_CLOSED, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                if (!Bank.isOpen()) {
                    SCScript.scriptState = PlayerData.stateToReturnTo;
                }
            }
        }
        else {
            Bank.open(PlayerData.currentBankLocation); // Bank#open returns true if Bank was opened successfully
            Sleep.sleepUntil(PlayerData.SHOULD_WALK, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }
}
