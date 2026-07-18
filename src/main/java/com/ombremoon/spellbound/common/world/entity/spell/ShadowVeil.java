package com.ombremoon.spellbound.common.world.entity.spell;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.world.entity.VFXEntity;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.OptionalInt;

public class ShadowVeil extends VFXEntity {
    private static final EntityDataAccessor<OptionalInt> OWNER_ID = SynchedEntityData.defineId(ShadowVeil.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);;
    public static final ResourceLocation VFX = CommonClass.customLocation("shadow_veil");
    private EntityDimensions dimensions;

    public ShadowVeil(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public ShadowVeil(Level level, LivingEntity caster) {
        this(SBEntities.SHADOW_VEIL.get(), level);
        setCaster(caster);
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        if (this.dimensions != null) return this.dimensions.makeBoundingBox(this.position());

        LivingEntity caster = getCaster();
        if (caster == null) return super.makeBoundingBox();

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

    @Nullable
    public LivingEntity getCaster() {
        if (this.entityData.get(OWNER_ID).isEmpty()) return null;
        return (LivingEntity) level().getEntity(this.entityData.get(OWNER_ID).getAsInt());
    }

    public void setCaster(LivingEntity caster) {
        this.entityData.set(OWNER_ID, OptionalInt.of(caster.getId()));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_ID, OptionalInt.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }
}
