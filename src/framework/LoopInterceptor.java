package framework;

import org.dreambot.api.utilities.impl.Condition;

public abstract class LoopInterceptor {

    private Condition shouldHandle;

    public LoopInterceptor(Condition c) {
        shouldHandle = c;
    }

    public void setShouldHandle(Condition c) {
        shouldHandle = c;
    }

    public boolean shouldHandle() {
        return shouldHandle.verify();
    }

    public abstract int handle();

}
