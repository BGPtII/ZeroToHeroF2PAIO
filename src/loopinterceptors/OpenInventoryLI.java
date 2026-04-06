package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.utilities.Logger;

public class OpenInventoryLI extends LoopInterceptor {

    private boolean locked;

    public OpenInventoryLI() {
        super(null);
        setShouldHandle(() -> (!Inventory.isOpen() && ScriptData.rollChance(35.75)) || locked);
    }

    @Override
    public int handle() {
        if (Inventory.isOpen()) {
            Logger.log("Done opening inventory");
            locked = false;
            return 0;
        }
        if (!locked) {
            Logger.log("Locked to open inventory");
            locked = true;
        }
        if (Bank.isOpen()) {
            Bank.close();
            return ScriptData.returnMSNormal();
        }

        if (Inventory.open()) {
            return ScriptData.returnMSNormal();
        }
        return ScriptData.returnMSFast();
    }

}
