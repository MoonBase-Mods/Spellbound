package com.ombremoon.spellbound.common.init;

import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.BossFightInstance;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.EntityBasedBossFight;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class SBBossFights {
    public static final ResourceKey<Registry<MapCodec<? extends BossFightInstance<?, ?>>>> BOSS_FIGHT_REGISTRY_KEY = ResourceKey.createRegistryKey(CommonClass.customLocation("boss_fight"));
    public static final Registry<MapCodec<? extends BossFightInstance<?, ?>>> REGISTRY = new RegistryBuilder<>(BOSS_FIGHT_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<MapCodec<? extends BossFightInstance<?, ?>>> BOSS_FIGHTS = DeferredRegister.create(REGISTRY, Constants.MOD_ID);

    public static final Supplier<MapCodec<EntityBasedBossFight.Instance>> DEFAULT = BOSS_FIGHTS.register("default", () -> EntityBasedBossFight.Instance.CODEC);

    public static void register(IEventBus modEventBus) {
        BOSS_FIGHTS.register(modEventBus);
    }
}
