package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

public class SpinBallsOfWoolLI extends LoopInterceptor {

    public SpinBallsOfWoolLI() {
        super(() -> Inventory.count(1737) + Inventory.count(1759) == 27 // Wool, Ball of wool
                && Inventory.contains(1737));
    }

    @Override
    public int handle() {
        if (ItemProcessing.isOpen()) {
            if (ItemProcessing.makeAll(1759)) { // The auto toggle of run will interrupt processing
                int walkingTrs = Walking.getRunThreshold();
                Walking.setRunThreshold(101);
                if (ScriptData.rollChance(65)) {
                    Logger.log("Hover over a skill");
                    Skills.hoverSkill(Skill.CRAFTING);
                }
                else if (ScriptData.rollChance(55)) {
                    Logger.log("Moving mouse off screen");
                    Mouse.moveOutsideScreen(ScriptData.rollChance(85));
                }
                Sleep.sleepUntil(ScriptData.STOPPED_PROCESSING, ScriptData.SECURE_RANDOM.nextInt(180000 - 120000 + 1) + 120000, 500);
                Walking.setRunThreshold(walkingTrs);
                Logger.log("Finished processing");
            }
            return ScriptData.returnMSFast();
        }

        if (!ScriptData.currentArea2.contains(Players.getLocal())) {
            if (ScriptData.walkToArea(ScriptData.currentArea2)) {
                return ScriptData.returnMSFast();
            }
            return ScriptData.returnMSNormal();
        }

        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 14889) {
            ScriptData.currentGameObject = GameObjects.closest(14889);
            return ScriptData.returnMSFast();
        }

        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 0 && ClientSettings.getEnergyThresholdToEnableRunning() != 0) {
            if (Walking.toggleRun()) {
                return ScriptData.returnMSNormal();
            }
            return ScriptData.returnMSFast();
        }

        if (ScriptData.currentGameObject.interact()) {
            Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            return ScriptData.returnMSFast();
        }

        return ScriptData.returnMSNormal();
    }
}
