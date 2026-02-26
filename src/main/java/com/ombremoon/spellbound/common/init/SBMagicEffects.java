package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.effects.types.*;
import com.ombremoon.spellbound.common.magic.effects.MagicEffect;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class SBMagicEffects {
    public static final ResourceKey<Registry<MagicEffect.Serializer<? extends MagicEffect>>> RITUAL_EFFECT_REGISTRY_KEY = ResourceKey.createRegistryKey(CommonClass.customLocation("magic_effect"));
    public static final Registry<MagicEffect.Serializer<? extends MagicEffect>> REGISTRY = new RegistryBuilder<>(RITUAL_EFFECT_REGISTRY_KEY).sync(true).create();
    public static final DeferredRegister<MagicEffect.Serializer<? extends MagicEffect>> RITUAL_EFFECTS = DeferredRegister.create(REGISTRY, Constants.MOD_ID);

    public static final Supplier<MagicEffect.Serializer<CreateItem>> CREATE_ITEM = RITUAL_EFFECTS.register("create_item", CreateItem.Serializer::new);
    public static final Supplier<MagicEffect.Serializer<CreateSpellTome>> CREATE_SPELL_TOME = RITUAL_EFFECTS.register("create_spell_tome", CreateSpellTome.Serializer::new);
    public static final Supplier<MagicEffect.Serializer<DamageEntity>> DAMAGE_ENTITY = RITUAL_EFFECTS.register("damage_entity", DamageEntity.Serializer::new);
    public static final Supplier<MagicEffect.Serializer<ApplyMobEffect>> APPLY_MOB_EFFECT = RITUAL_EFFECTS.register("apply_mob_effect", ApplyMobEffect.Serializer::new);
    public static final Supplier<MagicEffect.Serializer<SetResource>> SET_RESOURCE = RITUAL_EFFECTS.register("set_resource", SetResource.Serializer::new);
    public static final Supplier<MagicEffect.Serializer<DisarmEffect>> DISARM_EFFECT = RITUAL_EFFECTS.register("disarm_effect", DisarmEffect.Serializer::new);
    public static final Supplier<MagicEffect.Serializer<SummonEntity>> SUMMON_ENTITY = RITUAL_EFFECTS.register("summon_entity", SummonEntity.Serializer::new);
    public static final Supplier<MagicEffect.Serializer<SummonDoppelganger>> SUMMON_DOPPELGANGER = RITUAL_EFFECTS.register("summon_doppelganger", SummonDoppelganger.Serializer::new);
    public static final Supplier<MagicEffect.Serializer<ApplySpellModifier>> APPLY_SPELL_MODIFIER = RITUAL_EFFECTS.register("apply_spell_modifier", ApplySpellModifier.Serializer::new);
    public static final Supplier<MagicEffect.Serializer<TeleportEntity>> TELEPORT_ENTITY = RITUAL_EFFECTS.register("teleport_entity", TeleportEntity.Serializer::new);
    public static final Supplier<MagicEffect.Serializer<ModifyDamage>> MODIFY_DAMAGE = RITUAL_EFFECTS.register("modify_damage", ModifyDamage.Serializer::new);
    public static final Supplier<MagicEffect.Serializer<HealEntity>> HEAL_ENTITY = RITUAL_EFFECTS.register("heal_entity", HealEntity.Serializer::new);

    public static void register(IEventBus modEventBus) {
        RITUAL_EFFECTS.register(modEventBus);
    }
}
