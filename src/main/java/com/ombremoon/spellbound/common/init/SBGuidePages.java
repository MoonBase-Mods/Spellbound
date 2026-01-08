package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.acquisition.divine.DivineAction;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.world.item.SpellTomeItem;
import com.ombremoon.spellbound.datagen.provider.guide_builders.PageBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public interface SBGuidePages {
    int PAGE_TWO_START_X = 172;
    int PAGE_START_Y = 8;
    int PAGE_START_DOUBLE_Y = 4;
    int PAGE_START_CENTER_X = 72;
    int PAGE_TWO_START_CENTER_X = 247;

    //Books
    ResourceLocation SPELLBOUND_BOOK = loc("studies_in_the_arcane");
    ResourceLocation RUIN_BOOK = loc("grimoire_of_annihilation");
    ResourceLocation TRANSFIG_BOOK = loc("architects_lexicon");
    ResourceLocation SUMMON_BOOK = loc("the_necronomicon");
    ResourceLocation DIVINE_BOOK = loc("sanctified_codex");
    ResourceLocation DECEPTION_BOOK = loc("swindlers_guide");

    //Ruin Book
    ResourceKey<GuideBookPage> RUIN_COVER_PAGE = key("ruin_cover_page");
    ResourceKey<GuideBookPage> RUIN_DESCRIPTION = key("ruin_description");
    ResourceKey<GuideBookPage> RUIN_SUB_PATHS = key("ruin_sub_paths");
    ResourceKey<GuideBookPage> RUIN_BUILD_UP = key("ruin_build_up");
    ResourceKey<GuideBookPage> RUIN_PORTALS = key("ruin_portals");
    ResourceKey<GuideBookPage> RUIN_ARMOR_STAFF = key("ruin_armor_staff");
    ResourceKey<GuideBookPage> STORM_STRIKE = key("storm_strike_page");
    ResourceKey<GuideBookPage> ELECTRIC_CHARGE = key("electric_charge_page");
    ResourceKey<GuideBookPage> SHATTERING_CRYSTAL = key("shattering_crystal_page");
    ResourceKey<GuideBookPage> SOLAR_RAY = key("solar_ray_page");
    ResourceKey<GuideBookPage> STORM_RIFT = key("storm_rift_page");

    //Transfig Book
    ResourceKey<GuideBookPage> TRANSFIG_COVER_PAGE = key("transfig_cover_page");
    ResourceKey<GuideBookPage> TRANSFIG_DESCRIPTION = key("transfig_description");
    ResourceKey<GuideBookPage> TRANSFIG_RITUALS = key("transfig_rituals");
    ResourceKey<GuideBookPage> TRANSFIG_RITUAL_ITEMS_1 = key("transfig_display_pedestal");
    ResourceKey<GuideBookPage> TRANSFIG_RITUAL_ITEMS_2 = key("transfig_talisman_chalk");
//    ResourceKey<GuideBookPage> TRANSFIG_ARMOR_STAFF = key("divine_description"); //Armor & Staff
//    ResourceKey<GuideBookPage> FLUX_SHARD = key("flux_shard"); //Flux Shard
    ResourceKey<GuideBookPage> STRIDE = key("stride");
    ResourceKey<GuideBookPage> STRIDE_RITUAL = key("stride_ritual");
    ResourceKey<GuideBookPage> SHADOW_GATE = key("shadow_gate_");
    ResourceKey<GuideBookPage> SHADOW_GATE_RITUAL = key("shadow_gate_page_ritual");
    ResourceKey<GuideBookPage> MYSTIC_ARMOR = key("mystic_armor");
    ResourceKey<GuideBookPage> MYSTIC_ARMOR_RITUAL = key("mystic_armor_ritual");

    //Summon Book
    ResourceKey<GuideBookPage> SUMMON_COVER_PAGE = key("summon_cover_page");
    ResourceKey<GuideBookPage> SUMMON_DESCRIPTION = key("summon_description");
    ResourceKey<GuideBookPage> SUMMON_PORTALS = key("summon_portals");
    ResourceKey<GuideBookPage> SUMMON_PORTAL_ACTIVATION = key("summon_portal_activation");
    ResourceKey<GuideBookPage> WILD_MUSHROOM = key("wild_mushroom");
    ResourceKey<GuideBookPage> MUSHROOM_ACQ = key("mushroom_page_acq");

    //Divine Book
    ResourceKey<GuideBookPage> DIVINE_COVER_PAGE = key("divine_cover_page");
    ResourceKey<GuideBookPage> DIVINE_DESCRIPTION = key("divine_description");
    ResourceKey<GuideBookPage> DIVINE_JUDGEMENT = key("divine_judgement");
    ResourceKey<GuideBookPage> DIVINE_TEMPLE_VALKYR = key("temple_and_valkyr");
    ResourceKey<GuideBookPage> DIVINE_SHRINE = key("divine_shrine");
//    ResourceKey<GuideBookPage> DIVINE_ITEMS_1 = key("divine_description"); //Ambrosia, Holy Water, Blessed Bandages, Divine Phial
//    ResourceKey<GuideBookPage> DIVINE_ITEMS_2 = key("divine_description"); //Ambrosia, Holy Water, Blessed Bandages, Divine Phial
//    ResourceKey<GuideBookPage> DIVINE_ARMOR_STAFF = key("divine_description"); //Armor & Staff
//    ResourceKey<GuideBookPage> DIViNE_SHARDS = key("divine_description"); //Holy & Corrupted Shards
    ResourceKey<GuideBookPage> HEALING_TOUCH = key("healing_touch");
    ResourceKey<GuideBookPage> HEALING_TOUCH_ACTIONS = key("healing_touch_actions");
    ResourceKey<GuideBookPage> HEALING_BLOSSOM = key("healing_blossom");
    ResourceKey<GuideBookPage> HEALING_BLOSSOM_ACTIONS = key("healing_blossom_actions");

    //Deception Book
    ResourceKey<GuideBookPage> DECEPTION_COVER_PAGE = key("deception_cover_page");
    ResourceKey<GuideBookPage> DECEPTION_DESCRIPTION = key("deception_description");
    ResourceKey<GuideBookPage> SHADOWBOND = key("shadowbond_page");
    ResourceKey<GuideBookPage> PURGE_MAGIC = key("purge_magic_page");

    //Basic
    ResourceKey<GuideBookPage> SPELLBOUND_COVER_PAGE = key("basic_cover_page");
    ResourceKey<GuideBookPage> SPELLBOUND_DESCRIPTION = key("spellbound_description");
    ResourceKey<GuideBookPage> SPELL_PATHS = key("spellbound_paths");
    ResourceKey<GuideBookPage> GENERAL_ITEMS = key("general_items");
    ResourceKey<GuideBookPage> WORKBENCH = key("magis_workbench");
    ResourceKey<GuideBookPage> BOOK_RECIPES = key("book_recipes");
    ResourceKey<GuideBookPage> BOOK_RECIPES_CONT = key("book_recipes_cont");
    ResourceKey<GuideBookPage> SPELL_RESEARCH = key("spell_research");
    ResourceKey<GuideBookPage> PATH_ITEMS = key("path_items"); //Shards, Armors & Staves
    ResourceKey<GuideBookPage> SPELLS = key("spells"); //Spell Mastery, Tomes & Choices
    ResourceKey<GuideBookPage> SKILLS = key("skills"); //Skills

    static void bootstrap(BootstrapContext<GuideBookPage> context) {
        Ingredient talisman1 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(SBData.TALISMAN_RINGS.get(), 1).build(), SBItems.RITUAL_TALISMAN.get());
        Ingredient talisman2 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(SBData.TALISMAN_RINGS.get(), 2).build(), SBItems.RITUAL_TALISMAN.get());
        Ingredient talisman3 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(SBData.TALISMAN_RINGS.get(), 3).build(), SBItems.RITUAL_TALISMAN.get());
        Ingredient chalk1 = Ingredient.of(SBItems.CHALK.get());
        Ingredient chalk2 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.LIGHT_GRAY.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk3 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.GRAY.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk4 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.BLACK.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk5 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.BROWN.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk6 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.RED.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk7 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.ORANGE.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk8 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.YELLOW.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk9 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.LIME.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk10 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.GREEN.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk11 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.CYAN.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk12 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.LIGHT_BLUE.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk13 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.BLUE.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk14 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.PURPLE.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk15 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.MAGENTA.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());
        Ingredient chalk16 = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.PINK.getTextureDiffuseColor(), false)).build(), SBItems.CHALK.get());

        //Basic
        register(
                context,
                SPELLBOUND_COVER_PAGE,
                PageBuilder
                        .forBook(SPELLBOUND_BOOK)
                        .addElements(
                                PageBuilder.Image
                                        .of(loc("textures/gui/books/images/spellbound_logo.png"))
                                        .setDimensions(64, 64)
                                        .position(40, 20)
                                        .disableCorners()
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("guide.basic.discord")
                                        .position(55, 100)
                                        .setLink("https://discord.gg/hagCkhVwfb")
                                        .underline()
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("guide.basic.bugs")
                                        .position(42, 115)
                                        .setLink("https://github.com/MoonBase-Mods/Spellbound/issues")
                                        .underline().build(),
                                PageBuilder.Text
                                        .ofTranslatable("item.spellbound.studies_in_the_arcane")
                                        .position(PAGE_TWO_START_CENTER_X, PAGE_START_Y)
                                        .centered()
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("guide.basic.blurb")
                                        .position(PAGE_TWO_START_CENTER_X, 65)
                                        .centered()
                                        .build()
                        )
        );
        createDescriptionAndImages(
                context,
                SPELLBOUND_DESCRIPTION,
                SPELLBOUND_COVER_PAGE,
                Book.SPELLBOUND,
                translatable("guide.basic.spellbound"),
                translatable("guide.basic.spell_paths"),
                false,
                List.of(
                        new ImageEntryWithDimensions(loc("textures/gui/paths/ruin.png"), PAGE_TWO_START_X - 14, 110, 32, 32, false)
                ),
                new TextEntry(translatable("guide.basic.description1"), 35),
                new TextEntry(translatable("guide.basic.description2"), 90),
                new TextEntry(translatable("guide.basic.spell_paths1"), PAGE_TWO_START_X, 35),
                new TextEntry(translatable("guide.basic.ruin"), PAGE_TWO_START_X + 16, 90, 134)
        );
        createDescriptionAndImages(
                context,
                SPELL_PATHS,
                SPELLBOUND_DESCRIPTION,
                Book.SPELLBOUND,
                null,
                null,
                false,
                List.of(
                        new ImageEntryWithDimensions(loc("textures/gui/paths/transfiguration.png"), -3, 27, 32, 32, false),
                        new ImageEntryWithDimensions(loc("textures/gui/paths/summons.png"), 124, 135, 32, 32, false),
                        new ImageEntryWithDimensions(loc("textures/gui/paths/divine.png"), PAGE_TWO_START_X + 121, 26, 32, 32, false),
                        new ImageEntryWithDimensions(loc("textures/gui/paths/deception.png"), PAGE_TWO_START_X - 10, 130, 32, 32, false)
                ),
                new TextEntry(translatable("guide.basic.transfiguration"), 31, 0, 119),
                new TextEntry(translatable("guide.basic.summons"), 0, 114, 130),
                new TextEntry(translatable("guide.basic.divine"), PAGE_TWO_START_X, 0, 134),
                new TextEntry(translatable("guide.basic.deception"), PAGE_TWO_START_X + 25, 97, 134)
        );
        createDescriptionWithRecipeAndItem(
                context,
                GENERAL_ITEMS,
                SPELL_PATHS,
                Book.SPELLBOUND,
                translatable("guide.basic.general_items"),
                null,
                false,
                List.of(
                        new RecipeEntry(loc("magic_essence"), PAGE_TWO_START_X + 16, 111)
                ),
                List.of(
                        new ItemEntry(Ingredient.of(blockToItem(SBBlocks.ARCANTHUS)), 84, 27, false),
                        new ItemEntry(Ingredient.of(SBItems.MANA_TEAR.get()), -39, 105, false),
                        new ItemEntry(Ingredient.of(SBItems.MAGIC_ESSENCE.get()), 234, 95, false)
                ),
                new TextEntry(translatable("guide.basic.arcanthus"), 0, 30, 115),
                new TextEntry(translatable("guide.basic.mana_tear"), 29, 120, 130),
                new TextEntry(translatable("guide.basic.magic_essence"), PAGE_TWO_START_X, 35)
        );
        createDescriptionWithRecipeAndImage(
                context,
                WORKBENCH,
                GENERAL_ITEMS,
                Book.SPELLBOUND,
                translatable("block.spellbound.magis_workbench"),
                null,
                false,
                List.of(
                        new RecipeEntry(loc("magis_workbench"), 65 , 100)
                ),
                List.of(
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/magis_workbench.png"), 1, 116, 64, 64, false),
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/workbench_screen.png"), PAGE_TWO_START_X, 5, 150, 83),
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/spell_select.png"), PAGE_TWO_START_X, 98, 150, 83)
                ),
                new TextEntry(translatable("guide.basic.workbench1"), 35)
        );
        createDescriptionWithRecipeAndItem(
                context,
                BOOK_RECIPES,
                WORKBENCH,
                Book.SPELLBOUND,
                translatable("guide.basic.book_recipes"),
                null,
                false,
                List.of(
                        new RecipeEntry(RUIN_BOOK, PAGE_TWO_START_X + 65, 5),
                        new RecipeEntry(TRANSFIG_BOOK, PAGE_TWO_START_X, 105)
                ),
                List.of(
                        new ItemEntry(Ingredient.of(SBItems.RUIN_BOOK.get()), PAGE_TWO_START_X - 20, 5, false),
                        new ItemEntry(Ingredient.of(SBItems.TRANSFIG_BOOK.get()), 239, 105, false)
                ),
                new TextEntry(translatable("guide.basic.guide_books"), 35),
                new TextEntry(translatable("guide.basic.guide_books1"), 5, 95),
                new TextEntry(translatable("guide.basic.guide_books2"), 5, 105),
                new TextEntry(translatable("guide.basic.guide_books3"), 5, 115),
                new TextEntry(translatable("guide.basic.guide_books4"), 5, 125),
                new TextEntry(translatable("guide.basic.guide_books5"), 5, 135),
                new TextEntry(translatable("guide.basic.guide_books6"), 5, 155),
                new TextEntry(translatable("item.spellbound.grimoire_of_annihilation"), PAGE_TWO_START_X + 8, 8, 60),
                new TextEntry(translatable("item.spellbound.architects_lexicon"), PAGE_TWO_START_X + 90, 105, 60)
        );
        createDescriptionWithRecipeAndItem(
                context,
                BOOK_RECIPES_CONT,
                BOOK_RECIPES,
                Book.SPELLBOUND,
                null,
                null,
                false,
                List.of(
                        new RecipeEntry(SUMMON_BOOK, 5),
                        new RecipeEntry(DIVINE_BOOK, 65, 105),
                        new RecipeEntry(DECEPTION_BOOK, PAGE_TWO_START_X, 55)
                ),
                List.of(
                        new ItemEntry(Ingredient.of(SBItems.SUMMON_BOOK.get()), 67, 5, false),
                        new ItemEntry(Ingredient.of(SBItems.DIVINE_BOOK.get()), -20, 105, false),
                        new ItemEntry(Ingredient.of(SBItems.DECEPTION_BOOK.get()), 239, 55, false)
                ),
                new TextEntry(translatable("item.spellbound.the_necronomicon"), 86, 8, 70),
                new TextEntry(translatable("item.spellbound.sanctified_codex"), 8, 105, 60),
                new TextEntry(translatable("item.spellbound.swindlers_guide"), PAGE_TWO_START_X + 90, 55, 60)
        );
        createDescriptionAndImages(
                context,
                SPELL_RESEARCH,
                BOOK_RECIPES_CONT,
                Book.SPELLBOUND,
                translatable("entity.spellbound.spell_broker"),
                translatable("guide.basic.page_scraps"),
                true,
                List.of(
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/spell_broker.png"), 75, 75, 75, 75)
                ),
                new TextEntry(translatable("guide.basic.spell_broker"), 35),
                new TextEntry(translatable("guide.basic.spell_broker1"), 0, 85, 75),
                new TextEntry(translatable("guide.basic.page_scraps1"), PAGE_TWO_START_X, 30),
                new TextEntry(translatable("guide.basic.page_scraps2"), PAGE_TWO_START_X + 25, 90, 130),
                new TextEntry(translatable("guide.basic.page_scraps3"), PAGE_TWO_START_X, 143, 130)
        );
        createDescriptionAndItems(
                context,
                PATH_ITEMS,
                SPELL_RESEARCH,
                Book.SPELLBOUND,
                translatable("guide.basic.path_items"),
                null,
                false,
                List.of(
                        new ItemEntry(Ingredient.of(SBTags.Items.MAGIC_SHARD), - 25, 85, false),
                        new ItemEntry(Ingredient.of(SBTags.Items.STAFF), PAGE_TWO_START_X - 28, 10, false),
                        new ItemEntry(Ingredient.of(SBItems.TRANSFIGURER_HELMET.get(), SBItems.PYROMANCER_HELMET.get(), SBItems.STORMWEAVER_HELMET.get(), SBItems.CRYOMANCER_HELMET.get()), 239, 105, false)
                ),
                new TextEntry(translatable("guide.basic.path_items1"), 35),
                new TextEntry(translatable("guide.basic.shards"), 55, 90, 100),
                new TextEntry(translatable("guide.basic.staves"), PAGE_TWO_START_X + 55, 5, 90),
                new TextEntry(translatable("guide.basic.armor"), PAGE_TWO_START_X, 115, 90)
        );
        createDescriptionAndImages(
                context,
                SPELLS,
                PATH_ITEMS,
                Book.SPELLBOUND,
                translatable("guide.basic.spells"),
                null,
                false,
                List.of(
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/spells.png"), PAGE_TWO_START_X + 4, 75, 125, 125)
                ),
                new TextEntry(translatable("guide.basic.spell_tomes"), 35),
                new TextEntry(translatable("guide.basic.spell_mastery"), 120),
                new TextEntry(translatable("guide.basic.choice_spells"), PAGE_TWO_START_X, PAGE_START_Y)
        );
        createDescriptionAndImages(
                context,
                SKILLS,
                SPELLS,
                Book.SPELLBOUND,
                translatable("guide.basic.skills"),
                null,
                false,
                List.of(
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/choice_spell.png"), PAGE_TWO_START_X + 34, 127, 75, 75)
                ),
                new TextEntry(translatable("guide.basic.skills1"), 35),
                new TextEntry(translatable("guide.basic.skills2"), 89),
                new TextEntry(translatable("guide.basic.skills3"), 157),
                new TextEntry(translatable("guide.basic.modifier_skills"), PAGE_TWO_START_X, 0),
                new TextEntry(translatable("guide.basic.conditional_skills"), PAGE_TWO_START_X, 49),
                new TextEntry(translatable("guide.basic.choice_skills"), PAGE_TWO_START_X, 79)
        );

        //Ruin
        createCoverPage(context, RUIN_BOOK, RUIN_COVER_PAGE, SpellPath.RUIN,
                new ContentsEntry(translatable("guide.ruin.contents.description"), RUIN_DESCRIPTION),
                new ContentsEntry(translatable("guide.ruin.subpaths_cnt"), RUIN_SUB_PATHS),
                new ContentsEntry(translatable("guide.ruin.build_up"), RUIN_BUILD_UP),
                new ContentsEntry(translatable("guide.ruin.portals"), RUIN_PORTALS),
                new ContentsEntry(translatable("guide.ruin.armor"), RUIN_ARMOR_STAFF),
                new ContentsEntry(spellName(SBSpells.STORMSTRIKE.get()), STORM_STRIKE),
                new ContentsEntry(spellName(SBSpells.ELECTRIC_CHARGE.get()), ELECTRIC_CHARGE),
                new ContentsEntry(spellName(SBSpells.SHATTERING_CRYSTAL.get()), SHATTERING_CRYSTAL),
                new ContentsEntry(spellName(SBSpells.SOLAR_RAY.get()), SOLAR_RAY),
                new ContentsEntry(spellName(SBSpells.STORM_RIFT.get()), STORM_RIFT));
        createDescription(context,
                RUIN_DESCRIPTION,
                RUIN_COVER_PAGE,
                Book.RUIN,
                translatable("spellbound.path.ruin"),
                translatable("guide.ruin.subpaths"),
                false,
                new TextEntry(translatable("guide.ruin.description1"), 35),
                new TextEntry(translatable("guide.ruin.description2"), 100),
                new TextEntry(translatable("guide.ruin.subpaths1"), PAGE_TWO_START_X, 35),
                new TextEntry(translatable("guide.ruin.subpaths2"), PAGE_TWO_START_X, 80)
        );
        createDescription(context,
                RUIN_SUB_PATHS,
                RUIN_DESCRIPTION,
                Book.RUIN,
                translatable("guide.ruin.subpaths_cnt"),
                null, false,
                new TextEntry(translatable("guide.ruin.fire"), 35),
                new TextEntry(translatable("guide.ruin.frost"), 100),
                new TextEntry(translatable("guide.ruin.shock"), PAGE_TWO_START_X, 35));
        createDescription(context,
                RUIN_BUILD_UP,
                RUIN_SUB_PATHS,
                Book.RUIN,
                translatable("guide.ruin.build_up"),
                translatable("guide.ruin.effects"),
                false,
                new TextEntry(translatable("guide.ruin.build_up1"), 35),
                new TextEntry(translatable("guide.ruin.fire_status"), PAGE_TWO_START_X, 35),
                new TextEntry(translatable("guide.ruin.frost_status"), PAGE_TWO_START_X, 90),
                new TextEntry(translatable("guide.ruin.shock_status"), PAGE_TWO_START_X, 135)
                );
        createDescription(context,
                RUIN_PORTALS,
                RUIN_BUILD_UP,
                Book.RUIN,
                translatable("guide.ruin.portals"),
                translatable("guide.ruin.keystones"),
                false,
                new TextEntry(translatable("guide.ruin.portals1"), 35),
                new TextEntry(translatable("guide.ruin.portals2"), 110),
                new TextEntry(translatable("guide.ruin.portals3"), PAGE_TWO_START_X, 35));
        createArmorDescription(context,
                RUIN_ARMOR_STAFF,
                RUIN_PORTALS,
                Book.RUIN,
                SpellPath.RUIN,
                translatable("guide.ruin.drip"),
                null,
                false,
                List.of(
                        new EquipmentEntry(
                                SBItems.STORMWEAVER_HELMET,
                                SBItems.STORMWEAVER_CHESTPLATE,
                                SBItems.STORMWEAVER_LEGGINGS,
                                SBItems.STORMWEAVER_BOOTS,
                                SBItems.SHOCK_STAFF,
                                0, 35)
                ),
                new TextEntry(translatable("guide.ruin.portals2"), 110),
                new TextEntry(translatable("guide.ruin.portals3"), PAGE_TWO_START_X, 35));

        createSpellPage(context, STORM_STRIKE, RUIN_PORTALS, Book.RUIN, SBSpells.STORMSTRIKE);
        createSpellPage(context, ELECTRIC_CHARGE, STORM_STRIKE, Book.RUIN, SBSpells.ELECTRIC_CHARGE);
        createSpellPage(context, SHATTERING_CRYSTAL, ELECTRIC_CHARGE, Book.RUIN, SBSpells.SHATTERING_CRYSTAL);
        createSpellPage(context, SOLAR_RAY, SHATTERING_CRYSTAL, Book.RUIN, SBSpells.SOLAR_RAY);
        createSpellPage(context, STORM_RIFT, SOLAR_RAY, Book.RUIN, SBSpells.STORM_RIFT);

        //Transfiguration
        createCoverPage(context, TRANSFIG_BOOK, TRANSFIG_COVER_PAGE, SpellPath.TRANSFIGURATION,
                new ContentsEntry(translatable("guide.transfiguration.description"), TRANSFIG_DESCRIPTION),
                new ContentsEntry(translatable("guide.transfiguration.rituals_cont"), TRANSFIG_RITUALS),
                new ContentsEntry(translatable("guide.transfiguration.blocks"), TRANSFIG_RITUAL_ITEMS_1),
                new ContentsEntry(translatable("guide.transfiguration.items"), TRANSFIG_RITUAL_ITEMS_2),
                new ContentsEntry(spellName(SBSpells.STRIDE.get()), STRIDE),
                new ContentsEntry(spellName(SBSpells.SHADOW_GATE.get()), SHADOW_GATE),
                new ContentsEntry(spellName(SBSpells.MYSTIC_ARMOR.get()), MYSTIC_ARMOR));
        createDescription(
                context,
                TRANSFIG_DESCRIPTION,
                TRANSFIG_COVER_PAGE,
                Book.TRANSFIG,
                translatable("spellbound.path.transfiguration"),
                translatable("guide.transfiguration.rituals"),
                false,
                new TextEntry(translatable("guide.transfiguration.description1"), 35),
                new TextEntry(translatable("guide.transfiguration.description2"), 100),
                new TextEntry(translatable("guide.transfiguration.rituals1"), PAGE_TWO_START_X, 35),
                new TextEntry(translatable("guide.transfiguration.rituals2"), PAGE_TWO_START_X, 125)
        );
        createDescriptionAndImages(
                context,
                TRANSFIG_RITUALS,
                TRANSFIG_DESCRIPTION,
                Book.TRANSFIG,
                translatable("guide.transfiguration.rituals_cont"),
                null,
                false,
                List.of(
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/ritual_tier1.png"), 9, 37, 70, 70, false),
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/ritual_tier2.png"), 60, 110, 70, 70, false),
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/ritual_tier3.png"), PAGE_TWO_START_X + 9, 9, 100, 100, false),
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/pedestal.png"), PAGE_TWO_START_X + 9, 111, 16, 16, false),
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/display.png"), PAGE_TWO_START_X + 9, 130, 16, 16, false),
                        new ImageEntryWithDimensions(loc("textures/block/rune/rune_1.png"), 95, 50, 16, 16, false),
                        new ImageEntryWithDimensions(loc("textures/block/rune/rune_5.png"), 124, 64, 16, 16, false),
                        new ImageEntryWithDimensions(loc("textures/block/rune/rune_17.png"), 103, 83, 16, 16, false),
                        new ImageEntryWithDimensions(loc("textures/block/rune/rune_4.png"), 15, 125, 16, 16, false), //8, 21 -- 14, 19
                        new ImageEntryWithDimensions(loc("textures/block/rune/rune_22.png"), 38, 139, 16, 16, false),
                        new ImageEntryWithDimensions(loc("textures/block/rune/rune_19.png"), 4, 148, 16, 16, false),
                        new ImageEntryWithDimensions(loc("textures/block/rune/rune_13.png"), 284, 25, 16, 16, false),
                        new ImageEntryWithDimensions(loc("textures/block/rune/rune_6.png"), 294, 52, 16, 16, false),
                        new ImageEntryWithDimensions(loc("textures/block/rune/rune_18.png"), 304, 75, 16, 16, false),
                        new ImageEntryWithDimensions(loc("textures/block/rune/rune_15.png"), 284, 75, 16, 16, false),
                        new ImageEntryWithDimensions(loc("textures/block/rune/rune_26.png"), 304, 25, 16, 16, false)
                ),
                new TextEntry(translatable("guide.transfiguration.pedestal_legend"), PAGE_TWO_START_X + 31, 115),
                new TextEntry(translatable("guide.transfiguration.display_legend"), PAGE_TWO_START_X + 31, 134),
                new TextEntry(translatable("guide.transfiguration.rune_circuit"), PAGE_TWO_START_X, 150)
        );
        createDescriptionWithRecipeAndItem(
                context,
                TRANSFIG_RITUAL_ITEMS_1,
                TRANSFIG_RITUALS,
                Book.TRANSFIG,
                translatable("block.spellbound.transfiguration_pedestal"),
                translatable("block.spellbound.transfiguration_display"),
                false,
                List.of(
                        new RecipeEntry(loc("transfiguration_pedestal"), 65, 35),
                        new RecipeEntry(loc("transfiguration_display"), PAGE_TWO_START_X, 95)
                ),
                List.of(
                        new ItemEntry(Ingredient.of(blockToItem(SBBlocks.TRANSFIGURATION_PEDESTAL)), -20, 35, false),
                        new ItemEntry(Ingredient.of(blockToItem(SBBlocks.TRANSFIGURATION_DISPLAY)), 239, 95, false)
                ),
                new TextEntry(translatable("guide.transfiguration.pedestal"), 0, 125),
                new TextEntry(translatable("guide.transfiguration.display"), PAGE_TWO_START_X, 35)
        );
        createDescriptionWithRecipeAndItem(
                context,
                TRANSFIG_RITUAL_ITEMS_2,
                TRANSFIG_RITUAL_ITEMS_1,
                Book.TRANSFIG,
                translatable("item.spellbound.chalk"),
                translatable("item.spellbound.ritual_talisman"),
                false,
                List.of(
                        new RecipeEntry(loc("chalk"), 0, 100),
                        new RecipeEntry(loc("ritual_talisman"), PAGE_TWO_START_X + 65, 35)
                ),
                List.of(
                        new ItemEntry(CompoundIngredient.of(chalk1, chalk2, chalk3, chalk4, chalk5, chalk6, chalk7, chalk8, chalk9, chalk10, chalk11, chalk12, chalk13, chalk14, chalk15, chalk16), 67, 100, false),
                        new ItemEntry(CompoundIngredient.of(talisman1, talisman2, talisman3), PAGE_TWO_START_X - 20, 35, false)
                ),
                new TextEntry(translatable("guide.transfiguration.chalk"), 0, 35),
                new TextEntry(translatable("guide.transfiguration.ritual_talisman"), PAGE_TWO_START_X, 125)
        );
        createSpellPage(context, STRIDE, TRANSFIG_RITUAL_ITEMS_2, Book.TRANSFIG, SBSpells.STRIDE);
        createRitualPage(context, STRIDE_RITUAL, STRIDE, SBRituals.CREATE_STRIDE, 5, 0, RitualTier.ONE);
        createSpellPage(context, SHADOW_GATE, STRIDE_RITUAL, Book.TRANSFIG, SBSpells.SHADOW_GATE);
        createRitualPage(context, SHADOW_GATE_RITUAL, SHADOW_GATE, SBRituals.CREATE_SHADOW_GATE, 10, 0, RitualTier.TWO);
        createSpellPage(context, MYSTIC_ARMOR, SHADOW_GATE_RITUAL, Book.TRANSFIG, SBSpells.MYSTIC_ARMOR);
        createRitualPage(context, MYSTIC_ARMOR_RITUAL, MYSTIC_ARMOR, SBRituals.CREATE_MYSTIC_ARMOR, 10, 0, RitualTier.TWO);

        //Summon
        createCoverPage(context, SUMMON_BOOK, SUMMON_COVER_PAGE, SpellPath.SUMMONS,
                new ContentsEntry(translatable("guide.summon.description"), SUMMON_DESCRIPTION),
                new ContentsEntry(translatable("guide.summon.summoning_stone"), SUMMON_PORTALS),
                new ContentsEntry(translatable("guide.summon.portal_activation"), SUMMON_PORTAL_ACTIVATION),
                new ContentsEntry(spellName(SBSpells.WILD_MUSHROOM.get()), WILD_MUSHROOM));
        createDescription(
                context,
                SUMMON_DESCRIPTION,
                SUMMON_COVER_PAGE,
                Book.SUMMONS,
                translatable("spellbound.path.summons"),
                translatable("guide.summon.dimensions"),
                false,
                new TextEntry(translatable("guide.summon.description1"), 35),
                new TextEntry(translatable("guide.summon.description2"), 100),
                new TextEntry(translatable("guide.summon.dimensions1"), PAGE_TWO_START_X, 35),
                new TextEntry(translatable("guide.summon.dimensions2"), PAGE_TWO_START_X, 120)
        );
        createDescriptionWithRecipeAndImage(context,
                SUMMON_PORTALS,
                SUMMON_DESCRIPTION,
                Book.SUMMONS,
                translatable("guide.summon.summoning_stone"),
                translatable("guide.summon.summoning_portal"),
                false,
                List.of(
                        new RecipeEntry(defaultNameSpace("anvil"), PAGE_START_CENTER_X-40, 90)
                ),
                List.of(
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/summoning_portal.png"), PAGE_TWO_START_X, 35,145, 67)
                ),
                new TextEntry(translatable("guide.summon.summoning_stone1"), 35),
                new TextEntry(translatable("guide.summon.summoning_portal1"), PAGE_TWO_START_X, 115)
        );
        createDescription(context,
                SUMMON_PORTAL_ACTIVATION,
                SUMMON_PORTALS,
                Book.SUMMONS,
                translatable("guide.summon.portal_activation"),
                translatable("guide.summon.portal_activation"),
                false,
                new TextEntry(translatable("guide.summon.portal_activation1"), 35),
                new TextEntry(translatable("guide.summon.portal_activation2"), 110),
                new TextEntry(translatable("guide.summon.valid_portals"), PAGE_TWO_START_X, 35),
                new TextEntry(translatable("guide.summon.valid_portals1"), PAGE_TWO_START_X, 110)
                );
        createSpellPage(context, WILD_MUSHROOM, SUMMON_PORTAL_ACTIVATION, Book.SUMMONS, SBSpells.WILD_MUSHROOM);
        createSummonAcqPage(context, SUMMON_BOOK, MUSHROOM_ACQ, SUMMON_PORTAL_ACTIVATION, SBEntities.GIANT_MUSHROOM.get(), SBSpells.WILD_MUSHROOM.get());

        //Divine
        createCoverPage(context, DIVINE_BOOK, DIVINE_COVER_PAGE, SpellPath.DIVINE,
                new ContentsEntry(translatable("guide.divine.divine_judgement"), DIVINE_DESCRIPTION),
                new ContentsEntry(translatable("guide.divine.judgement_cont"), DIVINE_JUDGEMENT),
                new ContentsEntry(translatable("guide.divine.divine_temple"), DIVINE_TEMPLE_VALKYR),
                new ContentsEntry(translatable("guide.divine.divine_shrine"), DIVINE_SHRINE),
                new ContentsEntry(spellName(SBSpells.HEALING_TOUCH.get()), HEALING_TOUCH),
                new ContentsEntry(spellName(SBSpells.HEALING_BLOSSOM.get()), HEALING_BLOSSOM));
        createDescription(
                context,
                DIVINE_DESCRIPTION,
                DIVINE_COVER_PAGE,
                Book.DIVINE,
                translatable("spellbound.path.divine"),
                translatable("guide.divine.judgement"),
                false,
                new TextEntry(translatable("guide.divine.description1"), 35),
                new TextEntry(translatable("guide.divine.description2"), 100),
                new TextEntry(translatable("guide.divine.judgement1"), PAGE_TWO_START_X, 35),
                new TextEntry(translatable("guide.divine.judgement2"), PAGE_TWO_START_X, 80)
        );
        createDescription(
                context,
                DIVINE_JUDGEMENT,
                DIVINE_DESCRIPTION,
                Book.DIVINE,
                translatable("guide.divine.judgement_cont"),
                null,
                false,
                new TextEntry(translatable("guide.divine.judgement3"), 0, 35),
                new TextEntry(translatable("guide.divine.judgement4"), 0, 65),
                new TextEntry(translatable("guide.divine.judgement5"), PAGE_TWO_START_X, 10)
        );
        createDescriptionAndImages(
                context,
                DIVINE_TEMPLE_VALKYR,
                DIVINE_JUDGEMENT,
                Book.DIVINE,
                translatable("guide.divine.divine_temple"),
                null,
                false,
                List.of(
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/divine_temple.png"), 0, 95, 150, 80),
                        new ImageEntryWithDimensions(loc("textures/gui/books/images/valkyr.png"), PAGE_TWO_START_X, 10, 50, 80, false)
                ),
                new TextEntry(translatable("guide.divine.divine_temple1"), 0, 30),
                new TextEntry(translatable("guide.divine.valkyr1"), PAGE_TWO_START_X + 55, 20, 100),
                new TextEntry(translatable("guide.divine.valkyr2"), PAGE_TWO_START_X, 95)
        );
        createDescriptionAndItems(
                context,
                DIVINE_SHRINE,
                DIVINE_TEMPLE_VALKYR,
                Book.DIVINE,
                translatable("guide.divine.divine_shrine"),
                null,
                false,
                List.of(
                        new ItemEntry(Ingredient.of(blockToItem(SBBlocks.PLAINS_DIVINE_SHRINE)), -28, 70, false),
                        new ItemEntry(Ingredient.of(blockToItem(SBBlocks.SANDSTONE_DIVINE_SHRINE)), 24, 70, false),
                        new ItemEntry(Ingredient.of(blockToItem(SBBlocks.JUNGLE_DIVINE_SHRINE)), 76, 70, false)
                ),
                new TextEntry(translatable("guide.divine.divine_shrine1"), 0, 35),
                new TextEntry(translatable("guide.divine.divine_shrine2"), 0, 140),
                new TextEntry(translatable("guide.divine.divine_action1"), PAGE_TWO_START_X, 10),
                new TextEntry(translatable("guide.divine.divine_action2"), PAGE_TWO_START_X, 90)
        );
        createDivineSpellPage(context, HEALING_TOUCH, DIVINE_SHRINE, DIVINE_BOOK, SBSpells.HEALING_TOUCH, 0);
        createDivineActionPage(
                context,
                HEALING_TOUCH_ACTIONS,
                HEALING_TOUCH,
                SBSpells.HEALING_TOUCH,
                false,
                new ItemActionEntry(SBDivineActions.HEAL_MOB_TO_FULL, null, null, 5, 24000, 0, Ingredient.of(Items.SHEEP_SPAWN_EGG)),
                new ItemActionEntry(SBDivineActions.USE_BLESSED_BANDAGES, SBPageScraps.USE_BLESSED_BANDAGES, SBPageScraps.USE_BLESSED_BANDAGES_LORE, 5, 24000, 0, Ingredient.of(Items.GOLDEN_APPLE)),
                new ItemActionEntry(SBDivineActions.BLESS_SHRINE, SBPageScraps.BLESS_SHRINE, SBPageScraps.BLESS_SHRINE_LORE, 5, 24000, 10, talisman2)
        );
        createDivineSpellPage(context, HEALING_BLOSSOM, HEALING_TOUCH_ACTIONS, DIVINE_BOOK, SBSpells.HEALING_BLOSSOM, 50);
        createDivineActionPage(
                context,
                HEALING_BLOSSOM_ACTIONS,
                HEALING_BLOSSOM,
                SBSpells.HEALING_BLOSSOM,
                true,
                new ItemActionEntry(SBDivineActions.DECORATE_SHRINE, SBPageScraps.DECORATE_SHRINE, SBPageScraps.DECORATE_SHRINE_LORE, 5, 24000, 0, Ingredient.of(ItemTags.FLOWERS)),
                new ImageActionEntry(SBDivineActions.GROW_AMBROSIA_BUSH, SBPageScraps.GROW_AMBROSIA_BUSH, SBPageScraps.GROW_AMBROSIA_BUSH_LORE, 10, 12000, 15, new ImageEntryWithScale(defaultNameSpace("textures/block/sweet_berry_bush_stage3.png"))),
                new ItemActionEntry(SBDivineActions.PURIFY_WITHER_ROSE, SBPageScraps.PURIFY_WITHER_ROSE, SBPageScraps.PURIFY_WITHER_ROSE_LORE, 15, 6000, 35, Ingredient.of(Items.WITHER_ROSE))
        );

        //Deception
        createCoverPage(context, DECEPTION_BOOK, DECEPTION_COVER_PAGE, SpellPath.DECEPTION,
                new ContentsEntry(translatable("spellbound.path.deception"), DECEPTION_DESCRIPTION),
                new ContentsEntry(spellName(SBSpells.SHADOWBOND.get()), SHADOWBOND),
                new ContentsEntry(spellName(SBSpells.PURGE_MAGIC.get()), PURGE_MAGIC));
        createDescription(context,
                DECEPTION_DESCRIPTION,
                DECEPTION_COVER_PAGE,
                Book.DECEPTION,
                translatable("spellbound.path.deception"),
                translatable("guide.deception.acquisition"),
                false,
                new TextEntry(translatable("guide.deception.description1"), 35),
                new TextEntry(translatable("guide.deception.description2"), 120),
                new TextEntry(translatable("guide.deception.acquisition1"), PAGE_TWO_START_X, 35),
                new TextEntry(translatable("guide.deception.acquisition2"), PAGE_TWO_START_X, 90));
        createSpellPage(context, SHADOWBOND, DECEPTION_DESCRIPTION, Book.DECEPTION, SBSpells.SHADOWBOND);
        createSpellPage(context, PURGE_MAGIC, SHADOWBOND, Book.DECEPTION, SBSpells.PURGE_MAGIC);
    }

    private static void createCoverPage(
            BootstrapContext<GuideBookPage> context,
            ResourceLocation forBook,
            ResourceKey<GuideBookPage> currentPage,
            SpellPath path,
            ContentsEntry... contents
    ) {

        var builder = PageBuilder
                .forBook(forBook)
                .addElements(
                        PageBuilder.Image
                                .of(loc("textures/gui/paths/" + path.getSerializedName() + ".png"))
                                .setDimensions(150, 150)
                                .position(0, 25)
                                .disableCorners()
                                .build(),
                        PageBuilder.SpellBorder
                                .of(path)
                                .setPosition(PAGE_TWO_START_X, 0)
                                .build(),
                        PageBuilder.Text
                                .ofTranslatable("item.spellbound." + forBook.getPath())
                                .position(PAGE_TWO_START_CENTER_X, PAGE_START_Y)
                                .centered()
                                .build(),
                        PageBuilder.Text
                                .ofTranslatable("guide.general.table_contents")
                                .position(PAGE_TWO_START_X, 40)
                                .bold()
                                .build(),
                        PageBuilder.Text
                                .ofTranslatable("guide." + path.getSerializedName() + ".quote")
                                .position(PAGE_TWO_START_CENTER_X, 160)
                                .centered()
                                .italic()
                                .build()
                );

        var list = PageBuilder.TextList
                .of()
                .position(PAGE_TWO_START_X+10, 50)
                .rowGap(10);

        for (ContentsEntry entry : contents) {
            list.addEntry(entry.comp, CommonClass.customLocation("default"), entry.targetPage().location());
        }

        builder.addElements(list.build());

        register(
                context,
                currentPage,
                builder
        );
    }

    private static void createDescription(
            BootstrapContext<GuideBookPage> context,
            ResourceKey<GuideBookPage> currentPage,
            ResourceKey<GuideBookPage> prevPage,
            Book book,
            MutableComponent title,
            @Nullable MutableComponent secondTitle,
            boolean doubleTitle,
            TextEntry... texts
    ) {
        createDescription(context, currentPage, prevPage, book, book.path, title, secondTitle, doubleTitle, texts);
    }
    private static void createDescription(
            BootstrapContext<GuideBookPage> context,
            ResourceKey<GuideBookPage> currentPage,
            ResourceKey<GuideBookPage> prevPage,
            Book book,
            SpellPath path,
            MutableComponent title,
            @Nullable MutableComponent secondTitle,
            boolean doubleTitle,
            TextEntry... texts
    ) {
        var builder = PageBuilder.forBook(book.getLocation()).setPreviousPage(prevPage).addElements(
                PageBuilder.Text
                        .of(title)
                        .position(PAGE_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                        .bold()
                        .centered()
                        .build(),
                PageBuilder.SpellBorder
                        .of(path)
                        .setPosition(0, 0)
                        .build()
        );
        if (secondTitle != null) {
            builder.addElements(
                    PageBuilder.Text
                            .of(secondTitle)
                            .position(PAGE_TWO_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                            .bold()
                            .centered()
                            .build(),
                    PageBuilder.SpellBorder
                            .of(path)
                            .setPosition(PAGE_TWO_START_X, 0)
                            .build()
            );
        }

        for (var text : texts) {
            builder.addElements(
                    PageBuilder.Text
                            .of(text.text)
                            .position(text.xPos, text.yPos)
                            .build()
            );
        }

        register(context, currentPage, builder);
    }

    private static void createArmorDescription(
            BootstrapContext<GuideBookPage> context,
            ResourceKey<GuideBookPage> currentPage,
            ResourceKey<GuideBookPage> prevPage,
            Book book,
            MutableComponent title,
            @Nullable MutableComponent secondTitle,
            boolean doubleTitle,
            List<EquipmentEntry> equipment,
            TextEntry... texts
    ) {
        createArmorDescription(context,
                currentPage,
                prevPage,
                book,
                book.getPath(),
                title,
                secondTitle,
                doubleTitle,
                equipment,
                texts);
    }

    private static void createArmorDescription(
            BootstrapContext<GuideBookPage> context,
            ResourceKey<GuideBookPage> currentPage,
            ResourceKey<GuideBookPage> prevPage,
            Book book,
            SpellPath path,
            MutableComponent title,
            @Nullable MutableComponent secondTitle,
            boolean doubleTitle,
            List<EquipmentEntry> equipment,
            TextEntry... texts
    ) {
        var builder = PageBuilder.forBook(book.getLocation()).setPreviousPage(prevPage).addElements(
                PageBuilder.Text
                        .of(title)
                        .position(PAGE_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                        .bold()
                        .centered()
                        .build(),
                PageBuilder.SpellBorder
                        .of(path)
                        .setPosition(0, 0)
                        .build()
        );
        if (secondTitle != null) {
            builder.addElements(
                    PageBuilder.Text
                            .of(secondTitle)
                            .position(PAGE_TWO_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                            .bold()
                            .centered()
                            .build(),
                    PageBuilder.SpellBorder
                            .of(path)
                            .setPosition(PAGE_TWO_START_X, 0)
                            .build()
            );
        }

        for (var text : texts) {
            builder.addElements(
                    PageBuilder.Text
                            .of(text.text)
                            .position(text.xPos, text.yPos)
                            .build()
            );
        }

        for (var stand : equipment) {
            builder.addElements(
                    PageBuilder.EquipmentRenderer
                            .of()
                            .setHelmet(stand.helmet())
                            .setChestplate(stand.chestplate())
                            .setLeggings(stand.leggings())
                            .setBoots(stand.boots())
                            .setOffHand(stand.offHand())
                            .setMainHand(stand.mainHand())
                            .setPosition(stand.x(), stand.y())
                            .build()

            );
        }

        register(context, currentPage, builder);
    }

    private static void createDescriptionWithRecipeAndImage(
            BootstrapContext<GuideBookPage> context,
            ResourceKey<GuideBookPage> currentPage,
            ResourceKey<GuideBookPage> prevPage,
            Book book,
            MutableComponent title,
            @Nullable MutableComponent secondTitle,
            boolean doubleTitle,
            List<RecipeEntry> recipes,
            List<ImageEntryWithDimensions> images,
            TextEntry... texts
    ) {
        var builder = PageBuilder.forBook(book.getLocation()).setPreviousPage(prevPage).addElements(
                PageBuilder.Text
                        .of(title)
                        .position(PAGE_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                        .bold()
                        .centered()
                        .build(),
                PageBuilder.SpellBorder
                        .of(book.getPath())
                        .setPosition(0, 0)
                        .build()
        );
        if (secondTitle != null) {
            builder.addElements(
                    PageBuilder.Text
                            .of(secondTitle)
                            .position(PAGE_TWO_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                            .bold()
                            .centered()
                            .build(),
                    PageBuilder.SpellBorder
                            .of(book.getPath())
                            .setPosition(PAGE_TWO_START_X, 0)
                            .build()
            );
        }
        for (var text : texts) {
            builder.addElements(
                    PageBuilder.Text
                            .of(text.text)
                            .position(text.xPos, text.yPos)
                            .maxLineLength(text.lineLength)
                            .build()
            );
        }
        for (var recipe : recipes) {
            builder.addElements(PageBuilder.Recipe
                    .of(recipe.recipe)
                    .position(recipe.xPos, recipe.yPos)
                    .gridName(book.getCraftingGrid()).build());
        }
        for (var image : images) {
            var imageBuilder = PageBuilder.Image
                    .of(image.texture)
                    .position(image.xPos, image.yPos)
                    .setDimensions(image.width, image.height);
            if (!image.withCorners) {
                imageBuilder.disableCorners();
            } else imageBuilder.setCornerTexture(CommonClass.customLocation("textures/gui/books/image_borders/" + book.bookLocation.getPath() + ".png"));
            builder.addElements(imageBuilder.build());
        }

        register(context, currentPage, builder);
    }

    private static void createDescriptionWithRecipeAndItem(
            BootstrapContext<GuideBookPage> context,
            ResourceKey<GuideBookPage> currentPage,
            ResourceKey<GuideBookPage> prevPage,
            Book book,
            @Nullable MutableComponent title,
            @Nullable MutableComponent secondTitle,
            boolean doubleTitle,
            List<RecipeEntry> recipes,
            List<ItemEntry> items,
            TextEntry... texts
    ) {
        var builder = PageBuilder.forBook(book.getLocation()).setPreviousPage(prevPage);
        if (title != null) {
            builder.addElements(
                    PageBuilder.Text
                            .of(title)
                            .position(PAGE_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                            .bold()
                            .centered()
                            .build(),
                    PageBuilder.SpellBorder
                            .of(book.getPath())
                            .setPosition(0, 0)
                            .build()
            );
        }
        if (secondTitle != null) {
            builder.addElements(
                    PageBuilder.Text
                            .of(secondTitle)
                            .position(PAGE_TWO_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                            .bold()
                            .centered()
                            .build(),
                    PageBuilder.SpellBorder
                            .of(book.getPath())
                            .setPosition(PAGE_TWO_START_X, 0)
                            .build()
            );
        }
        for (var text : texts) {
            builder.addElements(
                    PageBuilder.Text
                            .of(text.text)
                            .position(text.xPos, text.yPos)
                            .maxLineLength(text.lineLength)
                            .build()
            );
        }
        for (var recipe : recipes) {
            builder.addElements(PageBuilder.Recipe
                    .of(recipe.recipe)
                    .position(recipe.xPos, recipe.yPos)
                    .gridName(book.getCraftingGrid()).build());
        }
        for (var item : items) {
            var itemBuilder = PageBuilder.StaticItem
                    .of()
                    .addItem(item.item)
                    .position(item.xPos, item.yPos)
                    .scale(2)
                    .setRequiredScrap(item.scrap);
            if (!item.withBackground) {
                itemBuilder.disableBackground();
            }
            builder.addElements(itemBuilder.build());

            if (item.tooltip != null) {
                builder.addElements(
                        PageBuilder.Tooltip
                                .of()
                                .addTooltip(item.tooltip)
                                .position(item.xPos, item.yPos)
                                .dimensions(32, 32)
                                .build()
                );
            }
        }

        register(context, currentPage, builder);
    }

    private static void createDescriptionAndImages(
            BootstrapContext<GuideBookPage> context,
            ResourceKey<GuideBookPage> currentPage,
            ResourceKey<GuideBookPage> prevPage,
            Book book,
            @Nullable MutableComponent title,
            @Nullable MutableComponent secondTitle,
            boolean doubleTitle,
            List<ImageEntryWithDimensions> images,
            TextEntry... texts
    ) {
        var builder = PageBuilder.forBook(book.getLocation()).setPreviousPage(prevPage);
        if (title != null) {
            builder.addElements(
                    PageBuilder.Text
                            .of(title)
                            .position(PAGE_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                            .bold()
                            .centered()
                            .build(),
                    PageBuilder.SpellBorder
                            .of(book.getPath())
                            .setPosition(0, 0)
                            .build()
            );
        }
        if (secondTitle != null) {
            builder.addElements(
                    PageBuilder.Text
                            .of(secondTitle)
                            .position(PAGE_TWO_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                            .bold()
                            .centered()
                            .build(),
                    PageBuilder.SpellBorder
                            .of(book.getPath())
                            .setPosition(PAGE_TWO_START_X, 0)
                            .build()
            );
        }
        for (var text : texts) {
            builder.addElements(
                    PageBuilder.Text
                            .of(text.text)
                            .position(text.xPos, text.yPos)
                            .maxLineLength(text.lineLength)
                            .build()
            );
        }
        for (var image : images) {
            var imageBuilder = PageBuilder.Image
                    .of(image.texture)
                    .position(image.xPos, image.yPos)
                    .setDimensions(image.width, image.height);
            if (!image.withCorners) {
                imageBuilder.disableCorners();
            } else {
                imageBuilder.setCornerTexture(CommonClass.customLocation("textures/gui/books/image_borders/" + book.bookLocation.getPath() + ".png"));
            }

            builder.addElements(imageBuilder.build());
        }

        register(context, currentPage, builder);
    }

    private static void createDescriptionAndItems(
            BootstrapContext<GuideBookPage> context,
            ResourceKey<GuideBookPage> currentPage,
            ResourceKey<GuideBookPage> prevPage,
            Book book,
            MutableComponent title,
            @Nullable MutableComponent secondTitle,
            boolean doubleTitle,
            List<ItemEntry> items,
            TextEntry... texts
    ) {
        var builder = PageBuilder.forBook(book.getLocation()).setPreviousPage(prevPage).addElements(
                PageBuilder.Text
                        .of(title)
                        .position(PAGE_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                        .bold()
                        .centered()
                        .build(),
                PageBuilder.SpellBorder
                        .of(book.getPath())
                        .setPosition(0, 0)
                        .build()
        );
        if (secondTitle != null) {
            builder.addElements(
                    PageBuilder.Text
                            .of(secondTitle)
                            .position(PAGE_TWO_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                            .bold()
                            .centered()
                            .build(),
                    PageBuilder.SpellBorder
                            .of(book.getPath())
                            .setPosition(PAGE_TWO_START_X, 0)
                            .build()
            );
        }
        for (var text : texts) {
            builder.addElements(
                    PageBuilder.Text
                            .of(text.text)
                            .position(text.xPos, text.yPos)
                            .maxLineLength(text.lineLength)
                            .build()
            );
        }
        for (var item : items) {
            var itemBuilder = PageBuilder.StaticItem
                    .of()
                    .addItem(item.item)
                    .position(item.xPos, item.yPos)
                    .scale(2);
            if (!item.withBackground) {
                itemBuilder.disableBackground();
            }

            builder.addElements(itemBuilder.build());
        }

        register(context, currentPage, builder);
    }

    private static <T extends AbstractSpell> void createSpellPage(BootstrapContext<GuideBookPage> context,
                                        ResourceKey<GuideBookPage> currentPage,
                                        ResourceKey<GuideBookPage> prevPage,
                                        Book book,
                                        Supplier<SpellType<T>> spell
    ) {
        SpellType<?> spellType = spell.get();
        String translations = "guide." + spellType.getPath().name() + "." + spellType.location().getPath() + ".";

        PageBuilder builder = PageBuilder
                .forBook(book.getLocation())
                .setPreviousPage(prevPage)
                .addElements(
                        PageBuilder.Text
                                .of(spellName(spellType))
                                .position(PAGE_START_CENTER_X , PAGE_START_Y)
                                .centered()
                                .bold()
                                .build(),
                        PageBuilder.SpellBorder
                                .of(spellType)
                                .build(),
                        PageBuilder.Image
                                .of(CommonClass.customLocation("textures/gui/books/images/spells/" + spellType.location().getPath() + ".png"))
                                .setDimensions(140, 79)
                                .position(3, 30)
                                .disableCorners()
                                .build(),
                        PageBuilder.Text
                                .of(spell.get().getRootSkill().getDescription())
                                .position(0, 115)
                                .build(),
                        //Hi

                        PageBuilder.Text
                                .ofTranslatable(translations + "lore")
                                .position(PAGE_TWO_START_X, 5)
                                .build(),
                        PageBuilder.Text
                                .ofTranslatable(translations + "boss_lore")
                                .position(PAGE_TWO_START_X, 68)
                                .build(),
                        PageBuilder.SpellInfo
                                .of(spellType)
                                .alwaysShow()
                                .position(PAGE_TWO_START_X, 195)
                                .build()
                );

        register(context, currentPage, builder);
    }

    private static void createSummonAcqPage(BootstrapContext<GuideBookPage> context,
                                            ResourceLocation forBook,
                                            ResourceKey<GuideBookPage> currentPage,
                                            ResourceKey<GuideBookPage> prevPage,
                                            EntityType<?> boss,
                                            SpellType<?> spell
    ) {
        register(context,
                currentPage,
                PageBuilder
                        .forBook(forBook)
                        .setPreviousPage(prevPage)
                        .addElements(
                                PageBuilder.Text
                                        .ofTranslatable(spell.createSpell().getNameId())
                                        .position(PAGE_START_CENTER_X, PAGE_START_DOUBLE_Y)
                                        .centered()
                                        .bold()
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("summon.acquisition.description")
                                        .position(0, 35)
                                        .italic()
                                        .build(),
                                PageBuilder.SpellBorder
                                        .of(SpellPath.SUMMONS)
                                        .build(),
                                //TODO: Fix recipe
                                PageBuilder.Recipe
                                        .of(ResourceLocation.withDefaultNamespace("anvil"))
                                        .gridName(PageBuilder.Recipe.SpellboundGrids.NECRONOMICON)
                                        .position(PAGE_START_CENTER_X - 40, 65)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("summon.acquisition." + spell.location().getPath() + ".lore")
                                        .position(PAGE_START_CENTER_X, 155)
                                        .centered()
                                        .build(),

                                PageBuilder.Text
                                        .ofTranslatable(boss.getDescriptionId())
                                        .position(PAGE_TWO_START_CENTER_X, PAGE_START_DOUBLE_Y)
                                        .centered()
                                        .bold()
                                        .build(),
                                PageBuilder.EntityRenderer
                                        .of()
                                        .addEntity(boss)
                                        .animated()
                                        .position(PAGE_TWO_START_CENTER_X, 110)
                                        .setRotations(-22.5f, 45, 0)
                                        .scale(12f)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("summon.acquisition.boss_rewards")
                                        .position(PAGE_TWO_START_CENTER_X, 125)
                                        .centered()
                                        .italic()
                                        .build(),
                                PageBuilder.ItemList
                                        .of()
                                        .addEntry(SpellTomeItem.createWithSpell(spell))
                                        .centered()
                                        .maxRows(1)
                                        .position(230, 140)
                                        .build()

                        ));
    }

    private static <T extends AbstractSpell> void createDivineSpellPage(BootstrapContext<GuideBookPage> context,
                                        ResourceKey<GuideBookPage> currentPage,
                                        ResourceKey<GuideBookPage> prevPage,
                                        ResourceLocation book,
                                        Supplier<SpellType<T>> spell,
                                        int judgement
    ) {
        SpellType<?> spellType = spell.get();
        String translations = "guide." + spellType.getPath().name() + "." + spellType.location().getPath() + ".";
        register(context,
                currentPage,
                PageBuilder
                        .forBook(book)
                        .setPreviousPage(prevPage)
                        .addElements(
                                PageBuilder.Text
                                        .ofTranslatable("spells.spellbound." + spellType.location().getPath())
                                        .position(PAGE_START_CENTER_X, PAGE_START_Y)
                                        .centered()
                                        .bold()
                                        .build(),
                                PageBuilder.SpellBorder
                                        .of(spellType)
                                        .setBottomText(translatable("divine_action.judgement_required").append(literal(Integer.toString(judgement)).withStyle(judgement >= 0 ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED)))
                                        .build(),
                                PageBuilder.Image
                                        .of(CommonClass.customLocation("textures/gui/books/images/spells/" + spellType.location().getPath() + ".png"))
                                        .setDimensions(140, 79)
                                        .position(3, 30)
                                        .disableCorners()
                                        .build(),
                                PageBuilder.Text
                                        .of(spell.get().getRootSkill().getDescription())
                                        .position(0, 115)
                                        .build(),

                                PageBuilder.Text
                                        .ofTranslatable(translations + "lore")
                                        .position(PAGE_TWO_START_X, 5)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable(translations + "boss_lore")
                                        .position(PAGE_TWO_START_X, 68)
                                        .build(),
                                PageBuilder.SpellInfo
                                        .of(spellType)
                                        .alwaysShow()
                                        .position(PAGE_TWO_START_X, 195)
                                        .build()
                        ));
    }

    private static void createRitualPage(BootstrapContext<GuideBookPage> context,
                                         ResourceKey<GuideBookPage> currentPage,
                                         ResourceKey<GuideBookPage> prevPage,
                                         ResourceKey<TransfigurationRitual> key,
                                         int activationTime,
                                         int duration,
                                         RitualTier tier
    ) {
        register(
            context,
            currentPage,
            PageBuilder
                    .forBook(TRANSFIG_BOOK)
                    .setPreviousPage(prevPage)
                    .addElements(
                            PageBuilder.Text
                                    .ofTranslatable("ritual.spellbound." + key.location().getPath())
                                    .position(PAGE_START_CENTER_X, PAGE_START_DOUBLE_Y)
                                    .centered()
                                    .bold()
                                    .build(),
                            PageBuilder.SpellBorder
                                    .of(SpellPath.TRANSFIGURATION)
                                    .build(),
                            PageBuilder.Text
                                    .ofTranslatable("ritual.spellbound." + key.location().getPath() + ".description")
                                    .position(0, 35)
                                    .italic()
                                    .build(),
                            PageBuilder.SpellBorder
                                    .of(SpellPath.TRANSFIGURATION)
                                    .build(),
                            PageBuilder.Text
                                    .ofLiteral("-------------------------")
                                    .position(-5, 55)
                                    .italic()
                                    .build(),
                            PageBuilder.TextList
                                    .of()
                                    .addEntry(translatable("spellbound.ritual.tier_" + tier.name))
                                    .addEntry(translatable("spellbound.ritual.activation_time", Integer.toString(activationTime)))
                                    .addEntry(duration > 0 ? translatable("spellbound.ritual.duration", Integer.toString(duration)) : translatable("spellbound.ritual.duration_not_applicable"))
                                    .position(10, 65)
                                    .build(),
                            PageBuilder.Text
                                    .ofTranslatable("spellbound.ritual.materials")
                                    .position(PAGE_TWO_START_CENTER_X, PAGE_START_Y)
                                    .centered()
                                    .underline()
                                    .build(),
                            PageBuilder.RitualRenderer
                                    .of(key)
                                    .build()
                    )
        );
    }

    private static <T extends AbstractSpell> void createDivineActionPage(
            BootstrapContext<GuideBookPage> context,
            ResourceKey<GuideBookPage> currentPage,
            ResourceKey<GuideBookPage> prevPage,
            Supplier<SpellType<T>> spellType,
            boolean flip,
            ActionEntry... entries
    ) {
        if (entries.length > 3)
            throw new IllegalStateException("Cannot have more than 3 divine actions: " + entries.length);

        PageBuilder builder = PageBuilder.forBook(DIVINE_BOOK).setPreviousPage(prevPage);
        builder.addElements(
                PageBuilder.Text
                        .of(translatable("guide.divine.divine_actions").append(translatable("spells.spellbound." + spellType.get().location().getPath())))
                        .position(PAGE_START_CENTER_X, PAGE_START_DOUBLE_Y)
                        .centered()
                        .bold()
                        .build(),
                PageBuilder.SpellBorder
                        .of(SpellPath.DIVINE)
                        .build()
        );
        var loreList = PageBuilder.TextList.of().position(PAGE_TWO_START_X, 5).maxRows(3).rowGap(55).bulletPoint("");
        for (int i = 0; i < entries.length; i++) {
            ActionEntry entry = entries[i];
            String action = entry.action().location().getPath().replace("/", ".");
            int judgement = entry.judgement();
            boolean positiveJudgement = judgement >= 0;
            int xPos = i != 1 ? 0 : 54;
            if (flip) {
                xPos = i != 1 ? 54 : 0;
            }

            int yPos = i != 0 ? i != 1 ? 150 : 93 : 40;
            builder.addElements(
                    PageBuilder.Text
                            .ofTranslatable("divine_action." + action)
                            .position(xPos, yPos)
                            .maxLineLength(100)
                            .setRequiredScrap(entry.actionScrap() != null ? entry.actionScrap() : SBPageScraps.DEFAULT)
                            .build(),
                    PageBuilder.Tooltip
                            .of()
                            .addTooltip(translatable(action + ".name").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE))
                            .addTooltip(literal(""))
                            .addTooltip(translatable("divine_action.judgement").append(positiveJudgement ? literal("+" + judgement).withStyle(ChatFormatting.DARK_GREEN) : literal(Integer.toString(judgement)).withStyle(ChatFormatting.DARK_RED)))
                            .addTooltip(translatable("divine_action.cooldown", Integer.toString(entry.cooldown())))
                            .position(0, yPos)
                            .dimensions(155, 40)
                            .build()
            );

            if (entry instanceof ItemActionEntry itemEntry) {
                int xRenderPos = i != 1 ? 74 : -26;
                if (flip) {
                    xRenderPos = i != 1 ? -26 : 74;
                }

                int yRenderPos = i != 0 ? i != 1 ? 110 : 64 : 7;
                builder.addElements(
                        PageBuilder.StaticItem.of()
                                .addItem(itemEntry.ingredient)
                                .position(xRenderPos, yRenderPos)
                                .scale(2)
                                .disableBackground()
                                .build()
                );
            } else if (entry instanceof EntityActionEntry entityEntry) {
                int xRenderPos = i != 1 ? 126 : 25;
                if (flip) {
                    xRenderPos = i != 1 ? 25 : 126;
                }

                int yRenderPos = i != 0 ? i != 1 ? 180 : 130 : 75;
                builder.addElements(
                        PageBuilder.EntityRenderer.of()
                                .addEntity(entityEntry.entity.entityType)
                                .position(xRenderPos, yRenderPos + entityEntry.entity.yOffset)
                                .scale(25 * entityEntry.entity.scale)
                                .setRotations(-22.5F, 45, 0)
                                .build()
                );
            } else if (entry instanceof ImageActionEntry imageEntry) {
                int xRenderPos = i != 1 ? 112 : 9;
                if (flip) {
                    xRenderPos = i != 1 ? 9 : 112;
                }

                int yRenderPos = i != 0 ? i != 1 ? 140 : 89 : 37;
                int scale = imageEntry.image.scale;
                builder.addElements(
                        PageBuilder.Image.of(imageEntry.image.texture)
                                .position(xRenderPos, yRenderPos)
                                .setDimensions(64 * scale, 64 * scale)
                                .disableCorners()
                                .build()
                );
            }

            MutableComponent translation = translatable(action + ".lore");
            loreList.addEntry(translation, entry.loreScrap() != null ? entry.loreScrap() : SBPageScraps.DEFAULT, entry.loreOffset());
        }

        builder.addElements(loreList.build());
        register(context, currentPage, builder);
    }

    private static ItemLike blockToItem(Supplier<Block> block) {
        return blockToItem(block.get());
    }

    private static ItemLike blockToItem(Block block) {
        return block.asItem();
    }

    private static MutableComponent translatable(String text) {
        return Component.translatable(text);
    }

    private static MutableComponent translatable(String text, String append) {
        return Component.translatable(text, append);
    }

    private static MutableComponent literal(String text) {
        return Component.literal(text);
    }

    private static ResourceLocation loc(String path) {
        return CommonClass.customLocation(path);
    }

    private static ResourceLocation defaultNameSpace(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    private static void register(BootstrapContext<GuideBookPage> context, ResourceKey<GuideBookPage> key, PageBuilder builder) {
        context.register(key, builder.build());
    }

    private static ResourceKey<GuideBookPage> key(String name) {
        return ResourceKey.create(Keys.GUIDE_BOOK, CommonClass.customLocation(name));
    }

    private static MutableComponent spellName(SpellType<?> spell) {
        return translatable("spells.spellbound." + spell.location().getPath());
    }

    record ItemActionEntry(ResourceKey<DivineAction> action, ResourceLocation actionScrap, ResourceLocation loreScrap, int judgement, int cooldown, int loreOffset, Ingredient ingredient) implements ActionEntry {}

    record EntityActionEntry(ResourceKey<DivineAction> action, ResourceLocation actionScrap, ResourceLocation loreScrap, int judgement, int cooldown, int loreOffset, EntityEntry entity) implements ActionEntry {}

    record ImageActionEntry(ResourceKey<DivineAction> action, ResourceLocation actionScrap, ResourceLocation loreScrap, int judgement, int cooldown, int loreOffset, ImageEntryWithScale image) implements ActionEntry {}

    record EntityEntry(EntityType<?> entityType, int yOffset, float scale) {

        EntityEntry(EntityType<?> entityType, float scale) {
            this(entityType, 0, scale);
        }

        EntityEntry(EntityType<?> entityType) {
            this(entityType, 1.0F);
        }

        static <T extends Entity> EntityEntry create(Supplier<EntityType<T>> entity, int yOffset, float scale) {
            return new EntityEntry(entity.get(), yOffset, scale);
        }

        static <T extends Entity> EntityEntry create(Supplier<EntityType<T>> entity, float scale) {
            return create(entity, 0, scale);
        }

        static <T extends Entity> EntityEntry create(Supplier<EntityType<T>> entity) {
            return create(entity, 1.0F);
        }
    }

    record ImageEntryWithScale(ResourceLocation texture, int xPos, int yPos, int scale) {

        ImageEntryWithScale(ResourceLocation texture, int xPos, int yPos) {
            this(texture, xPos, yPos, 1);
        }

        ImageEntryWithScale(ResourceLocation texture, int yPos) {
            this(texture, 0, yPos);
        }

        ImageEntryWithScale(ResourceLocation texture) {
            this(texture, 0);
        }
    }

    record RecipeEntry(ResourceLocation recipe, int xPos, int yPos) {

        RecipeEntry(ResourceLocation recipe, int yPos) {
            this(recipe, 0, yPos);
        }
    }

    record ImageEntryWithDimensions(ResourceLocation texture, int xPos, int yPos, int width, int height, boolean withCorners) {

        ImageEntryWithDimensions(ResourceLocation texture, int xPos, int yPos, int width, int height) {
            this(texture, xPos, yPos, width, height, true);
        }
    }

    record EquipmentEntry(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack offHand, ItemStack mainHand, int x, int y) {

        EquipmentEntry(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, int x, int y) {
            this(helmet, chestplate, leggings, boots, ItemStack.EMPTY, ItemStack.EMPTY, x, y);
        }

        EquipmentEntry(Supplier<Item> helmet, Supplier<Item> chestplate, Supplier<Item> leggings, Supplier<Item> boots, int x, int y) {
            this(helmet.get().getDefaultInstance(),
                    chestplate.get().getDefaultInstance(),
                    leggings.get().getDefaultInstance(),
                    boots.get().getDefaultInstance(),
                    ItemStack.EMPTY,
                    ItemStack.EMPTY,
                    x, y);
        }

        EquipmentEntry(Supplier<Item> helmet, Supplier<Item> chestplate, Supplier<Item> leggings, Supplier<Item> boots, Supplier<Item> mainHand, int x, int y) {
            this(helmet.get().getDefaultInstance(),
                    chestplate.get().getDefaultInstance(),
                    leggings.get().getDefaultInstance(),
                    boots.get().getDefaultInstance(),
                    ItemStack.EMPTY,
                    mainHand.get().getDefaultInstance(),
                    x, y);
        }
    }

    record TextEntry(Component text, int xPos, int yPos, int lineLength, boolean centered, ResourceLocation scrap) {

        TextEntry(Component text, int xPos, int yPos, int lineLength, boolean centered) {
            this(text, xPos, yPos, lineLength, centered, SBPageScraps.DEFAULT);
        }

        TextEntry(Component text, int xPos, int yPos, int lineLength) {
            this(text, xPos, yPos, lineLength, false);
        }

        TextEntry(Component text, int xPos, int yPos) {
            this(text, xPos, yPos, 150);
        }

        TextEntry(Component text, int yPos) {
            this(text, 0, yPos);
        }
    }

    record ItemEntry(Ingredient item, int xPos, int yPos, boolean withBackground, MutableComponent tooltip, ResourceLocation scrap) {

        ItemEntry(Ingredient item, int xPos, int yPos, boolean withBackground, MutableComponent tooltip) {
            this(item, xPos, yPos, withBackground, tooltip, SBPageScraps.DEFAULT);
        }

        ItemEntry(Ingredient item, int xPos, int yPos, boolean withBackground) {
            this(item, xPos, yPos, withBackground, null);
        }

        ItemEntry(Ingredient item, int xPos, int yPos) {
            this(item, xPos, yPos, true);
        }
    }

    record ContentsEntry(Component comp, ResourceKey<GuideBookPage> targetPage) {}

    enum Book {
        SPELLBOUND(SPELLBOUND_BOOK, null, PageBuilder.Recipe.SpellboundGrids.BASIC),
        RUIN(RUIN_BOOK, SpellPath.RUIN, PageBuilder.Recipe.SpellboundGrids.GRIMOIRE),
        TRANSFIG(TRANSFIG_BOOK, SpellPath.TRANSFIGURATION, PageBuilder.Recipe.SpellboundGrids.ARCHITECT),
        SUMMONS(SUMMON_BOOK, SpellPath.SUMMONS, PageBuilder.Recipe.SpellboundGrids.NECRONOMICON),
        DIVINE(DIVINE_BOOK, SpellPath.DIVINE, PageBuilder.Recipe.SpellboundGrids.CODEX),
        DECEPTION(DECEPTION_BOOK, SpellPath.DECEPTION, PageBuilder.Recipe.SpellboundGrids.SWINDLER);

        private final ResourceLocation bookLocation;
        private final SpellPath path;
        private final PageBuilder.Recipe.SpellboundGrids craftingGrid;

        Book(ResourceLocation bookLocation, @Nullable SpellPath path, PageBuilder.Recipe.SpellboundGrids craftingGrid) {
            this.bookLocation = bookLocation;
            this.path = path;
            this.craftingGrid = craftingGrid;
        }

        public ResourceLocation getLocation() {
            return this.bookLocation;
        }

        public SpellPath getPath() {
            return this.path;
        }

        public PageBuilder.Recipe.SpellboundGrids getCraftingGrid() {
            return craftingGrid;
        }
    }

    enum RitualTier {
        ONE("one"),
        TWO("two"),
        THREE("three");

        private final String name;

        RitualTier(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    interface ActionEntry {
        ResourceKey<DivineAction> action();

        ResourceLocation loreScrap();

        ResourceLocation actionScrap();

        int judgement();

        int cooldown();

        int loreOffset();
    }
}
