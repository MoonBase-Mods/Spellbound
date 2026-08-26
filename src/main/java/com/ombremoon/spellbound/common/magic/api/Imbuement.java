package com.ombremoon.spellbound.common.magic.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.DamageInstance;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBTags;
import com.ombremoon.spellbound.common.magic.SpellInstance;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record Imbuement(Optional<SpellInstance> spellInstance, Optional<DamageInstance> instance, int charges, ResourceLocation glint) {
    public static final Codec<Imbuement> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    SpellInstance.CODEC.optionalFieldOf("spell").forGetter(Imbuement::spellInstance),
                    DamageInstance.CODEC.optionalFieldOf("instance").forGetter(Imbuement::instance),
                    Codec.INT.fieldOf("charges").forGetter(Imbuement::charges),
                    ResourceLocation.CODEC.fieldOf("glint").forGetter(Imbuement::glint)
            ).apply(instance, Imbuement::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Imbuement> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(SpellInstance.STREAM_CODEC), Imbuement::spellInstance,
            ByteBufCodecs.VAR_INT, Imbuement::charges,
            ResourceLocation.STREAM_CODEC, Imbuement::glint,
            Imbuement::forStreamCodec
    );

    private static Imbuement forStreamCodec(Optional<SpellInstance> spellType, int charges, ResourceLocation glint) {
        return new Imbuement(spellType, Optional.empty(), charges, glint);
    }

    public static Imbuement empty() {
        return new Imbuement(Optional.empty(), Optional.empty(), -1, CommonClass.customLocation("imbuement"));
    }

    public static Imbuement create(@NotNull AbstractSpell spell, DamageInstance instance, int charges, ResourceLocation glint) {
        return new Imbuement(Optional.of(SpellInstance.fromSpell(spell)), Optional.ofNullable(instance), charges, glint);
    }

    public static Imbuement create(AbstractSpell spell, int charges, ResourceLocation glint) {
        return create(spell, null, charges, glint);
    }

    public static Imbuement create(AbstractSpell spell, DamageInstance instance, int charges) {
        return create(spell, instance, charges, spell.location());
    }

    public static Imbuement create(AbstractSpell spell, int charges) {
        return create(spell, charges, spell.location());
    }

    public static Imbuement create(AbstractSpell spell) {
        return create(spell, -1);
    }

    public static Imbuement create(DamageInstance instance, int charges, ResourceLocation glint) {
        return new Imbuement(Optional.empty(), Optional.of(instance), charges, glint);
    }

    public AbstractSpell getSpellFromInstance(LivingEntity caster) {
        return this.spellInstance.map(value -> value.getOrCreateSpell(caster)).orElse(null);

    }

    public boolean addsDamage() {
        return this.instance.isPresent();
    }

    public boolean canImbueStack(ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (!stack.is(SBTags.Items.IMBUEABLE))
            return false;

        Imbuement imbuement = stack.get(SBData.IMBUEMENT);
        return imbuement == null || (imbuement.spellInstance.isPresent() && this.spellInstance.isPresent() && imbuement.spellInstance.get().isSameSpell(this.spellInstance().get()));
    }

    public Imbuement setCharges(int charges) {
        return new Imbuement(this.spellInstance, this.instance, charges, this.glint);
    }

    /*public static boolean hasImbuement(Imbuement imbuement, ItemStack stack) {
        Imbuement other = stack.get(SBData.IMBUEMENT);
        return other != null && imbuement.spellInstance == other.spellInstance;
    }*/
}
