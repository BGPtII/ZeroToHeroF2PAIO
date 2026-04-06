package loopinterceptors;

import framework.LoopInterceptor;
import org.dreambot.api.methods.dialogues.Dialogues;

public abstract class CustomDialogueLI extends LoopInterceptor {

    public CustomDialogueLI() {
        super(Dialogues::inDialogue);
    }

    public abstract int handle();

}
