package com.ombremoon.spellbound.common.world.entity.spell;

import com.ombremoon.spellbound.common.world.entity.SpellEntity;
import com.ombremoon.spellbound.common.world.spell.ruin.ice.ShatteringCrystalSpell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.util.RandomUtil;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class ShatteringCrystal extends SpellEntity<ShatteringCrystalSpell> {
    public final float bobOffs;
    public boolean marked;

    public ShatteringCrystal(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.bobOffs = this.random.nextFloat() * (float) Math.PI * 2.0F;
    }

    @Override
    public void tick() {
        super.tick();
        RandomSource random = this.level().random;
        if (random.nextInt(8) == 0) {
            this.level().addParticle(
                    ParticleTypes.SNOWFLAKE,
                    this.getX(),
                    this.getY() + 2,
                    this.getZ(),
                    Mth.randomBetween(random, -0.5F, 0.5F) * 0.083333336F,
                    0.05F,
                    Mth.randomBetween(random, -0.5F, 0.5F) * 0.083333336F
            );
        }

        if (this.tickCount % 10 == 0)
            this.level().addParticle(
                    ParticleTypes.SNOWFLAKE,
                    this.getX() + RandomUtil.randomValueBetween(-1, 1),
                    this.getY() + RandomUtil.randomValueBetween(0, 3),
                    this.getZ() + RandomUtil.randomValueBetween(-1, 1),
                    0, 0, 0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER, 0, this::crystalController));
    }

    protected <S extends GeoAnimatable> PlayState crystalController(AnimationState<S> data) {
        if (isStarting()) {
            data.setAnimation(RawAnimation.begin().thenPlay("spawn"));
        } else if (isEnding()) {
            data.setAnimation(RawAnimation.begin().thenPlay("prime"));
        } else {
            data.setAnimation(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.CONTINUE;
    }

    public float getSpin(float partialTick) {
        return (this.tickCount + partialTick) / 40.0F + this.bobOffs;
    }
}
