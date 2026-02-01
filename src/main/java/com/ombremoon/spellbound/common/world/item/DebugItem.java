package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.EffectManager;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.world.entity.projectile.MushroomProjectile;
import com.ombremoon.spellbound.common.world.spell.ruin.shock.StormRiftSpell;
import com.ombremoon.spellbound.common.world.spell.transfiguration.StrideSpell;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.main.Keys;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.Loggable;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class DebugItem extends Item implements Loggable {
    public DebugItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var handler = SpellUtil.getSpellHandler(player);
        var skillHandler = SpellUtil.getSkills(player);
        duckDebug(level, player, usedHand, handler, skillHandler);
        ombreDebug(level, player, usedHand, handler, skillHandler);
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Player player = context.getPlayer();
        if (!level.isClientSide) {
            log(level.getBlockState(blockPos).getShape(level, blockPos).max(Direction.Axis.Y));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void ombreDebug(Level level, Player player, InteractionHand usedHand, SpellHandler spellHandler, SkillHolder skillHolder) {
        List<ArmorStand> entities = level.getEntitiesOfClass(ArmorStand.class, player.getBoundingBox().inflate(0.5));
        log(entities);
        for (ArmorStand stand : entities) {
            stand.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(SBItems.CREATIONIST_STAFF.get()));
        }
    }

    private void duckDebug(Level level, Player player, InteractionHand hand, SpellHandler spellHandler, SkillHolder skillHolder) {
        var handler = SpellUtil.getFamiliarHandler(player);
        handler.awardBond(SBFamiliars.FROG, handler.getMaxXPForFamiliar(SBFamiliars.FROG));
        player.sendSystemMessage(handler.selectFamiliar(SBFamiliars.FROG) ? Component.literal("Selected frog") : Component.literal("Failed to set familiar"));
    }
}
