package com.ombremoon.spellbound.client.gui;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;

import java.util.Locale;
import java.util.function.Supplier;

public abstract class SkillTooltip<T> {

    public static SkillTooltip<SpellDamage> DAMAGE_OVER_TIME = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(SpellDamage arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.damage", Integer.toString(arg.amount), arg.damage.getName()).withStyle(color(arg.amount))));
            };
        }
    };

    public static SkillTooltip<Integer> DURATION = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.duration", formatDuration(arg)).withStyle(color(arg))));
            };
        }
    };

    public static SkillTooltip<Float> MODIFY_DURATION = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(Component.translatable("spellbound.skill_tooltip.modify_duration", Float.toString(arg)).withStyle(color(arg)));
            };
        }
    };

    public static SkillTooltip<Float> TARGET_FIRE_RESIST = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(Component.translatable("spellbound.skill_tooltip.fire_resist", sign(arg), Float.toString(arg)).withStyle(color(arg)));
            };
        }
    };

    public static SkillTooltip<Float> TARGET_ICE_RESIST = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(Component.translatable("spellbound.skill_tooltip.ice_resist", sign(arg), Float.toString(arg)).withStyle(color(arg)));
            };
        }
    };

    public static SkillTooltip<Float> TARGET_SHOCK_RESIST = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(Component.translatable("spellbound.skill_tooltip.shock_resist", sign(arg), Float.toString(arg)).withStyle(color(arg)));
            };
        }
    };

    protected static Component formatDuration(int duration) {
        return Component.literal(formatTickDuration(duration));
    }

    public static String formatTickDuration(int ticks) {
        int i = Mth.floor((float)ticks / 20);
        int j = i / 60;
        i %= 60;
        j %= 60;
        return j > 0 ? String.format(Locale.ROOT, "%1d minutes", j) : String.format(Locale.ROOT, "%1d seconds", i);
    }

    protected static String sign(int arg) {
        return arg >= 0 ? "+" : "";
    }

    protected static String sign(float arg) {
        return arg >= 0.0F ? "+" : "";
    }

    protected static ChatFormatting color(float arg) {
        return arg >= 0.0 ? ChatFormatting.GREEN : ChatFormatting.RED;
    }

    public abstract SkillTooltipProvider tooltip(T arg, Supplier<DataComponentType<Unit>> component);

    public SkillTooltipProvider tooltip(T arg) {
        return this.tooltip(arg, SBData.DETAILS);
    }

    public record SpellDamage(DamageTranslation damage, int amount) {}

}
