package nodes.progressiontasks.training;

import framework.Node;
import framework.SCScript;
import data.global.ScriptData;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

public class MiningTraining implements Node {

    private Tile getMiningTile() {
        Tile[] opts = new Tile[10];
        byte optsSize = 0;
        for (Tile t : ScriptData.currentArea.getTiles()) {
            if (Walking.canWalk(t) && GameObjects.getTopObjectOnTile(t) == null && GameObjects.all(gameObject -> gameObject.distance(t) == 1 && (gameObject.getName().equals(ScriptData.currentEntityName) || (ScriptData.currentEntityName.startsWith("T") && gameObject.getName().equals("Copper rocks")))).size() >= 2) {
                opts[optsSize++] = t;
            }
        }
        if (optsSize == 0) {
            return null;
        }
        return opts[SCScript.SECURE_RANDOM.nextInt(opts.length)];
    }

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
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 10;
        }

        if (ScriptData.currentLoadOutData.shouldBank()) {
            Logger.log("Should bank");
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!ScriptData.currentArea.contains(Players.getLocal())) {
            if (!ScriptData.walkToArea(ScriptData.currentArea)) {
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }
        if (ScriptData.currentTile == null) {
            ScriptData.currentTile = getMiningTile();
            Logger.log("currentTile (miningTile): " + ScriptData.currentTile);
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!ScriptData.currentTile.equals(Players.getLocal().getTile())) {
            if (!ScriptData.walkToTile(ScriptData.currentTile)) {
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists()) {
            List<GameObject> rockOpts;
            rockOpts = GameObjects.all(gO -> (gO.getName().equals(ScriptData.currentEntityName) || (ScriptData.currentEntityName.startsWith("T") && gO.getName().equals("Copper rocks"))) && gO.distance() == 1);
            if (!rockOpts.isEmpty()) {
                ScriptData.currentGameObject = rockOpts.get(SCScript.SECURE_RANDOM.nextInt(rockOpts.size()));
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (ScriptData.currentGameObject.interact("Mine")) {
            Sleep.sleepUntil(ScriptData.DONE_RESOURCE_GATHERING, SCScript.SECURE_RANDOM.nextInt(50000 - 20000 + 1) + 20000, 300);
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
