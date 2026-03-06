package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Sleep;

/**
 * - Black Knight's Fortress contained within one region
 */
public class BlackKnightsFortressLI extends LoopInterceptor {

    private final Area SIR_AMIK_VARZE_AREA = new Area(2957, 3341, 2963, 3336, 2);
    private final Area GRILL_AREA = new Area(3025, 3506, 3025, 3510);
    private final Area HOLE_AREA = new Area(3027, 3509, 3030, 3505, 1);
    private final Area BK_DOOR_ACCESS_AREA = new Area(3015, 3516, 3019, 3515);

    public BlackKnightsFortressLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    @Override
    public int handle() {
        switch (PlayerSettings.getConfig(130)) {
            case 0:
            case 3:
                if (!ScriptData.interactWithNPC(1867, SIR_AMIK_VARZE_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 1:
                if (Inventory.contains(9589)) { // Dossier
                    if (Inventory.interact(9589, "Read")) {
                        Sleep.sleepUntil(() -> !Inventory.contains(9589), ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (GRILL_AREA.contains(Players.getLocal())) {
                    if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2342) {
                        ScriptData.currentGameObject = GameObjects.closest(2342); // Grill
                        return ScriptData.returnMSFast();
                    }
                    else if (ScriptData.currentGameObject.interact("Listen-at")) {
                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (!ScriptData.walkToArea(GRILL_AREA)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 2:
                if (HOLE_AREA.contains(Players.getLocal())) {
                    if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 696) { // Hole
                        ScriptData.currentGameObject = GameObjects.closest(696);
                        return ScriptData.returnMSFast();
                    }
                    else if (Inventory.isItemSelected()) {
                        if (ScriptData.currentGameObject.interact()) {
                            Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                        }
                    }
                    else {
                        Inventory.use(1965); // Cabbage
                    }
                }
                else if (BK_DOOR_ACCESS_AREA.contains(Players.getLocal()) && (ScriptData.currentCharacter = Players.getLocal().getCharacterInteractingWithMe()) != null && ScriptData.currentCharacter.hasAction("Attack")) {
                    if (ScriptData.currentCharacter.getHealthPercent() > 0 && !Players.getLocal().isInteracting(ScriptData.currentCharacter)) {
                        ScriptData.currentCharacter.interact("Attack");
                    }
                }
                else if (!ScriptData.walkToArea(HOLE_AREA)) {
                    return ScriptData.returnMSFast();
                }
                break;
        }

        return ScriptData.returnMSFast();
    }

}
