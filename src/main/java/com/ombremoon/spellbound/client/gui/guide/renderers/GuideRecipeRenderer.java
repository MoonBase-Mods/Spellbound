package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.spellbound.client.gui.guide.elements.GuideRecipeElement;
import com.ombremoon.spellbound.client.gui.guide.elements.special.GuideGhostItem;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuideRecipeRenderer implements IPageElementRenderer<GuideRecipeElement> {

    @Override
    public void render(GuideRecipeElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        RecipeManager manager = Minecraft.getInstance().player.connection.getRecipeManager();
        Optional<RecipeHolder<?>> recipeOpt = manager.byKey(element.recipeLoc());
        if (recipeOpt.isEmpty()) return;

        RecipeHolder<?> recipeHolder = recipeOpt.get();
        Recipe<?> recipe = recipeHolder.value();
        if (recipe.getType() != RecipeType.CRAFTING) return;

        if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
            List<GuideGhostItem> ghostRecipe = new ArrayList<>();

            boolean largeGrid = (recipe instanceof ShapedRecipe shapedRecipe && (shapedRecipe.getWidth() > 2 || shapedRecipe.getHeight()> 2)) || recipe instanceof ShapelessRecipe shapelessRecipe && shapelessRecipe.getIngredients().size() > 4;
            if (largeGrid) {
                graphics.blit(
                        CommonClass.customLocation("textures/gui/books/crafting_grids/large/" + element.gridName() + ".png"),
                        leftPos + element.position().xOffset(),
                        topPos + element.position().yOffset(),
                        0,
                        0,
                        (int) (81F * element.scale()),
                        (int) (82F * element.scale()),
                        (int) (81 * element.scale()),
                        (int) (82 * element.scale()));

                if (!isVisible(element.extras().scrap())) return;
                for (int i = 1; i <= recipe.getIngredients().size(); i++) {
                    int slotXOffset = (i-1)%3 * (int) ( 23 * element.scale());
                    int slotYOffset = 1;
                    if (i > 3) slotYOffset += (int) (23 * element.scale());
                    if (i > 6) slotYOffset += (int) (23 * element.scale());

                    ghostRecipe.add(new GuideGhostItem(recipe.getIngredients().get(i-1), slotXOffset, slotYOffset));
                }
            }
            else {
                graphics.blit(
                        CommonClass.customLocation("textures/gui/books/crafting_grids/medium/" + element.gridName() + ".png"),
                        leftPos + element.position().xOffset(),
                        topPos + element.position().yOffset(),
                        0,
                        0,
                        (int) (60 * element.scale()),
                        (int) (58 * element.scale()),
                        (int) (60 * element.scale()),
                        (int) (58 * element.scale()));

                if (!isVisible(element.extras().scrap())) return;
                for (int i = 1; i <= recipe.getIngredients().size(); i++) {
                    int slotXOffset = (i+1)%2 * ( (int) (23 * element.scale()));
                    int slotYOffset = i > 2 ? ( int)(23 * element.scale()) : 0;

                    ghostRecipe.add(new GuideGhostItem(recipe.getIngredients().get(i-1), slotXOffset, slotYOffset));
                }
            }

            renderRecipe(graphics, leftPos + element.position().xOffset(), topPos + element.position().yOffset(), ghostRecipe, element.scale(), tickCount);
        }
    }

    private void renderRecipe(GuiGraphics guiGraphics, int leftPos, int topPos, List<GuideGhostItem> recipe, float scale, int tickCount) {

        for (GuideGhostItem ghostItem : recipe) {
            int j = ghostItem.getX() + leftPos;
            int k = ghostItem.getY() + topPos;

            ItemStack itemstack = ghostItem.getItem(tickCount);
            renderItem(guiGraphics, itemstack, j, k, scale);
        }

    }

    private boolean isLargeGrid(Recipe<?> recipe) {
        return (recipe instanceof ShapedRecipe shapedRecipe && (shapedRecipe.getWidth() > 2 || shapedRecipe.getHeight() > 2))
                || recipe instanceof ShapelessRecipe shapelessRecipe && shapelessRecipe.getIngredients().size() > 4;
    }

    private Optional<Recipe<?>> getRecipe(GuideRecipeElement element) {
        RecipeManager manager = Minecraft.getInstance().player.connection.getRecipeManager();
        Optional<RecipeHolder<?>> holder = manager.byKey(element.recipeLoc());
        if (holder.isEmpty() || holder.get().value().getType() != RecipeType.CRAFTING) return Optional.empty();
        return Optional.of(holder.get().value());
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY, int leftPos, int topPos, GuideRecipeElement element) {
        Optional<Recipe<?>> recipeOpt = getRecipe(element);
        if (recipeOpt.isEmpty()) return false;

        Recipe<?> recipe = recipeOpt.get();
        boolean large = isLargeGrid(recipe);
        int gridW = large ? (int) (81F * element.scale()) : (int) (60 * element.scale());
        int gridH = large ? (int) (82F * element.scale()) : (int) (58 * element.scale());
        int x = leftPos + element.position().xOffset();
        int y = topPos + element.position().yOffset();
        return mouseX >= x && mouseX <= x + gridW && mouseY >= y && mouseY <= y + gridH;
    }

    @Override
    public void handleHover(GuideRecipeElement element, GuiGraphics guiGraphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        if (!isVisible(element.extras().scrap())) return;

        Optional<Recipe<?>> recipeOpt = getRecipe(element);
        if (recipeOpt.isEmpty()) return;

        Recipe<?> recipe = recipeOpt.get();
        boolean large = isLargeGrid(recipe);
        int slotSize = (int) (23 * element.scale());

        int relX = mouseX - leftPos - element.position().xOffset();
        int relY = mouseY - topPos - element.position().yOffset();

        int cols = large ? 3 : 2;
        int col = Math.min(relX / slotSize, cols - 1);
        int row = Math.min(relY / slotSize, (large ? 3 : 2) - 1);

        int index;
        if (large) {
            index = row * 3 + col;
        } else {
            index = row * 2 + col;
        }

        List<Ingredient> ingredients = recipe.getIngredients();
        if (index < 0 || index >= ingredients.size()) return;

        Ingredient ingredient = ingredients.get(index);
        ItemStack[] items = ingredient.getItems();
        if (items.length == 0) return;

        ItemStack stack = items[Mth.floor(ElementRenderDispatcher.getTickCount() / 60f) % items.length];
        guiGraphics.renderTooltip(Minecraft.getInstance().font,
                stack.getTooltipLines(Item.TooltipContext.of(Minecraft.getInstance().level), Minecraft.getInstance().player, TooltipFlag.NORMAL),
                Optional.empty(),
                mouseX,
                mouseY);
    }

    public static void renderItem(GuiGraphics graphics, ItemStack stack, int x, int y, float scale) {
        if (!stack.isEmpty()) {
            Minecraft minecraft = Minecraft.getInstance();
            BakedModel bakedmodel = minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 0);
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate((x + (18*scale)), (y + (16*scale)), (float)(150));

            try {
                float size = 16.0F * 1.2F * scale;
                pose.scale(size, -size, size);
                boolean flag = !bakedmodel.usesBlockLight();
                if (flag) {
                    Lighting.setupForFlatItems();
                }

                minecraft.getItemRenderer().render(stack, ItemDisplayContext.GUI, false, pose, graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
                graphics.flush();
                if (flag) {
                    Lighting.setupFor3DItems();
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
                crashreportcategory.setDetail("Item Type", () -> String.valueOf(stack.getItem()));
                crashreportcategory.setDetail("Item Components", () -> String.valueOf(stack.getComponents()));
                crashreportcategory.setDetail("Item Foil", () -> String.valueOf(stack.hasFoil()));
                throw new ReportedException(crashreport);
            }

            pose.popPose();
        }
    }
}
