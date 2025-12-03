package com.ombremoon.spellbound.common.magic.api.buff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

public record ModifierData(Holder<Attribute> attribute, AttributeModifier attributeModifier) {
    public static final Codec<ModifierData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(data -> data.attribute),
                    AttributeModifier.CODEC.fieldOf("attributeModifier").forGetter(data -> data.attributeModifier)
            ).apply(instance, ModifierData::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ModifierData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE), ModifierData::attribute,
            AttributeModifier.STREAM_CODEC, ModifierData::attributeModifier,
            ModifierData::new
    );
}

