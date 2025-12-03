package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.world.worldgen.ArcanthusConfig;
import com.ombremoon.spellbound.common.world.worldgen.ArcanthusFeature;
import com.ombremoon.spellbound.common.world.worldgen.HugeWildMushroomFeature;
import com.ombremoon.spellbound.common.world.worldgen.WildMushroomFeatureConfiguration;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SBFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Constants.MOD_ID);

    public static final Supplier<ArcanthusFeature> ARCANTHUS = FEATURES.register("arcanthus_patch", () -> new ArcanthusFeature(ArcanthusConfig.CODEC));
    public static final Supplier<HugeWildMushroomFeature> HUGE_WILD_MUSHROOM = FEATURES.register("huge_wild_mushroom", () -> new HugeWildMushroomFeature(WildMushroomFeatureConfiguration.CODEC));

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
