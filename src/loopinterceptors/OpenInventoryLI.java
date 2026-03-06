package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;

public class OpenInventoryLI extends LoopInterceptor {

    public OpenInventoryLI() {
        super(() -> ScriptData.NOT_HANDLE_LOAD_OUT.verify() && !Inventory.isOpen() && ScriptData.rollChance(5.75));
    }

    @Override
    public int handle() {
        if (Inventory.open()) {
            return ScriptData.returnMSNormal();
        }
        return ScriptData.returnMSFast();
    }

}
