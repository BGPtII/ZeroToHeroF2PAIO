package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

public class WoodcuttingLI extends LoopInterceptor {

    public WoodcuttingLI() {
        super(ScriptData.IN_CURRENT_AREA);
    }

    @Override
    public int handle() {
        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists()) {
            List<GameObject> opts = GameObjects.all(gameObject -> ScriptData.currentArea.contains(gameObject)
                    && gameObject.hasAction("Chop down")
                    && gameObject.distance() <= ScriptData.currentEntityDistance
                    && (gameObject.getName().equals(ScriptData.currentEntityName) || (ScriptData.currentEntityName.startsWith("T") && (gameObject.getName().equals("Dead tree") || gameObject.getName().equals("Evergreen tree")))));
            if (opts.isEmpty()) {
                ScriptData.incrementCurrentEntityDistance();
            }
            else {
                ScriptData.currentGameObject = opts.get(ScriptData.SECURE_RANDOM.nextInt(opts.size()));
                ScriptData.resetCurrentEntityDistance();
            }
            return ScriptData.returnMSFast();
        }

        if (ScriptData.currentGameObject.interact("Chop down")) {
            Sleep.sleepUntil(ScriptData.DONE_RESOURCE_GATHERING, ScriptData.SECURE_RANDOM.nextInt(300000 - 240000 + 1) + 240000, 300);
            return ScriptData.returnMSFast();
        }

        return ScriptData.returnMSNormal();
    }
}
