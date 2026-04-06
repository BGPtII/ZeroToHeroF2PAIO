package framework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.PersistedScriptInfo;
import data.global.PlayerData;
import data.global.ScriptData;
import loopinterceptors.*;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Randoms;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.event.impl.ExperienceEvent;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ExperienceListener;
import org.dreambot.api.utilities.AccountManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.message.Message;
import data.LoadOut;
import pipelines.*;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Different thread for handling:
 * - Path Randomization + Timer, Client SetUp Timer, Check Trade Restrictions Timer, Progression Task Timer, Money Making Task Timer
 * - Will assign the corresponding ScriptState if needed
 * Purpose of this script:
 * - Account with high total level + high qp
 */
@ScriptManifest(category = Category.UTILITY, name = "ZeroToHeroF2PAio", description = "Builds an account post-Tutorial Island.", author = "Hikkens", version = 1.0)
public class SCScript extends AbstractScript implements ChatListener, ExperienceListener {

    @Override
    public void onPaint(Graphics2D g) {
        g.drawString("currentPipelineI: " + ScriptData.currentPipelineI, 10, 10);
        g.drawString("progressionTaskTimer: " + ((ScriptData.progressionTaskTimer == null) ? "null" : Timer.formatTime(ScriptData.progressionTaskTimer.remaining())), 10, 20);
        g.drawString("secondaryTaskTimer: " + ((ScriptData.secondaryTaskTimer == null) ? "null" : Timer.formatTime(ScriptData.secondaryTaskTimer.remaining())), 10, 30);
        g.drawString("changePlayerSetUpTimer: " + ((ScriptData.changePlayerSetUpTimer == null) ? "null" : Timer.formatTime(ScriptData.changePlayerSetUpTimer.remaining())), 10, 40);
        g.drawString("taskType:" + ScriptData.taskType, 10, 50);
    }

    @Override
    public void onGameMessage(Message message) {
        if (!ScriptData.useOnGameMessageEvent) {
            return;
        }
        String contents = message.getMessage();
        switch (ScriptData.currentPipelineI) {
            case 11: // Cook's Assistant
                if (contents.startsWith("You put the grain") || contents.startsWith("There is already grain")) {
                    ScriptData.questOrderI++;
                }
                break;
            case 14: // Ernest the Chicken
                if (contents.startsWith("...")) {
                    ScriptData.questOrderI++;
                }
                break;
            case 18: // Pirate's Treasure
                if (contents.startsWith("There is already some rum")) {
                    ScriptData.questOrderI = 6;
                }
                break;
        }
    }

    @Override
    public void onLevelUp(ExperienceEvent event) {
        if (!ScriptData.useLevelUpEvent) {
            return;
        }
        switch (event.getSkill()) {
            case WOODCUTTING:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 41 || i == 31 || i == 21 || i == 11) { // Could potentially miss if level-ups skip any of these specific levels, loop through as to not miss; grab the highest first
                        PlayerData.initializeAxe(i, Skills.getRealLevel(Skill.ATTACK));
                        if (PlayerData.canEquipAxe) {
                            ScriptData.TASK_LOAD_OUTS[8].setEquipmentItem(0, PlayerData.axe, 1, 1, 1);
                            ScriptData.TASK_LOAD_OUTS[8].setInventoryItem(0, 0, 0, 0);
                        }
                        else {
                            ScriptData.TASK_LOAD_OUTS[8].setEquipmentItem(0, 0, 0, 0, 0);
                            ScriptData.TASK_LOAD_OUTS[8].setInventoryItem(0, PlayerData.axe, 1, 1, 1);
                        }
                        break;
                    }
                }
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (ScriptData.currentPipelineI == 8 && (i == 30 || i == 15)) { // Switch treeType
                        ScriptData.currentPipelineI = 33; // Determine Task
                        ScriptData.taskType = 3;
                        break;
                    }
                }
                break;
            case MINING:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 41 || i == 31 || i == 21 || i == 11) {
                        PlayerData.initializePickaxe(i, Skills.getRealLevel(Skill.ATTACK));
                        if (PlayerData.canEquipPickaxe) {
                            ScriptData.TASK_LOAD_OUTS[4].setEquipmentItem(0, PlayerData.pickaxe, 1, 1, 1);
                            ScriptData.TASK_LOAD_OUTS[4].setInventoryItem(0, 0, 0, 0);
                        }
                        else {
                            ScriptData.TASK_LOAD_OUTS[4].setInventoryItem(0, PlayerData.pickaxe, 1, 1, 1);
                            ScriptData.TASK_LOAD_OUTS[4].setEquipmentItem(0, 0, 0, 0, 0);
                        }
                        break;
                    }
                }
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (ScriptData.currentPipelineI == 4 && i == 15) { // Switch to Iron ore
                        ScriptData.currentPipelineI = 33; // Determine Task
                        ScriptData.taskType = 3;
                        break;
                    }
                }
                break;
            case FISHING:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (ScriptData.currentPipelineI == 2 && i == 20) { // Switch to Trout/Salmon
                        ScriptData.currentPipelineI = 33; // Determine Task
                        ScriptData.taskType = 3;
                        break;
                    }
                }
                break;
            case ATTACK:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 40 || i == 30 || i == 20 || i == 10) {
                        PlayerData.initializeMeleeWeapon(i);
                        PlayerData.initializeAxe(Skills.getRealLevel(Skill.WOODCUTTING), i);
                        PlayerData.initializePickaxe(Skills.getRealLevel(Skill.MINING), i);
                        ScriptData.TASK_LOAD_OUTS[3].setEquipmentItem(4, PlayerData.meleeWeapon, 1, 1, 1);
                        if (ScriptData.currentPipelineI == 3) {
                            ScriptData.currentPipelineI = 33; // Determine Task
                            ScriptData.taskType = 3;
                        }
                        break;
                    }
                }
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == PlayerData.switchMeleeCombatStyleLevel && PlayerData.meleeCombatStyle == CombatStyle.ATTACK) {
                        PlayerData.determineMeleeCombatStyle(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                        PlayerData.determineSwitchMeleeCombatStyleLevel(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                        break;
                    }
                }
                break;
            case STRENGTH:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == PlayerData.switchMeleeCombatStyleLevel && PlayerData.meleeCombatStyle == CombatStyle.STRENGTH) {
                        PlayerData.determineMeleeCombatStyle(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                        PlayerData.determineSwitchMeleeCombatStyleLevel(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                        break;
                    }
                }
                break;
            case DEFENCE:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 40 || i == 30 || i == 20 || i == 10) {
                        PlayerData.initializeMeleeArmour(Skills.getRealLevel(Skill.DEFENCE));
                        ScriptData.TASK_LOAD_OUTS[3].setEquipmentItem(0, PlayerData.meleeHat, 1, 1, 1);
                        ScriptData.TASK_LOAD_OUTS[3].setEquipmentItem(1, PlayerData.meleeChest, 1, 1, 1);
                        ScriptData.TASK_LOAD_OUTS[3].setEquipmentItem(2, PlayerData.meleeLegs, 1, 1, 1);
                        ScriptData.TASK_LOAD_OUTS[3].setEquipmentItem(3, PlayerData.meleeShield, 1, 1, 1);
                        if (ScriptData.currentPipelineI == 3) {
                            ScriptData.currentPipelineI = 33; // Determine Task
                            ScriptData.taskType = 3;
                        }
                        break;
                    }
                }
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == PlayerData.switchMeleeCombatStyleLevel && PlayerData.meleeCombatStyle == CombatStyle.DEFENCE) {
                        PlayerData.determineMeleeCombatStyle(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                        PlayerData.determineSwitchMeleeCombatStyleLevel(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                        break;
                    }
                }
                break;
            case RANGED:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 40 || i == 30 || i == 20 || i == 10) {
                        PlayerData.initializeRangedHat(i);
                        PlayerData.initializeRangedChest(i, Skills.getRealLevel(Skill.DEFENCE));
                        PlayerData.initializeRangedLegs(i);
                        PlayerData.initializeRangedHands(i);
                        PlayerData.initializeRangedWeaponArrows(i);
                        if (ScriptData.currentPipelineI == 5) {
                            ScriptData.currentPipelineI = 33; // Determine Task
                            ScriptData.taskType = 3;
                        }
                        break;
                    }
                }
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == PlayerData.switchRangedCombatStyleLevel) {
                        PlayerData.determineRangedCombatStyle();
                        PlayerData.determineSwitchRangedCombatStyleLevel(Skills.getRealLevel(Skill.RANGED));
                        break;
                    }
                }
                break;
            case HITPOINTS:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 25) {
                        PlayerData.determineFood(i);
                        break;
                    }
                }
                PlayerData.determineEatFoodHPTrs();
                break;
            case RUNECRAFTING:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (ScriptData.currentPipelineI == 6 && (i == 20 || i == 14 || i == 9)) {
                        ScriptData.currentPipelineI = 33; // Determine Task
                        ScriptData.taskType = 3;
                    }
                }
                break;
            case FIREMAKING:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (ScriptData.currentPipelineI == 1 && (i == 30 || i == 15)) {
                        ScriptData.currentPipelineI = 33; // Determine Task
                        ScriptData.taskType = 3;
                    }
                }
                break;
            case COOKING:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (ScriptData.currentPipelineI == 1 && (i == 25 || i == 15)) {
                        ScriptData.currentPipelineI = 33; // Determine Task
                        ScriptData.taskType = 3;
                    }
                }
                break;
            case SMITHING:
                break;
        }
    }

    @Override
    public void onStart() {
        Mouse.setMouseAlgorithm(new SmartMouseMultiDir());
        Randoms.setSeed(AccountManager.getAccountUsername() + AccountManager.getAccountBankPin() + AccountManager.getAccountTOTPKey());
        initializeTaskWeights();
        initializePipelines();
        initializeTargetLevels();
        initializeTierFood();
        initializeCape();
        initializeRuneCraftingMediums();
        initializeChangePlayerSettings();
        PlayerData.determineFood(Skills.getRealLevel(Skill.HITPOINTS));
        PlayerData.determineEatFoodHPTrs();
        PlayerData.initializeAxe(Skills.getRealLevel(Skill.WOODCUTTING), Skills.getRealLevel(Skill.ATTACK));
        PlayerData.initializePickaxe(Skills.getRealLevel(Skill.MINING), Skills.getRealLevel(Skill.ATTACK));
        PlayerData.initializeMeleeWeapon(Skills.getRealLevel(Skill.ATTACK));
        PlayerData.initializeMeleeArmour(Skills.getRealLevel(Skill.DEFENCE));
        PlayerData.initializeRangedHat(Skills.getRealLevel(Skill.RANGED));
        PlayerData.initializeRangedChest(Skills.getRealLevel(Skill.RANGED), Skills.getRealLevel(Skill.DEFENCE));
        PlayerData.initializeRangedLegs(Skills.getRealLevel(Skill.RANGED));
        PlayerData.initializeRangedHands(Skills.getRealLevel(Skill.RANGED));
        PlayerData.initializeRangedWeaponArrows(Skills.getRealLevel(Skill.RANGED));
        PlayerData.initializeCurrentRunecraftMedium(Skills.getRealLevel(Skill.RUNECRAFTING));
        PlayerData.initializeBestPickaxeAvail(Skills.getRealLevel(Skill.MINING), Skills.getRealLevel(Skill.ATTACK));
        PlayerData.initializeBestAxeAvail(Skills.getRealLevel(Skill.WOODCUTTING), Skills.getRealLevel(Skill.ATTACK));
        initializeLoadOut();
        deSerializeScriptState();
        Logger.log("Finished onStart, currentEntityName: " + ScriptData.currentEntityName);
    }

    @Override
    public void onExit() {
        serializeScriptState();
    }

    @Override
    public void onPause() {
        if (ScriptData.taskType == 0 && ScriptData.progressionTaskTimer != null && !ScriptData.progressionTaskTimer.isPaused()) {
            ScriptData.progressionTaskTimer.pause();
            ScriptData.unPauseTimer = 1;
        }
        else if (ScriptData.taskType != 0 && ScriptData.secondaryTaskTimer != null && !ScriptData.secondaryTaskTimer.isPaused()) {
            ScriptData.secondaryTaskTimer.pause();
            ScriptData.unPauseTimer = 2;
        }
        if (ScriptData.changePlayerSetUpTimer != null && !ScriptData.changePlayerSetUpTimer.isPaused()) {
            ScriptData.changePlayerSetUpTimer.pause();
            ScriptData.unPauseSetUpClientTimer = 1;
        }
        Logger.log("Script paused");
    }

    @Override
    public void onResume() {
        if (ScriptData.unPauseTimer == 1) {
            ScriptData.progressionTaskTimer.resume();
        }
        else if (ScriptData.unPauseTimer == 2) {
            ScriptData.secondaryTaskTimer.resume();
        }
        if (ScriptData.unPauseSetUpClientTimer == 1) {
            ScriptData.changePlayerSetUpTimer.resume();
            ScriptData.unPauseSetUpClientTimer = 0;
        }
        ScriptData.unPauseTimer = 0;
        Logger.log("Script resumed");
    }

    @Override
    public int onLoop() {
        return ScriptData.PIPELINES[ScriptData.currentPipelineI].run();
    }

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
    }

    private void initializeCape() {
        PlayerData.cape = 4315 + Randoms.random(50) * 2; // IDs: 4315-4413, step 2
    }

    private void initializeRuneCraftingMediums() {
        for (byte i = 0; i < 4; i++) {
            int roll = ScriptData.SECURE_RANDOM.nextInt(2);
            switch (i) {
                case 0: // Air
                    if (roll == 0) {
                        PlayerData.runecraftingMediums[i] = 1438; // Air talisman
                    }
                    else {
                        PlayerData.runecraftingMediums[i] = 5527; // Air tiara
                    }
                    break;
                case 1: // Earth
                    if (roll == 0) {
                        PlayerData.runecraftingMediums[i] = 1440;
                    }
                    else {
                        PlayerData.runecraftingMediums[i] = 5535;
                    }
                    break;
                case 2: // Fire
                    if (roll == 0) {
                        PlayerData.runecraftingMediums[i] = 1442;
                    }
                    else {
                        PlayerData.runecraftingMediums[i] = 5537;
                    }
                    break;
                case 3: // Body
                    if (roll == 0) {
                        PlayerData.runecraftingMediums[i] = 1446;
                    }
                    else {
                        PlayerData.runecraftingMediums[i] = 5533;
                    }
                    break;
            }
        }
    }

    private void initializeTargetLevels() {
        PlayerData.targetAttackLevel = ScriptData.SECURE_RANDOM.nextInt(50 - 30 + 1) + 30;
        PlayerData.targetStrengthLevel = ScriptData.SECURE_RANDOM.nextInt(50 - 30 + 1) + 30;
        PlayerData.targetDefenceLevel = ScriptData.SECURE_RANDOM.nextInt(50 - 30 + 1) + 30;
        PlayerData.targetRangedLevel = ScriptData.SECURE_RANDOM.nextInt(50 - 30 + 1) + 30;
        PlayerData.targetWoodcuttingLevel = ScriptData.SECURE_RANDOM.nextInt(50 - 30 + 1) + 30;
        PlayerData.targetMiningLevel = ScriptData.SECURE_RANDOM.nextInt(50 - 30 + 1) + 30;
        PlayerData.targetFishingLevel = ScriptData.SECURE_RANDOM.nextInt(50 - 30 + 1) + 30;
        PlayerData.targetRunecraftingLevel = ScriptData.SECURE_RANDOM.nextInt(50 - 30 + 1) + 30;
        PlayerData.targetFiremakingLevel = ScriptData.SECURE_RANDOM.nextInt(50 - 30 + 1) + 30;
        PlayerData.targetSmithingLevel = ScriptData.SECURE_RANDOM.nextInt(50 - 30 + 1) + 30;
        PlayerData.targetCookingLevel = ScriptData.SECURE_RANDOM.nextInt(50 - 30 + 1) + 30;
    }

    private void initializeChangePlayerSettings() {
        ScriptData.playerSetUpOpts = new byte[3];
        ScriptData.playerSetUpValues = new byte[3];
        for (byte i = 0; i < 3; i++) {
            ScriptData.playerSetUpOpts[i] = i;
            if (ScriptData.rollChance(50)) {
                ScriptData.playerSetUpValues[i] = 1;
            }
            else {
                ScriptData.playerSetUpValues[i] = 0;
            }
        }
        ScriptData.changePlayerSetUpTimer = new Timer(ScriptData.SECURE_RANDOM.nextInt(10800000 - 300000 + 1) + 300000); // 30m-3h
    }

    private void initializeLoadOut() {
        ScriptData.TASK_LOAD_OUTS[8] = new LoadOut(1, 1, ScriptData.INVENTORY_FULL); // Woodcutting
        ScriptData.TASK_LOAD_OUTS[8].addInventoryItem(0, 0, 0, 0);
        ScriptData.TASK_LOAD_OUTS[8].addEquipmentItem(0, 0, 0, 0);

        ScriptData.TASK_LOAD_OUTS[4] = new LoadOut(1, 1, ScriptData.INVENTORY_FULL); // Mining
        ScriptData.TASK_LOAD_OUTS[4].addInventoryItem(0, 0, 0, 0);
        ScriptData.TASK_LOAD_OUTS[4].addEquipmentItem(0, 0, 0, 0);

        ScriptData.TASK_LOAD_OUTS[2] = new LoadOut(2, 0, ScriptData.INVENTORY_FULL); // Fishing
        ScriptData.TASK_LOAD_OUTS[2].addInventoryItem(0, 0, 0, 0);
        ScriptData.TASK_LOAD_OUTS[2].addInventoryItem(0, 0, 0, 0);

        ScriptData.TASK_LOAD_OUTS[3] = new LoadOut(2, 7, // Melee
            () -> Inventory.count(PlayerData.food) < ScriptData.TASK_LOAD_OUTS[3].getInvItemQtyMin(1) // 1 == food index
                || (Inventory.isFull() && (ScriptData.TASK_LOAD_OUTS[3].getInvItemID(0) == 0
                    || !Inventory.contains(PlayerData.food)
                    || Skills.getBoostedLevel(Skill.HITPOINTS) + PlayerData.foodHP > Skills.getRealLevel(Skill.HITPOINTS)))
        );
        ScriptData.TASK_LOAD_OUTS[3].addEquipmentItem(0, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[3].addEquipmentItem(0, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[3].addEquipmentItem(0, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[3].addEquipmentItem(0, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[3].addEquipmentItem(0, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[3].addEquipmentItem(PlayerData.AMULET, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[3].addEquipmentItem(PlayerData.cape, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[3].addInventoryItem(983, 0, 0, 0); // Increase to 1 when rolling Hill Giants in Edgeville Dungeon
        ScriptData.TASK_LOAD_OUTS[3].addInventoryItem(PlayerData.food, 0, 0, 0); // Change max/init values every task roll

        ScriptData.TASK_LOAD_OUTS[5] = new LoadOut(2, 8, // Ranged
            () -> Inventory.count(PlayerData.food) < ScriptData.TASK_LOAD_OUTS[5].getInvItemQtyMin(1)
                || Equipment.count(PlayerData.rangedArrows) < ScriptData.TASK_LOAD_OUTS[5].getEqpItemQtyMin(4)
        );
        ScriptData.TASK_LOAD_OUTS[5].addEquipmentItem(PlayerData.rangedHat, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[5].addEquipmentItem(PlayerData.rangedChest, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[5].addEquipmentItem(PlayerData.rangedLegs, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[5].addEquipmentItem(PlayerData.rangedHands, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[5].addEquipmentItem(PlayerData.rangedWeapon, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[5].addEquipmentItem(PlayerData.rangedArrows, 0, 0, 0); // Determine min/max/init once per task
        ScriptData.TASK_LOAD_OUTS[5].addEquipmentItem(PlayerData.AMULET, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[5].addEquipmentItem(PlayerData.cape, 1, 1, 1);
        ScriptData.TASK_LOAD_OUTS[5].addInventoryItem(983, 0, 0, 0); // Increase to 1 when rolling Hill Giants in Edgeville Dungeon
        ScriptData.TASK_LOAD_OUTS[5].addInventoryItem(PlayerData.food, 0, 0, 0); // Change max/init values every task roll

        ScriptData.TASK_LOAD_OUTS[6] = new LoadOut(2, 1, () -> !Inventory.contains(7936)); // Runecrafting
        ScriptData.TASK_LOAD_OUTS[6].addEquipmentItem(0, 0, 0, 0);
        ScriptData.TASK_LOAD_OUTS[6].addInventoryItem(7936, 1, 28, 0); // Pure essence
        ScriptData.TASK_LOAD_OUTS[6].addInventoryItem(0, 0, 0, 0);

        ScriptData.TASK_LOAD_OUTS[1] = new LoadOut(2, 0, () -> !Inventory.contains(ScriptData.TASK_LOAD_OUTS[1].getInvItemID(1))); // Firemaking
        ScriptData.TASK_LOAD_OUTS[1].addInventoryItem(590, 1, 1, 1); // Tinderbox
        ScriptData.TASK_LOAD_OUTS[1].addInventoryItem(0, 0, 0, 0);


        ScriptData.TASK_LOAD_OUTS[7] = new LoadOut(2, 0, () -> !Inventory.contains(ScriptData.TASK_LOAD_OUTS[7].getInvItemID(1))); // Smithing
        ScriptData.TASK_LOAD_OUTS[7].addInventoryItem(2347, 1, 1, 1); // Hammer
        ScriptData.TASK_LOAD_OUTS[7].addInventoryItem(0, 0, 0, 0); // Bar

        ScriptData.TASK_LOAD_OUTS[0] = new LoadOut(2, 0, () -> !Inventory.contains(ScriptData.TASK_LOAD_OUTS[0].getInvItemID(0))); // Cooking
        ScriptData.TASK_LOAD_OUTS[0].addInventoryItem(0, 0, 0, 0); // Raw x
        ScriptData.TASK_LOAD_OUTS[0].addInventoryItem(0, 0, 0, 0); // Cooked xe

        ScriptData.TASK_LOAD_OUTS[32] = new LoadOut(2, 0, () -> Inventory.count(1759) == 27); // Spinning Balls of Wool
        ScriptData.TASK_LOAD_OUTS[32].addInventoryItem(1735, 0, 1, 0); // Shears
        ScriptData.TASK_LOAD_OUTS[32].addInventoryItem(1737, 0, 27, 0); // Wool

        ScriptData.TASK_LOAD_OUTS[31] = new LoadOut(2, 0, () -> !Inventory.containsAll(ScriptData.TASK_LOAD_OUTS[31].getInvItemID(0), ScriptData.TASK_LOAD_OUTS[1].getInvItemID(1))); // Smelting Bars
        ScriptData.TASK_LOAD_OUTS[31].addInventoryItem(0, 0, 0, 0);
        ScriptData.TASK_LOAD_OUTS[31].addInventoryItem(0, 0, 0, 0);

        ScriptData.TASK_LOAD_OUTS[29] = new LoadOut(1, 1, ScriptData.INVENTORY_FULL); // Chopping Logs
        ScriptData.TASK_LOAD_OUTS[29].addInventoryItem(0, 0, 0, 0);
        ScriptData.TASK_LOAD_OUTS[29].addEquipmentItem(0, 0, 0, 0);

        ScriptData.TASK_LOAD_OUTS[30] = new LoadOut(1, 1, ScriptData.INVENTORY_FULL); // Mining Ore
        ScriptData.TASK_LOAD_OUTS[30].addInventoryItem(0, 0, 0, 0);
        ScriptData.TASK_LOAD_OUTS[30].addEquipmentItem(0, 0, 0, 0);
    }

    private void initializePipelines() { // Don't initialize quests
        ScriptData.PIPELINES[0] = new OneAreaPipeline(ScriptData.progressionTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.walkToCurrentAreaLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, new CookingLI()); // Cooking
        ScriptData.PIPELINES[1] = new OneAreaPipeline(ScriptData.progressionTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.walkToCurrentAreaLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, new FiremakingLI()); // Firemaking
        ScriptData.PIPELINES[2] = new OneAreaPipeline(ScriptData.progressionTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.walkToCurrentAreaLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, new FishingLI()); // Fishing
        ScriptData.PIPELINES[3] = new OneAreaPipeline(ScriptData.progressionTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.walkToCurrentAreaLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, ScriptData.attackTargetNPCLI, ScriptData.checkMeleeCombatStyleLI, ScriptData.cantReachCurrentNPCLI, ScriptData.currentTileToTargetNPCTileLI, ScriptData.eatChosenFoodLI, ScriptData.findValidNPCTargetLI, ScriptData.lootNPCDropsLI); // Melee
        ScriptData.PIPELINES[4] = new OneAreaPipeline(ScriptData.progressionTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.walkToCurrentAreaLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, new MiningLI()); // Mining
        ScriptData.PIPELINES[5] = new OneAreaPipeline(ScriptData.progressionTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.walkToCurrentAreaLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, ScriptData.walkToCurrentAreaLI, ScriptData.attackTargetNPCLI, ScriptData.checkRangedCombatStyleLI, ScriptData.cantReachCurrentNPCLI, ScriptData.currentTileToTargetNPCTileLI, ScriptData.eatChosenFoodLI, ScriptData.findValidNPCTargetLI, ScriptData.lootNPCDropsLI); // Ranged
        ScriptData.PIPELINES[6] = new BasicTaskPipeline(ScriptData.progressionTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, new RunecraftLI(), new EnterRiftLI()); // Runecrafting
        ScriptData.PIPELINES[7] = new OneAreaPipeline(ScriptData.progressionTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.walkToCurrentAreaLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, new InteractWithAnvilLI(), new SmithingLI()); // Smithing
        ScriptData.PIPELINES[8] = new OneAreaPipeline(ScriptData.progressionTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.walkToCurrentAreaLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, ScriptData.woodcuttingLI); // Woodcutting

        ScriptData.PIPELINES[29] = new OneAreaPipeline(ScriptData.secondaryTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.walkToCurrentAreaLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, ScriptData.woodcuttingLI); // Chopping logs
        ScriptData.PIPELINES[30] = new OneAreaPipeline(ScriptData.secondaryTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.walkToCurrentAreaLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, ScriptData.miningLI); // Mining ore
        ScriptData.PIPELINES[31] = new OneAreaPipeline(ScriptData.secondaryTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.walkToCurrentAreaLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, new SmeltBarsLI()); // Smelting bars
        ScriptData.PIPELINES[32] = new BasicTaskPipeline(ScriptData.secondaryTaskTimerFinishedLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.openInventoryLI, ScriptData.changePlayerSetUpLI, new CollectShears(), new SpinBallsOfWoolLI(), new ShearSheepLI()); // Spinning Balls of Wool

        ScriptData.PIPELINES[33] = new DetermineTaskPipeline(new DetermineTaskLI());
        ScriptData.PIPELINES[34] = ScriptData.bankingPipeline;
        ScriptData.PIPELINES[35] = new GrandExchangePipeline(ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.buyItemsLI);
        ScriptData.PIPELINES[36] = new GrandExchangePipeline(ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.sellItemsLI);
    }

    private void initializeTaskWeights() {
        for (int i = 0; i < ScriptData.TASK_WEIGHTS.length; i++) {
            int weight = ScriptData.SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
            ScriptData.TASK_WEIGHTS[i] = weight;
            if (i < 9) {
                ScriptData.progressionTaskWeightTotal += weight;
            }
            else if ( i > 28) {
                ScriptData.secondaryTaskWeightTotal += weight;
            }
        }
    }

    private void serializeScriptState() {
        try {
            File dir = new File(System.getProperty("user.home")); // user.home == C:\Users\Someone\UserHomeCache\<username>
            if (!dir.exists()) {
                Logger.error("Directory doesn't exist, exiting");
                stop();
                return;
            }
            File file = new File(dir, AccountManager.getAccountUsername() + "-cache.json");

            PersistedScriptInfo p = new PersistedScriptInfo();
            p.bankCache = Bank.getBankHistoryCache();
            p.taskType = ScriptData.taskType;
            p.currentPipelineI = ScriptData.currentPipelineI;
            p.currentProgressionTaskI = ScriptData.currentProgressionTaskI;
            p.currentSecondaryTaskI = ScriptData.currentSecondaryTaskI;
            p.useOnGameMessageEvent = ScriptData.useOnGameMessageEvent;
            p.progressionTaskTimer = ScriptData.progressionTaskTimer;
            p.moneyMakingTaskTimer = ScriptData.secondaryTaskTimer;
            p.questOrderI = ScriptData.questOrderI;
            p.questOrder = ScriptData.questOrder;
            p.dialogueOpts = ScriptData.dialogueOpts;
            p.currentEntityName = ScriptData.currentEntityName;
            p.currentTile = ScriptData.currentTile;
            p.currentArea = ScriptData.currentArea;
            p.currentArea2 = ScriptData.currentArea2;
            p.currentArea3 = ScriptData.currentArea3;
            p.unPauseTimer = ScriptData.unPauseTimer;
            p.unPauseSetUpClientTimer = ScriptData.unPauseSetUpClientTimer;
            p.changePlayerSetUpTimer = ScriptData.changePlayerSetUpTimer;
            p.playerSetUpOpts = ScriptData.playerSetUpOpts;
            p.playerSetUpValues = ScriptData.playerSetUpValues;
            p.playerSetUpI = ScriptData.playerSetUpI;
            ScriptData.sellItemsLI.exportToPersistedScriptInfo(p);
            ScriptData.buyItemsLI.exportToPersistedScriptInfo(p);
            ScriptData.checkLoadOutLI.exportToPersistedScriptInfo(p);
            ScriptData.bankingPipeline.exportToPersistedScriptInfo(p);
            if (ScriptData.taskType == 1) {
                ScriptData.TASK_LOAD_OUTS[ScriptData.currentSecondaryTaskI].exportToPersistedScriptInfo(p);
            }
            else {
                ScriptData.TASK_LOAD_OUTS[ScriptData.currentProgressionTaskI].exportToPersistedScriptInfo(p);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter fileWriter = new FileWriter(file)) {
                gson.toJson(p, fileWriter);
                Logger.log("Serialized PersistedScriptInfo to file: " + file.getAbsolutePath());
            }
            catch (Exception e) {
                Logger.error("Failed to write to json: " + e.getMessage());
            }
        }
        catch (Exception e) {
            Logger.error("Failed to serialize PersistedScriptInfo: " + e.getMessage());
        }
    }

    private void deSerializeScriptState() { // Fresh accounts (post-tut island) SHOULD NOT have anything in their Banks
        File dir = new File(System.getProperty("user.home")); // user.home == C:\Users\Someone\UserHomeCache\<username>
        if (!dir.exists()) {
            Logger.error("Directory doesn't exist, exiting");
            stop();
            return;
        }
        File file = new File(dir, AccountManager.getAccountUsername() + "-cache.json");

        Gson gson = new Gson();
        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                PersistedScriptInfo p = gson.fromJson(reader, PersistedScriptInfo.class);
                ScriptData.taskType = p.taskType;
                ScriptData.currentPipelineI = p.currentPipelineI;
                ScriptData.currentProgressionTaskI = p.currentProgressionTaskI;
                ScriptData.currentSecondaryTaskI = p.currentSecondaryTaskI;
                ScriptData.useOnGameMessageEvent = p.useOnGameMessageEvent;
                ScriptData.progressionTaskTimer = p.progressionTaskTimer;
                ScriptData.secondaryTaskTimer = p.moneyMakingTaskTimer;
                ScriptData.questOrder = p.questOrder;
                ScriptData.questOrderI = p.questOrderI;
                ScriptData.dialogueOpts = p.dialogueOpts;
                ScriptData.currentEntityName = p.currentEntityName;
                ScriptData.currentTile = p.currentTile;
                ScriptData.currentArea = p.currentArea;
                ScriptData.currentArea2 = p.currentArea2;
                ScriptData.currentArea3 = p.currentArea3;
                ScriptData.unPauseTimer = p.unPauseTimer;
                ScriptData.unPauseSetUpClientTimer = p.unPauseSetUpClientTimer;
                ScriptData.changePlayerSetUpTimer = p.changePlayerSetUpTimer;
                ScriptData.playerSetUpOpts = p.playerSetUpOpts;
                ScriptData.playerSetUpValues = p.playerSetUpValues;
                ScriptData.playerSetUpI = p.playerSetUpI;
                ScriptData.sellItemsLI.importFromPersistedScriptInfo(p);
                ScriptData.buyItemsLI.importFromPersistedScriptInfo(p);
                ScriptData.checkLoadOutLI.importFromPersistedScriptInfo(p);
                ScriptData.bankingPipeline.importFromPersistedScriptInfo(p);
                if (ScriptData.taskType == 1) {
                    ScriptData.TASK_LOAD_OUTS[ScriptData.currentSecondaryTaskI].importFromPersistedScriptInfo(p);
                }
                else {
                    ScriptData.TASK_LOAD_OUTS[ScriptData.currentProgressionTaskI].importFromPersistedScriptInfo(p);
                }
                if (ScriptData.unPauseTimer == 1) {
                    ScriptData.progressionTaskTimer.resume();
                }
                else if (ScriptData.unPauseTimer == 2) {
                    ScriptData.secondaryTaskTimer.resume();
                }
                ScriptData.unPauseTimer = 0;
                if (ScriptData.currentProgressionTaskI > 8 && ScriptData.currentProgressionTaskI < 29 && ScriptData.taskType == 0) { // questing task, but re-instantiate pipeline + loadout
                    ScriptData.PIPELINES[ScriptData.currentPipelineI] = reInstantiateQuestPipeline();
                }
                Logger.log("deserialized PersistedScriptInfo from: " + file.getAbsolutePath());
                Logger.log("currentPipelineI: " + ScriptData.currentPipelineI);
            }
            catch (Exception e) {
                Logger.error("Failed to deSerialize PersistedScriptInfo: " + e.getMessage());
            }
        }
    }

    private Pipeline reInstantiateQuestPipeline() {
        switch (ScriptData.currentProgressionTaskI) {
            case 9:  // Below Ice Mountain
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new BelowIceMountainLI());
            case 10: // Black Knights' Fortress
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new BlackKnightsFortressLI());
            case 11: // Cook's Assistant
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new CooksAssistantLI());
            case 12: // Demon Slayer
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, new HandleDialogueDemonSlayerLI(), ScriptData.finishQuestLI, new PiratesTreasureLI());
            case 13: // Doric's Quest
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new DoricsQuestLI());
            case 14: // Ernest the Chicken
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new ErnestTheChickenLI());
            case 15: // Goblin Diplomacy
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new GoblinDiplomacyLI());
            case 16: // Imp Catcher
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new ImpCatcherLI());
            case 17: // Misthalin Mystery
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new MisthalinMysteryLI());
            case 18: // Pirate's Treasure
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, new HandleDialoguePiratesTreasureLI(), ScriptData.finishQuestLI, new PiratesTreasureLI());
            case 19: // Prince Ali Rescue
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new PrinceAliRescueLI());
            case 20: // Romeo and Juliet
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new RomeoAndJulietLI());
            case 21: // Rune Mysteries
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new RuneMysteriesLI());
            case 22: // Sheep Shearer
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new SheepShearerLI());
            case 23: // The Corsair Curse
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new TheCorsairCurseLI());
            case 24: // The Knight's Sword
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new TheKnightsSwordLI());
            case 25: // The Restless Ghost
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new TheRestlessGhostLI());
            case 26: // Vampyre Slayer
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new VampyreSlayerLI());
            case 27: // Witch's Potion
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new WitchsPotionLI());
            case 28: // X Marks the Spot
                return new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, new FinishQuestXMarksTheSpotLI(), new XMarksTheSpotLI());
        }
        return null;
    }

}
