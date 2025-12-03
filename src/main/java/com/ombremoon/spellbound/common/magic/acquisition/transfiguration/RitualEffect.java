package com.ombremoon.spellbound.common.magic.acquisition.transfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import com.ombremoon.spellbound.common.init.SBRitualEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

public interface RitualEffect {
    Codec<RitualEffect> CODEC = SBRitualEffects.REGISTRY
            .byNameCodec()
            .dispatch(RitualEffect::codec, Function.identity());


    void onActivated(ServerLevel level, int tier, Player player, BlockPos centerPos, Multiblock.MultiblockPattern pattern);

    default void onDeactivated(ServerLevel level, int tier, Player player, BlockPos centerPos, Multiblock.MultiblockPattern pattern) {

    }

    default boolean isValid(TransfigurationMultiblock multiblock) {
        return true;
    }

    MapCodec<? extends RitualEffect> codec();
}
