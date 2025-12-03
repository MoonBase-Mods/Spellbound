package com.ombremoon.spellbound.common.magic.acquisition.bosses;

import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public abstract class BossFight {
    protected static final Logger LOGGER = Constants.LOG;
    public static final BlockPos ORIGIN = new BlockPos(0, 64, 0);
    protected final SpellType<?> spell;
    protected final BlockPos blockScanStart;
    protected final BlockPos blockScanEnd;
    protected final Vec3 playerSpawnOffset;
    protected final DimensionData dimensionData;

    public BossFight(SpellType<?> spell, BlockPos blockScanStart, BlockPos blockScanEnd, Vec3 playerSpawnOffset, DimensionData dimensionData) {
        this.spell = spell;
        this.blockScanStart = blockScanStart;
        this.blockScanEnd = blockScanEnd;
        this.playerSpawnOffset = playerSpawnOffset;
        this.dimensionData = dimensionData;
    }

    public abstract BossFightInstance<?, ?> createFight(ServerLevel level);

    public SpellType<?> getSpell() {
        return this.spell;
    }

    public Vec3 getPlayerSpawnOffset() {
        return this.playerSpawnOffset;
    }

    public DimensionData getDimensionData() {
        return this.dimensionData;
    }

    public interface BossFightBuilder<T extends BossFight> {
        T build();
    }
}
