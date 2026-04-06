package loopinterceptors;

import framework.LoopInterceptor;
import org.dreambot.api.utilities.impl.Condition;

public abstract class TaskTimerFinishedLI extends LoopInterceptor {


    public TaskTimerFinishedLI(Condition c) {
        super(c);
    }

    public abstract int handle();

}
