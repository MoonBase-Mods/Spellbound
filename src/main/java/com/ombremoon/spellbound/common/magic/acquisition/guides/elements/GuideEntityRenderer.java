package com.ombremoon.spellbound.common.magic.acquisition.guides.elements;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ElementPosition;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.EntityRendererExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record GuideEntityRenderer(ResourceLocation entityLoc, EntityRendererExtras extras, ElementPosition position) implements IPageElement {
    public static final MapCodec<GuideEntityRenderer> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("entity").forGetter(GuideEntityRenderer::entityLoc),
            EntityRendererExtras.CODEC.optionalFieldOf("extras", EntityRendererExtras.getDefault()).forGetter(GuideEntityRenderer::extras),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideEntityRenderer::position)
    ).apply(inst, GuideEntityRenderer::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }
}
