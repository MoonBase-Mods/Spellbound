package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.api.Imbuement;
import com.ombremoon.spellbound.common.world.SpellDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.player.Player;
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
        if (imbuement != null /*or player is imbued*/) {
            SpellDamageSource source = SpellDamageSource.fromVanillaSource(cir.getReturnValue());
            var optional = imbuement.instance();
            optional.ifPresent(source::addDamageInstance);
            cir.setReturnValue(source);
        }
    }
}
