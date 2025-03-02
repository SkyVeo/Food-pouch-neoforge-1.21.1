package com.skyveo.foodpouch.util;

import com.skyveo.foodpouch.item.custom.FoodPouchItem;
import com.skyveo.foodpouch.mixin.BundleContentsMutableAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.skyveo.foodpouch.mixin.BundleContentsInvoker.getWeight;
import static com.skyveo.foodpouch.util.FoodPouchProcess.*;

public class FoodPouchContentsMutable extends BundleContents.Mutable {
    protected final BundleContentsMutableAccessor accessor = (BundleContentsMutableAccessor) this;
    private final int maxSize;

    public static Optional<FoodPouchContentsMutable> of(ItemStack foodPouch) {
        Optional<BundleContents> content = getContentComponent(foodPouch);

        if (content.isEmpty() || !(foodPouch.getItem() instanceof FoodPouchItem foodPouchItem)) {
            return Optional.empty();
        }
        return Optional.of(new FoodPouchContentsMutable(content.get(), foodPouchItem.getMaxSize()));
    }

    public FoodPouchContentsMutable(BundleContents contents, int maxSize) {
        super(contents);
        this.maxSize = maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    protected void addWeight(Fraction occupancy) {
        accessor.setWeight(weight().add(occupancy));
    }

    protected void addItem(int index, ItemStack stack) {
        accessor.getItems().add(index, stack);
    }

    protected ItemStack removeItem(int index) {
        return accessor.getItems().remove(index);
    }

    public int getMaxAmountToAdd(ItemStack stack) {
        Fraction freeSpace = Fraction.ONE.subtract(bundleToFoodPouchWeight(weight(), getMaxSize()));
        Fraction occupancy = bundleToFoodPouchWeight(getWeight(stack), getMaxSize());

        return Math.max(freeSpace.divideBy(occupancy).intValue(), 0);
    }

    @Override
    public int tryInsert(@NotNull ItemStack stack) {
        if (!canBeAdded(stack)) {
            return 0;
        }

        int toAdd = Math.min(stack.getCount(), getMaxAmountToAdd(stack));
        if (toAdd == 0) {
            return 0;
        }

        addWeight(getWeight(stack).multiplyBy(Fraction.getFraction(toAdd, 1)));

        int existingStackIndex = accessor.invokeFindStackIndex(stack);
        if (existingStackIndex != -1) {
            ItemStack existingStack = removeItem(existingStackIndex);
            int newCount = existingStack.getCount() + toAdd;
            int maxStackSize = existingStack.getMaxStackSize();

            if (newCount > maxStackSize) {
                addItem(existingStackIndex, existingStack.copyWithCount(maxStackSize));
                addItem(0, existingStack.copyWithCount(newCount - maxStackSize));
            } else {
                addItem(0, existingStack.copyWithCount(newCount));
            }
            stack.shrink(toAdd);
        } else {
            addItem(0, stack.split(toAdd));
        }

        return toAdd;
    }

    @Override
    public int tryTransfer(Slot slot, @NotNull Player player) {
        return tryInsert(slot.getItem());
    }
}
