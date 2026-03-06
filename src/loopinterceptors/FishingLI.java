package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.List;

public class FishingLI extends LoopInterceptor {

    private final Condition STARTED_FISHING = () -> Players.getLocal().getAnimation() == 621;
    private final Condition DONE_FISHING = () -> !Players.getLocal().isAnimating() || Dialogues.inDialogue();

    public FishingLI() {
        super(ScriptData.IN_CURRENT_AREA);
    }

    @Override
    public int handle() {
        if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists()) {
            List<NPC> opts = NPCs.all(npc -> ScriptData.currentArea.contains(npc) && npc.getName().equals(ScriptData.currentEntityName));
            if (!opts.isEmpty()) {
                ScriptData.currentNPC = opts.get(ScriptData.SECURE_RANDOM.nextInt(opts.size()));
            }
            return ScriptData.returnMSFast();
        }

        if (ScriptData.currentNPC.interact(ScriptData.currentEntityAction)) {
            Sleep.sleepUntil(STARTED_FISHING, ScriptData.SECURE_RANDOM.nextInt(20000 - 10000 + 1) + 10000, 300);
            Sleep.sleepUntil(DONE_FISHING, ScriptData.SECURE_RANDOM.nextInt(300000 - 240000 + 1) + 240000, 300);
            return ScriptData.returnMSFast();
        }

        return ScriptData.returnMSNormal();
    }
}
