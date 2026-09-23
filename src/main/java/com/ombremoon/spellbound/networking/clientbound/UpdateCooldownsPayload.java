package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.magic.skills.SkillProvider;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateCooldownsPayload(int entityId, SkillProvider skill, int duration) implements CustomPacketPayload {
    public static final Type<UpdateCooldownsPayload> TYPE = new Type<>(CommonClass.customLocation("update_cooldowns"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateCooldownsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, UpdateCooldownsPayload::entityId,
            SkillProvider.STREAM_CODEC, UpdateCooldownsPayload::skill,
            ByteBufCodecs.INT, UpdateCooldownsPayload::duration,
            UpdateCooldownsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
