package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SBStats {
    public static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create(Registries.CUSTOM_STAT, Constants.MOD_ID);

    public static final Supplier<ResourceLocation> SPELLS_LEARNED = registerStat("spells_learned");
    public static final Supplier<ResourceLocation> SPELLS_CAST = registerStat("spells_cast");
    public static final Supplier<ResourceLocation> WEAPONS_IMBUED = registerStat("weapons_imbued");
    public static final Supplier<ResourceLocation> INTERACT_WITH_BENCH = registerStat("interact_with_bench");
    public static final Supplier<ResourceLocation> INTERACT_WITH_DARK_ALTAR = registerStat("interact_with_dark_altar");

    public static Supplier<ResourceLocation> registerStat(String name) {
        return STATS.register(name, () -> CommonClass.customLocation(name));
    }

    public static void register(IEventBus modEventBus) {
        STATS.register(modEventBus);
    }
}

