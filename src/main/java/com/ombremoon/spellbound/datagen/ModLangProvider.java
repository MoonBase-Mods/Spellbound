package com.ombremoon.spellbound.datagen;

import com.google.common.collect.ImmutableMap;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ModLangProvider extends LanguageProvider {

    protected static final Map<String, String> REPLACE_LIST = ImmutableMap.of(
            "tnt", "TNT",
            "sus", "",
            "magis", "Magi's",
            "architects", "Architect's",
            "swindlers", "Swindler's",
            "dolphins", "Dolphin's",
            "mermaids", "Mermaids's"
    );

    public ModLangProvider(PackOutput gen) {
        super(gen, Constants.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        SBItems.ITEMS.getEntries().forEach(this::itemLang);
        SBSpells.SPELL_TYPES.getEntries().forEach(this::spellLang);
        SBSkills.SKILLS.getEntries().forEach(this::skillLang);
        SBBlocks.BLOCKS.getEntries().forEach(this::blockLang);
        SBEntities.ENTITIES.getEntries().forEach(this::entityLang);
        SBEffects.EFFECTS.getEntries().forEach(this::effectLang);
        SBAttributes.ATTRIBUTES.getEntries().forEach(this::attributeLang);

        pathLang();
        manualEntries();
    }

    protected void itemLang(DeferredHolder<Item, ? extends Item> entry) {
        if (!(entry.get() instanceof BlockItem) || entry.get() instanceof ItemNameBlockItem) {
            addItem(entry, checkReplace(entry));
        }
    }

    protected void spellLang(DeferredHolder<SpellType<?>, ? extends SpellType<?>> entry) {
        add(entry.get().createSpell().getNameId(), checkReplace(entry));
    }

    protected void pathLang() {
        for (SpellPath path : SpellPath.values()) {
            add("spellbound.path." + path.getSerializedName(), checkReplace(path.getSerializedName()));
        }
    }

    protected void skillLang(DeferredHolder<Skill, ? extends Skill> entry) {
        add(entry.get().getNameId(), checkReplace(entry));
    }

    protected void attributeLang(DeferredHolder<Attribute,? extends Attribute> entry) {
        add(entry.get().getDescriptionId(), checkReplace(entry));
    }

    protected void blockLang(DeferredHolder<Block, ? extends Block> entry) {
        addBlock(entry, checkReplace(entry));
    }

    protected void entityLang(DeferredHolder<EntityType<?>, ? extends EntityType<?>> entry) {
        addEntityType(entry, checkReplace(entry));
    }

    protected void effectLang(DeferredHolder<MobEffect, ? extends MobEffect> entry) {
        addEffect(entry, checkReplace(entry));
    }

    protected void manualEntries() {
        skillDescriptions();
        guideContents();

        add("spellbound.toast.spell_level_up", "%s has reached Level %s");
        add("spellbound.toast.path_level_up", "%s path has reached Level %s");
        add("spellbound.toast.scrap_unlocked", "New book entry unlocked");

        add("chat.spelltome.awardxp", "Spell already known. +10 spells XP.");
        add("chat.spelltome.nospell", "This spells tome is blank.");
        add("chat.spelltome.spellunlocked", "Spell unlocked: ");
        add("tooltip.spellbound.holdshift", "Hold shift for more information.");

        add("spellbound.sb_generic", "Magic Damage");
        add("spellbound.physical_damage", "Physical Damage");
        add("spellbound.ruin_fire", "Fire Damage");
        add("spellbound.ruin_frost", "Frost Damage");
        add("spellbound.ruin_shock", "Shock Damage");

        add("spellbound.path.level", "Lvl");

        add("command.spellbound.spellunknown", "You don't know the spells %1$s.");
        add("command.spellbound.spellforgot", "%s has been forgotten successfully.");
        add("command.spellbound.alreadyknown", "%s is already known.");
        add("command.spellbound.singleskilllearnt", "%s has been unlocked.");
        add("command.spellbound.learntskills", "All skills unlocked for %s");
        add("command.spellbound.spelllearnt", "%s has been learnt.");
        
        add("itemGroup.spellbound", "Spellbound🪄");
        add("spellbound.transfiguration_armor.buff", "+12.5% Transfiguration Spell Duration");
    }

    protected void guideContents() {
        add("guide.general.table_contents", "Table of Contents:");
        add("guide.element.spell_info", "Spell Info");
        add("guide.element.spell_info.spell_mastery", "Spell Mastery: %1$s");
        add("guide.element.spell_info.damage", "Damage: %1$s");
        add("guide.element.spell_info.mana_cost", "Mana Cost: %1$s");
        add("guide.element.spell_info.cast_time", "Cast Time: %1$s");
        add("guide.element.spell_info.duration", "Duration: %1$s");
        add("guide.element.spell_info.mana_per_tick", "Mana/Tick: %1$s");
        add("guide.element.spell_border.element", "Element: ");
        add("guide.element.spell_border.mastery", "Mastery: %1$s");
        add("guide.general.path_items", "Path Items");

        basicContents();
        ruinContents();
        transfigContents();
        summonContents();
        divineGuideContents();
        deceptionGuideContents();
    }

    private void addSpellContents(SpellPath path, SpellType<?> spell, String lore, String bossLore) {
        add("guide." + path.name() + "." + spell.location().getPath() + ".lore", lore);
        add("guide." + path.name() + "." + spell.location().getPath() + ".boss_lore", bossLore);
    }

    private void basicContents() {
        add("guide.basic.contents", "Book Contents");
        add("guide.basic.contributors", "Contributors");
        add("guide.basic.dev_team", "Development Team");
        add("guide.basic.past_contributors", "Former Contributors");
        add("guide.basic.spell_research", "Spell Research");

        add("guide.basic.discord", "Discord");
        add("guide.basic.bugs", "Bug Reports");
        add("guide.basic.blurb", "Welcome fellow Magi, to the world of Spellbound! I present to you the first in many books documenting the arcane. Let this book act as your guide, introducing you to the different paths of magic to follow, and the plethora of mysteries to be discovered.");
        add("guide.basic.spellbound", "What is Spellbound?");
        add("guide.basic.description1", "The magical world of Spellbound puts an emphasis on your choices to craft your experience, rather than relying on RNG.");
        add("guide.basic.description2", "Unlock powerful spells, each with their own set of unique upgrades. Fight dangerous foes in otherworldly dimensions. Explore the dark arts or become a peaceful saint. Choose your path.");
        add("guide.basic.spell_paths", "Spell Paths");
        add("guide.basic.spell_paths1", "All spells are broken up into 5 spell paths: §4Ruin§r, §2Transfiguration§r, §1Summons§r, §eDivine§r, and §9Deception§r");
        add("guide.basic.spell_paths_cont", "Spell Paths Cont.");
        add("guide.basic.ruin", "Ruin is the path of elemental energy, destruction and chaos. The Ruin path can be broken down into additional sub-paths: §lFire§r, §lFrost§r, and §lShock§r. Each path has a unique effect that is applied to targets after taking enough damage.");
        add("guide.basic.transfiguration", "Transfiguration is the path of alchemy and change. Offering a variety of utility and protection, Transfiguration spells are essential in almost all Magi's arsenal. Learn how to perform spells on an even grander scale through rituals.");
        add("guide.basic.summons", "Summons is the path creation. As a Summoner, bring forth allies from beyond the grave or other dimensions to fight in your stead by defeating them in combat. Befriend familiars to aid you in your journey.");
        add("guide.basic.divine", "Divine is the path of light and darkness. Aid your allies and smite the undead, or curse your enemies and steal their life force. The Divine forces fuel your power, but beware. For every action taken, judgement will be passed.");
        add("guide.basic.deception", "Deception is the path of illusions and trickery. Masters of the art of Deception make their moves from the shadows, striking when you least expect. It is said that there is a place beyond mortal reach, housing secrets for those determined enough to uncover them.");
        add("guide.basic.general_items", "General Items");
        add("guide.basic.arcanthus", "A magical flower found in small patches in flower fields, Arcanthus is the easiest way for a Magi to introduce oneself to the world of magic. Can be crafted into magic essence.");
        add("guide.basic.magic_essence", "Magic Essence is the core of all magic items in this world, utilizing the magical properties of Arcanthus to enhance its host object.");
        add("guide.basic.mana_tear", "As you and your spells grow stronger, the mana needed to cast them is increased. Mana Tears can be used to permanently increase mana reserves.");
        add("guide.basic.workbench1", "A Magi's Workbench is a Magi's most important tool for spell growth. This is where you can view, swap out, and upgrade your spells as you progress through the world.");
        add("guide.basic.book_recipes", "Book Recipes");
        add("guide.basic.book_recipes_cont", "Book Recipes Cont.");
        add("guide.basic.guide_books", "Guide Books are essential for any Magi, new or old. Within them contains all information relevant to its corresponding path. Some content includes: ");
        add("guide.basic.guide_books1", "▪ Path Overview");
        add("guide.basic.guide_books2", "▪ Unique Path Mechanics");
        add("guide.basic.guide_books3", "▪ Path Lore");
        add("guide.basic.guide_books4", "▪ Path Specific Items");
        add("guide.basic.guide_books5", "▪ Spell Info & Acquisition");
        add("guide.basic.guide_books6", "Note: Not all content has been added yet");
        add("guide.basic.spell_broker", "Located in a secluded tower in the forest and sometimes seen roaming the lands is a shady fellow, the Spell Broker.");
        add("guide.basic.spell_broker1", "Carrying spells he could only have obtained through suspicious means, the Broker seeks to trade things of magical value.");
        add("guide.basic.page_scraps", "Magic Research &\nPage Scraps");
        add("guide.basic.page_scraps1", "Path books hold key information about spell acquisition and path mechanics, but are encrypted in a magical language and need a cipher to read.");
        add("guide.basic.page_scraps2", "Page scraps can be obtained around the world through various actions, deciphering certain entries in a book.");
        add("guide.basic.page_scraps3", "Explore the lands, interact with mobs, and be on the look out for clues to unlock every book entry.");
        add("guide.basic.path_items", "Every path has a set of specific items that will help Magi's grow stronger: §lShards§r, §lStaves§r, and §lArmor§r");
        add("guide.basic.shards", "Shards are special magic catalyst items that can be found underground, dropped from certain mobs, or earned from specific tasks.");
        add("guide.basic.armor", "Enchanted regalia that buffs certain spell stats from the corresponding path.");
        add("guide.basic.staves", "A Magi's Staff allows its user to better control the flow of magic, bestowing a special buff to spells of the corresponding path.");
        add("guide.basic.spells", "Spells");
        add("guide.basic.spell_tomes", "To learn a spell, you must first obtain its Spell Tome. Spell Tomes cannot be found in dungeons or as common mob loot, but are instead acquired through certain tasks corresponding to its path.");
        add("guide.basic.spell_mastery", "All spells are locked under a Mastery Level. Path Mastery Level is increased by levelling up spells, which can be done through spell casting and harming/healing entities with your spells.");
        add("guide.basic.choice_spells", "As spells level up, they can be upgraded to become even stronger and more unique to your playstyle. Certain upgrades changes how spells work entirely—these are called §lChoice Spells§r.");
        add("guide.basic.skills", "Skills");
        add("guide.basic.skills1", "Skills are upgrades unique to each spell that adds additional functionality to it. All spells have a total of 10 skill upgrades.");
        add("guide.basic.skills2", "Skills are unlocked via Skill Points, which are earned every time you level up a spell. Spells can level up to a max level of 5, meaning only 5 of the 10 upgrades can be unlocked.");
        add("guide.basic.skills3", "There are 3 special types of skills: §lModifier§r, §lConditional§r, and §lChoice§r skills.");
        add("guide.basic.modifier_skills", "▪ Modifier skills give permanent buffs/debuffs to certain spell stats, such as duration, potency, or mana cost.");
        add("guide.basic.conditional_skills", "▪ Conditional skills require specific conditions to be met to either unlock or use.");
        add("guide.basic.choice_skills", "▪ Choice skills almost entirely change how a spell functions, giving Magi's the option to swap between different implementations of the same spell.");
    }

    protected void ruinContents() {
        add("guide.ruin.contents.description", "Ruin & Sub-Paths");

        add("guide.ruin.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can bend it to my will to destroy my enemies by harnessing the powers of ruin.");
        add("guide.ruin.quote", "Peace is a lie. There is only energy.");
        add("guide.ruin.subpaths", "Sub-Paths");
        add("guide.ruin.description1", "The destructive Path of Ruin, channelling the forces that hold the world together and twisting them to tear the world apart.");
        add("guide.ruin.description2", "To use this path to its fullest is to channel all your mana into pure elemental power, ripping through anyone who stands in your path or doubts your strength. Disregarding whatever damage you may do to the world around you.");
        add("guide.ruin.subpaths1", "Ruin manipulates the power holding the world together, but there isn't just one force doing this.");

        add("guide.ruin.subpaths2", "I believe there are 3 primordial elements this power consists of: §lFire§r, §lFrost§r and §lShock§r. They all seem to have a different impact on both the world and my magic. If I focus my studies on one Sub-Path, it's possible I could enhance my control over one of these forces exponentially.");
        add("guide.ruin.subpaths_cnt", "Sub-Paths Cont.");
        add("guide.ruin.fire", "§lFire§r\nFire is the first of the elements I have explored. It appears to have the highest damage and destructive output of the three, being able to set targets aflame.");
        add("guide.ruin.frost", "§lFrost§r\nLike the other side of a coin, Frost is the opposite of Fire. this one seems to affect living creatures ability to perform actions massively slowing any thing affected by it.");
        add("guide.ruin.shock", "§lShock§r\nThe final element, Shock, possesses the most chaotic and rapid nature of the three, chaining lightning and creating storm clouds at will. From what I've gathered, the shock can disrupt some enemies—hindering their spell casting abilities.");

        add("guide.ruin.build_up", "Elemental Overload");
        add("guide.ruin.effects", "Elemental Effects");
        add("guide.ruin.build_up1", "I've discovered that a single creatures body can only take so much of each element. When a target is unable to absorb more of an element, their body seems to react in a different way depending on the element. I'll keep experimenting and try to understand what exactly is happening.");
        add("guide.ruin.fire_status", "§lFire§r\nFire energy explodes out the target, dealing massive damage and setting fire to the area around the target.");
        add("guide.ruin.frost_status", "§lFrost§r\nFreezes foes solid, stopping them from performing any actions temporarily.");
        add("guide.ruin.shock_status", "§lShock§r\nTargets access to the arcane is disrupted, silencing them temporarily and draining their mana.");

        add("guide.ruin.portals", "Ruin Portals");
        add("guide.ruin.keystones", "Keystones");
        add("guide.ruin.portals1", "When I draw from this source of magic, I can feel others tug back at it. I believe there are ancient beings residing in their own realms that have learned to bend these elements to their will.");
        add("guide.ruin.portals2", "I aim to find the gateways to these ancient beings' realms so I can find how they are twisting the elements and create new ways of expressing my own magic.");
        add("guide.ruin.portals3", "While exploring I have found a few gateways, but they seem to all be locked. Unfortunately, the way to make keystones has been long forgotten. I'l have to buy my ruin spells from Spell Brokers for now.");

        add("guide.ruin.path_items", "I have discovered elemental attuned robes and staves throughout the world. As far as I can tell, these staves act as a catalyst for their respective element, causing those who I target to reach Elemental Overload faster. The robes, on the other hand, seems to increase the potency of my spells when I wear the full set.");
        add("guide.ruin.stormweaver_robes", "Within a trial chamber, I have found these robes belonging to a long forgotten Stormweaver. These robes resist Shock damage to a degree.");
        add("guide.ruin.pyromancer_robes", "Exploring the nether, I spotted these robes hot to the touch. With this kind of heat, I imagine Fire spells won't get through very well.");
        add("guide.ruin.cryomancer_robes", "Finally, some Frost robes to protect me from the cold! They were spread between igloos and abandoned shipwrecks.");

        addSpellContents(SpellPath.RUIN, SBSpells.SOLAR_RAY.get(),
                "I have purchased this spell tome from a Spell Broker as I have been unable to decipher its origin but I believe its siphoning energy from the sun.",
                "I was mistaken... This spell isn't channelling energy from the sun, but a Sun God! That's definitely a being I never want to run into again.");

        addSpellContents(SpellPath.RUIN, SBSpells.SHATTERING_CRYSTAL.get(),
                "My attacks are becoming too predictable. I wonder if I can make a spell with a delayed attack. No one will see it coming!",
                "All it came down to was the right element. Using the Frost's innate power to slow things down, I can delay the burst for as long as I want");

        addSpellContents(SpellPath.RUIN, SBSpells.STORMSTRIKE.get(),
                "The magic flowing inside me is fast, almost like an electric current in my veins. I think I have an idea for a new attack.",
                "AHAHAHA I DID IT! By focusing my energy in front of me, I can release the current inside to fire out a lightning bolt");

        addSpellContents(SpellPath.RUIN, SBSpells.ELECTRIC_CHARGE.get(),
                "I don't think I'm powerful enough to take people head on right now. I need to give my self an advantage before the fight starts.",
                "I'm certain this will work... This spell should be able to start building up shock charge on my target before they knew what hit them.");

        addSpellContents(SpellPath.RUIN, SBSpells.STORM_RIFT.get(),
                "I started experimenting with Shock spells by forming small storms in front of me. With all my new found knowledge, how powerful can I make that?",
                "I think I went a little overboard... The strength of the storm vortex was so strong, it ripped through space!");

    }

    protected void transfigContents() {
        add("guide.transfiguration.description", "Transfiguration & Rituals");
        add("guide.transfiguration.blocks", "Ritual Blocks");
        add("guide.transfiguration.items", "Ritual Items");
        add("guide.transfiguration.armor_recipe", "Apparel Ritual");
        add("guide.transfiguration.staff_recipe", "Staff Ritual");
        add("guide.transfigurations.mana_tear", "Mana Tear Ritual");

        add("spellbound.ritual.tier_one", "Tier: 1");
        add("spellbound.ritual.tier_two", "Tier: 2");
        add("spellbound.ritual.tier_three", "Tier: 3");
        add("spellbound.ritual.activation_time", "Activation Time: %s seconds");
        add("spellbound.ritual.duration", "Duration: %s seconds");
        add("spellbound.ritual.duration_not_applicable", "Duration: N/A");

        add("guide.transfiguration.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can manipulate the world around me through the study of transfiguration.");
        add("guide.transfiguration.quote", "Reality is not a rule. It is a suggestion");
        add("guide.transfiguration.description1", "Matter, equivalent exchange, logic—the Transfiguration Path requires great understanding of these concepts. It's more than magic—it's a science.");
        add("guide.transfiguration.description2", "To truly master this path is to reject the world as it appears. Stone can become water, iron can become gold, and a formidable enemy can become a harmless sheep. But to rewrite the laws of physics, you must understand them first.");
        add("guide.transfiguration.rituals", "Alchemy Rituals");
        add("guide.transfiguration.rituals1", "Alchemy, the foundation of everything I do. Whether I am extracting a new spell formula or manipulating my environment, the process is the same: arrange the geometry, balance the components, and force the transmutation.");
        add("guide.transfiguration.rituals2", "The apparatus appears to consists of a central pedestal surrounded by display stands, with each tier increasing the displays and height needed.");
        add("guide.transfiguration.rituals_cont", "Alchemy Rituals Cont.");
        add("guide.transfiguration.pedestal_legend", "- Pedestal");
        add("guide.transfiguration.display_legend", "- Display");
        add("guide.transfiguration.rune_circuit", "If I connect the displays in a ring with these runic symbols I found, the structure acts as a circuit, with the items on displays defining the properties of the ritual.");
        add("guide.transfiguration.pedestal", "I infused a little bit of my magic into an rickety table I had lying around in preparation for my studies. And now I have it—the heart of the altar, where the magic happens.");
        add("guide.transfiguration.display", "I've been chiseling away at these old magic stones for hours. My body aches, but at least I have a way to store my collection of materials");
        add("guide.transfiguration.chalk", "I discovered some runic marking on the walls in the caves. I don't understand what they mean, but it's almost as if I felt magic flowing through the text. I'll write some down for later.");
        add("guide.transfiguration.ritual_talisman", "I have everything in place to begin my experiment, but nothing is happening. Maybe I need a catalyst? I should be wary—if it isn't strong enough, it could be a waste.");
        add("spellbound.ritual.materials", "Ritual Materials");

        add("guide.transfiguration.stave", "In my studies, I've theorized a specialized catalyst that can be tuned to my Transfiguration spells. One that acts as a lens, extending the reach of my magic. To forge this, I must devise a new ritual. If my theories are correct, I'll document the results here.");
        add("guide.transfiguration.robes", "Continuing on with my experimentation, I have succeeded in designing a set of robes. The fabric traps some of the residual magical energy lost from casting, increasing the duration of my Transfiguration spells while wearing them.");

        addTransfigSpellContents(SBSpells.STRIDE.get(),
                "I have been trying to gather ingredients for all of my alchemy rituals, but it's taking me too long to find everything. I need a faster way...",
                "Well, I solved my problem with Transfiguration. You won't believe it, but by mixing the right ingredients, I can supercharge my body. Allowing my legs to work even faster!");
        addTransfigSpellContents(SBSpells.SHADOW_GATE.get(),
                "As my studies have advanced, I am finding myself needing to explore more biomes. I've found that teleportation could be quite useful.",
                "I think I got it! After experimenting with ender pearls, I found I can use them to rip open gateways temporarily. I have created a spell to keep these open for longer.");
        addTransfigSpellContents(SBSpells.MYSTIC_ARMOR.get(),
                "Uh oh, seems like I have angered a few too many Magi. I'm trying to throw together whatever I can, hoping I can protect my self a little more consistently.",
                "Well, I was trying to make myself absorb their mana. While that didn't completely work, I have managed to reduce the impact of their spells.");

        add("ritual.spellbound.create_transfig_helmet", "Create Item:\nCreationist Helmet");
        add("ritual.spellbound.create_transfig_chestplate", "Create Item:\nCreationist Chestplate");
        add("ritual.spellbound.create_transfig_leggings", "Create Item:\nCreationist Leggings");
        add("ritual.spellbound.create_transfig_boots", "Create Item:\nCreationist Boots");
        add("ritual.spellbound.create_transfig_stave", "Create Item:\nCreationist Staff");

        add("ritual.spellbound.create_transfig_helmet.description", "Creates a Creationist Helmet");
        add("ritual.spellbound.create_transfig_chestplate.description", "Creates a Creationist Chestplate");
        add("ritual.spellbound.create_transfig_leggings.description", "Creates Creationist Leggings");
        add("ritual.spellbound.create_transfig_boots.description", "Creates Creationist Boots");
        add("ritual.spellbound.create_transfig_stave.description", "Creates a Creationist Staff");

        add("ritual.spellbound.create_mana_tear", "Create Item:\nMana Tear");
        add("ritual.spellbound.create_mana_tear.description", "Creates a Mana Tear");
    }

    protected void addTransfigSpellContents(SpellType<?> spellType, String lore, String bossLore) {
        addSpellContents(SpellPath.TRANSFIGURATION, spellType, lore, bossLore);
        add("ritual.spellbound.create_" + spellType.location().getPath() + ".description", "Creates a " + checkReplace(spellType) + " spell tome");
        add("ritual.spellbound.create_" + spellType.location().getPath(), "Create Spell Tome:\n" + checkReplace(spellType));
    }

    private String checkReplace(SpellType<?> spellType) {
        return Arrays.stream(spellType.location().getPath().split("_"))
                .map(this::checkReplace)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "))
                .trim();
    }

    protected void summonContents() {
        add("guide.summon.description", "Hidden Dimensions");

        add("guide.summons.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can increase my strength through numbers with the rites of summoning.");
        add("guide.summons.quote", "Death is not the end. It's the beginning of recruitment.");
        add("guide.summon.dimensions", "Dimensions");
        add("guide.summon.description1", "Summoning magic... dragging creatures from the darkest of realms and forcing them to fight on your behalf. Only the strong willed can use this power.");
        add("guide.summon.description2", "To harness this source of magic, a creature must first be defeated to understand its power. But that first requires reaching into these twisted realms where they reside, and hunting these powerful entities.");
        add("guide.summon.dimensions1", "The most powerful creatures to exist lord over their own realms. If I am able to find a way to access these dimensions, then I can learn how to control these beasts and use their powers for myself.");
        add("guide.summon.dimensions2", "I know I can't be the first one to try this I plan to search for any potential portals left by others.");

        add("guide.summon.summoning_stone", "Summoning Stones");
        add("guide.summon.summoning_portal", "Portal Construction");
        add("guide.summon.summoning_stone1", "Within the nether I have found some abandoned forges. Within the rubble was this recipe for a summoning stone.");
        add("guide.summon.summoning_portal1", "I have come across a crumbling structure with these summoning stones arranged like an End portal. They seem to be activated with Magic Essence.");

        add("guide.summon.portal_activation", "Portal Activation");
        add("guide.summon.portal_activation1", "After focusing more of my studies on End portals, I have realised that I need something else. Maybe I can use a block to focus on to locate their destination.");
        add("guide.summon.portal_activation2", "I believe that if I can create just a single summoning stone adjusted to a specific dimension, I could open a gateway by placing it in the middle of my portal and activating it.");
        add("guide.summon.valid_portals", "Well, after a series of trial and error, I have found that these focused summoning stones must be very specific.");
        add("guide.summon.valid_portals1", "When I am able to find a way to create a new focused stone I will be sure to note it down in this book, along with any information regarding the spells I can draw out from the dimension.");

        add("summon.acquisition.description", "Use the keystone below to access the boss's dimension.");
        add("summon.acquisition.boss_rewards", "Boss Rewards");
        add("summon.acquisition.wild_mushroom.lore", "Come to think of it, it's rare to see a single mushroom on its own...");
        addSpellContents(SpellPath.SUMMONS, SBSpells.WILD_MUSHROOM.get(),
                "Fungi are some of the most resilient living organisms. If I can find a mushroom infested realm, just think of the power it could be hiding.",
                "Why did I think going to the home of a notably durable fungi would be a good idea. It always knows where I am... Must be these damn spores."
        );
    }

    protected void divineGuideContents() {
        add("guide.divine.divine_judgement", "Divine Judgement");

        add("divine_action.judgement", "Judgement: ");
        add("divine_action.judgement_required", "Judgement Required: ");
        add("divine_action.cooldown", "Cooldown: %s ticks");

        add("guide.divine.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can call upon divine forces to aid myself and allies and harm foes.");
        add("guide.divine.quote", "Faith is not spoken. It is proven.");
        add("guide.divine.description1", "The Divine Path is the discipline of miracles, drawing power from the absolute forces of not only Light, but Darkness. This magic is not learned—it is bestowed.");
        add("guide.divine.description2", "To walk this path is to become a conduit for higher powers. Whether you seek to be a Saint of Mercy who shields the weak, or a Paladin of Darkness who has fallen to the forces of evil, this path demands action.");
        add("guide.divine.judgement", "Judgement");
        add("guide.divine.judgement_cont", "Judgement Cont.");
        add("guide.divine.judgement1", "I used to think this magic was merely a tool, indifferent to the hand that wielded it. I was wrong. It is watching me.");
        add("guide.divine.judgement2", "I noticed the shift recently. After spending days protecting the weak, my benevolent magic surged, becoming far more potent than before. Yet, out of curiosity, when I turned to cruelty, that warmth withered. In its place, my darker arts flared with a terrifying, violent heat.");
        add("guide.divine.judgement3", "There is a hidden balance at play—a §lJudgement.");
        add("guide.divine.judgement4", "It seems my soul is constantly being weighed. Acts of light tip the scales, amplifying my ability to preserve life but suffocating my ability to take it. Acts of darkness tip them back, fueling my darker arts while severing my connection to the light.");
        add("guide.divine.judgement5", "This invisible score dictates everything. Forbidden knowledge remains locked to me until my nature aligns with its intent, and even the world itself seems to react to my standing. I must choose my path carefully. I cannot be both a Saint and a Monster. The Scales will not allow it.");
        add("guide.divine.divine_temple", "Divine Temples");
        add("guide.divine.divine_temple1", "I have discovered these mysterious, looming structures along my travels. Scattered across the lands are Divine Temples, ancient structures built to worship the Divine.");
        add("guide.divine.valkyr1", "They are not abandoned. Each is protected by a Valkyr, a sentinel of steel and light.");
        add("guide.divine.valkyr2", "I realized my safety hung by a thread. These guardians serve the balance of Judgement. To the righteous, they are silent watchers. But had I approached with a heart stained by cruelty, I have no doubt the Valkyr would have cut me down the moment I crossed the threshold.");
        add("guide.divine.divine_shrine", "The Divine Shrine");
        add("guide.divine.divine_shrine1", "The Divine Shrine is the heart of this magic, yet it functions unlike any other altar I have encountered. It does not desire materials; it desires proof.");
        add("guide.divine.divine_shrine2", "I have learned that spells are not made, but granted. To acquire them, I must perform specific Divine Actions within the Shrine's presence.");
        add("guide.divine.divine_action1", "Guarded by the Valkyr, the Shrine is bound to the temple. Should I attempt to mine or displace the altar, the Valkyr’s wrath will be immediate. I may use the Shrine where it stands, but I am forbidden from taking it.");
        add("guide.divine.divine_action2", "Like the Valkyr, the shrine too seems to measure my Judgement. If my Judgement does not meet the specific requirement for a spell, the altar remains dormant—\ndenying me the power until my nature aligns with its intent.");

        add("guide.divine.divine_actions", "Divine Actions:\n");
        add("healing_touch.heal_mob_to_full.name", "Shepherd");
        add("divine_action.healing_touch.heal_mob_to_full", "Heal any non-hostile mob to full");
        add("healing_touch.use_blessed_bandages.name", "Field Medic");
        add("divine_action.healing_touch.use_blessed_bandages", "Heal your wounds with Blessed Bandages.");
        add("healing_touch.bless_shrine.name", "Caretaker");
        add("divine_action.healing_touch.bless_shrine", "Bless a shrine with a Divine Phial");
        add("healing_touch.heal_mob_to_full.lore", "I'm still rather new to this whole healing magic thing, it might be best to start off small. I wonder if I can catch any animals to practice on?");
        add("healing_touch.use_blessed_bandages.lore", "This world is harsh... ferocious wildlife and undead monsters are running rampant. It will be a serious problem without a way to heal my wounds in a pinch");
        add("healing_touch.bless_shrine.lore", "This shrine I found in the temple seems to resonate with my every action—good or bad. I think as long as I take good care of it, I can become even stronger!");
        add("healing_blossom.decorate_shrine.name", "Gardener");
        add("divine_action.healing_blossom.decorate_shrine", "Decorate a shrine with 18 unique flowers");
        add("healing_blossom.purify_wither_rose.name", "Life From Death");
        add("healing_blossom.grow_ambrosia_bush.name", "Living Pollen");
        add("divine_action.healing_blossom.grow_ambrosia_bush", "Convert a Berry Bush into an Ambrosia Bush");
        add("divine_action.healing_blossom.purify_wither_rose", "Purify a Wither Rose");
        add("healing_blossom.decorate_shrine.lore", "As I grow in tune with the divine, I can feel a strong presence of magic emanating from Overworld flora. I should collect as much as I can to study.");
        add("healing_blossom.grow_ambrosia_bush.lore", "It seems bees fail to see me as an enemy after I've eaten this Ambrosia dish. While helpful, it's not really my taste. I'll just give some to the bees since they seem to like it.");
        add("healing_blossom.purify_wither_rose.lore", "I can sense a sinister aura coming from this black flower. As a servant of the Divine, it is my duty to cleanse it!");

        addSpellContents(SpellPath.DIVINE, SBSpells.HEALING_TOUCH.get(),
                "The power of the Divines is one in which harm to living creatures is seen as a sin. Does that mean I can use this power for the opposite?",
                "It works perfectly. After sustaining an attack, self inflicted or otherwise, I can channel the Divine energy to heal myself.");

        addSpellContents(SpellPath.DIVINE, SBSpells.HEALING_BLOSSOM.get(),
                "The Divines didn't just make sentient creatures, but also plants. I wonder if my borrowed power can be used for flora.",
                "I managed to grow a new kind of flower by imbuing one with some of my Divine energy. I can feel it emitting a healing aura when I'm around.");
    }

    protected void deceptionGuideContents() {
        add("guide.deception.quote", "Honor is a shackle. Break it.");

        add("guide.deception.acquisition", "Spell Acquisition");
        add("guide.deception.description1", "Deceit, trickery, fraud... All words of power to fool those who would doubt you. Using this path of magic I will twist what people see, manipulating what they understand as truth.");
        add("guide.deception.description2", "This discipline demands a new kind of sight—one that sees through the world's lies to weave falsehoods of my own");
        add("guide.deception.acquisition1", "How am I to learn this damn path when I can't even begin to find these books. I could ask around, but what if they lie? I must be cautious...");
        add("guide.deception.acquisition2", "FINE! I caved in and bought some spells from a Spell Broker. But you can bet I'm going to try these on someone else first!");

        addSpellContents(SpellPath.DECEPTION, SBSpells.SHADOWBOND.get(),
                "Someone has requested my help turning them invisible so they can sneak into a jungle temple's vault. If only I could reverse our roles...",
                "Well that has done the trick! As soon as they passed the traps, I swapped places with them. Let's just hope they don't track me down.");


        addSpellContents(SpellPath.DECEPTION, SBSpells.PURGE_MAGIC.get(),
                "I've learned to excel in sneaking, but when I'm caught, I'm finding my self too vulnerable. I need to find a way to turn the tides, and quick.",
                "This is good progress. This won't hurt anyone, but this should be a super effective a way to dispel my enemies spells in a pinch.");
    }

    protected void skillDescriptions() {
        add("spellbound.skill_tooltip.more_details", "Press §fShift§r for Details");
        add("spellbound.skill_tooltip.details", "When Unlocked:");
        add("spellbound.skill_tooltip.damage", "%s %s");
        add("spellbound.skill_tooltip.duration", "%s Duration");
        add("spellbound.skill_tooltip.radius", "%s Block Radius");
        add("spellbound.skill_tooltip.range", "%s Range");
        add("spellbound.skill_tooltip.mana_cost", "%s Mana Cost");
        add("spellbound.skill_tooltip.mana_tick_cost", "%s Mana/s");
        add("spellbound.skill_tooltip.mana", "%s Mana");
        add("spellbound.skill_tooltip.mana_damage", "%s Mana Damage");
        add("spellbound.skill_tooltip.heal", "%s Health");
        add("spellbound.skill_tooltip.max_health_heal", "Restore %s%% Health");
        add("spellbound.skill_tooltip.heal_from_target", "%s%% Health from Target");
        add("spellbound.skill_tooltip.hunger", "%s Hunger");
        add("spellbound.skill_tooltip.lifesteal", "%s%% Lifesteal");
        add("spellbound.skill_tooltip.manasteal", "%s%% Manasteal");
        add("spellbound.skill_tooltip.mana_absorb", "%s Mana Absorb");
        add("spellbound.skill_tooltip.mana_drain", "%s Mana Drain");
        add("spellbound.skill_tooltip.potency", "%s%% Potency");
        add("spellbound.skill_tooltip.cooldown", "%s Cooldown");
        add("spellbound.skill_tooltip.effect_duration", "%s Effect Duration");
        add("spellbound.skill_tooltip.proc_chance", "%s%% Proc Chance");
        add("spellbound.skill_tooltip.proc_duration", "%s Proc Duration");
        add("spellbound.skill_tooltip.proc_range", "%s Proc Range");
        add("spellbound.skill_tooltip.charge_duration", "%s Charge Duration");
        add("spellbound.skill_tooltip.max_charges", "%s Charges");
        add("spellbound.skill_tooltip.modify_damage", "%s%% %s");
        add("spellbound.skill_tooltip.modify_duration", "%s%% Duration");
        add("spellbound.skill_tooltip.modify_mana_cost", "%s%% Mana Cost");
        add("spellbound.skill_tooltip.modify_radius", "%s%% Radius");
        add("spellbound.skill_tooltip.modify_range", "%s%% Range");
        add("spellbound.skill_tooltip.modify_spell_xp", "%s%% Spell XP");
        add("spellbound.skill_tooltip.health_to_damage", "%s%% Health to Damage");
        add("spellbound.skill_tooltip.mana_to_health", "%s%% Mana to Health");
        add("spellbound.skill_tooltip.health_to_mana", "%s%% Health to Mana");
        add("spellbound.skill_tooltip.percent_damage_per_charge", "%s%% %s per Charge");
        add("spellbound.skill_tooltip.flat_damage_per_charge", "%s %s per Charge");
        add("spellbound.skill_tooltip.range_per_charge", "%s%% Range per Charge");
        add("spellbound.skill_tooltip.flat_range_per_charge", "%s Range per Charge");
        add("spellbound.skill_tooltip.radius_per_charge", "%s%% Radius per Charge");
        add("spellbound.skill_tooltip.flat_radius_per_charge", "%s Radius per Charge");
        add("spellbound.skill_tooltip.knockback_per_charge", "%s%% Knockback per Charge");
        add("spellbound.skill_tooltip.health_per_charge", "%s Health per Charge");
        add("spellbound.skill_tooltip.attribute_per_charge", "%s per Charge");
        add("spellbound.skill_tooltip.max_charge_damage", "%s%% %s at Max Charge");
        add("spellbound.skill_tooltip.random_damage", "%s-%s %s");
        add("spellbound.skill_tooltip.random_mana", "%s-%s Mana");
        add("spellbound.skill_tooltip.max_health_damage", "%s%% Max Health Damage");
        add("spellbound.skill_tooltip.damage_reflection", "%s%% Damage Reflection");
        add("spellbound.skill_tooltip.ally_range", "%s Ally Buff Range");
        add("spellbound.skill_tooltip.fire_build_up", "%s Fire Build Up");
        add("spellbound.skill_tooltip.frost_build_up", "%s Frost Build Up");
        add("spellbound.skill_tooltip.shock_build_up", "%s Shock Build Up");
        add("spellbound.skill_tooltip.disease_build_up", "%s Disease Build Up");
        add("spellbound.skill_tooltip.target_damage", "%s%% Target Damage");
        add("spellbound.skill_tooltip.target_physical_damage", "%s%% Target Physical Damage");
        add("spellbound.skill_tooltip.target_spell_damage", "%s%% Target Spell Damage");
        add("spellbound.skill_tooltip.target_spell_potency", "%s%% Target Spell Potency");
        add("spellbound.skill_tooltip.target_cast_chance", "%s%% Target Cast Chance");
        add("spellbound.skill_tooltip.target_fire_resist", "%s%% Fire Resist on Target");
        add("spellbound.skill_tooltip.target_ice_resist", "%s%% Ice Resist on Target");
        add("spellbound.skill_tooltip.target_shock_resist", "%s%% Shock Resist on Target");
        add("spellbound.skill_tooltip.target_disease_resist", "%s%% Disease Resist on Target");
        add("spellbound.skill_tooltip.knockback", "%s Knockback Strength");
        add("spellbound.skill_tooltip.explosion_radius", "%s Explosion Radius");
        add("spellbound.skill_tooltip.projectile_count", "%s Projectile Count");
        add("spellbound.skill_tooltip.random_projectile_count", "+%s-%s Projectile Count");
        add("spellbound.skill_tooltip.projectile_count_per_charge", "%s Projectile Count per Charge");
        add("spellbound.skill_tooltip.damage_redux", "%s%% Damage Reduction");
        add("spellbound.skill_tooltip.physical_damage_redux", "%s%% Physical Damage Reduction");
        add("spellbound.skill_tooltip.spell_damage_redux", "%s%% Spell Damage Reduction");
        add("spellbound.skill_tooltip.projectile_damage_redux", "%s%% Projectile Damage Reduction");
        add("spellbound.skill_tooltip.hp_threshold", "%s%% HP Threshold");
        add("spellbound.skill_tooltip.mob_effect", "Applies ");
        add("spellbound.skill_tooltip.spell", "Casts ");
        add("spellbound.skill_tooltip.potency_scaling", "Scales with Potency");
        add("spellbound.skill_tooltip.choice", "Choice Upgrade");
        add("spellbound.skill_tooltip.requires", "Requires ");
        add("spellbound.skill_tooltip.cast_scales", "Max Cast Scales with Level");
        add("spellbound.skill_tooltip.condition", "If ");
        add("spellbound.skill_tooltip.channeled", "Channeled Cast");
        add("spellbound.skill_tooltip.charged", "Charged Cast");
        add("spellbound.skill_tooltip.invisibility_no_stack", "Invisibility Does Not Stack");
        add("spellbound.skill_tooltip.invisibility_override", "Overrides Invisibility");
        add("spellbound.skill_tooltip.dolphins_fin", "%s%% Water Slowdown");
        add("spellbound.skill_tooltip.burning_adhesive", "%s Damage to Armor");
        add("spellbound.skill_tooltip.molten_core", "%s%% Damage per Pierce");
        add("spellbound.skill_tooltip.shatter_skin", "%s%% Armor to Damage");
        add("spellbound.skill_tooltip.mystic_armor", "%s%% Reduction per Path Level");
        add("spellbound.skill_tooltip.sublime_beacon", "%s%% Armor to Health");
        add("spellbound.skill_tooltip.farmer_villager", "Increase Crop Yields");
        add("spellbound.skill_tooltip.air_bubble", "%s Air Supply");
        add("spellbound.skill_tooltip.extended_grace", "%s%% Resource Regen");

        addSkillTooltip(SBSkills.FIREBALL, "Launches a ball of fire that explodes on impact");
        addSkillTooltip(SBSkills.RAPID_FIRE, "Fire a rapid stream of 3 smaller Fireballs.");
        addSkillTooltip(SBSkills.STICKY_BOMB, "Fireballs stick to surfaces or enemies, detonating after a short duration.");
        addSkillTooltip(SBSkills.CHARGED_BLAST, "Fireball can be Charged, increasing its size, explosion radius, and damage per charge.");
        addSkillTooltip(SBSkills.VOLATILE_CLUSTER, "Fireball can be Charged, increasing the number of Fireballs shot per charge.");
        addSkillTooltip(SBSkills.HOMING_MISSILE, "Fireball follows the caster's target.");
        addSkillTooltip(SBSkills.EXPLOSIVE_AMPLIFIER, "Increases the explosion radius of all Fireball variants.");
        addSkillTooltip(SBSkills.BURNING_ADHESIVE, "Enemies struck by Sticky Bomb take additional damage to their armor.");
        addSkillTooltip(SBSkills.MOLTEN_CORE, "Fully Charged Fireballs from Charged Blast pierce enemies.");
        addSkillTooltip(SBSkills.CLUSTER_STRIKE, "Increases the max charges of Volatile Cluster");
        addSkillTooltip(SBSkills.AUTO_TARGETING, "Homing Missile no longer needs a direct target and will track the nearest enemy to the caster.");

        addSkillTooltip(SBSkills.FLAME_JET, "Releases a short, concentrated burst of fire in front of the caster.");
        addSkillTooltip(SBSkills.JET_ENGINE, "Propels the caster in the direction they are facing, dealing Fire damage to enemies behind.");
        addSkillTooltip(SBSkills.FLAME_GEYSER, "Flame Jet originates from the position the caster points at, increasing the jet's length.");
        addSkillTooltip(SBSkills.FLAME_INFERNO, "Flame Jet fires in a ring around the caster, dealing reduced damage.");
        addSkillTooltip(SBSkills.TURBO_CHARGE, "Flame Jet can be charged. The longer the charge, the longer the length of the jet and damage dealt.");
        addSkillTooltip(SBSkills.IGNITION_BURST, "Flame Jet explodes at the end of its range at max charge, dealing extra Fire damage.");
        addSkillTooltip(SBSkills.EXPULSION_BLAST, "Flame Jet sends enemies flying in the opposite direction of the caster.");
        addSkillTooltip(SBSkills.JET_STABILIZATION, "Flame Jet can be cast while walking or sprinting.");
        addSkillTooltip(SBSkills.TURBULENCE_STREAM, "If Flame Jet is recast within a short duration, its range is increased.");
        addSkillTooltip(SBSkills.AFTERSHOCK_COMPRESSION, "If Flame Jet is recast within a short duration, its damage is increased.");
        addSkillTooltip(SBSkills.IRON_MAN, "Consume a Smoldering Shard. Flame Jet can be channeled to continuously propel the caster.");

        addSkillTooltip(SBSkills.SOLAR_RAY, "Fire a beam of fire that deals continuous damage.");
        addSkillTooltip(SBSkills.SUNSHINE, "Doubles the range of Solar Ray.");
        addSkillTooltip(SBSkills.HEALING_LIGHT, "Allies hit by the ray are healed.");
        addSkillTooltip(SBSkills.OVERPOWER, "Gain the ability to slowly move while casting Solar Ray.");
        addSkillTooltip(SBSkills.CONCENTRATED_HEAT, "Continuously hitting the same target increases the damage of Solar Ray.");
        addSkillTooltip(SBSkills.OVERHEAT, "Continuous use of Solar Ray causes the caster to emit intense heat, dealing fire damage to nearby enemies.");
        addSkillTooltip(SBSkills.SOLAR_BURST, "Both ends of the beam periodically release a small Solar Burst that deals additional fire damage.");
        addSkillTooltip(SBSkills.SOLAR_BORE, "The end of the Solar Ray opposite of the caster periodically explodes, setting the ground ablaze.");
        addSkillTooltip(SBSkills.BLINDING_LIGHT, "Enemies hit by the beam are blinded.");
        addSkillTooltip(SBSkills.AFTERGLOW, "Enemies hit are marked with a glow. Marked enemies take extra fire damage.");
        addSkillTooltip(SBSkills.POWER_OF_THE_SUN, "Solar Ray deals more damage during the day.");

        addSkillTooltip(SBSkills.VOLCANO, "Create a volcanic eruption that spits out 8 lava bombs per second for 10 seconds.");
        addSkillTooltip(SBSkills.INFERNO_CORE, "After the eruption ends, the volcano drops a Smoldering Shard.");
        addSkillTooltip(SBSkills.EXPLOSIVE_BARRAGE, "Each lava bomb explodes on impact.");
        addSkillTooltip(SBSkills.LAVA_FLOW, "Lava bombs turn into lava pools on impact.");

        addSkillTooltip(SBSkills.SHATTERING_CRYSTAL, "Creates a crystal of ice. Cast again to detonate it, dealing damage to nearby enemies.");
        addSkillTooltip(SBSkills.FRIGID_BLAST, "Enemies hit by the blast are significantly slowed.");
        addSkillTooltip(SBSkills.ICE_SHARD, "Recast on a crystal: destroy it to make it drop a frozen shard.");
        addSkillTooltip(SBSkills.CHILL, "Crystal passively emits a freezing aura, dealing frost damage every second.");
        addSkillTooltip(SBSkills.FROZEN_SHRAPNEL, "The crystal now sends out ice shards that deal extra frost damage.");
        addSkillTooltip(SBSkills.HYPOTHERMIA, "Enemies hit by the explosion have their frost resistance reduced.");
        addSkillTooltip(SBSkills.CRYSTAL_ECHO, "After detonation, the crystal temporarily reforms in its original location (once per crystal).");
        addSkillTooltip(SBSkills.THIN_ICE, "Enemies that walk near the crystal will instantly trigger the detonation.");
        addSkillTooltip(SBSkills.CHAOTIC_SHATTER, "Detonating one crystal will detonate others in the area.");
        addSkillTooltip(SBSkills.LINGERING_FROST, "After detonation, the crystal leaves a damaging mist behind, dealing frost damage and significant frost build up.");
        addSkillTooltip(SBSkills.GLACIAL_IMPACT, "Recast with a Frozen Shard to mark a crystal. Marked crystal's explosion applies Frozen and Permafrost to enemies hit.");

        addSkillTooltip(SBSkills.STORMSTRIKE, "Send out a bolt of lightning that charges a target");
        addSkillTooltip(SBSkills.STATIC_SHOCK, "Hitting a block now creates a small explosion that applies Stormstrike to anyone it hits.");
        addSkillTooltip(SBSkills.ELECTRIFY, "Decreases the target's shock resistance.");
        addSkillTooltip(SBSkills.SHOCK_FACTOR, "Deals extra damage scaling with the caster's mana.");
        addSkillTooltip(SBSkills.PURGE, "Deals extra damage to summoned targets, scaling with the caster's mana.");
        addSkillTooltip(SBSkills.REFRACTION, "Restores mana back to the caster when the target takes Shock damage from sources other than Stormstrike.");
        addSkillTooltip(SBSkills.PULSATION, "Chance to paralyze the target each damage tick.");
        addSkillTooltip(SBSkills.STORM_SHARD, "If the target dies while affected by Stormstrike, the caster is awarded a Storm Shard.");
        addSkillTooltip(SBSkills.CHARGED_ATMOSPHERE, "Decreases the mana cost of Shock spells for a duration.");
        addSkillTooltip(SBSkills.DISARM, "Chance to disarm the target each damage tick.");
        addSkillTooltip(SBSkills.SUPERCHARGE, "Increases the potency of Shock spells if the target dies while affected by Stormstrike.");

        addSkillTooltip(SBSkills.ELECTRIC_CHARGE, "Sneakily apply an electric charge to the target. Recast to discharge.");
        addSkillTooltip(SBSkills.ELECTRIFICATION, "Applies Stormstrike on discharge.");
        addSkillTooltip(SBSkills.SUPERCONDUCTOR, "Decreases target's shock resistance on discharge.");
        addSkillTooltip(SBSkills.PIEZOELECTRIC, "If killed by Electric Charge, the enemy drops a storm shard.");
        addSkillTooltip(SBSkills.OSCILLATION, "Increases the discharge damage for each storm shard in the caster's inventory. All shards are destroyed on discharge.");
        addSkillTooltip(SBSkills.HIGH_VOLTAGE, "Recast with a storm shard to instantly stun the target.");
        addSkillTooltip(SBSkills.UNLEASHED_STORM, "If killed by Electric Charge, the target will explode, damaging nearby enemies.");
        addSkillTooltip(SBSkills.STORM_SURGE, "If killed by Electric Charge, mana is restored to the caster.");
        addSkillTooltip(SBSkills.CHAIN_REACTION, "The discharge applies Electric Charge to all nearby enemies, including the caster. The secondary charge is discharged immediately.");
        addSkillTooltip(SBSkills.AMPLIFY, "Electric Charge can be Channeled seconds to increase the damage");
        addSkillTooltip(SBSkills.ALTERNATING_CURRENT, "The discharge has a small chance to instantly kill the target. Does not work on target with more than twice the caster's current health. On failure, the caster takes damage proportional to their max health.");

        addSkillTooltip(SBSkills.STORM_RIFT, "Creates a storm portal. If two portals are active, those approaching either get warped across and take shock damage to health and mana.");
        addSkillTooltip(SBSkills.STORM_FURY, "The vortex doubles in size and damage.");
        addSkillTooltip(SBSkills.DISPLACEMENT_FIELD, "A single portal can now teleport enemies to a random location.");
        addSkillTooltip(SBSkills.MAGNETIC_FIELD, "The vortex has twice the pull strength. Enemies caught in the field have their armor reduced.");
        addSkillTooltip(SBSkills.EVENT_HORIZON, "When a target is warped, they pull nearby enemies towards the warp field.");
        addSkillTooltip(SBSkills.CHARGED_RIFT, "Each warp between portals charges the storm, increasing shock damage.");
        addSkillTooltip(SBSkills.MOTION_SICKNESS, "Warped enemies have their movement, attack, and mining speed reduced.");
        addSkillTooltip(SBSkills.FORCED_WARP, "Upon being warped, targets are launched out of the portal with a high velocity, potentially dealing damage on impact.");
        addSkillTooltip(SBSkills.STORM_CALLER, "Generate a cloud above both portals that discharge lightning periodically dealing shock damage.");
        addSkillTooltip(SBSkills.IMPLOSION, "Recast while targeting a portal with a storm shard to detonate the portal, applying Stormstrike to anyone in the area.");
        addSkillTooltip(SBSkills.ORBITAL_SHELL, "Recast while targeting a portal with a shard to mark a portal. Marked portals will orbit around the origin.");

//        addSkillTooltip(SBSkills.CYCLONE, "Fire a tornado that blows away enemies with a 5-block radius for 10 seconds.");
//        addSkillTooltip(SBSkills.WHIRLING_TEMPEST, "The tornado now pulls enemies towards the center before launching them.");
//        addSkillTooltip(SBSkills.FALLING_DEBRIS, "Cyclone occasionally picks up blocks, dealing damage on impact.");
//        addSkillTooltip(SBSkills.VORTEX, "Cyclones can combine to increase the size and push/pull range. Can stack up to 3 times.");
//        addSkillTooltip(SBSkills.MAELSTROM, "Increases the max stack size of Cyclone from 3 to 6.");
//        addSkillTooltip(SBSkills.HURRICANE, "Increases the push/pull force.");
//        addSkillTooltip(SBSkills.EYE_OF_THE_STORM, "Caster can ride the Cyclone. Grants Slow Falling on dismount.");
//        addSkillTooltip(SBSkills.GALE_FORCE, "The cyclones moves faster and last 5 seconds longer.");
//        addSkillTooltip(SBSkills.FROSTFRONT, "Enemies caught take 4 frost damage per second and are have their movement speed slowed by 50%.");
//        addSkillTooltip(SBSkills.STATIC_CHARGE, "Enemies caught take 4 shock damage per second");
//        addSkillTooltip(SBSkills.HAILSTORM, "Casting Cyclone triggers a hailstorm (requires both Static Charge and Frostfront).");

        addSkillTooltip(SBSkills.STRIDE, "Increases the caster's movement speed");
        addSkillTooltip(SBSkills.QUICK_SPRINT, "Speed is additionally increased by a short amount at the start of the spell.");
        addSkillTooltip(SBSkills.GALLOPING_STRIDE, "Base spell speed is increased permanently.");
        addSkillTooltip(SBSkills.RIDERS_RESILIENCE, "All movement benefits are applied to mounts.");
        addSkillTooltip(SBSkills.FLEETFOOTED, "Nearby allies gain movement speed while near the caster.");
        addSkillTooltip(SBSkills.SUREFOOTED, "Step height is increased.");
        addSkillTooltip(SBSkills.AQUA_TREAD, "Gain the ability to walk on water.");
        addSkillTooltip(SBSkills.ENDURANCE, "Spell duration is doubled.");
        addSkillTooltip(SBSkills.MOMENTUM, "For each second travelled, the caster gains additional attack speed.");
        addSkillTooltip(SBSkills.STAMPEDE, "You can charge through enemies, knocking them back and dealing magic damage.");
        addSkillTooltip(SBSkills.MARATHON, "Food consumption is halted.");

        addSkillTooltip(SBSkills.DOLPHINS_FIN, "Increases the caster's swim speed and water movement efficiency. Also reduces the caster's water speed reduction.");
        addSkillTooltip(SBSkills.MERMAIDS_TAIL, "Increases the swim speed and water speed reduction bonuses.");
        addSkillTooltip(SBSkills.AQUATIC_DASH, "Recast to perform an instant dash forward through the water.");
        addSkillTooltip(SBSkills.SHARK_ATTACK, "Passing through an enemy with Aquatic Dash deals minor damage and knockback.");
        addSkillTooltip(SBSkills.ECHOLOCATION, "Recast to emit a sonar ping, highlighting all entities in the water within range.");
        addSkillTooltip(SBSkills.POD_LEADER, "Nearby allies gain the swim speed benefits.");
        addSkillTooltip(SBSkills.SLIPSTREAM, "Continuously swimming increases the swim speed bonus and grants immunity to Mining Fatigue.");
        addSkillTooltip(SBSkills.SONAR_BLAST, "Recast to emit a conical wave of force in the direction the caster is facing.");
        addSkillTooltip(SBSkills.KELP_VEIL, "Remaining motionless underwater grants the caster invisibility until the next movement or action.");
        addSkillTooltip(SBSkills.HAMMERHEAD, "Underwater mining speed reduction is negated.");
        addSkillTooltip(SBSkills.OCEAN_DWELLER, "Gain the ability to breathe underwater.");

        addSkillTooltip(SBSkills.COBBLED_HIDE, "Increases the caster's armor");
        addSkillTooltip(SBSkills.IRON_HIDE, "Additionally increases the caster's armor and gain projectile damage reduction.");
        addSkillTooltip(SBSkills.DIAMOND_HIDE, "Additionally increases the caster's armor and gain spell damage reduction.");
        addSkillTooltip(SBSkills.DRAGON_HIDE, "Additionally increases the caster's armor and gain fire resistance.");
        addSkillTooltip(SBSkills.GRANITE_GRIP, "Increases the caster's knockback resistance.");
        addSkillTooltip(SBSkills.BEDROCK_BASTION, "Falling below a certain HP threshold grants the caster temporary invulnerability until the next hit, reducing spell duration.");
        addSkillTooltip(SBSkills.STONE_WALL, "Increases the spell's duration.");
        addSkillTooltip(SBSkills.SHATTER_SKIN, "Recast to explode the rocky shell, dealing damage proportional to the armor bonus to nearby enemies.");
        addSkillTooltip(SBSkills.MASONRY_WARD, "Automatically block a portion of spell damage every few seconds.");
        addSkillTooltip(SBSkills.BOULDERBACK, "Retaliate a spray of gravel when struck by melee damage, reflecting a portion of the damage back to the attacker.");
        addSkillTooltip(SBSkills.INFUSED_STONE, "Receiving spell damage increases mana regeneration.");

        addSkillTooltip(SBSkills.SHADOW_GATE, "Deploy 2 shadow portals (must be in a low light level), allowing passage in both directions with 50 blocks.");
        addSkillTooltip(SBSkills.REACH, "Double the range of the portals");
        addSkillTooltip(SBSkills.BLINK, "Passing through the portals increases the caster's movement speed.");
        addSkillTooltip(SBSkills.SHADOW_ESCAPE, "When the caster enters the portal at low health, they gain invisibility after exiting.");
        addSkillTooltip(SBSkills.OPEN_INVITATION, "Anyone can pass through the portals.");
        addSkillTooltip(SBSkills.QUICK_RECHARGE, "The caster receives mana any time someone passes through a portal.");
        addSkillTooltip(SBSkills.UNWANTED_GUESTS, "Enemies that pass through a portal have their attack and spell damage reduced.");
        addSkillTooltip(SBSkills.BAIT_AND_SWITCH, "Enemies passing through a portal take damage to health and mana.");
        addSkillTooltip(SBSkills.DARKNESS_PREVAILS, "Portals can be spawned in any light level.");
        addSkillTooltip(SBSkills.GRAVITY_SHIFT, "Exiting the portal launches entities in the air, applying slow falling to the caster and allies, if applicable.");
        addSkillTooltip(SBSkills.DUAL_DESTINATION, "Can now deploy an additional portal. Order of travel goes by order placed.");

        addSkillTooltip(SBSkills.MYSTIC_ARMOR, "Reduces incoming spell damage, scaling with Path level.");
        addSkillTooltip(SBSkills.FORESIGHT, "Decreases spell's mana cost");
        addSkillTooltip(SBSkills.ARCANE_VENGEANCE, "Increases attack damage after the caster blocks an attack.");
        addSkillTooltip(SBSkills.EQUILIBRIUM, "When the caster gets hit, reflect damage scaling with health back to the attacker.");
        addSkillTooltip(SBSkills.PLANAR_DEFLECTION, "Reflects a portion of melee damage taken back to the attacker.");
        addSkillTooltip(SBSkills.PURSUIT, "Increases the caster's movement speed.");
        addSkillTooltip(SBSkills.COMBAT_PERCEPTION, "Chance to dodge a melee attack.");
        addSkillTooltip(SBSkills.CRYSTALLINE_ARMOR, "Increases the caster's armor.");
        addSkillTooltip(SBSkills.ELDRITCH_INTERVENTION, "Restores caster to half health if below a certain HP threshold.");
        addSkillTooltip(SBSkills.SUBLIME_BEACON, "Periodically restores health scaling with the caster's armor.");
        addSkillTooltip(SBSkills.SOUL_RECHARGE, "Restores the caster to full health if below a certain HP threshold, consuming a flux shard in the caster's inventory.");

        addSkillTooltip(SBSkills.SUMMON_UNDEAD, "Plants a wild mushroom at the target location, periodically expelling magic spores, dealing damage to nearby enemies.");
        addSkillTooltip(SBSkills.SUMMON_SKELETON, "Increases the spore radius.");
        addSkillTooltip(SBSkills.SUMMON_DROWNED, "Decreases the explosion interval.");
        addSkillTooltip(SBSkills.SUMMON_ZOMBIFIED_PIGLIN, "Spores now poison enemies.");
        addSkillTooltip(SBSkills.SUMMON_PHANTOM, "Enemies hit by a mushroom explosion are taunted.");
        addSkillTooltip(SBSkills.ROTTEN_SOLDIERS, "Each active mushroom increases the spell's damage.");
        addSkillTooltip(SBSkills.HALL_OF_THE_DEAD, "When 3 mushrooms are active, gain increased mana regeneration.");
        addSkillTooltip(SBSkills.SUNKEN_BREATH, "If a target dies to a mushroom, the spell temporarily gains potency.");
        addSkillTooltip(SBSkills.CRIMSON_PACT, "If a target dies to a mushroom, the casting cost of the spell is temporarily decreased.");
        addSkillTooltip(SBSkills.SILENT_NIGHT, "Recast on a mushroom to spawn a Mini Mushroom minion.");
        addSkillTooltip(SBSkills.CORPSE_EXPLOSION, "Mini Mushroom minions can be bonemealed to spawn a Giant Mushroom.");

        addSkillTooltip(SBSkills.WILD_MUSHROOM, "Plants a wild mushroom at the target location, periodically expelling magic spores, dealing damage to nearby enemies.");
        addSkillTooltip(SBSkills.VILE_INFLUENCE, "Increases the spore radius.");
        addSkillTooltip(SBSkills.HASTENED_GROWTH, "Decreases the explosion interval.");
        addSkillTooltip(SBSkills.ENVENOM, "Spores now poison enemies.");
        addSkillTooltip(SBSkills.PARASITIC_FUNGUS, "Enemies hit by a mushroom explosion are taunted.");
        addSkillTooltip(SBSkills.NATURES_DOMINANCE, "Each active mushroom increases the spell's damage.");
        addSkillTooltip(SBSkills.FUNGAL_HARVEST, "When 3 mushrooms are active, gain increased mana regeneration.");
        addSkillTooltip(SBSkills.POISON_ESSENCE, "If a target dies to a mushroom, the spell temporarily gains potency.");
        addSkillTooltip(SBSkills.SYNTHESIS, "If a target dies to a mushroom, the casting cost of the spell is temporarily decreased.");
        addSkillTooltip(SBSkills.LIVING_FUNGUS, "Recast on a mushroom to spawn a Mini Mushroom minion.");
        addSkillTooltip(SBSkills.PROLIFERATION, "Mini Mushroom minions can be bonemealed to spawn a Giant Mushroom.");

/*        addSkillTooltip(SBSkills.WILD_MUSHROOM, "Plants a wild mushroom at the target location, periodically expelling magic spores, dealing damage to nearby enemies.");
        addSkillTooltip(SBSkills.VILE_INFLUENCE, "Increases the spore radius.");
        addSkillTooltip(SBSkills.HASTENED_GROWTH, "Decreases the explosion interval.");
        addSkillTooltip(SBSkills.ENVENOM, "Spores now poison enemies.");
        addSkillTooltip(SBSkills.PARASITIC_FUNGUS, "Enemies hit by a mushroom explosion are taunted.");
        addSkillTooltip(SBSkills.NATURES_DOMINANCE, "Each active mushroom increases the spell's damage.");
        addSkillTooltip(SBSkills.FUNGAL_HARVEST, "When 3 mushrooms are active, gain increased mana regeneration.");
        addSkillTooltip(SBSkills.POISON_ESSENCE, "If a target dies to a mushroom, the spell temporarily gains potency.");
        addSkillTooltip(SBSkills.SYNTHESIS, "If a target dies to a mushroom, the casting cost of the spell is temporarily decreased.");
        addSkillTooltip(SBSkills.LIVING_FUNGUS, "Recast on a mushroom to spawn a Mini Mushroom minion.");
        addSkillTooltip(SBSkills.PROLIFERATION, "Mini Mushroom minions can be bonemealed to spawn a Giant Mushroom.");*/

        addSkillTooltip(SBSkills.SUMMON_CAT_SPIRIT, "Summons a totem spirit for 60 seconds. It changes between warrior form (fighting stance) and cat form (healing stance).");
        addSkillTooltip(SBSkills.CATS_AGILITY, "In cat form, the spirit gains increased movement speed.");
        addSkillTooltip(SBSkills.FERAL_FURY, "In cat form, the spirit gains increase attack damage and speed.");
        addSkillTooltip(SBSkills.PRIMAL_RESILIENCE, "In cat form, the spirit's regenerate +5% of its max health.");
        addSkillTooltip(SBSkills.TOTEMIC_BOND, "The caster receives a portion of the spirit's healing while in cat form.");
        addSkillTooltip(SBSkills.STEALTH_TACTICS, "In cat form, the spirit will turn invisible for 7 seconds if its health drops below 25%. 1 min. cooldown.");
        addSkillTooltip(SBSkills.SAVAGE_LEAP, "In warrior form, the spirit can perform a leap forward, knocking back all enemies.");
        addSkillTooltip(SBSkills.TOTEMIC_ARMOR, "In warrior form, the spirit receives an armor buff that reduces physical damage by 25%.");
        addSkillTooltip(SBSkills.WARRIORS_ROAR, "In warrior form, the spirit can let out a roar that increases ally attack damage by 15% for 10 seconds.");
        addSkillTooltip(SBSkills.TWIN_SPIRITS, "The caster gains the ability to summon a second spirit, allowing for two spirits to fight simultaneously - one in warrior form, the other in cat form.");
        addSkillTooltip(SBSkills.NINE_LIVES, "If the spirit is killed, it will instantly revive with 50% health (only once per summoning).");

        addSkillTooltip(SBSkills.HEALING_TOUCH, "Heals the caster for a short duration.");
        addSkillTooltip(SBSkills.BLASPHEMY, "Inflicts Disease buildup on the attacker when struck by melee.");
        addSkillTooltip(SBSkills.CONVALESCENCE, "Grants immunity to Disease.");
        addSkillTooltip(SBSkills.DIVINE_BALANCE, "Increases the duration and the mana cost.");
        addSkillTooltip(SBSkills.NATURES_TOUCH, "Instantly restores a small portion of health to the caster.");
        addSkillTooltip(SBSkills.CLEANSING_TOUCH, "Removes a random negative effect from the caster.");
        addSkillTooltip(SBSkills.ACCELERATED_GROWTH, "Instantly restores a small portion of hunger to the caster.");
        addSkillTooltip(SBSkills.HEALING_STREAM, "Each tick restores extra health scaling with the caster's missing mana.");
        addSkillTooltip(SBSkills.TRANQUILITY_OF_WATER, "Increases mana regeneration");
        addSkillTooltip(SBSkills.OVERGROWTH, "While at full health, each heal tick increases the caster's max health.");
        addSkillTooltip(SBSkills.OAK_BLESSING, "Temporarily increases armor if the caster's health drops below a certain threshold.");

        addSkillTooltip(SBSkills.BLESSING, "Grants a small regeneration buff to the target.");
        addSkillTooltip(SBSkills.COURAGE, "Increases the targets armor.");
        addSkillTooltip(SBSkills.ARCANE_RESTORATION, "Coverts the regeneration to mana.");
        addSkillTooltip(SBSkills.SATIATING_BLESSING, "Converts the regeneration to hunger.");
        addSkillTooltip(SBSkills.AIR_BUBBLE, "Converts the regeneration to air supply.");
        addSkillTooltip(SBSkills.PURIFYING_WARD, "Removes a random negative effect from the target.");
        addSkillTooltip(SBSkills.EXTENDED_GRACE, "Increases the duration and amplifies the regeneration effect.");
        addSkillTooltip(SBSkills.SHARED_BOON, "Allows Blessing to target two allies within a small radius if cast near another target.");
        addSkillTooltip(SBSkills.OVERFLOWING_AID, "Grants damage reduction to the target if the relevant resource is full.");
        addSkillTooltip(SBSkills.CONSECRATED_PRESENCE, "Allies within a short distance of a blessed target receive half the benefits.");
        addSkillTooltip(SBSkills.UPLIFTING_CHORUS, "If the target takes a fatal blow, they are revived. The caster takes half their current HP in damage.");

        addSkillTooltip(SBSkills.SIPHON, "Channel on to link to a target. While linked, the caster gains a portion of the target's health.");
        addSkillTooltip(SBSkills.GRIM_REACH, "Doubles the link range");
        addSkillTooltip(SBSkills.GLUTTONY, "If the caster is at full health, the spell restores hunger instead.");
        addSkillTooltip(SBSkills.WITHERING, "The target suffers Slowness II while being siphoned.");
        addSkillTooltip(SBSkills.SOUL_TAP, "You now drain mana instead of health (mana is not absorbed).");
        addSkillTooltip(SBSkills.PARASITIC_LINK, "When the target casts a spell, the caster receives a portion of the mana cost.");
        addSkillTooltip(SBSkills.UNRELENTING, "The link can persist through walls/blocks for a short duration before breaking.");
        addSkillTooltip(SBSkills.OVERHEAL, "If health and hunger are full, Siphon temporarily increases the caster's max health.");
        addSkillTooltip(SBSkills.IRON_MAIDEN, "Attacks from the target deal less damage to the caster.");
        addSkillTooltip(SBSkills.ARCANE_FEEDBACK, "If the target runs out of mana, they are Silenced and take damage instead.");
        addSkillTooltip(SBSkills.HARVEST, "The caster links to all nearby enemies, draining from them simultaneously.");

        addSkillTooltip(SBSkills.HEALING_BLOSSOM, "Plants a divine blossom. After blooming, the blossom heals the caster when nearby.");
        addSkillTooltip(SBSkills.THORNY_VINES, "Enemies within the range of the blossom take magic damage.");
        addSkillTooltip(SBSkills.BLOOM, "The blossom now activates immediately after casting.");
        addSkillTooltip(SBSkills.ETERNAL_SPRING, "The blossom duration is increased.");
        addSkillTooltip(SBSkills.FLOWER_FIELD, "Allies receive half the healing from the blossom.");
        addSkillTooltip(SBSkills.FLOURISHING_GROWTH, "If the caster's health reaches full, the excess health is converted into mana.");
        addSkillTooltip(SBSkills.HEALING_WINDS, "The blossom now follows the caster.");
        addSkillTooltip(SBSkills.BURST_OF_LIFE, "Instantly heals the caster upon activation.");
        addSkillTooltip(SBSkills.PETAL_SHIELD, "The caster gains extra armor when near the blossom.");
        addSkillTooltip(SBSkills.VERDANT_RENEWAL, "Cleanses all negative effects from the caster");
        addSkillTooltip(SBSkills.REBIRTH, "If the caster takes fatal damage near the blossom, half of their health is automatically restored.");

        addSkillTooltip(SBSkills.FLICKER, "Instantly teleports the caster half of their cast range. Can be used directionally.");
        addSkillTooltip(SBSkills.DISTANT_FLICKER, "Increases the range to the caster's full cast range.");
        addSkillTooltip(SBSkills.SWIFT_SHADOWS, "Can be cast while walking or sprinting.");
        addSkillTooltip(SBSkills.STEP_INTO_SHADOW, "The caster gains invisibility after teleporting.");
        addSkillTooltip(SBSkills.SILENT_STEP, "The caster gives off no sound after teleporting.");
        addSkillTooltip(SBSkills.CONFUSION, "Creates a Doppelgänger at the cast location.");
        addSkillTooltip(SBSkills.BLINDING_MIRAGE, "Enemies that damage the decoy are blinded.");
        addSkillTooltip(SBSkills.PHANTOM_LURE, "Nearby enemies are taunted towards the decoy.");
        addSkillTooltip(SBSkills.SHADOW_FEINT, "The decoy can move a short distance in a random direction.");
        addSkillTooltip(SBSkills.LOOK_OVER_HERE, "The caster no longer teleports, but immediately turns invisible, sending a decoy in a chosen direction.");
        addSkillTooltip(SBSkills.HALL_OF_MIRRORS, "Recast to swap places with the decoy, ending the invisibility.");

        addSkillTooltip(SBSkills.CURSED_RUNE, "Place a rune at the target location. Enemies who step on it take a small amount of magic damage.");
        addSkillTooltip(SBSkills.MAGE_WRECK, "Drains mana from enemies who step on the rune.");
        addSkillTooltip(SBSkills.DISARMING_CURSE, "Disarms the target.");
        addSkillTooltip(SBSkills.MIRROR_CURSE, "Summons a Doppelgänger of the target");
        addSkillTooltip(SBSkills.CURSE_OF_PAIN, "The target takes extra physical damage.");
        addSkillTooltip(SBSkills.CURSE_OF_SILENCE, "The target is silenced.");
        addSkillTooltip(SBSkills.VANISHING_CURSE, "Teleports the target away from the location.");
        addSkillTooltip(SBSkills.CURSE_OF_WEAKNESS, "Reduces the target's attack damage.");
        addSkillTooltip(SBSkills.CURSE_OF_SUSCEPTIBILITY, "The target takes extra magic damage.");
        addSkillTooltip(SBSkills.TANGLEFOOT_CURSE, "The target is rooted.");
        addSkillTooltip(SBSkills.HIDDEN_RUNE, "The Rune is invisible to everyone but the caster.");

        addSkillTooltip(SBSkills.SHADOWBOND, "Caster and target gain invisibility. When the invisibility is broken, the caster and target swap places.");
        addSkillTooltip(SBSkills.EVERLASTING_BOND, "Increases the duration of invisibility.");
        addSkillTooltip(SBSkills.SHADOW_STEP, "After the swap, the caster's movement speed is increased.");
        addSkillTooltip(SBSkills.SNEAK_ATTACK, "After the swap, the caster's first attack within a short duration deals more damage.");
        addSkillTooltip(SBSkills.SILENT_EXCHANGE, "After the swap, the target is Silenced.");
        addSkillTooltip(SBSkills.SNARE, "After the swap, the target is rooted.");
        addSkillTooltip(SBSkills.DISORIENTED, "After the swap, the target gets dizzy and temporarily deals less damage.");
        addSkillTooltip(SBSkills.OBSERVANT, "The target is outlined to the caster while invisible.");
        addSkillTooltip(SBSkills.REVERSAL, "After the swap, the caster can recast to swap back with the target.");
        addSkillTooltip(SBSkills.LIVING_SHADOW, "After the swap, the caster remains invisible and leaves behind a decoy.");
        addSkillTooltip(SBSkills.SHADOW_CHAIN, "The spell can now affect an additional target. Swapping order is in the order of targets affected.");

        addSkillTooltip(SBSkills.PURGE_MAGIC, "Stops all of the targets active spells.");
        addSkillTooltip(SBSkills.RADIO_WAVES, "Purge Magic is now cast in an AoE.");
        addSkillTooltip(SBSkills.COUNTER_MAGIC, "Gain a magic shield that negates any spell cast on the caster.");
        addSkillTooltip(SBSkills.CLEANSE, "Removes all harmful effects.");
        addSkillTooltip(SBSkills.MANA_SPONGE, "Absorbs a portion of the mana cost if attacked by a spell.");
        addSkillTooltip(SBSkills.DOMINANT_MAGIC, "Silences the target.");
        addSkillTooltip(SBSkills.RESIDUAL_DISRUPTION, "Targets hit with Purge Magic have reduced cast chance.");
        addSkillTooltip(SBSkills.UNFOCUSED, "Reduces the target's spell power.");
        addSkillTooltip(SBSkills.MAGIC_POISONING, "Mana is reduced for each active spell purged.");
        addSkillTooltip(SBSkills.NULLIFICATION, "Removes a random enchantment from the target's equipment");
        addSkillTooltip(SBSkills.EXPUNGE, "Removes a spell from the target's knowledge.");
    }

    protected void addSkillTooltip(Holder<Skill> skill, String description) {
        add(skill.value().getDescriptionId(), description);
    }

    protected String checkReplace(DeferredHolder<?, ?> holder) {
        return Arrays.stream(holder.getId().getPath().split("_"))
                .map(this::checkReplace)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "))
                .trim();
    }

    protected String checkReplace(String string) {
        return REPLACE_LIST.containsKey(string) ? REPLACE_LIST.get(string) : StringUtils.capitalize(string);
    }

}
