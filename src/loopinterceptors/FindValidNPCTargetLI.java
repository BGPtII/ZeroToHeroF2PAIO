package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.List;

public class FindValidNPCTargetLI extends LoopInterceptor {

    public FindValidNPCTargetLI() {
        super(() -> ScriptData.currentNPC == null
                || !ScriptData.currentNPC.exists()
                || ((ScriptData.currentCharacter = ScriptData.currentNPC.getCharacterInteractingWithMe()) != null
                && ScriptData.currentNPC.hasAction("Attack")
                && !ScriptData.currentCharacter.equals(Players.getLocal())));
    }

    @Override
    public int handle() {
        if (ScriptData.currentCharacter != null) { // Something attacking player, target not set
            ScriptData.currentNPC = (NPC) ScriptData.currentCharacter;
            ScriptData.currentTile = ScriptData.currentNPC.getServerTile();
            ScriptData.currentCharacter = null;
            Logger.log("character attacking player, currentCharacter set");
        }
        else { // Determine targetNPC
            List<NPC> opts;
            switch (ScriptData.currentEntityName) {
                case "Cow":
                    opts = NPCs.all(npc -> npc.getName().contains(ScriptData.currentEntityName) && npc.getCharacterInteractingWithMe() == null && npc.distance() <= ScriptData.currentEntityDistance);
                    break;
                case "Minotaur":
                    opts = NPCs.all(npc -> npc.getName().equals(ScriptData.currentEntityName) && npc.getLevel() == 12 && npc.getCharacterInteractingWithMe() == null && npc.distance() <= ScriptData.currentEntityDistance);
                    break;
                case "Barbarian":
                    opts = NPCs.all(npc -> npc.getName().equals(ScriptData.currentEntityName) && npc.getId() != 3068 && npc.getCharacterInteractingWithMe() == null && npc.distance() <= ScriptData.currentEntityDistance);
                    break;
                default:
                    opts = NPCs.all(npc -> npc.getName().equals(ScriptData.currentEntityName) && npc.getCharacterInteractingWithMe() == null && npc.distance() <= ScriptData.currentEntityDistance);
            }
            if (opts.isEmpty()) {
                ScriptData.incrementCurrentEntityDistance();
            }
            else {
                ScriptData.currentNPC = opts.get(ScriptData.SECURE_RANDOM.nextInt(opts.size()));
                ScriptData.currentTile = ScriptData.currentNPC.getServerTile();
                Logger.log("targetNPC set");
            }
        }
        return ScriptData.returnMSFast();
    }

}
