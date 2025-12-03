package com.ombremoon.spellbound.common.world.weather;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class ClientHailstormData implements HailstormData {
    private static final ResourceLocation HAIL_LOCATION = CommonClass.customLocation("textures/environment/hail.png");

    private boolean hailing;
    private float oHailLevel;
    private float hailLevel;

    @Override
    public boolean isHailing() {
        return this.hailing;
    }

    @Override
    public void setHailing(boolean hailing) {
        this.hailing = hailing;
    }

    @Override
    public float getHailLevel(float delta) {
        return Mth.lerp(delta, this.oHailLevel, this.hailLevel);
    }

    @Override
    public void setHailLevel(float strength) {
        float f = Mth.clamp(strength, 0.0F, 1.0F);
        this.oHailLevel = f;
        this.hailLevel = f;
    }

    @Override
    public void prepareHail() {
       if (this.isHailing())
           this.hailLevel = 1.0F;
    }

    public void renderHailstorm(RenderLevelStageEvent event) {
        LevelRenderer renderer = event.getLevelRenderer();
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        LightTexture lightTexture = gameRenderer.lightTexture();
        Camera camera = event.getCamera();
        Vec3 pos = camera.getPosition();
        double camX = pos.x();
        double camY = pos.y();
        double camZ = pos.z();
        ClientLevel level = Minecraft.getInstance().level;
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        if (level.effects().renderSnowAndRain(level, event.getRenderTick(), partialTick, lightTexture, camX, camY, camZ))
            return;

        float f = HailstormSavedData.get(level).getHailLevel(partialTick);
        if (!(f <= 0)) {
            lightTexture.turnOnLightLayer();
            int i = Mth.floor(camX);
            int j = Mth.floor(camY);
            int k = Mth.floor(camZ);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = null;
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            int l = 5;
            if (Minecraft.useFancyGraphics())
                l = 7;

            RenderSystem.depthMask(Minecraft.useShaderTransparency());
            int i1 = -1;
            float f1 = (float) event.getRenderTick() + partialTick;
            RenderSystem.setShader(GameRenderer::getParticleShader);
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for (int j1 = k - l; j1 <= k + l; j1++) {
                for (int k1 = i - l; k1 <= i + l; k1++) {
                    int l1 = (j1 - k + 16) * 32 + k1 - i + 16;
                    double d0 = (double) renderer.rainSizeX[l1] * 0.5;
                    double d1 = (double) renderer.rainSizeZ[l1] * 0.5;
                    mutableBlockPos.set(k1, camY, j1);
                    Biome biome = level.getBiome(mutableBlockPos).value();
                    if (biome.hasPrecipitation()) {
                        int i2 = level.getHeight(Heightmap.Types.MOTION_BLOCKING, k1, j1);
                        int j2 = j - l;
                        int k2 = j + l;
                        if (j2 < i2) {
                            j2 = i2;
                        }

                        if (k2 < i2) {
                            k2 = i2;
                        }

                        int l2 = i2;
                        if (i2 < j) {
                            l2 = j;
                        }

                        if (j2 != k2) {
                            RandomSource randomSource = RandomSource.create(((long) k1 * k1 * 3121 + k1 * 45238971L ^ (long) j1 * j1 * 418711 + j1 * 13761L));
                            mutableBlockPos.set(k1, j2, j1);
                            Biome.Precipitation precipitation = biome.getPrecipitationAt(mutableBlockPos);
                            if (precipitation != Biome.Precipitation.NONE) {
                                if (i1 != 1) {
                                    if (i1 >= 0) {
                                        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
                                    }

                                    i1 = 1;
                                    RenderSystem.setShaderTexture(0, HAIL_LOCATION);
                                    bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                                }

                                float f8 = -((float)(event.getRenderTick() & 511) + partialTick) / 32.0F;
                                float f9 = (float)(randomSource.nextDouble() + (double)f1 * 0.01 * (double)((float)randomSource.nextGaussian()));
                                float f10 = (float)(randomSource.nextDouble() + (double)(f1 * (float)randomSource.nextGaussian()) * 0.001);
                                double d4 = (double)k1 + 0.5 - camX;
                                double d5 = (double)j1 + 0.5 - camZ;
                                float f11 = (float)Math.sqrt(d4 * d4 + d5 * d5) / (float)l;
                                float f5 = ((1.0F - f11 * f11) * 0.3F + 0.5F) * f;
                                mutableBlockPos.set(k1, l2, j1);
                                int j4 = LevelRenderer.getLightColor(level, mutableBlockPos);
                                int k4 = j4 >> 16 & 65535;
                                int l4 = j4 & 65535;
                                int l3 = (k4 * 3 + 240) / 4;
                                int i4 = (l4 * 3 + 240) / 4;
                                bufferBuilder.addVertex(
                                                (float)((double)k1 - camX - d0 + 0.5), (float)((double)k2 - camY), (float)((double)j1 - camZ - d1 + 0.5)
                                        )
                                        .setUv(0.0F + f9, (float)j2 * 0.25F + f8 + f10)
                                        .setColor(1.0F, 1.0F, 1.0F, f5)
                                        .setUv2(i4, l3);
                                bufferBuilder.addVertex(
                                                (float)((double)k1 - camX + d0 + 0.5), (float)((double)k2 - camY), (float)((double)j1 - camZ + d1 + 0.5)
                                        )
                                        .setUv(1.0F + f9, (float)j2 * 0.25F + f8 + f10)
                                        .setColor(1.0F, 1.0F, 1.0F, f5)
                                        .setUv2(i4, l3);
                                bufferBuilder.addVertex(
                                                (float)((double)k1 - camX + d0 + 0.5), (float)((double)j2 - camY), (float)((double)j1 - camZ + d1 + 0.5)
                                        )
                                        .setUv(1.0F + f9, (float)k2 * 0.25F + f8 + f10)
                                        .setColor(1.0F, 1.0F, 1.0F, f5)
                                        .setUv2(i4, l3);
                                bufferBuilder.addVertex(
                                                (float)((double)k1 - camX - d0 + 0.5), (float)((double)j2 - camY), (float)((double)j1 - camZ - d1 + 0.5)
                                        )
                                        .setUv(0.0F + f9, (float)k2 * 0.25F + f8 + f10)
                                        .setColor(1.0F, 1.0F, 1.0F, f5)
                                        .setUv2(i4, l3);
                            }
                        }
                    }
                }
            }

            if (i1 > 0)
                BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            lightTexture.turnOffLightLayer();
        }
    }

    public void renderHailstormFog(ViewportEvent.ComputeFogColor event) {
        float f = this.getHailLevel((float) event.getPartialTick());
        f *= f;
        if (f > 0.0F) {
            float f1 = 1.0F - f * 0.5F;
            event.setRed(event.getRed() * f1);
            event.setBlue(event.getBlue() * f1);
            event.setGreen(event.getGreen() * f1);
        }
    }
}
