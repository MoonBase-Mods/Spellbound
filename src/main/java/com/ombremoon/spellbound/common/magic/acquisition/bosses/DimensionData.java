package com.ombremoon.spellbound.common.magic.acquisition.bosses;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;

public record DimensionData(ResourceKey<Biome> biome, Weather weather) {
    public static final Codec<DimensionData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(dimensionData -> dimensionData.biome),
                    Weather.CODEC.fieldOf("weather").forGetter(dimensionData -> dimensionData.weather)
            ).apply(instance, DimensionData::new)
    );

    public enum Weather implements StringRepresentable {
        CLEAR("clear"),
        RAIN("rain"),
        THUNDER("thunder");

        public static final StringRepresentableCodec<Weather> CODEC = StringRepresentable.fromEnum(Weather::values);

        private final String name;

        Weather(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
