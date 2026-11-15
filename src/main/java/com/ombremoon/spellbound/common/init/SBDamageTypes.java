package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public interface SBDamageTypes {
    ResourceKey<DamageType> SB_GENERIC = register("sb_generic");
    ResourceKey<DamageType> RUIN_FIRE = register("ruin_fire");
    ResourceKey<DamageType> RUIN_FROST = register("ruin_frost");
    ResourceKey<DamageType> RUIN_SHOCK = register("ruin_shock");
    ResourceKey<DamageType> HOLY = register("holy");
    ResourceKey<DamageType> DISEASE = register("disease");
    ResourceKey<DamageType> BLEED = register("bleed");

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, CommonClass.customLocation(name));
    }

    static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(SB_GENERIC, new DamageType("sb_generic", 0.1F));
        context.register(RUIN_FIRE, new DamageType("ruin_fire", 0.1F));
        context.register(RUIN_FROST, new DamageType("ruin_frost", 0.1F));
        context.register(RUIN_SHOCK, new DamageType("ruin_shock", 0.1F));
        context.register(HOLY, new DamageType("holy", 0.1F));
        context.register(DISEASE, new DamageType("disease", 0.1F));
        context.register(BLEED, new DamageType("bleed", 0.1F));
    }
}
