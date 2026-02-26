package com.ombremoon.spellbound.common.magic.effects;

import com.mojang.serialization.Codec;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.slf4j.Logger;

import java.util.List;

public interface TickProvider {
    Logger LOGGER = Constants.LOG;
    Codec<TickProvider> CODEC = TickProviderRegistry.CODEC.dispatchStable(TickProvider::serializer, TickProviderSerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, TickProvider> STREAM_CODEC = TickProviderRegistry.STREAM_CODEC.dispatch(TickProvider::serializer, TickProviderSerializer::streamCodec);

    TickProviderSerializer<?> serializer();

    boolean shouldTick(int tickCount);

    record AtTick(int tick) implements TickProvider {

        @Override
        public TickProviderSerializer<?> serializer() {
            return TickProviderRegistry.AT_TICK;
        }

        @Override
        public boolean shouldTick(int tickCount) {
            return tickCount == tick;
        }
    }

    record ForEveryTick(int interval) implements TickProvider {

        @Override
        public TickProviderSerializer<?> serializer() {
            return TickProviderRegistry.FOR_EVERY_TICK;
        }

        @Override
        public boolean shouldTick(int tickCount) {
            return tickCount % interval == 0;
        }
    }

    record AfterTick(int tick) implements TickProvider {

        @Override
        public TickProviderSerializer<?> serializer() {
            return TickProviderRegistry.AFTER_TICK;
        }

        @Override
        public boolean shouldTick(int tickCount) {
            return tickCount > tick;
        }
    }

    record BeforeTick(int tick) implements TickProvider {

        @Override
        public TickProviderSerializer<?> serializer() {
            return TickProviderRegistry.BEFORE_TICK;
        }

        @Override
        public boolean shouldTick(int tickCount) {
            return tickCount < tick;
        }
    }

    record EveryTick() implements TickProvider {

        @Override
        public TickProviderSerializer<?> serializer() {
            return TickProviderRegistry.EVERY_TICK;
        }

        @Override
        public boolean shouldTick(int tickCount) {
            return true;
        }
    }

    record NoTick() implements TickProvider {

        @Override
        public TickProviderSerializer<?> serializer() {
            return TickProviderRegistry.NO_TICK;
        }

        @Override
        public boolean shouldTick(int tickCount) {
            return false;
        }
    }

    //Add and/or check
    /*record CompoundTick(List<TickProvider> providers) implements TickProvider {

        public static CompoundTick compound(TickProvider... providers) {
            return new CompoundTick(List.of(providers));
        }

        @Override
        public TickProviderSerializer<?> serializer() {
            return TickProviderRegistry.COMPOUND_TICK;
        }

        @Override
        public boolean shouldTick(int tickCount) {
            for (TickProvider provider : providers) {
                if (!provider.shouldTick(tickCount))
                    return false;
            }

            return true;
        }
    }*/
}
