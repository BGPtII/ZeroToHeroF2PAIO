package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;

public class InteractWithAnvilLI extends LoopInterceptor {

    public InteractWithAnvilLI() {
        super(() -> ScriptData.currentArea.contains(Players.getLocal()) && !Widgets.isVisible(312, 0));
    }

    @Override
    public int handle() {
        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2097) { // Anvil#Normal
            ScriptData.currentGameObject = GameObjects.closest(2097);
            return ScriptData.returnMSFast();
        }
        if (!ScriptData.currentGameObject.canReach()) {
            if (ScriptData.walkToEntity(ScriptData.currentGameObject)) {
                return ScriptData.returnMSNormal();
            }
            return ScriptData.returnMSFast();
        }
        if (ScriptData.currentGameObject.interact()) {
            Sleep.sleepUntil(() -> Widgets.isVisible(312, 0), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
        }

        return ScriptData.returnMSFast();
    }
}
