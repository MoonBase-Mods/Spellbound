package com.ombremoon.spellbound.common.magic.acquisition.deception;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record RuleType<T> (ResourceLocation id, Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
    private static final Map<ResourceLocation, RuleType<?>> RULE_REGISTRY = new HashMap<>();
    public static final Codec<RuleType<?>> CODEC = ResourceLocation.CODEC
            .comapFlatMap(
                    location -> {
                        if (!RULE_REGISTRY.containsKey(location)) {
                            return DataResult.error(() -> "Tried to serialize unregistered puzzle rule type: " + location);
                        } else  {
                            return DataResult.success(getRuleFromLocation(location));
                        }
                    },
                    RuleType::id
            );
    public static final Codec<Map<RuleType<?>, List<?>>> MAP_CODEC = Codec.dispatchedMap(CODEC, RuleType::codecOrThrow);

    public static final StreamCodec<ByteBuf, RuleType<?>> STREAM_CODEC = ResourceLocation.STREAM_CODEC
            .map(RuleType::getRuleFromLocation, RuleType::id);

    private static final List<ResourceLocation> RULES = new ArrayList<>();
    public static final RuleType<Block> NO_BUILDING = makeRule("no_building", BuiltInRegistries.BLOCK.byNameCodec(), ByteBufCodecs.registry(Registries.BLOCK));
    public static final RuleType<Item> NO_INTERACT = makeRule("no_interact", BuiltInRegistries.ITEM.byNameCodec(), ByteBufCodecs.registry(Registries.ITEM));
    public static final RuleType<TagKey<Item>> NO_INTERACT_TAG = makeRule("no_interact_tag", TagKey.codec(Registries.ITEM), SerializationUtil.tagKeyStreamCodec(Registries.ITEM));
    public static final RuleType<Unit> NO_PVP = makeRule("no_pvp", Unit.CODEC, StreamCodec.unit(Unit.INSTANCE));
    public static final RuleType<EntityType<?>> NO_PVE = makeRule("no_pve", BuiltInRegistries.ENTITY_TYPE.byNameCodec(), ByteBufCodecs.registry(Registries.ENTITY_TYPE));
    public static final RuleType<EntityType<?>> NO_PVE_OR_PVP = makeRule("no_pve_or_pvp", BuiltInRegistries.ENTITY_TYPE.byNameCodec(), ByteBufCodecs.registry(Registries.ENTITY_TYPE));
    public static final RuleType<Unit> NO_FLYING = makeRule("no_flying", Unit.CODEC, StreamCodec.unit(Unit.INSTANCE));
    public static final RuleType<SpellType<?>> NO_SPELL_CASTING = makeRule("no_spell_casting", SBSpells.REGISTRY.byNameCodec(), ByteBufCodecs.registry(SBSpells.SPELL_TYPE_REGISTRY_KEY));

    public static boolean isRule(ResourceLocation rule) {
        return RULE_REGISTRY.containsKey(rule);
    }

    private static <T> RuleType<T> makeRule(String name, Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        RuleType<T> rule = new RuleType<>(CommonClass.customLocation(name), codec, streamCodec);
        registerRule(rule);
        return rule;
    }

    public static void registerRule(RuleType<?> rule) {
        if (isRule(rule.id()))
            throw new IllegalStateException("Rule type " + rule + " has already been registered");

        RULE_REGISTRY.putIfAbsent(rule.id(), rule);
    }

    public static RuleType<?> getRuleFromLocation(ResourceLocation resourceLocation) {
        return RULE_REGISTRY.getOrDefault(resourceLocation, null);
    }

    private Codec<List<T>> codecOrThrow() {
        Codec<List<T>> codec = this.codec().listOf();
        if (codec == null) {
            throw new IllegalStateException(this + " is not a persistent component");
        } else {
            return codec;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof RuleType<?> rule && this.id.equals(rule.id));
    }

    @Override
    public @NotNull String toString() {
        return this.id.toString();
    }
}
