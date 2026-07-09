package com.ombremoon.spellbound.common.world.sound;

import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SpellboundSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Constants.MOD_ID);

    //FIRE
    public static final Supplier<SoundEvent> FIREBALL_USE = registerSoundEvent("fireball_use");
    public static final Supplier<SoundEvent> FIREBALL_TRAVEL = registerSoundEvent("fireball_travel");
    public static final Supplier<SoundEvent> FLAMEJET_USE = registerSoundEvent("flamejet_use");
    public static final Supplier<SoundEvent> SOLAR_RAY = registerSoundEvent("solar_ray");
    //SHOCK
    public static final Supplier<SoundEvent> ELECTRIC_CHARGE_USE = registerSoundEvent("electric_charge_use");
    public static final Supplier<SoundEvent> ELECTRIC_CHARGE_RECAST = registerSoundEvent("electric_charge_recast");
    public static final Supplier<SoundEvent> STORMSTRIKE_USE = registerSoundEvent("stormstrike_use");
    public static final Supplier<SoundEvent> STORMSTRIKE_IMPACT = registerSoundEvent("stormstrike_impact");
    public static final Supplier<SoundEvent> INTERFERENCE_ZAP = registerSoundEvent("interference_zap");

    //DIVINE
    public static final Supplier<SoundEvent> BLESSING = registerSoundEvent("blessing");
    public static final Supplier<SoundEvent> SMITE_DARK_BLADE = registerSoundEvent("smite_dark_blade_imbuement");
    public static final Supplier<SoundEvent> SMITE = registerSoundEvent("smite_imbuement");
    public static final Supplier<SoundEvent> SMITE_PARRY = registerSoundEvent("smite_parry");
    public static final Supplier<SoundEvent> UPLIFTING_CHORUS = registerSoundEvent("uplifting_chorus");
    public static final Supplier<SoundEvent> MAGIC_SPARKLES = registerSoundEvent("magic_sparks");
    public static final Supplier<SoundEvent> DIVINE_ACTION = registerSoundEvent("divine_action");
    public static final Supplier<SoundEvent> DARK_DIVINE_ACTION = registerSoundEvent("dark_divine_action");



            //MISC
    public static final Supplier<SoundEvent> CURSED_RUNE_ACTIVATED = registerSoundEvent("cursed_rune_activated");
    public static final Supplier<SoundEvent> SHADOW_PLACE = registerSoundEvent("shadow_place");
    public static final Supplier<SoundEvent> SPEED = registerSoundEvent("speed");
    public static final Supplier<SoundEvent> PURGE_MAGIC = registerSoundEvent("purge_magic");
    public static final Supplier<SoundEvent> PURGE_MAGIC_EXPUNGE = registerSoundEvent("purge_magic_expunge");

    public static final Supplier<SoundEvent> COOLING_ARMOR = registerSoundEvent("cooling_armor");

    public static final Supplier<SoundEvent> CRYSTAL_SHATTER = registerSoundEvent("crystal_shatter");
    public static final Supplier<SoundEvent> CORPSE_EXPLOSION = registerSoundEvent("corpse_explosion");

    public static final Supplier<SoundEvent> SHATTER_SKIN = registerSoundEvent("shatter_skin");

    //NEED IMPLEMENTATION
    public static final Supplier<SoundEvent> NIGHTBLADE = registerSoundEvent("nightblade_imbuement");
    public static final Supplier<SoundEvent> SMITE_STRIKE = registerSoundEvent("smite_strike");
    public static final Supplier<SoundEvent> SMITE_SWOOSH = registerSoundEvent("smite_swoosh"); //this is for the projectile



    public static final Supplier<SoundEvent> ROCK_BUILD_UP = registerSoundEvent("rock_build_up");
    //MENU
    public static final  Supplier<SoundEvent> RES_STONE_OPEN = registerSoundEvent("res_stone_open");


    private static Supplier<SoundEvent> registerSoundEvent(String name){
        ResourceLocation id = CommonClass.customLocation(name);
        return SOUND_EVENTS.register(name, ()-> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }
}
