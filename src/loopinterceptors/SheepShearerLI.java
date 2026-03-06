package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.List;

public class SheepShearerLI extends LoopInterceptor {

    private final Condition SHEARED_SHEEP = () -> !ScriptData.currentNPC.hasAction("Shear");
    private final Condition DONE_PROCESSING = () -> !Inventory.contains(1737) || (Dialogues.inDialogue() && !ItemProcessing.isOpen());
    private final Area SHEEP = new Area(3193, 3276, 3212, 3257);
    private final Area SPINNING_WHEEL = new Area(3203, 3217, 3213, 3207, 1); // Spinning wheel
    private final Area FARMER_FRED = new Area(3188, 3275, 3192, 3270);

    public SheepShearerLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    @Override
    public int handle() {
        if (ItemProcessing.isOpen()) {
            if (ItemProcessing.makeAll(1759)) { // The auto toggle of run will interrupt processing
                int walkingTrs = Walking.getRunThreshold();
                Walking.setRunThreshold(101);
                Sleep.sleepUntil(DONE_PROCESSING, ScriptData.SECURE_RANDOM.nextInt(180000 - 120000 + 1) + 120000, 500);
                Walking.setRunThreshold(walkingTrs);
            }
        }

        if (FreeQuest.SHEEP_SHEARER.isFinished()) {
            if (Widgets.isVisible(153, 16)) {
                if (ScriptData.closeQuestCompletionWidget()) {
                    return ScriptData.returnMSNormal();
                }
            }
            else {
                ScriptData.finishProgressionQuestingTask();
            }
            return ScriptData.returnMSFast();
        }

        if (Inventory.count(1759) == 20) { // Ball of wool
            if (ScriptData.interactWithNPC(732, FARMER_FRED, "Talk-to", ScriptData.IN_DIALOGUE)) {
                return ScriptData.returnMSNormal();
            }
            return ScriptData.returnMSFast();
        }

        if (!Inventory.contains(1735)) { // Shears
            if (ScriptData.interactWithGroundItemSingle(1735, FARMER_FRED, "Take")) {
                return ScriptData.returnMSNormal();
            }
            return ScriptData.returnMSFast();
        }

        if (Inventory.count(1737) + Inventory.count(1759) < 20) { // Wool, Ball of wool
            if (SHEEP.contains(Players.getLocal())) {
                if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || !ScriptData.currentNPC.hasAction("Shear")) {
                    List<NPC> opts;
                    if (ScriptData.SECURE_RANDOM.nextInt(100) < 95) {
                        opts = NPCs.all(npc -> npc.getId() != 731 && npc.hasAction("Shear"));
                    }
                    else {
                        opts = NPCs.all(npc -> npc.distance() <= ScriptData.currentEntityDistance && npc.getId() != 731 && npc.hasAction("Shear"));
                    }
                    if (opts.isEmpty()) {
                        ScriptData.incrementCurrentEntityDistance();
                    }
                    else {
                        ScriptData.resetCurrentEntityDistance();
                        ScriptData.currentNPC = opts.get(ScriptData.SECURE_RANDOM.nextInt(opts.size()));
                    }
                    return ScriptData.returnMSFast();
                }
                else if (ScriptData.currentNPC.canReach()) {
                    if (ScriptData.currentNPC.interact("Shear")) {
                        Sleep.sleepUntil(SHEARED_SHEEP, ScriptData.SECURE_RANDOM.nextInt(30000 - 15000 + 1) + 15000, 300);
                    }
                }
                else {
                    if (ScriptData.walkToEntity(ScriptData.currentNPC)) {
                        return ScriptData.returnMSNormal();
                    }
                    return ScriptData.returnMSFast();
                }
            }
            else {
                if (!ScriptData.walkToArea(SHEEP)) {
                    return ScriptData.returnMSFast();
                }
            }
            return ScriptData.returnMSNormal();
        }

        if (SPINNING_WHEEL.contains(Players.getLocal())) { // Process Ball of wool
            if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 14889) {
                ScriptData.currentGameObject = GameObjects.closest(14889);
                return ScriptData.returnMSFast();
            }
            else if (ScriptData.currentGameObject.canReach()) {
                if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 0 && ClientSettings.getEnergyThresholdToEnableRunning() != 0) {
                    Walking.toggleRun();
                }
                else if (ScriptData.currentGameObject.interact("Spin")) {
                    Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(20000 - 3000 + 1) + 3000, 300);
                }
            }
            else if (!ScriptData.walkToEntity(ScriptData.currentGameObject)) {
                return ScriptData.returnMSFast();
            }
        }
        else if (!ScriptData.walkToArea(SPINNING_WHEEL)) {
            return ScriptData.returnMSFast();
        }

        return ScriptData.returnMSNormal();
    }

}
