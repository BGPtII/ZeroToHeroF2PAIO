package nodes;

import framework.Node;
import framework.SCScript;
import global.PlayerData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.utilities.Sleep;

public class TheRestlessGhost implements Node {

    private final Area FATHER_AERECK_AREA = new Area(3240, 3215, 3247, 3204);
    private final Area FATHER_URHNEY_AREA = new Area(3144, 3177, 3151, 3173);
    private final Area RESTLESS_GHOST_AREA = new Area(3247, 3195, 3251, 3190);
    private final Area GHOSTS_SKULL_AREA = new Area(3111, 9569, 3121, 9564);

    @Override
    public int loop() {
        if (PlayerData.currentLoadOutService.setUpNotValid()) {
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 300;
        }

        switch (PlayerSettings.getConfig(107)) { // TODO: No redundant instantiating of currentGO, efficiency, generalize methods
            case 0:
                return Utility.interactWithNPCNotInAreaSingle("Father Aereck", FATHER_AERECK_AREA, "Talk-to", Dialogues::inDialogue);
            case 1:
                return Utility.interactWithNPCNotInAreaSingle("Father Urhney", FATHER_URHNEY_AREA, "Talk-to", Dialogues::inDialogue);
            case 2:
                if (RESTLESS_GHOST_AREA.contains(Players.getLocal())) {
                    if ((Utility.currentNPC = NPCs.closest("Restless ghost")) != null) {
                        if (Utility.currentNPC.interact("Talk-to")) {
                            Sleep.sleepUntil(Dialogues::inDialogue, Utility.secureRandom.nextInt(10000 - 3000 + 1) + 3000, 300);
                        }
                    }
                    else if ((Utility.currentGO = GameObjects.closest("Coffin")) != null) {
                        if (Utility.currentGO.hasAction("Search")) {
                            if (Utility.currentGO.interact("Search")) {
                                Sleep.sleepUntil(() -> (Utility.currentNPC = NPCs.closest("Restless ghost")) != null, Utility.secureRandom.nextInt(10000 - 3000 + 1) + 3000, 300);
                            }
                        }
                        else if (Utility.currentGO.hasAction("Open")) {
                            if (Utility.currentGO.interact("Open")) {
                                Sleep.sleepUntil(() -> (Utility.currentGO = GameObjects.closest("Coffin")) != null && Utility.currentGO.hasAction("Search"), Utility.secureRandom.nextInt(10000 - 3000 + 1) + 3000, 300);
                            }
                        }
                    }
                }
                else {
                    return Utility.walkToArea(RESTLESS_GHOST_AREA);
                }
                break;
            case 3:
            case 4:
                if (Inventory.contains("Ghost's skull")) {
                    if (!RESTLESS_GHOST_AREA.contains(Players.getLocal())) {
                        return Utility.walkToArea(RESTLESS_GHOST_AREA);
                    }
                    else if ((Utility.currentGO = GameObjects.closest("Coffin")) != null) {
                        if (Utility.currentGO.hasAction("Search")) {
                            if (Utility.currentGO.interact("Search")) {
                                Sleep.sleepUntil(Dialogues::inDialogue, Utility.secureRandom.nextInt(10000 - 3000 + 1) + 3000, 300);
                            }
                        }
                        else if (Utility.currentGO.hasAction("Open")) {
                            if (Utility.currentGO.interact("Open")) {
                                Sleep.sleepUntil(() -> (Utility.currentGO = GameObjects.closest("Coffin")) != null && Utility.currentGO.hasAction("Search"), Utility.secureRandom.nextInt(10000 - 3000 + 1) + 3000, 300);
                            }
                        }
                    }
                }
                else {
                    return Utility.interactWithGONotInAreaSingle("Altar", "Search", GHOSTS_SKULL_AREA, () -> Inventory.contains("Ghost's skull"));
                }
                break;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
