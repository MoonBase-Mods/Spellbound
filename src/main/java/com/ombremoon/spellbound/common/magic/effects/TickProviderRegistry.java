package com.ombremoon.spellbound.common.magic.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SerializationUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.HashMap;
import java.util.Map;

public class TickProviderRegistry {
    private static final Map<ResourceLocation, TickProviderSerializer<?>> REGISTRY = new HashMap<>();
    public static final Codec<TickProviderSerializer<?>> CODEC = ResourceLocation.CODEC
            .comapFlatMap(
                    location -> {
                        TickProviderSerializer<?> serializer = REGISTRY.get(location);
                        return serializer != null
                                ? DataResult.success(serializer)
                                : DataResult.error(() -> "No TickProviderSerializer with key: " + location);
                    },
                    TickProviderSerializer::id
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, TickProviderSerializer<?>> STREAM_CODEC = SerializationUtil.REGISTRY_RESOURCE_STREAM_CODEC
            .map(TickProviderRegistry::getTickProviderSerializerFromLocation, TickProviderSerializer::id);

    public static TickProviderSerializer<?> getTickProviderSerializerFromLocation(ResourceLocation resourceLocation) {
        return REGISTRY.getOrDefault(resourceLocation, null);
    }

    public static final TickProviderSerializer<TickProvider.AtTick> AT_TICK = register("at_tick", new AtTickSerializer());
    public static final TickProviderSerializer<TickProvider.ForEveryTick> FOR_EVERY_TICK = register("for_every_tick", new ForEveryTickSerializer());
    public static final TickProviderSerializer<TickProvider.AfterTick> AFTER_TICK = register("after_tick", new AfterTickSerializer());
    public static final TickProviderSerializer<TickProvider.BeforeTick> BEFORE_TICK = register("before_tick", new BeforeTickSerializer());
    public static final TickProviderSerializer<TickProvider.EveryTick> EVERY_TICK = register("every_tick", new EveryTickSerializer());
    public static final TickProviderSerializer<TickProvider.NoTick> NO_TICK = register("no_tick", new NoTickSerializer());
//    public static final TickProviderSerializer<TickProvider.CompoundTick> COMPOUND_TICK = register("compound_tick", new CompoundTickSerializer());

    private static <T extends TickProviderSerializer<?>> T register(String name, T provider) {
        REGISTRY.put(CommonClass.customLocation(name), provider);
        return provider;
    }

    static class AtTickSerializer implements TickProviderSerializer<TickProvider.AtTick> {
        static final MapCodec<TickProvider.AtTick> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("tick").forGetter(TickProvider.AtTick::tick)
                ).apply(instance, TickProvider.AtTick::new)
        );
        static final StreamCodec<RegistryFriendlyByteBuf, TickProvider.AtTick> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, TickProvider.AtTick::tick,
                TickProvider.AtTick::new
        );

        @Override
        public ResourceLocation id() {
            return CommonClass.customLocation("at_tick");
        }

        @Override
        public MapCodec<TickProvider.AtTick> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TickProvider.AtTick> streamCodec() {
            return STREAM_CODEC;
        }
    }

    static class ForEveryTickSerializer implements TickProviderSerializer<TickProvider.ForEveryTick> {
        static final MapCodec<TickProvider.ForEveryTick> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("interval").forGetter(TickProvider.ForEveryTick::interval)
                ).apply(instance, TickProvider.ForEveryTick::new)
        );
        static final StreamCodec<RegistryFriendlyByteBuf, TickProvider.ForEveryTick> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, TickProvider.ForEveryTick::interval,
                TickProvider.ForEveryTick::new
        );

        @Override
        public ResourceLocation id() {
            return CommonClass.customLocation("for_every_tick");
        }

        @Override
        public MapCodec<TickProvider.ForEveryTick> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TickProvider.ForEveryTick> streamCodec() {
            return STREAM_CODEC;
        }
    }

    static class AfterTickSerializer implements TickProviderSerializer<TickProvider.AfterTick> {
        static final MapCodec<TickProvider.AfterTick> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("tick").forGetter(TickProvider.AfterTick::tick)
                ).apply(instance, TickProvider.AfterTick::new)
        );
        static final StreamCodec<RegistryFriendlyByteBuf, TickProvider.AfterTick> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, TickProvider.AfterTick::tick,
                TickProvider.AfterTick::new
        );

        @Override
        public ResourceLocation id() {
            return CommonClass.customLocation("after_tick");
        }

        @Override
        public MapCodec<TickProvider.AfterTick> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TickProvider.AfterTick> streamCodec() {
            return STREAM_CODEC;
        }
    }

    static class BeforeTickSerializer implements TickProviderSerializer<TickProvider.BeforeTick> {
        static final MapCodec<TickProvider.BeforeTick> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("tick").forGetter(TickProvider.BeforeTick::tick)
                ).apply(instance, TickProvider.BeforeTick::new)
        );
        static final StreamCodec<RegistryFriendlyByteBuf, TickProvider.BeforeTick> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, TickProvider.BeforeTick::tick,
                TickProvider.BeforeTick::new
        );

        @Override
        public ResourceLocation id() {
            return CommonClass.customLocation("before_tick");
        }

        @Override
        public MapCodec<TickProvider.BeforeTick> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TickProvider.BeforeTick> streamCodec() {
            return STREAM_CODEC;
        }
    }

    static class EveryTickSerializer implements TickProviderSerializer<TickProvider.EveryTick> {
        static final MapCodec<TickProvider.EveryTick> CODEC = MapCodec.unit(TickProvider.EveryTick::new);
        static final StreamCodec<RegistryFriendlyByteBuf, TickProvider.EveryTick> STREAM_CODEC = StreamCodec.unit(new TickProvider.EveryTick());

        @Override
        public ResourceLocation id() {
            return CommonClass.customLocation("every_tick");
        }

        @Override
        public MapCodec<TickProvider.EveryTick> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TickProvider.EveryTick> streamCodec() {
            return STREAM_CODEC;
        }
    }

    static class NoTickSerializer implements TickProviderSerializer<TickProvider.NoTick> {
        static final MapCodec<TickProvider.NoTick> CODEC = MapCodec.unit(TickProvider.NoTick::new);
        static final StreamCodec<RegistryFriendlyByteBuf, TickProvider.NoTick> STREAM_CODEC = StreamCodec.unit(new TickProvider.NoTick());

        @Override
        public ResourceLocation id() {
            return CommonClass.customLocation("no_tick");
        }

        @Override
        public MapCodec<TickProvider.NoTick> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TickProvider.NoTick> streamCodec() {
            return STREAM_CODEC;
        }
    }

    /*static class CompoundTickSerializer implements TickProviderSerializer<TickProvider.CompoundTick> {
        static final MapCodec<TickProvider.CompoundTick> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.lazyInitialized(TickProvider.CODEC::listOf).fieldOf("providers").forGetter(TickProvider.CompoundTick::providers)
                ).apply(instance, TickProvider.CompoundTick::new)
        );
        static final StreamCodec<RegistryFriendlyByteBuf, TickProvider.CompoundTick> STREAM_CODEC = StreamCodec.composite(
                TickProvider.STREAM_CODEC.apply(ByteBufCodecs.list()), TickProvider.CompoundTick::providers,
                TickProvider.CompoundTick::new
        );

        @Override
        public ResourceLocation id() {
            return CommonClass.customLocation("compound_tick");
        }

        @Override
        public MapCodec<TickProvider.CompoundTick> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TickProvider.CompoundTick> streamCodec() {
            return STREAM_CODEC;
        }
    }*/
}
