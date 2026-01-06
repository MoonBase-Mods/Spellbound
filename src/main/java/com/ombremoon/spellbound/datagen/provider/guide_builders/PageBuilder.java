package com.ombremoon.spellbound.datagen.provider.guide_builders;

import com.ombremoon.spellbound.client.gui.guide.elements.TransfigurationRitualElement;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.client.gui.guide.elements.*;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.*;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PageBuilder {
    private ResourceLocation bookId;
    private ResourceLocation pageScrap;
    private ResourceLocation insertAfter;
    private List<IPageElement> elements;

    private PageBuilder(ResourceLocation bookId) {
        this.bookId = bookId;
        this.insertAfter = GuideBookManager.FIRST_PAGE;
        this.pageScrap = GuideBookManager.FIRST_PAGE;
        this.elements = new ArrayList<>();
    }

    /**
     * Creates a builder and specifies what book the page will be for.
     * @param bookId The id for the book the page belongs to
     * @return new Builder
     */
    public static PageBuilder forBook(ResourceLocation bookId) {
        return new PageBuilder(bookId);
    }

    /**
     * Sets the page scrap required to view this page. By default page is visible without any scrap.
     * @param pageScrap The identifier of the page scrap
     * @return this
     */
    public PageBuilder setRequiredScrap(ResourceLocation pageScrap) {
        this.pageScrap = pageScrap;
        return this;
    }

    /**
     * Sets the page that should be found before this one in the book
     * @param page the location of the previous page
     * @return this
     * @implNote This does not guarantee it goes straight after this page, just that it will be somewhere after it
     */
    public PageBuilder setPreviousPage(ResourceKey<GuideBookPage> page) {
        this.insertAfter = page.location();
        return this;
    }

    /**
     * Adds multiple elements to the page
     * @param elements The elements to add
     * @return this
     */
    public PageBuilder addElements(IPageElement... elements) {
        this.elements.addAll(List.of(elements));
        return this;
    }

    /**
     * Constructs the GuideBookPage
     * @return GuideBookPage built using data provided
     */
    public GuideBookPage build() {
        return new GuideBookPage(bookId, pageScrap, insertAfter, elements);
    }

    //Builder for a GuideRecipe
    public static class Recipe implements PageBuilderType {
        private final ResourceLocation recipe;
        private String gridName;
        private float scale;
        private ElementPosition position;
        private ResourceLocation pageScrap;

        private Recipe(ResourceLocation recipe) {
            this.recipe = recipe;
            this.gridName = "basic";
            this.scale = 1f;
            this.position = ElementPosition.getDefault();
            this.pageScrap = GuideBookManager.FIRST_PAGE;
        }

        /**
         * Creates a new GuideRecipe builder for the given recipe
         * @param recipe The recipe to display
         * @return new GuideRecipe builder
         * @see GuideRecipeElement
         */
        public static Recipe of(ResourceLocation recipe) {
            return new Recipe(recipe);
        }

        /**
         * The grid texture to use, defaults to "basic"
         * @param gridName The name of the grid to use, not including textures/... or .png
         * @return this
         * @see SpellboundGrids
         * @see Recipe#gridName(SpellboundGrids)
         */
        public Recipe gridName(String gridName) {
            this.gridName = gridName;
            return this;
        }

        /**
         * Sets the grid texture for the recipe to use, using an enum of Spellbound made grids. Defaults to SpellboundGrids.BASIC
         * @param grid The grid texture to use
         * @return this
         */
        public Recipe gridName(SpellboundGrids grid) {
            return gridName(grid.name().toLowerCase());
        }

        /**
         * Sets the scale of the recipe. Defaults to 1f
         * @param scale Multiplier scale of recipe element
         * @return this
         */
        public Recipe scale(float scale) {
            this.scale = scale;
            return this;
        }

        /**
         * Sets the position the element appears on the page
         * @param x the x offset from left of the book
         * @param y the y offset from top of the book
         * @return this
         */
        public Recipe position(int x, int y) {
            this.position = new ElementPosition(x, y);
            return this;
        }

        /**
         * Sets the page scrap required to unlock this element. Will be obfuscated if not unlocked
         * @param scrap The id for the page scrap
         * @return this
         */
        public Recipe setRequiredScrap(ResourceLocation scrap) {
            this.pageScrap = scrap;
            return this;
        }

        /**
         * Turns the given data into a GuideRecipe element
         * @return constructed GuideRecipe
         */
        public GuideRecipeElement build() {
            return new GuideRecipeElement(recipe, gridName, scale, position, new RecipeExtras(pageScrap));
        }

        /**
         * List of Crafting grid textures used in Spellbound
         */
        public enum SpellboundGrids {
            BASIC,
            ARCHITECT,
            CODEX,
            GRIMOIRE,
            NECRONOMICON,
            SWINDLER
        }
    }

    //Builder for a GuideEntityRenderer
    public static class EntityRenderer implements PageBuilderType {
        private List<ResourceLocation> entity;
        private ElementPosition position;
        private boolean followMouse;
        private float scale;
        private ResourceLocation pageScrap;
        private float xRot;
        private float yRot;
        private float zRot;
        private boolean animated;

        private EntityRenderer() {
            this.entity = new ArrayList<>();
            this.position = ElementPosition.getDefault();
            this.followMouse = false;
            this.scale = 25;
            this.pageScrap = GuideBookManager.FIRST_PAGE;
            this.animated = false;
        }

        /**
         * Creates a new EntityRenderer builder using a given entity
         * @return new GuideEntityRenderer builder
         * @see GuideEntityElement
         */
        public static EntityRenderer of() {
            return new EntityRenderer();
        }

        /**
         * Makes the entity renderer follow the mouse like the player in the inventory, defaults to false
         * @return this
         */
        public EntityRenderer followsMouse() {
            this.followMouse = true;
            return this;
        }

        /**
         * Only works with GeoEntities but will tick them so they will play animations when viewed
         * @return this
         */
        public EntityRenderer animated() {
            this.animated = true;
            return this;
        }

        /**
         * Adds an entity to be displayed. If multiple are added then it cycles through them
         * @param entityType The entity to add
         * @return this
         */
        public EntityRenderer addEntity(EntityType<?> entityType) {
            this.entity.add(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
            return this;
        }

        /**
         * The rotation to put on the entity being rendered. Defaults to no rotation
         * @param xRot Rotation on X axis in degrees
         * @param yRot Rotation on Y axis in degrees
         * @param zRot Rotation on Z axis in degrees
         * @return
         */
        public EntityRenderer setRotations(float xRot, float yRot, float zRot) {
            this.xRot = xRot;
            this.yRot = yRot;
            this.zRot = zRot;
            return this;
        }

        /**
         * How the entity should be scaled, defaults to 25
         * @param scale multiplicative scalar
         * @return this
         */
        public EntityRenderer scale(float scale) {
            this.scale = scale;
            return this;
        }

        /**
         * Sets the position the element appears on the page
         * @param x the x offset from left of the book
         * @param y the y offset from top of the book
         * @return this
         */
        public EntityRenderer position(int x, int y) {
            this.position = new ElementPosition(x, y);
            return this;
        }

        /**
         * Sets the page scrap required to unlock this element. Will be obfuscated if not unlocked
         * @param scrap The id for the page scrap
         * @return this
         */
        public EntityRenderer setRequiredScrap(ResourceLocation scrap) {
            this.pageScrap = scrap;
            return this;
        }

        public GuideEntityElement build() {
            return new GuideEntityElement(
                    entity,
                    new EntityRendererExtras(
                            pageScrap,
                            followMouse,
                            scale,
                            xRot,
                            yRot,
                            zRot,
                            animated
                    ), position
            );
        }
    }

    //Builder for a GuideItemList
    public static class ItemList implements PageBuilderType {
        private List<GuideItemListElement.ItemListEntry> entries;
        private ElementPosition position;
        private ResourceLocation pageScrap;
        private int maxRows;
        private int rowGap;
        private int columnGap;
        private int countGap;
        private boolean dropShadow;
        private int textColour;

        private ItemList() {
            this.entries = new ArrayList<>();
            this.position = ElementPosition.getDefault();
            this.pageScrap = GuideBookManager.FIRST_PAGE;
            this.maxRows = 0;
            this.rowGap = 20;
            this.columnGap = 45;
            this.countGap = 33;
            this.dropShadow = false;
            this.textColour = 0;
        }

        /**
         * Creates a builder for GuideItemList
         * @return new ItemList builder
         */
        public static ItemList of() {
            return new ItemList();
        }

        /**
         * Adds a new entry to the item list
         * @param ingredient The ingredient to add to the list
         * @param count the number of the item to add
         * @return this
         */
        public ItemList addEntry(Ingredient ingredient, int count) {
            this.entries.add(new GuideItemListElement.ItemListEntry(
                    List.of(ingredient), count));
            return this;
        }

        /**
         * Adds a new entry to the item list
         * @param ingredient The ingredient to add to the list
         * @param count the number of the item to add
         * @return this
         */
        public ItemList addEntry(List<Ingredient> ingredient, int count) {
            this.entries.add(new GuideItemListElement.ItemListEntry(
                    ingredient, count));
            return this;
        }

        /**
         * Adds a single item to the item list
         * @param item The item to add
         * @return this
         */
        public ItemList addEntry(Ingredient item) {
            return addEntry(item, 1);
        }

        /**
         * Adds a single item to the item list
         * @param item The item to add
         * @return this
         */
        public ItemList addEntry(List<Ingredient> item) {
            return addEntry(item, 1);
        }

        /**
         * Sets the position the element appears on the page
         * @param x the x offset from left of the book
         * @param y the y offset from top of the book
         * @return this
         */
        public ItemList position(int x, int y) {
            this.position = new ElementPosition(x, y);
            return this;
        }

        /**
         * Sets the page scrap required to unlock this element. Will be obfuscated if not unlocked
         * @param scrap The id for the page scrap
         * @return this
         */
        public ItemList setRequiredScrap(ResourceLocation scrap) {
            this.pageScrap = scrap;
            return this;
        }

        /**
         * The maximum rows that are displayed before going into a new column, defaults to 0 (no limit)
         * @param max number of rows
         * @return this
         */
        public ItemList maxRows(int max) {
            this.maxRows = max;
            return this;
        }

        /**
         * Sets the number of pixels between each row, defaults to 20
         * @param gap number of pixels
         * @return this
         */
        public ItemList rowGap(int gap) {
            this.rowGap = gap;
            return this;
        }

        /**
         * Sets the number of pixels between each column, defaults to 45
         * @param gap number of pixels
         * @return this
         */
        public ItemList columnGap(int gap) {
            this.columnGap = gap;
            return this;
        }

        /**
         * Sets the number of pixels between the count and the item, defaults to 33
         * @param gap number of pixels
         * @return this
         */
        public ItemList countGap(int gap) {
            this.countGap = gap;
            return this;
        }

        /**
         * Enables drop shadow on the text
         * @return this
         */
        public ItemList dropShadow() {
            this.dropShadow = true;
            return this;
        }

        /**
         * Sets the text colour, defaults to white
         * @param colour the colour to use
         * @return this
         */
        public ItemList textColour(int colour) {
            this.textColour = colour;
            return this;
        }

        public GuideItemListElement build() {
            return new GuideItemListElement(
                    entries,
                    new ItemListExtras(
                            pageScrap,
                            maxRows,
                            rowGap,
                            columnGap,
                            countGap,
                            dropShadow,
                            textColour
                    ), position
            );
        }
    }

    //Builder for GuideSpellInfo
    public static class SpellInfo implements PageBuilderType {
        private ResourceLocation spell;
        private ElementPosition position;
        private int textColour;
        private int lineGap;
        private boolean dropShadow;
        private boolean mastery;
        private Display baseDamage;
        private Display castTime;
        private Display duration;
        private Display manaCost;
        private Display manaPerTick;
        private boolean alwaysShow;

        private SpellInfo(SpellType<?> spell) {
            this.spell = SBSpells.REGISTRY.getKey(spell);
            this.position = ElementPosition.getDefault();
            this.textColour = 0xFFFFFF;
            this.lineGap = 10;
            this.dropShadow = false;
            this.mastery = true;
            this.baseDamage = Display.IF_VALID;
            this.castTime = Display.IF_VALID;
            this.duration = Display.IF_VALID;
            this.manaCost = Display.IF_VALID;
            this.manaPerTick = Display.IF_VALID;
            this.alwaysShow = false;
        }

        /**
         * Creates a SpellInfo builder using a given path
         * @param spellType the path to show stats on
         * @return new SpellInfo builder
         */
        public static SpellInfo of(SpellType<?> spellType) {
            return new SpellInfo(spellType);
        }

        /**
         * Sets the position the element appears on the page
         * @param x the x offset from left of the book
         * @param y the y offset from top of the book
         * @return this
         */
        public SpellInfo position(int x, int y) {
            this.position = new ElementPosition(x, y);
            return this;
        }

        /**
         * The colour of the text, defaults to white
         * @param colour the colour value
         * @return
         */
        public SpellInfo textColour(int colour) {
            this.textColour = colour;
            return this;
        }

        /**
         * Sets the gap between each stat, 10
         * @param gap number of pixels
         * @return this
         */
        public SpellInfo lineGap(int gap) {
            this.lineGap = gap;
            return this;
        }

        /**
         * Enables drop shadow on the text
         * @return this
         */
        public SpellInfo dropShadow() {
            this.dropShadow = true;
            return this;
        }

        /**
         * Stops it from displaying the required mastery level
         * @return this
         */
        public SpellInfo hideMastery() {
            this.mastery = false;
            return this;
        }

        /**
         * Whether the base damage stat should be displayed
         * @param display Display setting
         * @return this
         */
        public SpellInfo baseDamage(Display display) {
            this.baseDamage = display;
            return this;
        }
        /**
         * Whether the cast time stat should be displayed
         * @param display Display setting
         * @return this
         */
        public SpellInfo castTime(Display display) {
            this.castTime = display;
            return this;
        }
        /**
         * Whether the duration stat should be displayed
         * @param display Display setting
         * @return this
         */
        public SpellInfo duration(Display display) {
            this.duration = display;
            return this;
        }
        /**
         * Whether the mana cost stat should be displayed
         * @param display Display setting
         * @return this
         */
        public SpellInfo manaCost(Display display) {
            this.manaCost = display;
            return this;
        }

        /**
         * Whether the mana per tick stat should be displayed
         * @param display Display setting
         * @return this
         */
        public SpellInfo manaPerTick(Display display) {
            this.manaPerTick = display;
            return this;
        }

        /**
         * Makes the stats show regardless of if the path is unlocked or not
         * @return this
         */
        public SpellInfo alwaysShow() {
            this.alwaysShow = true;
            return this;
        }

        public GuideSpellInfoElement build() {
            return new GuideSpellInfoElement(
                    spell,
                    new SpellInfoExtras(
                            textColour,
                            lineGap,
                            dropShadow,
                            mastery,
                            baseDamage.getValue(),
                            castTime.getValue(),
                            duration.getValue(),
                            manaCost.getValue(),
                            manaPerTick.getValue(),
                            alwaysShow
                    ), position
            );
        }


        /**
         * How stats should be displayed
         * NEVER - won't appear on the element
         * IF_VALID - will only appear if value is > 0
         * ALWAYS - will appear on element always
         */
        public enum Display {
            NEVER(0),
            IF_VALID(1),
            ALWAYS(2);

            private final int value;

            Display(int i) {
                this.value = i;
            }

            public int getValue() {
                return value;
            }
        }
    }

    //Builder for SpellBorder
    public static class SpellBorder implements PageBuilderType {
        private final SpellPath path;
        private final Optional<SpellMastery> mastery;
        private int colour;
        private ElementPosition position;
        private ResourceLocation pathTexture;
        private Optional<Component> topText;
        private Optional<Component> bottomText;

        private SpellBorder(SpellType<?> spellType) {
            this(spellType.getPath(), spellType.getMastery());
        }

        private SpellBorder(SpellPath path, @Nullable SpellMastery mastery) {
            this.path = path;
            this.mastery = Optional.ofNullable(mastery);
            this.position = ElementPosition.getDefault();
            this.pathTexture = CommonClass.customLocation("textures/gui/paths/" + path.name().toLowerCase() + ".png");
            this.colour = switch (path) {
                case RUIN -> -10678496;
                case TRANSFIGURATION -> -14396391;
                case SUMMONS -> -13490312;
                case DIVINE -> -3510773;
                case DECEPTION -> -13357984;
                default -> 0;
            };
            this.topText = Optional.empty();
            this.bottomText = Optional.empty();
        }

        /**
         * Creates a path border builder defaulting all values based on the path
         * @param spell The path the border is for
         * @return new SpellBorder builder
         */
        public static SpellBorder of(SpellType<?> spell) {
            return new SpellBorder(spell);
        }

        /**
         * Creates a path border builder defaulting all values based on the path
         * @param path The path the border is for
         * @param mastery The mastery the border is for, if there is one
         * @return new SpellBorder builder
         */
        public static SpellBorder of(SpellPath path, SpellMastery mastery) {
            return new SpellBorder(path, mastery);
        }

        /**
         * Creates a path border builder defaulting all values based on the path
         * @param path The path the border is for
         * @return new SpellBorder builder
         */
        public static SpellBorder of(SpellPath path) {
            return of(path, null);
        }

        /**
         * Sets the colour used in text and lines, should be relevant to pathTexture
         * @param colour The colour to use
         * @return this
         */
        public SpellBorder setColour(int colour) {
            this.colour = colour;
            return this;
        }

        /**
         * Sets the x and y offset of the element within the page
         * @param x the X offset
         * @param y the Y offset
         * @return this
         */
        public SpellBorder setPosition(int x, int y) {
            this.position = new ElementPosition(x, y);
            return this;
        }

        /**
         * Sets the symbol used to represent the path pathTexture
         * @param texture The location of the texture
         * @return this
         */
        public SpellBorder setPathTexture(ResourceLocation texture) {
            this.pathTexture = texture;
            return this;
        }

        public SpellBorder setTopText(Component topText) {
            this.topText = Optional.of(topText);
            return this;
        }

        public SpellBorder setBottomText(Component bottomText) {
            this.bottomText = Optional.of(bottomText);
            return this;
        }

        /**
         * Creates a GuideSpellBorderElement from builder
         * @return generated element
         */
        public GuideSpellBorderElement build() {
            return new GuideSpellBorderElement(
                    this.path, this.mastery, colour, position, pathTexture, topText, bottomText
            );
        }
    }

    //TODO: Obfuscation
    //Builder for GuideImage
    public static class Image implements PageBuilderType {
        private final ResourceLocation image;
        private int width;
        private int height;
        private ElementPosition position;
        private ResourceLocation pageScrap;
        private boolean enableCorners;

        private Image(ResourceLocation image) {
            this.image = image;
            this.position = ElementPosition.getDefault();
            this.pageScrap = GuideBookManager.FIRST_PAGE;
            this.enableCorners = true;
        }

        /**
         * Creates a new builder for GuideImage
         * @param image the texture location of the image
         * @return new GuideImage builder
         */
        public static Image of(ResourceLocation image) {
            return new Image(image);
        }

        /**
         * Disables the corners that are placed on corner of the image
         * @return this
         */
        public Image disableCorners() {
            this.enableCorners = false;
            return this;
        }

        /**
         * Sets the dimensions to use for display
         * @param width pixel width
         * @param height pixel height
         * @return this
         * @apiNote There is no default this must be set.
         */
        public Image setDimensions(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Sets the position the element appears on the page
         * @param x the x offset from left of the book
         * @param y the y offset from top of the book
         * @return this
         */
        public Image position(int x, int y) {
            this.position = new ElementPosition(x, y);
            return this;
        }

        /**
         * Sets the page scrap required to unlock this element. Will be obfuscated if not unlocked
         * @param scrap The id for the page scrap
         * @return this
         */
        public Image setRequiredScrap(ResourceLocation scrap) {
            this.pageScrap = scrap;
            return this;
        }

        public GuideImageElement build() {
            return new GuideImageElement(image, width, height, position, new GuideImageExtras(enableCorners));
        }
    }

    //Builder for GuideItem
    public static class StaticItem implements PageBuilderType {
        private final List<Ingredient> ingredients;
        private String tile;
        private float scale;
        private ElementPosition position;
        private ResourceLocation pageScrap;
        private boolean disableBackground;

        private StaticItem() {
            this.ingredients = new ArrayList<>();
            this.tile = Recipe.SpellboundGrids.BASIC.name().toLowerCase();
            this.scale = 1f;
            this.position = ElementPosition.getDefault();
            this.pageScrap = GuideBookManager.FIRST_PAGE;
            this.disableBackground = false;
        }

        /**
         * Creates a new GuideItem builder for a given item
         * @return new GuideItem builder
         */
        public static StaticItem of() {
            return new StaticItem();
        }

        /**
         * Adds an item to be displayed. If multiple are added it will cycle through them
         * @param item the item to display
         * @return this
         */
        public StaticItem addItem(Ingredient item) {
            this.ingredients.add(item);
            return this;
        }

/*        public StaticItem addItem(ItemLike item, TypedDataComponent<?>... data) {
            this.ingredients.add(createIngredient(Arrays.asList(data), item));
            return this;
        }

        public StaticItem addItem(HolderSet<Item> items, TypedDataComponent<?>... data) {
            this.ingredients.add(createIngredient(Arrays.asList(data), items));
            return this;
        }

        public StaticItem addItem(Item item, TypedDataComponent<?>... data) {
            this.ingredients.add(createIngredient(Arrays.asList(data), item));
            return this;
        }

        public StaticItem addItem(HolderSet<Item> items) {
            this.ingredients.add(createIngredient(List.of(), items));
            return this;
        }

        public StaticItem addItem(Item item) {
            this.ingredients.add(createIngredient(List.of(), item));
            return this;
        }

        private DataComponentIngredient createIngredient(List<TypedDataComponent<?>> data, ItemLike... items) {
            return createIngredient(data,HolderSet.direct(Arrays.stream(items).map(ItemLike::asItem).map(Item::builtInRegistryHolder).toList()));
        }

        private <S> DataComponentIngredient createIngredient(List<TypedDataComponent<?>> data, HolderSet<Item> items) {
            var builder = DataComponentPredicate.builder();
            for (var components : data) {
                builder.expect((DataComponentType<? super S>) components.type(), (S) components.value());
            }
            return new DataComponentIngredient(items, builder.build(), false);
        }*/

        /**
         * The grid texture to use, defaults to "basic"
         * @param tile The name of the grid to use, not including textures/... or .png
         * @return this
         * @see Recipe.SpellboundGrids
         * @see StaticItem#tile(Recipe.SpellboundGrids)
         */
        public StaticItem tile(String tile) {
            this.tile = tile;
            return this;
        }

        /**
         * Sets the tile using the built in spellbound grids
         * @param tile the tile to use
         * @return this
         */
        public StaticItem tile(Recipe.SpellboundGrids tile) {
            return this.tile(tile.name().toLowerCase());
        }

        /**
         * The scale multiplier to apply to the element
         * @param scale multiplier value
         * @return this
         */
        public StaticItem scale(float scale) {
            this.scale = scale;
            return this;
        }

        /**
         * Sets the position the element appears on the page
         * @param x the x offset from left of the book
         * @param y the y offset from top of the book
         * @return this
         */
        public StaticItem position(int x, int y) {
            this.position = new ElementPosition(x, y);
            return this;
        }

        /**
         * Sets the page scrap required to unlock this element. Will be obfuscated if not unlocked
         * @param scrap The id for the page scrap
         * @return this
         */
        public StaticItem setRequiredScrap(ResourceLocation scrap) {
            this.pageScrap = scrap;
            return this;
        }

        /**
         * Disables the crafting grid icon
         * @return this
         */
        public StaticItem disableBackground() {
            this.disableBackground = true;
            return this;
        }

        public GuideStaticItemElement build() {
            return new GuideStaticItemElement(
                    ingredients,
                    tile,
                    position,
                    new StaticItemExtras(pageScrap, scale, disableBackground)
            );
        }
    }

    //Builder for GuideText
    public static class Text implements PageBuilderType {
        private Component text;
        private ElementPosition position;
        private ResourceLocation pageScrap;
        private int colour;
        private int maxLineLength;
        private int lineGap;
        private boolean dropShadow;
        private boolean textWrapping;
        private boolean centered;
        private String link;
        private boolean unlockForLink;
        private boolean underline;
        private boolean bold;
        private String hoverText;
        private boolean italic;
        private boolean scrambledEnding;

        private Text(Component text) {
            this.text = text;
            this.position = ElementPosition.getDefault();
            this.pageScrap = GuideBookManager.FIRST_PAGE;
            this.colour = 0;
            this.maxLineLength = 150;
            this.lineGap = 9;
            this.dropShadow = false;
            this.textWrapping = true;
            this.centered = false;
            this.link = "";
            this.unlockForLink = true;
            this.underline = false;
            this.bold = false;
            this.hoverText = "";
            this.italic = false;
            this.scrambledEnding = false;
        }

        /**
         * Creates a new GuideText builder for a given string literal
         * @param literal The component text
         * @return new GuideText builder
         */
        public static Text ofLiteral(String literal) {
            return new Text(Component.literal(literal));
        }

        /**
         * Creates a new GuideText builder for a given translation key
         * @param translationKey The component translation key
         * @return new GuideText builder
         */
        public static Text ofTranslatable(String translationKey) {
            return new Text(Component.translatable(translationKey));
        }
        /**
         * Creates a new GuideText builder for a given translation key
         * @param component The component to display
         * @return new GuideText builder
         */
        public static Text of(Component component) {
            return new Text(component);
        }

        /**
         * The colour for the text to display, defaults to 0
         * @param colour the colour value
         * @return
         */
        public Text textColour(int colour) {
            this.colour = colour;
            return this;
        }

        /**
         * Max characters on a line, defaults to 150 (fills a page left to right)
         * @param length number of characters
         * @return this
         */
        public Text maxLineLength(int length) {
            this.maxLineLength = length;
            return this;
        }

        /**
         * Set a link to be opened when the user clicks on the link
         * @param link The link to open in users browser
         * @return this
         */
        public Text setLink(String link) {
            this.link = link;
            return this;
        }

        /**
         * Makes it so the user can open the link without the element being unlocked>
         * @return this
         */
        public Text openLinkWithoutUnlock() {
            this.unlockForLink = false;
            return this;
        }

        /**
         * Underlines the text
         * @return this
         */
        public Text underline() {
            this.underline = true;
            return this;
        }

        public Text italic() {
            this.italic = true;
            return this;
        }

        /**
         * Makes the text bold
         * @return this
         */
        public Text bold() {
            this.bold = true;
            return this;
        }

        /**
         * Gives text that appears when the user hovers over the element
         * @param translationKey the translation key for the hover text
         * @return this
         */
        public Text hoverText(String translationKey) {
            this.hoverText = translationKey;
            return this;
        }

        /**
         * Adds 5 obfuscated characters to end of text. Intended for incomplete features.
         * @return this
         */
        public Text scrambledEnding() {
            this.scrambledEnding = true;
            return this;
        }

        /**
         * The gap between each line, defaults to 9
         * @param gap the number of pixels
         * @return this
         */
        public Text lineGap(int gap) {
            this.lineGap = gap;
            return this;
        }

        /**
         * Enables dropShadow for the text
         * @return this
         */
        public Text dropShadow() {
            this.dropShadow = true;
            return this;
        }

        /**
         * Stops the text automatically wrapping around to a new line at max character limit
         * @return this
         */
        public Text disableWrapping() {
            this.textWrapping = false;
            return this;
        }

        /**
         * Wraps texts around a midpoint
         * @return this
         */
        public Text centered() {
            this.centered = true;
            return this;
        }

        /**
         * Sets the position the element appears on the page
         * @param x the x offset from left of the book
         * @param y the y offset from top of the book
         * @return this
         */
        public Text position(int x, int y) {
            this.position = new ElementPosition(x, y);
            return this;
        }

        /**
         * Sets the page scrap required to unlock this element. Will be obfuscated if not unlocked
         * @param scrap The id for the page scrap
         * @return this
         */
        public Text setRequiredScrap(ResourceLocation scrap) {
            this.pageScrap = scrap;
            return this;
        }

        public GuideTextElement build() {
            return new GuideTextElement(
                    text,
                    new TextExtras(
                            pageScrap, colour, maxLineLength, lineGap, dropShadow, textWrapping, centered, link, unlockForLink, underline, bold, hoverText, italic, scrambledEnding
                    ),
                    position
            );
        }
    }

    //Builder for GuideTooltipRenderer
    public static class Tooltip implements PageBuilderType {
        private final List<Component> tooltips = new ArrayList<>();
        private ElementPosition position;
        private int width, height;

        private Tooltip() {
            this.position = ElementPosition.getDefault();
        }

        /**
         * Creates a new builder for a TooltipRenderer element
         * @return new RitualRenderer builder
         */
        public static Tooltip of() {
            return new Tooltip();
        }


        /**
         * Adds a component to the tooltip
         * @param tooltip the item to display
         * @return this
         */
        public Tooltip addTooltip(Component tooltip) {
            this.tooltips.add(tooltip);
            return this;
        }

        /**
         * Sets the position the element appears on the page
         * @param x the x offset from left of the book
         * @param y the y offset from top of the book
         * @return this
         */
        public Tooltip position(int x, int y) {
            this.position = new ElementPosition(x, y);
            return this;
        }

        /**
         * Sets the position the element appears on the page
         * @param width the width of the mouse hover area
         * @param height the height of the mouse hover area
         * @return this
         */
        public Tooltip dimensions(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public GuideTooltipElement build() {
            return new GuideTooltipElement(this.tooltips, this.position, this.width, this.height);
        }
    }

    //Builder for GuideTextList
    public static class TextList implements PageBuilderType {
        private List<GuideTextListElement.ScrapComponent> entries;
        private ElementPosition position;
        private ResourceLocation pageScrap;
        private int maxRows;
        private int rowGap;
        private int columnGap;
        private int lineLength;
        private int extraOffset;
        private String bulletPoint;
        private boolean dropShadow;
        private int textColour;

        private TextList() {
            this.entries = new ArrayList<>();
            this.position = ElementPosition.getDefault();
            this.pageScrap = GuideBookManager.FIRST_PAGE;
            this.maxRows = 0;
            this.rowGap = 20;
            this.columnGap = 45;
            this.lineLength = 150;
            this.bulletPoint = "▪";
            this.dropShadow = false;
            this.textColour = 0;
        }

        /**
         * Creates a new TextList builder
         * @return new TextList Builder
         */
        public static TextList of() {
            return new TextList();
        }

        /**
         * Adds a new entry to the list
         * @param text The list entry
         * @return this
         */
        public TextList addEntry(Component text, ResourceLocation scrap, int extraOffset) {
            this.entries.add(new GuideTextListElement.ScrapComponent(text, scrap, extraOffset));
            return this;
        }

        public TextList addEntry(Component text, ResourceLocation scrap) {
            this.entries.add(new GuideTextListElement.ScrapComponent(text, scrap));
            return this;
        }

        public TextList addEntry(Component text, int extraOffset) {
            this.entries.add(new GuideTextListElement.ScrapComponent(text, extraOffset));
            return this;
        }

        public TextList addEntry(Component text) {
            this.entries.add(new GuideTextListElement.ScrapComponent(text));
            return this;
        }

        /**
         * Sets the position the element appears on the page
         * @param x the x offset from left of the book
         * @param y the y offset from top of the book
         * @return this
         */
        public TextList position(int x, int y) {
            this.position = new ElementPosition(x, y);
            return this;
        }

        /**
         * The maximum number of rows to be displayed, defaults to 0 (unlimited)
         * @param max the number of rows
         * @return this
         */
        public TextList maxRows(int max) {
            this.maxRows = max;
            return this;
        }

        /**
         * The gap between each row, defaults to 20
         * @param gap number of pixels
         * @return this
         */
        public TextList rowGap(int gap) {
            this.rowGap = gap;
            return this;
        }

        /**
         * Gap between columns, defaults to 45
         * @param gap number of pixels
         * @return this
         */
        public TextList columnGap(int gap) {
            this.columnGap = gap;
            return this;
        }

        /**
         * Max length of the component
         * @param length number of pixels
         * @return this
         */
        public TextList lineLength(int length) {
            this.columnGap = length;
            return this;
        }

        /**
         * The String to be used for a bullet point, defaults to "▪"
         * @param bullet the bullet point to be used
         * @return this
         */
        public TextList bulletPoint(String bullet) {
            this.bulletPoint = bullet;
            return this;
        }

        /**
         * Enables drop shadow on the text
         * @return this
         */
        public TextList dropShadow() {
            this.dropShadow = true;
            return this;
        }

        /**
         * Sets the colour of the text, defaults to black
         * @param colour the colour value
         * @return this
         */
        public TextList textColour(int colour) {
            this.textColour = colour;
            return this;
        }

        public GuideTextListElement build() {
            return new GuideTextListElement(
                    entries,
                    new TextListExtras(
                            maxRows,
                            rowGap,
                            columnGap,
                            lineLength,
                            dropShadow,
                            textColour,
                            bulletPoint
                    ), position
            );
        }
    }

    //Builder for GuideItemRenderer
    public static class ItemRenderer implements PageBuilderType {
        private final List<Ingredient> item;
        private ElementPosition position;
        private ResourceLocation pageScrap;
        private float scale;

        private ItemRenderer() {
            this.item = new ArrayList<>();
            this.position = ElementPosition.getDefault();
            this.pageScrap = GuideBookManager.FIRST_PAGE;
            this.scale = 25f;
        }

        /**
         * Creates a new builder for an ItemRenderer element
         * @param ingredient the item to render
         * @return new ItemRenderer builder
         */
        public static ItemRenderer of(Ingredient ingredient) {
            ItemRenderer builder = new ItemRenderer();
            return builder.addItem(ingredient);
        }

        /**
         * Adds an item to be displayed. If more than one or added it cycled through them
         * @param ingredient the item to display
         * @return this
         */
        public ItemRenderer addItem(Ingredient ingredient) {
            this.item.add(ingredient);
            return this;
        }

        /**
         * Sets the position the element appears on the page
         * @param x the x offset from left of the book
         * @param y the y offset from top of the book
         * @return this
         */
        public ItemRenderer position(int x, int y) {
            this.position = new ElementPosition(x, y);
            return this;
        }

        /**
         * Sets the page scrap required to unlock this element. Will be obfuscated if not unlocked
         * @param scrap The id for the page scrap
         * @return this
         */
        public ItemRenderer setRequiredScrap(ResourceLocation scrap) {
            this.pageScrap = scrap;
            return this;
        }

        /**
         * Sets the scale to be used for the item, defaults to 25
         * @param scale multiplier scalar
         * @return this
         */
        public ItemRenderer scale(float scale) {
            this.scale = scale;
            return this;
        }

        public GuideItemElement build() {
            return new GuideItemElement(
                    item,
                    position,
                    new ItemRendererExtras(
                            pageScrap,
                            scale
                    )
            );
        }
    }

    //Builder for GuideRitualRenderer
    public static class RitualRenderer implements PageBuilderType {
        private ResourceKey<TransfigurationRitual> ritual;
        private boolean leftPage = false;

        /**
         * Creates a new builder for a RitualRenderer element
         * @param ritual the ritual for items to render from
         * @return new RitualRenderer builder
         */
        public static RitualRenderer of(ResourceKey<TransfigurationRitual> ritual) {
            RitualRenderer builder = new RitualRenderer();
            return builder.createRitual(ritual);
        }

        private RitualRenderer createRitual(ResourceKey<TransfigurationRitual> ritual) {
            this.ritual = ritual;
            return this;
        }

        public RitualRenderer leftPage() {
            this.leftPage = true;
            return this;
        }

        public TransfigurationRitualElement build() {
            return new TransfigurationRitualElement(this.ritual, this.leftPage);
        }
    }

    public interface PageBuilderType {}
}
