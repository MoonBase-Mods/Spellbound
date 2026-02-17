package com.ombremoon.spellbound.common.magic.api.events;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockItemDropsEvent extends SpellEvent {
    private final BlockDropsEvent event;
    @Nullable
    private final BlockEntity blockEntity;
    private final List<ItemEntity> drops;
    @Nullable
    private final Entity breaker;
    private final ItemStack tool;
    private int experience;

    public BlockItemDropsEvent(LivingEntity caster, BlockDropsEvent event) {
        super(caster, event);
        this.event = event;
        this.drops = event.getDrops();
        this.blockEntity = event.getBlockEntity();
        this.breaker = event.getBreaker();
        this.tool = event.getTool();
        this.experience = event.getDroppedExperience();
    }

    public List<ItemEntity> getDrops() {
        return this.drops;
    }

    @Nullable
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Nullable
    public Entity getBreaker() {
        return this.breaker;
    }

    public ItemStack getTool() {
        return this.tool;
    }

    public ServerLevel getLevel() {
        return this.event.getLevel();
    }

    public int getDroppedExperience() {
        return experience;
    }

    public void setDroppedExperience(int experience) {
        Preconditions.checkArgument(experience >= 0, "May not set a negative experience drop.");
        this.experience = experience;
    }

    public BlockState getBlockState() {
        return this.event.getState();
    }

    public BlockPos getPos() {
        return this.event.getPos();
    }
}
