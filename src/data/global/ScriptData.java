package data.global;

import framework.SCScript;
import framework.ScriptState;
import nodes.DetermineTask;
import nodes.InitializeTask;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import data.LoadOutData;

import java.util.List;

public class ScriptData {

    public static Timer progressionTaskTimer;
    public static Timer moneyMakingTaskTimer;

    public static Area GRAND_EXCHANGE = new Area(3145, 3508, 3186, 3472);

    public static Area currentArea, currentArea2, currentArea3;
    public static Tile currentTile;
    public static GroundItem currentGroundItem;
    public static GameObject currentGameObject;
    public static Character currentCharacter;
    public static NPC currentNPC;
    public static WidgetChild currentWidgetChild;
    public static String currentEntityName;
    public static String currentEntityAction;
    public static int currentEntityDistance;

    public static void resetCurrentEntityDistance() {
        currentEntityDistance = SCScript.SECURE_RANDOM.nextInt(5 + 1 - 1) + 1;
    }
    public static void incrementCurrentEntityDistance() {
        currentEntityDistance = Math.min(50, currentEntityDistance + SCScript.SECURE_RANDOM.nextInt(5 + 1 - 1) + 1);
    }

    public static void resetEntities() {
        resetCurrentEntityDistance();
        currentGameObject = null;
        currentNPC = null;
        currentGroundItem = null;
        currentCharacter = null;
        Logger.log("resetEntities");
    }

    public static boolean interactWithNPC(int npcID, Area a, String action, Condition sleepUntil) {
        if (a.contains(Players.getLocal())) {
            if (currentNPC == null || !currentNPC.exists() || currentNPC.getId() != npcID) {
                currentNPC = NPCs.closest(npcID);
                return false;
            }
            if (currentNPC.canReach()) {
                if (currentNPC.interact(action)) {
                    Sleep.sleepUntil(sleepUntil, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                    return sleepUntil.verify();
                }
                return false;
            }
            return walkToEntity(currentNPC);
        }
        return walkToArea(a);
    }

    public static final Condition SHOULD_WALK = () -> Walking.shouldWalk(SCScript.SECURE_RANDOM.nextInt(7 - 4 + 1) + 4);
    public static boolean walkToArea(Area a) {
        if (Walking.walk(a)) {
            Tile destTile = Walking.getDestination();
            if (destTile != null && a.contains(destTile)) {
                Sleep.sleepUntil(() -> a.contains(Players.getLocal()), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000);
            }
            else {
                Sleep.sleepUntil(SHOULD_WALK, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000);
            }
            return true;
        }
        return false;
    }
    public static boolean walkToEntity(Entity e) {
        if (Walking.walk(e)) {
            Sleep.sleepUntil(SHOULD_WALK, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000);
            Logger.log("Successfully walkedToEntity");
            return true;
        }
        return false;
    }
    public static boolean walkToTile(Tile t) {
        if (Walking.walk(t)) {
            Tile walkingDest = Walking.getDestination();
            if (walkingDest != null && walkingDest.equals(t)) {
                Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(t), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000);
            }
            else {
                Sleep.sleepUntil(SHOULD_WALK, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000);
            }
            return true;
        }
        return false;
    }

    public static final Condition GRAND_EXCHANGE_IS_OPEN = GrandExchange::isOpen;
    public static final Condition GRAND_EXCHANGE_CLOSED = () -> !GrandExchange.isOpen();
    public static final Condition GRAND_EXCHANGE_READY_TO_COLLECT = GrandExchange::isReadyToCollect;
    public static final Condition IN_DIALOGUE = Dialogues::inDialogue;
    public static final Condition GROUND_ITEM_NOT_EXISTS_NULL = () -> ScriptData.currentGroundItem == null || !ScriptData.currentGroundItem.exists();
    public static final Condition INTERACTED_WITH_TARGET = () -> ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || ScriptData.currentNPC.getCharacterInteractingWithMe() != null;
    public static final Condition DONE_RESOURCE_GATHERING = () -> ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || Dialogues.inDialogue() || Inventory.isFull();

    public static LoadOutData currentLoadOutData;

    public static ScriptState currentProgressionTask;
    public static ScriptState currentMoneyMakingTask;
    public static ScriptState returnTo; // Post-Buy/Sell/Bank
    public static ScriptState currentTask;

    public static byte[] questOrder = null;
    public static byte questOrderI;
    public static String[] dialogueOpts;

    public static final Condition CURRENT_WIDGET_CHILD_NULL_NOT_VISIBLE = () -> currentWidgetChild == null || !currentWidgetChild.isVisible();
    public static boolean closeQuestCompletionWidget() {
        if ((currentWidgetChild = Widgets.get(153, 16)) != null && currentWidgetChild.isVisible() && currentWidgetChild.interact("Close")) { // Close quest completion
            Sleep.sleepUntil(CURRENT_WIDGET_CHILD_NULL_NOT_VISIBLE, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
            return CURRENT_WIDGET_CHILD_NULL_NOT_VISIBLE.verify();
        }
        return false;
    }

    public static final Condition CURRENT_GROUND_ITEM_NULL_NOT_EXISTS = () -> currentGroundItem == null || !currentGroundItem.exists();
    public static boolean interactWithGroundItemMultiple(int groundItemID, Area a, String action) {
        if (!a.contains(Players.getLocal())) {
            return walkToArea(a);
        }
        if (currentGroundItem == null || !currentGroundItem.exists() || currentGroundItem.getId() != groundItemID) {
            List<GroundItem> opts;
            opts = GroundItems.all(groundItem -> a.contains(groundItem) && groundItem.hasAction(action) && groundItem.getId() == groundItemID);
            if (!opts.isEmpty()) {
                currentGroundItem = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                return false;
            }
        }
        if (!currentGroundItem.canReach()) {
            return walkToEntity(currentGroundItem);
        }
        if (currentGroundItem.interact(action)) {
            Sleep.sleepUntil(CURRENT_GROUND_ITEM_NULL_NOT_EXISTS, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            return CURRENT_GROUND_ITEM_NULL_NOT_EXISTS.verify();
        }
        return false;
    }
    public static boolean interactWithGroundItemSingle(int groundItemID, Area a, String action) {
        if (!a.contains(Players.getLocal())) {
            return walkToArea(a);
        }
        if (currentGroundItem == null || !currentGroundItem.exists() || currentGroundItem.getId() != groundItemID) {
            currentGroundItem = GroundItems.closest(groundItem -> a.contains(groundItem) && groundItem.hasAction(action) && groundItem.getId() == groundItemID);
            return false;
        }
        if (!currentGroundItem.canReach()) {
            return walkToEntity(currentGroundItem);
        }
        if (currentGroundItem.interact(action)) {
            Sleep.sleepUntil(CURRENT_GROUND_ITEM_NULL_NOT_EXISTS, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            return CURRENT_GROUND_ITEM_NULL_NOT_EXISTS.verify();
        }
        return false;
    }

    public static final Condition CURRENT_GAME_OBJECT_NULL_NOT_EXISTS = () -> currentGameObject == null || !currentGameObject.exists();
    public static boolean interactWithGameObjectMultiple(int gameObjectID, Area a, String action) {
        if (!a.contains(Players.getLocal())) {
            return walkToArea(a);
        }
        if (currentGameObject == null || !currentGameObject.exists() || currentGameObject.getId() != gameObjectID) {
            List<GameObject> opts;
            opts = GameObjects.all(gameObject -> a.contains(gameObject) && gameObject.hasAction(action) && gameObject.getId() == gameObjectID);
            if (!opts.isEmpty()) {
                currentGameObject = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                return false;
            }
        }
        if (!currentGameObject.canReach()) {
            return walkToEntity(currentGameObject);
        }
        if (currentGameObject.interact(action)) {
            Sleep.sleepUntil(CURRENT_GAME_OBJECT_NULL_NOT_EXISTS, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            return CURRENT_GAME_OBJECT_NULL_NOT_EXISTS.verify();
        }
        return false;
    }
    public static boolean interactWithGameObjectMultiple(String gameObjectName, Area a, String action) {
        if (!a.contains(Players.getLocal())) {
            return walkToArea(a);
        }
        if (currentGameObject == null || !currentGameObject.exists() || !currentGameObject.getName().equals(gameObjectName)) {
            List<GameObject> opts;
            opts = GameObjects.all(gameObject -> a.contains(gameObject) && gameObject.hasAction(action) && gameObject.getName().equals(gameObjectName));
            if (!opts.isEmpty()) {
                currentGameObject = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                return false;
            }
        }
        if (!currentGameObject.canReach()) {
            return walkToEntity(currentGameObject);
        }
        if (currentGameObject.interact(action)) {
            Sleep.sleepUntil(CURRENT_GAME_OBJECT_NULL_NOT_EXISTS, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            return CURRENT_GAME_OBJECT_NULL_NOT_EXISTS.verify();
        }
        return false;
    }
    public static boolean interactWithGameObjectSingle(int gameObjectID, Area a, String action) {
        if (!a.contains(Players.getLocal())) {
            return walkToArea(a);
        }
        if (currentGameObject == null || !currentGameObject.exists() || currentGameObject.getId() != gameObjectID) {
            currentGameObject = GameObjects.closest(gameObject -> a.contains(gameObject) && gameObject.hasAction(action) && gameObject.getId() == gameObjectID);
            return false;
        }
        if (!currentGameObject.canReach()) {
            return walkToEntity(currentGameObject);
        }
        if (currentGameObject.interact(action)) {
            Sleep.sleepUntil(CURRENT_GAME_OBJECT_NULL_NOT_EXISTS, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            return CURRENT_GAME_OBJECT_NULL_NOT_EXISTS.verify();
        }
        return false;
    }
    public static boolean interactWithGameObjectSingle(int gameObjectID, Area a, String action, Condition sleepUntil) {
        if (!a.contains(Players.getLocal())) {
            return walkToArea(a);
        }
        if (currentGameObject == null || !currentGameObject.exists() || currentGameObject.getId() != gameObjectID) {
            currentGameObject = GameObjects.closest(gameObject -> a.contains(gameObject) && gameObject.hasAction(action) && gameObject.getId() == gameObjectID);
            return false;
        }
        if (!currentGameObject.canReach()) {
            return walkToEntity(currentGameObject);
        }
        if (currentGameObject.interact(action)) {
            Sleep.sleepUntil(sleepUntil, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            return sleepUntil.verify();
        }
        return false;
    }

    public static void finishProgressionTrainingTask() {
        progressionTaskTimer = null;
        SCScript.scriptState = ScriptState.DETERMINE_TASK;
        DetermineTask.taskType = 0;
        Logger.log("Finished currentProgressionTask");
    }
    public static void finishProgressionQuestingTask() {
        SCScript.useOnGameMessageEvent = false;
        SCScript.NODES[SCScript.scriptState.ordinal()] = null;
        SCScript.TASK_LOAD_OUTS[SCScript.scriptState.ordinal()] = null;
        SCScript.scriptState = ScriptState.DETERMINE_TASK;
        DetermineTask.taskType = 0;
        InitializeTask.initializeTaskI = 0;
    }

    public static void finishMoneyMakingTask() {
        moneyMakingTaskTimer = null;
        SCScript.scriptState = ScriptState.DETERMINE_TASK;
        DetermineTask.taskType = 2;
        Logger.log("Finished moneyMakingTask");
    }

}
