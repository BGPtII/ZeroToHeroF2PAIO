package data.global;

import data.LoadOut;
import framework.Pipeline;
import loopinterceptors.*;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.AccountManager;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import pipelines.BankingPipeline;

import java.security.SecureRandom;
import java.util.List;

/**
 * - 0: SKILLS: Cooking
 * - 1: Firemaking
 * - 2: Fishing
 * - 3: Melee (att/str/def)
 * - 4: Mining
 * - 5: Ranged
 * - 6: Runecrafting
 * - 7: Smithing
 * - 8: Woodcutting
 * - 9: QUESTS: Below Ice Mountain
 * - 10: Black Knights' Fortress
 * - 11: Cook's Assistant
 * - 12: Demon Slayer
 * - 13: Doric's Quest
 * - 14: Ernest the Chicken
 * - 15: Goblin Diplomacy
 * - 16: Imp Catcher
 * - 17: Misthalin Mystery
 * - 18: Pirate's Treasure
 * - 19: Prince Ali Rescue
 * - 20: Romeo and Juliet
 * - 21: Rune Mysteries
 * - 22: Sheep Shearer
 * - 23: The Corsair Curse
 * - 24: The Knight's Sword
 * - 25: The Restless Ghost
 * - 26: Vampire Slayer
 * - 27: Witch's Potion
 * - 28: X Marks the Spot
 * - 29: SECONDARIES: Chopping Logs
 * - 30: Mining Ore
 * - 31: Smelting Bars
 * - 32: Spinning Balls of Wool
 * - 33: UTILITY: DetermineTask
 * - 34: Banking
 * - 35: BuyItems
 * - 36: SellItems
 */
public class ScriptData {

    public static final SecureRandom SECURE_RANDOM = new SecureRandom((AccountManager.getAccountUsername() + AccountManager.getAccountBankPin() + AccountManager.getAccountTOTPKey()).getBytes());

    public static final int[] TASK_WEIGHTS = new int[33];
    public static final LoadOut[] TASK_LOAD_OUTS = new LoadOut[33];
    public static final Pipeline[] PIPELINES = new Pipeline[37]; // 33 - DetermineTask, 34 - Banking, 35 - BuyItemsLI, 36 - SellItemsLI
    public static int progressionTaskWeightTotal;
    public static int secondaryTaskWeightTotal;

    public static byte currentPipelineI = 33;
    public static byte currentProgressionTaskI = -1;
    public static byte currentSecondaryTaskI = -1;
    public static byte taskType; // 0 - primary, 1 - secondary, 2 - return to primary from secondary, 3 - re-initialize primary
    public static FreeQuest currentFreeQuest;
    public static Timer progressionTaskTimer;
    public static Timer secondaryTaskTimer;

    public static Timer changePlayerSetUpTimer; // Chat tabs, Game settings
    public static byte[] playerSetUpOpts;
    public static byte[] playerSetUpValues; // 1 - on, 0 - off
    public static byte playerSetUpI;

    public static boolean useLevelUpEvent = true;
    public static boolean useOnGameMessageEvent = false;
    public static byte unPauseTimer; // 0 - None, 1 - progressionTask, 2 - moneyMaking
    public static byte unPauseSetUpClientTimer; // 0 - no, 1 - yes

    public static Area currentArea, currentArea2, currentArea3 = new Area(0, 0, 0, 0);
    public static Tile currentTile;
    public static GroundItem currentGroundItem;
    public static GameObject currentGameObject;
    public static Character currentCharacter;
    public static NPC currentNPC;
    public static WidgetChild currentWidgetChild;
    public static String currentEntityName;
    public static String currentEntityAction;
    public static int currentEntityDistance;

    public static final Condition GRAND_EXCHANGE_IS_OPEN = GrandExchange::isOpen;
    public static final Condition GRAND_EXCHANGE_CLOSED = () -> !GrandExchange.isOpen();
    public static final Condition GRAND_EXCHANGE_READY_TO_COLLECT = GrandExchange::isReadyToCollect;
    public static final Condition IN_DIALOGUE = Dialogues::inDialogue;
    public static final Condition GROUND_ITEM_NOT_EXISTS_NULL = () -> currentGroundItem == null || !currentGroundItem.exists();
    public static final Condition INTERACTED_WITH_TARGET = () -> currentNPC == null || !currentNPC.exists() || currentNPC.getCharacterInteractingWithMe() != null;
    public static final Condition DONE_RESOURCE_GATHERING = () -> currentGameObject == null || !currentGameObject.exists() || Dialogues.inDialogue() || Inventory.isFull();
    public static final Condition CURRENT_FREE_QUEST_NOT_FINISHED = () -> !currentFreeQuest.isFinished() && !Dialogues.canContinue() && !Dialogues.areOptionsAvailable() && !Client.isInCutscene();
    public static final Condition SHOULD_WALK = () -> Walking.shouldWalk(SECURE_RANDOM.nextInt(7 - 4 + 1) + 4);
    public static final Condition IN_CURRENT_AREA = () -> currentArea.contains(Players.getLocal());
    public static final Condition INVENTORY_FULL = Inventory::isFull;
    public static final Condition STOPPED_PROCESSING = () -> ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].shouldBank().verify() || Dialogues.canContinue() || Dialogues.areOptionsAvailable();

    public static BankWithdrawModeLI bankWithdrawModeLI = new BankWithdrawModeLI();
    public static DepositAllEqpLI depositAllEqpLI = new DepositAllEqpLI();
    public static DepositAllInvLI depositAllInvLI = new DepositAllInvLI();
    public static DepositLI depositLI = new DepositLI();
    public static WithdrawLI withdrawLI = new WithdrawLI();
    public static WoodcuttingLI woodcuttingLI = new WoodcuttingLI();
    public static MiningLI miningLI = new MiningLI();
    public static DialogueOptionsLI dialogueOptionsLI = new DialogueOptionsLI();
    public static ContinueDialogueLI continueDialogueLI = new ContinueDialogueLI();
    public static CheckLoadOutLI checkLoadOutLI = new CheckLoadOutLI();
    public static WalkToCurrentAreaLI walkToCurrentAreaLI = new WalkToCurrentAreaLI();
    public static FindValidNPCTargetLI findValidNPCTargetLI = new FindValidNPCTargetLI();
    public static LootNPCDropsLI lootNPCDropsLI = new LootNPCDropsLI();
    public static CheckMeleeCombatStyleLI checkMeleeCombatStyleLI = new CheckMeleeCombatStyleLI();
    public static CheckRangedCombatStyleLI checkRangedCombatStyleLI = new CheckRangedCombatStyleLI();
    public static InCutsceneLI inCutsceneLI = new InCutsceneLI();
    public static FinishQuestLI finishQuestLI = new FinishQuestLI();
    public static AttackTargetNPCLI attackTargetNPCLI = new AttackTargetNPCLI();
    public static CantReachCurrentNPCLI cantReachCurrentNPCLI = new CantReachCurrentNPCLI();
    public static EatChosenFoodLI eatChosenFoodLI = new EatChosenFoodLI();
    public static CurrentTileToTargetNPCTileLI currentTileToTargetNPCTileLI = new CurrentTileToTargetNPCTileLI();
    public static SecondaryTaskTimerFinishedLI secondaryTaskTimerFinishedLI = new SecondaryTaskTimerFinishedLI();
    public static ProgressionTaskTimerFinishedLI progressionTaskTimerFinishedLI = new ProgressionTaskTimerFinishedLI();
    public static BuyItemsLI buyItemsLI = new BuyItemsLI();
    public static SellItemsLI sellItemsLI = new SellItemsLI();
    public static ChangePlayerSetUpLI changePlayerSetUpLI = new ChangePlayerSetUpLI();
    public static OpenInventoryLI openInventoryLI = new OpenInventoryLI();

    public static BankingPipeline bankingPipeline = new BankingPipeline(continueDialogueLI, dialogueOptionsLI, new WalkToOpenBankLI(), bankWithdrawModeLI, withdrawLI, depositLI, depositAllInvLI, depositAllEqpLI);

    public static Area GRAND_EXCHANGE = new Area(3145, 3508, 3186, 3472);

    public static void resetCurrentEntityDistance() {
        currentEntityDistance = SECURE_RANDOM.nextInt(5 + 1 - 1) + 1;
    }
    public static void incrementCurrentEntityDistance() {
        currentEntityDistance = Math.min(50, currentEntityDistance + SECURE_RANDOM.nextInt(5 + 1 - 1) + 1);
    }

    public static void resetEntities() {
        resetCurrentEntityDistance();
        currentGameObject = null;
        currentNPC = null;
        currentGroundItem = null;
        currentCharacter = null;
    }

    public static boolean interactWithNPC(int npcID, Area a, String action, Condition sleepUntil) {
        if (a.contains(Players.getLocal())) {
            if (currentNPC == null || !currentNPC.exists() || currentNPC.getId() != npcID) {
                currentNPC = NPCs.closest(npcID);
                return false;
            }
            if (currentNPC.canReach()) {
                if (currentNPC.interact(action)) {
                    Sleep.sleepUntil(sleepUntil, SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                    return sleepUntil.verify();
                }
                return false;
            }
            return walkToEntity(currentNPC);
        }
        return walkToArea(a);
    }


    public static boolean walkToArea(Area a) {
        if (Walking.walk(a)) {
            Tile destTile = Walking.getDestination();
            if (destTile != null && a.contains(destTile)) {
                Sleep.sleepUntil(() -> a.contains(Players.getLocal()), SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000);
            }
            else {
                Sleep.sleepUntil(SHOULD_WALK, SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000);
            }
            return true;
        }
        return false;
    }
    public static boolean walkToEntity(Entity e) {
        if (Walking.walk(e)) {
            Sleep.sleepUntil(SHOULD_WALK, SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000);
            return true;
        }
        return false;
    }
    public static boolean walkToTile(Tile t) {
        if (Walking.walk(t)) {
            Tile walkingDest = Walking.getDestination();
            if (walkingDest != null && walkingDest.equals(t)) {
                Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(t), SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000);
            }
            else {
                Sleep.sleepUntil(SHOULD_WALK, SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000);
            }
            return true;
        }
        return false;
    }

    public static byte[] questOrder = null;
    public static byte questOrderI;
    public static String[] dialogueOpts;

    public static final Condition CURRENT_WIDGET_CHILD_NULL_NOT_VISIBLE = () -> currentWidgetChild == null || !currentWidgetChild.isVisible();
    public static boolean closeQuestCompletionWidget() {
        if ((currentWidgetChild = Widgets.get(153, 16)) != null && currentWidgetChild.isVisible() && currentWidgetChild.interact("Close")) { // Close quest completion
            Sleep.sleepUntil(CURRENT_WIDGET_CHILD_NULL_NOT_VISIBLE, SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
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
                currentGroundItem = opts.get(SECURE_RANDOM.nextInt(opts.size()));
                return false;
            }
        }
        if (!currentGroundItem.canReach()) {
            return walkToEntity(currentGroundItem);
        }
        if (currentGroundItem.interact(action)) {
            Sleep.sleepUntil(CURRENT_GROUND_ITEM_NULL_NOT_EXISTS, SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
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
            Sleep.sleepUntil(CURRENT_GROUND_ITEM_NULL_NOT_EXISTS, SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
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
                currentGameObject = opts.get(SECURE_RANDOM.nextInt(opts.size()));
                return false;
            }
        }
        if (!currentGameObject.canReach()) {
            return walkToEntity(currentGameObject);
        }
        if (currentGameObject.interact(action)) {
            Sleep.sleepUntil(CURRENT_GAME_OBJECT_NULL_NOT_EXISTS, SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
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
                currentGameObject = opts.get(SECURE_RANDOM.nextInt(opts.size()));
                return false;
            }
        }
        if (!currentGameObject.canReach()) {
            return walkToEntity(currentGameObject);
        }
        if (currentGameObject.interact(action)) {
            Sleep.sleepUntil(CURRENT_GAME_OBJECT_NULL_NOT_EXISTS, SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
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
            Sleep.sleepUntil(sleepUntil, SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
            return sleepUntil.verify();
        }
        return false;
    }

    public static void finishProgressionQuestingTask() {
        TASK_LOAD_OUTS[currentPipelineI] = null;
        PIPELINES[currentPipelineI] = null;
        useOnGameMessageEvent = false;
        dialogueOpts = null;
        questOrder = null;
        currentTile = null;
        questOrderI = 0;
        taskType = 0;
        currentPipelineI = 33;
    }

    public static int returnMSFast() { // Failed interaction, selecting Entity
        int roll = SECURE_RANDOM.nextInt(100);
        if (roll < 60) return SECURE_RANDOM.nextInt(260 - 120 + 1) + 120;
        if (roll < 85) return SECURE_RANDOM.nextInt(600 - 260 + 1) + 260;
        if (roll < 95) return SECURE_RANDOM.nextInt(1200 - 600 + 1) + 600;
        return SECURE_RANDOM.nextInt(3000 - 1200 + 1) + 1200;
    }

    public static int returnMSNormal() { // Successful click, Finished walking, UI open
        int roll = SECURE_RANDOM.nextInt(100);
        if (roll < 55) {
            return SECURE_RANDOM.nextInt(850 - 450 + 1) + 450;
        }
        if (roll < 80) {
            return SECURE_RANDOM.nextInt(1600 - 850 + 1) + 850;
        }
        if (roll < 93) {
            return SECURE_RANDOM.nextInt(3500 - 1600 + 1) + 1600;
        }
        return SECURE_RANDOM.nextInt(7000 - 3500 + 1) + 3500;
    }

    public static boolean rollChance(double percent) {
        return SECURE_RANDOM.nextDouble() * 100.0 < percent;
    }

    public static boolean hopWorldsWH() { // Uses WorldHopper
        List<World> worlds = Worlds.all(world -> world.isNormal() && world.isF2P() && world.getPopulation() <= 1950 && world.getMinimumLevel() == 0);
        return WorldHopper.hopWorld(worlds.get(SECURE_RANDOM.nextInt(worlds.size())));
    }

}
