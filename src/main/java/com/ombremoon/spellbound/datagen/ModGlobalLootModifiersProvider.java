package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.datagen.loot.AddItemModifier;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.EnchantmentActiveCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Constants.MOD_ID);
    }

    @Override
    protected void start() {
        addToMultipleStructuresLootTable(
                "pyromancer",
                List.of(
                        vanillaChest("nether_bridge"),
                        vanillaChest("ruined_portal"),
                        vanillaChest("bastion_treasure")
                ),
                0.2F,
                SBItems.PYROMANCER_BOOTS,
                SBItems.PYROMANCER_CHESTPLATE,
                SBItems.PYROMANCER_LEGGINGS,
                SBItems.PYROMANCER_HELMET,
                SBItems.FIRE_STAFF
        );

        addToMultipleStructuresLootTable(
                "cryomancer",
                List.of(
                        vanillaChest("igloo_chest"),
                        vanillaChest("shipwreck_treasure")
                ),
                0.3F,
                SBItems.CRYOMANCER_HELMET,
                SBItems.CRYOMANCER_CHESTPLATE,
                SBItems.CRYOMANCER_LEGGINGS,
                SBItems.CRYOMANCER_BOOTS,
                SBItems.ICE_STAFF
        );

        addToMultipleStructuresLootTable(
                "cryomancer_village",
                List.of(
                        vanillaChest("village/village_snowy_house")
                ),
                0.02F,
                SBItems.CRYOMANCER_HELMET,
                SBItems.CRYOMANCER_CHESTPLATE,
                SBItems.CRYOMANCER_LEGGINGS,
                SBItems.CRYOMANCER_BOOTS,
                SBItems.ICE_STAFF
        );

        addToMultipleStructuresLootTable(
                "stormweaver",
                List.of(
                        vanillaChest("trial_chamber/reward_rare"),
                        vanillaChest("trial_chamber/reward_common"),
                        vanillaChest("trial_chamber/intersection"),
                        vanillaChest("trial_chamber/corridor")
                ),
                0.05F,
                SBItems.STORMWEAVER_HELMET,
                SBItems.STORMWEAVER_CHESTPLATE,
                SBItems.STORMWEAVER_LEGGINGS,
                SBItems.STORMWEAVER_BOOTS,
                SBItems.SHOCK_STAFF
        );

        addToMultipleStructuresLootTable(
                "stormweaver",
                List.of(
                        vanillaChest("trial_chamber/reward_rare"),
                        vanillaChest("trial_chamber/reward_common"),
                        vanillaChest("trial_chamber/intersection"),
                        vanillaChest("trial_chamber/corridor")
                ),
                0.4F,
                SBItems.STORMWEAVER_HELMET,
                SBItems.STORMWEAVER_CHESTPLATE,
                SBItems.STORMWEAVER_LEGGINGS,
                SBItems.STORMWEAVER_BOOTS,
                SBItems.SHOCK_STAFF
        );

        addToEntityLootTable("witch_arcanthus", EntityType.WITCH, 0.2F, new ItemStack(SBBlocks.ARCANTHUS.get(),  2));
    }

    protected void addToEntityLootTable(String modifierName, EntityType<?> entityType, float dropChance, ItemStack item) {
        add(modifierName, new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(entityType.getDefaultLootTable().location()).build(),
        }, dropChance, item));
    }

    protected void addToStructureLootTable(String modifierName, ResourceLocation resourceLocation, float probabilityChance, ItemStack... item) {
        add(modifierName, new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(resourceLocation).build()
        }, probabilityChance, item));
    }

    @SafeVarargs
    protected final void addToStructureLootTable(String modifierName, ResourceLocation resourceLocation, float probabilityChance, Supplier<Item>... item) {
        add(modifierName, new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(resourceLocation).build()
        }, probabilityChance, item));
    }

    @SafeVarargs
    protected final void addToMultipleStructuresLootTable(String modifierName, List<ResourceLocation> resLoc, float probabilityChance, Supplier<Item>... items) {
        for (ResourceLocation structure : resLoc) {
            addToStructureLootTable(structure.getPath() + "_" + modifierName, structure, probabilityChance, items);
        }
    }

    private ResourceLocation vanillaChest(String loc) {
        return ResourceLocation.withDefaultNamespace("chests/" + loc);
    }

    private ResourceLocation loc(String loc) {
        return CommonClass.customLocation(loc);
    }
}
