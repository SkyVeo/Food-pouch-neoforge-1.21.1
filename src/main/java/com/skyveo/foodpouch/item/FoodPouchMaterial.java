package com.skyveo.foodpouch.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BundleContents;

public interface FoodPouchMaterial {
    int getSize();

    default Item.Properties getProperties() {
        Item.Properties properties = new Item.Properties()
                .stacksTo(1)
                .component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);

        configureProperties(properties);
        return properties;
    }

    default void configureProperties(Item.Properties properties) {
        // Default does nothing, but can be overridden
    }
}
