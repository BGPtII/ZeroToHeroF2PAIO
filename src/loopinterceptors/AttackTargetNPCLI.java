package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

public class AttackTargetNPCLI extends LoopInterceptor {

    public AttackTargetNPCLI() {
        super(() -> ScriptData.currentNPC != null
                && ScriptData.currentNPC.getHealthPercent() > 0
                && ScriptData.currentNPC.canReach()
                && !Players.getLocal().isInteracting(ScriptData.currentNPC));
    }

    @Override
    public int handle() {
        if (ScriptData.currentNPC.interact("Attack")) {
            Sleep.sleepUntil(ScriptData.INTERACTED_WITH_TARGET, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
        }
        return ScriptData.returnMSFast();
    }

}
