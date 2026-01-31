package com.ombremoon.spellbound.client.renderer.entity;

import com.ombremoon.spellbound.common.world.entity.living.LivingShadow;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

public class LivingShadowRenderer extends HumanoidMobRenderer<LivingShadow, HumanoidModel<LivingShadow>> {
    public LivingShadowRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LivingShadow entity) {
        if (entity.getSummoner() != null) {
            AbstractClientPlayer player = (AbstractClientPlayer) entity.getSummoner();
            if (player != null) return player.getSkin().texture();
        }
        return DefaultPlayerSkin.getDefaultTexture();
    }
}
