package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GuideBookItem extends Item {
    private final ResourceLocation bookId;
    private final ResourceLocation bookTexture;

    public GuideBookItem(ResourceLocation bookId) {
        this(bookId, ResourceLocation.fromNamespaceAndPath(bookId.getNamespace(), "textures/gui/books/" + bookId.getPath() + ".png"));
    }

    public GuideBookItem(ResourceLocation bookId, ResourceLocation bookTexture) {
        super(new Properties().stacksTo(1));
        this.bookId = bookId;
        this.bookTexture = bookTexture;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (this.bookId == null) return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        if (GuideBookManager.getBook(this.bookId) == null) return InteractionResultHolder.fail(player.getItemInHand(usedHand));

        if (level.isClientSide) {
            RenderUtil.openBook(this.bookId, this.bookTexture);
        }

        return InteractionResultHolder.success(player.getItemInHand(usedHand));

    }
}
