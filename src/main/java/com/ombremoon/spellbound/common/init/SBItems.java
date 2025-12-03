package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.world.item.*;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SBItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);
    public static final List<Supplier<? extends Item>> SIMPLE_ITEM_LIST = new ArrayList<>();
    public static final List<Supplier<? extends Item>> EXCLUDED_ITEMS = new ArrayList<>();
    public static final List<Supplier<? extends Item>> BLOCK_ITEM_LIST = new ArrayList<>();

    public static final Supplier<Item> DEBUG = ITEMS.register("debug", () -> new DebugItem(getItemProperties()));
    public static final Supplier<Item> SOUL_SHARD = registerSimpleItem("soul_shard");
    public static final Supplier<Item> SMOLDERING_SHARD = registerSimpleItem("smoldering_shard");
    public static final Supplier<Item> FROZEN_SHARD = registerSimpleItem("frozen_shard");
    public static final Supplier<Item> STORM_SHARD = registerSimpleItem("storm_shard");
    public static final Supplier<Item> HOLY_SHARD = registerSimpleItem("holy_shard");
    public static final Supplier<Item> FOOL_SHARD = registerSimpleItem("fool_shard");

    public static final Supplier<Item> TRANSFIGURATION_STAFF = registerCatalystItem("transfiguration_staff", SpellPath.TRANSFIGURATION);
    public static final Supplier<Item> FIRE_STAFF = registerCatalystItem("fire_staff", SpellPath.FIRE);
    public static final Supplier<Item> ICE_STAFF = registerCatalystItem("ice_staff", SpellPath.FROST);
    public static final Supplier<Item> SHOCK_STAFF = registerCatalystItem("shock_staff", SpellPath.SHOCK);

    public static final Supplier<Item> TRANSFIGURER_HELMET = registerArmorItem("transfigurer_helmet", SBArmorMaterials.TRANSFIGURER, ArmorItem.Type.HELMET);
    public static final Supplier<Item> TRANSFIGURER_CHESTPLATE = registerArmorItem("transfigurer_chestplate", SBArmorMaterials.TRANSFIGURER, ArmorItem.Type.CHESTPLATE);
    public static final Supplier<Item> TRANSFIGURER_LEGGINGS = registerArmorItem("transfigurer_leggings", SBArmorMaterials.TRANSFIGURER, ArmorItem.Type.LEGGINGS);
    public static final Supplier<Item> TRANSFIGURER_BOOTS = registerArmorItem("transfigurer_boots", SBArmorMaterials.TRANSFIGURER, ArmorItem.Type.BOOTS);
    public static final Supplier<Item> PYROMANCER_HELMET = registerArmorItem("pyromancer_helmet", SBArmorMaterials.PYROMANCER, ArmorItem.Type.HELMET);
    public static final Supplier<Item> PYROMANCER_CHESTPLATE = registerArmorItem("pyromancer_chestplate", SBArmorMaterials.PYROMANCER, ArmorItem.Type.CHESTPLATE);
    public static final Supplier<Item> PYROMANCER_LEGGINGS = registerArmorItem("pyromancer_leggings", SBArmorMaterials.PYROMANCER, ArmorItem.Type.LEGGINGS);
    public static final Supplier<Item> PYROMANCER_BOOTS = registerArmorItem("pyromancer_boots", SBArmorMaterials.PYROMANCER, ArmorItem.Type.BOOTS);
    public static final Supplier<Item> CRYOMANCER_HELMET = registerArmorItem("cryomancer_helmet", SBArmorMaterials.CRYOMANCER, ArmorItem.Type.HELMET);
    public static final Supplier<Item> CRYOMANCER_CHESTPLATE = registerArmorItem("cryomancer_chestplate", SBArmorMaterials.CRYOMANCER, ArmorItem.Type.CHESTPLATE);
    public static final Supplier<Item> CRYOMANCER_LEGGINGS = registerArmorItem("cryomancer_leggings", SBArmorMaterials.CRYOMANCER, ArmorItem.Type.LEGGINGS);
    public static final Supplier<Item> CRYOMANCER_BOOTS = registerArmorItem("cryomancer_boots", SBArmorMaterials.CRYOMANCER, ArmorItem.Type.BOOTS);
    public static final Supplier<Item> STORMWEAVER_HELMET = registerArmorItem("stormweaver_helmet", SBArmorMaterials.STORMWEAVER, ArmorItem.Type.HELMET);
    public static final Supplier<Item> STORMWEAVER_CHESTPLATE = registerArmorItem("stormweaver_chestplate", SBArmorMaterials.STORMWEAVER, ArmorItem.Type.CHESTPLATE);
    public static final Supplier<Item> STORMWEAVER_LEGGINGS = registerArmorItem("stormweaver_leggings", SBArmorMaterials.STORMWEAVER, ArmorItem.Type.LEGGINGS);
    public static final Supplier<Item> STORMWEAVER_BOOTS = registerArmorItem("stormweaver_boots", SBArmorMaterials.STORMWEAVER, ArmorItem.Type.BOOTS);

    public static final Supplier<Item> SPELL_TOME = registerItem("spell_tome", () -> new SpellTomeItem(getItemProperties()), true, true);
    public static final Supplier<Item> MAGIC_ESSENCE = registerSimpleItem("magic_essence");
    public static final Supplier<Item> RITUAL_TALISMAN = registerItem("ritual_talisman", () -> new RitualTalismanItem(getItemProperties().durability(25)), true, true);
    public static final Supplier<Item> MANA_TEAR = registerItem("mana_tear", () -> new ManaTearItem(getItemProperties()));
    public static final Supplier<Item> CHALK = registerItem("chalk", () -> new ChalkItem(getItemProperties().stacksTo(16)));

    //Guide Books
    public static final Supplier<Item> STARTER_BOOK = registerItem("studies_in_the_arcane", () -> new GuideBookItem(CommonClass.customLocation("studies_in_the_arcane")));
    public static final Supplier<Item> RUIN_BOOK = registerItem("grimoire_of_annihilation", () -> new GuideBookItem(CommonClass.customLocation("grimoire_of_annihilation")));
    public static final Supplier<Item> DIVINE_BOOK = registerItem("sanctified_codex", () -> new GuideBookItem(CommonClass.customLocation("sanctified_codex")));
    public static final Supplier<Item> DECEPTION_BOOK = registerItem("swindlers_guide", () -> new GuideBookItem(CommonClass.customLocation("swindlers_guide")));
    public static final Supplier<Item> SUMMON_BOOK = registerItem("the_necronomicon", () -> new GuideBookItem(CommonClass.customLocation("the_necronomicon")));
    public static final Supplier<Item> TRANSFIG_BOOK = registerItem("architects_lexicon", () -> new GuideBookItem(CommonClass.customLocation("architects_lexicon")));

    public static final Supplier<CreativeModeTab> SPELL_TAB = CREATIVE_MODE_TABS.register("spell_tab", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP,0)
            .icon(() -> new ItemStack(SBBlocks.ARCANTHUS.get()))
            .displayItems(
                    (itemDisplayParameters,output)-> {
                        ITEMS.getEntries().forEach((registryObject)-> {
                            if (!EXCLUDED_ITEMS.contains(registryObject))
                                output.accept(new ItemStack(registryObject.get()));
                        });
                        for (int i = 0; i < 3; i++) {
                            ItemStack stack = new ItemStack(RITUAL_TALISMAN.get());
                            stack.set(SBData.TALISMAN_RINGS, i + 1);
                            output.accept(stack);
                        }
                        SBSpells.SPELL_TYPES.getEntries().forEach((registryObject) -> {
                            output.accept(SpellTomeItem.createWithSpell(registryObject.get()));
                        });
                    }).title(Component.translatable("itemGroup.spellbound"))
            .build());

    public static Supplier<Item> registerArmorItem(String name, Holder<ArmorMaterial> material, ArmorItem.Type type) {
        return registerItem(name, () -> new MageArmorItem(material, type, getItemProperties().stacksTo(1)));
    }

    public static Supplier<Item> registerCatalystItem(String name, SpellPath path) {
        return registerItem(name, () -> new CatalystItem(path, getItemProperties().stacksTo(1)), true);
    }

    public static Supplier<Item> registerSimpleItem(String name) {
        return registerItem(name, () -> new Item(getItemProperties()));
    }

    public static Supplier<Item> registerItem(String name, Supplier<Item> itemSupplier) {
        return registerItem(name, itemSupplier, false);
    }

    public static Supplier<Item> registerItem(String name, Supplier<Item> itemSupplier, boolean excludeModel) {
        return registerItem(name, itemSupplier, excludeModel, false);
    }

    public static Supplier<Item> registerItem(String name, Supplier<Item> itemSupplier, boolean excludeModel, boolean excludeTab) {
        Supplier<Item> item = ITEMS.register(name, itemSupplier);

        if (!excludeModel)
            SIMPLE_ITEM_LIST.add(item);

        if (excludeTab)
            EXCLUDED_ITEMS.add(item);

        return item;
    }

    public static Supplier<Item> registerBlockItem(String name, DeferredBlock<? extends Block> block) {
        Supplier<Item> item = ITEMS.register(name, () -> new BlockItem(block.get(), getItemProperties()));
        BLOCK_ITEM_LIST.add(item);
        return item;
    }

    public static Item.Properties getItemProperties() {
        return new Item.Properties();
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
