package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.dialogues.Dialogues;

public class HandleDialoguePiratesTreasureLI extends CustomDialogueLI {

    public HandleDialoguePiratesTreasureLI() {
        super();
    }

    @Override
    public int handle() {
        if (Dialogues.isProcessing()) {
            return ScriptData.returnMSNormal();
        }
        else if (Dialogues.canContinue()) {
            String npcDialogue = Dialogues.getNPCDialogue();
            if (Dialogues.continueDialogue()) {
                if (npcDialogue.startsWith("You wouldn't believe the demand for bananas")) {
                    ScriptData.questOrderI = 2;
                }
            }
        }
        else if (Dialogues.areOptionsAvailable()) {
            Dialogues.chooseFirstOption(ScriptData.dialogueOpts);
        }
        return ScriptData.returnMSNormal();
    }

}
