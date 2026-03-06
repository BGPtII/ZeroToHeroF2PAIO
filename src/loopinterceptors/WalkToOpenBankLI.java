package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.utilities.Sleep;

public class WalkToOpenBankLI extends LoopInterceptor {

    public WalkToOpenBankLI() {
        super(() -> !Bank.isOpen());
    }

    @Override
    public int handle() {
        Bank.open();
        Sleep.sleepUntil(ScriptData.SHOULD_WALK, ScriptData.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);

        return ScriptData.returnMSNormal();
    }

}
