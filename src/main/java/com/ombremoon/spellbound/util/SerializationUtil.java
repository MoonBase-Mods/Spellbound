package com.ombremoon.spellbound.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.common.magic.effects.EffectHolder;
import com.ombremoon.spellbound.common.world.recipe.RuneCraftingRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class SerializationUtil {
    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceLocation> REGISTRY_RESOURCE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ResourceLocation::getNamespace,
            ByteBufCodecs.STRING_UTF8, ResourceLocation::getPath,
            ResourceLocation::fromNamespaceAndPath
    );

    public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, Vec3::x,
            ByteBufCodecs.DOUBLE, Vec3::y,
            ByteBufCodecs.DOUBLE, Vec3::z,
            Vec3::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, NonNullList<Ingredient>> INGREDIENT_LIST_STREAM_CODEC = StreamCodec.of(
            SerializationUtil::ingredientsToNetwork, SerializationUtil::ingredientsFromNetwork
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, NonNullList<TransfigurationRitual.Value>> VALUE_LIST_STREAM_CODEC = StreamCodec.of(
            SerializationUtil::valuesToNetwork, SerializationUtil::valuesFromNetwork
    );

    public static final Codec<Float> NON_NEGATIVE_FLOAT = floatRangeWithMessage(0.0F, Float.MAX_VALUE, p_274847_ -> "Value must be non-negative: " + p_274847_);

    private static Codec<Float> floatRangeWithMessage(float min, float max, Function<Float, String> errorMessage) {
        return Codec.FLOAT
                .validate(
                        p_274865_ -> p_274865_.compareTo(min) >= 0 && p_274865_.compareTo(max) <= 0
                                ? DataResult.success(p_274865_)
                                : DataResult.error(() -> errorMessage.apply(p_274865_))
                );
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, TagKey<T>> tagKeyStreamCodec(ResourceKey<? extends Registry<T>> registryKey) {
        return REGISTRY_RESOURCE_STREAM_CODEC.map(p_319559_ -> TagKey.create(registryKey, p_319559_), TagKey::location);
    }

    private static NonNullList<Ingredient> ingredientsFromNetwork(RegistryFriendlyByteBuf buffer) {
        int i = buffer.readVarInt();
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
        nonnulllist.replaceAll(p_319735_ -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
        return nonnulllist;
    }

    private static void ingredientsToNetwork(RegistryFriendlyByteBuf buffer, NonNullList<Ingredient> items) {
        buffer.writeVarInt(items.size());
        for (Ingredient ingredient : items) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
        }
    }

    private static NonNullList<TransfigurationRitual.Value> valuesFromNetwork(RegistryFriendlyByteBuf buffer) {
        int i = buffer.readVarInt();
        var nonnulllist = NonNullList.withSize(i, TransfigurationRitual.Value.EMPTY);
        nonnulllist.replaceAll(p_319735_ -> TransfigurationRitual.Value.STREAM_CODEC.decode(buffer));
        return nonnulllist;
    }

    private static void valuesToNetwork(RegistryFriendlyByteBuf buffer, NonNullList<TransfigurationRitual.Value> items) {
        buffer.writeVarInt(items.size());
        for (var value : items) {
            TransfigurationRitual.Value.STREAM_CODEC.encode(buffer, value);
        }
    }
}
