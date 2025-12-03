package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.world.effect.*;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SBEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister
            .create(Registries.MOB_EFFECT, Constants.MOD_ID);

    //TODO: Make status effects do stuff
    //Status
    public static final Holder<MobEffect> COMBUST = EFFECTS.register("combust", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> FROZEN = EFFECTS.register("frozen", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> DISCHARGE = EFFECTS.register("discharge", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> DISEASE = EFFECTS.register("disease", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));

    //Crowd Control
    public static final Holder<MobEffect> ROOTED = EFFECTS.register("rooted", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> STUNNED = EFFECTS.register("stunned", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> SLEEP = EFFECTS.register("sleep", () -> new SleepEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> SILENCED = EFFECTS.register("silenced", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> FEAR = EFFECTS.register("fear", () -> new FearEffect(MobEffectCategory.HARMFUL, 8889187).addAttributeModifier(Attributes.ATTACK_DAMAGE, CommonClass.customLocation("fear_attack"), 0.75, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).addAttributeModifier(Attributes.ARMOR, CommonClass.customLocation("fear_armor"), 0.75, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> TAUNT = EFFECTS.register("taunt", () -> new TauntEffect(MobEffectCategory.HARMFUL, 8889187));

    public static final Holder<MobEffect> AFTERGLOW = EFFECTS.register("afterglow", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> PERMAFROST = EFFECTS.register("permafrost", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> STORMSTRIKE = EFFECTS.register("stormstrike", () -> new StormstrikeEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> BATTLE_CRY = EFFECTS.register("battle_cry", () -> new SBEffect(MobEffectCategory.BENEFICIAL, 8889187)); //Increase damage by 15%
    public static final Holder<MobEffect> COUNTER_MAGIC = EFFECTS.register("counter_magic", () -> new SBEffect(MobEffectCategory.BENEFICIAL, 8889187));

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
