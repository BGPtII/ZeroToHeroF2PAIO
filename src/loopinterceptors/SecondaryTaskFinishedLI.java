package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;

public class SecondaryTaskFinishedLI extends LoopInterceptor {

    public SecondaryTaskFinishedLI() {
        super(() -> ScriptData.secondaryTaskTimer != null && ScriptData.secondaryTaskTimer.finished());
    }

    @Override
    public int handle() {
        ScriptData.currentEntityName = null;
        ScriptData.secondaryTaskTimer = null;
        ScriptData.taskType = 2;
        ScriptData.currentPipelineI = 33;
        return 0;
    }
}
