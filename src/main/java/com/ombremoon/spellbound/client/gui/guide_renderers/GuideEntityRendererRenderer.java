package com.ombremoon.spellbound.client.gui.guide_renderers;

import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.GuideEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class GuideEntityRendererRenderer implements IPageElementRenderer<GuideEntityRenderer> {

    @Override
    public void render(GuideEntityRenderer element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick) {
        EntityType<?> entityType = Minecraft.getInstance().level.registryAccess().registry(Registries.ENTITY_TYPE).get().get(element.entityLoc());

        if (entityType == null) {
            LOGGER.warn("Entity could not be found {}", element.entityLoc());
            return;
        }
        Entity entity = entityType.create(Minecraft.getInstance().level);
        if (!(entity instanceof LivingEntity livingEntity)) {
            LOGGER.warn("Entity {} is not a living entity", element.entityLoc());
            return;
        }

        //Add obfuscated without scrap
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics,
                leftPos + element.position().xOffset(),
                topPos + element.position().yOffset(),
                leftPos + (3 * element.extras().scale()),
                topPos + (4 * element.extras().scale()),
                element.extras().scale(),
                0.25F,
                mouseX,
                mouseY,
                livingEntity);
    }
}
