package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.client.gui.toasts.SpellboundToasts;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public abstract class GuideBookItem extends Item {
    private final ResourceLocation bookId;
    private final ResourceLocation bookTexture;

    public GuideBookItem(ResourceLocation bookId) {
        this(bookId, ResourceLocation.fromNamespaceAndPath(bookId.getNamespace(), "textures/gui/books/" + bookId.getPath() + ".png"));
    }

    public ResourceLocation getBookId() {
        return bookId;
    }

    public ResourceLocation getBookTexture() {
        return bookTexture;
    }

    public GuideBookItem(ResourceLocation bookId, ResourceLocation bookTexture) {
        super(new Properties().stacksTo(1));
        this.bookId = bookId;
        this.bookTexture = bookTexture;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack book = player.getItemInHand(usedHand);
        if (this.bookId == null)
            return InteractionResultHolder.fail(book);

        if (GuideBookManager.getBook(this.bookId) == null)
            return InteractionResultHolder.fail(book);

        if (level.isClientSide) {
            RenderUtil.openBook(this.bookId, this.bookTexture);
        }

        return InteractionResultHolder.sidedSuccess(book, level.isClientSide);
    }

    public abstract Supplier<AttachmentType<Boolean>> getBookAttachment();

    public abstract SpellboundToasts getBookToast();
}
