package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;

public class FinishQuestXMarksTheSpotLI extends LoopInterceptor {

    private int lampSkillOpt;

    public FinishQuestXMarksTheSpotLI() {
        super(() -> ScriptData.currentFreeQuest.isFinished());
    }

    @Override
    public int handle() {
        if (FreeQuest.X_MARKS_THE_SPOT.isFinished()) {
            if (Widgets.isVisible(153, 16)) {
                if (ScriptData.closeQuestCompletionWidget()) {
                    return ScriptData.returnMSNormal();
                }
            }
            else if (Inventory.contains(23072)) { // Antique lamp
                if (Widgets.isVisible(240, 2)) { // Skill Range: 240, 2 - 240, 25
                    if (lampSkillOpt == 0) {
                        lampSkillOpt = new int[] { 2, 3, 4, 5, 6, 8, 9, 13, 17, 18, 19, 20, 21, 22 }[ScriptData.SECURE_RANDOM.nextInt((FreeQuest.RUNE_MYSTERIES.isFinished()) ? 15 : 14)]; // 14 - Runecraft
                        return ScriptData.returnMSFast();
                    }
                    if ((ScriptData.currentWidgetChild = Widgets.get(240, 27, 9)) != null // "Confirm" - Text
                            && ScriptData.currentWidgetChild.getText().length() == 7 // "Confirm"
                            && (ScriptData.currentWidgetChild = Widgets.get(240, lampSkillOpt)) != null) {
                        ScriptData.currentWidgetChild.interact();
                    }
                    else if ((ScriptData.currentWidgetChild = Widgets.get(240, 27)) != null) { // "Confirm" / "Confirm: <skill>"
                        ScriptData.currentWidgetChild.interact();
                    }
                }
                else if (Inventory.interact(23072, "Rub")) {
                    Sleep.sleepUntil(() -> Widgets.isVisible(240, 2), ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    return ScriptData.returnMSNormal();
                }
            }
            else {
                ScriptData.finishProgressionQuestingTask();
                return ScriptData.returnMSFast();
            }
        }
        return ScriptData.returnMSNormal();
    }

}
