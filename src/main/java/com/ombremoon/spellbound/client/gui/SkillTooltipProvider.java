package com.ombremoon.spellbound.client.gui;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.component.TooltipProvider;

public interface SkillTooltipProvider extends TooltipProvider {

    DataComponentType<?> component();
}
