package loopinterceptors;

import framework.LoopInterceptor;
import data.global.ScriptData;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;

public class RuneMysteriesLI extends LoopInterceptor {

    private final Area DUKE_HORACIO_AREA = new Area(3208, 3225, 3212, 3218, 1);
    private final Area ARCHMAGE_SEDRIDOR_AREA = new Area(3096, 9574, 3106, 9569);
    private final Area AUBURY_AREA = new Area(3250, 3403, 3255, 3399);

    public RuneMysteriesLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    @Override
    public int handle() {
        switch (PlayerSettings.getConfig(63)) {
            case 0:
                if (!ScriptData.interactWithNPC(815, DUKE_HORACIO_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 1:
            case 2:
            case 5:
                if (!ScriptData.interactWithNPC(5034, ARCHMAGE_SEDRIDOR_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 3:
            case 4:
                if (!ScriptData.interactWithNPC(2886, AUBURY_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
        }

        return ScriptData.returnMSNormal();
    }

}
