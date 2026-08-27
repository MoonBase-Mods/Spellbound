package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.Imbuement;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.world.SpellDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSources.class)
public class DamageSourcesMixin {

    @Inject(method = "playerAttack", at = @At("RETURN"), cancellable = true)
    private void onPlayerAttack(Player player, CallbackInfoReturnable<DamageSource> cir) {
        ItemStack stack = player.getWeaponItem();
        Imbuement imbuement = stack.get(SBData.IMBUEMENT);
        Imbuement playerImbuement = player.getData(SBData.ENTITY_IMBUEMENT);
        if (imbuement == null && playerImbuement.addsDamage())
            imbuement = playerImbuement;

        if (imbuement != null) {
            AbstractSpell spell = imbuement.getSpellFromInstance(player);
            SpellDamageSource source = SpellDamageSource.fromVanillaSource(spell, cir.getReturnValue());
            var optional = imbuement.instance();
            optional.ifPresent(source::addDamageInstance);
            cir.setReturnValue(source);
        }
    }

    @Inject(method = "arrow", at = @At("RETURN"), cancellable = true)
    private void onArrowHit(AbstractArrow arrow, Entity shooter, CallbackInfoReturnable<DamageSource> cir) {
        Imbuement imbuement = arrow.getData(SBData.ENTITY_IMBUEMENT);
        if (imbuement.addsDamage()) {
            AbstractSpell spell = shooter instanceof LivingEntity living ? imbuement.getSpellFromInstance(living) : null;
            SpellDamageSource source = SpellDamageSource.fromVanillaSource(spell, cir.getReturnValue());
            var optional = imbuement.instance();
            optional.ifPresent(source::addDamageInstance);
            cir.setReturnValue(source);
        }
    }
}
