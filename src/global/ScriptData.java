package global;

import framework.SCScript;
import framework.ScriptState;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import services.LoadOutService;

public class ScriptData {

    public static Area GRAND_EXCHANGE = new Area(3145, 3508, 3186, 3472);

    public static void depositInvItemsNotInLoadOut() {
        for (Item item : Inventory.toArray()) {
            if (item != null) {
                boolean itemInLoadOut = false;
                int id = item.getId();
                int invCount = Inventory.count(id);
                for (byte j = 0; j < PlayerData.currentLoadOutService.getInvSize(); j++) {
                    if (id == PlayerData.currentLoadOutService.getInvItemID(j)) {
                        itemInLoadOut = true;
                        if (invCount > PlayerData.currentLoadOutService.getInvItemQtyMax(j) && SCScript.BANKING_SERVICE.depositNotContainsID(id)) {
                            SCScript.BANKING_SERVICE.addItemToDeposit(id, invCount - PlayerData.currentLoadOutService.getInvItemQtyMax(j));
                        }
                        break;
                    }
                }
                if (!itemInLoadOut) {
                    boolean itemInDeposit = false;
                    for (byte k = 0; k < SCScript.BANKING_SERVICE.getDepositSize(); k++) {
                        if (SCScript.BANKING_SERVICE.getDepositId(k) == id) {
                            itemInDeposit = true;
                            break;
                        }
                    }
                    if (!itemInDeposit && SCScript.BANKING_SERVICE.depositNotContainsID(id)) {
                        SCScript.BANKING_SERVICE.addItemToDeposit(id, invCount);
                    }
                }
            }
        }
    }

    public static Area currentArea;
    public static Tile currentTile;
    public static GroundItem currentGroundItem;
    public static GameObject currentGameObject;
    public static Character currentCharacter;
    public static NPC currentNPC;
    public static WidgetChild currentWidgetChild;
    public static String currentEntityName;
    public static String currentEntityAction;
    public static int currentEntityDistTrs;

    public static void interactWithNPC(int npcID, Area a, String action, Condition sleepUntil) {
        if (a.contains(Players.getLocal())) {

        }
        else {

        }

    }

    public static final Condition SHOULD_WALK = () -> Walking.shouldWalk(SCScript.SECURE_RANDOM.nextInt(7 - 4 + 1) + 4);
    public static void walkToArea(Area a) {
        if (Walking.walk(a)) {
            Sleep.sleepUntil(SHOULD_WALK, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000);
        }
    }
    public static void walkToEntity(Entity e) {
        if (Walking.walk(e)) {
            Sleep.sleepUntil(SHOULD_WALK, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000);
        }
    }

    public static final Condition COLLECTED_ITEM = () -> !GrandExchange.isBuyOpen() || currentWidgetChild == null || currentWidgetChild.getActions() == null;
    public static final Condition GRAND_EXCHANGE_IS_OPEN = GrandExchange::isOpen;
    public static final Condition GRAND_EXCHANGE_CLOSED = () -> !GrandExchange.isOpen();
    public static final Condition GRAND_EXCHANGE_READY_TO_COLLECT = GrandExchange::isReadyToCollect;

    public static ScriptState stateToReturnTo; // Post Buy/Sell/Bank

    public static LoadOutService currentLoadOutService;
    public static ScriptState currentProgressionTask;

}
