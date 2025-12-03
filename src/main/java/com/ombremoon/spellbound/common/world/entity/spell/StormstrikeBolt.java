package com.ombremoon.spellbound.common.world.entity.spell;

import com.ombremoon.spellbound.common.world.entity.SpellProjectile;
import com.ombremoon.spellbound.common.world.spell.ruin.shock.StormstrikeSpell;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class StormstrikeBolt extends SpellProjectile<StormstrikeSpell> {
    public StormstrikeBolt(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

}
