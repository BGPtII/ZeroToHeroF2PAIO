package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.utilities.Sleep;

public class WithdrawLI extends LoopInterceptor {

    private final int[] ID;
    private final int[] QTY;
    private byte withdrawSize;
    private byte withdrawI;

    public WithdrawLI() {
        super(null);
        ID = new int[28];
        QTY = new int[28];
        setShouldHandle(() -> withdrawSize != 0 && ScriptData.bankWithdrawModeLI.getBankMode() == Bank.getWithdrawMode());
    }

    public void reset() {
        withdrawSize = 0;
    }

    public void addItemToWithdraw(int id, int qty) {
        ID[withdrawSize] = id;
        QTY[withdrawSize++] = qty;
    }

    public byte getWithdrawSize() {
        return withdrawSize;
    }

    public void shuffleWithdraw() {
        for (int i = withdrawSize - 1; i > 0; i--) {
            int j = ScriptData.SECURE_RANDOM.nextInt(i + 1);
            int tmpID = ID[i];
            int tmpQty = QTY[i];
            ID[i] = ID[j];
            QTY[i] = QTY[j];
            ID[j] = tmpID;
            QTY[j] = tmpQty;
        }
    }

    @Override
    public int handle() {
        if (withdrawI == withdrawSize) {
            withdrawSize = 0;
            withdrawI = 0;
            return ScriptData.returnMSFast();
        }
        else if (Inventory.isFull()) {
            withdrawSize = 0;
            withdrawI = 0;
            return ScriptData.returnMSFast();
        }
        else if (Bank.count(ID[withdrawI]) == 0) {
            withdrawI++;
            return ScriptData.returnMSFast();
        }
        else {
            int id = ID[withdrawI];
            int bankCount = Bank.count(id);
            if (Bank.withdraw(id, QTY[withdrawI])) {
                Sleep.sleepUntil(() -> Bank.count(id) != bankCount, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                if (Bank.count(id) != bankCount) {
                    withdrawI++;
                }
            }
            return ScriptData.returnMSFast();
        }
    }

}
