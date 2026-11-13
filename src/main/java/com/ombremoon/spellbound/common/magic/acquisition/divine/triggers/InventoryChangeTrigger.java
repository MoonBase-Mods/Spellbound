package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class InventoryChangeTrigger extends SimpleTrigger<InventoryChangeTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Inventory inventory, ItemStack stack) {
        int i = 0;
        int j = 0;
        int k = 0;

        for (int l = 0; l < inventory.getContainerSize(); l++) {
            ItemStack itemstack = inventory.getItem(l);
            if (itemstack.isEmpty()) {
                j++;
            } else {
                k++;
                if (itemstack.getCount() >= itemstack.getMaxStackSize()) {
                    i++;
                }
            }
        }

        this.trigger(player, inventory, stack, i, j, k);
    }

    private void trigger(ServerPlayer player, Inventory inventory, ItemStack stack, int full, int empty, int occupied) {
        this.trigger(player, instance -> instance.matches(inventory, stack, full, empty, occupied));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Slots slots, List<ItemPredicate> items) implements Instance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        Slots.CODEC.optionalFieldOf("slots", Slots.ANY).forGetter(TriggerInstance::slots),
                        ItemPredicate.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(TriggerInstance::items)
                ).apply(instance, TriggerInstance::new)
        );

        public static ActionCriterion<TriggerInstance> hasItems(ItemPredicate.Builder... items) {
            return hasItems(Stream.of(items).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
        }

        public static ActionCriterion<TriggerInstance> hasItems(ItemPredicate... items) {
            return SBTriggers.INVENTORY_CHANGED.get()
                    .createCriterion(
                            new TriggerInstance(Optional.empty(), Slots.ANY, List.of(items))
                    );
        }

        public static ActionCriterion<TriggerInstance> hasItems(ItemLike... items) {
            ItemPredicate[] aitempredicate = new ItemPredicate[items.length];

            for (int i = 0; i < items.length; i++) {
                aitempredicate[i] = new ItemPredicate(
                        Optional.of(HolderSet.direct(items[i].asItem().builtInRegistryHolder())), MinMaxBounds.Ints.ANY, DataComponentPredicate.EMPTY, Map.of()
                );
            }

            return hasItems(aitempredicate);
        }

        public boolean matches(Inventory inventory, ItemStack stack, int full, int empty, int occupied) {
            if (!this.slots.matches(full, empty, occupied)) {
                return false;
            } else if (this.items.isEmpty()) {
                return true;
            } else if (this.items.size() != 1) {
                List<ItemPredicate> list = new ObjectArrayList<>(this.items);
                int i = inventory.getContainerSize();

                for (int j = 0; j < i; j++) {
                    if (list.isEmpty()) {
                        return true;
                    }

                    ItemStack itemstack = inventory.getItem(j);
                    if (!itemstack.isEmpty()) {
                        list.removeIf(predicate -> predicate.test(itemstack));
                    }
                }

                return list.isEmpty();
            } else {
                return !stack.isEmpty() && this.items.get(0).test(stack);
            }
        }

        public record Slots(MinMaxBounds.Ints occupied, MinMaxBounds.Ints full, MinMaxBounds.Ints empty) {
            public static final Codec<Slots> CODEC = RecordCodecBuilder.create(
                    instance -> instance.group(
                            MinMaxBounds.Ints.CODEC.optionalFieldOf("occupied", MinMaxBounds.Ints.ANY).forGetter(Slots::occupied),
                            MinMaxBounds.Ints.CODEC.optionalFieldOf("full", MinMaxBounds.Ints.ANY).forGetter(Slots::full),
                            MinMaxBounds.Ints.CODEC.optionalFieldOf("empty", MinMaxBounds.Ints.ANY).forGetter(Slots::empty)
                    ).apply(instance, Slots::new)
            );

            public static final Slots ANY = new Slots(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY);

            public boolean matches(int full, int empty, int occupied) {
                if (!this.full.matches(full)) {
                    return false;
                }

                return !this.empty.matches(empty) ? false : this.occupied.matches(occupied);
            }
        }
    }
}
