package com.ombremoon.spellbound.client.renderer.blockentity;

import com.ombremoon.spellbound.client.model.blockentity.ValkyrStatueModel;
import com.ombremoon.spellbound.common.world.block.entity.ValkyrBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.nikdo53.tinymultiblocklib.client.GeoMultiblockRenderer;

public class ValkyrStatueRenderer extends GeoMultiblockRenderer<ValkyrBlockEntity> {
    public ValkyrStatueRenderer(BlockEntityRendererProvider.Context context) {
        super(new ValkyrStatueModel());
    }
}
