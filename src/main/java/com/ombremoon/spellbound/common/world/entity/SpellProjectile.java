package com.ombremoon.spellbound.common.world.entity;

import com.ombremoon.spellbound.client.photon.EffectCache;
import com.ombremoon.spellbound.common.init.SBEntityDataSerializers;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.SpellUtil;
import com.ombremoon.spellbound.util.math.SplineController;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

@SuppressWarnings("unchecked")
public abstract class SpellProjectile<T extends AbstractSpell> extends Projectile implements ISpellEntity<T>, SBSummonable {
    private static final EntityDataAccessor<String> SPELL_TYPE = SynchedEntityData.defineId(SpellProjectile.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> SPELL_ID = SynchedEntityData.defineId(SpellProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(SpellProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_HOMING = SynchedEntityData.defineId(SpellProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HOMING_TARGET_ID = SynchedEntityData.defineId(SpellProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(SpellProjectile.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<SplineController> SPLINE = SynchedEntityData.defineId(SpellProjectile.class, SBEntityDataSerializers.SPLINE_CONTROLLER.get());
    protected static final String CONTROLLER = "controller";
    protected T spell;
    protected SpellHandler handler;
    protected SkillHolder skills;
    private boolean isSpellCast;
    private boolean clientInit;
    @Nullable
    private IntOpenHashSet piercingIgnoreEntityIds;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final EffectCache effectCache = new EffectCache();

    //TODO: Change Homing Target to Homing Position
    protected SpellProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected double getDefaultGravity() {
        return this.isHoming() ? 0.0 : 0.06;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SPELL_TYPE, "");
        builder.define(SPELL_ID, -1);
        builder.define(OWNER_ID, 0);
        builder.define(IS_HOMING, false);
        builder.define(HOMING_TARGET_ID, -1);
        builder.define(PIERCE_LEVEL, (byte) 0);
        builder.define(SPLINE, SplineController.empty());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isSpellCast", this.isSpellCast);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.isSpellCast = compound.getBoolean("isSpellCast");
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.spell != null) {
            this.spell.onEntityTick(this, this.spell.getContext());
        }

        Vec3 vec3 = this.getDeltaMovement();

        SplineController spline = this.getSplineController();
        boolean canHome = this.getHomingTarget() != null && this.getHomingTarget().isAlive();
        boolean flag = !spline.isEmptySpline() && spline.isFollowingSpline();
        if (canHome) {
            if (flag) {
                Vec3 trajectoryGravity = spline.getGravityAtPosition(this.position());
                vec3 = vec3.add(trajectoryGravity);
            } else if (this.isHoming()) {
                Entity entity = this.getHomingTarget();
                if (entity instanceof LivingEntity) {
                    Vec3 vec31 = entity.position().add(0.0, entity.getBbHeight() / 2, 0.0).subtract(this.position());
                    vec3 = vec31.normalize();
                    this.setDeltaMovement(vec3);
                }
            }
        }
        
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult))
            this.hitTargetOrDeflectSelf(hitresult);
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        this.updateRotation();
        float f = 0.99F;
        if (this.isInWaterOrBubble()) {
            f = 0.89F;
        }
        this.setDeltaMovement(vec3.scale(f));
        if (!flag)
            this.applyGravity();

        this.setPos(d0, d1, d2);

        if (this.level().isClientSide && !this.clientInit) {
            this.initializeClientEntity();
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(target.getId()));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (this.getPierceLevel() > 0) {
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }

            if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
                this.discard();
                return;
            }

            this.piercingIgnoreEntityIds.add(entity.getId());
        }

        if (!this.level().isClientSide) {
            if (this.spell != null)
                this.spell.onProjectileHitEntity(this, spell.getContext(), result);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            if (this.spell != null)
                this.spell.onProjectileHitBlock(this, spell.getContext(), result);
        }
    }

    @Override
    public boolean isSpellCast() {
        return this.isSpellCast;
    }

    @Override
    public boolean isNoGravity() {
        return this.isHoming() || super.isNoGravity();
    }

    @Override
    public void onAddedToLevel() {
        if (this.getSummoner() instanceof LivingEntity livingEntity) {
            this.handler = SpellUtil.getSpellHandler(livingEntity);
            this.skills = SpellUtil.getSkills(livingEntity);
        }
        super.onAddedToLevel();
    }

    private void initializeClientEntity() {
        if (this.getSummoner() instanceof Player player /*or instanceof SpellCaster*/) {
            this.handler = SpellUtil.getSpellHandler(player);
            this.skills = SpellUtil.getSkills(player);
            this.getOrCreateSpell();
            this.clientInit = true;
        }
    }

    public T getOrCreateSpell() {
        if (this.spell == null) {
            SpellType<T> spellType = this.getSpellType();
            if (this.handler != null && spellType != null) {
                this.spell = this.handler.getSpell(spellType, this.getSpellId());
                if (this.spell == null && this.getSummoner() instanceof LivingEntity caster) {
                    this.spell = spellType.createSpellWithData(caster);
                }
            }
        }

        return this.spell;
    }

    public void setSpell(@NotNull AbstractSpell spell) {
        this.spell = (T) spell;
        this.setSpellType(spell.spellType());
        this.setSpellId(spell.getId());
        this.isSpellCast = true;
    }

    public SpellType<T> getSpellType(){
        return (SpellType<T>) SBSpells.REGISTRY.get(ResourceLocation.tryParse(this.entityData.get(SPELL_TYPE)));
    }

    public void setSpellType(SpellType<?> spellType) {
        this.entityData.set(SPELL_TYPE, spellType.location().toString());
    }

    public int getSpellId(){
        return this.entityData.get(SPELL_ID);
    }

    public void setSpellId(int id) {
        this.entityData.set(SPELL_ID, id);
    }

    protected boolean isOwner(LivingEntity entity) {
        return getSummoner() != null && getSummoner().is(entity);
    }

    public void setOwner(LivingEntity livingEntity) {
        this.entityData.set(OWNER_ID, livingEntity.getId());
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

    public void setPierceLevel(byte pierceLevel) {
        this.entityData.set(PIERCE_LEVEL, pierceLevel);
    }

    public byte getPierceLevel() {
        return this.entityData.get(PIERCE_LEVEL);
    }

    /**
     * Attaches a spline movement handler to this projectile.
     * Call this after creating the projectile to enable curved trajectory.
     */
    public void setSplineController(SplineController spline) {
        this.entityData.set(SPLINE, spline);
    }

    /**
     * Gets the spline movement handler if one is attached.
     */
    public SplineController getSplineController() {
        return this.entityData.get(SPLINE);
    }

    @Override
    public @Nullable Entity getSummoner() {
        return this.level().getEntity(this.entityData.get(OWNER_ID));
    }

    @Override
    public void setSummoner(int id) {
        this.entityData.set(OWNER_ID, id);
    }

    @Override
    public boolean isSummoner(LivingEntity entity) {
        Entity owner = this.getSummoner();
        return owner != null && owner.is(entity);
    }

    @Override
    public boolean hasSummoner() {
        Entity owner = this.getSummoner();
        return owner != null && owner.isAlive();
    }

    @Override
    public boolean wasSummoned() {
        return true;
    }

    @Override
    public EntityType<?> entityType() {
        return this.getType();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER, 0, this::genericController));
    }

    protected <S extends GeoAnimatable> PlayState genericController(AnimationState<S> data) {
        data.setAnimation(RawAnimation.begin().thenLoop("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public EffectCache getFXCache() {
        return this.effectCache;
    }
}
