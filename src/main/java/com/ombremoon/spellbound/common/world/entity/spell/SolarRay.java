package com.ombremoon.spellbound.common.world.entity.spell;

import com.ombremoon.spellbound.common.world.entity.SpellEntity;
import com.ombremoon.spellbound.common.world.spell.ruin.fire.SolarRaySpell;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class SolarRay extends SpellEntity<SolarRaySpell> {
    private static final EntityDataAccessor<Boolean> SUNSHINE = SynchedEntityData.defineId(SolarRay.class, EntityDataSerializers.BOOLEAN);

    public SolarRay(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SUNSHINE, false);
    }

    public boolean hasSunshine() {
        return this.entityData.get(SUNSHINE);
    }

    public void addSunshine() {
        this.entityData.set(SUNSHINE, true);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getOwner() != null)
            this.setPos(this.getOwner().position());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER, 0, this::solarRayController));
        controllers.add(new AnimationController<>(this, "burst", 0, state -> PlayState.STOP)
                .triggerableAnim("solar_burst", RawAnimation.begin().thenPlay("solar_burst")));
        controllers.add(new AnimationController<>(this, "burst_extended", 0, state -> PlayState.STOP)
                .triggerableAnim("solar_burst_extended", RawAnimation.begin().thenPlay("solar_burst_extended")));
    }

    protected <S extends GeoAnimatable> PlayState solarRayController(AnimationState<S> data) {
        String extended = this.hasSunshine() ? "_extended" : "";
        if (isStarting()) {
            data.setAnimation(RawAnimation.begin().thenPlay("spawn" + extended));
        } else if (isEnding()) {
            data.setAnimation(RawAnimation.begin().thenPlay("end" + extended));
        } else {
            data.setAnimation(RawAnimation.begin().thenLoop("idle" + extended));
        }
        return PlayState.CONTINUE;
    }
}
