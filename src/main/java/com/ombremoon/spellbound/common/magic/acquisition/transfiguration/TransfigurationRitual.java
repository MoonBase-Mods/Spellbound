package com.ombremoon.spellbound.common.magic.acquisition.transfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import com.ombremoon.spellbound.main.Keys;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public record TransfigurationRitual(Component description, RitualDefinition definition, NonNullList<Ingredient> materials, List<RitualEffect> effects) {
    public static final int DEFAULT_RITUAL_DURATION = 5;
    public static final Codec<TransfigurationRitual> DIRECT_CODEC = RecordCodecBuilder.create(
            p_344998_ -> p_344998_.group(
                            ComponentSerialization.CODEC.fieldOf("description").forGetter(TransfigurationRitual::description),
                            RitualDefinition.CODEC.fieldOf("definition").forGetter(TransfigurationRitual::definition),
                            Ingredient.CODEC_NONEMPTY
                                    .listOf()
                                    .fieldOf("materials")
                                    .flatXmap(list -> {
                                        Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                                        if (ingredients.length == 0) {
                                            return DataResult.error(() -> "No materials for transfiguration ritual.");
                                        } else {
                                            return ingredients.length > 24
                                                    ? DataResult.error(() -> "Too many materials for transfiguration ritual. You greedy gremlin. The max is 24.")
                                                    : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                                        }
                                    }, DataResult::success)
                                    .forGetter(ritual -> ritual.materials),
                            RitualEffect.CODEC.listOf().fieldOf("effects").forGetter(TransfigurationRitual::effects)
                    )
                    .apply(p_344998_, TransfigurationRitual::new)
    );
    public static final Codec<Holder<TransfigurationRitual>> CODEC = RegistryFixedCodec.create(Keys.RITUAL);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<TransfigurationRitual>> STREAM_CODEC = ByteBufCodecs.holderRegistry(Keys.RITUAL);

    public boolean matches(TransfigurationMultiblock multiblock, List<ItemStack> items) {
        return this.definition.tier == multiblock.getRings() && this.hasRequiredMaterials(items) && this.hasValidEffects(multiblock);
    }

    public boolean hasRequiredMaterials(List<ItemStack> items) {
        Collection<Ingredient> acceptedItems = new ArrayList<>(this.materials);
        for (ItemStack stack : items) {
            acceptedItems.removeIf(ingredient -> ingredient.test(stack));
        }
        return acceptedItems.isEmpty();
    }

    public boolean hasValidEffects(TransfigurationMultiblock multiblock) {
        for (RitualEffect effect : this.effects) {
            if (!effect.isValid(multiblock))
                return false;
        }

        return true;
    }

    public static Builder ritual(int tier, int startupTime, int duration) {
        return new Builder(new RitualDefinition(tier, startupTime, duration));
    }

    public static Builder ritual(int tier, int duration) {
        return ritual(tier, 100, duration);
    }

    public static Builder ritual(int tier) {
        return ritual(tier, DEFAULT_RITUAL_DURATION);
    }

    public static class Builder {
        private final RitualDefinition definition;
        private final NonNullList<Ingredient> materials = NonNullList.create();
        private final List<RitualEffect> effects = new ObjectArrayList<>();

        public Builder(RitualDefinition definition) {
            this.definition = definition;
        }

        public Builder requires(Ingredient ingredient) {
            return this.requires(ingredient, 1);
        }

        public Builder requires(Ingredient ingredient, int count) {
            for (int i = 0; i < count; i++) {
                this.materials.add(ingredient);
            }

            return this;
        }

        public Builder withEffect(RitualEffect effect) {
            this.effects.add(effect);
            return this;
        }

        public TransfigurationRitual build(ResourceLocation location) {
            return new TransfigurationRitual(
                    Component.translatable(Util.makeDescriptionId("ritual", location)), this.definition, this.materials, this.effects
            );
        }
    }

    public record RitualDefinition(int tier, int startupTime, int duration) {
        public static final MapCodec<RitualDefinition> CODEC = RecordCodecBuilder.mapCodec(
                p_344890_ -> p_344890_.group(
                                ExtraCodecs.intRange(1, 3).fieldOf("tier").forGetter(RitualDefinition::tier),
                                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("startupTime").forGetter(RitualDefinition::startupTime),
                                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("duration").forGetter(RitualDefinition::duration)
                        )
                        .apply(p_344890_, RitualDefinition::new)
        );
    }
}
