package com.ombremoon.spellbound.common.magic.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBAffinities;
import com.ombremoon.spellbound.main.CommonClass;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class FamiliarAffinity implements SkillProvider {
    private final ResourceLocation identifier;
    private final int cooldown;
    private final int requiredBond;

    public FamiliarAffinity(ResourceLocation identifier, int requiredBond) {
        this(identifier, 0, requiredBond);
    }

    public FamiliarAffinity(ResourceLocation identifier, int cooldown, int requiredBond) {
        this.identifier = identifier;
        this.cooldown = cooldown;
        this.requiredBond = requiredBond;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getRequiredBond() {
        return requiredBond;
    }

    @Override
    public ResourceLocation location() {
        return identifier;
    }

    @Override
    public Type getType() {
        return Type.FAMILIAR;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        ResourceLocation.STREAM_CODEC.encode(buf, location());
    }
}
