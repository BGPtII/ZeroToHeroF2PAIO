package nodes;

import framework.Node;
import framework.SCScript;
import global.PlayerData;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

import java.util.List;

public class MeleeTraining implements Node {

    private final Condition TOOK_GROUND_ITEM = () -> PlayerData.currentGroundItem.exists();
    private final Condition INTERACTED_WITH_TARGET = () -> PlayerData.currentNPC == null || !PlayerData.currentNPC.exists() || PlayerData.currentNPC.getCharacterInteractingWithMe() != null;

    @Override
    public int loop() {
        if (PlayerData.currentLoadOutService.setUpNotValid()) {
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
        }

        if (PlayerData.currentNPC == null || !PlayerData.currentNPC.exists() // No target NPC
                || (PlayerData.currentCharacter == null && (PlayerData.currentCharacter = PlayerData.currentNPC.getCharacterInteractingWithMe()) != null && PlayerData.currentCharacter.equals(Players.getLocal()))) { // Someone else attacking target
            if (PlayerData.currentTile != null) { // Collect npcDrop
                List<GroundItem> opts = GroundItems.getForTile(PlayerData.currentTile);
                if (opts.isEmpty()) {
                    PlayerData.currentTile = null;
                    PlayerData.currentNPC = null;
                    PlayerData.currentCharacter = null;
                }
                else {
                    PlayerData.currentGroundItem = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                    if (PlayerData.currentGroundItem.interact("Take")) {
                        Sleep.sleepUntil(TOOK_GROUND_ITEM, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                    }
                }
            }
            else if (PlayerData.currentArea.contains(Players.getLocal())) { // walkToArea
                PlayerData.walkToArea(PlayerData.currentArea);
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            else if (PlayerData.currentCharacter == null && (PlayerData.currentCharacter = Players.getLocal().getCharacterInteractingWithMe()) != null && PlayerData.currentCharacter.hasAction("Attack")) { // Something attacking player, target not set
                PlayerData.currentNPC = (NPC) PlayerData.currentCharacter;
                PlayerData.currentTile = PlayerData.currentNPC.getServerTile();
            }
            else { // Determine targetNPC
                List<NPC> opts;
                switch (PlayerData.currentEntityName) {
                    case "Cow":
                        opts = NPCs.all(npc -> npc.getName().contains(PlayerData.currentEntityName) && npc.getCharacterInteractingWithMe() == null);
                        break;
                    case "Minotaur":
                        opts = NPCs.all(npc -> npc.getName().equals(PlayerData.currentEntityName) && npc.getLevel() == 12 && npc.getCharacterInteractingWithMe() == null);
                        break;
                    case "Barbarian":
                        opts = NPCs.all(npc -> npc.getName().equals(PlayerData.currentEntityName) && npc.getId() != 3068 && npc.getCharacterInteractingWithMe() == null);
                        break;
                    default:
                        opts = NPCs.all(npc -> npc.getName().equals(PlayerData.currentEntityName) && npc.getCharacterInteractingWithMe() == null);
                }
                if (!opts.isEmpty()) {
                    PlayerData.currentNPC = opts.get(SCScript.SECURE_RANDOM.nextInt(opts.size()));
                    PlayerData.currentTile = PlayerData.currentNPC.getServerTile();
                }
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
        }

        if (!PlayerData.currentNPC.canReach()) {
            PlayerData.walkToEntity(PlayerData.currentNPC);
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (!Players.getLocal().isInteracting(PlayerData.currentNPC) && PlayerData.currentNPC.interact("Attack")) {
            Sleep.sleepUntil(INTERACTED_WITH_TARGET, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000);
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
