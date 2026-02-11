package com.ombremoon.spellbound.common.world.spell.ruin.fire;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.sentinellib.api.box.AABBSentinelBox;
import com.ombremoon.sentinellib.api.box.OBBSentinelBox;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.sentinellib.common.event.RegisterPlayerSentinelBoxEvent;
import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.client.particle.EffectBuilder;
import com.ombremoon.spellbound.common.init.SBDataTypes;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.ChargeableSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.SpellAnimation;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.BiPredicate;

public class FlameJetSpell extends AnimatedSpell implements ChargeableSpell, RadialSpell {
    protected static final BiPredicate<Entity, LivingEntity> NO_ATTACK = (entity, livingEntity) -> false;
    private static final List<OBBSentinelBox> FLAME_JET_BOXES = createFlameJetBoxes();
    private static final List<OBBSentinelBox> IGNITION_BURST_BOXES = createIgnitionBurstBoxes();
    private static final List<AABBSentinelBox> FLAME_INFERNO_BOXES = createFlameInfernoBoxes();
    private static final List<AABBSentinelBox> FLAME_GEYSER_BOXES = createFlameGeyserBoxes();
    private static final OBBSentinelBox REAR_JET_BOX = createRearJet();
    private static final SpellDataKey<Vector3f> GEYSER_POS = SyncedSpellData.registerDataKey(FlameJetSpell.class, SBDataTypes.VECTOR3.get());
    private int turbulenceCharges;
    private long lastFlameJetCast;
    private float damageMultiplier = 1.0F;
    public static Builder<FlameJetSpell> createFlameJetBuilder() {
        return createSimpleSpellBuilder(FlameJetSpell.class)
                .duration(5)
                .manaCost(35)
                .baseDamage(3)
                .castTime(5)
                .castCondition((context, spell) -> {
                    if (context.isChoice(SBSkills.FLAME_GEYSER)) {
                        double range = SpellUtil.getCastRange(context.getCaster());
                        Entity target = spell.getTargetEntity(context.getCaster(), range);
                        if (target != null)
                            return true;

                        BlockHitResult hitResult = spell.getTargetBlock(range);
                        if (hitResult.getDirection() != Direction.UP)
                            return false;

                        return spell.getTargetPosition(range) != null;
                   } else if (context.isChoice(SBSkills.IRON_MAN)) {
                        return context.hasCatalyst(SBItems.SMOLDERING_SHARD.get());
                    }

                    return true;
                })
                .castAnimation(context -> {
                    boolean flag = !context.hasSkill(SBSkills.JET_STABILIZATION);
                    String castPrefix = flag ? "" : "walking_";
                    return new SpellAnimation(castPrefix + "instant_cast", SpellAnimation.Type.CAST, flag);
                })
                .fullRecast(true)
                .updateInterval(1);
    }

    private static List<OBBSentinelBox> createFlameJetBoxes() {
        ObjectArrayList<OBBSentinelBox> boxes = new ObjectArrayList<>();
        for (int i = 0; i < 7; i++) {
            boxes.add(createFlameJet(i));
        }
        return boxes;
    }

    private static List<OBBSentinelBox> createIgnitionBurstBoxes() {
        ObjectArrayList<OBBSentinelBox> boxes = new ObjectArrayList<>();
        for (int i = 0; i < 7; i++) {
            boxes.add(createIgnitionBurst(i));
        }
        return boxes;
    }

    private static List<AABBSentinelBox> createFlameInfernoBoxes() {
        ObjectArrayList<AABBSentinelBox> boxes = new ObjectArrayList<>();
        for (int i = 0; i < 4; i++) {
            boxes.add(createFlameInferno(i));
        }
        return boxes;
    }

    private static List<AABBSentinelBox> createFlameGeyserBoxes() {
        ObjectArrayList<AABBSentinelBox> boxes = new ObjectArrayList<>();
        for (int i = 0; i < 4; i++) {
            boxes.add(createFlameGeyser(i));
        }
        return boxes;
    }

    public static void registerBoxes(RegisterPlayerSentinelBoxEvent event) {
        for (SentinelBox box : FLAME_JET_BOXES) {
            event.addEntry(box);
        }
        for (SentinelBox box : IGNITION_BURST_BOXES) {
            event.addEntry(box);
        }
        for (SentinelBox box : FLAME_INFERNO_BOXES) {
            event.addEntry(box);
        }
        for (SentinelBox box : FLAME_GEYSER_BOXES) {
            event.addEntry(box);
        }
        event.addEntry(REAR_JET_BOX);
    }

    public FlameJetSpell() {
        super(SBSpells.FLAME_JET.get(), createFlameJetBuilder());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.FLAME_JET, SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.FIRE, this.getModifiedDamage())));
        this.addSkillDetails(SBSkills.EXPULSION_BLAST,
                SkillTooltip.KNOCKBACK.tooltip(1F),
                SkillTooltip.KNOCKBACK_PER_CHARGE.tooltip(25F)
        );
        this.addSkillDetails(SBSkills.JET_ENGINE, SkillTooltip.CHOICE.tooltip());
        this.addSkillDetails(SBSkills.FLAME_GEYSER,
                SkillTooltip.RANGE.tooltip(0.5F),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.FLAME_INFERNO,
                SkillTooltip.MODIFY_DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.FIRE, -50F)),
                SkillTooltip.RADIUS.tooltip(1F),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.TURBO_CHARGE,
                SkillTooltip.CHARGE_DURATION.tooltip(60),
                SkillTooltip.MAX_CHARGES.tooltip(3),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.IGNITION_BURST,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.FIRE, this.getModifiedDamage())),
                SkillTooltip.RADIUS.tooltip(0.75F),
                SkillTooltip.EXPLOSION_RADIUS.tooltip(2F)
        );
        this.addSkillDetails(SBSkills.TURBULENCE_STREAM,
                SkillTooltip.RANGE_PER_CHARGE.tooltip(0.25F),
                SkillTooltip.MAX_CHARGES.tooltip(3),
                SkillTooltip.COOLDOWN.tooltip(40),
                SkillTooltip.CHOICE_CONDITION.tooltip(
                        new SkillTooltip.ChoiceTooltip(SBSkills.FLAME_INFERNO, SkillTooltip.RADIUS_PER_CHARGE.tooltip(25F))
                )
        );
        this.addSkillDetails(SBSkills.AFTERSHOCK_COMPRESSION,
                SkillTooltip.MODIFY_DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.FIRE, 33F)),
                SkillTooltip.MAX_CHARGES.tooltip(3),
                SkillTooltip.COOLDOWN.tooltip(40)
        );
        this.addSkillDetails(SBSkills.IRON_MAN,
                SkillTooltip.MANA_TICK_COST.tooltip(-5),
                SkillTooltip.DURATION.tooltip(100),
                SkillTooltip.CHOICE.tooltip()
        );
    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        super.defineSpellData(builder);
        builder.define(GEYSER_POS, new Vector3f());
    }

    public void setGeyserPosition(Vec3 pos) {
        this.spellData.set(GEYSER_POS, new Vector3f((float)pos.x(), (float)pos.y(), (float)pos.z()));
    }

    public Vec3 getGeyserPosition() {
        Vector3f pos = this.spellData.get(GEYSER_POS);
        return new Vec3(pos.x(), pos.y(), pos.z());
    }

    @Override
    public void onCastStart(SpellContext context) {
        super.onCastStart(context);
        LivingEntity caster = context.getCaster();
        var handler = SpellUtil.getSpellHandler(caster);
        this.lastFlameJetCast = handler.lastCastTick;

        if (context.hasSkill(SBSkills.TURBULENCE_STREAM) && handler.previouslyCastSpell != null) {
            long lastCastTick = this.lastFlameJetCast;
            if (handler.previouslyCastSpell.isSpellType(this) && caster.level().getGameTime() - lastCastTick < 60 && lastCastTick != 0) {
                FlameJetSpell prevSpell = (FlameJetSpell) handler.previouslyCastSpell;
                this.turbulenceCharges = Math.min(prevSpell.turbulenceCharges + 1, 3);
            }
        }
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        var handler = SpellUtil.getSpellHandler(caster);
        if (!context.getLevel().isClientSide) {
            if (context.isChoice(SBSkills.IRON_MAN)) {
                handler.setChargingOrChannelling(true);
                var boxOwner = (ISentinel) caster;
                boxOwner.triggerSentinelBox(REAR_JET_BOX);
                return;
            }

            int charges = this.getCharges();
            var boxOwner = (ISentinel) caster;

            long lastCastTick = this.lastFlameJetCast;
            if (handler.previouslyCastSpell.isSpellType(this) && caster.level().getGameTime() - lastCastTick < 60 && lastCastTick != 0) {
                if (context.hasSkill(SBSkills.TURBULENCE_STREAM)) {
                    charges += this.turbulenceCharges;
                }

                if (context.hasSkill(SBSkills.AFTERSHOCK_COMPRESSION)) {
                    this.damageMultiplier *= 1.33F;
                }
            }

            if (context.isChoice(SBSkills.FLAME_JET) || context.isChoice(SBSkills.TURBO_CHARGE)) {
                int index = Math.min(charges, 6);
                SentinelBox rayBox = FLAME_JET_BOXES.get(index);
                boxOwner.triggerSentinelBox(rayBox);

                if (context.isChoice(SBSkills.TURBO_CHARGE) && context.hasSkill(SBSkills.IGNITION_BURST) && this.getCharges() == 3) {
                    boxOwner.triggerSentinelBox(IGNITION_BURST_BOXES.get(index));
                }
            } else if (context.isChoice(SBSkills.JET_ENGINE)) {
                boxOwner.triggerSentinelBox(REAR_JET_BOX);
            } else if (context.isChoice(SBSkills.FLAME_INFERNO)) {
                boxOwner.triggerSentinelBox(createFlameInferno(Math.min(charges, 3)));
            } else if (context.isChoice(SBSkills.FLAME_GEYSER)) {
                SentinelBox geyserBox = FLAME_GEYSER_BOXES.get(Math.min(charges, 3));
                boxOwner.triggerSentinelBox(geyserBox);
            }
        } else {
            if (context.isChoice(SBSkills.FLAME_JET) || context.isChoice(SBSkills.TURBO_CHARGE)) {
                this.addFX(
                        EffectBuilder.Entity.of(CommonClass.customLocation("flame_jet"), caster.getId(), EntityEffectExecutor.AutoRotate.LOOK)
                );
            } else if (context.isChoice(SBSkills.FLAME_GEYSER)) {
                this.addFX(
                        EffectBuilder.Block.of(CommonClass.customLocation("flame_jet"), BlockPos.containing(this.getGeyserPosition()))
                                .setOffset(0, -2.0, 0)
                                .setRotation(0, 0, 90)
                );
            }
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            if (context.isChoice(SBSkills.IRON_MAN)) {
                Vec3 lookVec = caster.getLookAngle();
                double strength = 0.75;
                Vec3 motion = new Vec3(lookVec.x * strength, lookVec.y * strength, lookVec.z * strength);
                caster.setDeltaMovement(motion);
                caster.hurtMarked = true;
                if (this.tickCount % 20 == 0) {
                    this.consumeMana(caster, 5);
                }
            } else if (this.tickCount == 5 && context.isChoice(SBSkills.JET_ENGINE)) {
                Vec3 lookVec = caster.getLookAngle();
                caster.setDiscardFriction(true);
                double strength = 2.0F + (Math.min(this.turbulenceCharges + this.getCharges(), 6) * 0.25F);
                Vec3 motion = new Vec3(lookVec.x * strength, lookVec.y * strength, lookVec.z * strength);
                caster.setDeltaMovement(caster.getDeltaMovement().add(motion));
                caster.hurtMarked = true;
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        LivingEntity caster = context.getCaster();
        var handler = context.getSpellHandler();
        handler.setChargingOrChannelling(false);
        if (context.isChoice(SBSkills.JET_ENGINE))
            caster.setDiscardFriction(false);
    }

    @Override
    protected int getDuration(SpellContext context) {
        return context.isChoice(SBSkills.IRON_MAN) ? 100 : super.getDuration(context);
    }

    @Override
    public int maxCharges(SpellContext context) {
        return 3;
    }

    @Override
    public int getCastTime(SpellContext context) {
        return context.isChoice(SBSkills.TURBO_CHARGE) ? 60 : super.getCastTime(context);
    }

    @Override
    public boolean canCharge(SpellContext context) {
        return context.isChoice(SBSkills.TURBO_CHARGE);
    }

    @Override
    public @UnknownNullability CompoundTag saveData(CompoundTag compoundTag) {
        CompoundTag nbt = super.saveData(compoundTag);
        nbt.putInt("turbulence_charges", this.turbulenceCharges);
        return nbt;
    }

    @Override
    public void loadData(CompoundTag nbt) {
        super.loadData(nbt);
        this.turbulenceCharges = nbt.getInt("turbulence_charges");
    }

    private static OBBSentinelBox createFlameJet(int charges) {
        String name = "flame_jet";
        name = charges != 0 ? name + "_" + charges : name;
        float range = 1.0F + (0.25F * charges);
        return OBBSentinelBox.Builder.of(name)
                .sizeAndOffset(0.5F, 0.5F, range, 0.0F, 1.0F, range)
                .moverType(SentinelBox.MoverType.HEAD)
                .noDuration(entity -> entity instanceof LivingEntity livingEntity && !SpellUtil.isSpellActive(SBSpells.FLAME_JET.get(), livingEntity))
                .activeTicks((entity, integer) -> integer == 1)
                .attackCondition(NO_ATTACK)
                .onCollisionTick((entity, livingEntity) -> {
                    if (entity instanceof LivingEntity caster) {
                        var handler = SpellUtil.getSpellHandler(caster);
                        FlameJetSpell spell = handler.getSpell(SBSpells.FLAME_JET.get());
                        if (spell != null) {
                            SpellContext context = spell.getContext();
                            float damage = spell.getBaseDamage();
                            if (context.hasSkill(SBSkills.AFTERSHOCK_COMPRESSION)) {
                                damage *= spell.damageMultiplier;
                            }

                            damage *= 1.0F + (0.15F * charges);
                            if (spell.hurt(livingEntity, damage) && context.hasSkill(SBSkills.EXPULSION_BLAST)) {
                                double strength = 1.0F + (Math.min(spell.turbulenceCharges + spell.getCharges(), 6) * 0.25F);
                                livingEntity.knockback(strength, caster.getX() - livingEntity.getX(), caster.getZ() - livingEntity.getZ());
                            }
                        }
                    }
                }).build();
    }

    private static AABBSentinelBox createFlameInferno(int charges) {
        String name = "flame_inferno";
        name = charges != 0 ? name + "_" + charges : name;
        float range = 1.0F + (0.25F * charges);
        return AABBSentinelBox.Builder.of(name)
                .sizeAndOffset(range, 0.5F, range, 0.0F, 1.0F, 0.0F)
                .moverType(SentinelBox.MoverType.HEAD)
                .noDuration(entity -> entity instanceof LivingEntity livingEntity && !SpellUtil.isSpellActive(SBSpells.FLAME_JET.get(), livingEntity))
                .activeTicks((entity, integer) -> integer == 1)
                .attackCondition(NO_ATTACK)
                .onCollisionTick((entity, livingEntity) -> {
                    if (entity instanceof LivingEntity caster) {
                        var handler = SpellUtil.getSpellHandler(caster);
                        FlameJetSpell spell = handler.getSpell(SBSpells.FLAME_JET.get());
                        if (spell != null) {
                            SpellContext context = spell.getContext();
                            float damage = spell.getBaseDamage() * 0.5F;
                            if (context.hasSkill(SBSkills.AFTERSHOCK_COMPRESSION)) {
                                damage *= spell.damageMultiplier;
                            }

                            damage *= 0.15F * charges;
                            if (spell.hurt(livingEntity, damage) && context.hasSkill(SBSkills.EXPULSION_BLAST)) {
                                double strength = 1.0F + (Math.min(spell.turbulenceCharges + spell.getCharges(), 6) * 0.25F);
                                livingEntity.knockback(strength, caster.getX() - livingEntity.getX(), caster.getZ() - livingEntity.getZ());
                            }
                        }
                    }
                }).build();
    }

    private static AABBSentinelBox createFlameGeyser(int charges) {
        String name = "flame_geyser";
        name = charges != 0 ? name + "_" + charges : name;
        float range = 1.5F + (0.5F * charges);
        return AABBSentinelBox.Builder.of(name)
                .sizeAndOffset(0.5F + (0.25F * charges), range, 0.5F + (0.25F * charges), 0.0F, range, 0.0F)
                .noDuration(entity -> entity instanceof LivingEntity livingEntity && !SpellUtil.isSpellActive(SBSpells.FLAME_JET.get(), livingEntity))
                .activeTicks((entity, integer) -> integer == 1)
                .attackCondition(NO_ATTACK)
                .moverType(SentinelBox.MoverType.POSITION)
                .setPosition((entity, partialTick) -> {
                    if (!(entity instanceof LivingEntity livingEntity))
                        return entity.getPosition(partialTick);

                    var handler = SpellUtil.getSpellHandler(livingEntity);
                    FlameJetSpell spell = handler.getSpell(SBSpells.FLAME_JET.get());
                    if (spell != null) {
                        double castRange = SpellUtil.getCastRange(livingEntity);
                        Entity target = handler.getTargetEntity();
                        if (target != null) {
                            Vec3 vec3 = target.getPosition(partialTick);
                            spell.setGeyserPosition(vec3);
                            return vec3;
                        }

                        Vec3 vec3 = spell.getSpawnVec(castRange);
                        spell.setGeyserPosition(vec3);
                        return vec3;
                    }

                    return entity.getPosition(partialTick);
                })
                .onCollisionTick((entity, livingEntity) -> {
                    if (entity instanceof LivingEntity caster) {
                        var handler = SpellUtil.getSpellHandler(caster);
                        FlameJetSpell spell = handler.getSpell(SBSpells.FLAME_JET.get());
                        if (spell != null) {
                            SpellContext context = spell.getContext();
                            float damage = spell.getBaseDamage();

                            if (context.hasSkill(SBSkills.AFTERSHOCK_COMPRESSION)) {
                                damage *= spell.damageMultiplier;
                            }

                            damage *= 0.15F * charges;
                            if (spell.hurt(livingEntity, damage) && context.hasSkill(SBSkills.EXPULSION_BLAST)) {
                                double strength = 0.5F + (Math.min(spell.turbulenceCharges, 3) * 0.25F);
                                Vec3 upwardMotion = new Vec3(0, strength, 0);
                                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(upwardMotion.scale(1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE))));
                                livingEntity.hurtMarked = true;
                            }
                        }
                    }
                }).build();
    }

    private static OBBSentinelBox createIgnitionBurst(int charges) {
        String name = "ignition_burst";
        name = charges != 0 ? name + "_" + charges : name;
        float range = 1.0F + (0.5F * charges);
        return OBBSentinelBox.Builder.of(name)
                .sizeAndOffset(0.75F, 0, 1.0F, 2.0F * range)
                .moverType(SentinelBox.MoverType.HEAD)
                .noDuration(entity -> entity instanceof LivingEntity livingEntity && !SpellUtil.isSpellActive(SBSpells.FLAME_JET.get(), livingEntity))
                .activeTicks((entity, integer) -> integer == 1)
                .attackCondition(NO_ATTACK)
                .onBoxTick((entity, instance) -> {
                    Level level = entity.level();
                    if (!(entity instanceof LivingEntity livingEntity)) return;

                    if (!level.isClientSide && instance.tickCount == 1) {
                        Vec3 vec3 = instance.getCenter();
                        level.explode(livingEntity, Explosion.getDefaultDamageSource(level, livingEntity), null, vec3.x(), vec3.y(), vec3.z(), 2.0F, false, Level.ExplosionInteraction.NONE);
                    }
                }).build();
    }

    private static OBBSentinelBox createRearJet() {
        String name = "rear_jet";
        return OBBSentinelBox.Builder.of(name)
                .sizeAndOffset(0.75F, 0.5F, 0.5F, 0.0F, 0.5F, -0.5F)
                .moverType(SentinelBox.MoverType.HEAD_NO_X)
                .noDuration(entity -> entity instanceof LivingEntity livingEntity && !SpellUtil.isSpellActive(SBSpells.FLAME_JET.get(), livingEntity))
                .activeTicks((entity, integer) -> integer % 5 == 1)
                .attackCondition(NO_ATTACK)
                .onCollisionTick((entity, livingEntity) -> {
                    if (entity instanceof LivingEntity caster) {
                        var handler = SpellUtil.getSpellHandler(caster);
                        FlameJetSpell spell = handler.getSpell(SBSpells.FLAME_JET.get());
                        if (spell != null) {
                            SpellContext context = spell.getContext();
                            if (spell.hurt(livingEntity) && context.hasSkill(SBSkills.EXPULSION_BLAST)) {
                                livingEntity.knockback(1.0F, caster.getX() - livingEntity.getX(), caster.getZ() - livingEntity.getZ());
                            }
                        }
                    }
                }).build();
    }
}
