package com.ombremoon.spellbound.common.world.block;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.world.block.entity.SimpleExtendedBlockEntity;
import com.ombremoon.spellbound.common.init.SBTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class DivineShrineBlock extends AbstractExtendedBlock implements PreviewableExtendedBlock {
    public static final VoxelShape SHAPE = makeShape();

    public DivineShrineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Stream<BlockPos> fullBlockShape(@Nullable Direction direction, BlockPos center) {
        return Stream.of(center, center.above());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SimpleExtendedBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1f;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return voxelShapeHelper(state, level ,pos , SHAPE, 0, 1 , 0);
    }

    public static VoxelShape makeShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.1875, -0.25, 0.1875, 0.8125, 0.25, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, -0.25, 0.3125, 0.1875, 0, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, -0.25, 0.3125, 1, 0, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, -0.25, 0, 0.6875, 0, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, -0.25, 0.8125, 0.6875, 0, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, -1, 0, 1, -0.25, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.25, 0, 1, 1.25, 1), BooleanOp.OR);

        return shape;
    }

    @Nullable
    public static Pair<BlockPos, BlockState> getNearestShrine(Player player) {
        return getNearestShrine(player.level(), player.getOnPos());
    }

    @Nullable
    public static Pair<BlockPos, BlockState> getNearestShrine(Level level, BlockPos blockPos) {
        for (BlockPos pos : BlockPos.betweenClosed(blockPos.subtract(new Vec3i(7, 7, 7)), blockPos.offset(new Vec3i(7, 7, 7)))) {
            BlockState state = level.getBlockState(pos);

            if (state.is(SBTags.Blocks.DIVINE_SHRINE))
                return Pair.of(pos, state);
        }

        return null;
    }
}
