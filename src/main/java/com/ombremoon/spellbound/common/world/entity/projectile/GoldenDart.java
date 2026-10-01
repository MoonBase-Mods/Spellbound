package com.ombremoon.spellbound.common.world.entity.projectile;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.world.entity.VFXSpellProjectile;
import com.ombremoon.spellbound.common.world.spell.divine.SmiteSpell;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import com.ombremoon.spellbound.util.math.SplineController;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.RandomUtil;
import org.jetbrains.annotations.Nullable;

public class GoldenDart extends VFXSpellProjectile<SmiteSpell> {
    private static final EntityDataAccessor<Integer> ANIMATION_TICKS = SynchedEntityData.defineId(GoldenDart.class, EntityDataSerializers.INT);
    private static final ResourceLocation SHARDS_OF_PURITY = CommonClass.customLocation("shards_of_purity");
    private static final int ANIMATION_DURATION = 20;
    @Nullable
    private Vec3 startPos;
    @Nullable
    private Vec3 targetPos;

    public GoldenDart(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected EffectBuilder<?> getEffect() {
        return EffectBuilder.Entity.of(this.getEffectLocation(), this.getId(), EntityEffectExecutor.AutoRotate.LOOK)
                .setRotation(180, 180, 0)
                .setScale(0.5, 0.5, 0.5);
    }

    @Override
    protected ResourceLocation getEffectLocation() {
        return CommonClass.customLocation("smite_golden_dart");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIMATION_TICKS, 0);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("animationTicks", this.getAnimationTicks());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setAnimationTicks(compound.getInt("animationTicks"));
    }

    @Override
    public void tick() {
        if (this.startPos == null) {
            this.initializeAnimation();
        }

        SmiteSpell smiteSpell = this.getOrCreateSpell();
        if (this.targetPos != null && this.getAnimationTicks() <= 15) {
            this.updateEasedPosition();
            this.incrementAnimationTicks();
        } else if (smiteSpell != null && this.getAnimationTicks() >= 15 && this.getHomingTarget() == null) {
            this.findTarget(smiteSpell);
        }


        super.tick();
    }

    private void findTarget(SmiteSpell spell) {
        var list = spell.getAttackableEntities(this.getSummoner(), 5, living -> spell.isChoice(SBSkills.BLACK_BLADE) || living.getType().is(EntityTypeTags.UNDEAD));
        if (!list.isEmpty()) {
            LivingEntity target = list.get(this.random.nextInt(list.size()));
            this.setHomingTarget(target);
            this.setSplineController(SplineController.createSpline(this.getSummoner().position(), target.position())
                    .addControlPoint(0.005, new Vec3(RandomUtil.randomValueBetween(-0.02, 0.02), RandomUtil.randomValueBetween(0, 0.02), RandomUtil.randomValueBetween(0.02, 0.05))));
        } else if (this.getSummoner() instanceof LivingEntity living) {
            this.setHomingTarget(living);
        }
    }

    private void initializeAnimation() {
        Entity owner = this.getSummoner();
        if (owner != null) {
            this.startPos = this.position();
            this.targetPos = this.position().add(0, owner.getEyeHeight() + 0.25, 0);
        }
    }

    private void updateEasedPosition() {
        if (this.startPos == null || this.targetPos == null) {
            return;
        }

        float progress = Math.min((float) this.getAnimationTicks() / 15, 1.0F);
        float easedProgress = easeOutQuad(progress);

        Vec3 easedPos = this.startPos.lerp(this.targetPos, easedProgress);
        this.setPos(easedPos);
    }

    private float easeOutQuad(float t) {
        return 1.0F - (1.0F - t) * (1.0F - t);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        if (!this.level().isClientSide && target instanceof LivingEntity livingentity) {
            SmiteSpell smiteSpell = this.getOrCreateSpell();
            if (smiteSpell != null) {
                SpellContext context = smiteSpell.getContext();
                if (livingentity.is(context.getCaster())) {
                    smiteSpell.heal(livingentity, 0.2F);
                } else {
                    if (smiteSpell.isChoice(SBSkills.BLACK_BLADE) && this.hurtTarget(livingentity, smiteSpell, context)) {
                        smiteSpell.addBlackBladeDebuff(livingentity);
                    } else if (livingentity.getType().is(EntityTypeTags.UNDEAD)) {
                        this.hurtTarget(livingentity, smiteSpell, context);
                    }
                }
            }

            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    private boolean hurtTarget(LivingEntity living, SmiteSpell spell, SpellContext context) {
        if (spell.hurt(living, 3F)) {
            if (context.hasSkill(SBSkills.SHARDS_OF_PURITY)) {
                this.addDartDebuff(living);
            }

            return true;
        }

        return false;
    }

    private void addDartDebuff(LivingEntity target) {
        SmiteSpell smiteSpell = this.getOrCreateSpell();
        if (smiteSpell != null) {
            var handler = SpellUtil.getSpellHandler(target);
            var optional = handler.getSkillBuff(SBSkills.SHARDS_OF_PURITY.value());
            double debuff = -0.5F;
            if (optional.isPresent() && optional.get().object() instanceof ModifierData modifierData) {
                debuff = Math.max(debuff, modifierData.attributeModifier().amount() + debuff);
            }

            smiteSpell.addSkillBuff(
                    target,
                    SBSkills.SHARDS_OF_PURITY,
                    SHARDS_OF_PURITY,
                    BuffCategory.HARMFUL,
                    SkillBuff.ATTRIBUTE_MODIFIER,
                    new ModifierData(SBAttributes.ATTACK_POWER, new AttributeModifier(SHARDS_OF_PURITY, debuff, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                    200
            );
        }
    }

    private int getAnimationTicks() {
        return this.entityData.get(ANIMATION_TICKS);
    }

    private void setAnimationTicks(int ticks) {
        this.entityData.set(ANIMATION_TICKS, ticks);
    }

    private void incrementAnimationTicks() {
        this.setAnimationTicks(this.getAnimationTicks() + 1);
    }
}
