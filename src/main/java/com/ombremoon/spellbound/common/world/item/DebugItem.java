package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.world.entity.projectile.MushroomProjectile;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.Loggable;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

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
        if (!level.isClientSide) {
            MushroomProjectile projectile = new MushroomProjectile(level, player);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
            level.addFreshEntity(projectile);
//            ((EntityBasedBossFight.Instance)data.getCurrentBossFight()).initializeWinCondition((ServerLevel) level, ((EntityBasedBossFight.Instance) data.getCurrentBossFight()).getBossFight());
        } else {
//            SBShaders.HEAT_DISTORTION_SHADER.toggleShader();

        }
    }

    private void duckDebug(Level level, Player player, InteractionHand hand, SpellHandler spellHandler, SkillHolder skillHolder) {
        for (ResourceLocation loc : player.getData(SBData.BOOK_SCRAPS)) {
            Constants.LOG.debug(loc.toString());
        }
    }
}
