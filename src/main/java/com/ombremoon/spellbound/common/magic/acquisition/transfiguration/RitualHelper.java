package com.ombremoon.spellbound.common.magic.acquisition.transfiguration;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.ombremoon.spellbound.common.world.multiblock.MultiblockHolder;
import com.ombremoon.spellbound.common.world.multiblock.MultiblockManager;
import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class RitualHelper extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = Constants.LOG;
    private final HolderLookup.Provider registries;
    private static Map<ResourceLocation, TransfigurationRitual> RITUALS = ImmutableMap.of();
    private static Multimap<Integer, TransfigurationRitual> byTier = ImmutableMultimap.of();

    private static RitualHelper instance;

    public static RitualHelper getInstance(Level level) {
        if (instance == null) {
            instance = new RitualHelper(level.registryAccess());
        }
        return instance;
    }

    public RitualHelper(HolderLookup.Provider registries) {
        super(GSON, Registries.elementsDirPath(Keys.RITUAL));
        this.registries = registries;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, TransfigurationRitual> builder = ImmutableMap.builder();
        ImmutableMultimap.Builder<Integer, TransfigurationRitual> builder1 = ImmutableMultimap.builder();

        for (var entry : object.entrySet()) {
            ResourceLocation location = entry.getKey();
            try {
                TransfigurationRitual ritual = TransfigurationRitual.DIRECT_CODEC.parse(this.registries.createSerializationContext(JsonOps.INSTANCE), entry.getValue()).getOrThrow(JsonParseException::new);
                builder.put(location, ritual);
                builder1.put(ritual.definition().tier(), ritual);
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                LOGGER.error("Parsing error loading multiblock {}", location, jsonParseException);
            }
        }

        RITUALS = builder.build();
        byTier = builder1.build();
        LOGGER.info("Loaded {} multiblocks", RITUALS.size());
    }

    public static Optional<TransfigurationRitual> getRitualFor(TransfigurationMultiblock multiblock, List<ItemStack> items) {
        return RITUALS.values()
                .stream()
                .filter(ritual -> ritual.matches(multiblock, items))
                .findFirst();
    }

    public static Optional<TransfigurationRitual> getRitualFor(TransfigurationMultiblock multiblock, List<ItemStack> items, int tier) {
        return byTier(tier)
                .stream()
                .filter(ritual -> ritual.matches(multiblock, items))
                .findFirst();
    }

    public static TransfigurationRitual getRitualFor(ResourceKey<TransfigurationRitual> ritual) {
        return RITUALS.get(ritual.location());
    }

    private static Collection<TransfigurationRitual> byTier(int tier) {
        return byTier.get(tier);
    }

    public static void createItem(Level level, BlockPos pos, ItemStack item) {
        createItem(level, pos.getCenter(), item);
    }

    public static void createItem(Level level, Vec3 pos, ItemStack item) {
        createItem(level, pos, item, Optional.empty());
    }

    public static <T> void createItem(Level level, BlockPos pos, ItemStack item, Optional<DataComponentStorage> storage) {
        createItem(level, pos.getBottomCenter(), item, storage);
    }

    public static <T> void createItem(Level level, Vec3 pos, ItemStack item, Optional<DataComponentStorage> storage) {
        if (!level.isClientSide) {
            storage.ifPresent(dataStorage -> {
                for (var typeComponent : dataStorage.dataComponents()) {
                    item.set((DataComponentType<T>) typeComponent.type(), (T) typeComponent.value());
                }
            });

            ItemEntity entity = new ItemEntity(level, pos.x(), pos.y() + 1.5F, pos.z(), item);
            entity.setDeltaMovement(Vec3.ZERO);
            level.addFreshEntity(entity);
        }
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        RitualHelper helper = new RitualHelper(event.getRegistryAccess());
        event.addListener(helper);
    }
}
