package com.ombremoon.spellbound.common.magic.acquisition.transfiguration;

import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class RitualHelper {

    public static Optional<TransfigurationRitual> getRitualFor(Level level, TransfigurationMultiblock multiblock, List<ItemStack> items) {
        return level.registryAccess().registry(Keys.RITUAL).get().stream().filter(ritual -> ritual.matches(multiblock, items)).findFirst();
    }

    public static void createItem(Level level, BlockPos pos, ItemStack item) {
        createItem(level, pos.getCenter(), item);
    }

    public static void createItem(Level level, Vec3 pos, ItemStack item) {
        createItem(level, pos, item, Optional.empty());
    }

    public static <T> void createItem(Level level, BlockPos pos, ItemStack item, Optional<DataComponentStorage> storage) {
        createItem(level, pos.getBottomCenter(), item, storage);
    }

    public static <T> void createItem(Level level, Vec3 pos, ItemStack item, Optional<DataComponentStorage> storage) {
        if (!level.isClientSide) {
            storage.ifPresent(dataStorage -> {
                for (var typeComponent : dataStorage.dataComponents()) {
                    item.set((DataComponentType<T>) typeComponent.type(), (T) typeComponent.value());
                }
            });

            ItemEntity entity = new ItemEntity(level, pos.x(), pos.y() + 1.5F, pos.z(), item);
            entity.setDeltaMovement(Vec3.ZERO);
            level.addFreshEntity(entity);
        }
    }
}
