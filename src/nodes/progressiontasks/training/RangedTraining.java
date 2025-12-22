package nodes.progressiontasks.training;

import framework.Node;
import framework.SCScript;
import data.global.PlayerData;
import data.global.ScriptData;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

import java.util.List;

public class RangedTraining implements Node {

    @Override
    public int loop() {
        if (Dialogues.inDialogue()) {
            Logger.log("In dialogue");
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

        if (ScriptData.currentNPC == null
                || !ScriptData.currentNPC.exists() // No target NPC
                || (ScriptData.currentCharacter == null && (ScriptData.currentCharacter = ScriptData.currentNPC.getCharacterInteractingWithMe()) != null && !ScriptData.currentCharacter.equals(Players.getLocal()))) { // Someone else attacking target
            if (ScriptData.currentTile != null) { // Collect npcDrop
                List<GroundItem> opts = GroundItems.getForTile(ScriptData.currentTile);
                if (opts.isEmpty()) {
                    ScriptData.currentTile = null;
                    ScriptData.currentNPC = null;
                    ScriptData.currentCharacter = null;
                }
                else {
                    ScriptData.currentGroundItem = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                    if (ScriptData.currentGroundItem.interact("Take")) {
                        Sleep.sleepUntil(ScriptData.GROUND_ITEM_NOT_EXISTS_NULL, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                    }
                }
            }
            else if (!ScriptData.currentArea.contains(Players.getLocal())) { // walkToArea
                ScriptData.walkToArea(ScriptData.currentArea);
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            else if (ScriptData.currentCharacter == null
                    && (ScriptData.currentCharacter = Players.getLocal().getCharacterInteractingWithMe()) != null
                    && ScriptData.currentCharacter.hasAction("Attack")) { // Something attacking player, target not set
                ScriptData.currentNPC = (NPC) ScriptData.currentCharacter;
                ScriptData.currentTile = ScriptData.currentNPC.getServerTile();
            }
            else { // Determine targetNPC
                List<NPC> opts;
                switch (ScriptData.currentEntityName) {
                    case "Cow":
                        opts = NPCs.all(npc -> npc.getName().contains("Cow") && npc.getCharacterInteractingWithMe() == null);
                        break;
                    case "Hill Giant":
                        opts = NPCs.all(npc -> npc.getName().equals(ScriptData.currentEntityName) && npc.distance() < 6 && npc.getCharacterInteractingWithMe() == null);
                        break;
                    case "Moss giant":
                        opts = NPCs.all(npc -> npc.getName().equals(ScriptData.currentEntityName) && npc.distance() <= 4 && npc.getCharacterInteractingWithMe() == null);
                        break;
                    case "Wizard":
                        opts = NPCs.all(npc -> npc.getName().contains("izard") && npc.getCharacterInteractingWithMe() == null);
                        break;
                    default:
                        opts = NPCs.all(npc -> npc.getName().equals(ScriptData.currentEntityName) && npc.getCharacterInteractingWithMe() == null);
                }
                if (!opts.isEmpty()) {
                    ScriptData.currentNPC = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                    ScriptData.currentTile = ScriptData.currentNPC.getServerTile();
                }
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!ScriptData.currentNPC.canReach()) {
            ScriptData.walkToEntity(ScriptData.currentNPC);
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (Combat.getCombatStyle() != PlayerData.rangedCombatStyle) {
            Logger.log("Needs to set rangedCombatStyle");
            if (Combat.setCombatStyle(PlayerData.rangedCombatStyle)) {
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!Players.getLocal().isInteracting(ScriptData.currentNPC) && ScriptData.currentNPC.interact("Attack")) {
            Sleep.sleepUntil(ScriptData.INTERACTED_WITH_TARGET, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000);
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
