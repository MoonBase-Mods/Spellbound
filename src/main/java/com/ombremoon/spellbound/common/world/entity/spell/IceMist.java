package com.ombremoon.spellbound.common.world.entity.spell;

import com.ombremoon.spellbound.common.world.entity.SpellEntity;
import com.ombremoon.spellbound.common.world.spell.ruin.ice.ShatteringCrystalSpell;
import com.ombremoon.spellbound.common.init.SBSpells;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.util.RandomUtil;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import java.util.List;

public class IceMist extends SpellEntity<ShatteringCrystalSpell> {
    public IceMist(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER, 0, this::mistController));
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        float width = super.getDimensions(pPose).width();
        float growth = Math.min(width * (0.2F * (1 + this.tickCount / 80.0F)), 1.0F);
        return super.getDimensions(pPose).scale(growth, 1.0F);
    }

    @Override
    public boolean requiresSpellToPersist() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.refreshDimensions();
        if (this.getSummoner() instanceof LivingEntity owner) {
            ShatteringCrystalSpell spell = SBSpells.SHATTERING_CRYSTAL.get().createSpellWithData(owner);
            if (spell != null) {
                List<Entity> entityList = this.level().getEntities(owner, this.getBoundingBox());
                for (Entity entity : entityList) {
                    if (entity instanceof LivingEntity livingEntity && this.tickCount % 20 == 0) {
                        spell.hurt(this, livingEntity, 2);
                        livingEntity.setIsInPowderSnow(true);
                    }
                }
            }
        }

        if (this.tickCount % 5 == 0)
            this.level().addParticle(
                    ParticleTypes.SNOWFLAKE,
                    this.getX() + RandomUtil.randomValueBetween(-1.5, 1.5),
                    this.getY() + RandomUtil.randomValueBetween(0, 1.5),
                    this.getZ() + RandomUtil.randomValueBetween(-1.5, 1.5),
                    0, 0, 0);

        if (this.tickCount >= 200) {
            this.discard();
        }
    }

    protected <S extends GeoAnimatable> PlayState mistController(AnimationState<S> data) {
        data.setAnimation(RawAnimation.begin().thenPlay("mist"));
        return PlayState.CONTINUE;
    }
}
