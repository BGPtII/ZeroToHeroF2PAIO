package loopinterceptors;

import data.global.PlayerData;
import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;

public class CheckMeleeCombatStyleLI extends LoopInterceptor {

    public CheckMeleeCombatStyleLI() {
        super(() -> Combat.getCombatStyle() != PlayerData.meleeCombatStyle);
    }

    @Override
    public int handle() {
        if (Bank.isOpen()) {
            Bank.close();
            return ScriptData.returnMSNormal();
        }
        if (Combat.setCombatStyle(PlayerData.meleeCombatStyle)) {
            return ScriptData.returnMSNormal();
        }
        return ScriptData.returnMSFast();
    }

}
