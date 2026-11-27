package com.ombremoon.spellbound.common.events;

import com.ombremoon.spellbound.common.events.custom.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.ChanneledSpell;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

public class EventFactory {

    public static void onEffectAdded(LivingEntity entity, MobEffectInstance oldEffectInstance, MobEffectInstance newEffectInstance, Entity source) {
        NeoForge.EVENT_BUS.post(new MobEffectEvent.Added(entity, oldEffectInstance, newEffectInstance, source));
    }

    public static void onEffectRemoved(LivingEntity entity, MobEffectInstance effectInstance) {
        NeoForge.EVENT_BUS.post(new MobEffectEvent.Remove(entity, effectInstance));
    }

    public static InteractionResult onMobInteractPre(Player player, Mob mob, InteractionHand hand) {
        MobInteractEvent.Pre pre = new MobInteractEvent.Pre(player, hand, mob);
        NeoForge.EVENT_BUS.post(pre);
        return pre.isCanceled() ? pre.getResult() : null;
    }

    public static InteractionResult onMobInteractPost(Player player, Mob mob, InteractionHand hand) {
        MobInteractEvent.Post post = new MobInteractEvent.Post(player, hand, mob);
        NeoForge.EVENT_BUS.post(post);
        return post.isCanceled() ? post.getResult() : null;
    }

    public static AnimatedSpell.Builder<?> getAnimatedBuilder(SpellType<?> spellType, AnimatedSpell.Builder<?> builder) {
        BuildSpellEvent.Animated event = new BuildSpellEvent.Animated(spellType, builder);
        NeoForge.EVENT_BUS.post(event);
        return event.getBuilder();
    }

    public static ChanneledSpell.Builder<?> getChanneledBuilder(SpellType<?> spellType, ChanneledSpell.Builder<?> builder) {
        BuildSpellEvent.Channeled event = new BuildSpellEvent.Channeled(spellType, builder);
        NeoForge.EVENT_BUS.post(event);
        return event.getBuilder();
    }

    public static void onSpellCast(LivingEntity caster, AbstractSpell spell, SpellContext context) {
        NeoForge.EVENT_BUS.post(new SpellCastEvent(caster, spell, context));
    }

    public static boolean onLivingDodge(LivingEntity caster, DamageContainer container) {
        return !NeoForge.EVENT_BUS.post(new LivingDodgeEvent(caster, container)).isCanceled();
    }
}
