package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.utilities.Logger;

public class CantReachCurrentNPCLI extends LoopInterceptor {

    public CantReachCurrentNPCLI() {
        super(() -> ScriptData.currentNPC != null
                && ScriptData.currentNPC.getHealthPercent() > 0
                && !ScriptData.currentNPC.canReach());
    }

    @Override
    public int handle() {
        Logger.log("Can't reach currentNPC");
        if (ScriptData.walkToEntity(ScriptData.currentNPC)) {
            return ScriptData.returnMSNormal();
        }
        return ScriptData.returnMSFast();
    }
}
