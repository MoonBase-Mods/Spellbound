package com.ombremoon.spellbound.common.magic.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class EffectContextParamSets {
    public static final LootContextParam<Integer> RITUAL_TIER = create("ritual_tier");

    public static final LootContextParamSet MAGIC_DAMAGE = LootContextParamSets.register(
            "magic_damage",
            p_344709_ -> p_344709_.required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ORIGIN)
                    .required(LootContextParams.DAMAGE_SOURCE)
                    .required(LootContextParams.TOOL)
                    .optional(RITUAL_TIER)
                    .optional(LootContextParams.DIRECT_ATTACKING_ENTITY)
                    .optional(LootContextParams.ATTACKING_ENTITY)
    );
    public static final LootContextParamSet MAGIC_ITEM = LootContextParamSets.register(
            "magic_item", p_344705_ -> p_344705_.required(LootContextParams.TOOL)
                    .required(LootContextParams.THIS_ENTITY)
                    .optional(RITUAL_TIER)
    );
    public static final LootContextParamSet MAGIC_ENTITY = LootContextParamSets.register(
            "magic_entity",
            p_344707_ -> p_344707_.required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ORIGIN)
                    .optional(RITUAL_TIER)
    );

    private static <T> LootContextParam<T> create(String id) {
        return new LootContextParam<>(ResourceLocation.withDefaultNamespace(id));
    }
}
