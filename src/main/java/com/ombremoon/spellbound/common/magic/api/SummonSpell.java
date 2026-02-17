package com.ombremoon.spellbound.common.magic.api;

import com.ombremoon.spellbound.common.init.SBDataTypes;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.common.world.entity.ISpellEntity;
import com.ombremoon.spellbound.common.world.entity.SmartSpellEntity;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.world.entity.ai.goal.FollowSummonerGoal;
import com.ombremoon.spellbound.common.world.spell.summon.SummonUndeadSpell;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.*;

public abstract class SummonSpell extends AnimatedSpell {
    private static final SpellDataKey<Set<Integer>> SUMMONS = SyncedSpellData.registerDataKey(SummonSpell.class, SBDataTypes.INT_SET.get());
    private static final SpellDataKey<BlockPos> SUMMON_POS = SyncedSpellData.registerDataKey(SummonSpell.class, SBDataTypes.BLOCK_POS.get());
    private static final ResourceLocation POST_DAMAGE_EVENT = CommonClass.customLocation("summon_post_damage");
    private static final ResourceLocation CASTER_ATTACK_EVENT = CommonClass.customLocation("summon_caster_attack");
    private boolean summonedEntity;
    private boolean isSpecialChoice;
    private Skill choice;

    @SuppressWarnings("unchecked")
    public static <T extends SummonSpell> Builder<T> createSummonBuilder(Class<T> spellClass) {
        return (Builder<T>) new Builder<>()
                .castCondition((context, spell) -> {
                    var handler = context.getSpellHandler();
                    if (spell.isSpecialChoice) {
                        int summonCount = spell.getSummonSize(context);
                        int maxSummons = spell.getMaxSummons(context);
                        var list = handler.getActiveSpells(spell.spellType(), abstractSpell -> abstractSpell instanceof SummonSpell summonSpell && context.isChoice(summonSpell.choice));
                        if (!list.isEmpty() && spell.skipEndOnRecast(context)) {
                            list.forEach(AbstractSpell::endSpell);
                            return false;
                        } else if (summonCount + spell.getCharges() >= maxSummons) {
                            if (summonCount < maxSummons) {
                                spell.setCharges(maxSummons - summonCount - 1);
                                return true;
                            }

                            return false;
                        }
                    }

                    if (spell.hasValidSpawnPos()) {
                        spell.setSummonPos(spell.getSpawnPos());
                        return true;
                    }

                    return false;
                });
    }

    public SummonSpell(SpellType<?> spellType, Builder<?> builder) {
        super(spellType, builder);
        this.isSpecialChoice = builder.isSpecialChoice;
    }

    @Override
    protected void defineSpellData(SyncedSpellData.Builder builder) {
        super.defineSpellData(builder);
        builder.define(SUMMONS, new HashSet<>());
        builder.define(SUMMON_POS, BlockPos.ZERO);
    }

    protected boolean hasSpecialChoice(SummonSpell spell, SpellContext context) {
        var handler = context.getSpellHandler();
        var list = handler.getActiveSpells(spell.spellType(), abstractSpell -> abstractSpell instanceof SummonSpell summonSpell && context.isChoice(summonSpell.choice));
        return !list.isEmpty();
    }

    @Override
    public void onCastStart(SpellContext context) {
        var skills = context.getSkills();
        if (this.isSpecialChoice)
            this.choice = skills.getChoice(this.spellType());

        super.onCastStart(context);
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        var handler = context.getSpellHandler();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            handler.getListener().addListener(SpellEventListener.Events.POST_DAMAGE, POST_DAMAGE_EVENT, post -> {
                Entity entity = post.getSource().getEntity();
                var summons = this.getSummons();
                for (int id : summons) {
                    Entity summon = level.getEntity(id);
                    if (summon instanceof LivingEntity livingEntity && entity instanceof LivingEntity attacker) {
                        SpellUtil.setTarget(livingEntity, attacker);
                    }
                }
            });
            handler.getListener().addListener(SpellEventListener.Events.ATTACK, CASTER_ATTACK_EVENT, attack -> {
                Entity entity = attack.getTarget();
                var summons = this.getSummons();
                for (int id : summons) {
                    Entity summon = level.getEntity(id);
                    if (summon instanceof LivingEntity livingEntity && entity instanceof LivingEntity target && !target.is(livingEntity)) {
                        SpellUtil.setTarget(livingEntity, target);
                    }
                }
            });
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        Level level = context.getLevel();
        var summons = this.getSummons();
        if (!level.isClientSide && this.summonedEntity && summons.isEmpty())
            endSpell();
    }

    protected void onMobSummoned(LivingEntity entity, SpellContext context) {
        if (entity instanceof PathfinderMob mob) {
            mob.goalSelector.addGoal(1, new FollowSummonerGoal(mob, 1.0, 10.0F, 2.0F));
        }
    }

    public void onMobRemoved(LivingEntity entity, SpellContext context, @Nullable DamageSource source, Entity.RemovalReason reason) {

    }

    public void onMobPreDamage(SpellContext context, LivingDamageEvent.Pre event) {
    }

    public void onMobPostDamage(SpellContext context, LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof ISpellEntity<?>)) {
            this.awardXp(this.calculateHurtXP(event.getNewDamage()));
        }
    }

    public void onMobIncomingHurt(SpellContext context, LivingIncomingDamageEvent event) {
    }

    public void onMobPreHurt(SpellContext context, LivingDamageEvent.Pre event) {
    }

    public void onMobPostHurt(SpellContext context, LivingDamageEvent.Post event) {
    }

    /**
     * Discards the summons and removes the event listeners
     * @param context the context of the spells
     */
    @Override
    protected void onSpellStop(SpellContext context) {
        Level level = context.getLevel();
        var handler = context.getSpellHandler();
        if (!level.isClientSide) {
            var summons = this.getSummons();
            for (int summonId : summons) {
                Entity entity = level.getEntity(summonId);
                if (entity instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
                    this.onMobRemoved(livingEntity, context, null, Entity.RemovalReason.DISCARDED);
                    if (entity instanceof SmartSpellEntity) {
                        //SET DESPAWN ANIMATIONS
                    }
                    entity.discard();
                }
            }

            handler.getListener().removeListener(POST_DAMAGE_EVENT);
            handler.getListener().removeListener(CASTER_ATTACK_EVENT);
        }
    }

    /**
     * Returns the IDs of all summons created by this spells
     * @return Set of entity IDs
     */
    public Set<Integer> getSummons() {
        return this.spellData.get(SUMMONS);
    }

    private void summonEntity(LivingEntity livingEntity) {
        var summons = this.getSummons();
        summons.add(livingEntity.getId());
        this.spellData.set(SUMMONS, summons, true);
    }

    public void removeSummon(LivingEntity livingEntity) {
        var summons = this.getSummons();
        summons.remove(livingEntity.getId());
        this.spellData.set(SUMMONS, summons, true);
    }

    protected int getSummonSize(SpellContext context) {
        var handler = context.getSpellHandler();
        List<AbstractSpell> summonSpells = handler.getActiveSpells(this.spellType());
        int summons = 0;
        for (AbstractSpell spell : summonSpells) {
            summons += ((SummonSpell) spell).getSummons().size();
        }

        return summons;
    }

    protected int getMaxSummons(SpellContext context) {
        int i = this.hasSummonStaffBuff(context) ? 1 : 0;
        return context.getSpellLevel() + 1 + i;
    }

    public Vec3 getSummonPos() {
        return Vec3.atBottomCenterOf(this.spellData.get(SUMMON_POS));
    }

    public void setSummonPos(BlockPos pos) {
        this.spellData.set(SUMMON_POS, pos);
    }

    protected Vec3 getSurroundingSpawnPosition(Vec3 origin, float yaw, float radius, int charge, int maxCharges) {
        double angleStep = 2 * Math.PI / maxCharges;
        double angle = angleStep * charge;
        double totalAngle = angle + Math.toRadians(yaw);
        double xOffset = -Math.sin(totalAngle) * radius;
        double zOffset = Math.cos(totalAngle) * radius;
        return new Vec3(origin.x + xOffset, origin.y, origin.z + zOffset);
    }

    public Skill getSpecialChoice() {
        return this.choice;
    }

    @Override
    public <T extends Entity> T summonEntity(SpellContext context, EntityType<T> entityType, Vec3 spawnPos, Consumer<T> extraData) {
        T entity = super.summonEntity(context, entityType, spawnPos, extraData);
        if (entity instanceof LivingEntity livingEntity) {
            this.summonEntity(livingEntity);
            this.summonedEntity = true;
            this.onMobSummoned(livingEntity, context);
        }

        return entity;
    }

    @Override
    public @UnknownNullability CompoundTag saveData(CompoundTag compoundTag) {
        CompoundTag tag = super.saveData(compoundTag);
        tag.putString("Choice", this.choice == null ? "" : this.choice.location().toString());
        return tag;
    }

    public static class Builder<T extends SummonSpell> extends AnimatedSpell.Builder<T> {
        private boolean isSpecialChoice;

        public Builder() {
            this.summonCast();
        }

        public Builder<T> manaCost(int manaCost) {
            this.manaCost = manaCost;
            return this;
        }

        public Builder<T> duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder<T> baseDamage(float baseDamage) {
            this.baseDamage = baseDamage;
            return this;
        }

        public Builder<T> xpModifier(float modifier) {
            this.xpModifier = modifier;
            return this;
        }

        public Builder<T> castTime(int castTime) {
            this.castTime = castTime;
            return this;
        }

        public Builder<T> castAnimation(BiFunction<SpellContext, T, SpellAnimation> castAnimationName) {
            this.castAnimation = castAnimationName;
            return this;
        }

        public Builder<T> castCondition(BiPredicate<SpellContext, T> castCondition) {
            this.castPredicate = castCondition;
            return this;
        }

        public Builder<T> additionalCondition(BiPredicate<SpellContext, T> castCondition) {
            this.castPredicate = this.castPredicate.and(castCondition);
            return this;
        }

        public Builder<T> castType(CastType castType) {
            this.castType = castType;
            return this;
        }

        public Builder<T> castSound(SoundEvent castSound) {
            this.castSound = castSound;
            return this;
        }

        public Builder<T> fullRecast(boolean resetDuration) {
            this.fullRecast = true;
            this.resetDuration = resetDuration;
            return this;
        }

        public Builder<T> skipEndOnRecast(Predicate<SpellContext> skipIf) {
            this.skipEndOnRecast = skipIf;
            return this;
        }

        public Builder<T> skipEndOnRecast() {
            this.skipEndOnRecast = context -> true;
            return this;
        }

        public Builder<T> updateInterval(int updateInterval) {
            this.updateInterval = updateInterval;
            return this;
        }

        public Builder<T> isSpecialChoice() {
            this.isSpecialChoice = true;
            return this;
        }

        public Builder<T> hasLayer() {
            this.hasLayer = true;
            return this;
        }
    }
}
