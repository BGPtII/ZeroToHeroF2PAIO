package nodes;

import framework.Node;
import framework.SCScript;
import framework.ScriptState;
import data.global.PlayerData;
import data.global.ScriptData;
import nodes.moneymakingtasks.ChoppingLogs;
import nodes.moneymakingtasks.SmeltingBronzeBars;
import nodes.moneymakingtasks.SpinningBallsOfWool;
import nodes.progressiontasks.training.*;
import nodes.services.Banking;
import nodes.services.BuyItems;
import nodes.services.SellItems;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Randoms;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import data.LoadOutData;

public class InitializeScript implements Node {

    private byte setUpStage;

    private void initializeTierFood() {
        switch (Randoms.random(4)) {
            case 0: // Shrimps
                PlayerData.foodTier1 = 315;
                PlayerData.foodHPTier1 = 3;
                break;
            case 1: // Anchovies
                PlayerData.foodTier1 = 319;
                PlayerData.foodHPTier1 = 1;
                break;
            case 2: // Sardine
                PlayerData.foodTier1 = 325;
                PlayerData.foodHPTier1 = 4;
                break;
            case 3: // Herring
                PlayerData.foodTier1 = 347;
                PlayerData.foodHPTier1 = 5;
                break;
        }
        switch (Randoms.random(5)) {
            case 0: // Pike
                PlayerData.foodTier2 = 351;
                PlayerData.foodHPTier2 = 8;
                break;
            case 1: // Salmon
                PlayerData.foodTier2 = 329;
                PlayerData.foodHPTier2 =9;
                break;
            case 2: // Trout
                PlayerData.foodTier2 = 333;
                PlayerData.foodHPTier2 = 7;
                break;
            case 3: // Herring
                PlayerData.foodTier2 = 347;
                PlayerData.foodHPTier2 = 5;
                break;
            case 4: // Tuna
                PlayerData.foodTier2 = 361;
                PlayerData.foodHPTier2 = 10;
                break;
        }
        Logger.log("foodTier1: " + PlayerData.foodTier1);
        Logger.log("foodTier2: " + PlayerData.foodTier2);
    }

    private void initializeCape() {
        PlayerData.cape = 4315 + Randoms.random(50) * 2; // IDs: 4315-4413, step 2
        Logger.log("cape: " + PlayerData.cape);
    }

    private void initializeRuneCraftingMediums() {
        for (byte i = 0; i < 4; i++) {
            int roll = SCScript.SECURE_RANDOM.nextInt(2);
            switch (i) {
                case 0: // Air
                    if (roll == 0) {
                        PlayerData.runecraftMediums[i] = 1438; // Air talisman
                    }
                    else {
                        PlayerData.runecraftMediums[i] = 5527; // Air tiara
                    }
                    break;
                case 1: // Earth
                    if (roll == 0) {
                        PlayerData.runecraftMediums[i] = 1440;
                    }
                    else {
                        PlayerData.runecraftMediums[i] = 5535;
                    }
                    break;
                case 2: // Fire
                    if (roll == 0) {
                        PlayerData.runecraftMediums[i] = 1442;
                    }
                    else {
                        PlayerData.runecraftMediums[i] = 5537;
                    }
                    break;
                case 3: // Body
                    if (roll == 0) {
                        PlayerData.runecraftMediums[i] = 1446;
                    }
                    else {
                        PlayerData.runecraftMediums[i] = 5533;
                    }
                    break;
            }
        }
    }

    private void initializeLoadOut() {
        ScriptData.currentLoadOutData = new LoadOutData(1, 1, Inventory::isFull);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.axe, 0, 0);
        ScriptData.currentLoadOutData.addInventoryItem(PlayerData.axe, 0, 0, 0);
        SCScript.TASK_LOAD_OUTS[ScriptState.WOODCUTTING_TRAINING.ordinal()] = ScriptData.currentLoadOutData; // Woodcutting

        ScriptData.currentLoadOutData = new LoadOutData(1, 1, Inventory::isFull);
        ScriptData.currentLoadOutData.addInventoryItem(PlayerData.pickaxe, 0, 0, 0);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.pickaxe, 0, 0);
        SCScript.TASK_LOAD_OUTS[ScriptState.MINING_TRAINING.ordinal()] = ScriptData.currentLoadOutData; // Mining

        ScriptData.currentLoadOutData = new LoadOutData(2, 1, Inventory::isFull);
        ScriptData.currentLoadOutData.addInventoryItem(0, 0, 0, 0);
        ScriptData.currentLoadOutData.addInventoryItem(0, 0, 0, 0);
        SCScript.TASK_LOAD_OUTS[ScriptState.FISHING_TRAINING.ordinal()] = ScriptData.currentLoadOutData; // Fishing

        ScriptData.currentLoadOutData = new LoadOutData(2, 7, null);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.meleeHat, 1, 1);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.meleeChest, 1, 1);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.meleeLegs, 1, 1);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.meleeShield, 1, 1);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.meleeWeapon, 1, 1);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.AMULET, 1, 1);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.cape, 1, 1);
        ScriptData.currentLoadOutData.addInventoryItem(983, 0, 0, 0); // Increase to 1 when rolling Hill Giants in Edgeville Dungeon
        ScriptData.currentLoadOutData.addInventoryItem(PlayerData.food, 0, 0, 0); // Change max/init values every task roll
        ScriptData.currentLoadOutData.setShouldBank(() -> Inventory.count(PlayerData.food) < ScriptData.currentLoadOutData.getInvItemQtyMin(1)
                || (Inventory.isFull() && (ScriptData.currentLoadOutData.getInvItemID(0) == 0
                    || !Inventory.contains(PlayerData.food)
                    || Skills.getBoostedLevel(Skill.HITPOINTS) + PlayerData.foodHP > Skills.getRealLevel(Skill.HITPOINTS))));
        SCScript.TASK_LOAD_OUTS[ScriptState.MELEE_TRAINING.ordinal()] = ScriptData.currentLoadOutData; // Melee

        ScriptData.currentLoadOutData = new LoadOutData(2, 8, null);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.rangedHat, 1, 1);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.rangedChest, 1, 1);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.rangedLegs, 1, 1);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.rangedHands, 1, 1);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.rangedArrows, 0, 0); // Determine init and min once per task
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.rangedWeapon, 1, 1); // Determine init and min once per task
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.AMULET, 1, 1);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.cape, 1, 1);
        ScriptData.currentLoadOutData.addInventoryItem(983, 0, 0, 0); // Increase to 1 when rolling Hill Giants in Edgeville Dungeon
        ScriptData.currentLoadOutData.addInventoryItem(PlayerData.food, 0, 0, 0); // Change max/init values every task roll
        ScriptData.currentLoadOutData.setShouldBank(() -> Inventory.count(PlayerData.food) < ScriptData.currentLoadOutData.getInvItemQtyMin(1)
                || Equipment.count(PlayerData.rangedArrows) < ScriptData.currentLoadOutData.getEqpItemQtyMin(4));
        SCScript.TASK_LOAD_OUTS[ScriptState.RANGED_TRAINING.ordinal()] = ScriptData.currentLoadOutData; // Ranged

        ScriptData.currentLoadOutData = new LoadOutData(2, 1, () -> !Inventory.contains(7936)); // Pure essence
        ScriptData.currentLoadOutData.addEquipmentItem(0, 0, 0);
        ScriptData.currentLoadOutData.addInventoryItem(0, 0, 0, 0);
        ScriptData.currentLoadOutData.addInventoryItem(0, 0, 0, 0);
        SCScript.TASK_LOAD_OUTS[ScriptState.RUNECRAFT_TRAINING.ordinal()] = ScriptData.currentLoadOutData; // Runecraft

        ScriptData.currentLoadOutData = new LoadOutData(2, 0, () -> Inventory.isFull() && !Inventory.contains(1737));
        ScriptData.currentLoadOutData.addInventoryItem(1735, 0, 1, 0); // Shears
        ScriptData.currentLoadOutData.addInventoryItem(1737, 0, 27, 0); // Wool
        SCScript.TASK_LOAD_OUTS[ScriptState.SPINNING_BALLS_OF_WOOL.ordinal()] = ScriptData.currentLoadOutData; // SpinningBallsOfWool

        ScriptData.currentLoadOutData = new LoadOutData(3, 1, () -> Inventory.contains(2349) && !Inventory.containsAll(438, 436)); // Tin ore, Copper ore
        ScriptData.currentLoadOutData.addInventoryItem(PlayerData.bestPickaxeAvail, 0, 0, 0);
        ScriptData.currentLoadOutData.addInventoryItem(438, 0, 14, 0); // Tin ore
        ScriptData.currentLoadOutData.addInventoryItem(436, 0, 14, 0); // Copper ore
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.bestPickaxeAvail, 0, 0);
        SCScript.TASK_LOAD_OUTS[ScriptState.SMELTING_BRONZE_BARS.ordinal()] = ScriptData.currentLoadOutData; // SmeltingBronzeBars

        ScriptData.currentLoadOutData = new LoadOutData(1, 1, Inventory::isFull);
        ScriptData.currentLoadOutData.addInventoryItem(PlayerData.bestAxeAvail, 0, 0, 0);
        ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.bestAxeAvail, 0, 0);
        SCScript.TASK_LOAD_OUTS[ScriptState.CHOPPING_LOGS.ordinal()] = ScriptData.currentLoadOutData; // ChoppingLogs
    }

    private void initializeNodes() { // Don't initialize quests
        SCScript.NODES[ScriptState.DETERMINE_TASK.ordinal()] = new DetermineTask();
        SCScript.NODES[ScriptState.BUY_ITEMS.ordinal()] = new BuyItems();
        SCScript.NODES[ScriptState.SELL_ITEMS.ordinal()] = new SellItems();
        SCScript.NODES[ScriptState.BANKING.ordinal()] = new Banking();
        SCScript.NODES[ScriptState.INITIALIZE_TASK.ordinal()] = new InitializeTask();

        SCScript.NODES[ScriptState.WOODCUTTING_TRAINING.ordinal()] = new WoodcuttingTraining();
        SCScript.NODES[ScriptState.MINING_TRAINING.ordinal()] = new MiningTraining();
        SCScript.NODES[ScriptState.FISHING_TRAINING.ordinal()] = new FishingTraining();
        SCScript.NODES[ScriptState.MELEE_TRAINING.ordinal()] = new MeleeTraining();
        SCScript.NODES[ScriptState.RANGED_TRAINING.ordinal()] = new RangedTraining();
        SCScript.NODES[ScriptState.RUNECRAFT_TRAINING.ordinal()] = new RunecraftTraining();

        SCScript.NODES[ScriptState.SPINNING_BALLS_OF_WOOL.ordinal()] = new SpinningBallsOfWool();
        SCScript.NODES[ScriptState.SMELTING_BRONZE_BARS.ordinal()] = new SmeltingBronzeBars();
        SCScript.NODES[ScriptState.CHOPPING_LOGS.ordinal()] = new ChoppingLogs();
    }

    private void initializeTaskWeights() {
        SCScript.TASK_WEIGHTS[ScriptState.WOODCUTTING_TRAINING.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.MINING_TRAINING.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.FISHING_TRAINING.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.MELEE_TRAINING.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.RANGED_TRAINING.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.RUNECRAFT_TRAINING.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;

        SCScript.TASK_WEIGHTS[ScriptState.THE_RESTLESS_GHOST.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.COOKS_ASSISTANT.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.RUNE_MYSTERIES.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.X_MARKS_THE_SPOT.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.PIRATES_TREASURE.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.WITCHS_POTION.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.ERNEST_THE_CHICKEN.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.THE_KNIGHTS_SWORD.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.MISTHALIN_MYSTERY.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;

        SCScript.TASK_WEIGHTS[ScriptState.SPINNING_BALLS_OF_WOOL.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.SMELTING_BRONZE_BARS.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        SCScript.TASK_WEIGHTS[ScriptState.CHOPPING_LOGS.ordinal()] = SCScript.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
    }

    @Override
    public int loop() {
        if (Client.isLoggedIn() && Skills.getRealLevel(Skill.HITPOINTS) != 1) {
            switch (setUpStage) {
                case 0:
                    setUpStage++;
                    initializeTierFood();
                    PlayerData.determineFood(Skills.getRealLevel(Skill.HITPOINTS));
                    PlayerData.determineEatFoodHPTrs();
                    initializeCape();
                    PlayerData.initializeAxe(Skills.getRealLevel(Skill.WOODCUTTING), Skills.getRealLevel(Skill.ATTACK));
                    PlayerData.initializePickaxe(Skills.getRealLevel(Skill.MINING), Skills.getRealLevel(Skill.ATTACK));
                    PlayerData.initializeBestPickaxeAvail(Skills.getRealLevel(Skill.MINING), Skills.getRealLevel(Skill.ATTACK));
                    PlayerData.initializeBestAxeAvail(Skills.getRealLevel(Skill.WOODCUTTING), Skills.getRealLevel(Skill.ATTACK));

                    PlayerData.initializeMeleeWeapon(Skills.getRealLevel(Skill.ATTACK));
                    PlayerData.initializeMeleeArmour(Skills.getRealLevel(Skill.DEFENCE));

                    PlayerData.initializeRangedHat(Skills.getRealLevel(Skill.RANGED));
                    PlayerData.initializeRangedChest(Skills.getRealLevel(Skill.RANGED), Skills.getRealLevel(Skill.DEFENCE));
                    PlayerData.initializeRangedLegs(Skills.getRealLevel(Skill.RANGED));
                    PlayerData.initializeRangedHands(Skills.getRealLevel(Skill.RANGED));
                    PlayerData.initializeRangedWeaponArrows(Skills.getRealLevel(Skill.RANGED));

                    initializeRuneCraftingMediums();
                    PlayerData.initializeCurrentRunecraftMedium(Skills.getRealLevel(Skill.RUNECRAFTING));

                    initializeNodes();
                    initializeTaskWeights();
                    initializeLoadOut();
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                case 1:
                    if (Bank.isOpen()) {
                        Bank.updateCache();
                        SCScript.NODES[ScriptState.INITIALIZE_SCRIPT.ordinal()] = null;
                        SCScript.scriptState = ScriptState.DETERMINE_TASK;
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else {
                        Bank.open(); // Bank#open returns true if Bank was opened successfully
                        Sleep.sleepUntil(ScriptData.SHOULD_WALK, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                    }
                    break;
            }
        }
        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
