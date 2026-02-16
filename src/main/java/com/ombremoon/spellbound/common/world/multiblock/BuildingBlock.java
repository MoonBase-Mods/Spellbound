package com.ombremoon.spellbound.common.world.multiblock;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTags;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public final class BuildingBlock implements Predicate<BlockState> {
    public static final BuildingBlock EMPTY = of(BlockTags.AIR);
    public static final BuildingBlock ANY = of(SBTags.Blocks.RITUAL_COMPATIBLE);
    private final Value[] values;
    @Nullable
    private Block[] blockStates;
    public static final Codec<BuildingBlock> CODEC = codec(true);
    public static final Codec<BuildingBlock> CODEC_NON_EMPTY = codec(false);
    public static final StreamCodec<RegistryFriendlyByteBuf, BuildingBlock> STREAM_CODEC = Value.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(values -> new BuildingBlock(values.toArray(Value[]::new)), buildingBlock -> Arrays.stream(buildingBlock.values).toList());

    private BuildingBlock(Stream<? extends Value> values) {
        this.values = values.toArray(Value[]::new);
    }

    private BuildingBlock(Value[] values) {
        this.values = values;
    }

    public Block[] getBlocks() {
        if (this.blockStates == null) {
            final Stream<Block> stream = Arrays.stream(this.values).flatMap(value -> value.getBlocks().stream());
            this.blockStates = stream.collect(Collectors.toCollection(BlockLinkedSet::createTypeAndComponentsSet)).toArray(Block[]::new);
        }

        return this.blockStates;
    }

    @Override
    public boolean test(BlockState block) {
        if (block == null) {
            return false;
        } else if (this.isEmpty()) {
            return block.isAir();
        } else {
            for (Value value : this.values) {
                if (value.matches(block)) return true;
            }
        }

        return false;
    }

    public boolean isEmpty() {
        return this.values.length == 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BuildingBlock buildingBlock && Arrays.equals(this.values, buildingBlock.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }

    public Value[] getValues() {
        return this.values;
    }

    public static BuildingBlock fromValues(Stream<? extends Value> stream) {
        BuildingBlock buildingBlock = new BuildingBlock(stream);
        return buildingBlock.isEmpty() ? EMPTY : buildingBlock;
    }

    public static BuildingBlock of() {
        return EMPTY;
    }

    public static BuildingBlock of(Block block) {
        return of(Stream.of(block));
    }

    public static <T extends Comparable<T>> BuildingBlock of(Block block, PropertyValue<T>... properties) {
        return of(Stream.of(block), properties);
    }

    private static BuildingBlock of(Stream<Block> blocks) {
        return fromValues(blocks.map(block -> new BlockValue(block, Optional.empty())));
    }

    private static <T extends Comparable<T>> BuildingBlock of(Stream<Block> blocks, PropertyValue<T>... properties) {
        StatePropertiesPredicate.Builder builder = StatePropertiesPredicate.Builder.properties();
        for (var propertyValue : properties) {
            T value = propertyValue.value;
            if (value instanceof Integer intValue) {
                builder.hasProperty((Property<Integer>) propertyValue.property(), intValue);
            } else if (value instanceof Boolean booleanValue) {
                builder.hasProperty((Property<Boolean>) propertyValue.property(), booleanValue);
            } else if (value instanceof StringRepresentable stringValue) {
                builder.hasProperty(propertyValue.property(), stringValue.getSerializedName());
            }
        }
        return fromValues(blocks.map(block -> new BlockValue(block, builder.build())));
    }

    public static BuildingBlock of(TagKey<Block> tag) {
        return fromValues(Stream.of(new TagValue(tag, Optional.empty())));
    }

    private static Codec<BuildingBlock> codec(boolean allowEmpty) {
        Codec<Value[]> codec = Codec.list(Value.CODEC)
                .comapFlatMap(
                        values -> !allowEmpty && values.isEmpty()
                                ? DataResult.error(() -> "Block array cannot be empty, at least one block must be defined")
                                : DataResult.success(values.toArray(new Value[0])),
                        List::of
                );
        return Codec.either(codec, Value.CODEC)
                .flatComapMap(
                        either -> either.map(BuildingBlock::new, value -> new BuildingBlock(new Value[]{value})),
                        buildingBlock -> {
                            if (buildingBlock.values.length == 1) {
                                return DataResult.success(Either.right(buildingBlock.values[0]));
                            } else {
                                return buildingBlock.values.length == 0 && !allowEmpty
                                        ? DataResult.error(() -> "Block array cannot be empty, at least one block must be defined")
                                        : DataResult.success(Either.left(buildingBlock.values));
                            }
                        }
                );
    }

    public record BlockValue(Block block, Optional<StatePropertiesPredicate> state) implements Value {
        static final MapCodec<BlockValue> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(blockValue -> blockValue.block),
                        StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(blockValue -> blockValue.state)
                ).apply(instance, BlockValue::new)
        );
        static final Codec<BlockValue> CODEC = MAP_CODEC.codec();
        static final StreamCodec<RegistryFriendlyByteBuf, BlockValue> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.BLOCK), BlockValue::block,
                ByteBufCodecs.optional(StatePropertiesPredicate.STREAM_CODEC), BlockValue::state,
                BlockValue::new
        );

        @Override
        public boolean equals(Object obj) {
            return obj instanceof BlockValue blockValue && blockValue.block.equals(this.block) && blockValue.block == this.block;
        }

        @Override
        public int hashCode() {
            return 31 * block.hashCode();
        }

        @Override
        public boolean matches(BlockState state) {
            if (!state.is(this.block)) return false;
            return this.state.isEmpty() || this.state.get().matches(state);
        }

        @Override
        public Set<Block> getBlocks() {
            return Collections.singleton(this.block);
        }

        @Override
        public Optional<StatePropertiesPredicate> getProperties() {
            return this.state;
        }
    }

    public record TagValue(TagKey<Block> tag, Optional<StatePropertiesPredicate> state) implements Value {
        static final MapCodec<TagValue> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(tagValue -> tagValue.tag),
                        StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(blockValue -> blockValue.state)
                ).apply(instance, TagValue::new)
        );
        static final Codec<TagValue> CODEC = MAP_CODEC.codec();
        static final StreamCodec<RegistryFriendlyByteBuf, TagValue> STREAM_CODEC = StreamCodec.of(
                TagValue::toNetwork, TagValue::fromNetwork
        );

        private static TagValue fromNetwork(RegistryFriendlyByteBuf buffer) {
            return new TagValue(TagKey.create(Registries.BLOCK, ResourceLocation.STREAM_CODEC.decode(buffer)), ByteBufCodecs.optional(StatePropertiesPredicate.STREAM_CODEC).decode(buffer));
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TagValue tagValue) {
            ResourceLocation.STREAM_CODEC.encode(buffer, tagValue.tag.location());
            ByteBufCodecs.optional(StatePropertiesPredicate.STREAM_CODEC).encode(buffer, tagValue.state);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TagValue tagValue && tagValue.tag.location().equals(this.tag.location());
        }

        @Override
        public boolean matches(BlockState blockState) {
            boolean tagMatch = (this.tag == SBTags.Blocks.RITUAL_COMPATIBLE)
                    ? !blockState.is(this.tag)
                    : blockState.is(this.tag);
            if (!tagMatch) return false;
            return this.state.isEmpty() || this.state.get().matches(blockState);
        }

        @Override
        public Set<Block> getBlocks() {
            Set<Block> set = Sets.newHashSet();

            if (this.tag == SBTags.Blocks.RITUAL_COMPATIBLE) {
                var blocks = BuiltInRegistries.BLOCK.stream().filter(block -> !block.defaultBlockState().is(SBTags.Blocks.RITUAL_COMPATIBLE)).toList();
                set.addAll(blocks);
            } else {
                for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(this.tag)) {
                    set.add(holder.value());
                }
            }

            return set;
        }

        @Override
        public Optional<StatePropertiesPredicate> getProperties() {
            return this.state;
        }
    }

    public interface Value {
        MapCodec<Value> MAP_CODEC = NeoForgeExtraCodecs.xor(BlockValue.MAP_CODEC, TagValue.MAP_CODEC)
                .xmap(either -> either.map(blockValue -> blockValue, tagValue -> tagValue), value -> {
                    if (value instanceof TagValue tagValue) {
                        return Either.right(tagValue);
                    } else if (value instanceof BlockValue blockValue) {
                        return Either.left(blockValue);
                    } else {
                        throw new UnsupportedOperationException("This is neither a block nor a tag value.");
                    }
                });
        Codec<Value> CODEC = MAP_CODEC.codec();
        StreamCodec<RegistryFriendlyByteBuf, Value> STREAM_CODEC = ByteBufCodecs.either(BlockValue.STREAM_CODEC, TagValue.STREAM_CODEC)
                .map(Either::unwrap, value -> {
                    if (value instanceof TagValue tagValue) {
                        return Either.right(tagValue);
                    } else if (value instanceof BlockValue blockValue) {
                        return Either.left(blockValue);
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
        );

        boolean matches(BlockState state);

        Set<Block> getBlocks();

        Optional<StatePropertiesPredicate> getProperties();
    }

    public record PropertyValue<T extends Comparable<T>>(Property<T> property, T value) {}
}
