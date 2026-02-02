package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SBAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister
            .create(Registries.ATTRIBUTE, Constants.MOD_ID);

    //Casting
    public static Holder<Attribute> CAST_RANGE = register("cast_range", 10.0, 0.0, 100.0);
    public static Holder<Attribute> CAST_SPEED = register("cast_speed", 1.0, 0.0, 2.0);

    //Mana
    public static Holder<Attribute> MANA_REGEN = register("mana_regen", 1d, 0d, 100d);
    public static Holder<Attribute> MAX_MANA = register("max_mana", 100d, 100d, 5000d);

    //Resistances
    public static Holder<Attribute> MAGIC_RESIST = registerResistance("magic_resistance");
    public static Holder<Attribute> FIRE_SPELL_RESIST = registerResistance("fire_spell_resistance");
    public static Holder<Attribute> FROST_SPELL_RESIST = registerResistance("frost_spell_resistance");
    public static Holder<Attribute> SHOCK_SPELL_RESIST = registerResistance("shock_spell_resistance");
    public static Holder<Attribute> DISEASE_RESIST = registerResistance("disease_resistance");

    public static Holder<Attribute> registerResistance(String name) {
        return register(name, 0.0, -1.0, 1.0);
    }

    public static Holder<Attribute> register(String name, double defaultVal, double min, double max) {
        return ATTRIBUTES.register(name, () -> new RangedAttribute("attribute.name.spellbound." + name,
                defaultVal, min, max).setSyncable(true));
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
