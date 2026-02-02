package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HurtByTargetGoal.class)
public class HurtByTargetGoalMixin extends TargetGoal {

    public HurtByTargetGoalMixin(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @Inject(method = "alertOthers", at = @At("HEAD"), cancellable = true)
    private void alertOthers(CallbackInfo ci) {
        if (SpellUtil.isSummon(this.mob)) {
            ci.cancel();
        }
    }

    @Override
    public boolean canUse() {
        return false;
    }
}
