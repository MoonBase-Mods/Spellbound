package com.ombremoon.spellbound.common.world.recipe;

import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class TransfigurationWrapper implements RecipeInput {
    private final TransfigurationMultiblock multiblock;
//    private final int ingredientCount;
    private final List<ItemStack> items = new ObjectArrayList<>();

    public TransfigurationWrapper(TransfigurationMultiblock multiblock) {
        this.multiblock = multiblock;
    }

    @Override
    public ItemStack getItem(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
