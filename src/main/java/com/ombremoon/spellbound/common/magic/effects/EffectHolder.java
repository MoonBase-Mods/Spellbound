package com.ombremoon.spellbound.common.magic.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.effects.types.Nothing;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public record EffectHolder(MagicEffect effect, Optional<LootItemCondition> requirement, TickProvider tickProvider, Optional<RangeProvider> range, DataComponentMap components, BuffCategory category, int effectDuration, Optional<ResourceLocation> name) {
    public static final EffectHolder EMPTY = new EffectHolder(new Nothing(), Optional.empty(), new TickProvider.NoTick(), Optional.empty(), DataComponentMap.EMPTY, BuffCategory.NEUTRAL, 0, Optional.empty());

    public static final Codec<EffectHolder> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            MagicEffect.CODEC.fieldOf("effect").forGetter(EffectHolder::effect),
            LootItemCondition.DIRECT_CODEC.optionalFieldOf("requirement").forGetter(EffectHolder::requirement),
            TickProvider.CODEC.fieldOf("tick_provider").forGetter(EffectHolder::tickProvider),
            RangeProvider.CODEC.optionalFieldOf("range").forGetter(EffectHolder::range),
            DataComponentMap.CODEC.optionalFieldOf("components", DataComponentMap.EMPTY).forGetter(EffectHolder::components),
            BuffCategory.CODEC.fieldOf("category").forGetter(EffectHolder::category),
            ExtraCodecs.POSITIVE_INT.fieldOf("effect_duration").forGetter(EffectHolder::effectDuration),
            ResourceLocation.CODEC.optionalFieldOf("name").forGetter(EffectHolder::name)
    ).apply(inst, EffectHolder::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectHolder> STREAM_CODEC = StreamCodec.composite(
            MagicEffect.STREAM_CODEC, EffectHolder::effect,
            TickProvider.STREAM_CODEC, EffectHolder::tickProvider,
            ByteBufCodecs.optional(RangeProvider.STREAM_CODEC), EffectHolder::range,
            NeoForgeStreamCodecs.enumCodec(BuffCategory.class), EffectHolder::category,
            ByteBufCodecs.VAR_INT, EffectHolder::effectDuration,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), EffectHolder::name,
            EffectHolder::forStreamCodec
    );

    public static EffectHolder simple(MagicEffect effect, Optional<LootItemCondition> requirement, TickProvider tickProvider, BuffCategory category, int effectDuration) {
        return new EffectHolder(effect, requirement, tickProvider, Optional.empty(), DataComponentMap.EMPTY, category, effectDuration, Optional.empty());
    }

    public static EffectHolder withComponents(MagicEffect effect, Optional<LootItemCondition> requirement, TickProvider tickProvider, DataComponentMap.Builder components, BuffCategory category, int effectDuration) {
        return new EffectHolder(effect, requirement, tickProvider, Optional.empty(), components.build(), category, effectDuration, Optional.empty());
    }

    public static EffectHolder withRange(MagicEffect effect, Optional<LootItemCondition> requirement, TickProvider tickProvider, RangeProvider range, BuffCategory category, int effectDuration) {
        return new EffectHolder(effect, requirement, tickProvider, Optional.of(range), DataComponentMap.EMPTY, category, effectDuration, Optional.empty());
    }

    public EffectHolder named(ResourceLocation name) {
        return new EffectHolder(this.effect, this.requirement, this.tickProvider, this.range, this.components, this.category, this.effectDuration, Optional.of(name));
    }

    public <T> boolean hasComponent(Supplier<DataComponentType<T>> component) {
        return this.hasComponent(component.get());
    }

    public boolean hasComponent(DataComponentType<?> component) {
        return this.components().has(component);
    }

    public boolean hasComponents() {
        return !this.components().isEmpty();
    }

    public <T> T getComponentValue(Supplier<DataComponentType<T>> component) {
        return this.getComponentValue(component.get());
    }

    public <T> T getComponentValue(DataComponentType<T> component) {
        return this.components.get(component);
    }


    public static EffectHolder forStreamCodec(MagicEffect effect, TickProvider tickProvider, Optional<RangeProvider> range, BuffCategory category, int effectDuration, Optional<ResourceLocation> name) {
        return new EffectHolder(effect, Optional.empty(), tickProvider, range, DataComponentMap.EMPTY, category, effectDuration, name);
    }
}
