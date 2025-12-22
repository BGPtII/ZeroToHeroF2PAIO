package framework;

import data.global.PlayerData;
import data.global.ScriptData;
import nodes.*;
import org.dreambot.api.methods.Randoms;
import org.dreambot.api.methods.combat.CombatStyle;
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
import org.dreambot.api.wrappers.widgets.message.Message;
import data.BankingData;
import data.GrandExchangeData;
import data.LoadOutData;

import java.awt.Graphics2D;
import java.security.SecureRandom;

/**
 * Different thread for handling:
 * - Path Randomization + Timer, Client SetUp Timer, Check Trade Restrictions Timer, Progression Task Timer, Money Making Task Timer
 * - Will assign the corresponding ScriptState if needed
 */
@ScriptManifest(category = Category.UTILITY, name = "ZeroToHeroF2PAio", description = "Builds an account post-Tutorial Island.", author = "Hikkens", version = 1.0)
public class SCScript extends AbstractScript implements ChatListener, ExperienceListener {

    public static ScriptState scriptState = ScriptState.INITIALIZE_SCRIPT;
    public static final SecureRandom SECURE_RANDOM = new SecureRandom((AccountManager.getAccountUsername() + AccountManager.getAccountBankPin() + AccountManager.getAccountTOTPKey()).getBytes());
    public static final BankingData BANKING_DATA = new BankingData();
    public static final GrandExchangeData GE_DATA = new GrandExchangeData();
    public static final Node[] NODES = new Node[ScriptState.values().length]; // Jump Table
    public static final int[] TASK_WEIGHTS = new int[ScriptState.values().length];
    public static final LoadOutData[] TASK_LOAD_OUTS = new LoadOutData[ScriptState.values().length]; // Persist Re-Occurring (Training/MoneyMaking) - Rest Null
    public static boolean useLevelUpEvent = true;
    public static boolean useOnGameMessageEvent = false;

    @Override
    public void onPaint(Graphics2D graphics) {
        graphics.drawString("scriptState:" + scriptState, 10, 10);
        graphics.drawString("returnTo:" + ScriptData.returnTo, 10, 20);
        graphics.drawString("currentTask:" + ScriptData.currentTask, 10, 30);
        graphics.drawString("currentProgressionTask:" + ScriptData.currentProgressionTask, 10, 40);
        graphics.drawString("currentMoneyMakingTask:" + ScriptData.currentMoneyMakingTask, 10, 50);
        graphics.drawString("progressionTaskTimer:" + (ScriptData.progressionTaskTimer == null ? "null" : ScriptData.progressionTaskTimer.formatRemainingTime()), 10, 60);
        graphics.drawString("moneyMakingTaskTimer:" + (ScriptData.moneyMakingTaskTimer == null ? "null" : ScriptData.moneyMakingTaskTimer.formatRemainingTime()), 10, 70);
        graphics.drawString("initializeTaskI:" + InitializeTask.initializeTaskI, 10, 80);
    }

    @Override
    public void onGameMessage(Message message) {
        if (!useOnGameMessageEvent) {
            return;
        }
        final String contents = message.getMessage();
        if (SCScript.scriptState == ScriptState.COOKS_ASSISTANT) {
            if (contents.startsWith("You put the grain") || contents.startsWith("There is already grain")) {
                ScriptData.questOrderI++;
                Logger.log("Grain is in the hopper");
            }
        }
        else if (SCScript.scriptState == ScriptState.ERNEST_THE_CHICKEN) {
            if (contents.startsWith("... then die and")) {
                ScriptData.questOrderI++;
                Logger.log("Poisoned the piranhas");
            }
        }
    }

    @Override
    public void onLevelUp(ExperienceEvent event) {
        if (!useLevelUpEvent) {
            return;
        }
        Logger.log("Level up info: eventSkill: " + event.getSkill() + ", eventChange: " + event.getChange() + ", eventSkillLevel: " + event.getSkill().getLevel());
        switch (event.getSkill()) {
            case WOODCUTTING:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 41 || i == 31 || i == 21 || i == 11) { // Could potentially miss if level-ups skip any of these specific levels, loop through as to not miss; grab the highest first
                        PlayerData.initializeAxe(i, Skills.getRealLevel(Skill.ATTACK));
                        if (PlayerData.canEquipAxe) {
                            SCScript.TASK_LOAD_OUTS[ScriptState.WOODCUTTING_TRAINING.ordinal()].setEquipmentItem(0, PlayerData.axe, 1, 1);
                            SCScript.TASK_LOAD_OUTS[ScriptState.WOODCUTTING_TRAINING.ordinal()].setInventoryItem(0, 0, 0, 0);
                        }
                        else {
                            SCScript.TASK_LOAD_OUTS[ScriptState.WOODCUTTING_TRAINING.ordinal()].setInventoryItem(0, PlayerData.axe, 1, 1, 1);
                        }
                        Logger.log("Determined new axe: " + PlayerData.axe);
                        if (SCScript.scriptState == ScriptState.WOODCUTTING_TRAINING) {
                            SCScript.scriptState = ScriptState.INITIALIZE_TASK;
                            InitializeTask.initializeTaskI = 0;
                        }
                        break;
                    }
                }
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (ScriptData.currentTask == ScriptState.WOODCUTTING_TRAINING && (i == 30 || i == 15)) { // Switch treeType
                        Logger.log("Needs to switch treeType, reInitialize Task (taskType 3)");
                        SCScript.scriptState = ScriptState.DETERMINE_TASK;
                        DetermineTask.taskType = 3;
                        break;
                    }
                }
                break;
            case MINING:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 41 || i == 31 || i == 21 || i == 11) {
                        PlayerData.initializePickaxe(i, Skills.getRealLevel(Skill.ATTACK));
                        Logger.log("Determined new pickaxe: " + PlayerData.pickaxe);
                        if (PlayerData.canEquipPickaxe) {
                            SCScript.TASK_LOAD_OUTS[ScriptState.MINING_TRAINING.ordinal()].setEquipmentItem(0, PlayerData.pickaxe, 1, 1);
                            SCScript.TASK_LOAD_OUTS[ScriptState.MINING_TRAINING.ordinal()].setInventoryItem(0, 0, 0, 0);
                        }
                        else {
                            SCScript.TASK_LOAD_OUTS[ScriptState.MINING_TRAINING.ordinal()].setInventoryItem(0, PlayerData.pickaxe, 1, 1, 1);
                            SCScript.TASK_LOAD_OUTS[ScriptState.MINING_TRAINING.ordinal()].setEquipmentItem(0, 0, 0);
                        }
                        if (SCScript.scriptState == ScriptState.MINING_TRAINING) {
                            SCScript.scriptState = ScriptState.INITIALIZE_TASK;
                            InitializeTask.initializeTaskI = 0;
                            ScriptData.progressionTaskTimer.pause();
                        }
                        break;
                    }
                }
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (ScriptData.currentTask == ScriptState.MINING_TRAINING && i == 15) { // Switch to Iron ore
                        Logger.log("Needs to switch to IronOre, reInitializeTask");
                        SCScript.scriptState = ScriptState.DETERMINE_TASK;
                        DetermineTask.taskType = 3;
                        ScriptData.progressionTaskTimer.pause();
                        break;
                    }
                }
                break;
            case FISHING:
                Logger.log("Fishing level up");
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (ScriptData.currentTask == ScriptState.FISHING_TRAINING && i == 20) { // Switch to Trout/Salmon
                        Logger.log("Needs to switch to salmon/trout, reInitializeTask");
                        SCScript.scriptState = ScriptState.DETERMINE_TASK;
                        InitializeTask.initializeTaskI = 0; // In case needs to buy fly-fishing rod/feather
                        ScriptData.progressionTaskTimer.pause();
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
                        ScriptData.currentLoadOutData.setEquipmentItem(4, PlayerData.meleeWeapon, 1, 1);
                        if (SCScript.scriptState == ScriptState.MELEE_TRAINING) {
                            SCScript.scriptState = ScriptState.INITIALIZE_TASK;
                            InitializeTask.initializeTaskI = 0;
                            ScriptData.progressionTaskTimer.pause();
                        }
                        break;
                    }
                }
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == PlayerData.switchMeleeCombatStyleLevel && PlayerData.meleeCombatStyle == CombatStyle.ATTACK) {
                        PlayerData.determineMeleeCombatStyle(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                        PlayerData.determineSwitchMeleeCombatStyleLevel(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                    }
                }
                break;
            case STRENGTH:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == PlayerData.switchMeleeCombatStyleLevel && PlayerData.meleeCombatStyle == CombatStyle.STRENGTH) {
                        PlayerData.determineMeleeCombatStyle(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                        PlayerData.determineSwitchMeleeCombatStyleLevel(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                    }
                }
                break;
            case DEFENCE:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 40 || i == 30 || i == 20 || i == 10) {
                        PlayerData.initializeMeleeArmour(Skills.getRealLevel(Skill.DEFENCE));
                        ScriptData.currentLoadOutData.setEquipmentItem(0, PlayerData.meleeHat, 1, 1);
                        ScriptData.currentLoadOutData.setEquipmentItem(1, PlayerData.meleeChest, 1, 1);
                        ScriptData.currentLoadOutData.setEquipmentItem(2, PlayerData.meleeLegs, 1, 1);
                        ScriptData.currentLoadOutData.setEquipmentItem(3, PlayerData.meleeShield, 1, 1);
                        if (SCScript.scriptState == ScriptState.MELEE_TRAINING) {
                            SCScript.scriptState = ScriptState.INITIALIZE_TASK;
                            InitializeTask.initializeTaskI = 0;
                            ScriptData.progressionTaskTimer.pause();
                        }
                        break;
                    }
                }
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == PlayerData.switchMeleeCombatStyleLevel && PlayerData.meleeCombatStyle == CombatStyle.DEFENCE) {
                        PlayerData.determineMeleeCombatStyle(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                        PlayerData.determineSwitchMeleeCombatStyleLevel(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
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
                        if (SCScript.scriptState == ScriptState.RANGED_TRAINING) {
                            SCScript.scriptState = ScriptState.INITIALIZE_TASK;
                            InitializeTask.initializeTaskI = 0;
                            ScriptData.progressionTaskTimer.pause();
                        }
                        break;
                    }
                }
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == PlayerData.switchRangedCombatStyleLevel) {
                        PlayerData.determineRangedCombatStyle();
                        PlayerData.determineSwitchRangedCombatStyleLevel(Skills.getRealLevel(Skill.RANGED));
                    }
                }
                break;
            case HITPOINTS:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 25) {
                        PlayerData.determineFood(i);
                        SCScript.TASK_LOAD_OUTS[ScriptState.MELEE_TRAINING.ordinal()].setInventoryItem(1, 1, SCScript.SECURE_RANDOM.nextInt(25 - 5 + 1) + 5, SCScript.SECURE_RANDOM.nextInt(1000 - 50 + 1) + 50); // Food
                        SCScript.TASK_LOAD_OUTS[ScriptState.RANGED_TRAINING.ordinal()].setInventoryItem(1, 1, SCScript.SECURE_RANDOM.nextInt(25 - 5 + 1) + 5, SCScript.SECURE_RANDOM.nextInt(1000 - 50 + 1) + 50); // Food
                        break;
                    }
                }
                PlayerData.determineEatFoodHPTrs();
                break;
            case RUNECRAFTING:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 9 || i == 14 || i == 20) {
                        PlayerData.initializeCurrentRunecraftMedium(i);
                        if (SCScript.scriptState == ScriptState.RUNECRAFT_TRAINING) {
                            SCScript.scriptState = ScriptState.INITIALIZE_TASK;
                            InitializeTask.initializeTaskI = 0;
                            ScriptData.resetEntities();
                        }

                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void onStart() {
        Randoms.setSeed(AccountManager.getAccountUsername() + AccountManager.getAccountBankPin() + AccountManager.getAccountTOTPKey());
        NODES[ScriptState.INITIALIZE_SCRIPT.ordinal()] = new InitializeScript();
    }

    @Override
    public int onLoop() {
        return NODES[scriptState.ordinal()].loop();
    }

}
