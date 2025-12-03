package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.common.world.multiblock.BuildingBlock;
import com.ombremoon.spellbound.common.world.multiblock.MultiblockOutput;
import com.ombremoon.spellbound.common.world.multiblock.MultiblockProvider;
import com.ombremoon.spellbound.common.world.multiblock.type.StandardMultiblock;
import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class ModMultiblockProvider extends MultiblockProvider {
    public ModMultiblockProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildMultiblocks(MultiblockOutput multiblockOutput) {
        StandardMultiblock.Builder.of()
                .pattern("^ ^",
                         " $ ",
                         "^$^")
                .pattern("$ $",
                         "   ",
                         "$ $")
                .pattern("^^^",
                         "^^^",
                         "^^^")
                .key('^', BuildingBlock.ANY)
                .key('$', BuildingBlock.of(Blocks.DIAMOND_BLOCK))
                .build(multiblockOutput, CommonClass.customLocation("building_block_test"));

        TransfigurationMultiblock.Builder.of()
                .rings(1)
                .build(multiblockOutput, CommonClass.customLocation("one_ring"));

        TransfigurationMultiblock.Builder.of()
                .rings(2)
                .build(multiblockOutput, CommonClass.customLocation("two_ring"));
    }
}
