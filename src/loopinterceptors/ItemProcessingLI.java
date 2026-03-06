package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;

public class ItemProcessingLI extends LoopInterceptor {

    private int processedItemID;
    private int[] preProcessedItemIDs;
    private int processingGameObjectID;

    private final Condition STOPPED_PROCESSING = () -> !Inventory.containsAll(preProcessedItemIDs) || Dialogues.canContinue() || Dialogues.areOptionsAvailable() || !ItemProcessing.isOpen();

    public ItemProcessingLI(int processedItemID, int[] preProcessedItemIDs, int processingGameObjectID) {
        super(ScriptData.IN_CURRENT_AREA.and(ScriptData.NOT_HANDLE_LOAD_OUT));
        this.processedItemID = processedItemID;
        this.preProcessedItemIDs = preProcessedItemIDs;
        this.processingGameObjectID = processingGameObjectID;
    }

    public void setProcessedItemID(int processedItemID) {
        this.processedItemID = processedItemID;
    }

    public void setPreProcessedItemIDs(int[] preProcessedItemIDs) {
        this.preProcessedItemIDs = preProcessedItemIDs;
    }

    public void setProcessingGameObjectID(int processingGameObjectID) {
        this.processingGameObjectID = processingGameObjectID;
    }

    @Override
    public int handle() {
        if (ItemProcessing.isOpen()) {
            if (ItemProcessing.makeAll(processedItemID)) { // The auto toggle of run will interrupt processing
                int walkingTrs = Walking.getRunThreshold();
                Walking.setRunThreshold(101);
                Sleep.sleepUntil(STOPPED_PROCESSING, ScriptData.SECURE_RANDOM.nextInt(180000 - 120000 + 1) + 120000, 500);
                Walking.setRunThreshold(walkingTrs);
            }
        }

        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != processingGameObjectID) {
            ScriptData.currentGameObject = GameObjects.closest(processingGameObjectID);
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
