package nodes.moneymakingtasks;

import framework.Node;
import framework.SCScript;
import data.global.ScriptData;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.List;

/**
 * Needs to bank:
 * - Inventory is full, Inventory doesn't contain Wool
 * LoadOut(inv) (min, max init):
 * - Shears: 0, 1, 0
 * - Wool: 0, 27, 0
 */
public class SpinningBallsOfWool implements Node {

    private final Condition SHEARED_SHEEP = () -> !ScriptData.currentNPC.hasAction("Shear");
    private final Condition DONE_PROCESSING = () -> !Inventory.contains(1737) || (Dialogues.inDialogue() && !ItemProcessing.isOpen());

    @Override
    public int loop() {
        if (Dialogues.inDialogue()) {
            if (ItemProcessing.isOpen()) {
                if (ItemProcessing.makeAll(1759)) { // The auto toggle of run will interrupt processing
                    int walkingTrs = Walking.getRunThreshold();
                    Walking.setRunThreshold(101);
                    Sleep.sleepUntil(DONE_PROCESSING, SCScript.SECURE_RANDOM.nextInt(180000 - 120000 + 1) + 120000, 500);
                    Walking.setRunThreshold(walkingTrs);
                }
            }
            else if (Dialogues.canContinue()) {
                Dialogues.continueDialogue();
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (ScriptData.moneyMakingTaskTimer.finished()) {
            ScriptData.finishMoneyMakingTask();
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (ScriptData.currentLoadOutData.shouldBank()) {
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!Inventory.contains(1735)) {
            if (ScriptData.interactWithGroundItemSingle(1735, ScriptData.currentArea3, "Take")) {
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!Inventory.isFull()) { // Collect Wool
            if (ScriptData.currentArea.contains(Players.getLocal())) {
                if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || !ScriptData.currentNPC.hasAction("Shear")) {
                    List<NPC> opts;
                    if (SCScript.SECURE_RANDOM.nextInt(100) < 95) {
                        opts = NPCs.all(npc -> npc.getId() != 731 && npc.hasAction("Shear"));
                    }
                    else {
                        opts = NPCs.all(npc -> npc.distance() <= ScriptData.currentEntityDistance && npc.getId() != 731 && npc.hasAction("Shear"));
                    }
                    if (opts.isEmpty()) {
                        ScriptData.incrementCurrentEntityDistance();
                    }
                    else {
                        ScriptData.resetCurrentEntityDistance();
                        ScriptData.currentNPC = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                    }
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else if (ScriptData.currentNPC.canReach()) {
                    if (ScriptData.currentNPC.interact("Shear")) {
                        Sleep.sleepUntil(SHEARED_SHEEP, SCScript.SECURE_RANDOM.nextInt(30000 - 15000 + 1) + 15000, 300);
                    }
                }
                else {
                    if (ScriptData.walkToEntity(ScriptData.currentNPC)) {
                        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                    }
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
            }
            else {
                if (ScriptData.walkToArea(ScriptData.currentArea)) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (ScriptData.currentArea2.contains(Players.getLocal())) { // Process Ball of wool
            if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 14889) {
                ScriptData.currentGameObject = GameObjects.closest(14889);
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            }
            else if (ScriptData.currentGameObject.canReach()) {
                if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 0 && ClientSettings.getEnergyThresholdToEnableRunning() != 0) {
                    Walking.toggleRun();
                }
                else if (ScriptData.currentGameObject.interact("Spin")) {
                    Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 3000 + 1) + 3000, 300);
                }
            }
            else {
                if (!ScriptData.walkToArea(ScriptData.currentArea2)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
            }
        }
        else if (!ScriptData.walkToArea(ScriptData.currentArea2)) {
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
