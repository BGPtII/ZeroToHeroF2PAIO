package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Sleep;

public class PrinceAliRescueLI extends LoopInterceptor {

    private final Area CHANCELLOR_HASSAN_AREA = new Area(3298, 3166, 3303, 3159);
    private final Area OSMAN_AREA = new Area(3282, 3185, 3289, 3179);
    private final Area NED_AREA = new Area(3096, 3261, 3100, 3256);
    private final Area AGGIE_AREA = new Area(3083, 3261, 3087, 3256);
    private final Area LEELA_AREA = new Area(3106, 3266, 3115, 3261);
    private final Area DRAYNOR_JAIL = new Area(3121, 3245, 3130, 3240);
    private final Area PRINCE_ALI_CELL = new Area(3121, 3243, 3125, 3240);

    public PrinceAliRescueLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    @Override
    public int handle() {
        switch (PlayerSettings.getConfig(273)) {
            case 0:
                if (!ScriptData.interactWithNPC(4285, CHANCELLOR_HASSAN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 10:
                if (!ScriptData.interactWithNPC(6165, OSMAN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 20:
                if (Inventory.containsAll(2423, 2349)) { // Key print, Bronze bar
                    if (!ScriptData.interactWithNPC(6165, OSMAN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (Inventory.contains(1759)) { // Ball of wool
                    if (!ScriptData.interactWithNPC(4280, NED_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (Inventory.contains(1933)) { // Pot of flour
                    if (!ScriptData.interactWithNPC(4284, AGGIE_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (Inventory.contains(1765)) { // Yellow dye
                    if (ScriptData.SECURE_RANDOM.nextInt(2) == 1) {
                        Inventory.combine(1765, 2421); //Wig#Grey - 2421
                    }
                    else {
                        Inventory.combine(2421, 1765);
                    }
                }
                else if (!Inventory.contains(2423)) {
                    if (!ScriptData.interactWithNPC(11578, DRAYNOR_JAIL, "Talk-to", ScriptData.IN_DIALOGUE)) { // Lady Keli
                        return ScriptData.returnMSFast();
                    }
                }
                else if (!ScriptData.interactWithNPC(4274, LEELA_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 30:
            case 31:
                if (DRAYNOR_JAIL.contains(Players.getLocal())) {
                    if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || ScriptData.currentNPC.getId() != 11577) {
                        ScriptData.currentNPC = NPCs.closest(11577);
                        return ScriptData.returnMSFast();
                    }
                    else if (Inventory.isItemSelected()) {
                        if (ScriptData.currentNPC.interact()) {
                            Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                        }
                    }
                    else {
                        Inventory.use(1917); // Beer
                    }
                }
                else if (!ScriptData.walkToArea(DRAYNOR_JAIL)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 40:
                if (!DRAYNOR_JAIL.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(DRAYNOR_JAIL)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || ScriptData.currentNPC.getId() != 11578) {
                    ScriptData.currentNPC = NPCs.closest(11578);
                    return ScriptData.returnMSFast();
                }
                else if (Inventory.isItemSelected()) {
                    if (ScriptData.currentNPC.interact()) {
                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else {
                    Inventory.use(954); // Rope
                }
                break;
            case 50:
                if (PRINCE_ALI_CELL.contains(Players.getLocal())) {
                    if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || ScriptData.currentNPC.getId() != 11579) {
                        ScriptData.currentNPC = NPCs.closest(11579); // Prince Ali#Imprisoned
                        return ScriptData.returnMSFast();
                    }
                    if (ScriptData.currentNPC.interact("Talk-to")) {
                        Sleep.sleepUntil(Dialogues::inDialogue, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (!DRAYNOR_JAIL.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(DRAYNOR_JAIL)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2881) { // Prison Gate
                    ScriptData.currentGameObject = GameObjects.closest(2881);
                    return ScriptData.returnMSFast();
                }
                else if (ScriptData.currentGameObject.interact()) {
                    Sleep.sleepUntil(() -> PRINCE_ALI_CELL.contains(Players.getLocal()), ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                }
                break;
            case 100:
                if (PRINCE_ALI_CELL.contains(Players.getLocal())) {
                    if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2881) { // Prison Gate
                        ScriptData.currentGameObject = GameObjects.closest(2881);
                        return ScriptData.returnMSFast();
                    }
                    else if (ScriptData.currentGameObject.interact()) {
                        Sleep.sleepUntil(() -> !PRINCE_ALI_CELL.contains(Players.getLocal()), ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (!ScriptData.interactWithNPC(4285, CHANCELLOR_HASSAN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
        }

        return ScriptData.returnMSNormal();
    }

}
