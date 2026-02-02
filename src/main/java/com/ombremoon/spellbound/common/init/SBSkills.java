package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.api.buff.SpellModifier;
import com.ombremoon.spellbound.common.magic.skills.ModifierSkill;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.BiPredicate;

@SuppressWarnings("unchecked")
public class SBSkills {
    public static final ResourceKey<Registry<Skill>> SKILL_REGISTRY_KEY = ResourceKey.createRegistryKey(CommonClass.customLocation("skill"));
    public static final Registry<Skill> REGISTRY = new RegistryBuilder<>(SKILL_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<Skill> SKILLS = DeferredRegister.create(REGISTRY, Constants.MOD_ID);

    //Flame Jet
    public static final Holder<Skill> FLAME_JET = registerRadialSkill("flame_jet");
    public static final Holder<Skill> FLAME_GEYSER = registerRadialSkill("flame_geyser", -75, 50, preReqs(FLAME_JET));
    public static final Holder<Skill> EXPULSION_BLAST = registerSkill("expulsion_blast", 0, 50, preReqs(FLAME_JET));
    public static final Holder<Skill> JET_ENGINE = registerRadialSkill("jet_engine", -50, 100, preReqs(EXPULSION_BLAST));
    public static final Holder<Skill> FLAME_INFERNO = registerRadialSkill("flame_inferno", 0, 100, preReqs(EXPULSION_BLAST));
    public static final Holder<Skill> IGNITION_BURST = registerSkill("ignition_burst", 50, 100, preReqs(EXPULSION_BLAST));
    public static final Holder<Skill> TURBO_CHARGE = registerRadialSkill("turbo_charge", 100, 100, preReqs(EXPULSION_BLAST));
    public static final Holder<Skill> JET_STABILIZATION = registerSkill("jet_stabilization", 0, 150, preReqs(JET_ENGINE, FLAME_INFERNO, IGNITION_BURST));
    public static final Holder<Skill> TURBULENCE_STREAM = registerSkill("turbulence_stream", -50, 200, preReqs(JET_STABILIZATION));
    public static final Holder<Skill> AFTERSHOCK_COMPRESSION = registerSkill("aftershock_compression", 50, 200, preReqs(JET_STABILIZATION));
    public static final Holder<Skill> IRON_MAN = registerRadialSkill("iron_man", 0, 250, preReqs(TURBULENCE_STREAM, AFTERSHOCK_COMPRESSION));

    //Solar Ray
    public static final Holder<Skill> SOLAR_RAY = registerSkill("solar_ray");
    public static final Holder<Skill> SUNSHINE = registerSkill("sunshine", -50, 50, preReqs(SOLAR_RAY));
    public static final Holder<Skill> OVERPOWER = registerSkill("overpower", -50, 100, preReqs(SUNSHINE));
    public static final Holder<Skill> HEALING_LIGHT = registerSkill("healing_light", -50, 150, preReqs(OVERPOWER));
    public static final Holder<Skill> CONCENTRATED_HEAT = registerSkill("concentrated_heat", 0, 50, preReqs(SOLAR_RAY));
    public static final Holder<Skill> OVERHEAT = registerSkill("overheat", 0, 100, preReqs(CONCENTRATED_HEAT));
    public static final Holder<Skill> SOLAR_BURST = registerSkill("solar_burst", 0, 150, preReqs(OVERHEAT));
    public static final Holder<Skill> SOLAR_BORE = registerSkill("solar_bore", 50, 200, preReqs(SOLAR_BURST));
    public static final Holder<Skill> AFTERGLOW = registerSkill("afterglow", 0, 200, preReqs(SOLAR_BURST));
    public static final Holder<Skill> BLINDING_LIGHT = registerSkill("blinding_light", -50, 250, preReqs(AFTERGLOW));
    public static final Holder<Skill> POWER_OF_THE_SUN = registerSkill("power_of_the_sun", 0, 250, preReqs(AFTERGLOW));

    //Volcano
    public static final Holder<Skill> VOLCANO = registerSkill("volcano");
    public static final Holder<Skill> INFERNO_CORE = registerSkill("inferno_core", 0, 50, preReqs(VOLCANO));
    public static final Holder<Skill> LAVA_FLOW = registerSkill("lava_flow", 50, 50, preReqs(VOLCANO));
    public static final Holder<Skill> EXPLOSIVE_BARRAGE = registerSkill("explosive_barrage", -50, 50, preReqs(VOLCANO));
    public static final Holder<Skill> SHRAPNEL = registerSkill("shrapnel", -50, 100, preReqs(EXPLOSIVE_BARRAGE));
    public static final Holder<Skill> HEATWAVE = registerSkill("heatwave", -100, 100, preReqs(EXPLOSIVE_BARRAGE));
    public static final Holder<Skill> SCORCHED_EARTH = registerSkill("scorched_earth", 50, 100, preReqs(LAVA_FLOW));
    public static final Holder<Skill> SEISMIC_SHOCK = registerSkill("seismic_shock", -100, 150, preReqs(HEATWAVE));
    public static final Holder<Skill> MOLTEN_SHIELD = registerSkill("molten_shield", 100, 100, preReqs(LAVA_FLOW));
    public static final Holder<Skill> PYROCLASTIC_CLOUD = registerSkill("pyroclastic_cloud", 0, 100, preReqs(INFERNO_CORE));
    public static final Holder<Skill> APOCALYPSE = registerSkill("apocalypse", 0, 175, preReqs(SHRAPNEL, PYROCLASTIC_CLOUD, SCORCHED_EARTH));

    //Shattering Crystal
    public static final Holder<Skill> SHATTERING_CRYSTAL = registerSkill("shattering_crystal");
    public static final Holder<Skill> ICE_SHARD = registerSkill("ice_shard", 0, 50, preReqs(SHATTERING_CRYSTAL));
    public static final Holder<Skill> FRIGID_BLAST = registerSkill("frigid_blast", -50, 50, preReqs(SHATTERING_CRYSTAL));
    public static final Holder<Skill> FROZEN_SHRAPNEL = registerSkill("frozen_shrapnel", -75, 100, preReqs(FRIGID_BLAST));
    public static final Holder<Skill> HYPOTHERMIA = registerSkill("hypothermia", -25, 100, preReqs(FRIGID_BLAST));
    public static final Holder<Skill> CRYSTAL_ECHO = registerSkill("crystal_echo", -50, 150, preReqs(FROZEN_SHRAPNEL, HYPOTHERMIA));
    public static final Holder<Skill> CHILL = registerSkill("chill", 50, 50, preReqs(SHATTERING_CRYSTAL));
    public static final Holder<Skill> THIN_ICE = registerSkill("thin_ice", 25, 100, preReqs(CHILL));
    public static final Holder<Skill> CHAOTIC_SHATTER = registerSkill("chaotic_shatter", 75, 100, preReqs(CHILL));
    public static final Holder<Skill> LINGERING_FROST = registerSkill("lingering_frost", 50, 150, preReqs(THIN_ICE, CHAOTIC_SHATTER));
    public static final Holder<Skill> GLACIAL_IMPACT = registerSkill("glacial_impact", 0, 200, preReqs(CRYSTAL_ECHO, LINGERING_FROST));

    //Stormstrike
    public static final Holder<Skill> STORMSTRIKE = registerSkill("stormstrike");
    public static final Holder<Skill> STATIC_SHOCK = registerSkill("static_shock", -75, 50, preReqs(STORMSTRIKE));
    public static final Holder<Skill> ELECTRIFY = registerSkill("electrify", 0, 50, preReqs(STORMSTRIKE));
    public static final Holder<Skill> SHOCK_FACTOR = registerSkill("shock_factor", 75, 50, preReqs(STORMSTRIKE));
    public static final Holder<Skill> PURGE = registerSkill("purge", 75, 100, preReqs(SHOCK_FACTOR));
    public static final Holder<Skill> REFRACTION = registerSkill("refraction", -37, 100, preReqs(ELECTRIFY));
    public static final Holder<Skill> CHARGED_ATMOSPHERE = registerSkill("charged_atmosphere", 37, 100, preReqs(ELECTRIFY));
    public static final Holder<Skill> PULSATION = registerSkill("pulsation", -37, 150, preReqs(REFRACTION, CHARGED_ATMOSPHERE));
    public static final Holder<Skill> DISARM = registerSkill("disarm", 37, 150, preReqs(REFRACTION, CHARGED_ATMOSPHERE));
    public static final Holder<Skill> STORM_SHARD = registerSkill("storm_shard", -37, 200, preReqs(PULSATION, DISARM));
    public static final Holder<Skill> SUPERCHARGE = registerSkill("supercharge", 37, 200, preReqs(PULSATION, DISARM));

    //Electric Charge
    public static final Holder<Skill> ELECTRIC_CHARGE = registerSkill("electric_charge");
    public static final Holder<Skill> ELECTRIFICATION = registerSkill("electrification", -25, 50, preReqs(ELECTRIC_CHARGE));
    public static final Holder<Skill> SUPERCONDUCTOR = registerSkill("superconductor", 25, 50, preReqs(ELECTRIC_CHARGE));
    public static final Holder<Skill> PIEZOELECTRIC = registerSkill("piezoelectric", 50, 100, preReqs(ELECTRIFICATION, SUPERCONDUCTOR));
    public static final Holder<Skill> OSCILLATION = registerSkill("oscillation", 50, 150, preReqs(PIEZOELECTRIC));
    public static final Holder<Skill> HIGH_VOLTAGE = registerSkill("high_voltage", 100, 150, preReqs(PIEZOELECTRIC));
    public static final Holder<Skill> UNLEASHED_STORM = registerSkill("unleashed_storm", -50, 100, preReqs(ELECTRIFICATION, SUPERCONDUCTOR));
    public static final Holder<Skill> STORM_SURGE = registerSkill("storm_surge", 0, 100, preReqs(ELECTRIFICATION, SUPERCONDUCTOR));
    public static final Holder<Skill> CHAIN_REACTION = registerSkill("chain_reaction", -25, 150, preReqs(UNLEASHED_STORM, STORM_SURGE));
    public static final Holder<Skill> AMPLIFY = registerSkill("amplify", -50, 200, preReqs(CHAIN_REACTION));
    public static final Holder<Skill> ALTERNATING_CURRENT = registerSkill("alternating_current", 0, 200, preReqs(CHAIN_REACTION));

    //Storm Rift
    public static final Holder<Skill> STORM_RIFT = registerSkill("storm_rift");
    public static final Holder<Skill> STORM_FURY = registerSkill("storm_fury", 0, 50, preReqs(STORM_RIFT));
    public static final Holder<Skill> DISPLACEMENT_FIELD = registerSkill("displacement_field", 50, 50, preReqs(STORM_RIFT));
    public static final Holder<Skill> MAGNETIC_FIELD = registerSkill("magnetic_field", 0, 100, preReqs(STORM_FURY));
    public static final Holder<Skill> EVENT_HORIZON = registerSkill("event_horizon", -50, 100, preReqs(STORM_FURY));
    public static final Holder<Skill> CHARGED_RIFT = registerSkill("charged_rift", 0, 150, preReqs(MAGNETIC_FIELD));
    public static final Holder<Skill> MOTION_SICKNESS = registerSkill("motion_sickness", 0, 200, preReqs(CHARGED_RIFT));
    public static final Holder<Skill> FORCED_WARP = registerSkill("forced_warp", -50, 200, preReqs(CHARGED_RIFT));
    public static final Holder<Skill> STORM_CALLER = registerSkill("storm_caller", 50, 200, preReqs(CHARGED_RIFT));
    public static final Holder<Skill> IMPLOSION = registerSkill("implosion", -25, 250, preReqs(FORCED_WARP, MOTION_SICKNESS, STORM_CALLER));
    public static final Holder<Skill> ORBITAL_SHELL = registerSkill("orbital_shell", 25, 250, preReqs(FORCED_WARP, MOTION_SICKNESS, STORM_CALLER));

    //Cyclone
//    public static final Holder<Skill> CYCLONE = registerSkill("cyclone");
//    public static final Holder<Skill> EYE_OF_THE_STORM = registerSkill("eye_of_the_storm", -50, 50, preReqs(CYCLONE));
//    public static final Holder<Skill> FALLING_DEBRIS = registerSkill("falling_debris", -50, 100, preReqs(EYE_OF_THE_STORM));
//    public static final Holder<Skill> WHIRLING_TEMPEST = registerSkill("whirling_tempest", 0, 50, preReqs(CYCLONE));
//    public static final Holder<Skill> HURRICANE = registerSkill("hurricane", 50, 50, preReqs(CYCLONE));
//    public static final Holder<Skill> MAELSTROM = registerSkill("maelstrom", 50, 100, preReqs(HURRICANE));
//    public static final Holder<Skill> VORTEX = registerSkill("vortex", 0, 100, preReqs(WHIRLING_TEMPEST));
//    public static final Holder<Skill> GALE_FORCE = registerModifierSkill("gale_force", -50, 150, preReqs(WHIRLING_TEMPEST), SpellModifier.GALE_FORCE);
//    public static final Holder<Skill> FROSTFRONT = registerSkill("frostfront", 0, 150, preReqs(WHIRLING_TEMPEST));
//    public static final Holder<Skill> STATIC_CHARGE = registerSkill("static_charge", 50, 150, preReqs(WHIRLING_TEMPEST));
//    public static final Holder<Skill> HAILSTORM = registerConditionalSkill("hailstorm", 0, 200, preReqs(GALE_FORCE, FROSTFRONT, STATIC_CHARGE), (player, holder) -> holder.hasSkill(FROSTFRONT.value()) && holder.hasSkill(STATIC_CHARGE.value()));

    //Stride
    public static final Holder<Skill> STRIDE = registerSkill("stride");
    public static final Holder<Skill> QUICK_SPRINT = registerSkill("quick_sprint", 0, 50, preReqs(STRIDE));
    public static final Holder<Skill> GALLOPING_STRIDE = registerSkill("galloping_stride", 0, 100, preReqs(QUICK_SPRINT));
    public static final Holder<Skill> FLEETFOOTED = registerSkill("fleetfooted", -50, 150, preReqs(GALLOPING_STRIDE));
    public static final Holder<Skill> RIDERS_RESILIENCE = registerSkill("riders_resilience", -75, 200, preReqs(FLEETFOOTED));
    public static final Holder<Skill> SUREFOOTED = registerSkill("surefooted", 50, 150, preReqs(GALLOPING_STRIDE));
    public static final Holder<Skill> AQUA_TREAD = registerSkill("aqua_tread", 75, 200, preReqs(SUREFOOTED));
    public static final Holder<Skill> ENDURANCE = registerModifierSkill("endurance", 0, 150, preReqs(GALLOPING_STRIDE), SpellModifier.ENDURANCE);
    public static final Holder<Skill> MOMENTUM = registerSkill("momentum", -25, 200, preReqs(ENDURANCE));
    public static final Holder<Skill> STAMPEDE = registerSkill("stampede", 25, 200, preReqs(ENDURANCE));
    public static final Holder<Skill> MARATHON = registerSkill("marathon", 0, 250, preReqs(MOMENTUM, STAMPEDE));

    public static final Holder<Skill> DOLPHINS_FIN = registerRadialSkill("dolphins_fin");
    public static final Holder<Skill> MERMAIDS_TAIL = registerSkill("mermaids_tail", 0, 50, preReqs(DOLPHINS_FIN));
    public static final Holder<Skill> POD_LEADER = registerSkill("pod_leader", -75, 50, preReqs(DOLPHINS_FIN));
    public static final Holder<Skill> SLIPSTREAM = registerSkill("slipstream", 0, 100, preReqs(MERMAIDS_TAIL));
    public static final Holder<Skill> ECHOLOCATION = registerRadialSkill("echolocation", 50, 100, preReqs(MERMAIDS_TAIL));
    public static final Holder<Skill> SONAR_BLAST = registerRadialSkill("sonar_blast", -50, 100, preReqs(MERMAIDS_TAIL));
    public static final Holder<Skill> AQUATIC_DASH = registerRadialSkill("aquatic_dash", 0, 150, preReqs(SLIPSTREAM, ECHOLOCATION, SONAR_BLAST));
    public static final Holder<Skill> SHARK_ATTACK = registerSkill("shark_attack", 0, 200, preReqs(AQUATIC_DASH));
    public static final Holder<Skill> HAMMERHEAD = registerSkill("hammerhead", -50, 200, preReqs(AQUATIC_DASH));
    public static final Holder<Skill> KELP_VEIL = registerSkill("kelp_veil", 50, 200, preReqs(AQUATIC_DASH));
    public static final Holder<Skill> OCEAN_DWELLER = registerSkill("ocean_dweller", 0, 250, preReqs(SHARK_ATTACK, HAMMERHEAD, KELP_VEIL));

    //Shadow Gate
    public static final Holder<Skill> SHADOW_GATE = registerRadialSkill("shadow_gate");
    public static final Holder<Skill> REACH = registerSkill("reach", -50, 50, preReqs(SHADOW_GATE));
    public static final Holder<Skill> BLINK = registerSkill("blink", -75, 100, preReqs(REACH));
    public static final Holder<Skill> SHADOW_ESCAPE = registerSkill("shadow_escape", -75, 150, preReqs(BLINK));
    public static final Holder<Skill> OPEN_INVITATION = registerSkill("open_invitation", 50, 50, preReqs(SHADOW_GATE));
    public static final Holder<Skill> QUICK_RECHARGE = registerSkill("quick_recharge", 50, 100, preReqs(OPEN_INVITATION));
    public static final Holder<Skill> UNWANTED_GUESTS = registerSkill("unwanted_guests", 100, 100, preReqs(OPEN_INVITATION));
    public static final Holder<Skill> BAIT_AND_SWITCH = registerSkill("bait_and_switch", 100, 150, preReqs(UNWANTED_GUESTS));
    public static final Holder<Skill> DARKNESS_PREVAILS = registerSkill("darkness_prevails", 0, 100, preReqs(SHADOW_GATE));
    public static final Holder<Skill> GRAVITY_SHIFT = registerRadialSkill("gravity_shift", -25, 150, preReqs(DARKNESS_PREVAILS));
    public static final Holder<Skill> DUAL_DESTINATION = registerSkill("dual_destination", 25, 150, preReqs(DARKNESS_PREVAILS));

    //Mystic Armor
    public static final Holder<Skill> MYSTIC_ARMOR = registerSkill("mystic_armor");
    public static final Holder<Skill> FORESIGHT = registerModifierSkill("foresight", -50, 50, preReqs(MYSTIC_ARMOR), SpellModifier.FORESIGHT);
    public static final Holder<Skill> ARCANE_VENGEANCE = registerSkill("arcane_vengeance", 0 , 50, preReqs(MYSTIC_ARMOR));
    public static final Holder<Skill> EQUILIBRIUM = registerSkill("equilibrium", 0, 100, preReqs(ARCANE_VENGEANCE));
    public static final Holder<Skill> PLANAR_DEFLECTION = registerSkill("planar_deflection", 0, 150, preReqs(EQUILIBRIUM));
    public static final Holder<Skill> PURSUIT = registerSkill("pursuit", 50, 50, preReqs(MYSTIC_ARMOR));
    public static final Holder<Skill> COMBAT_PERCEPTION = registerSkill("combat_perception", 50, 100, preReqs(PURSUIT));
    public static final Holder<Skill> CRYSTALLINE_ARMOR = registerSkill("crystalline_armor", 50, 150, preReqs(COMBAT_PERCEPTION));
    public static final Holder<Skill> ELDRITCH_INTERVENTION = registerSkill("eldritch_intervention", 50, 200, preReqs(CRYSTALLINE_ARMOR));
    public static final Holder<Skill> SUBLIME_BEACON = registerSkill("sublime_beacon", 100, 200, preReqs(CRYSTALLINE_ARMOR));
    public static final Holder<Skill> SOUL_RECHARGE = registerSkill("soul_recharge", 75, 250, preReqs(ELDRITCH_INTERVENTION, SUBLIME_BEACON));

    //Cobbled hide
    public static final Holder<Skill> COBBLED_HIDE = registerSkill("cobbled_hide");
    public static final Holder<Skill> IRON_HIDE = registerSkill("iron_hide", 0, 50, preReqs(COBBLED_HIDE));
    public static final Holder<Skill> DIAMOND_HIDE = registerSkill("diamond_hide", 0, 50, preReqs(COBBLED_HIDE));
    public static final Holder<Skill> DRAGON_HIDE = registerSkill("dragon_hide", 0, 50, preReqs(COBBLED_HIDE));
    public static final Holder<Skill> THORNY_HIDE = registerSkill("thorny_hide", 0, 50, preReqs(COBBLED_HIDE));
    public static final Holder<Skill> REPULSIVE_SKIN = registerSkill("repulsive_skin", 0, 50, preReqs(COBBLED_HIDE));
    public static final Holder<Skill> REFLECTIVE_FLESH = registerSkill("reflective_flesh", 0, 50, preReqs(COBBLED_HIDE));
    public static final Holder<Skill> BLAST_RESISTANT = registerSkill("blast_resistant", 0, 50, preReqs(COBBLED_HIDE));
    public static final Holder<Skill> SPONGY_FLESH = registerSkill("spongy_flesh", 0, 50, preReqs(COBBLED_HIDE));
    public static final Holder<Skill> INFECTIOUS = registerSkill("infectious", 0, 50, preReqs(COBBLED_HIDE));
    public static final Holder<Skill> REINFORCED = registerSkill("reinforced", 0, 50, preReqs(COBBLED_HIDE));
    public static final Holder<Skill> VIRAL = registerSkill("viral", 0, 50, preReqs(COBBLED_HIDE));

    //Summon Undead
    public static final Holder<Skill> SUMMON_UNDEAD = registerRadialSkill("summon_undead");
    public static final Holder<Skill> SUMMON_ZOMBIFIED_PIGLIN = registerRadialSkill("summon_zombie_piglin", -100, 50, preReqs(SUMMON_UNDEAD));
    public static final Holder<Skill> SUMMON_SKELETON = registerRadialSkill("summon_skeleton", -50, 50, preReqs(SUMMON_UNDEAD));
    public static final Holder<Skill> SUMMON_PHANTOM = registerRadialSkill("summon_phantom", 50, 50, preReqs(SUMMON_UNDEAD));
    public static final Holder<Skill> SUMMON_DROWNED = registerRadialSkill("summon_drowned", 100, 50, preReqs(SUMMON_UNDEAD));
    public static final Holder<Skill> ROTTEN_SOLDIERS = registerSkill("rotten_soldiers", 0, 100, preReqs(SUMMON_UNDEAD));
    public static final Holder<Skill> CRIMSON_PACT = registerSkill("crimson_pact", -100, 100, preReqs(SUMMON_ZOMBIFIED_PIGLIN));
    public static final Holder<Skill> HALL_OF_THE_DEAD = registerSkill("hall_of_the_dead", -50, 100, preReqs(SUMMON_SKELETON));
    public static final Holder<Skill> SILENT_NIGHT = registerSkill("silent_night", 50, 100, preReqs(SUMMON_PHANTOM));
    public static final Holder<Skill> SUNKEN_BREATH = registerSkill("sunken_breath", 100, 100, preReqs(SUMMON_DROWNED));
    public static final Holder<Skill> CORPSE_EXPLOSION = registerSkill("corpse_explosion", 0, 150, preReqs(ROTTEN_SOLDIERS, CRIMSON_PACT, HALL_OF_THE_DEAD, SILENT_NIGHT, SUNKEN_BREATH));

    //Wild Mushroom
    public static final Holder<Skill> WILD_MUSHROOM = registerSkill("wild_mushroom");
    public static final Holder<Skill> VILE_INFLUENCE = registerSkill("vile_influence", -50, 50, preReqs(WILD_MUSHROOM));
    public static final Holder<Skill> HASTENED_GROWTH = registerSkill("hastened_growth", -50, 100, preReqs(VILE_INFLUENCE));
    public static final Holder<Skill> PARASITIC_FUNGUS = registerSkill("parasitic_fungus", 50, 50, preReqs(WILD_MUSHROOM));
    public static final Holder<Skill> ENVENOM = registerSkill("envenom", 0, 50, preReqs(WILD_MUSHROOM));
    public static final Holder<Skill> NATURES_DOMINANCE = registerSkill("natures_dominance", 0, 100, preReqs(ENVENOM));
    public static final Holder<Skill> POISON_ESSENCE = registerSkill("poison_essence", 50, 150, preReqs(NATURES_DOMINANCE));
    public static final Holder<Skill> SYNTHESIS = registerSkill("synthesis", 50, 200, preReqs(POISON_ESSENCE));
    public static final Holder<Skill> FUNGAL_HARVEST = registerSkill("fungal_harvest", 0, 150, preReqs(NATURES_DOMINANCE));
    public static final Holder<Skill> LIVING_FUNGUS = registerSkill("living_fungus", 0, 200, preReqs(FUNGAL_HARVEST));
    public static final Holder<Skill> PROLIFERATION = registerSkill("proliferation", 0, 250, preReqs(LIVING_FUNGUS));


    //Healing Touch
    //TODO: Tree
    public static final Holder<Skill> HEALING_TOUCH = registerSkill("healing_touch");
    public static final Holder<Skill> BLASPHEMY = registerSkill("blasphemy", -50, 50, preReqs(HEALING_TOUCH));
    public static final Holder<Skill> CONVALESCENCE = registerSkill("convalescence", -50, 100, preReqs(BLASPHEMY));
    public static final Holder<Skill> DIVINE_BALANCE = registerModifierSkill("divine_balance", 0, 50, preReqs(HEALING_TOUCH), SpellModifier.DIVINE_BALANCE_DURATION, SpellModifier.DIVINE_BALANCE_MANA);
    public static final Holder<Skill> NATURES_TOUCH = registerSkill("natures_touch", 0, 100, preReqs(DIVINE_BALANCE));
    public static final Holder<Skill> CLEANSING_TOUCH = registerSkill("cleansing_touch", 0, 150, preReqs(NATURES_TOUCH));
    public static final Holder<Skill> HEALING_STREAM = registerSkill("healing_stream", 50, 50, preReqs(HEALING_TOUCH));
    public static final Holder<Skill> TRANQUILITY_OF_WATER = registerSkill("tranquility_of_water", 50, 100, preReqs(HEALING_STREAM));
    public static final Holder<Skill> ACCELERATED_GROWTH = registerSkill("accelerated_growth", 50, 150, preReqs(TRANQUILITY_OF_WATER));
    public static final Holder<Skill> OVERGROWTH = registerSkill("overgrowth", 0, 200, preReqs(TRANQUILITY_OF_WATER, CLEANSING_TOUCH));
    public static final Holder<Skill> OAK_BLESSING = registerSkill("oak_blessing", 50, 200, preReqs(TRANQUILITY_OF_WATER, CLEANSING_TOUCH));

    public static final Holder<Skill> BLESSING = registerSkill("blessing");
    public static final Holder<Skill> FORTIFIED_HEARTS = registerSkill("fortified_hearts", 0, 50, preReqs(BLESSING));
    public static final Holder<Skill> ARCANE_RESTORATION = registerSkill("arcane_restoration", 0, 100, preReqs(BLESSING));
    public static final Holder<Skill> SATIATING_BLESSING = registerSkill("satiating_blessing", 0, 150, preReqs(BLESSING));
    public static final Holder<Skill> AIR_BUBBLE = registerSkill("air_bubble", 0, 200, preReqs(BLESSING));
    public static final Holder<Skill> PURIFYING_WARD = registerSkill("purifying_ward", 0, 250, preReqs(BLESSING));
    public static final Holder<Skill> EXTENDED_GRACE = registerSkill("extended_grace", 0, 300, preReqs(BLESSING));
    public static final Holder<Skill> SHARED_BOON = registerSkill("shared_boon", 0, 350, preReqs(BLESSING));
    public static final Holder<Skill> OVERFLOWING_AID = registerSkill("overflowing_aid", 0, 400, preReqs(BLESSING));
    public static final Holder<Skill> CONSECRATED_PRESENCE = registerSkill("consecrated_presence", 0, 450, preReqs(BLESSING));
    public static final Holder<Skill> UPLIFTING_CHORUS = registerSkill("uplifting_chorus", 0, 500, preReqs(BLESSING));

    //Healing Blossom
    public static final Holder<Skill> HEALING_BLOSSOM = registerSkill("healing_blossom");
    public static final Holder<Skill> THORNY_VINES = registerSkill("thorny_vines", -75, 50, preReqs(HEALING_BLOSSOM));
    public static final Holder<Skill> BLOOM = registerSkill("bloom", -25, 50, preReqs(HEALING_BLOSSOM));
    public static final Holder<Skill> ETERNAL_SPRING = registerSkill("eternal_spring", -25, 100, preReqs(BLOOM));
    public static final Holder<Skill> FLOWER_FIELD = registerSkill("flower_field", 25, 50, preReqs(HEALING_BLOSSOM));
    public static final Holder<Skill> FLOURISHING_GROWTH = registerSkill("flourishing_growth", 25, 100, preReqs(FLOWER_FIELD));
    public static final Holder<Skill> HEALING_WINDS = registerSkill("healing_winds", 25, 150, preReqs(FLOURISHING_GROWTH));
    public static final Holder<Skill> BURST_OF_LIFE = registerSkill("burst_of_life", 75, 50, preReqs(HEALING_BLOSSOM));
    public static final Holder<Skill> PETAL_SHIELD = registerSkill("petal_shield", 75, 100, preReqs(BURST_OF_LIFE));
    public static final Holder<Skill> VERDANT_RENEWAL = registerSkill("verdant_renewal", 75, 150, preReqs(PETAL_SHIELD));
    public static final Holder<Skill> REBIRTH = registerSkill("rebirth", 50, 200, preReqs(VERDANT_RENEWAL, HEALING_WINDS));

    //Shadowbond
    public static Holder<Skill> SHADOWBOND = registerSkill("shadowbond");
    public static Holder<Skill> SHADOW_STEP = registerSkill("shadow_step", -50 , 50 , preReqs(SHADOWBOND));
    public static Holder<Skill> SNEAK_ATTACK = registerSkill("sneak_attack", -50, 100, preReqs(SHADOW_STEP));
    public static Holder<Skill> SILENT_EXCHANGE = registerSkill("silent_exchange", 50, 50, preReqs(SHADOWBOND));
    public static Holder<Skill> SNARE = registerSkill("snare", 50, 100, preReqs(SILENT_EXCHANGE));
    public static Holder<Skill> DISORIENTED = registerSkill("disoriented", 100, 100, preReqs(SILENT_EXCHANGE));
    public static Holder<Skill> EVERLASTING_BOND = registerSkill("everlasting_bond", 0, 50, preReqs(SHADOWBOND));
    public static Holder<Skill> OBSERVANT = registerSkill("observant", 0, 100, preReqs(EVERLASTING_BOND));
    public static Holder<Skill> LIVING_SHADOW = registerSkill("living_shadow", -50, 150, preReqs(OBSERVANT));
    public static Holder<Skill> REVERSAL = registerSkill("reversal", 0, 150, preReqs(OBSERVANT));
    public static Holder<Skill> SHADOW_CHAIN = registerSkill("shadow_chain", 50, 150, preReqs(OBSERVANT));

    //Purge Magic
    public static Holder<Skill> PURGE_MAGIC = registerRadialSkill("purge_magic");
    public static Holder<Skill> COUNTER_MAGIC = registerRadialSkill("counter_magic", -50, 50, preReqs(PURGE_MAGIC));
    public static Holder<Skill> CLEANSE = registerSkill("cleanse", -50, 100, preReqs(COUNTER_MAGIC));
    public static Holder<Skill> AVERSION = registerSkill("aversion", -50, 150, preReqs(CLEANSE));
    public static Holder<Skill> RADIO_WAVES = registerSkill("radio_waves", 0, 50, preReqs(PURGE_MAGIC));
    public static Holder<Skill> DOMINANT_MAGIC = registerSkill("dominant_magic", 50, 50, preReqs(PURGE_MAGIC));
    public static Holder<Skill> RESIDUAL_DISRUPTION = registerSkill("residual_disruption", 25, 100, preReqs(DOMINANT_MAGIC));
    public static Holder<Skill> UNFOCUSED = registerSkill("unfocused", 25, 150, preReqs(RESIDUAL_DISRUPTION));
    public static Holder<Skill> MAGIC_POISONING = registerSkill("magic_poisoning", 75, 100, preReqs(DOMINANT_MAGIC));
    public static Holder<Skill> NULLIFICATION = registerSkill("nullification", 75, 150, preReqs(MAGIC_POISONING));
    public static Holder<Skill> EXPUNGE = registerSkill("expunge", 0, 200, preReqs(AVERSION, UNFOCUSED, NULLIFICATION));

    //Totem Spirit
    //TODO: Tree
    public static Holder<Skill> SUMMON_CAT_SPIRIT = registerSkill("conjure_cat_totem");
    public static Holder<Skill> CATS_AGILITY = registerSkill("cats_agility", -50 , 50 , preReqs(SUMMON_CAT_SPIRIT));
    public static Holder<Skill> FERAL_FURY = registerSkill("feral_fury", -50 , 100 , preReqs(CATS_AGILITY));
    public static Holder<Skill> PRIMAL_RESILIENCE = registerSkill("primal_resilience", -50 , 150 , preReqs(FERAL_FURY));
    public static Holder<Skill> TOTEMIC_BOND = registerSkill("totemic_bond", -100 , 150 , preReqs(PRIMAL_RESILIENCE)); //TODO
    public static Holder<Skill> STEALTH_TACTICS = registerSkill("stealth_tactic", -100 , 200 , preReqs(TOTEMIC_BOND)); //TODO
    public static Holder<Skill> SAVAGE_LEAP = registerSkill("savage_leap", 50 , 50 , preReqs(SUMMON_CAT_SPIRIT)); //TODO
    public static Holder<Skill> TOTEMIC_ARMOR = registerSkill("totemic_armor", 50 , 100 , preReqs(SAVAGE_LEAP)); //TODO
    public static Holder<Skill> WARRIORS_ROAR = registerSkill("warriors_roar", 50 , 150 , preReqs(TOTEMIC_ARMOR)); //TODO
    public static Holder<Skill> TWIN_SPIRITS = registerSkill("twin_spirits", 0 , 200 , preReqs(WARRIORS_ROAR, PRIMAL_RESILIENCE)); //TODO
    public static Holder<Skill> NINE_LIVES = registerSkill("nine_lives", 0 , 250 , preReqs(TWIN_SPIRITS)); //TODO

    private static Holder<Skill> registerSkill(String name) {
        return SKILLS.register(name, Skill::new);
    }

    private static Holder<Skill> registerSkill(String name, int xPos, int yPos, HolderSet<Skill> prereqs) {
        return SKILLS.register(name, () -> new Skill(xPos, yPos, prereqs));
    }

    private static Holder<Skill> registerRadialSkill(String name) {
        return SKILLS.register(name, () -> new Skill() {
            @Override
            public boolean isRadial() {
                return true;
            }
        });
    }

    private static Holder<Skill> registerRadialSkill(String name, int xPos, int yPos, HolderSet<Skill> prereqs) {
        return SKILLS.register(name, () -> new Skill(xPos, yPos, prereqs) {
            @Override
            public boolean isRadial() {
                return true;
            }
        });
    }

    private static Holder<Skill> registerConditionalSkill(String name, int xPos, int yPos, HolderSet<Skill> prereqs, BiPredicate<Player, SkillHolder> skillCondition) {
        return SKILLS.register(name, () -> new Skill(xPos, yPos, prereqs) {
            @Override
            public boolean canUnlockSkill(Player player, SkillHolder holder) {
                return skillCondition.test(player, holder);
            }
        });
    }

    private static Holder<Skill> registerModifierSkill(String name, int xPos, int yPos, HolderSet<Skill> prereqs, SpellModifier... spellModifiers) {
        return SKILLS.register(name, () -> new ModifierSkill(xPos, yPos, prereqs, spellModifiers));
    }

    private static HolderSet<Skill> preReqs(Holder<Skill>... skills) {
        return HolderSet.direct(skills);
    }

    public static void register(IEventBus eventBus) {
        SKILLS.register(eventBus);
    }
}
