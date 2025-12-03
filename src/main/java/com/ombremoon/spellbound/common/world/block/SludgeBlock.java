package com.ombremoon.spellbound.common.world.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SludgeBlock extends Block {
    public static final MapCodec<SludgeBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.BOOL.fieldOf("causeHarm").forGetter(sludgeBlock -> sludgeBlock.causeHarm),
                    propertiesCodec()
            ).apply(instance, SludgeBlock::new)
    );
    private final boolean causeHarm;

    public SludgeBlock(boolean causeHarm, Properties properties) {
        super(properties);
        this.causeHarm = causeHarm;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        Vec3 vec3 = new Vec3(0.75, 0.85, 0.75);
        entity.makeStuckInBlock(state, vec3);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(5) == 0) {
            level.addParticle(
                    SBParticles.SLUDGE.get(),
                    (double)pos.getX() + random.nextDouble(),
                    (double)pos.getY() + 1.1,
                    (double)pos.getZ() + random.nextDouble(),
                    0.0,
                    0.0,
                    0.0
            );
        }
    }
}
