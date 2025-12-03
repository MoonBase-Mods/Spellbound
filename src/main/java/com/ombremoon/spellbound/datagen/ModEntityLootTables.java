package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.world.loot.functions.SetSpellFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModEntityLootTables extends EntityLootSubProvider {

    protected ModEntityLootTables(HolderLookup.Provider registries) {
        super(FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    public void generate() {
        multiDropsWithChance(SBEntities.MINI_MUSHROOM.get(),
                new LootEntry(SBBlocks.WILD_MUSHROOM.get().asItem(), ConstantValue.exactly(1), 0.25F),
                new LootEntry(Items.BONE_MEAL, ConstantValue.exactly(1), 1.0F)
        );
        multiDrops(SBEntities.SPELL_BROKER.get(), new LootEntry(SBItems.SPELL_TOME.get(), ConstantValue.exactly(1), 0.25F, SetSpellFunction.setSpell(SBSpells.WILD_MUSHROOM)));
    }

    private void multiDrops(EntityType<?> type, LootEntry... entries) {
        LootPool.Builder pool = LootPool.lootPool();
        pool.setRolls(ConstantValue.exactly(1));
        for (LootEntry entry : entries) {
            var builder = LootItem.lootTableItem(entry.item())
                    .apply(SetItemCountFunction.setCount(entry.numberProvider()));

            for (var function : entry.builders) {
                builder.apply(function);
            }

            pool.add(builder);
        }
        this.add(type, LootTable.lootTable().withPool(pool));
    }

    private void dropRange(EntityType<?> entityType, Item item, float min, float max) {
        LootTable.Builder builder = LootTable.lootTable();
        builder.withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))));
        add(entityType, builder);
    }

    private void dropSingle(EntityType<?> entityType, Item item) {
        dropSetAmount(entityType, item, 1);
    }

    private void dropSetAmount(EntityType<?> entityType, Item item, float amount) {
        LootTable.Builder builder = LootTable.lootTable();
        builder.withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(amount)))));
        add(entityType, builder);
    }

    private void multiDropsWithChance(EntityType<?> type, LootEntry... entries) {
        LootPool.Builder pool = LootPool.lootPool();
        pool.setRolls(ConstantValue.exactly(1));
        for (LootEntry entry : entries) {
            var builder = LootItem.lootTableItem(entry.item())
                    .apply(SetItemCountFunction.setCount(entry.numberProvider()))
                    .when(LootItemRandomChanceCondition.randomChance(entry.chance()));

            for (var function : entry.builders) {
                builder.apply(function);
            }

            pool.add(builder);

        }
        this.add(type, LootTable.lootTable().withPool(pool));
    }

    private void dropRangeWithChance(EntityType<?> entityType, Item item, float min, float max, float chance) {
        LootTable.Builder builder = LootTable.lootTable();
        builder.withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))).when(LootItemRandomChanceCondition.randomChance(chance)));
        add(entityType, builder);
    }

    private void dropSingleWithChance(EntityType<?> entityType, Item item, float chance) {
        dropSetAmountWithChance(entityType, item, 1, chance);
    }

    private void dropSetAmountWithChance(EntityType<?> entityType, Item item, float amount, float chance) {
        LootTable.Builder builder = LootTable.lootTable();
        builder.withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(amount)))).when(LootItemRandomChanceCondition.randomChance(chance)));
        add(entityType, builder);
    }

    @Override
    protected boolean canHaveLootTable(EntityType<?> p_249029_) {
        return this.getKnownEntityTypes().toList().contains(p_249029_) && super.canHaveLootTable(p_249029_);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return SBEntities.MOBS.stream().map(Supplier::get);
    }

    record LootEntry(Item item, NumberProvider numberProvider, float chance, LootItemFunction.Builder... builders) {}
}
