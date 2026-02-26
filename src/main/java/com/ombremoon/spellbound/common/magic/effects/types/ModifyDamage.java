package com.ombremoon.spellbound.common.magic.effects.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBMagicEffects;
import com.ombremoon.spellbound.common.magic.effects.EffectContextParamSets;
import com.ombremoon.spellbound.common.magic.effects.MagicEffect;
import com.ombremoon.spellbound.common.world.EntityResource;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ModifyDamage(float amount, EntityResource.Operation operation) implements MagicEffect {

    @Override
    public void onActivated(ServerLevel level, int tier, @Nullable Entity source, LivingEntity target, BlockPos centerPos, Multiblock.MultiblockPattern pattern) {

    }

    @Override
    public void onPreDamage(ServerLevel level, int tier, @Nullable Entity source, LivingEntity target, BlockPos centerPos, Multiblock.MultiblockPattern pattern, DamageContainer container) {
        container.setNewDamage((float) this.operation.apply(container.getOriginalDamage(), this.amount));
    }

    @Override
    public void onPreAttack(ServerLevel level, int tier, @Nullable Entity source, LivingEntity target, BlockPos centerPos, Multiblock.MultiblockPattern pattern, DamageContainer container) {
        container.setNewDamage((float) this.operation.apply(container.getOriginalDamage(), this.amount));
    }

    @Override
    public MagicEffect.Serializer<? extends MagicEffect> serializer() {
        return SBMagicEffects.MODIFY_DAMAGE.get();
    }

    @Override
    public LootContext createContext(ServerLevel level, int tier, @Nullable Entity source, LivingEntity caster, BlockPos centerPos, Multiblock.MultiblockPattern pattern, @Nullable DamageSource damageSource) {
        LootParams lootparams = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, caster)
                .withParameter(LootContextParams.ORIGIN, caster.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                .withParameter(LootContextParams.TOOL, caster.getMainHandItem())
                .withOptionalParameter(EffectContextParamSets.RITUAL_TIER, tier)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, damageSource.getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity())
                .create(EffectContextParamSets.MAGIC_DAMAGE);
        return new LootContext.Builder(lootparams).create(Optional.empty());
    }

    public static class Serializer implements MagicEffect.Serializer<ModifyDamage> {
        public static final MapCodec<ModifyDamage> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                    Codec.FLOAT.fieldOf("amount").forGetter(ModifyDamage::amount),
                    EntityResource.Operation.CODEC.fieldOf("operation").forGetter(ModifyDamage::operation)
                ).apply(instance, ModifyDamage::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ModifyDamage> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, ModifyDamage::amount,
                NeoForgeStreamCodecs.enumCodec(EntityResource.Operation.class), ModifyDamage::operation,
                ModifyDamage::new
        );

        @Override
        public MapCodec<ModifyDamage> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ModifyDamage> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
