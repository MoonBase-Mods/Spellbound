package com.ombremoon.spellbound.common.magic.acquisition.guides.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ElementPosition;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.RecipeExtras;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record GuideRecipe(ResourceLocation recipeLoc, String gridName, float scale, ElementPosition position, RecipeExtras extras) implements IPageElement {

    public static final MapCodec<GuideRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("recipe").forGetter(GuideRecipe::recipeLoc),
            Codec.STRING.optionalFieldOf("gridName", "basic").forGetter(GuideRecipe::gridName),
            Codec.FLOAT.optionalFieldOf("scale", 1f).forGetter(GuideRecipe::scale),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideRecipe::position),
            RecipeExtras.CODEC.optionalFieldOf("extras", RecipeExtras.getDefault()).forGetter(GuideRecipe::extras)
    ).apply(inst, GuideRecipe::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }
}
