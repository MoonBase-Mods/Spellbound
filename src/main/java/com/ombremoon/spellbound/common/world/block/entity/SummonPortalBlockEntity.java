package com.ombremoon.spellbound.common.world.block.entity;

import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.common.world.dimension.DynamicDimensionFactory;
import com.ombremoon.spellbound.common.init.SBBlockEntities;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.ArenaSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SummonPortalBlockEntity extends BlockEntity {
    protected static final Logger LOGGER = Constants.LOG;
    private int arenaId;
    private boolean arenaReady;

    protected SummonPortalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public SummonPortalBlockEntity(BlockPos pos, BlockState blockState) {
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
            if (blockEntity instanceof SummonPortalBlockEntity) {
                ArenaSavedData data = ArenaSavedData.get((ServerLevel) level);
                ResourceKey<Level> levelKey = data.getOrCreateKey(server, this.arenaId);
                ServerLevel arena = DynamicDimensionFactory.getOrCreateDimension(server, levelKey);
                if (arena != null) {
                    ArenaSavedData arenaData = ArenaSavedData.get(arena);
                    if (arenaData.getCurrentBossFight() != null && arenaData.spawnedArena()) {
                        arenaData.spawnInArena(arena, entity);
                    } else if (arenaData.getCurrentBossFight() == null) {
                        arenaData.destroyPortal(arena);
                    }
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

    public boolean isArenaReady() {
        return this.arenaReady;
    }

    public void setArenaReady(boolean ready) {
        this.arenaReady = ready;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public boolean shouldRenderFace(Direction face) {
        return face.getAxis() == Direction.Axis.Y;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("ArenaId", this.arenaId);
        tag.putBoolean("ArenaReady", this.arenaReady);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.arenaId = tag.getInt("ArenaId");
        this.arenaReady = tag.getBoolean("ArenaReady");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
