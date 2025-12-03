package com.ombremoon.spellbound.common.world.block;

import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.world.block.entity.PedestalBlockEntity;
import com.ombremoon.spellbound.common.world.item.RitualTalismanItem;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.common.world.multiblock.MultiblockManager;
import com.ombremoon.spellbound.common.world.multiblock.MultiblockPart;
import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TransfigurationPedestalBlock extends BaseEntityBlock {
    public static final MapCodec<TransfigurationPedestalBlock> CODEC = simpleCodec(TransfigurationPedestalBlock::new);
    public static final List<BlockPos> FIRST_RUNE_OFFSETS = BlockPos.betweenClosedStream(-3, 0, -3, 3, 0, 3)
            .filter(blockPos -> Math.abs(blockPos.getX()) == 3 || Math.abs(blockPos.getZ()) == 3)
            .map(BlockPos::immutable)
            .toList();
    public static final List<BlockPos> SECOND_RUNE_OFFSETS = BlockPos.betweenClosedStream(-5, 0, -5, 5, 1, 5)
            .filter(blockPos -> (Math.abs(blockPos.getX()) == 5 || Math.abs(blockPos.getZ()) == 5) && Math.abs(blockPos.getY()) == 1)
            .map(BlockPos::immutable)
            .toList();
    public static final List<BlockPos> THIRD_RUNE_OFFSETS = BlockPos.betweenClosedStream(-7, 0, -7, 7, 2, 7)
            .filter(blockPos -> (Math.abs(blockPos.getX()) == 7 || Math.abs(blockPos.getZ()) == 7) && Math.abs(blockPos.getY()) == 2)
            .map(BlockPos::immutable)
            .toList();

    public TransfigurationPedestalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof MultiblockPart part) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.is(SBItems.RITUAL_TALISMAN.get()) && !part.isAssigned()) {
                Integer rings = itemStack.get(SBData.TALISMAN_RINGS);
                if (!level.isClientSide && rings != null) {
                    RitualTalismanItem.Rings multiblockRings = RitualTalismanItem.Rings.values()[rings - 1];
                    var holder = MultiblockManager.byKey(getCircleLocation(multiblockRings));
                    if (holder != null) {
                        Multiblock multiblock = holder.value();
                        if (multiblock.tryCreateMultiblock(level, player, pos, Direction.NORTH)) {
                            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                            return ItemInteractionResult.SUCCESS;
                        }
                    }
                }

                return ItemInteractionResult.CONSUME;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static ResourceLocation getCircleLocation(RitualTalismanItem.Rings rings) {
        return CommonClass.customLocation(rings.getName() + "_ring");
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalBlockEntity(pos, state);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof MultiblockPart part && part.getMultiblock() instanceof TransfigurationMultiblock multiblock) {
            List<BlockPos> offsets;
            switch (multiblock.getRings()) {
                case 2 -> offsets = SECOND_RUNE_OFFSETS;
                case 3 -> offsets = THIRD_RUNE_OFFSETS;
                default -> offsets = FIRST_RUNE_OFFSETS;
            }

            for (BlockPos blockPos : offsets) {
                if (random.nextInt(4) == 0 && isValidRune(level, pos, blockPos)) {
                    level.addParticle(
                        ParticleTypes.ENCHANT,
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 2.0,
                        (double)pos.getZ() + 0.5,
                        (double)((float)blockPos.getX() + random.nextFloat()) - 0.5,
                        ((float)blockPos.getY() - random.nextFloat() - 1.0F),
                        (double)((float)blockPos.getZ() + random.nextFloat()) - 0.5
                    );
                }
            }

            level.addParticle(
                    ParticleTypes.END_ROD,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + 1.15,
                    pos.getZ() + random.nextDouble(),
                    random.nextGaussian() * 0.005,
                    random.nextGaussian() * 0.005,
                    random.nextGaussian() * 0.005
            );
        }
    }

    private boolean isValidRune(Level level, BlockPos pos, BlockPos blockPos) {
        return level.getBlockState(pos.offset(blockPos)).is(SBBlocks.RUNE.get());
    }
}
