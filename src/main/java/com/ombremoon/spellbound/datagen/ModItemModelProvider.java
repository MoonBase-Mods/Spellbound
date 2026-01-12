package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.common.init.SBItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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

        simpleGeneratedModel(SBItems.SOUL_SHARD.get());
        simpleGeneratedModel(SBItems.FOOL_SHARD.get());
        simpleGeneratedModel(SBItems.FROZEN_SHARD.get());
        simpleGeneratedModel(SBItems.SMOLDERING_SHARD.get());
        simpleGeneratedModel(SBItems.STORM_SHARD.get());
        simpleGeneratedModel(SBItems.HOLY_SHARD.get());

        simpleGeneratedModel(SBBlocks.MAGI_WORKBENCH.get().asItem());
        simpleGeneratedModel(SBItems.CREATIONIST_BOOTS.get());
        simpleGeneratedModel(SBItems.CREATIONIST_CHESTPLATE.get());
        simpleGeneratedModel(SBItems.CREATIONIST_LEGGINGS.get());
        simpleGeneratedModel(SBItems.CREATIONIST_HELMET.get());
        simpleGeneratedModel(SBItems.CHALK.get());


        simpleGeneratedModel(SBItems.CRYOMANCER_BOOTS.get());
        simpleGeneratedModel(SBItems.CRYOMANCER_CHESTPLATE.get());
        simpleGeneratedModel(SBItems.CRYOMANCER_LEGGINGS.get());
        simpleGeneratedModel(SBItems.CRYOMANCER_HELMET.get());

        simpleGeneratedModel(SBItems.PYROMANCER_BOOTS.get());
        simpleGeneratedModel(SBItems.PYROMANCER_CHESTPLATE.get());
        simpleGeneratedModel(SBItems.PYROMANCER_LEGGINGS.get());
        simpleGeneratedModel(SBItems.PYROMANCER_HELMET.get());

        simpleGeneratedModel(SBItems.STORMWEAVER_BOOTS.get());
        simpleGeneratedModel(SBItems.STORMWEAVER_CHESTPLATE.get());
        simpleGeneratedModel(SBItems.STORMWEAVER_LEGGINGS.get());
        simpleGeneratedModel(SBItems.STORMWEAVER_HELMET.get());


    }

    private void registerItemModels(Collection<Supplier<? extends Item>> registryObjects) {
        registryObjects.stream().map(Supplier::get).forEach(this::simpleGeneratedModel);
    }

    protected ItemModelBuilder simpleGeneratedModel(Item item) {
        return simpleModel(item, mcLoc("item/generated"));
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

