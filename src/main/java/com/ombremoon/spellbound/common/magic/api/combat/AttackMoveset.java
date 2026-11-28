package com.ombremoon.spellbound.common.magic.api.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.api.SBAnimation;

import java.util.List;

public record AttackMoveset(List<SBAnimation> animations) {
    public static final Codec<AttackMoveset> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    SBAnimation.CODEC.listOf().fieldOf("animations").forGetter(AttackMoveset::animations)
            ).apply(instance, AttackMoveset::new)
    );

    public boolean isEmpty() {
        return this.animations.isEmpty();
    }

    public int size() {
        return this.animations.size();
    }

    public SBAnimation getAnimation(int index) {
        return this.animations.get(index);
    }
}
