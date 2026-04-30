package com.ombremoon.spellbound.common.world.entity;

import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.client.photon.EffectCache;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.networking.PayloadHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.AnimatableManager;

public abstract class VFXSpellEntity<T extends AbstractSpell> extends SpellEntity<T> {

    protected VFXSpellEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    protected abstract EffectBuilder<?> getEffect();

    protected abstract ResourceLocation getEffectLocation();

    protected void triggerFX(EffectData builder) {
        if (!this.level().isClientSide) {
            PayloadHandler.triggerEntityFx(this, builder);
        }
    }

    protected void removeEntityFX(ResourceLocation effect) {
        if (!this.level().isClientSide) {
            PayloadHandler.removeEntityFX(this, effect);
        }
    }

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
}
