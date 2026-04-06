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
 * 0: Plan out buy/sell/deposit/withdraw + shuffleOrder 1+
 * - When reaches end, re-plan - if either buy/sell/deposit/withdraw not empty > stage = 0, shuffleOrder 1+
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

    public int[] itemsToEqp = new int[11];
    public byte itemsToEqpSize;
    public byte currentItemToEqp;
    public byte closeOutOfBankToEqp = -1;

    public byte currentStageI;
    public final byte[] stage = new byte[] { 0, 1, 2, 3 }; // 0 - plan, 1 - determine buyItems/sellItems/determineSecondaryTask, 2 - eqpItems, 3 - routeToBanking

    public boolean locked;

    public CheckLoadOutLI() {
        super(null);
        setShouldHandle(() -> ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].shouldBank().verify() || locked);
    }

    public void reset() {
        currentStageI = 0;
        shuffleStage();
    }

    @Override
    public int handle() {
        if (currentStageI >= stage.length) {
            shuffleStage();
            currentStageI = 0;
            if (ScriptData.buyItemsLI.getBuySize() == 0 && ScriptData.sellItemsLI.getSellSize() == 0
                    && ScriptData.depositLI.getDepositSize() == 0 && ScriptData.withdrawLI.getWithdrawSize() == 0
                    && !ScriptData.depositAllEqpLI.shouldHandle() && !ScriptData.depositAllInvLI.shouldHandle()
                    && itemsToEqpSize == 0) {
                locked = false;
                ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].setSetUp(false);
                if (ScriptData.taskType == 0 && ScriptData.progressionTaskTimer == null) {
                    ScriptData.progressionTaskTimer = getTaskTimer();
                    Logger.log("Initialized progressionTaskTimer");
                }
                else if (ScriptData.secondaryTaskTimer == null) {
                    ScriptData.secondaryTaskTimer = getTaskTimer();
                    Logger.log("Initialized secondaryTaskTimer");
                }
                Logger.log("Finished CheckLoadOutLI");
            }
            ScriptData.buyItemsLI.reset();
            ScriptData.sellItemsLI.reset();
            ScriptData.withdrawLI.reset();
            ScriptData.depositLI.reset();
            return ScriptData.returnMSFast();
        }
        else if (!locked) {
            shuffleStage();
            locked = true;
            currentStageI = 0;
            Logger.log("locked now true, checkThis order:" + Arrays.toString(stage));
            return ScriptData.returnMSFast();
        }
        else {
            switch (stage[currentStageI]) {
                case 0: // plan
                    Logger.log("Starting planning");
                    manageEqpLoadOut();
                    depositExtraInv();
                    depositExtraEqp();
                    manageInvLoadOut();
                    Logger.log("Done planning; buySize: " + ScriptData.buyItemsLI.getBuySize() + ", withdrawSize: " + ScriptData.withdrawLI.getWithdrawSize() + ", itemsToEqpSize: " + itemsToEqpSize + ", depositSize: " + ScriptData.depositLI.getDepositSize() + ", depositAllInv: " + ScriptData.depositAllInvLI.shouldHandle());
                    if (ScriptData.buyItemsLI.getBuySize() == 0 && ScriptData.sellItemsLI.getSellSize() == 0
                            && ScriptData.depositLI.getDepositSize() == 0 && ScriptData.withdrawLI.getWithdrawSize() == 0
                            && !ScriptData.depositAllEqpLI.shouldHandle() && !ScriptData.depositAllInvLI.shouldHandle()
                            && itemsToEqpSize == 0) {
                        currentStageI = 4;
                        Logger.log("Doesn't need to checkLoadOut anymore");
                    }
                    else {
                        currentStageI++;
                    }
                    return ScriptData.returnMSFast();
                case 1: // determine if routing to buy/sell/determineSecondary
                    if (ScriptData.buyItemsLI.getBuySize() != 0) {
                        int coinsHeld = Bank.count(995) + Inventory.count(995);
                        int totalPrice = ScriptData.buyItemsLI.getBuyTotalPrice();
                        if (coinsHeld >= totalPrice) {
                            ScriptData.withdrawLI.reset();
                            ScriptData.depositLI.reset();
                            ScriptData.sellItemsLI.reset();
                            ScriptData.bankingPipeline.setBankingReturnToI(ScriptData.currentPipelineI);
                            ScriptData.currentPipelineI = 35;
                            Logger.log("Routing to buyItems, buySize: " + ScriptData.buyItemsLI.getBuySize() + ", withdrawSize: " + ScriptData.withdrawLI.getWithdrawSize());
                        }
                        else {
                            attemptAddSellableItems();
                            if (ScriptData.sellItemsLI.getSellSize() != 0) {
                                ScriptData.currentPipelineI = 36;
                                ScriptData.withdrawLI.reset();
                                ScriptData.depositLI.reset();
                                ScriptData.buyItemsLI.reset();
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
                        currentStageI = 0;
                    }
                    else {
                        Logger.log("Done determining routing to buy/sell/determineSecondary");
                        currentStageI++;
                    }
                    return ScriptData.returnMSFast();
                case 2: // withdraw/depositItems
                    Logger.log("Withdraw/depositItems");
                    if (ScriptData.withdrawLI.getWithdrawSize() != 0) {
                        ScriptData.withdrawLI.shuffleWithdraw();
                    }
                    if (ScriptData.depositLI.getDepositSize() != 0) {
                        ScriptData.depositLI.shuffleDeposit();
                    }
                    if (ScriptData.withdrawLI.getWithdrawSize() != 0 || ScriptData.depositLI.getDepositSize() != 0
                            || ScriptData.depositAllInvLI.shouldHandle() || ScriptData.depositAllEqpLI.shouldHandle()) {
                        ScriptData.bankingPipeline.shuffleLoopInterceptors();
                        ScriptData.bankingPipeline.setBankingReturnToI(ScriptData.currentPipelineI);
                        ScriptData.currentPipelineI = 34;
                        Logger.log("Go to bankingPipeline (34)");
                    }
                    currentStageI++;
                    return ScriptData.returnMSFast();
                case 3: // eqpItems
                    Logger.log("eqpItems, itemsToEqpSize: " + itemsToEqpSize);
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
                    else if (!Inventory.contains(itemsToEqp[currentItemToEqp])) {
                        currentItemToEqp++;
                    }
                    else {
                        Item item = Inventory.get(itemsToEqp[currentItemToEqp]);
                        if (item != null) {
                            Logger.log("item: " + item.getName());
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
        p.currentStageI = currentStageI;
        p.stage = stage;
        p.locked = locked;
    }

    public void importFromPersistedScriptInfo(PersistedScriptInfo p) {
        itemsToEqp = p.itemsToEqp;
        itemsToEqpSize = p.itemsToEqpSize;
        currentItemToEqp = p.currentItemToEqp;
        closeOutOfBankToEqp = p.closeOutOfBankToEqp;
        currentStageI = p.currentStageI;
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

    private void shuffleStage() {
        if (ScriptData.rollChance(50)) {
            byte tmp = stage[2];
            stage[2] = stage[3];
            stage[3] = tmp;
        }
        Logger.log("Shuffled, " + Arrays.toString(stage));
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
                    Logger.log("Added toSell, id: " + id + ", sellQty: " + sellQty + ", pricePer: " + pricePer);
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
                    Logger.log("Added toSell, id: " + id + ", sellQty: " + sellQty + ", pricePer: " + pricePer);
                    totalBuyPriceRemaining -= sellQty * pricePer;
                }
                if (totalBuyPriceRemaining <= 0) {
                    return;
                }
            }
        }
    }

    private void manageEqpLoadOut() {
        for (byte i = 0; i < ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpSize(); i++) {
            int id = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpItemID(i);
            int eqpCount = Equipment.count(id);
            int invCount = Inventory.count(id);
            int bankCount = Bank.count(id);
            int minCount = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpItemQtyMin(i);
            int maxCount = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpItemQtyMax(i);
            int initCount = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getEqpItemQtyInit(i);
            if ((ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].shouldSetUp() && eqpCount + invCount + bankCount < initCount)
                    || (!ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].shouldSetUp() && eqpCount + invCount + bankCount < minCount)) {
                ScriptData.buyItemsLI.addBuyItem(id, initCount - invCount - eqpCount - bankCount); // Buy more eqpItems
                Logger.log("Added " + id + " to buyItems");
            }
            else if (minCount > 0 && invCount > 0) { // Equip/Wear eqpItems that are in inventory
                addItemToEqp(id);
                Logger.log("Added " + id + " to itemsToEqp");
            }
            else if (bankCount > 0 && maxCount > 0 && eqpCount + invCount < maxCount) {
                Logger.log("bankCount: " + bankCount + ", eqpCount: " + eqpCount + ", maxCount: " + maxCount);
                ScriptData.withdrawLI.addItemToWithdraw(id, Math.min(bankCount, maxCount)); // Withdraw eqpItems from bank
                Logger.log("Added " + id + " to withdraw, qty: " + Math.min(bankCount, maxCount));
            }
        }
    }

    private void depositExtraInv() {
        HashSet<Integer> alreadyAdded = new HashSet<>(28);
        HashSet<Integer> uniqueItems = new HashSet<>(28);
        for (Item item : Inventory.toArray()) {
            if (item != null) {
                uniqueItems.add(item.getId());
                boolean contains = false;
                int itemID = item.getId();
                for (byte i = 0; i < ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvSize(); i++) {
                    int loadOutID = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvItemID(i);
                    if (itemID == loadOutID) {
                        contains = true;
                        break;
                    }
                }
                if (!contains && !alreadyAdded.contains(itemID)) {
                    ScriptData.depositLI.addItemToDeposit(itemID, Inventory.count(itemID));
                    alreadyAdded.add(itemID);
                }
            }
        }
        Logger.log("alreadyAdded (size): " + alreadyAdded.size());
        Logger.log("uniqueItems (size): " + uniqueItems.size());
        if (!uniqueItems.isEmpty() && uniqueItems.size() == ScriptData.depositLI.getDepositSize()) {
            Logger.log("Needs to depositAllInv instead of depositing individual items");
            ScriptData.depositLI.reset();
            ScriptData.depositAllInvLI.setDepositAllInv(true);
        }
    }

    private void depositExtraEqp() {
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
    }

    private void manageInvLoadOut() {
        for (byte i = 0; i < ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvSize(); i++) {
            int id = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvItemID(i);
            int invCount = Inventory.count(id);
            int bankCount = Bank.count(id);
            int minCount = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvItemQtyMin(i);
            int maxCount = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvItemQtyMax(i);
            int initCount = ScriptData.TASK_LOAD_OUTS[ScriptData.currentPipelineI].getInvItemQtyInit(i);
            Logger.log("i: " + i + ", id: " + id + ", invCount: " + invCount + ", bankCount: " + bankCount + ", maxCount: " + maxCount);
            if (bankCount > 0 && bankCount + invCount >= minCount && invCount < maxCount) {
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
    }

}
