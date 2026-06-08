package com.ombremoon.spellbound.common.magic.api;

import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public abstract class ImbuementSpell extends AnimatedSpell implements RadialSpell {
    private final BiFunction<SpellContext, ImbuementSpell, EffectData> effect;
    protected ItemStack stack;
    protected Imbuement imbuement;
    private int imbuedSlot;
    private boolean effectTriggered;

    public static <T extends ImbuementSpell> Builder<T> createImbuementSpellBuilder(Class<T> spellClass) {
        return (Builder<T>) new Builder<>()
                .fullRecast(true)
                .skipEndOnRecast()
                .castCondition((context, imbuementSpell) -> {
                    LivingEntity caster = context.getCaster();
                    ItemStack itemStack = context.getMainHandItem();
                    var handler = context.getSpellHandler();
                    Imbuement imbuement = imbuementSpell.createImbuement(context);
                    if (itemStack.isEmpty()) {
                        return false;
                    } else if (!imbuement.canImbueStack(itemStack)) {
                        return false;
                    } else if (context.isRecast() && !imbuementSpell.isMainChoice(context)) {
                        ImbuementSpell spell = (ImbuementSpell) handler.getSpell(imbuementSpell.spellType());
                        if (spell != null)
                            spell.onUseImbuement(context);

                        return false;
                    } else if (imbuementSpell.isMainChoice(context)) {
                        imbuementSpell.stack = itemStack;
                        imbuementSpell.imbuement = imbuement;
                        if (caster instanceof Player player) {
                            imbuementSpell.imbuedSlot = player.getInventory().findSlotMatchingItem(itemStack);
                        }

                        return true;
                    }

                    return false;
                });
    }

    public ImbuementSpell(SpellType<?> spellType, Builder<?> builder) {
        super(spellType, builder);
        this.effect = (BiFunction<SpellContext, ImbuementSpell, EffectData>) builder.effect;
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide && this.isMainChoice(context)) {
            this.stack.set(SBData.IMBUEMENT, this.imbuement);
            var handler = context.getSpellHandler();
            handler.getListener().addListener(
                    SpellEventListener.Events.USE_ITEM,
                    this.location(),
                    useItemEvent -> this.onUseImbuement(context)
            );
        }
    }

    @Override
    protected void onSpellTick(SpellContext context) {
        super.onSpellTick(context);
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        EffectData effect = this.getImbuementEffect(context);
        if (!level.isClientSide && effect != null) {
            this.displayImbuementEffect(caster, effect);
        }
    }

    protected void displayImbuementEffect(LivingEntity caster, EffectData effect) {
        ItemStack stack = caster.getMainHandItem();
        if (ItemStack.isSameItemSameComponents(stack, this.stack) && !this.effectTriggered) {
            this.triggerSpellFX(effect);
            this.effectTriggered = true;
        } else if (!ItemStack.isSameItemSameComponents(stack, this.stack) && this.effectTriggered) {
            this.removeSpellFX(effect.getLocation());
            this.effectTriggered = false;
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            EffectData effect = this.getImbuementEffect(context);
            if (effect != null) {
                this.removeSpellFX(effect.getLocation());
            }

//            this.stack.set(SBData.IMBUEMENT, null);
            if (caster instanceof Player player) {
                ItemStack stack = player.getInventory().getItem(this.imbuedSlot);
                stack.set(SBData.IMBUEMENT, null);
            } else {
                caster.getMainHandItem().set(SBData.IMBUEMENT, null);
            }

            var handler = context.getSpellHandler();
            handler.getListener().removeListener(SpellEventListener.Events.USE_ITEM, this.location());
        }
    }

    protected abstract void onUseImbuement(SpellContext context);

    protected Imbuement createImbuement(SpellContext context) {
        return new Imbuement(this.spellType(), -1, this.location());
    }

    protected EffectData getImbuementEffect(SpellContext context) {
        return this.effect.apply(context, this);
    }

    public static class Builder<T extends ImbuementSpell> extends AnimatedSpell.Builder<T> {
        protected BiFunction<SpellContext, T, EffectData> effect = (context, spell) -> null;

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

        public Builder<T> selfBuffCast() {
            this.castAnimation = (context, spell) -> new SpellAnimation("self_buff", SpellAnimation.Type.CAST, true);
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
        public Builder<T> hasLayer() {
            this.hasLayer = true;
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

        public Builder<T> imbuementEffect(BiFunction<SpellContext, T, EffectData> data) {
            this.effect = data;
            return this;
        }
    }
}
