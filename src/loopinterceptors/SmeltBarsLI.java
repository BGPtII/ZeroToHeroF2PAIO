package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;

public class SmeltBarsLI extends LoopInterceptor {

    private final Condition STOPPED_PROCESSING = () -> ScriptData.NOT_HANDLE_LOAD_OUT.verify() || Dialogues.canContinue() || Dialogues.areOptionsAvailable() || !ItemProcessing.isOpen();

    public SmeltBarsLI() {
        super(ScriptData.NOT_HANDLE_LOAD_OUT);
    }

    @Override
    public int handle() {
        if (ItemProcessing.isOpen()) {
            if ((Skills.getRealLevel(Skill.SMITHING) >= 30 && ItemProcessing.makeAll(2353))
                    || (Skills.getRealLevel(Skill.SMITHING) < 30 && ItemProcessing.makeAll(2349))) { // The auto toggle of run will interrupt processing
                int walkingTrs = Walking.getRunThreshold();
                Walking.setRunThreshold(101);
                Sleep.sleepUntil(STOPPED_PROCESSING, ScriptData.SECURE_RANDOM.nextInt(180000 - 120000 + 1) + 120000, 500);
                Walking.setRunThreshold(walkingTrs);
            }
        }

        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getName().equals("Furnace")) {
            ScriptData.currentGameObject = GameObjects.closest("Furnace");
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
