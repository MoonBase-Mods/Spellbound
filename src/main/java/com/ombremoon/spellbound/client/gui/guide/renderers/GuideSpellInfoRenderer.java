package com.ombremoon.spellbound.client.gui.guide.renderers;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.client.gui.guide.elements.GuideSpellInfoElement;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.ElementPosition;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.SpellInfoExtras;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.ChanneledSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class GuideSpellInfoRenderer implements IPageElementRenderer<GuideSpellInfoElement> {
    private static final ResourceLocation DATA_SPRITE = ResourceLocation.withDefaultNamespace("advancements/title_box");
    private static final ResourceLocation TITLE_BOX_SPRITE = ResourceLocation.withDefaultNamespace("advancements/box_unobtained");
    private static final Component HIDDEN = Component.literal("???").withStyle(ChatFormatting.OBFUSCATED);

    @Override
    public void render(GuideSpellInfoElement element, GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY, float partialTick, int tickCount) {
        Registry<SpellType<?>> spellRegistry = Minecraft.getInstance().level.registryAccess().registry(SBSpells.SPELL_TYPE_REGISTRY_KEY).get();
        SpellType<?> spellType = spellRegistry.get(element.spellLoc());
        if (spellType == null) {
            LOGGER.warn("Error parsing path info. Spell {} not found in registry.", element.spellLoc());
            return;
        }

        SpellInfoExtras extras = element.extras();
        boolean shouldShow = extras.alwaysShow() || Minecraft.getInstance().player.isCreative() || SpellUtil.getSpellHandler(Minecraft.getInstance().player).getSpellList().contains(spellType);

        AbstractSpell spell = spellType.createSpellWithData(Minecraft.getInstance().player);
        float baseDamage = spell.getBaseDamage();
        int castTime = spell.getCastTime();
        int duration = spell.getDuration();
        float manaCost = spell.getManaCost();
        float manaPerTick = 0;
        if (spell instanceof ChanneledSpell channeledSpell) {
            manaPerTick = channeledSpell.getManaTickCost();
            duration = 0;
        }

        List<Pair<String, Object>> data = new ArrayList<>();

        if (extras.mastery()) {
            data.add(Pair.of("spell_mastery", Component.translatable(spell.getSpellMastery().toString())));
        }
        if (extras.baseDamage() == 2 || (extras.baseDamage() == 1 && baseDamage > 0)) {
            data.add(Pair.of("damage", shouldShow ? baseDamage : HIDDEN));
        }
        if (extras.manaCost() == 2 || (extras.manaCost() == 1 && manaCost > 0)) {
            data.add(Pair.of("mana_cost", shouldShow ? manaCost : HIDDEN));
        }
        if (extras.castTime() == 2 || (extras.castTime() == 1 && castTime > 0)) {
            data.add(Pair.of("cast_time", shouldShow ? castTime : HIDDEN));
        }
        if (extras.duration() == 2 || (extras.duration() == 1 && duration > 0)) {
            data.add(Pair.of("duration", shouldShow ? duration : HIDDEN));
        }
        if (extras.manaPerTick() == 2 || (extras.manaPerTick() == 1 && manaPerTick > 0)) {
            data.add(Pair.of("mana_per_tick", shouldShow ? manaPerTick : HIDDEN));
        }

        ElementPosition position = element.position();
        int dataHeight = (data.size() *
                (Minecraft.getInstance().font.lineHeight + (element.extras().lineGap()/2))) + element.extras().lineGap()/2;

        graphics.blitSprite(DATA_SPRITE, leftPos + position.xOffset(), topPos + position.yOffset() - dataHeight, 147, dataHeight);
        graphics.blitSprite(TITLE_BOX_SPRITE, leftPos + position.xOffset(), topPos + position.yOffset() - dataHeight, 147, 17);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("guide.element.spell_info"), leftPos + position.xOffset() + 4, topPos + 5 - dataHeight + position.yOffset(), extras.colour(), extras.dropShadow());

        for (int i = 0; i < data.size(); i++) {
            Pair<String, Object> pair = data.get(i);
            if (pair.getSecond() instanceof Component value) drawString(pair.getFirst(), value, data.size() - i, leftPos, topPos, graphics, element);
            else if (pair.getSecond() instanceof Integer value) drawString(pair.getFirst(), value, data.size() - i, leftPos, topPos, graphics, element);
            else if (pair.getSecond() instanceof Float value) drawString(pair.getFirst(), value, data.size() - i, leftPos, topPos, graphics, element);
        }

    }

    private void drawString(String key, int value, int elementCount, int leftPos, int topPos, GuiGraphics graphics, GuideSpellInfoElement element) {
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("guide.element.spell_info." + key, value), leftPos + element.position().xOffset() + 4, topPos - 5 + element.position().yOffset() + (-elementCount * element.extras().lineGap()), element.extras().colour(), element.extras().dropShadow());
    }

    private void drawString(String key, Component value, int elementCount, int leftPos, int topPos, GuiGraphics graphics, GuideSpellInfoElement element) {
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("guide.element.spell_info." + key, value), leftPos + element.position().xOffset() + 4, topPos - 5 + element.position().yOffset() + (-elementCount * element.extras().lineGap()), element.extras().colour(), element.extras().dropShadow());
    }

    private void drawString(String key, float value, int elementCount, int leftPos, int topPos, GuiGraphics graphics, GuideSpellInfoElement element) {
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("guide.element.spell_info." + key, value), leftPos + element.position().xOffset() + 4, topPos - 5 + element.position().yOffset() + (-elementCount * element.extras().lineGap()), element.extras().colour(), element.extras().dropShadow());
    }

}
