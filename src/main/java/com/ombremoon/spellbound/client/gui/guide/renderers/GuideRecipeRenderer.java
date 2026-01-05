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

        if (recipe instanceof ShapedRecipe shapedRecipe) {
            List<GuideGhostItem> ghostRecipe = new ArrayList<>();

            boolean largeGrid = shapedRecipe.getWidth() > 2 || shapedRecipe.getHeight() > 2;
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
                for (int i = 1; i <= 9; i++) {
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
                for (int i = 1; i <= 4; i++) {
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
