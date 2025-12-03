package com.ombremoon.spellbound.common.world.multiblock;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class MultiblockManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = Constants.LOG;
    private final HolderLookup.Provider registries;
    private static Map<ResourceLocation, MultiblockHolder<?>> MULTIBLOCKS = ImmutableMap.of();

    private static MultiblockManager instance;

    public static MultiblockManager getInstance(Level level) {
        if (instance == null) {
            instance = new MultiblockManager(level.registryAccess());
        }
        return instance;
    }

    public MultiblockManager(HolderLookup.Provider registries) {
        super(GSON, Registries.elementsDirPath(Keys.MULTIBLOCKS));
        this.registries = registries;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, MultiblockHolder<?>> builder = ImmutableMap.builder();

        for (var entry : object.entrySet()) {
            ResourceLocation location = entry.getKey();
            try {
                Multiblock multiblock = Multiblock.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow(JsonParseException::new);
                MultiblockHolder<?> holder = new MultiblockHolder<>(location, multiblock);
                builder.put(location, holder);
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                LOGGER.error("Parsing error loading multiblock {}", location, jsonParseException);
            }
        }

        MULTIBLOCKS = builder.build();
        LOGGER.info("Loaded {} multiblocks", MULTIBLOCKS.size());
    }

    public static MultiblockHolder<?> byKey(ResourceLocation id) {
        return MULTIBLOCKS.get(id);
    }


    public static List<MultiblockHolder<?>> getMultiblocks() {
        return MULTIBLOCKS.values().stream().toList();
    }

    public void updateMultiblocks(Iterable<MultiblockHolder<?>> multiblocks) {
        ImmutableMap.Builder<ResourceLocation, MultiblockHolder<?>> builder = ImmutableMap.builder();

        for (MultiblockHolder<?> multiblockHolder : multiblocks) {
            builder.put(multiblockHolder.id(), multiblockHolder);
        }

        MULTIBLOCKS = builder.build();
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        MultiblockManager manager = new MultiblockManager(event.getRegistryAccess());
        event.addListener(manager);
    }
}
