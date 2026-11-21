package com.ombremoon.spellbound.client.gui.guide.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RuneCraftingElement(ResourceLocation recipe, boolean leftPage) implements IPageElement {
    public static final MapCodec<RuneCraftingElement> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("ritual").forGetter(RuneCraftingElement::recipe),
            Codec.BOOL.fieldOf("leftPage").forGetter(RuneCraftingElement::leftPage)
    ).apply(inst, RuneCraftingElement::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }
}
