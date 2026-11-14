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
    public static final Supplier<ConsumeItemTrigger> CONSUME_ITEM = register("consume_item", new ConsumeItemTrigger());
    public static final Supplier<CuredZombieVillagerTrigger> CURED_ZOMBIE_VILLAGER = register("cured_zombie_villager", new CuredZombieVillagerTrigger());
    public static final Supplier<DivineTrigger> BLESS_SHRINE = register("bless_shrine", new DivineTrigger());
    public static final Supplier<DivineTrigger> CURSE_SHRINE = register("curse_shrine", new DivineTrigger());
    public static final Supplier<DivineTrigger> USE_HALF_FILLED_SIPHON = register("use_half_filled_siphon", new DivineTrigger());
    public static final Supplier<DivineTrigger> DECORATED_SHRINE = register("decorated_shrine", new DivineTrigger());
    public static final Supplier<EntityHurtPlayerTrigger> ENTITY_HURT_PLAYER = register("entity_hurt_player", new EntityHurtPlayerTrigger());
    public static final Supplier<PlayerHurtEntityTrigger> PLAYER_HURT_ENTITY = register("player_hurt_entity", new PlayerHurtEntityTrigger());
    public static final Supplier<PlayerHurtEntityTrigger> DEFLECT_AND_HIT_PROJECTILE = register("deflect_and_hit_projectile", new PlayerHurtEntityTrigger());
    public static final Supplier<InteractWithBlockTrigger> INTERACT_WITH_BLOCK = register("interact_with_block", new InteractWithBlockTrigger());
    public static final Supplier<LearnSpellTrigger> LEARN_SPELL = register("learn_spell", new LearnSpellTrigger());
    public static final Supplier<SkillBuffTrigger> RECEIVED_SKILL_BUFF = register("received_skill_buff", new SkillBuffTrigger());
    public static final Supplier<SkillBuffTrigger> BUFF_SELF = register("buff_self", new SkillBuffTrigger());
    public static final Supplier<ItemUsedOnEntityTrigger> USE_ITEM_ON_ENTITY = register("use_item_on_entity", new ItemUsedOnEntityTrigger());
    public static final Supplier<UseMagicCauldronTrigger> USE_HOLY_CAULDRON = register("use_holy_cauldron", new UseMagicCauldronTrigger());
    public static final Supplier<InventoryChangeTrigger> INVENTORY_CHANGED = register("inventory_changed", new InventoryChangeTrigger());
    public static final Supplier<RecipeCraftedTrigger> RECIPE_CRAFTED = register("recipe_crafted", new RecipeCraftedTrigger());
    public static final Supplier<TameAnimalTrigger> TAME_ANIMAL = register("tame_animal", new TameAnimalTrigger());

    public static <T extends ActionTrigger<?>> Supplier<T> register(String name, T trigger) {
        return TRIGGERS.register(name, () -> trigger);
    }

    public static void register(IEventBus modEventBus) {
        TRIGGERS.register(modEventBus);
    }
}
