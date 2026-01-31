package com.ombremoon.spellbound.client.renderer.entity;

import com.ombremoon.spellbound.client.renderer.entity.familiar.FrogModel;
import com.ombremoon.spellbound.common.world.entity.living.familiars.FrogEntity;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FrogRenderer<T extends FrogEntity> extends MobRenderer<T, FrogModel<T>> {

    public FrogRenderer(EntityRendererProvider.Context context) {
        super(context, new FrogModel<>(context.bakeLayer(SBModelLayerLocs.FROG)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(FrogEntity frogEntity) {
        return CommonClass.customLocation("textures/entity/familiar/frog/frog.png");
    }
}
