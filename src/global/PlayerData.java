package global;

import framework.SCScript;
import framework.ScriptState;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import services.LoadOutService;

public class PlayerData {

    public static int axe;
    public static boolean canEquipAxe;

    public static int pickaxe;
    public static boolean canEquipPickaxe;

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
    }
    public static void determineEatFoodHPTrs() {
        int low = (int) Math.ceil(0.25 * Skills.getRealLevel(Skill.HITPOINTS));
        int high = Skills.getRealLevel(Skill.HITPOINTS) - foodHP;
        eatFoodHPTrs = SCScript.SECURE_RANDOM.nextInt(high - low + 1) + low;
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

    public static boolean playerHasItemID(int id) {
        return Bank.contains(id) || Inventory.contains(id) || Equipment.contains(id);
    }
    public static int getTotalHeldCount(int id) { // Bank + Equipment + Inventory
        return Bank.count(id) + Inventory.count(id) + Equipment.count(id);
    }

    public static void initializeAxe() {
        int woodcuttingLevel = Skills.getRealLevel(Skill.WOODCUTTING);
        int attackLevel = Skills.getRealLevel(Skill.ATTACK);
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
            axe = 1631;
            canEquipAxe = attackLevel >= 10;
        }
        else {
            axe = 1349;
            canEquipAxe = true;
        }
    }

    public static void initializeCape() {
        if (cape == 0) {
            cape = 4315 + (SCScript.SECURE_RANDOM.nextInt(50) * 2); // IDs: 4315-4413, step 2
        }
    }

    public static void initializeMeleeArmour() {
        int defenceLevel = Skills.getRealLevel(Skill.DEFENCE);
        if (defenceLevel >= 41) {

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
    public static void initializeMeleeWeapon() {
        int attackLevel = Skills.getRealLevel(Skill.ATTACK);
        if (attackLevel >= 41) {

        }
        else if (attackLevel >= 31) {

        }
        else if (attackLevel >= 21) {

        }
        else if (attackLevel >= 11) {

        }
        else { // Iron

        }
    }

    public static void initializePickaxe() {
        int miningLevel = Skills.getRealLevel(Skill.MINING);
        if (miningLevel >= 41) {

        }
        if (miningLevel >= 31) {

        }
        if (miningLevel >= 21) {

        }
        if (miningLevel >= 11) {

        }
    }

}
