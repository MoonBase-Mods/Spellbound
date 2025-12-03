package com.ombremoon.spellbound.common.magic.acquisition.transfiguration.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.DataComponentStorage;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualEffect;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Optional;

public record CreateItem(Item item, Optional<DataComponentStorage> data) implements RitualEffect {
    public static final MapCodec<CreateItem> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(CreateItem::item),
                    DataComponentStorage.CODEC.optionalFieldOf("data").forGetter(CreateItem::data)
            ).apply(instance, CreateItem::new)
    );

    public static CreateItem withData(Item item, TypedDataComponent<?>... data) {
        DataComponentStorage storage = new DataComponentStorage(Arrays.asList(data));
        return new CreateItem(item, Optional.of(storage));
    }

    @Override
    public void onActivated(ServerLevel level, int tier, Player player, BlockPos centerPos, Multiblock.MultiblockPattern pattern) {
        Vec3 pos = centerPos.getBottomCenter();
        RitualHelper.createItem(level, pos, new ItemStack(item), this.data);
    }

    @Override
    public MapCodec<? extends RitualEffect> codec() {
        return CODEC;
    }
}
