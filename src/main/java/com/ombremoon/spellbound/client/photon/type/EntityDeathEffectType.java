package com.ombremoon.spellbound.client.photon.type;

import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.client.photon.converter.EffectType;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class EntityDeathEffectType extends EffectType<EffectData.DeadEntity> {
    public static final ResourceLocation LOCATION = CommonClass.customLocation("entity_death");
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectData.DeadEntity> STREAM_CODEC = StreamCodec.ofMember(
                EffectData.DeadEntity::toNetwork, EffectData.DeadEntity::fromNetwork
    );

    @Override
    public ResourceLocation id() {
        return LOCATION;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EffectData.DeadEntity> streamCodec() {
        return STREAM_CODEC;
    }

    public static EffectBuilder<?> convertEntityDeath(EffectData effectData) {
        if (!(effectData instanceof EffectData.DeadEntity entityData)) {
            return null;
        }

        EffectBuilder.DeadEntity builder = EffectBuilder.DeadEntity.of(entityData.location, entityData.entityId, entityData.rotate);
        builder.setOffset(entityData.offset.x, entityData.offset.y, entityData.offset.z);
        builder.setRotation(entityData.rotation.x, entityData.rotation.y, entityData.rotation.z);
        builder.setScale(entityData.scale.x, entityData.scale.y, entityData.scale.z);
        builder.setDelay(entityData.delay);
        builder.setForcedDeath(entityData.forcedDeath);
        builder.setAllowMulti(entityData.allowMulti);
        return builder;
    }
}
