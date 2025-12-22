package nodes.progressiontasks.questing;

import framework.Node;
import framework.SCScript;
import framework.ScriptState;
import data.global.ScriptData;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;

public class TheKnightsSword implements Node {

    private final Area SQUIRE_AREA = new Area(2980, 3348, 2965, 3337);
    private final Area RELDO_AREA = new Area(3207, 3497, 3216, 3490);
    private final Area THURGO_AREA = new Area(2995, 3148, 3001, 3143);
    private final Area PORTRAIT_AREA = new Area(2981, 3335, 2984, 3334, 2);

    @Override
    public int loop() {
        if (Dialogues.inDialogue()) {
            if (Dialogues.isProcessing()) {
                return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
            }
            if (Dialogues.canContinue()) {
                Dialogues.continueDialogue();
            }
            else if (Dialogues.areOptionsAvailable()) {
                Dialogues.chooseFirstOption(ScriptData.dialogueOpts);
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (FreeQuest.THE_KNIGHTS_SWORD.isFinished()) {
            if (Widgets.isVisible(153, 16)) {
                if (ScriptData.closeQuestCompletionWidget()) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
            }
            else {
                ScriptData.finishProgressionQuestingTask();
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        switch (PlayerSettings.getConfig(122)) {
            case 0:
            case 4:
                if (!ScriptData.interactWithNPC(4737, SQUIRE_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // Squire
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 1:
                if (!ScriptData.interactWithNPC(6203, RELDO_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // Reldo
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 2:
            case 3:
                if (!ScriptData.interactWithNPC(4733, THURGO_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 5:
                if (Inventory.contains(666)) { // Portrait
                    if (!ScriptData.interactWithNPC(4733, THURGO_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (PORTRAIT_AREA.contains(Players.getLocal())) {
                    if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || ScriptData.currentNPC.getId() != 4736) { // Sir Vyvin
                        ScriptData.currentNPC = NPCs.closest(4736);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if ((ScriptData.currentGameObject = GameObjects.closest(2272, 2271)) != null) { // Cupboard#Open - 2272, Closed - 2271
                        if (ScriptData.currentGameObject.getId() == 2271) {
                            if (!ScriptData.interactWithGameObjectSingle(2271, PORTRAIT_AREA, "Open", () -> ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists())) {
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                        }
                        else if (ScriptData.currentNPC.getY() > 3336) {
                            if (!ScriptData.interactWithGameObjectSingle(2272, PORTRAIT_AREA, "Search", () -> ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists())) {
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                        }
                    }
                }
                else if (!ScriptData.walkToArea(PORTRAIT_AREA)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 6:
                if (Inventory.contains(667)) { // Blurite sword
                    if (!ScriptData.interactWithNPC(4737, SQUIRE_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) { // Squire
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (Inventory.count(2351) == 2) { // Iron bar
                    if (!ScriptData.interactWithNPC(4733, THURGO_AREA, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (Inventory.contains(668) && !Inventory.contains(2351)) { // Blurite ore, Iron bar
                    SCScript.BANKING_DATA.addItemToWithdraw(2351, 2);
                    ScriptData.returnTo = ScriptData.currentProgressionTask;
                    SCScript.scriptState = ScriptState.BANKING;
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else if (ScriptData.currentTile.equals(Players.getLocal().getTile())) {
                    if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || !ScriptData.currentGameObject.getName().equals("Blurite rocks")) {
                        ScriptData.currentGameObject = GameObjects.closest("Blurite rocks");
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (ScriptData.currentGameObject.interact()) {
                        Sleep.sleepUntil(() -> Inventory.contains(668), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (!ScriptData.walkToTile(ScriptData.currentTile)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
