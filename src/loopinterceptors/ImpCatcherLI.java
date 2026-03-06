package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.map.Area;

public class ImpCatcherLI extends LoopInterceptor {

    private final Area WIZARD_MIZGOG_AREA = new Area(3105, 3162, 3102, 3165, 2);

    public ImpCatcherLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    @Override
    public int handle() {
        if (!ScriptData.interactWithNPC(7746, WIZARD_MIZGOG_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
            return ScriptData.returnMSFast();
        }
        return ScriptData.returnMSNormal();
    }

}
