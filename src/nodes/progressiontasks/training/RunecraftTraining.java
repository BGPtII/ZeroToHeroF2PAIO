package nodes.progressiontasks.training;

import framework.Node;
import framework.SCScript;
import data.global.PlayerData;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

/**
 * Areas
 * - Altar
 * - Rift
 * Path
 * - 1-9: Air runes
 * - 9-14: Earth runes
 * - 14-20: Fire runes
 * - 20-99: Body runes
 */
public class RunecraftTraining implements Node {

    @Override
    public int loop() {
        if (Dialogues.inDialogue()) {
            if (Dialogues.canContinue()) {
                Dialogues.continueDialogue();
                Logger.log("Attempted to continue dialogue");
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (ScriptData.progressionTaskTimer.finished()) {
            ScriptData.finishProgressionTrainingTask();
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!ScriptData.currentArea2.contains(Players.getLocal()) && ScriptData.currentLoadOutData.shouldBank()) { // Don't bank if in rift
            Logger.log("Should bank");
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (ScriptData.currentArea2.contains(Players.getLocal())) { // Craft the runes
            Logger.log("In currentArea2");
            if (Inventory.contains(7936)) { // Has Pure essence
                if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || !ScriptData.currentGameObject.getName().equals("Altar")) {
                    ScriptData.currentGameObject = GameObjects.closest("Altar");
                    Logger.log("Determined altar");
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else if (ScriptData.currentGameObject.interact("Craft-rune")) {
                    Logger.log("Attempting to craftRune");
                    Sleep.sleepUntil(() -> !Inventory.contains(7936), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                }
            }
            else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || !ScriptData.currentGameObject.getName().equals("Portal")) {
                ScriptData.currentGameObject = GameObjects.closest(gameObject -> gameObject.getName().contains("Portal"));
                Logger.log("Determined portal");
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            }
            else if (ScriptData.currentGameObject.interact()) {
                Sleep.sleepUntil(() -> !ScriptData.currentArea2.contains(Players.getLocal()), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (ScriptData.currentArea.contains(Players.getLocal())) { // Enter the rift
            if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || !ScriptData.currentGameObject.getName().equals("Mysterious ruins")) {
                ScriptData.currentGameObject = GameObjects.closest("Mysterious ruins");
            }
            else if (PlayerData.currentRunecraftMedium > 5000 || Inventory.isItemSelected()) {
                if (ScriptData.currentGameObject.interact()) {
                    Sleep.sleepUntil(() -> ScriptData.currentArea2.contains(Players.getLocal()), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                }
            }
            else if (Inventory.use(PlayerData.currentRunecraftMedium)) {
                Sleep.sleepUntil(Inventory::isItemSelected, SCScript.SECURE_RANDOM.nextInt(5000 - 2000 + 1) + 2000, 300);
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!ScriptData.walkToArea(ScriptData.currentArea)) { // Walk to ruins
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
