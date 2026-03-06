package pipelines;

import framework.LoopInterceptor;
import framework.Pipeline;
import loopinterceptors.*;

public class MeleePipeline extends Pipeline {

    private DialogueOptionsLI dialogueOptionsLI;
    private ContinueDialogueLI continueDialogueLI;
    private OpenInventoryLI openInventoryLI;
    private ChangePlayerSetUpLI changePlayerSetUpLI;
    private CheckLoadOutLI checkLoadOutLI;

    public MeleePipeline(DialogueOptionsLI dialogueOptionsLI, ContinueDialogueLI continueDialogueLI, OpenInventoryLI openInventoryLI, ChangePlayerSetUpLI changePlayerSetUpLI, CheckLoadOutLI checkLoadOutLI) {
        super(null);
    }

    @Override
    public void shuffleLoopInterceptors() {
        super.shuffleLoopInterceptors();
    }
}
