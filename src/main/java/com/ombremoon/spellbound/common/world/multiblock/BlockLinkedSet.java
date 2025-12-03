package com.ombremoon.spellbound.common.world.multiblock;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class BlockLinkedSet {
    public static final Hash.Strategy<? super Block> TYPE_AND_TAG = new Hash.Strategy<>() {
        @Override
        public int hashCode(Block o) {
            if (o != null) {
                return 31 * o.hashCode();
            } else {
                return 0;
            }
        }

        @Override
        public boolean equals(Block a, Block b) {
            return a.defaultBlockState().is(b);
        }
    };

    public static Set<Block> createTypeAndComponentsSet() {
        return new ObjectLinkedOpenCustomHashSet<>(TYPE_AND_TAG);
    }
}
