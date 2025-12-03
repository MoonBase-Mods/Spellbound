package com.ombremoon.spellbound.client.renderer.types;

import com.ombremoon.spellbound.client.model.SBCatalystModel;
import com.ombremoon.spellbound.common.world.item.CatalystItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SBCatalystRenderer extends GeoItemRenderer<CatalystItem> {
    public SBCatalystRenderer() {
        super(new SBCatalystModel());
    }
}
