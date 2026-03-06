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
import org.dreambot.api.wrappers.map.Region;

public class TheCorsairCurseLI extends LoopInterceptor {

    private final Area CAPTAIN_TOCK_SOUTH_FALLY_FARM = new Area(3025, 3277, 3034, 3269);
    private final Area CAPTAIN_TOCK_RIMM = new Area(2907, 3226, 2915, 3225);
    private final Area CAPTAIN_TOCK_CC = new Area(2574, 2837, 2581, 2835, 1);
    private final Area ITHOI_AREA = new Area(2527, 2841, 2532, 2835, 1);
    private final Area ARSEN_COLIN_AREA = new Area(2553, 2859, 2559, 2853, 1);
    private final Area GNOCCI_AREA = new Area(2543, 2864, 2546, 2860, 1);
    private final Area TESS_AREA = new Area(2009, 9009, 2014, 9003, 1);
    private final Area LUMP_OF_SAND = new Area(2503, 2840, 2505, 2838);
    private final Area DRIFT_WOOD_AREA = new Area(2528, 2836, 2533, 2840);
    private final Area SPADE_AREA = new Area(2549, 2848, 2560, 2842);

    public TheCorsairCurseLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    private boolean talkedToIthoi() { return PlayerSettings.getBitValue(6075) == 1; }

    private boolean talkedToArsen() {
        return PlayerSettings.getBitValue(6074) >= 2;
    }
    private boolean returnedToothPick() {
        return PlayerSettings.getBitValue(6074) == 4;
    }
    private boolean finishedArsen() {
        return PlayerSettings.getBitValue(6074) >= 6;
    }

    private boolean talkedToColin() {
        return PlayerSettings.getBitValue(6072) >= 1;
    }
    private boolean lookedThroughTelescope() { return PlayerSettings.getBitValue(6072) == 2; }
    private boolean finishedColin() { return PlayerSettings.getBitValue(6072) == 3; }


    private boolean talkedToGnocci() { return PlayerSettings.getBitValue(6073) == 1; }
    private boolean foundDoll() {
        return PlayerSettings.getBitValue(6073) == 2;
    }
    private boolean finishedGnocci() {
        return PlayerSettings.getBitValue(6073) == 3;
    }

    @Override
    public int handle() {
        switch (PlayerSettings.getBitValue(6071)) {
            case 0:
                if (!ScriptData.interactWithNPC(7956, CAPTAIN_TOCK_SOUTH_FALLY_FARM, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    ScriptData.returnMSFast();
                }
                break;
            case 10:
                if (!ScriptData.interactWithNPC(7958, CAPTAIN_TOCK_RIMM, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    ScriptData.returnMSFast();
                }
                break;
            case 15:
                if (talkedToIthoi()) {
                    switch (ScriptData.questOrder[ScriptData.questOrderI]) {
                        case 0: // Gnocci
                            if (finishedGnocci()) {
                                ScriptData.questOrderI++;
                                ScriptData.returnMSFast();
                            }
                            else if (!Inventory.contains(952)) { // Spade
                                if (!ScriptData.interactWithGameObjectSingle(31585, SPADE_AREA, "Take", () -> Inventory.contains(952))) {
                                    ScriptData.returnMSFast();
                                }
                            }
                            else if (talkedToGnocci() && !foundDoll()) {
                                if (LUMP_OF_SAND.contains(Players.getLocal())) {
                                    if (Inventory.interact(952, "Dig")) {
                                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                                    }
                                }
                                else if (!ScriptData.walkToArea(LUMP_OF_SAND)) {
                                    ScriptData.returnMSFast();
                                }
                            }
                            else if (!ScriptData.interactWithNPC(7970, GNOCCI_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                                ScriptData.returnMSFast();
                            }
                            break;
                        case 1: // Arsen
                            if (finishedArsen()) {
                                ScriptData.questOrderI++;
                                ScriptData.returnMSFast();
                            }
                            else if (talkedToArsen() && !returnedToothPick() && Inventory.contains("Ogre artefact")) {
                                if (!ScriptData.interactWithNPC(7988, TESS_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                                    ScriptData.returnMSFast();
                                }
                            }
                            else if (!talkedToArsen() || returnedToothPick()) {
                                if (!ScriptData.interactWithNPC(7956, ARSEN_COLIN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                                    ScriptData.returnMSFast();
                                }
                            }
                            else if (!ScriptData.interactWithNPC(7958, CAPTAIN_TOCK_CC, "Talk-to", ScriptData.IN_DIALOGUE)) {
                                ScriptData.returnMSFast();
                            }
                            break;
                        case 2: // Colin
                            if (finishedColin()) {
                                ScriptData.questOrderI++;
                                ScriptData.returnMSFast();
                            }
                            else if (talkedToColin() && !lookedThroughTelescope()) {
                                if (!ScriptData.interactWithGameObjectSingle(31632, ITHOI_AREA, "Observe", ScriptData.IN_DIALOGUE)) { // Telescope
                                    ScriptData.returnMSFast();
                                }
                            }
                            else if (!ScriptData.interactWithNPC(7965, ARSEN_COLIN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                                ScriptData.returnMSFast();
                            }
                            break;
                    }
                }
                else if (!ScriptData.interactWithNPC(7961, ITHOI_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    ScriptData.returnMSFast();
                }
                break;
            case 20:
            case 55:
                if (ITHOI_AREA.contains(Region.fromInstance(Players.getLocal().getTile()))) {
                    if ((ScriptData.currentGameObject = GameObjects.closest(222)) != null) {
                        if (ScriptData.currentGameObject.interact("Climb")) {
                            Sleep.sleepUntil(() -> !ITHOI_AREA.contains(Region.fromInstance(Players.getLocal().getTile())), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        }
                    }
                }
                else if (!ScriptData.interactWithNPC(7958, CAPTAIN_TOCK_CC, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    ScriptData.returnMSFast();
                }
                break;
            case 25:
                if (!ScriptData.interactWithNPC(7970, GNOCCI_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    ScriptData.returnMSFast();
                }
                break;
            case 30:
                if (!ScriptData.interactWithNPC(7976, ARSEN_COLIN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    ScriptData.returnMSFast();
                }
                break;
            case 35:
                if (!talkedToColin()) {
                    if (!ScriptData.interactWithNPC(7965, ARSEN_COLIN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // Cabin Boy Colin
                        ScriptData.returnMSFast();
                    }
                }
                else if (!ScriptData.interactWithNPC(7961, ITHOI_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // Ithoi the Navigator
                    ScriptData.returnMSFast();
                }
                break;
            case 40:
                if (!ScriptData.interactWithNPC(7961, ITHOI_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // Ithoi the Navigator
                    ScriptData.returnMSFast();
                }
                break;
            case 45:
                if (Inventory.contains(590)) { // Tinderbox
                    if (DRIFT_WOOD_AREA.contains(Players.getLocal())) {
                        if (Inventory.isItemSelected()) {
                            if ((ScriptData.currentGameObject = GameObjects.closest(31723)) != null) {
                                if (ScriptData.currentGameObject .interact()) {
                                    Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                                }
                            }
                        }
                        else {
                            Inventory.use(590);
                        }
                    }
                    else if (!ScriptData.walkToArea(DRIFT_WOOD_AREA)) {
                        ScriptData.returnMSFast();
                    }
                }
                else if (GNOCCI_AREA.contains(Players.getLocal())) {
                    if ((ScriptData.currentGameObject = GameObjects.closest(31634)) != null) {
                        if (ScriptData.currentGameObject.interact("Take")) {
                            Sleep.sleepUntil(() -> Inventory.contains(590), ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                        }
                    }
                }
                else if (!ScriptData.walkToArea(GNOCCI_AREA)) {
                    ScriptData.returnMSFast();
                }
                break;
            case 50:
                if (talkedToIthoi()) {
                    if (!ScriptData.interactWithNPC(7958, CAPTAIN_TOCK_CC, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        ScriptData.returnMSFast();
                    }
                }
                else if (!ScriptData.interactWithNPC(7961, ITHOI_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // Ithoi the Navigator
                    ScriptData.returnMSFast();
                }
            case 52:
                if (ITHOI_AREA.contains(Region.fromInstance(Players.getLocal().getTile()))) {
                    if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || ScriptData.currentNPC.getId() != 7964) { // Ithoi#Attackable
                        ScriptData.currentNPC = NPCs.closest(7964);
                        ScriptData.returnMSFast();
                    }
                    else if (ScriptData.currentNPC.getHealthPercent() > 0 && !Players.getLocal().isInteracting(ScriptData.currentNPC)) {
                        ScriptData.currentNPC.interact("Attack");
                    }
                }
                else if (!ScriptData.walkToArea(ITHOI_AREA)) {
                    ScriptData.returnMSFast();
                }
                break;
        }

        return ScriptData.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
