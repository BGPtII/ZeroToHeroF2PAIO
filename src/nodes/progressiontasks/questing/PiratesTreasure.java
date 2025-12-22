package nodes.progressiontasks.questing;

import framework.Node;
import framework.SCScript;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

public class PiratesTreasure implements Node {

    private int luthasPaymentTotal = -1;

    private final Area REDBEARD_FRANK_AREA = new Area(3044, 3254, 3054, 3245);
    private final Area KARAMJA_ALC_SHOP = new Area(2918, 3147, 2930, 3142);
    private final Area BANANA_PLANTATION = new Area(2907, 3176, 2932, 3153);
    private final Area LUTHAS_AREA = new Area(2938, 3156, 2941, 3152);
    private final Area CRATE_AREA = new Area(2942, 3152, 2944, 3149);
    private final Area BLUE_MOON_INN_CHEST_AREA = new Area(3218, 3396, 3221, 3394, 1);
    private final Area SPADE_AREA = new Area(2981, 3370, 2984, 3368);
    private final Area WHITE_APRON_AREA = new Area(3011, 3229, 3017, 3223);
    private final Area FOOD_STORE_BACK = new Area(3009, 3210, 3010, 3203);
    private final Area FALADOR_PARK = new Area(2994, 3386, 3007, 3378);
    private final Tile DIG_TILE = new Tile(2999, 3383, 0);

    @Override
    public int loop() {
        if (Dialogues.inDialogue()) {
            if (Dialogues.isProcessing()) {
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            if (Dialogues.canContinue()) {
                String npcDialogue = Dialogues.getNPCDialogue();
                if (Dialogues.continueDialogue()) {
                    if (npcDialogue.startsWith("You wouldn't believe the demand for bananas")) {
                        Logger.log("\"You wouldn't believe the demand for bananas\" appeared, questOrderI now 2");
                        ScriptData.questOrderI = 2;
                    }
                }
            }
            else if (Dialogues.areOptionsAvailable()) {
                Dialogues.chooseFirstOption(ScriptData.dialogueOpts);
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (FreeQuest.PIRATES_TREASURE.isFinished()) {
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

        switch (PlayerSettings.getConfig(71)) {
            case 0:
                if (luthasPaymentTotal == -1) {
                    luthasPaymentTotal = PlayerSettings.getConfig(4512);
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else {
                    if (!ScriptData.interactWithNPC(3643, REDBEARD_FRANK_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                break;
            case 1:
                switch (ScriptData.questOrderI) {
                    case 0: // Obtain rum
                        if (Inventory.contains(431)) { // Karamjan rum
                            if (Shop.isOpen()) {
                                Shop.close();
                            }
                            else {
                                ScriptData.questOrderI = 1;
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                        }
                        else if (KARAMJA_ALC_SHOP.contains(Players.getLocal())) {
                            if (Shop.isOpen()) {
                                if (Shop.contains(item -> item.getId() == 431 && item.getValue() <= 30)) {
                                    Shop.purchase(431, 1);
                                }
                            }
                        }
                        else if (!ScriptData.interactWithNPC(13655, KARAMJA_ALC_SHOP, "Trade", Shop::isOpen)) { // Zembo
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        break;
                    case 1: // Gain employment
                        if (!ScriptData.interactWithNPC(3647, LUTHAS_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        break;
                    case 2: // Collect bananas
                        if (Inventory.count(1963) >= 10) { // Banana
                            ScriptData.questOrderI = 3;
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        else if (!BANANA_PLANTATION.contains(Players.getLocal())) {
                            if (!ScriptData.walkToArea(BANANA_PLANTATION)) {
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                        }
                        else if (ScriptData.currentTile == null) { // Select
                            List<GameObject> opts = GameObjects.all(gO -> gO.getId() > 2072 && gO.getId() < 2078); // Tree has banana ID range (2078 == empty)
                            if (!opts.isEmpty()) {
                                ScriptData.currentTile = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size())).getTile();
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                        }
                        else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists()) {
                            ScriptData.currentGameObject = GameObjects.getTopObjectOnTile(ScriptData.currentTile);
                            if (ScriptData.currentGameObject != null && (ScriptData.currentGameObject.getId() < 2073 || ScriptData.currentGameObject.getId() > 2077)) {
                                ScriptData.currentTile = null;
                                ScriptData.currentGameObject = null;
                                Logger.log("Couldn't find a valid Banana tree on tile selected, setting everything to null");
                            }
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        if (ScriptData.currentGameObject.interact("Pick")) {
                            Sleep.sleepUntil(ScriptData.DONE_RESOURCE_GATHERING, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        }
                        break;
                    case 3: // Fill crate with bananas
                        if (!Inventory.contains(1963)) {
                            ScriptData.questOrderI = 4;
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        else if (CRATE_AREA.contains(Players.getLocal())) {
                            if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2072) { // Crate
                                ScriptData.currentGameObject = GameObjects.closest(2072);
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                            else if (ScriptData.currentGameObject.interact("Fill")) {
                                Sleep.sleepUntil(() -> !Inventory.contains("Banana") || ScriptData.questOrderI == 4, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            }
                        }
                        else if (!ScriptData.walkToArea(CRATE_AREA)) {
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        break;
                    case 4: // Put rum in crate
                        if (!Inventory.contains(431)) {
                            ScriptData.questOrderI = 5;
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        else if (CRATE_AREA.contains(Players.getLocal())) {
                            if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2072) { // Crate
                                ScriptData.currentGameObject = GameObjects.closest(2072);
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                            else if (Inventory.isItemSelected()) {
                                if (ScriptData.currentGameObject.interact()) {
                                    Sleep.sleepUntil(() -> !Inventory.contains(431), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                                }
                            }
                            else {
                                Inventory.use(431);
                            }
                        }
                        else if (!ScriptData.walkToArea(CRATE_AREA)) {
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        break;
                    case 5: // Collect payment
                        if (luthasPaymentTotal != PlayerSettings.getConfig(4512)) {
                            ScriptData.questOrderI = 6;
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        else if (!ScriptData.interactWithNPC(3647, LUTHAS_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        break;
                    case 6: // Equip White apron
                        if (Equipment.contains(1005)) { // White apron
                            ScriptData.questOrderI = 7;
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        else if (Inventory.contains(1005)) {
                            if (Inventory.interact(1005, "Wear")) {
                                Sleep.sleepUntil(() -> Equipment.contains(1005), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            }
                        }
                        else if (WHITE_APRON_AREA.contains(Players.getLocal())) {
                            if (ScriptData.currentGroundItem == null || !ScriptData.currentGroundItem.exists()) {
                                ScriptData.currentGroundItem = GroundItems.closest(7957); // White apron (on wall)
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                            else if (ScriptData.currentGroundItem.interact("Take")) {
                                Sleep.sleepUntil(() -> Inventory.contains(1005), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            }
                        }
                        else if (!ScriptData.walkToArea(WHITE_APRON_AREA)) {
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        break;
                    case 7:
                        if (Inventory.contains(431)) {
                            ScriptData.questOrderI = 8;
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        else if (FOOD_STORE_BACK.contains(Players.getLocal())) {
                            if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2071) {
                                ScriptData.currentGameObject = GameObjects.closest(2071);
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                            else if (ScriptData.currentGameObject.interact("Search")) {
                                Sleep.sleepUntil(() -> Inventory.contains(431), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            }
                        }
                        else if (!ScriptData.walkToArea(FOOD_STORE_BACK)) {
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        break;
                    case 8:
                        if (!ScriptData.interactWithNPC(3643, REDBEARD_FRANK_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        break;
                }
                break;
            case 2:
                if (Inventory.contains(433)) { // Pirate message
                    Inventory.interact(433, "Read");
                }
                else if (BLUE_MOON_INN_CHEST_AREA.contains(Players.getLocal())) {
                    if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2079) {
                        ScriptData.currentGameObject = GameObjects.closest(2079); // Chest
                    }
                    else if (Inventory.isItemSelected()) {
                        if (ScriptData.currentGameObject.interact()) {
                            Sleep.sleepUntil(() -> Inventory.contains(433), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        }
                    }
                    else {
                        Inventory.use(432); // Chest key
                    }
                }
                else if (!ScriptData.walkToArea(BLUE_MOON_INN_CHEST_AREA)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 3:
                if (!Inventory.contains(952)) { // Spade
                    if (!ScriptData.interactWithGroundItemSingle(952, SPADE_AREA, "Take")) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (FALADOR_PARK.contains(Players.getLocal())) {
                    if ((ScriptData.currentCharacter = Players.getLocal().getCharacterInteractingWithMe()) != null && ScriptData.currentCharacter.hasAction("Attack")) {
                        if (ScriptData.currentCharacter.interact("Attack")) {
                            Sleep.sleepUntil(() -> ScriptData.currentCharacter == null || !ScriptData.currentCharacter.exists(), SCScript.SECURE_RANDOM.nextInt(30000 - 20000 + 1) + 20000, 300);
                        }
                    }
                    else if (DIG_TILE.equals(Players.getLocal().getTile())) {
                        if (Inventory.interact(952, "Dig")) {
                            Sleep.sleepUntil(() -> Players.getLocal().getCharacterInteractingWithMe() != null || Inventory.contains(7956), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                    }
                    else if (!ScriptData.walkToTile(DIG_TILE)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (!ScriptData.walkToArea(FALADOR_PARK)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
        }

        return 0;
    }
}
