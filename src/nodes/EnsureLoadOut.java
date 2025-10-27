package nodes;

import framework.Node;
import framework.SCScript;
import framework.ScriptState;
import global.PlayerData;
import global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.wrappers.items.Item;

/**
 * - Buys items it doesn't have
 * - Gets rid of items it doesn't need
 */
public class EnsureLoadOut implements Node {

    private byte ensureLoadOutI; // Reset all once finished

    @Override
    public int loop() {
        switch (ensureLoadOutI) {
            case 0: // Check initial
                for (byte i = 0; i < PlayerData.currentLoadOutService.getInvSize(); i++) { // Add inv toBuy
                    int heldCount = PlayerData.getTotalHeldCount(PlayerData.currentLoadOutService.getInvItemID(i));
                    if (heldCount < PlayerData.currentLoadOutService.getInvItemQtyInit(i)) {
                        SCScript.GRAND_EXCHANGE_SERVICE.addBuyItem(PlayerData.currentLoadOutService.getInvItemID(i), PlayerData.currentLoadOutService.getInvItemQtyInit(i) - heldCount);
                    }
                }
                for (byte i = 0; i < PlayerData.currentLoadOutService.getEqpSize(); i++) { // Add eqp toBuy
                    int heldCount = PlayerData.getTotalHeldCount(PlayerData.currentLoadOutService.getEqpItemID(i));
                    if (heldCount < PlayerData.currentLoadOutService.getEqpItemQtyInit(i)) {
                        SCScript.GRAND_EXCHANGE_SERVICE.addBuyItem(PlayerData.currentLoadOutService.getEqpItemID(i), PlayerData.currentLoadOutService.getEqpItemQtyInit(i) - heldCount);
                    }
                }
                if (SCScript.GRAND_EXCHANGE_SERVICE.getBuySize() != 0) { // Needs to buy items
                    int coinsHeld = Bank.count("Coins") + Inventory.count("Coins");
                    boolean canBuyAllItems = true;
                    for (byte i = 0; i < SCScript.GRAND_EXCHANGE_SERVICE.getBuySize(); i++) { // Must be able to buy all items in order to trigger
                        if (coinsHeld >= SCScript.GRAND_EXCHANGE_SERVICE.getBuyTotalPrice(i)) {
                            coinsHeld -= SCScript.GRAND_EXCHANGE_SERVICE.getBuyTotalPrice(i);
                        }
                        else {
                            canBuyAllItems = false;
                        }
                    }
                    if (canBuyAllItems) { // Trigger toBuy, will re-verify here post buy attempt
                        SCScript.GRAND_EXCHANGE_SERVICE.shuffleBuy();
                        PlayerData.stateToReturnTo = ScriptState.ENSURE_LOAD_OUT;
                        SCScript.scriptState = ScriptState.BUY_ITEMS;
                    }
                    else { // Check if items can be sold to afford
                        PlayerData.stateToReturnTo = ScriptState.ENSURE_LOAD_OUT;
                        SCScript.scriptState = ScriptState.DETERMINE_MONEY_MAKING_TASK;
                    }
                }
                else {
                    ensureLoadOutI++; // Move to next stage, has all initial items
                }
                break;
            case 1: // Purge inv/eqp
                for (byte i = 0; i < EquipmentSlot.values().length; i++) {
                    Item item = Equipment.getItemInSlot(i);
                    if (item != null) {
                        boolean itemInLoadOut = false;
                        int id = item.getId();
                        for (byte j = 0; j < PlayerData.currentLoadOutService.getEqpSize(); j++) {
                            if (id == PlayerData.currentLoadOutService.getEqpItemID(j)) {
                                itemInLoadOut = true;
                                break;
                            }
                        }
                        if (!itemInLoadOut) { // Use deposit all equipment widget
                            SCScript.BANKING_SERVICE.toggleDpAllEqpInit(true);
                            break;
                        }
                    }
                }
                ScriptData.depositInvItemsNotInLoadOut();
                if (SCScript.BANKING_SERVICE.bankingShouldBeToggled()) {
                    PlayerData.stateToReturnTo = ScriptState.ENSURE_LOAD_OUT;
                    SCScript.scriptState = ScriptState.BANKING;
                }
                ensureLoadOutI++;
                break;
            case 2:
                ensureLoadOutI = 0;
                SCScript.scriptState = PlayerData.currentProgressionTask;
                break;
        }

        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
    }

}
