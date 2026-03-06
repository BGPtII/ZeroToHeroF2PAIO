package framework;

import data.global.ScriptData;

public abstract class Pipeline {

    protected final LoopInterceptor[] loopInterceptors;
    protected byte currentLoopInterceptorI;

    public Pipeline(LoopInterceptor[] loopInterceptors) {
        this.loopInterceptors = loopInterceptors;
        currentLoopInterceptorI = 0; // Start at first
    }

    public int run() {
        if (loopInterceptors[currentLoopInterceptorI].shouldHandle()) {
            return loopInterceptors[currentLoopInterceptorI].handle();
        }
        currentLoopInterceptorI++;
        if (currentLoopInterceptorI >= loopInterceptors.length) {
            shuffleLoopInterceptors();
            currentLoopInterceptorI = 0;
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
    }

}
