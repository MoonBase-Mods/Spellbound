package com.ombremoon.spellbound.common.magic.acquisition.guides.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.SpellInfoExtras;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record SpellPredicate(Optional<ResourceLocation> spell) {
    public static final Codec<SpellPredicate> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    ResourceLocation.CODEC.optionalFieldOf("spell").forGetter(SpellPredicate::spell)
            ).apply(inst, SpellPredicate::new));

    public static SpellPredicate spell(Optional<ResourceLocation> spell) {
        return new SpellPredicate(spell);
    }

    public static SpellPredicate spell(SpellType<?> spell) {
        return new SpellPredicate(Optional.ofNullable(SBSpells.REGISTRY.getKey(spell)));
    }

    public boolean matches(SpellType<?> spell) {
        return this.spell.isPresent() && SBSpells.REGISTRY.getKey(spell).equals(this.spell.get());
    }
}
