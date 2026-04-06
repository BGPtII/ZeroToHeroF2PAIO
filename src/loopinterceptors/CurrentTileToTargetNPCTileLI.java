package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.utilities.Logger;

public class CurrentTileToTargetNPCTileLI extends LoopInterceptor {

    public CurrentTileToTargetNPCTileLI() {
        super(() -> ScriptData.currentNPC != null
                && ScriptData.currentNPC.getHealthPercent() > 0
                && (ScriptData.currentTile == null || !ScriptData.currentTile.equals(ScriptData.currentNPC.getServerTile())));
    }

    @Override
    public int handle() {
        Logger.log("Set currentTile as currentNPC serverTile");
        ScriptData.currentTile = ScriptData.currentNPC.getServerTile();
        return 0;
    }
}
