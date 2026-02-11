package com.ombremoon.spellbound.common.world.block;

import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.world.block.entity.SummonPortalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SummonPortalBlock extends BaseEntityBlock {
    public static final MapCodec<SummonPortalBlock> CODEC = simpleCodec(SummonPortalBlock::new);
    protected static final VoxelShape SHAPE = Block.box(0.0, 6.0, 0.0, 16.0, 12.0, 16.0);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public SummonPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SummonPortalBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SummonPortalBlockEntity summonBlockEntity)
            summonBlockEntity.entityInside(state, level, pos, entity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean isReady = blockEntity instanceof SummonPortalBlockEntity summon && summon.isArenaReady();

        double x = (double) pos.getX() + random.nextDouble();
        double y = (double) pos.getY() + 0.8;
        double z = (double) pos.getZ() + random.nextDouble();

        if (isReady) {
            level.addParticle(ParticleTypes.PORTAL, x, y, z, 0.0, 0.0, 0.0);
        } else {
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.02, 0.0);
            if (random.nextInt(3) == 0) {
                level.addParticle(ParticleTypes.ENCHANT, x, y + 0.5, z, 0.0, 0.5, 0.0);
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return ItemStack.EMPTY;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }
}
