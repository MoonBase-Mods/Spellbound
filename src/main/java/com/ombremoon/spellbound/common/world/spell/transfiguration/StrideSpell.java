package com.ombremoon.spellbound.common.world.spell.transfiguration;

import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class StrideSpell extends AnimatedSpell {
    protected static final ResourceLocation STRIDE = CommonClass.customLocation("thunderous_hooves");
    protected static final ResourceLocation QUICK_SPRINT = CommonClass.customLocation("quick_sprint");
    protected static final ResourceLocation SUREFOOTED = CommonClass.customLocation("surefooted");
    protected static final ResourceLocation FLEETFOOTED = CommonClass.customLocation("fleetfooted");
    protected static final ResourceLocation MOMENTUM = CommonClass.customLocation("momentum");
    private int initialFoodLevel;
    private Vec3 currentPos;
    private int movementTicks;
    private Entity mount;
    private float modifierAmount;

    public static Builder<StrideSpell> createStrideBuilder() {
        return createSimpleSpellBuilder(StrideSpell.class)
                .duration(600)
                .manaCost(12)
                .selfBuffCast()
                .hasLayer()
                .fullRecast(true);
    }

    public StrideSpell() {
        super(SBSpells.STRIDE.get(), createStrideBuilder());
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            this.modifierAmount = potency(context.hasSkill(SBSkills.GALLOPING_STRIDE) ? 1.5F : 1.25F);
            applyMovementBenefits(caster, context);

            if (caster instanceof Player player) {
                if (context.hasSkill(SBSkills.MARATHON))
                    this.initialFoodLevel = player.getFoodData().getFoodLevel();

                if (context.hasSkill(SBSkills.MOMENTUM))
                    this.currentPos = caster.position();
            }
        }
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.STRIDE,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(STRIDE, potency(1.25F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))),
                SkillTooltip.DURATION.tooltip(600),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.QUICK_SPRINT,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(STRIDE, potency(0.15F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))),
                SkillTooltip.EFFECT_DURATION.tooltip(200),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.GALLOPING_STRIDE,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(STRIDE, potency(1.5F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.FLEETFOOTED,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(STRIDE, potency(1.25F * 0.5F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))),
                SkillTooltip.RANGE.tooltip(5F),
                SkillTooltip.CONDITION.tooltip(
                        new SkillTooltip.UnlockedTooltip(SBSkills.GALLOPING_STRIDE, SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(STRIDE, potency(1.5F * 0.5F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))))
                ),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.SUREFOOTED,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.STEP_HEIGHT, new AttributeModifier(SUREFOOTED, 0.4, AttributeModifier.Operation.ADD_VALUE)))
        );
        this.addSkillDetails(SBSkills.ENDURANCE,
                SkillTooltip.MODIFY_DURATION.tooltip(100F)
        );
        this.addSkillDetails(SBSkills.MOMENTUM,
                SkillTooltip.ATTRIBUTE_PER_CHARGE.tooltip(new ModifierData(Attributes.ATTACK_SPEED, new AttributeModifier(MOMENTUM, potency(0.04F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))),
                SkillTooltip.MAX_CHARGES.tooltip(5),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.STAMPEDE,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.MAGIC, this.getModifiedDamage(3F))),
                SkillTooltip.KNOCKBACK.tooltip(0.4F)
        );
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();

        if (context.hasSkill(SBSkills.AQUA_TREAD)) {
            boolean flag = caster.getVehicle() != null && context.hasSkill(SBSkills.RIDERS_RESILIENCE);
            Entity entity = flag ? caster.getVehicle() : caster;
            entity.wasTouchingWater = false;
            Vec3 vec3 = entity.getDeltaMovement().scale(1.15F);
            if (entity.getBlockStateOn().is(Blocks.WATER)) {
                entity.setDeltaMovement(vec3.x, flag ? 0.035F : 0.025F, vec3.z);
                entity.setSwimming(false);
                entity.setOnGround(true);
                float f1 = Math.min(1.0F, (float)Math.sqrt(vec3.x * vec3.x * 0.2F + vec3.y * vec3.y + vec3.z * vec3.z * 0.2F) * 0.35F);
                caster.playSound(SoundEvents.PLAYER_SWIM, f1, 1.0F + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.4F);
            }
        }

        if (!level.isClientSide) {
            if (context.hasSkill(SBSkills.QUICK_SPRINT) && this.tickCount >= 200) {
                if (hasAttributeModifier(caster, Attributes.MOVEMENT_SPEED, QUICK_SPRINT)) {
                    removeSkillBuff(caster, SBSkills.QUICK_SPRINT);
                } else if (caster.getVehicle() instanceof LivingEntity vehicle && hasAttributeModifier(vehicle, Attributes.MOVEMENT_SPEED, QUICK_SPRINT)) {
                    removeSkillBuff(vehicle, SBSkills.QUICK_SPRINT);
                }
            }

            if (context.hasSkill(SBSkills.FLEETFOOTED)) {
                var allies = this.getAlliedEntities(5);
                for (LivingEntity ally : allies) {
                    addSkillBuff(
                            ally,
                            SBSkills.FLEETFOOTED,
                            FLEETFOOTED,
                            BuffCategory.BENEFICIAL,
                            SkillBuff.ATTRIBUTE_MODIFIER,
                            new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(FLEETFOOTED, this.modifierAmount * potency(0.5F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                            20);
                }
            }

            if (context.hasSkill(SBSkills.RIDERS_RESILIENCE)) {
                Entity entity = caster.getVehicle();
                if (entity != null) {
                    this.mount = entity;
                    applyMovementBenefits(entity, context);
                } else if (this.mount != null) {
                    removeMovementBenefits(this.mount);
                    this.mount = null;
                }
            }

            if (context.hasSkill(SBSkills.STAMPEDE)) {
                var entities = level.getEntitiesOfClass(LivingEntity.class, caster.getBoundingBox().inflate(1.5), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
                boolean flag = caster.getVehicle() != null && context.hasSkill(SBSkills.RIDERS_RESILIENCE);
                for (LivingEntity living : entities) {
                    if (!isCaster(living) && !living.isAlliedTo(caster) && (caster.isSprinting() || flag && !living.is(caster.getVehicle()))) {
                        living.knockback(0.4, caster.getX() - living.getX(), caster.getZ() - living.getZ());
                        living.hurtMarked = true;
                        this.hurt(living, 1.5F);
                    }
                }
            }

            if (context.hasSkill(SBSkills.MOMENTUM)) {
                if (this.tickCount % 4 == 0) {
                    if (!this.currentPos.equals(caster.position())) {
                        this.movementTicks += 4;
                        this.currentPos = caster.position();
                        if (this.movementTicks % 20 == 0)
                            addSkillBuff(
                                    caster,
                                    SBSkills.MOMENTUM,
                                    MOMENTUM,
                                    BuffCategory.BENEFICIAL,
                                    SkillBuff.ATTRIBUTE_MODIFIER,
                                    new ModifierData(Attributes.ATTACK_SPEED, new AttributeModifier(MOMENTUM, potency((float) Math.min(0.04 * this.movementTicks / 20, 0.2)), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                                    100);
                    } else {
                        this.movementTicks = 0;
                    }
                }
            }

            if (context.hasSkill(SBSkills.MARATHON) && caster instanceof Player player && player.getFoodData().getFoodLevel() < this.initialFoodLevel)
                player.getFoodData().eat(this.initialFoodLevel - player.getFoodData().getFoodLevel(), 0);
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        LivingEntity caster = context.getCaster();
        removeMovementBenefits(caster);
        removeSkillBuff(caster, SBSkills.MOMENTUM);

        if (this.mount != null)
            removeMovementBenefits(this.mount);
    }

    private void applyMovementBenefits(Entity entity, SpellContext context) {
        if (entity instanceof LivingEntity livingEntity) {
            addSkillBuff(
                    livingEntity,
                    SBSkills.STRIDE,
                    STRIDE,
                    BuffCategory.BENEFICIAL,
                    SkillBuff.ATTRIBUTE_MODIFIER,
                    new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(STRIDE, this.modifierAmount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
            if (context.hasSkill(SBSkills.QUICK_SPRINT))
                addSkillBuff(
                        livingEntity,
                        SBSkills.QUICK_SPRINT,
                        QUICK_SPRINT,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(QUICK_SPRINT, potency(0.15F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));

            if (context.hasSkill(SBSkills.SUREFOOTED))
                addSkillBuff(
                        livingEntity,
                        SBSkills.SUREFOOTED,
                        SUREFOOTED,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.STEP_HEIGHT, new AttributeModifier(SUREFOOTED, 0.4, AttributeModifier.Operation.ADD_VALUE)));
        }
    }

    private void removeMovementBenefits(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            removeSkillBuff(livingEntity, SBSkills.STRIDE);
            removeSkillBuff(livingEntity, SBSkills.QUICK_SPRINT);
            removeSkillBuff(livingEntity, SBSkills.SUREFOOTED);
        }
    }
}
