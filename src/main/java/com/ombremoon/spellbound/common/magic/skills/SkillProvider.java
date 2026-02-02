package com.ombremoon.spellbound.common.magic.skills;

import com.mojang.serialization.Codec;
import com.ombremoon.spellbound.common.init.SBAffinities;
import com.ombremoon.spellbound.common.init.SBSkills;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public interface SkillProvider {

    ResourceLocation location();

    Type getType();

    void encode(RegistryFriendlyByteBuf buf);

    default String string() {
        return "Skill: [location: " + this.location().toString() + ", type: " + getType().name() + "]";
    }

    @SuppressWarnings("unchecked")
    static <T extends SkillProvider> T decode(Type type, RegistryFriendlyByteBuf buf) {
        return switch (type) {
            case SPELL -> (T) ByteBufCodecs.registry(SBSkills.SKILL_REGISTRY_KEY).decode(buf);
            case FAMILIAR -> (T) SBAffinities.REGISTRY.get(ResourceLocation.STREAM_CODEC.decode(buf));
            case null, default -> null;
        };
    }

    @SuppressWarnings("unchecked")
    static <T extends SkillProvider> T getFromId(Type type, ResourceLocation identifier) {
        return switch (type) {
            case SPELL -> (T) SBSkills.REGISTRY.get(identifier);
            case FAMILIAR -> (T) SBAffinities.REGISTRY.get(identifier);
            case null, default -> null;
        };
    }

    enum Type {
        SPELL,
        FAMILIAR
    }
}
