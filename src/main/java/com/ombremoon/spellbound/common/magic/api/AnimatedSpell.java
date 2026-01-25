package com.ombremoon.spellbound.common.magic.api;

import com.ombremoon.spellbound.common.events.EventFactory;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The main class most spells will extend from. Primary utility is to handle spells casting animations.
 */
public abstract class AnimatedSpell extends AbstractSpell {
    private final Function<SpellContext, SpellAnimation> castAnimation;

    public static <T extends AnimatedSpell> Builder<T> createSimpleSpellBuilder(Class<T> spellClass) {
        return new Builder<>();
    }

    public AnimatedSpell(SpellType<?> spellType, Builder<?> builder) {
        super(spellType, EventFactory.getAnimatedBuilder(spellType, builder));
        this.castAnimation = builder.castAnimation;
    }

    @Override
    public void onCastStart(SpellContext context) {
        super.onCastStart(context);
        Level level = context.getLevel();
        LivingEntity caster = context.getCaster();
        String animation = this.castAnimation.apply(context).animation();
        if (!level.isClientSide && !animation.isEmpty() && caster instanceof Player player) {
            this.playAnimation(player, animation);
        }
    }

    /**
     * Plays an animation for the player. This is called server-side for all players to see the animation
     * @param player The player performing the animation
     * @param animationName The animation path location
     */
    protected void playAnimation(Player player, ResourceLocation animationName) {
        if (!player.level().isClientSide) {
            var handler = SpellUtil.getSpellHandler(player);
            handler.playAnimation(player, animationName, SpellUtil.getCastSpeed(player));
        }
    }

    protected void playAnimation(Player player, String animationName) {
        this.playAnimation(player, CommonClass.customLocation(animationName));
    }

    protected void stopAnimation(Player player, ResourceLocation animationName) {
        if (!player.level().isClientSide)
            PayloadHandler.handleAnimation(player, animationName, 0.0F, true);
    }

    protected void stopAnimation(Player player, String animationName) {
        this.stopAnimation(player, CommonClass.customLocation(animationName));
    }

    protected void playMovementAnimation(Player player, String animationName, @Nullable String fallbackAnimation) {
        var caster = SpellUtil.getSpellHandler(player);
        if (caster.isMoving() && !caster.movementDirty) {
            caster.movementDirty = true;
            playAnimation(player, animationName);
        } else if (!caster.isMoving() && caster.movementDirty) {
            caster.movementDirty = false;
            if (fallbackAnimation != null) {
                playAnimation(player, fallbackAnimation);
            } else {
                stopAnimation(player, animationName);
            }
        }
    }

    @Override
    public boolean isStationaryCast(SpellContext context) {
        return this.castAnimation.apply(context).stationary();
    }

    public SpellAnimation getCastAnimation(SpellContext context) {
        return this.castAnimation.apply(context);
    }

    public static class Builder<T extends AnimatedSpell> extends AbstractSpell.Builder<T> {
        protected Function<SpellContext, SpellAnimation> castAnimation = context -> new SpellAnimation("simple_cast", SpellAnimation.Type.CAST, true);

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

        public Builder<T> castAnimation(Function<SpellContext, SpellAnimation> castAnimationName) {
            this.castAnimation = castAnimationName;
            return this;
        }

        public Builder<T> instantCast() {
            this.castAnimation = context -> new SpellAnimation("instant_cast", SpellAnimation.Type.CAST, true);
            this.castTime = 5;
            return this;
        }

        public Builder<T> summonCast() {
            this.castAnimation = context -> new SpellAnimation("summon", SpellAnimation.Type.CAST, true);
            this.castTime = 30;
            return this;
        }

        public Builder<T> selfBuffCast() {
            this.castAnimation = context -> new SpellAnimation("self_buff", SpellAnimation.Type.CAST, true);
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
