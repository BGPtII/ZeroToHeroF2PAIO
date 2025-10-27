package framework;

import global.PlayerData;
import nodes.*;
import org.dreambot.api.methods.Randoms;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.event.impl.ExperienceEvent;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ExperienceListener;
import org.dreambot.api.utilities.AccountManager;
import services.BankingService;
import services.GrandExchangeService;
import services.LoadOutService;

import java.awt.*;
import java.security.SecureRandom;

/**
 * Different thread for handling:
 * - Path Randomization + Timer, Client SetUp Timer, Check Trade Restrictions Timer, Progression Task Timer, Money Making Task Timer
 * - Will assign the corresponding ScriptState if needed
 */
@ScriptManifest(category = Category.UTILITY, name = "ZeroToHeroF2PAio", description = "Builds an account post-Tutorial Island.", author = "Hikkens", version = 1.0)
public class SCScript extends AbstractScript implements ChatListener, ExperienceListener {

    public static ScriptState scriptState = ScriptState.DETERMINE_PROGRESSION_TASK;
    public static final SecureRandom SECURE_RANDOM = new SecureRandom((AccountManager.getAccountUsername() + AccountManager.getAccountBankPin() + AccountManager.getAccountTOTPKey()).getBytes());
    public static final BankingService BANKING_SERVICE = new BankingService();
    public static final GrandExchangeService GRAND_EXCHANGE_SERVICE = new GrandExchangeService();
    public static final Node[] NODES = new Node[ScriptState.values().length]; // Jump Table
    public static final int[] TASK_WEIGHTS = new int[ScriptState.values().length];
    public static final LoadOutService[] TASK_LOAD_OUTS = new LoadOutService[ScriptState.values().length]; // Persist Re-Occurring (Training/MoneyMaking) - Rest Null
    public static boolean useLevelUpEvent = false;

    @Override
    public void onPaint(Graphics2D graphics) {
        graphics.drawString(scriptState.toString(), 10, 10);
    }

    @Override
    public void onLevelUp(ExperienceEvent event) {
        if (!useLevelUpEvent) {
            return;
        }
        switch (event.getSkill()) {
            case WOODCUTTING:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 41 || i == 31 || i == 21 || i == 11) { // Could potentially miss if level-ups skip any of these specific levels, loop through as to not miss; grab the highest first
                        PlayerData.initializeAxe();
                        break;
                    }
                }
                break;
            case MINING:
                for (int i = event.getSkill().getLevel() - event.getChange(); i <= event.getSkill().getLevel(); i++) {
                    if (i == 41 || i == 31 || i == 21 || i == 11) {
                        PlayerData.initializePickaxe();
                        break;
                    }
                }
                break;
            case ATTACK:
                break;
            case STRENGTH:
                break;
            case DEFENCE:
                break;
            case RANGED:
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
        }
    }

    @Override
    public void onStart() {
        Randoms.setSeed(AccountManager.getAccountUsername() + AccountManager.getAccountBankPin() + AccountManager.getAccountTOTPKey());
        NODES[ScriptState.DETERMINE_PROGRESSION_TASK.ordinal()] = new DetermineProgressionTask();
        NODES[ScriptState.BUY_ITEMS.ordinal()] = new BuyItems();
        NODES[ScriptState.SELL_ITEMS.ordinal()] = new SellItems();
        NODES[ScriptState.BANKING.ordinal()] = new Banking();
        NODES[ScriptState.ENSURE_LOAD_OUT.ordinal()] = new EnsureLoadOut();

        NODES[ScriptState.WOODCUTTING_TRAINING.ordinal()] = new WoodcuttingTraining();
        NODES[ScriptState.MINING_TRAINING.ordinal()] = new MiningTraining();
        NODES[ScriptState.FISHING_TRAINING.ordinal()] = new FishingTraining();
        NODES[ScriptState.MELEE_TRAINING.ordinal()] = new MeleeTraining();
        NODES[ScriptState.RANGED_TRAINING.ordinal()] = new RangedTraining();

        TASK_WEIGHTS[ScriptState.WOODCUTTING_TRAINING.ordinal()] = SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        TASK_WEIGHTS[ScriptState.MINING_TRAINING.ordinal()] = SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        TASK_WEIGHTS[ScriptState.FISHING_TRAINING.ordinal()] = SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        TASK_WEIGHTS[ScriptState.MELEE_TRAINING.ordinal()] = SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        TASK_WEIGHTS[ScriptState.RANGED_TRAINING.ordinal()] = SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;

        TASK_WEIGHTS[ScriptState.THE_RESTLESS_GHOST.ordinal()] = SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;
        TASK_WEIGHTS[ScriptState.COOKS_ASSISTANT.ordinal()] = SECURE_RANDOM.nextInt(501 - 250 + 1) + 250;

        initializeTierFood();
        PlayerData.determineFood(Skills.getRealLevel(Skill.HITPOINTS));
        PlayerData.determineEatFoodHPTrs();

        PlayerData.initializeCape();
        PlayerData.initializeAxe();
        PlayerData.initializePickaxe();
        PlayerData.initializeMeleeWeapon();
        PlayerData.initializeMeleeArmour();

    }

    @Override
    public int onLoop() {
        return NODES[scriptState.ordinal()].loop();
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

}
