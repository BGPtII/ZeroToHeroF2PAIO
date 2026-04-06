package pipelines;

import data.global.ScriptData;
import framework.LoopInterceptor;
import framework.Pipeline;
import loopinterceptors.*;

public class QuestPipeline extends Pipeline {

    public QuestPipeline(InCutsceneLI inCutsceneLI, CheckLoadOutLI checkLoadOutLI, ContinueDialogueLI continueDialogueLI, DialogueOptionsLI dialogueOptionsLI, LoopInterceptor... loopInterceptors) {
        super(null);
        LoopInterceptor[] loopInterceptorArr = new LoopInterceptor[loopInterceptors.length + 4];
        byte size = 0;
        loopInterceptorArr[size++] = inCutsceneLI;
        loopInterceptorArr[size++] = checkLoadOutLI;
        loopInterceptorArr[size++] = continueDialogueLI;
        loopInterceptorArr[size++] = dialogueOptionsLI;
        for (LoopInterceptor loopInterceptor : loopInterceptors) {
            loopInterceptorArr[size++] = loopInterceptor;
        }
        setLoopInterceptors(loopInterceptorArr);
    }

    public QuestPipeline(InCutsceneLI inCutsceneLI, CheckLoadOutLI checkLoadOutLI, CustomDialogueLI customDialogueLI, LoopInterceptor... loopInterceptors) {
        super(null);
        LoopInterceptor[] loopInterceptorArr = new LoopInterceptor[loopInterceptors.length + 3];
        byte size = 0;
        loopInterceptorArr[size++] = inCutsceneLI;
        loopInterceptorArr[size++] = checkLoadOutLI;
        loopInterceptorArr[size++] = customDialogueLI;
        for (LoopInterceptor loopInterceptor : loopInterceptors) {
            loopInterceptorArr[size++] = loopInterceptor;
        }
        setLoopInterceptors(loopInterceptorArr);
    }

    @Override
    public void shuffleLoopInterceptors() {
        int continueDialogueI = 1;
        int dialogueOptionsLI = 2;
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
    }

}
