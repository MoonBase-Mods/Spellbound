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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

public class ShadowVeil extends VFXSpellEntity<ShadowVeilSpell> {
    public static final ResourceLocation VFX = CommonClass.customLocation("shadow_veil");
    private final Int2IntArrayMap veilAttendees = new Int2IntArrayMap();
    private final IntOpenHashSet fearedEntities = new IntOpenHashSet();
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
                    }
                }

                toRemove.forEach(this.veilAttendees::remove);
            }
        }
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
