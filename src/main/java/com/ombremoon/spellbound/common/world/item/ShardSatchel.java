package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBTags;
import com.ombremoon.spellbound.common.world.item.components.SatchelContents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.structures.SnbtDatafixer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ShardSatchel extends Item {
    private static final int TOOLTIP_MAX_WEIGHT = 64;

    public ShardSatchel(Properties properties) {
        super(properties.stacksTo(1).component(SBData.SATCHEL_CONTENTS, SatchelContents.EMPTY));
    }

    public boolean containsCatalyst(ItemStack stack, Item catalyst) {
        SatchelContents contents = stack.get(SBData.SATCHEL_CONTENTS);
        if (contents == null)
            return false;

        for (ItemStack bagItem : contents.items()) {
            if (bagItem.is(catalyst)) return true;
        }

        return false;
    }

    public void useCatalyst(ItemStack stack, Item catalyst) {
        SatchelContents satchelContents = stack.get(SBData.SATCHEL_CONTENTS);
        if (satchelContents == null)
            return;

        boolean shrunk = false;
        SatchelContents.Mutable contents = new SatchelContents.Mutable(SatchelContents.EMPTY);
        for (ItemStack item : satchelContents.items()) {
            if (item.is(catalyst)) {
                if (!shrunk) {
                    item.shrink(1);
                    shrunk = true;
                }
            }
            contents.tryInsert(item);
        }

        stack.set(SBData.SATCHEL_CONTENTS, contents.toImmutable());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if (dropContents(itemstack, player)) {
            this.playDropContentsSound(player);
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    private static boolean dropContents(ItemStack stack, Player player) {
        SatchelContents satchelContents = stack.get(SBData.SATCHEL_CONTENTS);
        if (satchelContents != null && !satchelContents.isEmpty()) {
            stack.set(SBData.SATCHEL_CONTENTS, SatchelContents.EMPTY);
            if (player instanceof ServerPlayer) {
                satchelContents.itemsCopy().forEach((p_330078_) -> player.drop(p_330078_, true));
            }

            return true;
        } else {
            return false;
        }
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP) ? Optional.ofNullable(stack.get(SBData.SATCHEL_CONTENTS)).map(SatchelContents::asBundle).map(BundleTooltip::new) : Optional.empty();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        SatchelContents contents = stack.get(SBData.SATCHEL_CONTENTS);
        tooltipComponents.add(Component.translatable("item.spellbound.shard_satchel.limit", SatchelContents.MAX_PER_STACK).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("item.spellbound.shard_satchel.total_limit", contents == null ? 0 : contents.getItemCount(), SatchelContents.TOTAL_MAX_ITEMS).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (stack.getCount() != 1 || action != ClickAction.SECONDARY) return false;
        SatchelContents satchelContents = stack.get(SBData.SATCHEL_CONTENTS);
        if (satchelContents == null) return false;

        ItemStack slotItem = slot.getItem();
        SatchelContents.Mutable satchelContents$mutable = new SatchelContents.Mutable(satchelContents);
        if (slotItem.isEmpty()) {
            int insertedCount = satchelContents$mutable.tryTransfer(slot);
            if (insertedCount > 0) {
                this.playRemoveOneSound(player);
            }
        } else if (slotItem.is(SBTags.Items.MAGIC_SHARD)) {
            int insertedCount = satchelContents$mutable.tryInsert(slotItem);
            if (insertedCount > 0) {
                this.playInsertSound(player);
            }
        } else return false;

        stack.set(SBData.SATCHEL_CONTENTS, satchelContents$mutable.toImmutable());
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (stack.getCount() != 1 || other.isEmpty() || !other.is(SBTags.Items.MAGIC_SHARD)) return false;
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

        SatchelContents satchelContents = stack.get(SBData.SATCHEL_CONTENTS);
        if (satchelContents == null) return false;

        SatchelContents.Mutable satchelContents$mutable = new SatchelContents.Mutable(satchelContents);
        int insertedCount = satchelContents$mutable.tryInsert(other);
        if (insertedCount > 0) {
            this.playInsertSound(player);
        }

        stack.set(SBData.SATCHEL_CONTENTS, satchelContents$mutable.toImmutable());
        return true;
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
