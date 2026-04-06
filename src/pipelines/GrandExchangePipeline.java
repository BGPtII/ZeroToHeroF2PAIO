package pipelines;

import data.global.ScriptData;
import framework.LoopInterceptor;
import framework.Pipeline;
import loopinterceptors.BuyItemsLI;
import loopinterceptors.ContinueDialogueLI;
import loopinterceptors.DialogueOptionsLI;
import loopinterceptors.SellItemsLI;

public class GrandExchangePipeline extends Pipeline {

    public GrandExchangePipeline(ContinueDialogueLI continueDialogueLI, DialogueOptionsLI dialogueOptionsLI, BuyItemsLI buyItemsLI) {
        super(null);
        LoopInterceptor[] loopInterceptorArr = new LoopInterceptor[3];
        byte size = 0;
        loopInterceptorArr[size++] = continueDialogueLI;
        loopInterceptorArr[size++] = dialogueOptionsLI;
        loopInterceptorArr[size] = buyItemsLI;
        setLoopInterceptors(loopInterceptorArr);
    }

    public GrandExchangePipeline(ContinueDialogueLI continueDialogueLI, DialogueOptionsLI dialogueOptionsLI, SellItemsLI sellItemsLI) {
        super(null);
        LoopInterceptor[] loopInterceptorArr = new LoopInterceptor[3];
        byte size = 0;
        loopInterceptorArr[size++] = continueDialogueLI;
        loopInterceptorArr[size++] = dialogueOptionsLI;
        loopInterceptorArr[size] = sellItemsLI;
        setLoopInterceptors(loopInterceptorArr);
    }

    @Override
    public void shuffleLoopInterceptors() {
        for (int i = loopInterceptors.length - 1; i > 0; i--) {
            int j = ScriptData.SECURE_RANDOM.nextInt(i) + 1;
            LoopInterceptor tmp = loopInterceptors[i];
            loopInterceptors[i] = loopInterceptors[j];
            loopInterceptors[j] = tmp;
        }
    }

}
