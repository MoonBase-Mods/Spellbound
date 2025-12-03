package com.ombremoon.spellbound.common.world.multiblock;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBMultiblockSerializers;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.Loggable;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class Multiblock implements Loggable {
    private static final Logger LOGGER = Constants.LOG;
    public static final Codec<Multiblock> CODEC = SBMultiblockSerializers.REGISTRY.byNameCodec().dispatch(Multiblock::getSerializer, MultiblockSerializer::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, Multiblock> STREAM_CODEC = ByteBufCodecs.registry(SBMultiblockSerializers.MULTIBLOCK_SERIALIZER_REGISTRY_KEY)
            .dispatch(Multiblock::getSerializer, MultiblockSerializer::streamCodec);
    protected final MultiblockStructure structure;
    public final Map<MultiblockIndex, BuildingBlock> indices;
    private final int width;
    private final int height;
    private final int depth;
    private final MultiblockIndex activeIndex;

    protected Multiblock(MultiblockStructure structure) {
        this.structure = structure;
        this.indices = structure.indices;
        this.width = structure.width;
        this.height = structure.height;
        this.depth = structure.depth;
        this.activeIndex = structure.activeIndex;
    }

    public abstract MultiblockSerializer<?> getSerializer();

    public void onActivate(Player player, Level level, MultiblockPattern pattern) {

    }

    public void onRemoved(Player player, Level level, MultiblockPattern pattern) {

    }

    public Block getBlock(MultiblockIndex index) {
        BuildingBlock block = this.indices.get(index);
        return block != null ? block.getBlocks()[0] : null;
    }

    public BlockPos locateOrigin(MultiblockIndex index, BlockPos blockPos, Direction facing) {
        return blockPos.relative(facing.getCounterClockWise(), index.x()).relative(Direction.DOWN, index.y()).relative(facing.getOpposite(), index.z());
    }

    public MultiblockIndex getFinalIndex() {
        return new MultiblockIndex(this.getWidth() - 1, this.getHeight() - 1, this.getDepth() - 1);
    }

    public MultiblockPattern findPattern(LevelAccessor level, BlockPos blockPos, Direction facing) {
        return this.findPattern(level, blockPos, facing, this.getActiveIndex());
    }

    public MultiblockPattern findPattern(LevelAccessor level, BlockPos blockPos, Direction facing, MultiblockIndex index) {
        return this.findPattern(level, blockPos, facing, Direction.UP, index);
    }

    private MultiblockPattern findPattern(LevelAccessor level, BlockPos blockPos, Direction facing, Direction up, MultiblockIndex index) {
        BlockPos origin = this.locateOrigin(index, blockPos, facing);
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                for (int k = 0; k < this.depth; k++) {
                    MultiblockIndex currentIndex = new MultiblockIndex(i, j, k);
                    BlockPos currentPos = currentIndex.toPos(facing, origin);
                    BlockState testState = level.getBlockState(currentPos);
                    if (!this.indices.get(currentIndex).test(testState))
                        return null;
                }
            }
        }


        LOGGER.debug("Found multiblock pattern for {} from {} to {}", this, origin, this.getFinalIndex().toPos(facing, origin));
        return new MultiblockPattern(this, origin, facing, up);
    }

    public boolean tryCreateMultiblock(Level level, Player player, BlockPos blockPos, Direction facing) {
        if (!this.checkForMultiblock(level, blockPos)) {
            Multiblock.MultiblockPattern pattern = this.findPattern(level, blockPos, facing);
            if (pattern != null) {
                pattern.assignMultiblock(level);
                this.onActivate(player, level, pattern);
                return true;
            }
        }
        return false;
    }

    private boolean checkForMultiblock(Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        return blockEntity instanceof MultiblockPart part && part.isAssigned();
    }

    public void clearMultiblock(Player player, Level level, MultiblockPattern pattern) {
        if (!level.isClientSide) {
            this.onRemoved(player, level, pattern);
            this.clearMultiblock(level, pattern.frontBottomLeft, pattern.facing);
        }
    }

    private void clearMultiblock(Level level, BlockPos blockPos, Direction facing) {
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                for (int k = 0; k < this.depth; k++) {
                    MultiblockIndex currentIndex = new MultiblockIndex(i, j, k);
                    BlockPos currentPos = currentIndex.toPos(facing, blockPos);
                    BlockEntity blockEntity = level.getBlockEntity(currentPos);
                    if (blockEntity instanceof MultiblockPart part && part.getMultiblock() == this && part.isAssigned()) {
                        BlockState state = blockEntity.getBlockState();
                        part.assign(null, MultiblockIndex.ORIGIN, facing);
                        part.onCleared(level, currentPos);
                        level.sendBlockUpdated(currentPos, state, state, 3);
                    }
                }
            }
        }

        LOGGER.debug("Successfully removed multiblock {} from {} to {}", this, blockPos, this.getFinalIndex().toPos(facing, blockPos));
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getDepth() {
        return this.depth;
    }

    public MultiblockIndex getActiveIndex() {
        return this.activeIndex;
    }

    public void debugMultiblock(Level level, BlockPos blockPos, Direction facing) {
        for (var entry : this.indices.entrySet()) {
            var block = entry.getValue();
            var possibleStates = block.getBlocks()[0].getStateDefinition().getPossibleStates();
            Optional<BlockState> optional = possibleStates.stream().filter(block).findFirst();
            if (optional.isPresent()) {
                BlockState state = block.equals(BuildingBlock.ANY) ? Blocks.GRASS_BLOCK.defaultBlockState() : optional.get();
                BlockPos indexPos = entry.getKey().toPos(facing, blockPos);
                if (!level.isClientSide)
                    level.setBlock(indexPos, state, 3);
            }
        }
    }

    protected static abstract class MultiblockBuilder implements MultiblockRegistration {
        protected List<String[]> pattern = Lists.newArrayList();
        protected final Map<Character, BuildingBlock> key = Maps.newHashMap();
        protected int width;
        protected int depth;
        protected MultiblockIndex activeIndex;

        protected MultiblockBuilder() {
            this.key.put(' ', BuildingBlock.EMPTY);
            this.activeIndex = MultiblockIndex.ORIGIN;
        }
    }

    public record MultiblockStructure(Map<MultiblockIndex, BuildingBlock> indices, int width, int height,
                                      int depth, MultiblockIndex activeIndex, Optional<MultiblockPattern.Data> data) {
        public static final MapCodec<MultiblockStructure> MAP_CODEC = MultiblockPattern.Data.MAP_CODEC
                .flatXmap(
                        MultiblockStructure::unpack,
                        multiblockStructure -> multiblockStructure.data.map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked multiblock"))
                );
        public static final StreamCodec<RegistryFriendlyByteBuf, MultiblockStructure> STREAM_CODEC = StreamCodec.ofMember(
                MultiblockStructure::toNetwork, MultiblockStructure::fromNetwork
        );

        public static MultiblockStructure of(Map<Character, BuildingBlock> key, List<String[]> pattern, MultiblockIndex activeIndex) {
            MultiblockPattern.Data data = new MultiblockPattern.Data(key, pattern, activeIndex);
            return unpack(data).getOrThrow();
        }

        private static DataResult<MultiblockStructure> unpack(MultiblockPattern.Data data) {
            List<String[]> pattern = data.pattern;
            int i = pattern.size();
            int j = pattern.getFirst().length;
            int k = pattern.getFirst()[0].length();
            CharSet charSet = new CharArraySet(data.key.keySet());

            for (int i1 = 0; i1 < i; i1++) {
                var array = pattern.get(i1);
                for (String s : array) {
                    for (int l = 0; l < s.length(); l++) {
                        char c0 = s.charAt(l);
                        BuildingBlock block = c0 == ' ' ? BuildingBlock.EMPTY : data.key.get(c0);
                        if (block == null) {
                            return DataResult.error(() -> "Pattern references symbol '" + c0 + "' but it's not defined in the key");
                        }

                        charSet.remove(c0);
                    }
                }
            }

            return !charSet.isEmpty()
                    ? DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + charSet)
                    : DataResult.success(new MultiblockStructure(buildPattern(data.key, data.pattern), k, i, j, data.index, Optional.of(data)));
        }

        private static Map<MultiblockIndex, BuildingBlock> buildPattern(Map<Character, BuildingBlock> key, List<String[]> pattern) {
            ensureAllCharactersMatched(key);
            Map<MultiblockIndex, BuildingBlock> map = Maps.newHashMap();
            BuildingBlock BuildingBlock;
            int width = pattern.getFirst()[0].length();
            int depth = pattern.getFirst().length;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < pattern.size(); j++) {
                    for (int k = 0; k < depth; k++) {
                        MultiblockIndex index = new MultiblockIndex(i, j, k);
                        BuildingBlock = key.get(pattern.get(j)[k].charAt(i));
                        map.put(index, BuildingBlock);
                    }
                }
            }

            return map;
        }

        private static void ensureAllCharactersMatched(Map<Character, BuildingBlock> key) {
            List<Character> list = Lists.newArrayList();

            for (Map.Entry<Character, BuildingBlock> entry : key.entrySet()) {
                if (entry.getValue() == null) {
                    list.add(entry.getKey());
                }
            }

            if (!list.isEmpty()) {
                throw new IllegalStateException("Predicates for character(s) " + Joiner.on(",").join(list) + " are missing");
            }
        }

        private void toNetwork(RegistryFriendlyByteBuf buffer) {
            buffer.writeVarInt(this.indices.size());
            for (var entry : indices.entrySet()) {
                MultiblockIndex.STREAM_CODEC.encode(buffer, entry.getKey());
                BuildingBlock.STREAM_CODEC.encode(buffer, entry.getValue());
            }

            buffer.writeVarInt(this.width);
            buffer.writeVarInt(this.height);
            buffer.writeVarInt(this.depth);

            MultiblockIndex.STREAM_CODEC.encode(buffer, this.activeIndex);
        }

        private static MultiblockStructure fromNetwork(RegistryFriendlyByteBuf buffer) {
            Map<MultiblockIndex, BuildingBlock> indices = Maps.newHashMap();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                MultiblockIndex index = MultiblockIndex.STREAM_CODEC.decode(buffer);
                BuildingBlock block = BuildingBlock.STREAM_CODEC.decode(buffer);
                indices.put(index, block);
            }

            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            int depth = buffer.readVarInt();
            MultiblockIndex activeIndex = MultiblockIndex.STREAM_CODEC.decode(buffer);
            return new MultiblockStructure(indices, width, height, depth, activeIndex, Optional.empty());
        }
    }

    public record MultiblockPattern(Multiblock multiblock, BlockPos frontBottomLeft, Direction facing, Direction up) {

        public BlockState getBlock(LevelAccessor level, MultiblockIndex index) {
            return this.getBlock(level, index.x(), index.y(), index.z());
        }

        public BlockState getBlock(LevelAccessor level, int xOffset, int yOffset, int zOffset) {
            return level.getBlockState(getIndexPos(xOffset, yOffset, zOffset));
        }

        public BlockPos getIndexPos(MultiblockIndex index) {
            return this.frontBottomLeft.relative(facing.getClockWise(), index.x()).relative(up, index.y()).relative(facing, index.z());
        }

        public BlockPos getIndexPos(int xOffset, int yOffset, int zOffset) {
            return this.frontBottomLeft.relative(facing.getClockWise(), xOffset).relative(up, yOffset).relative(facing, zOffset);
        }

        void assignMultiblock(Level level) {
            this.forEachBlock(level, (blockState, index) -> {
                BlockPos blockPos = getIndexPos(index);
                BlockEntity blockEntity = level.getBlockEntity(blockPos);
                if (blockEntity instanceof MultiblockPart part) {
                    part.assign(multiblock, index, facing);
                    level.sendBlockUpdated(blockPos, blockState, blockState, 3);
                }
            });
        }

        public void forEachBlock(LevelAccessor level, BiConsumer<BlockState, MultiblockIndex> consumer) {
            for (int i = 0; i < multiblock.width; i++) {
                for (int j = 0; j < multiblock.height; j++) {
                    for (int k = 0; k < multiblock.depth; k++) {
                        consumer.accept(getBlock(level, i, j, k), new MultiblockIndex(i, j, k));
                    }
                }
            }
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            Multiblock.CODEC
                    .encodeStart(NbtOps.INSTANCE, this.multiblock)
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(nbt -> tag.put("Multiblock", nbt));
            tag.putInt("X", frontBottomLeft.getX());
            tag.putInt("Y", frontBottomLeft.getY());
            tag.putInt("Z", frontBottomLeft.getZ());
            tag.putString("Facing", facing.getSerializedName());
            tag.putString("Up", up.getSerializedName());
            return tag;
        }

        public static MultiblockPattern load(CompoundTag tag) {
            Multiblock multiblock = Multiblock.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, tag.get("Multiblock"))).resultOrPartial(LOGGER::error).orElseThrow();
            BlockPos origin = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
            Direction facing = Direction.byName(tag.getString("Facing"));
            Direction up = Direction.byName(tag.getString("Up"));
            return new MultiblockPattern(multiblock, origin, facing, up);
        }

        record Data(Map<Character, BuildingBlock> key, List<String[]> pattern, MultiblockIndex index) {
            private static final Codec<List<String[]>> PATTERN_CODEC = Codec.STRING.listOf().listOf().comapFlatMap(stringList -> {
                int i = stringList.getFirst().getFirst().length();

                for (String s : stringList.getFirst()) {
                    if (i != s.length()) {
                        return DataResult.error(() -> "Invalid pattern: each row must be the same width");
                    }
                }

                return DataResult.success(stringList.stream()
                        .map(list -> list.toArray(new String[0]))
                        .toList());
            }, stringArray -> stringArray.stream()
                        .map(Arrays::asList)
                        .toList());
            private static final Codec<Character> SYMBOL_CODEC = Codec.STRING.comapFlatMap(symbol -> {
                if (symbol.length() != 1) {
                    return DataResult.error(() -> "Invalid key entry: '" + symbol + "' is an invalid symbol (must be 1 character only).");
                } else {
                    return DataResult.success(symbol.charAt(0));
                }
            }, String::valueOf);
            public static final MapCodec<Data> MAP_CODEC = RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            ExtraCodecs.strictUnboundedMap(SYMBOL_CODEC, BuildingBlock.CODEC).fieldOf("key").forGetter(data -> data.key),
                            PATTERN_CODEC.fieldOf("pattern").forGetter(data -> data.pattern),
                            MultiblockIndex.CODEC.fieldOf("index").forGetter(data -> data.index)
                    ).apply(instance, Data::new)
            );
        }
    }
}
