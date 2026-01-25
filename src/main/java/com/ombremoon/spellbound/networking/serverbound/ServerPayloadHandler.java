package com.ombremoon.spellbound.networking.serverbound;

import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    public static void handleNetworkSwitchMode(final SwitchModePayload payload, final IPayloadContext context) {
        var handler = SpellUtil.getSpellHandler(context.player());
        handler.switchMode();
    }

    public static void handleNetworkCastSpell(final CastSpellPayload payload, final IPayloadContext context) {
        var level = context.player().level();
        if (!level.isClientSide) {
            var handler = SpellUtil.getSpellHandler(context.player());
            AbstractSpell spell = handler.getCurrentlyCastSpell();
            spell.setCharges(payload.charges());
            spell.castSpell(context.player());
        }
    }

    public static void handleNetworkSetSpell(final SetSpellPayload payload, final IPayloadContext context) {
        var handler = SpellUtil.getSpellHandler(context.player());
        handler.setSelectedSpell(payload.spellType());
    }

    public static void handleNetworkEquipSpell(final EquipSpellPayload payload, final IPayloadContext context) {
        var handler = SpellUtil.getSpellHandler(context.player());
        if (payload.equip()) {
            handler.equipSpell(payload.spellType());
        } else {
            handler.unequipSpell(payload.spellType());
        }
    }

    public static void handleNetworkSetCastSpell(final SetCastingSpellPayload payload, final IPayloadContext context) {
        Level level = context.player().level();
        var spellContext = new SpellContext(payload.spellType(), context.player(), level.getEntity(payload.targetID()), payload.isRecast());
        AbstractSpell spell = payload.spellType().createSpell();
        var handler = SpellUtil.getSpellHandler(context.player());
        spell.setCastContext(spellContext);
        handler.setCurrentlyCastingSpell(spell);
    }

    public static void handleNetworkCastStart(final CastStartPayload payload, final IPayloadContext context) {
        var handler = SpellUtil.getSpellHandler(context.player());
        AbstractSpell spell = handler.getCurrentlyCastSpell();
        if (spell != null)
            spell.onCastStart(spell.getCastContext());
    }

    public static void handleNetworkCastReset(final CastResetPayload payload, final IPayloadContext context) {
        var handler = SpellUtil.getSpellHandler(context.player());
        AbstractSpell spell = handler.getCurrentlyCastSpell();
        if (spell != null)
            spell.onCastReset(spell.getCastContext());
    }

    public static void handleNetworkStopChanneledSpell(final StopChanneledSpellPayload payload, final IPayloadContext context) {
        var handler = SpellUtil.getSpellHandler(context.player());
        AbstractSpell spell = handler.getSpell(payload.spellType());
        if (spell != null)
            spell.endSpell();
    }

    public static void handleNetworkUpdateChoice(final UpdateChoicePayload payload, final IPayloadContext context) {
        var skills = SpellUtil.getSkills(context.player());
        skills.setChoice(payload.spellType(), payload.skill());
    }

    public static void handleNetworkChargeOrChannel(final ChargeOrChannelPayload payload, final IPayloadContext context) {
        var handler = SpellUtil.getSpellHandler(context.player());
        handler.setChargingOrChannelling(payload.isChargingOrChannelling());
    }

    public static void handleNetworkUnlockSkill(final UnlockSkillPayload payload, final IPayloadContext context) {
        var holder = SpellUtil.getSkills(context.player());
        holder.unlockSkill(payload.skill(), true);
        context.player().sendSystemMessage(Component.literal("You have unlocked the " + payload.skill().getName().getString() + " skill"));
    }

    public static void handleNetworkPlayerMovement(final PlayerMovementPayload payload, final IPayloadContext context) {
        Player player = context.player();
        var handler = SpellUtil.getSpellHandler(player);
        if (payload.movement() == PlayerMovementPayload.Movement.MOVE) {
            handler.forwardImpulse = payload.forwardImpulse();
            handler.leftImpulse = payload.leftImpulse();
        } else if (payload.movement() == PlayerMovementPayload.Movement.ROTATE) {
            player.setYBodyRot(payload.yRot());
        }
    }
}
