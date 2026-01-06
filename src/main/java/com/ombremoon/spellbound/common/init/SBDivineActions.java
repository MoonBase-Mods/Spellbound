package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionHolder;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionRequirements;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionRewards;
import com.ombremoon.spellbound.common.magic.acquisition.divine.DivineAction;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.CuredZombieVillagerTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.HealActionTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.KillActionTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.SpecialTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.datagen.provider.guide_builders.PageBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public interface SBDivineActions {
    List<EntityType<?>> NON_HOSTILE_MOBS = Arrays.asList(
            EntityType.ALLAY,
            EntityType.ARMADILLO,
            EntityType.AXOLOTL,
            EntityType.BAT,
            EntityType.BEE,
            EntityType.CAMEL,
            EntityType.CAT,
            EntityType.CHICKEN,
            EntityType.COD,
            EntityType.COW,
            EntityType.DOLPHIN,
            EntityType.DONKEY,
            EntityType.FOX,
            EntityType.FROG,
            EntityType.GLOW_SQUID,
            EntityType.GOAT,
            EntityType.HORSE,
            EntityType.IRON_GOLEM,
            EntityType.LLAMA,
            EntityType.MOOSHROOM,
            EntityType.MULE,
            EntityType.OCELOT,
            EntityType.PANDA,
            EntityType.PARROT,
            EntityType.PIG,
            EntityType.POLAR_BEAR,
            EntityType.PUFFERFISH,
            EntityType.RABBIT,
            EntityType.SALMON,
            EntityType.SHEEP,
            EntityType.SNIFFER,
            EntityType.SQUID,
            EntityType.STRIDER,
            EntityType.TADPOLE,
            EntityType.TRADER_LLAMA,
            EntityType.TROPICAL_FISH,
            EntityType.TURTLE,
            EntityType.VILLAGER,
            EntityType.WANDERING_TRADER,
            EntityType.WOLF
    );

    ResourceKey<DivineAction> HEAL_MOB_TO_FULL = key("healing_touch/heal_mob_to_full");
    ResourceKey<DivineAction> USE_BLESSED_BANDAGES = key("healing_touch/use_blessed_bandages");
    ResourceKey<DivineAction> BLESS_SHRINE = key("healing_touch/bless_shrine");
    ResourceKey<DivineAction> DECORATE_SHRINE = key("healing_blossom/decorate_shrine");
    ResourceKey<DivineAction> PURIFY_WITHER_ROSE = key("healing_blossom/purify_wither_rose");
    ResourceKey<DivineAction> GROW_AMBROSIA_BUSH = key("healing_blossom/grow_ambrosia_bush");
    ResourceKey<DivineAction> CURE_ZOMBIE_VILLAGER = key("blessing/cure_zombie_villager");
    ResourceKey<DivineAction> KILL_VILLAGER = key("unidentified/kill_villager");

    static void bootstrap(BootstrapContext<DivineAction> context) {
        register(context,
                HEAL_MOB_TO_FULL,
                addMobsToHeal(DivineAction.Builder.divineAction(), NON_HOSTILE_MOBS)
                        .requirements(ActionRequirements.Strategy.OR)
                        .rewards(ActionRewards.Builder.spell(SBSpells.HEALING_TOUCH.get()).addExperience(10)));
        register(context,
                CURE_ZOMBIE_VILLAGER,
                DivineAction.Builder.divineAction()
                        .addCriterion("cured_zombie", CuredZombieVillagerTrigger.TriggerInstance.curedZombieVillager())
                        .rewards(ActionRewards.Builder.spell(SBSpells.SOLAR_RAY/*BLESSING*/.get()).addExperience(10)));
        register(context,
                KILL_VILLAGER,
                DivineAction.Builder.divineAction()
                        .addCriterion("kill_villager", KillActionTrigger.Instance.playerKilledVillager(
                                EntityPredicate.Builder.entity(),
                                MinMaxBounds.Ints.exactly(5)))
                        .rewards(ActionRewards.Builder.spell(SBSpells.STORM_RIFT/*SIPHON*/.get()).addExperience(20)));
        register(context,
                DECORATE_SHRINE,
                DivineAction.Builder.divineAction()
                        .addCriterion("decorate_shrine", SpecialTrigger.TriggerInstance.decoratedShrine())
                        .rewards(ActionRewards.Builder.spell(SBSpells.HEALING_BLOSSOM.get()).addExperience(20)));
    }

    private static DivineAction.Builder addMobsToHeal(DivineAction.Builder builder, List<EntityType<?>> mobs) {
        mobs.forEach(entityType -> {
            builder.addCriterion(
                    BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString(),
                    HealActionTrigger.Instance.healedToFull(EntityPredicate.Builder.entity().of(entityType)));
        });

        return builder;
    }

    private static ResourceLocation loc(String path) {
        return CommonClass.customLocation(path);
    }

    private static void register(BootstrapContext<DivineAction> context, ResourceKey<DivineAction> key, DivineAction.Builder builder) {
        context.register(key, builder.build());
    }

    private static ResourceKey<DivineAction> key(String name) {
        return ResourceKey.create(Keys.DIVINE_ACTION, CommonClass.customLocation(name));
    }
}
