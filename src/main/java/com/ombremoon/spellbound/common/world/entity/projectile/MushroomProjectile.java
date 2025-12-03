package com.ombremoon.spellbound.common.world.entity.projectile;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.particle.EffectBuilder;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.world.entity.SpellProjectile;
import com.ombremoon.spellbound.common.world.entity.living.wildmushroom.GiantMushroom;
import com.ombremoon.spellbound.common.world.entity.spell.WildMushroom;
import com.ombremoon.spellbound.common.world.spell.summon.WildMushroomSpell;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.tslat.smartbrainlib.util.RandomUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MushroomProjectile extends SpellProjectile<WildMushroomSpell> {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean primaryProjectile;

    public MushroomProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public MushroomProjectile(Level level, LivingEntity thrower) {
        this(SBEntities.MUSHROOM_PROJECTILE.get(), level);
        this.setOwner(thrower);
        this.setPos(
                thrower.getX() - (double)(thrower.getBbWidth() + 1.0F) * 0.5 * (double) Mth.sin(thrower.yBodyRot * (float) (Math.PI / 180.0)),
                thrower.getEyeY() - 0.1F,
                thrower.getZ() + (double)(thrower.getBbWidth() + 1.0F) * 0.5 * (double)Mth.cos(thrower.yBodyRot * (float) (Math.PI / 180.0))
        );
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (this.level().isClientSide) {
            this.addFX(EffectBuilder.Entity.of(
                    CommonClass.customLocation("toxic_projectile"),
                    this.getId(),
                    EntityEffectExecutor.AutoRotate.LOOK)
                    .setRotation(180, 180, 0)
                    .setOffset(0, -0.5, 0));
        }
    }

    @Override
    public void onClientRemoval() {
        this.removeFX(CommonClass.customLocation("toxic_projectile"), false);
    }

    protected double getDefaultGravity() {
        return 0.12;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isInWaterOrBubble())
            this.discard();

    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Level level = this.level();
        if (!level.isClientSide) {
            Entity entity = result.getEntity();
            if (!(entity instanceof MushroomProjectile || entity instanceof WildMushroom)) {
                if (this.getOwner() instanceof GiantMushroom mushroom) {
                    DamageSource damagesource = mushroom.spellDamageSource(level);
                    if (entity instanceof LivingEntity livingEntity && mushroom.hurtTarget(livingEntity, damagesource, 8.0F * mushroom.getPhase())) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, mushroom.getPhase()));
                    }
                }

                this.discard();
            }
        } else {
            this.removeFX(CommonClass.customLocation("toxic_projectile"), false);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        Level level = this.level();
        if (!level.isClientSide) {
            Entity entity = this.getOwner();
            if (entity instanceof GiantMushroom mushroom && result.getDirection() == Direction.UP) {
                if (mushroom.getPhase() >= 2 && this.isPrimaryProjectile()) {
                    int numMushrooms = RandomUtil.randomNumberBetween(4, 6);
                    float x = (float) Math.toDegrees(-2 * Mth.PI / 3);
                    for (int i = 0; i < numMushrooms; i++) {
                        MushroomProjectile mushroomProjectile = new MushroomProjectile(level, mushroom);
                        mushroomProjectile.setPos(result.getLocation());
                        float y = (float) Math.toDegrees(i * Mth.TWO_PI / numMushrooms);
                        float f = -Mth.sin(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
                        float f1 = -Mth.sin(x * 0.017453292F);
                        float f2 = Mth.cos(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
                        mushroomProjectile.shoot(f, f1, f2, (float) RandomUtil.randomValueBetween(0.5, 0.75), 1.0F);
                        level.addFreshEntity(mushroomProjectile);
                    }
                } else {
                    WildMushroom wildMushroom = new WildMushroom(level, mushroom);
                    wildMushroom.setPos(result.getLocation());
                    level.addFreshEntity(wildMushroom);
                }
            }
        } else {
            this.removeFX(CommonClass.customLocation("toxic_projectile"));
        }

        this.discard();
    }

    public boolean isPrimaryProjectile() {
        return this.primaryProjectile;
    }

    public void setPrimaryProjectile(boolean primaryProjectile) {
        this.primaryProjectile = primaryProjectile;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
