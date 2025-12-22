package nodes;

import framework.Node;
import framework.SCScript;
import framework.ScriptState;
import data.global.PlayerData;
import data.global.ScriptData;
import nodes.progressiontasks.questing.*;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.utilities.Logger;
import data.LoadOutData;

import java.util.Arrays;

/**
 * When finished MoneyMakingTask:
 * - taskType set to 3
 * - skip rolling here, re-determine LoadOut, etc. for currentProgressionTask
 */
public class DetermineTask implements Node {

    public static byte taskType; // 0 - determineProgressionTask, 1 - determineMoneyMakingTask, 2 - returnToProgressionTaskFromMoneyMaking, 3 - Re-initialize progressionTask

    private Area currentAreaFactory() {
        switch (ScriptData.currentEntityName) {
            case "Chicken":
                switch (SCScript.SECURE_RANDOM.nextInt(5)) {
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
                switch (SCScript.SECURE_RANDOM.nextInt(4)) {
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
                switch (SCScript.SECURE_RANDOM.nextInt(5)) {
                    case 0:
                        return new Area(3236, 3153, 3153, 3198); // Lumbridge Swamp
                    case 1:
                        return new Area(3199, 3200, 3188, 3214); // West of Lumbridge Castle
                    case 2:
                        return new Area(2988, 3200, 3004, 3184); // North of Musa Point Chapel
                    case 3:
                        return new Area(3215, 9875, 3256, 9858); // Varrock Sewers Entrance
                    case 4:
                        return new Area(3088, 9899, 3093, 9893); // Edgeville Dungeon Entrance Cage
                }
                break;
            case "Goblin":
                switch (SCScript.SECURE_RANDOM.nextInt(8)) {
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
                switch (SCScript.SECURE_RANDOM.nextInt(upperBound)) {
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
                switch (SCScript.SECURE_RANDOM.nextInt(Combat.getCombatLevel() >= 11 ? 8 : 6)) {
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
                switch (SCScript.SECURE_RANDOM.nextInt(Combat.getCombatLevel() >= 15 ? 9 : 8)) {
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
                if (ScriptData.currentProgressionTask == ScriptState.MELEE_TRAINING) {
                    switch (SCScript.SECURE_RANDOM.nextInt(2)) {
                        case 0:
                            ScriptData.currentLoadOutData.setInventoryItem(0, 1, 1, 1); // Brass key needed
                            return new Area(3091, 9855, 3125, 9823); // Edgeville Dungeon
                        case 1:
                            return new Area(3368, 3156, 3389, 3142); // Giant's Plateau
                    }
                }
                else { // Ranged
                    switch (SCScript.SECURE_RANDOM.nextInt(3)) {
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
                switch (SCScript.SECURE_RANDOM.nextInt(2)) {
                    case 0:
                        return new Area(3102, 3173, 3117, 3154); // Wizard's Tower 1st floor
                    case 1:
                        return new Area(3083, 3246, 3098, 3226); // Draynor Bank area
                }
                break;
            case "Barbarian":
                return new Area(3075, 3445, 3082, 3436); // Barbarian Village LongHall
            case "Minotaur":
                switch (SCScript.SECURE_RANDOM.nextInt(2)) {
                    case 0:
                        return new Area(1857, 5194, 1875, 5185); // SW Stronghold of Security Vault of War
                    case 1:
                        return new Area(1870, 5222, 1882, 5208); //Middle-West Stronghold of Security Vault of War
                }
                break;
            case "Giant frog":
                return new Area(3183, 3197, 3205, 3167); // Lumbridge Swamp
            case "Hobgoblin":
                switch (SCScript.SECURE_RANDOM.nextInt(2)) {
                    case 0:
                        return new Area(2902, 3299, 2917, 3267); // Hobgoblin Peninsula
                    case 1:
                        return new Area(3008, 9585, 3023, 9571); // Asgarnian Ice Dungeon South Path
                }
                break;
            case "Al Kharid warrior":
                return new Area(3282, 3177, 3303, 3167); // Al Kharid Palace
            case "Iron rocks":
                switch (SCScript.SECURE_RANDOM.nextInt(2)) {
                    case 0:
                        return new Area(2967, 3251, 2988, 3230); // Rimmington
                    case 1:
                        return new Area(3405, 3167, 3397, 3172); // Citharde Abbey
                }
                break;
            case "Tin rocks":
                switch (SCScript.SECURE_RANDOM.nextInt(Combat.getCombatLevel() >= 13 ? 5 : 3)) {
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
        }
        Logger.log("Returning currentArea as null");
        return null;
    }

    private void setUpTask() { // loadOut setUp, determineTaskVariables
        ScriptData.currentLoadOutData = SCScript.TASK_LOAD_OUTS[ScriptData.currentTask.ordinal()]; // Could potentially be null
        switch (ScriptData.currentTask) {
            case WOODCUTTING_TRAINING:
                Logger.log("canEquipAxe: " + PlayerData.canEquipAxe);
                Logger.log("axe: " + PlayerData.axe);
                if (PlayerData.canEquipAxe) {
                    ScriptData.currentLoadOutData.setEquipmentItem(0, PlayerData.axe, 1, 1);
                    ScriptData.currentLoadOutData.setInventoryItem(0, PlayerData.axe, 0, 0, 0);
                }
                else {
                    ScriptData.currentLoadOutData.setEquipmentItem(0, PlayerData.axe, 0, 0);
                    ScriptData.currentLoadOutData.setInventoryItem(0, PlayerData.axe, 1, 1, 1);
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
                ScriptData.currentArea = currentAreaFactory();
                Logger.log("Determined loadOut for woodcuttingTraining");
                break;
            case MINING_TRAINING:
                if (PlayerData.canEquipPickaxe) {
                    ScriptData.currentLoadOutData.setEquipmentItem(0, PlayerData.pickaxe, 1, 1);
                    ScriptData.currentLoadOutData.setInventoryItem(0, PlayerData.pickaxe, 0, 0, 0);
                    Logger.log("canEquipPickaxe");
                }
                else {
                    ScriptData.currentLoadOutData.setEquipmentItem(0, PlayerData.pickaxe, 0, 0);
                    ScriptData.currentLoadOutData.setInventoryItem(0, PlayerData.pickaxe, 1, 1, 1);
                }
                if (Skills.getRealLevel(Skill.MINING) >= 15) {
                    ScriptData.currentEntityName = "Iron rocks";
                }
                else {
                    ScriptData.currentEntityName = "Tin rocks";
                }
                ScriptData.currentArea = currentAreaFactory();
                Logger.log("Determined loadOut for miningTraining");
                Logger.log("currentEntityName: " + ScriptData.currentEntityName);
                break;
            case FISHING_TRAINING:
                if (Skills.getRealLevel(Skill.FISHING) >= 20) {
                    ScriptData.currentLoadOutData.setInventoryItem(0, 309, 1, 1, 1);
                    int featherCount = SCScript.SECURE_RANDOM.nextInt(5000 - 500 + 1) + 500;
                    Logger.log("featherCount: " + featherCount);
                    ScriptData.currentLoadOutData.setInventoryItem(1, 314, 1, featherCount, featherCount);
                    ScriptData.currentEntityName = "Rod Fishing spot";
                    ScriptData.currentEntityAction = "Lure";
                    ScriptData.currentLoadOutData.setShouldBank(() -> Inventory.isFull() || !Inventory.contains(314));
                    switch (SCScript.SECURE_RANDOM.nextInt(2)) {
                        case 0:
                            ScriptData.currentArea = new Area(3243, 3238, 3237, 3255); // Lumbridge along river
                            break;
                        case 1:
                            ScriptData.currentArea = new Area(3100, 3436, 3111, 3422); // Lure/Bait
                            break;
                    }
                }
                else {
                    ScriptData.currentLoadOutData.setInventoryItem(0, 303, 1, 1, 1);
                    ScriptData.currentEntityName = "Fishing spot";
                    ScriptData.currentEntityAction = "Small Net";
                    switch (SCScript.SECURE_RANDOM.nextInt((Combat.getCombatLevel() >= 15) ? 3 : 2)) {
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
                    ScriptData.currentLoadOutData.setShouldBank(Inventory::isFull);
                }
                break;
            case MELEE_TRAINING:
                ScriptData.currentLoadOutData.setInventoryItem(1, 1, SCScript.SECURE_RANDOM.nextInt(25 - 5 + 1) + 5, SCScript.SECURE_RANDOM.nextInt(1000 - 50 + 1) + 50); // Food
                int attackLevel = Skills.getRealLevel(Skill.ATTACK);
                int strengthLevel = Skills.getRealLevel(Skill.STRENGTH);
                int defenceLevel = Skills.getRealLevel(Skill.DEFENCE);
                ScriptData.currentLoadOutData.setEquipmentItem(0, PlayerData.meleeHat, 1, 1); // Need to be reset (if changes occurred)
                ScriptData.currentLoadOutData.setEquipmentItem(1, PlayerData.meleeChest, 1, 1);
                ScriptData.currentLoadOutData.setEquipmentItem(2, PlayerData.meleeLegs, 1, 1);
                ScriptData.currentLoadOutData.setEquipmentItem(3, PlayerData.meleeShield, 1, 1);
                ScriptData.currentLoadOutData.setEquipmentItem(4, PlayerData.meleeWeapon, 1, 1);
                if (attackLevel >= 30 && strengthLevel >= 30 && defenceLevel >= 30) {
                    switch (SCScript.SECURE_RANDOM.nextInt(3)) {
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
                    switch (SCScript.SECURE_RANDOM.nextInt(4)) {
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
                    switch (SCScript.SECURE_RANDOM.nextInt(4)) {
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
                    switch (SCScript.SECURE_RANDOM.nextInt(4)) {
                        case 0:
                            ScriptData.currentEntityName = "Chicken";
                            ScriptData.currentLoadOutData.setInventoryItem(1, 0, 0, 0);
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
                Logger.log("currentEntityName: " + ScriptData.currentEntityName);
                PlayerData.determineMeleeCombatStyle(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                PlayerData.determineSwitchMeleeCombatStyleLevel(Skills.getRealLevel(Skill.ATTACK), Skills.getRealLevel(Skill.STRENGTH), Skills.getRealLevel(Skill.DEFENCE));
                ScriptData.currentArea = currentAreaFactory();
                break;
            case RANGED_TRAINING:
                ScriptData.currentLoadOutData.setInventoryItem(1, 1, SCScript.SECURE_RANDOM.nextInt(25 - 5 + 1) + 5, SCScript.SECURE_RANDOM.nextInt(1000 - 50 + 1) + 50);
                ScriptData.currentLoadOutData.setEquipmentItem(4, SCScript.SECURE_RANDOM.nextInt(25 - 5 + 1) + 5, SCScript.SECURE_RANDOM.nextInt(1000 - 50 + 1) + 50);
                int rangedLevel = Skills.getRealLevel(Skill.RANGED);
                if (rangedLevel >= 50) {
                    switch (SCScript.SECURE_RANDOM.nextInt(3)) {
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
                    switch (SCScript.SECURE_RANDOM.nextInt(2)) {
                        case 0:
                            ScriptData.currentEntityName = "Hill Giant";
                            break;
                        case 1:
                            ScriptData.currentEntityName = "Giant frog";
                            break;
                    }
                }
                else if (rangedLevel >= 20) {
                    switch (SCScript.SECURE_RANDOM.nextInt(3)) {
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
                    switch (SCScript.SECURE_RANDOM.nextInt(3)) {
                        case 0:
                            ScriptData.currentEntityName = "Chicken";
                            ScriptData.currentLoadOutData.setInventoryItem(1, 0, 0, 0);
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
                PlayerData.determineSwitchRangedCombatStyleLevel(Skills.getRealLevel(Skill.RANGED));
                ScriptData.currentArea = currentAreaFactory();
                break;
            case RUNECRAFT_TRAINING:
                if (Skills.getRealLevel(Skill.RUNECRAFTING) >= 20) { // currentArea - Ruins, currentArea2 - Altar
                    ScriptData.currentArea = new Area(3046, 3454, 3065, 3430);
                    ScriptData.currentArea2 = new Area(2504, 4855, 2538, 4824);
                }
                else if (Skills.getRealLevel(Skill.RUNECRAFTING) >= 14) {
                    ScriptData.currentArea = new Area(3305, 3263, 3320, 3248);
                    ScriptData.currentArea2 = new Area(2568, 4856, 2600, 4823);
                }
                else if (Skills.getRealLevel(Skill.RUNECRAFTING) >= 9) {
                    ScriptData.currentArea = new Area(3300, 3475, 3310, 3464);
                    ScriptData.currentArea2 = new Area(2636, 4857, 2676, 4817);
                }
                else {
                    ScriptData.currentArea = new Area(2978, 3299, 2989, 3287);
                    ScriptData.currentArea2 = new Area(2828, 4848, 2858, 4818);
                }
                ScriptData.currentLoadOutData.setInventoryItem(1, 7936, 1, 27, SCScript.SECURE_RANDOM.nextInt(3000 - 300 + 1) + 300); // Pure essence
                if (PlayerData.currentRunecraftMedium > 5000) {
                    ScriptData.currentLoadOutData.setEquipmentItem(0, PlayerData.currentRunecraftMedium, 1, 1);
                    ScriptData.currentLoadOutData.setInventoryItem(0, 0, 0, 0);
                }
                else {
                    ScriptData.currentLoadOutData.setInventoryItem(0, PlayerData.currentRunecraftMedium, 1, 1, 1);
                    ScriptData.currentLoadOutData.setEquipmentItem(0, PlayerData.currentRunecraftMedium, 0, 0);
                }
                break;
            case THE_RESTLESS_GHOST: // Questing, will need to instantiate but not persist - Node + LoadOut
                ScriptData.dialogueOpts = new String[] {
                    "I'm looking for a quest!",
                    "Yes.",
                    "Father Aereck sent me to talk to you.",
                    "He's got a ghost haunting his graveyard.",
                    "Yep, now tell me what the problem is."
                };
                SCScript.NODES[ScriptData.currentTask.ordinal()] = new TheRestlessGhost(); // Nullify after completion
                ScriptData.currentLoadOutData = new LoadOutData(0, 1, null);
                if (PlayerData.playerHasItemID(552)) { // GhostSpeak Amulet (uncharged)
                    ScriptData.currentLoadOutData.addEquipmentItem(552, 0, 1);
                }
                break;
            case COOKS_ASSISTANT:
                ScriptData.questOrder = new byte[] { // 0 - start quest, 1 - egg, 2 - bucket of milk, 3 - pot of flour (pre-grainInHopper), 4 - pot of flour (post-grainInHopper), 5 - finish quest, 6 get bucket, 7 get pot; 6 before 2, 7 before 3, 3 direct left of 4)
                    0, 1, 2, 3, 4, 6, 7, 5
                };
                ScriptData.questOrderI = 0;
                while (true) {
                    for (int i = 6; i >= 1; i--) {
                        int j = 1 + SCScript.SECURE_RANDOM.nextInt(i);
                        byte t = ScriptData.questOrder[i];
                        ScriptData.questOrder[i] = ScriptData.questOrder[j];
                        ScriptData.questOrder[j] = t;
                    }

                    int p2 = -1, p3 = -1, p4 = -1, p6 = -1, p7 = -1;
                    for (int i = 1; i <= 6; i++) {
                        switch (ScriptData.questOrder[i]) {
                            case 2: p2 = i; break;
                            case 3: p3 = i; break;
                            case 4: p4 = i; break;
                            case 6: p6 = i; break;
                            case 7: p7 = i; break;
                        }
                    }

                    if (p6 < p2 && p7 < p3 && p4 == p3 + 1) {
                        break;
                    }
                }
                Logger.log("questOrder: " + Arrays.toString(ScriptData.questOrder));

                ScriptData.dialogueOpts = new String[] {
                    "What's wrong?",
                    "Yes.",
                    "Actually, I know where to find this stuff.",
                    "I'll get right on it."
                };
                if (SCScript.SECURE_RANDOM.nextInt(2) == 1) { // Egg
                    ScriptData.currentArea = new Area(3168, 3308, 3186, 3288); // SW of Windmill
                }
                else {
                    ScriptData.currentArea = new Area(3225, 3301, 3235, 3295); // Farm East of River Lum
                }

                if (SCScript.SECURE_RANDOM.nextInt(2) == 1) { // Dairy Cow
                    ScriptData.currentArea2 = new Area(3170, 3321, 3177, 3316); // North of Windmill
                }
                else {
                    ScriptData.currentArea2 = new Area(3249, 3280, 3260, 3268); // East of River Lum (2 here)
                }

                SCScript.NODES[ScriptData.currentTask.ordinal()] = new CooksAssistant();
                ScriptData.currentLoadOutData = new LoadOutData(3, 0, null);
                if (PlayerData.playerHasItemID(1933)) { // Pot of flour
                    ScriptData.currentLoadOutData.addInventoryItem(1933, 0, 1, 0);
                }
                else {
                    ScriptData.currentLoadOutData.addInventoryItem(1931, 0, 1, 0); // Pot
                    ScriptData.currentLoadOutData.addInventoryItem(1947, 0, 1, 0); // Grain
                }
                ScriptData.currentLoadOutData.addInventoryItem(1944, 0, 1, 0); // Egg
                SCScript.useOnGameMessageEvent = true;
                break;
            case RUNE_MYSTERIES:
                ScriptData.dialogueOpts = new String[] {
                    "Have you any quests for me?",
                    "Yes.",
                    "Okay, here you are.",
                    "Actually, I'm not interested.",
                    "Yes, certainly.",
                    "I've been sent here with a package for you.",
                    "I'd better get going."
                };
                SCScript.NODES[ScriptData.currentTask.ordinal()] = new RuneMysteries();
                ScriptData.currentLoadOutData = new LoadOutData(3, 0, null);
                ScriptData.currentLoadOutData.addInventoryItem(291, 0, 1, 0); // Research notes
                ScriptData.currentLoadOutData.addInventoryItem(290, 0, 1, 0); // Research package
                ScriptData.currentLoadOutData.addInventoryItem(1438, 0, 1, 0); // Air talisman
                break;
            case X_MARKS_THE_SPOT:
                ScriptData.dialogueOpts = new String[] {
                    "I'm looking for a quest.",
                    "Yes.",
                    "Okay, thanks Veos.",
                    "I'm good thanks."
                };
                SCScript.NODES[ScriptData.currentTask.ordinal()] = new XMarksTheSpot();
                ScriptData.currentLoadOutData = new LoadOutData(5, 0, null);
                ScriptData.currentLoadOutData.addInventoryItem(952, 1, 1, 1); // Spade
                ScriptData.currentLoadOutData.addInventoryItem(23069, 0, 1, 0); // Mysterious orb
                ScriptData.currentLoadOutData.addInventoryItem(23067, 0, 1, 0); // Treasure scroll (Step 1)
                ScriptData.currentLoadOutData.addInventoryItem(23068, 0, 1, 0); // Treasure scroll (Step 2)
                ScriptData.currentLoadOutData.addInventoryItem(23070, 0, 1, 0); // Treasure scroll (Step 3)
                break;
            case PIRATES_TREASURE:
                {
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
                    SCScript.NODES[ScriptData.currentTask.ordinal()] = new PiratesTreasure();
                    ScriptData.currentLoadOutData = new LoadOutData(2, 1, null);
                    int coinsReq = (SCScript.SECURE_RANDOM.nextInt(7 - 3 + 1) + 3) * 30;
                    Logger.log("coinsReq: " + coinsReq);
                    ScriptData.currentLoadOutData.addInventoryItem(995, coinsReq, coinsReq, coinsReq); // Coins
                    ScriptData.currentLoadOutData.addInventoryItem(952, 0, 1, 0); // Spade
                    ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.meleeWeapon, 1, 1);
                }
                break;
            case WITCHS_POTION:
                {
                    ScriptData.questOrder = new byte[] { // 0 - start quest, 1 - burnt meat, 2 - Eye of newt, 3 - Onion, 4 - Rat's tail (Requires start quest), 5 - finish quest
                        0, 1, 2, 3, 4, 5
                    };
                    ScriptData.questOrderI = 0;
                    for (byte i = 4; i >= 1; i--) { // shuffle 0-4
                        int j = SCScript.SECURE_RANDOM.nextInt(i + 1);
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
                    Logger.log("questOrder: " + Arrays.toString(ScriptData.questOrder));
                    if (SCScript.SECURE_RANDOM.nextInt(2) == 0) {
                        ScriptData.currentEntityName = "Giant rat";
                    }
                    else {
                        ScriptData.currentEntityName = "Cow";
                    }
                    Logger.log("currentEntityName: " + ScriptData.currentEntityName);
                    ScriptData.currentArea = currentAreaFactory();
                    switch (SCScript.SECURE_RANDOM.nextInt(2)) { // Onion area
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
                    SCScript.NODES[ScriptData.currentTask.ordinal()] = new WitchsPotion();
                    ScriptData.currentLoadOutData = new LoadOutData(2, 1, null);
                    int coinsReq = SCScript.SECURE_RANDOM.nextInt(100 - 3 + 1) + 3;
                    ScriptData.currentLoadOutData.addInventoryItem(995, coinsReq, coinsReq, coinsReq); // Coins
                    ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.meleeWeapon, 1, 1);
                }
                break;
            case ERNEST_THE_CHICKEN:
                ScriptData.questOrder = new byte[] { // 0 - start quest, 1 - oil can, 2 - Pressure gauge (pre-poisoned fountain), 3 - Pressure gauge (post-poisoned fountain), 4 - Rubber tube, 5 - finish quest
                    0, 1, 2, 3, 4, 5
                };
                ScriptData.questOrderI = 0;
                for (byte i = 4; i >= 1; i--) { // shuffle 1-4
                    int j = SCScript.SECURE_RANDOM.nextInt(i) + 1;
                    byte t = ScriptData.questOrder[i];
                    ScriptData.questOrder[i] = ScriptData.questOrder[j];
                    ScriptData.questOrder[j] = t;
                }
                byte p4 = -1, p3 = -1;
                for (byte i = 1; i < 5; i++) {
                    if (ScriptData.questOrder[i] == 4) {
                        p4 = i;
                    }
                    else if (ScriptData.questOrder[i] == 3) {
                        p3 = i;
                    }
                }
                if (p3 > p4) { // Swap
                    byte temp = ScriptData.questOrder[p3];
                    ScriptData.questOrder[p3] = ScriptData.questOrder[p4];
                    ScriptData.questOrder[p4] = temp;
                }
                Logger.log("questOrder: " + Arrays.toString(ScriptData.questOrder));
                ScriptData.dialogueOpts = new String[] {
                    "Yes.",
                    "I'm looking for a guy called Ernest.",
                    "Change him back this instant!"
                };
                SCScript.NODES[ScriptData.currentTask.ordinal()] = new ErnestTheChicken();
                ScriptData.currentLoadOutData = new LoadOutData(5, 0, null);
                ScriptData.currentLoadOutData.addInventoryItem(952, 0, 1, 0); // Spade
                ScriptData.currentLoadOutData.addInventoryItem(275, 0, 1, 0); // Key
                ScriptData.currentLoadOutData.addInventoryItem(276, 0, 1, 0); // Rubber tube
                ScriptData.currentLoadOutData.addInventoryItem(277, 0, 1, 0); // Oil can
                ScriptData.currentLoadOutData.addInventoryItem(271, 0, 1, 0); // Pressure gauge
                break;
            case THE_KNIGHTS_SWORD:
                if (SCScript.SECURE_RANDOM.nextInt(2) == 1) { // Blurite rock tile
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
                SCScript.NODES[ScriptData.currentTask.ordinal()] = new TheKnightsSword();
                Logger.log("pickaxe: " + PlayerData.pickaxe);
                if (PlayerData.canEquipPickaxe) {
                    ScriptData.currentLoadOutData = new LoadOutData(2, 1, null);
                    ScriptData.currentLoadOutData.addEquipmentItem(PlayerData.pickaxe, 1, 1);
                }
                else {
                    ScriptData.currentLoadOutData = new LoadOutData(3, 0, null);
                    ScriptData.currentLoadOutData.addInventoryItem(PlayerData.pickaxe, 1, 1, 1);
                }
                ScriptData.currentLoadOutData.addInventoryItem(2351, 0, 0, 2); // Iron bar
                ScriptData.currentLoadOutData.addInventoryItem(2325, 1, 1, 1); // Redberry pie
                break;
            case MISTHALIN_MYSTERY:
                ScriptData.dialogueOpts = new String[] {
                    "",
                    "Yes.",
                    "What has happened here?",
                    "What do you want me to do?",
                };
                switch (SCScript.SECURE_RANDOM.nextInt(3)) {
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
                    int j = SCScript.SECURE_RANDOM.nextInt(i + 1);
                    byte temp = ScriptData.questOrder[i];
                    ScriptData.questOrder[i] = ScriptData.questOrder[j];
                    ScriptData.questOrder[j] = temp;
                }

                ScriptData.currentLoadOutData = new LoadOutData(7, 1, null);
                ScriptData.currentLoadOutData.addEquipmentItem(21059, 1, 0); // Killer's knife
                ScriptData.currentLoadOutData.addInventoryItem(1925, 0, 1, 0); // Bucket
                ScriptData.currentLoadOutData.addInventoryItem(1929, 0, 1, 0); // Bucket of water
                ScriptData.currentLoadOutData.addInventoryItem(21056, 0, 1, 0); // Notes
                ScriptData.currentLoadOutData.addInventoryItem(21057, 0, 1, 0); // Notes
                ScriptData.currentLoadOutData.addInventoryItem(21058, 0, 1, 0); // Notes
                ScriptData.currentLoadOutData.addInventoryItem(946, 0, 1, 0); // Knife
                ScriptData.currentLoadOutData.addInventoryItem(590, 0, 1, 0); // Tinderbox

                LocalPathFinder localPathFinder = LocalPathFinder.getLocalPathFinder();
                localPathFinder.addBlacklistedTile(new Tile(1643, 4839, 0));
                localPathFinder.addBlacklistedTile(new Tile(1643, 4840, 0));
                localPathFinder.addBlacklistedTile(new Tile(1633, 4842, 0));
                localPathFinder.addBlacklistedTile(new Tile(1634, 4842, 0));
                localPathFinder.addObstacle(new PassableObstacle("Damaged wall", "Climb"));

                SCScript.NODES[ScriptData.currentTask.ordinal()] = new MisthalinMystery();
                Logger.log("Initialized loadOut for MisthalinMystery");
                break;
            case SPINNING_BALLS_OF_WOOL:
                ScriptData.currentArea = new Area(3193, 3276, 3212, 3257); // Sheep pen
                ScriptData.currentArea2 = new Area(3203, 3217, 3213, 3207, 1); // Spinning wheel
                ScriptData.currentArea3 = new Area(3188, 3275, 3192, 3270); // Shears area
                break;
            case SMELTING_BRONZE_BARS:
                ScriptData.currentArea = new Area(3222, 3150, 3232, 3142); // SE Lumbridge Mine
                ScriptData.currentArea2 = new Area(3219, 3257, 3228, 3247); // Lumbridge Furnace
                if (PlayerData.canEquipBestPickaxeAvail) {
                    ScriptData.currentLoadOutData.setEquipmentItem(0, 1, 1);
                    ScriptData.currentLoadOutData.setInventoryItem(0, 0, 0, 0);
                    ScriptData.currentLoadOutData.setInventoryItem(1, 0, 14, 0);
                    ScriptData.currentLoadOutData.setInventoryItem(2, 0, 14, 0);
                }
                else {
                    ScriptData.currentLoadOutData.setEquipmentItem(0, 0, 0);
                    ScriptData.currentLoadOutData.setInventoryItem(0, 1, 1, 1);
                    ScriptData.currentLoadOutData.setInventoryItem(1, 0, 13, 0);
                    ScriptData.currentLoadOutData.setInventoryItem(2, 0, 13, 0);
                }
                break;
            case CHOPPING_LOGS:
                ScriptData.currentEntityName = "Tree";
                ScriptData.currentArea = currentAreaFactory();
                PlayerData.initializeBestAxeAvail(Skills.getRealLevel(Skill.WOODCUTTING), Skills.getRealLevel(Skill.ATTACK));
                if (PlayerData.canEquipBestAxeAvail) {
                    ScriptData.currentLoadOutData.setEquipmentItem(0, 1, 1);
                    ScriptData.currentLoadOutData.setInventoryItem(0, 0, 0, 0);
                }
                else {
                    ScriptData.currentLoadOutData.setEquipmentItem(0, 0, 0);
                    ScriptData.currentLoadOutData.setInventoryItem(0, 1, 1, 1);
                }
                break;
            case SMELTING_STEEL_BARS:
                break;
        }
    }

    private boolean isValidTask(ScriptState scriptState) {
        switch (scriptState) {
            case THE_RESTLESS_GHOST:
                return !FreeQuest.THE_RESTLESS_GHOST.isFinished();
            case COOKS_ASSISTANT:
                return !FreeQuest.COOKS_ASSISTANT.isFinished();
            case RUNE_MYSTERIES:
                return !FreeQuest.RUNE_MYSTERIES.isFinished() && Combat.getCombatLevel() >= 13; // For Mugger by Aubury
            case X_MARKS_THE_SPOT:
                return !FreeQuest.X_MARKS_THE_SPOT.isFinished();
            case SMELTING_STEEL_BARS:
                return Skills.getRealLevel(Skill.WOODCUTTING) >= 12 && Combat.getCombatLevel() >= 13 && Skills.getRealLevel(Skill.MINING) >= 30 && Skills.getRealLevel(Skill.SMITHING) >= 30;
            case PIRATES_TREASURE:
                return !FreeQuest.PIRATES_TREASURE.isFinished() && Combat.getCombatLevel() >= 10;
            case WITCHS_POTION:
                return !FreeQuest.WITCHS_POTION.isFinished() && Combat.getCombatLevel() >= 13;
            case ERNEST_THE_CHICKEN:
                return !FreeQuest.ERNEST_THE_CHICKEN.isFinished();
            case THE_KNIGHTS_SWORD:
                return !FreeQuest.THE_KNIGHTS_SWORD.isFinished() && Skills.getRealLevel(Skill.MINING) >= 10;
            case MISTHALIN_MYSTERY:
                return !FreeQuest.MISTHALIN_MYSTERY.isFinished();
            default:
                if (taskType == 0) {
                    return scriptState != ScriptData.currentProgressionTask;
                }
                else {
                    return scriptState != ScriptData.currentMoneyMakingTask;
                }
        }
    }

    @Override
    public int loop() {
        if (taskType == 2) { // returnToProgressionTaskFromMoneyMakingTask
            InitializeTask.initializeTaskI = 1;
            ScriptData.currentTask = ScriptData.currentProgressionTask;
            Logger.log("taskType 2 (returnToProgressionTaskFromMoneyMakingTask, currentTask set to currentProgressionTask: " + ScriptData.currentTask);
        }
        else if (taskType != 3) { // 3 == reInitializeProgressionTask
            int totalWeight = 0;
            byte startI;
            byte endI;
            if (taskType == 0) { // progressionTask
                startI = 0;
                endI = 14;
            }
            else { // moneyMakingTask
                startI = 15;
                endI = 18;
            }
            Logger.log("startI: " + startI + ", endI: " + endI);
            Logger.log("taskType: " + taskType);
            for (byte i = startI; i < endI; i++) { // Update when adding new ProgressionTasks
                if (isValidTask(ScriptState.values()[i])) {
                    Logger.log("Adding weight of " + ScriptState.values()[i] +"(" + SCScript.TASK_WEIGHTS[i] + ") to totalWeight");
                    totalWeight += SCScript.TASK_WEIGHTS[i];
                }
            }
            Logger.log("totalWeight: " + totalWeight);
            int taskRoll = SCScript.SECURE_RANDOM.nextInt(totalWeight);
            totalWeight = 0;
            for (byte i = startI; i < endI; i++) {
                if (isValidTask(ScriptState.values()[i])) { // Prevent selecting a Node that isn't a Progression Task
                    totalWeight += SCScript.TASK_WEIGHTS[i];
                    if (taskRoll < totalWeight) {
                        if (taskType == 0) {
                            ScriptData.currentProgressionTask = ScriptState.values()[i];
                            ScriptData.currentTask = ScriptData.currentProgressionTask;
                            InitializeTask.initializeTaskI = 0;
                            Logger.log("currentTask set to currentProgressionTask: " + ScriptData.currentProgressionTask);
                        }
                        else {
                            ScriptData.currentMoneyMakingTask = ScriptState.values()[i];
                            ScriptData.currentTask = ScriptData.currentMoneyMakingTask;
                            InitializeTask.initializeTaskI = 2;
                            Logger.log("currentTask set to currentMoneyMakingTask: " + ScriptData.currentMoneyMakingTask);
                        }
                        break;
                    }
                }
            }
        }
        ScriptData.resetEntities();
        setUpTask();
        SCScript.scriptState = ScriptState.INITIALIZE_TASK;
        ScriptData.returnTo = SCScript.scriptState;
        Logger.log("currentTask: " + ScriptData.currentTask);

        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
    }

}
