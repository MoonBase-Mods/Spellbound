package com.ombremoon.spellbound.common.world.block;

import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.world.block.entity.IceSheetBlockEntity;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class IceSheetBlock extends BaseEntityBlock {
    public static final MapCodec<IceSheetBlock> CODEC = simpleCodec(IceSheetBlock::new);
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public IceSheetBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        level.scheduleTick(pos, this, 60);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof IceSheetBlockEntity iceSheetBlockEntity && iceSheetBlockEntity.getOwner() != null) {
            level.removeBlock(pos, false);
        } else {
            level.removeBlockEntity(pos);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        BlockEntity entity = level.getBlockEntity(pos);


        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof IceSheetBlockEntity iceSheetBlockEntity) {
            iceSheetBlockEntity.stepOn(level, pos, state, entity);
        } else {
            entity.setIsInPowderSnow(true);
        }
    }

    @Override
    public float getFriction(BlockState state, LevelReader levelReader, BlockPos pos, @Nullable Entity entity) {
        if (entity == null)
            return super.getFriction(state, levelReader, pos, entity);

        Level level = entity.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof IceSheetBlockEntity iceSheetBlockEntity && iceSheetBlockEntity.getOwner() != null) {
            Player player = level.getPlayerByUUID(iceSheetBlockEntity.getOwner());
            if (player != null) {
                var skills = SpellUtil.getSkills(player);
                if (entity.is(player) || skills.hasSkill(SBSkills.SNOW_BOOTS) && SpellUtil.IS_ALLIED.test(entity, player)) {
                    return 0.6F;
                }
            }
        }

        return super.getFriction(state, levelReader, pos, entity);
    }

    @Override
    protected VoxelShape getShape(BlockState p_152917_, BlockGetter p_152918_, BlockPos p_152919_, CollisionContext p_152920_) {
        return SHAPE;
    }

    @Override
    protected BlockState updateShape(
            BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor level, BlockPos pos, BlockPos pos1
    ) {
        return !blockState.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(blockState, direction, blockState1, level, pos, pos1);
    }

    @Override
    protected boolean canSurvive(BlockState p_152922_, LevelReader p_152923_, BlockPos p_152924_) {
        return !p_152923_.isEmptyBlock(p_152924_.below()) && !p_152923_.getBlockState(p_152924_.below()).is(this);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IceSheetBlockEntity(pos, state);
    }
}
