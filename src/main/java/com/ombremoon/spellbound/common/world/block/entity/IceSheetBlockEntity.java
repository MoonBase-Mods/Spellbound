package com.ombremoon.spellbound.common.world.block.entity;

import com.ombremoon.spellbound.client.photon.EffectCache;
import com.ombremoon.spellbound.client.photon.FXEmitter;
import com.ombremoon.spellbound.common.init.SBBlockEntities;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.EffectManager;
import com.ombremoon.spellbound.common.world.spell.ruin.ice.IceSkateSpell;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class IceSheetBlockEntity extends BlockEntity implements FXEmitter {
    private final EffectCache cache = new EffectCache();
    private UUID spellOwner;
    private boolean harmful;
    private boolean canFreeze;

    public IceSheetBlockEntity(BlockPos pos, BlockState blockState) {
        super(SBBlockEntities.ICE_SHEET.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.spellOwner != null) {
            tag.putUUID("Owner", this.spellOwner);
        }

        tag.putBoolean("Harmful", this.harmful);
        tag.putBoolean("CanFreeze", this.canFreeze);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID("Owner")) {
            this.spellOwner = tag.getUUID("Owner");
        }

        this.harmful = tag.getBoolean("Harmful");
        this.canFreeze = tag.getBoolean("CanFreeze");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        if (this.spellOwner != null) {
            tag.putUUID("Owner", this.spellOwner);
        }

        tag.putBoolean("Harmful", this.harmful);
        tag.putBoolean("CanFreeze", this.canFreeze);

        return tag;
    }

    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (this.spellOwner != null) {
            Player player = level.getPlayerByUUID(this.spellOwner);
            if (player != null && !entity.is(player)) {
                IceSkateSpell spell = this.createSpell(player);
                if (spell != null && entity instanceof LivingEntity livingEntity && this.harmful) {
                    livingEntity.setIsInPowderSnow(true);
                    if (!player.level().isClientSide && this.canFreeze) {
                        var effects = SpellUtil.getSpellEffects(livingEntity);
                        effects.incrementBuildEffects(EffectManager.Effect.FROST, 1);
                    }
                }
            }
        }
    }

    private IceSkateSpell createSpell(Player player) {
        return SBSpells.ICE_SKATE.get().createSpellWithData(player);
    }

    @Nullable
    public UUID getOwner() {
        return this.spellOwner;
    }

    public void setOwner(LivingEntity owner, boolean harmful, boolean canFreeze) {
        this.spellOwner = owner.getUUID();
        this.harmful = harmful;
        this.canFreeze = canFreeze;
        this.setChanged();
    }

    @Override
    public EffectCache getFXCache() {
        return this.cache;
    }
}
