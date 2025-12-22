package nodes.progressiontasks.questing;

import framework.Node;
import framework.SCScript;
import data.global.ScriptData;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;

public class RuneMysteries implements Node {

    private final Area DUKE_HORACIO_AREA = new Area(3208, 3225, 3212, 3218, 1);
    private final Area ARCHMAGE_SEDRIDOR_AREA = new Area(3096, 9574, 3106, 9569);
    private final Area AUBURY_AREA = new Area(3250, 3403, 3255, 3399);

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

        if (FreeQuest.RUNE_MYSTERIES.isFinished()) {
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

        switch (PlayerSettings.getConfig(63)) {
            case 0:
                if (!ScriptData.interactWithNPC(815, DUKE_HORACIO_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
            case 1:
            case 2:
            case 5:
                if (!ScriptData.interactWithNPC(5034, ARCHMAGE_SEDRIDOR_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
            case 3:
            case 4:
                if (!ScriptData.interactWithNPC(5034, AUBURY_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
