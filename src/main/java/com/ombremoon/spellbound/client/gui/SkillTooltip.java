package com.ombremoon.spellbound.client.gui;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
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

    public static SkillTooltip<SpellDamage> DAMAGE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(SpellDamage arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.damage", sign(arg.amount), arg.damage.getName()).withStyle(color(arg.amount))));
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

    public static SkillTooltip<Integer> RADIUS = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.radius", sign(arg)).withStyle(color(arg))));
            };
        }
    };

    public static SkillTooltip<Float> MANA_COST = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.mana_cost", sign(arg)).withStyle(invertedColor(arg))));
            };
        }
    };

    public static SkillTooltip<Integer> MANA = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.mana", sign(arg)).withStyle(color(arg))));
            };
        }
    };

    public static SkillTooltip<Float> POTENCY = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.potency", sign(arg)).withStyle(color(arg))));
            };
        }
    };

    public static SkillTooltip<Integer> COOLDOWN = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.cooldown", formatDuration(arg)).withStyle(color(arg))));
            };
        }
    };

    public static SkillTooltip<Integer> EFFECT_DURATION = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.effect_duration", formatDuration(arg)).withStyle(color(arg))));
            };
        }
    };

    public static SkillTooltip<Float> PROC_CHANCE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.proc_chance", sign(arg)).withStyle(color(arg))));
            };
        }
    };

    public static SkillTooltip<Float> MODIFY_DURATION = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.modify_duration", sign(arg)).withStyle(color(arg))));
            };
        }
    };

    public static SkillTooltip<Float> MANA_TO_DAMAGE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.mana_to_damage", sign(arg)).withStyle(color(arg))));
            };
        }
    };

    public static SkillTooltip<Float> TARGET_FIRE_RESIST = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.target_fire_resist", sign(arg)).withStyle(color(arg))));
            };
        }
    };

    public static SkillTooltip<Float> TARGET_ICE_RESIST = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.target_ice_resist", sign(arg)).withStyle(color(arg))));
            };
        }
    };

    public static SkillTooltip<Float> TARGET_SHOCK_RESIST = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.target_shock_resist", sign(arg)).withStyle(color(arg))));
            };
        }
    };

    public static SkillTooltip<Unit> POTENCY_SCALING = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Unit arg, Supplier<DataComponentType<Unit>> component) {
            return (context, tooltipAdder, tooltipFlag) -> {
                tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.potency_scaling").withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)));
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
        return j > 0 ? String.format(Locale.ROOT, "%1d min", j) : String.format(Locale.ROOT, "%1d s", i);
    }

    protected static String sign(int arg) {
        String sign = arg >= 0 ? "+" : "";
        return sign + arg;
    }

    protected static String sign(float arg) {
        String sign = arg >= 0.0F ? "+" : "";
        return sign + arg;
    }

    protected static ChatFormatting color(int arg) {
        return arg >= 0.0 ? ChatFormatting.BLUE : ChatFormatting.RED;
    }

    protected static ChatFormatting color(float arg) {
        return arg >= 0.0 ? ChatFormatting.BLUE : ChatFormatting.RED;
    }

    protected static ChatFormatting invertedColor(int arg) {
        return arg >= 0.0 ? ChatFormatting.RED : ChatFormatting.BLUE;
    }

    protected static ChatFormatting invertedColor(float arg) {
        return arg >= 0.0 ? ChatFormatting.RED : ChatFormatting.BLUE;
    }

    public abstract SkillTooltipProvider tooltip(T arg, Supplier<DataComponentType<Unit>> component);

    public SkillTooltipProvider tooltip(T arg) {
        return this.tooltip(arg, SBData.DETAILS);
    }

    public SkillTooltipProvider tooltip() {
        return this.tooltip(null, SBData.DETAILS);
    }

    public record SpellDamage(DamageTranslation damage, int amount) {}

}
