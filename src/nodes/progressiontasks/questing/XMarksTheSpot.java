package nodes.progressiontasks.questing;

import framework.Node;
import framework.SCScript;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

public class XMarksTheSpot implements Node {

    private final Area SHEARED_RAM = new Area(3226, 3242, 3233, 3239);
    private final Tile FIRST_CLUE_DIG = new Tile(3230, 3209, 0);
    private final Tile SECOND_CLUE_DIG = new Tile(3203, 3212, 0);
    private final Tile THIRD_CLUE_DIG = new Tile(3108, 3264, 0);
    private final Tile FOURTH_CLUE_DIG = new Tile(3077, 3260, 0);
    private final Area VEOS_PORT_SARIM = new Area(3051, 3249, 3055, 3245);
    private int lampSkillOpt;

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

        if (FreeQuest.X_MARKS_THE_SPOT.isFinished()) {
            if (Widgets.isVisible(153, 16)) {
                if (ScriptData.closeQuestCompletionWidget()) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
            }
            else if (Inventory.contains(23072)) { // Antique lamp
                if (Widgets.isVisible(240, 2)) { // Skill Range: 240, 2 - 240, 25
                    if (lampSkillOpt == 0) {
                        lampSkillOpt = new int[] { 2, 3, 4, 5, 6, 8, 9, 13, 17, 18, 19, 20, 21, 22 }[SCScript.SECURE_RANDOM.nextInt((FreeQuest.RUNE_MYSTERIES.isFinished()) ? 15 : 14)]; // 14 - Runecraft
                        Logger.log("lampSkillOpt: " + lampSkillOpt);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    if ((ScriptData.currentWidgetChild = Widgets.get(240, 27, 9)) != null // "Confirm" - Text
                            && ScriptData.currentWidgetChild.getText().length() == 7 // "Confirm"
                            && (ScriptData.currentWidgetChild = Widgets.get(240, lampSkillOpt)) != null) {
                        ScriptData.currentWidgetChild.interact();
                        Logger.log("Tried to interact with lampSkillOpt widget, lampSkillOpt: " + lampSkillOpt);
                    }
                    else if ((ScriptData.currentWidgetChild = Widgets.get(240, 27)) != null) { // "Confirm" / "Confirm: <skill>"
                        ScriptData.currentWidgetChild.interact();
                        Logger.log("Interacted with Confirm widget");
                    }
                }
                else if (Inventory.interact(23072, "Rub")) {
                    Sleep.sleepUntil(() -> Widgets.isVisible(240, 2), SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                }
            }
            else {
                ScriptData.finishProgressionQuestingTask();
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        switch (PlayerSettings.getBitValue(8063)) {
            case 0:
            case 1:
                if (!ScriptData.interactWithNPC(8632, SHEARED_RAM, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 2:
                if (FIRST_CLUE_DIG.equals(Players.getLocal().getTile())) {
                    if (Inventory.interact( 952, "Dig")) {
                        Sleep.sleepUntil(() -> PlayerSettings.getBitValue(8063) != 2, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (ScriptData.walkToTile(FIRST_CLUE_DIG)) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            case 3:
                if (SECOND_CLUE_DIG.equals(Players.getLocal().getTile())) {
                    if (Inventory.interact( 952, "Dig")) {
                        Sleep.sleepUntil(() -> PlayerSettings.getBitValue(8063) != 3, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (ScriptData.walkToTile(SECOND_CLUE_DIG)) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
                break;
            case 4:
                if (THIRD_CLUE_DIG.equals(Players.getLocal().getTile())) {
                    if (Inventory.interact("Spade", "Dig")) {
                        Sleep.sleepUntil(() -> PlayerSettings.getBitValue(8063) != 4, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (ScriptData.walkToTile(THIRD_CLUE_DIG)) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
                break;
            case 5:
                if (FOURTH_CLUE_DIG.equals(Players.getLocal().getTile())) {
                    if (Inventory.interact("Spade", "Dig")) {
                        Sleep.sleepUntil(() -> PlayerSettings.getBitValue(8063) != 5, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (ScriptData.walkToTile(FOURTH_CLUE_DIG)) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
                break;
            case 6:
            case 7:
                if (!ScriptData.interactWithNPC(1063, VEOS_PORT_SARIM, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
