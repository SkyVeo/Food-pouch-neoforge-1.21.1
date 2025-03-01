package com.skyveo.foodpouch.item;

import net.minecraft.world.item.Item;

public enum FoodPouchMaterials implements FoodPouchMaterial {
    LEATHER(Item.DEFAULT_MAX_STACK_SIZE),
    IRON(Item.DEFAULT_MAX_STACK_SIZE * 2),
    GOLD(Item.DEFAULT_MAX_STACK_SIZE * 3),
    DIAMOND(Item.DEFAULT_MAX_STACK_SIZE * 4),
    NETHERITE(Item.DEFAULT_MAX_STACK_SIZE * 5) {
        @Override
        public void configureProperties(Item.Properties properties) {
            properties.fireResistant();
        }
    };

    private final int size;

    FoodPouchMaterials(int size) {
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }
}
