package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

public class DepositLI extends LoopInterceptor {

    private final int[] ID;
    private final int[] QTY;
    private byte depositSize;
    private byte depositI;

    public DepositLI() {
        super(null);
        ID = new int[28];
        QTY = new int[28];
        setShouldHandle(() -> depositSize != 0);
    }

    public void addItemToDeposit(int id, int qty) {
        ID[depositSize] = id;
        QTY[depositSize++] = qty;
    }

    public boolean depositNotContainsID(int id) {
        for (byte i = 0; i < depositSize; i++) {
            if (ID[i] == id) {
                return false;
            }
        }
        return true;
    }

    public void reset() {
        depositSize = 0;
        depositI = 0;
    }

    public byte getDepositSize() {
        return depositSize;
    }

    public void shuffleDeposit() {
        for (int i = depositSize - 1; i > 0; i--) {
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
        if (depositI == depositSize) {
            depositI = 0;
            depositSize = 0;
            Logger.log("Done depositing");
        }
        else {
            int id = ID[depositI];
            int bankCount = Bank.count(id);
            if (Bank.deposit(id, QTY[depositI])) {
                Sleep.sleepUntil(() -> Bank.count(id) != bankCount, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                if (Bank.count(id) != bankCount) {
                    depositI++;
                }
            }
        }
        return ScriptData.returnMSFast();
    }

}
