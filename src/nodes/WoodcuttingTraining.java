package nodes;

import framework.Node;
import framework.SCScript;
import global.PlayerData;
import global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

public class WoodcuttingTraining implements Node {

    private final Condition DONE_CHOPPING = () -> ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || Dialogues.inDialogue() || Inventory.isFull();

    @Override
    public int loop() {
        if (PlayerData.currentLoadOutService.setUpNotValid()) {
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
        }

        if (!ScriptData.currentArea.contains(Players.getLocal())) {
            ScriptData.walkToArea(ScriptData.currentArea);
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists()) {
            List<GameObject> opts = GameObjects.all(gameObject -> gameObject.getName().equals(ScriptData.currentEntityName) || (ScriptData.currentEntityName.startsWith("T") && gameObject.getName().equals("Dead tree")));
            if (!opts.isEmpty()) {
                ScriptData.currentGameObject = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (ScriptData.currentGameObject.interact("Chop down")) {
            Sleep.sleepUntil(DONE_CHOPPING, SCScript.SECURE_RANDOM.nextInt(300000 - 240000 + 1) + 240000, 300);
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
