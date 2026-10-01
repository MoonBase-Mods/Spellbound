package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Mixin(EffectRenderingInventoryScreen.class)
public class EffectRenderingInventoryScreenMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    @Shadow @Final private static ResourceLocation EFFECT_BACKGROUND_LARGE_SPRITE;

    @Shadow @Final private static ResourceLocation EFFECT_BACKGROUND_SMALL_SPRITE;

    public EffectRenderingInventoryScreenMixin(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo info) {
        spellbound$renderSpells(guiGraphics, mouseX, mouseY);
    }

    @Unique
    private void spellbound$renderSpells(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Player player = this.minecraft.player;
        var handler = SpellUtil.getSpellHandler(player);
        int i = this.leftPos;
        Collection<AbstractSpell> spells = handler.getActiveSpells().stream().filter(AbstractSpell::shouldRender).toList();
        if (!spells.isEmpty() && i >= 32) {
            boolean flag = i >= 120;
            int j = 33;
            if (spells.size() > 5)
                j = 132 / (spells.size() - 1);

            this.renderSpellBackgrounds(guiGraphics, i, j, spells, flag);
            this.renderSpellIcons(guiGraphics, i, j, spells, flag);
            if (flag) {
                this.renderSpellLabels(guiGraphics, i, j, spells);
            } else if (mouseX <= i && mouseX >= i - 33) {
                int k = this.topPos;
                AbstractSpell spell = null;

                for (AbstractSpell spell1 : spells) {
                    if (mouseY >= k && mouseY <= k + j)
                        spell = spell1;

                    k += j;
                }

                if (spell != null) {
                    List<Component> list = List.of(
                            spell.getName(),
                            formatDuration(spell, 1.0F, this.minecraft.level.tickRateManager().tickrate())
                    );
                    guiGraphics.renderTooltip(this.font, list, Optional.empty(), mouseX, mouseY);
                }
            }
        }
    }

    private void renderSpellBackgrounds(GuiGraphics guiGraphics, int renderX, int yOffset, Iterable<AbstractSpell> spells, boolean isSmall) {
        int i = renderX - 33;
        int j = this.topPos;

        for (AbstractSpell spell : spells) {
            if (isSmall) {
                guiGraphics.blitSprite(EFFECT_BACKGROUND_LARGE_SPRITE, i - 88, j, 120, 32);
            } else {
                guiGraphics.blitSprite(EFFECT_BACKGROUND_SMALL_SPRITE, i, j, 32, 32);
            }

            j += yOffset;
        }
    }

    private void renderSpellIcons(GuiGraphics guiGraphics, int renderX, int yOffset, Iterable<AbstractSpell> spells, boolean isSmall) {
        int i = renderX - 33;
        int j = this.topPos;

        for (AbstractSpell spell : spells) {
            guiGraphics.blit(spell.getTexture(), i + (isSmall ? -84 : 4), j + 4, 0, 0, 24, 24, 24, 24);
            j += yOffset;
        }
    }

    private void renderSpellLabels(GuiGraphics guiGraphics, int renderX, int yOffset, Iterable<AbstractSpell> spells) {
        int i = this.topPos;

        for (AbstractSpell spell : spells) {
            Component component = spell.getName();
            guiGraphics.drawString(this.font, component, renderX - 88, i + 6, 16777215);
            Component component1 = formatDuration(spell, 1.0F, this.minecraft.level.tickRateManager().tickrate());
            guiGraphics.drawString(this.font, component1, renderX - 88, i + 6 + 10, 8355711);
            i += yOffset;
        }
    }

    private static Component formatDuration(AbstractSpell spell, float durationFactor, float ticksPerSecond) {
        if (spell.getCastType() == AbstractSpell.CastType.CHANNEL) {
            return Component.translatable("effect.duration.infinite");
        } else {
            int i = Mth.floor((float)(spell.getDuration() - spell.tickCount) * durationFactor);
            return Component.literal(StringUtil.formatTickDuration(i, ticksPerSecond));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

    }
}
