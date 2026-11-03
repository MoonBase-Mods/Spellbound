package com.ombremoon.spellbound.client.photon.type;

import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.client.photon.converter.EffectType;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class StaticEntityEffectType extends EffectType<EffectData.StaticEntity> {
    public static final ResourceLocation LOCATION = CommonClass.customLocation("static_entity");
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectData.StaticEntity> STREAM_CODEC = StreamCodec.ofMember(
                EffectData.StaticEntity::toNetwork, EffectData.StaticEntity::fromNetwork
    );

    @Override
    public ResourceLocation id() {
        return LOCATION;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EffectData.StaticEntity> streamCodec() {
        return STREAM_CODEC;
    }

    public static EffectBuilder<?> convertStaticEntity(EffectData effectData) {
        if (!(effectData instanceof EffectData.StaticEntity entityData)) {
            return null;
        }

        EffectBuilder.StaticEntity builder = EffectBuilder.StaticEntity.of(entityData.getLocation(), entityData.entityId, entityData.rotate);
        builder.setPos(entityData.effectPos);
        builder.setOffset(entityData.offset.x, entityData.offset.y, entityData.offset.z);
        builder.setRotation(entityData.rotation.x, entityData.rotation.y, entityData.rotation.z);
        builder.setScale(entityData.scale.x, entityData.scale.y, entityData.scale.z);
        builder.setDelay(entityData.delay);
        builder.setForcedDeath(entityData.forcedDeath);
        builder.setAllowMulti(entityData.allowMulti);

        for (var config : entityData.getConfigs()) {
            builder.defineConfig(config);
        }

        return builder;
    }
}
