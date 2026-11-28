package com.ombremoon.spellbound.common.magic.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.DamageInstance;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBTags;
import com.ombremoon.spellbound.common.magic.SpellInstance;
import com.ombremoon.spellbound.common.magic.api.combat.AttackMoveset;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record Imbuement(Optional<SpellInstance> spellInstance, Optional<DamageInstance> instance, Optional<AttackMoveset> moveset, int charges, ResourceLocation glint) {
    private static final ResourceLocation DEFAULT_GLINT = CommonClass.customLocation("imbuement");
    public static final Imbuement EMPTY = new Imbuement(Optional.empty(), Optional.empty(), Optional.empty(), -1, DEFAULT_GLINT);
    public static final Codec<Imbuement> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    SpellInstance.CODEC.optionalFieldOf("spell").forGetter(Imbuement::spellInstance),
                    DamageInstance.CODEC.optionalFieldOf("instance").forGetter(Imbuement::instance),
                    AttackMoveset.CODEC.optionalFieldOf("moveset").forGetter(Imbuement::moveset),
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
        return new Imbuement(spellType, Optional.empty(), Optional.empty(), charges, glint);
    }

    public static Imbuement spellImbuement(@NotNull AbstractSpell spell) {
        return new Imbuement(Optional.of(SpellInstance.fromSpell(spell)), Optional.empty(), Optional.empty(), -1, spell.location());
    }

    public static Imbuement create() {
        return Imbuement.EMPTY;
    }

    public Imbuement addDamage(DamageInstance instance) {
        return new Imbuement(this.spellInstance, Optional.of(instance), this.moveset, this.charges, this.glint);
    }

    public Imbuement addMoveset(AttackMoveset moveset) {
        return new Imbuement(this.spellInstance, this.instance, Optional.of(moveset), this.charges, this.glint);
    }

    public Imbuement addCharges(int charges) {
        return new Imbuement(this.spellInstance, this.instance, this.moveset, charges, this.glint);
    }

    public Imbuement addGlint(ResourceLocation glint) {
        return new Imbuement(this.spellInstance, this.instance, this.moveset, this.charges, glint);
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

    public boolean outOfCharge() {
        return this.charges == 0;
    }

    public static boolean slotLocked(ItemStack stack) {
        Imbuement imbuement = stack.get(SBData.IMBUEMENT);
        return imbuement != null && imbuement.spellInstance().isPresent();
    }

    public static Imbuement getPriorityImbuement(LivingEntity living) {
        ItemStack stack = living.getMainHandItem();
        Imbuement imbuement = stack.get(SBData.IMBUEMENT);
        if (imbuement == null) {
            imbuement = living.getData(SBData.ENTITY_IMBUEMENT);
        }

        return imbuement;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Imbuement imbuement)) {
            return false;
        }

        if (this.charges != imbuement.charges()) {
            return false;
        }

        if (this.spellInstance.isPresent() != imbuement.spellInstance.isPresent()) {
            return false;
        }

        return this.spellInstance.map(value -> value.isSameSpell(imbuement.spellInstance().get())).orElse(true);

    }
}
