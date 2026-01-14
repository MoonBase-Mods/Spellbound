package com.ombremoon.spellbound.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.ombremoon.spellbound.client.shader.RenderEngine;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.Objects;
import java.util.function.BiFunction;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class SBRenderTypes {

    private static ShaderInstance rendertypeHeatDistortion;

    public static RenderType heatDistortion(ResourceLocation location) {
        return HEAT_DISTORTION.apply(location, RenderStateShard.NO_CULL);
    }

    static final BiFunction<ResourceLocation, RenderStateShard.CullStateShard, RenderType> HEAT_DISTORTION = Util.memoize(
            (location, cullStateShard) -> RenderType.create(
                    "heat_distortion",
                    DefaultVertexFormat.POSITION_TEX_COLOR,
                    VertexFormat.Mode.QUADS,
                    1536,
                    RenderType.CompositeState.builder()
                            .setShaderState(RenderStateShards.RENDERTYPE_BLOOM_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                            .setCullState(cullStateShard)
                            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                            .setOutputState(RenderStateShards.HEAT_DISTORTION_TARGET)
                            .createCompositeState(false)
            )
    );

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), CommonClass.customLocation("rendertype_heat_distortion"), DefaultVertexFormat.POSITION_TEX_COLOR), shaderInstance -> {
            rendertypeHeatDistortion = shaderInstance;
        });
    }

    public static class RenderStateShards {
        public static final RenderStateShard.ShaderStateShard RENDERTYPE_BLOOM_SHADER = new RenderStateShard.ShaderStateShard(RenderStateShards::getHeatDistortionShader);


        public static ShaderInstance getHeatDistortionShader() {
            return Objects.requireNonNull(rendertypeHeatDistortion, "Attempted to call getHeatDistortionShader before shaders have finished loading.");
        }

        public static final RenderStateShard.OutputStateShard HEAT_DISTORTION_TARGET = new RenderStateShard.OutputStateShard(
                "heat_distortion_target",
                () -> RenderEngine.getInstance().heatDistortionTarget().bindWrite(false),
                () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false)
        );
    }
}
