package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.world.loot.functions.SetSpellFunction;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SBLootFunctions {
    public static final DeferredRegister<LootItemFunctionType<?>> FUNCTION_TYPE = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, Constants.MOD_ID);

    public static final Supplier<LootItemFunctionType<SetSpellFunction>> SET_SPELL = FUNCTION_TYPE.register("set_spell", () -> new LootItemFunctionType<>(SetSpellFunction.CODEC));

    public static void register(IEventBus modEventBus) {
        FUNCTION_TYPE.register(modEventBus);
    }
}
