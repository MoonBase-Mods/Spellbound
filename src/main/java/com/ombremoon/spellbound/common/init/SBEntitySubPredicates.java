package com.ombremoon.spellbound.common.init;

import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.AttributePredicate;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SBEntitySubPredicates {
    public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> ENTITY_SUB_PREDICATES = DeferredRegister.create(Registries.ENTITY_SUB_PREDICATE_TYPE, Constants.MOD_ID);

    public static final Supplier<MapCodec<AttributePredicate>> ATTRIBUTE = ENTITY_SUB_PREDICATES.register("attribute", () -> AttributePredicate.CODEC);

    public static void register(IEventBus modEventBus) {
        ENTITY_SUB_PREDICATES.register(modEventBus);
    }
}
