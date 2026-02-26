package com.ombremoon.spellbound.common.magic.effects.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBMagicEffects;
import com.ombremoon.spellbound.common.magic.effects.EffectContextParamSets;
import com.ombremoon.spellbound.common.magic.effects.MagicEffect;
import com.ombremoon.spellbound.common.world.item.SpellTomeItem;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualHelper;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record CreateSpellTome(SpellType<?> spell, int tier) implements MagicEffect {
    public static final MapCodec<CreateSpellTome> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    SBSpells.REGISTRY.byNameCodec().fieldOf("path").forGetter(CreateSpellTome::spell),
                    ExtraCodecs.intRange(1, 3).fieldOf("tier").forGetter(CreateSpellTome::tier)
            ).apply(instance, CreateSpellTome::new)
    );

    @Override
    public void onActivated(ServerLevel level, int tier, @Nullable Entity source, LivingEntity caster, BlockPos centerPos, Multiblock.MultiblockPattern pattern) {
        ItemStack spellTome = SpellTomeItem.createWithSpell(this.spell);
        Vec3 pos = centerPos.getBottomCenter();
        RitualHelper.createItem(level, pos, spellTome);
    }

    @Override
    public MagicEffect.Serializer<? extends MagicEffect> serializer() {
        return SBMagicEffects.CREATE_SPELL_TOME.get();
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

    public static class Serializer implements MagicEffect.Serializer<CreateSpellTome> {
        public static final MapCodec<CreateSpellTome> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        SBSpells.REGISTRY.byNameCodec().fieldOf("path").forGetter(CreateSpellTome::spell),
                        ExtraCodecs.intRange(1, 3).fieldOf("tier").forGetter(CreateSpellTome::tier)
                ).apply(instance, CreateSpellTome::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, CreateSpellTome> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(SBSpells.SPELL_TYPE_REGISTRY_KEY), CreateSpellTome::spell,
                ByteBufCodecs.VAR_INT, CreateSpellTome::tier,
                CreateSpellTome::new
        );

        @Override
        public MapCodec<CreateSpellTome> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CreateSpellTome> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
