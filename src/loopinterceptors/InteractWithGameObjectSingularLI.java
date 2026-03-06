package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;

public class InteractWithGameObjectSingularLI extends LoopInterceptor {

    private int gameObjectID;
    private String action;
    private Condition sleepUntil;

    public InteractWithGameObjectSingularLI() {
        super(() -> ScriptData.IN_CURRENT_AREA.verify() && ScriptData.NOT_HANDLE_LOAD_OUT.verify());
    }

    public void setGameObjectID(int gameObjectID) {
        this.gameObjectID = gameObjectID;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setSleepUntil(Condition sleepUntil) {
        this.sleepUntil = sleepUntil;
    }

    @Override
    public int handle() {
        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != gameObjectID) {
            ScriptData.currentGameObject = GameObjects.closest(gameObject -> ScriptData.currentArea.contains(gameObject) && gameObject.hasAction(action) && gameObject.getId() == gameObjectID);
            return ScriptData.returnMSFast();
        }
        if (!ScriptData.currentGameObject.canReach()) {
            if (ScriptData.walkToEntity(ScriptData.currentGameObject)) {
                return ScriptData.returnMSNormal();
            }
            return ScriptData.returnMSFast();
        }
        if (ScriptData.currentGameObject.interact(action)) {
            Sleep.sleepUntil(sleepUntil, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
        }

        return ScriptData.returnMSFast();
    }
}
