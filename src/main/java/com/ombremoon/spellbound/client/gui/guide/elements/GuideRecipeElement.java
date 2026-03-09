package com.ombremoon.spellbound.client.gui.guide.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.ElementPosition;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.RecipeExtras;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IHoverable;
import org.jetbrains.annotations.NotNull;

public record GuideRecipeElement(ResourceLocation recipeLoc, String gridName, float scale, ElementPosition position, RecipeExtras extras) implements IPageElement, IHoverable {

    public static final MapCodec<GuideRecipeElement> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("recipe").forGetter(GuideRecipeElement::recipeLoc),
            Codec.STRING.optionalFieldOf("gridName", "basic").forGetter(GuideRecipeElement::gridName),
            Codec.FLOAT.optionalFieldOf("scale", 1f).forGetter(GuideRecipeElement::scale),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideRecipeElement::position),
            RecipeExtras.CODEC.optionalFieldOf("extras", RecipeExtras.getDefault()).forGetter(GuideRecipeElement::extras)
    ).apply(inst, GuideRecipeElement::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }
}
