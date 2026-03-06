package loopinterceptors;

import data.global.PlayerData;
import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.combat.Combat;

public class CheckMeleeCombatStyleLI extends LoopInterceptor {

    public CheckMeleeCombatStyleLI() {
        super(() -> ScriptData.NOT_HANDLE_LOAD_OUT.verify() && Combat.getCombatStyle() != PlayerData.meleeCombatStyle);
    }

    @Override
    public int handle() {
        if (Combat.setCombatStyle(PlayerData.meleeCombatStyle)) {
            return ScriptData.returnMSNormal();
        }
        return ScriptData.returnMSFast();
    }

}
