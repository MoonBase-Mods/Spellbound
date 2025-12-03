package com.ombremoon.spellbound.common.magic.acquisition.bosses;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class WaveBasedBossFight extends BossFight {
    private final List<List<Pair<EntityType<?>, Integer>>> waves;
    private final int waveInterval;

    public WaveBasedBossFight(List<List<Pair<EntityType<?>, Integer>>> waves, int waveInterval, SpellType<?> spell, BlockPos blockScanStart, BlockPos blockScanEnd, Vec3 playerSpawnOffset, DimensionData dimensionData) {
        super(spell, blockScanStart, blockScanEnd, playerSpawnOffset, dimensionData);
        this.waves = waves;
        this.waveInterval = waveInterval;
    }

    @Override
    public BossFightInstance<?, ?> createFight(ServerLevel level) {
        return null;
    }
}
