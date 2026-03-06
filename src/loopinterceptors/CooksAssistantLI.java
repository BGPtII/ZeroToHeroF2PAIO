package loopinterceptors;

import framework.LoopInterceptor;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Sleep;

/**
 * - eggArea = currentArea
 * - dairyCowArea = currentArea2
 */
public class CooksAssistantLI extends LoopInterceptor {

    private final Area COOK_AREA = new Area(3205, 3216, 3211, 3212);
    private final Area BUCKET_AREA = new Area(3212, 9625, 3216, 9622);
    private final Area GRAIN_AREA = new Area(3154, 3305, 3163, 3290);
    private final Area HOPPER_AREA = new Area(3164, 3309, 3169, 3304, 2);
    private final Area FLOUR_BIN_AREA = new Area(3164, 3309, 3169, 3304, 0);

    public CooksAssistantLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }
    
    @Override
    public int handle() {
        switch (ScriptData.questOrder[ScriptData.questOrderI]) {
            case 0:
                if (FreeQuest.COOKS_ASSISTANT.isStarted()) {
                    ScriptData.questOrderI++;
                }
                else {
                    if (ScriptData.interactWithNPC(4626, COOK_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return ScriptData.returnMSNormal();
                    }
                }
                return ScriptData.returnMSFast();
            case 1:
                if (Inventory.contains(1944)) { // Egg
                    ScriptData.questOrderI++;
                }
                else {
                    if (ScriptData.interactWithGroundItemMultiple(1944, ScriptData.currentArea,  "Take")) {
                        return ScriptData.returnMSNormal();
                    }
                }
                return ScriptData.returnMSFast();
            case 2:
                if (Inventory.contains(1927)) { // Bucket of milk
                    ScriptData.questOrderI++;
                }
                else {
                    if (ScriptData.interactWithGameObjectMultiple(8689, ScriptData.currentArea2, "Milk")) {
                        return ScriptData.returnMSNormal();
                    }
                }
                return ScriptData.returnMSFast();
            case 3: // Pot of flour - Pre-Grain in Hopper
                if (Inventory.contains(1933)) { // Pot of flour
                    ScriptData.questOrderI++;
                }
                else if (Inventory.contains(1947)) { // Grain
                    if (!HOPPER_AREA.contains(Players.getLocal())) {
                        if (ScriptData.walkToArea(HOPPER_AREA)) {
                            return ScriptData.returnMSNormal();
                        }
                    }
                    else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 24961) {
                        ScriptData.currentGameObject = GameObjects.closest(24961); // Hopper
                    }
                    else if (ScriptData.currentGameObject.interact("Fill")) {
                        Sleep.sleepUntil(() -> ScriptData.questOrderI == 4, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                        return ScriptData.returnMSNormal();
                    }
                }
                else if (ScriptData.interactWithGameObjectMultiple("Wheat", GRAIN_AREA, "Pick")) { // Different IDs for Wheat, use Name
                    return ScriptData.returnMSNormal();
                }
                return ScriptData.returnMSFast();
            case 4: // Pot of flour - Post-Grain in Hopper
                if (Inventory.contains(1933)) { // Pot of flour
                    ScriptData.questOrderI++;
                }
                else if (PlayerSettings.getBitValue(4920) > 0) { // Flour bin has Flour
                    if (ScriptData.interactWithGameObjectSingle(1781, FLOUR_BIN_AREA, "Empty", () -> Inventory.contains(1933))) { // Flour bin#Full
                        return ScriptData.returnMSNormal();
                    }
                    return ScriptData.returnMSFast();
                }
                else if (!HOPPER_AREA.contains(Players.getLocal())) {
                    if (ScriptData.walkToArea(HOPPER_AREA)) {
                        return ScriptData.returnMSNormal();
                    }
                }
                else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 24964) {
                    ScriptData.currentGameObject = GameObjects.closest(24964); // Hopper
                    return ScriptData.returnMSFast();
                }
                else if (ScriptData.currentGameObject.interact("Operate")) {
                    Sleep.sleepUntil(() -> PlayerSettings.getBitValue(4920) != 0, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                }
                break;
            case 5: // Finish quest
                if (ScriptData.interactWithNPC(4626, COOK_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                return ScriptData.returnMSNormal();
            case 6:
                if (Inventory.contains(1925, 1927)) { // Bucket, Bucket of milk
                    ScriptData.questOrderI++;
                }
                else {
                    if (ScriptData.interactWithGroundItemSingle(1925, BUCKET_AREA, "Take")) {
                        return ScriptData.returnMSNormal();
                    }
                    return ScriptData.returnMSFast();
                }
                break;
            case 7:
                if (Inventory.contains(1931, 1933)) { // Pot, Pot of flour
                    ScriptData.questOrderI++;
                }
                else {
                    if (ScriptData.interactWithGroundItemSingle(1931, COOK_AREA, "Take")) {
                        return ScriptData.returnMSNormal();
                    }
                    return ScriptData.returnMSFast();
                }
                break;
        }

        return ScriptData.returnMSNormal();
    }

}
