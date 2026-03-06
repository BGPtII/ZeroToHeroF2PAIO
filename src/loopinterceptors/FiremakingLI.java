package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.items.Item;

import java.util.List;

public class FiremakingLI extends LoopInterceptor {

    private final Condition DONE_LIGHTING_LOGS = () -> !Players.getLocal().getTile().equals(ScriptData.currentTile);

    public FiremakingLI() {
        super(() -> ScriptData.NOT_HANDLE_LOAD_OUT.verify() && ScriptData.IN_CURRENT_AREA.verify());
    }

    private void translateToNextAvailTile() {
        List<Tile> bounds = ScriptData.currentArea.getBoundaryPoints();
        ScriptData.currentTile.setX(ScriptData.currentTile.getX() - 1);
        if (!ScriptData.currentArea.contains(ScriptData.currentTile)) { // Moved outside xLeft bound
            ScriptData.currentTile.setY(ScriptData.currentTile.getY() - 1);
            ScriptData.currentTile.setX(bounds.get(0).getX() - 1);
            if (!ScriptData.currentArea.contains(ScriptData.currentTile)) { // Moved outside yBot bound
                ScriptData.currentTile.setY(bounds.get(0).getY());
            }
        }
    }

    @Override
    public int handle() {
        if (!ScriptData.currentTile.equals(Players.getLocal().getTile())) {
            if (!ScriptData.currentTile.canReach() || !ScriptData.currentArea.contains(ScriptData.currentTile)) {
                translateToNextAvailTile();
                return ScriptData.returnMSFast();
            }
            else if (!ScriptData.walkToTile(ScriptData.currentTile)) {
                translateToNextAvailTile();
                return ScriptData.returnMSFast();
            }
            return ScriptData.returnMSNormal();
        }

        if (GameObjects.getTopObjectOnTile(ScriptData.currentTile) != null) {
            translateToNextAvailTile();
            return ScriptData.returnMSFast();
        }

        if (Inventory.isItemSelected()) {
            if (Inventory.interact(590)) {
                int[] logSlots = new int[28];
                byte logSlotsSize = 0;
                for (Item item : Inventory.toArray()) {
                    if (item != null && item.getId() == ScriptData.TASK_LOAD_OUTS[1].getInvItemID(1)) {
                        logSlots[logSlotsSize++] = item.getSlot();
                    }
                }
                if (Inventory.slotInteract(logSlots[ScriptData.SECURE_RANDOM.nextInt(logSlotsSize)])) {
                    Sleep.sleepUntil(DONE_LIGHTING_LOGS, ScriptData.SECURE_RANDOM.nextInt(30000 - 15000 + 1) + 15000, 300);
                    if (DONE_LIGHTING_LOGS.verify()) {
                        translateToNextAvailTile();
                    }
                }
            }
            return ScriptData.returnMSFast();
        }

        if (Inventory.slotInteract(590)) { // Tinderbox
            return ScriptData.returnMSNormal();
        }

        return ScriptData.returnMSFast();
    }

}
