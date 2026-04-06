package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.utilities.Sleep;

public class CollectShears extends LoopInterceptor {

    public CollectShears() {
        super(() -> !Inventory.contains(1735));
    }

    @Override
    public int handle() {
        if (!ScriptData.currentArea3.contains(Players.getLocal())) {
            if (ScriptData.walkToArea(ScriptData.currentArea3)) {
                return ScriptData.returnMSNormal();
            }
            return ScriptData.returnMSFast();
        }

        if (ScriptData.currentGroundItem == null || !ScriptData.currentGroundItem.exists() || ScriptData.currentGroundItem.getId() != 1735) {
            ScriptData.currentGroundItem = GroundItems.closest(1735);
            return ScriptData.returnMSFast();
        }

        if (ScriptData.currentGroundItem.interact("Take")) {
            Sleep.sleepUntil(() -> ScriptData.currentGroundItem == null || !ScriptData.currentGroundItem.exists(), ScriptData.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
            return ScriptData.returnMSFast();
        }

        return ScriptData.returnMSNormal();
    }
}
