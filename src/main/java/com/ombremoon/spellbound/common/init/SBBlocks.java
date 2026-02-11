package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.acquisition.bosses.BossFight;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.BossFights;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.common.world.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SBBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);

    public static final Supplier<Block> MAGI_WORKBENCH = registerBlock(
            "magis_workbench", () -> new UnnamedWorkbenchBlock(
                    blockProperties()
                            .noOcclusion()
            )
    );
    public static final Supplier<Block> ARCANTHUS = registerBlock(
            "arcanthus", () -> new ArcanthusCropBlock(
                    blockProperties()
                            .noOcclusion()
            )
    );
    public static final Supplier<Block> FROZEN_CRYSTAL_BLOCK = registerBlock(
            "frozen_crystal_block", () -> new CrystalBlock(
                    CrystalBlock.CrystalType.FROZEN, blockProperties()
                        .mapColor(MapColor.COLOR_LIGHT_BLUE)
                        .strength(1.5F)
                        .sound(SoundType.AMETHYST)
                        .requiresCorrectToolForDrops()
            )
    );
    public static final Supplier<Block> BUDDING_FROZEN_CRYSTAL = registerBlock(
            "budding_frozen_crystal", () -> new BuddingCrystalBlock(
                    CrystalBlock.CrystalType.FROZEN, blockProperties()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .randomTicks()
                    .strength(1.5F)
                    .sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops()
                    .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final Supplier<Block> FROZEN_CRYSTAL_CLUSTER = registerBlock(
            "frozen_crystal_cluster", () -> new CrystalClusterBlock(
                    CrystalBlock.CrystalType.FROZEN, 7.0F, 3.0F, blockProperties()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .randomTicks()
                    .strength(1.5F)
                    .sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops()
                    .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final Supplier<Block> LARGE_FROZEN_CRYSTAL_BUD = registerBlock(
            "large_frozen_crystal_bud", () -> new CrystalClusterBlock(
                    CrystalBlock.CrystalType.FROZEN, 5.0F, 4.0F, BlockBehaviour.Properties.ofLegacyCopy(FROZEN_CRYSTAL_CLUSTER.get())
            )
    );
    public static final Supplier<Block> MEDIUM_FROZEN_CRYSTAL_BUD = registerBlock(
            "medium_frozen_crystal_bud", () -> new CrystalClusterBlock(
                    CrystalBlock.CrystalType.FROZEN, 4.0F, 4.0F, BlockBehaviour.Properties.ofLegacyCopy(FROZEN_CRYSTAL_CLUSTER.get())
            )
    );
    public static final Supplier<Block> SMALL_FROZEN_CRYSTAL_BUD = registerBlock(
            "small_frozen_crystal_bud", () -> new CrystalClusterBlock(
                    CrystalBlock.CrystalType.FROZEN, 3.0F, 4.0F, BlockBehaviour.Properties.ofLegacyCopy(FROZEN_CRYSTAL_CLUSTER.get())
            )
    );
    public static final Supplier<Block> SMOLDERING_CRYSTAL_BLOCK = registerBlock(
            "smoldering_crystal_block", () -> new CrystalBlock(
                    CrystalBlock.CrystalType.SMOLDERING, blockProperties()
                        .mapColor(MapColor.FIRE)
                        .strength(1.5F)
                        .sound(SoundType.AMETHYST)
                        .requiresCorrectToolForDrops()
            )
    );
    public static final Supplier<Block> BUDDING_SMOLDERING_CRYSTAL = registerBlock(
            "budding_smoldering_crystal", () -> new BuddingCrystalBlock(
                    CrystalBlock.CrystalType.SMOLDERING, blockProperties()
                    .mapColor(MapColor.FIRE)
                    .randomTicks()
                    .strength(1.5F)
                    .sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops()
                    .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final Supplier<Block> SMOLDERING_CRYSTAL_CLUSTER = registerBlock(
            "smoldering_crystal_cluster", () -> new CrystalClusterBlock(
                    CrystalBlock.CrystalType.SMOLDERING, 7.0F, 3.0F, blockProperties()
                    .mapColor(MapColor.FIRE)
                    .randomTicks()
                    .strength(1.5F)
                    .sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops()
                    .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final Supplier<Block> LARGE_SMOLDERING_CRYSTAL_BUD = registerBlock(
            "large_smoldering_crystal_bud", () -> new CrystalClusterBlock(
                    CrystalBlock.CrystalType.SMOLDERING, 5.0F, 4.0F, BlockBehaviour.Properties.ofLegacyCopy(SMOLDERING_CRYSTAL_CLUSTER.get())
            )
    );
    public static final Supplier<Block> MEDIUM_SMOLDERING_CRYSTAL_BUD = registerBlock(
            "medium_smoldering_crystal_bud", () -> new CrystalClusterBlock(
                    CrystalBlock.CrystalType.SMOLDERING, 4.0F, 4.0F, BlockBehaviour.Properties.ofLegacyCopy(SMOLDERING_CRYSTAL_CLUSTER.get())
            )
    );
    public static final Supplier<Block> SMALL_SMOLDERING_CRYSTAL_BUD = registerBlock(
            "small_smoldering_crystal_bud", () -> new CrystalClusterBlock(
                    CrystalBlock.CrystalType.SMOLDERING, 3.0F, 4.0F, BlockBehaviour.Properties.ofLegacyCopy(SMOLDERING_CRYSTAL_CLUSTER.get())
            )
    );
    public static final Supplier<Block> STORM_CRYSTAL_BLOCK = registerBlock(
            "storm_crystal_block", () -> new CrystalBlock(
                    CrystalBlock.CrystalType.STORM, blockProperties()
                        .mapColor(MapColor.COLOR_YELLOW)
                        .strength(1.5F)
                        .sound(SoundType.AMETHYST)
                        .requiresCorrectToolForDrops()
            )
    );
    public static final Supplier<Block> BUDDING_STORM_CRYSTAL = registerBlock(
            "budding_storm_crystal", () -> new BuddingCrystalBlock(
                    CrystalBlock.CrystalType.STORM, blockProperties()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .randomTicks()
                    .strength(1.5F)
                    .sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops()
                    .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final Supplier<Block> STORM_CRYSTAL_CLUSTER = registerBlock(
            "storm_crystal_cluster", () -> new CrystalClusterBlock(
                    CrystalBlock.CrystalType.STORM, 7.0F, 3.0F, blockProperties()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .randomTicks()
                    .strength(1.5F)
                    .sound(SoundType.AMETHYST)
                    .lightLevel(state -> 7)
                    .requiresCorrectToolForDrops()
                    .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final Supplier<Block> LARGE_STORM_CRYSTAL_BUD = registerBlock(
            "large_storm_crystal_bud", () -> new CrystalClusterBlock(
                    CrystalBlock.CrystalType.STORM, 5.0F, 4.0F, BlockBehaviour.Properties.ofLegacyCopy(STORM_CRYSTAL_CLUSTER.get())
            )
    );
    public static final Supplier<Block> MEDIUM_STORM_CRYSTAL_BUD = registerBlock(
            "medium_storm_crystal_bud", () -> new CrystalClusterBlock(
                    CrystalBlock.CrystalType.STORM, 4.0F, 4.0F, BlockBehaviour.Properties.ofLegacyCopy(STORM_CRYSTAL_CLUSTER.get())
            )
    );
    public static final Supplier<Block> SMALL_STORM_CRYSTAL_BUD = registerBlock(
            "small_storm_crystal_bud", () -> new CrystalClusterBlock(
                    CrystalBlock.CrystalType.STORM, 3.0F, 4.0F, BlockBehaviour.Properties.ofLegacyCopy(STORM_CRYSTAL_CLUSTER.get())
            )
    );
    public static final Supplier<Block> TRANSFIGURATION_PEDESTAL = registerBlock(
            "transfiguration_pedestal", () -> new TransfigurationPedestalBlock(
                    blockProperties()
                        .noOcclusion()
            )
    );
    public static final Supplier<Block> TRANSFIGURATION_DISPLAY = registerBlock(
            "transfiguration_display", () -> new TransfigurationDisplayBlock(
                    blockProperties()
                        .noOcclusion()
            )
    );
    public static final Supplier<Block> RUNE = registerBlock(
            "rune", () -> new RuneBlock(
                blockProperties()
                    .strength(0.1F)
                    .noCollission()
                    .pushReaction(PushReaction.DESTROY)
                    .noLootTable()
                    .noOcclusion()
            ), false
    );
    public static final Supplier<Block> SUMMON_STONE = registerBlock(
            "summon_stone", () -> new SummonStoneBlock(
                    blockProperties()
                        .lightLevel(state -> state.getValue(SummonStoneBlock.POWERED) ? 13 : 0)
            )
    );
    public static final Supplier<Block> CRACKED_SUMMON_STONE = registerSimpleBlock("cracked_summon_stone");
    public static final Supplier<Block> SUMMON_PORTAL = registerBlock(
            "summon_portal", () -> new SummonPortalBlock(
                    blockProperties()
                        .mapColor(MapColor.COLOR_BLACK)
                        .noCollission()
                        .lightLevel(state -> 11)
                        .strength(-1.0F)
                        .sound(SoundType.GLASS)
                        .noLootTable()
                        .pushReaction(PushReaction.BLOCK)
            ), false
    );
    public static final Supplier<Block> SANDSTONE_DIVINE_SHRINE = registerBlock("sandstone_divine_shrine", () -> new DivineShrineBlock(blockProperties()));
    public static final Supplier<Block> PLAINS_DIVINE_SHRINE = registerBlock("plains_divine_shrine", () -> new DivineShrineBlock(blockProperties()));
    public static final Supplier<Block> JUNGLE_DIVINE_SHRINE = registerBlock("jungle_divine_shrine", () -> new DivineShrineBlock(blockProperties()));
    public static final Supplier<Block> VALKYR_STATUE = registerBlock("valkyr_statue", () -> new ValkyrStatueBlock(blockProperties().noOcclusion().strength(5f)));
    public static final Supplier<Block> UMBRAL_SLUDGE = registerSludge("umbral_sludge", true);
    public static final Supplier<Block> PENUMBRAL_SLUDGE = registerSludge("penumbral_sludge", false);
//    public static final Supplier<Block> WOVEN_SHADE = registerSimpleBlock("woven_shade");

    public static final Supplier<Block> GREEN_SPORE_BLOCK = registerBlock("green_spore_block", () -> new SporeBlock(blockProperties()));
    public static final Supplier<Block> GREEN_SPORE_SLAB = registerBlock("green_spore_slab", () -> new SporeSlabBlock(blockProperties()));
    public static final Supplier<Block> PURPLE_SPORE_BLOCK = registerBlock("purple_spore_block", () -> new SporeBlock(blockProperties()));
    public static final Supplier<Block> PURPLE_SPORE_SLAB = registerBlock("purple_spore_slab", () -> new SporeSlabBlock(blockProperties()));
    public static final Supplier<Block> PINK_SPORE_BLOCK = registerBlock("pink_spore_block", () -> new SporeBlock(blockProperties()));
    public static final Supplier<Block> PINK_SPORE_SLAB = registerBlock("pink_spore_slab", () -> new SporeSlabBlock(blockProperties()));
    public static final Supplier<Block> RED_SPORE_BLOCK = registerBlock("red_spore_block", () -> new SporeBlock(blockProperties()));
    public static final Supplier<Block> RED_SPORE_SLAB = registerBlock("red_spore_slab", () -> new SporeSlabBlock(blockProperties()));
    public static final Supplier<Block> WILD_MUSHROOM = registerBlock(
            "wild_mushroom", () -> new MushroomBlock(
                    SBConfiguredFeatures.HUGE_WILD_MUSHROOM,
                    blockProperties()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .sound(SoundType.GRASS)
                            .lightLevel(state -> 1)
                            .hasPostProcess(SBBlocks::always)
                            .pushReaction(PushReaction.DESTROY)
            )
    );
    public static final Supplier<Block> MYCELIUM_CARPET = registerBlock(
            "mycelium_carpet", () -> new MyceliumCarpetBlock(
                    blockProperties()
                        .mapColor(MapColor.COLOR_PURPLE)
                        .strength(0.1F)
                        .replaceable()
                        .sound(SoundType.MOSS_CARPET)
                        .pushReaction(PushReaction.DESTROY)
                        .randomTicks()));
    public static final Supplier<Block> WILD_MUSHROOM_SUMMON_STONE = registerSummonStone("wild_mushroom", BossFights.WILD_MUSHROOM);

    public static final Supplier<Block> DECEPTION_TEST_BLOCK = registerBlock("deception_test_block", () -> new DeceptionTestBlock(blockProperties()));

    private static Boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }

    public static Supplier<Block> registerSummonStone(String name, @Nullable BossFight.BossFightBuilder<?> bossFight) {
        return registerBlock(name + "_summon_stone", () -> new SummonStoneBlock(CommonClass.customLocation(name), bossFight, blockProperties()));
    }

    public static DeferredBlock<Block> registerSludge(String name, boolean causeHarm) {
        return registerBlock(name, () -> new SludgeBlock(causeHarm, blockProperties()
                .mapColor(MapColor.COLOR_BLACK)
                .noLootTable()
                .forceSolidOn()
                .noCollission()
                .strength(-1.0F, 3600000.0F)));
    }

    private static DeferredBlock<Block> registerSimpleBlock(String name) {
        return registerBlock(name, () -> new Block(blockProperties()), true);
    }

    private static BlockBehaviour.Properties blockProperties() {
        return BlockBehaviour.Properties.of();
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, true);
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block, boolean registerItem) {
        DeferredBlock<T> deferredBlock = BLOCKS.register(name, block);
        if (registerItem)
            SBItems.registerBlockItem(name, deferredBlock);

        return deferredBlock;
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}