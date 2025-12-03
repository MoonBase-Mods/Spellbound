package com.ombremoon.spellbound.common.world.entity;

import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.Map;

public abstract class PortalEntity<T extends AbstractSpell> extends SpellEntity<T> {
    private final Map<Integer, Integer> portalCooldown = new Object2IntOpenHashMap<>();

    public PortalEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        this.portalCooldown.entrySet().removeIf(entry -> entry.getValue() <= this.tickCount);
    }

    public abstract int getPortalCooldown();

    public void addCooldown(Entity entity) {
        this.portalCooldown.put(entity.getId(), this.tickCount + this.getPortalCooldown());
    }

    public boolean isOnCooldown(Entity entity) {
        return this.portalCooldown.containsKey(entity.getId());
    }
}
