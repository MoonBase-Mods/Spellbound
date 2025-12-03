package com.ombremoon.spellbound.common.world.entity.living;

import com.ombremoon.spellbound.common.world.entity.SmartSpellEntity;
import com.ombremoon.spellbound.common.world.spell.deception.ShadowbondSpell;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;

import java.util.List;

public class LivingShadow extends SmartSpellEntity<ShadowbondSpell> {
    public LivingShadow(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= 200) discard();
    }

    public static AttributeSupplier.Builder createLivingShadowAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1.0D);
    }

    @Override
    public List<? extends ExtendedSensor<? extends SmartSpellEntity<ShadowbondSpell>>> getSensors() {
        return ObjectArrayList.of();
    }
}
