package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Sleep;

public class VampyreSlayerLI extends LoopInterceptor {

    private final Area MORGAN_AREA = new Area(3096, 3269, 3102, 3266);
    private final Area BLUE_MOON_INN = new Area(3218, 3402, 3227, 3394);
    private final Area COUNT_DRAYNOR_AREA = new Area(3075, 9778, 3080, 9768);
    private final Area GARLIC_AREA = new Area(3096, 3270, 3102, 3266, 1);

    public VampyreSlayerLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    @Override
    public int handle() {
        if (!Inventory.contains(1550)) { // Garlic
            if (GARLIC_AREA.contains(Players.getLocal())) {
                if ((ScriptData.currentGameObject = GameObjects.closest("Cupboard")) != null) {
                    if (ScriptData.currentGameObject.interact()) {
                        if (ScriptData.currentGameObject.hasAction("Search")) {
                            Sleep.sleepUntil(() -> Inventory.contains(1550), ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                        }
                        else {
                            Sleep.sleepUntil(() -> ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists(), ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 500);
                        }
                    }
                }
            }
            else if (!ScriptData.walkToArea(GARLIC_AREA)) {
                return ScriptData.returnMSFast();
            }
        }
        switch (PlayerSettings.getConfig(178)) {
            case 0:
                if (!ScriptData.interactWithNPC(3479, MORGAN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 1:
            case 2:
                if (Inventory.contains(1549)) {
                    if (COUNT_DRAYNOR_AREA.contains(Players.getLocal())) {
                        if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || ScriptData.currentNPC.getId() != 3481) {
                            ScriptData.currentNPC = NPCs.closest(npc -> npc.isInteracting(Players.getLocal()) && npc.getId() == 3481); // Count Draynor
                            if (ScriptData.currentNPC == null) {
                                if ((ScriptData.currentGameObject = GameObjects.closest(46237)) != null) { // Coffin#Closed
                                    if (ScriptData.currentGameObject.interact("Open")) {
                                        Sleep.sleepUntil(() -> (ScriptData.currentNPC = NPCs.closest(npc -> npc.isInteracting(Players.getLocal()) && npc.getId() == 3481)) != null, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                                    }
                                }
                            }
                        }
                        if (!Players.getLocal().isInteracting(ScriptData.currentNPC)) {
                            ScriptData.currentNPC.interact("Attack");
                        }
                    }
                    else if (!ScriptData.walkToArea(COUNT_DRAYNOR_AREA)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (!ScriptData.interactWithNPC(3480, BLUE_MOON_INN, "Talk-to", ScriptData.IN_DIALOGUE)) { // Dr Harlow
                    return ScriptData.returnMSFast();
                }
                break;
        }

        return ScriptData.returnMSNormal();
    }

}
