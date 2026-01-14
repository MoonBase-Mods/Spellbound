package com.ombremoon.spellbound.common.magic.acquisition.bosses;

import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;

import java.util.UUID;

public class PortalCache {
    protected static final Logger LOGGER = Constants.LOG;
    private UUID owner;
    private int arenaID;
    private BlockPos portalPos;
    private ResourceKey<Level> portalLevel;

    public UUID getOwner() {
        return this.owner;
    }

    public int getArenaID() {
        return this.arenaID;
    }

    public BlockPos getPortalPos() {
        return this.portalPos;
    }

    public ResourceKey<Level> getPortalLevel() {
        return this.portalLevel;
    }

    public void destroyPortal(ServerLevel level) {
        if (this.portalPos != null) {
            BlockPos pos = this.portalPos.offset(-4, 0, -4);
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    BlockPos blockPos1 = pos.offset(i, 0, j);
                    BlockState blockState = level.getBlockState(blockPos1);
                    if (blockState.is(SBBlocks.SUMMON_STONE.get()) || blockState.is(SBBlocks.SUMMON_PORTAL.get()))
                        level.setBlock(blockPos1, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        ArenaSavedData data = ArenaSavedData.get(level);
        data.cacheClosedArena(this.owner, this.arenaID);
    }

    public void loadCache(Player owner, int arenaID, BlockPos portalPos, ResourceKey<Level> portalLevel) {
        this.owner = owner.getUUID();
        this.arenaID = arenaID;
        this.portalPos = portalPos;
        this.portalLevel = portalLevel;
    }

    public @UnknownNullability CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (this.owner != null)
            nbt.putUUID("PortalOwner", this.owner);

        nbt.putInt("PortalId", this.arenaID);

        if (this.portalPos != null)
            nbt.put("PortalPos", NbtUtils.writeBlockPos(this.portalPos));

        if (this.portalLevel != null)
            nbt.putString("PortalLevel", this.portalLevel.location().toString());

        return nbt;
    }

    public void deserializeNBT(CompoundTag compoundTag) {
        if (compoundTag.contains("PortalOwner"))
            this.owner = compoundTag.getUUID("PortalOwner");

        if (compoundTag.contains("PortalId", 99))
            this.arenaID = compoundTag.getInt("PortalId");

        NbtUtils.readBlockPos(compoundTag, "PortalPos").ifPresent(blockPos -> this.portalPos = blockPos);

        if (compoundTag.contains("PortalLevel", 8))
            this.portalLevel = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(compoundTag.getString("PortalLevel")));
    }
}
