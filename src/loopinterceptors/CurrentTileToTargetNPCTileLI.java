package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;

public class CurrentTileToTargetNPCTileLI extends LoopInterceptor {

    public CurrentTileToTargetNPCTileLI() {
        super(() -> ScriptData.currentNPC != null
                && ScriptData.currentNPC.exists()
                && (ScriptData.currentTile == null || !ScriptData.currentTile.equals(ScriptData.currentNPC.getServerTile())));
    }

    @Override
    public int handle() {
        ScriptData.currentTile = ScriptData.currentNPC.getServerTile();
        return 0;
    }
}
