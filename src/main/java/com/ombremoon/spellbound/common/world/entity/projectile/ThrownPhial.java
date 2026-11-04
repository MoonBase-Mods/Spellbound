package com.ombremoon.spellbound.common.world.entity.projectile;

import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellModifier;
import com.ombremoon.spellbound.common.magic.skills.PseudoSkillProvider;
import com.ombremoon.spellbound.common.world.item.PhialItem;
import com.ombremoon.spellbound.datagen.ModTagProvider;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class ThrownPhial extends ThrowableItemProjectile implements ItemSupplier {
    public static final EntityDataAccessor<PhialItem.PhialType> PHIAL_TYPE = SynchedEntityData.defineId(ThrownPhial.class, SBEntityDataSerializers.PHIAL_TYPE.get());
    public static final Predicate<LivingEntity> HOLY_WATER_SENSITIVE_OR_ON_FIRE = livingEntity -> livingEntity.isSensitiveToWater() || livingEntity.isOnFire() || livingEntity.getType().is(EntityTypeTags.UNDEAD);
    public static final ResourceLocation DIVINE_BUFF_LOCATION = CommonClass.customLocation("divine_phial");
    public static final PseudoSkillProvider DIVINE_BUFF = PseudoSkillProvider.withoutSpell(DIVINE_BUFF_LOCATION);

    public ThrownPhial(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownPhial(LivingEntity shooter, PhialItem.PhialType phialType, Level level) {
        super(SBEntities.PHIAL.get(), shooter, level);
        this.setPhialType(phialType);
    }

    public ThrownPhial(double x, double y, double z, PhialItem.PhialType phialType, Level level) {
        super(SBEntities.PHIAL.get(), x, y, z, level);
        this.setPhialType(phialType);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PHIAL_TYPE, PhialItem.PhialType.DIVINE);
    }

    @Override
    protected Item getDefaultItem() {
        return SBItems.DIVINE_PHIAL.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            PhialItem.PhialType type = this.getPhialType();
            Direction direction = result.getDirection();
            BlockPos blockpos = result.getBlockPos();
            BlockPos blockpos1 = blockpos.relative(direction);
            this.handleBlockInteractions(type, blockpos1);
            this.handleBlockInteractions(type, blockpos1.relative(direction.getOpposite()));

            for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                this.handleBlockInteractions(type, blockpos1.relative(direction1));
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            PhialItem.PhialType type = this.getPhialType();
            if (type == PhialItem.PhialType.DIVINE) {
                this.applyWater();
            }

            this.applySplashFromType(type, result.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)result).getEntity() : null);
            for (int color : type.getColors()) {
                this.level().levelEvent(2007, this.blockPosition(), color);
            }

            this.discard();
        }
    }

    private void applyWater() {
        AABB aabb = this.getBoundingBox().inflate(4.0, 2.0, 4.0);

        for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, aabb, HOLY_WATER_SENSITIVE_OR_ON_FIRE)) {
            double d0 = this.distanceToSqr(livingentity);
            if (d0 < 16.0) {
                boolean undead = livingentity.getType().is(EntityTypeTags.UNDEAD);
                if (livingentity.isSensitiveToWater() || undead) {
                    livingentity.hurt(this.damageSources().indirectMagic(this, this.getOwner()), 1.0F);
                }

                if (livingentity.isOnFire() && livingentity.isAlive() || !undead) {
                    livingentity.extinguishFire();
                }
            }
        }

        for (Axolotl axolotl : this.level().getEntitiesOfClass(Axolotl.class, aabb)) {
            axolotl.rehydrate();
        }
    }

    private void applySplashFromType(PhialItem.PhialType type, @Nullable Entity entity) {
        switch (type) {
            case CURSED -> this.applyCursedSplash(entity);
            case ECLIPSE -> this.applyEclipseSplash(entity);
            default -> this.applyDivineSplash(entity);
        }
    }

    private void applyDivineSplash(@Nullable Entity entity) {
        AABB aabb = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
        if (!list.isEmpty()) {
            Entity owner = this.getEffectSource();

            for (LivingEntity livingEntity : list) {
                if (livingEntity.isAffectedByPotions()) {
                    double d0 = this.distanceToSqr(livingEntity);
                    if (d0 < 16.0) {
                        double d1;
                        if (livingEntity == entity) {
                            d1 = 1.0;
                        } else {
                            d1 = 1.0 - Math.sqrt(d0) / 4.0;
                        }

                        if (!(livingEntity.getType().is(EntityTypeTags.UNDEAD) || livingEntity.getType().is(Tags.EntityTypes.BOSSES))) {
                            var handler = SpellUtil.getSpellHandler(livingEntity);
                            SkillBuff<SpellModifier> buff = new SkillBuff<>(DIVINE_BUFF, DIVINE_BUFF_LOCATION, BuffCategory.BENEFICIAL, SkillBuff.SPELL_MODIFIER, SpellModifier.DIVINE_PHIAL);
                            handler.addSkillBuff(buff, owner, 400);

                            livingEntity.removeEffect(MobEffects.POISON);
                            livingEntity.removeEffect(MobEffects.WITHER);
                            livingEntity.removeEffect(SBEffects.DISEASE);
                        }
                    }
                }
            }
        }
    }

    private void applyCursedSplash(@Nullable Entity entity) {

    }

    private void applyEclipseSplash(@Nullable Entity entity) {

    }

    private void blessShrine() {

    }

    public PhialItem.PhialType getPhialType() {
        return this.entityData.get(PHIAL_TYPE);
    }

    public void setPhialType(PhialItem.PhialType phialType) {
        this.entityData.set(PHIAL_TYPE, phialType);
    }

    private void handleBlockInteractions(PhialItem.PhialType type, BlockPos pos) {
        BlockState blockstate = this.level().getBlockState(pos);
        Entity owner = this.getEffectSource();
        if (type == PhialItem.PhialType.DIVINE) {
            if (blockstate.is(BlockTags.FIRE)) {
                this.level().destroyBlock(pos, false, this);
            } else if (AbstractCandleBlock.isLit(blockstate)) {
                AbstractCandleBlock.extinguish(null, blockstate, this.level(), pos);
            } else if (CampfireBlock.isLitCampfire(blockstate)) {
                this.level().levelEvent(null, 1009, pos, 0);
                CampfireBlock.dowse(this.getOwner(), this.level(), pos, blockstate);
                this.level().setBlockAndUpdate(pos, blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(false)));
            }

            if (blockstate.is(SBTags.Blocks.DIVINE_SHRINE) && owner instanceof ServerPlayer player) {
                SBTriggers.BLESS_SHRINE.get().trigger(player);
            }
        }
    }

    @Override
    public DoubleDoubleImmutablePair calculateHorizontalHurtKnockbackDirection(LivingEntity entity, DamageSource damageSource) {
        double d0 = entity.position().x - this.position().x;
        double d1 = entity.position().z - this.position().z;
        return DoubleDoubleImmutablePair.of(d0, d1);
    }
}
