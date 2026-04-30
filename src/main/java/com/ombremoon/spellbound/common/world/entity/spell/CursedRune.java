package com.ombremoon.spellbound.common.world.entity.spell;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.magic.EffectManager;
import com.ombremoon.spellbound.common.magic.effects.EffectHolder;
import com.ombremoon.spellbound.common.world.entity.VFXSpellEntity;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.common.world.spell.deception.CursedRuneSpell;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.RenderUtil;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CursedRune extends VFXSpellEntity<CursedRuneSpell> {
    private static final Logger LOGGER = Constants.LOG;
    private static final EntityDataAccessor<Boolean> HIDDEN = SynchedEntityData.defineId(CursedRune.class, EntityDataSerializers.BOOLEAN);
    private final List<EffectHolder> effects = new ArrayList<>();

    public CursedRune(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected EffectBuilder<?> getEffect() {
        return EffectBuilder.StaticEntity.of(CommonClass.customLocation("cursed_rune_place"), this.getId(), EntityEffectExecutor.AutoRotate.NONE)
                .setOffset(0, 0.1, 0);
    }

    @Override
    protected ResourceLocation getEffectLocation() {
        return CommonClass.customLocation("cursed_rune");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HIDDEN, false);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Hidden", this.isHidden());
        ListTag listTag = new ListTag();
        for (var effect : this.effects) {
            CompoundTag tag = new CompoundTag();
            EffectHolder.CODEC
                    .encodeStart(NbtOps.INSTANCE, effect)
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(nbt -> tag.put("Effect", nbt));
            listTag.add(tag);
        }
        compound.put("MagicEffects", listTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHidden(compound.getBoolean("Hidden"));
        if (compound.contains("MagicEffects")) {
            ListTag listTag = compound.getList("MagicEffects", 10);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag tag = listTag.getCompound(i);
                if (tag.contains("Effect")) {
                    EffectHolder.CODEC
                            .parse(NbtOps.INSTANCE, tag.getCompound("Effect"))
                            .resultOrPartial(LOGGER::error)
                            .ifPresent(this.effects::add);
                }
            }
        }
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return super.shouldRender(x, y, z) && !this.isHidden();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && !this.isStarting() && !this.isEnding()) {
            var list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox(), this.canActivateRune());
            if (!list.isEmpty()) {
                for (LivingEntity entity : list) {
                    if (!this.isOwner(entity)) {
                        EffectManager effectManager = SpellUtil.getSpellEffects(entity);
                        this.effects.forEach(effect -> effectManager.addMagicEffect(effect, this.getSummoner()));
                    }
                }

                this.triggerFX(
                        EffectData.StaticEntity.of(CommonClass.customLocation("cursed_rune_discharge"), this.getId(), EntityEffectExecutor.AutoRotate.NONE)
                                .setOffset(0, 0.1, 0)
                );

                this.setEndTick(10);
            }

            var list1 = this.level().getEntitiesOfClass(Projectile.class, this.getBoundingBox().inflate(0.25, 0.5, 0.25));
            if (!list1.isEmpty()) {
                this.removeEntityFX(this.getEffectLocation());
                this.triggerFX(
                        EffectData.StaticEntity.of(CommonClass.customLocation("cursed_rune_discharge"), this.getId(), EntityEffectExecutor.AutoRotate.NONE)
                                .setOffset(0, 0.1, 0)
                );
            }
        }

        if (this.level().isClientSide) {
            if (this.tickCount == 20) {
                var builder = EffectBuilder.StaticEntity.of(CommonClass.customLocation("cursed_rune"), this.getId(), EntityEffectExecutor.AutoRotate.NONE)
                        .setOffset(0, 0.1, 0);
                if (this.isHidden()) {
                    RenderUtil.displayClientEffect(this, this.getSummoner(), builder);
                } else {
                    this.addFX(builder);
                }
            }
        }
    }

    private Predicate<LivingEntity> canActivateRune() {
        return livingEntity -> {
            Entity summoner = this.getSummoner();
            return !(summoner instanceof LivingEntity living) || SpellUtil.CAN_ATTACK_ENTITY.test(living, livingEntity);
        };
    }

    public void setRuneEffects(List<EffectHolder> effects) {
        this.effects.addAll(effects);
    }

    public boolean isHidden() {
        return this.entityData.get(HIDDEN);
    }

    public void setHidden(boolean hidden) {
        this.entityData.set(HIDDEN, hidden);
    }

    @Override
    public boolean requiresSpellToPersist() {
        return false;
    }
}
