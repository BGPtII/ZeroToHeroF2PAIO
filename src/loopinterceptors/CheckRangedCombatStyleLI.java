package loopinterceptors;

import data.global.PlayerData;
import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.Bank;

public class CheckRangedCombatStyleLI extends LoopInterceptor {

    public CheckRangedCombatStyleLI() {
        super(() -> Combat.getCombatStyle() != PlayerData.rangedCombatStyle);
    }

    @Override
    public int handle() {
        if (Bank.isOpen()) {
            Bank.close();
            return ScriptData.returnMSNormal();
        }
        if (Combat.setCombatStyle(PlayerData.rangedCombatStyle)) {
            return ScriptData.returnMSNormal();
        }
        return ScriptData.returnMSFast();
    }

}
