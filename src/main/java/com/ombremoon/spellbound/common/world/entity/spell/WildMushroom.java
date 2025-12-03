package com.ombremoon.spellbound.common.world.entity.spell;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.particle.EffectBuilder;
import com.ombremoon.spellbound.common.world.entity.SpellEntity;
import com.ombremoon.spellbound.common.world.entity.living.wildmushroom.GiantMushroom;
import com.ombremoon.spellbound.common.world.entity.living.wildmushroom.MiniMushroom;
import com.ombremoon.spellbound.common.world.spell.summon.WildMushroomSpell;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class WildMushroom extends SpellEntity<WildMushroomSpell> {
    public static final EntityDataAccessor<Integer> BOSS_PHASE = SynchedEntityData.defineId(WildMushroom.class, EntityDataSerializers.INT);

    public WildMushroom(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public WildMushroom(Level level, GiantMushroom owner) {
        super(SBEntities.MUSHROOM.get(), level);
        this.setOwner(owner);
        this.setPhase(owner.getPhase());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BOSS_PHASE, 1);
    }

    @Override
    public boolean requiresSpellToPersist() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isSpellCast()) {
            Level level = this.level();
            Entity owner = this.getOwner();
            int phase = this.getPhase();
            int interval = phase == 1 ? 60 : 40;
            if (this.tickCount % interval == 0) {
                if (!level.isClientSide) {
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3), livingEntity -> !(livingEntity instanceof MiniMushroom || livingEntity instanceof GiantMushroom));
                    for (LivingEntity livingEntity : entities) {
                        if (livingEntity.hurt(this.spellDamageSource(level), 8.0F * this.getPhase()) && (phase > 2 || owner instanceof GiantMushroom mush && mush.hasOwner())) {
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, this.getPhase()));
                        }
                    }
                } else {
                    this.addFX(
                            EffectBuilder.Entity.of(CommonClass.customLocation("mushroom_explosion"), this.getId(), EntityEffectExecutor.AutoRotate.NONE)
                                    .setOffset(0.0, -0.25, 0.0)
                    );
                }
            }

            if (this.tickCount % 240 == 0) {
                if (!level.isClientSide) {
                    if (owner instanceof GiantMushroom mushroom && this.getPhase() >= 2) {
                        MiniMushroom miniMushroom = new MiniMushroom(level, mushroom);
                        miniMushroom.setPos(this.position());
                        level.addFreshEntity(miniMushroom);
                    }
                    this.discard();
                }
            }
        }
    }

    public int getPhase() {
        return this.entityData.get(BOSS_PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(BOSS_PHASE, phase);
    }
}
