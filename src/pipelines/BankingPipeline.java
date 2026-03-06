package pipelines;

import data.global.ScriptData;
import framework.LoopInterceptor;
import framework.Pipeline;
import org.dreambot.api.utilities.Logger;

public class BankingPipeline extends Pipeline {

    private byte returnToI; // Pipeline index to return to

    public void setReturnToI(byte i) {
        returnToI = i;
    }

    public BankingPipeline(LoopInterceptor[] loopInterceptors) {
        super(loopInterceptors);
    }

    @Override
    public void shuffleLoopInterceptors() {
        for (int i = loopInterceptors.length - 1; i > 1; i--) { // Don't include 0
            int j = 1 + ScriptData.SECURE_RANDOM.nextInt(i);
            LoopInterceptor tmp = loopInterceptors[i];
            loopInterceptors[i] = loopInterceptors[j];
            loopInterceptors[j] = tmp;
        }
    }

    @Override
    public int run() {
        if (loopInterceptors[currentLoopInterceptorI].shouldHandle()) {
            return loopInterceptors[currentLoopInterceptorI].handle();
        }
        currentLoopInterceptorI++;
        if (currentLoopInterceptorI >= loopInterceptors.length) {
            currentLoopInterceptorI = 0;
            ScriptData.currentPipelineI = returnToI;
            Logger.log("Done bankingPipeline (34), going back to currentPipelineI (" + ScriptData.currentPipelineI + ")");
        }
        return 0;
    }
}
