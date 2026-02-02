package com.ombremoon.spellbound.common.world.entity.ai.path;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomFlyingTarget;
import org.jetbrains.annotations.Nullable;

public class SetFlyingTarget<E extends PathfinderMob> extends SetRandomFlyingTarget<E> {

    @Override
    protected @Nullable Vec3 getTargetPos(E entity) {
        Vec3 entityFacing = entity.getViewVector(0);
        return AirAndWaterRandomPos.getPos(entity, (int)(Math.ceil(this.radius.xzRadius())), (int)Math.ceil(this.radius.yRadius()), this.verticalWeight.applyAsInt(entity), entityFacing.x, entityFacing.z, Mth.HALF_PI);
    }
}
