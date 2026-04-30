package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.acquisition.bosses.DynamicLevelSpawnData;
import com.ombremoon.spellbound.common.magic.acquisition.deception.RuleType;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleConfiguration;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleDefinition;
import com.ombremoon.spellbound.common.magic.acquisition.deception.ResetFlags;
import com.ombremoon.spellbound.common.magic.acquisition.divine.SpellAction;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.InteractWithBlockTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.PlayerHurtTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.triggers.PlayerKillEntityTrigger;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

public interface SBPuzzleConfigs {
    ResourceKey<PuzzleConfiguration> FLICKER = key("flicker");
    ResourceKey<PuzzleConfiguration> SHADOW_VEIL = key("shadow_veil");
    ResourceKey<PuzzleConfiguration> CURSED_RUNE = key("cursed_rune");
    ResourceKey<PuzzleConfiguration> NIGHTBLADE = key("nightblade");

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
                                                                        .interactedWithBlock(BlockPredicate.Builder.block().of(SBBlocks.DARK_ALTAR.get()).build())
                                                        )
                                        )
                                        .resetOn(
                                                SpellAction.Builder.action()
                                                        .addCriterion(
                                                                "hurt_by_fall",
                                                                PlayerHurtTrigger.Instance
                                                                        .entityHurtPlayer(DamagePredicate.Builder.damageInstance().type(
                                                                                DamageSourcePredicate.Builder.damageType().tag(
                                                                                        TagPredicate.is(DamageTypeTags.IS_FALL)
                                                                                )
                                                                        ))
                                                        )
                                                        .cooldown(5))
                                        .maxResetCount(3)
                                        .spawnData(
                                                DynamicLevelSpawnData.Builder.create()
                                                        .playerOffset(new Vec3(-14, -5, 0))
                                                        .playerRotation(-90F)
                                                        .spellOffset(new Vec3(14, -4, 0))
                                        )
                                        .addRule(RuleType.NO_INTERACT)
                                        .addRule(RuleType.NO_BUILDING)
                                        .addRule(RuleType.NO_FLYING)
                                        .addRule(RuleType.NO_PVE_OR_PVP)
                                        .addRule(RuleType.NO_SPELL_CASTING)
                        )
        );
        register(context,
                SHADOW_VEIL,
                PuzzleConfiguration.Builder.configuration()
                        .addPuzzle(
                                PuzzleDefinition.Builder
                                        .define(CommonClass.customLocation("shadow_veil"))
                                        .withObjective(
                                                SpellAction.Builder.action()
                                                        .addCriterion(
                                                                "interact_with_altar",
                                                                InteractWithBlockTrigger.Instance
                                                                        .interactedWithBlock(BlockPredicate.Builder.block().of(SBBlocks.DARK_ALTAR.get()).build())
                                                        )
                                        )
                                        .resetOn(
                                                SpellAction.Builder.action()
                                                        .addCriterion(
                                                                "hurt_by_fall",
                                                                PlayerHurtTrigger.Instance
                                                                        .entityHurtPlayer(DamagePredicate.Builder.damageInstance().type(
                                                                                DamageSourcePredicate.Builder.damageType().tag(
                                                                                        TagPredicate.is(DamageTypeTags.IS_FALL)
                                                                                )
                                                                        ))
                                                        )
                                                        .cooldown(5))
                                        .maxResetCount(3)
                                        .spawnData(
                                                DynamicLevelSpawnData.Builder.create()
                                                        .playerOffset(new Vec3(18, 1, 0))
                                                        .playerRotation(90F)
                                                        .spellOffset(new Vec3(-18, 2, 0))
                                        )
                                        .addRule(RuleType.NO_INTERACT)
                                        .addRule(RuleType.NO_BUILDING)
                                        .addRule(RuleType.NO_FLYING)
                                        .addRule(RuleType.NO_PVE_OR_PVP)
                                        .addRule(RuleType.NO_SPELL_CASTING)
                        )
        );
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
                                                                        .interactedWithBlock(BlockPredicate.Builder.block().of(SBBlocks.DARK_ALTAR.get()).build())
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
                                                        )
                                                        .cooldown(5))
                                        .resetOn(
                                                SpellAction.Builder.action()
                                                        .addCriterion(
                                                                "hurt_by_fall",
                                                                PlayerHurtTrigger.Instance
                                                                        .entityHurtPlayer(DamagePredicate.Builder.damageInstance().type(
                                                                                DamageSourcePredicate.Builder.damageType().tag(
                                                                                        TagPredicate.is(DamageTypeTags.IS_FALL)
                                                                                )
                                                                        ))
                                                        )
                                                        .cooldown(5))
                                        .maxResetCount(3)
                                        .spawnData(
                                                DynamicLevelSpawnData.Builder.create()
                                                        .playerOffset(new Vec3(-18, -5, 0))
                                                        .playerRotation(-90F)
                                                        .spellOffset(new Vec3(18, -4, 0))
                                                        .spellOffset(new Vec3(18, -4, 0))
                                        )
                                        .addRuleWithException(RuleType.NO_INTERACT, Items.SNOWBALL)
                                        .addRule(RuleType.NO_BUILDING)
                                        .addRule(RuleType.NO_FLYING)
                                        .addRule(RuleType.NO_PVE_OR_PVP)
                                        .addRule(RuleType.NO_SPELL_CASTING)
                                        .withFlag(ResetFlags.RESET_INVENTORY)
                        ));
        register(context,
                NIGHTBLADE,
                PuzzleConfiguration.Builder.configuration()
                        .addPuzzle(
                                PuzzleDefinition.Builder
                                        .define(CommonClass.customLocation("nightblade"))
                                        .withObjective(
                                                SpellAction.Builder.action()
                                                        .addCriterion(
                                                                "player_killed_nightblade",
                                                                PlayerKillEntityTrigger.Instance
                                                                        .playerKilledEntity(
                                                                                EntityPredicate.Builder.entity().of(SBEntities.MINI_MUSHROOM.get())
                                                                        )
                                                        )
                                        )
                                        .resetOn(
                                                SpellAction.Builder.action()
                                                        .addCriterion(
                                                                "hurt_by_fall",
                                                                PlayerHurtTrigger.Instance
                                                                        .entityHurtPlayer(DamagePredicate.Builder.damageInstance().type(
                                                                                DamageSourcePredicate.Builder.damageType().tag(
                                                                                        TagPredicate.is(DamageTypeTags.IS_FALL)
                                                                                )
                                                                        ))
                                                        )
                                                        .cooldown(5))
                                        .maxResetCount(99)
                                        .spawnData(
                                                DynamicLevelSpawnData.Builder.create()
                                                        .playerOffset(new Vec3(-18, -9, 0))
                                                        .playerRotation(-90F)
                                                        .spellOffset(new Vec3(0, -4, 0))
                                        )
                                        .addRule(RuleType.NO_BUILDING)
                                        .withFlag(ResetFlags.RESET_POSITION)
                        ));
    }

    private static void register(BootstrapContext<PuzzleConfiguration> context, ResourceKey<PuzzleConfiguration> key, PuzzleConfiguration.Builder builder) {
        context.register(key, builder.build());
    }

    private static ResourceKey<PuzzleConfiguration> key(String name) {
        return ResourceKey.create(Keys.PUZZLE_CONFIG, CommonClass.customLocation(name));
    }
}
