package com.ombremoon.spellbound.common.magic.acquisition.guides.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.init.SBPageElements;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * This interface should be implemented by any and all elements to be used for the guide books.
 */
public interface IPageElement {
    Codec<IPageElement> CODEC = SBPageElements.REGISTRY
            .byNameCodec()
            .dispatch(IPageElement::codec, Function.identity());

    /**
     * The Codec for the datapack entry for this element
     * @return MapCodec for this element
     */
    @NotNull MapCodec<? extends IPageElement> codec();
}
