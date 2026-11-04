package com.ombremoon.spellbound.common.world.block;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock;
import net.nikdo53.tinymultiblocklib.block.IPreviewableMultiblock;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class DivineShrineBlock extends AbstractMultiBlock implements IPreviewableMultiblock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final VoxelShape SHAPE = makeShape();

    public DivineShrineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVE, false));
    }

    /*@Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(SBItems.MAGIC_ESSENCE.get()) && !state.getValue(ACTIVE)) {
            BlockPos otherPos = pos.relative(state.getValue(CENTER) ? Direction.UP : Direction.DOWN);
            BlockState other = level.getBlockState(otherPos);
            level.setBlock(pos, state.setValue(ACTIVE, true), 3);
            level.setBlock(otherPos, other.setValue(ACTIVE, true), 3);
            //Trigger FX
            //Play Sound
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            level.scheduleTick(pos, this, 1200);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }*/

    private static Direction getNeighbourDirection(boolean center) {
        return center ? Direction.UP : Direction.DOWN;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        /*if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), 3);
            //Remove FX
            //Play Sound
        }*/
    }

    @Override
    public List<BlockPos> makeFullBlockShape(Level level, BlockPos center, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction) {
        return List.of(center, center.above());
    }

    @Override
    public RenderShape getMultiblockRenderShape(BlockState state, boolean isCenter) {
        return RenderShape.MODEL;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1F;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            state.setValue(ACTIVE, false);
        }

        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Nullable
    public static Pair<BlockPos, BlockState> getNearestShrine(Player player) {
        return getNearestShrine(player.level(), player.getOnPos());
    }

    @Nullable
    public static Pair<BlockPos, BlockState> getNearestShrine(Level level, BlockPos blockPos) {
        for (BlockPos pos : BlockPos.betweenClosed(blockPos.subtract(new Vec3i(10, 10, 10)), blockPos.offset(new Vec3i(7, 7, 7)))) {
            BlockState state = level.getBlockState(pos);

            if (state.is(SBTags.Blocks.DIVINE_SHRINE))
                return Pair.of(pos, state);
        }

        return null;
    }
}
