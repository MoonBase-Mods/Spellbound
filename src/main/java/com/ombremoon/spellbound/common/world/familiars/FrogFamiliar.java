package com.ombremoon.spellbound.common.world.familiars;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import com.ombremoon.spellbound.common.init.SBAffinities;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBTags;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.buff.SpellModifier;
import com.ombremoon.spellbound.common.magic.api.buff.events.DamageEvent;
import com.ombremoon.spellbound.common.magic.api.buff.events.DeathEvent;
import com.ombremoon.spellbound.common.magic.api.buff.events.SpellEvent;
import com.ombremoon.spellbound.common.magic.familiars.Familiar;
import com.ombremoon.spellbound.common.magic.familiars.FamiliarContext;
import com.ombremoon.spellbound.common.world.entity.living.familiars.FrogEntity;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrogFamiliar extends Familiar<FrogEntity> {
    public static final List<Block> FROG_LIGHTS = new ArrayList<>(Arrays.asList(
            Blocks.VERDANT_FROGLIGHT,
            Blocks.OCHRE_FROGLIGHT,
            Blocks.PEARLESCENT_FROGLIGHT
    ));

    private boolean hasSwampBuff = false;

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> modifyOwnerAttributes(FamiliarContext context, int rebirths, int bond) {
        return ImmutableListMultimap.of(
                Attributes.JUMP_STRENGTH, affinityModifier("jump", context, SBAffinities.SPECTRAL_HOPS, 0.15D, AttributeModifier.Operation.ADD_VALUE),
                Attributes.SAFE_FALL_DISTANCE, affinityModifier("safe_fall", context, SBAffinities.SPECTRAL_HOPS, 1D, AttributeModifier.Operation.ADD_VALUE),
                Attributes.BLOCK_INTERACTION_RANGE, affinityModifier("block_reach", context, SBAffinities.ELONGATED_TONGUE, 3D, AttributeModifier.Operation.ADD_VALUE),
                Attributes.ENTITY_INTERACTION_RANGE, affinityModifier("entity_reach", context, SBAffinities.ELONGATED_TONGUE, 3D, AttributeModifier.Operation.ADD_VALUE)
        );
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> modifyFamiliarAttributes(FamiliarContext context, int rebirths, int bond) {
        return ImmutableListMultimap.of(
                Attributes.MAX_HEALTH, affinityModifier("hp", context, SBAffinities.WARTFUL_SKIN, 2D*rebirths, AttributeModifier.Operation.ADD_VALUE)
        );
    }

    @Override
    public void onSpawn(FamiliarContext context, BlockPos spawnPos) {
        super.onSpawn(context, spawnPos);
        if (hasAffinity(context, SBAffinities.MAGMA_DIGESTION)) addMagmaDigestionEvent(context);
        if (hasAffinity(context, SBAffinities.SLIMEY_EXPULSION)) addSlimeyExpulsionEvent(context);

        if (hasAffinity(context,  SBAffinities.MURKY_HABITAT)) {
            boolean inSwamp = context.getLevel().getBiome(context.getOwner().blockPosition()).is(Tags.Biomes.IS_SWAMP);
            if (inSwamp) addSwampPotency(context);
        }
    }

    @Override
    public boolean shouldTick(FamiliarContext context, int tickCount) {
        return tickCount % 20 == 0;
    }

    @Override
    public void tick(FamiliarContext context) {
        super.tick(context);

        if (hasAffinity(context, SBAffinities.MURKY_HABITAT)) {
            boolean inSwamp = context.getLevel().getBiome(context.getOwner().blockPosition()).is(Tags.Biomes.IS_SWAMP);
            if (inSwamp) addSwampPotency(context);
            else removeSwampPotency(context);
        }
    }

    @Override
    public void onBondUp(FamiliarContext context, int oldLevel, int newLevel) {
        super.onBondUp(context, oldLevel, newLevel);

        int magmaLevel = SBAffinities.MAGMA_DIGESTION.getRequiredBond();
        if (oldLevel < magmaLevel && newLevel >= magmaLevel) {
            addMagmaDigestionEvent(context);
        }

        int slimeyLevel = SBAffinities.SLIMEY_EXPULSION.getRequiredBond();
        if (oldLevel < slimeyLevel && newLevel >= slimeyLevel) {
            addSlimeyExpulsionEvent(context);
        }
    }

    @Override
    public void onRebirth(FamiliarContext context, int rebirths) {
        super.onRebirth(context, rebirths);
        removeEventListener(context, SBAffinities.MAGMA_DIGESTION.location());
        removeEventListener(context, SBAffinities.SLIMEY_EXPULSION.location());
        removeSwampPotency(context);
    }

    @Override
    public void onRemove(FamiliarContext context, BlockPos removePos) {
        super.onRemove(context, removePos);
        removeEventListener(context, SBAffinities.MAGMA_DIGESTION.location());
        removeEventListener(context, SBAffinities.SLIMEY_EXPULSION.location());
        removeSwampPotency(context);
    }

    private void addSwampPotency(FamiliarContext context) {
        if (hasSwampBuff) return;
        hasSwampBuff = true;
        addSkillBuff(
                context.getOwner(),
                SBAffinities.MURKY_HABITAT,
                BuffCategory.BENEFICIAL,
                SkillBuff.SPELL_MODIFIER,
                SpellModifier.MURKY_HABITAT
        );
    }

    private void removeSwampPotency(FamiliarContext context) {
        if (!hasSwampBuff) return;
        hasSwampBuff = false;
        removeSkillBuff(context.getOwner(), SBAffinities.MURKY_HABITAT);
    }

    private void addMagmaDigestionEvent(FamiliarContext context) {
        addEventListener(
                context,
                SpellEventListener.Events.ENTITY_KILL,
                SBAffinities.MAGMA_DIGESTION.location(),
                this::magmaDigestion);
    }

    private void magmaDigestion(DeathEvent event) {
        LivingDeathEvent deathEvent = event.getDeathEvent();

        boolean flag = (SpellUtil.isSummon(event.getCaster()) && SpellUtil.getOwner(event.getCaster()).is(this.getOwner()))
                || event.getCaster().is(getOwner());

        if (flag && getRandom().nextInt(100) < 30) {
            deathEvent.getEntity().spawnAtLocation(
                    FROG_LIGHTS.get(getRandom().nextInt(FROG_LIGHTS.size())).asItem().getDefaultInstance()
            );
        }
    }

    private void addSlimeyExpulsionEvent(FamiliarContext context) {
        addEventListener(context,
                SpellEventListener.Events.POST_DAMAGE,
                SBAffinities.SLIMEY_EXPULSION.location(),
                this::slimeyExpulsion);
    }

    private void slimeyExpulsion(DamageEvent.Post post) {
        if (post.isCancelled()) return;
        if (post.getSource().getEntity().is(this.getOwner())) {
            post.getEntity().addEffect(
                    new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20)
            );
        }
    }


}
