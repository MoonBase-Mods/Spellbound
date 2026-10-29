package com.ombremoon.spellbound.common.world.entity;

import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.AnimatableManager;

public abstract class VFXSpellProjectile<T extends AbstractSpell> extends SpellProjectile<T> {
    protected VFXSpellProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    protected abstract EffectBuilder<?> getEffect();

    protected abstract ResourceLocation getEffectLocation();

    @Override
    protected void initializeClientEntity() {
        super.initializeClientEntity();
        var builder = this.getEffect();
        if (builder != null)
            this.addFX(builder);
    }

    @Override
    public void onClientRemoval() {
        this.removeFX(this.getEffectLocation());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }
}
