package com.ombremoon.spellbound.common.world.block.entity;

import com.ombremoon.spellbound.common.init.SBBlockEntities;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBRecipes;
import com.ombremoon.spellbound.common.magic.effects.EffectHolder;
import com.ombremoon.spellbound.common.world.recipe.RuneCraftingInput;
import com.ombremoon.spellbound.common.world.recipe.RuneCraftingRecipe;
import com.ombremoon.spellbound.main.Constants;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DarkAltarBlockEntity extends BlockEntity {
    private final RecipeManager.CachedCheck<RuneCraftingInput, RuneCraftingRecipe> quickCheck = RecipeManager.createCheck(SBRecipes.RUNE_RECIPE.get());
    private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private int craftingProgress = 0;
    private int craftingTime = 40;
    public ItemStack chalk = ItemStack.EMPTY;
    public float rot;
    public float oRot;
    public float tRot;
    public int chalkTick;

    public DarkAltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(SBBlockEntities.DARK_ALTAR.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DarkAltarBlockEntity blockEntity) {
        boolean flag = false;

        if (!blockEntity.hasChalk())
            return;

        if (blockEntity.canTransmuteChalk()) {
            List<EffectHolder> combinedEffects = new ArrayList<>(blockEntity.chalk.getOrDefault(SBData.RUNE_EFFECTS, List.of()));
            int size = combinedEffects.size();
            if (size >= 5) {
                blockEntity.craftingProgress = 0;
                return;
            }

            List<EffectHolder> chalkEffects = new ArrayList<>();
            for (ItemStack item : blockEntity.items) {
                List<EffectHolder> effects = item.getOrDefault(SBData.RUNE_EFFECTS, List.of());
                chalkEffects.addAll(effects);
            }

            if (chalkEffects.size() + size > 5) {
                blockEntity.craftingProgress = 0;
                return;
            }

            flag = true;
            blockEntity.craftingProgress++;
            if (blockEntity.craftingProgress >= blockEntity.craftingTime) {
                combinedEffects.addAll(chalkEffects);
                blockEntity.chalk.set(SBData.RUNE_EFFECTS, combinedEffects);
                blockEntity.craftingProgress = 0;
                blockEntity.items.clear();
                level.sendBlockUpdated(pos, state, state, 3);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
            }
        } else {
            for (ItemStack item : blockEntity.items) {
                if (item.isEmpty()) {
                    return;
                }
            }

            RecipeHolder<RuneCraftingRecipe> recipeHolder = blockEntity.quickCheck.getRecipeFor(new RuneCraftingInput(blockEntity.items), level).orElse(null);
            if (!blockEntity.isRunicChalk(blockEntity.chalk) && recipeHolder != null) {
                flag = true;
                blockEntity.craftingProgress++;
                if (blockEntity.craftingProgress >= blockEntity.craftingTime) {
                    RuneCraftingRecipe recipe = recipeHolder.value();
                    blockEntity.chalk = recipe.assemble(new RuneCraftingInput(blockEntity.items), level.registryAccess());
                    blockEntity.craftingProgress = 0;
                    blockEntity.items.clear();
                    level.sendBlockUpdated(pos, state, state, 3);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
                }
            } else {
                blockEntity.craftingProgress = 0;
            }
        }

        if (flag) {
            setChanged(level, pos, state);
        }
    }

    public static void itemAnimationTick(Level level, BlockPos pos, BlockState state, DarkAltarBlockEntity display) {
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
        display.chalkTick++;
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public boolean hasChalk() {
        return !this.chalk.isEmpty();
    }

    public boolean canTransmuteChalk() {
        return this.isRunicChalk(this.chalk)
                && this.items.stream()
                .filter(stack -> !stack.isEmpty())
                .allMatch(this::isRunicChalk)
                && this.items.stream()
                .anyMatch(this::isRunicChalk);
    }

    public boolean isRunicChalk(ItemStack stack) {
        var list = stack.getOrDefault(SBData.RUNE_EFFECTS, List.of());
        return !stack.isEmpty() && stack.is(SBItems.CHALK.get()) && !list.isEmpty();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.chalk.isEmpty()) {
            tag.put("Chalk", this.chalk.save(registries));
        }

        ContainerHelper.saveAllItems(tag, this.items, true, registries);
        tag.putInt("CraftingProgress", this.craftingProgress);
        tag.putInt("CraftingTime", this.craftingTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Chalk", 10)) {
            this.chalk = ItemStack.parse(registries, tag.get("Chalk")).orElse(ItemStack.EMPTY);
        } else {
            this.chalk = ItemStack.EMPTY;
        }

        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.craftingProgress = tag.getInt("CraftingProgress");
        this.craftingTime = tag.getInt("CraftingTime");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, this.items, true, registries);
        if (!this.chalk.isEmpty())
            tag.put("Chalk", this.chalk.save(registries));

        return tag;
    }


    public boolean placeOrTakeItem(@Nullable LivingEntity entity, BlockPos pos, ItemStack stack, int index) {
        if (this.craftingProgress > 0) {
            return false;
        } else if (index != -1) {
            ItemStack itemStack = this.items.get(index);
            if (itemStack.isEmpty() && !stack.isEmpty()) {
                this.items.set(index, stack.consumeAndReturn(1, entity));
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
                this.markUpdated();
                return true;
            } else if (!itemStack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                this.items.set(index, ItemStack.EMPTY);
                this.markUpdated();
                return true;
            }
        } else {
            if (this.chalk.isEmpty() && stack.is(SBItems.CHALK.get())) {
                this.chalk = stack.consumeAndReturn(1, entity);
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
                this.markUpdated();
                return true;
            } else if (!this.chalk.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), this.chalk);
                this.chalk = ItemStack.EMPTY;
                this.markUpdated();
                return true;
            }
        }

        return false;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
}
