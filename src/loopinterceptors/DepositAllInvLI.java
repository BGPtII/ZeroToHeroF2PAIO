package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.utilities.Sleep;

public class DepositAllInvLI extends LoopInterceptor {

    private boolean depositAllInv;

    public DepositAllInvLI() {
        super(null);
        setShouldHandle(() -> depositAllInv);
    }

    public void setDepositAllInv(boolean depositAllInv) {
        this.depositAllInv = depositAllInv;
    }

    @Override
    public int handle() {
        if (Bank.depositAllItems()) {
            Sleep.sleepUntil(Inventory::isEmpty, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            if (Inventory.isEmpty()) {
                depositAllInv = false;
            }
            return ScriptData.returnMSFast();
        }

        return ScriptData.returnMSNormal();
    }

}
