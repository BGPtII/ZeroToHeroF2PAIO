package loopinterceptors;

import data.global.PlayerData;
import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

public class EnterRiftLI extends LoopInterceptor {

    public EnterRiftLI() {
        super(() -> !ScriptData.currentArea.contains(Players.getLocal()));
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

        if (PlayerData.currentRunecraftingMedium >= 5000 || Inventory.isItemSelected()) { // Tiara == > 5000
            Logger.log(PlayerData.currentRunecraftingMedium);
            if (ScriptData.currentGameObject.interact()) {
                Sleep.sleepUntil(() -> !Inventory.contains(7936), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300); // Pure essence
            }
            return ScriptData.returnMSFast();
        }

        Inventory.interact(PlayerData.currentRunecraftingMedium);
        return ScriptData.returnMSNormal();
    }

}
