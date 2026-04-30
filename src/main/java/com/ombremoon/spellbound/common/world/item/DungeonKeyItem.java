package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleConfiguration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DungeonKeyItem extends Item {
    public DungeonKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Boolean bool = stack.get(SBData.ENCRYPTED_KEY);
        if (bool != null && bool) {
            ResourceKey<PuzzleConfiguration> riddleKey = stack.get(SBData.PUZZLE);
            if (riddleKey != null) {
                String path = riddleKey.location().getPath();
                tooltipComponents.add(Component.translatable("dungeon_key." + path + ".riddle"));
            }
        }
    }

    public static ItemStack createWithRiddle(ResourceKey<PuzzleConfiguration> riddleKey) {
        ItemStack key = new ItemStack(SBItems.DUNGEON_KEY.get());
        key.set(SBData.PUZZLE, riddleKey);
        key.set(SBData.ENCRYPTED_KEY, true);

        return key;
    }
}
