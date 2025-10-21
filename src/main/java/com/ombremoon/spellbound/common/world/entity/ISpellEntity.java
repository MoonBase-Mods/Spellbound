package com.ombremoon.spellbound.common.world.entity;

import com.ombremoon.spellbound.client.particle.FXEmitter;
import com.ombremoon.spellbound.common.init.SBDamageTypes;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

public interface ISpellEntity<T extends AbstractSpell> extends GeoEntity, FXEmitter {

    default Entity getEntity() {
        return (Entity) this;
    }

    EntityType<?> entityType();

    void setOwner(@NotNull Entity entity);

    @Nullable
    Entity getOwner();

    void setSpell(@NotNull AbstractSpell spell);

    T getSpell();

    boolean isSpellCast();

    default boolean requiresSpellToPersist() {
        return true;
    }

    default DamageSource spellDamageSource(Level level) {
        return SpellUtil.damageSource(level, SBDamageTypes.SB_GENERIC, this.getOwner(), this.getEntity());
    }
}
