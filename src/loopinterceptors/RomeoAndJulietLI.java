package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

public class RomeoAndJulietLI extends LoopInterceptor {

    private final Area ROMEO_AREA = new Area(3205, 3437, 3222, 3409);
    private final Area JULIET_AREA = new Area(3155, 3426, 3161, 3425, 1);
    private final Area FATHER_LAWRENCE_AREA = new Area(3252, 3488, 3259, 3479);
    private final Area APOTHECARY_AREA = new Area(3192, 3406, 3198, 3402);
    private final Area CADAVA_BERRY_AREA = new Area(3261, 3375, 3279, 3364);

    public RomeoAndJulietLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    @Override
    public int handle() {
        if (PlayerSettings.getConfig(144) >= ScriptData.questOrderI && PlayerSettings.getConfig(144) < 50 && !Inventory.contains(753)) { // Cadava berries
            if (CADAVA_BERRY_AREA.contains(Players.getLocal())) {
                if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists()) {
                    List<GameObject> opts = GameObjects.all(item -> {
                        int id =  item.getId();
                        return id == 23625 || id == 23626;
                    });
                    if (!opts.isEmpty()) {
                        ScriptData.currentGameObject = opts.get(ScriptData.SECURE_RANDOM.nextInt(opts.size()));
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.currentGameObject.interact("Pick-from")) {
                    Sleep.sleepUntil(() -> ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists(), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                }
            }
            else if (!ScriptData.walkToArea(CADAVA_BERRY_AREA)) {
                return ScriptData.returnMSFast();
            }
            return ScriptData.returnMSNormal();
        }

        switch (PlayerSettings.getConfig(144)) {
            case 0:
            case 20:
            case 60:
                if (!ScriptData.interactWithNPC(5037, ROMEO_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 10:
                if (!ScriptData.interactWithNPC(5035, JULIET_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 30:
                if (!ScriptData.interactWithNPC(5038, FATHER_LAWRENCE_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 40:
                if (!ScriptData.interactWithNPC(5036, APOTHECARY_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 50:
                if (Inventory.contains(756)) { // Cadava potion
                    if (!ScriptData.interactWithNPC(5035, JULIET_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (!ScriptData.interactWithNPC(5036, APOTHECARY_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
        }

        return ScriptData.returnMSNormal();
    }

}
