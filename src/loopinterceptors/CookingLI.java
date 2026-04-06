package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;

public class CookingLI extends LoopInterceptor {

    public CookingLI() {
        super(ScriptData.IN_CURRENT_AREA);
    }

    @Override
    public int handle() {
        if (ItemProcessing.isOpen()) {
            if (ItemProcessing.makeAll(ScriptData.TASK_LOAD_OUTS[0].getInvItemID(0))) { // The auto toggle of run will interrupt processing
                int walkingTrs = Walking.getRunThreshold();
                Walking.setRunThreshold(101);
                Sleep.sleepUntil(ScriptData.STOPPED_PROCESSING, ScriptData.SECURE_RANDOM.nextInt(180000 - 120000 + 1) + 120000, 500);
                Walking.setRunThreshold(walkingTrs);
            }
            return ScriptData.returnMSFast();
        }

        if (ScriptData.currentGameObject == null) {
            ScriptData.currentGameObject = GameObjects.closest(114); // Cook-o-matic
            return ScriptData.returnMSFast();
        }

        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 0 && ClientSettings.getEnergyThresholdToEnableRunning() != 0) {
            if (Walking.toggleRun()) {
                return ScriptData.returnMSNormal();
            }
            return ScriptData.returnMSFast();
        }

        if (!Inventory.isItemSelected()) {
            int[] rawFoodSlots = new int[28];
            byte rawFoodSlotsSize = 0;
            for (Item item : Inventory.toArray()) {
                if (item != null && item.getId() == ScriptData.TASK_LOAD_OUTS[0].getInvItemID(0)) {
                    rawFoodSlots[rawFoodSlotsSize++] = item.getSlot();
                }
            }
            if (Inventory.slotInteract(rawFoodSlots[ScriptData.SECURE_RANDOM.nextInt(rawFoodSlotsSize)])) {
                Sleep.sleepUntil(Inventory::isItemSelected, ScriptData.SECURE_RANDOM.nextInt(30000 - 15000 + 1) + 15000, 300);
            }
            return ScriptData.returnMSFast();
        }

        if (ScriptData.currentGameObject.interact()) {
            Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            return ScriptData.returnMSFast();
        }

        return ScriptData.returnMSFast();
    }

}
