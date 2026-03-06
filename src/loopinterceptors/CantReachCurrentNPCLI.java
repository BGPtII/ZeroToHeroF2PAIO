package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;

public class CantReachCurrentNPCLI extends LoopInterceptor {

    public CantReachCurrentNPCLI() {
        super(() -> ScriptData.NOT_HANDLE_LOAD_OUT.verify()
                && ScriptData.currentNPC != null
                && ScriptData.currentNPC.getHealthPercent() > 0
                && !ScriptData.currentNPC.canReach());
    }

    @Override
    public int handle() {
        if (ScriptData.walkToEntity(ScriptData.currentNPC)) {
            return ScriptData.returnMSNormal();
        }
        return ScriptData.returnMSFast();
    }
}
