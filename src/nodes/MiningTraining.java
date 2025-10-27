package nodes;

import framework.Node;
import framework.SCScript;
import global.PlayerData;
import global.ScriptData;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;

public class MiningTraining implements Node {

    private Tile getMiningTile() {
        Tile[] opts = new Tile[10];
        byte optsSize = 0;
        for (Tile t : ScriptData.currentArea.getTiles()) {
            if (Walking.canWalk(t) && GameObjects.all(gameObject -> gameObject.distance() == 1 && (gameObject.getName().equals(ScriptData.currentEntityName) || ScriptData.currentEntityName.startsWith("T") && gameObject.getName().equals("Copper rocks"))).size() >= 2) {
                opts[optsSize++] = t;
            }
        }
        if (optsSize == 0) {
            return null;
        }
        return opts[SCScript.SECURE_RANDOM.nextInt(opts.length)];
    }

    @Override
    public int loop() {
        if (PlayerData.currentLoadOutService.setUpNotValid()) {
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
        }

        if (!ScriptData.currentArea.contains(Players.getLocal())) {
            PlayerData.walkToArea(ScriptData.currentArea);
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }
        if (ScriptData.currentTile == null) {
            ScriptData.currentTile = getMiningTile();
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
        }


        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
