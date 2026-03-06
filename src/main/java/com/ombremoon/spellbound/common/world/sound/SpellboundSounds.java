package com.ombremoon.spellbound.common.world.sound;

import com.mojang.datafixers.kinds.Const;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.main.Spellbound;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SpellboundSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Constants.MOD_ID);

    //FIRE
    public static final Supplier<SoundEvent> FIREBALL_USE = registerSoundEvent("fireball_use");
    public static final Supplier<SoundEvent> FIREBALL_TRAVEL = registerSoundEvent("fireball_travel");
    public static final Supplier<SoundEvent> FLAMEJET_USE = registerSoundEvent("flamejet_use");
    //SHOCK
    public static final Supplier<SoundEvent> ELECTRIC_CHARGE_USE = registerSoundEvent("electric_charge_use");
    public static final Supplier<SoundEvent> ELECTRIC_CHARGE_RECAST = registerSoundEvent("electric_charge_recast");
    public static final Supplier<SoundEvent> STORMSTRIKE_USE = registerSoundEvent("stormstrike_use");

    private static Supplier<SoundEvent> registerSoundEvent(String name){
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
        return SOUND_EVENTS.register(name, ()-> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }
}
