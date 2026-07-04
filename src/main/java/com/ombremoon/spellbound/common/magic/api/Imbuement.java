package com.ombremoon.spellbound.common.magic.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.init.SBTags;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record Imbuement(SpellType<?> spellType, int charges, ResourceLocation glint) {
    public static final Codec<Imbuement> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    SBSpells.REGISTRY.byNameCodec().fieldOf("spell").forGetter(Imbuement::spellType),
                    Codec.INT.fieldOf("charges").forGetter(Imbuement::charges),
                    ResourceLocation.CODEC.fieldOf("glint").forGetter(Imbuement::glint)
            ).apply(instance, Imbuement::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Imbuement> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(SBSpells.SPELL_TYPE_REGISTRY_KEY), Imbuement::spellType,
            ByteBufCodecs.VAR_INT, Imbuement::charges,
            ResourceLocation.STREAM_CODEC, Imbuement::glint,
            Imbuement::new
    );

    public boolean canImbueStack(ItemStack stack) {
        if (!stack.is(SBTags.Items.IMBUEABLE)) {
            return false;
        }

        Imbuement imbuement = stack.get(SBData.IMBUEMENT);
        return imbuement == null || imbuement.spellType == this.spellType;
    }

    public static boolean hasImbuement(Imbuement imbuement, ItemStack stack) {
        Imbuement other = stack.get(SBData.IMBUEMENT);
        return other != null && imbuement.spellType == other.spellType;
    }
}
