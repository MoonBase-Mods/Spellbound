package com.ombremoon.spellbound.common.world.spell.transfiguration;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.client.gui.SkillTooltipProvider;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.DataComponentStorage;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import com.ombremoon.spellbound.common.world.effect.SBEffectInstance;
import com.ombremoon.spellbound.common.world.sound.SpellboundSounds;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DolphinsFinSpell extends AnimatedSpell implements RadialSpell {
    private static final ResourceLocation DOLPHINS_FIN_SWIM = CommonClass.customLocation("dolphins_fin_swim");
    private static final ResourceLocation DOLPHINS_FIN_MOVEMENT = CommonClass.customLocation("dolphins_fin_movement");
    private static final ResourceLocation SLIPSTREAM_SWIM = CommonClass.customLocation("slipstream_swim");
    private static final ResourceLocation SLIPSTREAM_MINING = CommonClass.customLocation("slipstream_mining");
    private static final ResourceLocation HAMMERHEAD = CommonClass.customLocation("hammerhead");
    private static final ResourceLocation ECHOLOCATION = CommonClass.customLocation("echo_location");
    private static final ResourceLocation OCEAN_DWELLER = CommonClass.customLocation("ocean_dwell");
    private static final ResourceLocation KELP_VEIL = CommonClass.customLocation("kelp_veil");
    private static final ResourceLocation KELP_VEIL_ATTACK = CommonClass.customLocation("kelp_veil_attack");
    private static final ResourceLocation KELP_VEIL_SPELL_CAST = CommonClass.customLocation("kelp_veil_spell_cast");
    private static final ResourceLocation POD_LEADER = CommonClass.customLocation("pod_leader");
    private float modifierAmount;
    private boolean usedDash = false;
    private int prevDuration;
    private int dashTicks = 0;
    private int slipStreamTicks = 0;
    private int kelpVeilTicks = 0;

    public static Builder<DolphinsFinSpell> createDolphinsFinBuilder() {
        return createSimpleSpellBuilder(DolphinsFinSpell.class)
                .duration(600)
                .manaCost(20)
                .castCondition((context, dolphinsFinSpell) -> {
                    if (dolphinsFinSpell.isChoice(SBSkills.AQUATIC_DASH) || dolphinsFinSpell.isChoice(SBSkills.ECHOLOCATION) || dolphinsFinSpell.isChoice(SBSkills.SONAR_BLAST)) {
                        if (dolphinsFinSpell.isChoice(SBSkills.SONAR_BLAST) && !(context.getTarget() instanceof LivingEntity))
                            return false;

                        return context.getCaster().isInWater();
                    }
                    return true;
                })
                .fullRecast(true);
    }

    public DolphinsFinSpell() {
        super(SBSpells.DOLPHINS_FIN.get(), createDolphinsFinBuilder());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.DOLPHINS_FIN,
                SkillTooltip.ATTRIBUTE.tooltip(
                        new ModifierData(NeoForgeMod.SWIM_SPEED, new AttributeModifier(DOLPHINS_FIN_SWIM, potency(0.5F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ),
                SkillTooltip.ATTRIBUTE.tooltip(
                        new ModifierData(Attributes.WATER_MOVEMENT_EFFICIENCY, new AttributeModifier(DOLPHINS_FIN_MOVEMENT, 0.5F, AttributeModifier.Operation.ADD_VALUE))
                ),
                WATER_SLOWDOWN.tooltip(-90F),
                SkillTooltip.POTENCY_SCALING.tooltip(),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.MERMAIDS_TAIL,
                SkillTooltip.ATTRIBUTE.tooltip(
                        new ModifierData(NeoForgeMod.SWIM_SPEED, new AttributeModifier(DOLPHINS_FIN_SWIM, potency(0.75F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ),
                WATER_SLOWDOWN.tooltip(-96F),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.SHARK_ATTACK,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.MAGIC, this.getModifiedDamage(2.0F))),
                SkillTooltip.KNOCKBACK.tooltip(1F)
        );
        this.addSkillDetails(SBSkills.ECHOLOCATION,
                SkillTooltip.RADIUS.tooltip(potency(25F)),
                SkillTooltip.POTENCY_SCALING.tooltip(),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.POD_LEADER, SkillTooltip.ALLY_RANGE.tooltip(5.0F));
        this.addSkillDetails(SBSkills.SLIPSTREAM,
                SkillTooltip.ATTRIBUTE.tooltip(
                        new ModifierData(NeoForgeMod.SWIM_SPEED, new AttributeModifier(POD_LEADER, potency(0.25F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                ),
                SkillTooltip.PROC_DURATION.tooltip(60),
                SkillTooltip.EFFECT_DURATION.tooltip(200),
                SkillTooltip.POTENCY_SCALING.tooltip()
        );
        this.addSkillDetails(SBSkills.SONAR_BLAST,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.MAGIC, this.getModifiedDamage(2.0F))
                ),
                SkillTooltip.KNOCKBACK.tooltip(2.5F),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.KELP_VEIL,
                SkillTooltip.PROC_DURATION.tooltip(40),
                SkillTooltip.EFFECT_DURATION.tooltip(60),
                SkillTooltip.MOB_EFFECT.tooltip(SBEffects.MAGI_INVISIBILITY),
                SkillTooltip.INVISIBILITY_NO_STACK.tooltip()
        );
        this.addSkillDetails(SBSkills.HAMMERHEAD,
                SkillTooltip.ATTRIBUTE.tooltip(
                        new ModifierData(Attributes.SUBMERGED_MINING_SPEED, new AttributeModifier(HAMMERHEAD, 0.8F, AttributeModifier.Operation.ADD_VALUE))
                )
        );
        this.addSkillDetails(SBSkills.OCEAN_DWELLER,
                SkillTooltip.MOB_EFFECT.tooltip(MobEffects.WATER_BREATHING)
        );
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            this.modifierAmount = context.hasSkill(SBSkills.MERMAIDS_TAIL) ? 0.75F : 0.5F;
            this.addSkillBuff(
                    caster,
                    SBSkills.DOLPHINS_FIN,
                    DOLPHINS_FIN_SWIM,
                    BuffCategory.BENEFICIAL,
                    SkillBuff.ATTRIBUTE_MODIFIER,
                    new ModifierData(NeoForgeMod.SWIM_SPEED, new AttributeModifier(DOLPHINS_FIN_SWIM, potency(this.modifierAmount), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
            );
            this.addSkillBuff(
                    caster,
                    SBSkills.DOLPHINS_FIN,
                    DOLPHINS_FIN_MOVEMENT,
                    BuffCategory.BENEFICIAL,
                    SkillBuff.ATTRIBUTE_MODIFIER,
                    new ModifierData(Attributes.WATER_MOVEMENT_EFFICIENCY, new AttributeModifier(DOLPHINS_FIN_MOVEMENT, 0.5F, AttributeModifier.Operation.ADD_VALUE))
            );
            //VFX
            playCastSound(level, context);
            this.triggerSpellFX(EffectData.Entity.of(CommonClass.customLocation("dolphin_fin_trail"), caster.getId(),
                    EntityEffectExecutor.AutoRotate.NONE).setOffset(0, -0.5, 0));

        }

        if (context.hasSkill(SBSkills.HAMMERHEAD)) {
            this.addSkillBuff(
                    caster,
                    SBSkills.HAMMERHEAD,
                    HAMMERHEAD,
                    BuffCategory.BENEFICIAL,
                    SkillBuff.ATTRIBUTE_MODIFIER,
                    new ModifierData(Attributes.SUBMERGED_MINING_SPEED, new AttributeModifier(HAMMERHEAD, 0.8F, AttributeModifier.Operation.ADD_VALUE))
            );
        }

        if (context.hasSkill(SBSkills.OCEAN_DWELLER)) {
            this.addSkillBuff(
                    caster,
                    SBSkills.OCEAN_DWELLER,
                    OCEAN_DWELLER,
                    BuffCategory.BENEFICIAL,
                    SkillBuff.MOB_EFFECT,
                    new MobEffectInstance(MobEffects.WATER_BREATHING, this.getDuration(), 0, false, false)
            );
        }
    }


    public void playCastSound(Level level, SpellContext context) {
        float volume = 0.3F + level.random.nextFloat() * 0.2F;
        float pitch = 0.8F + level.random.nextFloat() * 0.2F;
        level.playSound(null, context.getCaster().blockPosition(), SoundEvents.DOLPHIN_JUMP,
                SoundSource.PLAYERS, volume, pitch);
        level.playSound(null, context.getCaster().blockPosition(), SoundEvents.DOLPHIN_PLAY,
                SoundSource.PLAYERS, volume, pitch);

    }

    //Coalesce mob effect duration

    @Override
    protected void onSpellRecast(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide && caster.isInWater()) {
            if (this.isChoice(SBSkills.AQUATIC_DASH)) {
                Vec3 lookVec = caster.getLookAngle();
                double strength = 3.0F;
                Vec3 motion = new Vec3(lookVec.x * strength, lookVec.y * strength, lookVec.z * strength);
                caster.setDeltaMovement(caster.getDeltaMovement().add(motion));
                caster.hurtMarked = true;
                this.usedDash = true;
                this.setRemainingTicks(this.getDuration() - this.prevDuration);
            }

            if (this.isChoice(SBSkills.ECHOLOCATION)) {
                var list = level.getEntitiesOfClass(LivingEntity.class, this.getInflatedBB(caster, potency(25)));
                for (LivingEntity entity : list) {
                    if (!isCaster(entity)) {
                        this.addSkillBuff(
                                entity,
                                SBSkills.ECHOLOCATION,
                                ECHOLOCATION,
                                BuffCategory.BENEFICIAL,
                                SkillBuff.MOB_EFFECT,
                                new SBEffectInstance(caster, SBEffects.TARGET_AURA, 60, true,  0, false, false),
                                60
                        );
                    }
                }
                this.setRemainingTicks(this.getDuration() - this.prevDuration);
                this.addCooldown(SBSkills.ECHOLOCATION, 100);
            }

            if (this.isChoice(SBSkills.SONAR_BLAST)) {
                Entity entity = context.getTarget();
                if (entity instanceof LivingEntity target && target.isAlive() && target.isInWater()) {
                    this.performSonarBlast((ServerLevel) level, caster, target);
                }
            }
        }
    }

    private void performSonarBlast(ServerLevel level, LivingEntity caster, LivingEntity target) {
        Vec3 vec3 = caster.position().add(0, caster.getEyeHeight(), 0);
        Vec3 vec31 = target.getEyePosition().subtract(vec3);
        Vec3 vec32 = vec31.normalize();
        int i = Mth.floor(vec31.length()) + 7;

        for (int j = 1; j < i; j++) {
            Vec3 vec33 = vec3.add(vec32.scale(j));
            level.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        caster.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
        if (this.hurt(target, 2.0F)) {
            double d1 = 0.5 * (1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double d0 = 2.5 * (1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            target.push(vec32.x() * d0, vec32.y() * d1, vec32.z() * d0);
        }
    }

    //TEST NEW KELP VEIL

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        var handler = context.getSpellHandler();
        if (!level.isClientSide) {
            if (caster.isInWater()) {
                if (this.usedDash) {
                    this.dashTicks++;
                    if (this.isChoice(SBSkills.AQUATIC_DASH) && context.hasSkill(SBSkills.SHARK_ATTACK) && this.dashTicks < 20) {
                        var entities = level.getEntitiesOfClass(LivingEntity.class, this.getInflatedBB(caster, 0.5F));
                        for (LivingEntity entity : entities) {
                            if (!this.isCaster(entity) && this.hurt(entity, 2.0F)) {
                                entity.knockback(1F, entity.getX() - caster.getX(), entity.getZ() - caster.getZ());
                            }
                        }
                    } else {
                        this.usedDash = false;
                        this.dashTicks = 0;
                    }
                }

                if (context.hasSkill(SBSkills.SLIPSTREAM)) {
                    if (caster.isSwimming()) {
                        this.slipStreamTicks++;

                        if (this.slipStreamTicks >= 60 && !context.hasSkillBuff(SBSkills.SLIPSTREAM)) {
                            this.addSkillBuff(
                                    caster,
                                    SBSkills.SLIPSTREAM,
                                    SLIPSTREAM_SWIM,
                                    BuffCategory.BENEFICIAL,
                                    SkillBuff.ATTRIBUTE_MODIFIER,
                                    new ModifierData(NeoForgeMod.SWIM_SPEED, new AttributeModifier(SLIPSTREAM_SWIM, potency(0.25F), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                                    60
                            );
                            this.addEventBuff(
                                    caster,
                                    SBSkills.SLIPSTREAM,
                                    BuffCategory.BENEFICIAL,
                                    SpellEventListener.Events.EFFECT_APPLICABLE,
                                    SLIPSTREAM_MINING,
                                    event -> {
                                        if (event.getEffectInstance().getEffect() == MobEffects.DIG_SLOWDOWN)
                                            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                                    },
                                    60
                            );
                        }
                    } else {
                        this.slipStreamTicks = 0;
                    }
                }

                if (context.hasSkill(SBSkills.POD_LEADER)) {
                    var list = this.getAlliedEntities(caster, 5);
                    for (LivingEntity entity : list) {
                        if (!SpellUtil.hasSkillBuff(entity, SBSkills.POD_LEADER)) {
                            this.addSkillBuff(
                                    entity,
                                    SBSkills.POD_LEADER,
                                    POD_LEADER,
                                    BuffCategory.BENEFICIAL,
                                    SkillBuff.ATTRIBUTE_MODIFIER,
                                    new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(POD_LEADER, potency(this.modifierAmount), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                                    this.getRemainingTime()
                            );
                        }
                    }
                }

                if (context.hasSkill(SBSkills.KELP_VEIL)) {
                    if (!handler.isMoving()) {
                        this.kelpVeilTicks++;
                        if (this.kelpVeilTicks >= 40 && !context.hasSkillBuff(SBSkills.KELP_VEIL)) {
                            int duration = this.getRemainingTime();
                            if (!caster.hasEffect(SBEffects.MAGI_INVISIBILITY)) {
                                this.addSkillBuff(
                                        caster,
                                        SBSkills.KELP_VEIL,
                                        KELP_VEIL,
                                        BuffCategory.BENEFICIAL,
                                        SkillBuff.MOB_EFFECT,
                                        new MobEffectInstance(SBEffects.MAGI_INVISIBILITY, duration, 0, false, false),
                                        duration
                                );
                                this.addEventBuff(
                                        caster,
                                        SBSkills.KELP_VEIL,
                                        BuffCategory.BENEFICIAL,
                                        SpellEventListener.Events.ATTACK,
                                        KELP_VEIL_ATTACK,
                                        playerAttackEvent -> {
                                            this.removeSkillBuff(caster, SBSkills.KELP_VEIL);
                                            this.kelpVeilTicks = 0;
                                        },
                                        duration
                                );
                                this.addEventBuff(
                                        caster,
                                        SBSkills.KELP_VEIL,
                                        BuffCategory.BENEFICIAL,
                                        SpellEventListener.Events.CAST_SPELL,
                                        KELP_VEIL_SPELL_CAST,
                                        castSpellEvent -> {
                                            this.removeSkillBuff(caster, SBSkills.KELP_VEIL);
                                            this.kelpVeilTicks = 0;
                                        },
                                        duration
                                );
                            }
                        }
                    } else {
                        this.kelpVeilTicks = 0;
                        this.removeSkillBuff(caster, SBSkills.KELP_VEIL);
                    }
                }
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        LivingEntity caster = context.getCaster();
        this.removeSkillBuff(caster, SBSkills.DOLPHINS_FIN);
        this.removeSkillBuff(caster, SBSkills.HAMMERHEAD);
        this.removeSkillBuff(caster, SBSkills.OCEAN_DWELLER);
    }

    @Override
    public @UnknownNullability CompoundTag saveData(CompoundTag compoundTag) {
        CompoundTag nbt = super.saveData(compoundTag);
        nbt.putInt("previous_duration", this.tickCount);
        return nbt;
    }

    @Override
    public void loadData(CompoundTag nbt) {
        super.loadData(nbt);
        this.prevDuration = nbt.getInt("previous_duration");
    }

    private static SkillTooltip<Float> WATER_SLOWDOWN = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.dolphins_fin", sign(arg)).withStyle(invertedColor(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };
}
