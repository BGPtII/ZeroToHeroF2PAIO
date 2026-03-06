package loopinterceptors;

import data.global.PlayerData;
import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.wrappers.items.Item;

public class EatChosenFoodLI extends LoopInterceptor {

    public EatChosenFoodLI() {
        super(() -> Inventory.contains(PlayerData.food) && Skills.getBoostedLevel(Skill.HITPOINTS) <= PlayerData.eatFoodHPTrs);
    }

    @Override
    public int handle() {
        int[] foodSlots = new int[28];
        byte foodSlotsSize = 0;
        for (Item item : Inventory.toArray()) {
            if (item != null && item.getId() == PlayerData.food) {
                foodSlots[foodSlotsSize++] = item.getSlot();
            }
        }
        if (Inventory.slotInteract(foodSlots[ScriptData.SECURE_RANDOM.nextInt(foodSlotsSize)], "Eat")) {
            PlayerData.determineEatFoodHPTrs();
            return ScriptData.returnMSNormal();
        }
        return ScriptData.returnMSFast();
    }

}
