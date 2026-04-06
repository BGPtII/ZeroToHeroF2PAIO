package data.global;

import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;

public class PlayerData {

    public static int targetAttackLevel;
    public static int targetStrengthLevel;
    public static int targetDefenceLevel;
    public static int targetWoodcuttingLevel;
    public static int targetMiningLevel;
    public static int targetRunecraftingLevel;
    public static int targetFishingLevel;
    public static int targetRangedLevel;
    public static int targetFiremakingLevel;
    public static int targetSmithingLevel;
    public static int targetCookingLevel;

    public static int axe;
    public static boolean canEquipAxe;
    public static int bestAxeAvail;
    public static boolean canEquipBestAxeAvail;

    public static int pickaxe;
    public static boolean canEquipPickaxe;
    public static int bestPickaxeAvail;
    public static boolean canEquipBestPickaxeAvail;

    public static int[] runecraftingMediums = new int[4]; // Tiara or talisman - Number determined by linear training steps
    public static int currentRunecraftingMedium;
    public static void initializeCurrentRunecraftMedium(int runecraftingLevel) {
        if (runecraftingLevel >= 20) {
            currentRunecraftingMedium = runecraftingMediums[3];
        }
        else if (runecraftingLevel >= 14) {
            currentRunecraftingMedium = runecraftingMediums[2];
        }
        else if (runecraftingLevel >= 9) {
            currentRunecraftingMedium = runecraftingMediums[1];
        }
        else {
            currentRunecraftingMedium = runecraftingMediums[0];
        }
        Logger.log("currentRunecraftMedium: " + currentRunecraftingMedium);
    }

    public static int food;
    public static byte foodHP;
    public static int foodTier1;
    public static byte foodHPTier1;
    public static int foodTier2;
    public static byte foodHPTier2;
    public static int eatFoodHPTrs;
    public static void determineFood(int hitPointsLevel) {
        if (hitPointsLevel >= 25) {
            food = foodTier2;
            foodHP  = foodHPTier2;
        }
        else {
            food = foodTier1;
            foodHP = foodHPTier1;
        }
        Logger.log("food: " + food);
        Logger.log("foodHP: " + foodHP);
    }
    public static void determineEatFoodHPTrs() {
        int low = (int) Math.ceil(0.25 * Skills.getRealLevel(Skill.HITPOINTS));
        int high = Skills.getRealLevel(Skill.HITPOINTS) - foodHP;
        Logger.log("low: " + low + ", high: " + high);
        eatFoodHPTrs = ScriptData.SECURE_RANDOM.nextInt(high - low + 1) + low;
        Logger.log("eatFoodHPTrs: " + eatFoodHPTrs);
    }

    public static final int AMULET = 1731; // Amulet of power
    public static int cape;

    public static int meleeHat; // Follows EquipmentSlot naming convention - hat always full helm
    public static int meleeChest; // Early level platebody, high level chainbody (chooses NPCs with crush attack for higher levels, earlier levels usually don't crush)
    public static int meleeLegs; // Uses plateskirt (less weight than platelegs)
    public static int meleeShield; // Always kiteshield, sq shield sucks
    public static int meleeWeapon; // Always scimitar

    public static int rangedHat;
    public static int rangedChest;
    public static int rangedLegs;
    public static int rangedHands;
    public static int rangedWeapon;
    public static int rangedArrows;

    public static boolean playerHasItemID(int id) {
        return Bank.contains(id) || Inventory.contains(id) || Equipment.contains(id);
    }

    public static void initializeAxe(int woodcuttingLevel, int attackLevel) {
        if (woodcuttingLevel >= 41) {
            axe = 1359;
            canEquipAxe = attackLevel >= 40;
        }
        else if (woodcuttingLevel >= 31) {
            axe = 1357;
            canEquipAxe = attackLevel >= 30;
        }
        else if (woodcuttingLevel >= 21) {
            axe = 1355;
            canEquipAxe = attackLevel >= 20;
        }
        else if (woodcuttingLevel >= 11) {
            axe = 1361;
            canEquipAxe = attackLevel >= 10;
        }
        else {
            axe = 1349; // Iron
            canEquipAxe = true;
        }
        Logger.log("initialized axe: ," + axe + ", canEquipAxe: " + canEquipAxe);
    }
    public static void initializeBestAxeAvail(int woodcuttingLevel, int attackLevel) {
        if (woodcuttingLevel >= 41 && playerHasItemID(1359)) {
            bestAxeAvail = 1359;
            canEquipBestAxeAvail = attackLevel >= 40;
        }
        else if (woodcuttingLevel >= 31 && playerHasItemID(1357)) {
            bestAxeAvail = 1357;
            canEquipBestAxeAvail = attackLevel >= 30;
        }
        else if (woodcuttingLevel >= 21 && playerHasItemID(1355)) {
            bestAxeAvail = 1355;
            canEquipBestAxeAvail = attackLevel >= 20;
        }
        else if (woodcuttingLevel >= 11 && playerHasItemID(1361)) {
            bestAxeAvail = 1361;
            canEquipBestAxeAvail = attackLevel >= 10;
        }
        else if (playerHasItemID(1349)) {
            bestAxeAvail = 1349;
            canEquipBestAxeAvail = true;
        }
        else {
            bestAxeAvail = 1351;
            canEquipBestAxeAvail = true;
        }
        Logger.log("initialized bestAxeAvail: ," + bestAxeAvail + ", canEquipBestAxeAvail: " + canEquipBestAxeAvail);
    }

    public static void initializeMeleeArmour(int defenceLevel) {
        if (defenceLevel >= 41) {
            meleeHat = 1163;
            meleeChest = 1113; // Chain
            meleeLegs = 1093;
            meleeShield = 1201;
        }
        else if (defenceLevel >= 31) {
            meleeHat = 1161;
            meleeChest = 1123;
            meleeLegs = 1091;
            meleeShield = 1199;
        }
        else if (defenceLevel >= 21) {
            meleeHat = 1159;
            meleeChest = 1121;
            meleeLegs = 1085;
            meleeShield = 1197;
        }
        else if (defenceLevel >= 11) {
            meleeHat = 1165;
            meleeChest = 1125;
            meleeLegs = 1089;
            meleeShield = 1195;
        }
        else { // Iron
            meleeHat = 1153;
            meleeChest = 1115;
            meleeLegs = 1081;
            meleeShield = 1191;
        }
    }
    public static void initializeMeleeWeapon(int attackLevel) {
        if (attackLevel >= 41) {
            meleeWeapon = 1333;
        }
        else if (attackLevel >= 31) {
            meleeWeapon = 1331;
        }
        else if (attackLevel >= 21) {
            meleeWeapon = 1329;
        }
        else if (attackLevel >= 11) {
            meleeWeapon = 1327;
        }
        else { // Iron
            meleeWeapon = 1323;
        }
    }

    public static void initializePickaxe(int miningLevel, int attackLevel) {
        if (miningLevel >= 41) {
            pickaxe = 1275;
            canEquipPickaxe = attackLevel >= 40;
        }
        else if (miningLevel >= 31) {
            pickaxe = 1271;
            canEquipPickaxe = attackLevel >= 30;
        }
        else if (miningLevel >= 21) {
            pickaxe = 1273;
            canEquipPickaxe = attackLevel >= 20;
        }
        else if (miningLevel >= 11) {
            pickaxe = 12297;
            canEquipPickaxe = attackLevel >= 10;
        }
        else {
            pickaxe = 1267;
            canEquipPickaxe = true;
        }
        Logger.log("initialized pickaxe, canEquipPickaxe: " + canEquipPickaxe);
    }
    public static void initializeBestPickaxeAvail(int miningLevel, int attackLevel) {
        if (miningLevel >= 41 && playerHasItemID(1275)) {
            bestPickaxeAvail = 1275;
            canEquipBestPickaxeAvail = attackLevel >= 40;
        }
        else if (miningLevel >= 31 && playerHasItemID(1271)) {
            bestPickaxeAvail = 1271;
            canEquipBestPickaxeAvail = attackLevel >= 30;
        }
        else if (miningLevel >= 21 && playerHasItemID(1273)) {
            bestPickaxeAvail = 1273;
            canEquipBestPickaxeAvail = attackLevel >= 20;
        }
        else if (miningLevel >= 11 && playerHasItemID(12297)) {
            bestPickaxeAvail = 12297;
            canEquipBestPickaxeAvail = attackLevel >= 10;
        }
        else if (playerHasItemID(1267)) {
            bestPickaxeAvail = 1267;
            canEquipBestPickaxeAvail = true;
        }
        else {
            bestPickaxeAvail = 1265;
            canEquipBestPickaxeAvail = true;
        }
    }

    public static void initializeRangedHat(int rangedLevel) {
        if (rangedLevel >= 20) {
            rangedHat = 1169;
        }
        else {
            rangedHat = 1167;
        }
    }
    public static void initializeRangedChest(int rangedLevel, int defenceLevel) {
        if (rangedLevel >= 40 && defenceLevel >= 40 && FreeQuest.DRAGON_SLAYER_I.isFinished()) {
            rangedChest = 1135;
        }
        else if (rangedLevel >= 20 && defenceLevel >= 20) {
            rangedChest = 1133;
        }
        else if (defenceLevel >= 10) {
            rangedChest = 1131;
        }
        else {
            rangedChest = 1129;
        }
    }
    public static void initializeRangedLegs(int rangedLevel) {
        if (rangedLevel >= 40) {
            rangedLegs = 1099;
        }
        else if (rangedLevel >= 20) {
            rangedLegs = 1097;
        }
        else {
            rangedLegs = 1095;
        }
    }
    public static void initializeRangedWeaponArrows(int rangedLevel) {
        if (rangedLevel >= 30) {
            rangedWeapon = 853;
            rangedArrows = 888;
        }
        else if (rangedLevel >= 20) {
            rangedWeapon = 849;
            rangedArrows = 888;
        }
        else {
            rangedWeapon = 841;
            rangedArrows = 884;
        }
    }
    public static void initializeRangedHands(int rangedLevel) {
        if (rangedLevel >= 40) {
            rangedHands = 1065;
        }
        else {
            rangedHands = 1063;
        }
    }

    public static CombatStyle meleeCombatStyle;
    public static int switchMeleeCombatStyleLevel;
    public static void determineMeleeCombatStyle(int attackLevel, int strengthLevel, int defenceLevel) {
        CombatStyle[] combatStylePool = new CombatStyle[3];
        byte poolSize = 0;
        if (attackLevel >= 30 && strengthLevel >= 30 && defenceLevel >= 30) {
            combatStylePool[poolSize++] = CombatStyle.ATTACK;
            combatStylePool[poolSize++] = CombatStyle.STRENGTH;
            combatStylePool[poolSize++] = CombatStyle.DEFENCE;
        }
        else if (attackLevel >= 20 && strengthLevel >= 20 && defenceLevel >= 20) {
            if (attackLevel < 30) {
                combatStylePool[poolSize++] = CombatStyle.ATTACK;
            }
            if (strengthLevel < 30) {
                combatStylePool[poolSize++] = CombatStyle.STRENGTH;
            }
            if (defenceLevel < 30) {
                combatStylePool[poolSize++] = CombatStyle.DEFENCE;
            }
        }
        else if (attackLevel >= 10 && strengthLevel >= 10 && defenceLevel >= 10) {
            if (attackLevel < 20) {
                combatStylePool[poolSize++] = CombatStyle.ATTACK;
            }
            if (strengthLevel < 20) {
                combatStylePool[poolSize++] = CombatStyle.STRENGTH;
            }
            if (defenceLevel < 20) {
                combatStylePool[poolSize++] = CombatStyle.DEFENCE;
            }
        }
        else {
            if (attackLevel < 10) {
                combatStylePool[poolSize++] = CombatStyle.ATTACK;
            }
            if (strengthLevel < 10) {
                combatStylePool[poolSize++] = CombatStyle.STRENGTH;
            }
            if (defenceLevel < 10) {
                combatStylePool[poolSize++] = CombatStyle.DEFENCE;
            }
        }
        meleeCombatStyle = combatStylePool[ScriptData.SECURE_RANDOM.nextInt(poolSize)];
    }
    public static void determineSwitchMeleeCombatStyleLevel(int attackLevel, int strengthLevel, int defenceLevel) {
        if (attackLevel >= 30 && strengthLevel >= 30 && defenceLevel >= 30) {
            switchMeleeCombatStyleLevel = ScriptData.SECURE_RANDOM.nextInt(7 - 2 + 1) + 2;
        }
        else if (attackLevel >= 20 && strengthLevel >= 20 && defenceLevel >= 20) {
            switchMeleeCombatStyleLevel = Math.min(30, ScriptData.SECURE_RANDOM.nextInt(7 - 2 + 1) + 2);
        }
        else if (attackLevel >= 10 && strengthLevel >= 10 && defenceLevel >= 10) {
            switchMeleeCombatStyleLevel = Math.min(20, ScriptData.SECURE_RANDOM.nextInt(7 - 2 + 1) + 2);
        }
        else {
            switchMeleeCombatStyleLevel = Math.min(10, ScriptData.SECURE_RANDOM.nextInt(7 - 2 + 1) + 2);
        }
        switch (meleeCombatStyle) {
            case ATTACK:
                switchMeleeCombatStyleLevel += attackLevel;
                break;
            case STRENGTH:
                switchMeleeCombatStyleLevel += strengthLevel;
                break;
            case DEFENCE:
                switchMeleeCombatStyleLevel += defenceLevel;
                break;
        }
    }

    public static CombatStyle rangedCombatStyle;
    public static int switchRangedCombatStyleLevel;
    public static void determineRangedCombatStyle() {
        CombatStyle[] combatStylePool = new CombatStyle[] {
            CombatStyle.RANGED,
            CombatStyle.RANGED_RAPID,
            CombatStyle.RANGED_DEFENCE
        };
        rangedCombatStyle = combatStylePool[ScriptData.SECURE_RANDOM.nextInt(3)];
    }
    public static void determineSwitchRangedCombatStyleLevel(int rangedLevel) {
        if (rangedLevel >= 50) {
            switchRangedCombatStyleLevel = ScriptData.SECURE_RANDOM.nextInt(7 - 2 + 1) + 2;
        }
        else if (rangedLevel >= 30) {
            switchRangedCombatStyleLevel = Math.min(50, ScriptData.SECURE_RANDOM.nextInt(7 - 2 + 1) + 2);
        }
        else if (rangedLevel >= 20) {
            switchRangedCombatStyleLevel = Math.min(30, ScriptData.SECURE_RANDOM.nextInt(7 - 2 + 1) + 2);
        }
        else {
            switchRangedCombatStyleLevel = Math.min(20, ScriptData.SECURE_RANDOM.nextInt(7 - 2 + 1) + 2);
        }
    }

}
