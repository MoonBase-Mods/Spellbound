package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EndSpellPayload(int entityId, SpellType<?> spellType, int castId) implements CustomPacketPayload {
    public static final Type<EndSpellPayload> TYPE = new Type<>(CommonClass.customLocation("end_spell"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EndSpellPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EndSpellPayload::entityId,
            ByteBufCodecs.registry(SBSpells.SPELL_TYPE_REGISTRY_KEY), EndSpellPayload::spellType,
            ByteBufCodecs.INT, EndSpellPayload::castId,
            EndSpellPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
