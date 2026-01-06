package com.ombremoon.spellbound.client.gui.guide.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.ElementPosition;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.StaticItemExtras;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.DataComponentStorage;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record GuideStaticItemElement(List<Ingredient> item, String tileName, ElementPosition position, StaticItemExtras extras) implements IPageElement {
    public static final MapCodec<GuideStaticItemElement> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.listOf().fieldOf("item").forGetter(GuideStaticItemElement::item),
            Codec.STRING.optionalFieldOf("tileName", "basic").forGetter(GuideStaticItemElement::tileName),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideStaticItemElement::position),
            StaticItemExtras.CODEC.optionalFieldOf("extras", StaticItemExtras.getDefault()).forGetter(GuideStaticItemElement::extras)
    ).apply(inst, GuideStaticItemElement::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }

    record IngredientValue(Ingredient ingredient, Optional<DataComponentStorage> data) {}
}
