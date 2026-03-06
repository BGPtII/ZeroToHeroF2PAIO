package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Sleep;

public class DemonSlayerLI extends LoopInterceptor {

    private final Area ARIS_AREA = new Area(3198, 3429, 3210, 3420);
    private final Area SIR_PYRSIN_AREA = new Area(3201, 3475, 3206, 3469);
    private final Area CAPTAIN_ROVIN_AREA = new Area(3199, 3501, 3206, 3494, 2);
    private final Area WIZARD_TRIBORN_AREA = new Area(3110, 3159, 3114, 3154, 1);
    private final Area KITCHEN_AREA = new Area(3218, 3497, 3226, 3491);
    private final Area RUSTY_KEY = new Area(3222, 9899, 3229, 9896);
    private final Area STONE_CIRCLE = new Area(3225, 3372, 3230, 3366);

    public DemonSlayerLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    private boolean flushedKeyDownDrain() {
        return PlayerSettings.getBitValue(2568) != 0;
    }

    @Override
    public int handle() {
        switch (PlayerSettings.getBitValue(2561)) {
            case 0:
                if (!ScriptData.interactWithNPC(11868, ARIS_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 1:
                if (!ScriptData.interactWithNPC(5083, SIR_PYRSIN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 2:
                if (Equipment.contains(2402)) {
                    if (Client.isDynamicRegion()) {
                        if ((ScriptData.currentCharacter = Players.getLocal().getCharacterInteractingWithMe()) != null && ScriptData.currentCharacter.hasAction("Attack")) {
                            if (ScriptData.currentCharacter.getHealthPercent() > 0 && !Players.getLocal().isInteracting(ScriptData.currentCharacter)) {
                                ScriptData.currentCharacter.interact("Attack");
                            }
                        }
                        else if ((ScriptData.currentNPC = NPCs.closest("Delrith", "Weakened Delrith")) != null) {
                            if (ScriptData.currentNPC.hasAction("Banish")) {
                                if (ScriptData.currentNPC.interact("Banish")) {
                                    Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(20000 - 10000 + 1) + 10000, 300);
                                }
                            }
                            else if (!Players.getLocal().isInteracting(ScriptData.currentNPC)) {
                                ScriptData.currentNPC.interact("Attack");
                            }
                        }
                    }
                    else if (!ScriptData.walkToArea(STONE_CIRCLE)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.questOrderI == ScriptData.questOrder.length) { // Has all 3 keys
                    if (!ScriptData.interactWithNPC(5083, SIR_PYRSIN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else {
                    switch (ScriptData.questOrder[ScriptData.questOrderI]) {
                        case 0: // Captain Rovin
                            if (Inventory.contains(2400)) {
                                ScriptData.questOrderI++;
                                return ScriptData.returnMSFast();
                            }
                            else if (!ScriptData.interactWithNPC(5085, CAPTAIN_ROVIN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                                return ScriptData.returnMSFast();
                            }
                            break;
                        case 1: // Drain
                            if (Inventory.contains(2401)) {
                                ScriptData.questOrderI++;
                                return ScriptData.returnMSFast();
                            }
                            else if (flushedKeyDownDrain()) {
                                if (!ScriptData.interactWithGameObjectSingle(222, RUSTY_KEY, "Take", ScriptData.IN_DIALOGUE)) {
                                    return ScriptData.returnMSFast();
                                }
                            }
                            else if (!KITCHEN_AREA.contains(Players.getLocal())) {
                                if (!ScriptData.walkToArea(KITCHEN_AREA))  {
                                    return ScriptData.returnMSFast();
                                }
                            }
                            else if (Inventory.isItemSelected()) {
                                if ((ScriptData.currentGameObject = GameObjects.closest("Drain")) != null) {
                                    if (ScriptData.currentGameObject.interact()) {
                                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                                    }
                                }
                            }
                            else {
                                Inventory.interact("Bucket of water");
                            }
                            break;
                        case 2: // Traiborn
                            if (Inventory.contains(2399)) {
                                ScriptData.questOrderI++;
                                return ScriptData.returnMSFast();
                            }
                            else if (!ScriptData.interactWithNPC(5081, WIZARD_TRIBORN_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                                return ScriptData.returnMSFast();
                            }
                            break;
                    }
                }
                break;
        }

        return ScriptData.returnMSNormal();
    }
    
}
