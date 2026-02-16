package com.ombremoon.spellbound.common.magic.acquisition.transfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.main.Keys;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class TransfigurationRitual {
    public static final int DEFAULT_RITUAL_DURATION = 1;
    public static final Codec<TransfigurationRitual> DIRECT_CODEC = RecordCodecBuilder.create(
            p_344998_ -> p_344998_.group(
                            RitualDefinition.CODEC.fieldOf("definition").forGetter(TransfigurationRitual::definition),
                            Value.CODEC
                                    .listOf()
                                    .fieldOf("ingredients")
                                    .flatXmap(list -> {
                                        Value[] avalue = list.toArray(Value[]::new);
                                        if (avalue.length == 0) {
                                            return DataResult.error(() -> "No ingredients for modular part recipe");
                                        } else {
                                            return avalue.length > 36
                                                    ? DataResult.error(() -> "Too many ingredients for mech recipe. You greedy gremlin. The max is 36.")
                                                    : DataResult.success(NonNullList.of(Value.EMPTY, avalue));
                                        }
                                    }, DataResult::success)
                                    .forGetter(recipe -> recipe.elementMaterials),
                            RitualEffect.CODEC.listOf().fieldOf("effects").forGetter(TransfigurationRitual::effects)
                    )
                    .apply(p_344998_, TransfigurationRitual::new)
    );
    public static final Codec<Holder<TransfigurationRitual>> CODEC = RegistryFixedCodec.create(Keys.RITUAL);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<TransfigurationRitual>> STREAM_CODEC = ByteBufCodecs.holderRegistry(Keys.RITUAL);
    private final RitualDefinition definition;
    private final NonNullList<Value> elementMaterials;
    private final NonNullList<Ingredient> materials = NonNullList.create();
    private final List<RitualEffect> effects;
    private final int startupTime;

    TransfigurationRitual(RitualDefinition definition, NonNullList<Value> elementMaterials, List<RitualEffect> effects) {
        this.definition = definition;
        this.elementMaterials = elementMaterials;
        this.effects = effects;
        this.startupTime = 5 * definition.tier * 20;

        this.materials.addAll(this.convertValueToIngredient(elementMaterials));
    }

    public boolean matches(TransfigurationMultiblock input, List<ItemStack> items) {
        if (items.size() != this.materials.size()) {
            return false;
        } else {
            return this.matches(items, this.materials) && this.definition.tier == input.getRings() && this.hasValidEffects(input);
        }
    }

    public boolean matches(List<ItemStack> from, NonNullList<Ingredient> to) {
        List<Ingredient> remaining = new ArrayList<>(to);
        for (ItemStack itemStack : from) {
            boolean found = false;
            Iterator<Ingredient> iter = remaining.iterator();
            while (iter.hasNext()) {
                if (iter.next().test(itemStack)) {
                    iter.remove();
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return remaining.isEmpty();
    }

    private NonNullList<Ingredient> convertValueToIngredient(NonNullList<Value> values) {
        NonNullList<Ingredient> list = NonNullList.create();
        for (Value value : values) {
            for (int i = 0; i < value.count; i++) {
                list.add(value.ingredient);
            }
        }

        return list;
    }

    public boolean hasValidEffects(TransfigurationMultiblock multiblock) {
        for (RitualEffect effect : this.effects) {
            if (!effect.isValid(multiblock))
                return false;
        }

        return true;
    }

    public RitualDefinition definition() {
        return this.definition;
    }

    public NonNullList<Value> clientMaterials() {
        return this.elementMaterials;
    }

    public List<RitualEffect> effects() {
        return this.effects;
    }

    public int getStartupTime() {
        return this.startupTime;
    }

    public static Builder ritual(int tier, int duration, int pathXP, SpellMastery mastery) {
        return new Builder(new RitualDefinition(tier, duration, pathXP, mastery));
    }

    public static Builder ritual(int tier, int pathXP, SpellMastery mastery) {
        return ritual(tier, DEFAULT_RITUAL_DURATION, pathXP, mastery);
    }

    public static class Builder {
        private final RitualDefinition definition;
        private final NonNullList<Value> materials = NonNullList.create();
        private final List<RitualEffect> effects = new ObjectArrayList<>();

        public Builder(RitualDefinition definition) {
            this.definition = definition;
        }

        public Builder requires(Ingredient ingredient) {
            return this.requires(ingredient, 1);
        }

        public Builder requires(Ingredient ingredient, int count) {
            this.materials.add(new Value(ingredient, count));
            return this;
        }

        public Builder withEffect(RitualEffect effect) {
            this.effects.add(effect);
            return this;
        }

        public TransfigurationRitual build() {
            return new TransfigurationRitual(this.definition, this.materials, this.effects);
        }
    }

    public record RitualDefinition(int tier, int duration, int pathXP, SpellMastery mastery) {
        public static final MapCodec<RitualDefinition> CODEC = RecordCodecBuilder.mapCodec(
                p_344890_ -> p_344890_.group(
                                ExtraCodecs.intRange(1, 3).fieldOf("tier").forGetter(RitualDefinition::tier),
                                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("duration").forGetter(RitualDefinition::duration),
                                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("path_xp").forGetter(RitualDefinition::pathXP),
                                SpellMastery.CODEC.fieldOf("mastery_requirement").forGetter(RitualDefinition::mastery)
                        )
                        .apply(p_344890_, RitualDefinition::new)
        );
    }

    public record Value(Ingredient ingredient, int count) {
        public static final Value EMPTY = new Value(Ingredient.EMPTY, 0);
        public static final Codec<Value> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(Value::ingredient),
                        Codec.INT.fieldOf("count").forGetter(Value::count)
                ).apply(instance, Value::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Value> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, Value::ingredient,
                ByteBufCodecs.INT, Value::count,
                Value::new
        );

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (!(obj instanceof Value value)) {
                return false;
            } else {
                return value.ingredient.equals(ingredient);
            }
        }
    }
}
