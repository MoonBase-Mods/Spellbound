package com.ombremoon.spellbound.common.magic.api;

import com.ombremoon.spellbound.common.events.EventFactory;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The main class most spells will extend from. Primary utility is to handle spells casting animations.
 */
public abstract class AnimatedSpell extends AbstractSpell {
    private final Function<SpellContext, String> castAnimation;
    private final Function<SpellContext, String> failAnimation;

    public static <T extends AnimatedSpell> Builder<T> createSimpleSpellBuilder(Class<T> spellClass) {
        return new Builder<>();
    }

    public AnimatedSpell(SpellType<?> spellType, Builder<?> builder) {
        super(spellType, EventFactory.getAnimatedBuilder(spellType, builder));
        this.castAnimation = builder.castAnimation;
        this.failAnimation = builder.failAnimation;
    }

    @Override
    public void onCastStart(SpellContext context) {
        super.onCastStart(context);
        Level level = context.getLevel();
        LivingEntity caster = context.getCaster();
        String animation = this.castAnimation.apply(context);
        if (!level.isClientSide) {
            if (!animation.isEmpty() && caster instanceof Player player)
                this.playAnimation(player, animation);
        }
    }

    @Override
    public void onCastReset(SpellContext context) {
        super.onCastReset(context);
        Level level = context.getLevel();
        var handler = context.getSpellHandler();
//        context.getSpellHandler().setStationaryTicks(this.getCastTime());
//        String animation = this.failAnimation.apply(context);
//        if (!context.getLevel().isClientSide && !animation.isEmpty() && context.getCaster() instanceof Player player)
//            playAnimation(player, animation);
    }

    public static class Builder<T extends AnimatedSpell> extends AbstractSpell.Builder<T> {
        protected Function<SpellContext, String> castAnimation = context -> "simple_cast";
        protected Function<SpellContext, String> failAnimation = context -> "spell_fail";

        public Builder<T> mastery(SpellMastery mastery) {
            this.spellMastery = mastery;
            return this;
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

        public Builder<T> castTime(int castTime, int stationaryTicks) {
            this.castTime = castTime;
            this.stationaryTicks = stationaryTicks;
            return this;
        }

        public Builder<T> castTime(int castTime) {
            this.castTime = castTime;
            this.stationaryTicks = castTime;
            return this;
        }

        public Builder<T> castAnimation(Function<SpellContext, String> castAnimationName) {
            this.castAnimation = castAnimationName;
            return this;
        }

        public Builder<T> failAnimation(Function<SpellContext, String> failAnimationName) {
            this.failAnimation = failAnimationName;
            return this;
        }

        public Builder<T> instantCast() {
            this.castAnimation = context -> "instant_cast";
            this.castTime = 5;
            this.stationaryTicks = 11;
            return this;
        }

        public Builder<T> summonCast() {
            this.castAnimation = context -> "summon";
            this.castTime = 30;
            this.stationaryTicks = 62;
            return this;
        }

        public Builder<T> selfBuffCast() {
            this.castAnimation = context -> "self_buff";
            this.stationaryTicks = 43;
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

        public Builder<T> fullRecast() {
            this.fullRecast = true;
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
        public Builder<T> hasLayer() {
            this.hasLayer = true;
            return this;
        }

        public Builder<T> negativeScaling(Predicate<SpellContext> negativeScaling) {
            this.negativeScaling = negativeScaling;
            return this;
        }

        public Builder<T> negativeScaling() {
            this.negativeScaling = context -> true;
            return this;
        }
    }
}
