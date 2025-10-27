package nodes;

import framework.Node;
import framework.SCScript;
import global.PlayerData;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.List;

public class FishingTraining implements Node {

    private final Condition STARTED_FISHING = () -> Players.getLocal().getAnimation() == 621;
    private final Condition DONE_FISHING = () -> !Players.getLocal().isAnimating() || Dialogues.inDialogue();

    @Override
    public int loop() {
        if (PlayerData.currentLoadOutService.setUpNotValid()) {
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
        }

        if (!PlayerData.currentArea.contains(Players.getLocal())) {
            PlayerData.walkToArea(PlayerData.currentArea);
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (PlayerData.currentNPC == null || !PlayerData.currentNPC.exists()) {
            List<NPC> opts = NPCs.all(npc -> PlayerData.currentArea.contains(npc) && npc.getName().equals(PlayerData.currentEntityName));
            if (!opts.isEmpty()) {
                PlayerData.currentNPC = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
        }

        if (PlayerData.currentNPC.interact(PlayerData.currentEntityAction)) {
            Sleep.sleepUntil(STARTED_FISHING, SCScript.SECURE_RANDOM.nextInt(20000 - 10000 + 1) + 10000, 300);
            Sleep.sleepUntil(DONE_FISHING, SCScript.SECURE_RANDOM.nextInt(300000 - 240000 + 1) + 240000, 300);
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
