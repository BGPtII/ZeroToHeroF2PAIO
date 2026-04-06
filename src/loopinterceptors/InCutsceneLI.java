package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.Client;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;

public class InCutsceneLI extends LoopInterceptor {

    private final Condition DONE = () -> !Client.isInCutscene() || Dialogues.canContinue() || Dialogues.areOptionsAvailable();

    public InCutsceneLI() {
        super(() -> Client.isInCutscene() && !Dialogues.canContinue() && !Dialogues.areOptionsAvailable());
    }

    @Override
    public int handle() {
        Sleep.sleepUntil(DONE, ScriptData.SECURE_RANDOM.nextInt(30000 - 20000 + 1) + 20000, 300);
        return ScriptData.returnMSFast();
    }

}
