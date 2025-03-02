package com.skyveo.foodpouch.mixin;

import com.skyveo.foodpouch.item.custom.FoodPouchItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    protected abstract void triggerItemUseEffects(ItemStack stack, int particleCount);

    @Inject(method = "triggerItemUseEffects", at = @At("HEAD"), cancellable = true)
    protected void spawnFoodPouchFirstFoodParticles(ItemStack stack, int amount, CallbackInfo info) {
        if (stack.getItem() instanceof FoodPouchItem foodPouch) {
            this.triggerItemUseEffects(foodPouch.getFirstFood(stack), amount);
            info.cancel();
        }
    }
}
