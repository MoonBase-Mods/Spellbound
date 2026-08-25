package com.ombremoon.spellbound.common.magic.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.DamageInstance;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.init.SBTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record Imbuement(Optional<SpellType<?>> spellType, Optional<DamageInstance> instance, int charges, ResourceLocation glint) {
    public static final Codec<Imbuement> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    SBSpells.REGISTRY.byNameCodec().optionalFieldOf("spell").forGetter(Imbuement::spellType),
                    DamageInstance.CODEC.optionalFieldOf("instance").forGetter(Imbuement::instance),
                    Codec.INT.fieldOf("charges").forGetter(Imbuement::charges),
                    ResourceLocation.CODEC.fieldOf("glint").forGetter(Imbuement::glint)
            ).apply(instance, Imbuement::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Imbuement> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.registry(SBSpells.SPELL_TYPE_REGISTRY_KEY)), Imbuement::spellType,
            ByteBufCodecs.VAR_INT, Imbuement::charges,
            ResourceLocation.STREAM_CODEC, Imbuement::glint,
            Imbuement::forStreamCodec
    );

    private static Imbuement forStreamCodec(Optional<SpellType<?>> spellType, int charges, ResourceLocation glint) {
        return new Imbuement(spellType, Optional.empty(), charges, glint);
    }

    public static Imbuement create(SpellType<?> spellType, DamageInstance instance, int charges, ResourceLocation glint) {
        return new Imbuement(Optional.of(spellType), Optional.of(instance), charges, glint);
    }

    public static Imbuement create(SpellType<?> spellType, int charges, ResourceLocation glint) {
        return new Imbuement(Optional.of(spellType), Optional.empty(), charges, glint);
    }

    public static Imbuement create(DamageInstance instance, int charges, ResourceLocation glint) {
        return new Imbuement(Optional.empty(), Optional.of(instance), charges, glint);
    }

    public boolean canImbueStack(ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (!stack.is(SBTags.Items.IMBUEABLE))
            return false;

        Imbuement imbuement = stack.get(SBData.IMBUEMENT);
        return imbuement == null || (imbuement.spellType.isPresent() && this.spellType.isPresent() && imbuement.spellType.get() == this.spellType.get());
    }

    public Imbuement setCharges(int charges) {
        return new Imbuement(this.spellType, this.instance, charges, this.glint);
    }

    /*public static boolean hasImbuement(Imbuement imbuement, ItemStack stack) {
        Imbuement other = stack.get(SBData.IMBUEMENT);
        return other != null && imbuement.spellType == other.spellType;
    }*/
}
