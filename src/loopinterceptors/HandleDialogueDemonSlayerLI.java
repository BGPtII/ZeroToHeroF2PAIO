package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.Client;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Logger;

import java.util.Arrays;

public class HandleDialogueDemonSlayerLI extends LoopInterceptor {

    private String[] incantationOrder;
    private byte incantationOrderI;

    public HandleDialogueDemonSlayerLI() {
        super(Dialogues::inDialogue);
    }

    @Override
    public int handle() {
        if (Dialogues.isProcessing()) {
            return ScriptData.returnMSFast();
        }
        else if (Dialogues.canContinue()) {
            Dialogues.continueDialogue();
        }
        else if (Dialogues.areOptionsAvailable()) {
            if (Client.isDynamicRegion()) {
                if (PlayerSettings.getBitValue(2561) > 0 && incantationOrder == null) {
                    incantationOrder = new String[5];
                    byte j = 0;
                    for (int i = 2562; i < 2567; i++) {
                        switch (PlayerSettings.getBitValue(i)) {
                            case 0:
                                incantationOrder[j++] = "Carlem";
                                break;
                            case 1:
                                incantationOrder[j++] = "Aber";
                                break;
                            case 2:
                                incantationOrder[j++] = "Camerinthum";
                                break;
                            case 3:
                                incantationOrder[j++] = "Purchai";
                                break;
                            case 4:
                                incantationOrder[j++] = "Gabindo";
                                break;
                        }
                    }
                    Logger.log("incantationOrder: " + Arrays.toString(incantationOrder));
                    return ScriptData.returnMSFast();
                }
                else if (!Dialogues.chooseFirstOption(incantationOrder[incantationOrderI])) {
                    incantationOrderI++;
                    return ScriptData.returnMSFast();
                }
            }
            else if (!Dialogues.chooseFirstOption(ScriptData.dialogueOpts)) {
                return ScriptData.returnMSFast();
            }
        }
        return ScriptData.returnMSNormal();
    }

}
