package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;

public class BankWithdrawModeLI extends LoopInterceptor {

    private BankMode bankMode;

    public BankWithdrawModeLI() {
        super(null);
        bankMode = BankMode.ITEM;
        setShouldHandle(() -> Bank.getWithdrawMode() != bankMode && Bank.isOpen());
    }

    public void setBankMode(BankMode bankMode) {
        this.bankMode = bankMode;
    }

    public BankMode getBankMode() {
        return bankMode;
    }

    @Override
    public int handle() {
        if (Bank.setWithdrawMode(bankMode)) {
            return ScriptData.returnMSNormal();
        }
        return ScriptData.returnMSFast();
    }
}
