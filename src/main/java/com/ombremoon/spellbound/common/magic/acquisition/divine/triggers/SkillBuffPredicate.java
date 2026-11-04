package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.skills.SkillProvider;

import java.util.Optional;

public record SkillBuffPredicate(Optional<SkillProvider> skill) {
    public static final MapCodec<SkillBuffPredicate> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    SkillProvider.CODEC.optionalFieldOf("skill").forGetter(SkillBuffPredicate::skill)
            ).apply(instance, SkillBuffPredicate::new)
    );

    public static SkillBuffPredicate isSkill(SkillProvider skill) {
        return new SkillBuffPredicate(Optional.of(skill));
    }

    public boolean matches(SkillBuff<?> skillBuff) {
        return this.skill.isEmpty() || skillBuff.isSkill(this.skill().get());
    }
}
