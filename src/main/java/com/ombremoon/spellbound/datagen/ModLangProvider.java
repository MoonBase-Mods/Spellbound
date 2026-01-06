package com.ombremoon.spellbound.datagen;

import com.google.common.collect.ImmutableMap;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
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
            "sus", ""
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
//        EntityInit.ENTITIES.getEntries().forEach(this::entityLang);
//        StatusEffectInit.STATUS_EFFECTS.getEntries().forEach(this::effectLang);

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

        add("spellbound.toast.scrap_unlocked", "New book entry unlocked");

        add("chat.spelltome.awardxp", "Spell already known. +10 spells XP.");
        add("chat.spelltome.nospell", "This spells tome is blank.");
        add("chat.spelltome.spellunlocked", "Spell unlocked: %1$s");
        add("tooltip.spellbound.holdshift", "Hold shift for more information.");

        add("spellbound.path.level", "Lvl");

        add("command.spellbound.spellunknown", "You don't know the spells %1$s.");
        add("command.spellbound.spellforgot", "%1$s has been forgotten successfully.");
        add("command.spellbound.alreadyknown", "%1$s is already known.");
        add("command.spellbound.singleskilllearnt", "%1$s has been unlocked.");
        add("command.spellbound.learntskills", "All skills unlocked for %1$s");
        add("command.spellbound.spelllearnt", "%1%s has been learnt.");
        
        add("itemGroup.spellbound", "Spellbound🪄");
    }

    protected void guideContents() {
        //Elements
        add("guide.element.spell_info", "Spell Info");
        add("guide.element.spell_info.spell_mastery", "Spell Mastery: %1$s");
        add("guide.element.spell_info.damage", "Damage: %1$s");
        add("guide.element.spell_info.mana_cost", "Mana Cost: %1$s");
        add("guide.element.spell_info.cast_time", "Cast Time: %1$s");
        add("guide.element.spell_info.duration", "Duration: %1$s");
        add("guide.element.spell_info.mana_per_tick", "Mana/Tick: %1$s");
        add("guide.element.spell_border.element", "Element: ");
        add("guide.element.spell_border.mastery", "Mastery: %1$s");

        //Summon Acq
        //Transfig
        add("guide.basic.acquisition", "Spell Acquisition");

        basicContents();
        ruinContents();
        transfigContents();
        summonContents();
        divineGuideContents();

        //Deception
        add("guide.deception.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can utilize the shadows to my advantage with the art of deception.");
    }

    private void addSpellContents(SpellPath path, SpellType<?> spell, String description, String lore, String bossLore) {
        add("guide." + path.name() + "." + spell.location().getPath() + ".description", description);
        add("guide." + path.name() + "." + spell.location().getPath() + ".lore", lore);
        add("guide." + path.name() + "." + spell.location().getPath() + ".boss_lore", bossLore);
    }

    private void basicContents() {
        add("guide.basic.ruin.cover_page", "The Ruin Path focuses on destructive spells that can destroy both living creatures and the environment utilising different elemental powers.");
        add("guide.basic.discord", "Discord");
        add("guide.basic.bugs", "Bug Reports");
        add("guide.basic.blurb", "Welcome fellow Magi! I present to you the first in many books documenting my exploration into the arcane. This book will act as a guideline introducing you to the different paths of magic I have discovered and how they can be used.");
        add("guide.basic.ruin.description", "Ruin spells are split up into Sub-Paths each with a different effect allowing you to use §lFire§r, §lFrost§r, §lShock§r or a mix of all three.");
        add("guide.basic.ruin.description1", "Each Sub-path has special effects they cause on your target allowing you to either deal more damage or crowd control targets.");
        add("guide.basic.ruin.acquisition1", "Ruin spells are acquired by defeating powerful enemies that use different Ruin Path spells to deal damage.");
        add("guide.basic.ruin.acquisition2", "These enemies reside in their own arenas that require keystones to access. The recipes for these have been long forgotten however, leaving ruin spells to be known only to Spell Brokers");
    }

    protected void ruinContents() {
        add("guide.ruin.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can bend it to my will to destroy my enemies by harnessing the powers of ruin.");

        add("guide.ruin.subpaths", "Sub-Paths");
        add("guide.ruin.description1", "The destructive Path of Ruin, channelling the forces gripping the world together and twisting them to tear the world apart.");
        add("guide.ruin.description2", "To use this path to its fullest is to channel all your mana into pure elemental power ripping through anyone who stands in your path or doubts your true power, disregarding what ever damage you may do to the world around you.");
        add("guide.ruin.subpaths1", "Ruin manipulates the power holding the world together but there isnt just one force doing this.");

        add("guide.ruin.subpaths2", "I believe there are 3 primordial elements this power consists of: §lFire§r, §lFrost§r and §lShock§r. They all seem to have a different impact on both the world and my magic. If I focus my studies on one Sub-Path its possible I could enhance my control over one of these forces exponentially.");
        add("guide.ruin.subpaths_cnt", "Sub-Paths Cont.");
        add("guide.ruin.fire", "§lFire§r\nFire is the first of the elements I have explored and it seems to have the highest damage output of the 3 being able to set targets alight.");
        add("guide.ruin.frost", "§lFrost§r\nThe fire elements opposite, Frost, this one seems to affect living creatures ability to perform actions massively slowing any thing affected by it.");
        add("guide.ruin.shock", "§lShock§r\nThe final of the three elements seems to be linked directly to the weather as it allows me to create storm clouds to destroy my foes. The shock seems to disrupt some enemies thoughts stunning them.");

        add("guide.ruin.portals", "Ruin Portals");
        add("guide.ruin.keystones", "Keystones");
        add("guide.ruin.portals1", "When I draw from this source of magic I can feel others tug at it. I believe there are some ancient beings residing in their own realms that have learnt to bend these elements to their will.");
        add("guide.ruin.portals2", "I aim to find the gateways to these ancient beings realms so I can find how they are twisting the elements to create new ways of expressing their magic.");
        add("guide.ruin.portals3", "While exploring I have found a few gateways but they seem to all be locked. Until a way to create keystones to open the portals is found I'l have to learn what I can from Spell Brokers.");

        addSpellContents(SpellPath.RUIN, SBSpells.SOLAR_RAY.get(),
                "Channel the power of the sun forward creating a powerful beam setting fire to targets in its path.",
                "I have purchased this spell tome from a Spell Broker as I have been unable to decipher its origin but I believe its siphoning energy from the sun.",
                "I was mistaken... This spell isn't channelling energy from the sun, but a Sun God! That's definitely a being to avoid");

    }

    protected void summonContents() {
        add("guide.summons.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can increase my strength through numbers with the rites of summoning.");

        add("guide.summon.dimensions", "Dimensions");
        add("guide.summon.description1", "Summoning magic... dragging creatures from the darkest of realms and forcing them to fight on your behalf. Only the strong willed can use this power.");
        add("guide.summon.description2", "To harness this source of magic a creature must first be battled to understand its power. But that requires first reaching into these twisted realms where they reside and hunting these powerful entities.");
        add("guide.summon.dimensions1", "The most powerful creatures to exist, lord over their own realms. If I am able to find a way to access these dimensions then I can learn how to control these beasts and their powers.");
        add("guide.summon.dimensions2", "I know I can't be the first one to try this I plan to search for any potential portals left by others.");

        add("guide.summon.summoning_stone", "Summoning Stones");
        add("guide.summon.summoning_portal", "Portal Construction");
        add("guide.summon.summoning_stone1", "After speaking to a few Spell Brokers they have told me of these summoning stones that can be crafted to create a gateway.");
        add("guide.summon.summoning_portal1", "Ah-ha! Mimicking an End portal seems to allow these stones to be activated with Magic Essence. Now I just need to find how to access a specific dimension.");

        add("guide.summon.portal_activation", "Portal Activation");
        add("guide.summon.portal_activation1", "After focusing more of my studies on End portals I have realised that these types of portals need a block to focus on, to locate their destination.");
        add("guide.summon.portal_activation2", "I believe that if I can create just a single summoning stone adjusted to a specific dimension I could open a gateway by placing it in the middle of my portal and activating it.");
        add("guide.summon.valid_portals", "Well after multiple stages of trial and error I have found that these focused summoning stones must be very specific.");
        add("guide.summon.valid_portals1", "When I am able to find a way to create a new focused stone I will be sure to note it down in this book, along with any information regarding the spells I can draw out from the dimension.");

        add("summon.acquisition.description", "Use the keystone below to access the boss's dimension.");
        add("summon.acquisition.boss_rewards", "Boss Rewards");
        add("summon.acquisition.wild_mushroom.lore", "Come to think of it, it's rare to see a single mushroom on its own...");
        addSpellContents(SpellPath.SUMMONS, SBSpells.WILD_MUSHROOM.get(),
                "Grows a mushroom out of the ground at the target location, periodically emitting a damaging poison cloud.",
                "Fungi are some of the most resilient living organisms, if I can find a mushroom infested realm think of the power it could be hiding.",
                "Why did I think going to the home of a notably durable fungi would be a good idea. It always knows where I am it must be these damn spores."
        );
    }

    protected void transfigContents() {
        add("spellbound.ritual.tier_one", "Tier: 1");
        add("spellbound.ritual.tier_two", "Tier: 2");
        add("spellbound.ritual.tier_three", "Tier: 3");
        add("spellbound.ritual.activation_time", "Activation Time: %s seconds");
        add("spellbound.ritual.duration", "Duration: %s seconds");
        add("spellbound.ritual.duration_not_applicable", "Duration: N/A");

        add("ritual.spellbound.create_stride", "Create Spell Tome:\nStride");
        add("ritual.spellbound.create_stride.description", "Creates a Stride spell tome");
        add("ritual.spellbound.create_shadow_gate", "Create Spell Tome:\nShadow Gate");
        add("ritual.spellbound.create_shadow_gate.description", "Creates a Shadow Gate spell tome");
        add("ritual.spellbound.create_mystic_armor", "Create Spell Tome:\nMystic Armor");
        add("ritual.spellbound.create_mystic_armor.description", "Creates a Mystic Armor spell tome");
        add("spellbound.ritual.materials", "Ritual Materials");
        add("guide.transfiguration.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can manipulate the world around me through the study of transfiguration.");
        addSpellContents(SpellPath.TRANSFIGURATION, SBSpells.STRIDE.get(),
                "Revamps the way your body conserves energy granting an increase in the casters movement speed.",
                "I have been trying to gather ingredients for all of my transfigurations but its taking me too long to find everything. I need a faster way.",
                "Well I solved my problem with transfiguration. You wont believe it but by mixing the right ingredients I can supercharge my body allowing my legs to work even faster!");
        addSpellContents(SpellPath.TRANSFIGURATION, SBSpells.SHADOW_GATE.get(),
                "Rips open a portal through the darkness linking two points for transportation",
                "As my studies have advanced I am finding myself needing to explore more biomes and found that teleportation could be quite useful.",
                "I think I got it! After experimenting with ender pearls I found they rip open gateways temporarily. I have created a spell to keep these open for longer.");
        addSpellContents(SpellPath.TRANSFIGURATION, SBSpells.MYSTIC_ARMOR.get(),
                "Grants the caster a magically charged shield, reducing incoming spell damage based on level.",
                "Uh oh seems like I have angered a few too many Magi. Im trying to throw together what ever I can hoping i can protect my self a little more consistantly.",
                "Well i was trying to make myself absorb their mana and while that didn't completely work I have managed to reduce the impact of their spells.");

    }

    protected void divineGuideContents() {
        add("divine_action.judgement", "Judgement: ");
        add("divine_action.judgement_required", "Judgement Required: ");
        add("divine_action.cooldown", "Cooldown: %s ticks");

        add("guide.divine.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can call upon divine forces to aid myself and allies and harm foes.");
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
        add("guide.divine.divine_action1", "Guarded by the Valkyr, the Shrine is bound to the temple. Should I attempt to mine or displace the altar, the Valkyr’s wrath is immediate. I may use the Shrine where it stands, but I am forbidden from taking it.");
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
    }

    protected void skillDescriptions() {
        addSkillTooltip(SBSkills.SOLAR_RAY, "Fire a thin beam of light that deals 5 fire damage per second.");
        addSkillTooltip(SBSkills.SUNSHINE, "Doubles the range of Solar Ray.");
        addSkillTooltip(SBSkills.HEALING_LIGHT, "Allies hit by the ray are healed for 2 health per second.");
        addSkillTooltip(SBSkills.OVERPOWER, "Gain the ability to slowly move while casting Solar Ray.");
        addSkillTooltip(SBSkills.CONCENTRATED_HEAT, "After 5 seconds of hitting the same target, the damage doubles.");
        addSkillTooltip(SBSkills.OVERHEAT, "After using Solar Ray for 5 seconds, the caster emits intense heat, dealing 3 fire damage per seconds to nearby enemies.");
        addSkillTooltip(SBSkills.SOLAR_BURST, "Every 3 seconds, both ends of the beam release a small solar burst that deals an additional 3 fire damage around both areas.");
        addSkillTooltip(SBSkills.SOLAR_BORE, "The end of the Solar Ray opposite of the caster explodes once per second, setting the ground ablaze.");
        addSkillTooltip(SBSkills.BLINDING_LIGHT, "Enemies hit by the beam are blinded for 3 seconds.");
        addSkillTooltip(SBSkills.AFTERGLOW, "Enemies hit are marked with a glow for 5 seconds. While marked, they take 20% extra fire damage.");
        addSkillTooltip(SBSkills.POWER_OF_THE_SUN, "Solar Ray deals 50% more damage during the day.");

        addSkillTooltip(SBSkills.VOLCANO, "Create a volcanic eruption that spits out 8 lava bombs per second for 10 seconds.");
        addSkillTooltip(SBSkills.INFERNO_CORE, "After the eruption ends, the volcano drops a Smoldering Shard.");
        addSkillTooltip(SBSkills.EXPLOSIVE_BARRAGE, "Each lava bomb explodes on impact.");
        addSkillTooltip(SBSkills.LAVA_FLOW, "Lava bombs turn into lava pools on impact.");

        addSkillTooltip(SBSkills.STORMSTRIKE, "Send out a bolt of lightning that charges a target, dealing 2 shock damage per second for 3 seconds.");
        addSkillTooltip(SBSkills.STATIC_SHOCK, "Hitting a block now creates a small explosion that applies Stormstrike to anyone it hits.");
        addSkillTooltip(SBSkills.ELECTRIFY, "Decrease the target's shock resistance by 30%.");
        addSkillTooltip(SBSkills.SHOCK_FACTOR, "Deals extra damage equal to 1% of your current mana each damage tick.");
        addSkillTooltip(SBSkills.PURGE, "Deals extra damage to summoned targets, equal to 10% of the caster's current mana.");
        addSkillTooltip(SBSkills.REFRACTION, "When the target takes damage from your shock-based Ruin spells while affected with Stormstrike, restores 15 mana back to the caster.");
        addSkillTooltip(SBSkills.PULSATION, "Chance to paralyze the target for 1 second each damage tick.");
        addSkillTooltip(SBSkills.STORM_SHARD, "If the target dies while affected by Stormstrike, the caster is awarded a Storm Shard. 30 sec. cooldown.");
        addSkillTooltip(SBSkills.CHARGED_ATMOSPHERE, "Decreases shock-based Ruin spells' mana costs by 25% for 8 seconds.");
        addSkillTooltip(SBSkills.DISARM, "Chance to disarm the target each damage tick.");
        addSkillTooltip(SBSkills.SUPERCHARGE, "If the target dies while affected by Stormstrike, increases the damage of shock-based Ruin spells by 50% for 10 seconds.");

        addSkillTooltip(SBSkills.ELECTRIC_CHARGE, "Sneakily apply an electric charge to the target. Recast to discharge.");
        addSkillTooltip(SBSkills.ELECTRIFICATION, "Applies Stormstrike on discharge.");
        addSkillTooltip(SBSkills.SUPERCONDUCTOR, "Decreases target's shock resistance by 33% for 10 seconds on discharge.");
        addSkillTooltip(SBSkills.PIEZOELECTRIC, "If killed by Electric Charge, the enemy drops a storm shard. 30 sec. cooldown.");
        addSkillTooltip(SBSkills.OSCILLATION, "Increases the discharge damage by 5% for each storm shard in the caster's inventory. All shards are destroyed on discharge.");
        addSkillTooltip(SBSkills.HIGH_VOLTAGE, "Recast with a storm shard to stun the target for 2 seconds. Enemies that come in range of the target are also stunned. 30 sec. cooldown.");
        addSkillTooltip(SBSkills.UNLEASHED_STORM, "If killed by Electric Charge, the target will explode dealing half the base shock damage.");
        addSkillTooltip(SBSkills.STORM_SURGE, "If killed by Electric Charge, 10 to 20 mana is restored to the caster.");
        addSkillTooltip(SBSkills.CHAIN_REACTION, "The discharge applies Electric Charge to all nearby enemies, including the caster. The secondary charge is discharged immediately.");
        addSkillTooltip(SBSkills.AMPLIFY, "Electric Charge can be held for 3 seconds to increase the damage up to 100%.");
        addSkillTooltip(SBSkills.ALTERNATING_CURRENT, "The discharge has a small chance to instantly kill the target. Does not work on target with more than twice the caster's current health. On failure, the caster takes damage equal to 5% of their max health.");

        addSkillTooltip(SBSkills.STORM_RIFT, "Creates a storm portal for 20 seconds. If two portals are active, those approaching either get warped across and take 5 shock damage to health and mana.");
        addSkillTooltip(SBSkills.STORM_FURY, "The vortex doubles in size and damage.");
        addSkillTooltip(SBSkills.DISPLACEMENT_FIELD, "A single portal can now teleport enemies to a random location within 10 blocks.");
        addSkillTooltip(SBSkills.MAGNETIC_FIELD, "The vortex has twice the pull strength. Enemies caught in the field have their armor reduced by 25%.");
        addSkillTooltip(SBSkills.EVENT_HORIZON, "When a target is warped, they pull nearby enemies towards the warp field.");
        addSkillTooltip(SBSkills.CHARGED_RIFT, "Each warp between portals charges the storm, increasing shock damage 1 up to a max of 5.");
        addSkillTooltip(SBSkills.MOTION_SICKNESS, "Warped enemies have their movement, attack, and mining speed reduced by 40% for 10 seconds.");
        addSkillTooltip(SBSkills.FORCED_WARP, "Upon being warped, targets are launched out of the portal with a high velocity, potentially dealing damage on impact.");
        addSkillTooltip(SBSkills.STORM_CALLER, "Generate a cloud above both portals that discharge lightning periodically dealing for 5 shock damage.");
        addSkillTooltip(SBSkills.IMPLOSION, "Recast while targeting a portal with a storm shard to detonate the portal, applying Stormstrike to anyone in the area.");
        addSkillTooltip(SBSkills.ORBITAL_SHELL, "Recast while targeting a portal with a shard to mark a portal. Marked portals will move in a 3-block radius circle centered around the origin.");

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

        addSkillTooltip(SBSkills.STRIDE, "Movement speed is increased by 25% for 30 seconds.");
        addSkillTooltip(SBSkills.QUICK_SPRINT, "For the first 10 seconds, movement speed is increased by an additional 15%.");
        addSkillTooltip(SBSkills.GALLOPING_STRIDE, "Speed is increased by another 25%.");
        addSkillTooltip(SBSkills.RIDERS_RESILIENCE, "All movement benefits are applied to mounts.");
        addSkillTooltip(SBSkills.FLEETFOOTED, "Nearby allies gain 15% movement speed while near the caster.");
        addSkillTooltip(SBSkills.SUREFOOTED, "Step height is increased.");
        addSkillTooltip(SBSkills.AQUA_TREAD, "Gain the ability to walk on water.");
        addSkillTooltip(SBSkills.ENDURANCE, "Duration is increased by 30 seconds.");
        addSkillTooltip(SBSkills.MOMENTUM, "For each second travelled, your attack speed is increased by 4%, up to a max of 20%, for 5 seconds.");
        addSkillTooltip(SBSkills.STAMPEDE, "You can charge through enemies, knocking them back and dealing 3 damage.");
        addSkillTooltip(SBSkills.MARATHON, "Food consumption is halted.");

        addSkillTooltip(SBSkills.SHADOW_GATE, "Deploy 2 shadow portals (must be in a low light level), allowing passage in both directions with 50 blocks.");
        addSkillTooltip(SBSkills.REACH, "Double the range of the portals");
        addSkillTooltip(SBSkills.BLINK, "Passing through the portals increases the caster's movement speed for 25 seconds.");
        addSkillTooltip(SBSkills.SHADOW_ESCAPE, "When the caster enters the portal below 50% health, they gain invisibility for 10 seconds after exiting.");
        addSkillTooltip(SBSkills.OPEN_INVITATION, "Anyone can pass through the portals.");
        addSkillTooltip(SBSkills.QUICK_RECHARGE, "The caster receives 20 mana any time someone passes through a portal.");
        addSkillTooltip(SBSkills.UNWANTED_GUESTS, "Enemies that pass through a portal have their attack and spell damage reduced by 10%.");
        addSkillTooltip(SBSkills.BAIT_AND_SWITCH, "Enemies passing through a portal take 5 damage to health and mana.");
        addSkillTooltip(SBSkills.DARKNESS_PREVAILS, "Portals can be spawned in any light level.");
        addSkillTooltip(SBSkills.GRAVITY_SHIFT, "Exiting the portal launches entities in the air, applying slow falling to the caster and allies, if applicable.");
        addSkillTooltip(SBSkills.DUAL_DESTINATION, "Can now deploy an additional portal. Order of travel goes by order placed.");

        addSkillTooltip(SBSkills.MYSTIC_ARMOR, "Reduces incoming spell damage by 15% for 60 seconds (+3% per level on the Transfiguration Path, up to 30% max).");
        addSkillTooltip(SBSkills.FORESIGHT, "Decreases mana cost by 15%.");
        addSkillTooltip(SBSkills.ARCANE_VENGEANCE, "Increases attack damage by 15% for 10 seconds after you block an attack.");
        addSkillTooltip(SBSkills.EQUILIBRIUM, "When you get hit, deals damage equal to 10% of your total health back to the attacker.");
        addSkillTooltip(SBSkills.PLANAR_DEFLECTION, "Deflects 30% of melee damage taken back to the attacker.");
        addSkillTooltip(SBSkills.PURSUIT, "Movement speed is increased by 15%.");
        addSkillTooltip(SBSkills.COMBAT_PERCEPTION, "Chance to dodge a melee attack.");
        addSkillTooltip(SBSkills.CRYSTALLINE_ARMOR, "Increase armor points by 25%.");
        addSkillTooltip(SBSkills.ELDRITCH_INTERVENTION, "Restores caster's health to 50% if it drops below 20%. 2 min. cooldown.");
        addSkillTooltip(SBSkills.SUBLIME_BEACON, "Restores health equal to 25% of your armor points every 3 seconds.");
        addSkillTooltip(SBSkills.SOUL_RECHARGE, "Restores you to full health if your health drops below 10%, consuming a filled soul shard in the caster's inventory. 3 min. cooldown.");

        addSkillTooltip(SBSkills.WILD_MUSHROOM, "Plants a wild mushroom at the target location, expelling poisonous spores every 3 seconds, dealing 4 damage to all nearby enemies.");
        addSkillTooltip(SBSkills.VILE_INFLUENCE, "Increases the spore radius.");
        addSkillTooltip(SBSkills.HASTENED_GROWTH, "Decreases the explosion interval by 1 second.");
        addSkillTooltip(SBSkills.ENVENOM, "Spores now poison targets for 4 seconds.");
        addSkillTooltip(SBSkills.PARASITIC_FUNGUS, "Spores deal extra damage, scaling with the caster's current mana, to poisoned or diseased enemies.");
        addSkillTooltip(SBSkills.NATURES_DOMINANCE, "Each active mushroom increases the spell's damage by 10%.");
        addSkillTooltip(SBSkills.FUNGAL_HARVEST, "When 3 mushrooms are active, gain increased mana regeneration.");
        addSkillTooltip(SBSkills.POISON_ESSENCE, "If a target dies to a mushroom, the spell deals 25% more damage for 10 seconds.");
        addSkillTooltip(SBSkills.SYNTHESIS, "If a target dies to a mushroom, the casting cost of the spell is decreased by 100% for 5 seconds.");
        addSkillTooltip(SBSkills.LIVING_FUNGUS, "When the spell ends, restores 7 - 15 mana back to the caster.");
        addSkillTooltip(SBSkills.PROLIFERATION, "Getting hit by the same mushroom twice petrifies the target for 4 seconds.");

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

        addSkillTooltip(SBSkills.HEALING_TOUCH, "Heals the caster 2 health per second for 5 seconds.");
        addSkillTooltip(SBSkills.BLASPHEMY, "When the caster is hit, applies Disease to the attacker for 3 seconds. 5 sec. cooldown");
        addSkillTooltip(SBSkills.CONVALESCENCE, "Restores 1 health when the caster attack a target affected by poison or disease.");
        addSkillTooltip(SBSkills.DIVINE_BALANCE, "Increases the duration of the spell by 100% and the mana cost by 50%.");
        addSkillTooltip(SBSkills.NATURES_TOUCH, "Instantly restores 4 health to the caster.");
        addSkillTooltip(SBSkills.CLEANSING_TOUCH, "Removes a random negative effect from the caster.");
        addSkillTooltip(SBSkills.ACCELERATED_GROWTH, "Instantly restores 2 hunger to the caster.");
        addSkillTooltip(SBSkills.HEALING_STREAM, "Each tick restores extra health equal to 2% of the caster's missing mana.");
        addSkillTooltip(SBSkills.TRANQUILITY_OF_WATER, "Increases mana regeneration by 25%");
        addSkillTooltip(SBSkills.OVERGROWTH, "While at full health, each tick applies a stack of Overgrowth (up to a max of 5 stacks.) When hit, Overgrowth restores 4 health, consuming 1 stack.");
        addSkillTooltip(SBSkills.OAK_BLESSING, "Increases armor by 15% for 10 seconds if the caster's health drops below 30% while Healing Touch is active. 30 sec. cooldown.");

        addSkillTooltip(SBSkills.HEALING_BLOSSOM, "Plants a divine blossom. The blossom blooms 10 seconds after casting and last 10 seconds. The blossom heals the caster 2 health per seconds when within 5 blocks.");
        addSkillTooltip(SBSkills.THORNY_VINES, "Enemies within the range of the blossom take 4 damage per second.");
        addSkillTooltip(SBSkills.BLOOM, "The blossom now activates immediately after casting.");
        addSkillTooltip(SBSkills.ETERNAL_SPRING, "The healing duration is increased to 15 seconds.");
        addSkillTooltip(SBSkills.FLOWER_FIELD, "Allies receive half the healing from the blossom.");
        addSkillTooltip(SBSkills.FLOURISHING_GROWTH, "If the caster's health reaches full, the excess health is converted into 5 points of mana per second.");
        addSkillTooltip(SBSkills.HEALING_WINDS, "The blossom now follows the caster.");
        addSkillTooltip(SBSkills.BURST_OF_LIFE, "Instantly heals the caster 4 health upon activation.");
        addSkillTooltip(SBSkills.PETAL_SHIELD, "The caster gains 20% damage resistance.");
        addSkillTooltip(SBSkills.VERDANT_RENEWAL, "Cleanses all negative effects from the caster");
        addSkillTooltip(SBSkills.REBIRTH, "Mark a blossom with a holy shard. If the caster takes fatal damage near the blossom, half of their health is automatically restored.");

        addSkillTooltip(SBSkills.SHADOWBOND, "Caster and target gain invisibility for 10 seconds. When the invisibility is broken, the caster and target swap places.");
        addSkillTooltip(SBSkills.EVERLASTING_BOND, "Increases the duration of invisibility to 20 seconds.");
        addSkillTooltip(SBSkills.SHADOW_STEP, "After the swap, the caster's movement speed is increased by 30% for 5 seconds");
        addSkillTooltip(SBSkills.SNEAK_ATTACK, "After the swap, the caster's first attack within 5 seconds deals 50% more damage.");
        addSkillTooltip(SBSkills.SILENT_EXCHANGE, "After the swap, the target is Silenced for 5 seconds.");
        addSkillTooltip(SBSkills.SNARE, "After the swap, the target is rooted.");
        addSkillTooltip(SBSkills.DISORIENTED, "After the swap, the target gets dizzy and deals 20% less damage for 5 seconds.");
        addSkillTooltip(SBSkills.OBSERVANT, "The target is outlined to the caster while invisible.");
        addSkillTooltip(SBSkills.REVERSAL, "After the swap, the caster can recast with a fool's shard within 5 seconds to swap back with the target.");
        addSkillTooltip(SBSkills.LIVING_SHADOW, "After the swap, the caster remains invisible for another 5 seconds and leaves behind a decoy for 10 seconds.");
        addSkillTooltip(SBSkills.SHADOW_CHAIN, "The spell can now affect an additional target. Swapping order is in the order of targets affected.");

        addSkillTooltip(SBSkills.PURGE_MAGIC, "Stops all of the targets active spells.");
        addSkillTooltip(SBSkills.RADIO_WAVES, "Purge Magic is now cast in an AoE.");
        addSkillTooltip(SBSkills.COUNTER_MAGIC, "(Choice) Gain a magic shield that negates any spell cast on the caster for 10 seconds.");
        addSkillTooltip(SBSkills.CLEANSE, "Removes all harmful effects.");
        addSkillTooltip(SBSkills.AVERSION, "Counter Magic reflects 100% of the spell damage back to the attacker.");
        addSkillTooltip(SBSkills.DOMINANT_MAGIC, "Silence the target for 10 seconds.");
        addSkillTooltip(SBSkills.RESIDUAL_DISRUPTION, "Targets hit with Purge Magic have a 50% chance to fail spells cast within the next 5 seconds.");
        addSkillTooltip(SBSkills.UNFOCUSED, "Reduces the target's spell power by 10% for 20 seconds.");
        addSkillTooltip(SBSkills.MAGIC_POISONING, "Mana is reduced by 20 points for each active spell purged.");
        addSkillTooltip(SBSkills.NULLIFICATION, "Removes a random enchantment from the target's armor or weapon.");
        addSkillTooltip(SBSkills.EXPUNGE, "Cast with a fool's shard to remove a spell from the target's knowledge. Can only be used once a day.");

        addSkillTooltip(SBSkills.SHATTERING_CRYSTAL, "Creates a crystal of Ice. Cast again to detonate it, dealing damage to nearby enemies.");
        addSkillTooltip(SBSkills.FRIGID_BLAST, "Enemies hit by the blast are slower by 50% for 5 seconds.");
        addSkillTooltip(SBSkills.ICE_SHARD, "Recast on a crystal: destroy it to make it drop a frozen shard. 30 sec. cooldown.");
        addSkillTooltip(SBSkills.CHILL, "Crystal passively emits a freezing aura, dealing frost damage every second.");
        addSkillTooltip(SBSkills.FROZEN_SHRAPNEL, "The crystal now sends out ice shards that deal extra frost damage.");
        addSkillTooltip(SBSkills.HYPOTHERMIA, "Enemies hit by the explosion have their frost resistance reduced by 10% for 10 seconds.");
        addSkillTooltip(SBSkills.CRYSTAL_ECHO, "After detonation, the crystal reforms in its original location for 10 seconds (once per crystal).");
        addSkillTooltip(SBSkills.THIN_ICE, "Enemies that walk near the crystal will instantly trigger the detonation.");
        addSkillTooltip(SBSkills.CHAOTIC_SHATTER, "Detonating one crystal will detonate others in the area.");
        addSkillTooltip(SBSkills.LINGERING_FROST, "After detonation, the crystal leaves a damaging mist behind. Enemies caught in the mist for 3 seconds will get frozen.");
        addSkillTooltip(SBSkills.GLACIAL_IMPACT, "Recast with a frozen shard to mark a crystal. Marked crystal's explosion applies freeze and Permafrost to enemies hit.");

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
