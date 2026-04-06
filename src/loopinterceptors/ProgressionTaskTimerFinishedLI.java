package loopinterceptors;

import data.global.ScriptData;

public class ProgressionTaskTimerFinishedLI extends TaskTimerFinishedLI {

    public ProgressionTaskTimerFinishedLI() {
        super(() -> ScriptData.progressionTaskTimer != null && ScriptData.progressionTaskTimer.finished());
    }

    @Override
    public int handle() {
        ScriptData.currentEntityName = null;
        ScriptData.progressionTaskTimer = null;
        ScriptData.taskType = 0;
        ScriptData.resetEntities();
        ScriptData.currentTile = null;
        ScriptData.questOrder = null;
        ScriptData.questOrderI = 0;
        ScriptData.currentPipelineI = 33;
        return 0;
    }

}
