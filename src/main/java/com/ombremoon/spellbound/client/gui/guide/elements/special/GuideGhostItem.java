package com.ombremoon.spellbound.client.gui.guide.elements.special;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class GuideGhostItem {
    private final Ingredient ingredient;
    private final int x;
    private final int y;

    public GuideGhostItem(Ingredient ingredient, int x, int y) {
        this.ingredient = ingredient;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ItemStack getItem(int tickCount, float swapRate) {
        ItemStack[] itemstack = this.ingredient.getItems();
        return itemstack.length == 0 ? ItemStack.EMPTY : itemstack[Mth.floor(tickCount / swapRate) % itemstack.length];
    }

    public ItemStack getItem(int tickCount) {
        return this.getItem(tickCount, 60.0F);
    }
}
