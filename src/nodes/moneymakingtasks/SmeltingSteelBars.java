package nodes.moneymakingtasks;

import framework.Node;
import framework.SCScript;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.utilities.Logger;

/**
 * Requirements:
 * - 12 Woodcutting (Canoe System)
 * - 13+ Combat (Avoid Mugger aggro)
 * - 30 Mining (Coal)
 * - 30 Smithing
 * Steps:
 * - Walk to SW Varrock Mining Spot, mine Iron
 * - Go to Barbarian Village, mine Coal
 * - Smith at Edgeville furnace
 */
public class SmeltingSteelBars implements Node {

    @Override
    public int loop() {
        if (Dialogues.inDialogue()) {
            if (Dialogues.canContinue()) {
                Dialogues.continueDialogue();
                Logger.log("Attempted to continue dialogue");
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }
}
