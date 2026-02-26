package com.ombremoon.spellbound.common.magic.skills;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record PseudoSkillProvider(ResourceLocation location) implements SkillProvider {

    @Override
    public ResourceLocation location() {
        return this.location;
    }

    @Override
    public Type getType() {
        return Type.CUSTOM;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(this.location);
    }
}
