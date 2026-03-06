package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.widget.Widgets;

public class FinishQuestLI extends LoopInterceptor {

    public FinishQuestLI() {
        super(() -> ScriptData.currentFreeQuest.isFinished());
    }

    @Override
    public int handle() {
        if (Widgets.isVisible(153, 16)) {
            if (ScriptData.closeQuestCompletionWidget()) {
                return ScriptData.returnMSNormal();
            }
        }
        else {
            ScriptData.finishProgressionQuestingTask();
        }
        return ScriptData.returnMSFast();
    }

}
