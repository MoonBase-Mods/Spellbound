package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBPageScraps;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.init.SBTags;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionHolder;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionRequirements;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionRewards;
import com.ombremoon.spellbound.common.magic.acquisition.divine.SpellAction;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.*;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.InventoryChangeTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.RecipeCraftedTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.TameAnimalTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.guides.triggers.LearnSpellTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.guides.triggers.SpellPredicate;
import com.ombremoon.spellbound.datagen.provider.PageScrapProvider;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModPageScrapProvider extends PageScrapProvider {
    public ModPageScrapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<ActionHolder> writer) {
        SpellAction.Builder.action()
                .addCriterion("learn_spell",
                        LearnSpellTrigger.Instance.learnSpell(
                                SpellPredicate.spell(SBSpells.SOLAR_RAY.get())))
                .rewards(ActionRewards.Builder.bookScrap(SBPageScraps.UNLOCKED_SOLAR_RAY))
                .save(writer, SBPageScraps.UNLOCKED_SOLAR_RAY);

        SpellAction.Builder.action()
                .addCriterion("acquire_blessed_bandages", InventoryChangeTrigger.TriggerInstance
                        .hasItems(SBItems.BLESSED_BANDAGES.get()))
                .rewards(ActionRewards.Builder
                        .bookScrap(SBPageScraps.USE_BLESSED_BANDAGES_LORE))
                .save(writer, SBPageScraps.USE_BLESSED_BANDAGES_LORE);
        SpellAction.Builder.action()
                .addCriterion("acquire_divine_phial", InventoryChangeTrigger.TriggerInstance
                        .hasItems(SBItems.DIVINE_PHIAL.get()))
                .rewards(ActionRewards.Builder
                        .bookScrap(SBPageScraps.USE_PHIAL_LORE))
                .save(writer, SBPageScraps.USE_PHIAL_LORE);
        SpellAction.Builder.action()
                .addCriterion("acquire_divine_shrine", InventoryChangeTrigger.TriggerInstance
                        .hasItems(ItemPredicate.Builder.item()
                                .of(SBTags.Items.DIVINE_SHRINE)))
                .addCriterion("use_divine_shrine", InteractWithBlockTrigger.TriggerInstance
                        .interactedWithBlock(BlockPredicate.Builder.block()
                                .of(SBTags.Blocks.DIVINE_SHRINE)
                                .build()))
                .requirements(ActionRequirements.Strategy.OR)
                .rewards(ActionRewards.Builder
                        .bookScrap(SBPageScraps.BLESS_SHRINE_LORE))
                .save(writer, SBPageScraps.BLESS_SHRINE_LORE);
        SpellAction.Builder.action()
                .addCriterion("kill_zombie_villager", PlayerKillEntityTrigger.TriggerInstance
                        .playerKilledEntity(EntityPredicate.Builder.entity()
                                .of(EntityType.ZOMBIE_VILLAGER)))
                .rewards(ActionRewards.Builder
                        .bookScrap(SBPageScraps.CURE_VILLAGER_LORE))
                .save(writer, SBPageScraps.CURE_VILLAGER_LORE);
        SpellAction.Builder.action()
                .addCriterion("craft_bestow_charm", RecipeCraftedTrigger.TriggerInstance
                        .craftedItem(CommonClass.customLocation("divine_charm")))
                .addCriterion("tame_an_animal", TameAnimalTrigger.TriggerInstance
                        .tamedAnimal())
                .requirements(ActionRequirements.Strategy.OR)
                .rewards(ActionRewards.Builder
                        .bookScrap(SBPageScraps.BESTOW_CHARM_LORE))
                .save(writer, SBPageScraps.BESTOW_CHARM_LORE);
        SpellAction.Builder.action()
                .addCriterion("kill_undead_with_holy_damage", PlayerKillEntityTrigger.TriggerInstance
                        .playerKilledEntity(
                                EntityPredicate.Builder.entity().of(EntityTypeTags.UNDEAD),
                                DamageSourcePredicate.Builder.damageType()
                                        .tag(TagPredicate
                                                .is(SBTags.DamageTypes.HOLY_DAMAGE)).build()))
                .rewards(ActionRewards.Builder
                        .bookScrap(SBPageScraps.ANOINT_WEAPON_LORE))
                .save(writer, SBPageScraps.ANOINT_WEAPON_LORE);
        SpellAction.Builder.action()
                .addCriterion("acquired_magic_shield", InventoryChangeTrigger.TriggerInstance
                        .hasItems(SBItems.MAGIC_SHIELD.get()))
                .rewards(ActionRewards.Builder
                        .bookScrap(SBPageScraps.DEFLECT_UNDEAD_ARROW_LORE))
                .save(writer, SBPageScraps.DEFLECT_UNDEAD_ARROW_LORE);
        SpellAction.Builder.action()
                .addCriterion("acquired_ominous_bottle", InventoryChangeTrigger.TriggerInstance
                        .hasItems(Items.OMINOUS_BOTTLE))
                .addCriterion("acquired_cursed_phial", InventoryChangeTrigger.TriggerInstance
                        .hasItems(SBItems.CURSED_PHIAL.get()))
                .requirements(ActionRequirements.Strategy.OR)
                .rewards(ActionRewards.Builder
                        .bookScrap(SBPageScraps.CURSE_WEAPON_LORE))
                .save(writer, SBPageScraps.CURSE_WEAPON_LORE);
        SpellAction.Builder.action()
                .addCriterion("acquired_cursed_phial", InventoryChangeTrigger.TriggerInstance
                        .hasItems(SBItems.CURSED_PHIAL.get()))
                .rewards(ActionRewards.Builder
                        .bookScrap(SBPageScraps.CURSE_ENEMY_WITH_PHIAL_LORE))
                .save(writer, SBPageScraps.CURSE_ENEMY_WITH_PHIAL_LORE);
        SpellAction.Builder.action()
                .addCriterion("craft_mana_siphon", InventoryChangeTrigger.TriggerInstance
                        .hasItems(SBItems.CURSED_PHIAL.get()))
                .rewards(ActionRewards.Builder
                        .bookScrap(SBPageScraps.USE_HALF_FILLED_SIPHON_LORE))
                .save(writer, SBPageScraps.USE_HALF_FILLED_SIPHON_LORE);
        SpellAction.Builder.action()
                .addCriterion("acquired_cursed_phial", InventoryChangeTrigger.TriggerInstance
                        .hasItems(SBItems.CURSED_PHIAL.get()))
                .rewards(ActionRewards.Builder
                        .bookScrap(SBPageScraps.CURSE_SHRINE_LORE))
                .save(writer, SBPageScraps.CURSE_SHRINE_LORE);
    }

    @Override
    public String getName() {
        return "Page Scraps";
    }
}
