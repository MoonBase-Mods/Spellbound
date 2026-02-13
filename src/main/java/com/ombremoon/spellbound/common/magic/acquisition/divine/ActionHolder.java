package com.ombremoon.spellbound.common.magic.acquisition.divine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.List;

public record ActionHolder(ResourceLocation id, SpellAction value) {
    private static final Logger LOGGER = Constants.LOG;
    public static final Codec<ActionHolder> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(ActionHolder::id),
                    SpellAction.CODEC.fieldOf("value").forGetter(ActionHolder::value)
            ).apply(instance, ActionHolder::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ActionHolder> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ActionHolder::id,
            SpellAction.STREAM_CODEC, ActionHolder::value,
            ActionHolder::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ActionHolder>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", this.id.toString());
        SpellAction.CODEC.encodeStart(NbtOps.INSTANCE, this.value).resultOrPartial(LOGGER::error).ifPresent(nbt -> tag.put("Value", nbt));
        return tag;
    }

    public static ActionHolder deserializeNBT(CompoundTag tag) {
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("Id"));
        SpellAction value = SpellAction.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, tag.get("Value"))).resultOrPartial(LOGGER::error).orElse(null);
        return new ActionHolder(id, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof ActionHolder actionHolder && this.id.equals(actionHolder.id);
        }
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
