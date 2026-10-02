package com.ombremoon.spellbound.client.gui.guide.renderers.init;

import com.ombremoon.spellbound.common.init.SBTags;
import com.ombremoon.spellbound.common.world.multiblock.*;
import com.ombremoon.spellbound.datagen.ModTagProvider;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.StructureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.antlr.v4.runtime.misc.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuideBlockAndTintGetter implements BlockAndTintGetter {
    private static final int STRUCTURE_CACHE_TIMEOUT = 600;

    public static Map<ResourceLocation, StructureInfo> loadedStructures = new HashMap<>();
    public static Map<ResourceLocation, Integer> structureCache = new HashMap<>();
    public static Map<ResourceLocation, Integer> structureQueue = new HashMap<>();
    private final ResourceLocation structure;
    private final boolean isMultiblock;
    private Map<BlockPos, BlockState> blocks = new HashMap<>();
    private Vec3 size;
    private final Biome plainsBiome;

    /**
     * Adds a structure to the loaded structures map and removes it from the queue
     * @param structureInfo The structure to add
     */
    public static void loadStructure(StructureInfo structureInfo) {
        if (structureQueue.containsKey(structureInfo.location())) structureQueue.remove(structureInfo.location());
        loadedStructures.put(structureInfo.location(), structureInfo);
        structureCache.put(structureInfo.location(), Minecraft.getInstance().player.tickCount);
    }

    /**
     * Checks if a structure has not been accessed for over 5 minutes. If not accessed removes it to free up memory
     */
    public static void tickStructureCache() {
        if (Minecraft.getInstance().player == null) return;

        int currentCount = Minecraft.getInstance().player.tickCount;
        var iterator = structureCache.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<ResourceLocation, Integer> entry = iterator.next();
            int originCount = entry.getValue();
            if ((originCount > currentCount && currentCount > STRUCTURE_CACHE_TIMEOUT) || originCount + STRUCTURE_CACHE_TIMEOUT > currentCount ) {
                iterator.remove();
                loadedStructures.remove(entry.getKey());
            }
        }
    }

    /**
     * Get structure currently being rendered
     * @return ResourceLocation of the structure/multiblock being rendered
     */
    public ResourceLocation getStructure() {
        return structure;
    }

    /**
     * Gets the structure info for the requested structure or makes a request for it over network if not available
     * @param structureLoc ResourceLocation of the structure
     * @param isMultiblock false if structure nbt, true if multiblock registry object
     */
    public GuideBlockAndTintGetter(ResourceLocation structureLoc, boolean isMultiblock) {
        this.structure = structureLoc;
        this.isMultiblock = isMultiblock;
        this.plainsBiome = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.BIOME).get(Biomes.PLAINS);

        //If its not loaded request it otherwise piss off
        StructureInfo structureInfo = loadedStructures.get(structureLoc);
        if (structureInfo == null) {
            requestStructure(structureLoc, isMultiblock);
        } else blockInfoToBlocks(structureInfo);
    }

    /**
     * Converts the StructureInfo object to a Map of blockpos -> blockstate to be used in the renderer, also sets the size
     * @param blockInfo The structureInfo to convert
     */
    private void blockInfoToBlocks(StructureInfo blockInfo) {
        Map<BlockPos, BlockState> blockMap = new HashMap<>();
        for (StructureInfo.BlockData block : blockInfo.structure()) {
            blockMap.put(block.pos(), block.state());
        }
        this.blocks = blockMap;
        this.size = new Vec3(blockInfo.size());
    }

    /**
     * Will attempt to fetch the requested structure, If the structure does not exist the StructureInfo block list added to map will be empty
     * @param structure the ResourceLocation of the structure/multiblock to fetch
     * @param isMultiblock true if is a multiblock, false if structure nbt
     */
    private void requestStructure(ResourceLocation structure, boolean isMultiblock) {
        Integer queueTimer = structureQueue.get(structure);
        int playerTickCount = Minecraft.getInstance().player.tickCount;
        if (queueTimer != null && ((queueTimer > playerTickCount && playerTickCount < 60) || (queueTimer + 60 <= playerTickCount))) return;

        PayloadHandler.requestStructureData(structure, isMultiblock);
        structureQueue.put(structure, playerTickCount);
    }

    /**
     * gets the size of the structure/multiblock
     * @return length, height, depth
     */
    public Vec3 getSize() {
        if (this.size == null) {
            StructureInfo info = loadedStructures.get(this.structure);
            if (info == null) return this.size;
            blockInfoToBlocks(info);
        }
        return this.size;
    }

    /**
     * Gets a map of blockpos -> blockstate for structure renderering
     * @return the map dipshit
     */
    public Map<BlockPos, BlockState> getBlocks() {
        if (this.blocks != null) return this.blocks;

        StructureInfo blockInfo = loadedStructures.get(this.structure);
        if (blockInfo == null) {
            requestStructure(this.structure, isMultiblock);
            return null;
        } else blockInfoToBlocks(blockInfo);

        structureCache.put(this.structure, Minecraft.getInstance().player.tickCount); //Update cache
        return this.blocks;
    }

    @Override
    public int getBrightness(LightLayer lightType, BlockPos blockPos) {
        return 15;
    }

    @Override
    public int getRawBrightness(BlockPos blockPos, int amount) {
        return 15 - amount;
    }


    @Override
    public float getShade(@NotNull Direction direction, boolean shade) {
        return 1f;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return colorResolver.getColor(plainsBiome, blockPos.getX(), blockPos.getZ());
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        BlockState state = this.blocks.get(blockPos);
        return state == null || state.is(SBTags.Blocks.MULTIBLOCK_HIDDEN_BLOCKS) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return getBlockState(blockPos).getFluidState();
    }

    @Override
    public int getHeight() {
        return 255;
    }

    @Override
    public int getMinBuildHeight() {
        return 0;
    }
}
