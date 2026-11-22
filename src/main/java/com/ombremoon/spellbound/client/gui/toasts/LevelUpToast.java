package com.ombremoon.spellbound.client.gui.toasts;

import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LevelUpToast implements Toast {
    public static final String SPELL_TRANS = "spellbound.toast.spell_level_up";
    public static final String PATH_TRANS = "spellbound.toast.path_level_up";
    private int level;
    private SpellboundToasts toast;
    private SpellType<?> spell;
    private SpellPath path;

    public LevelUpToast(int level, SpellboundToasts toast, @Nullable SpellType<?> spell, @Nullable SpellPath path) {
        this.level = level;
        this.toast = toast;
        this.spell = spell;
        this.path = path;
    }

    @Override
    public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeVisible) {
        Font font = toastComponent.getMinecraft().font;

        guiGraphics.blit(
                toast.getTexture(),
                0, 0,
                0, 0,
                this.width(), this.height(),
                this.width(), this.height());

        String popupText = this.spell == null
                ? Component.translatable(PATH_TRANS, this.path.getSerializedName().substring(0, 1).toUpperCase() + this.path.getSerializedName().substring(1), Integer.toString(this.level)).getString()
                :  Component.translatable(SPELL_TRANS, this.spell.createSpell().getName(), Integer.toString(this.level)).getString();
        int lettersRevealed = Math.clamp(timeVisible / 120, 0, popupText.length());
        MutableComponent comp = Component.translatable(popupText.substring(0, lettersRevealed))
                .append(Component.translatable(popupText.substring(lettersRevealed))
                        .withStyle(ChatFormatting.OBFUSCATED));

        List<FormattedCharSequence> lines = font.split(comp, 154);
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(font, lines.get(i), (width()-font.width(lines.get(i)))/2, (height()-13)/2 + (i * font.lineHeight), 0, false);
        }
        return timeVisible < 8000 ? Visibility.SHOW : Visibility.HIDE;
    }
}
