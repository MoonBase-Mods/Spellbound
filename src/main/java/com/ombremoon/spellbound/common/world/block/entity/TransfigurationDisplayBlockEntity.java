package com.ombremoon.spellbound.common.world.block.entity;

import com.mojang.datafixers.kinds.Const;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.ombremoon.spellbound.common.world.multiblock.TransfigurationMultiblockPart;
import com.ombremoon.spellbound.common.init.SBBlockEntities;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TransfigurationDisplayBlockEntity extends TransfigurationMultiblockPart {
    public ItemStack currentItem;
    public float rot;
    public float oRot;
    public float tRot;
    public int itemTick;
    public long spiralStartTick;
    public boolean active;
    public int spiralTick;
    public BlockPos pedestalPos;
    public double centerDistX;
    public double centerDistY;
    public double centerDistZ;
    public double distToCenter;
    public double spiralRot;

    public TransfigurationDisplayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public TransfigurationDisplayBlockEntity(BlockPos pos, BlockState blockState) {
        super(SBBlockEntities.TRANSFIGURATION_DISPLAY.get(), pos, blockState);
    }

    public static void itemAnimationTick(Level level, BlockPos pos, BlockState state, TransfigurationDisplayBlockEntity display) {
        if (!display.active) {
            display.oRot = display.rot;
            display.tRot += 0.02F;

            while (display.rot < (float) -Math.PI) {
                display.rot += (float) (Math.PI * 2);
            }

            while (display.tRot >= (float) Math.PI) {
                display.tRot -= (float) (Math.PI * 2);
            }

            while (display.tRot < (float) -Math.PI) {
                display.tRot += (float) (Math.PI * 2);
            }

            float f2 = display.tRot - display.rot;

            while (f2 >= (float) Math.PI) {
                f2 -= (float) (Math.PI * 2);
            }

            while (f2 < (float) -Math.PI) {
                f2 += (float) (Math.PI * 2);
            }

            display.rot += f2 * 0.4F;
            display.itemTick++;

            if (display.spiralTick > 0) {
                resetDisplay(display);
            }
        } else if (display.pedestalPos != null) {
            initSpiral(display, display.pedestalPos);
        } else {
            if (display.getRitual() == null)
                return;

            tickItems(display);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TransfigurationDisplayBlockEntity display) {
        if (display.active) {
            if (display.getRitual() == null) {
                display.setActive(false);
                return;
            }

            tickItems(display);
        }

    }

    private static void tickItems(TransfigurationDisplayBlockEntity display) {
        display.spiralTick++;
        if (display.spiralTick >= display.getRitual().definition().startupTime()) {
            resetDisplay(display);
        }
    }

    private static void resetDisplay(TransfigurationDisplayBlockEntity display) {
        display.setItem(null);
        display.centerDistX = 0;
        display.centerDistY = 0;
        display.centerDistZ = 0;
        display.distToCenter = 0;
        display.spiralRot = 0;
        display.spiralStartTick = 0;
        display.spiralTick = 0;
        display.active = false;
        display.setRitual(null);
        display.setChanged();
    }

    public static void initSpiral(TransfigurationDisplayBlockEntity display, BlockPos center) {
        display.centerDistX = display.worldPosition.getX() - center.getX();
        display.centerDistY = display.worldPosition.getY() - center.getY();
        display.centerDistZ = display.worldPosition.getZ() - center.getZ();
        display.distToCenter = Mth.length(display.centerDistX, display.centerDistZ);
        display.spiralRot = Mth.atan2(display.centerDistZ, display.centerDistX);
        if (display.spiralRot < 0) display.spiralRot += Mth.TWO_PI;
        display.spiralStartTick = display.level.getGameTime();
        display.pedestalPos = null;
    }

    public Vec3 spiralOffset(float partialTicks, double turns) {
        double raw = ((level.getGameTime() - spiralStartTick) + partialTicks) / this.getRitual().definition().startupTime();
        double p = Mth.clamp(raw, 0.0, 1.0);
        double ease = 1.0 - Math.pow(1.0 - p, 3.0);

        float radius = (float) (distToCenter * (1.0 - ease));
        float theta = (float) (spiralRot + turns * Math.PI * 2 * ease);

        double x = radius * Mth.cos(theta);
        double z = radius * Mth.sin(theta);
        return new Vec3(x - centerDistX, -centerDistY, z - centerDistZ);
    }

    public void setItem(ItemStack stack) {
        this.currentItem = stack;
        setChanged();
    }

    public void setCenter(BlockPos center) {
        this.pedestalPos = center;
        setChanged();
    }

    public void setActive(boolean active) {
        this.active = active;
        setChanged();
    }

    @Override
    public void onCleared(Level level, BlockPos blockPos) {
        this.pedestalPos = null;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.currentItem != null && !this.currentItem.isEmpty()) {
            ItemStack.CODEC
                    .encodeStart(NbtOps.INSTANCE, this.currentItem)
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(nbt -> tag.put("CurrentItem", nbt));
        }

        if (this.pedestalPos != null) {
            tag.putInt("CenterX", this.pedestalPos.getX());
            tag.putInt("CenterY", this.pedestalPos.getY());
            tag.putInt("CenterZ", this.pedestalPos.getZ());
        }

        tag.putBoolean("Active", this.active);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("CurrentItem", 10)) {
            DataResult<ItemStack> dataResult = ItemStack.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, tag.get("CurrentItem")));
            dataResult.resultOrPartial(LOGGER::error).ifPresent(this::setItem);
        } else {
            this.currentItem = null;
        }

        this.pedestalPos = BlockPos.containing(tag.getInt("CenterX"), tag.getInt("CenterY"), tag.getInt("CenterZ"));
        this.active = tag.getBoolean("Active");
    }
}
