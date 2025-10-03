package com.ombremoon.spellbound.common.content.block.entity;

import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.common.content.world.dimension.DynamicDimensionFactory;
import com.ombremoon.spellbound.common.init.SBBlockEntities;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.ArenaSavedData;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.slf4j.Logger;

import java.util.UUID;

public class SummonBlockEntity extends BlockEntity {
    protected static final Logger LOGGER = Constants.LOG;
    private int arenaId;

    protected SummonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public SummonBlockEntity(BlockPos pos, BlockState blockState) {
        this(SBBlockEntities.SUMMON_PORTAL.get(), pos, blockState);
    }

    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.level().isClientSide || entity.getServer() == null)
            return;

        if (entity.isOnPortalCooldown())
            return;

        boolean canTeleportInDimension = entity.level().dimension() == Level.NETHER || entity.level().dimension() == Level.OVERWORLD;
        if (entity instanceof LivingEntity livingEntity && canTeleportInDimension && livingEntity.canUsePortal(false)
                && Shapes.joinIsNotEmpty(
                Shapes.create(entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ())),
                state.getShape(level, pos),
                BooleanOp.AND
        )) {
            MinecraftServer server = level.getServer();
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SummonBlockEntity) {
                ArenaSavedData data = ArenaSavedData.get((ServerLevel) level);
                ResourceKey<Level> levelKey = data.getOrCreateKey(server, this.arenaId);
                ServerLevel arena = DynamicDimensionFactory.getOrCreateDimension(server, levelKey);
                if (arena != null) {
                    ArenaSavedData arenaData = ArenaSavedData.get(arena);
                    arenaData.spawnInArena(arena, entity);
                }
            }
        }
    }

    public int getArenaID() {
        return this.arenaId;
    }

    public void setArenaID(int arenaId) {
        this.arenaId = arenaId;
        this.setChanged();
    }

    public boolean shouldRenderFace(Direction face) {
        return face.getAxis() == Direction.Axis.Y;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("ArenaId", this.arenaId);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.arenaId = tag.getInt("ArenaId");
    }
}
