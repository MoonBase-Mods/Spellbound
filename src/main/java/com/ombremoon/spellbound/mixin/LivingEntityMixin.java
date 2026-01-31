package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.events.EventFactory;
import com.ombremoon.spellbound.common.world.entity.living.familiars.SBFamiliarEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Shadow @Final private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;

    @Inject(method = "onEffectRemoved", at = @At(value = "TAIL"))
    private void onEffectRemoved(MobEffectInstance instance, CallbackInfo info) {
        EventFactory.onEffectRemoved(spellbound$self(), instance);
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private void addEffect(MobEffectInstance effectInstance, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        MobEffectInstance mobeffectinstance = this.activeEffects.get(effectInstance.getEffect());
        EventFactory.onEffectAdded(spellbound$self(), mobeffectinstance, effectInstance, entity);
    }


    @Unique
    private LivingEntity spellbound$self() {
        return (LivingEntity) (Object) this;
    }
}
