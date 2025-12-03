package com.ombremoon.spellbound.client.model.entity.spell;

import com.ombremoon.spellbound.client.model.LivingModel;
import com.ombremoon.spellbound.common.world.entity.living.SpellBroker;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class SpellBrokerModel extends LivingModel<SpellBroker> {

    @Override
    public void setCustomAnimations(SpellBroker animatable, long instanceId, AnimationState<SpellBroker> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        GeoBone rightLeg = getAnimationProcessor().getBone("right_leg");
        GeoBone leftLeg = getAnimationProcessor().getBone("left_leg");

        if (rightLeg != null && leftLeg != null && !animatable.isDeadOrDying()) {
            float limbSwing = animationState.getLimbSwing();
            float limbSwingAmount = animationState.getLimbSwingAmount();

            rightLeg.setRotX(Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F);
            leftLeg.setRotX(Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F);
            rightLeg.setRotY(0.0F);
            leftLeg.setRotY(0.0F);
        }
    }
}
