package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.client.particle.EffectBuilder;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class ModTagProvider {

    public static class Items extends TagsProvider<Item> {

        public Items(PackOutput p_256596_, CompletableFuture<HolderLookup.Provider> p_256513_, @Nullable ExistingFileHelper existingFileHelper) {
            super(p_256596_, Registries.ITEM, p_256513_, Constants.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            populateTag(ItemTags.DYEABLE, SBItems.CHALK);
            populateTag(SBTags.Items.MAGIC_SHARD, SBItems.SMOLDERING_SHARD, SBItems.FROZEN_SHARD, SBItems.STORM_SHARD, SBItems.HOLY_SHARD, SBItems.FOOL_SHARD, SBItems.SOUL_SHARD);
            populateTag(SBTags.Items.STAFF, SBItems.FIRE_STAFF, SBItems.ICE_STAFF, SBItems.SHOCK_STAFF, SBItems.CREATIONIST_STAFF);
            populateTagFromBlocks(SBTags.Items.DIVINE_SHRINE, SBBlocks.JUNGLE_DIVINE_SHRINE, SBBlocks.PLAINS_DIVINE_SHRINE, SBBlocks.SANDSTONE_DIVINE_SHRINE);
            populateTagFromBlocks(Tags.Items.MUSHROOMS, SBBlocks.WILD_MUSHROOM);
            populateTagFromBlocks(SBTags.Items.FROG_LIGHTS, net.minecraft.world.level.block.Blocks.OCHRE_FROGLIGHT, net.minecraft.world.level.block.Blocks.PEARLESCENT_FROGLIGHT, net.minecraft.world.level.block.Blocks.VERDANT_FROGLIGHT);
        }

        public void populateTagFromBlocks(TagKey<Item> tag, Supplier<Block>... items){
            for (Supplier<Block> item : items) {
                tag(tag).add(BuiltInRegistries.ITEM.getResourceKey(item.get().asItem()).get());
            }
        }

        public void populateTagFromBlocks(TagKey<Item> tag, Block... items){
            for (Block item : items) {
                tag(tag).add(BuiltInRegistries.ITEM.getResourceKey(item.asItem()).get());
            }
        }

        public void populateTag(TagKey<Item> tag, Supplier<Item>... items){
            for (Supplier<Item> item : items) {
                tag(tag).add(BuiltInRegistries.ITEM.getResourceKey(item.get()).get());
            }
        }
    }

    public static class Blocks extends TagsProvider<Block> {

        public Blocks(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator, Registries.BLOCK, provider, Constants.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            for (Block block : TransfigurationMultiblock.EXCLUDED_BLOCKS) {
                this.populateTag(SBTags.Blocks.RITUAL_COMPATIBLE, block);
            }
            this.populateTag(BlockTags.FLOWERS, SBBlocks.ARCANTHUS.get());
            this.populateTag(SBTags.Blocks.DIVINE_SHRINE, SBBlocks.JUNGLE_DIVINE_SHRINE.get(), SBBlocks.PLAINS_DIVINE_SHRINE.get(), SBBlocks.SANDSTONE_DIVINE_SHRINE.get());
        }

        public void populateTag(TagKey<Block> tag, Block... blocks){
            for (Block block : blocks) {
                tag(tag).add(BuiltInRegistries.BLOCK.getResourceKey(block).get());
            }
        }
    }

    public static class EntityTypes extends EntityTypeTagsProvider {
        public EntityTypes(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, provider, Constants.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.tag(Tags.EntityTypes.BOSSES)
                    .add(SBEntities.GIANT_MUSHROOM.get());
        }
    }

    public static class DamageTypes extends TagsProvider<DamageType> {
        public DamageTypes(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, Registries.DAMAGE_TYPE, provider, Constants.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.tag(SBTags.DamageTypes.SPELL_DAMAGE)
                    .add(SBDamageTypes.SB_GENERIC)
                    .add(SBDamageTypes.RUIN_FIRE)
                    .add(SBDamageTypes.RUIN_FROST)
                    .add(SBDamageTypes.RUIN_SHOCK);

            this.tag(SBTags.DamageTypes.SPELL_DAMAGE)
                    .addTags(Tags.DamageTypes.IS_MAGIC);

        }
    }
}
