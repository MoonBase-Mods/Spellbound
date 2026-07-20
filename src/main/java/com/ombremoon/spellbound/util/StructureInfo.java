package com.ombremoon.spellbound.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

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
