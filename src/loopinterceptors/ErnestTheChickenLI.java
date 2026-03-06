package loopinterceptors;

import framework.LoopInterceptor;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

public class ErnestTheChickenLI extends LoopInterceptor {

    private final Area VERONICA_AREA = new Area(3108, 3332, 3110, 3329);
    private final Area PROFESSOR_ODDENSTEIN_AREA = new Area(3108, 3370, 3112, 3362, 2);
    private final Area COMPOST_HEAP_AREA = new Area(3084, 3363, 3087, 3359);
    private final Area RUBBER_TUBE_AREA = new Area(3108, 3368, 3111, 3366);
    private final Area POISON_AREA = new Area(3097, 3366, 3100, 3364);
    private final Area FISH_FOOD_AREA = new Area(3107, 3361, 3109, 3354, 1);
    private final Area FOUNTAIN_AREA = new Area(3090, 3332, 3085, 3336);
    private final Area OIL_CAN_PUZZLE_AREA = new Area(3118, 9744, 3089, 9767);
    private final Area SPADE_AREA = new Area(3120, 3360, 3126, 3354);

    private final Area BEDROOM = new Area(3091, 3363, 3096, 3354);
    private final Area LIVING_ROOM = new Area(3097, 3363, 3104, 3354);

    private byte leverPuzzleStage;

    public ErnestTheChickenLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    private boolean leverADown() {
        return PlayerSettings.getBitValue(1788) == 1;
    }

    private boolean leverBDown() {
        return PlayerSettings.getBitValue(1789) == 1;
    }

    private boolean leverCDown() {
        return PlayerSettings.getBitValue(1798) == 1;
    }

    private boolean leverDDown() { return PlayerSettings.getBitValue(1802) == 1; }

    private boolean leverEDown() {
        return PlayerSettings.getBitValue(1792) == 1;
    }

    private boolean leverFDown() {
        return PlayerSettings.getBitValue(1795) == 1;
    }

    @Override
    public int handle() {
        switch (ScriptData.questOrder[ScriptData.questOrderI]) {
            case 0: // Start quest
                if (FreeQuest.ERNEST_THE_CHICKEN.isStarted()) {
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (!ScriptData.interactWithNPC(3561, VERONICA_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 1: // Oil can
                if (Inventory.contains(277)) {
                    if (OIL_CAN_PUZZLE_AREA.contains(Players.getLocal())) {
                        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 132) { // Ladder
                            ScriptData.currentGameObject = GameObjects.closest(132);
                            Logger.log("currentGameObject set to Ladder");
                            return ScriptData.returnMSFast();
                        }
                        else if (!ScriptData.currentGameObject.canReach()) {
                            if (!ScriptData.walkToEntity(ScriptData.currentGameObject)) {
                                return ScriptData.returnMSFast();
                            }
                        }
                        else if (ScriptData.currentGameObject.interact()) {
                            Sleep.sleepUntil(() -> BEDROOM.contains(Players.getLocal()), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            return ScriptData.returnMSFast();
                        }
                    }
                    else if (BEDROOM.contains(Players.getLocal())) {
                        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 160) { // Lever
                            ScriptData.currentGameObject = GameObjects.closest(160);
                            return ScriptData.returnMSFast();
                        }
                        else if (ScriptData.currentGameObject.interact()) {
                            Sleep.sleepUntil(() -> LIVING_ROOM.contains(Players.getLocal()), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            return ScriptData.returnMSFast();
                        }
                    }
                    else {
                        ScriptData.questOrderI++;
                        return ScriptData.returnMSFast();
                    }
                }
                else if (OIL_CAN_PUZZLE_AREA.contains(Players.getLocal())) {
                    switch (leverPuzzleStage) {
                        case 0:
                            if (!leverBDown()) {
                                if (!ScriptData.interactWithGameObjectSingle(147, OIL_CAN_PUZZLE_AREA, "Pull", this::leverBDown)) {
                                    return ScriptData.returnMSFast();
                                }
                            }
                            else if (!leverADown()) {
                                if (!ScriptData.interactWithGameObjectSingle(146, OIL_CAN_PUZZLE_AREA, "Pull", this::leverBDown)) {
                                    return ScriptData.returnMSFast();
                                }
                            }
                            else {
                                leverPuzzleStage++;
                            }
                            break;
                        case 1:
                            if (!leverDDown()) {
                                if (!ScriptData.interactWithGameObjectSingle(149, OIL_CAN_PUZZLE_AREA, "Pull", this::leverDDown)) {
                                    return ScriptData.returnMSFast();
                                }
                            }
                            else {
                                leverPuzzleStage++;
                            }
                            break;
                        case 2:
                            if (leverBDown()) {
                                if (!ScriptData.interactWithGameObjectSingle(147, OIL_CAN_PUZZLE_AREA, "Pull", () -> !leverBDown())) {
                                    return ScriptData.returnMSFast();
                                }
                            }
                            else if (leverADown()) {
                                if (!ScriptData.interactWithGameObjectSingle(146, OIL_CAN_PUZZLE_AREA, "Pull", () -> !leverADown())) {
                                    return ScriptData.returnMSFast();
                                }
                            }
                            else {
                                leverPuzzleStage++;
                            }
                            break;
                        case 3:
                            if (!leverEDown()) {
                                if (!ScriptData.interactWithGameObjectSingle(150, OIL_CAN_PUZZLE_AREA, "Pull", this::leverEDown)) {
                                    return ScriptData.returnMSFast();
                                }
                            }
                            else if (!leverFDown()) {
                                if (!ScriptData.interactWithGameObjectSingle(151, OIL_CAN_PUZZLE_AREA, "Pull", this::leverFDown)) {
                                    return ScriptData.returnMSFast();
                                }
                            }
                            else {
                                leverPuzzleStage++;
                            }
                            break;
                        case 4:
                            if (!leverCDown()) {
                                if (!ScriptData.interactWithGameObjectSingle(148, OIL_CAN_PUZZLE_AREA, "Pull", this::leverCDown)) {
                                    return ScriptData.returnMSFast();
                                }
                            }
                            else {
                                leverPuzzleStage++;
                            }
                            break;
                        case 5:
                            if (leverEDown()) {
                                if (!ScriptData.interactWithGameObjectSingle(150, OIL_CAN_PUZZLE_AREA, "Pull", () -> !leverEDown())) {
                                    return ScriptData.returnMSFast();
                                }
                            }
                            else {
                                leverPuzzleStage++;
                            }
                            break;
                        case 6: // Collect oil can
                            if (!ScriptData.interactWithGroundItemSingle(277, OIL_CAN_PUZZLE_AREA, "Take")) {
                                return ScriptData.returnMSFast();
                            }
                    }
                }
                else if (BEDROOM.contains(Players.getLocal())) {
                    if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 133) { // Ladder
                        ScriptData.currentGameObject = GameObjects.closest(133);
                        return ScriptData.returnMSFast();
                    }
                    else if (ScriptData.currentGameObject.interact()) {
                        Sleep.sleepUntil(() -> OIL_CAN_PUZZLE_AREA.contains(Players.getLocal()), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        return ScriptData.returnMSFast();
                    }
                }
                else if (LIVING_ROOM.contains(Players.getLocal())) {
                    if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || !ScriptData.currentGameObject.getName().equals("Bookcase")) {
                        ScriptData.currentGameObject = GameObjects.closest((ScriptData.SECURE_RANDOM.nextInt(2) == 1) ? 155 : 156);
                        Logger.log("Determined bookcase as currentGameObject: " + ScriptData.currentGameObject);
                        return ScriptData.returnMSFast();
                    }
                    else if (ScriptData.currentGameObject.interact("Search")) {
                        Sleep.sleepUntil(() -> BEDROOM.contains(Players.getLocal()), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        return ScriptData.returnMSFast();
                    }
                }
                else if (!ScriptData.walkToArea(LIVING_ROOM)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 2: // Pressure gauge (before using Poisoned fish food on Fountain)
                if (Inventory.contains(271)) {
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (Inventory.containsAll(273, 272)) { // Poison, Fish food
                    if ((ScriptData.SECURE_RANDOM.nextInt(2) == 1 && Inventory.combine(272, 273)) || Inventory.combine(273, 272)) {
                        Sleep.sleepUntil(() -> Inventory.contains(274), ScriptData.SECURE_RANDOM.nextInt(5000 - 2000 + 1) + 2000, 300);
                        return ScriptData.returnMSFast();
                    }
                }
                else if (Inventory.contains(274)) { // Poisoned fish food
                    if (FOUNTAIN_AREA.contains(Players.getLocal())) {
                        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 153) {
                            ScriptData.currentGameObject = GameObjects.closest(153);
                            return ScriptData.returnMSFast();
                        }
                        else if (Inventory.isItemSelected()) {
                            if (ScriptData.currentGameObject.interact()) {
                                Sleep.sleepUntil(() -> ScriptData.questOrderI == 3, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            }
                            return ScriptData.returnMSFast();
                        }
                        else {
                            Inventory.interact(274);
                        }
                    }
                    else if (!ScriptData.walkToArea(FOUNTAIN_AREA)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (!Inventory.contains(273)) {
                    if (!ScriptData.interactWithGroundItemSingle(273, POISON_AREA, "Take")) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (!Inventory.contains(272)) {
                    if (!ScriptData.interactWithGroundItemSingle(272, FISH_FOOD_AREA, "Take")) {
                        return ScriptData.returnMSFast();
                    }
                }
                break;
            case 3: // Pressure gauge (After using Poisoned fish food on Fountain)
                if (Inventory.contains(271)) {
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 153) {
                    ScriptData.currentGameObject = GameObjects.closest(153);
                }
                else if (ScriptData.currentGameObject.interact()) {
                    Sleep.sleepUntil(() -> ScriptData.questOrderI == 3, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                }
                return ScriptData.returnMSFast();
            case 4: // Rubber tube
                if (Inventory.contains(276)) {
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (Inventory.contains(275)) { // Key
                    if (!ScriptData.interactWithGroundItemSingle(276, RUBBER_TUBE_AREA, "Take")) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (Inventory.contains(952)) { // Spade
                    if (!ScriptData.interactWithGameObjectSingle(152, COMPOST_HEAP_AREA, "Search", () -> Inventory.contains(275))) { // Compost heap
                        return ScriptData.returnMSFast();
                    }
                }
                else if (!ScriptData.interactWithGroundItemSingle(952, SPADE_AREA, "Take")) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 5: // Finish quest
                if (!ScriptData.interactWithNPC(3562, PROFESSOR_ODDENSTEIN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
        }

        return ScriptData.returnMSNormal();
    }

}
