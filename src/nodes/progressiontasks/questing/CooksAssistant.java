package nodes.progressiontasks.questing;

import framework.Node;
import framework.SCScript;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;

/**
 * - eggArea = currentArea
 * - dairyCowArea = currentArea2
 */
public class CooksAssistant implements Node {

    private final Area COOK_AREA = new Area(3205, 3216, 3211, 3212);
    private final Area BUCKET_AREA = new Area(3212, 9625, 3216, 9622);
    private final Area GRAIN_AREA = new Area(3154, 3305, 3163, 3290);
    private final Area HOPPER_AREA = new Area(3164, 3309, 3169, 3304, 2);
    private final Area FLOUR_BIN_AREA = new Area(3164, 3309, 3169, 3304, 0);

    @Override
    public int loop() {
        if (Dialogues.inDialogue()) {
            if (Dialogues.isProcessing()) {
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            if (Dialogues.canContinue()) {
                Dialogues.continueDialogue();
            }
            else if (Dialogues.areOptionsAvailable()) {
                Dialogues.chooseFirstOption(ScriptData.dialogueOpts);
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (ScriptData.currentLoadOutData.shouldBank()) {
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (FreeQuest.COOKS_ASSISTANT.isFinished()) {
            if (Widgets.isVisible(153, 16)) {
                if (ScriptData.closeQuestCompletionWidget()) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
            }
            else {
                ScriptData.finishProgressionQuestingTask();
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        switch (ScriptData.questOrder[ScriptData.questOrderI]) {
            case 0:
                if (FreeQuest.COOKS_ASSISTANT.isStarted()) {
                    ScriptData.questOrderI++;
                }
                else {
                    if (ScriptData.interactWithNPC(4626, COOK_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                    }
                }
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            case 1:
                if (Inventory.contains(1944)) { // Egg
                    ScriptData.questOrderI++;
                }
                else {
                    if (ScriptData.interactWithGroundItemMultiple(1944, ScriptData.currentArea,  "Take")) {
                        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                    }
                }
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            case 2:
                if (Inventory.contains(1927)) { // Bucket of milk
                    ScriptData.questOrderI++;
                }
                else {
                    if (ScriptData.interactWithGameObjectMultiple(8689, ScriptData.currentArea2, "Milk")) {
                        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                    }
                }
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            case 3: // Pot of flour - Pre-Grain in Hopper
                if (Inventory.contains(1933)) { // Pot of flour
                    ScriptData.questOrderI++;
                }
                else if (Inventory.contains(1947)) { // Grain
                    if (!HOPPER_AREA.contains(Players.getLocal())) {
                        if (ScriptData.walkToArea(HOPPER_AREA)) {
                            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                        }
                    }
                    else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 24961) {
                        ScriptData.currentGameObject = GameObjects.closest(24961); // Hopper
                    }
                    else if (ScriptData.currentGameObject.interact("Fill")) {
                        Sleep.sleepUntil(() -> ScriptData.questOrderI == 4, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                    }
                }
                else if (ScriptData.interactWithGameObjectMultiple("Wheat", GRAIN_AREA, "Pick")) { // Different IDs for Wheat, use Name
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            case 4: // Pot of flour - Post-Grain in Hopper
                if (Inventory.contains(1933)) { // Pot of flour
                    ScriptData.questOrderI++;
                }
                else if (PlayerSettings.getBitValue(4920) > 0) { // Flour bin has Flour
                    if (ScriptData.interactWithGameObjectSingle(1781, FLOUR_BIN_AREA, "Empty", () -> Inventory.contains(1933))) { // Flour bin#Full
                        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                    }
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else if (!HOPPER_AREA.contains(Players.getLocal())) {
                    if (ScriptData.walkToArea(HOPPER_AREA)) {
                        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                    }
                }
                else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 24964) {
                    ScriptData.currentGameObject = GameObjects.closest(24964); // Hopper
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else if (ScriptData.currentGameObject.interact("Operate")) {
                    Sleep.sleepUntil(() -> PlayerSettings.getBitValue(4920) != 0, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                }
                break;
            case 5: // Finish quest
                if (ScriptData.interactWithNPC(4626, COOK_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            case 6:
                if (Inventory.contains(1925, 1927)) { // Bucket, Bucket of milk
                    ScriptData.questOrderI++;
                }
                else {
                    if (ScriptData.interactWithGroundItemSingle(1925, BUCKET_AREA, "Take")) {
                        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                    }
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 7:
                if (Inventory.contains(1931, 1933)) { // Pot, Pot of flour
                    ScriptData.questOrderI++;
                }
                else {
                    if (ScriptData.interactWithGroundItemSingle(1931, COOK_AREA, "Take")) {
                        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                    }
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
