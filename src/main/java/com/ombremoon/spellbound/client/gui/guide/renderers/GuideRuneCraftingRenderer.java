package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.ombremoon.spellbound.client.gui.guide.elements.RuneCraftingElement;
import com.ombremoon.spellbound.client.gui.guide.elements.special.GuideGhostItem;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBRecipes;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Optional;

public class GuideRuneCraftingRenderer implements IPageElementRenderer<RuneCraftingElement> {

    @Override
    public void render(RuneCraftingElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        Recipe<?> recipe = this.getRecipe(element);
        if (recipe == null)
            return;

        int itemOffset = element.leftPage() ? 33 : 205;
        int altarX = leftPos + itemOffset;
        int altarY = topPos + 95;
        ItemStack item = SBBlocks.DARK_ALTAR.get().asItem().getDefaultInstance();
        RenderUtil.renderItem(graphics, item, altarX, altarY, 2F);

        var ingredients = recipe.getIngredients();
        for (int i = 0; i < Math.min(ingredients.size(), 4); i++) {
            Ingredient ingredient = ingredients.get(i);
            ItemStack stack = new GuideGhostItem(ingredient, 30, 30).getItem(tickCount);

            int angle = i * 90;
            float f = tickCount + partialTick;
            double radians = Math.toRadians(angle + f * 0.9);
            int offsetX = (int) Math.round(Math.cos(radians) * 45) + 19;
            int offsetY = (int) Math.round(Math.sin(radians) * 45) + 15;

            RenderUtil.renderItem(graphics, stack, altarX + offsetX, altarY + offsetY, 1F, 200);
        }
    }

    private Recipe<?> getRecipe(RuneCraftingElement element) {
        RecipeManager manager = Minecraft.getInstance().player.connection.getRecipeManager();
        Optional<RecipeHolder<?>> holder = manager.byKey(element.recipe());
        if (holder.isEmpty() || holder.get().value().getType() != SBRecipes.RUNE_RECIPE.get())
            return null;

        return holder.get().value();
    }
}
