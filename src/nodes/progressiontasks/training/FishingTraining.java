package nodes.progressiontasks.training;

import framework.Node;
import framework.SCScript;
import data.global.ScriptData;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.List;

public class FishingTraining implements Node {

    private final Condition STARTED_FISHING = () -> Players.getLocal().getAnimation() == 621;
    private final Condition DONE_FISHING = () -> !Players.getLocal().isAnimating() || Dialogues.inDialogue();

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

        if (ScriptData.currentLoadOutData.shouldBank()) {
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!ScriptData.currentArea.contains(Players.getLocal())) {
            ScriptData.walkToArea(ScriptData.currentArea);
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists()) {
            List<NPC> opts = NPCs.all(npc -> ScriptData.currentArea.contains(npc) && npc.getName().equals(ScriptData.currentEntityName));
            if (!opts.isEmpty()) {
                ScriptData.currentNPC = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                Logger.log("Determined currentNPC: " + ScriptData.currentNPC);
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (ScriptData.currentNPC.interact(ScriptData.currentEntityAction)) {
            Sleep.sleepUntil(STARTED_FISHING, SCScript.SECURE_RANDOM.nextInt(20000 - 10000 + 1) + 10000, 300);
            Sleep.sleepUntil(DONE_FISHING, SCScript.SECURE_RANDOM.nextInt(300000 - 240000 + 1) + 240000, 300);
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
