package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.List;

public class ShearSheepLI extends LoopInterceptor {

    private final Condition SHEARED_SHEEP = () -> ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || !ScriptData.currentNPC.hasAction("Shear");

    public ShearSheepLI() {
        super(() -> ScriptData.NOT_HANDLE_LOAD_OUT.verify() && !Inventory.isFull() && Inventory.contains(1735));
    }

    @Override
    public int handle() {
        if (ScriptData.currentArea.contains(Players.getLocal())) {
            int playersInPen = Players.all(player -> ScriptData.currentArea.contains(player) && !player.equals(Players.getLocal())).size();
            if (playersInPen >= ScriptData.questOrder[0]
                    || ScriptData.questOrderI >= ScriptData.questOrder[1]) {
                Logger.log("playersInPen: " + playersInPen + ", maxPlayersInPenAllowed: " + ScriptData.questOrder[1]);
                Logger.log("Times failed to find valid sheep: " + ScriptData.questOrderI + ", maxAllowedFailures: " + ScriptData.questOrder[0]);
                if (ScriptData.hopWorldsWH()) {
                    ScriptData.questOrder[0] = (byte) (ScriptData.SECURE_RANDOM.nextInt(7 - 3 + 1) + 3);
                    ScriptData.questOrder[1] = (byte) (ScriptData.SECURE_RANDOM.nextInt(7 - 3 + 1) + 3);
                    ScriptData.questOrderI = 0;
                    return ScriptData.returnMSNormal();
                }
                return ScriptData.returnMSFast();
            }
            else if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || !ScriptData.currentNPC.hasAction("Shear")) {
                List<NPC> opts;
                if (ScriptData.rollChance(95)) {
                    opts = NPCs.all(npc -> npc.distance() <= ScriptData.currentEntityDistance && npc.getId() != 731 && npc.hasAction("Shear"));
                }
                else {
                    opts = NPCs.all(npc -> npc.getId() != 731 && npc.hasAction("Shear"));
                }
                if (opts.isEmpty()) {
                    ScriptData.questOrderI++;
                    ScriptData.incrementCurrentEntityDistance();
                }
                else {
                    ScriptData.resetCurrentEntityDistance();
                    ScriptData.currentNPC = opts.get(ScriptData.SECURE_RANDOM.nextInt(opts.size()));
                }
                return ScriptData.returnMSFast();
            }
            else if (ScriptData.currentNPC.canReach()) {
                int emptySlots = Inventory.getEmptySlots();
                if (ScriptData.currentNPC.interact("Shear")) {
                    Sleep.sleepUntil(SHEARED_SHEEP, ScriptData.SECURE_RANDOM.nextInt(30000 - 15000 + 1) + 15000, 300);
                    if (emptySlots != Inventory.getEmptySlots()) {
                        ScriptData.questOrderI++;
                    }
                }
            }
            else if (!ScriptData.walkToEntity(ScriptData.currentNPC)) {
                return ScriptData.returnMSFast();
            }
        }
        else if (!ScriptData.walkToArea(ScriptData.currentArea)) {
            return ScriptData.returnMSFast();
        }
        return ScriptData.returnMSNormal();
    }

}
