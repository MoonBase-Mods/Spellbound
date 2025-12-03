package com.ombremoon.spellbound.common.world.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class CrystalBlock extends Block {
    public static final MapCodec<CrystalBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    CrystalType.CODEC.fieldOf("crystal").forGetter(block -> block.crystal),
                    propertiesCodec()
            ).apply(instance, CrystalBlock::new)
    );
    protected final CrystalType crystal;

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public CrystalBlock(CrystalType crystal, Properties properties) {
        super(properties);
        this.crystal = crystal;
    }

    @Override
    protected void onProjectileHit(Level p_152001_, BlockState p_152002_, BlockHitResult p_152003_, Projectile p_152004_) {
        if (!p_152001_.isClientSide) {
            BlockPos blockpos = p_152003_.getBlockPos();
            p_152001_.playSound(null, blockpos, this.crystal.blockHit, SoundSource.BLOCKS, 1.0F, 0.5F + p_152001_.random.nextFloat() * 1.2F);
            p_152001_.playSound(null, blockpos, this.crystal.blockChime, SoundSource.BLOCKS, 1.0F, 0.5F + p_152001_.random.nextFloat() * 1.2F);
        }
    }

    public enum CrystalType implements StringRepresentable {
        SMOLDERING("smoldering", SBBlocks.SMALL_SMOLDERING_CRYSTAL_BUD, SBBlocks.MEDIUM_SMOLDERING_CRYSTAL_BUD, SBBlocks.LARGE_SMOLDERING_CRYSTAL_BUD, SBBlocks.SMOLDERING_CRYSTAL_CLUSTER, SoundEvents.AMETHYST_BLOCK_HIT, SoundEvents.AMETHYST_BLOCK_CHIME),
        FROZEN("frozen", SBBlocks.SMALL_FROZEN_CRYSTAL_BUD, SBBlocks.MEDIUM_FROZEN_CRYSTAL_BUD, SBBlocks.LARGE_FROZEN_CRYSTAL_BUD, SBBlocks.FROZEN_CRYSTAL_CLUSTER, SoundEvents.AMETHYST_BLOCK_HIT, SoundEvents.AMETHYST_BLOCK_CHIME),
        STORM("storm", SBBlocks.SMALL_STORM_CRYSTAL_BUD, SBBlocks.MEDIUM_STORM_CRYSTAL_BUD, SBBlocks.LARGE_STORM_CRYSTAL_BUD, SBBlocks.STORM_CRYSTAL_CLUSTER, SoundEvents.AMETHYST_BLOCK_HIT, SoundEvents.AMETHYST_BLOCK_CHIME);

        public static final StringRepresentableCodec<CrystalType> CODEC = StringRepresentable.fromEnum(CrystalType::values);
        private final String name;
        private final Supplier<Block> smallBud;
        private final Supplier<Block> mediumBud;
        private final Supplier<Block> largeBud;
        private final Supplier<Block> cluster;
        private final SoundEvent blockHit;
        private final SoundEvent blockChime;

        CrystalType(String name, Supplier<Block> smallBud, Supplier<Block> mediumBud, Supplier<Block> largeBud, Supplier<Block> cluster, SoundEvent blockHit, SoundEvent blockChime) {
            this.name = name;
            this.smallBud = smallBud;
            this.mediumBud = mediumBud;
            this.largeBud = largeBud;
            this.cluster = cluster;
            this.blockHit = blockHit;
            this.blockChime = blockChime;
        }

        public Supplier<Block> getSmallCluster() {
            return this.smallBud;
        }

        public Supplier<Block> getMediumCluster() {
            return this.mediumBud;
        }

        public Supplier<Block> getLargeCluster() {
            return this.largeBud;
        }

        public Supplier<Block> getCluster() {
            return this.cluster;
        }

        public SoundEvent getHitSound() {
            return this.blockHit;
        }

        public SoundEvent getChimeSound() {
            return this.blockChime;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
