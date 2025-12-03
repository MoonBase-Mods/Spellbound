package com.ombremoon.spellbound.common.world.block;

import com.google.common.base.Predicates;
import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.world.dimension.DynamicDimensionFactory;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.BossFight;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.common.world.block.entity.SummonBlockEntity;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.ArenaSavedData;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SummonStoneBlock extends Block {
    public static final MapCodec<SummonStoneBlock> CODEC = simpleCodec(SummonStoneBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    private static BlockPattern portalShape;
    private final ResourceLocation spell;
    private final BossFight.BossFightBuilder<?> bossFight;

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public SummonStoneBlock(Properties properties) {
        this(CommonClass.customLocation(""), null, properties);
    }

    public SummonStoneBlock(ResourceLocation spell, BossFight.BossFightBuilder<?> bossFight, Properties properties) {
        super(properties);
        this.spell = spell;
        this.bossFight = bossFight;
        this.registerDefaultState(this.getStateDefinition().any().setValue(POWERED, Boolean.FALSE).setValue(FACING, Direction.NORTH));
    }

    public ResourceLocation getSpell() {
        return this.spell;
    }

    public boolean hasSpell() {
        return !this.defaultBlockState().is(SBBlocks.SUMMON_STONE.get());
    }

    public BossFight getBossFight() {
        return this.bossFight.build();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(POWERED, Boolean.FALSE).setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(SBItems.MAGIC_ESSENCE.get())) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            } else if (!ArenaSavedData.isArena(level)) {
                this.activateStone(state, level, pos, player, hand);
                return ItemInteractionResult.CONSUME;
            }
        }
        return ItemInteractionResult.FAIL;
    }

    public void activateStone(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        if (!state.getValue(POWERED)) {
            boolean canTeleportInDimension = level.dimension() == Level.NETHER || level.dimension() == Level.OVERWORLD;
            if (this.hasSpell() && canTeleportInDimension) {
                BlockPattern.BlockPatternMatch blockPatternMatch = getOrCreatePortalShape().find(level, pos);
                if (blockPatternMatch != null) {
                    var handler = SpellUtil.getSpellHandler(player);
                    if (!level.isClientSide) {
                        ArenaSavedData data = ArenaSavedData.get((ServerLevel) level);
                        int arenaId = data.incrementId();

                        handler.openArena(arenaId);
                        BlockPos frontTopLeft = blockPatternMatch.getFrontTopLeft();
                        BlockPos blockPos = frontTopLeft.offset(-3, 0, -3);

                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) {
                                BlockPos blockPos1 = blockPos.offset(i, 0, j);
                                level.setBlock(blockPos1, SBBlocks.SUMMON_PORTAL.get().defaultBlockState(), 2);
                                BlockEntity blockEntity = level.getBlockEntity(blockPos1);
                                if (blockEntity instanceof SummonBlockEntity summonBlockEntity) {
                                    summonBlockEntity.setArenaID(arenaId);
                                }
                            }
                        }

                        MinecraftServer server = level.getServer();
                        ResourceKey<Level> levelKey = data.getOrCreateKey(server, arenaId);
                        ServerLevel arena = DynamicDimensionFactory.getOrCreateDimension(server, levelKey);
                        if (arena != null && this.spell != null) {
                            ArenaSavedData arenaData = ArenaSavedData.get(arena);
                            arenaData.initializeArena(arena, player, arenaId, frontTopLeft, level.dimension(), this.spell, this.getBossFight());
                        }
                    }
                } else {
                    return;
                }
            } else {
                BlockState blockState = state.setValue(POWERED, Boolean.TRUE);
                level.setBlock(pos, blockState, 3);
            }

            level.levelEvent(1503, pos, 0);

            if (!player.getAbilities().instabuild)
                player.getItemInHand(hand).shrink(1);
        }
    }

    public static BlockPattern getOrCreatePortalShape() {
        if (portalShape == null) {
            portalShape = BlockPatternBuilder.start()
                    .aisle("?$$$?", "$???$", "$?*?$", "$???$", "?$$$?")
                    .where('?', BlockInWorld.hasState(BlockStatePredicate.ANY))
                    .where('$',
                            BlockInWorld.hasState(
                                    BlockStatePredicate.forBlock(SBBlocks.SUMMON_STONE.get())
                                            .where(POWERED, Predicates.equalTo(true))
                            )
                    )
                    .where('*',
                            BlockInWorld.hasState(
                                    state -> state.getBlock() instanceof SummonStoneBlock block && block.spell != null
                            )
                    )
                    .build();
        }
        return portalShape;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, FACING);
    }
}
