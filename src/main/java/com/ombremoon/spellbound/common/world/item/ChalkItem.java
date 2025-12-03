package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.world.block.RuneBlock;
import com.ombremoon.spellbound.common.world.block.entity.RuneBlockEntity;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBData;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.stream.IntStream;

public class ChalkItem extends Item {
    public ChalkItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().above();
        ItemStack stack = context.getItemInHand();
        BlockState state = level.getBlockState(pos);
        if (state.canBeReplaced()) {
            int type = this.getRuneType(stack);
            level.setBlock(pos, SBBlocks.RUNE.get().defaultBlockState().setValue(RuneBlock.RUNE_TYPE, type), 3);
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof RuneBlockEntity runeBlock) {
                DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
                if (color != null) {
                    runeBlock.setData(SBData.RUNE_COLOR, color.rgb());
                }
            }

            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
    }

    private int getRuneType(ItemStack stack) {
        int[] runes = getOrCreateRunes(stack);
        var type = stack.get(SBData.RUNE_INDEX);

        if (type == null || type >= 26) {
            shuffle(runes);
            type = 0;
        }

        int rune = runes[type];
        stack.set(SBData.RUNE_INDEX, type + 1);
        return rune;
    }

    private static int[] getOrCreateRunes(ItemStack stack) {
        var list = stack.get(SBData.RUNES);
        if (list == null) {
            int[] newRunes = IntStream.rangeClosed(1, 26).toArray();
            shuffle(newRunes);
            stack.set(SBData.RUNES, new IntArrayList(newRunes));
            return newRunes;
        }

        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    private static void shuffle(int[] array) {
        RandomSource random = RandomSource.create();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
