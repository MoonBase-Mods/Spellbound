package com.ombremoon.spellbound.common.world.entity;

import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.client.photon.EffectCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class VFXEntity extends Entity implements IVFXEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final EffectCache effectCache = new EffectCache();

    public VFXEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    protected abstract EffectBuilder<?> getEffect();

    protected abstract ResourceLocation getEffectLocation();

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (this.level().isClientSide) {
            var builder = this.getEffect();
            if (builder != null)
                this.addFX(builder);
        }
    }

    @Override
    public void onClientRemoval() {
        this.removeFX(this.getEffectLocation());
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public EffectCache getFXCache() {
        return this.effectCache;
    }
}
