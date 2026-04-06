package framework;

import data.global.ScriptData;
import org.dreambot.api.utilities.Logger;

import java.util.Arrays;

public abstract class Pipeline {

    protected LoopInterceptor[] loopInterceptors;

    public Pipeline(LoopInterceptor[] loopInterceptors) {
        this.loopInterceptors = loopInterceptors;
    }

    public int run() {
        for (LoopInterceptor loopInterceptor : loopInterceptors) {
            if (loopInterceptor.shouldHandle()) {
                return loopInterceptor.handle();
            }
        }
        return 0;
    }

    public void shuffleLoopInterceptors() { // Fisher–Yates shuffle
        for (int i = loopInterceptors.length - 1; i > 0; i--) {
            int j = ScriptData.SECURE_RANDOM.nextInt(i + 1);
            LoopInterceptor tmp = loopInterceptors[i];
            loopInterceptors[i] = loopInterceptors[j];
            loopInterceptors[j] = tmp;
        }
        Logger.log("Shuffled loopInterceptors: " + Arrays.toString(loopInterceptors));
    }

    public void setLoopInterceptors(LoopInterceptor[] loopInterceptors) {
        this.loopInterceptors = loopInterceptors;
    }

}
