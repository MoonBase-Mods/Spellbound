package com.ombremoon.spellbound.client.renderer.types;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.spellbound.client.model.types.VFXSpellModel;
import com.ombremoon.spellbound.common.world.entity.SpellEntity;
import com.ombremoon.spellbound.common.world.entity.SpellProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VFXSpellRenderer<T extends SpellEntity<?>> extends GeoEntityRenderer<T> {
    public VFXSpellRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VFXSpellModel<>());
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

    }
}
