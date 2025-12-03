package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateSpellIdPayload(int entityId, SpellType<?> spellType, int shiftId, int castId) implements CustomPacketPayload {
    public static final Type<UpdateSpellIdPayload> TYPE = new Type<>(CommonClass.customLocation("update_spell_id"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSpellIdPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, UpdateSpellIdPayload::entityId,
            ByteBufCodecs.registry(SBSpells.SPELL_TYPE_REGISTRY_KEY), UpdateSpellIdPayload::spellType,
            ByteBufCodecs.INT, UpdateSpellIdPayload::shiftId,
            ByteBufCodecs.INT, UpdateSpellIdPayload::castId,
            UpdateSpellIdPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
