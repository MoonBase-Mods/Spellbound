package com.ombremoon.spellbound.common.world.block.entity;

import com.ombremoon.spellbound.common.init.SBBlockEntities;
import com.ombremoon.spellbound.common.world.block.ManaSiphonBlock;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ManaSiphonBlockEntity extends BlockEntity {
    private float storedMana;
    private float maxMana = 100;
    private UUID owner;
    private int activeTime;

    public ManaSiphonBlockEntity(BlockPos pos, BlockState blockState) {
        super(SBBlockEntities.MANA_SIPHON.get(), pos, blockState);
    }

    public static void siphonManaTick(Level level, BlockPos pos, BlockState state, ManaSiphonBlockEntity entity) {
        boolean flag = entity.isActive();
        boolean flag1 = false;
        if (entity.isActive()) {
            entity.activeTime--;
        }

        if (entity.isActive() && entity.canSiphonMana() && entity.activeTime % 10 == 0) {
            AABB aabb = new AABB(pos).inflate(5);
            Player player = level.getPlayerByUUID(entity.owner);
            for (LivingEntity livingEntity : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (player != null && livingEntity.is(player))
                    continue;

                var handler = SpellUtil.getSpellHandler(livingEntity);
                float mana = (float) handler.getMana();
                float f = Math.min((float) (2.0F + handler.getMaxMana() * 0.02), mana);
                if (mana > 0 && handler.consumeMana(f)) {
                    entity.siphonMana(state, level, pos, f);
                    flag1 = true;
                }
            }
        }

        if (flag != entity.isActive()) {
            flag1 = true;
            state = state.setValue(ManaSiphonBlock.ACTIVE, entity.isActive());
            level.setBlock(pos, state, 3);
        }

        if (flag1) {
            entity.markUpdated();
        }
    }

    public boolean canSiphonMana() {
        return this.storedMana < this.maxMana;
    }

    public void siphonMana(BlockState state, Level level, BlockPos pos, float amount) {
        this.storedMana = Mth.clamp(this.storedMana + amount, 0, this.maxMana);
        this.updateManaState(state, level, pos);
        this.markUpdated();
    }

    public float getStoredMana() {
        return this.storedMana;
    }

    public boolean increaseManaStorage(BlockState state, Level level, BlockPos pos, float amount) {
        if (this.maxMana >= 1000)
            return false;

        this.maxMana = Mth.clamp(this.maxMana + amount, 0, 1000);
        this.updateManaState(state, level, pos);
        this.markUpdated();
        return true;
    }

    public float getMaxMana() {
        return this.maxMana;
    }

    public void activateSiphon(Player player, BlockState state, Level level, BlockPos pos) {
        this.owner = player.getUUID();
        this.activeTime = 2400;
        this.markUpdated();
    }

    private void updateManaState(BlockState state, Level level, BlockPos pos) {
        float f = this.storedMana / this.maxMana;
        int i = 0;
        if (f >= 1.0F) {
            i = 4;
        } else if (f >= 0.75F) {
            i = 3;
        } else if (f > 0.5F) {
            i = 2;
        } else if (f > 0) {
            i = 1;
        }

        level.setBlock(pos, state.setValue(ManaSiphonBlock.LEVEL, i), 3);
    }

    public boolean isOwner(Player player) {
        return this.owner == player.getUUID();
    }

    public boolean isActive() {
        return this.activeTime > 0;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("StoredMana", this.storedMana);
        tag.putFloat("MaxMana", this.maxMana);
        if (this.owner != null)
            tag.putUUID("Owner", this.owner);

        tag.putInt("ActiveTime", this.activeTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.storedMana = tag.getFloat("StoredMana");
        this.maxMana = tag.getFloat("MaxMana");
        if (tag.hasUUID("Owner"))
            this.owner = tag.getUUID("Owner");

        this.activeTime = tag.getInt("ActiveTime");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("StoredMana", this.storedMana);
        tag.putFloat("MaxMana", this.maxMana);
        if (this.owner != null)
            tag.putUUID("Owner", this.owner);

        tag.putInt("ActiveTime", this.activeTime);
        return tag;
    }
}
