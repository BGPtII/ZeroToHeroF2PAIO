package nodes.progressiontasks.questing;

import framework.Node;
import framework.SCScript;
import data.global.ScriptData;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.map.Region;

/**
 * If mirror is facing a wardrobe, will prevent the swirl from moving over that specific mirror
 */
public class MisthalinMystery implements Node {

    private final Area STARTING_AREA = new Area(3208, 3158, 3249, 3137);
    private final Area FOUNTAIN = new Area(1616, 4821, 1625, 4814);
    private final Area RAIN_BARREL = new Area(1613, 4828, 1617, 4824);
    private final Area KNIFE_TABLE = new Area(1633, 4834, 1639, 4825);
    private final Area PINK_EMERALD_DOOR = new Area(1633, 4839, 1635, 4835);
    private final Area PAINTINGS = new Area(1628, 4834, 1632, 4825);
    private final Area DEMOLITION_CANDLES = new Area(1641, 4832, 1647, 4824);
    private final Area OBSERVE_TREE = new Area(1632, 4850, 1635, 4847);
    private final Area PIANO = new Area(1634, 4844, 1647, 4840);
    private final Area BANDOS_GODSWORD = new Area(1630, 4844, 1632, 4835);
    private final Area FIREPLACE = new Area(1641, 4839, 1647, 4833);
    private final Area BOSS = new Area(1619, 4834, 1627, 4825);
    private final Area MANDY_AREA = new Area(1632, 4819, 1642, 4809);
    private final Area MANOR = new Area(1611, 4852, 1658, 4800);

    private int wardrobeX;
    private final int[] WARDROBE_BOUNDS = new int[2];
    private boolean orientMirror(int x, int y) { // x, y = translation values
        Tile playerTile = Players.getLocal().getTile();
        Tile mirrorTile = ScriptData.currentNPC.getTile();
        Tile mirrorTileTranslated = mirrorTile.translate(x, y);
        Logger.log("mirrorTile: " + mirrorTile);
        Logger.log("mirrorTileTranslated: " + mirrorTileTranslated);
        Logger.log("playerTile: " + playerTile);
        if (playerTile.equals(mirrorTileTranslated)) {
            if (ScriptData.currentNPC.interact()) {
                Sleep.sleepUntil(() -> Players.getLocal().getAnimation() != -1, SCScript.SECURE_RANDOM.nextInt(5000 - 1000 + 1) + 1000);
                Sleep.sleepUntil(() -> Players.getLocal().getAnimation() == -1, SCScript.SECURE_RANDOM.nextInt(10000 - 5000 + 1) + 5000, 300);
                Logger.log("Pushed mirror");
                return true;
            }
        }
        else if (Walking.walkExact(mirrorTileTranslated)) {
            Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(mirrorTileTranslated), SCScript.SECURE_RANDOM.nextInt(10000 - 5000 + 1) + 5000, 300);
            Logger.log("Walked to mirrorTileTranslated: " + mirrorTileTranslated);
            return true;
        }
        return false;
    }

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

        if (Client.isInCutscene()) {
            Logger.log("isInCutscene");
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        if (FreeQuest.MISTHALIN_MYSTERY.isFinished()) {
            if (Widgets.isVisible(153, 16)) {
                if (ScriptData.closeQuestCompletionWidget()) {
                    return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
                }
            }
            else if (MANOR.contains(Players.getLocal())) {
                if ((ScriptData.currentGameObject = GameObjects.closest(30109)) != null) { // Rowboat
                    if (ScriptData.currentGameObject.interact("Board")) {
                        Sleep.sleepUntil(() -> !MANOR.contains(Players.getLocal()), SCScript.SECURE_RANDOM.nextInt(20000 - 10000 + 1) + 10000, 300);
                    }
                }
            }
            else {
                LocalPathFinder localPathFinder = LocalPathFinder.getLocalPathFinder();
                localPathFinder.removeBlacklistedTile(new Tile(1643, 4839, 0));
                localPathFinder.removeBlacklistedTile(new Tile(1643, 4840, 0));
                localPathFinder.removeBlacklistedTile(new Tile(1633, 4842, 0));
                localPathFinder.removeBlacklistedTile(new Tile(1634, 4842, 0));
                localPathFinder.removeObstacle(new PassableObstacle("Damaged wall", "Climb"));
                ScriptData.finishProgressionQuestingTask();
            }
            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
        }

        if (PlayerSettings.getBitValue(3468) > 0 && !Client.isDynamicRegion() && !MANOR.contains(Players.getLocal())) {
            if (!ScriptData.interactWithGameObjectSingle(30108, STARTING_AREA, "Board", () -> MANOR.contains(Players.getLocal()))) {
                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
            }
            return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
        }

        switch (PlayerSettings.getBitValue(3468)) {
            case 0:
                if (STARTING_AREA.contains(Players.getLocal())) {
                    if ((ScriptData.currentNPC = NPCs.closest(npc -> npc.isInteracting(Players.getLocal()) && npc.getName().equals("Giant rat") && npc.getAnimation() != -1)) != null) {
                        if (ScriptData.currentNPC.interact("Attack")) {
                            Sleep.sleepUntil(() -> ScriptData.currentNPC == null || !ScriptData.currentNPC.exists(), SCScript.SECURE_RANDOM.nextInt(240000 - 120000 + 1) + 120000, 300);
                        }
                    }
                    else if ((ScriptData.currentNPC = NPCs.closest(7428)) != null) { // Can't walk to tile Abigale is on, therefore can reach is always false
                        if (ScriptData.currentNPC.interact("Talk-to")) {
                            Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        }
                    }
                }
                else if (!ScriptData.walkToArea(STARTING_AREA)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 10:
            case 15:
            case 20:
            case 25:
                if (Inventory.contains(21052)) { // Manor key
                    if (!ScriptData.walkToArea(PINK_EMERALD_DOOR)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (!Inventory.contains(1925, 1929)) { // Bucket, Bucket of water
                    if (!ScriptData.interactWithGameObjectSingle(30147, FOUNTAIN, "Take", () -> Inventory.contains(1925))) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (!RAIN_BARREL.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(RAIN_BARREL)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if ((ScriptData.currentGameObject = GameObjects.closest(29649)) != null) {
                    if (ScriptData.currentGameObject.getName().startsWith("A")) { // A barrel of rainwater#Full
                        if (Inventory.isItemSelected()) {
                            if (ScriptData.currentGameObject.interact()) {
                                Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                            }
                        }
                        else if (Inventory.use(1925)) {
                            Sleep.sleepUntil(Inventory::isItemSelected, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                        }
                    }
                    else if (ScriptData.currentGameObject.interact("Search")) {
                        Sleep.sleepUntil(() -> Inventory.contains(21052), SCScript.SECURE_RANDOM.nextInt(10000 - 3000 + 1) + 3000, 300);
                    }
                }
                break;
            case 30:
            case 35:
                if (Inventory.contains(21056)) { // Notes
                    if (Inventory.interact(21056, "Read")) {
                        Sleep.sleepUntil(() -> Widgets.isVisible(70, 3), SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                    }
                }
                else if (!PINK_EMERALD_DOOR.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(PINK_EMERALD_DOOR)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if ((ScriptData.currentGameObject = GameObjects.closest(gO -> gO.getId() == 2266)) != null) { // Notes
                    if (ScriptData.currentGameObject.interact("Take")) {
                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                    }
                }
                else if ((ScriptData.currentGameObject = GameObjects.closest(gO -> gO.getId() == 30112)) != null) { // Pink door
                    if (ScriptData.currentGameObject.interact("Open")) {
                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                    }
                }
                break;
            case 40:
                if (Widgets.isVisible(70, 104)) {
                    if ((ScriptData.currentWidgetChild = Widgets.get(70, 104)) != null && ScriptData.currentWidgetChild.interact("Close")) {
                        Sleep.sleepUntil(() -> !Widgets.isVisible(70, 104), SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                    }
                }
                else if (!Inventory.contains(946)) { // Knife
                    if (!ScriptData.interactWithGameObjectSingle(30145, KNIFE_TABLE, "Take-knife", () -> Inventory.contains(946))) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (!PAINTINGS.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(PAINTINGS)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 29650) { // NE painting (Gloomy valley)
                    ScriptData.currentGameObject = GameObjects.closest(29650);
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else if (Inventory.isItemSelected()) {
                    if (ScriptData.currentGameObject.interact()) {
                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                    }
                }
                else {
                    Inventory.use(946); // Knife
                }
                break;
            case 45:
                if (Inventory.contains(21053)) { // Ruby key
                    if (!ScriptData.walkToArea(DEMOLITION_CANDLES)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (!PAINTINGS.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(PAINTINGS)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 29650) { // NE painting (Gloomy valley)
                    ScriptData.currentGameObject = GameObjects.closest(29650);
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else if (ScriptData.currentGameObject.hasAction("Search")) {
                    if (ScriptData.currentGameObject.interact("Search")) {
                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                    }
                }
                else if (Inventory.isItemSelected()) {
                    if (ScriptData.currentGameObject.interact()) {
                        Sleep.sleepUntil(Dialogues::inDialogue, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                    }
                }
                else {
                    Inventory.use(946);
                }
                break;
            case 50:
                if (!DEMOLITION_CANDLES.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(DEMOLITION_CANDLES)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (!Inventory.contains(590)) { // Tinderbox
                    if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 30146) { // Shelves (containing Tinderbox)
                        ScriptData.currentGameObject = GameObjects.closest(30146);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    else if (ScriptData.currentGameObject.interact("Take-tinderbox")) {
                        Sleep.sleepUntil(() -> Inventory.contains(590), SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                    }
                }
                else {
                    switch (ScriptData.questOrder[ScriptData.questOrderI]) {
                        case 0: // NW candle
                            if ((ScriptData.currentGameObject = GameObjects.closest(29652)) != null) {
                                if (ScriptData.currentGameObject.getName().equals("Candle")) {
                                    ScriptData.questOrderI++;
                                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                }
                                else if (Inventory.isItemSelected()) {
                                    if (ScriptData.currentGameObject.interact()) {
                                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                                    }
                                }
                                else {
                                    Inventory.use(590); // Tinderbox
                                }
                            }
                            break;
                        case 1: // SW Candle
                            if ((ScriptData.currentGameObject = GameObjects.closest(29655)) != null) {
                                if (ScriptData.currentGameObject.getName().equals("Candle")) {
                                    ScriptData.questOrderI++;
                                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                }
                                else if (Inventory.isItemSelected()) {
                                    if (ScriptData.currentGameObject.interact()) {
                                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                                    }
                                }
                                else {
                                    Inventory.use(590);
                                }
                            }
                            break;
                        case 2: // SE Candle
                            if ((ScriptData.currentGameObject = GameObjects.closest(29654)) != null) {
                                if (ScriptData.currentGameObject.getName().equals("Candle")) {
                                    ScriptData.questOrderI++;
                                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                }
                                else if (Inventory.isItemSelected()) {
                                    if (ScriptData.currentGameObject.interact()) {
                                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                                    }
                                }
                                else {
                                    Inventory.use(590);
                                }
                            }
                            break;
                        case 3: // NE Castle
                            if ((ScriptData.currentGameObject = GameObjects.closest(29653)) != null) {
                                if (ScriptData.currentGameObject.getName().equals("Candle")) {
                                    ScriptData.questOrderI++;
                                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                }
                                else if (Inventory.isItemSelected()) {
                                    if (ScriptData.currentGameObject.interact()) {
                                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                                    }
                                }
                                else {
                                    Inventory.use(590);
                                }
                            }
                            break;
                    }
                }
                break;
            case 55:
                if (!DEMOLITION_CANDLES.contains(Players.getLocal())) {
                    if (!ScriptData.walkToArea(DEMOLITION_CANDLES)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (ScriptData.currentGameObject == null || !ScriptData.currentGameObject.exists() || ScriptData.currentGameObject.getId() != 29651) { // NE painting (Gloomy valley)
                    ScriptData.currentGameObject = GameObjects.closest(29651);
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                else if (Inventory.isItemSelected()) {
                    if (ScriptData.currentGameObject.interact()) {
                        Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                    }
                }
                else {
                    Inventory.use(590);
                }
                break;
            case 60:
                if (!ScriptData.walkToArea(KNIFE_TABLE)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 65:
            case 70:
                if (Inventory.contains(21057)) {
                    if (Inventory.interact(21057, "Read")) {
                        Sleep.sleepUntil(() -> Widgets.isVisible(70, 3), SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                    }
                }
                else if (OBSERVE_TREE.contains(Players.getLocal())) {
                    if ((ScriptData.currentGameObject = GameObjects.closest(2267)) != null) {
                        if (ScriptData.currentGameObject.interact("Take")) {
                            Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                        }
                    }
                }
                else if (DEMOLITION_CANDLES.contains(Players.getLocal())) {
                    if ((ScriptData.currentGameObject = GameObjects.closest("Damaged wall")) != null) {
                        if (ScriptData.currentGameObject.interact("Climb")) {
                            Sleep.sleepUntil(() -> !DEMOLITION_CANDLES.contains(Players.getLocal()), SCScript.SECURE_RANDOM.nextInt(15000 - 5000 + 1) + 5000, 300);
                        }
                    }
                }
                else if (!ScriptData.walkToArea(OBSERVE_TREE)) {
                    Logger.log(3);
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 75:
            case 80:
                if (Widgets.isVisible(70, 104)) {
                    if ((ScriptData.currentWidgetChild = Widgets.get(70, 104)) != null && ScriptData.currentWidgetChild.interact("Close")) {
                        Sleep.sleepUntil(() -> !Widgets.isVisible(70, 104), SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                    }
                }
                else if (Inventory.contains(21054) && !BANDOS_GODSWORD.contains(Players.getLocal())) { // Emerald key
                    if (!ScriptData.walkToArea(BANDOS_GODSWORD)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (PIANO.contains(Players.getLocal())) {
                    int pianoAttempts = PlayerSettings.getBitValue(4049);
                    if ((pianoAttempts == 0 || pianoAttempts == 3) && Widgets.isVisible(554, 21)) { // 2nd key (D)
                        if ((ScriptData.currentWidgetChild = Widgets.get(554, 21)) != null && ScriptData.currentWidgetChild.interact()) {
                            Sleep.sleepUntil(() -> pianoAttempts != PlayerSettings.getBitValue(4049), SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                        }
                    }
                    else if (pianoAttempts == 1 && Widgets.isVisible(554, 22)) { // 3rd key (E)
                        if ((ScriptData.currentWidgetChild = Widgets.get(554, 22)) != null && ScriptData.currentWidgetChild.interact()) {
                            Sleep.sleepUntil(() -> pianoAttempts != PlayerSettings.getBitValue(4049), SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                        }
                    }
                    else if (pianoAttempts == 2 && Widgets.isVisible(554, 25)) { // 6nd key (A)
                        if ((ScriptData.currentWidgetChild = Widgets.get(554, 25)) != null && ScriptData.currentWidgetChild.interact()) {
                            Sleep.sleepUntil(() -> pianoAttempts != PlayerSettings.getBitValue(4049), SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                        }
                    }
                    else if ((ScriptData.currentGameObject = GameObjects.closest(29658)) != null) {
                        if (ScriptData.currentGameObject.hasAction("Search")) {
                            if (ScriptData.currentGameObject.interact("Search")) {
                                Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                            }
                        }
                        else if (ScriptData.currentGameObject.interact("Play")) {
                            Sleep.sleepUntil(() -> Widgets.isVisible(554, 21), SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                        }
                    }
                }
                else if (!ScriptData.walkToArea(PIANO)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 85:
            case 90:
                if (Inventory.contains(21058)) { // Notes
                    if (Inventory.interact(21058, "Read")) {
                        Sleep.sleepUntil(() -> Widgets.isVisible(70, 3), SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                    }
                }
                else if (BANDOS_GODSWORD.contains(Players.getLocal())) {
                    if ((ScriptData.currentGameObject = GameObjects.closest(gO -> gO.getId() == 29648 && gO.hasAction("Take"))) != null) {
                        if (ScriptData.currentGameObject.interact("Take")) {
                            Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                        }
                    }
                    else if ((ScriptData.currentGameObject = GameObjects.closest(30118)) != null) {
                        if (ScriptData.currentGameObject.interact("Open")) {
                            Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                        }
                    }
                }
                else if (!ScriptData.walkToArea(BANDOS_GODSWORD)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 95:
            case 100:
            case 105:
                if (Inventory.contains(21055)) { // Sapphire key
                    if (!ScriptData.walkToArea(BOSS)) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (!Inventory.contains(946)) { // Knife
                    if (!ScriptData.interactWithGameObjectSingle(30145, KNIFE_TABLE, "Take-knife", () -> Inventory.contains(946))) {
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                }
                else if (FIREPLACE.contains(Players.getLocal())) {
                    if (Widgets.isVisible(555, 1, 1)) {
                        int attempts = PlayerSettings.getBitValue(4050);
                        switch (attempts) {
                            case 0: // Sapphire
                                if ((ScriptData.currentWidgetChild = Widgets.get(555, 19)) != null && ScriptData.currentWidgetChild.interact()) {
                                    Sleep.sleepUntil(() -> PlayerSettings.getBitValue(4050) != attempts, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                                }
                                break;
                            case 1: //Diamond
                                if ((ScriptData.currentWidgetChild = Widgets.get(555, 3)) != null && ScriptData.currentWidgetChild.interact()) {
                                    Sleep.sleepUntil(() -> PlayerSettings.getBitValue(4050) != attempts, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                                }
                                break;
                            case 2: // Zenyte
                                if ((ScriptData.currentWidgetChild = Widgets.get(555, 11)) != null && ScriptData.currentWidgetChild.interact()) {
                                    Sleep.sleepUntil(() -> PlayerSettings.getBitValue(4050) != attempts, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                                }
                                break;
                            case 3: // Emerald
                                if ((ScriptData.currentWidgetChild = Widgets.get(555, 23)) != null && ScriptData.currentWidgetChild.interact()) {
                                    Sleep.sleepUntil(() -> PlayerSettings.getBitValue(4050) != attempts, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                                }
                                break;
                            case 4: // Onyx
                                if ((ScriptData.currentWidgetChild = Widgets.get(555, 7)) != null && ScriptData.currentWidgetChild.interact()) {
                                    Sleep.sleepUntil(() -> PlayerSettings.getBitValue(4050) != attempts, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                                }
                                break;
                            case 5: // Ruby
                                if ((ScriptData.currentWidgetChild = Widgets.get(555, 15)) != null && ScriptData.currentWidgetChild.interact()) {
                                    Sleep.sleepUntil(() -> PlayerSettings.getBitValue(4050) != attempts, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                                }
                                break;
                        }
                    }
                    else if ((ScriptData.currentGameObject = GameObjects.closest(29659)) != null) {
                        if (ScriptData.currentGameObject.hasAction("Search")) {
                            if (ScriptData.currentGameObject.interact("Search")) {
                                Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                            }
                        }
                        else if (Inventory.isItemSelected()) {
                            if (ScriptData.currentGameObject.interact()) {
                                Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                            }
                        }
                        else {
                            Inventory.use(946);
                        }
                    }
                }
                else if (!ScriptData.walkToArea(FIREPLACE)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 110:
            case 111: // Mirror orientation: 0 - S, 1536 - E, 512 - W, 1024 - N; mirror limits (non-instanced): x (1622-1624), y (4828-4831)
                if (BOSS.contains(Region.fromInstance(Players.getLocal().getTile()))) {
                    if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || ScriptData.currentNPC.getId() != 7436) { // Mirror
                        ScriptData.currentNPC = NPCs.closest(7436);
                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                    }
                    GraphicsObject swirl;
                    if ((swirl = GraphicsObjects.closest(483)) != null && (ScriptData.currentTile == null || !ScriptData.currentTile.equals(swirl.getTile()))) {
                        ScriptData.currentTile = swirl.getTile();
                        Logger.log("swirlTile (currentTile): " + ScriptData.currentTile);
                        wardrobeX = Region.fromInstance(swirl.getTile()).getX(); // 1622,4834 - North, 1627,4831 - East, 1624,4825 - South, 1619,4828 - West
                        WARDROBE_BOUNDS[0] = ScriptData.currentTile.getX();
                        WARDROBE_BOUNDS[1] = ScriptData.currentTile.getY();
                        Logger.log("wardrobeX:" + wardrobeX);
                    }
                    else if (ScriptData.currentTile != null) {
                        if (wardrobeX % 2 != 0) { // E/W
                            if (ScriptData.currentNPC.getY() > WARDROBE_BOUNDS[1]) { // Move mirror South
                                if (!orientMirror(0, 1)) {
                                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                }
                            }
                            else if (ScriptData.currentNPC.getY() < WARDROBE_BOUNDS[1]) { // Move mirror North
                                if (!orientMirror(0, -1)) {
                                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                }
                            }
                            else if (wardrobeX == 1627) { // East wardrobe (orientation: 1024 = North, 1536 = East, 0 = South, 512 = West)
                                if (ScriptData.currentNPC.getOrientation() != 1536) {
                                    if (ScriptData.currentNPC.getX() == WARDROBE_BOUNDS[0] - 3) { // Need to push mirror West (at xEastBoundary)
                                        if (!orientMirror(1, 0)) {
                                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                        }
                                    }
                                    else if (!orientMirror(-1, 0)) { // Push mirror East
                                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                    }
                                }
                            }
                            else if (ScriptData.currentNPC.getOrientation() != 512) {
                                if (ScriptData.currentNPC.getX() == WARDROBE_BOUNDS[0] + 3) { // Need to push mirror East (at xWestBoundary)
                                    if (!orientMirror(-1, 0)) {
                                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                    }
                                }
                                else if (!orientMirror(1, 0)) { // Push mirror West
                                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                }
                            }
                        } // N/S passed this line
                        else if (ScriptData.currentNPC.getX() < WARDROBE_BOUNDS[0]) {
                            if (!orientMirror(-1, 0)) {
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                        }
                        else if (ScriptData.currentNPC.getX() > WARDROBE_BOUNDS[0]) {
                            if (!orientMirror(1, 0)) {
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                        }
                        else if (wardrobeX == 1622) { // North
                            if (ScriptData.currentNPC.getOrientation() != 1024) {
                                if (ScriptData.currentNPC.getY() == WARDROBE_BOUNDS[1] - 3) { // Need to push mirror South (at yNorthBoundary)
                                    if (!orientMirror(0, 1)) {
                                        return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                    }
                                }
                                else if (!orientMirror(0, -1)) { // Push mirror North
                                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                }
                            }
                        }
                        else if (ScriptData.currentNPC.getOrientation() != 0) { // South
                            if (ScriptData.currentNPC.getY() == WARDROBE_BOUNDS[1] + 3) { // Need to push mirror North (at ySouthBoundary)
                                if (!orientMirror(0, -1)) {
                                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                                }
                            }
                            else if (!orientMirror(0, 1)) { // Push mirror South
                                return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                            }
                        }
                    }
                }
                else if (!ScriptData.walkToArea(BOSS)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 120:
                if (BOSS.contains(Region.fromInstance(Players.getLocal().getTile()))) {
                    if (Equipment.contains(21059)) { // Killer's knife
                        if (ScriptData.currentNPC == null || !ScriptData.currentNPC.exists() || ScriptData.currentNPC.getId() != 7634) { // Abigale#Killer
                            ScriptData.currentNPC = NPCs.closest(7634);
                            return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                        }
                        else if (ScriptData.currentNPC.interact("Fight")) {
                            Sleep.sleepUntil(ScriptData.IN_DIALOGUE, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 300);
                        }
                    }
                    else if (Inventory.contains(21059)) {
                        if (Inventory.interact(21059, "Wield")) {
                            Sleep.sleepUntil(() -> Equipment.contains(21059), SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                        }
                    }
                    else if ((ScriptData.currentGroundItem = GroundItems.closest("Killer's knife")) != null) {
                        if (ScriptData.currentGroundItem.interact("Take")) {
                            Sleep.sleepUntil(() -> Inventory.contains("Killer's knife"), SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                        }
                    }
                }
                else if (!ScriptData.walkToArea(BOSS)) {
                    return SCScript.SECURE_RANDOM.nextInt(300 - 100 + 1) + 100;
                }
                break;
            case 125:
                if ((ScriptData.currentGameObject = GameObjects.closest("Door")) != null) {
                    if (ScriptData.currentGameObject.interact("Open")) {
                        Sleep.sleepUntil(() -> NPCs.closest("Hewey") == null, SCScript.SECURE_RANDOM.nextInt(20000 - 5000 + 1) + 5000, 200);
                    }
                }
                break;
            case 130:
                ScriptData.interactWithNPC(7434, MANDY_AREA, "Talk-to", ScriptData.IN_DIALOGUE);
                break;
        }

        return 0;
    }
}
