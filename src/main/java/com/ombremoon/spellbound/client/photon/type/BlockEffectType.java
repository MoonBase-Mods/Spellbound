package com.ombremoon.spellbound.client.photon.type;

import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.client.photon.converter.EffectType;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class BlockEffectType extends EffectType<EffectData.Block> {
    public static final ResourceLocation LOCATION = CommonClass.customLocation("block");
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectData.Block> STREAM_CODEC = StreamCodec.ofMember(
            EffectData.Block::toNetwork, EffectData.Block::fromNetwork
        );

    @Override
    public ResourceLocation id() {
        return LOCATION;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EffectData.Block> streamCodec() {
        return STREAM_CODEC;
    }

    public static EffectBuilder<?> convertBlock(EffectData effectData) {
        if (!(effectData instanceof EffectData.Block blockData)) {
            return null;
        }

        EffectBuilder.Block builder = EffectBuilder.Block.of(blockData.getLocation(), blockData.blockPos);
        builder.setOffset(blockData.offset.x, blockData.offset.y, blockData.offset.z);
        builder.setRotation(blockData.rotation.x, blockData.rotation.y, blockData.rotation.z);
        builder.setScale(blockData.scale.x, blockData.scale.y, blockData.scale.z);
        builder.setDelay(blockData.delay);
        builder.setForcedDeath(blockData.forcedDeath);
        builder.setAllowMulti(blockData.allowMulti);
        builder.setCheckState(blockData.checkState);

        for (var config : blockData.getConfigs()) {
            builder.defineConfig(config);
        }

        return builder;
    }
}
