package com.ombremoon.spellbound.common.world.entity.ai.sensor;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.init.SBMemoryTypes;
import com.ombremoon.spellbound.common.init.SBSensors;
import com.ombremoon.spellbound.common.init.SBTags;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class NearbyShrineSensor<E extends LivingEntity> extends PredicateSensor<BlockState, E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(SBLMemoryTypes.NEARBY_BLOCKS.get());

    protected SquareRadius radius = new SquareRadius(1, 1);

    public NearbyShrineSensor() {
        super((state, entity) -> !state.isAir());
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return SBSensors.NEARBY_SHRINE.get();
    }

    /**
     * Set the radius for the sensor to scan
     * @param radius The coordinate radius, in blocks
     * @return this
     */
    public NearbyShrineSensor<E> setRadius(double radius) {
        return setRadius(radius, radius);
    }

    /**
     * Set the radius for the sensor to scan.
     * @param xz The X/Z coordinate radius, in blocks
     * @param y The Y coordinate radius, in blocks
     * @return this
     */
    public NearbyShrineSensor<E> setRadius(double xz, double y) {
        this.radius = new SquareRadius(xz, y);

        return this;
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        List<Pair<BlockPos, BlockState>> blocks = new ObjectArrayList<>();

        for (BlockPos pos : BlockPos.betweenClosed(entity.blockPosition().subtract(this.radius.toVec3i()), entity.blockPosition().offset(this.radius.toVec3i()))) {
            BlockState state = level.getBlockState(pos);

            if (state.is(SBTags.Blocks.DIVINE_SHRINE))
                blocks.add(Pair.of(pos.immutable(), state));
        }

        if (blocks.isEmpty()) {
            BrainUtils.clearMemory(entity, SBMemoryTypes.NEARBY_SHRINES.get());
        }
        else {
            BrainUtils.setMemory(entity, SBMemoryTypes.NEARBY_SHRINES.get(), blocks);
        }
    }
}
