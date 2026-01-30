package com.ombremoon.spellbound.client.gui;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.api.buff.ModifierData;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.world.DamageTranslation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class SkillTooltip<T> {

    public static SkillTooltip<SpellDamage> DAMAGE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(SpellDamage arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.damage", damageSign(arg.amount), arg.damage.getName()).withStyle(color(arg.amount))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Integer> DURATION = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.duration", formatDuration(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> RADIUS = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.radius", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> RANGE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.range", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> MANA_COST = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.mana_cost", sign(arg)).withStyle(invertedColor(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Integer> MANA_TICK_COST = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.mana_tick_cost", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Integer> MANA = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.mana", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> POTENCY = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.potency", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Integer> COOLDOWN = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.cooldown", formatDuration(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Integer> EFFECT_DURATION = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.effect_duration", formatDuration(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> PROC_CHANCE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.proc_chance", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Integer> PROC_DURATION = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.proc_duration", formatDuration(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Integer> CHARGE_DURATION = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.charge_duration", formatDuration(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Integer> MAX_CHARGES = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Integer arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.max_charges", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<SpellDamage> MODIFY_DAMAGE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(SpellDamage arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.modify_damage", sign(arg.amount), arg.damage.getName()).withStyle(color(arg.amount))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> MODIFY_DURATION = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.modify_duration", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> MANA_TO_DAMAGE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.mana_to_damage", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<SpellDamage> DAMAGE_PER_CHARGE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(SpellDamage arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.damage_per_charge", sign(arg.amount), arg.damage.getName()).withStyle(color(arg.amount))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> RANGE_PER_CHARGE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.range_per_charge", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> RADIUS_PER_CHARGE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.radius_per_charge", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> KNOCKBACK_PER_CHARGE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.knockback_per_charge", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> ALLY_RANGE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.ally_range", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };


    public static SkillTooltip<Float> TARGET_FIRE_RESIST = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.target_fire_resist", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> TARGET_ICE_RESIST = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.target_ice_resist", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> TARGET_SHOCK_RESIST = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.target_shock_resist", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> KNOCKBACK = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.knockback", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Float> EXPLOSION_RADIUS = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.explosion_radius", sign(arg)).withStyle(color(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<ModifierData> ATTRIBUTE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(ModifierData arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(arg.attribute().value().toComponent(arg.attributeModifier(), tooltipFlag)).withStyle(color((float) arg.attributeModifier().amount())));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Unit> POTENCY_SCALING = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Unit arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.potency_scaling").withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
            };
        }
    };

    public static SkillTooltip<Unit> CHOICE = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Unit arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }

                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.NEW_LINE);
                    tooltipAdder.accept(Component.translatable("spellbound.skill_tooltip.choice").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                }
            };
        }
    };

    public static SkillTooltip<ChoiceTooltip> CHOICE_CONDITION = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(ChoiceTooltip arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }

                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    Skill skill = arg.skill.value();
                    int color = skill.getSpell().getIdentifiablePath().getColor();
                    tooltipAdder.accept(CommonComponents.NEW_LINE);
                    tooltipAdder.accept(Component.translatable("spellbound.skill_tooltip.choice_condition").withStyle(ChatFormatting.GRAY).append(skill.getName().append(":").withColor(color).withStyle(ChatFormatting.ITALIC)));
                    arg.tooltip.addToTooltip(Item.TooltipContext.EMPTY, tooltipAdder, TooltipFlag.NORMAL);
                }
            };
        }
    };

    public static SkillTooltip<Float> WATER_SLOWDOWN = new SkillTooltip<>() {
        @Override
        public SkillTooltipProvider tooltip(Float arg, Supplier<DataComponentType<Unit>> component) {
            return new SkillTooltipProvider() {
                @Override
                public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
                    tooltipAdder.accept(CommonComponents.space().append(Component.translatable("spellbound.skill_tooltip.dolphins_fin", sign(arg)).withStyle(invertedColor(arg))));
                }

                @Override
                public DataComponentType<?> component() {
                    return component.get();
                }
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

    protected static String damageSign(float arg) {
        String sign = arg >= 0.0F ? "+" : "";
        return sign +  String.format("%.1f", arg);
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

    public record SpellDamage(DamageTranslation damage, float amount) {}

    public record ChoiceTooltip(Holder<Skill> skill, SkillTooltipProvider tooltip) {}

}
