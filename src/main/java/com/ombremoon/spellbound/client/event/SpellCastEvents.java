package com.ombremoon.spellbound.client.event;

import com.ombremoon.spellbound.client.AnimationHelper;
import com.ombremoon.spellbound.client.KeyBinds;
import com.ombremoon.spellbound.common.magic.EffectManager;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.ChargeableSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class SpellCastEvents {
    private static boolean wasCastKeyPressed = false;

    @SubscribeEvent
    public static void onSpellMode(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isCanceled()) return;

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (EffectManager.isStunned(player)) {
                event.setSwingHand(false);
                event.setCanceled(true);
                return;
            }

            var handler = SpellUtil.getSpellHandler(player);
            var spellType = handler.getSelectedSpell();

            if (spellType == null) return;

            if (handler.inCastMode()) {
                if (KeyBinds.getSpellCastMapping().isDown()) {
                    event.setSwingHand(false);
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void chargeOrChannelSpell(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof AbstractClientPlayer player)) return;

        AnimationHelper.tick(player);
        var handler = SpellUtil.getSpellHandler(player);
        var skills = SpellUtil.getSkills(player);

        if (!isAbleToSpellCast()) return;
        if (!handler.inCastMode()) return;

        var spellType = handler.getSelectedSpell();
        if (spellType == null) return;

        AbstractSpell spell = handler.getCurrentlyCastSpell();
        if (spell != null) {
            if (handler.castTick > 0 && !SpellUtil.canCastSpell(player, spell)) {
                SpellContext spellContext = createContext(player, spell);
                spell.resetCast(handler, spellContext);
            }

            boolean isCastKeyPressed = KeyBinds.getSpellCastMapping().isDown();
            boolean castKeyJustPressed = isCastKeyPressed && !wasCastKeyPressed;

            if (castKeyJustPressed) {
                if (canChargeSpell(skills, spell)) {
                    if (handler.isChargingOrChannelling()) {
                        stopChargeOrChannel(player, handler, spell, true);
                    } else if (handler.castTick == 0) {
                        startCasting(player, handler, spell);
                        handler.setChargingOrChannelling(true);
                        PayloadHandler.setChargeOrChannel(true);
                    }
                } else if (handler.isChargingOrChannelling()) {
                    stopChargeOrChannel(player, handler, spell, false);
                } else if (handler.castTick == 0) {
                    startCasting(player, handler, spell);
                }
            }

            if (handler.castTick > 0) {
                int castTime = spell.getCastTime();
                if (spell instanceof ChargeableSpell chargeable && handler.isChargingOrChannelling()) {
                    int maxCharges = chargeable.maxCharges();
                    int ticksPerCharge = castTime / maxCharges;
                    if (handler.castTick % ticksPerCharge == 0 && spell.getCharges() < maxCharges) {
                        spell.incrementCharges();
                    }

                    if (spell.getCharges() >= maxCharges) {
                        stopChargeOrChannel(player, handler, spell, true);
                    } else {
                        handler.castTick++;
                    }
                } else if (!handler.isChargingOrChannelling()) {
                    if (handler.castTick >= castTime) {
                        castSpell(player, spell.getCharges());
                        handler.castTick = 0;
                    } else {
                        handler.castTick++;
                    }
                }
            }

            wasCastKeyPressed = isCastKeyPressed;
        } else {
            spell = spellType.createSpellWithData(player);
            handler.setCurrentlyCastingSpell(spell);
            createContext(player, spell);
        }
    }

    public static SpellContext createContext(Player player, AbstractSpell spell) {
        SpellContext prevContext = spell.getContext();
        SpellContext context = new SpellContext(spell.spellType(), prevContext.getCaster(), spell.getTargetEntity(player, SpellUtil.getCastRange(player)), prevContext.isRecast());

        spell.setCastContext(context);
        PayloadHandler.setCastingSpell(spell.spellType(), context);
        return context;
    }

    private static boolean canChargeSpell(SkillHolder skills, AbstractSpell spell) {
        return spell instanceof ChargeableSpell chargeable && chargeable.canCharge((skill, choice) -> skills.hasSkill(skill) && (choice == null || skills.getChoice(spell.spellType()) == choice));
    }

    private static void startCasting(Player player, SpellHandler handler, AbstractSpell spell) {
        SpellContext spellContext = createContext(player, spell);
        spell.onCastStart(spellContext);
        PayloadHandler.castStart();
        handler.castTick = 1;
        spell.resetCharges();
    }

    private static void stopChargeOrChannel(Player player, SpellHandler handler, AbstractSpell spell, boolean castSpell) {
        handler.setChargingOrChannelling(false);
        PayloadHandler.setChargeOrChannel(false);
        handler.castTick = 0;
        if (castSpell) {
            castSpell(player, spell.getCharges());
            spell.resetCharges();
        } else {
            PayloadHandler.stopChannel(spell.spellType());
        }
    }

    private static boolean isAbleToSpellCast() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getOverlay() != null) return false;
        if (minecraft.screen != null) return false;
        if (!minecraft.mouseHandler.isMouseGrabbed()) return false;
        if (EffectManager.isSilenced(minecraft.player)) return false;
        return minecraft.isWindowActive();
    }

    public static void castSpell(Player player, int charges) {
        if (player.isSpectator()) return;
        PayloadHandler.castSpell(charges);
    }
}
