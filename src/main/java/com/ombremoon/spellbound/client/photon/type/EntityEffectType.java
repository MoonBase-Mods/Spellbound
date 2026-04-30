package com.ombremoon.spellbound.client.photon.type;

import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.client.photon.converter.EffectType;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class EntityEffectType extends EffectType<EffectData.Entity> {
    public static final ResourceLocation LOCATION = CommonClass.customLocation("entity");
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectData.Entity> STREAM_CODEC = StreamCodec.ofMember(
                EffectData.Entity::toNetwork, EffectData.Entity::fromNetwork
    );

    @Override
    public ResourceLocation id() {
        return LOCATION;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EffectData.Entity> streamCodec() {
        return STREAM_CODEC;
    }

    public static EffectBuilder<?> convertEntity(EffectData effectData) {
        if (!(effectData instanceof EffectData.Entity entityData)) {
            return null;
        }

        EffectBuilder.Entity builder = EffectBuilder.Entity.of(entityData.location, entityData.entityId, entityData.rotate);
        builder.setOffset(entityData.offset.x, entityData.offset.y, entityData.offset.z);
        builder.setRotation(entityData.rotation.x, entityData.rotation.y, entityData.rotation.z);
        builder.setScale(entityData.scale.x, entityData.scale.y, entityData.scale.z);
        builder.setDelay(entityData.delay);
        builder.setForcedDeath(entityData.forcedDeath);
        builder.setAllowMulti(entityData.allowMulti);
        return builder;
    }
}
