package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.effects.EffectHolder;
import com.ombremoon.spellbound.common.magic.effects.RangeProvider;
import com.ombremoon.spellbound.common.magic.effects.TickProvider;
import com.ombremoon.spellbound.common.magic.effects.types.*;
import com.ombremoon.spellbound.common.world.EntityResource;
import com.ombremoon.spellbound.common.world.recipe.RuneCraftingRecipeBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.neoforged.neoforge.common.Tags;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(pOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SBItems.MAGIC_ESSENCE.get(), 2)
                .requires(SBBlocks.ARCANTHUS.get())
                .unlockedBy("has_arcanthus", has(SBBlocks.ARCANTHUS.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBItems.RITUAL_TALISMAN.get())
                .define('G', Items.GOLD_INGOT)
                .define('M', SBItems.MAGIC_ESSENCE.get())
                .pattern("GGG")
                .pattern(" M ")
                .pattern(" G ")
                .unlockedBy("has_magic_essence", has(SBItems.MAGIC_ESSENCE.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBBlocks.TRANSFIGURATION_PEDESTAL.get())
                .define('W', Items.GREEN_WOOL)
                .define('L', ItemTags.LOGS)
                .define('S', ItemTags.WOODEN_SLABS)
                .define('M', SBItems.MAGIC_ESSENCE.get())
                .pattern("SWS")
                .pattern(" M ")
                .pattern("SLS")
                .unlockedBy("has_magic_essence", has(SBItems.MAGIC_ESSENCE.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBBlocks.TRANSFIGURATION_DISPLAY.get())
                .define('S', ItemTags.STONE_BRICKS)
                .define('C', Items.COPPER_INGOT)
                .define('#', SBItems.MAGIC_ESSENCE.get())
                .pattern(" S ")
                .pattern("C#C")
                .pattern("SSS")
                .unlockedBy("has_copper", has(Items.COPPER_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBItems.CHALK.get())
                .define('C', Items.CALCITE)
                .define('M', SBItems.MAGIC_ESSENCE.get())
                .pattern("C  ")
                .pattern(" M ")
                .pattern("  C")
                .unlockedBy("has_magic_essence", has(SBItems.MAGIC_ESSENCE.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBBlocks.MAGI_WORKBENCH.get())
                .define('L', ItemTags.LOGS)
                .define('W', Items.BLUE_WOOL)
                .define('#', SBItems.MAGIC_ESSENCE.get())
                .define('^', Items.BOOK)
                .pattern("^#^")
                .pattern("WWW")
                .pattern("L L")
                .unlockedBy("has_blue_wool", has(Items.BLUE_WOOL))
                .save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SBItems.STARTER_BOOK.get())
                .requires(SBItems.MAGIC_ESSENCE.get())
                .requires(Items.BOOK)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBItems.RUIN_BOOK.get())
                .define('B', Items.BOOK)
                .define('*', Items.COAL)
                .define('#', Items.SNOWBALL)
                .define('^', Items.COPPER_INGOT)
                .define('M', SBItems.MAGIC_ESSENCE.get())
                .pattern(" # ")
                .pattern("*B^")
                .pattern(" M ")
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBItems.TRANSFIG_BOOK.get())
                .define('B', Items.BOOK)
                .define('*', Items.AMETHYST_SHARD)
                .define('^', Items.GLASS_BOTTLE)
                .define('M', SBItems.MAGIC_ESSENCE.get())
                .pattern(" M ")
                .pattern("*B^")
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBItems.SUMMON_BOOK.get())
                .define('B', Items.BOOK)
                .define('*', Items.BONE)
                .define('^', Items.ROTTEN_FLESH)
                .define('M', SBItems.MAGIC_ESSENCE.get())
                .pattern(" M ")
                .pattern("*B^")
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBItems.DIVINE_BOOK.get())
                .define('B', Items.BOOK)
                .define('*', Items.FERMENTED_SPIDER_EYE)
                .define('^', Items.GOLD_NUGGET)
                .define('M', SBItems.MAGIC_ESSENCE.get())
                .pattern(" M ")
                .pattern("*B^")
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBItems.DECEPTION_BOOK.get())
                .define('B', Items.BOOK)
                .define('*', Items.TRIPWIRE_HOOK)
                .define('^', Items.INK_SAC)
                .define('M', SBItems.MAGIC_ESSENCE.get())
                .pattern(" M ")
                .pattern("*B^")
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBBlocks.WILD_MUSHROOM_SUMMON_STONE.get())
                .define('S', SBBlocks.SUMMON_STONE.get())
                .define('#', Tags.Items.MUSHROOMS)
                .pattern("SSS")
                .pattern("S#S")
                .pattern("SSS")
                .unlockedBy("has_mushroom", has(Tags.Items.MUSHROOMS))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBBlocks.SUMMON_STONE.get())
                .define('S', Blocks.STONE)
                .define('#', SBItems.MAGIC_ESSENCE.get())
                .define('^', Items.ENDER_PEARL)
                .define('O', Blocks.OBSIDIAN)
                .pattern("SOS")
                .pattern("^#^")
                .pattern("SOS")
                .unlockedBy("has_mushroom", has(Tags.Items.MUSHROOMS))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBBlocks.RESONANCE_STONE.get())
                .define('D', Blocks.DEEPSLATE_BRICKS)
                .define('M', SBItems.MAGIC_ESSENCE.get())
                .define('L', Items.LAPIS_LAZULI)
                .pattern("DLD")
                .pattern("DMD")
                .pattern("D D")
                .unlockedBy("has_deepslate", has(Tags.Items.COBBLESTONES_DEEPSLATE))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SBItems.SPIRIT_WHISTLE.get())
                .define('W', ItemTags.PLANKS)
                .define('S', Items.STRING)
                .define('M', SBItems.MAGIC_ESSENCE.get())
                .pattern("WS")
                .pattern("M ")
                .unlockedBy("has_magic_essence", has(SBItems.MAGIC_ESSENCE.get()))
                .save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SBBlocks.SUMMON_STONE.get())
                .requires(SBBlocks.CRACKED_SUMMON_STONE.get())
                .requires(SBItems.MAGIC_ESSENCE.get())
                .unlockedBy("has_summon_stone", has(SBBlocks.SUMMON_STONE.get()))
                .save(output, getConversionRecipeName(SBBlocks.SUMMON_STONE.get(), SBBlocks.CRACKED_SUMMON_STONE.get()));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBItems.MANA_TEAR.get())
                .define('#', SBItems.MAGIC_ESSENCE.get())
                .define('D', Items.DIAMOND)
                .pattern("###")
                .pattern("#D#")
                .pattern("###")
                .unlockedBy("has_magic_essence", has(SBItems.MAGIC_ESSENCE.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBItems.DIVINE_CHARM.get())
                .define('#', SBItems.MAGIC_ESSENCE.get())
                .define('D', Items.DIAMOND)
                .define('^', Items.GOLD_INGOT)
                .pattern("#^#")
                .pattern("^D^")
                .pattern("#^#")
                .unlockedBy("has_magic_essence", has(SBItems.MAGIC_ESSENCE.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBItems.ARCANE_EYE.get())
                .define('#', SBItems.MAGIC_ESSENCE.get())
                .define('*', Items.ENDER_PEARL)
                .pattern("###")
                .pattern("#*#")
                .pattern("###")
                .unlockedBy("has_magic_essence", has(SBItems.MAGIC_ESSENCE.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SBBlocks.DUNGEON_DOOR.get())
                .define('#', SBItems.MAGIC_ESSENCE.get())
                .define('D', ItemTags.DOORS)
                .define('*', SBItems.ARCANE_EYE.get())
                .pattern("#*#")
                .pattern("#D#")
                .pattern("###")
                .unlockedBy("has_arcane_eye", has(SBItems.ARCANE_EYE.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, SBItems.MAGIC_SHIELD.get())
                .define('#', SBItems.MAGIC_ESSENCE.get())
                .define('S', Items.SHIELD)
                .define('*', Items.GOLD_INGOT)
                .pattern("#*#")
                .pattern("*S*")
                .pattern("#*#")
                .unlockedBy("has_shield", has(Items.SHIELD))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBBlocks.DARK_ALTAR.get())
                .define('#', SBItems.MAGIC_ESSENCE.get())
                .define('^', Items.GOLD_INGOT)
                .define('*', Items.PURPLE_CARPET)
                .define('S', Items.DEEPSLATE)
                .pattern("*^*")
                .pattern("S#S")
                .pattern("SSS")
                .unlockedBy("has_magic_essence", has(SBItems.MAGIC_ESSENCE.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SBBlocks.MANA_SIPHON.get())
                .define('#', SBItems.MAGIC_ESSENCE.get())
                .define('^', Items.TINTED_GLASS)
                .define('*', Items.HOPPER)
                .define('S', Items.SOUL_SAND)
                .define('O', Items.OBSIDIAN)
                .define('C', Items.CAULDRON)
                .pattern("^#^")
                .pattern("S*S")
                .pattern("OCO")
                .unlockedBy("has_magic_essence", has(SBItems.MAGIC_ESSENCE.get()))
                .save(output);

        RuneCraftingRecipeBuilder.createRune(
                EffectHolder.simple(
                        new DamageEntity(SBDamageTypes.SB_GENERIC, 2),
                        Optional.empty(),
                        new TickProvider.AtTick(0),
                        BuffCategory.HARMFUL,
                        1
                )
        )
                .requires(Items.FERMENTED_SPIDER_EYE)
                .requires(Items.FERMENTED_SPIDER_EYE)
                .requires(Items.GUNPOWDER)
                .requires(Items.GUNPOWDER)
                .save(output, CommonClass.customLocation("curse"));
        RuneCraftingRecipeBuilder.createRune(
                EffectHolder.simple(
                        new SetResource(EntityResource.MANA, -4, EntityResource.Operation.ADD),
                        Optional.empty(),
                        new TickProvider.ForEveryTick(10),
                        BuffCategory.HARMFUL,
                        200
                )
        )
                .requires(Items.SOUL_SAND)
                .requires(Items.LAPIS_LAZULI)
                .requires(Items.AMETHYST_SHARD)
                .requires(SBItems.MAGIC_ESSENCE.get())
                .save(output, CommonClass.customLocation("mage_wreck"));
        RuneCraftingRecipeBuilder.createRune(
                EffectHolder.simple(
                        new DisarmEffect(),
                        Optional.empty(),
                        new TickProvider.AtTick(0),
                        BuffCategory.HARMFUL,
                        1
                )
        )
                .requires(Items.SLIME_BALL)
                .requires(Items.SLIME_BALL)
                .requires(Items.IRON_NUGGET)
                .requires(Items.PHANTOM_MEMBRANE)
                .save(output, CommonClass.customLocation("disarming_curse"));
        RuneCraftingRecipeBuilder.createRune(
                EffectHolder.simple(
                        new SummonDoppelganger(),
                        Optional.empty(),
                        new TickProvider.AtTick(0),
                        BuffCategory.HARMFUL,
                        1
                )
        )
                .requires(Items.GLASS_PANE)
                .requires(Items.ECHO_SHARD)
                .requires(Items.ENDER_PEARL)
                .requires(Items.GLOW_INK_SAC)
                .save(output, CommonClass.customLocation("mirror_curse"));
        RuneCraftingRecipeBuilder.createRune(
                EffectHolder.withComponents(
                        new ModifyDamage(0.25F, EntityResource.Operation.ADD_MULTIPLIED_TOTAL),
                        Optional.of(
                                DamageSourceCondition.hasDamageSource(
                                        DamageSourcePredicate.Builder.damageType().tag(
                                                TagPredicate.isNot(SBTags.DamageTypes.SPELL_DAMAGE)
                                        )
                                ).build()
                        ),
                        new TickProvider.NoTick(),
                        DataComponentMap.builder()
                                .set(SBData.PRE_DAMAGE, EnchantmentTarget.VICTIM),
                        BuffCategory.HARMFUL,
                        200
                )
        )
                .requires(Items.BONE)
                .requires(Items.BONE)
                .requires(Items.FLINT)
                .requires(Items.FLINT)
                .save(output, CommonClass.customLocation("pain_curse"));
        RuneCraftingRecipeBuilder.createRune(
                EffectHolder.simple(
                        new ApplyMobEffect(HolderSet.direct(SBEffects.SILENCED), 3, 3, 0, 0),
                        Optional.empty(),
                        new TickProvider.EveryTick(),
                        BuffCategory.HARMFUL,
                        1
                )
        )
                .requires(Items.WHITE_WOOL)
                .requires(Items.STRING)
                .requires(Items.FERMENTED_SPIDER_EYE)
                .requires(SBItems.MAGIC_ESSENCE.get())
                .save(output, CommonClass.customLocation("silent_curse"));
        RuneCraftingRecipeBuilder.createRune(
                EffectHolder.simple(
                        new TeleportEntity(new RangeProvider(10.0F, 20.0F, 1.0F, 5.0F), false),
                        Optional.empty(),
                        new TickProvider.AtTick(0),
                        BuffCategory.HARMFUL,
                        1
                )
        )
                .requires(Items.CHORUS_FRUIT)
                .requires(Items.CHORUS_FRUIT)
                .requires(Items.ENDER_PEARL)
                .requires(Items.FEATHER)
                .save(output, CommonClass.customLocation("vanishing_curse"));
        RuneCraftingRecipeBuilder.createRune(
                EffectHolder.withComponents(
                        new ModifyDamage(-0.1F, EntityResource.Operation.ADD_MULTIPLIED_TOTAL),
                        Optional.of(
                                DamageSourceCondition.hasDamageSource(
                                        DamageSourcePredicate.Builder.damageType().tag(
                                                TagPredicate.isNot(SBTags.DamageTypes.SPELL_DAMAGE)
                                        )
                                ).build()
                        ),
                        new TickProvider.NoTick(),
                        DataComponentMap.builder()
                                .set(SBData.PRE_ATTACK, EnchantmentTarget.ATTACKER),
                        BuffCategory.HARMFUL,
                        200
                )
        )
                .requires(Items.FERMENTED_SPIDER_EYE)
                .requires(Items.FERMENTED_SPIDER_EYE)
                .requires(Items.BROWN_MUSHROOM)
                .requires(Items.BROWN_MUSHROOM)
                .save(output, CommonClass.customLocation("weakness_curse"));
        RuneCraftingRecipeBuilder.createRune(
                EffectHolder.withComponents(
                        new ModifyDamage(0.1F, EntityResource.Operation.ADD_MULTIPLIED_TOTAL),
                        Optional.of(
                                DamageSourceCondition.hasDamageSource(
                                        DamageSourcePredicate.Builder.damageType().tag(
                                                TagPredicate.is(SBTags.DamageTypes.SPELL_DAMAGE)
                                        )
                                ).build()
                        ),
                        new TickProvider.NoTick(),
                        DataComponentMap.builder()
                                .set(SBData.PRE_DAMAGE, EnchantmentTarget.VICTIM),
                        BuffCategory.HARMFUL,
                        200
                )
        )
                .requires(Items.AMETHYST_SHARD)
                .requires(Items.GLOWSTONE_DUST)
                .requires(Items.REDSTONE)
                .requires(SBItems.MAGIC_ESSENCE.get())
                .save(output, CommonClass.customLocation("susceptibility_curse"));
        RuneCraftingRecipeBuilder.createRune(
                EffectHolder.simple(
                        new ApplyMobEffect(HolderSet.direct(SBEffects.ROOTED), 3, 3, 0, 0),
                        Optional.empty(),
                        new TickProvider.AtTick(0),
                        BuffCategory.HARMFUL,
                        1
                )
        )
                .requires(Items.COBWEB)
                .requires(Items.COBWEB)
                .requires(Items.SLIME_BALL)
                .requires(Items.LEATHER_BOOTS)
                .save(output, CommonClass.customLocation("tanglefoot"));
    }
}
