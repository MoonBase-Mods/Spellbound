package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.acquisition.bosses.DynamicLevelSpawnData;
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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public interface SBPuzzleConfigs {
    ResourceKey<PuzzleConfiguration> FLICKER = key("flicker");
    ResourceKey<PuzzleConfiguration> CURSED_RUNE = key("cursed_rune");

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
                                                                                        TagPredicate.is(DamageTypeTags.IS_FIRE)
                                                                                )
                                                                        ))
                                                        )
                                                        .cooldown(5))
                                        .maxResetCount(3)
                                        .spawnData(
                                                DynamicLevelSpawnData.Builder.create()
                                                        .playerOffset(new Vec3(-14, -5, 0))
                                                        .spellOffset(new Vec3(14, -4, 0))
                                        )
                                        .addRule(DungeonRules.NO_INTERACT)
                                        .addRule(DungeonRules.NO_BUILDING)
                                        .addRule(DungeonRules.NO_FLYING)
                                        .addRule(DungeonRules.NO_PVE_OR_PVP)
                                        .addRule(DungeonRules.NO_SPELL_CASTING)
                        ));
        register(context,
                CURSED_RUNE,
                PuzzleConfiguration.Builder.configuration()
                        .addPuzzle(
                                PuzzleDefinition.Builder
                                        .define(CommonClass.customLocation("cursed_rune"))
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
                                                                                        TagPredicate.is(DamageTypeTags.IS_FIRE)
                                                                                )
                                                                        ))
                                                        )
                                                        .cooldown(5))
                                        .maxResetCount(3)
                                        .spawnData(
                                                DynamicLevelSpawnData.Builder.create()
                                                        .playerOffset(new Vec3(-14, -5, 0))
                                                        .spellOffset(new Vec3(14, -4, 0))
                                        )
                                        .addRule(DungeonRules.NO_INTERACT)
                                        .addRule(DungeonRules.NO_BUILDING)
                                        .addRule(DungeonRules.NO_FLYING)
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
