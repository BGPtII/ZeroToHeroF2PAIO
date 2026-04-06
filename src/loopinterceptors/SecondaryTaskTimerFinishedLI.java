package loopinterceptors;

import data.global.ScriptData;
import org.dreambot.api.utilities.Logger;

public class SecondaryTaskTimerFinishedLI extends TaskTimerFinishedLI {

    public SecondaryTaskTimerFinishedLI() {
        super(() -> ScriptData.secondaryTaskTimer != null && ScriptData.secondaryTaskTimer.finished());
    }

    @Override
    public int handle() {
        Logger.log("secondaryTaskTimer finished, taskType: 2, currentPipelineI: 33");
        ScriptData.resetEntities();
        ScriptData.currentEntityName = null;
        ScriptData.secondaryTaskTimer = null;
        ScriptData.taskType = 2;
        ScriptData.currentPipelineI = 33;
        ScriptData.resetEntities();
        ScriptData.currentTile = null;
        ScriptData.questOrder = null;
        ScriptData.questOrderI = 0;
        return 0;
    }
}
