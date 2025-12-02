package com.ombremoon.spellbound.client.gui.guide_renderers;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.GuideItem;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ItemExtras;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GuideItemRenderer implements IPageElementRenderer<GuideItem> {

    @Override
    public void render(GuideItem element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        Registry<Item> registry = Minecraft.getInstance().level.registryAccess().registry(Registries.ITEM).get();

        RandomSource rand = Minecraft.getInstance().level.getRandom();
        rand.setSeed(Math.floorDiv(Minecraft.getInstance().player.tickCount, 10));

        Item item = isVisible(element.extras().pageScrap()) ? registry.get(element.itemLoc()) : registry.getRandom(rand).get().value();
        if (item == null) return;

        graphics.blit(CommonClass.customLocation("textures/gui/books/crafting_grids/medium/" + element.tileName() + ".png"),
                leftPos + element.position().xOffset(),
                topPos + element.position().yOffset(),
                0,
                0,
                (int) (48 * element.scale()),
                (int) (46 * element.scale()),
                (int) (48 * element.scale()),
                (int) (46 * element.scale()));
        renderItem(graphics, item.getDefaultInstance(), leftPos + element.position().xOffset(), topPos + element.position().yOffset(), 1.3f * element.scale());
    }

    public static void renderItem(GuiGraphics graphics, ItemStack stack, int x, int y, float scale) {
        if (!stack.isEmpty()) {
            Minecraft minecraft = Minecraft.getInstance();
            BakedModel bakedmodel = minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 0);
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate((x + (19*scale)), (y + (17*scale)), (float)(150));

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
