package nodes.progressiontasks.training;

import framework.Node;
import framework.SCScript;
import data.global.PlayerData;
import data.global.ScriptData;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;

import java.util.List;

/**
 * Switching combat styles:
 * - Determine level to switch
 */
public class MeleeTraining implements Node {

    @Override
    public int loop() {
        if (Inventory.contains(PlayerData.food) && Skills.getBoostedLevel(Skill.HITPOINTS) <= PlayerData.eatFoodHPTrs) { // Eat food
            Logger.log("Needs to eat food");
            int[] foodSlots = new int[28];
            byte foodSlotsSize = 0;
            for (Item item : Inventory.toArray()) {
                if (item != null && item.getId() == PlayerData.food) {
                    foodSlots[foodSlotsSize++] = item.getSlot();
                }
            }
            if (!Inventory.slotInteract(foodSlots[SCScript.SECURE_RANDOM.nextInt(foodSlotsSize)], "Eat")) {
                PlayerData.determineEatFoodHPTrs();
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            }
            Logger.log("Attempted to eat food at random slot");
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (Dialogues.inDialogue()) {
            Logger.log("In dialogue");
            if (Dialogues.canContinue()) {
                Dialogues.continueDialogue();
                Logger.log("Attempted to continue dialogue");
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (ScriptData.progressionTaskTimer.finished()) { // Finished progressionTask
            Logger.log("progressionTaskTime finished");
            ScriptData.finishProgressionTrainingTask();
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (Inventory.isFull() && Inventory.contains(PlayerData.food)) { // Too much food
            ScriptData.currentLoadOutData.setInventoryItemMax(0, Math.max(0, ScriptData.currentLoadOutData.getInventoryItemMax(0) - Inventory.count(PlayerData.food)));
            Logger.log("Too much food, new foodMax: " + ScriptData.currentLoadOutData.getInventoryItemMax(0));
            if (ScriptData.currentLoadOutData.getInventoryItemMax(0) == 0) {
                ScriptData.currentLoadOutData.setInventoryItemMin(0, 0);
                Logger.log("maxFood is 0, therefore set minFood to 0");
            }
        }
        else if (ScriptData.currentLoadOutData.getInventoryItemMax(0) != 0 && !Inventory.isFull() && !Inventory.contains(PlayerData.food)) { // Too little food
            ScriptData.currentLoadOutData.setInventoryItemMax(0, Math.min(27, ScriptData.currentLoadOutData.getInventoryItemMax(0) + 1));
            Logger.log("Too little food, new foodMax: " + ScriptData.currentLoadOutData.getInventoryItemMax(0));
        }

        if (ScriptData.currentLoadOutData.shouldBank()) {
            Logger.log("Needs to bank");
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (ScriptData.currentNPC == null
                || !ScriptData.currentNPC.exists() // No target NPC
                || (ScriptData.currentCharacter == null && (ScriptData.currentCharacter = ScriptData.currentNPC.getCharacterInteractingWithMe()) != null && !ScriptData.currentCharacter.equals(Players.getLocal()))) { // Someone else attacking target
            if (ScriptData.currentTile != null) { // Collect npcDrop
                List<GroundItem> opts = GroundItems.getForTile(ScriptData.currentTile);
                if (opts.isEmpty()) {
                    Logger.log("No groundItems left, setting everything to null");
                    ScriptData.resetEntities();
                }
                else if (Inventory.isFull() && Inventory.contains(PlayerData.food) && Skills.getBoostedLevel(Skill.HITPOINTS) + PlayerData.foodHP <= Skills.getRealLevel(Skill.HITPOINTS)) {
                    Logger.log("There are items to collect, player has food, and can eat food to free up an inventory space");
                    int[] foodSlots = new int[28];
                    byte foodSlotsSize = 0;
                    for (Item item : Inventory.toArray()) {
                        if (item != null && item.getId() == PlayerData.food) {
                            foodSlots[foodSlotsSize++] = item.getSlot();
                        }
                    }
                    Inventory.slotInteract(foodSlots[SCScript.SECURE_RANDOM.nextInt(foodSlotsSize)], "Eat");
                    Logger.log("Attempted to eat food at random slot ");
                }
                else {
                    ScriptData.currentGroundItem = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                    if (ScriptData.currentGroundItem.interact("Take")) {
                        Sleep.sleepUntil(ScriptData.GROUND_ITEM_NOT_EXISTS_NULL, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                        Logger.log("Interacted with groundItem");
                    }
                }
            }
            else if (!ScriptData.currentArea.contains(Players.getLocal())) { // walkToArea
                if (ScriptData.walkToArea(ScriptData.currentArea)) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
            }
            else if (ScriptData.currentCharacter == null
                    && (ScriptData.currentCharacter = Players.getLocal().getCharacterInteractingWithMe()) != null
                    && ScriptData.currentCharacter.hasAction("Attack")) { // Something attacking player, target not set
                ScriptData.currentNPC = (NPC) ScriptData.currentCharacter;
                ScriptData.currentTile = ScriptData.currentNPC.getServerTile();
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
                    ScriptData.currentNPC = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                    ScriptData.currentTile = ScriptData.currentNPC.getServerTile();
                    Logger.log("targetNPC set");
                }
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!ScriptData.currentNPC.canReach()) {
            if (ScriptData.walkToEntity(ScriptData.currentNPC)) {
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (Combat.getCombatStyle() != PlayerData.meleeCombatStyle) {
            Logger.log("Needs to set meleeCombatStyle");
            if (Combat.setCombatStyle(PlayerData.meleeCombatStyle)) {
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (!Players.getLocal().isInteracting(ScriptData.currentNPC)
                && ScriptData.currentNPC.getHealthPercent() > 0 && ScriptData.currentNPC.interact("Attack")) {
            Sleep.sleepUntil(ScriptData.INTERACTED_WITH_TARGET, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000);
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        ScriptData.currentTile = ScriptData.currentNPC.getServerTile();
        Logger.log("Set currentTile as currentNPC serverTile");

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
