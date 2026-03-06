package com.ombremoon.spellbound.common.world.spell.deception;

import com.ombremoon.spellbound.client.gui.SkillTooltip;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.effects.EffectHolder;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.effects.RangeProvider;
import com.ombremoon.spellbound.common.magic.effects.TickProvider;
import com.ombremoon.spellbound.common.magic.effects.types.*;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import com.ombremoon.spellbound.common.world.EntityResource;
import com.ombremoon.spellbound.common.world.entity.spell.CursedRune;
import com.ombremoon.spellbound.common.world.sound.SpellboundSounds;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.FluidPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class CursedRuneSpell extends AnimatedSpell implements RadialSpell {
    private static Builder<CursedRuneSpell> createCursedRuneBuilder() {
        return createSimpleSpellBuilder(CursedRuneSpell.class)
                .duration(300)
                .manaCost(10)
                .baseDamage(2)
                .castCondition((context, spell) -> {
                    Vec3 pos = spell.getSpawnVec();
                    if (pos != null) {
                        spell.spawnPos = pos;
                        return true;
                    }

                    return false;
                });
    }
    private static final SpellDataKey<Integer> CURSED_RUNE = SyncedSpellData.registerDataKey(CursedRuneSpell.class, SBDataTypes.INT.get());
    private Vec3 spawnPos;

    public enum CastSoundType {
        PLACE,
        DISCHARGE
    }

    public CursedRuneSpell() {
        super(SBSpells.CURSED_RUNE.get(), createCursedRuneBuilder());
    }

    @Override
    public void registerSkillTooltips() {
        this.addSkillDetails(SBSkills.CURSED_RUNE,
                SkillTooltip.DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.MAGIC, this.getModifiedDamage())),
                SkillTooltip.DURATION.tooltip(300),
                SkillTooltip.MANA_COST.tooltip(this.getManaCost()),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.MAGE_WRECK,
                SkillTooltip.MANA_DRAIN.tooltip(potency(5)),
                SkillTooltip.EFFECT_DURATION.tooltip(200),
                SkillTooltip.POTENCY_SCALING.tooltip(),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.MIRROR_CURSE,
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.DISARMING_CURSE,
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.CURSE_OF_PAIN,
                SkillTooltip.MODIFY_DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.PHYSICAL, potency(25F))),
                SkillTooltip.EFFECT_DURATION.tooltip(200),
                SkillTooltip.POTENCY_SCALING.tooltip(),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.CURSE_OF_SILENCE,
                SkillTooltip.MOB_EFFECT.tooltip(SBEffects.SILENCED),
                SkillTooltip.EFFECT_DURATION.tooltip(60),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.VANISHING_CURSE,
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.CURSE_OF_WEAKNESS,
                SkillTooltip.TARGET_PHYSICAL_DAMAGE.tooltip(potency(-10F)),
                SkillTooltip.EFFECT_DURATION.tooltip(200),
                SkillTooltip.POTENCY_SCALING.tooltip(),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.CURSE_OF_SUSCEPTIBILITY,
                SkillTooltip.MODIFY_DAMAGE.tooltip(new SkillTooltip.SpellDamage(DamageTranslation.MAGIC, potency(10F))),
                SkillTooltip.EFFECT_DURATION.tooltip(200),
                SkillTooltip.POTENCY_SCALING.tooltip(),
                SkillTooltip.CHOICE.tooltip()
        );
        this.addSkillDetails(SBSkills.TANGLEFOOT_CURSE,
                SkillTooltip.MOB_EFFECT.tooltip(SBEffects.ROOTED),
                SkillTooltip.EFFECT_DURATION.tooltip(60),
                SkillTooltip.CHOICE.tooltip()
        );
    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        super.defineSpellData(builder);
        builder.define(CURSED_RUNE, 0);
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            EffectHolder effect = EffectHolder.simple(
                    new DamageEntity(SBDamageTypes.SB_GENERIC, 2),
                    Optional.empty(),
                    new TickProvider.AtTick(0),
                    BuffCategory.HARMFUL,
                    1
            );
            if (this.isChoice(SBSkills.MAGE_WRECK)) {
                effect = EffectHolder.simple(
                        new SetResource(EntityResource.MANA, potency(-5), EntityResource.Operation.ADD),
                        Optional.empty(),
                        new TickProvider.ForEveryTick(20),
                        BuffCategory.HARMFUL,
                        200
                );
            } else if (this.isChoice(SBSkills.DISARMING_CURSE)) {
                effect = EffectHolder.simple(
                        new DisarmEffect(),
                        Optional.empty(),
                        new TickProvider.AtTick(0),
                        BuffCategory.HARMFUL,
                        1
                );
            } else if (this.isChoice(SBSkills.MIRROR_CURSE)) {
                effect = EffectHolder.simple(
                        new SummonDoppelganger(),
                        Optional.empty(),
                        new TickProvider.AtTick(0),
                        BuffCategory.HARMFUL,
                        1
                );
            } else if (this.isChoice(SBSkills.CURSE_OF_PAIN)) {
                effect = EffectHolder.withComponents(
                        new ModifyDamage(potency(0.25F), EntityResource.Operation.ADD_MULTIPLIED_TOTAL),
                        Optional.of(
                                DamageSourceCondition.hasDamageSource(
                                        DamageSourcePredicate.Builder.damageType().tag(
                                                TagPredicate.isNot(SBTags.DamageTypes.SPELL_DAMAGE)
                                        )
                                ).build()
                        ),
                        new TickProvider.NoTick(),
                        DataComponentMap.builder()
                                .set(SBData.PRE_DAMAGE, EnchantmentTarget.VICTIM),
                        BuffCategory.HARMFUL,
                        200
                );
            } else if (this.isChoice(SBSkills.CURSE_OF_SILENCE)) {
                effect = EffectHolder.simple(
                        new ApplyMobEffect(HolderSet.direct(SBEffects.SILENCED), 3, 3, 0, 0),
                        Optional.empty(),
                        new TickProvider.EveryTick(),
                        BuffCategory.HARMFUL,
                        1
                );
            } else if (this.isChoice(SBSkills.VANISHING_CURSE)) {
                effect = EffectHolder.simple(
                        new TeleportEntity(new RangeProvider(10.0F, 20.0F, 1.0F, 5.0F), false),
                        Optional.empty(),
                        new TickProvider.AtTick(0),
                        BuffCategory.HARMFUL,
                        1
                );
            } else if (this.isChoice(SBSkills.CURSE_OF_WEAKNESS)) {
                effect = EffectHolder.withComponents(
                        new ModifyDamage(potency(-0.1F), EntityResource.Operation.ADD_MULTIPLIED_TOTAL),
                        Optional.of(
                                DamageSourceCondition.hasDamageSource(
                                        DamageSourcePredicate.Builder.damageType().tag(
                                                TagPredicate.isNot(SBTags.DamageTypes.SPELL_DAMAGE)
                                        )
                                ).build()
                        ),
                        new TickProvider.NoTick(),
                        DataComponentMap.builder()
                                .set(SBData.PRE_ATTACK, EnchantmentTarget.ATTACKER),
                        BuffCategory.HARMFUL,
                        200
                );
            } else if (this.isChoice(SBSkills.CURSE_OF_SUSCEPTIBILITY)) {
                effect = EffectHolder.withComponents(
                        new ModifyDamage(potency(0.1F), EntityResource.Operation.ADD_MULTIPLIED_TOTAL),
                        Optional.of(
                                DamageSourceCondition.hasDamageSource(
                                        DamageSourcePredicate.Builder.damageType().tag(
                                                TagPredicate.is(SBTags.DamageTypes.SPELL_DAMAGE)
                                        )
                                ).build()
                        ),
                        new TickProvider.NoTick(),
                        DataComponentMap.builder()
                                .set(SBData.PRE_DAMAGE, EnchantmentTarget.VICTIM),
                        BuffCategory.HARMFUL,
                        200
                );
            } else if (this.isChoice(SBSkills.TANGLEFOOT_CURSE)) {
                effect = EffectHolder.simple(
                        new ApplyMobEffect(HolderSet.direct(SBEffects.ROOTED), 3, 3, 0, 0),
                        Optional.empty(),
                        new TickProvider.AtTick(0),
                        BuffCategory.HARMFUL,
                        1
                );
            }

            EffectHolder finalEffect = effect;
            this.summonEntity(context, SBEntities.CURSED_RUNE.get(), this.spawnPos, cursedRune -> {
                cursedRune.setRuneEffects(List.of(finalEffect));
                cursedRune.setHidden(context.hasSkill(SBSkills.HIDDEN_RUNE));
                this.setRune(cursedRune);
                //sound
                playCastSound(level, context, CastSoundType.PLACE);
            });

        }
    }

    public void playCastSound(Level level, SpellContext context, CastSoundType type){
        float volume = 0;
        float pitch = 0;

        if (type == CastSoundType.PLACE) {
            volume = 0.1F + level.random.nextFloat() * 0.1F;
            pitch = 1.0F + level.random.nextFloat() * 0.3F;
        } else if(type==CastSoundType.DISCHARGE){
            volume = 0.5F + level.random.nextFloat() * 0.3F;
            pitch = 0.3F + level.random.nextFloat() * 0.2F;
    }
        level.playSound(null, context.getBlockPos(),
                SpellboundSounds.CURSED_RUNE_ACTIVATED.get(),
                SoundSource.PLAYERS, volume, pitch);
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        Level level = context.getLevel();
        if (!level.isClientSide && this.getRune(context) == null) {
            this.endSpell();
            playCastSound(level, context, CastSoundType.DISCHARGE);

        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide && this.getRune(context) != null) {
            CursedRune rune = this.getRune(context);
            if (rune != null) {
                rune.setEndTick(10);
                playCastSound(level, context, CastSoundType.DISCHARGE);
            }
        }
    }

    public CursedRune getRune(SpellContext context) {
        Entity entity = context.getLevel().getEntity(this.spellData.get(CURSED_RUNE));
        return entity instanceof CursedRune cursedRune ? cursedRune : null;
    }

    public void setRune(CursedRune rune) {
        this.spellData.set(CURSED_RUNE, rune.getId());
    }
}
