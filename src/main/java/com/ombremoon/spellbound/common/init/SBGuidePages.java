package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.datagen.provider.guide_builders.PageBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.ChatFormatting;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

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
    ResourceKey<GuideBookPage> STRIDE_INFO = key("stride_page_info");
    ResourceKey<GuideBookPage> STRIDE_ACQ = key("stride_page_acq");
    ResourceKey<GuideBookPage> SHADOW_GATE_INFO = key("shadow_gate_page_info");
    ResourceKey<GuideBookPage> SHADOW_GATE_ACQ = key("shadow_gate_page_acq");
    ResourceKey<GuideBookPage> MYSTIC_ARMOR_INFO = key("mystic_armor_page_info");
    ResourceKey<GuideBookPage> MYSTIC_ARMOR_ACQ = key("mystic_armor_page_acq");

    //Summon Book
    ResourceKey<GuideBookPage> SUMMON_COVER_PAGE = key("summon_cover_page");
    ResourceKey<GuideBookPage> MUSHROOM_ACQ = key("mushroom_page_acq");

    //Divine Book
    ResourceKey<GuideBookPage> DIVINE_COVER_PAGE = key("divine_cover_page");
    ResourceKey<GuideBookPage> HEALING_TOUCH_ACTIONS = key("healing_touch_actions");

    //Deception Book
    ResourceKey<GuideBookPage> DECEPTION_COVER_PAGE = key("deception_cover_page");

    //Basic Book
    ResourceKey<GuideBookPage> BASIC_COVER_PAGE = key("basic_cover_page");

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
        createSummonAcqPage(context, SUMMON, MUSHROOM_ACQ, SUMMON_COVER_PAGE, SBEntities.GIANT_MUSHROOM.get(), SBSpells.WILD_MUSHROOM.get());

        //Transfiguration
        createCoverPage(context, TRANSFIG, TRANSFIG_COVER_PAGE, SpellPath.TRANSFIGURATION);
        createSpellPage(context, STRIDE_INFO, TRANSFIG_COVER_PAGE, TRANSFIG, SBSpells.STRIDE);
        createRitualPage(context, STRIDE_ACQ, STRIDE_INFO, SBRituals.CREATE_STRIDE, 5, 0, RitualTier.ONE);
        createSpellPage(context, SHADOW_GATE_INFO, STRIDE_ACQ, TRANSFIG, SBSpells.SHADOW_GATE);
        createRitualPage(context, SHADOW_GATE_ACQ, SHADOW_GATE_INFO, SBRituals.CREATE_SHADOW_GATE, 10, 0, RitualTier.TWO);
        createSpellPage(context, MYSTIC_ARMOR_INFO, SHADOW_GATE_ACQ, TRANSFIG, SBSpells.MYSTIC_ARMOR);
        createRitualPage(context, MYSTIC_ARMOR_ACQ, MYSTIC_ARMOR_INFO, SBRituals.CREATE_MYSTIC_ARMOR, 10, 0, RitualTier.TWO);

        //Summon
        createCoverPage(context, SUMMON, SUMMON_COVER_PAGE, SpellPath.SUMMONS);

        //Divine
        createCoverPage(context, DIVINE, DIVINE_COVER_PAGE, SpellPath.DIVINE);
        register(
                context,
                HEALING_TOUCH_ACTIONS,
                PageBuilder
                        .forBook(DIVINE)
                        .addElements(
                                PageBuilder.Text
                                        .of(translatable("guide.divine.divine_actions").append(translatable("spells.spellbound.healing_touch")))
                                        .position(PAGE_START_CENTER_X, PAGE_START_DOUBLE_Y)
                                        .centered()
                                        .bold()
                                        .build(),
                                PageBuilder.SpellBorder
                                        .of(SpellPath.DIVINE)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("divine_action.healing_touch.heal_mob_to_full")
                                        .position(0, 40)
                                        .maxLineLength(100)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("divine_action.healing_touch.apply_blessed_bandages")
                                        .position(54, 93)
                                        .maxLineLength(100)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("divine_action.healing_touch.purify_shrine")
                                        .position(0, 150)
                                        .maxLineLength(100)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("divine_action.healing_touch.lore")
                                        .position(PAGE_TWO_START_X, 5)
                                        .build(),
                                PageBuilder.Tooltip
                                        .of()
                                        .addTooltip(literal("Shepherd").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE))
                                        .addTooltip(literal(""))
                                        .addTooltip(literal("Judgement: "))
                                        .addTooltip(literal("Cooldown: "))
                                        .position(0, 40)
                                        .dimensions(100, 40)
                                        .build(),
                                PageBuilder.Tooltip
                                        .of()
                                        .addTooltip(literal("TBD").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE))
                                        .addTooltip(literal(""))
                                        .addTooltip(literal("Judgement: "))
                                        .addTooltip(literal("Cooldown: "))
                                        .position(54, 93)
                                        .dimensions(100, 40)
                                        .build(),
                                PageBuilder.Tooltip
                                        .of()
                                        .addTooltip(literal("TBD").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE))
                                        .addTooltip(literal(""))
                                        .addTooltip(literal("Judgement: "))
                                        .addTooltip(literal("Cooldown: "))
                                        .position(0, 150)
                                        .dimensions(100, 40)
                                        .build(),
                                PageBuilder.StaticItem
                                        .of(Ingredient.of(Blocks.ZOMBIE_HEAD))
                                        .position(77, 7)
                                        .scale(2)
                                        .disableBackground()
                                        .build(),
                                PageBuilder.StaticItem
                                        .of(Ingredient.of(Items.SHEEP_SPAWN_EGG))
                                        .position(-26, 64)
                                        .scale(2)
                                        .disableBackground()
                                        .build(),
                                PageBuilder.StaticItem
                                        .of(Ingredient.of(Items.WITHER_ROSE))
                                        .position(77, 110)
                                        .scale(2)
                                        .disableBackground()
                                        .build()
                        )
        );

        //Deception
        createCoverPage(context, DECEPTION, DECEPTION_COVER_PAGE, SpellPath.DECEPTION);
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
                                PageBuilder.Text
                                        .ofLiteral("-------------------------")
                                        .position(-5, 55)
                                        .italic()
                                        .build(),
                                //TODO: Fix recipe
                                PageBuilder.Recipe
                                        .of(ResourceLocation.withDefaultNamespace("anvil"))
                                        .gridName(PageBuilder.Recipe.SpellboundGrids.NECRONOMICON)
                                        .position(PAGE_START_CENTER_X - 40, 85)
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
                                        .position(PAGE_TWO_START_CENTER_X, 100)
                                        .build(),
                                PageBuilder.Text
                                        .ofTranslatable("summon.acquisition.boss_rewards")
                                        .position(PAGE_TWO_START_CENTER_X, 150)
                                        .centered()
                                        .italic()
                                        .build(),
                                PageBuilder.ItemList
                                        .of()
                                        .addEntry(Ingredient.of(SBItems.SPELL_TOME.get()))
                                        .position(230, 180)
                                        .build()

                        ));
    }

    private static void createCoverPage(BootstrapContext<GuideBookPage> context,
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

    private static void register(BootstrapContext<GuideBookPage> context, ResourceKey<GuideBookPage> key, PageBuilder builder) {
        context.register(key, builder.build());
    }

    private static ResourceKey<GuideBookPage> key(String name) {
        return ResourceKey.create(Keys.GUIDE_BOOK, CommonClass.customLocation(name));
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

    enum RendererType {
        ITEM, STATIC_ITEM, ENTITY, IMAGE
    }
}
