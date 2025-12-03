package com.ombremoon.spellbound.common.world.block;

import com.ombremoon.spellbound.common.world.block.entity.ExtendedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.stream.Stream;

public interface ExtendedBlock {


    /*
   How to use:
   implement fullBlockShape and make it return the whole shape
   getDirectionProperty = for directions in fullBlockShape, null if there are none

   For placement:
   Override setPlacedBy - return place
   Override getStateForPlacement - return getStateForPlacementHelper

   For destroying:
   Override updateShape - return updateShapeHelper
   Override canSurvive - return canSurviveHelper
   Optionally Override extraSurviveRequirements
   Optionally add preventCreativeDrops into playerWillDestroy

   growHelper - for bone meal and tick growth
   voxelShapeHelper - pretty self-explanatory

   */

    Stream<BlockPos> fullBlockShape(@Nullable Direction direction, BlockPos center);

    default @Nullable DirectionProperty getDirectionProperty(){
        return null; // null if block doesn't have directions
    }

    default Direction getDirection(BlockState state){
        if (getDirectionProperty() != null){
            return state.getValue(getDirectionProperty());
        }
        throw new RuntimeException("Tried to get Direction, but DirectionProperty is null");
    }

    default Block getBlock(){
        if (this instanceof Block block){
            return block;
        } else {
            throw new RuntimeException(this.getClass().getSimpleName() + " is not implemented on a Block");
        }
    }

    default Stream<BlockPos> fullBlockShape(BlockPos center, @Nullable BlockState state){
        if (getDirectionProperty() == null || state == null)
            return fullBlockShape(null, center);

        return fullBlockShape(getDirection(state), center);
    }

    default @Nullable BiFunction<BlockState, BlockPos, BlockState> getStateFromOffset() {
        return null; // For use with json models, changes blockState based on offset from centre
    };

    default void place(Level level, BlockPos posOriginal, BlockState stateOriginal){
        fullBlockShape(posOriginal, stateOriginal).forEach(posNew -> {
            int flags = level.isClientSide ? 0 : 3;

            BlockState stateNew = stateOriginal.setValue(AbstractExtendedBlock.CENTER, posOriginal.equals(posNew));
            if (getStateFromOffset() != null) stateNew = getStateFromOffset().apply(stateNew, posNew.subtract(posOriginal));

            level.setBlock(posNew, stateNew, flags);
            if(level.getBlockEntity(posNew) instanceof ExtendedBlockEntity entity) {
                entity.setCenter(posOriginal);
            }
        });
    }

    default BlockState getStateForPlacementHelper(BlockPlaceContext context) {
        return getStateForPlacementHelper(context, context.getHorizontalDirection());
    }
    default BlockState getStateForPlacementHelper(BlockPlaceContext context, Direction direction) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = getBlock().defaultBlockState().setValue(AbstractExtendedBlock.CENTER, true);

        if (getDirectionProperty() != null){
            state = state.setValue(getDirectionProperty(), direction);
        }

        return canPlace(level, pos, state) ? state : null;
    }

    default boolean canPlace(LevelReader level, BlockPos center, BlockState state) {
        return fullBlockShape(center, state).allMatch(blockPos -> level.getBlockState(blockPos).canBeReplaced() && extraSurviveRequirements(level, blockPos, state));
    }

    default void destroy(BlockPos center, Level level, BlockState state){
        if (level.isClientSide()) return;
        fullBlockShape(center, state).forEach(pos ->{
            BlockState blockState = level.getBlockState(pos);
            Block block = state.getBlock();
            if (blockState.is(block)) {
                level.destroyBlock(pos, true);
            }
        });
    }

    default boolean allBlocksPresent(LevelReader level, BlockPos pos, BlockState state){
        if (level.isClientSide()) return true;
        BlockPos center = getCenter(level, pos);

        boolean ret = fullBlockShape(center, state).allMatch(blockPos -> level.getBlockState(blockPos).is(getBlock()));

        if (ret && level.getBlockEntity(pos) instanceof ExtendedBlockEntity entity && !entity.isPlaced) {
            fullBlockShape(center, state).forEach(blockPos -> ExtendedBlockEntity.setPlaced(level, blockPos));
        }

        return ret;
    }

    default BlockState updateShapeHelper(BlockState state, LevelAccessor level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof ExtendedBlockEntity entity){
            boolean canSurvive = state.canSurvive(level, pos);
            if (!canSurvive){
                destroy(entity.getCenter(), (Level) level, state);
                return Blocks.AIR.defaultBlockState();
            }
        } else {
            level.destroyBlock(pos, true);
            return Blocks.AIR.defaultBlockState();
        }

        return state;
    }

    default boolean canSurviveHelper(BlockState state, LevelReader level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof ExtendedBlockEntity entity) {
            //survive logic
            boolean extraSurvive = fullBlockShape(entity.getCenter(), state).allMatch(blockPos -> extraSurviveRequirements(level, blockPos, state));
            return (allBlocksPresent(level, pos, state) || !entity.isPlaced) && extraSurvive;
        } else {
            //placement logic
            return canPlace(level, pos, state);
        }
    }

    //Override this one to check for other blocks (like if bondripia can hang)
    //Runs for every single block
    default boolean extraSurviveRequirements(LevelReader level, BlockPos pos, BlockState state){
        return true;
    }

    // Add this to playerWillDestroy
    default void preventCreativeDrops(Player player, Level level, BlockPos pos){
        if (player.isCreative() && level.getBlockEntity(pos) instanceof ExtendedBlockEntity entity) {
            level.destroyBlock(entity.center, false);
        }
    }

    default void fixInStructures(BlockState state, ServerLevelAccessor level, BlockPos pos){
        if (isCenter(state)) {
            level.scheduleTick(pos, state.getBlock(), 3);
        }
    }

    default void fixTick(BlockState state, Level level, BlockPos pos){
        if (isCenter(state)){

            fullBlockShape(pos, state).forEach(posNew -> {
                if (level.getBlockEntity(posNew) instanceof ExtendedBlockEntity entity) {
                    entity.setCenter(pos);

                    entity.setChanged();
                    level.sendBlockUpdated(posNew, state, state, 2);
                }
            });
        }
    }

    default boolean isBroken(LevelReader level, BlockPos pos, BlockState state){
        if (!isCenter(state)) return false;

        return fullBlockShape(pos, state).anyMatch(blockPos -> {

           if (level.getBlockEntity(blockPos) instanceof ExtendedBlockEntity entity){
              return !(entity.center.equals(pos) && !isCenter(level.getBlockState(blockPos)));
           }

           return true;
        });
    }

    default BlockPos getCenter(LevelReader level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof ExtendedBlockEntity entity){
            return entity.getCenter();
        }
        return pos;
    }

    default boolean isCenter(LevelReader level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof ExtendedBlockEntity entity) {
            return entity.getCenter().equals(pos);
        }
        return false;
    }

    default boolean isCenter(BlockState state){
        return state.getValue(AbstractExtendedBlock.CENTER);
    }

    default int getXOffset(BlockGetter level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof ExtendedBlockEntity entity) {
            return pos.getX() - entity.getCenter().getX();
        }
        return 0;
    }

    default int getYOffset(BlockGetter level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof ExtendedBlockEntity entity) {
            return pos.getY() - entity.getCenter().getY();
        }
        return 0;
    }

    default int getZOffset(BlockGetter level, BlockPos pos){
        if (level.getBlockEntity(pos) instanceof ExtendedBlockEntity entity) {
            return pos.getZ() - entity.getCenter().getZ();
        }
        return 0;
    }

    static Rotation rotationFromDirection(Direction direction){
        return switch (direction){
            case DOWN, NORTH, UP -> Rotation.NONE;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            case EAST -> Rotation.CLOCKWISE_90;
        };
    }

    default VoxelShape voxelShapeHelper(BlockState state, BlockGetter level, BlockPos pos, VoxelShape shape){
        return voxelShapeHelper(state,level,pos,shape, 0, 0, 0);
    }

    default VoxelShape voxelShapeHelper(BlockState state, BlockGetter level, BlockPos pos, VoxelShape shape, float xOffset, float yOffset, float zOffset){
        return voxelShapeHelper(state,level,pos,shape, xOffset, yOffset, zOffset, false);
    }


    default VoxelShape voxelShapeHelper(BlockState state, BlockGetter level, BlockPos pos, VoxelShape shape, float xOffset, float yOffset, float zOffset, boolean hasDirectionOffsets){
        if (level.getBlockEntity(pos) instanceof ExtendedBlockEntity entity) {
            var x = entity.center.getX() - pos.getX() + xOffset;
            var y = entity.center.getY() - pos.getY() + yOffset;
            var z = entity.center.getZ() - pos.getZ() + zOffset;

            if (getDirectionProperty() != null && hasDirectionOffsets) {
                switch (state.getValue(getDirectionProperty())) {
                    case EAST -> x += 1;
                    case NORTH -> {
                        x += 1;
                        z -= 1;
                    }
                    case WEST -> z -= 1;
                }
            }
            return shape.move(x,y,z);
        }
        return shape;
    }

    default void growHelper(Level level, BlockPos blockPos, BlockState blockState){
        Block block = blockState.getBlock();
        if (block instanceof CropBlock cropBlock) {
            if(level.getBlockEntity(blockPos) instanceof ExtendedBlockEntity entity) {
                fullBlockShape(entity.getCenter(), level.getBlockState(blockPos)).forEach(pos -> {
                    if(level.getBlockState(pos).is(block)) {
                        cropBlock.growCrops(level, pos, level.getBlockState(pos));
                    }else {
                        level.destroyBlock(pos, false);
                    }
                });
            } else level.destroyBlock(blockPos, true);
        }
    }

}
