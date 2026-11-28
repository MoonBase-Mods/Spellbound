package com.ombremoon.spellbound.common.magic.api.combat;

import com.ombremoon.spellbound.common.magic.api.SBAnimation;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.player.AbstractClientPlayer;

import java.util.Map;

public class PlayerAnimData {
    private final AbstractClientPlayer player;
    private final Map<AnimationRule, Boolean> rules = new Object2ObjectArrayMap<>();
    private SBAnimation currentAnimation;
    private int currentAnimationTick;
    private boolean animationActive;

    public PlayerAnimData(AbstractClientPlayer player) {
        this.player = player;
    }

    public AbstractClientPlayer getPlayer() {
        return this.player;
    }

    public void tick() {
        this.updateAnimationRestrictions();
    }

    public void updateAnimationRestrictions() {
        if (this.currentAnimation != null && this.animationActive) {
            AnimProperties properties = this.currentAnimation.properties();
            if (this.currentAnimationTick > properties.startTick() && ) {

            }
        }
    }

    public boolean checkAnimationRule(AnimationRule rule) {
        return this.rules.getOrDefault(rule, rule.defaultRule());
    }
}
