package com.ombremoon.spellbound.common.world.spell.deception;

import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import com.ombremoon.spellbound.common.magic.api.SpellAnimation;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FlickerSpell extends AnimatedSpell implements RadialSpell {
    private static final ResourceLocation STEP_INTO_SHADOW = CommonClass.customLocation("step_into_shadow");

    private static Builder<FlickerSpell> createFlickerBuilder() {
        return createSimpleSpellBuilder(FlickerSpell.class)
                .duration(5)
                .manaCost(27)
                .castTime(5)
                .castAnimation(context -> {
                    boolean flag = !context.hasSkill(SBSkills.SWIFT_SHADOWS);
                    String castPrefix = flag ? "" : "walking_";
                    return new SpellAnimation(castPrefix + "instant_cast", SpellAnimation.Type.CAST, flag);
                })
                .castCondition((context, flickerSpell) -> {
                    double range = SpellUtil.getCastRange(context.getCaster());
                    if (!context.hasSkill(SBSkills.DISTANT_FLICKER))
                        range /= 2;

                    return flickerSpell.getTargetBlock(range) != null;
                })
                .fullRecast(true);
    }

    public FlickerSpell() {
        super(SBSpells.FLICKER.get(), createFlickerBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            double range = SpellUtil.getCastRange(context.getCaster());
            if (!context.hasSkill(SBSkills.DISTANT_FLICKER))
                range /= 2;

            Vec3 vec3 = this.findTeleportLocation(level, caster, (float) range);
            if (vec3 != null) {
                if (context.hasSkill(SBSkills.CONFUSION)) {
                    this.summonEntity(context, SBEntities.LIVING_SHADOW.get(), caster.position());
                }

                caster.teleportTo(vec3.x, vec3.y, vec3.z);
                if (context.hasSkill(SBSkills.STEP_INTO_SHADOW)) {
                    this.addSkillBuff(
                            caster,
                            SBSkills.STEP_INTO_SHADOW,
                            STEP_INTO_SHADOW,
                            BuffCategory.BENEFICIAL,
                            SkillBuff.MOB_EFFECT,
                            new MobEffectInstance(SBEffects.MAGI_INVISIBILITY, 80),
                            80
                    );
                }
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {

    }

    @Override
    public boolean shouldRender(SpellContext context) {
        return false;
    }
}
