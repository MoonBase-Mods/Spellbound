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
            add("spellbound.pathTexture." + path.getSerializedName(), checkReplace(path.getSerializedName()));
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
        ritualContents();
        divineActionContents();

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
        add("summon.acquisition.description", "Use the correct keystone to access the bosses dimension.");
        add("summon.acquisition.boss_rewards", "Boss Rewards");

        //Basic
        add("guide.basic.discord", "Discord");
        add("guide.basic.bugs", "Bug Reports");
        add("guide.basic.blurb", "Welcome fellow Magi! I present to you the first in many books documenting my exploration into the arcane. This book will act as a guideline introducing you to the different paths of magic I have discovered and how they can be used.");

        //Ruin
        add("guide.ruin.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can bend it to my will to destroy my enemies by harnessing the powers of ruin.");
        add("guide.ruin.v1_p2.ruin_portal", "During my adventures I have come across these strange portals that seem like they have been damaged by some kind of destructive magic.\n\nPerhaps its possible to repair, if I can just figure out where these portals lead...");
        add("guide.ruin.v1_p2.keystone", "HAHA! Success! These portals are more than I first thought.\n\nIt seems they lead to unknown pocket dimensions. From my research I have deduced the destination depends on these keystones,\n\nI have jotted down a recipe but they seem to need a catalyst to be powered.");
        add("guide.ruin.v1_p3.keystones", "Dammit, I just cant figure out how to empower these keystones. Maybe given more time I'll be able to figure it out...\n\nFor now im shifting my focus to looking for other spell casters that can hopefully shine a light on how to manipulate the arcane in these lands.\n\nI will begin my search within the forests and perhaps if I'm feeling adventurous I might try the End...");
        add("guide.ruin.v1_p3.spell_broker", "Well, I found a reclusive fellow hold up in this tower. As strange as he was he seemed knowledgeable in destructive magic and I purchased a few books from him.\n\nI'll document what I can of my findings from these books until I can crack keystones.");

        addSpellContents(SpellPath.RUIN, SBSpells.SOLAR_RAY.get(),
                "Channel the power of the sun forward creating a powerful beam setting fire to targets in its path.",
                "I have purchased this spell tome from a Spell Broker as I have been unable to decipher its origin but I believe its siphoning energy from the sun.",
                "I was mistaken... This spell isn't channelling energy from the sun, but a Sun God! This is a realm im not coming back to...");

        //Transfig
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

        //Summon
        add("guide.summons.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can increase my strength through numbers with the rites of summoning.");

        //Divine
        add("guide.divine.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can call upon divine forces to aid myself and allies and harm foes.");
        add("guide.divine.divine_actions", "Divine Actions:\n");

        //Deception
        add("guide.deception.cover_page", "This book shall document my discoveries throughout my adventures into the arcane and how I can utilize the shadows to my advantage with the art of deception.");
    }

    private void addSpellContents(SpellPath path, SpellType<?> spell, String description, String lore, String bossLore) {
        add("guide." + path.name() + "." + spell.location().getPath() + ".description", description);
        add("guide." + path.name() + "." + spell.location().getPath() + ".lore", lore);
        add("guide." + path.name() + "." + spell.location().getPath() + ".boss_lore", bossLore);
    }

    protected void ritualContents() {
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
    }

    protected void divineActionContents() {
        add("divine_action.judgement", "Judgement: ");
        add("divine_action.cooldown", "Cooldown: %s ticks");

        add("divine_action.healing_touch.heal_mob_to_full", "Heal any non-hostile mob to full.");
        add("divine_action.healing_touch.apply_blessed_bandages", "Heal your wounds with Blessed Bandages.");
        add("divine_action.healing_touch.purify_shrine", "Purify a shrine with a Divine Phial.");
        add("healing_touch.heal_mob_to_full.lore", "This is a bunch of lore for the first divine action. Watch me fill this translation with an absolute word soup you'd want to have at your next family Christmas dinner.\n\n The second divine action lore entry won't make any more sense than the first. And don't expect too much from the last one either, I'm just using all this text as a means of testing. Can you believe that? Testing? Me?\n\n Well, unfortunately, I'm running out of words to use for the last lore entry. So this is where I sign off o7.");
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
