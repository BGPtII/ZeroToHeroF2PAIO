package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;

public class RunecraftLI extends LoopInterceptor {


    public RunecraftLI() {
        super(() -> ScriptData.currentArea.contains(Players.getLocal()) && ScriptData.NOT_HANDLE_LOAD_OUT.verify());
    }

    @Override
    public int handle() {
        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || !ScriptData.currentGameObject.getName().equals("Altar")) {
            ScriptData.currentGameObject = GameObjects.closest("Altar");
            return ScriptData.returnMSFast();
        }
        if (!ScriptData.currentGameObject.canReach()) {
            if (ScriptData.walkToEntity(ScriptData.currentGameObject)) {
                return ScriptData.returnMSNormal();
            }
            return ScriptData.returnMSFast();
        }
        if (ScriptData.currentGameObject.interact("Craft-rune")) {
            Sleep.sleepUntil(() -> !Inventory.contains(7936), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
        }

        return ScriptData.returnMSFast();
    }

}
