package com.ombremoon.spellbound.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

public class RandomPosUtil {
    @Nullable
    public static Vec3 getLandPos(Entity entity, int radius, int verticalRange) {
        return getLandPos(entity, radius, verticalRange, value -> 0.0F);
    }

    @Nullable
    public static Vec3 getAirAndWaterPos(Entity entity, int maxDistance, int yRange, int y, double x, double z, double amplifier) {
        return generateRandomPos(
                () -> generateRandomAirAndWaterPos(entity, maxDistance, yRange, y, x, z, amplifier)
        );
    }

    @Nullable
    public static Vec3 getLandPos(Entity entity, int radius, int yRange, ToDoubleFunction<BlockPos> toDoubleFunction) {
        return RandomPos.generateRandomPos(() -> {
            BlockPos blockpos = RandomPos.generateRandomDirection(entity.getRandom(), radius, yRange);
            BlockPos blockpos1 = generateRandomLandPosTowardDirection(entity, blockpos);
            return blockpos1 == null ? null : movePosUpOutOfSolid(entity, blockpos1);
        }, toDoubleFunction);
    }

    @Nullable
    public static BlockPos generateRandomAirAndWaterPos(
            Entity entity, int maxDistance, int yRange, int y, double x, double z, double amplifier
    ) {
        BlockPos blockpos = RandomPos.generateRandomDirectionWithinRadians(
                entity.getRandom(), maxDistance, yRange, y, x, z, amplifier
        );
        if (blockpos == null) {
            return null;
        } else {
            BlockPos blockpos1 = generateRandomPosTowardDirection(entity, blockpos);
            blockpos1 = RandomPos.moveUpOutOfSolid(blockpos1, entity.level().getMaxBuildHeight(), p_148376_ -> entity.level().getBlockState(p_148376_).isSolid());
            return blockpos1;
        }
    }

    public static BlockPos generateRandomPosTowardDirection(Entity entity, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getZ();
        return BlockPos.containing((double)i + entity.getX(), (double)pos.getY() + entity.getY(), (double)j + entity.getZ());
    }

    @Nullable
    public static BlockPos generateRandomLandPosTowardDirection(Entity entity, BlockPos pos) {
        BlockPos blockpos = generateRandomPosTowardDirection(entity, pos);
        return !isStableDestination(entity, blockpos) ? blockpos : null;
    }

    @Nullable
    public static Vec3 generateRandomPos(Supplier<BlockPos> posSupplier) {
        return generateRandomPos(posSupplier, value -> 0.0F);
    }

    @Nullable
    public static Vec3 generateRandomPos(Supplier<BlockPos> posSupplier, ToDoubleFunction<BlockPos> toDoubleFunction) {
        double d0 = Double.NEGATIVE_INFINITY;
        BlockPos blockpos = null;

        for (int i = 0; i < 10; i++) {
            BlockPos blockpos1 = posSupplier.get();
            if (blockpos1 != null) {
                double d1 = toDoubleFunction.applyAsDouble(blockpos1);
                if (d1 > d0) {
                    d0 = d1;
                    blockpos = blockpos1;
                }
            }
        }

        return blockpos != null ? Vec3.atBottomCenterOf(blockpos) : null;
    }

    private static boolean isStableDestination(Entity entity, BlockPos pos) {
        BlockPos blockpos = pos.below();
        return entity.level().getBlockState(blockpos).isSolidRender(entity.level(), blockpos);
    }

    @Nullable
    public static BlockPos movePosUpOutOfSolid(Entity entity, BlockPos pos) {
        pos = RandomPos.moveUpOutOfSolid(pos, entity.level().getMaxBuildHeight(), p_148534_ -> entity.level().getBlockState(p_148534_).isSolid());
        return !entity.level().getFluidState(pos).is(FluidTags.WATER) ? pos : null;
    }
}
