package com.ombremoon.spellbound.common.world.block;

import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.init.SBBlockEntities;
import com.ombremoon.spellbound.common.init.SBStats;
import com.ombremoon.spellbound.common.world.block.entity.DarkAltarBlockEntity;
import com.ombremoon.spellbound.common.world.recipe.RuneCraftingRecipe;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DarkAltarBlock extends BaseEntityBlock {
    public DarkAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof DarkAltarBlockEntity darkAltar) {
            ItemStack itemStack = player.getItemInHand(hand);
            Optional<Integer> cornerIndex = getCornerIndex(hitResult.getLocation(), pos);
            if (cornerIndex.isPresent()) {
                if (!level.isClientSide && darkAltar.placeOrTakeItem(player, pos, itemStack, cornerIndex.get())) {
                    player.awardStat(SBStats.INTERACT_WITH_DARK_ALTAR.get());
                    return ItemInteractionResult.SUCCESS;
                }

                return ItemInteractionResult.CONSUME;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private Optional<Integer> getCornerIndex(Vec3 location, BlockPos pos) {
        double relX = location.x - pos.getX();
        double relY = location.y - pos.getY();
        double relZ = location.z - pos.getZ();

        if (relY < 0.9 || relY > 1.1)
            return Optional.empty();

        if (relX >= 0.7 && relX <= 1.0 && relZ >= 0.7 && relZ <= 1.0)
            return Optional.of(0);

        if (relX >= 0.0 && relX <= 0.3 && relZ >= 0.7 && relZ <= 1.0)
            return Optional.of(1);

        if (relX >= 0.0 && relX <= 0.3 && relZ >= 0.0 && relZ <= 0.3)
            return Optional.of(2);

        if (relX >= 0.7 && relX <= 1.0 && relZ >= 0.0 && relZ <= 0.3)
            return Optional.of(3);

        if (relX >= 0.3 && relX <= 0.7 && relZ >= 0.3 && relZ <= 0.7) {
            return Optional.of(-1);
        }

        return Optional.empty();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return InteractionResult.SUCCESS_NO_ITEM_USED;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DarkAltarBlockEntity darkAltar) {
                Containers.dropContents(level, pos, darkAltar.getItems());
                Containers.dropContents(level, pos, NonNullList.of(ItemStack.EMPTY, darkAltar.chalk));
            }

            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DarkAltarBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (!level.isClientSide) {
            return createTickerHelper(blockEntityType, SBBlockEntities.DARK_ALTAR.get(), DarkAltarBlockEntity::serverTick);
        } else {
            return createTickerHelper(blockEntityType, SBBlockEntities.DARK_ALTAR.get(), DarkAltarBlockEntity::itemAnimationTick);
        }
    }
}
