package com.ombremoon.spellbound.common.world.entity.spell;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.world.entity.VFXEntity;
import com.ombremoon.spellbound.common.world.entity.VFXSpellEntity;
import com.ombremoon.spellbound.common.world.spell.deception.ShadowVeilSpell;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

public class ShadowVeil extends VFXSpellEntity<ShadowVeilSpell> {
    public static final ResourceLocation VFX = CommonClass.customLocation("shadow_veil");
    private static final List<SoundEvent> MOB_SOUNDS = List.of(
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.SKELETON_AMBIENT,
            SoundEvents.ZOMBIE_AMBIENT,
            SoundEvents.SPIDER_AMBIENT
    );
    private final Int2IntArrayMap veilAttendees = new Int2IntArrayMap();
    private final IntOpenHashSet fearedEntities = new IntOpenHashSet();
    private final IntOpenHashSet projectilesInVeil = new IntOpenHashSet();
    private EntityDimensions dimensions;

    public ShadowVeil(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            AABB veilBox = this.getBoundingBox();
            if (this.spell != null) {
                SpellContext context = this.spell.getContext();
                LivingEntity caster = context.getCaster();
                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, veilBox);
                for (LivingEntity living : list) {
                    int id = living.getId();
                    if (this.veilAttendees.containsKey(id)) {
                        if (context.hasSkill(SBSkills.SAPPING_FEAR) && !this.fearedEntities.contains(id) && living.tickCount >= this.veilAttendees.get(id) + this.spell.getDuration() / 2) {
                            var handler = context.getSpellHandler();
                            handler.applyFearEffect(living, 40);
                            this.fearedEntities.add(id);
                        }
                    } else {
                        this.veilAttendees.put(living.getId(), living.tickCount);
                        this.spell.addVeilEffects(context, living, this);
                    }
                }

                if (context.hasSkill(SBSkills.WEIGHTED_VEIL)) {
                    List<Projectile> projectiles = this.level().getEntitiesOfClass(Projectile.class, veilBox, projectile -> {
                        Entity source = projectile.getOwner();
                        return source != null && !SpellUtil.IS_ALLIED.test(caster, source);
                    });
                    for (Projectile projectile : projectiles) {
                        int projectileId = projectile.getId();
                        if (!this.projectilesInVeil.contains(projectileId)) {
                            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(0.25F));
                            this.projectilesInVeil.add(projectileId);
                        }
                    }
                }

                Set<Integer> toRemove = new HashSet<>();
                for (int i : this.veilAttendees.keySet()) {
                    Entity entity = this.level().getEntity(i);
                    if (entity == null || !entity.isAlive() || !veilBox.intersects(entity.getBoundingBox())) {
                        toRemove.add(i);
                        if (entity instanceof LivingEntity living) {
                            this.spell.removeVeilEffects(context, living);
                        }
                    }

                    if (entity instanceof LivingEntity living && !toRemove.contains(i)) {
                        this.spell.tickVeilEffects(context, living, this);

                        if (context.hasSkill(SBSkills.DECEPTIVE_ECHOES)) {
                            this.playRandomMobSound(living);
                        }

                        if (context.hasSkill(SBSkills.SHADOW_DOMAIN) && entity instanceof LivingEntity && SpellUtil.CAN_ATTACK_ENTITY.test(caster, living)) {
                            knockbackEntityIntoVeil(living, veilBox);
                        }
                    }
                }

                toRemove.forEach(this.veilAttendees::remove);
            }
        }
    }

    private void knockbackEntityIntoVeil(LivingEntity entity, AABB veilBox) {
        AABB entityBox = entity.getBoundingBox();
        double edgeThreshold = 0.5;

        double distToMinX = entityBox.minX - veilBox.minX;
        double distToMaxX = veilBox.maxX - entityBox.maxX;
        double distToMinZ = entityBox.minZ - veilBox.minZ;
        double distToMaxZ = veilBox.maxZ - entityBox.maxZ;

        boolean nearEdge = (distToMinX >= -edgeThreshold && distToMinX <= edgeThreshold) ||
                           (distToMaxX >= -edgeThreshold && distToMaxX <= edgeThreshold) ||
                           (distToMinZ >= -edgeThreshold && distToMinZ <= edgeThreshold) ||
                           (distToMaxZ >= -edgeThreshold && distToMaxZ <= edgeThreshold);

        if (!nearEdge) return;
        
        Vec3 entityPos = entity.position();
        Vec3 veilCenter = veilBox.getCenter();
        
        double distX = entityPos.x - veilCenter.x;
        double distZ = entityPos.z - veilCenter.z;
        double distance = Math.sqrt(distX * distX + distZ * distZ);
        
        if (distance > 0.1) {
            double knockbackStrength = 0.5;
            double knockbackX = (-distX / distance) * knockbackStrength;
            double knockbackZ = (-distZ / distance) * knockbackStrength;
            
            entity.push(knockbackX, 0, knockbackZ);
            entity.hurtMarked = true;
        }
    }

    private void playRandomMobSound(LivingEntity target) {
        if (!(target instanceof Player player))
            return;

        player.playNotifySound(MOB_SOUNDS.get(this.random.nextInt(MOB_SOUNDS.size())), SoundSource.HOSTILE, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        if (this.dimensions != null) return this.dimensions.makeBoundingBox(this.position());

        Entity owner = this.getSummoner();
        if (!(owner instanceof LivingEntity caster))
            return super.makeBoundingBox();

        SkillHolder skillHolder = SpellUtil.getSkills(caster);
        if (skillHolder.hasSkill(SBSkills.EXPANDING_SHADOWS)) {
            if (dimensions == null) {
                this.dimensions = EntityDimensions.scalable(10f, 4f);
            }
            return this.dimensions.makeBoundingBox(this.position());
        }

        return super.makeBoundingBox();
    }

    @Override
    protected EffectBuilder<?> getEffect() {
        return EffectBuilder.Entity.of(VFX, this.getId(), EntityEffectExecutor.AutoRotate.NONE)
                .setOffset(0, -1.5, 0);
    }

    @Override
    protected ResourceLocation getEffectLocation() {
        return VFX;
    }

    public Set<Integer> getVeilAttendees() {
        return this.veilAttendees.keySet();
    }
}
