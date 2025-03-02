package com.skyveo.foodpouch.item;

import com.skyveo.foodpouch.FoodPouch;
import com.skyveo.foodpouch.item.custom.FoodPouchItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FoodPouch.MOD_ID);

    public static final DeferredItem<Item> FOOD_POUCH = registerFoodPouch("food_pouch", FoodPouchMaterials.LEATHER);
    public static final DeferredItem<Item> IRON_FOOD_POUCH = registerFoodPouch("iron_food_pouch", FoodPouchMaterials.IRON);
    public static final DeferredItem<Item> GOLDEN_FOOD_POUCH = registerFoodPouch("golden_food_pouch", FoodPouchMaterials.GOLD);
    public static final DeferredItem<Item> DIAMOND_FOOD_POUCH = registerFoodPouch("diamond_food_pouch", FoodPouchMaterials.DIAMOND);
    public static final DeferredItem<Item> NETHERITE_FOOD_POUCH = registerFoodPouch("netherite_food_pouch", FoodPouchMaterials.NETHERITE);

    public static DeferredItem<Item> registerFoodPouch(String name, FoodPouchMaterial material) {
        return ITEMS.register(name, () -> new FoodPouchItem(material));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
