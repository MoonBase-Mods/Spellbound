package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.world.multiblock.MultiblockSerializer;
import com.ombremoon.spellbound.common.world.multiblock.type.StandardMultiblock;
import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class SBMultiblockSerializers {
    public static final ResourceKey<Registry<MultiblockSerializer<?>>> MULTIBLOCK_SERIALIZER_REGISTRY_KEY = ResourceKey.createRegistryKey(CommonClass.customLocation("multiblock_serializer"));
    public static final Registry<MultiblockSerializer<?>> REGISTRY = new RegistryBuilder<>(MULTIBLOCK_SERIALIZER_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<MultiblockSerializer<?>> MULTIBLOCK_SERIALIZERS = DeferredRegister.create(REGISTRY, Constants.MOD_ID);

    public static final Supplier<MultiblockSerializer<StandardMultiblock>> STANDARD_MULTIBLOCK = MULTIBLOCK_SERIALIZERS.register("standard_multiblock", StandardMultiblock.Serializer::new);
    public static final Supplier<MultiblockSerializer<TransfigurationMultiblock>> TRANSFIGURATION_MULTIBLOCK = MULTIBLOCK_SERIALIZERS.register("transfiguration_multiblock", TransfigurationMultiblock.Serializer::new);

    public static void register(IEventBus modEventBus) {
        MULTIBLOCK_SERIALIZERS.register(modEventBus);
    }
}
