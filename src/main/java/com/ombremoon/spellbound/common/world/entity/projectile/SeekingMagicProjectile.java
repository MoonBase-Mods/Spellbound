package com.ombremoon.spellbound.common.world.entity.projectile;

import com.ombremoon.spellbound.common.init.SBDamageTypes;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.magic.BuildUpEffect;
import com.ombremoon.spellbound.common.magic.EffectManager;
import com.ombremoon.spellbound.common.magic.familiars.Familiar;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.OptionalInt;

public class SeekingMagicProjectile extends Arrow {
    private static final EntityDataAccessor<OptionalInt> TARGET_ID = SynchedEntityData.defineId(SeekingMagicProjectile.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final EntityDataAccessor<OptionalInt> OWNER_ID = SynchedEntityData.defineId(SeekingMagicProjectile.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);


    public SeekingMagicProjectile(EntityType<? extends Arrow> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public SeekingMagicProjectile(Level level, LivingEntity target, LivingEntity owner) {
        this(SBEntities.SEEKING_PROJECTILE.get(), level);
        setTarget(target);
        setOwner(owner);
    }

    public void setOwner(LivingEntity owner) {
        this.entityData.set(OWNER_ID, OptionalInt.of(owner.getId()));
    }

    public LivingEntity getOwner() {
        if (this.entityData.get(OWNER_ID).isEmpty()) return null;
        return (LivingEntity) level().getEntity(this.entityData.get(OWNER_ID).getAsInt());
    }

    public void setTarget(LivingEntity target) {
        this.entityData.set(TARGET_ID, OptionalInt.of(target.getId()));
    }

    public LivingEntity getTarget() {
        if (this.entityData.get(TARGET_ID).isEmpty()) return null;
        return (LivingEntity) level().getEntity(this.entityData.get(TARGET_ID).getAsInt());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TARGET_ID, OptionalInt.empty());
        builder.define(OWNER_ID, OptionalInt.empty());
    }

    @Override
    public void tick() {
        LivingEntity target = getTarget();
        if (target == null) {
            this.discard();
            return;
        }

        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333) - this.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        Vec3 vec3 = new Vec3(d0, d1 + d3 * 0.2F, d2).normalize().scale(0.7f);

        Vec3 currentPos = this.position();
        Vec3 destPos = currentPos.add(vec3);

        HitResult hitresult = this.level().clip(new ClipContext(currentPos, destPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitresult.getType() != HitResult.Type.MISS) {
            destPos = hitresult.getLocation();
        }

        while (!this.isRemoved()) {
            EntityHitResult entityhitresult = this.findHitEntity(currentPos, destPos);
            if (entityhitresult != null) {
                hitresult = entityhitresult;
            }

            if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)hitresult).getEntity();
                Entity entity1 = this.getOwner();
                if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                    hitresult = null;
                    entityhitresult = null;
                }
            }

            if (hitresult != null && hitresult.getType() != HitResult.Type.MISS) {
                if (net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, hitresult))
                    break;
                ProjectileDeflection projectiledeflection = this.hitTargetOrDeflectSelf(hitresult);
                this.hasImpulse = true;
                if (projectiledeflection != ProjectileDeflection.NONE) {
                    break;
                }
            }

            if (entityhitresult == null || this.getPierceLevel() <= 0) {
                break;
            }

            hitresult = null;
        }


        this.setDeltaMovement(vec3);
        this.setPos(
                this.getX() + vec3.x(),
                this.getY() + vec3.y(),
                this.getZ() + vec3.z()
        );

        if (level().isClientSide) {
            level().addParticle(ParticleTypes.EFFECT, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        LivingEntity owner = getOwner();
        return owner == null || (getOwner() != null && !target.is(getOwner())) || SpellUtil.isSummonOf(target, owner);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!(result.getEntity() instanceof LivingEntity entity)) return;
        float damage = 1f;
        DamageSource damagesource = damageSources().source(SBDamageTypes.SB_GENERIC);

        boolean flag = entity.hurt(damagesource, damage);
        if (flag) {
            if (getOwner() instanceof LivingEntity summoner) {
                var handler = SpellUtil.getFamiliarHandler(summoner);
                handler.awardBond(handler.getSelectedFamiliar(), damage * (1.0F + Familiar.HURT_XP_MODIFIER));
                summoner.setLastHurtMob(entity);
            }

            var effectManager = SpellUtil.getSpellEffects(entity);
            BuildUpEffect effect = effectManager.getEffectFromDamageType(damagesource.typeHolder().getKey());

            if (effect != null)
                effectManager.incrementBuildEffects(effect, damage);

            entity.knockback((0.5F), Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, (double)1.0F, 0.6));


            if (level() instanceof ServerLevel server) {
                ServerLevel serverlevel1 = server;
                EnchantmentHelper.doPostAttackEffects(serverlevel1, entity, damagesource);
            }
        }

        this.discard();
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return false;
    }
}
