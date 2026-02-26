package com.ombremoon.spellbound.client.renderer.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.spellbound.client.renderer.types.VFXSpellRenderer;
import com.ombremoon.spellbound.common.world.entity.spell.CursedRune;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class CursedRuneRenderer extends VFXSpellRenderer<CursedRune> {
    public CursedRuneRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(CursedRune cursedRune, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (cursedRune.isHidden() && Minecraft.getInstance().player != cursedRune.getSummoner())
            return;

        super.render(cursedRune, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
