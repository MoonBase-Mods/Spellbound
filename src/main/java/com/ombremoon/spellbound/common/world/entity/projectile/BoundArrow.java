package com.ombremoon.spellbound.common.world.entity.projectile;

import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.world.entity.SpellProjectile;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BoundArrow extends AbstractArrow {
    private static final EntityDataAccessor<ArrowVariant> ARROW_VARIANT = SynchedEntityData.defineId(BoundArrow.class, SBEntityDataSerializers.ARROW_VARIANT.get());
    private static final EntityDataAccessor<Boolean> IS_HOMING = SynchedEntityData.defineId(BoundArrow.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HOMING_TARGET_ID = SynchedEntityData.defineId(BoundArrow.class, EntityDataSerializers.INT);
    private final List<BlockPos> blocksHit = new ArrayList<>();

    public BoundArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public BoundArrow(Level level, double x, double y, double z, @Nullable ItemStack firedFromWeapon) {
        super(SBEntities.BOUND_ARROW.get(), x, y, z, level, ItemStack.EMPTY, firedFromWeapon);
    }

    public BoundArrow(Level level, LivingEntity owner, @Nullable ItemStack firedFromWeapon) {
        super(SBEntities.BOUND_ARROW.get(), owner, level, ItemStack.EMPTY, firedFromWeapon);
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
        super.tick();
        Vec3 vec3;
        if (this.isHoming()) {
            Entity entity = this.getHomingTarget();
            if (entity instanceof LivingEntity) {
                Vec3 vec31 = entity.position().add(0.0, entity.getBbHeight() / 2, 0.0).subtract(this.position());
                vec3 = vec31.normalize().scale(1.5F);
                this.setDeltaMovement(vec3);
            }
        }
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
            if (context.hasSkill(SBSkills.WRAITH_SHOT) && !this.blocksHit.contains(pos) && this.blocksHit.size() <= 3) {
                this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                this.blocksHit.add(pos);
                return;
            }
        }

        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.setCritArrow(false);
        this.setPierceLevel((byte)0);
        this.setSoundEvent(SoundEvents.ARROW_HIT);
        this.resetPiercedEntities();
//        this.discard();
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
