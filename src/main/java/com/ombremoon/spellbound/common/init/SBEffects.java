package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.api.buff.SpellModifier;
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

    //Status
    public static final Holder<MobEffect> COMBUST = EFFECTS.register("combust", () -> new CombustEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> FROZEN = EFFECTS.register("frozen", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> DISCHARGE = EFFECTS.register("discharge", () -> new DischargeEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> DISEASE = EFFECTS.register("disease", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> BLOOD_LOSS = EFFECTS.register("blood_loss", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));

    //Crowd Control
    public static final Holder<MobEffect> ROOTED = EFFECTS.register("rooted", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> STUNNED = EFFECTS.register("stunned", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> SLEEP = EFFECTS.register("sleep", () -> new SleepEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> SILENCED = EFFECTS.register("silenced", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> FEAR = EFFECTS.register("fear", () -> new FearEffect(MobEffectCategory.HARMFUL, 8889187).addAttributeModifier(Attributes.ATTACK_DAMAGE, CommonClass.customLocation("fear_attack"), 0.75, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).addAttributeModifier(Attributes.ARMOR, CommonClass.customLocation("fear_armor"), 0.75, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> TAUNT = EFFECTS.register("taunt", () -> new TauntEffect(MobEffectCategory.HARMFUL, 8889187));

    //Set Bonuses
    public static final Holder<MobEffect> PYROMANCER = EFFECTS.register("pyromancer_set", () -> new SBEffect(MobEffectCategory.BENEFICIAL, 8889187).addSpellModifiers(SpellModifier.PYROMANCER_SET));
    public static final Holder<MobEffect> STORMWEAVER = EFFECTS.register("stormweaver_set", () -> new SBEffect(MobEffectCategory.BENEFICIAL, 8889187).addSpellModifiers(SpellModifier.STORMWEAVER_SET));
    public static final Holder<MobEffect> CRYOMANCER = EFFECTS.register("cryomancer_set", () -> new SBEffect(MobEffectCategory.BENEFICIAL, 8889187).addSpellModifiers(SpellModifier.CRYOMANCER_SET));
    public static final Holder<MobEffect> CREATIONIST = EFFECTS.register("creationist_set", () -> new SBEffect(MobEffectCategory.BENEFICIAL, 8889187).addSpellModifiers(SpellModifier.TRANSFIG_SET));

    public static final Holder<MobEffect> MAGI_INVISIBILITY = EFFECTS.register("magi_invisibility", () -> new SBEffect(MobEffectCategory.NEUTRAL, 8889187));
    public static final Holder<MobEffect> TARGET_AURA = EFFECTS.register("target_aura", () -> new SBEffect(MobEffectCategory.NEUTRAL, 8889187));
    public static final Holder<MobEffect> PERMAFROST = EFFECTS.register("permafrost", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> STORMSTRIKE = EFFECTS.register("stormstrike", () -> new StormstrikeEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> BATTLE_CRY = EFFECTS.register("battle_cry", () -> new SBEffect(MobEffectCategory.BENEFICIAL, 8889187)); //Increase damage by 15%
    public static final Holder<MobEffect> COUNTER_MAGIC = EFFECTS.register("counter_magic", () -> new SBEffect(MobEffectCategory.BENEFICIAL, 8889187));
    public static final Holder<MobEffect> OBFUSCATED = EFFECTS.register("obfuscated", () -> new SBEffect(MobEffectCategory.HARMFUL, 8889187));

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
