package com.ombremoon.spellbound.common.magic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

public record SpellInstance(SpellType<?> spellType, int spellId) {
    public static final Codec<SpellInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    SBSpells.REGISTRY.byNameCodec().fieldOf("spell").forGetter(SpellInstance::spellType),
                    Codec.INT.fieldOf("spellId").forGetter(SpellInstance::spellId)
            ).apply(instance, SpellInstance::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SpellInstance> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(SBSpells.SPELL_TYPE_REGISTRY_KEY), SpellInstance::spellType,
            ByteBufCodecs.VAR_INT, SpellInstance::spellId,
            SpellInstance::new
    );

    public static SpellInstance fromSpell(AbstractSpell spell) {
        return new SpellInstance(spell.spellType(), spell.getId());
    }

    public boolean isSameSpell(SpellInstance instance) {
        return this.spellType == instance.spellType;
    }

    public AbstractSpell getOrCreateSpell(LivingEntity caster) {
        var handler = SpellUtil.getSpellHandler(caster);
        AbstractSpell spell = handler.getSpell(this.spellType, this.spellId);
        if (spell == null) {
            spell = this.spellType.createSpellWithData(caster);
        }

        return spell;
    }
}
