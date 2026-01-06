package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.acquisition.divine.DivineAction;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.datagen.provider.guide_builders.PageBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface SBGuidePages {
    int PAGE_TWO_START_X = 169;
    int PAGE_START_Y = 8;
    int PAGE_START_DOUBLE_Y = 4;
    int PAGE_START_CENTER_X = 72;
    int PAGE_TWO_START_CENTER_X = 247;

    //Books
    ResourceLocation BASIC = loc("studies_in_the_arcane");
    ResourceLocation RUIN = loc("grimoire_of_annihilation");
    ResourceLocation TRANSFIG = loc("architects_lexicon");
    ResourceLocation SUMMON = loc("the_necronomicon");
    ResourceLocation DIVINE = loc("sanctified_codex");
    ResourceLocation DECEPTION = loc("swindlers_guide");

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

    //Divine Book
    ResourceKey<GuideBookPage> DIVINE_COVER_PAGE = key("divine_cover_page");
    ResourceKey<GuideBookPage> DIVINE_DESCRIPTION = key("divine_description"); //Description & Judgement
    ResourceKey<GuideBookPage> DIVINE_TEMPLE_VALKYR = key("divine_description"); //Temple & Valkyr
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

    //Basic Book
    ResourceKey<GuideBookPage> BASIC_COVER_PAGE = key("basic_cover_page");
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
                        .forBook(BASIC)
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
        createCoverPage(context, RUIN, RUIN_COVER_PAGE, SpellPath.RUIN);
        register(
                context,
                RUIN_P2,
                PageBuilder
                        .forBook(RUIN)
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
                        .forBook(RUIN)
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
        createSpellPage(context, SOLAR_RAY, RUIN_P3, RUIN, SBSpells.SOLAR_RAY);

        //Transfiguration
        createCoverPage(context, TRANSFIG, TRANSFIG_COVER_PAGE, SpellPath.TRANSFIGURATION);
        createSpellPage(context, STRIDE, TRANSFIG_COVER_PAGE, TRANSFIG, SBSpells.STRIDE);
        createRitualPage(context, STRIDE_RITUAL, STRIDE, SBRituals.CREATE_STRIDE, 5, 0, RitualTier.ONE);
        createSpellPage(context, SHADOW_GATE, STRIDE_RITUAL, TRANSFIG, SBSpells.SHADOW_GATE);
        createRitualPage(context, SHADOW_GATE_RITUAL, SHADOW_GATE, SBRituals.CREATE_SHADOW_GATE, 10, 0, RitualTier.TWO);
        createSpellPage(context, MYSTIC_ARMOR, SHADOW_GATE_RITUAL, TRANSFIG, SBSpells.MYSTIC_ARMOR);
        createRitualPage(context, MYSTIC_ARMOR_RITUAL, MYSTIC_ARMOR, SBRituals.CREATE_MYSTIC_ARMOR, 10, 0, RitualTier.TWO);

        //Summon
        createCoverPage(context, SUMMON, SUMMON_COVER_PAGE, SpellPath.SUMMONS);

        //Divine
        createCoverPage(context, DIVINE, DIVINE_COVER_PAGE, SpellPath.DIVINE);
//        createDescriptionOnlyPage(context, );
        createDivineSpellPage(context, HEALING_TOUCH, DIVINE_COVER_PAGE, DIVINE, SBSpells.HEALING_TOUCH, 0);

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
        createDivineSpellPage(context, HEALING_BLOSSOM, HEALING_TOUCH_ACTIONS, DIVINE, SBSpells.HEALING_BLOSSOM, 50);
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
        createCoverPage(context, DECEPTION, DECEPTION_COVER_PAGE, SpellPath.DECEPTION);
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

    private static void createDescriptionOnlyPage(
            BootstrapContext<GuideBookPage> context,
            ResourceKey<GuideBookPage> currentPage,
            ResourceKey<GuideBookPage> prevPage,
            ResourceLocation book,
            Component title,
            @Nullable Component secondTitle,
            boolean doubleTitle,
            TextPosition... texts
    ) {
        var builder = PageBuilder.forBook(book).setPreviousPage(prevPage).addElements(
                PageBuilder.Text
                        .of(title)
                        .position(PAGE_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                        .bold()
                        .underline()
                        .centered()
                        .build()
        );
        if (secondTitle != null) {
            builder.addElements(
                    PageBuilder.Text
                            .of(title)
                            .position(PAGE_START_CENTER_X, doubleTitle ? PAGE_START_DOUBLE_Y : PAGE_START_Y)
                            .bold()
                            .underline()
                            .centered()
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

    private static <T extends AbstractSpell> void createSpellPage(BootstrapContext<GuideBookPage> context,
                                        ResourceKey<GuideBookPage> currentPage,
                                        ResourceKey<GuideBookPage> prevPage,
                                        ResourceLocation book,
                                        Supplier<SpellType<T>> spell
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
                    .forBook(TRANSFIG)
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

        PageBuilder builder = PageBuilder.forBook(DIVINE).setPreviousPage(prevPage);
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

    record ImageWithScale(ResourceLocation texture, int scale) {

        ImageWithScale(ResourceLocation texture) {
            this(texture, 1);
        }
    }

    record TextPosition(Component text, int xPos, int yPos) {

        TextPosition(Component text, int yPos) {
            this(text, 0, yPos);
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
