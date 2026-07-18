package com.ombremoon.spellbound.client.gui.guide.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.ElementPosition;
import com.ombremoon.spellbound.client.gui.guide.elements.extras.TextListExtras;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IClickable;
import com.ombremoon.spellbound.client.gui.guide.elements.special.IInteractable;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public record GuideTextListElement(List<ScrapComponent> list, TextListExtras extras, ElementPosition position) implements IPageElement, IClickable {
    public static final MapCodec<GuideTextListElement> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ScrapComponent.CODEC.listOf().fieldOf("list").forGetter(GuideTextListElement::list),
            TextListExtras.CODEC.optionalFieldOf("extras", TextListExtras.getDefault()).forGetter(GuideTextListElement::extras),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideTextListElement::position)
    ).apply(inst, GuideTextListElement::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }

    public record ScrapComponent(Component component, ResourceLocation scrap, int extraOffset, ResourceLocation targetPage, boolean notAdded) {
        static final Codec<ScrapComponent> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ComponentSerialization.CODEC.fieldOf("component").forGetter(ScrapComponent::component),
                        ResourceLocation.CODEC.fieldOf("scrap").forGetter(ScrapComponent::scrap),
                        Codec.INT.fieldOf("extraOffset").forGetter(ScrapComponent::extraOffset),
                        ResourceLocation.CODEC.fieldOf("target_page").forGetter(ScrapComponent::targetPage),
                        Codec.BOOL.fieldOf("not_added").forGetter(ScrapComponent::notAdded)
                ).apply(instance, ScrapComponent::new)
        );

        public ScrapComponent(Component component, int extraOffset) {
            this(component, CommonClass.customLocation("default"), extraOffset, CommonClass.customLocation("default"), false);
        }

        public ScrapComponent(Component component, ResourceLocation scrap) {
            this(component, scrap, 0, CommonClass.customLocation("default"), false);
        }

        public ScrapComponent(Component component) {
            this(component, CommonClass.customLocation("default"), 0, CommonClass.customLocation("default"), false);
        }

        public ScrapComponent(Component component, ResourceLocation scrap, ResourceLocation targetPage) {
            this(component, scrap, 0, targetPage, false);
        }

        public ScrapComponent(Component component, ResourceLocation scrap, int extraOffset, ResourceLocation targetPage) {
            this(component, scrap, extraOffset, targetPage, false);
        }

        public ScrapComponent(Component text, ResourceLocation scrap, ResourceLocation targetPage, boolean notAdded) {
            this(text, scrap, 0, targetPage, notAdded);
        }
    }
}
