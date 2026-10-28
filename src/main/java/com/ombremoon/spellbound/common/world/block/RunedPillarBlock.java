package com.ombremoon.spellbound.common.world.block;

import com.ombremoon.spellbound.common.init.SBBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluids;

public class RunedPillarBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public RunedPillarBlock(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState((BlockState)this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        BlockPos connectedPillar = getConnectedPillar(state, pos);
        if (level.getBlockState(connectedPillar).is(state.getBlock())) return;

        level.setBlock(connectedPillar, state.setValue(HALF, state.getValue(HALF).getOtherHalf()).setValue(FACING, state.getValue(FACING).getOpposite()), 3);
    }

    public Direction getPillarDirection(BlockState state) {
        Direction.Axis axis = state.getValue(AXIS);
        Direction facing = state.getValue(FACING);

        if (axis.equals(Direction.Axis.Y))
            return state.getValue(HALF).equals(DoubleBlockHalf.LOWER) ? Direction.UP : Direction.DOWN;

        return facing;
    }

    public BlockPos getConnectedPillar(BlockState state, BlockPos pos) {
        Direction direction = getPillarDirection(state);

        return pos.relative(direction);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!getConnectedPillar(state, pos).equals(neighborPos)) return;
        BlockState nextState = level.getBlockState(neighborPos);
        if (nextState.is(state.getBlock())) return;
        level.destroyBlock(pos, false);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos))) {
            DoubleBlockHalf doubleblockhalf = (DoubleBlockHalf)state.getValue(HALF);
            if (doubleblockhalf == DoubleBlockHalf.UPPER) {
                BlockPos blockpos = pos.below();
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                    level.setBlock(blockpos, blockstate1, 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
//        BlockPos blockpos = pos.below();
//        BlockState blockstate = level.getBlockState(blockpos);
//        return state.getValue(HALF) == DoubleBlockHalf.LOWER || blockstate.is(this);
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS, FACING, HALF});
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) direction = Direction.NORTH;

        BlockState toPlace = this.defaultBlockState()
                .setValue(AXIS, context.getClickedFace().getAxis())
                .setValue(FACING, direction);
        if (context.getClickedFace().equals(Direction.DOWN)) toPlace = toPlace.setValue(HALF, DoubleBlockHalf.UPPER);

        BlockPos connected = getConnectedPillar(toPlace, pos);
        BlockState connectedState = context.getLevel().getBlockState(connected);
        if (!connectedState.canBeReplaced()) return null;

        return toPlace;
    }
}
