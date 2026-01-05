package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.datagen.provider.DatapackRegistriesProvider;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class ModDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        boolean includeServer = event.includeServer();
        boolean includeClient = event.includeClient();
        event.createDatapackRegistryObjects(DatapackRegistriesProvider.BUILDER);
        generator.addProvider(includeServer, new ModRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(includeServer, new ModLootTableProvider(packOutput, lookupProvider));
        generator.addProvider(includeServer, new ModSoundProvider(packOutput, existingFileHelper));
        generator.addProvider(includeServer, new ModTagProvider.Blocks(packOutput, event.getLookupProvider(), existingFileHelper));
        generator.addProvider(includeServer, new ModTagProvider.Items(packOutput, event.getLookupProvider(), existingFileHelper));
        generator.addProvider(includeServer, new ModTagProvider.EntityTypes(packOutput, event.getLookupProvider(), existingFileHelper));
        generator.addProvider(includeServer, new ModTagProvider.DamageTypes(packOutput, event.getLookupProvider(), existingFileHelper));
        generator.addProvider(includeServer, new ModGlobalLootModifiersProvider(packOutput, lookupProvider));
        generator.addProvider(includeServer, new ModPageScrapProvider(packOutput, lookupProvider));
        generator.addProvider(includeServer, new ModMultiblockProvider(packOutput, lookupProvider));
        generator.addProvider(includeClient, new ModItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(includeClient, new ModBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(includeClient, new ModLangProvider(packOutput));
    }
}
