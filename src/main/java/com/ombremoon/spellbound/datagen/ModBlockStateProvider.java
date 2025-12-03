package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.common.world.block.SummonStoneBlock;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Constants.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof SummonStoneBlock summonStone && block != SBBlocks.SUMMON_STONE.get())
                markedSummonStoneModel(summonStone);
        }
    }

    private void blockItem(Supplier<Block> blockSupplier) {
        simpleBlockItem(blockSupplier.get(), new ModelFile.UncheckedModelFile("spellbound:block/" + BuiltInRegistries.BLOCK.getKey(blockSupplier.get()).getPath()));
    }
    private void blockWithItem(Supplier<Block> blockSupplier) {
        simpleBlockWithItem(blockSupplier.get(), cubeAll(blockSupplier.get()));
    }

    protected BlockModelBuilder markedSummonStoneModel(SummonStoneBlock block) {
        String name = getName(block);
        var builder = models().withExistingParent(name, key(SBBlocks.SUMMON_STONE.get()))
                .texture("up", modLoc("block/" + block.getSpell().getPath() + "_stone"));
        itemModels().getBuilder(key(block).getPath()).parent(builder);
        return builder;
    }

    protected BlockModelBuilder blockCubeTopModel(Block block) {
        String name = getName(block);
        return models().cubeBottomTop(name, modLoc("block/" + name + "_side"), modLoc("block/" + name + "_bottom"), modLoc("block/" + name + "_top"));
    }

    protected ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    protected String getName(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

}