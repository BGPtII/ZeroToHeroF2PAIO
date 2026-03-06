package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;

public class EnterRiftLI extends LoopInterceptor {

    public EnterRiftLI() {
        super(() -> !ScriptData.currentArea.contains(Players.getLocal()) && ScriptData.NOT_HANDLE_LOAD_OUT.verify());
    }

    @Override
    public int handle() {
        if (!ScriptData.currentArea2.contains(Players.getLocal())) {
            if (ScriptData.walkToArea(ScriptData.currentArea2)) {
                return ScriptData.returnMSNormal();
            }
            return ScriptData.returnMSFast();
        }

        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || !ScriptData.currentGameObject.getName().equals("Mysterious ruins")) {
            ScriptData.currentGameObject = GameObjects.closest("Mysterious ruins");
            return ScriptData.returnMSFast();
        }
        if (!ScriptData.currentGameObject.canReach()) {
            if (ScriptData.walkToEntity(ScriptData.currentGameObject)) {
                return ScriptData.returnMSNormal();
            }
            return ScriptData.returnMSFast();
        }

        if (ScriptData.TASK_LOAD_OUTS[6].getEqpItemID(0) >= 5000 || Inventory.isItemSelected()) { // Tiara == > 5000
            if (ScriptData.currentGameObject.interact("Craft-rune")) {
                Sleep.sleepUntil(ScriptData.NOT_HANDLE_LOAD_OUT, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            }
            return ScriptData.returnMSFast();
        }

        Inventory.interact(ScriptData.TASK_LOAD_OUTS[6].getEqpItemID(0));
        return ScriptData.returnMSNormal();
    }
}
