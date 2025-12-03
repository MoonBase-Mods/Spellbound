package com.ombremoon.spellbound.common.world.spell.ruin.fire;

import com.ombremoon.sentinellib.api.box.AABBSentinelBox;
import com.ombremoon.sentinellib.api.box.OBBSentinelBox;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.spellbound.common.world.entity.spell.SolarRay;
import com.ombremoon.spellbound.common.world.effect.SBEffectInstance;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.api.ChanneledSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.RandomUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

//TODO: ADD SOLAR BURST ANIMATIONS
//TODO: ADD BLUE TEXTURE FOR CONCENTRATED HEAT?

public class SolarRaySpell extends ChanneledSpell {
    protected static final SpellDataKey<Integer> SOLAR_RAY_ID = SyncedSpellData.registerDataKey(SolarRaySpell.class, SBDataTypes.INT.get());
    protected static final ResourceLocation OVERPOWER_WALK = CommonClass.customLocation("overpower_walk");
    protected static final ResourceLocation OVERPOWER_JUMP = CommonClass.customLocation("overpower_jump");
    protected static final ResourceLocation AFTERGLOW = CommonClass.customLocation("afterglow");
    protected static final BiPredicate<Entity, LivingEntity> NO_ATTACK = (entity, livingEntity) -> false;
    private static final List<SentinelBox> BOXES = new ObjectArrayList<>();
    protected static final BiConsumer<Entity, LivingEntity> SOLAR_RAY_HURT = (entity, livingEntity) -> {
        if (entity instanceof LivingEntity caster) {
            var handler = SpellUtil.getSpellHandler(caster);
            SolarRaySpell spell = handler.getSpell(SBSpells.SOLAR_RAY.get());
            if (spell != null)
                spell.hurt(livingEntity, spell.getBaseDamage());
        }
    };
    public static final OBBSentinelBox SOLAR_RAY = createSolarRay(false);
    public static final OBBSentinelBox SOLAR_RAY_EXTENDED = createSolarRay(true);
    public static final AABBSentinelBox OVERHEAT = AABBSentinelBox.Builder.of("overheat")
            .sizeAndOffset(3, 0, 1.5F, 0)
            .noDuration(entity -> false)
            .onBoxTick((entity, boxInstance) -> {
                Vec3 vec3 = boxInstance.getCenter();
                if (entity.level().isClientSide) {
                    double x = vec3.x;
                    double y = vec3.y;
                    double z = vec3.z;
                    for (int i = 0; i < 15; i++) {
                        entity.level().addParticle(ParticleTypes.FLAME,
                                x + RandomUtil.randomValueBetween(-3, 3),
                                y + RandomUtil.randomValueBetween(-3, 3),
                                z + RandomUtil.randomValueBetween(-3, 3),
                                entity.getRandom().nextDouble() * 0.05,
                                entity.getRandom().nextDouble() * 0.05,
                                entity.getRandom().nextDouble() * 0.05);
                    }
                }
            })
            .activeTicks((entity, integer) -> integer == 1 || integer % 20 == 0)
            .attackCondition(NO_ATTACK)
            .onCollisionTick(SOLAR_RAY_HURT).build();
    public static final AABBSentinelBox SOLAR_BURST_FRONT = AABBSentinelBox.Builder.of("solar_burst_front")
            .sizeAndOffset(2, 0, 2, 0)
            .noDuration(entity -> false)
            .activeTicks((entity, integer) -> integer % 60 == 0)
            .attackCondition(NO_ATTACK)
            .onCollisionTick(SOLAR_RAY_HURT).build();
    public static final OBBSentinelBox SOLAR_BURST_END = createSolarBurstEnd(false);
    public static final OBBSentinelBox SOLAR_BURST_END_EXTENDED = createSolarBurstEnd(true);
    private final Set<Integer> concentratedHeatSet = new IntOpenHashSet();
    private final Map<Integer, Integer> heatTracker = new Int2IntOpenHashMap();

    public static Builder<SolarRaySpell> createSolarRayBuilder() {
        return createChannelledSpellBuilder(SolarRaySpell.class)
                .mastery(SpellMastery.EXPERT)
                .manaCost(40)
                .manaTickCost(10)
                .baseDamage(3)
                .castTime(23)
                .castAnimation(context -> "solar_ray_cast")
                .channelAnimation(context -> "solar_ray_channel")
                .stopChannelAnimation("solar_ray_end");
    }

    public SolarRaySpell() {
        super(SBSpells.SOLAR_RAY.get(), createSolarRayBuilder());
    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        builder.define(SOLAR_RAY_ID, 0);
    }

    @Override
    public void onCastStart(SpellContext context) {
        super.onCastStart(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            this.summonEntity(context, SBEntities.SOLAR_RAY.get(), caster.position(), solarRay -> {
                if (context.getSkills().hasSkill(SBSkills.SUNSHINE))
                    solarRay.addSunshine();

                solarRay.setSpellId(1);
                solarRay.setStartTick(18);
                this.setSolarRay(solarRay.getId());
                solarRay.setPos(caster.position());
            });
        }

        context.getSpellHandler().setStationaryTicks(this.getCastTime() + 1);
    }

    @Override
    public void onCastReset(SpellContext context) {
        super.onCastReset(context);
        SolarRay solarRay = getSolarRay(context);
        if (solarRay != null)
            solarRay.setEndTick(9);

        context.getSpellHandler().setStationaryTicks(0);
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        super.onSpellStart(context);
        LivingEntity caster = context.getCaster();
        if (!context.getLevel().isClientSide) {
            var boxOwner = (ISentinel) caster;
            boolean hasSunshine = context.hasSkill(SBSkills.SUNSHINE);
            SentinelBox rayBox = hasSunshine ? SOLAR_RAY_EXTENDED : SOLAR_RAY;
            SentinelBox burstBox = hasSunshine ? SOLAR_BURST_END_EXTENDED : SOLAR_BURST_END;
            boxOwner.triggerSentinelBox(rayBox);

            if (context.hasSkill(SBSkills.SOLAR_BURST)) {
                boxOwner.triggerSentinelBox(SOLAR_BURST_FRONT);
                boxOwner.triggerSentinelBox(burstBox);
            }
            
            if (context.hasSkill(SBSkills.OVERPOWER)) {
                this.addSkillBuff(
                        caster,
                        SBSkills.OVERPOWER,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(OVERPOWER_WALK, -0.75, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                );
                this.addSkillBuff(
                        caster,
                        SBSkills.OVERPOWER,
                        BuffCategory.BENEFICIAL,
                        SkillBuff.ATTRIBUTE_MODIFIER,
                        new ModifierData(Attributes.JUMP_STRENGTH, new AttributeModifier(OVERPOWER_JUMP, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                );
            }
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        LivingEntity caster = context.getCaster();
        var handler = context.getSpellHandler();
        if (context.hasSkill(SBSkills.OVERHEAT) && this.tickCount == 100)
            ((ISentinel)caster).triggerSentinelBox(OVERHEAT);

        boolean overPower = context.hasSkill(SBSkills.OVERPOWER);
        if (!overPower)
            handler.setStationaryTicks(1);

        if (caster instanceof Player player) {
            if (!context.getLevel().isClientSide && overPower) {
                playMovementAnimation(player, "solar_walk", "solar_ray_channel");
            } else {
//                shakeScreen(player, 10, 5);
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        super.onSpellStop(context);
        LivingEntity caster = context.getCaster();
        var handler = context.getSpellHandler();
        handler.setStationaryTicks(20);
        removeSkillBuff(caster, SBSkills.OVERPOWER);
        for (SentinelBox box : BOXES) {
            ((ISentinel) caster).removeSentinelInstance(box);
        }

        SolarRay solarRay = getSolarRay(context);
        if (solarRay != null)
            solarRay.setEndTick(9);
    }

    private void setSolarRay(int solarRay) {
        this.spellData.set(SOLAR_RAY_ID, solarRay);
    }

    private SolarRay getSolarRay(SpellContext context) {
        Entity entity = context.getLevel().getEntity(this.spellData.get(SOLAR_RAY_ID));
        return entity instanceof SolarRay solarRay ? solarRay : null;
    }

    private static OBBSentinelBox createSolarRay(boolean isExtended) {
        String name = "solar_ray";
        float range = isExtended ? 7.7F : 3.85F;
        return OBBSentinelBox.Builder.of(isExtended ? name + "_extended" : name)
                .sizeAndOffset(0.75F, 0.75F, range, 0.0F, 1.7F, range)
                .moverType(SentinelBox.MoverType.HEAD_NO_X)
                .noDuration(entity -> false)
                .activeTicks((entity, integer) -> integer % 10 == 1)
                .attackCondition(NO_ATTACK)
                .onCollisionTick((entity, livingEntity) -> {
                    if (entity instanceof LivingEntity caster) {
                        var skills = SpellUtil.getSkills(caster);
                        var handler = SpellUtil.getSpellHandler(caster);
                        SolarRaySpell spell = handler.getSpell(SBSpells.SOLAR_RAY.get());
                        if (spell != null) {
                            boolean isAllied = SpellUtil.IS_ALLIED.test(caster, livingEntity);
                            if (skills.hasSkill(SBSkills.HEALING_LIGHT) && isAllied) {
                                livingEntity.heal(2);
                            } else if (!isAllied) {
                                float damage = spell.getBaseDamage();
                                int bonus = spell.concentratedHeatSet.contains(livingEntity.getId()) ? 2 : 1;
                                damage *= bonus;
                                if (skills.hasSkill(SBSkills.POWER_OF_THE_SUN) && livingEntity.level().isDay())
                                    damage *= 1.5F;

                                if (spell.hurt(livingEntity, damage)) {
                                    if (skills.hasSkill(SBSkills.CONCENTRATED_HEAT)) {
                                        int entityID = livingEntity.getId();
                                        if (spell.heatTracker.containsKey(entityID)) {
                                            int startTime = livingEntity.getData(SBData.HEAT_TICK);
                                            int currentTime = spell.heatTracker.get(entityID);
                                            if (livingEntity.tickCount != currentTime + 10) {
                                                livingEntity.setData(SBData.HEAT_TICK, livingEntity.tickCount);
                                                spell.concentratedHeatSet.remove(entityID);
                                            } else {
                                                if (livingEntity.tickCount == startTime + 100)
                                                    spell.concentratedHeatSet.add(livingEntity.getId());
                                            }

                                            spell.heatTracker.replace(entityID, livingEntity.tickCount);
                                        } else {
                                            spell.heatTracker.put(entityID, livingEntity.tickCount);
                                            livingEntity.setData(SBData.HEAT_TICK, livingEntity.tickCount);
                                        }
                                    }

                                    if (skills.hasSkill(SBSkills.BLINDING_LIGHT))
                                        spell.addSkillBuff(
                                                livingEntity,
                                                SBSkills.BLINDING_LIGHT,
                                                BuffCategory.HARMFUL,
                                                SkillBuff.MOB_EFFECT,
                                                new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, true, true),
                                                100
                                        );

                                    if (skills.hasSkill(SBSkills.AFTERGLOW)) {
                                        spell.addSkillBuff(
                                                livingEntity,
                                                SBSkills.AFTERGLOW,
                                                BuffCategory.HARMFUL,
                                                SkillBuff.MOB_EFFECT,
                                                new SBEffectInstance(caster, SBEffects.AFTERGLOW, 100, true, 0, true, true),
                                                100
                                        );
                                        spell.addEventBuff(
                                                livingEntity,
                                                SBSkills.AFTERGLOW,
                                                BuffCategory.HARMFUL,
                                                SpellEventListener.Events.PRE_DAMAGE,
                                                AFTERGLOW,
                                                pre -> {
                                                    DamageSource source = pre.getSource();
                                                    if (source.is(SBDamageTypes.RUIN_FIRE) || source.is(DamageTypeTags.IS_FIRE))
                                                        pre.setNewDamage(pre.getOriginalDamage() * 1.2F);
                                                },
                                                100
                                        );
                                    }
                                }
                            }
                        }
                    }
                }).build();
    }

    private static OBBSentinelBox createSolarBurstEnd(boolean isExtended) {
        String name = "solar_burst_end";
        String newName = isExtended ? name + "_extended" : name;
        return OBBSentinelBox.Builder.of(newName)
                .sizeAndOffset(2, 0, 2, 9.2F)
                .moverType(SentinelBox.MoverType.HEAD_NO_X)
                .noDuration(entity -> false)
                .activeTicks((entity, integer) -> integer % 60 == 0)
                .attackCondition(NO_ATTACK)
                .onBoxTick((entity, instance) -> {
                    Level level = entity.level();
                    if (!(entity instanceof LivingEntity livingEntity)) return;

                    var handler = SpellUtil.getSpellHandler(livingEntity);
                    var skills = SpellUtil.getSkills(livingEntity);
                    if (!level.isClientSide) {
                        if (skills.hasSkill(SBSkills.SOLAR_BORE)) {
                            Vec3 vec3 = instance.getCenter();
                            if (instance.tickCount % 60 == 0)
                                level.explode(livingEntity, Explosion.getDefaultDamageSource(level, livingEntity), null, vec3.x(), vec3.y(), vec3.z(), 4.0F, true, Level.ExplosionInteraction.TNT);
                        }

                        SolarRaySpell spell = handler.getSpell(SBSpells.SOLAR_RAY.get());
                        if (spell != null && spell.getContext() != null) {
                            SolarRay solarRay = spell.getSolarRay(spell.getContext());
                            String extended = skills.hasSkill(SBSkills.SUNSHINE) ? "_extended" : "";
                            if (solarRay != null && instance.tickCount % 60 == 0)
                                solarRay.triggerAnim("burst" + extended, "solar_burst" + extended);
                        }
                    }
                })
                .onCollisionTick(SOLAR_RAY_HURT).build();
    }

    static {
        BOXES.add(SOLAR_RAY);
        BOXES.add(SOLAR_RAY_EXTENDED);
        BOXES.add(OVERHEAT);
        BOXES.add(SOLAR_BURST_FRONT);
        BOXES.add(SOLAR_BURST_END);
        BOXES.add(SOLAR_BURST_END_EXTENDED);
    }
}
