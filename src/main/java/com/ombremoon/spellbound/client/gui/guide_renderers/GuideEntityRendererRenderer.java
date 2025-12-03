package com.ombremoon.spellbound.client.gui.guide_renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.GuideEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.joml.Quaternionf;
import software.bernie.geckolib.animatable.GeoEntity;

public class GuideEntityRendererRenderer implements IPageElementRenderer<GuideEntityRenderer> {
    private Entity entity;

    @Override
    public void render(GuideEntityRenderer element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        EntityType<?> entityType = Minecraft.getInstance().level.registryAccess().registry(Registries.ENTITY_TYPE).get().get(element.entityLoc());
        entityType = SBEntities.HEALING_BLOSSOM.get();

        if (entityType == null) {
            LOGGER.warn("Entity could not be found {}", element.entityLoc());
            return;
        }

        if (this.entity == null || this.entity.getType() != (entityType)) this.entity = entityType.create(Minecraft.getInstance().level);

        if (entity instanceof GeoEntity){
            //if (entity instanceof SpellEntity<?> spellEntity) spellEntity.setOwner(Minecraft.getInstance().player);
            entity.tickCount++;
            entity.tick();
        }

        renderEntityInInventory(graphics,
                leftPos + element.position().xOffset(),
                topPos + element.position().yOffset(),
                element.extras().scale(),
                entity, isVisible(element.extras().pageScrap()),
                element.extras().followMouse(), mouseX, mouseY);
    }

    public static void renderEntityInInventory(GuiGraphics guiGraphics, float x, float y, float scale, Entity entity, boolean isVisible, boolean followsMouse, int mouseX, int mouseY) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(x, y, 50);

        if (followsMouse) {
            float f = (float)(x + x + entity.getBbWidth()) / 2.0F;
            float f1 = (float)(y +  y + entity.getBbHeight()) / 2.0F;
            float f2 = (float)Math.atan((double)((f - mouseX) / 40.0F));
            float f3 = (float)Math.atan((double)((f1 - mouseY) / 40.0F));

            Quaternionf quaternionf = new Quaternionf()
                    .rotateZ((float)Math.PI)         // flip
                    .rotateX(-f3 * 20.0F * ((float)Math.PI / 180F))  // vertical pitch
                    .rotateY(f2);                // horizontal yaw

            poseStack.mulPose(quaternionf);
        } else {
            poseStack.mulPose(new Quaternionf()
                    .rotateZ((float) Math.PI));
        }

        poseStack.scale(scale, scale, scale);

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();

        dispatcher.setRenderShadow(false);
        dispatcher.render(entity, 0, 0, 0, 0, 0, poseStack, buffers, isVisible ? 15728880 : 0);
        buffers.endBatch();
        dispatcher.setRenderShadow(true);

        poseStack.popPose();
    }
}
