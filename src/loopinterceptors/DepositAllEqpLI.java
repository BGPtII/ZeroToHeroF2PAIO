package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.utilities.Sleep;

public class DepositAllEqpLI extends LoopInterceptor {

    private boolean depositAllEqp;

    public DepositAllEqpLI() {
        super(null);
        setShouldHandle(() -> depositAllEqp);
    }

    public void setDepositAllEqp(boolean depositAllEqp) {
        this.depositAllEqp = depositAllEqp;
    }

    @Override
    public int handle() {
        if (Bank.depositAllEquipment()) {
            Sleep.sleepUntil(Equipment::isEmpty, ScriptData.SECURE_RANDOM.nextInt(8000 - 3000 + 1) + 3000, 300);
            if (Equipment.isEmpty()) {
                depositAllEqp = false;
            }
            return ScriptData.returnMSFast();
        }
        return ScriptData.returnMSNormal();
    }

}
