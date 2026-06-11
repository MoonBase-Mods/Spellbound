package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    protected abstract BlockPos getOnPos(float offset);

    @Shadow
    private Level level;

    @Shadow
    public abstract BlockPos getOnPos();

    @Inject(method = "canSpawnSprintParticle", at = @At("RETURN"), cancellable = true)
    private void disableSprintParticles(CallbackInfoReturnable<Boolean> cir) {
        if (this.self() instanceof LivingEntity living && living.hasEffect(SBEffects.MAGI_INVISIBILITY))
            cir.setReturnValue(false);
    }

    @Inject(method = "getBlockPosBelowThatAffectsMyMovement", at = @At("RETURN"), cancellable = true)
    private void getLowerPositionWhenSkating(CallbackInfoReturnable<BlockPos> cir) {
        BlockPos blockPos = this.getOnPos();
        BlockState state = this.self().level().getBlockState(blockPos);
        if (state.is(SBBlocks.ICE_SHEET.get())) {
            cir.setReturnValue(blockPos);
        }

    }

    @Inject(method = "getOnPosLegacy", at = @At("RETURN"), cancellable = true)
    private void getLowerPosWhenSkating(CallbackInfoReturnable<BlockPos> cir) {
        BlockPos pos = this.getOnPos(0.2F);
        BlockState blockState = this.level.getBlockState(this.getOnPos());
        if (blockState.is(SBBlocks.ICE_SHEET.get())) {
            pos = this.getOnPos();
        }

        cir.setReturnValue(pos);
    }

    private Entity self() {
        return (Entity) (Object) this;
    }
}
