package com.ombremoon.spellbound.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.ombremoon.spellbound.common.events.EventFactory;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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

    @Redirect(method = "travel", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void modifySpeed(LivingEntity instance, Vec3 vec3) {
        var skills = SpellUtil.getSkills(instance);
        if ((SpellUtil.isSpellActive(SBSpells.DOLPHINS_FIN.get(), instance) || SpellUtil.hasSkillBuff(instance, SBSkills.POD_LEADER)) && !instance.hasEffect(MobEffects.DOLPHINS_GRACE)) {
            Vec3 vec36 = instance.getDeltaMovement();
            if (instance.horizontalCollision && instance.onClimbable()) {
                vec36 = new Vec3(vec36.x, 0.2, vec36.z);
            }

            float f = skills.hasSkill(SBSkills.MERMAIDS_TAIL) ? 0.96F : 0.9F;
            instance.setDeltaMovement(vec36.multiply(f, 0.8F, f));
        } else {
            instance.setDeltaMovement(vec3);
        }
    }

    @Inject(method = "updateInvisibilityStatus", at = @At("HEAD"))
    private void updateInvisibilityStatus(CallbackInfo info) {
        if (spellbound$self().hasEffect(SBEffects.MAGI_INVISIBILITY)) {
            spellbound$self().setInvisible(true);
        }
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/EntityHurtPlayerTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/damagesource/DamageSource;FFZ)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, float f, boolean flag) {
        SBTriggers.PLAYER_HURT.get().trigger((ServerPlayer) spellbound$self(), source, f, amount, flag);
    }

    @Unique
    private LivingEntity spellbound$self() {
        return (LivingEntity) (Object) this;
    }
}
