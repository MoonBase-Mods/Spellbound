package com.ombremoon.spellbound.mixin;

import com.ombremoon.spellbound.common.world.block.ExtendedBlock;
import com.ombremoon.spellbound.common.world.weather.ClientHailstormData;
import com.ombremoon.spellbound.common.world.weather.HailstormSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Shadow
    @Final
    private LevelRenderer levelRenderer;

    @Inject(method = "getSkyDarken", at = @At("RETURN"), cancellable = true)
    private void getSkyDarken(float partialTick, CallbackInfoReturnable<Float> cir) {
        ClientLevel level = Minecraft.getInstance().level;
        ClientHailstormData data = (ClientHailstormData) HailstormSavedData.get(level);
        float f = level.getTimeOfDay(partialTick);
        float f1 = 1.0F - (Mth.cos(f * (float) (Math.PI * 2)) * 2.0F + 0.2F);
        float f2 = data.getHailLevel(partialTick) * data.getHailLevel(partialTick);
        f1 = Mth.clamp(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 *= 1.0F - level.getRainLevel(partialTick) * 5.0F / 16.0F;
        f1 *= 1.0F - level.getThunderLevel(partialTick) * 5.0F / 16.0F;
        f1 *= 1.0F - f2 * 5.0F / 16.0F;
        cir.setReturnValue(Float.valueOf(f1 * 0.8F + 0.2F));
    }

    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void getSkyColor(Vec3 pos, float partialTick, CallbackInfoReturnable<Vec3> cir) {
        ClientLevel level = Minecraft.getInstance().level;
        float f = level.getTimeOfDay(partialTick);
        Vec3 vec3 = pos.subtract(2.0, 2.0, 2.0).scale(0.25);
        BiomeManager biomemanager = level.getBiomeManager();
        Vec3 vec31 = CubicSampler.gaussianSampleVec3(
                vec3, (p_194161_, p_194162_, p_194163_) -> Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(p_194161_, p_194162_, p_194163_).value().getSkyColor())
        );
        float f1 = Mth.cos(f * (float) (Math.PI * 2)) * 2.0F + 0.5F;
        f1 = Mth.clamp(f1, 0.0F, 1.0F);
        float f2 = (float)vec31.x * f1;
        float f3 = (float)vec31.y * f1;
        float f4 = (float)vec31.z * f1;
        ClientHailstormData data = (ClientHailstormData) HailstormSavedData.get(level);
        float f6 = data.getHailLevel(partialTick);
        f6 *= f6;
        if (f6 > 0.0F) {
            float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
            float f8 = 1.0F - f6 * 0.75F;
            f2 = f2 * f8 + f10 * (1.0F - f8);
            f3 = f3 * f8 + f10 * (1.0F - f8);
            f4 = f4 * f8 + f10 * (1.0F - f8);
        }
        cir.setReturnValue(new Vec3(f2, f3, f4));
    }

    @Inject(method = "getCloudColor", at = @At("TAIL"), cancellable = true)
    private void getCloudColor(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        ClientLevel level = Minecraft.getInstance().level;
        ClientHailstormData data = (ClientHailstormData) HailstormSavedData.get(level);
        float f = level.getTimeOfDay(partialTick);
        float f1 = Mth.cos(f * (float) (Math.PI * 2)) * 2.0F + 0.5F;
        f1 = Mth.clamp(f1, 0.0F, 1.0F);
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 1.0F;

        f2 *= f1 * 0.9F + 0.1F;
        f3 *= f1 * 0.9F + 0.1F;
        f4 *= f1 * 0.85F + 0.15F;
        float f5 = data.getHailLevel(partialTick);
        f5 *= f5;
        if (f5 > 0.0F) {
            float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
            float f7 = 1.0F - f5 * 0.95F;
            f2 = f2 * f7 + f6 * (1.0F - f7);
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
        }
        cir.setReturnValue(new Vec3(f2, f3, f4));
    }

    @Inject(method = "destroyBlockProgress", at = @At(value = "HEAD"), cancellable = true)
    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress, CallbackInfo ci) {
        ClientLevel level = (ClientLevel) (Object)this;
        BlockState blockState = level.getBlockState(pos);

        if (blockState.getBlock() instanceof ExtendedBlock multiBlock && !blockState.getRenderShape().equals(RenderShape.MODEL)) {

            levelRenderer.destroyBlockProgress(breakerId, multiBlock.getCenter(level, pos), progress);

            ci.cancel();
        }
    }
}
