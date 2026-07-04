package com.ombremoon.spellbound.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.main.CommonClass;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Supplier;

public class ImbuementRenderers extends RenderType {
    public static final ResourceLocation BLACK_BLADE = CommonClass.customLocation("smite_black_blade");
    private static final Map<ResourceLocation, RenderType> IMBUEMENTS = new Object2ObjectArrayMap<>();

    private static final RenderType SMITE_GLINT = create("smite_glint");
    private static final RenderType BLACK_BLADE_GLINT = create("black_blade_glint");
    private static final RenderType NIGHTBLADE_GLINT = create("nightblade_glint");
    private static final RenderType SMITE_ENTITY_GLINT_DIRECT = create("smite_entity_glint_direct");

    public ImbuementRenderers(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static void registerImbuements() {
        put(SBSpells.SMITE, getSmiteGlint());
        put(BLACK_BLADE, getBlackBladeGlint());
        put(SBSpells.NIGHTBLADE, getNightbladeGlint());
    }

    public static RenderType getGlint(ResourceLocation location) {
        return IMBUEMENTS.getOrDefault(location, RenderType.GLINT);
    }

    private static void put(ResourceLocation location, RenderType renderType) {
        IMBUEMENTS.put(location, renderType);
    }

    private static <T extends AbstractSpell> void put(Supplier<SpellType<T>> spell, RenderType renderType) {
        IMBUEMENTS.put(spell.get().location(), renderType);
    }

    public static RenderType getSmiteGlint() {
        return SMITE_GLINT;
    }

    public static RenderType getBlackBladeGlint() {
        return BLACK_BLADE_GLINT;
    }

    public static RenderType getNightbladeGlint() {
        return NIGHTBLADE_GLINT;
    }

    public static RenderType getSmiteEntityGlintDirect() {
        return SMITE_ENTITY_GLINT_DIRECT;
    }

    protected static RenderType create(String glintName) {
        ResourceLocation texture = CommonClass.customLocation("textures/imbuement/" + glintName + ".png");
        return create(
                glintName,
                DefaultVertexFormat.POSITION_TEX,
                VertexFormat.Mode.QUADS,
                1536,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_GLINT_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
                        .setWriteMaskState(COLOR_WRITE)
                        .setCullState(NO_CULL)
                        .setDepthTestState(EQUAL_DEPTH_TEST)
                        .setTransparencyState(GLINT_TRANSPARENCY)
                        .setTexturingState(GLINT_TEXTURING)
                        .createCompositeState(false));
    }
}
