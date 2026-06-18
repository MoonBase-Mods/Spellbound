package com.ombremoon.spellbound.common.world.item.components;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.ombremoon.spellbound.common.init.SBTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.apache.commons.lang3.math.Fraction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SatchelContents implements TooltipComponent {
    public static final SatchelContents EMPTY = new SatchelContents(List.of());
    public static final Codec<SatchelContents> CODEC = ItemStack.CODEC.listOf().xmap(SatchelContents::new, (p_331551_) -> p_331551_.items);;
    public static final StreamCodec<RegistryFriendlyByteBuf, SatchelContents> STREAM_CODEC = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).map(SatchelContents::new, (p_331649_) -> p_331649_.items);;
    public static final int TOTAL_MAX_ITEMS = 32;
    public static final int MAX_PER_STACK = 4;
    private final List<ItemStack> items;
    private int itemCount = 0;

    public BundleContents asBundle() {
        return new BundleContents(this.items);
    }

    public SatchelContents(List<ItemStack> items) {
        this.items = items;
        for (ItemStack stack : items) {
            itemCount += stack.getCount();
        }
    }

    public ItemStack getItemUnsafe(int index) {
        return (ItemStack)this.items.get(index);
    }

    public Stream<ItemStack> itemCopyStream() {
        return this.items.stream().map(ItemStack::copy);
    }

    public Iterable<ItemStack> items() {
        return this.items;
    }

    public int getItemCount() {
        return this.itemCount;
    }

    public Iterable<ItemStack> itemsCopy() {
        return Lists.transform(this.items, ItemStack::copy);
    }

    public int size() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            if (other instanceof SatchelContents contents) {
                return ItemStack.listMatches(this.items, contents.items);
            }

            return false;
        }
    }

    public int hashCode() {
        return ItemStack.hashStackList(this.items);
    }

    public String toString() {
        return "SatchelContents" + String.valueOf(this.items);
    }

    public static class Mutable {
        private final List<ItemStack> items;
        private int itemCount = 0;

        public Mutable(SatchelContents contents) {
            this.items = new ArrayList(contents.items);
            this.itemCount = contents.getItemCount();
        }

        public SatchelContents.Mutable clearItems() {
            this.items.clear();
            this.itemCount = 0;
            return this;
        }

        private boolean isShard(ItemStack stack) {
            return stack.is(SBTags.Items.MAGIC_SHARD);
        }

        private int findStackIndex(ItemStack stack) {
            if (!isShard(stack)) return -1;
            for (int i = 0; i < this.items.size(); i++) {
                if (ItemStack.isSameItemSameComponents(stack, this.items.get(i))) return i;
            }

            return -1;
        }

        private int getAmountToAdd(ItemStack stack) {
            if (!isShard(stack)) return 0;

            int stackIndex = findStackIndex(stack);
            if (stackIndex == -1) return Math.min(stack.getCount(), Math.min(MAX_PER_STACK, TOTAL_MAX_ITEMS - this.itemCount));

            ItemStack existingStack = this.items.get(stackIndex);
            int remainingSpace = Math.min(MAX_PER_STACK - existingStack.getCount(), TOTAL_MAX_ITEMS - this.itemCount);
            return remainingSpace <= 0 ? 0 : Math.min(stack.getCount(), remainingSpace);
        }

        public int tryInsert(ItemStack stack) {
            if (stack.isEmpty() || !isShard(stack)) return 0;
            int toAdd = getAmountToAdd(stack);
            if (toAdd == 0) return 0;

            int stackIndex = findStackIndex(stack);
            if (stackIndex == -1) {
                this.items.addFirst(stack.copyWithCount(toAdd));
            } else {
                ItemStack ogStack = this.items.remove(stackIndex);
                this.items.addFirst(stack.copyWithCount(ogStack.getCount() + toAdd));
            }

            stack.shrink(toAdd);
            itemCount += toAdd;
            return toAdd;
        }

        public int tryTransfer(Slot slot) {
            if (this.items.isEmpty()) return 0;

            ItemStack stack = slot.getItem();
            ItemStack itemToPlace = this.items.getFirst();
            if (stack.isEmpty()) {
                ItemStack inserted = slot.safeInsert(itemToPlace);
                if (itemToPlace.isEmpty()) this.items.removeFirst();
                itemCount -= inserted.getCount();
                return inserted.getCount();
            }

            return 0;
        }

        public SatchelContents toImmutable() {
            return new SatchelContents(List.copyOf(this.items));
        }
    }
}
