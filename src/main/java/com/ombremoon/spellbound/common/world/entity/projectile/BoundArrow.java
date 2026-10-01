package com.ombremoon.spellbound.common.world.entity.projectile;

import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.DataComponentStorage;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.world.spell.summon.BoundBowSpell;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.Nullable;

public class BoundArrow extends AbstractArrow {
    private static final EntityDataAccessor<ArrowVariant> ARROW_VARIANT = SynchedEntityData.defineId(BoundArrow.class, SBEntityDataSerializers.ARROW_VARIANT.get());
    private static final EntityDataAccessor<Boolean> IS_HOMING = SynchedEntityData.defineId(BoundArrow.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HOMING_TARGET_ID = SynchedEntityData.defineId(BoundArrow.class, EntityDataSerializers.INT);
    private boolean hitBlock;
    private int blockDespawnTick;

    public BoundArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public BoundArrow(Level level, double x, double y, double z, @Nullable ItemStack firedFromWeapon) {
        super(SBEntities.BOUND_ARROW.get(), x, y, z, level, new ItemStack(Items.ARROW), firedFromWeapon);
    }

    public BoundArrow(Level level, LivingEntity owner, @Nullable ItemStack firedFromWeapon) {
        super(SBEntities.BOUND_ARROW.get(), owner, level, new ItemStack(Items.ARROW), firedFromWeapon);
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ARROW_VARIANT, ArrowVariant.MAGIC);
        builder.define(IS_HOMING, false);
        builder.define(HOMING_TARGET_ID, -1);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return Items.ARROW.getDefaultInstance();
    }

    @Override
    public void tick() {
        /*if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.hasBeenShot = true;
        }*/

        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }

        if (this.firstTick) {
            this.firstTick = false;
        }

        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI));
            this.setXRot((float)(Mth.atan2(vec3.y, d0) * 180.0F / (float)Math.PI));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        if (this.isHoming()) {
            Entity entity = this.getHomingTarget();
            if (entity instanceof LivingEntity) {
                Vec3 vec31 = entity.position().add(0.0, entity.getBbHeight() / 2, 0.0).subtract(this.position());
                vec3 = vec31.normalize().scale(1.5F);
                this.setDeltaMovement(vec3);
            }
        }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level().getBlockState(blockpos);
        if (!blockstate.isAir()) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = this.position();

                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        this.hitBlock = true;
                        break;
                    }
                }
            }
        }

        if (this.hitBlock && !this.level().isClientSide) {
            this.tickDespawn();
        }

        this.inGroundTime = 0;
        Vec3 vec32 = this.position();
        Vec3 vec33 = vec32.add(vec3);
        HitResult hitresult = this.level().clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        while (!this.isRemoved()) {
            EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
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

        vec3 = this.getDeltaMovement();
        double d5 = vec3.x;
        double d6 = vec3.y;
        double d1 = vec3.z;
        if (this.isCritArrow()) {
            for (int i = 0; i < 4; i++) {
                this.level()
                        .addParticle(
                                ParticleTypes.CRIT,
                                this.getX() + d5 * (double)i / 4.0,
                                this.getY() + d6 * (double)i / 4.0,
                                this.getZ() + d1 * (double)i / 4.0,
                                -d5,
                                -d6 + 0.2,
                                -d1
                        );
            }
        }

        double d7 = this.getX() + d5;
        double d2 = this.getY() + d6;
        double d3 = this.getZ() + d1;
        double d4 = vec3.horizontalDistance();
        this.setYRot((float)(Mth.atan2(d5, d1) * 180.0F / (float)Math.PI));
        this.setXRot((float)(Mth.atan2(d6, d4) * 180.0F / (float)Math.PI));
        this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
        this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
        float f = 0.99F;
        if (this.isInWater()) {
            for (int j = 0; j < 4; j++) {
                float f1 = 0.25F;
                this.level().addParticle(ParticleTypes.BUBBLE, d7 - d5 * f1, d2 - d6 * f1, d3 - d1 * f1, d5, d6, d1);
            }

            f = this.getWaterInertia();
        }

        this.setDeltaMovement(vec3.scale(f));
        this.applyGravity();
        this.setPos(d7, d2, d3);
        this.checkInsideBlocks();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity owner = this.getOwner();
        Entity resultEntity = result.getEntity();
        if (owner instanceof LivingEntity caster && resultEntity instanceof LivingEntity target) {
            BoundBowSpell spell = SBSpells.BOUND_BOW.get().createSpellWithData(caster);
            SpellContext context = spell.getContext();
            if (context.hasSkill(SBSkills.BOUND_ARROW)) {
                spell.addSkillBuff(
                        target,
                        SBSkills.BOUND_ARROW,
                        BuffCategory.HARMFUL,
                        SkillBuff.DATA_ATTACHMENT,
                        DataComponentStorage.of(SBData.BOUND_ARROW_MARK, Unit.INSTANCE),
                        200
                );
            }

            var targetHandler = SpellUtil.getSpellHandler(target);
            if (targetHandler.hasSkillBuff(SBSkills.BOUND_ARROW.value())) {
                //Fill Soul Shard
            }
        }

        super.onHitEntity(result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BlockPos pos = result.getBlockPos();
        this.lastState = this.level().getBlockState(pos);
        BlockState blockstate = this.level().getBlockState(pos);
        blockstate.onProjectileHit(this.level(), blockstate, result, this);

        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity living) {
            BoundBowSpell spell = SBSpells.BOUND_BOW.get().createSpellWithData(living);
            SpellContext context = spell.getContext();
            if (context.hasSkill(SBSkills.WRAITH_SHOT)) {
                this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                return;
            }
        }

        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.setCritArrow(false);
        this.setPierceLevel((byte)0);
        this.setSoundEvent(SoundEvents.ARROW_HIT);
        this.resetPiercedEntities();
        this.discard();
    }

    @Override
    protected void tickDespawn() {
        this.blockDespawnTick++;
        if (this.blockDespawnTick >= 5) {
            this.discard();
        }
    }

    public void setVariant(ArrowVariant variant) {
        this.entityData.set(ARROW_VARIANT, variant);
    }

    public ArrowVariant getVariant() {
        return this.entityData.get(ARROW_VARIANT);
    }

    public boolean isHoming() {
        return this.entityData.get(IS_HOMING);
    }

    public void setHoming(boolean homing) {
        this.entityData.set(IS_HOMING, homing);
    }

    public Entity getHomingTarget() {
        return this.level().getEntity(this.entityData.get(HOMING_TARGET_ID));
    }

    public void setHomingTarget(LivingEntity entity) {
        this.setHoming(true);
        this.entityData.set(HOMING_TARGET_ID, entity.getId());
    }

    public enum ArrowVariant {
        MAGIC("bound", SBDamageTypes.SB_GENERIC),
        FIRE("fire", SBDamageTypes.RUIN_FIRE),
        ICE("ice", SBDamageTypes.RUIN_FROST),
        SHOCK("shock", SBDamageTypes.RUIN_SHOCK);

        public static StreamCodec<RegistryFriendlyByteBuf, ArrowVariant> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(ArrowVariant.class);
        private final String name;
        private final ResourceKey<DamageType> damageType;

        ArrowVariant(String name, ResourceKey<DamageType> damageType) {
            this.name = name;
            this.damageType = damageType;
        }

        public String getName() {
            return this.name;
        }

        public ResourceKey<DamageType> getDamageType() {
            return this.damageType;
        }
    }
}
