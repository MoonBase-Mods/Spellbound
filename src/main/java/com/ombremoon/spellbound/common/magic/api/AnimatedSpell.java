package com.ombremoon.spellbound.common.magic.api;

import com.ombremoon.spellbound.common.events.EventFactory;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * The main class most spells will extend from. Primary utility is to handle spells casting animations.
 */
@SuppressWarnings("unchecked")
public abstract class AnimatedSpell extends AbstractSpell {
    private final BiFunction<SpellContext, AnimatedSpell, SBAnimation> castAnimation;

    public static <T extends AnimatedSpell> Builder<T> createSimpleSpellBuilder(Class<T> spellClass) {
        return new Builder<>();
    }

    public AnimatedSpell(SpellType<?> spellType, Builder<?> builder) {
        super(spellType, EventFactory.getAnimatedBuilder(spellType, builder));
        this.castAnimation = (BiFunction<SpellContext, AnimatedSpell, SBAnimation>) builder.castAnimation;
    }

    @Override
    public void onCastStart(SpellContext context) {
        super.onCastStart(context);
        LivingEntity caster = context.getCaster();
        SBAnimation animation = this.castAnimation.apply(context, this);
        if (!this.isSoftCast && animation != null && caster instanceof Player player) {
            this.playAnimation(player, animation);
        }
    }
    /**
     * Plays an animation for the player. This is called server-side for all players to see the animation
     * @param player The player performing the animation
     * @param animation The animation information
     */
    protected void playAnimation(Player player, SBAnimation animation) {
        var handler = SpellUtil.getSpellHandler(player);
        handler.playAnimation(player, animation, SpellUtil.getCastSpeed(player));
    }

    protected void stopAnimation(Player player, SBAnimation animation) {
        var handler = SpellUtil.getSpellHandler(player);
        handler.stopAnimation(player, animation);
    }

    protected void playMovementAnimation(Player player, ResourceLocation movementAnimation, @Nullable SBAnimation fallbackAnimation) {
        var handler = SpellUtil.getSpellHandler(player);
        SBAnimation animation = new SBAnimation(movementAnimation, SBAnimation.Type.CAST, false, false);
        if (handler.isMoving() && !handler.movementDirty) {
            handler.movementDirty = true;
            playAnimation(player, animation);
        } else if (!handler.isMoving() && handler.movementDirty) {
            handler.movementDirty = false;
            if (fallbackAnimation != null) {
                playAnimation(player, fallbackAnimation);
            } else {
                stopAnimation(player, animation);
            }
        }
    }

    public static class Builder<T extends AnimatedSpell> extends AbstractSpell.Builder<T> {
        protected BiFunction<SpellContext, T, SBAnimation> castAnimation = (context, spell) -> new SBAnimation("simple_cast", SBAnimation.Type.CAST, true);

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

        public Builder<T> castAnimation(BiFunction<SpellContext, T, SBAnimation> castAnimationName) {
            this.castAnimation = castAnimationName;
            return this;
        }

        public Builder<T> instantCast() {
            this.castAnimation = (context, spell) -> new SBAnimation("instant_cast", SBAnimation.Type.CAST, true);
            this.castTime = 5;
            return this;
        }

        public Builder<T> summonCast() {
            this.castAnimation = (context, spell) -> new SBAnimation("summon", SBAnimation.Type.CAST, true);
            this.castTime = 30;
            return this;
        }

        public Builder<T> selfBuffCast() {
            this.castAnimation = (context, spell) -> new SBAnimation("self_buff", SBAnimation.Type.CAST, true);
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

        public Builder<T> skipEndOnRecast(BiPredicate<SpellContext, T> skipIf) {
            this.skipEndOnRecast = skipIf;
            return this;
        }

        public Builder<T> skipEndOnRecast() {
            this.skipEndOnRecast = (context, spell) -> true;
            return this;
        }

        public Builder<T> updateInterval(int updateInterval) {
            this.updateInterval = updateInterval;
            return this;
        }
        public Builder<T> spellLayer(BiFunction<SpellContext, T, ResourceLocation> spellLayer) {
            this.spellLayer = spellLayer;
            return this;
        }

        public Builder<T> negativeScaling(BiPredicate<SpellContext, T> negativeScaling) {
            this.negativeScaling = negativeScaling;
            return this;
        }

        public Builder<T> negativeScaling() {
            this.negativeScaling = (context, spell) -> true;
            return this;
        }
    }
}
