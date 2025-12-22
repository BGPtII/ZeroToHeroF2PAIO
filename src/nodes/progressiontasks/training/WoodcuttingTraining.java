package nodes.progressiontasks.training;

import framework.Node;
import framework.SCScript;
import data.global.ScriptData;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

public class WoodcuttingTraining implements Node {

    @Override
    public int loop() {
        if (Dialogues.inDialogue()) {
            if (Dialogues.canContinue()) {
                Dialogues.continueDialogue();
                Logger.log("Attempted to continue dialogue");
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (ScriptData.progressionTaskTimer.finished()) {
            ScriptData.finishProgressionTrainingTask();
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (ScriptData.currentLoadOutData.shouldBank()) {
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!ScriptData.currentArea.contains(Players.getLocal())) {
            if (ScriptData.walkToArea(ScriptData.currentArea)) {
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists()) {
            List<GameObject> opts = GameObjects.all(gameObject -> ScriptData.currentArea.contains(gameObject)
                    && gameObject.hasAction("Chop down")
                    && gameObject.distance() <= ScriptData.currentEntityDistance
                    && (gameObject.getName().equals(ScriptData.currentEntityName) || (ScriptData.currentEntityName.startsWith("T") && (gameObject.getName().equals("Dead tree") || gameObject.getName().equals("Evergreen tree")))));
            if (opts.isEmpty()) {
                ScriptData.incrementCurrentEntityDistance();
            }
            else {
                ScriptData.currentGameObject = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                ScriptData.resetCurrentEntityDistance();
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!ScriptData.currentGameObject.canReach()) {
            if (ScriptData.walkToEntity(ScriptData.currentGameObject)) {
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (ScriptData.currentGameObject.interact("Chop down")) {
            Sleep.sleepUntil(ScriptData.DONE_RESOURCE_GATHERING, SCScript.SECURE_RANDOM.nextInt(300000 - 240000 + 1) + 240000, 300);
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
