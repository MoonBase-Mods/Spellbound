package com.ombremoon.spellbound.common.world;

import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SpellDamageSource extends DamageSource {
    private final AbstractSpell spell;

    public SpellDamageSource(Holder<DamageType> type, AbstractSpell spell, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition) {
        super(type, directEntity, causingEntity, damageSourcePosition);
        this.spell = spell;
    }

    public SpellDamageSource(Holder<DamageType> type, AbstractSpell spell, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        this(type, spell, directEntity, causingEntity, null);
    }

    public SpellDamageSource(Holder<DamageType> type, AbstractSpell spell, Vec3 damageSourcePosition) {
        this(type, spell, null, null, damageSourcePosition);
    }

    public SpellDamageSource(Holder<DamageType> type, AbstractSpell spell, @Nullable Entity entity) {
        this(type, spell, entity, entity);
    }

    public SpellDamageSource(Holder<DamageType> type, AbstractSpell spell) {
        this(type, spell, null, null, null);
    }

    public AbstractSpell getSpell() {
        return this.spell;
    }

    public boolean isSpell(AbstractSpell spell) {
        if (this.spell == null)
            return false;

        return this.spell.isSpellType(spell);
    }
}
