package com.ombremoon.spellbound.client.gui.guide.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.init.SBPageElements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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

    default RenderOrder getRenderOrder() {return RenderOrder.STANDARD;}

    enum RenderOrder {
        BELOW,
        STANDARD,
        ABOVE
    }

}
