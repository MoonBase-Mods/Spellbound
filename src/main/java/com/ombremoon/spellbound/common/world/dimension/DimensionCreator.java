package com.ombremoon.spellbound.common.world.dimension;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Commoble, used with permission.
 * https://gist.github.com/Commoble/7db2ef25f94952a4d2e2b7e3d4be53e0
 */
public interface DimensionCreator {

    static DimensionCreator get() {
        return DimensionManager.INSTANCE;
    }

    ServerLevel getOrCreateLevel(final MinecraftServer server, final ResourceKey<Level> levelKey, final Supplier<LevelStem> dimensionFactory);

    void markDimensionForUnregistration(final MinecraftServer server, final ResourceKey<Level> levelToRemove);

    Set<ResourceKey<Level>> getLevelsPendingUnregistration();
}
