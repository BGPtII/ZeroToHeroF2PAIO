package loopinterceptors;

import data.LoadOut;
import data.global.PlayerData;
import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.utilities.Logger;
import pipelines.QuestPipeline;

public class DetermineTaskLI extends LoopInterceptor {

    public DetermineTaskLI() {
        super(() -> true);
    }

    @Override
    public int handle() {
        Logger.log("Starting DetermineTaskLI, taskType: " + ScriptData.taskType);
        ScriptData.currentTile = null;
        if (ScriptData.taskType == 3) { // Re-initialize progressionTask
            Logger.log("Re-initialize progressionTask");
            ScriptData.currentPipelineI = ScriptData.currentProgressionTaskI;
        }
        else if (ScriptData.taskType == 2) { // return to progression from secondary
            Logger.log("Returning to progression from secondary");
            ScriptData.currentPipelineI = ScriptData.currentProgressionTaskI;
        }
        else if (ScriptData.taskType == 1) { // roll secondary
            Logger.log("Needs to roll secondary task");
            if (ScriptData.currentProgressionTaskI == 1) { // fireMaking == roll a money making or log acquisition task
                if (Skills.getRealLevel(Skill.FIREMAKING) >= 30 && Skills.getRealLevel(Skill.WOODCUTTING) >= 30) {
                    ScriptData.currentEntityName = "Willow tree";
                    ScriptData.currentSecondaryTaskI = 29;
                }
                else if (Skills.getRealLevel(Skill.FIREMAKING) >= 15 && Skills.getRealLevel(Skill.WOODCUTTING) >= 15) {
                    ScriptData.currentEntityName = "Oak tree";
                    ScriptData.currentSecondaryTaskI = 29;
                }
                else if (Skills.getRealLevel(Skill.FIREMAKING) < 15) {
                    ScriptData.currentEntityName = "Tree";
                    ScriptData.currentSecondaryTaskI = 29;
                }
                else { // roll money making task
                    ScriptData.currentSecondaryTaskI = 32;
                }
            }
            else if (ScriptData.currentProgressionTaskI == 7) { // smithing == roll either money making, smelting bars, or mining ore
                int barsReq = ScriptData.TASK_LOAD_OUTS[7].getInvItemQtyInit((byte) 1);
                if (Skills.getRealLevel(Skill.SMITHING) >= 30) {
                    int coalQty = Bank.count(453) + Inventory.count(453);
                    int ironOreQty = Bank.count(440) + Inventory.count(440);
                    if (Bank.count(2353) + Inventory.count(2353) >= barsReq) { // Steel bar
                        ScriptData.TASK_LOAD_OUTS[7].setInventoryItem(0, 2353, 1, 27, barsReq);
                    }
                    else {
                        if (coalQty >= barsReq * 2 && ironOreQty >= barsReq) { // Must smelt bars
                            ScriptData.TASK_LOAD_OUTS[31].setInventoryItem(0, 0, 19, barsReq * 2);
                            ScriptData.TASK_LOAD_OUTS[31].setInventoryItem(1, 0, 9, barsReq);
                            ScriptData.currentSecondaryTaskI = 31;
                        }
                        else { // Needs the ore
                            String[] oreOpts = new String[2];
                            byte oreOptsSize = 0;
                            if (coalQty < barsReq * 2 && Skills.getRealLevel(Skill.MINING) >= 30) {
                                oreOpts[oreOptsSize++] = "Coal";
                            }
                            if (ironOreQty < barsReq && Skills.getRealLevel(Skill.MINING) >= 15) {
                                oreOpts[oreOptsSize++] = "Iron rocks";
                            }
                            if (oreOptsSize == 0) { // roll moneyMaking task
                                Logger.log("Can't mine either Coal or Iron, just roll a moneyMaking task");
                                ScriptData.currentSecondaryTaskI = 32;
                            }
                            else { // Secondary task becomes a resource gathering task (mining ore)
                                ScriptData.currentSecondaryTaskI = 30;
                                ScriptData.currentEntityName = oreOpts[ScriptData.SECURE_RANDOM.nextInt(oreOptsSize)];
                            }
                        }
                    }
                }
                else if (Bank.count(2349) + Inventory.count(2349) >= barsReq) { // Smith
                    ScriptData.TASK_LOAD_OUTS[7].setInventoryItem(0, 2349, 1, 27, barsReq);
                    Logger.log("Has the Bronze bars to smith");
                }
                else { // Bronze bar
                    int tinQty = Bank.count(438) + Inventory.count(438);
                    int copperQty = Bank.count(436) + Inventory.count(436);
                    if (tinQty >= barsReq * 2 && copperQty >= barsReq * 2) { // Smelt the bar
                        Logger.log("Has the tin and copper to smelt bars");
                        ScriptData.TASK_LOAD_OUTS[31].setInventoryItem(0, 0, 14, barsReq); // Copper ore
                        ScriptData.TASK_LOAD_OUTS[31].setInventoryItem(1, 0, 14, barsReq); // Tin ore
                        ScriptData.currentSecondaryTaskI = 31;
                        Logger.log("Needs to mine tin or copper");
                    }
                    else { // Mine the ore
                        String[] oreOpts = new String[2];
                        byte oreOptsSize = 0;
                        if (tinQty < barsReq) {
                            oreOpts[oreOptsSize++] = "Tin rocks";
                        }
                        if (copperQty < barsReq) {
                            oreOpts[oreOptsSize++] = "Copper rocks";
                        }
                        PlayerData.initializeBestPickaxeAvail(Skills.getRealLevel(Skill.MINING), Skills.getRealLevel(Skill.ATTACK));
                        if (PlayerData.canEquipBestPickaxeAvail) {
                            ScriptData.TASK_LOAD_OUTS[30].setInventoryItem(0, 0, 0, 0);
                            ScriptData.TASK_LOAD_OUTS[30].setEquipmentItem(0, PlayerData.bestPickaxeAvail, 1, 1, 1);
                        }
                        else {
                            ScriptData.TASK_LOAD_OUTS[30].setInventoryItem(0, PlayerData.bestPickaxeAvail, 0, 0, 0);
                            ScriptData.TASK_LOAD_OUTS[30].setEquipmentItem(0, 0, 0, 0, 0);
                        }
                        ScriptData.currentSecondaryTaskI = 30; // Secondary task becomes a resource gathering task (mining ore)
                        ScriptData.currentEntityName = oreOpts[ScriptData.SECURE_RANDOM.nextInt(oreOptsSize)];
                    }
                }
            }
            else { // roll money making task
                ScriptData.currentSecondaryTaskI = 32;
            }
            Logger.log("Rolled a secondaryTask: " + ScriptData.currentSecondaryTaskI);
            ScriptData.currentPipelineI = ScriptData.currentSecondaryTaskI;
        }
        else if (ScriptData.taskType == 0) {
            Logger.log("Rolling primary task");
            int taskRoll;
            if (ScriptData.currentProgressionTaskI != -1) {
                taskRoll = ScriptData.SECURE_RANDOM.nextInt(ScriptData.progressionTaskWeightTotal - ScriptData.TASK_WEIGHTS[ScriptData.currentProgressionTaskI]);
            }
            else {
                taskRoll = ScriptData.SECURE_RANDOM.nextInt(ScriptData.progressionTaskWeightTotal);
            }
            byte startI; // inclusive
            byte endI; // exclusive
            if (ScriptData.taskType == 0) {
                startI = 0;
                endI = 29;
            }
            else {
                startI = 29;
                endI = 32;
            }
            int incrementalTotalWeight = 0;
            for (byte i = startI; i < endI; i++) {
                if (isValidTask(i)) {
                    incrementalTotalWeight += ScriptData.TASK_WEIGHTS[i];
                    if (taskRoll < incrementalTotalWeight) {
                        ScriptData.currentProgressionTaskI = i;
                        Logger.log("Selected currentProgressionTaskI: " + ScriptData.currentProgressionTaskI);
                        ScriptData.currentPipelineI = ScriptData.currentProgressionTaskI;
                        break;
                    }
                }
            }
        }
        setUpTask();
        ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].setSetUp(true);
        ScriptData.PIPELINES[ScriptData.currentPipelineI].shuffleLoopInterceptors();
        ScriptData.resetEntities();
        ScriptData.checkLoadOutLI.reset();
        Logger.log("Finished determineTaskLI, taskType: " + ScriptData.taskType + ", progressionTaskI: " + ScriptData.currentPipelineI);
        return ScriptData.returnMSFast();
    }

    private boolean isValidTask(byte taskId) {
        switch (taskId) {
            case 25:
                return !FreeQuest.THE_RESTLESS_GHOST.isFinished();
            case 11:
                return !FreeQuest.COOKS_ASSISTANT.isFinished();
            case 21:
                return !FreeQuest.RUNE_MYSTERIES.isFinished() && Combat.getCombatLevel() >= 13; // For Mugger by Aubury
            case 28:
                return !FreeQuest.X_MARKS_THE_SPOT.isFinished();
            case 18:
                return !FreeQuest.PIRATES_TREASURE.isFinished() && Combat.getCombatLevel() >= 10;
            case 27:
                return !FreeQuest.WITCHS_POTION.isFinished() && Skills.getTotalLevel() >= 75 && Combat.getCombatLevel() >= 13;
            case 14:
                return !FreeQuest.ERNEST_THE_CHICKEN.isFinished() && Skills.getRealLevel(Skill.HITPOINTS) >= 20;
            case 24:
                return !FreeQuest.THE_KNIGHTS_SWORD.isFinished() && Skills.getRealLevel(Skill.MINING) >= 10;
            case 17:
                return !FreeQuest.MISTHALIN_MYSTERY.isFinished();
            case 19:
                return !FreeQuest.PRINCE_ALI_RESCUE.isFinished() && Skills.getRealLevel(Skill.DEFENCE) >= 15;
            case 9:
                return !FreeQuest.BELOW_ICE_MOUNTAIN.isFinished()
                    && Skills.getRealLevel(Skill.MINING) >= 10
                    && Skills.getRealLevel(Skill.DEFENCE) >= 20
                    && Quests.getQuestPoints() >= 16;
            case 22:
                return !FreeQuest.SHEEP_SHEARER.isFinished();
            case 20:
                return !FreeQuest.ROMEO_AND_JULIET.isFinished() && Skills.getTotalLevel() >= 150;
            case 16:
                return !FreeQuest.IMP_CATCHER.isFinished() && Skills.getTotalLevel() >= 150;
            case 26:
                return !FreeQuest.VAMPYRE_SLAYER.isFinished()
                    && Skills.getRealLevel(Skill.ATTACK) >= 15
                    && Skills.getRealLevel(Skill.STRENGTH) >= 15
                    && Skills.getRealLevel(Skill.DEFENCE) >= 15;
            case 10:
                return !FreeQuest.BLACK_KNIGHTS_FORTRESS.isFinished()
                    && Skills.getRealLevel(Skill.ATTACK) >= 20
                    && Skills.getRealLevel(Skill.STRENGTH) >= 20
                    && Skills.getRealLevel(Skill.DEFENCE) >= 20;
            case 23:
                return !FreeQuest.THE_CORSAIR_CURSE.isFinished()
                    && Skills.getRealLevel(Skill.ATTACK) >= 20
                    && Skills.getRealLevel(Skill.STRENGTH) >= 20
                    && Skills.getRealLevel(Skill.DEFENCE) >= 20
                    && Skills.getRealLevel(Skill.RANGED) >= 20;
            case 12:
                return !FreeQuest.DEMON_SLAYER.isFinished()
                    && Skills.getRealLevel(Skill.ATTACK) >= 20
                    && Skills.getRealLevel(Skill.STRENGTH) >= 20
                    && Skills.getRealLevel(Skill.DEFENCE) >= 20
                    && Skills.getRealLevel(Skill.RANGED) >= 20;
            case 13:
                return !FreeQuest.DORICS_QUEST.isFinished()
                    && Skills.getTotalLevel() >= 75
                    && Skills.getRealLevel(Skill.MINING) >= 10;
            case 15:
                return !FreeQuest.GOBLIN_DIPLOMACY.isFinished() && Skills.getTotalLevel() >= 150;
            case 8:
                return Skills.getRealLevel(Skill.WOODCUTTING) < PlayerData.targetWoodcuttingLevel
                    && ScriptData.currentProgressionTaskI != taskId;
            case 4:
                return Skills.getRealLevel(Skill.MINING) < PlayerData.targetMiningLevel
                    && ScriptData.currentProgressionTaskI != taskId;
            case 2:
                return Skills.getRealLevel(Skill.FISHING) < PlayerData.targetFishingLevel
                    && ScriptData.currentProgressionTaskI != taskId;
            case 6:
                return Skills.getRealLevel(Skill.RUNECRAFTING) < PlayerData.targetRunecraftingLevel
                    && ScriptData.currentProgressionTaskI != taskId;
            case 1:
                return Skills.getRealLevel(Skill.FIREMAKING) < PlayerData.targetFiremakingLevel
                    && ScriptData.currentProgressionTaskI != taskId;
            case 7:
                return Skills.getRealLevel(Skill.SMITHING) < PlayerData.targetSmithingLevel
                    && ScriptData.currentProgressionTaskI != taskId;
            case 0:
                return FreeQuest.COOKS_ASSISTANT.isFinished()
                    && Skills.getRealLevel(Skill.COOKING) < PlayerData.targetCookingLevel
                    && ScriptData.currentProgressionTaskI != taskId;
            case 3:
                return Skills.getRealLevel(Skill.ATTACK) < PlayerData.targetAttackLevel
                    && Skills.getRealLevel(Skill.STRENGTH) < PlayerData.targetStrengthLevel
                    && Skills.getRealLevel(Skill.DEFENCE) < PlayerData.targetDefenceLevel
                    && ScriptData.currentProgressionTaskI != taskId;
            case 5:
                return Skills.getRealLevel(Skill.RANGED) < PlayerData.targetRangedLevel
                    && ScriptData.currentProgressionTaskI != taskId;
            default:
                return ScriptData.currentSecondaryTaskI != taskId;
        }
    }

    private void setUpTask() { // loadOut setUp, determineTaskVariables
        switch (ScriptData.currentPipelineI) {
            case 8: // Woodcutting
                if (PlayerData.canEquipAxe) {
                    Logger.log("Can equip axe: " + PlayerData.axe);
                    ScriptData.TASK_LOAD_OUTS[8].setEquipmentItem(0, PlayerData.axe, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[8].setInventoryItem(0, PlayerData.axe, 0, 0, 0);
                }
                else {
                    Logger.log("Can't equip axe: " + PlayerData.axe);
                    ScriptData.TASK_LOAD_OUTS[8].setEquipmentItem(0, PlayerData.axe, 0, 0, 0);
                    ScriptData.TASK_LOAD_OUTS[8].setInventoryItem(0, PlayerData.axe, 1, 1, 1);
                }

                int woodcuttingLevel = Skills.getRealLevel(Skill.WOODCUTTING);
                if (woodcuttingLevel >= 30) {
                    ScriptData.currentEntityName = "Willow tree";
                }
                else if (woodcuttingLevel >= 15) {
                    ScriptData.currentEntityName = "Oak tree";
                }
                else {
                    ScriptData.currentEntityName = "Tree";
                }
                ScriptData.currentArea = currentEntityNameCurrentAreaFactory();
                break;
            case 4: // Mining
                if (PlayerData.canEquipPickaxe) {
                    ScriptData.TASK_LOAD_OUTS[4].setEquipmentItem(0, PlayerData.pickaxe, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[4].setInventoryItem(0, PlayerData.pickaxe, 0, 0, 0);
                }
                else {
                    ScriptData.TASK_LOAD_OUTS[4].setEquipmentItem(0, PlayerData.pickaxe, 0, 0, 0);
                    ScriptData.TASK_LOAD_OUTS[4].setInventoryItem(0, PlayerData.pickaxe, 1, 1, 1);
                }
                if (Skills.getRealLevel(Skill.MINING) >= 15) {
                    ScriptData.currentEntityName = "Iron rocks";
                }
                else {
                    ScriptData.currentEntityName = "Tin rocks";
                }
                ScriptData.currentArea = currentEntityNameCurrentAreaFactory();
                ScriptData.currentTile = null;
                break;
            case 2: // Fishing
                if (Skills.getRealLevel(Skill.FISHING) >= 20) {
                    ScriptData.TASK_LOAD_OUTS[2].setInventoryItem(0, 309, 1, 1, 1);
                    int featherCount = ScriptData.SECURE_RANDOM.nextInt(5000 - 500 + 1) + 500;
                    ScriptData.TASK_LOAD_OUTS[2].setInventoryItem(1, 314, 1, featherCount, featherCount);
                    ScriptData.currentEntityName = "Rod Fishing spot";
                    ScriptData.currentEntityAction = "Lure";
                    switch (ScriptData.SECURE_RANDOM.nextInt(2)) {
                        case 0:
                            ScriptData.currentArea = new Area(3243, 3238, 3237, 3255); // Lumbridge along river
                            break;
                        case 1:
                            ScriptData.currentArea = new Area(3100, 3436, 3111, 3422); // Lure/Bait
                            break;
                    }
                }
                else {
                    ScriptData.TASK_LOAD_OUTS[2].setInventoryItem(0, 303, 1, 1, 1);
                    ScriptData.currentEntityName = "Fishing spot";
                    ScriptData.currentEntityAction = "Small Net";
                    switch (ScriptData.SECURE_RANDOM.nextInt((Combat.getCombatLevel() >= 15) ? 3 : 2)) {
                        case 0:
                            ScriptData.currentEntityAction = "Net";
                            ScriptData.currentArea = new Area(3249, 3144, 3237, 3163); // Lumbridge Swamp
                            break;
                        case 1:
                            ScriptData.currentArea = new Area(3264, 3152, 3279, 3137); // Al Kharid
                            break;
                        case 2:
                            ScriptData.currentArea = new Area(3090, 3219, 3079, 3239); // Draynor - Requires 15+ combat
                            break;
                    }
                }
                break;
            case 3: // Melee
                ScriptData.TASK_LOAD_OUTS[3].setInventoryItem(1, PlayerData.food, 1, ScriptData.SECURE_RANDOM.nextInt(25 - 5 + 1) + 5, ScriptData.SECURE_RANDOM.nextInt(1000 - 50 + 1) + 50); // Food
                Logger.log("Food count: " + ScriptData.TASK_LOAD_OUTS[3].getEqpItemQtyMax(1));
                int attackLevel = Skills.getRealLevel(Skill.ATTACK);
                int strengthLevel = Skills.getRealLevel(Skill.STRENGTH);
                int defenceLevel = Skills.getRealLevel(Skill.DEFENCE);
                ScriptData.TASK_LOAD_OUTS[3].setEquipmentItem(0, PlayerData.meleeHat, 1, 1, 1);
                ScriptData.TASK_LOAD_OUTS[3].setEquipmentItem(1, PlayerData.meleeChest, 1, 1, 1);
                ScriptData.TASK_LOAD_OUTS[3].setEquipmentItem(2, PlayerData.meleeLegs, 1, 1, 1);
                ScriptData.TASK_LOAD_OUTS[3].setEquipmentItem(3, PlayerData.meleeShield, 1, 1, 1);
                ScriptData.TASK_LOAD_OUTS[3].setEquipmentItem(4, PlayerData.meleeWeapon, 1, 1, 1);
                if (attackLevel >= 30 && strengthLevel >= 30 && defenceLevel >= 30) {
                    switch (ScriptData.SECURE_RANDOM.nextInt(3)) {
                        case 0:
                            ScriptData.currentEntityName = "Hill Giant";
                            break;
                        case 1:
                            ScriptData.currentEntityName = "Giant frog";
                            break;
                        case 2:
                            ScriptData.currentEntityName = "Hobgoblin";
                            break;
                    }
                }
                else if (attackLevel >= 20 && strengthLevel >= 20 && defenceLevel >= 20) {
                    switch (ScriptData.SECURE_RANDOM.nextInt(4)) {
                        case 0:
                            ScriptData.currentEntityName = "Minotaur";
                            break;
                        case 1:
                            ScriptData.currentEntityName = "Al Kharid warrior";
                            break;
                        case 2:
                            ScriptData.currentEntityName = "Barbarian";
                            break;
                        case 3:
                            ScriptData.currentEntityName = "Giant frog";
                            break;
                    }
                }
                else if (attackLevel >= 10 && strengthLevel >= 10 && defenceLevel >= 10) {
                    switch (ScriptData.SECURE_RANDOM.nextInt(4)) {
                        case 0:
                            ScriptData.currentEntityName = "Cow";
                            break;
                        case 1:
                            ScriptData.currentEntityName = "Man";
                            break;
                        case 2:
                            ScriptData.currentEntityName = "Minotaur"; // Level 7
                            break;
                        case 3:
                            ScriptData.currentEntityName = "Giant rat";
                            break;
                    }
                }
                else {
                    switch (ScriptData.SECURE_RANDOM.nextInt(4)) {
                        case 0:
                            ScriptData.currentEntityName = "Chicken";
                            ScriptData.TASK_LOAD_OUTS[3].setInventoryItem(1, 0, 0, 0);
                            break;
                        case 1:
                            ScriptData.currentEntityName = "Giant rat";
                            break;
                        case 2:
                            ScriptData.currentEntityName = "Cow";
                            break;
                        case 3:
                            ScriptData.currentEntityName = "Goblin";
                            break;
                    }
                }
                PlayerData.determineMeleeCombatStyle(attackLevel, strengthLevel, defenceLevel);
                PlayerData.determineSwitchMeleeCombatStyleLevel(attackLevel, strengthLevel, defenceLevel);
                ScriptData.currentArea = currentEntityNameCurrentAreaFactory();
                break;
            case 5: // Ranged
                int initMax = ScriptData.SECURE_RANDOM.nextInt(1000 - 50 + 1) + 50;
                ScriptData.TASK_LOAD_OUTS[5].setInventoryItem(1, PlayerData.food, ScriptData.SECURE_RANDOM.nextInt(25 - 5 + 1) + 5, initMax, initMax);
                initMax = ScriptData.SECURE_RANDOM.nextInt(1000 - 50 + 1) + 50;
                ScriptData.TASK_LOAD_OUTS[5].setEquipmentItem(5, PlayerData.rangedArrows, ScriptData.SECURE_RANDOM.nextInt(25 - 5 + 1) + 5, initMax, initMax);
                ScriptData.TASK_LOAD_OUTS[5].setInventoryItem(0, 983, 0, 0, 0);
                int rangedLevel = Skills.getRealLevel(Skill.RANGED);
                if (rangedLevel >= 50) {
                    switch (ScriptData.SECURE_RANDOM.nextInt(3)) {
                        case 0:
                            ScriptData.currentEntityName = "Hill Giant";
                            break;
                        case 1:
                            ScriptData.currentEntityName = "Giant frog";
                            break;
                        case 2:
                            ScriptData.currentEntityName = "Moss giant";
                            break;
                    }
                }
                else if (rangedLevel >= 30) {
                    switch (ScriptData.SECURE_RANDOM.nextInt(2)) {
                        case 0:
                            ScriptData.currentEntityName = "Hill Giant";
                            ScriptData.TASK_LOAD_OUTS[5].setInventoryItem(0, 983, 1, 1, 1);
                            break;
                        case 1:
                            ScriptData.currentEntityName = "Giant frog";
                            break;
                    }
                }
                else if (rangedLevel >= 20) {
                    switch (ScriptData.SECURE_RANDOM.nextInt(3)) {
                        case 0:
                            ScriptData.currentEntityName = "Barbarian";
                            break;
                        case 1:
                            ScriptData.currentEntityName = "Wizard";
                            break;
                        case 2:
                            ScriptData.currentEntityName = "Minotaur";
                            break;
                    }
                }
                else {
                    switch (ScriptData.SECURE_RANDOM.nextInt(3)) {
                        case 0:
                            ScriptData.currentEntityName = "Chicken";
                            ScriptData.TASK_LOAD_OUTS[5].setInventoryItem(1, 0, 0, 0);
                            break;
                        case 1:
                            ScriptData.currentEntityName = "Cow";
                            break;
                        case 2:
                            ScriptData.currentEntityName = "Goblin";
                            break;
                    }
                }
                PlayerData.determineRangedCombatStyle();
                PlayerData.determineSwitchRangedCombatStyleLevel(rangedLevel);
                ScriptData.currentArea = currentEntityNameCurrentAreaFactory();
                break;
            case 6: // Runecrafting
                if (Skills.getRealLevel(Skill.RUNECRAFTING) >= 20) { // Body
                    ScriptData.currentArea = new Area(2504, 4855, 2538, 4824); // Rift
                    ScriptData.currentArea2 = new Area(3043, 3452, 3064, 3426); // Mysterious ruins
                }
                else if (Skills.getRealLevel(Skill.RUNECRAFTING) >= 14) { // Fire
                    ScriptData.currentArea = new Area(2568, 4856, 2600, 4823);
                    ScriptData.currentArea2 = new Area(3297, 3262, 3318, 3248);
                }
                else if (Skills.getRealLevel(Skill.RUNECRAFTING) >= 9) { // Earth
                    ScriptData.currentArea = new Area(2636, 4857, 2676, 4817);
                    ScriptData.currentArea2 = new Area(3295, 3478, 3310, 3463);
                }
                else { // Air
                    ScriptData.currentArea = new Area(2828, 4848, 2858, 4818);
                    ScriptData.currentArea2 = new Area(2976, 3300, 2993, 3286);
                }
                if (PlayerData.currentRunecraftingMedium > 5000) {
                    ScriptData.TASK_LOAD_OUTS[6].setEquipmentItem(0, PlayerData.currentRunecraftingMedium, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[6].setInventoryItem(0, 0, 0, 0);
                    ScriptData.TASK_LOAD_OUTS[6].setInventoryItem(1, 7936, 1, 28, ScriptData.SECURE_RANDOM.nextInt(3000 - 300 + 1) + 300); // Pure essence
                }
                else {
                    ScriptData.TASK_LOAD_OUTS[6].setInventoryItem(0, PlayerData.currentRunecraftingMedium, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[6].setEquipmentItem(0, 0, 0, 0, 0);
                    ScriptData.TASK_LOAD_OUTS[6].setInventoryItem(1, 7936, 1, 27, ScriptData.SECURE_RANDOM.nextInt(3000 - 300 + 1) + 300);
                }
                break;
            case 1: // Firemaking
                if (Skills.getRealLevel(Skill.FIREMAKING) >= 30) {
                    ScriptData.TASK_LOAD_OUTS[1].setInventoryItem(1, 1519, 1, 27, ScriptData.SECURE_RANDOM.nextInt(500 - 100 + 1) + 100); // Willow logs
                }
                else if (Skills.getRealLevel(Skill.FIREMAKING) >= 15) {
                    ScriptData.TASK_LOAD_OUTS[1].setInventoryItem(1, 1521, 1, 27, (Skills.getExperienceForLevel(30) - Skills.getExperience(Skill.FIREMAKING)) / 60); // Oak logs
                }
                else {
                    ScriptData.TASK_LOAD_OUTS[1].setInventoryItem(1, 1511, 1, 27, (Skills.getExperienceForLevel(15) - Skills.getExperience(Skill.FIREMAKING)) / 40); // Logs
                }

                switch (ScriptData.SECURE_RANDOM.nextInt(6)) { // Area must be 27 valid blocks min, be contained within a single Region
                    case 0:
                        ScriptData.currentArea = new Area(3077, 3250, 3097, 3247); // Draynor North of Bank
                        ScriptData.currentTile = new Tile(3097, 3250, 0); // NE corner
                        break;
                    case 1:
                        ScriptData.currentArea = new Area(2995, 3364, 3032, 3360); // North of Falador East Bank
                        ScriptData.currentTile = new Tile(3032, 3360, 0); // NE corner
                        break;
                    case 2:
                        ScriptData.currentArea = new Area(3169, 3432, 3199, 3428); // South of West Varrock Bank
                        ScriptData.currentTile = new Tile(3199, 3432, 0); // NE corner
                        break;
                    case 3:
                        ScriptData.currentArea = new Area(3237, 3430, 3265, 3428); // North of East Varrock Bank
                        ScriptData.currentTile = new Tile(3265, 3430, 0); // NE corner
                        break;
                    case 4:
                        ScriptData.currentArea = new Area(3152, 3479, 3179, 3473); // South GE
                        ScriptData.currentTile = new Tile(3179, 3479, 0); // NE corner
                        break;
                    case 5:
                        ScriptData.currentArea = new Area(3150, 3507, 3181, 3500); // North GE
                        ScriptData.currentTile = new Tile(3181, 3507, 0); // NE corner
                        break;
                }
                break;
            case 7: // Smithing
                {
                    ScriptData.currentArea = new Area(3185, 3427, 3190, 3422); // Anvil (West Varrock)
                    int barsReq = ScriptData.SECURE_RANDOM.nextInt(300 - 50 + 1) + 50;
                    if (Skills.getRealLevel(Skill.SMITHING) >= 30) {
                        ScriptData.TASK_LOAD_OUTS[7].setInventoryItem(1, 2353, 1, 27, barsReq);
                    }
                    else {
                        ScriptData.TASK_LOAD_OUTS[7].setInventoryItem(1, 2349, 1, 27, barsReq);
                    }
                }
                break;
            case 0: // Cooking
                int initial = ScriptData.SECURE_RANDOM.nextInt(300 - 50 + 1) + 50;
                ScriptData.currentArea = new Area(3205, 3217, 3212, 3212); // Cook-o-matic 100
                if (Skills.getRealLevel(Skill.COOKING) >= 25) {
                    ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(0, 331, 1, 28, initial); // Raw salmon
                    ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(1, 329, 0, 0, 0); // Cooked salmon
                }
                else if (Skills.getRealLevel(Skill.COOKING) >= 15) {
                    ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(0, 335, 1, 28, initial); // Raw trout
                    ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(1, 333, 1, 0, 0); // Trout
                }
                else {
                    switch (ScriptData.SECURE_RANDOM.nextInt(5)) {
                        case 0:
                            ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(0, 317, 1, 28, initial); // Raw shrimps
                            ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(1, 315, 0, 0, 0); // Shrimps
                            break;
                        case 1:
                            ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(0, 321, 1, 28, initial); // Raw anchovies
                            ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(1, 319, 0, 0, 0); // Anchovies
                            break;
                        case 2:
                            ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(0, 2132, 1, 28, initial); // Raw beef
                            ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(1, 2142, 0, 0, 0); // Cooked meat
                            break;
                        case 3:
                            ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(0, 2138, 1, 28, initial); // Raw chicken
                            ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(1, 2140, 0, 0, 0); // Cooked chicken
                            break;
                        case 4:
                            ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(0, 2134, 1, 28, initial); // Raw rat meat
                            ScriptData.TASK_LOAD_OUTS[0].setInventoryItem(1, 2142, 0, 0, 0); // Cooked meat
                            break;
                    }
                }
                break;
            case 10: // Black Knights Fortress
                {
                    ScriptData.currentFreeQuest = FreeQuest.BLACK_KNIGHTS_FORTRESS;
                    ScriptData.dialogueOpts = new String[] {
                        "I don't care. I'm going in anyway.",
                        "I seek a quest!",
                        "I laugh in the face of danger!",
                        "Ok, I'll do my best.",
                        "Yes."
                    };
                    ScriptData.TASK_LOAD_OUTS[10] = new LoadOut(1, 7, null);
                    int foodReq = ScriptData.SECURE_RANDOM.nextInt(26 - 15 - 1) + 15;
                    ScriptData.TASK_LOAD_OUTS[10].addInventoryItem(PlayerData.food, foodReq, foodReq, foodReq);
                    ScriptData.TASK_LOAD_OUTS[10].addEquipmentItem(PlayerData.meleeWeapon, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[10].addEquipmentItem(1139, 1, 1, 1); // Bronze med helm
                    ScriptData.TASK_LOAD_OUTS[10].addEquipmentItem(1101, 1, 1, 1); // Iron chainbody
                    ScriptData.TASK_LOAD_OUTS[10].addEquipmentItem(PlayerData.meleeLegs, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[10].addEquipmentItem(PlayerData.meleeShield, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[10].addEquipmentItem(PlayerData.AMULET, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[10].addEquipmentItem(PlayerData.cape, 1, 1, 1);
                    LocalPathFinder localPathFinder = LocalPathFinder.getLocalPathFinder();
                    localPathFinder.addBlacklistedTile(new Tile(1643, 4839, 0));
                    localPathFinder.addBlacklistedTile(new Tile(1643, 4840, 0));
                    localPathFinder.addBlacklistedTile(new Tile(1633, 4842, 0));
                    localPathFinder.addBlacklistedTile(new Tile(1634, 4842, 0));
                    localPathFinder.addObstacle(new PassableObstacle("Wall", "Push"));
                    localPathFinder.addObstacle(new PassableObstacle("Sturdy door", "Open"));
                    ScriptData.PIPELINES[10] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new BlackKnightsFortressLI());
                }
                break;
            case 25: // The Restless Ghost
                ScriptData.currentFreeQuest = FreeQuest.THE_RESTLESS_GHOST;
                ScriptData.dialogueOpts = new String[] {
                    "I'm looking for a quest!",
                    "Yes.",
                    "Father Aereck sent me to talk to you.",
                    "He's got a ghost haunting his graveyard.",
                    "Yep, now tell me what the problem is."
                };
                ScriptData.TASK_LOAD_OUTS[25] = new LoadOut(0, 1, null);
                ScriptData.TASK_LOAD_OUTS[25].addEquipmentItem(552, 0, 0, 0); // GhostSpeak Amulet (uncharged)
                ScriptData.PIPELINES[25] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new TheRestlessGhostLI());
                break;
            case 11: // Cooks Assistant
                ScriptData.currentFreeQuest = FreeQuest.COOKS_ASSISTANT;
                ScriptData.PIPELINES[11] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new CooksAssistantLI());
                ScriptData.questOrder = new byte[] { // 0 - start quest, 1 - egg, 2 - bucket of milk, 3 - pot of flour (pre-grainInHopper), 4 - pot of flour (post-grainInHopper), 5 - finish quest, 6 get bucket, 7 get pot; 6 before 2, 7 before 3, 3 direct left of 4)
                    0, 1, 2, 3, 4, 6, 7, 5
                };
                ScriptData.questOrderI = 0;
                while (true) {
                    for (int i = 6; i >= 1; i--) {
                        int j = 1 + ScriptData.SECURE_RANDOM.nextInt(i);
                        byte t = ScriptData.questOrder[i];
                        ScriptData.questOrder[i] = ScriptData.questOrder[j];
                        ScriptData.questOrder[j] = t;
                    }
                    int p2 = -1, p3 = -1, p4 = -1, p6 = -1, p7 = -1;
                    for (int i = 1; i <= 6; i++) {
                        switch (ScriptData.questOrder[i]) {
                            case 2:
                                p2 = i;
                                break;
                            case 3:
                                p3 = i;
                                break;
                            case 4:
                                p4 = i;
                                break;
                            case 6:
                                p6 = i;
                                break;
                            case 7:
                                p7 = i;
                                break;
                        }
                    }
                    if (p6 < p2 && p7 < p3 && p4 == p3 + 1) {
                        break;
                    }
                }
                ScriptData.dialogueOpts = new String[] {
                    "What's wrong?",
                    "Yes.",
                    "Actually, I know where to find this stuff.",
                    "I'll get right on it."
                };
                if (ScriptData.SECURE_RANDOM.nextInt(2) == 1) { // Egg
                    ScriptData.currentArea = new Area(3168, 3308, 3186, 3288); // SW of Windmill
                }
                else {
                    ScriptData.currentArea = new Area(3225, 3301, 3235, 3295); // Farm East of River Lum
                }

                if (ScriptData.SECURE_RANDOM.nextInt(2) == 1) { // Dairy Cow
                    ScriptData.currentArea2 = new Area(3170, 3321, 3177, 3316); // North of Windmill
                }
                else {
                    ScriptData.currentArea2 = new Area(3249, 3280, 3260, 3268); // East of River Lum (2 here)
                }
                ScriptData.TASK_LOAD_OUTS[11] = new LoadOut(4, 0, null);
                ScriptData.TASK_LOAD_OUTS[11].addInventoryItem(1933, 0, 1, 0); // Pot of flour
                ScriptData.TASK_LOAD_OUTS[11].addInventoryItem(1931, 0, 1, 0); // Pot
                ScriptData.TASK_LOAD_OUTS[11].addInventoryItem(1947, 0, 1, 0); // Grain
                ScriptData.TASK_LOAD_OUTS[11].addInventoryItem(1944, 0, 1, 0); // Egg
                ScriptData.useOnGameMessageEvent = true;
                break;
            case 21: // Rune Mysteries
                ScriptData.currentFreeQuest = FreeQuest.RUNE_MYSTERIES;
                ScriptData.dialogueOpts = new String[] {
                    "Have you any quests for me?",
                    "Yes.",
                    "Okay, here you are.",
                    "Actually, I'm not interested.",
                    "Yes, certainly.",
                    "I've been sent here with a package for you.",
                    "I'd better get going."
                };
                ScriptData.TASK_LOAD_OUTS[21] = new LoadOut(3, 0, null);
                ScriptData.TASK_LOAD_OUTS[21].addInventoryItem(291, 0, 1, 0); // Research notes
                ScriptData.TASK_LOAD_OUTS[21].addInventoryItem(290, 0, 1, 0); // Research package
                ScriptData.TASK_LOAD_OUTS[21].addInventoryItem(1438, 0, 1, 0); // Air talisman
                ScriptData.PIPELINES[21] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new RuneMysteriesLI());
                break;
            case 28: // X Marks the Spot
                ScriptData.currentFreeQuest = FreeQuest.X_MARKS_THE_SPOT;
                ScriptData.dialogueOpts = new String[] {
                    "I'm looking for a quest.",
                    "Yes.",
                    "Okay, thanks Veos.",
                    "I'm good thanks."
                };
                ScriptData.TASK_LOAD_OUTS[28] = new LoadOut(5, 0, null);
                ScriptData.TASK_LOAD_OUTS[28].addInventoryItem(952, 1, 1, 1); // Spade
                ScriptData.TASK_LOAD_OUTS[28].addInventoryItem(23069, 0, 1, 0); // Mysterious orb
                ScriptData.TASK_LOAD_OUTS[28].addInventoryItem(23067, 0, 1, 0); // Treasure scroll (Step 1)
                ScriptData.TASK_LOAD_OUTS[28].addInventoryItem(23068, 0, 1, 0); // Treasure scroll (Step 2)
                ScriptData.TASK_LOAD_OUTS[28].addInventoryItem(23070, 0, 1, 0); // Treasure scroll (Step 3)
                ScriptData.PIPELINES[28] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new FinishQuestXMarksTheSpotLI(), new XMarksTheSpotLI());
                break;
            case 18: // Pirates Treasure
                {
                    ScriptData.currentFreeQuest = FreeQuest.PIRATES_TREASURE;
                    ScriptData.useOnGameMessageEvent = true;
                    ScriptData.dialogueOpts = new String[] {
                        "I'm in search of treasure.",
                        "Yes.",
                        "Ok, I will bring you some rum",
                        "Could you offer me employment on your plantation?",
                        "Thank you, I'll be on my way",
                        "Well, can I get a job here?",
                        "Ok thanks, I'll go and get it.",
                        "Can I journey on this ship?",
                        "Search away, I have nothing to hide.",
                        "Ok.",
                        "Yes please.",
                        "No, the crate isn't full yet.",
                        "Will you pay me for another crate full?"
                    };
                    ScriptData.TASK_LOAD_OUTS[18] = new LoadOut(2, 1, null);
                    int coinsReq = (ScriptData.SECURE_RANDOM.nextInt(7 - 3 + 1) + 3) * 30;
                    ScriptData.TASK_LOAD_OUTS[18].addInventoryItem(995, coinsReq, coinsReq, coinsReq); // Coins
                    ScriptData.TASK_LOAD_OUTS[18].addInventoryItem(952, 0, 1, 0); // Spade
                    ScriptData.TASK_LOAD_OUTS[18].addEquipmentItem(PlayerData.meleeWeapon, 1, 1, 1);
                    ScriptData.PIPELINES[18] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, new HandleDialoguePiratesTreasureLI(), ScriptData.finishQuestLI, new PiratesTreasureLI());
                }
                break;
            case 27: // Witchs Potion
                {
                    ScriptData.currentFreeQuest = FreeQuest.WITCHS_POTION;
                    ScriptData.questOrder = new byte[] { // 0 - start quest, 1 - burnt meat, 2 - Eye of newt, 3 - Onion, 4 - Rat's tail (Requires start quest), 5 - finish quest
                        0, 1, 2, 3, 4, 5
                    };
                    ScriptData.questOrderI = 0;
                    for (byte i = 4; i >= 1; i--) { // shuffle 0-4
                        int j = ScriptData.SECURE_RANDOM.nextInt(i + 1);
                        byte t = ScriptData.questOrder[i];
                        ScriptData.questOrder[i] = ScriptData.questOrder[j];
                        ScriptData.questOrder[j] = t;
                    }
                    byte p0 = -1, p4 = -1; // 4 must be after 0 (p4 > p0)
                    for (byte i = 0; i <= 4; i++) {
                        byte v = ScriptData.questOrder[i];
                        if (v == 0) {
                            p0 = i;
                        }
                        else if (v == 4) {
                            p4 = i;
                        }
                    }
                    if (p4 < p0) { // swap 4 with 0 if 4 comes before 0
                        byte t = ScriptData.questOrder[p4];
                        ScriptData.questOrder[p4] = ScriptData.questOrder[p0];
                        ScriptData.questOrder[p0] = t;
                    }
                    if (ScriptData.SECURE_RANDOM.nextInt(2) == 0) {
                        ScriptData.currentEntityName = "Giant rat";
                    }
                    else {
                        ScriptData.currentEntityName = "Cow";
                    }
                    ScriptData.currentArea = currentEntityNameCurrentAreaFactory();
                    switch (ScriptData.SECURE_RANDOM.nextInt(2)) { // Onion area
                        case 0:
                            ScriptData.currentArea2 = new Area(3186, 3269, 3192, 3265); // Farmer Fred
                            break;
                        case 1:
                            ScriptData.currentArea2 = new Area(2945, 3254, 2956, 3248); // Rimmington
                            break;
                    }
                    ScriptData.dialogueOpts = new String[] {
                        "I am in search of a quest.",
                        "Yes.",
                        "Yes, help me become one with my darker side."
                    };
                    ScriptData.TASK_LOAD_OUTS[27] = new LoadOut(2, 1, null);
                    int coinsReq = ScriptData.SECURE_RANDOM.nextInt(100 - 3 + 1) + 3;
                    ScriptData.TASK_LOAD_OUTS[27].addInventoryItem(995, coinsReq, coinsReq, coinsReq); // Coins
                    ScriptData.TASK_LOAD_OUTS[27].addEquipmentItem(PlayerData.meleeWeapon, 1, 1, 1);
                    ScriptData.PIPELINES[27] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new WitchsPotionLI());
                }
                break;
            case 14: // Ernest the Chicken
                ScriptData.currentFreeQuest = FreeQuest.ERNEST_THE_CHICKEN;
                ScriptData.useOnGameMessageEvent = true;
                ScriptData.questOrder = new byte[] { // 0 - start quest, 1 - oil can, 2 - Pressure gauge (pre-poisoned fountain), 3 - Pressure gauge (post-poisoned fountain), 4 - Rubber tube, 5 - finish quest
                    0, 1, 2, 3, 4, 5
                };
                ScriptData.questOrderI = 0;
                for (byte i = 4; i >= 1; i--) { // shuffle 1-4
                    int j = ScriptData.SECURE_RANDOM.nextInt(i) + 1;
                    byte t = ScriptData.questOrder[i];
                    ScriptData.questOrder[i] = ScriptData.questOrder[j];
                    ScriptData.questOrder[j] = t;
                }
                byte p2 = -1, p3 = -1;
                for (byte i = 1; i < 5; i++) {
                    if (ScriptData.questOrder[i] == 2) {
                        p2 = i;
                    }
                    else if (ScriptData.questOrder[i] == 3) {
                        p3 = i;
                    }
                }
                if (p2 > p3) { // 2 must come before 3
                    byte temp = ScriptData.questOrder[p3];
                    ScriptData.questOrder[p3] = ScriptData.questOrder[p2];
                    ScriptData.questOrder[p2] = temp;
                }
                ScriptData.dialogueOpts = new String[] {
                    "Yes.",
                    "I'm looking for a guy called Ernest.",
                    "Change him back this instant!"
                };
                ScriptData.TASK_LOAD_OUTS[14] = new LoadOut(5, 0, null);
                ScriptData.TASK_LOAD_OUTS[14].addInventoryItem(952, 0, 1, 0); // Spade
                ScriptData.TASK_LOAD_OUTS[14].addInventoryItem(275, 0, 1, 0); // Key
                ScriptData.TASK_LOAD_OUTS[14].addInventoryItem(276, 0, 1, 0); // Rubber tube
                ScriptData.TASK_LOAD_OUTS[14].addInventoryItem(277, 0, 1, 0); // Oil can
                ScriptData.TASK_LOAD_OUTS[14].addInventoryItem(271, 0, 1, 0); // Pressure gauge
                ScriptData.PIPELINES[14] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new WitchsPotionLI());
                break;
            case 24: // The Knights Sword
                ScriptData.currentFreeQuest = FreeQuest.THE_KNIGHTS_SWORD;
                if (ScriptData.SECURE_RANDOM.nextInt(2) == 1) { // Blurite rock tile
                    ScriptData.currentTile = new Tile(3049, 9567, 0);
                }
                else {
                    ScriptData.currentTile = new Tile(3059, 9565, 0);
                }
                ScriptData.dialogueOpts = new String[] {
                    "And how is life as a squire?",
                    "I can make a new sword if you like...",
                    "So would these dwarves make another one?",
                    "Are you an Imcando dwarf? I need a special sword.",
                    "Ok, I'll give it a go.",
                    "Yes.",
                    "What do you know about the Imcando dwarves?",
                    "Would you like a redberry pie?",
                    "Something else.",
                    "Can you make a special sword for me?",
                    "About that sword...",
                    "Can you make that replacement sword now?"
                };
                if (ScriptData.TASK_LOAD_OUTS[24] == null) {
                    if (PlayerData.canEquipPickaxe) {
                        ScriptData.TASK_LOAD_OUTS[24] = new LoadOut(2, 1, null);
                        ScriptData.TASK_LOAD_OUTS[24].addEquipmentItem(PlayerData.pickaxe, 1, 1, 1);
                    }
                    else {
                        ScriptData.TASK_LOAD_OUTS[24] = new LoadOut(3, 0, null);
                        ScriptData.TASK_LOAD_OUTS[24].addInventoryItem(PlayerData.pickaxe, 1, 1, 1);
                    }
                    ScriptData.TASK_LOAD_OUTS[24].addInventoryItem(2351, 0, 2, 2); // Iron bar
                    ScriptData.TASK_LOAD_OUTS[24].addInventoryItem(2325, 0, 1, 1); // Redberry pie
                    ScriptData.PIPELINES[24] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new TheKnightsSwordLI());
                }
                break;
            case 17: // Misthalin Mystery
                {
                    ScriptData.currentFreeQuest = FreeQuest.MISTHALIN_MYSTERY;
                    ScriptData.dialogueOpts = new String[] {
                        "",
                        "Yes.",
                        "What has happened here?",
                        "What do you want me to do?",
                    };
                    switch (ScriptData.SECURE_RANDOM.nextInt(3)) {
                        case 0:
                            ScriptData.dialogueOpts[0] = "Count Draynor";
                            break;
                        case 1:
                            ScriptData.dialogueOpts[0] = "Count Check";
                            break;
                        case 2:
                            ScriptData.dialogueOpts[0] = "Lord Drakan";
                            break;
                    }
                    ScriptData.questOrder = new byte[] { 0, 1, 2, 3 };
                    for (int i = ScriptData.questOrder.length - 1; i > 0; i--) { // Fisher-Yates shuffle
                        int j = ScriptData.SECURE_RANDOM.nextInt(i + 1);
                        byte temp = ScriptData.questOrder[i];
                        ScriptData.questOrder[i] = ScriptData.questOrder[j];
                        ScriptData.questOrder[j] = temp;
                    }
                    if (ScriptData.TASK_LOAD_OUTS[17] == null) {
                        ScriptData.TASK_LOAD_OUTS[17] = new LoadOut(7, 1, null);
                        ScriptData.TASK_LOAD_OUTS[17].addEquipmentItem(21059, 1, 0, 0); // Killer's knife
                        ScriptData.TASK_LOAD_OUTS[17].addInventoryItem(1925, 0, 1, 0); // Bucket
                        ScriptData.TASK_LOAD_OUTS[17].addInventoryItem(1929, 0, 1, 0); // Bucket of water
                        ScriptData.TASK_LOAD_OUTS[17].addInventoryItem(21056, 0, 1, 0); // Notes
                        ScriptData.TASK_LOAD_OUTS[17].addInventoryItem(21057, 0, 1, 0); // Notes
                        ScriptData.TASK_LOAD_OUTS[17].addInventoryItem(21058, 0, 1, 0); // Notes
                        ScriptData.TASK_LOAD_OUTS[17].addInventoryItem(946, 0, 1, 0); // Knife
                        ScriptData.TASK_LOAD_OUTS[17].addInventoryItem(590, 0, 1, 0); // Tinderbox
                        ScriptData.PIPELINES[17] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new MisthalinMysteryLI());
                    }
                    LocalPathFinder localPathFinder = LocalPathFinder.getLocalPathFinder();
                    localPathFinder.addBlacklistedTile(new Tile(1643, 4839, 0));
                    localPathFinder.addBlacklistedTile(new Tile(1643, 4840, 0));
                    localPathFinder.addBlacklistedTile(new Tile(1633, 4842, 0));
                    localPathFinder.addBlacklistedTile(new Tile(1634, 4842, 0));
                    localPathFinder.addObstacle(new PassableObstacle("Damaged wall", "Climb"));

                }
                break;
            case 19: // Prince Ali Rescue
                {
                    ScriptData.currentFreeQuest = FreeQuest.PRINCE_ALI_RESCUE;
                    ScriptData.dialogueOpts = new String[] {
                        "Is there anything I can help you with?",
                        "Yes.",
                        "Do you know why they've taken the Prince?",
                        "No. I think I know everything I need to.",
                        "Okay, I better find some things.",
                        "Could you make other things apart from rope?",
                        "How about some sort of wig?",
                        "I have them here. Please make me a wig.",
                        "Yes please. Mix me some skin paste.",
                        "Can you make skin paste?",
                        "Heard of you? You're famous in Gielinor!",
                        "What's your latest plan then?",
                        "How do you know someone won't try to free him?",
                        "Could I see the key please?",
                        "Could I touch the key for a moment please?",
                        "I have some beer here, fancy one?"
                    };
                    if (ScriptData.TASK_LOAD_OUTS[19] == null) {
                        ScriptData.TASK_LOAD_OUTS[19] = new LoadOut(12, 0, null);
                        ScriptData.TASK_LOAD_OUTS[19].addInventoryItem(1761, 1, 1, 1); // Soft clay
                        ScriptData.TASK_LOAD_OUTS[19].addInventoryItem(1759, 3, 3, 3); // Ball of wool
                        ScriptData.TASK_LOAD_OUTS[19].addInventoryItem(592, 1, 1, 1); // Ashes
                        ScriptData.TASK_LOAD_OUTS[19].addInventoryItem(1933, 1, 1, 1); // Pot of flour
                        ScriptData.TASK_LOAD_OUTS[19].addInventoryItem(1929, 1, 1, 1); // Bucket of water
                        ScriptData.TASK_LOAD_OUTS[19].addInventoryItem(1765, 1, 1, 1); // Yellow dye
                        ScriptData.TASK_LOAD_OUTS[19].addInventoryItem(1951, 1, 1, 1); // Redberries
                        ScriptData.TASK_LOAD_OUTS[19].addInventoryItem(2349, 1, 1, 1); // Bronze bar
                        ScriptData.TASK_LOAD_OUTS[19].addInventoryItem(1013, 1, 1, 1); // Pink skirt
                        ScriptData.TASK_LOAD_OUTS[19].addInventoryItem(1917, 3, 3, 3); // Beer
                        ScriptData.TASK_LOAD_OUTS[19].addInventoryItem(954, 1, 1, 1); // Rope
                        int foodReq = ScriptData.SECURE_RANDOM.nextInt(13 - 7 + 1) + 7;
                        ScriptData.TASK_LOAD_OUTS[19].addInventoryItem(PlayerData.food, foodReq, foodReq, foodReq);
                        ScriptData.PIPELINES[19] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new PrinceAliRescueLI());
                    }
                }
                break;
            case 9: // Below Ice Mountain
                {
                    ScriptData.currentFreeQuest = FreeQuest.BELOW_ICE_MOUNTAIN;
                    ScriptData.dialogueOpts = new String[] {
                        "",
                        "Yes.",
                        "I'm looking for a man named Marley. Have you seen him?",
                        "I was wondering if you'd be able to make me a Steak sandwich?"
                    };
                    switch (ScriptData.SECURE_RANDOM.nextInt(3)) {
                        case 0:
                            ScriptData.dialogueOpts[0] = "Rock";
                            break;
                        case 1:
                            ScriptData.dialogueOpts[0] = "Paper";
                            break;
                        case 2:
                            ScriptData.dialogueOpts[0] = "Scissors";
                            break;
                    }
                    if (ScriptData.TASK_LOAD_OUTS[9] == null) {
                        ScriptData.TASK_LOAD_OUTS[9] = new LoadOut(5, 7, null);
                        ScriptData.TASK_LOAD_OUTS[9].addInventoryItem(2309, 1, 1, 1); // Bread
                        ScriptData.TASK_LOAD_OUTS[9].addInventoryItem(2142, 1, 1, 1); // Cooked meat
                        ScriptData.TASK_LOAD_OUTS[9].addInventoryItem(946, 1, 1, 1); // Knife
                        ScriptData.TASK_LOAD_OUTS[9].addInventoryItem(1917, 1, 1, 1); // Beer
                        int foodReq = ScriptData.SECURE_RANDOM.nextInt(24 - 15 - 1) + 15;
                        ScriptData.TASK_LOAD_OUTS[9].addInventoryItem(PlayerData.food, foodReq, foodReq, foodReq);
                        ScriptData.TASK_LOAD_OUTS[9].addEquipmentItem(PlayerData.bestPickaxeAvail, 1, 1, 1);
                        ScriptData.TASK_LOAD_OUTS[9].addEquipmentItem(PlayerData.meleeHat, 1, 1, 1);
                        ScriptData.TASK_LOAD_OUTS[9].addEquipmentItem(PlayerData.meleeChest, 1, 1, 1);
                        ScriptData.TASK_LOAD_OUTS[9].addEquipmentItem(PlayerData.meleeLegs, 1, 1, 1);
                        ScriptData.TASK_LOAD_OUTS[9].addEquipmentItem(PlayerData.meleeShield, 1, 1, 1);
                        ScriptData.TASK_LOAD_OUTS[9].addEquipmentItem(PlayerData.AMULET, 1, 1, 1);
                        ScriptData.TASK_LOAD_OUTS[9].addEquipmentItem(PlayerData.cape, 1, 1, 1);
                        ScriptData.PIPELINES[9] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new BelowIceMountainLI());
                    }
                }
                break;
            case 22: // Sheep Shearer
                ScriptData.currentFreeQuest = FreeQuest.SHEEP_SHEARER;
                ScriptData.dialogueOpts = new String[] {
                    "I'm looking for a quest.",
                    "Yes."
                };
                ScriptData.TASK_LOAD_OUTS[22] = new LoadOut(0, 0, null);
                ScriptData.PIPELINES[22] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new SheepShearerLI());
                break;
            case 20: // Romeo and Juliet
                ScriptData.currentFreeQuest = FreeQuest.ROMEO_AND_JULIET;
                byte[] questStageOpts = new byte[] { 0, 10, 20, 30, 40 };
                ScriptData.questOrderI = questStageOpts[ScriptData.SECURE_RANDOM.nextInt(questStageOpts.length)]; // pickCadavaBerryStage
                ScriptData.dialogueOpts = new String[] {
                    "Perhaps I could help to find her for you?",
                    "Yes.",
                    "Ok, thanks",
                    "Talk about something else.",
                    "Talk about Romeo & Juliet."
                };
                ScriptData.TASK_LOAD_OUTS[20] = new LoadOut(0, 0, null);
                ScriptData.PIPELINES[20] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new RomeoAndJulietLI());
                break;
            case 16: // Imp Catcher
                ScriptData.currentFreeQuest = FreeQuest.IMP_CATCHER;
                ScriptData.dialogueOpts = new String[] {
                    "Give me a quest please.",
                    "Yes."
                };
                ScriptData.TASK_LOAD_OUTS[16] = new LoadOut(4, 0, null);
                ScriptData.TASK_LOAD_OUTS[16].addInventoryItem(1470, 1, 1, 1); // Red bead
                ScriptData.TASK_LOAD_OUTS[16].addInventoryItem(1476, 1, 1, 1); // White bead
                ScriptData.TASK_LOAD_OUTS[16].addInventoryItem(1472, 1, 1, 1); // Yellow bead
                ScriptData.TASK_LOAD_OUTS[16].addInventoryItem(1474, 1, 1, 1); // Black bead
                ScriptData.PIPELINES[16] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new ImpCatcherLI());
                break;
            case 26: // Vampyre Slayer
                {
                    ScriptData.currentFreeQuest = FreeQuest.VAMPYRE_SLAYER;
                    ScriptData.dialogueOpts = new String[] {
                        "Yes.",
                        "Ok, I'm up for an adventure.",
                        "A glass of your finest ale please.",
                        "Morgan needs your help!"
                    };
                    ScriptData.TASK_LOAD_OUTS[16] = new LoadOut(4, 7, null);
                    ScriptData.TASK_LOAD_OUTS[16].addInventoryItem(2347, 1, 1, 1); // Hammer
                    ScriptData.TASK_LOAD_OUTS[16].addInventoryItem(1917, 1, 1, 1); // Beer
                    ScriptData.TASK_LOAD_OUTS[16].addInventoryItem(1549, 0, 1, 0); // Stake
                    int foodReq = ScriptData.SECURE_RANDOM.nextInt(24 - 10 - 1) + 10;
                    ScriptData.TASK_LOAD_OUTS[16].addInventoryItem(PlayerData.food, foodReq, foodReq, foodReq);
                    ScriptData.TASK_LOAD_OUTS[16].addEquipmentItem(PlayerData.meleeWeapon, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[16].addEquipmentItem(PlayerData.meleeHat, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[16].addEquipmentItem(PlayerData.meleeChest, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[16].addEquipmentItem(PlayerData.meleeLegs, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[16].addEquipmentItem(PlayerData.meleeShield, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[16].addEquipmentItem(PlayerData.AMULET, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[16].addEquipmentItem(PlayerData.cape, 1, 1, 1);
                    ScriptData.PIPELINES[16] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new VampyreSlayerLI());
                }
                break;
            case 23: // The Corsair Curse
                {
                    ScriptData.currentFreeQuest = FreeQuest.THE_CORSAIR_CURSE;
                    ScriptData.dialogueOpts = new String[] {
                        "What kind of help do you need?",
                        "Sure, I'll try to help with your curse.",
                        "Yes.",
                        "Okay, I'm ready go to Corsair Cove.",
                        "Let's go",
                        "I hear you've been cursed.",
                        "Where did you find this mermaid?",
                        "Where did you say this happened?",
                        "Arsen says he gave you a sacred ogre relic.",
                        "A man called Arsen told me he'd visited your cave.",
                        "About that sacred ogre relic...",
                        "I've come to return what Arsen stole.",
                        "So you never cursed the Corsairs for stealing from you?",
                        "Search for the possessed doll and face the consequences.",
                        "I've ruled out all the Corsairs' theories...",
                        "So what do I do now?",
                        "I hear it happened straight after dinner.",
                        "I hear Ithoi cooked the meal you ate that night",
                        "What is the mission Francois is doing?",
                        "I hear the Captain's thinking of firing Ithoi.",
                        "I hear you cooked the meal they ate before getting sick.",
                        "Maybe because the Captain's thinking of firing you.",
                        "I know you've faked the curse.",
                        "I bet I can prove you're well enough to get up.",
                        "I saw you running around! You're not sick!",
                        "I've seen Ithoi running around. He's not sick at all.",
                        "I'll be back.",
                        "I've killed Ithoi for poisoning your crew."
                    };
                    ScriptData.questOrder = new byte[] { 0, 1, 2 };
                    for (int i = 2; i >= 0; i--) { // Fisher-Yates shuffle 0-2
                        int j = ScriptData.SECURE_RANDOM.nextInt(3);
                        byte temp = ScriptData.questOrder[i];
                        ScriptData.questOrder[i] = ScriptData.questOrder[j];
                        ScriptData.questOrder[j] = temp;
                    }
                    ScriptData.TASK_LOAD_OUTS[23] = new LoadOut(1, 7, null);
                    int foodReq = ScriptData.SECURE_RANDOM.nextInt(26 - 15 - 1) + 15;
                    ScriptData.TASK_LOAD_OUTS[23].addInventoryItem(PlayerData.food, foodReq, foodReq, foodReq);
                    ScriptData.TASK_LOAD_OUTS[23].addEquipmentItem(PlayerData.meleeWeapon, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[23].addEquipmentItem(PlayerData.rangedHat, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[23].addEquipmentItem(PlayerData.rangedHands, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[23].addEquipmentItem(PlayerData.rangedChest, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[23].addEquipmentItem(PlayerData.rangedLegs, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[23].addEquipmentItem(PlayerData.AMULET, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[23].addEquipmentItem(PlayerData.cape, 1, 1, 1);
                    ScriptData.PIPELINES[23] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new TheCorsairCurseLI());
                }
                break;
            case 12: // Demon Slayer
                {
                    ScriptData.currentFreeQuest = FreeQuest.DEMON_SLAYER;
                    ScriptData.dialogueOpts = new String[] {
                        "Okay, thanks. I'll do my best to stop the demon.",
                        "Yes.",
                        "Ok, here you go.",
                        "Okay, where is he? I'll kill him for you!",
                        "So how did Wally kill Delrith?",
                        "Where can I find Silverlight?",
                        "The Demon Slayer Quest",
                        "Aris said I should come and talk to you.",
                        "I need to find Silverlight.",
                        "He's back and unfortunately I've got to deal with him.",
                        "And why is this a problem?",
                        "Can you give me your key?",
                        "So what does the drain lead to?",
                        "Well I'd better go key hunting.",
                        "Yes I know, but this is important.",
                        "There's a demon who wants to invade this city.",
                        "Yes, very.",
                        "It's not them who are going to fight the demon, it's me.",
                        "Sir Prysin said you would give me the key.",
                        "Why did he give you one of the keys then?",
                        "Talk about Demon Slayer.",
                        "Well, have you got any keys knocking around?",
                        "I'll get the bones for you."
                    };
                    ScriptData.questOrder = new byte[] { 0, 1, 2 };
                    for (int i = 2; i >= 0; i--) { // Fisher-Yates shuffle 0-2
                        int j = ScriptData.SECURE_RANDOM.nextInt(3);
                        byte temp = ScriptData.questOrder[i];
                        ScriptData.questOrder[i] = ScriptData.questOrder[j];
                        ScriptData.questOrder[j] = temp;
                    }
                    ScriptData.TASK_LOAD_OUTS[12] = new LoadOut(1, 7, null);
                    int foodReq = ScriptData.SECURE_RANDOM.nextInt(26 - 15 - 1) + 15;
                    ScriptData.TASK_LOAD_OUTS[12].addInventoryItem(PlayerData.food, foodReq, foodReq, foodReq);
                    ScriptData.TASK_LOAD_OUTS[12].addEquipmentItem(2402, 1, 1, 1); // Silverlight
                    ScriptData.TASK_LOAD_OUTS[12].addEquipmentItem(PlayerData.rangedHat, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[12].addEquipmentItem(PlayerData.rangedHands, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[12].addEquipmentItem(PlayerData.rangedChest, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[12].addEquipmentItem(PlayerData.rangedLegs, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[12].addEquipmentItem(PlayerData.AMULET, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[12].addEquipmentItem(PlayerData.cape, 1, 1, 1);
                    ScriptData.PIPELINES[12] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, new HandleDialogueDemonSlayerLI(), ScriptData.finishQuestLI, new DemonSlayerLI());
                }
                break;
            case 13: // Dorics Quest
                ScriptData.currentFreeQuest = FreeQuest.DORICS_QUEST;
                ScriptData.dialogueOpts = new String[] {
                    "I wanted to use your anvils.",
                    "Yes.",
                    "Certainly, I'll be right back!"
                };
                ScriptData.questOrder = new byte[] { 0, 1, 2, 3 };
                for (int i = 2; i >= 0; i--) { // Fisher-Yates shuffle 0-2
                    int j = ScriptData.SECURE_RANDOM.nextInt(3);
                    byte temp = ScriptData.questOrder[i];
                    ScriptData.questOrder[i] = ScriptData.questOrder[j];
                    ScriptData.questOrder[j] = temp;
                }
                switch (ScriptData.SECURE_RANDOM.nextInt(5)) { // Copper rocks
                    case 0:
                        ScriptData.currentArea = new Area(3027, 9829, 3034, 9822); // Dwarven Mine W-N
                        break;
                    case 1:
                        ScriptData.currentArea = new Area(3018, 9804, 3028, 9796); // Dwarven Mine W-S
                        break;
                    case 2:
                        ScriptData.currentArea = new Area(3226, 3149, 3232, 3141); // Lumbridge Swamp E
                        break;
                    case 3:
                        ScriptData.currentArea = new Area(2974, 3250, 2981, 3242); // Rimmington
                        break;
                    case 4:
                        ScriptData.currentArea = new Area(3281, 3369, 3292, 3359); // Varrock SE
                        break;
                }
                switch (ScriptData.SECURE_RANDOM.nextInt(3)) { // Clay rocks
                    case 0:
                        ScriptData.currentArea2 = new Area(3050, 9821, 3055, 9816); // Dwarven Mine E
                        break;
                    case 1:
                        ScriptData.currentArea2 = new Area(3026, 9812, 3031, 9806); // Dwarven Mine W
                        break;
                    case 2:
                        ScriptData.currentArea2 = new Area(2984, 3241, 2988, 3237); // Rimmington
                        break;
                }
                switch (ScriptData.SECURE_RANDOM.nextInt(4)) { // Iron rocks
                    case 0:
                        ScriptData.currentArea3 = new Area(3050, 9829, 3056, 9820); // Dwarven Mine E
                        break;
                    case 1:
                        ScriptData.currentArea3 = new Area(3029, 9828, 3034, 9822); // Dwarven Mine W
                        break;
                    case 2:
                        ScriptData.currentArea3 = new Area(3405, 3167, 3398, 3172); // Citharde Abbey
                        break;
                    case 3:
                        ScriptData.currentArea3 = new Area(2967, 3248, 2984, 3230); // Rimmington
                        break;
                }
                if (PlayerData.canEquipPickaxe) {
                    ScriptData.TASK_LOAD_OUTS[13] = new LoadOut(0, 1, null);
                    ScriptData.TASK_LOAD_OUTS[13].addEquipmentItem(PlayerData.pickaxe, 1, 1, 1);
                }
                else {
                    ScriptData.TASK_LOAD_OUTS[13] = new LoadOut(1, 0, null);
                    ScriptData.TASK_LOAD_OUTS[13].addInventoryItem(PlayerData.pickaxe, 1, 1, 1);
                }
                ScriptData.PIPELINES[13] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new DoricsQuestLI());
                break;

            case 15: // Goblin Diplomacy
                {
                    ScriptData.currentFreeQuest = FreeQuest.GOBLIN_DIPLOMACY;
                    ScriptData.dialogueOpts = new String[] {
                        "Do you want me to pick an armour colour for you?",
                        "Yes.",
                        "What about a different colour?",
                        "So How is life for the goblins?",
                        "I have some blue armour here",
                        "I have some orange armour here",
                        "I have some brown armour here",
                        "I'll leave you to it.",
                        "Yes please, I need woad leaves.",
                        "How about 20 coins?",
                        "What could you make for me?",
                        "I'm okay, thanks.",
                        "No thanks, I am happy the colour I am."
                    };

                    ScriptData.questOrder = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 }; // 0-2 get redberries/woad leaf/onions, 3-5 purchase dyes, 6-8 get goblin mail, 9 create orange dye, 10 create orange goblin mail, 11 give orange goblin mail, 12 create blue goblin mail, 13 give blue goblin mail, 14 give goblin mail
                    for (int i = 2; i >= 0; i--) { // Fisher-Yates shuffle: 0-2
                        int j = ScriptData.SECURE_RANDOM.nextInt(i + 1);
                        byte temp = ScriptData.questOrder[i];
                        ScriptData.questOrder[i] = ScriptData.questOrder[j];
                        ScriptData.questOrder[j] = temp;
                    }
                    for (int i = 5; i >= 3; i--) { // Fisher-Yates shuffle: 3-5
                        int j = ScriptData.SECURE_RANDOM.nextInt(5 - 3 + 1) + 3;
                        byte temp = ScriptData.questOrder[i];
                        ScriptData.questOrder[i] = ScriptData.questOrder[j];
                        ScriptData.questOrder[j] = temp;
                    }
                    for (int i = 8; i >= 6; i--) { // Fisher-Yates shuffle: 6-8
                        int j = ScriptData.SECURE_RANDOM.nextInt(8 - 6 + 1) + 6;
                        byte temp = ScriptData.questOrder[i];
                        ScriptData.questOrder[i] = ScriptData.questOrder[j];
                        ScriptData.questOrder[j] = temp;
                    }
                    switch (ScriptData.SECURE_RANDOM.nextInt(2)) { // Onion
                        case 0:
                            ScriptData.currentArea = new Area(3186, 3269, 3192, 3265); // Farmer fred
                            break;
                        case 1:
                            ScriptData.currentArea = new Area(2945, 3254, 2955, 3248); // East of Melzar's Maze
                            break;
                    }
                    ScriptData.TASK_LOAD_OUTS[15] = new LoadOut(1, 0, null);
                    int coinsReq = ScriptData.SECURE_RANDOM.nextInt(100 - 45 + 1) + 45;
                    ScriptData.TASK_LOAD_OUTS[15].addInventoryItem(995, coinsReq, coinsReq, coinsReq);
                    ScriptData.PIPELINES[13] = new QuestPipeline(ScriptData.inCutsceneLI, ScriptData.checkLoadOutLI, ScriptData.continueDialogueLI, ScriptData.dialogueOptionsLI, ScriptData.finishQuestLI, new GoblinDiplomacyLI());
                }
                break;
            case 29: // Chopping Logs
                if (ScriptData.currentEntityName == null) {
                    ScriptData.currentEntityName = "Tree";
                }
                ScriptData.currentArea = currentEntityNameCurrentAreaFactory();
                PlayerData.initializeBestAxeAvail(Skills.getRealLevel(Skill.WOODCUTTING), Skills.getRealLevel(Skill.ATTACK));
                if (PlayerData.canEquipBestAxeAvail) {
                    ScriptData.TASK_LOAD_OUTS[29].setEquipmentItem(0, PlayerData.bestAxeAvail, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[29].setInventoryItem(0, 0, 0, 0, 0);
                }
                else {
                    ScriptData.TASK_LOAD_OUTS[29].setEquipmentItem(0, 0, 0, 0, 0);
                    ScriptData.TASK_LOAD_OUTS[29].setInventoryItem(0, PlayerData.bestAxeAvail, 1, 1, 1);
                }
                break;
            case 31: // Smelting Bars
                {
                    switch (ScriptData.SECURE_RANDOM.nextInt(Skills.getTotalLevel() >= 200 ? 4 : 3)) {
                        case 0:
                            ScriptData.currentArea = new Area(3272, 3188, 3278, 3184); // Al Kharid
                            break;
                        case 1:
                            ScriptData.currentArea = new Area(3222, 3257, 3228, 3252); // Lumbridge
                            break;
                        case 2:
                            ScriptData.currentArea = new Area(2971, 3370, 2977, 3368); // Falador
                            break;
                        case 3:
                            ScriptData.currentArea = new Area(3105, 3501, 3110, 3496); // Edgeville
                            break;
                    }
                    ScriptData.currentArea = new Area(3222, 3150, 3232, 3142); // SE Lumbridge Mine
                    ScriptData.currentArea2 = new Area(3219, 3257, 3228, 3247); // Lumbridge Furnace

                }
                break;
            case 30: // Mining Ore
                PlayerData.initializeBestPickaxeAvail(Skills.getRealLevel(Skill.MINING), Skills.getRealLevel(Skill.ATTACK));
                if (PlayerData.canEquipBestPickaxeAvail) {
                    ScriptData.TASK_LOAD_OUTS[30].setEquipmentItem(0, PlayerData.bestPickaxeAvail, 1, 1, 1);
                    ScriptData.TASK_LOAD_OUTS[30].setInventoryItem(0, PlayerData.bestPickaxeAvail, 0, 0, 0);
                }
                else {
                    ScriptData.TASK_LOAD_OUTS[30].setEquipmentItem(0, PlayerData.bestPickaxeAvail, 0, 0, 0);
                    ScriptData.TASK_LOAD_OUTS[30].setInventoryItem(0, PlayerData.bestPickaxeAvail, 1, 1, 1);
                }
                if (ScriptData.currentEntityName == null) {
                    if (Skills.getRealLevel(Skill.MINING) >= 15) {
                        ScriptData.currentEntityName = "Iron rocks";
                    }
                    else {
                        ScriptData.currentEntityName = "Tin rocks";
                    }
                }
                ScriptData.currentArea = currentEntityNameCurrentAreaFactory();
                ScriptData.currentTile = null;
                break;
            case 32: // Spinning balls of wool
                ScriptData.currentArea = new Area(3193, 3276, 3212, 3257); // Sheep area
                ScriptData.currentArea2 = new Area(3208, 3217, 3213, 3212, 1); // Loom area
                ScriptData.currentArea3 = new Area(3188, 3275, 3192, 3270); // Shear area
                ScriptData.questOrder = new byte[2];
                ScriptData.questOrder[0] = (byte) (ScriptData.SECURE_RANDOM.nextInt(7 - 3 + 1) + 3); // Player count threshold in sheepArea to hop
                ScriptData.questOrder[1] = (byte) (ScriptData.SECURE_RANDOM.nextInt(7 - 3 + 1) + 3); // Failed attempts to find valid sheep to hop
                break;
            default:
                break;
        }
    }

    private Area currentEntityNameCurrentAreaFactory() {
        switch (ScriptData.currentEntityName) {
            case "Chicken":
                switch (ScriptData.SECURE_RANDOM.nextInt(5)) {
                    case 0:
                        return new Area(3225, 3301, 3236, 3287); // Lumbridge East Farm
                    case 1:
                        return new Area(3184, 3279, 3192, 3276); // Fred the Farmer
                    case 2:
                        return new Area(3185, 3288, 3169, 3307); // Lumbridge Giant Chicken Coop
                    case 3:
                        return new Area(3021, 3292, 3040, 3280); // South Falador Farm Entrance
                    case 4:
                        return new Area(3014, 3298, 3020, 3282); // South Falador Chicken Coop
                }
                break;
            case "Cow":
                switch (ScriptData.SECURE_RANDOM.nextInt(4)) {
                    case 0:
                        return new Area(3265, 3255, 3240, 3298); // East Lumbridge Cow Pen
                    case 1:
                        return new Area(3193, 3302, 3212, 3282); // West Lumbridge/North of Fred the Farmer Cow Pen
                    case 2:
                        return new Area(3209, 3309, 3153, 3346); // North-Most West Lumbridge Cow Pen
                    case 3:
                        return new Area(2938, 3267, 2916, 3292); // Crafting Guild Cow Pen
                }
                break;
            case "Giant rat":
                switch (ScriptData.SECURE_RANDOM.nextInt(4)) {
                    case 0:
                        return new Area(3236, 3153, 3153, 3198); // Lumbridge Swamp
                    case 1:
                        return new Area(3199, 3200, 3188, 3214); // West of Lumbridge Castle
                    case 2:
                        return new Area(2988, 3200, 3004, 3184); // North of Musa Point Chapel
                    case 3:
                        return new Area(3215, 9875, 3256, 9858); // Varrock Sewers Entrance
                }
                break;
            case "Goblin":
                switch (ScriptData.SECURE_RANDOM.nextInt(8)) {
                    case 0:
                        return new Area(3237, 3272, 3266, 3218); // Lumbridge East of River
                    case 1:
                        return new Area(3138, 3306, 3151, 3298); // Lumbridge - West of Windmill
                    case 2:
                        return new Area(2988, 3221, 3007, 3191); // East of Rimmington, West of Port Sarim, North of Chapel
                    case 3:
                        return new Area(3206, 3238, 3175, 3255); // West of Lumbridge General Store
                    case 4:
                        return new Area(3135, 3265, 3154, 3252); // NE of Draynor Jail
                    case 5:
                        return new Area(3134, 3237, 3158, 3219); // NE of Wizards' Tower, SE of Draynor Jail
                    case 6:
                        return new Area(2948, 3515, 2965, 3484); // Goblin Village
                    case 7:
                        return new Area(3109, 3456, 3135, 3418); // West of Cook's Guild
                }
                break;
            case "Man":
                return new Area(3091, 3513, 3100, 3507);
            case "Tree":
                byte upperBound;
                int combatLevel = Combat.getCombatLevel();
                if (combatLevel >= 13) {
                    upperBound = 13;
                }
                else if (combatLevel >= 11) {
                    upperBound = 12;
                }
                else {
                    upperBound = 9;
                }
                switch (ScriptData.SECURE_RANDOM.nextInt(upperBound)) {
                    case 0:
                        return new Area(3144, 3466, 3182, 3448); // South of GrandExchange
                    case 1:
                        return new Area(3106, 3458, 3148, 3418); // West Cook's Guild
                    case 2:
                        return new Area(2946, 3440, 2972, 3395); // NE of Falador (Along Taverly Wall)
                    case 3:
                        return new Area(3266, 3213, 3238, 3259); // East Lumbridge
                    case 4:
                        return new Area(3037, 3474, 3067, 3417); // South of Monastery/West of Barbarian Village
                    case 5:
                        return new Area(3264, 3485, 3295, 3430); // East of Varrock, West of lumberyard/Pub
                    case 6:
                        return new Area(3144, 3205, 3101, 3226); // South of Draynor Jail
                    case 7:
                        return new Area(3008, 3328, 3067, 3309); // South of Falador, North of South Falador Farm
                    case 8:
                        return new Area(2972, 3235, 3013, 3191); // East of Rimmington, West of Port Sarim
                    case 9:
                        return new Area(3009, 3280, 3065, 3257); // South of Falador Farm (11+ combat required)
                    case 10:
                        return new Area(2960, 3317, 3012, 3278); // South of Falador (11+ combat required)
                    case 11:
                        return new Area(3074, 3328, 3131, 3283); // North of Draynor Village, South of Draynor Manor (11+ combat required)
                    case 12:
                        return new Area(3155, 3236, 3200, 3207); // West of Lumbridge Castle (13+ combat required)
                }
                break;
            case "Oak tree":
                switch (ScriptData.SECURE_RANDOM.nextInt(Combat.getCombatLevel() >= 11 ? 8 : 6)) {
                    case 0:
                        return new Area(3285, 3412, 3274, 3438); // Outside East Varrock Entrance
                    case 1:
                        return new Area(3188, 3463, 3196, 3455); // SE of Varrock Castle
                    case 2:
                        return new Area(2979, 3218, 3012, 3200); // West of Port Sarim
                    case 3:
                        return new Area(2996, 3368, 3005, 3361); // NW of East Falador Bank
                    case 4:
                        return new Area(3098, 3245, 3104, 3240); // East of Draynor Bank
                    case 5:
                        return new Area(3215, 3208, 3220, 3203); // Lumbridge (Within Castle Wall Bounds)
                    case 6:
                        return new Area(3159, 3422, 3172, 3408); // South of Falador Farm (11+ combat)
                    case 7:
                        return new Area(3075, 3304, 3110, 3280); // North of Draynor (11+ combat)
                }
                break;
            case "Willow tree":
                switch (ScriptData.SECURE_RANDOM.nextInt(Combat.getCombatLevel() >= 15 ? 9 : 8)) {
                    case 0:
                        return new Area(2910, 3305, 2924, 3293); // North of Crafting Guild
                    case 1:
                        return new Area(2958, 3201, 2977, 3189); // South of Rimmington
                    case 2:
                        return new Area(2984, 3192, 2993, 3181); // West of Port Sarim Jail
                    case 3:
                        return new Area(3031, 3178, 2994, 3156); // North of Musa Point
                    case 4:
                        return new Area(3056, 3256, 3064, 3249); // SE of Port Sarim Pub
                    case 5:
                        return new Area(3160, 3275, 3180, 3262); // West of Fred the Farmer
                    case 6:
                        return new Area(3232, 3245, 3236, 3234); // East of Sheared Ram Pub
                    case 7:
                        return new Area(3218, 3309, 3224, 3298); // NW of Lumbridge East Farm
                    case 8:
                        return new Area(3080, 3239, 3092, 3224); // Draynor (15+ combat req)
                }
                break;
            case "Hill Giant":
                if (ScriptData.currentPipelineI == 3) { // Melee
                    switch (ScriptData.SECURE_RANDOM.nextInt(2)) {
                        case 0:
                            ScriptData.TASK_LOAD_OUTS[3].setInventoryItem(0, 1, 1, 1); // Brass key needed
                            return new Area(3091, 9855, 3125, 9823); // Edgeville Dungeon
                        case 1:
                            return new Area(3368, 3156, 3389, 3142); // Giant's Plateau
                    }
                }
                else { // Ranged
                    switch (ScriptData.SECURE_RANDOM.nextInt(3)) {
                        case 0:
                            return new Area(3123, 9849, 3124, 9848); // NE safe spot
                        case 1:
                            return new Area(3097, 9838, 3098, 9837); // SW safe spot
                        case 2:
                            return new Area(3101, 9825, 3103, 9825); // South-most safe spot
                    }
                }
                break;
            case "Wizard":
                switch (ScriptData.SECURE_RANDOM.nextInt(2)) {
                    case 0:
                        return new Area(3102, 3173, 3117, 3154); // Wizard's Tower 1st floor
                    case 1:
                        return new Area(3083, 3246, 3098, 3226); // Draynor Bank area
                }
                break;
            case "Barbarian":
                return new Area(3075, 3445, 3082, 3436); // Barbarian Village LongHall
            case "Minotaur":
                switch (ScriptData.SECURE_RANDOM.nextInt(2)) {
                    case 0:
                        return new Area(1857, 5194, 1875, 5185); // SW Stronghold of Security Vault of War
                    case 1:
                        return new Area(1870, 5222, 1882, 5208); //Middle-West Stronghold of Security Vault of War
                }
                break;
            case "Giant frog":
                return new Area(3183, 3197, 3205, 3167); // Lumbridge Swamp
            case "Hobgoblin":
                switch (ScriptData.SECURE_RANDOM.nextInt(2)) {
                    case 0:
                        return new Area(2902, 3299, 2917, 3267); // Hobgoblin Peninsula
                    case 1:
                        return new Area(3008, 9585, 3023, 9571); // Asgarnian Ice Dungeon South Path
                }
                break;
            case "Al Kharid warrior":
                return new Area(3282, 3177, 3303, 3167); // Al Kharid Palace
            case "Iron rocks":
                switch (ScriptData.SECURE_RANDOM.nextInt(2)) {
                    case 0:
                        return new Area(2967, 3251, 2988, 3230); // Rimmington
                    case 1:
                        return new Area(3405, 3167, 3397, 3172); // Citharde Abbey
                }
                break;
            case "Tin rocks":
                switch (ScriptData.SECURE_RANDOM.nextInt(Combat.getCombatLevel() >= 13 ? 5 : 3)) {
                    case 0:
                        return new Area(3222, 3149, 3233, 3142); // Lumbridge SE
                    case 1:
                        return new Area(2967, 3251, 2988, 3230); // Rimmington
                    case 2:
                        return new Area(3077, 3423, 3084, 3416); // Barbarian Village
                    case 3:
                        return new Area(3277, 3371, 3293, 3357); // SE Varrock (13+ combat)
                    case 4:
                        return new Area(3170, 3381, 3184, 3362); // SW Varrock (13+ combat)
                }
                break;
            case "Coal":
                switch (ScriptData.SECURE_RANDOM.nextInt(2)) {
                    case 0:
                        return new Area(3078, 3423, 3085, 3416); // Barbarian village
                    case 1:
                        return new Area(3399, 3173, 3406, 3165); // Citharde Abbey
                }
                break;
        }
        return null;
    }

}
