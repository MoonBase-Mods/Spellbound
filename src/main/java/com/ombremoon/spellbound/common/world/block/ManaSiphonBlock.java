package com.ombremoon.spellbound.common.world.block;

import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.init.SBBlockEntities;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.world.block.entity.ManaSiphonBlockEntity;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ManaSiphonBlock extends BaseEntityBlock {
    public static final MapCodec<ManaSiphonBlock> CODEC = simpleCodec(ManaSiphonBlock::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public ManaSiphonBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVE, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof ManaSiphonBlockEntity siphon) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (!level.isClientSide) {
                if (itemStack.is(SBItems.MAGIC_ESSENCE.get()) && !state.getValue(ACTIVE)) {
                    level.setBlock(pos, state.setValue(ACTIVE, true), 3);
                    siphon.activateSiphon(player);
                    return ItemInteractionResult.SUCCESS;
                } else if (itemStack.is(SBItems.MANA_BOTTLE.get()) && siphon.canSiphonMana()) {
                    siphon.siphonMana(10);
                    itemStack.consume(1, player);
                    return ItemInteractionResult.SUCCESS;
                } else if (itemStack.is(SBItems.MANA_TEAR.get()) && siphon.increaseManaStorage(100)) {
                    itemStack.consume(1, player);
                    return ItemInteractionResult.SUCCESS;
                } else if (itemStack.is(Items.GLASS_BOTTLE) && siphon.getStoredMana() >= 10) {
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(SBItems.MANA_BOTTLE.get())));
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof ManaSiphonBlockEntity siphon) {
            if (!level.isClientSide && siphon.getStoredMana() > 0) {
                var handler = SpellUtil.getSpellHandler(player);
                float siphonedMana = (float) Math.min(handler.getMaxMana() - handler.getMana(), siphon.getStoredMana());
                handler.awardMana(siphonedMana);
                siphon.siphonMana(-siphonedMana);

                if (siphonedMana >= siphon.getMaxMana() / 2 && player instanceof ServerPlayer serverPlayer) {
                    SBTriggers.USE_HALF_FILLED_SIPHON.get().trigger(serverPlayer);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ManaSiphonBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, SBBlockEntities.MANA_SIPHON.get(), ManaSiphonBlockEntity::siphonManaTick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
