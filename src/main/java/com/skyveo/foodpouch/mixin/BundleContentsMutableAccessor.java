package com.skyveo.foodpouch.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(BundleContents.Mutable.class)
public interface BundleContentsMutableAccessor {
    @Accessor("weight")
    void setWeight(Fraction occupancy);

    @Accessor
    List<ItemStack> getItems();

    @Invoker("findStackIndex")
    int invokeFindStackIndex(ItemStack stack);
}
