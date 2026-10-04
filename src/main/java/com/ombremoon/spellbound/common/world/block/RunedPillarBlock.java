package com.ombremoon.spellbound.common.world.block;

import com.ombremoon.spellbound.common.init.SBBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;

public class RunedPillarBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public RunedPillarBlock(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState((BlockState)this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(FACING, Direction.NORTH)
                .setValue(UP, true)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (state.getValue(HALF).equals(DoubleBlockHalf.LOWER)) {
            BlockPos upperPos;
            if (!state.getValue(UP)) upperPos = pos.below();
            else upperPos = pos.relative(state.getValue(AXIS), 1);

            BlockState upperHalf = state.setValue(HALF, DoubleBlockHalf.UPPER);
            level.setBlock(upperPos, upperHalf, 3);
        }
    }

    public BlockPos getConnectedPillar(BlockState state, BlockPos pos) {
        if (state.getValue(HALF).equals(DoubleBlockHalf.LOWER)) {
            return state.getValue(UP) ? pos.relative(state.getValue(AXIS), 1) : pos.below();
        } else {
            return state.getValue(UP) ? pos.relative(state.getValue(AXIS), -1) : pos.above();
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!getConnectedPillar(state, pos).equals(neighborPos)) return;
        BlockState nextState = level.getBlockState(neighborPos);
        if (!nextState.is(state.getBlock())) return;
        if (!nextState.getValue(HALF).getOtherHalf().equals(state.getValue(HALF))) {
            level.destroyBlock(pos, state.getValue(HALF).equals(DoubleBlockHalf.LOWER));
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER || blockstate.is(this);
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS, FACING, UP, HALF});
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)this.defaultBlockState()
                .setValue(AXIS, context.getClickedFace().getAxis())
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(UP, context.getClickedFace() != Direction.DOWN);
    }
}
