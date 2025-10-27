package nodes;

import framework.Node;
import framework.SCScript;
import global.PlayerData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Sleep;

public class CooksAssistant implements Node {

    @Override
    public int loop() {
        if (PlayerData.currentLoadOutService.setUpNotValid()) {
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
        }

        // TODO: Switch statements, more randomization
        if (Utility.questOrder[Utility.questOrderI] == 0 || Utility.questOrder[Utility.questOrderI] == 4) {
            if (Utility.questOrder[Utility.questOrderI] == 0 && FreeQuest.COOKS_ASSISTANT.isStarted()) {
                Utility.questOrderI++;
            }
            else {
                Utility.interactWithNPCNotInAreaSingle("Cook", COOK_AREA, "Talk-to", Dialogues::inDialogue);
            }
        }
        else if (Utility.questOrder[Utility.questOrderI] == 1) { // Egg
            if (Inventory.contains("Egg")) {
                Utility.questOrderI++;
            }
            else {
                return Utility.interactWithGINotInAreaMultiple("Egg", "Take", Utility.currentAreaP2, Utility.CURRENT_GI_NULL_NOT_EXIST, 0, Utility.secureRandom.nextInt(5000 - 3000 + 1) + 3000, 300);
            }
        }
        else if (Utility.questOrder[Utility.questOrderI] == 2) { // Bucket of milk
            if (Inventory.contains("Bucket of milk")) {
                Utility.questOrderI++;
            }
            else {
                return Utility.interactWithGONotInAreaMultiple("Dairy cow", "Milk", Utility.currentAreaP1, Utility.CURRENT_GI_NULL_NOT_EXIST, 0, Utility.secureRandom.nextInt(5000 - 3000 + 1) + 3000, 300);
            }
        }
        else if (Utility.questOrder[Utility.questOrderI] == 3) { // Pot of flour
            if (Inventory.contains("Pot of flour")) {
                Utility.questOrderI++;
            }
            else if (PlayerSettings.getBitValue(4920) > 0) {
                return Utility.interactWithGONotInAreaSingle("Flour bin", "Empty", FLOUR_BIN_AREA, () -> Inventory.contains("Pot of flour"));
            }
            else if (Utility.cooksAssistantWindmillStage == 0) {
                if (Inventory.contains("Grain")) {
                    Utility.cooksAssistantWindmillStage++;
                }
                else {
                    return Utility.interactWithGONotInAreaMultiple("Wheat", "Pick", GRAIN_AREA, Utility.CURRENT_GO_NULL_NOT_EXIST, 1, Utility.secureRandom.nextInt(5000 - 3000 + 1) + 3000, 300);
                }
            }
            else if (Utility.cooksAssistantWindmillStage > 0) {
                if (HOPPER_AREA.contains(Players.getLocal())) {
                    if (Utility.cooksAssistantWindmillStage == 1) {
                        if (Utility.currentGO == null || !Utility.currentGO.exists() || !Utility.currentGO.getName().equals("Hopper")) {
                            Utility.currentGO = GameObjects.closest("Hopper");
                        }
                        else if (Utility.currentGO.interact("Fill")) {
                            Sleep.sleepUntil(() -> Utility.cooksAssistantWindmillStage == 2, Utility.secureRandom.nextInt(10000 - 3000 + 1) + 3000, 200);
                        }
                    }
                    else if (Utility.cooksAssistantWindmillStage == 2) {
                        if (Utility.currentGO == null || !Utility.currentGO.exists() || !Utility.currentGO.getName().equals("Hopper controls")) {
                            Utility.currentGO = GameObjects.closest("Hopper controls");
                        }
                        else if (Utility.currentGO.interact("Operate")) {
                            Sleep.sleepUntil(() -> PlayerSettings.getBitValue(4920) > 0, Utility.secureRandom.nextInt(10000 - 3000 + 1) + 3000, 200);
                        }
                    }
                }
                else {
                    return Utility.walkToArea(HOPPER_AREA);
                }
            }
        }
        else if (Utility.questOrder[Utility.questOrderI] == 5) { // Bucket
            if (Inventory.contains("Bucket", "Bucket of milk")) {
                Utility.questOrderI++;
            }
            else {
                return Utility.interactWithGINotInAreaSingle("Bucket", "Take", BUCKET_AREA, () -> Inventory.contains("Bucket"));
            }
        }
        else if (Utility.questOrder[Utility.questOrderI] == 6) { // Pot
            if (Inventory.contains("Pot", "Pot of flour")) {
                Utility.questOrderI++;
            }
            else {
                return Utility.interactWithGINotInAreaSingle("Pot", "Take", COOK_AREA, () -> Inventory.contains("Pot"));
            }
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
