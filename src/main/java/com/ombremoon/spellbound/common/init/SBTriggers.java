package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.*;
import com.ombremoon.spellbound.common.magic.acquisition.guides.triggers.LearnSpellTrigger;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class SBTriggers {
    public static final ResourceKey<Registry<ActionTrigger<?>>> ACTION_TRIGGERS_REGISTRY_KEY = ResourceKey.createRegistryKey(CommonClass.customLocation("action_triggers"));
    public static final Registry<ActionTrigger<?>> REGISTRY = new RegistryBuilder<>(ACTION_TRIGGERS_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<ActionTrigger<?>> TRIGGERS = DeferredRegister.create(REGISTRY, Constants.MOD_ID);

    public static final Supplier<PlayerKillEntityTrigger> PLAYER_KILL = register("player_kill", new PlayerKillEntityTrigger());
    public static final Supplier<PlayerKillEntityTrigger> PLAYER_KILLED = register("player_killed", new PlayerKillEntityTrigger());
    public static final Supplier<PlayerKillEntityTrigger> KILL_UNDEAD = register("kill_undead", new PlayerKillEntityTrigger());
    public static final Supplier<PlayerKillEntityTrigger> KILL_VILLAGER = register("kill_villager", new PlayerKillEntityTrigger());
    public static final Supplier<HealActionTrigger> ENTITY_HEALED = register("entity_healed", new HealActionTrigger());
    public static final Supplier<HealActionTrigger> HEAL_TO_FULL = register("heal_to_full", new HealActionTrigger());
    public static final Supplier<CuredZombieVillagerTrigger> CURED_ZOMBIE_VILLAGER = register("cured_zombie_villager", new CuredZombieVillagerTrigger());
    public static final Supplier<SpecialTrigger> DECORATED_SHRINE = register("decorated_shrine", new SpecialTrigger());
    public static final Supplier<PlayerHurtTrigger> PLAYER_HURT = register("player_hurt", new PlayerHurtTrigger());
    public static final Supplier<InteractWithBlockTrigger> INTERACT_WITH_BLOCK = register("interact_with_block", new InteractWithBlockTrigger());
    public static final Supplier<LearnSpellTrigger> LEARN_SPELL = register("learn_spell", new LearnSpellTrigger());

    public static <T extends ActionTrigger<?>> Supplier<T> register(String name, T trigger) {
        return TRIGGERS.register(name, () -> trigger);
    }

    public static void register(IEventBus modEventBus) {
        TRIGGERS.register(modEventBus);
    }
}
