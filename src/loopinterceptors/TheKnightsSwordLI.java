package loopinterceptors;

import framework.LoopInterceptor;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Sleep;

public class TheKnightsSwordLI extends LoopInterceptor {

    private final Area SQUIRE_AREA = new Area(2980, 3348, 2965, 3337);
    private final Area RELDO_AREA = new Area(3207, 3497, 3216, 3490);
    private final Area THURGO_AREA = new Area(2995, 3148, 3001, 3143);
    private final Area PORTRAIT_AREA = new Area(2981, 3335, 2984, 3334, 2);

    public TheKnightsSwordLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    @Override
    public int handle() {
        switch (PlayerSettings.getConfig(122)) {
            case 0:
            case 4:
                if (!ScriptData.interactWithNPC(4737, SQUIRE_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // Squire
                    return ScriptData.returnMSFast();
                }
                break;
            case 1:
                if (!ScriptData.interactWithNPC(6203, RELDO_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // Reldo
                    return ScriptData.returnMSFast();
                }
                break;
            case 2:
            case 3:
                if (!ScriptData.interactWithNPC(4733, THURGO_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 5:
                if (Inventory.contains(666)) { // Portrait
                    if (!ScriptData.interactWithNPC(4733, THURGO_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (PORTRAIT_AREA.contains(Players.getLocal())) {
                    if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || ScriptData.currentNPC.getId() != 4736) { // Sir Vyvin
                        ScriptData.currentNPC = NPCs.closest(4736);
                        return ScriptData.returnMSFast();
                    }
                    else if ((ScriptData.currentGameObject = GameObjects.closest(2272, 2271)) != null) { // Cupboard#Open - 2272, Closed - 2271
                        if (ScriptData.currentGameObject.getId() == 2271) {
                            if (!ScriptData.interactWithGameObjectSingle(2271, PORTRAIT_AREA, "Open", () -> ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists())) {
                                return ScriptData.returnMSFast();
                            }
                        }
                        else if (ScriptData.currentNPC.getY() > 3336) {
                            if (!ScriptData.interactWithGameObjectSingle(2272, PORTRAIT_AREA, "Search", () -> ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists())) {
                                return ScriptData.returnMSFast();
                            }
                        }
                    }
                }
                else if (!ScriptData.walkToArea(PORTRAIT_AREA)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 6:
                if (Inventory.contains(667)) { // Blurite sword
                    if (!ScriptData.interactWithNPC(4737, SQUIRE_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // Squire
                        return ScriptData.returnMSFast();
                    }
                }
                else if (Inventory.count(2351) == 2) { // Iron bar
                    if (!ScriptData.interactWithNPC(4733, THURGO_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.currentTile.equals(Players.getLocal().getTile())) {
                    if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || !ScriptData.currentGameObject.getName().equals("Blurite rocks")) {
                        ScriptData.currentGameObject = GameObjects.closest("Blurite rocks");
                        return ScriptData.returnMSFast();
                    }
                    else if (ScriptData.currentGameObject.interact()) {
                        Sleep.sleepUntil(() -> Inventory.contains(668), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        return ScriptData.returnMSFast();
                    }
                }
                else if (!ScriptData.walkToTile(ScriptData.currentTile)) {
                    return ScriptData.returnMSFast();
                }
                break;
        }

        return ScriptData.returnMSNormal();
    }

}
