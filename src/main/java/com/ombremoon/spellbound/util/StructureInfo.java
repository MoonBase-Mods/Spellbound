package com.ombremoon.spellbound.util;

import com.ombremoon.spellbound.common.world.block.RuneBlock;
import com.ombremoon.spellbound.common.world.item.ChalkItem;
import com.ombremoon.spellbound.common.world.multiblock.BuildingBlock;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.common.world.multiblock.MultiblockIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.*;

public record StructureInfo(ResourceLocation location, Vector3f size, List<BlockData> structure) {

    public static final StreamCodec<RegistryFriendlyByteBuf, StructureInfo> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, StructureInfo::location,
            ByteBufCodecs.VECTOR3F, StructureInfo::size,
            BlockData.STREAM_CODEC.apply(ByteBufCodecs.list()), StructureInfo::structure,
            StructureInfo::new
    );

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof StructureInfo info && info.location().equals(this.location()));
    }

    public static StructureInfo fromMultiblock(ResourceLocation id, Multiblock multiblock) {
        List<BlockData> blockData = new ArrayList<>();

        for (Map.Entry<MultiblockIndex, BuildingBlock> entry : multiblock.indices.entrySet()) {
            BuildingBlock buildingBlock = entry.getValue();
            if (buildingBlock == null) continue;

            if (buildingBlock.equals(BuildingBlock.ANY)) {
                blockData.add(new BlockData(entry.getKey().toPos(), Blocks.AIR.defaultBlockState()));
                continue;
            }

            Block[] blocks = entry.getValue().getBlocks();
            if (blocks == null || blocks.length == 0) continue;

            BlockState state;
            if (blocks[0] instanceof RuneBlock rune)
                state = rune.defaultBlockState().setValue(RuneBlock.RUNE_TYPE, new Random().nextInt(1, 26));
            else state = blocks[0].defaultBlockState();

            blockData.add(new BlockData(entry.getKey().toPos(), state));
        }

        return new StructureInfo(
                id,
                new Vector3f(
                        multiblock.getWidth(),
                        multiblock.getHeight(),
                        multiblock.getDepth()),
                blockData);
    }

    public record BlockData(BlockPos pos, BlockState state) {
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockData> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, BlockData::pos,
                ByteBufCodecs.fromCodec(BlockState.CODEC), BlockData::state,
                BlockData::new
        );

        public BlockData(BlockPos pos, BlockState state, CompoundTag nbt) {
            this(pos, state);
        }

        public static List<BlockData> structureToBlockData(List<StructureTemplate.StructureBlockInfo> list) {
            List<BlockData> toReturn = new ArrayList<>();
            for (var block : list) {
                toReturn.add(new BlockData(block.pos(), block.state()));
            }
            return toReturn;
        }
    }
}
