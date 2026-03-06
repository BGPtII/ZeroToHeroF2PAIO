package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.GroundItem;

import java.util.List;

public class LootNPCDropsLI extends LoopInterceptor {

    public LootNPCDropsLI() {
        super(() -> {
            if (ScriptData.currentTile == null || Inventory.isFull()) {
                return false;
            }
            List<GroundItem> groundItems = GroundItems.getForTile(ScriptData.currentTile);
            if (groundItems.isEmpty()) {
                return false;
            }
            ScriptData.currentGroundItem = groundItems.get(ScriptData.SECURE_RANDOM.nextInt(groundItems.size()));
            return ScriptData.currentGroundItem != null;
        });
    }

    @Override
    public int handle() {
        if (ScriptData.currentGroundItem.interact("Take")) {
            Sleep.sleepUntil(ScriptData.GROUND_ITEM_NOT_EXISTS_NULL, ScriptData.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
        }
        return ScriptData.returnMSFast();
    }

}
