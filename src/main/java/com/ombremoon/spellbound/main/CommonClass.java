package com.ombremoon.spellbound.main;

import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.world.item.MageArmorItem;
import com.ombremoon.spellbound.common.world.sound.SpellboundSounds;
import com.ombremoon.spellbound.mixin.DuckRangedAttribute;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLLoader;

public class CommonClass {

    public static void init(IEventBus modEventBus) {
        SBAffinities.register();
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
        SBEntityDataSerializers.register(modEventBus);
        SBFamiliars.register();
        SBFeatures.register(modEventBus);
        SBItems.register(modEventBus);
        SBLootFunctions.register(modEventBus);
        SBMemoryTypes.register(modEventBus);
        SBMultiblockSerializers.register(modEventBus);
        SBPageElements.register(modEventBus);
        SBParticles.register(modEventBus);
        SBMagicEffects.register(modEventBus);
        SBSensors.register(modEventBus);
        SBSkills.register(modEventBus);
        SBSpells.register(modEventBus);
        SBStats.register(modEventBus);
        SBStructures.register(modEventBus);
        SBTriggers.register(modEventBus);
        SBLootModifiers.register(modEventBus);

        SpellboundSounds.register(modEventBus);

        MageArmorItem.armorAttributeInit();
        fixAttributes();
    }

    public static boolean isDevEnv() {
        return !FMLLoader.isProduction();
    }

    private static void fixAttributes() {
        final Holder<Attribute> armorHolder = Attributes.ARMOR;
        final DuckRangedAttribute newArmor = (DuckRangedAttribute) armorHolder.value();
        newArmor.setMaxValue(50);
    }

    public static ResourceLocation customLocation(String name) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
    }
}
