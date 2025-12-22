package nodes.services;

import framework.Node;
import framework.SCScript;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;

public class Banking implements Node {

    private final Condition EQP_EMPTY = Equipment::isEmpty;
    private final Condition INV_EMPTY = Inventory::isEmpty;
    private final Condition WITHDRAW_MODE_NOTE = () -> Bank.getWithdrawMode() == BankMode.NOTE;
    private final Condition WITHDRAW_MODE_ITEM = () -> Bank.getWithdrawMode() == BankMode.ITEM;
    private boolean shuffle = true;
    private byte withdrawI;
    private byte depositI;

    @Override
    public int loop() {
        if (shuffle) {
            if (SCScript.BANKING_DATA.getDepositSize() != 0) {
                SCScript.BANKING_DATA.shuffleDeposit();
            }
            if (SCScript.BANKING_DATA.getWithdrawSize() != 0) {
                SCScript.BANKING_DATA.shuffleWithdraw();
            }

            ScriptData.resetEntities();
            shuffle = false;
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }
        if (Bank.isOpen()) {
            if (SCScript.BANKING_DATA.depositAllEquipment()) {
                Logger.log("Banking: Entered depositAllEqp");
                if (Bank.depositAllEquipment()) {
                    Sleep.sleepUntil(EQP_EMPTY, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                    if (EQP_EMPTY.verify()) {
                        SCScript.BANKING_DATA.toggleDpAllEqpInit(false);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
            }
            else if (SCScript.BANKING_DATA.depositAllInventory()) {
                Logger.log("Banking: Entered depositAllInventory");
                if (Bank.depositAllItems()) {
                    Sleep.sleepUntil(INV_EMPTY, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                    if (INV_EMPTY.verify()) {
                        SCScript.BANKING_DATA.toggleDpAllInvInit(false);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
            }
            else if (SCScript.BANKING_DATA.getDepositSize() != 0) {
                Logger.log("Banking: Entered deposit");
                if (depositI == SCScript.BANKING_DATA.getDepositSize()) {
                    Logger.log("Done depositing");
                    depositI = 0;
                    SCScript.BANKING_DATA.resetDeposit();
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else {
                    int id = SCScript.BANKING_DATA.getDepositID(depositI);
                    int bankCount = Bank.count(id);
                    Logger.log("Needs to deposit id: " + id + ", qty: " + SCScript.BANKING_DATA.getDepositQty(depositI));
                    if (Bank.deposit(id, SCScript.BANKING_DATA.getDepositQty(depositI))) {
                        Logger.log("Successfully deposited");
                        Sleep.sleepUntil(() -> Bank.count(id) != bankCount || !Inventory.contains(id), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        if (Bank.count(id) != bankCount || !Inventory.contains(id)) {
                            depositI++;
                            Logger.log("Increased depositI to " + depositI + ", depositSize: " + SCScript.BANKING_DATA.getDepositSize());
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                    }
                    else {
                       Logger.error("Failed to deposit");
                    }
                }
            }
            else if (SCScript.BANKING_DATA.getWithdrawModeIsNoted() && Bank.getWithdrawMode() == BankMode.ITEM) {
                if (Bank.setWithdrawMode(BankMode.NOTE)) {
                    Sleep.sleepUntil(WITHDRAW_MODE_NOTE, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
            }
            else if (!SCScript.BANKING_DATA.getWithdrawModeIsNoted() && Bank.getWithdrawMode() == BankMode.NOTE) {
                if (Bank.setWithdrawMode(BankMode.ITEM)) {
                    Sleep.sleepUntil(WITHDRAW_MODE_ITEM, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
            }
            else if (SCScript.BANKING_DATA.getWithdrawSize() != 0) {
                Logger.log("Banking: Entered withdraw");
                if (withdrawI == SCScript.BANKING_DATA.getWithdrawSize()) {
                    Logger.log("Done withdrawing");
                    SCScript.BANKING_DATA.resetWithdraw();
                    withdrawI = 0;
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else {
                    int id = SCScript.BANKING_DATA.getWithdrawID(withdrawI);
                    int bankCount = Bank.count(id);
                    Logger.log("withdrawI: " + withdrawI + ", toWithdrawID: " + id + ", bankCount of ID: " + bankCount + "toWithdrawQty: " + SCScript.BANKING_DATA.getWithdrawQty(withdrawI));
                    if (Bank.withdraw(id, SCScript.BANKING_DATA.getWithdrawQty(withdrawI))) {
                        Logger.log("Withdrew qty: " + SCScript.BANKING_DATA.getWithdrawQty(withdrawI));
                        Sleep.sleepUntil(() -> Bank.count(id) != bankCount, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        if (Bank.count(id) != bankCount) {
                            Logger.log("Current bankCount not equal to previous bankCount, incrementing withdrawI");
                            withdrawI++;
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                    }
                }
            }
            else {
                SCScript.scriptState = ScriptData.returnTo;
                shuffle = true;
            }
        }
        else {
            Bank.open(); // Bank#open returns true if Bank was opened successfully
            Sleep.sleepUntil(ScriptData.SHOULD_WALK, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }
}
