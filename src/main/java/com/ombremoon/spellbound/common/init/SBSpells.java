package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.world.spell.deception.PurgeMagicSpell;
import com.ombremoon.spellbound.common.world.spell.deception.ShadowbondSpell;
import com.ombremoon.spellbound.common.world.spell.divine.BlessingSpell;
import com.ombremoon.spellbound.common.world.spell.divine.HealingBlossomSpell;
import com.ombremoon.spellbound.common.world.spell.divine.HealingTouchSpell;
import com.ombremoon.spellbound.common.world.spell.ruin.fire.FlameJetSpell;
import com.ombremoon.spellbound.common.world.spell.ruin.fire.SolarRaySpell;
import com.ombremoon.spellbound.common.world.spell.ruin.ice.ShatteringCrystalSpell;
import com.ombremoon.spellbound.common.world.spell.ruin.shock.ElectricChargeSpell;
import com.ombremoon.spellbound.common.world.spell.ruin.shock.StormRiftSpell;
import com.ombremoon.spellbound.common.world.spell.ruin.shock.StormstrikeSpell;
import com.ombremoon.spellbound.common.world.spell.summon.SummonUndeadSpell;
import com.ombremoon.spellbound.common.world.spell.summon.WildMushroomSpell;
import com.ombremoon.spellbound.common.world.spell.transfiguration.DolphinsFinSpell;
import com.ombremoon.spellbound.common.world.spell.transfiguration.MysticArmorSpell;
import com.ombremoon.spellbound.common.world.spell.transfiguration.ShadowGateSpell;
import com.ombremoon.spellbound.common.world.spell.transfiguration.StrideSpell;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class SBSpells {
    public static final ResourceKey<Registry<SpellType<?>>> SPELL_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(CommonClass.customLocation("spell_type"));
    public static final Registry<SpellType<?>> REGISTRY = new RegistryBuilder<>(SPELL_TYPE_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<SpellType<?>> SPELL_TYPES = DeferredRegister.create(REGISTRY, Constants.MOD_ID);

    //Ruin
    public static final Supplier<SpellType<FlameJetSpell>> FLAME_JET = registerSpell("flame_jet", fireRuinBuilder("flame_jet", SpellMastery.NOVICE, FlameJetSpell::new)
            .skills(SBSkills.FLAME_JET, SBSkills.JET_ENGINE, SBSkills.FLAME_GEYSER, SBSkills.FLAME_INFERNO,
                    SBSkills.IGNITION_BURST, SBSkills.TURBO_CHARGE, SBSkills.EXPULSION_BLAST,
                    SBSkills.TURBULENCE_STREAM, SBSkills.JET_STABILIZATION, SBSkills.AFTERSHOCK_COMPRESSION, SBSkills.IRON_MAN));
    public static final Supplier<SpellType<SolarRaySpell>> SOLAR_RAY = registerSpell("solar_ray", fireRuinBuilder("solar_ray", SpellMastery.EXPERT, SolarRaySpell::new)
            .skills(SBSkills.SOLAR_RAY, SBSkills.SUNSHINE, SBSkills.OVERPOWER, SBSkills.HEALING_LIGHT,
                    SBSkills.CONCENTRATED_HEAT, SBSkills.OVERHEAT, SBSkills.SOLAR_BURST,
                    SBSkills.SOLAR_BORE, SBSkills.BLINDING_LIGHT, SBSkills.AFTERGLOW, SBSkills.POWER_OF_THE_SUN));
//    public static final Supplier<SpellType<VolcanoSpell>> VOLCANO = registerSpell("volcano", fireRuinBuilder("volcano", VolcanoSpell::new)
//            .skills(SBSkills.VOLCANO, SBSkills.INFERNO_CORE, SBSkills.LAVA_FLOW, SBSkills.EXPLOSIVE_BARRAGE,
//                    SBSkills.SHRAPNEL, SBSkills.HEATWAVE, SBSkills.SCORCHED_EARTH, SBSkills.SEISMIC_SHOCK,
//                    SBSkills.MOLTEN_SHIELD, SBSkills.PYROCLASTIC_CLOUD, SBSkills.APOCALYPSE));
    public static final Supplier<SpellType<ShatteringCrystalSpell>> SHATTERING_CRYSTAL = registerSpell("shattering_crystal", iceRuinBuilder("shattering_crystal", SpellMastery.EXPERT, ShatteringCrystalSpell::new)
            .skills(SBSkills.SHATTERING_CRYSTAL, SBSkills.ICE_SHARD, SBSkills.FRIGID_BLAST, SBSkills.CHILL,
                    SBSkills.FROZEN_SHRAPNEL, SBSkills.HYPOTHERMIA, SBSkills.THIN_ICE, SBSkills.CHAOTIC_SHATTER,
                    SBSkills.CRYSTAL_ECHO, SBSkills.LINGERING_FROST, SBSkills.GLACIAL_IMPACT));
    public static final Supplier<SpellType<StormstrikeSpell>> STORMSTRIKE = registerSpell("stormstrike", shockRuinBuilder("stormstrike", SpellMastery.NOVICE, StormstrikeSpell::new)
            .skills(SBSkills.STORMSTRIKE, SBSkills.STATIC_SHOCK, SBSkills.ELECTRIFY,
                    SBSkills.SHOCK_FACTOR, SBSkills.PURGE, SBSkills.REFRACTION, SBSkills.CHARGED_ATMOSPHERE,
                    SBSkills.PULSATION, SBSkills.DISARM, SBSkills.STORM_SHARD, SBSkills.SUPERCHARGE));
    public static final Supplier<SpellType<ElectricChargeSpell>> ELECTRIC_CHARGE = registerSpell("electric_charge", shockRuinBuilder("electric_charge", SpellMastery.NOVICE, ElectricChargeSpell::new)
            .skills(SBSkills.ELECTRIC_CHARGE, SBSkills.ELECTRIFICATION, SBSkills.SUPERCONDUCTOR, SBSkills.PIEZOELECTRIC,
                    SBSkills.OSCILLATION, SBSkills.HIGH_VOLTAGE, SBSkills.UNLEASHED_STORM, SBSkills.STORM_SURGE,
                    SBSkills.CHAIN_REACTION, SBSkills.AMPLIFY, SBSkills.ALTERNATING_CURRENT));
    public static final Supplier<SpellType<StormRiftSpell>> STORM_RIFT = registerSpell("storm_rift", shockRuinBuilder("storm_rift", SpellMastery.MASTER, StormRiftSpell::new)
            .skills(SBSkills.STORM_RIFT, SBSkills.STORM_FURY, SBSkills.DISPLACEMENT_FIELD, SBSkills.MAGNETIC_FIELD,
                    SBSkills.EVENT_HORIZON, SBSkills.CHARGED_RIFT, SBSkills.MOTION_SICKNESS, SBSkills.FORCED_WARP,
                    SBSkills.STORM_CALLER, SBSkills.IMPLOSION, SBSkills.ORBITAL_SHELL));
//    public static final Supplier<SpellType<CycloneSpell>> CYCLONE = registerSpell("cyclone", ruinBuilder("cyclone", CycloneSpell::new)
//            .skills(SBSkills.CYCLONE, SBSkills.WHIRLING_TEMPEST, SBSkills.VORTEX,
//                    SBSkills.FALLING_DEBRIS, SBSkills.MAELSTROM, SBSkills.HURRICANE, SBSkills.EYE_OF_THE_STORM,
//                    SBSkills.GALE_FORCE, SBSkills.FROSTFRONT, SBSkills.STATIC_CHARGE, SBSkills.HAILSTORM));

    //Transfiguration
    public static final Supplier<SpellType<StrideSpell>> STRIDE = registerSpell("stride", trasnfigurationBuilder("stride", SpellMastery.NOVICE, StrideSpell::new)
            .skills(SBSkills.STRIDE, SBSkills.QUICK_SPRINT, SBSkills.GALLOPING_STRIDE,
                    SBSkills.RIDERS_RESILIENCE, SBSkills.FLEETFOOTED, SBSkills.SUREFOOTED, SBSkills.AQUA_TREAD,
                    SBSkills.ENDURANCE, SBSkills.MOMENTUM, SBSkills.STAMPEDE, SBSkills.MARATHON));
    public static final Supplier<SpellType<DolphinsFinSpell>> DOLPHINS_FIN = registerSpell("dolphins_fin", trasnfigurationBuilder("dolphins_fin", SpellMastery.NOVICE, DolphinsFinSpell::new)
            .skills(SBSkills.DOLPHINS_FIN, SBSkills.MERMAIDS_TAIL, SBSkills.AQUATIC_DASH,
                    SBSkills.SHARK_ATTACK, SBSkills.ECHOLOCATION, SBSkills.POD_LEADER, SBSkills.SLIPSTREAM,
                    SBSkills.SONAR_BLAST, SBSkills.KELP_VEIL, SBSkills.HAMMERHEAD, SBSkills.OCEAN_DWELLER));
    public static final Supplier<SpellType<ShadowGateSpell>> SHADOW_GATE = registerSpell("shadow_gate", trasnfigurationBuilder("shadow_gate", SpellMastery.ADEPT, ShadowGateSpell::new)
            .skills(SBSkills.SHADOW_GATE, SBSkills.REACH, SBSkills.BLINK, SBSkills.SHADOW_ESCAPE,
                    SBSkills.OPEN_INVITATION, SBSkills.QUICK_RECHARGE, SBSkills.UNWANTED_GUESTS, SBSkills.BAIT_AND_SWITCH,
                    SBSkills.DARKNESS_PREVAILS, SBSkills.GRAVITY_SHIFT, SBSkills.DUAL_DESTINATION));
    public static final Supplier<SpellType<MysticArmorSpell>> MYSTIC_ARMOR = registerSpell("mystic_armor", trasnfigurationBuilder("mystic_armor", SpellMastery.ADEPT, MysticArmorSpell::new)
            .skills(SBSkills.MYSTIC_ARMOR, SBSkills.FORESIGHT, SBSkills.ARCANE_VENGEANCE,
                    SBSkills.EQUILIBRIUM, SBSkills.PLANAR_DEFLECTION, SBSkills.PURSUIT, SBSkills.COMBAT_PERCEPTION,
                    SBSkills.CRYSTALLINE_ARMOR, SBSkills.ELDRITCH_INTERVENTION, SBSkills.SUBLIME_BEACON, SBSkills.SOUL_RECHARGE));

    //Summons
    public static final Supplier<SpellType<SummonUndeadSpell>> SUMMON_UNDEAD = registerSpell("summon_undead", summonBuilder("summon_undead", SpellMastery.NOVICE, SummonUndeadSpell::new)
            .skills(SBSkills.SUMMON_UNDEAD, SBSkills.SUMMON_HUSK, SBSkills.SUMMON_SKELETON, SBSkills.SUMMON_PHANTOM,
                    SBSkills.SUMMON_DROWNED, SBSkills.ROTTEN_SOLDIERS, SBSkills.DESERT_RESILIENCE,
                    SBSkills.DAYWALKER, SBSkills.HALL_OF_THE_DEAD, SBSkills.SILENT_NIGHT, SBSkills.SUNKEN_BREATH));
    public static final Supplier<SpellType<WildMushroomSpell>> WILD_MUSHROOM = registerSpell("wild_mushroom", summonBuilder("wild_mushroom", SpellMastery.ADEPT, WildMushroomSpell::new)
            .skills(SBSkills.WILD_MUSHROOM, SBSkills.VILE_INFLUENCE, SBSkills.HASTENED_GROWTH, SBSkills.ENVENOM,
                    SBSkills.PARASITIC_FUNGUS, SBSkills.NATURES_DOMINANCE, SBSkills.POISON_ESSENCE,
                    SBSkills.LIVING_FUNGUS, SBSkills.PROLIFERATION, SBSkills.FUNGAL_HARVEST, SBSkills.SYNTHESIS));
//    public static final Supplier<SpellType<SpiritTotemSpell>> SPIRIT_TOTEM = registerSpell("conjure_spirit_totem", summonBuilder("conjure_spirit_totem", SpiritTotemSpell::new)
//            .skills(SBSkills.SUMMON_CAT_SPIRIT, SBSkills.CATS_AGILITY, SBSkills.FERAL_FURY,
//                    SBSkills.PRIMAL_RESILIENCE, SBSkills.TOTEMIC_BOND, SBSkills.STEALTH_TACTICS, SBSkills.SAVAGE_LEAP,
//                    SBSkills.TOTEMIC_ARMOR, SBSkills.WARRIORS_ROAR, SBSkills.TWIN_SPIRITS, SBSkills.NINE_LIVES));

    //Divine
    public static final Supplier<SpellType<HealingTouchSpell>> HEALING_TOUCH = registerSpell("healing_touch", divineBuilder("healing_touch", SpellMastery.NOVICE, HealingTouchSpell::new)
            .skills(SBSkills.HEALING_TOUCH, SBSkills.DIVINE_BALANCE, SBSkills.HEALING_STREAM, SBSkills.ACCELERATED_GROWTH,
                    SBSkills.TRANQUILITY_OF_WATER, SBSkills.NATURES_TOUCH, SBSkills.CLEANSING_TOUCH,
                    SBSkills.OVERGROWTH, SBSkills.BLASPHEMY, SBSkills.CONVALESCENCE, SBSkills.OAK_BLESSING));
    public static final Supplier<SpellType<BlessingSpell>> BLESSING = registerSpell("blessing", divineBuilder("blessing", SpellMastery.NOVICE, BlessingSpell::new)
            .skills(SBSkills.BLESSING, SBSkills.FORTIFIED_HEARTS, SBSkills.ARCANE_RESTORATION, SBSkills.SATIATING_BLESSING,
                    SBSkills.AIR_BUBBLE, SBSkills.PURIFYING_WARD, SBSkills.EXTENDED_GRACE, SBSkills.SHARED_BOON,
                    SBSkills.OVERFLOWING_AID, SBSkills.CONSECRATED_PRESENCE, SBSkills.UPLIFTING_CHORUS));
    public static final Supplier<SpellType<HealingBlossomSpell>> HEALING_BLOSSOM = registerSpell("healing_blossom", divineBuilder("healing_blossom", SpellMastery.EXPERT, HealingBlossomSpell::new)
            .skills(SBSkills.HEALING_BLOSSOM, SBSkills.THORNY_VINES, SBSkills.BLOOM, SBSkills.ETERNAL_SPRING,
                    SBSkills.FLOWER_FIELD, SBSkills.FLOURISHING_GROWTH, SBSkills.HEALING_WINDS, SBSkills.BURST_OF_LIFE,
                    SBSkills.PETAL_SHIELD, SBSkills.VERDANT_RENEWAL, SBSkills.REBIRTH));

    //Deception
    public static final Supplier<SpellType<ShadowbondSpell>> SHADOWBOND = registerSpell("shadowbond", deceptionBuilder("shadowbond", SpellMastery.ADEPT, ShadowbondSpell::new)
            .skills(SBSkills.SHADOWBOND, SBSkills.EVERLASTING_BOND, SBSkills.SHADOW_STEP, SBSkills.SNEAK_ATTACK,
                    SBSkills.SILENT_EXCHANGE, SBSkills.SNARE, SBSkills.DISORIENTED,
                    SBSkills.OBSERVANT, SBSkills.LIVING_SHADOW, SBSkills.REVERSAL, SBSkills.SHADOW_CHAIN));
    public static final Supplier<SpellType<PurgeMagicSpell>> PURGE_MAGIC = registerSpell("purge_magic", deceptionBuilder("purge_magic", SpellMastery.ADEPT, PurgeMagicSpell::new)
            .skills(SBSkills.PURGE_MAGIC, SBSkills.COUNTER_MAGIC, SBSkills.RADIO_WAVES, SBSkills.DOMINANT_MAGIC,
                    SBSkills.CLEANSE, SBSkills.AVERSION, SBSkills.RESIDUAL_DISRUPTION, SBSkills.UNFOCUSED,
                    SBSkills.MAGIC_POISONING, SBSkills.NULLIFICATION, SBSkills.EXPUNGE));

    private static <T extends AbstractSpell> Supplier<SpellType<T>> registerSpell(String name, SpellType.Builder<T> builder) {
        return SPELL_TYPES.register(name, builder::build);
    }

    private static <T extends AbstractSpell> SpellType.Builder<T> fireRuinBuilder(String name, SpellMastery mastery, SpellType.SpellFactory<T> factory) {
        return new SpellType.Builder<>(name, factory).setPath(SpellPath.RUIN, SpellPath.FIRE).setMastery(mastery);
    }

    private static <T extends AbstractSpell> SpellType.Builder<T> iceRuinBuilder(String name, SpellMastery mastery, SpellType.SpellFactory<T> factory) {
        return new SpellType.Builder<>(name, factory).setPath(SpellPath.RUIN, SpellPath.FROST).setMastery(mastery);
    }

    private static <T extends AbstractSpell> SpellType.Builder<T> shockRuinBuilder(String name, SpellMastery mastery, SpellType.SpellFactory<T> factory) {
        return new SpellType.Builder<>(name, factory).setPath(SpellPath.RUIN, SpellPath.SHOCK).setMastery(mastery);
    }

    private static <T extends AbstractSpell> SpellType.Builder<T> trasnfigurationBuilder(String name, SpellMastery mastery, SpellType.SpellFactory<T> factory) {
        return new SpellType.Builder<>(name, factory).setPath(SpellPath.TRANSFIGURATION).setMastery(mastery);
    }

    private static <T extends AbstractSpell> SpellType.Builder<T> summonBuilder(String name, SpellMastery mastery, SpellType.SpellFactory<T> factory) {
        return new SpellType.Builder<>(name, factory).setPath(SpellPath.SUMMONS).setMastery(mastery);
    }

    private static <T extends AbstractSpell> SpellType.Builder<T> divineBuilder(String name, SpellMastery mastery, SpellType.SpellFactory<T> factory) {
        return new SpellType.Builder<>(name, factory).setPath(SpellPath.DIVINE).setMastery(mastery);
    }

    private static <T extends AbstractSpell> SpellType.Builder<T> deceptionBuilder(String name, SpellMastery mastery, SpellType.SpellFactory<T> factory) {
        return new SpellType.Builder<>(name, factory).setPath(SpellPath.DECEPTION).setMastery(mastery);
    }

    public static void register(IEventBus modEventBus) {
        SPELL_TYPES.register(modEventBus);
    }
























}
