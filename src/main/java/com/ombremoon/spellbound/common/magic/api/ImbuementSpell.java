package com.ombremoon.spellbound.common.magic.api;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.SpellContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public abstract class ImbuementSpell extends AnimatedSpell {
    private InteractionHand imbuedHand;

    public ImbuementSpell(SpellType<?> spellType, Builder<?> builder) {
        super(spellType, builder);
    }

    public ResourceLocation getImbuementFX() {
        return spellType().location().withPrefix("imbuement/");
    }

    protected InteractionHand pickImbuementHand(SpellContext context) {
        if (!context.getMainHandItem().isEmpty()) return InteractionHand.MAIN_HAND;
        if (!context.getOffHandItem().isEmpty()) return InteractionHand.OFF_HAND;
        return null;
    }

    @Override
    protected void onSpellStart(SpellContext context) {
        if (context.getLevel().isClientSide) return;

        InteractionHand hand = pickImbuementHand(context);
        if (hand == null) return;

        LivingEntity caster = context.getCaster();
        ItemStack stack = caster.getItemInHand(hand);
        if (stack.isEmpty()) return;

        stack.set(SBData.IMBUEMENT.get(), spellType());
        this.imbuedHand = hand;
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        if (context.getLevel().isClientSide) return;
        if (this.imbuedHand == null) return;

        LivingEntity caster = context.getCaster();
        ItemStack stack = caster.getItemInHand(this.imbuedHand);
        SpellType<?> current = stack.get(SBData.IMBUEMENT.get());
        if (current != null && current == spellType()) {
            stack.remove(SBData.IMBUEMENT.get());
        }
        this.imbuedHand = null;
    }
}
