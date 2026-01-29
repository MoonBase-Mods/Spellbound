package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.common.init.SBItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Collection;
import java.util.function.Supplier;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        SBItems.SIMPLE_ITEM_LIST.stream().map(Supplier::get).forEach(this::tempItem);
//        tempItem(SBItems.DEBUG.get());
        simpleGeneratedModel(SBBlocks.ARCANTHUS.get().asItem());

        simpleGeneratedModel(SBItems.SOUL_SHARD);
        simpleGeneratedModel(SBItems.FOOL_SHARD);
        simpleGeneratedModel(SBItems.FROZEN_SHARD);
        simpleGeneratedModel(SBItems.SMOLDERING_SHARD);
        simpleGeneratedModel(SBItems.STORM_SHARD);
        simpleGeneratedModel(SBItems.HOLY_SHARD);

        simpleGeneratedModel(SBBlocks.MAGI_WORKBENCH);
        simpleGeneratedModel(SBBlocks.VALKYR_STATUE);
        simpleGeneratedModel(SBItems.CREATIONIST_BOOTS);
        simpleGeneratedModel(SBItems.CREATIONIST_CHESTPLATE);
        simpleGeneratedModel(SBItems.CREATIONIST_LEGGINGS);
        simpleGeneratedModel(SBItems.CREATIONIST_HELMET);
        simpleGeneratedModel(SBItems.CHALK);

        simpleGeneratedModel(SBItems.CRYOMANCER_BOOTS);
        simpleGeneratedModel(SBItems.CRYOMANCER_CHESTPLATE);
        simpleGeneratedModel(SBItems.CRYOMANCER_LEGGINGS);
        simpleGeneratedModel(SBItems.CRYOMANCER_HELMET);

        simpleGeneratedModel(SBItems.PYROMANCER_BOOTS);
        simpleGeneratedModel(SBItems.PYROMANCER_CHESTPLATE);
        simpleGeneratedModel(SBItems.PYROMANCER_LEGGINGS);
        simpleGeneratedModel(SBItems.PYROMANCER_HELMET);

        simpleGeneratedModel(SBItems.STORMWEAVER_BOOTS);
        simpleGeneratedModel(SBItems.STORMWEAVER_CHESTPLATE);
        simpleGeneratedModel(SBItems.STORMWEAVER_LEGGINGS);
        simpleGeneratedModel(SBItems.STORMWEAVER_HELMET);


        simpleGeneratedModel(SBItems.MAGIC_ESSENCE);
        simpleGeneratedModel(SBItems.MANA_TEAR);

        simpleGeneratedModel(SBItems.DECEPTION_BOOK);
        simpleGeneratedModel(SBItems.DIVINE_BOOK);
        simpleGeneratedModel(SBItems.RUIN_BOOK);
        simpleGeneratedModel(SBItems.TRANSFIG_BOOK);
        simpleGeneratedModel(SBItems.SUMMON_BOOK);
        simpleGeneratedModel(SBItems.STARTER_BOOK);


    }

    private void registerItemModels(Collection<Supplier<? extends Item>> registryObjects) {
        registryObjects.stream().map(Supplier::get).forEach(this::simpleGeneratedModel);
    }

    protected ItemModelBuilder simpleGeneratedModel(ItemLike item) {
        return simpleModel(item.asItem(), mcLoc("item/generated"));
    }

    protected <T extends ItemLike> ItemModelBuilder simpleGeneratedModel(Supplier<T> item) {
        return simpleModel(item.get().asItem(), mcLoc("item/generated"));
    }

    protected ItemModelBuilder simpleHandHeldModel(Item item) {
        return simpleModel(item, mcLoc("item/handheld"));
    }

    protected ItemModelBuilder tempHandHeldModel(Item item) {
        return tempModel(item, mcLoc("item/handheld"));
    }

    protected ItemModelBuilder simpleModel(Item item, ResourceLocation parent) {
        String name = getName(item);
        return singleTexture(name, parent, "layer0", modLoc("item/" + name));
    }

    protected ItemModelBuilder tempModel(Item item, ResourceLocation parent) {
        String name = getName(item);
        return singleTexture("temp", parent, "layer0", modLoc("item/" + name));
    }

    protected ItemModelBuilder tempItem(Item item) {
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item).getPath(),
                ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated")).texture("layer0",
                CommonClass.customLocation("item/" + "temp_texture"));
    }

    protected String getName(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    protected String getName(Block item) {
        return BuiltInRegistries.BLOCK.getKey(item).getPath();
    }

}

