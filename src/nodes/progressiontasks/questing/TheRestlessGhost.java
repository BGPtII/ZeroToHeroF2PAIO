package nodes.progressiontasks.questing;

import framework.Node;
import framework.SCScript;
import data.global.ScriptData;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

public class TheRestlessGhost implements Node {

    private final Area FATHER_AERECK_AREA = new Area(3240, 3215, 3247, 3204);
    private final Area FATHER_URHNEY_AREA = new Area(3144, 3177, 3151, 3173);
    private final Area RESTLESS_GHOST_AREA = new Area(3247, 3195, 3251, 3190);
    private final Area GHOSTS_SKULL_AREA = new Area(3111, 9569, 3121, 9564);

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

        if (Client.isInCutscene()) {
            Logger.log("isInCutscene");
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (FreeQuest.THE_RESTLESS_GHOST.isFinished()) {
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

        switch (PlayerSettings.getConfig(107)) {
            case 0:
                if (ScriptData.interactWithNPC(2812, FATHER_AERECK_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            case 1:
                if (ScriptData.interactWithNPC(923, FATHER_URHNEY_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            case 2:
                if (Inventory.contains(552)) {
                    Inventory.interact(552);
                }
                else if (RESTLESS_GHOST_AREA.contains(Players.getLocal())) {
                    if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || (ScriptData.currentNPC.getId() != 922 && ScriptData.currentNPC.getId() != 15061)) { // Restless ghost
                        ScriptData.currentNPC = NPCs.closest(922);
                        if (ScriptData.currentNPC == null) {
                            if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || (ScriptData.currentGameObject.getId() != 2145 && ScriptData.currentGameObject.getId() != 15061)) {
                                ScriptData.currentGameObject = GameObjects.closest(2145, 15061); // Coffin
                                Logger.log("currentGameObject: " + ScriptData.currentGameObject);
                            }
                            if (ScriptData.currentGameObject != null) {
                                ScriptData.currentGameObject.interact();
                                Sleep.sleepUntil(() -> (ScriptData.currentNPC = NPCs.closest(922)) != null, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                                Logger.log("Interacted with Coffin");
                            }
                        }
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (ScriptData.currentNPC.interact("Talk-to")) {
                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else {
                    if (ScriptData.walkToArea(RESTLESS_GHOST_AREA)) {
                        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                    }
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 3:
            case 4:
                if (Inventory.contains(553)) { // Ghost's skull
                    if (!RESTLESS_GHOST_AREA.contains(Players.getLocal())) {
                        if (ScriptData.walkToArea(RESTLESS_GHOST_AREA)) {
                            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                        }
                    }
                    else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || (ScriptData.currentGameObject.getId() != 2145 && ScriptData.currentGameObject.getId() != 15061)) {
                        ScriptData.currentGameObject = GameObjects.closest(2145, 15061); // Coffin
                    }
                    else if (ScriptData.currentGameObject.interact()) {
                        Sleep.sleepUntil(() -> Dialogues.inDialogue() || ScriptData.currentGameObject == null,SCScript.SECURE_RANDOM.nextInt(10000 - 5000 + 1) + 5000, 300);
                        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                    }
                }
                else {
                    if (!GHOSTS_SKULL_AREA.contains(Players.getLocal())) {
                        if (ScriptData.walkToArea(GHOSTS_SKULL_AREA)) {
                            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                        }
                    }
                    else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2146) {
                        ScriptData.currentGameObject = GameObjects.closest(2146); // Altar with Ghost's skull
                    }
                    else if (ScriptData.currentGameObject.interact()) {
                        Sleep.sleepUntil(() -> Inventory.contains(553), SCScript.SECURE_RANDOM.nextInt(10000 - 5000 + 1) + 5000, 300);
                        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                    }
                }
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
