package com.skyveo.foodpouch.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BundleItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BundleItem.class)
public interface BundleItemInvoker {
    @Invoker("playInsertSound")
    void invokePlayInsertSound(Entity entity);

    @Invoker("playRemoveOneSound")
    void invokePlayRemoveOneSound(Entity entity);
}
