package pipelines;

import data.global.ScriptData;
import framework.LoopInterceptor;
import framework.Pipeline;
import loopinterceptors.*;
import org.dreambot.api.utilities.Logger;

import java.util.Arrays;

public class OneAreaPipeline extends Pipeline {

    public OneAreaPipeline(TaskTimerFinishedLI taskTimerFinishedLI, CheckLoadOutLI checkLoadOutLI, WalkToCurrentAreaLI walkToCurrentAreaLI, ContinueDialogueLI continueDialogueLI, DialogueOptionsLI dialogueOptionsLI, LoopInterceptor... loopInterceptors) {
        super(null);
        LoopInterceptor[] loopInterceptorArr = new LoopInterceptor[loopInterceptors.length + 5];
        byte size = 0;
        loopInterceptorArr[size++] = taskTimerFinishedLI;
        loopInterceptorArr[size++] = checkLoadOutLI;
        loopInterceptorArr[size++] = continueDialogueLI;
        loopInterceptorArr[size++] = dialogueOptionsLI;
        loopInterceptorArr[size++] = walkToCurrentAreaLI;
        for (LoopInterceptor loopInterceptor : loopInterceptors) {
            loopInterceptorArr[size++] = loopInterceptor;
        }
        setLoopInterceptors(loopInterceptorArr);
    }

    @Override
    public void shuffleLoopInterceptors() {
        int continueDialogueI = 3;
        int dialogueOptionsLI = 4;
        for (int i = loopInterceptors.length - 1; i > 2; i--) {
            int j = ScriptData.SECURE_RANDOM.nextInt(i) + 1;
            if (i == continueDialogueI) {
                continueDialogueI = j;
            }
            else if (j == continueDialogueI) {
                continueDialogueI = i;
            }
            if (i == dialogueOptionsLI) {
                dialogueOptionsLI = j;
            }
            else if (j == dialogueOptionsLI) {
                dialogueOptionsLI = i;
            }
            LoopInterceptor tmp = loopInterceptors[i];
            loopInterceptors[i] = loopInterceptors[j];
            loopInterceptors[j] = tmp;
        }

        int j = ScriptData.SECURE_RANDOM.nextInt(loopInterceptors.length - 1) + 1;
        LoopInterceptor tmp = loopInterceptors[continueDialogueI];
        loopInterceptors[continueDialogueI] = loopInterceptors[j];
        loopInterceptors[j] = tmp;

        j = ScriptData.SECURE_RANDOM.nextInt(loopInterceptors.length - 1) + 1;
        tmp = loopInterceptors[dialogueOptionsLI];
        loopInterceptors[dialogueOptionsLI] = loopInterceptors[j];
        loopInterceptors[j] = tmp;

        Logger.log("Shuffled loopInterceptors: " + Arrays.toString(loopInterceptors));
    }

}
