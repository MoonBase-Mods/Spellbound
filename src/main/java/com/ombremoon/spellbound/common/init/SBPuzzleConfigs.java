package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.acquisition.deception.DungeonRules;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleConfiguration;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleDefinition;
import com.ombremoon.spellbound.common.magic.acquisition.divine.SpellAction;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.InteractWithBlockTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.PlayerHurtTrigger;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;

public interface SBPuzzleConfigs {
    ResourceKey<PuzzleConfiguration> FLICKER = key("flicker");

    static void bootstrap(BootstrapContext<PuzzleConfiguration> context) {
        register(context,
                FLICKER,
                PuzzleConfiguration.Builder.configuration()
                        .addPuzzle(
                                PuzzleDefinition.Builder
                                        .define(CommonClass.customLocation("flicker"))
                                        .withObjective(
                                                SpellAction.Builder.action()
                                                        .addCriterion(
                                                                "interact_with_altar",
                                                                InteractWithBlockTrigger.Instance
                                                                        .interactedWithBlock(BlockPredicate.Builder.block().of(Blocks.CHEST).build())
                                                        )
                                        )
                                        .resetOn(
                                                SpellAction.Builder.action()
                                                        .addCriterion(
                                                                "hurt_by_magic",
                                                                PlayerHurtTrigger.Instance
                                                                        .entityHurtPlayer(DamagePredicate.Builder.damageInstance().type(
                                                                                DamageSourcePredicate.Builder.damageType().tag(
                                                                                        TagPredicate.is(SBTags.DamageTypes.SPELL_DAMAGE)
                                                                                )
                                                                        ))
                                                        ))
                                        .withAlternativeConfig(CommonClass.customLocation("flicker_1"))
                                        .withAlternativeConfig(CommonClass.customLocation("flicker_2"))
                                        .withAlternativeConfig(CommonClass.customLocation("flicker_3"))
                                        .addRule(DungeonRules.NO_BUILDING)
                                        .addRule(DungeonRules.NO_FLYING)
                                        .addRule(DungeonRules.NO_PVE)
                                        .addRule(DungeonRules.NO_PVP)
                                        .addRule(DungeonRules.NO_PVE_OR_PVP)
                                        .addRule(DungeonRules.NO_SPELL_CASTING)
                        ));
    }

    private static void register(BootstrapContext<PuzzleConfiguration> context, ResourceKey<PuzzleConfiguration> key, PuzzleConfiguration.Builder builder) {
        context.register(key, builder.build());
    }

    private static ResourceKey<PuzzleConfiguration> key(String name) {
        return ResourceKey.create(Keys.PUZZLE_CONFIG, CommonClass.customLocation(name));
    }
}
