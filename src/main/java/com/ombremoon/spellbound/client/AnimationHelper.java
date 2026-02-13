package com.ombremoon.spellbound.client;

import com.ombremoon.spellbound.common.magic.api.SpellAnimation;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.SpellUtil;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractFadeModifier;
import com.zigythebird.playeranimcore.animation.layered.modifier.SpeedModifier;
import com.zigythebird.playeranimcore.easing.EasingType;
import net.minecraft.client.player.AbstractClientPlayer;

public class AnimationHelper {

    public static void playAnimation(AbstractClientPlayer player, SpellAnimation animation, float animationSpeed) {
        PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(player, animation.type().getAnimationLayer());
        if (controller != null) {
            controller.addModifier(new SpeedModifier(animationSpeed), 0);
            controller.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(2, EasingType.EASE_IN_OUT_SINE), animation.animation(), !controller.isPlayingTriggeredAnimation());
        }
    }

    public static void stopAnimation(AbstractClientPlayer player, SpellAnimation animation) {
        PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(player, animation.type().getAnimationLayer());
        if (controller == null)
            return;

        if (isAnimationPlaying(controller, animation)) {
            controller.stopTriggeredAnimation();
        }
    }

    public static void tick(AbstractClientPlayer player) {
        PlayerAnimationController spellController = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(player, SpellAnimation.SPELL_CAST_ANIMATION);
        if (spellController != null) {
            if (!spellController.isActive() && spellController.getModifierCount() > 0) {
                spellController.removeAllModifiers();
            } else if (spellController.isActive()) {
                player.setYBodyRot(player.getYHeadRot());
                PayloadHandler.updateRotation(player.yBodyRot);
                var handler = SpellUtil.getSpellHandler(player);
                SpellAnimation animation = handler.getAnimationForLayer(SpellAnimation.SPELL_CAST_ANIMATION);
                if (animation != null && isAnimationPlaying(spellController, animation) && animation.stationary())
                    handler.setStationaryTicks(1);
            }
        }
    }

    public static boolean isAnimationPlaying(PlayerAnimationController controller, SpellAnimation animation) {
        return controller != null && controller.getCurrentAnimation() != null && controller.getCurrentAnimation().animation().getNameOrId().equals(animation.animation().getPath());
    }
}
