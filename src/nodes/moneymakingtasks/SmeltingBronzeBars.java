package nodes.moneymakingtasks;

import framework.Node;
import framework.SCScript;
import data.global.PlayerData;
import data.global.ScriptData;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

/**
 * - Mine tin/copper from Lumbridge mine
 * - Smelt at Lumbridge furnace
 * - currentArea - mining, currentArea2 - smithing
 * - uses bestPickaxeAvail
 */
public class SmeltingBronzeBars implements Node {

    private final Condition DONE_PROCESSING = () -> !Inventory.containsAll(438, 436) || (Dialogues.inDialogue() && !ItemProcessing.isOpen()); // Tin ore, Copper ore

    @Override
    public int loop() {
        if (Dialogues.inDialogue()) {
            if (ItemProcessing.isOpen()) {
                if (ItemProcessing.makeAll(2349)) { // The auto toggle of run will interrupt processing
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

        if (((PlayerData.canEquipPickaxe && Inventory.isFull())
                || (!PlayerData.canEquipPickaxe && Inventory.getEmptySlots() == 1))
                || Inventory.contains(2349)) {
            if (!ScriptData.currentArea2.contains(Players.getLocal())) {
                if (ScriptData.walkToArea(ScriptData.currentArea2)) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            }
            else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 24009) {
                ScriptData.currentGameObject = GameObjects.closest(24009);
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            }
            else if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 0 && ClientSettings.getEnergyThresholdToEnableRunning() != 0) {
                Walking.toggleRun();
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            else if (ScriptData.currentGameObject.interact("Smelt")) {
                Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 3000 + 1) + 3000, 300);
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (!ScriptData.currentArea.contains(Players.getLocal())) {
            if (ScriptData.walkToArea(ScriptData.currentArea)) {
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists()) {
            List<GameObject> rockOpts;
            rockOpts = GameObjects.all(gO -> (Inventory.count(438) < ScriptData.currentLoadOutData.getInvItemQtyMax(2) && gO.getName().equals("Tin rocks")) || (Inventory.count(436) < ScriptData.currentLoadOutData.getInvItemQtyMax(2) && gO.getName().equals("Copper rocks")));
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
