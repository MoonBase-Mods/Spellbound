package com.ombremoon.spellbound.common.world.block;

import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.world.block.entity.SimpleExtendedBlockEntity;
import com.ombremoon.spellbound.common.init.SBStats;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class UnnamedWorkbenchBlock extends AbstractExtendedBlock implements PreviewableExtendedBlock {
    public static final MapCodec<UnnamedWorkbenchBlock> CODEC = simpleCodec(UnnamedWorkbenchBlock::new);
    public static final EnumProperty<WorkbenchPart> PART = EnumProperty.create("workbench", WorkbenchPart.class);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape SHAPE_NORTH = makeShapeNorth();
    public static final VoxelShape SHAPE_SOUTH = makeShapeSouth();
    public static final VoxelShape SHAPE_WEST = makeShapeWest();
    public static final VoxelShape SHAPE_EAST = makeShapeEast();

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public UnnamedWorkbenchBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PART, WorkbenchPart.LEFT));
    }

    @Override
    public @Nullable DirectionProperty getDirectionProperty() {
        return FACING;
    }

    @Override
    public BlockState getDefaultStateForPreviews(Direction direction) {
        return PreviewableExtendedBlock.super.getDefaultStateForPreviews(direction).setValue(FACING, direction.getClockWise());
    }

    @Override
    public @Nullable BiFunction<BlockState, BlockPos, BlockState> getStateFromOffset() {
        return ((blockState, pos) -> blockState.setValue(PART, WorkbenchPart.fromOffset(pos, getDirection(blockState))));
    }

    @Override
    public Stream<BlockPos> fullBlockShape(@Nullable Direction direction, BlockPos center) {
        return BlockPos.betweenClosedStream(center, center.relative(direction).above());
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateForPlacementHelper(context, context.getHorizontalDirection().getClockWise());
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SimpleExtendedBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            RenderUtil.openWorkbench();
            return InteractionResult.SUCCESS;
        } else {
            player.awardStat(SBStats.INTERACT_WITH_BENCH.get());
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    public enum WorkbenchPart implements StringRepresentable {
        LEFT("left", new BlockPos(0, 0, 0)),
        RIGHT("right",  new BlockPos(0, 0, -1)),
        TOP_LEFT("top_left",  new BlockPos(0, 1, 0)),
        TOP_RIGHT("top_right",  new BlockPos(0, 1, -1));

        private final String name;
        private final BlockPos offset;

        WorkbenchPart(String name, BlockPos offset) {
            this.name = name;
            this.offset = offset;
        }

        public static WorkbenchPart fromOffset(BlockPos offset, @Nullable Direction direction) {
            for (WorkbenchPart part : WorkbenchPart.values()){
               BlockPos offset1 = part.offset;
               if (direction != null) offset1 = offset1.rotate(ExtendedBlock.rotationFromDirection(direction));

               if (offset1.equals(offset)) return part;

           }
           return WorkbenchPart.LEFT;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
       VoxelShape shape = switch (getDirection(state)){
           case NORTH -> SHAPE_NORTH;
           case SOUTH -> SHAPE_SOUTH;
           case WEST -> SHAPE_WEST;
           case EAST -> SHAPE_EAST;
           case DOWN, UP -> Shapes.block();
       };

       return voxelShapeHelper(state, level, pos, shape);
    }

    public static VoxelShape makeShapeWest(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(-1, 0, 0, 1, 0.875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-1, 0.875, 0.875, 1, 1.75, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.8125, 0, 0, 0.8125, 0.625, 0.8125), BooleanOp.ONLY_FIRST);

        return shape;
    }

    public static VoxelShape makeShapeSouth(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.875, 2), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.875, 0.875, 0, 1, 1.75, 2), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.1875, 0.8125, 0.625, 1.8125), BooleanOp.ONLY_FIRST);

        return shape;
    }

    public static VoxelShape makeShapeEast(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 2, 0.875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.875, 0, 2, 1.75, 0.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.1875, 1.8125, 0.625, 1), BooleanOp.ONLY_FIRST);

        return shape;
    }


    public static VoxelShape makeShapeNorth(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, -1, 1, 0.875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.875, -1, 0.125, 1.75, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, -0.8125, 1, 0.625, 0.8125), BooleanOp.ONLY_FIRST);

        return shape;
    }

}
