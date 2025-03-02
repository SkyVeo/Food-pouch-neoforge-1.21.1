package com.skyveo.foodpouch.item.custom;

import com.skyveo.foodpouch.item.FoodPouchMaterial;
import com.skyveo.foodpouch.mixin.BundleItemInvoker;
import com.skyveo.foodpouch.util.FoodPouchContentsMutable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.skyveo.foodpouch.util.FoodPouchProcess.*;

public class FoodPouchItem extends BundleItem {
    private final int maxSize;

    public FoodPouchItem(FoodPouchMaterial material) {
        super(material.getProperties());
        this.maxSize = material.getSize();
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public ItemStack getFirstFood(ItemStack foodPouch) {
        BundleContents component = getOrDefaultContentComponent(foodPouch);
        return component.isEmpty() ? ItemStack.EMPTY : component.getItemUnsafe(0);
    }

    protected void updateFoodPouchContents(ItemStack foodPouch) {
        ItemStack stack = getFirstFood(foodPouch);
        if (stack.isEmpty()) {
            foodPouch.remove(DataComponents.FOOD);
        } else {
            foodPouch.set(DataComponents.FOOD, stack.get(DataComponents.FOOD));
        }
    }

    protected boolean insertFood(ItemStack foodPouch, ItemStack food, Slot slot, ClickAction action, Player player, @Nullable SlotAccess cursorStackReference) {
        if (action != ClickAction.SECONDARY || (cursorStackReference != null && !slot.allowModification(player))) {
            return false;
        }

        Optional<FoodPouchContentsMutable> optionalBuilder = FoodPouchContentsMutable.of(foodPouch);

        return optionalBuilder.map(builder -> {
            BundleItemInvoker invoker = (BundleItemInvoker) this;

            if (food.isEmpty()) {
                ItemStack removedItem = builder.removeOne();
                if (removedItem == null) {
                    return false;
                }

                invoker.invokePlayRemoveOneSound(player);

                if (cursorStackReference != null) {
                    cursorStackReference.set(removedItem);
                } else {
                    ItemStack leftovers = slot.safeInsert(removedItem);
                    builder.tryInsert(leftovers);
                }
            } else {
                if (builder.tryInsert(food) == 0) {
                    return false;
                }
                invoker.invokePlayInsertSound(player);
            }

            foodPouch.set(DataComponents.BUNDLE_CONTENTS, builder.toImmutable());
            updateFoodPouchContents(foodPouch);

            return true;
        }).orElse(false);
    }

    @Override
    public boolean overrideStackedOnOther(@NotNull ItemStack stack, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player) {
        return insertFood(stack, slot.getItem(), slot, action, player, null);
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        return insertFood(stack, other, slot, action, player, access);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        ItemStack food = getFirstFood(stack);
        return food.getItem().getUseDuration(food, entity);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        ItemStack food = getFirstFood(stack);
        return food.getItem().getUseAnimation(food);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack foodPouch = player.getItemInHand(usedHand);
        return getFirstFood(foodPouch).getItem().use(level, player, usedHand);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        Optional<FoodPouchContentsMutable> optionalBuilder = FoodPouchContentsMutable.of(stack);

        return optionalBuilder.map(builder -> {
            ItemStack food = builder.removeOne();
            if (food == null) {
                return stack;
            }

            ItemStack leftovers = food.getItem().finishUsingItem(food, level, livingEntity);
            int i = builder.tryInsert(leftovers);
            if (i == 0 && livingEntity instanceof Player playerEntity && !playerEntity.isCreative()) {
                if (!playerEntity.getInventory().add(leftovers)) {
                    playerEntity.drop(leftovers, false);
                }
            }

            stack.set(DataComponents.BUNDLE_CONTENTS, builder.toImmutable());
            updateFoodPouchContents(stack);

            return stack;
        }).orElse(stack);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        BundleContents content = getOrDefaultContentComponent(stack);
        Fraction weight = bundleToFoodPouchWeight(content.weight(), getMaxSize());

        return Math.min(1 + Mth.mulAndTruncate(weight, MAX_BAR_WIDTH - 1), MAX_BAR_WIDTH);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Optional<BundleContents> content = getContentComponent(stack);
        if (content.isPresent()) {
            Fraction weight = bundleToFoodPouchWeight(content.get().weight(), this.maxSize);
            tooltipComponents.add(Component.translatable(
                    "item.minecraft.bundle.fullness",
                    weight.getNumerator(),
                    weight.getDenominator()
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
