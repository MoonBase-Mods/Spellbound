package com.ombremoon.spellbound.client.gui.guide.renderers.init;

import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.StructureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GuideBlockAndTintGetter implements BlockAndTintGetter {
    public static Map<ResourceLocation, StructureInfo> loadedStructures = new HashMap<>();
    public static Map<ResourceLocation, Integer> structureQueue = new HashMap<>();
    private ResourceLocation structure;
    private Map<BlockPos, BlockState> blocks = new HashMap<>();
    private Vec3 size;
    private Level level;
    private Biome plainsBiome;

    public static void loadStructure(StructureInfo structureInfo) {
        if (structureQueue.containsKey(structureInfo.location())) structureQueue.remove(structureInfo.location());
        loadedStructures.put(structureInfo.location(), structureInfo);
    }

    public GuideBlockAndTintGetter(Level level, ResourceLocation structureLoc) {
        this.level = level;
        this.structure = structureLoc;
        this.plainsBiome = level.registryAccess().registryOrThrow(Registries.BIOME).get(Biomes.PLAINS);

        StructureInfo structureInfo = loadedStructures.get(structureLoc);
        if (structureInfo == null) {
            requestStructure(structureLoc);
        } else blockInfoToBlocks(structureInfo);
    }

    private void blockInfoToBlocks(StructureInfo blockInfo) {
        Map<BlockPos, BlockState> blockMap = new HashMap<>();
        for (StructureInfo.BlockData block : blockInfo.structure()) {
            blockMap.put(block.pos(), block.state());
        }
        this.blocks = blockMap;
        this.size = new Vec3(blockInfo.size());
    }

    private void requestStructure(ResourceLocation structure) {
        Integer queueTimer = structureQueue.get(structure);
        if (queueTimer != null && queueTimer + 60 <= Minecraft.getInstance().player.tickCount) return;
        PayloadHandler.requestStructureData(structure);
        structureQueue.put(structure, Minecraft.getInstance().player.tickCount);
    }

    public Vec3 getSize() {
        Map<ResourceLocation, StructureInfo> lol = loadedStructures;
        return this.size;
    }

    public Map<BlockPos, BlockState> getBlocks() {
        if (this.blocks != null) return this.blocks;

        StructureInfo blockInfo = loadedStructures.get(this.structure);
        if (blockInfo == null) {
            requestStructure(this.structure);
            return null;
        } else blockInfoToBlocks(blockInfo);

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
        if (!shade) return 1.0F;

        return switch (direction) {
            case DOWN -> 0.5F;
            case UP -> 1.0F;
            case NORTH, SOUTH -> 0.8F;
            case WEST, EAST -> 0.6F;
        };
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
        return state == null ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return this.blocks.get(blockPos).getFluidState();
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
