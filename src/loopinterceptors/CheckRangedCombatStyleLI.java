package loopinterceptors;

import data.global.PlayerData;
import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.combat.Combat;

public class CheckRangedCombatStyleLI extends LoopInterceptor {

    public CheckRangedCombatStyleLI() {
        super(() -> Combat.getCombatStyle() != PlayerData.rangedCombatStyle);
    }

    @Override
    public int handle() {
        if (Combat.setCombatStyle(PlayerData.rangedCombatStyle)) {
            return ScriptData.returnMSNormal();
        }
        return ScriptData.returnMSFast();
    }

}
