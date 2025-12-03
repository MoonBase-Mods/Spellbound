package com.ombremoon.spellbound.common.world.entity.spell;

import com.ombremoon.spellbound.common.world.entity.SpellEntity;
import com.ombremoon.spellbound.common.world.spell.ruin.shock.StormRiftSpell;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class StormBolt extends SpellEntity<StormRiftSpell> {
    public StormBolt(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER, 0, this::stormBoltController));
    }

    protected <T extends GeoAnimatable> PlayState stormBoltController(AnimationState<T> data) {
        if (!this.isRemoved()) {
            data.setAnimation(RawAnimation.begin().thenPlay("spawn"));
        }
        return PlayState.CONTINUE;
    }
}
