package com.ombremoon.spellbound.common.world.entity.living.wildmushroom;

import com.ombremoon.spellbound.common.world.entity.SmartSpellEntity;
import com.ombremoon.spellbound.common.world.spell.summon.WildMushroomSpell;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public abstract class LivingMushroom extends SmartSpellEntity<WildMushroomSpell> {
    protected static final String EXPLOSION = "explosion";
    private static final EntityDataAccessor<Boolean> EXPLODING = SynchedEntityData.defineId(LivingMushroom.class, EntityDataSerializers.BOOLEAN);
    protected int explosionTimer = 24;

    protected LivingMushroom(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(EXPLODING, false);
    }

    public boolean isExploding() {
        return this.entityData.get(EXPLODING);
    }

    public void setExploding(boolean exploding) {
        this.entityData.set(EXPLODING, exploding);
    }
}
