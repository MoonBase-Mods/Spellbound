package com.ombremoon.spellbound.common.magic.acquisition.guides.elements;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.ElementPosition;
import com.ombremoon.spellbound.common.magic.acquisition.guides.elements.extras.RecipeExtras;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record GuideRecipe(ResourceLocation recipeLoc, String gridName, float scale, ElementPosition position, RecipeExtras extras) implements IPageElement {

    public static final MapCodec<GuideRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("recipe").forGetter(GuideRecipe::recipeLoc),
            Codec.STRING.optionalFieldOf("gridName", "basic").forGetter(GuideRecipe::gridName),
            Codec.FLOAT.optionalFieldOf("scale", 1f).forGetter(GuideRecipe::scale),
            ElementPosition.CODEC.optionalFieldOf("position", ElementPosition.getDefault()).forGetter(GuideRecipe::position),
            RecipeExtras.CODEC.optionalFieldOf("extras", RecipeExtras.getDefault()).forGetter(GuideRecipe::extras)
    ).apply(inst, GuideRecipe::new));

    @Override
    public @NotNull MapCodec<? extends IPageElement> codec() {
        return CODEC;
    }
}
