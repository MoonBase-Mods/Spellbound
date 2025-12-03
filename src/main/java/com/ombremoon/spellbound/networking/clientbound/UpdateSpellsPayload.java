package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateSpellsPayload(int entityId, SpellType<?> spellType, int castId, CompoundTag initTag, CompoundTag spellData) implements CustomPacketPayload {
    public static final Type<UpdateSpellsPayload> TYPE = new Type<>(CommonClass.customLocation("update_spells"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSpellsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, UpdateSpellsPayload::entityId,
            ByteBufCodecs.registry(SBSpells.SPELL_TYPE_REGISTRY_KEY), UpdateSpellsPayload::spellType,
            ByteBufCodecs.INT, UpdateSpellsPayload::castId,
            ByteBufCodecs.COMPOUND_TAG, UpdateSpellsPayload::initTag,
            ByteBufCodecs.COMPOUND_TAG, UpdateSpellsPayload::spellData,
            UpdateSpellsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
