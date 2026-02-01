package com.ombremoon.spellbound.common.world.familiars;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import com.ombremoon.sentinellib.api.box.OBBSentinelBox;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.spellbound.common.init.SBAffinities;
import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBFamiliars;
import com.ombremoon.spellbound.common.magic.familiars.Familiar;
import com.ombremoon.spellbound.common.magic.skills.FamiliarAffinity;
import com.ombremoon.spellbound.common.magic.familiars.FamiliarContext;
import com.ombremoon.spellbound.common.magic.familiars.FamiliarHandler;
import com.ombremoon.spellbound.common.world.entity.living.familiars.OwlEntity;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class OwlFamiliar extends Familiar<OwlEntity> {
    public static final OBBSentinelBox TWISTED_OBB = createAlertBox();

    private static OBBSentinelBox createAlertBox() {
        return OBBSentinelBox.Builder.of("owl")
                .attackCondition((entity, livingEntity) -> !livingEntity.isAlliedTo(entity))
                .onCollisionTick(((entity, livingEntity) -> {
                    if (entity instanceof LivingEntity owner) {
                        FamiliarHandler handler = SpellUtil.getFamiliarHandler(owner);
                        handler.putSkillOnCooldown(SBAffinities.TWISTED_HEAD);
                        var OBBOwner = (ISentinel) owner;
                        OBBOwner.removeSentinelInstance(TWISTED_OBB);

                        if (owner.level().isClientSide) {
                            //TODO: Change sound
                            owner.level().playSound(owner,
                                    livingEntity.blockPosition(),
                                    SoundEvents.ANVIL_FALL, SoundSource.HOSTILE,
                                    1f,
                                    1f);
                        }
                    }
                }))
                .noDuration(entity -> false)
                .activeTicks((entity, integer) -> integer % 20 == 0)
                .build();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> modifyFamiliarAttributes(FamiliarContext context, int rebirths, int bond) {
        return ImmutableListMultimap.of(
                Attributes.MAX_HEALTH, affinityModifier("hp", context, SBAffinities.STEEL_FEATHERS, 2D, AttributeModifier.Operation.ADD_VALUE)
        );
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> modifyOwnerAttributes(FamiliarContext context, int rebirths, int bond) {
        return ImmutableListMultimap.of(
                SBAttributes.CAST_RANGE, affinityModifier("cast_range",context, SBAffinities.OWL_VISION, 1.15D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        );
    }

    @Override
    public void onSpawn(FamiliarContext context, BlockPos spawnPos) {
        super.onSpawn(context, spawnPos);

        context.getOwner().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1,0, true, true));
        enableTwistedOBB(context);
    }

    @Override
    public void onAffinityOffCooldown(FamiliarContext context, FamiliarAffinity affinity) {
        super.onAffinityOffCooldown(context, affinity);

        if (affinity == SBAffinities.TWISTED_HEAD) enableTwistedOBB(context);
    }

    private void enableTwistedOBB(FamiliarContext context) {
        if (hasAffinity(context, SBAffinities.TWISTED_HEAD)) {
            var sentinelOwner = (ISentinel) context.getOwner();
            sentinelOwner.triggerSentinelBox(TWISTED_OBB);
            useAffinity(context, SBAffinities.TWISTED_HEAD);
        }
    }

    @Override
    public void onRemove(FamiliarContext context, BlockPos removePos) {
        super.onRemove(context, removePos);

        context.getOwner().removeEffect(MobEffects.NIGHT_VISION);
    }
}
