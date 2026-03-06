package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.dialogues.Dialogues;

public class DialogueOptionsLI extends LoopInterceptor {

    public DialogueOptionsLI() {
        super(Dialogues::areOptionsAvailable);
    }

    @Override
    public int handle() {
        String[] opts = Dialogues.getOptions();
        if (opts != null) {
            boolean containsOpt = false;
            if (ScriptData.dialogueOpts != null) {
                for (String dialogueOpt : ScriptData.dialogueOpts) {
                    for (String opt : opts) {
                        if (opt.equals(dialogueOpt)) {
                            containsOpt = true;
                            break;
                        }
                    }
                    if (containsOpt) {
                        break;
                    }
                }
            }
            if (!containsOpt) {
                Dialogues.chooseFirstOption(opts[ScriptData.SECURE_RANDOM.nextInt(opts.length)]);
            }
            else {
                Dialogues.chooseFirstOptionContaining(ScriptData.dialogueOpts);
            }
        }
        return ScriptData.returnMSNormal();
    }

}
