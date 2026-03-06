package loopinterceptors;

import data.global.PlayerData;
import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.emotes.Emote;
import org.dreambot.api.methods.emotes.Emotes;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

public class BelowIceMountainLI extends LoopInterceptor {

    private final Area WILLOW = new Area(2995, 3438, 3009, 3429);
    private final Area CHECKAL = new Area(3078, 3423, 3088, 3412);
    private final Area BURNTOF = new Area(2952, 3378, 2961, 3366);
    private final Area CHARLIE_THE_TRAMP = new Area(3200, 3394, 3212, 3382);
    private final Area COOK = new Area(3227, 3402, 3232, 3393);
    private final Area MARLEY = new Area(3085, 3473, 3091, 3468);
    private final Area RUINS_ENTRANCE = new Area(2993, 3498, 3001, 3490);
    private final Area BARBARIAN_LONGHALL = new Area(3075, 3445, 3082, 3436);

    public BelowIceMountainLI() {
        super(ScriptData.CURRENT_FREE_QUEST_NOT_FINISHED);
    }

    @Override
    public int handle() {
        switch (PlayerSettings.getBitValue(12063)) {
            case 0:
                if (!ScriptData.interactWithNPC(10638, WILLOW, "Talk-to", ScriptData.IN_DIALOGUE)) {
                    return ScriptData.returnMSFast();
                }
                break;
            case 10:
            case 15:
                switch (ScriptData.questOrder[ScriptData.questOrderI]) {
                    case 0:
                        switch (PlayerSettings.getBitValue(12064)) {
                            case 40:
                                ScriptData.questOrderI++;
                                return ScriptData.returnMSFast();
                            case 0:
                                if (!ScriptData.interactWithNPC(5209, CHARLIE_THE_TRAMP, "Talk-to", ScriptData.IN_DIALOGUE)) {
                                    return ScriptData.returnMSFast();
                                }
                                break;
                            case 2:
                                if (!ScriptData.interactWithNPC(2895, COOK, "Talk-to", ScriptData.IN_DIALOGUE)) {
                                    return ScriptData.returnMSFast();
                                }
                                break;
                            case 10:
                                if (Inventory.contains(25631)) { // Steak sandwich
                                    if (!ScriptData.interactWithNPC(10656, MARLEY, "Talk-to", ScriptData.IN_DIALOGUE)) {
                                        return ScriptData.returnMSFast();
                                    }
                                }
                                else if (ScriptData.SECURE_RANDOM.nextInt(2) == 1) {
                                    Inventory.combine(2142, 2309); // Cooked meat, Bread
                                }
                                else {
                                    Inventory.combine(2309, 2142);
                                }
                                break;
                        }
                        break;
                    case 1:
                        switch (PlayerSettings.getBitValue(12065)) {
                            case 40:
                                ScriptData.questOrderI++;
                                return ScriptData.returnMSFast();
                            case 0:
                            case 15:
                                if (!ScriptData.interactWithNPC(10657, CHECKAL, "Talk-to", ScriptData.IN_DIALOGUE)) {
                                    return ScriptData.returnMSFast();
                                }
                                break;
                            case 5:
                            case 10:
                                if (!ScriptData.interactWithNPC(10658, BARBARIAN_LONGHALL, "Talk-to", ScriptData.IN_DIALOGUE)) {
                                    return ScriptData.returnMSFast();
                                }
                                break;
                            case 20:
                                if (CHECKAL.contains(Players.getLocal())) {
                                    if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || ScriptData.currentNPC.getId() != 10657) {
                                        ScriptData.currentNPC = NPCs.closest(10657);
                                        return ScriptData.returnMSFast();
                                    }
                                    else if (ScriptData.currentNPC.tileDistance(Players.getLocal().getTile()) < 2) {
                                        if (Emotes.doEmote(Emote.FLEX)) {
                                            Sleep.sleepUntil(ScriptData.IN_DIALOGUE, ScriptData.SECURE_RANDOM.nextInt(5000 - 3000 + 1) + 3000, 300);
                                        }
                                    }
                                    else if (!ScriptData.walkToEntity(ScriptData.currentNPC)) {
                                        return ScriptData.returnMSFast();
                                    }
                                }
                                else if (!ScriptData.walkToArea(CHECKAL)) {
                                    return ScriptData.returnMSFast();
                                }
                                break;
                        }
                        break;
                    case 2:
                        if (PlayerSettings.getBitValue(12066) == 40) {
                            ScriptData.questOrderI++;
                            return ScriptData.returnMSFast();
                        }
                        else if (!ScriptData.interactWithNPC(10648, BURNTOF, "Talk-to", ScriptData.IN_DIALOGUE)) {
                            return ScriptData.returnMSFast();
                        }
                        break;
                }
                break;
            case 20:
            case 35:
                if (Client.isDynamicRegion()) {
                    if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || !ScriptData.currentNPC.getName().equals("Ancient Guardian")) {
                        ScriptData.currentNPC = NPCs.closest("Ancient Guardian");
                        return ScriptData.returnMSFast();
                    }
                    else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || !ScriptData.currentGameObject.getName().equals("Structural pillar")) {
                        List<GameObject> pillarOpts = GameObjects.all("Structural pillar");
                        if (!pillarOpts.isEmpty()) {
                            ScriptData.currentGameObject = pillarOpts.get(ScriptData.SECURE_RANDOM.nextInt(pillarOpts.size()));
                        }
                        return ScriptData.returnMSFast();
                    }
                    else if (ScriptData.currentGameObject.interact("Mine")) {
                        Sleep.sleepUntil(() -> ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || Skills.getBoostedLevel(Skill.HITPOINTS) <= Math.max(11, PlayerData.foodHP), ScriptData.SECURE_RANDOM.nextInt(40000 - 20000 + 1) + 20000, 300);
                        return ScriptData.returnMSFast();
                    }
                }
                else if (PlayerSettings.getBitValue(12063) == 20) {
                    if (!ScriptData.interactWithNPC(10638, WILLOW, "Talk-to", ScriptData.IN_DIALOGUE)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if (!RUINS_ENTRANCE.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(RUINS_ENTRANCE)) {
                        return ScriptData.returnMSFast();
                    }
                }
                else if ((ScriptData.currentGameObject = GameObjects.closest("Ruins Entrance")) != null) {
                    if (ScriptData.currentGameObject.interact("Enter")) {
                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE.or(Client::isDynamicRegion), ScriptData.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        return ScriptData.returnMSFast();
                    }
                }
                break;
        }

        return ScriptData.returnMSNormal();
    }

}
