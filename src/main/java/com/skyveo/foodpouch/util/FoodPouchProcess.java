package com.skyveo.foodpouch.util;

import com.skyveo.foodpouch.item.custom.FoodPouchItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;

import java.util.Optional;

public class FoodPouchProcess {
    public static Optional<BundleContents> getContentComponent(ItemStack foodPouch) {
        return Optional.ofNullable(foodPouch.get(DataComponents.BUNDLE_CONTENTS));
    }

    public static BundleContents getOrDefaultContentComponent(ItemStack foodPouch) {
        return foodPouch.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
    }

    public static Fraction bundleToFoodPouchWeight(Fraction bundleOccupancy, int maxSize) {
        int itemOccupancy = Mth.mulAndTruncate(bundleOccupancy, Item.DEFAULT_MAX_STACK_SIZE);
        return Fraction.getFraction(itemOccupancy, maxSize);
    }

    public static boolean canBeAdded(ItemStack stack) {
        Item item = stack.getItem();
        return item.canFitInsideContainerItems()
                && !(item instanceof FoodPouchItem)
                && stack.has(DataComponents.FOOD);
    }

    public static Optional<FoodPouchItem> getFoodPouchItem(ItemStack stack) {
        return stack.getItem() instanceof FoodPouchItem foodPouchItem ? Optional.of(foodPouchItem) : Optional.empty();
    }
}
