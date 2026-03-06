package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

/**
 * questOrderI
 */
public class MiningLI extends LoopInterceptor {

    public MiningLI() {
        super(() -> ScriptData.IN_CURRENT_AREA.verify() && ScriptData.NOT_HANDLE_LOAD_OUT.verify());
    }

    private Tile getMiningTile() {
        Tile[] opts = new Tile[10];
        byte optsSize = 0;
        for (Tile t : ScriptData.currentArea.getTiles()) {
            if (Walking.canWalk(t) && GameObjects.getTopObjectOnTile(t) == null && GameObjects.all(gameObject -> gameObject.distance(t) == 1 && (gameObject.getName().equals(ScriptData.currentEntityName) || (ScriptData.currentEntityName.startsWith("T") && gameObject.getName().equals("Copper rocks")))).size() >= 2) {
                opts[optsSize++] = t;
            }
        }
        if (optsSize == 0) {
            return null;
        }
        return opts[ScriptData.SECURE_RANDOM.nextInt(opts.length)];
    }

    @Override
    public int handle() {
        if (ScriptData.currentTile == null) { // determineMiningTile
            if ((ScriptData.currentTile = getMiningTile()) != null) {
                return ScriptData.returnMSFast();
            }
            return ScriptData.returnMSNormal();
        }

        if (!ScriptData.currentTile.equals(Players.getLocal().getTile())) {
            if (ScriptData.walkToTile(ScriptData.currentTile)) {
                return ScriptData.returnMSNormal();
            }
            return ScriptData.returnMSFast();
        }

        if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists()) { // determineRockToMine
            List<GameObject> opts;
            opts = GameObjects.all(gO -> (gO.getName().equals(ScriptData.currentEntityName) || (ScriptData.currentEntityName.startsWith("T") && gO.getName().equals("Copper rocks"))) && gO.distance() == 1);
            if (!opts.isEmpty()) {
                ScriptData.currentGameObject = opts.get(ScriptData.SECURE_RANDOM.nextInt(opts.size()));
            }
            return ScriptData.returnMSFast();
        }

        if (ScriptData.currentGameObject.interact("Mine")) {
            Sleep.sleepUntil(ScriptData.DONE_RESOURCE_GATHERING, ScriptData.SECURE_RANDOM.nextInt(50000 - 20000 + 1) + 20000, 300);
            return ScriptData.returnMSFast();
        }

        return ScriptData.returnMSNormal();
    }

}
