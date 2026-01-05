package com.ombremoon.spellbound.datagen.provider;

import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatapackRegistriesProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Keys.RITUAL, SBRituals::bootstrap)
            .add(Keys.DIVINE_ACTION, SBDivineActions::bootstrap)
            .add(Keys.GUIDE_BOOK, SBGuidePages::bootstrap)
            .add(Registries.PLACED_FEATURE, SBPlacedFeatures::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, SBConfiguredFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, SBBiomeModifiers::bootstrap)
            .add(Registries.DAMAGE_TYPE, SBDamageTypes::bootstrap);

    public DatapackRegistriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Constants.MOD_ID));
    }
}
