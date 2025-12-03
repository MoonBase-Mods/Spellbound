package com.ombremoon.spellbound.common.world.dimension;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.serialization.Lifecycle;
import com.ombremoon.spellbound.common.events.custom.UnregisterDimensionEvent;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.networking.PayloadHandler;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * @author Commoble, used with permission.
 * https://gist.github.com/Commoble/7db2ef25f94952a4d2e2b7e3d4be53e0
 */
public final class DimensionManager implements DimensionCreator {
    private static final RegistrationInfo DIMENSION_REGISTRATION_INFO = new RegistrationInfo(Optional.empty(), Lifecycle.stable());

    private DimensionManager() {
    }

    public static final DimensionManager INSTANCE = new DimensionManager();

    private static final Logger LOGGER = Constants.LOG;
    private static final Set<ResourceKey<Level>> VANILLA_LEVELS = Set.of(Level.OVERWORLD, Level.NETHER, Level.END);

    private Set<ResourceKey<Level>> levelsPendingUnregistration = new HashSet<>();

    public ServerLevel getOrCreateLevel(final MinecraftServer server, final ResourceKey<Level> levelKey, final Supplier<LevelStem> dimensionFactory) {
        @SuppressWarnings("deprecation")
        Map<ResourceKey<Level>, ServerLevel> map = server.forgeGetWorldMap();
        @Nullable ServerLevel existingLevel = map.get(levelKey);

        return existingLevel == null
                ? createAndRegisterLevel(server, map, levelKey, dimensionFactory)
                : existingLevel;
    }

    public void markDimensionForUnregistration(final MinecraftServer server, final ResourceKey<Level> levelToRemove) {
        if (!VANILLA_LEVELS.contains(levelToRemove)) {
            ServerLevel level = server.getLevel(levelToRemove);
            if (level != null) {
                level.save(null, true, false);
                levelsPendingUnregistration.add(levelToRemove);
            }
        }
    }

    public Set<ResourceKey<Level>> getLevelsPendingUnregistration() {
        return ImmutableSet.copyOf(levelsPendingUnregistration);
    }

    @SuppressWarnings("deprecation")
    private static ServerLevel createAndRegisterLevel(final MinecraftServer server, final Map<ResourceKey<Level>, ServerLevel> map, final ResourceKey<Level> levelKey, Supplier<LevelStem> dimensionFactory) {
        final ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        final ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registries.LEVEL_STEM, levelKey.location());
        final LevelStem dimension = dimensionFactory.get();

        final ChunkProgressListener chunkProgressListener = ReflectionHelper.MinecraftServerAccess.progressListenerFactory.apply(server).create(11);
        final Executor executor = ReflectionHelper.MinecraftServerAccess.executor.apply(server);
        final LevelStorageSource.LevelStorageAccess anvilConverter = ReflectionHelper.MinecraftServerAccess.storageSource.apply(server);
        final WorldData worldData = server.getWorldData();
        final DerivedLevelData derivedLevelData = new DerivedLevelData(worldData, worldData.overworldData());

        Registry<LevelStem> dimensionRegistry = server.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
        if (dimensionRegistry instanceof MappedRegistry<LevelStem> writableRegistry) {
            writableRegistry.unfreeze();
            writableRegistry.register(dimensionKey, dimension, DIMENSION_REGISTRATION_INFO);
        } else {
            throw new IllegalStateException(String.format("Unable to register dimension %s -- dimension registry not writable", dimensionKey.location()));
        }

        final ServerLevel newLevel = new ServerLevel(
                server,
                executor,
                anvilConverter,
                derivedLevelData,
                levelKey,
                dimension,
                chunkProgressListener,
                worldData.isDebugWorld(),
                overworld.getSeed(),
                List.of(),
                false,
                null
        );

        overworld.getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(newLevel.getWorldBorder()));
        map.put(levelKey, newLevel);
        server.markWorldsDirty();
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(newLevel));
        PayloadHandler.updateDimensions(server, Set.of(levelKey), true);
        return newLevel;
    }

    @SuppressWarnings("deprecation")
    private void unregisterScheduledDimensions(final MinecraftServer server) {
        if (this.levelsPendingUnregistration.isEmpty())
            return;

        final Set<ResourceKey<Level>> keysToRemove = this.levelsPendingUnregistration;
        this.levelsPendingUnregistration = new HashSet<>();

        final Registry<LevelStem> oldRegistry = server.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
        if (!(oldRegistry instanceof MappedRegistry<LevelStem> oldMappedRegistry)) {
            LOGGER.warn("Cannot unload dimensions: dimension registry not an instance of MappedRegistry. There may be another mod causing incompatibility with Infiniverse, or Infiniverse may need to be updated for your version of forge/minecraft.");
            return;
        }
        LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess = ReflectionHelper.MinecraftServerAccess.registries.apply(server);
        RegistryAccess.Frozen composite = ReflectionHelper.LayeredRegistryAccessAccess.composite.apply(layeredRegistryAccess);
        if (!(composite instanceof RegistryAccess.ImmutableRegistryAccess immutableRegistryAccess)) {
            LOGGER.warn("Cannot unload dimensions: composite registry not an instance of ImmutableRegistryAccess. There may be another mod causing incompatibility with Infiniverse, or Infiniverse may be updated for your version of forge/minecraft.");
            return;
        }

        final Set<ResourceKey<Level>> removedLevelKeys = new HashSet<>();
        final ServerLevel overworld = server.getLevel(Level.OVERWORLD);

        for (final ResourceKey<Level> levelKeyToRemove : keysToRemove) {
            final @Nullable ServerLevel levelToRemove = server.getLevel(levelKeyToRemove);
            if (levelToRemove == null)
                continue;

            UnregisterDimensionEvent unregisterDimensionEvent = new UnregisterDimensionEvent(levelToRemove);
            NeoForge.EVENT_BUS.post(unregisterDimensionEvent);
            if (unregisterDimensionEvent.isCanceled())
                continue;

            final @Nullable ServerLevel removedLevel = server.forgeGetWorldMap().remove(levelKeyToRemove);

            if (removedLevel != null) {
                for (final ServerPlayer player : Lists.newArrayList(removedLevel.players())) {
                    ResourceKey<Level> respawnKey = player.getRespawnDimension();
                    if (keysToRemove.contains(respawnKey)) {
                        respawnKey = Level.OVERWORLD;
                        player.setRespawnPosition(respawnKey, null, 0, false, false);
                    }
                    if (respawnKey == null) {
                        respawnKey = Level.OVERWORLD;
                    }
                    @Nullable ServerLevel destinationLevel = server.getLevel(respawnKey);
                    if (destinationLevel == null) {
                        destinationLevel = overworld;
                    }

                    @Nullable
                    BlockPos destinationPos = player.getRespawnPosition();
                    if (destinationPos == null) {
                        destinationPos = destinationLevel.getSharedSpawnPos();
                    }

                    final float respawnAngle = player.getRespawnAngle();
                    player.teleportTo(destinationLevel, destinationPos.getX(), destinationPos.getY(), destinationPos.getZ(), respawnAngle, 0F);
                }
                removedLevel.save(null, false, removedLevel.noSave());

                NeoForge.EVENT_BUS.post(new LevelEvent.Unload(removedLevel));

                final WorldBorder overworldBorder = overworld.getWorldBorder();
                final WorldBorder removedWorldBorder = removedLevel.getWorldBorder();
                final List<BorderChangeListener> listeners = ReflectionHelper.WorldBorderAccess.listeners.apply(overworldBorder);
                BorderChangeListener targetListener = null;
                for (BorderChangeListener listener : listeners) {
                    if (listener instanceof BorderChangeListener.DelegateBorderChangeListener delegate
                            && removedWorldBorder == ReflectionHelper.DelegateBorderChangeListenerAccess.worldBorder.apply(delegate)) {
                        targetListener = listener;
                        break;
                    }
                }
                if (targetListener != null) {
                    overworldBorder.removeListener(targetListener);
                }

                removedLevelKeys.add(levelKeyToRemove);
            }
        }

        if (!removedLevelKeys.isEmpty()) {
            final MappedRegistry<LevelStem> newRegistry = new MappedRegistry<>(Registries.LEVEL_STEM, oldMappedRegistry.registryLifecycle());

            for (final var entry : oldRegistry.entrySet()) {
                final ResourceKey<LevelStem> oldKey = entry.getKey();
                final ResourceKey<Level> oldLevelKey = ResourceKey.create(Registries.DIMENSION, oldKey.location());
                final LevelStem dimension = entry.getValue();
                if (oldKey != null && dimension != null && !removedLevelKeys.contains(oldLevelKey)) {
                    newRegistry.register(oldKey, dimension, oldRegistry.registrationInfo(oldKey).orElse(DIMENSION_REGISTRATION_INFO));
                }
            }

            List<RegistryAccess.Frozen> newRegistryAccessList = new ArrayList<>();
            for (RegistryLayer layer : RegistryLayer.values()) {
                if (layer == RegistryLayer.DIMENSIONS) {
                    newRegistryAccessList.add(new RegistryAccess.ImmutableRegistryAccess(List.of(newRegistry)).freeze());
                } else {
                    newRegistryAccessList.add(layeredRegistryAccess.getLayer(layer));
                }
            }
            Map<ResourceKey<? extends Registry<?>>, Registry<?>> newRegistryMap = new HashMap<>();
            for (var registryAccess : newRegistryAccessList) {
                var registries = registryAccess.registries().toList();
                for (var registryEntry : registries) {
                    newRegistryMap.put(registryEntry.key(), registryEntry.value());
                }
            }
            ReflectionHelper.LayeredRegistryAccessAccess.values.set(layeredRegistryAccess, List.copyOf(newRegistryAccessList));
            ReflectionHelper.ImmutableRegistryAccessAccess.registries.set(immutableRegistryAccess, newRegistryMap);

            server.markWorldsDirty();

            PayloadHandler.updateDimensions(server, removedLevelKeys, false);
        }
    }

    @EventBusSubscriber(modid = Constants.MOD_ID)
    private static class ForgeEventHandler {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onServerTick(final ServerTickEvent.Post event) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                DimensionManager.INSTANCE.unregisterScheduledDimensions(server);
            }
        }

        @SubscribeEvent
        public static void onServerStopped(final ServerStoppedEvent event) {
            DimensionManager.INSTANCE.levelsPendingUnregistration = new HashSet<>();
        }
    }
}
