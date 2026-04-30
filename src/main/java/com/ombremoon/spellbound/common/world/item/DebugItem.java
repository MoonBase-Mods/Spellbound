package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.EffectManager;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleDungeonData;
import com.ombremoon.spellbound.common.magic.api.buff.BuffCategory;
import com.ombremoon.spellbound.common.magic.effects.EffectHolder;
import com.ombremoon.spellbound.common.magic.effects.TickProvider;
import com.ombremoon.spellbound.common.magic.effects.types.DamageEntity;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.world.entity.projectile.MushroomProjectile;
import com.ombremoon.spellbound.common.world.entity.spell.CursedRune;
import com.ombremoon.spellbound.common.world.spell.ruin.shock.StormRiftSpell;
import com.ombremoon.spellbound.common.world.spell.transfiguration.StrideSpell;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.main.Keys;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.Loggable;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DebugItem extends Item implements Loggable {
    public DebugItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!CommonClass.isDevEnv())
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));

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
//        if (!CommonClass.isDevEnv())
//             return InteractionResult.FAIL;

        BlockPos blockPos = context.getClickedPos();
        if (!level.isClientSide) {
            CursedRune rune = new CursedRune(SBEntities.CURSED_RUNE.get(), level);
            rune.setPos(Vec3.atBottomCenterOf(blockPos.above()));
            rune.setHidden(true);
            rune.setRuneEffects(
                    List.of(
                            EffectHolder.simple(
                                    new DamageEntity(SBDamageTypes.SB_GENERIC, 2),
                                    Optional.empty(),
                                    new TickProvider.AtTick(0),
                                    BuffCategory.HARMFUL,
                                    1
                            )
                    )
            );
            level.addFreshEntity(rune);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void ombreDebug(Level level, Player player, InteractionHand usedHand, SpellHandler spellHandler, SkillHolder skillHolder) {
        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(SBItems.DUNGEON_KEY.get());
            stack.set(SBData.PUZZLE, SBPuzzleConfigs.NIGHTBLADE);
            stack.set(SBData.ENCRYPTED_KEY, true);
            player.setInvisible(false);
        }
    }

    private void duckDebug(Level level, Player player, InteractionHand hand, SpellHandler spellHandler, SkillHolder skillHolder) {
        var handler = SpellUtil.getFamiliarHandler(player);
        handler.awardBond(SBFamiliars.CAT, handler.getMaxXPForFamiliar(SBFamiliars.CAT));
        player.sendSystemMessage(handler.selectFamiliar(SBFamiliars.CAT) ? Component.literal("Selected cat") : Component.literal("Failed to set familiar"));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Mysterious").withStyle(ChatFormatting.OBFUSCATED);
    }
}
