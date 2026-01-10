package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.datagen.loot.ModBlockLootTables;
import com.ombremoon.spellbound.datagen.loot.ModChestLootTables;
import com.ombremoon.spellbound.datagen.loot.ModEntityLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, Set.of(), List.of(
                new SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(ModEntityLootTables::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(ModChestLootTables::new, LootContextParamSets.CHEST)
        ), lookupProvider);
    }
}
