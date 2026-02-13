package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> {

    @Shadow
    public ModelPart body;

    @Shadow
    @Final
    public ModelPart head;

    @Inject(
        method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/model/geom/ModelPart;y:F",
            opcode = Opcodes.PUTFIELD,
            ordinal = 4
        )
    )
    private void modifyBodyY(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        var handler = SpellUtil.getSpellHandler(entity);
        if (handler.inCastMode()) {
            this.body.y = 0;
            this.head.y = 0;
        }
    }
}
