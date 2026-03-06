package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public class SmithingLI extends LoopInterceptor {

    private final Condition DONE_PROCESSING_SMITH = () -> ScriptData.NOT_HANDLE_LOAD_OUT.verify() || Dialogues.canContinue() || Dialogues.areOptionsAvailable(); // questOrderI used for barsReq for selectedSmithEqp

    public SmithingLI() {
        super(() -> Widgets.isVisible(312, 0));
    }

    @Override
    public int handle() {
        WidgetChild[] opts = new WidgetChild[33 - 9];
        byte optsSize = 0;
        for (int i = 9; i < 33; i++) {
            WidgetChild widgetChild = Widgets.get(312, i);
            if (widgetChild != null && !widgetChild.getName().contains("Members")) {
                WidgetChild[] widgetChildren = widgetChild.getChildren();
                if (widgetChildren.length > 0 && widgetChildren[1].getTextColor() != 0) {
                    opts[optsSize++] = widgetChild;
                }
            }
        }
        WidgetChild randOpt = opts[ScriptData.SECURE_RANDOM.nextInt(optsSize)];
        WidgetChild[] widgetChildren = randOpt.getChildren();
        ScriptData.questOrderI = Byte.parseByte(widgetChildren[2].getText().split(" ")[0]);
        if (randOpt.interact()) {
            Sleep.sleepUntil(DONE_PROCESSING_SMITH, ScriptData.SECURE_RANDOM.nextInt(180000 - 120000 + 1) + 120000, 300);
        }
        return ScriptData.returnMSFast();
    }

}
