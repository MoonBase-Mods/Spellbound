package com.ombremoon.spellbound.main;

import com.ombremoon.spellbound.common.init.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLLoader;

public class CommonClass {

    public static void init(IEventBus modEventBus) {
        SBArmorMaterials.register(modEventBus);
        SBAttributes.register(modEventBus);
        SBBlockEntities.register(modEventBus);
        SBBlocks.register(modEventBus);
        SBBossFights.register(modEventBus);
        SBChunkGenerators.register(modEventBus);
        SBData.register(modEventBus);
        SBDataTypes.register(modEventBus);
        SBEffects.register(modEventBus);
        SBEntities.register(modEventBus);
        SBFeatures.register(modEventBus);
        SBItems.register(modEventBus);
        SBLootFunctions.register(modEventBus);
        SBMemoryTypes.register(modEventBus);
        SBMultiblockSerializers.register(modEventBus);
        SBPageElements.register(modEventBus);
        SBParticles.register(modEventBus);
        SBRitualEffects.register(modEventBus);
        SBSensors.register(modEventBus);
        SBSkills.register(modEventBus);
        SBSpells.register(modEventBus);
        SBStats.register(modEventBus);
        SBTriggers.register(modEventBus);
//        fixAttributes();
    }

    public static boolean isDevEnv() {
        return !FMLLoader.isProduction();
    }

    private static void fixAttributes() {
    }

    public static ResourceLocation customLocation(String name) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
    }
}
