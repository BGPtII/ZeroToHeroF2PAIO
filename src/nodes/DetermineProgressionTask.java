package nodes;

import framework.Node;
import framework.SCScript;
import framework.ScriptState;
import global.PlayerData;
import global.ScriptData;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import services.LoadOutService;

public class DetermineProgressionTask implements Node {

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
                if (PlayerData.currentProgressionTask == ScriptState.MELEE_TRAINING) {
                    switch (SCScript.SECURE_RANDOM.nextInt(2)) {
                        case 0:
                            PlayerData.currentLoadOutService.setInventoryItem(0, 1, 1, 1); // Brass key needed
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
        }
        return null;
    }

    private LoadOutService loadOutFactory() { // loadOut setUp, determineTaskVariables
        PlayerData.currentLoadOutService = SCScript.TASK_LOAD_OUTS[SCScript.scriptState.ordinal()]; // Could potentially be null
        switch (SCScript.scriptState) {
            case WOODCUTTING_TRAINING:
                if (PlayerData.currentLoadOutService == null) {
                    PlayerData.currentLoadOutService = new LoadOutService(1, 1, true);
                    if (PlayerData.canEquipAxe) {
                        SCScript.TASK_LOAD_OUTS[SCScript.scriptState.ordinal()].addEquipmentItem(PlayerData.axe, 1, 1);
                    }
                    else {
                        SCScript.TASK_LOAD_OUTS[SCScript.scriptState.ordinal()].addInventoryItem(PlayerData.axe, 1, 1, 1);
                    }
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
                break;
            case MINING_TRAINING:
                PlayerData.initializePickaxe();
                if (PlayerData.currentLoadOutService == null) {
                    PlayerData.currentLoadOutService = new LoadOutService(1, 1, true);
                    if (PlayerData.canEquipPickaxe) {
                        SCScript.TASK_LOAD_OUTS[SCScript.scriptState.ordinal()].addEquipmentItem(PlayerData.pickaxe, 1, 1);
                    }
                    else {
                        SCScript.TASK_LOAD_OUTS[SCScript.scriptState.ordinal()].addInventoryItem(PlayerData.pickaxe, 1, 1, 1);
                    }
                }
                if (Skills.getRealLevel(Skill.MINING) >= 15) {
                    ScriptData.currentEntityName = "Iron rocks";
                }
                else {
                    ScriptData.currentEntityName = "Tin rocks";
                }
                ScriptData.currentArea = currentAreaFactory();
                break;
            case FISHING_TRAINING:
                if (PlayerData.currentLoadOutService == null) {
                    PlayerData.currentLoadOutService = new LoadOutService(2, 0, true);
                }
                if (Skills.getRealLevel(Skill.FISHING) >= 20) {
                    PlayerData.currentLoadOutService.addInventoryItem(309, 1, 1, 1);
                    int featherCount = SCScript.SECURE_RANDOM.nextInt(5000 - 500 + 1) + 500;
                    PlayerData.currentLoadOutService.addInventoryItem(314, 1, featherCount, featherCount);
                    ScriptData.currentEntityName = "Rod Fishing spot";
                    switch (SCScript.SECURE_RANDOM.nextInt(3)) {
                        case 0:
                            ScriptData.currentArea = new Area(3243, 3238, 3237, 3255); // Lumbridge along river
                            break;
                        case 1:
                            ScriptData.currentArea = new Area(3100, 3436, 3111, 3422); // Lure/Bait
                            break;
                    }
                }
                else {
                    PlayerData.currentLoadOutService.addInventoryItem(303, 1, 1, 1);
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
                }
                break;
            case MELEE_TRAINING:
                if (PlayerData.currentLoadOutService == null) {
                    PlayerData.currentLoadOutService = new LoadOutService(2, 7, true);
                    PlayerData.currentLoadOutService.addEquipmentItem(PlayerData.meleeHat, 1, 1);
                    PlayerData.currentLoadOutService.addEquipmentItem(PlayerData.meleeChest, 1, 1);
                    PlayerData.currentLoadOutService.addEquipmentItem(PlayerData.meleeLegs, 1, 1);
                    PlayerData.currentLoadOutService.addEquipmentItem(PlayerData.meleeShield, 1, 1);
                    PlayerData.currentLoadOutService.addEquipmentItem(PlayerData.meleeWeapon, 1, 1);
                    PlayerData.currentLoadOutService.addEquipmentItem(PlayerData.AMULET, 1, 1);
                    PlayerData.currentLoadOutService.addEquipmentItem(PlayerData.cape, 1, 1);

                    PlayerData.currentLoadOutService.addInventoryItem(983, 0, 0, 0); // Increase to 1 when rolling Hill Giants in Edgeville Dungeon
                    PlayerData.currentLoadOutService.addInventoryItem(PlayerData.food, 0, 0, 0); // Change max/init values every task roll
                }
                PlayerData.currentLoadOutService.setInventoryItem(1, 1, SCScript.SECURE_RANDOM.nextInt(25 - 5 + 1) + 5, SCScript.SECURE_RANDOM.nextInt(1000 - 50 + 1) + 50);
                int attackLevel = Skills.getRealLevel(Skill.ATTACK);
                int strengthLevel = Skills.getRealLevel(Skill.STRENGTH);
                int defenceLevel = Skills.getRealLevel(Skill.DEFENCE);
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
                            PlayerData.currentLoadOutService.setInventoryItem(1, 0, 0, 0);
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
                ScriptData.currentArea = currentAreaFactory();
                break;
            case RANGED_TRAINING:
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
                            break;
                        case 1:
                            ScriptData.currentEntityName = "Cow";
                            break;
                        case 2:
                            ScriptData.currentEntityName = "Goblin";
                            break;
                    }
                }
                break;
            case THE_RESTLESS_GHOST: // Questing, will need to instantiate but not persist - Node + LoadOut
                SCScript.NODES[SCScript.scriptState.ordinal()] = new TheRestlessGhost(); // Nullify after completion
                PlayerData.currentLoadOutService = new LoadOutService(0, 0, false);

                if (PlayerData.playerHasItemID(552)) { // Ghostspeak Amulet (uncharged)
                    PlayerData.currentLoadOutService.addEquipmentItem(552, 1, 1);
                }
                break;
            case COOKS_ASSISTANT:
                SCScript.NODES[SCScript.scriptState.ordinal()] = new CooksAssistant();
                PlayerData.currentLoadOutService = new LoadOutService(0, 0, false);
                if (PlayerData.playerHasItemID(1933)) { // Pot of flour
                    PlayerData.currentLoadOutService.addInventoryItem(1933, 1, 1, 1);
                }
                else {
                    if (PlayerData.playerHasItemID(1931)) { // Pot
                        PlayerData.currentLoadOutService.addInventoryItem(1931, 1, 1, 1);
                    }
                    if (PlayerData.playerHasItemID(1947)) { // Grain
                        PlayerData.currentLoadOutService.addInventoryItem(1947, 1, 1, 1);
                    }
                }
                if (PlayerData.playerHasItemID(1944)) { // Egg
                    PlayerData.currentLoadOutService.addInventoryItem(1944, 1, 1, 1);
                }
                break;
        }
        return SCScript.TASK_LOAD_OUTS[SCScript.scriptState.ordinal()];
    }

    private boolean isValidTask(ScriptState scriptState) {
        switch (scriptState) {
            case THE_RESTLESS_GHOST:
                return !FreeQuest.THE_RESTLESS_GHOST.isFinished();
            case COOKS_ASSISTANT:
                return !FreeQuest.COOKS_ASSISTANT.isFinished();
            default:
                return false;
        }
    }

    @Override
    public int loop() {
        int totalWeight = 0;
        for (int i = 0; i < SCScript.TASK_WEIGHTS.length; i++) {
            totalWeight += SCScript.TASK_WEIGHTS[i];
        }
        int taskRoll = SCScript.SECURE_RANDOM.nextInt(totalWeight);
        totalWeight = 0;
        for (int i = 0; i < ScriptState.values().length; i++) {
            if (SCScript.TASK_WEIGHTS[i] == 0 && isValidTask(ScriptState.values()[i])) { // Prevent selecting a Node that isn't a Progression Task
                totalWeight += SCScript.TASK_WEIGHTS[i];
                if (taskRoll < totalWeight) {
                    SCScript.scriptState = ScriptState.values()[i];
                    PlayerData.currentProgressionTask = ScriptState.values()[i];
                    PlayerData.currentLoadOutService = loadOutFactory();
                    break;
                }
            }
        }

        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
    }

}
