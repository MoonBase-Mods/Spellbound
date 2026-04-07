package com.ombremoon.spellbound.common.world.spell.transfiguration;

import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.buff.*;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import com.ombremoon.spellbound.common.world.entity.spell.ShadowGate;
import com.ombremoon.spellbound.common.world.sound.SpellboundSounds;
import com.ombremoon.spellbound.common.world.spell.deception.CursedRuneSpell;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import com.ombremoon.spellbound.util.portal.PortalMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

//TODO: ADD OPEN/CLOSE ANIMATIONS

public class ShadowGateSpell extends AnimatedSpell implements RadialSpell {
    private static final ResourceLocation BLINK = CommonClass.customLocation("blink");
    private static final ResourceLocation UNWANTED_GUESTS_DAMAGE = CommonClass.customLocation("unwanted_guests");
    private static final ResourceLocation UNWANTED_GUESTS_POTENCY = CommonClass.customLocation("unwanted_guests_potency");
    private static final ResourceLocation SHADOW_ESCAPE = CommonClass.customLocation("shadow_escape");
    private static final ResourceLocation GRAVITY_SHIFT = CommonClass.customLocation("gravity_shift");
    private final PortalMap<ShadowGate> portalMap = new PortalMap<>();



    private static Builder<ShadowGateSpell> createShadowGateBuilder() {
        return createSimpleSpellBuilder(ShadowGateSpell.class)
                .manaCost(25)
                .duration(1200)
                .castCondition((context, spell) -> {
                    boolean hasReach = context.hasSkill(SBSkills.REACH);
                    BlockPos blockPos = spell.getSpawnPos(hasReach ? 100 : 50);
                    if (blockPos == null) return false;

                    if (!context.getLevel().getBlockState(blockPos).isAir()) return false;

                    if (context.hasSkill(SBSkills.DARKNESS_PREVAILS)) return true;
                    int i = context.getLevel().getRawBrightness(blockPos, 0) + context.getLevel().getBrightness(LightLayer.BLOCK, blockPos) - context.getLevel().getSkyDarken();
                    return i <= 9;
                })
                .summonCast()
                .fullRecast(false)
                .skipEndOnRecast();
    }

    public ShadowGateSpell() {
        super(SBSpells.SHADOW_GATE.get(), createShadowGateBuilder());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.SHADOW_GATE,
                SkillTooltip.DURATION.tooltip(1200),
                SkillTooltip.MANA_COST.tooltip(this.getManaCost()),
                SkillTooltip.RANGE.tooltip(50F),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.REACH,
                SkillTooltip.MODIFY_RANGE.tooltip(100F)
        );
        this.addSkillDetails(SBSkills.BLINK,
                SkillTooltip.ATTRIBUTE.tooltip(new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(BLINK, 0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))),
                SkillTooltip.EFFECT_DURATION.tooltip(100)
        );
        this.addSkillDetails(SBSkills.SHADOW_ESCAPE,
                SkillTooltip.HP_THRESHOLD.tooltip(50F),
                SkillTooltip.EFFECT_DURATION.tooltip(100),
                SkillTooltip.MOB_EFFECT.tooltip(SBEffects.MAGI_INVISIBILITY),
                SkillTooltip.INVISIBILITY_NO_STACK.tooltip()
        );
        this.addSkillDetails(SBSkills.QUICK_RECHARGE,
                SkillTooltip.MANA.tooltip(10F)
        );
        this.addSkillDetails(SBSkills.UNWANTED_GUESTS,
                SkillTooltip.TARGET_PHYSICAL_DAMAGE.tooltip(-10F),
                SkillTooltip.TARGET_SPELL_DAMAGE.tooltip(-10F),
                SkillTooltip.TARGET_SPELL_POTENCY.tooltip(-10F)
        );
        this.addSkillDetails(SBSkills.BAIT_AND_SWITCH,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.MAGIC, 5F)),
                SkillTooltip.MANA_DAMAGE.tooltip(5F)
        );
        this.addSkillDetails(SBSkills.GRAVITY_SHIFT,
                SkillTooltip.MOB_EFFECT.tooltip(MobEffects.SLOW_FALLING),
                SkillTooltip.EFFECT_DURATION.tooltip(100),
                SkillTooltip.CHOICE.tooltip()
        );
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            boolean hasReach = context.hasSkill(SBSkills.REACH);
            this.summonEntity(context, SBEntities.SHADOW_GATE.get(), hasReach ? 100 : 50, shadowGate -> {
                int maxPortals = context.hasSkill(SBSkills.DUAL_DESTINATION) ? 3 : 2;
                if (this.isChoice(SBSkills.GRAVITY_SHIFT))
                    shadowGate.shift();

                this.portalMap.createOrShiftPortal(shadowGate, maxPortals, 20);
            });
            playCastSound(level, context, CursedRuneSpell.CastSoundType.PLACE);
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            if (!this.portalMap.isEmpty()) {
                for (var entry : this.portalMap.entrySet()) {
                    ShadowGate shadowGate = (ShadowGate) level.getEntity(entry.getKey());
                    if (shadowGate != null) {
                        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, shadowGate.getBoundingBox());
                        List<LivingEntity> teleportList = new ObjectArrayList<>();
                        for (LivingEntity entity : entities) {
                            if (context.hasSkill(SBSkills.OPEN_INVITATION)) {
                                teleportList.add(entity);
                            } else if (isCaster(entity)) {
                                teleportList.add(entity);
                            }
                        }

                        for (LivingEntity entity : teleportList) {
                            if (this.portalMap.attemptTeleport(entity, shadowGate)) {
                                if (context.hasSkill(SBSkills.BLINK) && isCaster(entity))
                                    addSkillBuff(
                                            caster,
                                            SBSkills.BLINK,
                                            BLINK,
                                            BuffCategory.BENEFICIAL,
                                            SkillBuff.ATTRIBUTE_MODIFIER,
                                            new ModifierData(Attributes.MOVEMENT_SPEED, new AttributeModifier(BLINK, 0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                                            100);

                                if (context.hasSkillReady(SBSkills.QUICK_RECHARGE)) {
                                    this.consumeMana(caster, -10);
                                    addCooldown(SBSkills.QUICK_RECHARGE, 200);
                                }

                                if (context.hasSkill(SBSkills.SHADOW_ESCAPE) && isCaster(entity) && caster.getHealth() < caster.getMaxHealth() * 0.5F && !caster.hasEffect(SBEffects.MAGI_INVISIBILITY)) {
                                    addSkillBuff(
                                            caster,
                                            SBSkills.SHADOW_ESCAPE,
                                            SHADOW_ESCAPE,
                                            BuffCategory.BENEFICIAL,
                                            SkillBuff.MOB_EFFECT,
                                            new MobEffectInstance(SBEffects.MAGI_INVISIBILITY, 100, 0, false, false, true),
                                            100
                                    );
                                }

                                if (!isCaster(entity) && context.hasSkill(SBSkills.UNWANTED_GUESTS) && !entity.isAlliedTo(context.getCaster())) {
                                    addEventBuff(
                                            entity,
                                            SBSkills.UNWANTED_GUESTS,
                                            BuffCategory.HARMFUL,
                                            SpellEventListener.Events.PRE_DAMAGE,
                                            UNWANTED_GUESTS_DAMAGE,
                                            pre -> pre.setNewDamage(pre.getOriginalDamage() * 0.9F),
                                            200);
                                    addSkillBuff(
                                            entity,
                                            SBSkills.UNWANTED_GUESTS,
                                            UNWANTED_GUESTS_POTENCY,
                                            BuffCategory.HARMFUL,
                                            SkillBuff.SPELL_MODIFIER,
                                            SpellModifier.UNWANTED_GUESTS,
                                            200);
                                }

                                if (context.hasSkill(SBSkills.BAIT_AND_SWITCH) && !entity.isAlliedTo(caster)) {
                                    this.hurt(entity, 5);
                                    this.consumeMana(entity, 5);
                                }

                                ShadowGate adjacentGate = this.portalMap.getAdjacentPortal(shadowGate, level);
                                if (adjacentGate.isShifted()) {
                                    Vec3 lookVec = adjacentGate.getViewVector(1.0F);
                                    entity.setDeltaMovement(lookVec.x, 2, lookVec.z);
                                    entity.hurtMarked = true;
                                    if (isCaster(entity)) {
                                        addSkillBuff(
                                                caster,
                                                SBSkills.GRAVITY_SHIFT,
                                                GRAVITY_SHIFT,
                                                BuffCategory.BENEFICIAL,
                                                SkillBuff.MOB_EFFECT,
                                                new MobEffectInstance(MobEffects.SLOW_FALLING, 100),
                                                100
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            this.portalMap.forEach((id, portalInfo) -> {
                Entity entity = context.getLevel().getEntity(id);
                if (entity instanceof ShadowGate shadowGate)
                    shadowGate.setEndTick(20);
            });
            playCastSound(level, context, CursedRuneSpell.CastSoundType.DISCHARGE);
        }
    }

    public void playCastSound(Level level, SpellContext context, CursedRuneSpell.CastSoundType type){
       float volume = 0.1F + level.random.nextFloat() * 0.3F;
       float pitch = 0.5F + level.random.nextFloat() * 0.3F;

        if (type == CursedRuneSpell.CastSoundType.PLACE) {
            level.playSound(null, context.getBlockPos(),
                    SpellboundSounds.CURSED_RUNE_ACTIVATED.get(),
                    SoundSource.PLAYERS, 0.8F * volume , 0.8F * pitch);

            level.playSound(null, context.getBlockPos(),
                    SpellboundSounds.SHADOW_PLACE.get(),
                    SoundSource.PLAYERS, volume, pitch);

        } else if(type== CursedRuneSpell.CastSoundType.DISCHARGE){
            level.playSound(null, context.getBlockPos(),
                    SpellboundSounds.CURSED_RUNE_ACTIVATED.get(),
                    SoundSource.PLAYERS, volume, pitch);
        }
    }

    @Override
    public @UnknownNullability CompoundTag saveData(CompoundTag compoundTag) {
        CompoundTag nbt = super.saveData(compoundTag);
        this.portalMap.serialize(nbt);
        return nbt;
    }

    @Override
    public void loadData(CompoundTag nbt) {
        super.loadData(nbt);
        this.portalMap.deserialize(nbt);
    }
}
