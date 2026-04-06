package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.utilities.Logger;

public class ContinueDialogueLI extends LoopInterceptor {

    public ContinueDialogueLI() {
        super(Dialogues::canContinue);
    }

    @Override
    public int handle() {
        Logger.log("Entered ContinueDialogueLI");
        if (ScriptData.rollChance(50)) {
            if (Dialogues.clickContinue()) {
                return ScriptData.returnMSNormal();
            }
        }
        else if (Dialogues.continueDialogue()) {
            return ScriptData.returnMSNormal();
        }
        return ScriptData.returnMSFast();
    }

}
