package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionRequirements;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionRewards;
import com.ombremoon.spellbound.common.magic.acquisition.divine.SpellAction;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.*;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.ConsumeItemTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.CuredZombieVillagerTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.PlayerHurtEntityTrigger;
import com.ombremoon.spellbound.common.world.entity.projectile.ThrownPhial;
import com.ombremoon.spellbound.common.world.item.PhialItem;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.List;

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

    ResourceKey<SpellAction> HEAL_MOB_TO_FULL = key("healing_touch/heal_mob_to_full");
    ResourceKey<SpellAction> USE_BLESSED_BANDAGES = key("healing_touch/use_blessed_bandages");
    ResourceKey<SpellAction> USE_DIVINE_PHIAL = key("healing_touch/use_divine_phial");
    ResourceKey<SpellAction> CURE_ZOMBIE_VILLAGER = key("blessing/cure_zombie_villager");
    ResourceKey<SpellAction> BLESS_SHRINE = key("blessing/bless_shrine");
    ResourceKey<SpellAction> BESTOW_CHARM = key("blessing/bestow_charm");
    ResourceKey<SpellAction> ANOINT_WEAPON = key("smite/anoint_weapon");
    ResourceKey<SpellAction> DEFLECT_UNDEAD_ARROW = key("smite/deflect_undead_arrow");
    ResourceKey<SpellAction> CURSE_WEAPON = key("smite/curse_weapon");
    ResourceKey<SpellAction> CURSE_ENEMY_WITH_PHIAL = key("siphon/curse_enemy_with_phial");
    ResourceKey<SpellAction> USE_HALF_FILLED_SIPHON = key("siphon/use_half_filled_siphon");
    ResourceKey<SpellAction> CURSE_SHRINE = key("siphon/curse_shrine");
    ResourceKey<SpellAction> DECORATE_SHRINE = key("healing_blossom/decorate_shrine");
    ResourceKey<SpellAction> PURIFY_WITHER_ROSE = key("healing_blossom/purify_wither_rose");
    ResourceKey<SpellAction> GROW_AMBROSIA_BUSH = key("healing_blossom/grow_ambrosia_bush");
    ResourceKey<SpellAction> KILL_VILLAGER = key("siphon/kill_villager");

    static void bootstrap(BootstrapContext<SpellAction> context) {
        //Spell Actions
        register(context,
                HEAL_MOB_TO_FULL,
                addMobsToHeal(SpellAction.Builder.action(), NON_HOSTILE_MOBS)
                        .requirements(ActionRequirements.Strategy.OR)
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.HEALING_TOUCH.get())
                                .addExperience(2))
                        .cooldown(6000));
        register(context,
                USE_BLESSED_BANDAGES,
                SpellAction.Builder.action()
                        .addCriterion("use_blessed_bandages", ConsumeItemTrigger.TriggerInstance
                                .usedItem(SBItems.BLESSED_BANDAGES.get()))
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.HEALING_TOUCH.get())
                                .addJudgement(1)
                                .addBookScrap(SBPageScraps.USE_BLESSED_BANDAGES, SBPageScraps.USE_BLESSED_BANDAGES_LORE))
                        .cooldown(6000));
        register(context,
                USE_DIVINE_PHIAL,
                SpellAction.Builder.action()
                        .addCriterion("use_divine_phial", SkillBuffTrigger.TriggerInstance
                                .buffSelf(SkillBuffPredicate.isSkill(ThrownPhial.DIVINE_BUFF)))
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.HEALING_TOUCH.get())
                                .addJudgement(1)
                                .addBookScrap(SBPageScraps.USE_PHIAL, SBPageScraps.USE_PHIAL_LORE))
                        .cooldown(6000));
        register(context,
                BLESS_SHRINE,
                SpellAction.Builder.action()
                        .addCriterion("bless_shrine", DivineTrigger.TriggerInstance
                                .blessShrine())
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.BLESSING.get())
                                .addJudgement(1)
                                .addBookScrap(SBPageScraps.BLESS_SHRINE, SBPageScraps.BLESS_SHRINE_LORE))
                        .cooldown(6000));
        register(context,
                CURE_ZOMBIE_VILLAGER,
                SpellAction.Builder.action()
                        .addCriterion("cured_zombie", CuredZombieVillagerTrigger.TriggerInstance
                                .curedZombieVillager())
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.BLESSING.get())
                                .addJudgement(2)
                                .addExperience(5)
                                .addBookScrap(SBPageScraps.CURE_VILLAGER, SBPageScraps.CURE_VILLAGER_LORE)));
        register(context,
                BESTOW_CHARM,
                SpellAction.Builder.action()
                        .addCriterion("bestow_charm", ItemUsedOnEntityTrigger.TriggerInstance
                                .itemUsedOnEntity(ItemPredicate.Builder.item()
                                        .of(SBItems.DIVINE_CHARM.get())))
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.BLESSING.get())
                                .addJudgement(1)
                                .addBookScrap(SBPageScraps.BESTOW_CHARM, SBPageScraps.BESTOW_CHARM_LORE))
                        .cooldown(6000));
        register(context,
                ANOINT_WEAPON,
                SpellAction.Builder.action()
                        .addCriterion("anoint_weapon", UseMagicCauldronTrigger.TriggerInstance
                                .blessItem(ItemPredicate.Builder.item()
                                        .of(SBTags.Items.IMBUEABLE),
                                        PhialItem.PhialType.DIVINE))
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.SMITE.get())
                                .addJudgement(1)
                                .addBookScrap(SBPageScraps.ANOINT_WEAPON, SBPageScraps.ANOINT_WEAPON_LORE))
                        .cooldown(6000));
        register(context,
                DEFLECT_UNDEAD_ARROW,
                SpellAction.Builder.action()
                        .addCriterion("deflect_undead_arrow", PlayerHurtEntityTrigger.TriggerInstance
                                .deflectAndHitProjectile(EntityPredicate.Builder.entity()
                                        .of(EntityType.SKELETON)))
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.SMITE.get())
                                .addJudgement(1)
                                .addBookScrap(SBPageScraps.DEFLECT_UNDEAD_ARROW, SBPageScraps.DEFLECT_UNDEAD_ARROW_LORE))
                        .cooldown(6000));
        register(context,
                CURSE_WEAPON,
                SpellAction.Builder.action()
                        .addCriterion("curse_weapon", UseMagicCauldronTrigger.TriggerInstance
                                .blessItem(ItemPredicate.Builder.item()
                                                .of(SBTags.Items.IMBUEABLE),
                                        PhialItem.PhialType.CURSED))
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.SMITE.get())
                                .addJudgement(-1)
                                .addBookScrap(SBPageScraps.CURSE_WEAPON, SBPageScraps.CURSE_WEAPON_LORE))
                        .cooldown(6000));
        register(context,
                CURSE_ENEMY_WITH_PHIAL,
                SpellAction.Builder.action()
                        .addCriterion("curse_enemy_with_phial", PlayerHurtEntityTrigger.TriggerInstance
                                .playerHurtEntityWithDamage(DamagePredicate.Builder.damageInstance()
                                        .type(DamageSourcePredicate.Builder.damageType()
                                                .direct(EntityPredicate.Builder.entity()
                                                        .of(SBEntities.PHIAL.get())))))
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.SIPHON.get())
                                .addJudgement(-1)
                                .addBookScrap(SBPageScraps.CURSE_ENEMY_WITH_PHIAL, SBPageScraps.CURSE_ENEMY_WITH_PHIAL_LORE))
                        .cooldown(6000));
        register(context,
                USE_HALF_FILLED_SIPHON,
                SpellAction.Builder.action()
                        .addCriterion("use_half_filled_siphon", DivineTrigger.TriggerInstance
                                .useHalfFilledSiphon())
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.SIPHON.get())
                                .addJudgement(-1)
                                .addBookScrap(SBPageScraps.USE_HALF_FILLED_SIPHON, SBPageScraps.USE_HALF_FILLED_SIPHON_LORE))
                        .cooldown(6000));
        register(context,
                CURSE_SHRINE,
                SpellAction.Builder.action()
                        .addCriterion("curse_shrine", DivineTrigger.TriggerInstance
                                .curseShrine())
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.SIPHON.get())
                                .addJudgement(-1)
                                .addBookScrap(SBPageScraps.CURSE_SHRINE, SBPageScraps.CURSE_SHRINE_LORE))
                        .cooldown(6000));
        register(context,
                DECORATE_SHRINE,
                SpellAction.Builder.action()
                        .addCriterion("decorate_shrine", DivineTrigger.TriggerInstance
                                .decoratedShrine())
                        .rewards(ActionRewards.Builder
                                .spell(SBSpells.HEALING_BLOSSOM.get())
                                .addJudgement(5)
                                .addBookScrap(SBPageScraps.DECORATE_SHRINE, SBPageScraps.DECORATE_SHRINE_LORE))
                        .cooldown(24000));

        //General Actions
        register(context,
                KILL_VILLAGER,
                SpellAction.Builder.action()
                        .addCriterion("kill_villager", PlayerKillEntityTrigger.TriggerInstance
                                .playerKilledEntity(
                                        EntityPredicate.Builder.entity()
                                                .of(EntityType.VILLAGER)))
                        .rewards(ActionRewards.Builder
                                .judgementGranted(-2)));
    }

    private static SpellAction.Builder addMobsToHeal(SpellAction.Builder builder, List<EntityType<?>> mobs) {
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

    private static void register(BootstrapContext<SpellAction> context, ResourceKey<SpellAction> key, SpellAction.Builder builder) {
        context.register(key, builder.build());
    }

    private static ResourceKey<SpellAction> key(String name) {
        return ResourceKey.create(Keys.DIVINE_ACTION, CommonClass.customLocation(name));
    }
}
