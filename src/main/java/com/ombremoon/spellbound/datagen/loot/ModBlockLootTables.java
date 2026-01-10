package com.ombremoon.spellbound.datagen.loot;

import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.dropSelf(SBBlocks.MAGI_WORKBENCH.get());
        this.dropSelf(SBBlocks.ARCANTHUS.get());
        this.dropSelf(SBBlocks.FROZEN_CRYSTAL_BLOCK.get());
        this.dropSelf(SBBlocks.BUDDING_FROZEN_CRYSTAL.get());
        this.add(SBBlocks.FROZEN_CRYSTAL_CLUSTER.get(), block -> this.createShardDrops(block, SBItems.FROZEN_SHARD.get()));
        this.dropWhenSilkTouch(SBBlocks.LARGE_FROZEN_CRYSTAL_BUD.get());
        this.dropWhenSilkTouch(SBBlocks.MEDIUM_FROZEN_CRYSTAL_BUD.get());
        this.dropWhenSilkTouch(SBBlocks.SMALL_FROZEN_CRYSTAL_BUD.get());
        this.dropSelf(SBBlocks.SMOLDERING_CRYSTAL_BLOCK.get());
        this.dropSelf(SBBlocks.BUDDING_SMOLDERING_CRYSTAL.get());
        this.add(SBBlocks.SMOLDERING_CRYSTAL_CLUSTER.get(), block -> this.createShardDrops(block, SBItems.SMOLDERING_SHARD.get()));
        this.dropWhenSilkTouch(SBBlocks.LARGE_SMOLDERING_CRYSTAL_BUD.get());
        this.dropWhenSilkTouch(SBBlocks.MEDIUM_SMOLDERING_CRYSTAL_BUD.get());
        this.dropWhenSilkTouch(SBBlocks.SMALL_SMOLDERING_CRYSTAL_BUD.get());
        this.dropSelf(SBBlocks.STORM_CRYSTAL_BLOCK.get());
        this.dropSelf(SBBlocks.BUDDING_STORM_CRYSTAL.get());
        this.add(SBBlocks.STORM_CRYSTAL_CLUSTER.get(), block -> this.createShardDrops(block, SBItems.STORM_SHARD.get()));
        this.dropWhenSilkTouch(SBBlocks.LARGE_STORM_CRYSTAL_BUD.get());
        this.dropWhenSilkTouch(SBBlocks.MEDIUM_STORM_CRYSTAL_BUD.get());
        this.dropWhenSilkTouch(SBBlocks.SMALL_STORM_CRYSTAL_BUD.get());
        this.dropSelf(SBBlocks.TRANSFIGURATION_PEDESTAL.get());
        this.dropSelf(SBBlocks.TRANSFIGURATION_DISPLAY.get());
        this.dropSelf(SBBlocks.SUMMON_STONE.get());
        this.dropSelf(SBBlocks.CRACKED_SUMMON_STONE.get());
        this.dropSelf(SBBlocks.WILD_MUSHROOM_SUMMON_STONE.get());
        this.dropSelf(SBBlocks.PLAINS_DIVINE_SHRINE.get());
        this.dropSelf(SBBlocks.SANDSTONE_DIVINE_SHRINE.get());
        this.dropSelf(SBBlocks.JUNGLE_DIVINE_SHRINE.get());
        this.dropSelf(SBBlocks.VALKYR_STATUE.get());
        this.dropSelf(SBBlocks.WILD_MUSHROOM.get());
        this.dropSelf(SBBlocks.MYCELIUM_CARPET.get());
        this.dropSelf(SBBlocks.GREEN_SPORE_BLOCK.get());
        this.dropSelf(SBBlocks.PURPLE_SPORE_BLOCK.get());
        this.dropSelf(SBBlocks.RED_SPORE_BLOCK.get());
        this.dropSelf(SBBlocks.PINK_SPORE_BLOCK.get());
        this.add(SBBlocks.GREEN_SPORE_SLAB.get(), this::createSlabItemTable);
        this.add(SBBlocks.PURPLE_SPORE_SLAB.get(), this::createSlabItemTable);
        this.add(SBBlocks.RED_SPORE_SLAB.get(), this::createSlabItemTable);
        this.add(SBBlocks.PINK_SPORE_SLAB.get(), this::createSlabItemTable);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return (Iterable<Block>) SBBlocks.BLOCKS.getEntries().stream().map(Supplier::get).toList();
    }

    protected LootTable.Builder createShardDrops(Block block, Item item) {
        return this.createSilkTouchDispatchTable(
                block,
                this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
        );
    }

    protected boolean shouldDropSelf(Block block) {
        return shouldGenerateLoot(block);
    }

    protected boolean shouldGenerateLoot(Block block) {
        return block.asItem() != Items.AIR && !(block instanceof DropExperienceBlock);
    }
}