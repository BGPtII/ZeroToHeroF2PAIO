package loopinterceptors;

import framework.LoopInterceptor;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

public class PiratesTreasureLI extends LoopInterceptor {

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

    public PiratesTreasureLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    @Override
    public int handle() {
        switch (PlayerSettings.getConfig(71)) {
            case 0:
                if (luthasPaymentTotal == -1) {
                    luthasPaymentTotal = PlayerSettings.getConfig(4512);
                    return ScriptData.returnMSFast();
                }
                else {
                    if (!ScriptData.interactWithNPC(3643, REDBEARD_FRANK_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return ScriptData.returnMSFast();
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
                                return ScriptData.returnMSFast();
                            }
                        }
                        else if (KARAMJA_ALC_SHOP.contains(Players.getLocal())) {
                            if (Shop.isOpen()) {
                                if (Shop.contains(item -> item.getId() == 431 && item.getValue() <= 30)) {
                                    if (Shop.purchase(431, 1)) {
                                        Sleep.sleepUntil(() -> Inventory.contains(431), ScriptData.SECURE_RANDOM.nextInt(7000 - 3000 + 1) + 3000, 300);
                                    }
                                }
                            }
                            else if (!ScriptData.interactWithNPC(13655, KARAMJA_ALC_SHOP, "Trade", Shop::isOpen)) { // Zembo
                                return ScriptData.returnMSFast();
                            }
                        }
                        else if (!ScriptData.walkToArea(KARAMJA_ALC_SHOP)) {
                            return ScriptData.returnMSFast();
                        }
                        break;
                    case 1: // Gain employment
                        if (!ScriptData.interactWithNPC(3647, LUTHAS_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                            return ScriptData.returnMSFast();
                        }
                        break;
                    case 2: // Collect bananas
                        if (Inventory.count(1963) >= 10) { // Banana
                            ScriptData.questOrderI = 3;
                            return ScriptData.returnMSFast();
                        }
                        else if (!BANANA_PLANTATION.contains(Players.getLocal())) {
                            if (!ScriptData.walkToArea(BANANA_PLANTATION)) {
                                return ScriptData.returnMSFast();
                            }
                        }
                        else if (ScriptData.currentTile == null) { // Select
                            List<GameObject> opts = GameObjects.all(gO -> gO.getId() > 2072 && gO.getId() < 2078); // Tree has banana ID range (2078 == empty)
                            if (!opts.isEmpty()) {
                                ScriptData.currentTile = opts.get(ScriptData.SECURE_RANDOM.nextInt(opts.size())).getTile();
                                return ScriptData.returnMSFast();
                            }
                        }
                        else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists()) {
                            ScriptData.currentGameObject = GameObjects.getTopObjectOnTile(ScriptData.currentTile);
                            if (ScriptData.currentGameObject != null && (ScriptData.currentGameObject.getId() < 2073 || ScriptData.currentGameObject.getId() > 2077)) {
                                ScriptData.currentTile = null;
                                ScriptData.currentGameObject = null;
                                Logger.log("Couldn't find a valid Banana tree on tile selected, setting everything to null");
                            }
                            return ScriptData.returnMSFast();
                        }
                        else if (ScriptData.currentGameObject.interact("Pick")) {
                            Sleep.sleepUntil(ScriptData.DONE_RESOURCE_GATHERING, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        }
                        break;
                    case 3: // Fill crate with bananas
                        if (!Inventory.contains(1963)) {
                            ScriptData.questOrderI = 4;
                            return ScriptData.returnMSFast();
                        }
                        else if (CRATE_AREA.contains(Players.getLocal())) {
                            if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2072) { // Crate
                                ScriptData.currentGameObject = GameObjects.closest(2072);
                                return ScriptData.returnMSFast();
                            }
                            else if (ScriptData.currentGameObject.interact("Fill")) {
                                Sleep.sleepUntil(() -> !Inventory.contains("Banana") || ScriptData.questOrderI == 4, ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            }
                        }
                        else if (!ScriptData.walkToArea(CRATE_AREA)) {
                            return ScriptData.returnMSFast();
                        }
                        break;
                    case 4: // Put rum in crate
                        if (!Inventory.contains(431)) {
                            ScriptData.questOrderI = 5;
                            return ScriptData.returnMSFast();
                        }
                        else if (CRATE_AREA.contains(Players.getLocal())) {
                            if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2072) { // Crate
                                ScriptData.currentGameObject = GameObjects.closest(2072);
                                return ScriptData.returnMSFast();
                            }
                            else if (Inventory.isItemSelected()) {
                                if (ScriptData.currentGameObject.interact()) {
                                    Sleep.sleepUntil(() -> !Inventory.contains(431), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                                }
                            }
                            else {
                                Inventory.use(431);
                            }
                        }
                        else if (!ScriptData.walkToArea(CRATE_AREA)) {
                            return ScriptData.returnMSFast();
                        }
                        break;
                    case 5: // Collect payment
                        if (luthasPaymentTotal != PlayerSettings.getConfig(4512)) {
                            ScriptData.questOrderI = 6;
                            return ScriptData.returnMSFast();
                        }
                        else if (!ScriptData.interactWithNPC(3647, LUTHAS_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                            return ScriptData.returnMSFast();
                        }
                        break;
                    case 6: // Equip White apron
                        if (Equipment.contains(1005)) { // White apron
                            ScriptData.questOrderI = 7;
                            return ScriptData.returnMSFast();
                        }
                        else if (Inventory.contains(1005)) {
                            if (Inventory.interact(1005, "Wear")) {
                                Sleep.sleepUntil(() -> Equipment.contains(1005), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            }
                        }
                        else if (WHITE_APRON_AREA.contains(Players.getLocal())) {
                            if (ScriptData.currentGroundItem == null || !ScriptData.currentGroundItem.exists()) {
                                ScriptData.currentGroundItem = GroundItems.closest(7957); // White apron (on wall)
                                return ScriptData.returnMSFast();
                            }
                            else if (ScriptData.currentGroundItem.interact("Take")) {
                                Sleep.sleepUntil(() -> Inventory.contains(1005), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            }
                        }
                        else if (!ScriptData.walkToArea(WHITE_APRON_AREA)) {
                            return ScriptData.returnMSFast();
                        }
                        break;
                    case 7:
                        if (Inventory.contains(431)) {
                            ScriptData.questOrderI = 8;
                            return ScriptData.returnMSFast();
                        }
                        else if (FOOD_STORE_BACK.contains(Players.getLocal())) {
                            if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 2071) {
                                ScriptData.currentGameObject = GameObjects.closest(2071);
                                return ScriptData.returnMSFast();
                            }
                            else if (ScriptData.currentGameObject.interact("Search")) {
                                Sleep.sleepUntil(() -> Inventory.contains(431), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            }
                        }
                        else if (!ScriptData.walkToArea(FOOD_STORE_BACK)) {
                            return ScriptData.returnMSFast();
                        }
                        break;
                    case 8:
                        if (!ScriptData.interactWithNPC(3643, REDBEARD_FRANK_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                            return ScriptData.returnMSFast();
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
                            Sleep.sleepUntil(() -> Inventory.contains(433), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        }
                    }
                    else {
                        Inventory.use(432); // Chest key
                    }
                }
                else if (!ScriptData.walkToArea(BLUE_MOON_INN_CHEST_AREA)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 3:
                if (!Inventory.contains(952)) { // Spade
                    if (!ScriptData.interactWithGroundItemSingle(952, SPADE_AREA, "Take")) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (FALADOR_PARK.contains(Players.getLocal())) {
                    if ((ScriptData.currentCharacter = Players.getLocal().getCharacterInteractingWithMe()) != null && ScriptData.currentCharacter.hasAction("Attack")) {
                        if (ScriptData.currentCharacter.interact("Attack")) {
                            Sleep.sleepUntil(() -> ScriptData.currentCharacter == null || !ScriptData.currentCharacter.exists(), ScriptData.SECURE_RANDOM.nextInt(30000 - 20000 + 1) + 20000, 300);
                        }
                    }
                    else if (DIG_TILE.equals(Players.getLocal().getTile())) {
                        if (Inventory.interact(952, "Dig")) {
                            Sleep.sleepUntil(() -> Players.getLocal().getCharacterInteractingWithMe() != null || Inventory.contains(7956), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                            return ScriptData.returnMSFast();
                        }
                    }
                    else if (!ScriptData.walkToTile(DIG_TILE)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (!ScriptData.walkToArea(FALADOR_PARK)) {
                    return ScriptData.returnMSFast();
                }
                break;
        }

        return ScriptData.returnMSNormal();
    }

}
