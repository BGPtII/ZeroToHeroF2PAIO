package nodes.progressiontasks.questing;

import framework.Node;
import framework.SCScript;
import data.global.PlayerData;
import data.global.ScriptData;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.List;

public class WitchsPotion implements Node {

    private final Area BETTY_SHOP = new Area(3011, 3261, 3015, 3256);
    private final Area HETTY_AREA = new Area(2965, 3208, 2970, 3203);
    private final Area ARCHERY_SHOP = new Area(2953, 3206, 2960, 3202);

    @Override
    public int loop() {
        if (Dialogues.inDialogue()) {
            if (Dialogues.isProcessing()) {
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            if (Dialogues.canContinue()) {
                Dialogues.continueDialogue();
            }
            else if (Dialogues.areOptionsAvailable()) {
                Dialogues.chooseFirstOption(ScriptData.dialogueOpts);
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (FreeQuest.WITCHS_POTION.isFinished()) {
            if (Widgets.isVisible(153, 16)) {
                if (ScriptData.closeQuestCompletionWidget()) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
            }
            else {
                ScriptData.finishProgressionQuestingTask();
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        switch (ScriptData.questOrder[ScriptData.questOrderI]) {
            case 0: // Start quest
                if (!ScriptData.interactWithNPC(4619, HETTY_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 1: // Burnt meat
                if (Inventory.contains(2146)) { // Burnt meat
                    ScriptData.questOrderI++;
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else if (Inventory.contains(2134, 2132, 2142)) { // Raw rat meat, Raw beef, Cooked meat
                    if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 24969) { // Hetty fireplace
                        ScriptData.currentGameObject = GameObjects.closest(24969);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (Inventory.isItemSelected()) {
                        if (ScriptData.currentGameObject.interact()) {
                            if (Inventory.contains(2134, 2132)) {
                                Sleep.sleepUntil(() -> Inventory.contains(2142, 2146), SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                            }
                            else {
                                Sleep.sleepUntil(() -> Inventory.contains("Burnt meat"), SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                            }
                        }
                    }
                    else if (Inventory.contains(2134)) {
                        Inventory.use(2134);
                    }
                    else if (Inventory.contains(2132)) {
                        Inventory.use(2132);
                    }
                    else if (Inventory.contains(2142)) {
                        Inventory.use(2142);
                    }
                }
                else {
                    if (ScriptData.currentNPC == null
                            || !ScriptData.currentNPC.exists() // No target NPC
                            || (ScriptData.currentCharacter == null && (ScriptData.currentCharacter = ScriptData.currentNPC.getCharacterInteractingWithMe()) != null && !ScriptData.currentCharacter.equals(Players.getLocal()))) { // Someone else attacking target
                        if (ScriptData.currentTile != null) { // Collect Raw rat meat/Raw beef
                            ScriptData.currentGroundItem = GroundItems.closest(2134, 2132);
                            if (ScriptData.currentGroundItem != null) {
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
                        else {
                            List<NPC> opts = NPCs.all(npc -> npc.getName().contains(ScriptData.currentEntityName) && npc.getCharacterInteractingWithMe() == null);
                            if (!opts.isEmpty()) {
                                ScriptData.currentNPC = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                                ScriptData.currentTile = ScriptData.currentNPC.getServerTile();
                                Logger.log("targetNPC set");
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                        }
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
                }
                break;
            case 2: // Eye of newt
                if (Inventory.contains(221)) { // Eye of newt
                    ScriptData.questOrderI++;
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else if (BETTY_SHOP.contains(Players.getLocal())) {
                    if (Shop.isOpen()) {
                        if (Shop.contains(item -> item.getId() == 221 && item.getValue() <= 3)) {
                            Shop.purchase(431, 1);
                        }
                    }
                    else if (!ScriptData.interactWithNPC(5905, BETTY_SHOP, "Trade", Shop::isOpen)) { // Zembo
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                break;
            case 3: // Onion - currentArea2
                if (Inventory.contains(1957)) { // Onion
                    ScriptData.questOrderI++;
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else if (!ScriptData.interactWithGroundItemMultiple(3366, ScriptData.currentArea2, "Pick")) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 4: // Rat's tail
                if (Inventory.contains(300)) { // Rat's tail
                    ScriptData.questOrderI++;
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else if (ARCHERY_SHOP.contains(Players.getLocal())) {
                    if ((ScriptData.currentGroundItem = GroundItems.closest(300)) != null) {
                        if (ScriptData.currentGroundItem.interact("Take")) {
                            Sleep.sleepUntil(() -> Inventory.contains(300), SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                        }
                    }
                    else if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || ScriptData.currentNPC.getId() != 2855) {
                        ScriptData.currentNPC = NPCs.closest(2855);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (ScriptData.currentNPC.getHealthPercent() > 0 && !Players.getLocal().isInteracting(ScriptData.currentNPC)) {
                        if (ScriptData.currentNPC.interact("Attack")) {
                            Sleep.sleepUntil(() -> (ScriptData.currentGroundItem = GroundItems.closest(300)) != null, SCScript.SECURE_RANDOM.nextInt(60000 - 30000 + 1) + 30000, 500);
                        }
                    }
                }
                else if (!ScriptData.walkToArea(ARCHERY_SHOP)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 5: // Finish quest
                if (HETTY_AREA.contains(Players.getLocal())) {
                    if (PlayerSettings.getConfig(67) != 2) {
                        if (!ScriptData.interactWithNPC(4619, HETTY_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                    }
                    else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2024) { // Cauldron
                        ScriptData.currentGameObject = GameObjects.closest(2024);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (ScriptData.currentGameObject.interact("Drink-from")) {
                        Sleep.sleepUntil(FreeQuest.WITCHS_POTION::isFinished, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                else if (!ScriptData.walkToArea(HETTY_AREA)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }
}
