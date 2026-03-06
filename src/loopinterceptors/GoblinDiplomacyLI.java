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
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

public class GoblinDiplomacyLI extends LoopInterceptor {

    private final Area REDBERRY_BUSH_AREA = new Area(3262, 3375, 3279, 3364);
    private final Area WYSON_THE_GARDENER_AREA = new Area(3022, 3383, 3029, 3375);
    private final Area AGGIE_AREA = new Area(3083, 3261, 3088, 3256);
    private final Area GOBLIN_MAIL_CRATE_S = new Area(2952, 3498, 2959, 3496, 2);
    private final Area S_CRATE_LADDER = new Area(2952, 3499, 2958, 3492);
    private final Area GOBLIN_MAIL_CRATE_W = new Area(2951, 3508, 2952, 3507);
    private final Area GOBLIN_MAIL_CRATE_N = new Area(2959, 3515, 2961, 3514);
    private final Area GOBLIN_GENERALS_AREA = new Area(2954, 3513, 2960, 3510);

    public GoblinDiplomacyLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    @Override
    public int handle() {
        switch (ScriptData.questOrder[ScriptData.questOrderI]) {
            case 0:
                if (Inventory.count(1951) == 3 || Inventory.contains(1763, 1769, 286)) { // Redberries, Red dye, Orange dye, Orange goblin mail
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (!REDBERRY_BUSH_AREA.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(REDBERRY_BUSH_AREA)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists()) {
                    List<GameObject> opts = GameObjects.all(23628, 23629);
                    if (!opts.isEmpty()) {
                        ScriptData.currentGameObject = opts.get(ScriptData.SECURE_RANDOM.nextInt(opts.size()));
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.currentGameObject.interact()) {
                    Sleep.sleepUntil(ScriptData.CURRENT_GAME_OBJECT_NULL_NOT_EXISTS, ScriptData.SECURE_RANDOM.nextInt(10000 - 4000 + 1) + 4000, 300);
                }
                break;
            case 1:
                if (Inventory.count(1793) == 2 || Inventory.contains(1767, 287)) { // Woad leaf, Blue dye, Blue goblin mail
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (!ScriptData.interactWithNPC(5422, WYSON_THE_GARDENER_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 2:
                if (Inventory.count(1957) == 2 || Inventory.contains(1765, 1769, 286)) { // Onion, Yellow dye, Orange dye, Orange goblin mail
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (!ScriptData.interactWithGameObjectMultiple(3366, ScriptData.currentArea, "Pick")) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 3:
            case 4:
            case 5:
                if ((ScriptData.questOrder[ScriptData.questOrderI] == 3 && Inventory.contains(1763, 1769, 286)) // Red dye, Orange dye, Orange goblin mail
                        || (ScriptData.questOrder[ScriptData.questOrderI] == 4 && Inventory.contains(1765, 1769, 286)) // Yellow dye, Orange dye, Orange goblin mail
                        || (ScriptData.questOrder[ScriptData.questOrderI] == 5) && Inventory.contains(1767, 287)) {
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (!AGGIE_AREA.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(AGGIE_AREA)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if ((ScriptData.currentNPC = NPCs.closest("Aggie")) != null && ((ScriptData.currentNPC.hasAction("Dyes") && ScriptData.currentNPC.interact("Dyes"))
                        || (ScriptData.currentNPC.hasAction("Talk-to") && ScriptData.currentNPC.interact("Talk-to")))) {
                    Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(8000 - 5000 + 1) + 5000, 300);
                }
                break;
            case 6:
                if ((PlayerSettings.getConfig(62) >> 8 & 1) == 1) {
                    if (GOBLIN_MAIL_CRATE_S.contains(Players.getLocal())) {
                        if ((ScriptData.currentGameObject = GameObjects.closest("Ladder")) != null) {
                            if (ScriptData.currentGameObject.interact()) {
                                Sleep.sleepUntil(() -> !GOBLIN_MAIL_CRATE_S.contains(Players.getLocal()), ScriptData.SECURE_RANDOM.nextInt(20000 - 10000 + 1) + 10000, 300);
                            }
                        }
                    }
                    else {
                        ScriptData.questOrderI++;
                        return ScriptData.returnMSFast();
                    }
                }
                else if (GOBLIN_MAIL_CRATE_S.contains(Players.getLocal())) {
                    if (!ScriptData.interactWithGameObjectSingle(16561, GOBLIN_MAIL_CRATE_S, "Search", ScriptData.IN_DIALOGUE)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (S_CRATE_LADDER.contains(Players.getLocal())) {
                    if ((ScriptData.currentGameObject = GameObjects.closest("Ladder")) != null) {
                        if (ScriptData.currentGameObject.interact()) {
                            Sleep.sleepUntil(() -> GOBLIN_MAIL_CRATE_S.contains(Players.getLocal()), ScriptData.SECURE_RANDOM.nextInt(20000 - 10000 + 1) + 10000, 300);
                        }
                    }
                }
                else if (!ScriptData.walkToArea(S_CRATE_LADDER)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 7:
                if ((PlayerSettings.getConfig(62) >> 7 & 1) == 1) {
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (!ScriptData.interactWithGameObjectSingle(16560, GOBLIN_MAIL_CRATE_W, "Search", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 8:
                if ((PlayerSettings.getConfig(62) >> 6 & 1) == 1) {
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (!ScriptData.interactWithGameObjectSingle(16559, GOBLIN_MAIL_CRATE_N, "Search", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 9:
                if (Inventory.contains(1769, 286)) { // Orange dye, Orange goblin mail
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (ScriptData.SECURE_RANDOM.nextInt(2) == 1) {
                    Inventory.combine(1763, 1765); // Red dye, Yellow dye
                }
                else {
                    Inventory.combine(1765, 1763);
                }
                break;
            case 10:
                if (Inventory.contains(286)) {
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (ScriptData.SECURE_RANDOM.nextInt(2) == 1) {
                    Inventory.combine(1769, 288); // Orange dye, Goblin mail
                }
                else {
                    Inventory.combine(288, 1769);
                }
                break;
            case 11:
                if (!Inventory.contains(286)) { // Orange goblin mail
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (ScriptData.interactWithNPC(ScriptData.SECURE_RANDOM.nextInt(2) == 1 ? 670 : 669, GOBLIN_GENERALS_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // General Wartface, General Bentnoze
                    return ScriptData.returnMSFast();
                }
                break;
            case 12:
                if (Inventory.contains(287)) { // Blue goblin mail
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (ScriptData.SECURE_RANDOM.nextInt(2) == 1) {
                    Inventory.combine(1767, 288); // Blue dye, Goblin mail
                }
                else {
                    Inventory.combine(288, 1767);
                }
                break;
            case 13:
                if (!Inventory.contains(287)) {
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (ScriptData.interactWithNPC(ScriptData.SECURE_RANDOM.nextInt(2) == 1 ? 670 : 669, GOBLIN_GENERALS_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // General Wartface, General Bentnoze
                    return ScriptData.returnMSFast();
                }
                break;
            case 14:
                if (ScriptData.interactWithNPC(ScriptData.SECURE_RANDOM.nextInt(2) == 1 ? 670 : 669, GOBLIN_GENERALS_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // General Wartface, General Bentnoze
                return ScriptData.returnMSFast();
                }
                break;
        }

        return 0;
    }

}
