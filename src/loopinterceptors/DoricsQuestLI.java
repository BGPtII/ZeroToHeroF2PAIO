package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

public class DoricsQuestLI extends LoopInterceptor {

    private final Area DORIC_AREA = new Area(2950, 3452, 2953, 3449);

    public DoricsQuestLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    @Override
    public int handle() {
        switch (ScriptData.questOrder[ScriptData.questOrderI]) {
            case 0: // Copper
                if (Inventory.count(436) >= 4) {
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (!ScriptData.currentArea.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(ScriptData.currentArea)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || !ScriptData.currentGameObject.getName().equals("Copper rocks")) {
                    List<GameObject> opts = GameObjects.all(gameObject -> ScriptData.currentArea.contains(gameObject) && gameObject.getName().equals("Copper rocks"));
                    if (!opts.isEmpty()) {
                        ScriptData.currentGameObject = opts.get(ScriptData.SECURE_RANDOM.nextInt(opts.size()));
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.currentGameObject.interact("Mine")) {
                    Sleep.sleepUntil(() -> ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists(), ScriptData.SECURE_RANDOM.nextInt(30000 - 15000 + 1) + 15000, 300);
                }
                break;
            case 1: // Clay
                if (Inventory.count(434) >= 6) {
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (!ScriptData.currentArea2.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(ScriptData.currentArea2)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || !ScriptData.currentGameObject.getName().equals("Clay rocks")) {
                    List<GameObject> opts = GameObjects.all(gameObject -> ScriptData.currentArea2.contains(gameObject) && gameObject.getName().equals("Clay rocks"));
                    if (!opts.isEmpty()) {
                        ScriptData.currentGameObject = opts.get(ScriptData.SECURE_RANDOM.nextInt(opts.size()));
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.currentGameObject.interact("Mine")) {
                    Sleep.sleepUntil(() -> ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists(), ScriptData.SECURE_RANDOM.nextInt(30000 - 15000 + 1) + 15000, 300);
                }
                break;
            case 2: // Iron rocks
                if (Inventory.count(440) >= 2) {
                    ScriptData.questOrderI++;
                    return ScriptData.returnMSFast();
                }
                else if (!ScriptData.currentArea3.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(ScriptData.currentArea3)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || !ScriptData.currentGameObject.getName().equals("Iron rocks")) {
                    List<GameObject> opts = GameObjects.all(gameObject -> ScriptData.currentArea3.contains(gameObject) && gameObject.getName().equals("Iron rocks"));
                    if (!opts.isEmpty()) {
                        ScriptData.currentGameObject = opts.get(ScriptData.SECURE_RANDOM.nextInt(opts.size()));
                        return ScriptData.returnMSFast();
                    }
                }
                else if (ScriptData.currentGameObject.interact("Mine")) {
                    Sleep.sleepUntil(() -> ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists(), ScriptData.SECURE_RANDOM.nextInt(30000 - 15000 + 1) + 15000, 300);
                }
                break;
            case 3: // Finish quest
                if (!ScriptData.interactWithNPC(3893, DORIC_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
        }

        return ScriptData.returnMSNormal();
    }

}
