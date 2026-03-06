package loopinterceptors;

import framework.LoopInterceptor;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Sleep;

public class XMarksTheSpotLI extends LoopInterceptor {

    private final Area SHEARED_RAM = new Area(3226, 3242, 3233, 3239);
    private final Tile FIRST_CLUE_DIG = new Tile(3230, 3209, 0);
    private final Tile SECOND_CLUE_DIG = new Tile(3203, 3212, 0);
    private final Tile THIRD_CLUE_DIG = new Tile(3108, 3264, 0);
    private final Tile FOURTH_CLUE_DIG = new Tile(3077, 3260, 0);
    private final Area VEOS_PORT_SARIM = new Area(3051, 3249, 3055, 3245);

    public XMarksTheSpotLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }
    
    @Override
    public int handle() {
        switch (PlayerSettings.getBitValue(8063)) {
            case 0:
            case 1:
                if (!ScriptData.interactWithNPC(8632, SHEARED_RAM, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 2:
                if (FIRST_CLUE_DIG.equals(Players.getLocal().getTile())) {
                    if (Inventory.interact( 952, "Dig")) {
                        Sleep.sleepUntil(() -> PlayerSettings.getBitValue(8063) != 2, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (ScriptData.walkToTile(FIRST_CLUE_DIG)) {
                    return ScriptData.returnMSNormal();
                }
                return ScriptData.returnMSFast();
            case 3:
                if (SECOND_CLUE_DIG.equals(Players.getLocal().getTile())) {
                    if (Inventory.interact( 952, "Dig")) {
                        Sleep.sleepUntil(() -> PlayerSettings.getBitValue(8063) != 3, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (ScriptData.walkToTile(SECOND_CLUE_DIG)) {
                    return ScriptData.returnMSNormal();
                }
                break;
            case 4:
                if (THIRD_CLUE_DIG.equals(Players.getLocal().getTile())) {
                    if (Inventory.interact("Spade", "Dig")) {
                        Sleep.sleepUntil(() -> PlayerSettings.getBitValue(8063) != 4, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (ScriptData.walkToTile(THIRD_CLUE_DIG)) {
                    return ScriptData.returnMSNormal();
                }
                break;
            case 5:
                if (FOURTH_CLUE_DIG.equals(Players.getLocal().getTile())) {
                    if (Inventory.interact("Spade", "Dig")) {
                        Sleep.sleepUntil(() -> PlayerSettings.getBitValue(8063) != 5, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (ScriptData.walkToTile(FOURTH_CLUE_DIG)) {
                    return ScriptData.returnMSNormal();
                }
                break;
            case 6:
            case 7:
                if (!ScriptData.interactWithNPC(1063, VEOS_PORT_SARIM, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
        }

        return ScriptData.returnMSNormal();
    }
    
}
