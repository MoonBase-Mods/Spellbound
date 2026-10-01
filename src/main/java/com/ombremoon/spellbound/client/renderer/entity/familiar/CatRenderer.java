package com.ombremoon.spellbound.client.renderer.entity.familiar;

import com.ombremoon.spellbound.client.renderer.SBModelLayerLocs;
import com.ombremoon.spellbound.common.world.entity.living.familiars.CatEntity;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CatRenderer extends MobRenderer<CatEntity, CatModel> {
    public CatRenderer(EntityRendererProvider.Context context) {
        super(context, new CatModel(context.bakeLayer(SBModelLayerLocs.CAT)), 1f);
    }

    @Override
    public ResourceLocation getTextureLocation(CatEntity catEntity) {
        return CommonClass.customLocation("textures/entity/familiar/cat/cat.png");
    }
}
