package com.ombremoon.spellbound.common.world;

import com.ombremoon.spellbound.common.DamageInstance;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpellDamageSource extends DamageSource {
    @Nullable
    private final AbstractSpell spell;
    private final Set<ResourceKey<DamageType>> extraDamageTypes = new HashSet<>();
    private final List<DamageInstance> extraDamage = new ArrayList<>();

    public SpellDamageSource(Holder<DamageType> type, @Nullable AbstractSpell spell, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition) {
        super(type, directEntity, causingEntity, damageSourcePosition);
        this.spell = spell;
    }

    public SpellDamageSource(Holder<DamageType> type, @Nullable AbstractSpell spell, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        this(type, spell, directEntity, causingEntity, null);
    }

    public SpellDamageSource(Holder<DamageType> type, @Nullable AbstractSpell spell, Vec3 damageSourcePosition) {
        this(type, spell, null, null, damageSourcePosition);
    }

    public SpellDamageSource(Holder<DamageType> type, @Nullable AbstractSpell spell, @Nullable Entity entity) {
        this(type, spell, entity, entity);
    }

    public SpellDamageSource(Holder<DamageType> type, @Nullable AbstractSpell spell) {
        this(type, spell, null, null, null);
    }

    public SpellDamageSource addDamageInstance(DamageInstance instance) {
        this.extraDamage.add(instance);
        this.extraDamageTypes.add(instance.damageType());
        return this;
    }

    public void modifyDamage(LivingDamageEvent.Pre event) {
        float damage = event.getNewDamage();
        LivingEntity entity = event.getEntity();
        for (DamageInstance instance : this.extraDamage) {
            var optional = instance.exclusiveTo();
            if (optional.isEmpty() || entity.getType().is(optional.get())) {
                damage += instance.amount();
            }
        }

        if (entity.hasEffect(SBEffects.PERMAFROST))
            damage *= 1.15F;

        event.setNewDamage(damage);
    }

    @Override
    public boolean is(ResourceKey<DamageType> damageTypeKey) {
        return this.extraDamageTypes.contains(damageTypeKey) || super.is(damageTypeKey);
    }

    public @Nullable AbstractSpell getSpell() {
        return this.spell;
    }

    public boolean isSpell(AbstractSpell spell) {
        if (this.spell == null)
            return false;

        return this.spell.isSpellType(spell);
    }

    public static SpellDamageSource fromVanillaSource(DamageSource source) {
        if (source instanceof SpellDamageSource spellSource) {
            return spellSource;
        }

        return new SpellDamageSource(source.typeHolder(), null, source.getDirectEntity(), source.getEntity(), source.getSourcePosition());
    }
}
