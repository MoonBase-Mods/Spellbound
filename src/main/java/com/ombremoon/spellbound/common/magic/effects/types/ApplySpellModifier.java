package com.ombremoon.spellbound.common.magic.effects.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBMagicEffects;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellModifier;
import com.ombremoon.spellbound.common.magic.effects.EffectContextParamSets;
import com.ombremoon.spellbound.common.magic.effects.MagicEffect;
import com.ombremoon.spellbound.common.magic.skills.PseudoSkillProvider;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ApplySpellModifier(
        ResourceLocation location,
        SpellModifier modifier,
        BuffCategory buffCategory,
        int minDuration,
        int maxDuration
) implements MagicEffect {

    @Override
    public void onActivated(ServerLevel level, int tier, @Nullable Entity source, LivingEntity caster, BlockPos centerPos, Multiblock.MultiblockPattern pattern) {
        if (AbstractSpell.checkForCounterMagic(caster) && buffCategory == BuffCategory.HARMFUL)
            return;

        SkillBuff<?> skillBuff = new SkillBuff<>(new PseudoSkillProvider(location), location, buffCategory, SkillBuff.SPELL_MODIFIER, modifier);
        var handler = SpellUtil.getSpellHandler(caster);
        int i = Math.round(Mth.randomBetween(caster.getRandom(), this.minDuration, this.maxDuration) * 20.0F);
        handler.addSkillBuff(skillBuff, source, i);
    }

    @Override
    public MagicEffect.Serializer<? extends MagicEffect> serializer() {
        return SBMagicEffects.APPLY_SPELL_MODIFIER.get();
    }

    @Override
    public LootContext createContext(ServerLevel level, int tier, @Nullable Entity source, LivingEntity caster, BlockPos centerPos, Multiblock.MultiblockPattern pattern, @Nullable DamageSource damageSource) {
        LootParams lootparams = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, caster)
                .withParameter(LootContextParams.ORIGIN, centerPos.getCenter())
                .withOptionalParameter(EffectContextParamSets.RITUAL_TIER, tier)
                .create(EffectContextParamSets.MAGIC_ENTITY);
        return new LootContext.Builder(lootparams).create(Optional.empty());
    }

    public static class Serializer implements MagicEffect.Serializer<ApplySpellModifier> {
        public static final MapCodec<ApplySpellModifier> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        ResourceLocation.CODEC.fieldOf("location").forGetter(ApplySpellModifier::location),
                        SpellModifier.CODEC.fieldOf("modifier").forGetter(ApplySpellModifier::modifier),
                        BuffCategory.CODEC.fieldOf("buff_category").forGetter(ApplySpellModifier::buffCategory),
                        ExtraCodecs.POSITIVE_INT.fieldOf("min_duration").forGetter(ApplySpellModifier::minDuration),
                        ExtraCodecs.POSITIVE_INT.fieldOf("max_duration").forGetter(ApplySpellModifier::maxDuration)
                ).apply(instance, ApplySpellModifier::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ApplySpellModifier> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, ApplySpellModifier::location,
                SpellModifier.STREAM_CODEC, ApplySpellModifier::modifier,
                NeoForgeStreamCodecs.enumCodec(BuffCategory.class), ApplySpellModifier::buffCategory,
                ByteBufCodecs.VAR_INT, ApplySpellModifier::minDuration,
                ByteBufCodecs.VAR_INT, ApplySpellModifier::maxDuration,
                ApplySpellModifier::new
        );


       @Override
        public MapCodec<ApplySpellModifier> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ApplySpellModifier> streamCodec() {
            return STREAM_CODEC;
        }
    }
}


