package loopinterceptors;

import data.PersistedScriptInfo;
import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;

import java.util.Arrays;
import java.util.HashSet;

/**
 * If initial:
 * - Remove items not in loadOut (eqp + inv) from Inventory/Equipment
 */
public class CheckLoadOutLI extends LoopInterceptor {

    private final int[] SELLABLE_ITEMS_NOT_RESTRICTED = new int[] { // Uses Normal/Un-noted IDs
        1511, // Logs
        1759, 2349, // Ball of wool, Bronze bar
        526, 2138, 2134, 2132, // Bones, Raw chicken, Raw rat meat, Raw beef
        1623, 1619, 1621, 1617  // Uncut sapphire, Uncut ruby, Uncut emerald, Uncut diamond
    };
    private final int[] SELLABLE_ITEMS_RESTRICTED = new int[] {
        1521, 1519, // Oak logs, Willow logs
        438, 436, 440, // Tin ore, Copper ore, Iron ore
        556, 557, 554, 559 // Air rune, Earth rune, Fire rune, Body rune
    };

    private int[] itemsToEqp = new int[11];
    private byte itemsToEqpSize;
    private byte currentItemToEqp;
    private byte closeOutOfBankToEqp = -1;

    private byte currentStageI;
    private final byte[] stage = new byte[] { 0, 1, 2, 3 };

    private boolean locked;

    public CheckLoadOutLI() {
        super(null);
        setShouldHandle(() -> ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].shouldBank().verify() || locked);
    }

    public void reset() {
        locked = false;
    }

    @Override
    public int handle() {
        if (currentStageI >= stage.length) {
            locked = false;
            currentStageI = 0;
            ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].setSetUp(false);
            Logger.log("locked now false");
            if (ScriptData.taskType == 0 && ScriptData.progressionTaskTimer == null) {
                ScriptData.progressionTaskTimer = getTaskTimer();
                Logger.log("Initialized progressionTaskTimer");
            }
            else if (ScriptData.secondaryTaskTimer == null) {
                ScriptData.secondaryTaskTimer = getTaskTimer();
                Logger.log("Initialized secondaryTaskTimer");
            }
            Logger.log("Finished CheckLoadOutLI");
            return ScriptData.returnMSFast();
        }
        else if (!locked) {
            shuffleCheckThis();
            locked = true;
            currentStageI = 0;
            Logger.log("locked now true, checkThis order:" + Arrays.toString(stage));
            return ScriptData.returnMSFast();
        }
        else {
            switch (stage[currentStageI]) {
                case 0: // plan
                    Logger.log("Starting planning");
                    Logger.log("eqpSize: " + ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpSize());
                    for (byte i = 0; i < ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpSize(); i++) {
                        int id = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpItemID(i);
                        int eqpCount = Equipment.count(id);
                        int invCount = Inventory.count(id);
                        int bankCount = Bank.count(id);
                        int minCount = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpItemQtyMin(i);
                        int maxCount = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpItemQtyMax(i);
                        int initCount = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpItemQtyInit(i);
                        if (invCount > 0) { // Equip/Wear eqpItems that are in inventory
                            addItemToEqp(id);
                        }
                        if (bankCount > 0 && eqpCount + invCount < maxCount) {
                            ScriptData.withdrawLI.addItemToWithdraw(id, Math.min(bankCount, maxCount)); // Withdraw eqpItems from bank
                            Logger.log("Added " + id + " to withdraw");
                        }
                        else if (initCount > 0) {
                            ScriptData.buyItemsLI.addBuyItem(id, initCount - invCount - eqpCount - bankCount); // Buy more eqpItems
                            Logger.log("Added " + id + " to buyItems");
                        }
                    }
                    HashSet<Integer> alreadyAdded = new HashSet<>();
                    for (Item item : Inventory.toArray()) {
                        if (item != null) {
                            boolean contains = false;
                            int itemID = item.getId();
                            Logger.log("invItemID to check: " + itemID);
                            for (byte i = 0; i < ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvSize(); i++) {
                                int loadOutID = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvItemID(i);
                                Logger.log("loadOutID to check: " + loadOutID);
                                if (itemID == loadOutID) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (!contains && !alreadyAdded.contains(itemID)) {
                                Logger.log("Added id " + itemID + " to deposit");
                                ScriptData.depositLI.addItemToDeposit(itemID, Inventory.count(itemID));
                                alreadyAdded.add(itemID);
                            }
                        }
                    }
                    Logger.log("Added inventory items to deposit, size: " + ScriptData.depositLI.getDepositSize());
                    for (Object obj : Equipment.toArray()) {
                        Item item = (Item) obj;
                        if (item != null) {
                            int itemID = item.getId();
                            boolean contains = false;
                            for (byte i = 0; i < ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpSize(); i++) {
                                int loadOutID = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpItemID(i);
                                if (itemID == loadOutID) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (!contains) {
                                Logger.log("Needs to deposit all equipment items");
                                ScriptData.depositAllEqpLI.setDepositAllEqp(true);
                                break;
                            }
                        }
                    }
                    Logger.log("Added equipment items to deposit, size: " + ScriptData.depositLI.getDepositSize());
                    Logger.log("invSize: " + ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvSize());
                    Logger.log("Iterating over inv loadout to see withdraw");
                    for (byte i = 0; i < ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvSize(); i++) {
                        int id = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvItemID(i);
                        int invCount = Inventory.count(id);
                        int bankCount = Bank.count(id);
                        int minCount = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvItemQtyMin(i);
                        int maxCount = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvItemQtyMax(i);
                        int initCount = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvItemQtyInit(i);
                        Logger.log("i: " + i + ", id: " + id + ", invCount: " + invCount + ", bankCount: " + bankCount + ", maxCount: " + maxCount);
                        if (bankCount > 0 && (bankCount + invCount >= minCount || invCount < maxCount)) {
                            ScriptData.withdrawLI.addItemToWithdraw(id, Math.min(bankCount, maxCount)); // Withdraw invItems from bank
                            Logger.log("Adding id " + id + " to withdraw");
                        }
                        else if (invCount > maxCount) {
                            ScriptData.depositLI.addItemToDeposit(id, invCount - maxCount); // Deposit extra invItems
                        }
                        else if (bankCount + invCount < minCount) {
                            ScriptData.buyItemsLI.addBuyItem(id, initCount - invCount - bankCount); // Buy more invItems
                            Logger.log("Added id " + id + " to buyItems");
                        }
                    }
                    Logger.log("Done planning; buySize: " + ScriptData.buyItemsLI.getBuySize() + ", withdrawSize: " + ScriptData.withdrawLI.getWithdrawSize());
                    currentStageI++;
                    return ScriptData.returnMSFast();
                case 1: // withdraw/depositItems
                    boolean triggerBanking = false;
                    if (ScriptData.withdrawLI.getWithdrawSize() != 0) {
                        triggerBanking = true;
                        ScriptData.withdrawLI.shuffleWithdraw();
                    }
                    if (ScriptData.depositLI.getDepositSize() != 0) {
                        triggerBanking = true;

                        ScriptData.depositLI.shuffleDeposit();

                    }
                    if (triggerBanking) {
                        ScriptData.bankingPipeline.setReturnToI(ScriptData.currentPipelineI);
                        ScriptData.currentPipelineI = 34;
                        Logger.log("Go to bankingPipeline (34)");
                    }
                    currentStageI++;
                    return ScriptData.returnMSFast();
                case 2: // determine if routing to buy/sell/determineSecondary
                    if (ScriptData.buyItemsLI.getBuySize() != 0) {
                        int coinsHeld = Bank.count(995) + Inventory.count(995);
                        int totalPrice = ScriptData.buyItemsLI.getBuyTotalPrice();
                        if (coinsHeld >= totalPrice) {
                            ScriptData.withdrawLI.reset();
                            ScriptData.bankingPipeline.setReturnToI(ScriptData.currentPipelineI);
                            ScriptData.currentPipelineI = 35;
                            Logger.log("Routing to buyItems");
                        }
                        else {
                            attemptAddSellableItems();
                            if (ScriptData.sellItemsLI.getSellSize() != 0) {
                                ScriptData.bankingPipeline.setReturnToI(ScriptData.currentPipelineI);
                                ScriptData.currentPipelineI = 36;
                                Logger.log("Routing to sellItems");
                            }
                            else { // Roll a secondary task
                                ScriptData.currentPipelineI = 33;
                                ScriptData.taskType = 1;
                                ScriptData.buyItemsLI.reset();
                                ScriptData.sellItemsLI.reset();
                                ScriptData.withdrawLI.reset();
                                ScriptData.depositLI.reset();
                                Logger.log("Routing to determineTask to roll a secondary task");
                            }
                        }
                    }
                    Logger.log("Done determining routing to buy/sell/determineSecondary");
                    currentStageI++;
                    return ScriptData.returnMSFast();
                case 3: // eqpItems
                    if (currentItemToEqp >= itemsToEqpSize) {
                        currentStageI++;
                        closeOutOfBankToEqp = -1;
                        currentItemToEqp = 0;
                        itemsToEqpSize = 0;
                        Logger.log("Done eqpItems");
                    }
                    else if (closeOutOfBankToEqp == -1) {
                        closeOutOfBankToEqp = (byte) ScriptData.SECURE_RANDOM.nextInt(2);
                        shuffleItemsToEqp();
                    }
                    else if (closeOutOfBankToEqp == 0 && Bank.isOpen()) {
                        Bank.close();
                        return ScriptData.returnMSNormal();
                    }
                    else if (Equipment.contains(itemsToEqp[currentItemToEqp]) && !Inventory.contains(itemsToEqp[currentItemToEqp])) {
                        currentItemToEqp++;
                    }
                    else {
                        Item item = Inventory.get(itemsToEqp[currentItemToEqp]);
                        if (item != null) {
                            if (item.hasAction("Wield")) {
                                if (item.interact("Wield")) {
                                    Sleep.sleepUntil(() -> Inventory.getIdForSlot(item.getSlot()) == -1, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                                }
                            }
                            else if (item.interact("Wear")) { // Wear
                                Sleep.sleepUntil(() -> Inventory.getIdForSlot(item.getSlot()) == -1, ScriptData.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                            }
                        }
                    }
                    return ScriptData.returnMSFast();
            }
        }

        return ScriptData.returnMSNormal();
    }

    private int getLevelForTimer() {
        switch (ScriptData.currentPipelineI) {
            case 0:
                return Skills.getRealLevel(Skill.COOKING);
            case 1:
                return Skills.getRealLevel(Skill.FIREMAKING);
            case 2:
                return Skills.getRealLevel(Skill.FISHING);
            case 3: {
                int a = Skills.getRealLevel(Skill.ATTACK);
                int s = Skills.getRealLevel(Skill.STRENGTH);
                int d = Skills.getRealLevel(Skill.DEFENCE);
                return (a + s + d) / 3;
            }
            case 4:
            case 30:
                return Skills.getRealLevel(Skill.MINING);
            case 5:
                return Skills.getRealLevel(Skill.RANGED);
            case 6:
                return Skills.getRealLevel(Skill.RUNECRAFTING);
            case 7:
            case 31:
                return Skills.getRealLevel(Skill.SMITHING);
            case 8:
            case 29:
                return Skills.getRealLevel(Skill.WOODCUTTING);
            case 32:
                return Skills.getRealLevel(Skill.CRAFTING);
        }
        return ScriptData.SECURE_RANDOM.nextInt(30) + 1;
    }
    private Timer getTaskTimer() {
        int level = Math.max(1, Math.min(30, getLevelForTimer())); // Must be between inclusive 1-30
        double minMinutes = (25.0 / 29.0) * level + (120.0 / 29.0); // lvl 1 == 5, lvl 30+ == 30
        double maxMinutes = (75.0 / 29.0) * level + (360.0 / 29.0); // lvl 1 == 15, lvl 30+ == 90
        long minMS = Math.round(minMinutes * 60000);
        long maxMS = Math.round(maxMinutes * 60000);
        return new Timer((long) (ScriptData.SECURE_RANDOM.nextDouble() * (maxMS - minMS + 1)) + minMS); // roll
    }

    public void exportToPersistedScriptInfo(PersistedScriptInfo p) {
        p.itemsToEqp = itemsToEqp;
        p.itemsToEqpSize = itemsToEqpSize;
        p.currentItemToEqp = currentItemToEqp;
        p.closeOutOfBankToEqp = closeOutOfBankToEqp;
        p.checkLoadOutStage = currentStageI;
        p.locked = locked;
    }

    public void importFromPersistedScriptInfo(PersistedScriptInfo p) {
        itemsToEqp = p.itemsToEqp;
        itemsToEqpSize = p.itemsToEqpSize;
        currentItemToEqp = p.currentItemToEqp;
        closeOutOfBankToEqp = p.closeOutOfBankToEqp;
        currentStageI = p.checkLoadOutStage;
        locked = p.locked;
    }

    private void shuffleItemsToEqp() {
        for (int i = itemsToEqpSize - 1; i > 0; i--) {
            int j = ScriptData.SECURE_RANDOM.nextInt(i + 1);
            int tmp = itemsToEqp[i];
            itemsToEqp[i] = itemsToEqp[j];
            itemsToEqp[j] = tmp;
        }
    }

    private void shuffleCheckThis() {
        for (int i = stage.length - 1; i > 1; i--) {
            int j = 1 + ScriptData.SECURE_RANDOM.nextInt(i);
            byte tmp = stage[i];
            stage[i] = stage[j];
            stage[j] = tmp;
        }
    }

    private void addItemToEqp(int id) {
        itemsToEqp[itemsToEqpSize++] = id;
    }

    private boolean shouldSell(int id) {
        if (id == 1511 && Skills.getRealLevel(Skill.FIREMAKING) < 15) { // Logs
            return false;
        }
        if (id == 2349 && Skills.getRealLevel(Skill.SMITHING) < 30) { // Bronze bar
            return false;
        }
        return (id != 2138 && id != 2134 && id != 2132) && Skills.getRealLevel(Skill.COOKING) < 15; // Raw chicken, Raw rat meat, Raw beef
    }
    private void shuffleSellableItemsNotRestricted() {
        for (int i = SELLABLE_ITEMS_NOT_RESTRICTED.length - 1; i > 0; i--) {
            int j = ScriptData.SECURE_RANDOM.nextInt(i + 1);
            int temp = SELLABLE_ITEMS_NOT_RESTRICTED[i];
            SELLABLE_ITEMS_NOT_RESTRICTED[i] = SELLABLE_ITEMS_NOT_RESTRICTED[j];
            SELLABLE_ITEMS_NOT_RESTRICTED[j] = temp;
        }
    }
    private void shuffleSellableItemsRestricted() {
        for (int i = SELLABLE_ITEMS_RESTRICTED.length - 1; i > 0; i--) {
            int j = ScriptData.SECURE_RANDOM.nextInt(i + 1);
            int temp = SELLABLE_ITEMS_RESTRICTED[i];
            SELLABLE_ITEMS_RESTRICTED[i] = SELLABLE_ITEMS_RESTRICTED[j];
            SELLABLE_ITEMS_RESTRICTED[j] = temp;
        }
    }
    private int getRandomSellPrice(int id) {
        int livePrice = LivePrices.get(id);
        if (livePrice == 0) {
            return 1;
        }
        else {
            int min = livePrice - (int) Math.max(1, Math.floor(ScriptData.SECURE_RANDOM.nextInt(21) / 100.0 * livePrice));
            int max = livePrice + (int) Math.ceil(ScriptData.SECURE_RANDOM.nextInt(21) / 100.0 * livePrice);
            return ScriptData.SECURE_RANDOM.nextInt(max - min + 1) + min;
        }
    }
    private void attemptAddSellableItems() {
        int totalBuyPriceRemaining = ScriptData.buyItemsLI.getBuyTotalPrice();
        totalBuyPriceRemaining -= Bank.count(995) + Inventory.count(995);
        if (Skills.getTotalLevel() >= 300 && Quests.getQuestPoints() >= 10) { // 100 total level actual threshold
            shuffleSellableItemsRestricted();
            for (int id : SELLABLE_ITEMS_RESTRICTED) {
                int qtyHeld = Inventory.count(id) + Bank.count(id);
                int pricePer = getRandomSellPrice(id);
                int qtyNeeded = (int) Math.ceil(totalBuyPriceRemaining / (double) pricePer);
                int sellQty = Math.min(qtyHeld, qtyNeeded);
                if (sellQty > 0) {
                    ScriptData.sellItemsLI.addSell(id, pricePer, sellQty);
                    totalBuyPriceRemaining -= sellQty * pricePer;
                }
                if (totalBuyPriceRemaining <= 0) {
                    return;
                }
            }
        }
        shuffleSellableItemsNotRestricted();
        for (int id : SELLABLE_ITEMS_NOT_RESTRICTED) {
            int qtyHeld = Inventory.count(id) + Bank.count(id);
            if (qtyHeld > 0 && shouldSell(id)) {
                int pricePer = getRandomSellPrice(id);
                int qtyNeeded = (int) Math.ceil(totalBuyPriceRemaining / (double) pricePer);
                int sellQty = Math.min(qtyHeld, qtyNeeded);
                if (sellQty > 0) {
                    ScriptData.sellItemsLI.addSell(id, pricePer, sellQty);
                    totalBuyPriceRemaining -= sellQty * pricePer;
                }
                if (totalBuyPriceRemaining <= 0) {
                    return;
                }
            }
        }
    }

}
