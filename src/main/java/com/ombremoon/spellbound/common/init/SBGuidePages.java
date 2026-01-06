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
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.Nullable;

import java.awt.print.Book;
import java.util.List;
import java.util.function.Supplier;

public interface SBGuidePages {
    int PAGE_TWO_START_X = 169;
    int PAGE_START_Y = 8;
    int PAGE_START_DOUBLE_Y = 4;
    int PAGE_START_CENTER_X = 72;
    int PAGE_TWO_START_CENTER_X = 247;

    //Books
    ResourceLocation BASIC_BOOK = loc("studies_in_the_arcane");
    ResourceLocation RUIN_BOOK = loc("grimoire_of_annihilation");
    ResourceLocation TRANSFIG_BOOK = loc("architects_lexicon");
    ResourceLocation SUMMON_BOOK = loc("the_necronomicon");
    ResourceLocation DIVINE_BOOK = loc("sanctified_codex");
    ResourceLocation DECEPTION_BOOK = loc("swindlers_guide");

    //Ruin Book
    ResourceKey<GuideBookPage> RUIN_COVER_PAGE = key("ruin_cover_page");
    ResourceKey<GuideBookPage> RUIN_P2 = key("sb_ruin_v1_p2");
    ResourceKey<GuideBookPage> RUIN_P3 = key("sb_ruin_v1_p3");
    ResourceKey<GuideBookPage> SOLAR_RAY = key("solar_ray_page");

    //Transfig Book
    ResourceKey<GuideBookPage> TRANSFIG_COVER_PAGE = key("transfig_cover_page");
    ResourceKey<GuideBookPage> TRANSFIG_DESCRIPTION = key("transfig_cover_page"); //Description & Rituals
    ResourceKey<GuideBookPage> TRANSFIG_RITUALS = key("transfig_cover_page"); //Rituals & Layouts
    ResourceKey<GuideBookPage> TRANSFIG_RITUAL_ITEMS_1 = key("transfig_cover_page"); //Display, Pedestal, Chalk, and Talismans
    ResourceKey<GuideBookPage> TRANSFIG_RITUAL_ITEMS_2 = key("transfig_cover_page"); //Display, Pedestal, Chalk, and Talismans
    ResourceKey<GuideBookPage> TRANSFIG_ARMOR_STAFF = key("divine_description"); //Armor & Staff
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
    ResourceKey<GuideBookPage> DIVINE_DESCRIPTION = key("divine_description"); //Description & Judgement
    ResourceKey<GuideBookPage> DIVINE_JUDGEMENT = key("divine_judgement"); //Description & Judgement
    ResourceKey<GuideBookPage> DIVINE_TEMPLE_VALKYR = key("temple_and_valkyr"); //Temple & Valkyr
    ResourceKey<GuideBookPage> DIVINE_SHRINE = key("divine_description"); // Shrine & Spell Acquisition
    ResourceKey<GuideBookPage> DIVINE_ITEMS_1 = key("divine_description"); //Ambrosia, Holy Water, Blessed Bandages, Divine Phial
    ResourceKey<GuideBookPage> DIVINE_ITEMS_2 = key("divine_description"); //Ambrosia, Holy Water, Blessed Bandages, Divine Phial
    ResourceKey<GuideBookPage> DIVINE_ARMOR_STAFF = key("divine_description"); //Armor & Staff
    ResourceKey<GuideBookPage> DIViNE_SHARDS = key("divine_description"); //Holy & Corrupted Shards
    ResourceKey<GuideBookPage> HEALING_TOUCH = key("healing_touch");
    ResourceKey<GuideBookPage> HEALING_TOUCH_ACTIONS = key("healing_touch_actions");
    ResourceKey<GuideBookPage> HEALING_BLOSSOM = key("healing_blossom");
    ResourceKey<GuideBookPage> HEALING_BLOSSOM_ACTIONS = key("healing_blossom_actions");

    //Deception Book
    ResourceKey<GuideBookPage> DECEPTION_COVER_PAGE = key("deception_cover_page");

    //Basic Bookmarked pages
    ResourceKey<GuideBookPage> BASIC_COVER_PAGE = key("basic_cover_page");
    ResourceKey<GuideBookPage> BASIC_TRANSFIG_PAGE = key("basic_transfig_cover");
    ResourceKey<GuideBookPage> BASIC_SUMMON_PAGE = key("basic_summon_cover");
    ResourceKey<GuideBookPage> BASIC_DIVINE_PAGE = key("basic_divine_cover");
    ResourceKey<GuideBookPage> BASIC_DECEPTION_PAGE = key("basic_deception_cover");
    ResourceKey<GuideBookPage> BASIC_RUIN_PAGE = key("basic_ruin_cover");

    //Basic
    ResourceKey<GuideBookPage> SPELLBOUND_DESCRIPTION = key("basic_cover_page"); //What is Spellbound & Paths
    ResourceKey<GuideBookPage> IMPORTANT_ITEMS = key("basic_cover_page"); //Arcanthus, Magic Essence, and Workbench
    ResourceKey<GuideBookPage> BOOK_RECIPES = key("basic_cover_page"); //Book Recipes
    ResourceKey<GuideBookPage> SPELL_BROKER = key("basic_cover_page"); //Spell Broker
    ResourceKey<GuideBookPage> MORE_ITEMS = key("basic_cover_page"); //Shards, Armors & Staves
    ResourceKey<GuideBookPage> SPELLS = key("basic_cover_page"); //Spell Tomes & Choice Spells
    ResourceKey<GuideBookPage> SKILLS = key("basic_cover_page"); //Skills

    static void bootstrap(BootstrapContext<GuideBookPage> context) {

        register(
                context,
                BASIC_COVER_PAGE,
                PageBuilder
                        .forBook(BASIC_BOOK)
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
                                        .position(PAGE_TWO_START_X, 20)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("guide.basic.blurb")
                                        .position(PAGE_TWO_START_X, 40)
                                        .build()
                        )
        );

        //Ruin
        createCoverPage(context, RUIN_BOOK, RUIN_COVER_PAGE, SpellPath.RUIN);
        register(
                context,
                RUIN_P2,
                PageBuilder
                        .forBook(RUIN_BOOK)
                        .setPreviousPage(RUIN_COVER_PAGE)
                        .addElements(
                                PageBuilder.Image
                                        .of(CommonClass.customLocation("textures/gui/books/images/ruin_portal.png"))
                                        .setDimensions(140, 74)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("guide.ruin.v1_p2.ruin_portal")
                                        .position(0, 80)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("guide.ruin.v1_p2.keystone")
                                        .position(PAGE_TWO_START_X, 5)
                                        .build(),
                                PageBuilder.Recipe
                                        .of(ResourceLocation.withDefaultNamespace("crafting_table"))
                                        .gridName(PageBuilder.Recipe.SpellboundGrids.GRIMOIRE)
                                        .position(195, 125)
                                        .build()
                        )
        );
        register(
                context,
                RUIN_P3,
                PageBuilder
                        .forBook(RUIN_BOOK)
                        .setPreviousPage(RUIN_P2)
                        .addElements(
                                PageBuilder.Text
                                        .ofTranslatable("guide.ruin.v1_p3.keystones")
                                        .build(),
                                PageBuilder.Image
                                        .of(loc("textures/gui/books/images/broker_tower.png"))
                                        .setDimensions(150, 87)
                                        .position(PAGE_TWO_START_X, 0)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("guide.ruin.v1_p3.spell_broker")
                                        .position(PAGE_TWO_START_X, 95)
                                        .build()
                        )
        );
        createSpellPage(context, SOLAR_RAY, RUIN_P3, Book.RUIN, SBSpells.SOLAR_RAY);

        //Transfiguration
        createCoverPage(context, TRANSFIG_BOOK, TRANSFIG_COVER_PAGE, SpellPath.TRANSFIGURATION);
        createSpellPage(context, STRIDE, TRANSFIG_COVER_PAGE, Book.TRANSFIG, SBSpells.STRIDE);
        createRitualPage(context, STRIDE_RITUAL, STRIDE, SBRituals.CREATE_STRIDE, 5, 0, RitualTier.ONE);
        createSpellPage(context, SHADOW_GATE, STRIDE_RITUAL, Book.TRANSFIG, SBSpells.SHADOW_GATE);
        createRitualPage(context, SHADOW_GATE_RITUAL, SHADOW_GATE, SBRituals.CREATE_SHADOW_GATE, 10, 0, RitualTier.TWO);
        createSpellPage(context, MYSTIC_ARMOR, SHADOW_GATE_RITUAL, Book.TRANSFIG, SBSpells.MYSTIC_ARMOR);
        createRitualPage(context, MYSTIC_ARMOR_RITUAL, MYSTIC_ARMOR, SBRituals.CREATE_MYSTIC_ARMOR, 10, 0, RitualTier.TWO);

        //Summon
        createCoverPage(context, SUMMON_BOOK, SUMMON_COVER_PAGE, SpellPath.SUMMONS);
        createDescription(
                context,
                SUMMON_DESCRIPTION,
                SUMMON_COVER_PAGE,
                Book.SUMMONS,
                translatable("spellbound.path.summons"),
                translatable("guide.summon.dimensions"),
                false,
                new TextPosition(translatable("guide.summon.description1"), 35),
                new TextPosition(translatable("guide.summon.description2"), 100),
                new TextPosition(translatable("guide.summon.dimensions1"), PAGE_TWO_START_X, 35),
                new TextPosition(translatable("guide.summon.dimensions2"), PAGE_TWO_START_X, 120)
        );
        createDescriptionAndRecipeAndImage(context,
                SUMMON_PORTALS,
                SUMMON_DESCRIPTION,
                Book.SUMMONS,
                translatable("guide.summon.summoning_stone"),
                translatable("guide.summon.summoning_portal"),
                false,
                List.of(
                        new RecipePosition(ResourceLocation.withDefaultNamespace("anvil"), PAGE_START_CENTER_X-40, 90)
                ),
                List.of(
                        new ImageWithDimensions(loc("textures/gui/books/images/summoning_portal.png"), PAGE_TWO_START_X, 35,150, 80)
                ),
                new TextPosition(translatable("guide.summon.summoning_stone1"), 35),
                new TextPosition(translatable("guide.summon.summoning_portal1"), PAGE_TWO_START_X, 125));
        createDescription(context,
                SUMMON_PORTAL_ACTIVATION,
                SUMMON_PORTALS,
                Book.SUMMONS,
                translatable("guide.summon.portal_activation"),
                translatable("guide.summon.portal_activation"),
                false,
                new TextPosition(translatable("guide.summon.portal_activation1"), 35),
                new TextPosition(translatable("guide.summon.portal_activation2"), 110),
                new TextPosition(translatable("guide.summon.valid_portals"), PAGE_TWO_START_X, 35),
                new TextPosition(translatable("guide.summon.valid_portals1"), PAGE_TWO_START_X, 110)
                );
        createSpellPage(context, WILD_MUSHROOM, SUMMON_PORTAL_ACTIVATION, Book.SUMMONS, SBSpells.WILD_MUSHROOM);
        createSummonAcqPage(context, SUMMON_BOOK, MUSHROOM_ACQ, SUMMON_PORTAL_ACTIVATION, SBEntities.GIANT_MUSHROOM.get(), SBSpells.WILD_MUSHROOM.get());

        //Divine
        createCoverPage(context, DIVINE_BOOK, DIVINE_COVER_PAGE, SpellPath.DIVINE);
        createDescription(
                context,
                DIVINE_DESCRIPTION,
                DIVINE_COVER_PAGE,
                Book.DIVINE,
                translatable("spellbound.path.divine"),
                translatable("guide.divine.judgement"),
                false,
                new TextPosition(translatable("guide.divine.description1"), 35),
                new TextPosition(translatable("guide.divine.description2"), 100),
                new TextPosition(translatable("guide.divine.judgement1"), PAGE_TWO_START_X, 35),
                new TextPosition(translatable("guide.divine.judgement2"), PAGE_TWO_START_X, 80)
        );
        createDescription(
                context,
                DIVINE_JUDGEMENT,
                DIVINE_DESCRIPTION,
                Book.DIVINE,
                translatable("guide.divine.judgement_cont"),
                null,
                false,
                new TextPosition(translatable("guide.divine.judgement3"), 0, 35),
                new TextPosition(translatable("guide.divine.judgement4"), 0, 65),
                new TextPosition(translatable("guide.divine.judgement5"), PAGE_TWO_START_X, 10)
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
                        new ImageWithDimensions(loc("textures/gui/books/images/divine_temple.png"), 0, 95, 150, 80),
                        new ImageWithDimensions(loc("textures/gui/books/images/valkyr.png"), PAGE_TWO_START_X, 10, 50, 80, false)
                ),
                new TextPosition(translatable("guide.divine.divine_temple1"), 0, 30),
                new TextPosition(translatable("guide.divine.valkyr1"), PAGE_TWO_START_X + 55, 20, 100),
                new TextPosition(translatable("guide.divine.valkyr2"), PAGE_TWO_START_X, 95)
        );
        createDivineSpellPage(context, HEALING_TOUCH, DIVINE_TEMPLE_VALKYR, DIVINE_BOOK, SBSpells.HEALING_TOUCH, 0);
        Ingredient talisman = DataComponentIngredient.of(false, DataComponentPredicate.builder().expect(SBData.TALISMAN_RINGS.get(), 2).build(), SBItems.RITUAL_TALISMAN.get());
        createDivineActionPage(
                context,
                HEALING_TOUCH_ACTIONS,
                HEALING_TOUCH,
                SBSpells.HEALING_TOUCH,
                false,
                new ItemActionEntry(SBDivineActions.HEAL_MOB_TO_FULL, null, null, 5, 24000, 0, Ingredient.of(Items.SHEEP_SPAWN_EGG)),
                new ItemActionEntry(SBDivineActions.USE_BLESSED_BANDAGES, SBPageScraps.USE_BLESSED_BANDAGES, SBPageScraps.USE_BLESSED_BANDAGES_LORE, 5, 24000, 0, Ingredient.of(Items.GOLDEN_APPLE)),
                new ItemActionEntry(SBDivineActions.BLESS_SHRINE, SBPageScraps.BLESS_SHRINE, SBPageScraps.BLESS_SHRINE_LORE, 5, 24000, 10, talisman)
        );
        createDivineSpellPage(context, HEALING_BLOSSOM, HEALING_TOUCH_ACTIONS, DIVINE_BOOK, SBSpells.HEALING_BLOSSOM, 50);
        createDivineActionPage(
                context,
                HEALING_BLOSSOM_ACTIONS,
                HEALING_BLOSSOM,
                SBSpells.HEALING_BLOSSOM,
                true,
                new ItemActionEntry(SBDivineActions.DECORATE_SHRINE, SBPageScraps.DECORATE_SHRINE, SBPageScraps.DECORATE_SHRINE_LORE, 5, 24000, 0, Ingredient.of(ItemTags.FLOWERS)),
                new ImageActionEntry(SBDivineActions.GROW_AMBROSIA_BUSH, SBPageScraps.GROW_AMBROSIA_BUSH, SBPageScraps.GROW_AMBROSIA_BUSH_LORE, 10, 12000, 15, new ImageWithScale(defaultNameSpace("textures/block/sweet_berry_bush_stage3.png"))),
                new ItemActionEntry(SBDivineActions.PURIFY_WITHER_ROSE, SBPageScraps.PURIFY_WITHER_ROSE, SBPageScraps.PURIFY_WITHER_ROSE_LORE, 15, 6000, 35, Ingredient.of(Items.WITHER_ROSE))
        );

        //Deception
        createCoverPage(context, DECEPTION_BOOK, DECEPTION_COVER_PAGE, SpellPath.DECEPTION);
    }

    private static void createCoverPage(
            BootstrapContext<GuideBookPage> context,
            ResourceLocation forBook,
            ResourceKey<GuideBookPage> currentPage,
            SpellPath path
    ) {
        register(
                context,
                currentPage,
                PageBuilder
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
                                        .ofTranslatable("guide." + path.getSerializedName() + ".cover_page")
                                        .position(PAGE_TWO_START_CENTER_X, 65)
                                        .centered()
                                        .build()
                        )
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
            TextPosition... texts
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
                            .build()
            );
        }

        register(context, currentPage, builder);
    }

    private static void createDescriptionAndRecipeAndImage(
            BootstrapContext<GuideBookPage> context,
            ResourceKey<GuideBookPage> currentPage,
            ResourceKey<GuideBookPage> prevPage,
            Book book,
            MutableComponent title,
            @Nullable MutableComponent secondTitle,
            boolean doubleTitle,
            List<RecipePosition> recipes,
            List<ImageWithDimensions> images,
            TextPosition... texts
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
            }
            builder.addElements(imageBuilder.build());
        }

        register(context, currentPage, builder);
    }

    private static void createDescriptionAndImages(
            BootstrapContext<GuideBookPage> context,
            ResourceKey<GuideBookPage> currentPage,
            ResourceKey<GuideBookPage> prevPage,
            Book book,
            MutableComponent title,
            @Nullable MutableComponent secondTitle,
            boolean doubleTitle,
            List<ImageWithDimensions> images,
            TextPosition... texts
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
        for (var image : images) {
            var imageBuilder = PageBuilder.Image
                    .of(image.texture)
                    .position(image.xPos, image.yPos)
                    .setDimensions(image.width, image.height);
            if (!image.withCorners) {
                imageBuilder.disableCorners();
            }
            builder.addElements(imageBuilder.build());
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
        register(context,
                currentPage,
                PageBuilder
                        .forBook(book.getLocation())
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
                                        .build(),
                                PageBuilder.Image
                                        .of(CommonClass.customLocation("textures/gui/books/images/spells/" + spellType.location().getPath() + ".png"))
                                        .setDimensions(140, 74)
                                        .position(0, 40)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable(translations + "description")
                                        .position(0, 125)
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
                                        .setDimensions(140, 74)
                                        .position(0, 40)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable(translations + "description")
                                        .position(0, 125)
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
        var loreList = PageBuilder.TextList.of().position(PAGE_TWO_START_X + 10, 5).maxRows(3).rowGap(55).bulletPoint("");
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
                                .setDimensions(32 * scale, 32 * scale)
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

    record ItemActionEntry(ResourceKey<DivineAction> action, ResourceLocation actionScrap, ResourceLocation loreScrap, int judgement, int cooldown, int loreOffset, Ingredient ingredient) implements ActionEntry {}

    record EntityActionEntry(ResourceKey<DivineAction> action, ResourceLocation actionScrap, ResourceLocation loreScrap, int judgement, int cooldown, int loreOffset, EntityTranslations entity) implements ActionEntry {}

    record ImageActionEntry(ResourceKey<DivineAction> action, ResourceLocation actionScrap, ResourceLocation loreScrap, int judgement, int cooldown, int loreOffset, ImageWithScale image) implements ActionEntry {}

    record EntityTranslations(EntityType<?> entityType, int yOffset, float scale) {

        EntityTranslations(EntityType<?> entityType, float scale) {
            this(entityType, 0, scale);
        }

        EntityTranslations(EntityType<?> entityType) {
            this(entityType, 1.0F);
        }

        static <T extends Entity> EntityTranslations create(Supplier<EntityType<T>> entity, int yOffset, float scale) {
            return new EntityTranslations(entity.get(), yOffset, scale);
        }

        static <T extends Entity> EntityTranslations create(Supplier<EntityType<T>> entity, float scale) {
            return create(entity, 0, scale);
        }

        static <T extends Entity> EntityTranslations create(Supplier<EntityType<T>> entity) {
            return create(entity, 1.0F);
        }
    }

    record ImageWithScale(ResourceLocation texture, int xPos, int yPos, int scale) {

        ImageWithScale(ResourceLocation texture, int xPos, int yPos) {
            this(texture, xPos, yPos, 1);
        }

        ImageWithScale(ResourceLocation texture, int yPos) {
            this(texture, 0, yPos);
        }

        ImageWithScale(ResourceLocation texture) {
            this(texture, 0);
        }
    }

    record RecipePosition(ResourceLocation recipe, int xPos, int yPos) {}

    record ImageWithDimensions(ResourceLocation texture, int xPos, int yPos, int width, int height, boolean withCorners) {

        ImageWithDimensions(ResourceLocation texture, int xPos, int yPos, int width, int height) {
            this(texture, xPos, yPos, width, height, true);
        }
    }

    record TextPosition(Component text, int xPos, int yPos, int lineLength) {

        TextPosition(Component text, int xPos, int yPos) {
            this(text, xPos, yPos, 150);
        }

        TextPosition(Component text, int yPos) {
            this(text, 0, yPos);
        }
    }

    enum Book {
        BASIC(BASIC_BOOK, null, PageBuilder.Recipe.SpellboundGrids.BASIC),
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
