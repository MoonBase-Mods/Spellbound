package com.ombremoon.spellbound.client.gui;

import com.ombremoon.spellbound.common.init.SBData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.component.TooltipProvider;

public interface SkillTooltipProvider extends TooltipProvider {

    default DataComponentType<?> component() {
        return SBData.DETAILS.get();
    }
}
