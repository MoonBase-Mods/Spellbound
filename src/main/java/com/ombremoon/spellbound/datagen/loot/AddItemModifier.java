package com.ombremoon.spellbound.datagen.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTags;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class AddItemModifier extends IndividualChanceLootModifier {
    public static final MapCodec<AddItemModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            IndividualChanceLootModifier.codecStart(inst).and(
                    ItemStack.STRICT_CODEC.listOf().fieldOf("items").forGetter(e -> e.items)).apply(inst, AddItemModifier::new));
    public List<ItemStack> items;

    public AddItemModifier(LootItemCondition[] conditions, LootItemRandomChanceCondition chanceCon , List<ItemStack> items) {
        super(conditions, chanceCon);
        this.items = items;
    }

    public AddItemModifier(LootItemCondition[] conditionsIn, float chance, ItemStack... items) {
        super(conditionsIn, (LootItemRandomChanceCondition) LootItemRandomChanceCondition.randomChance(chance).build());
        this.items = List.of(items);
    }

    public AddItemModifier(LootItemCondition[] conditionsIn, float chance, Supplier<Item>... items) {
        super(conditionsIn, (LootItemRandomChanceCondition) LootItemRandomChanceCondition.randomChance(chance).build());
        this.items = Arrays
                .stream(items)
                .map(item -> item.get().getDefaultInstance())
                .toList();
    }

    public AddItemModifier(LootItemCondition[] conditionsIn, float chance, Item... items) {
        super(conditionsIn, (LootItemRandomChanceCondition) LootItemRandomChanceCondition.randomChance(chance).build());
        this.items = Arrays
                .stream(items)
                .map(item -> item.getDefaultInstance())
                .toList();
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootItemRandomChanceCondition chanceCon, LootContext lootContext) {
        for (ItemStack stack : this.items) {
            if (chanceCon.test(lootContext)) generatedLoot.add(stack);
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
