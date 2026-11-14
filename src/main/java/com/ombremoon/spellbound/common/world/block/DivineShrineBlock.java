package com.ombremoon.spellbound.common.world.block;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.client.photon.converter.EffectData;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBTags;
import com.ombremoon.spellbound.common.world.item.PhialItem;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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

public class DivineShrineBlock extends AbstractMultiBlock implements IPreviewableMultiblock {
    public static final ResourceLocation DIVINE_FX = CommonClass.customLocation("divine_action");;
    public static final ResourceLocation DIVINE_ACTIVE_FX = CommonClass.customLocation("divine_action_idle");;
    public static final ResourceLocation CURSED_FX = CommonClass.customLocation("dark_divine_action");
    public static final ResourceLocation CURSED_ACTIVE_FX = CommonClass.customLocation("dark_divine_action_idle");
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty CURSED = BooleanProperty.create("cursed");
    public static final VoxelShape SHAPE = makeShape();

    public DivineShrineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVE, false).setValue(CURSED, false));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos center = this.getShrineBase(state, pos);
        BlockState blockState = level.getBlockState(center);
        if (stack.is(SBItems.MAGIC_ESSENCE.get()) && !blockState.getValue(ACTIVE)) {
            if (!level.isClientSide) {
                level.setBlock(center, blockState.setValue(ACTIVE, true), 3);
                ResourceLocation fx = blockState.getValue(CURSED) ? CURSED_ACTIVE_FX : DIVINE_ACTIVE_FX;
                RenderUtil.triggerBlockEffect((ServerLevel) level, center, EffectData.Block.of(fx, center)
                        .setOffset(0, -0.49, 0));
            }

            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            level.scheduleTick(center, this, 1200);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), 3);
            ResourceLocation activeFX = state.getValue(CURSED) ? CURSED_ACTIVE_FX : DIVINE_ACTIVE_FX;
            RenderUtil.removeBlockEffect(level, pos, activeFX);
        }
    }

    public BlockPos getShrineBase(BlockState state, BlockPos pos) {
        return state.getValue(CENTER) ? pos : pos.below();
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
            state.setValue(CURSED, false);
        }

        return state;
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
        builder.add(ACTIVE, CURSED);
    }

    public boolean canConvert(BlockState state, PhialItem.PhialType type) {
        boolean isCursed = state.getValue(CURSED);
        if (type == PhialItem.PhialType.ECLIPSE) {
            return false;
        } else if (isCursed && type == PhialItem.PhialType.CURSED) {
            return false;
        } else if (!isCursed && type == PhialItem.PhialType.DIVINE) {
            return false;
        }

        return true;
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
