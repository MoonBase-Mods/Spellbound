package com.ombremoon.spellbound.client.renderer.types;

import com.ombremoon.spellbound.common.world.entity.SpellEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class OutlineSpellRenderer<T extends SpellEntity> extends GenericSpellRenderer<T> {
    public OutlineSpellRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public @Nullable RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.itemEntityTranslucentCull(texture);
    }
}
