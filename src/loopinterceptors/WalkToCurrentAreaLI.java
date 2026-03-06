package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.interactive.Players;

public class WalkToCurrentAreaLI extends LoopInterceptor {

    public WalkToCurrentAreaLI() {
        super(() -> ScriptData.NOT_HANDLE_LOAD_OUT.verify() && !ScriptData.currentArea.contains(Players.getLocal()));
    }

    @Override
    public int handle() {
        if (ScriptData.walkToArea(ScriptData.currentArea)) {
            return ScriptData.returnMSNormal();
        }
        return ScriptData.returnMSFast();
    }

}
