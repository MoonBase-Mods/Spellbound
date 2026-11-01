package com.ombremoon.spellbound.client.photon.data;

import com.lowdragmc.photon.client.gameobject.emitter.data.RendererSetting;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleRendererSetting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class ParticleRendererBuilder extends RendererBuilder {

    public static ParticleRendererBuilder of() {
        return new ParticleRendererBuilder();
    }

    public ParticleRendererBuilder renderMode(Mode mode) {
        this.configData.putString("renderMode", mode.name());
        return this;
    }

    public ParticleRendererBuilder model(ResourceLocation model) {
        CompoundTag tag = new CompoundTag();
        tag.putString("model", model.toString());
        this.configData.put("model", tag);
        return this;
    }

    public ParticleRendererBuilder shade(boolean shade) {
        this.configData.putBoolean("shade", shade);
        return this;
    }

    public ParticleRendererBuilder useBlockUV(boolean useBlockUV) {
        this.configData.putBoolean("useBlockUV", useBlockUV);
        return this;
    }

    public ParticleRendererBuilder modelPivot(float x, float y, float z) {
        ListTag listTag = new ListTag();
        listTag.add(FloatTag.valueOf(x));
        listTag.add(FloatTag.valueOf(y));
        listTag.add(FloatTag.valueOf(z));
        this.configData.put("modelPivot", listTag);
        return this;
    }

    public ParticleRendererBuilder useGPUInstance(boolean useGPUInstance) {
        this.configData.putBoolean("useGPUInstance", useGPUInstance);
        return this;
    }

    public ParticleRendererBuilder materials(MaterialSettingBuilder... materials) {
        super.materials(materials);
        return this;
    }

    public ParticleRendererBuilder layer(RendererSetting.Layer layer) {
        super.layer(layer);
        return this;
    }

    public ParticleRendererBuilder cull(boolean cull, @Nullable AABB cullBox) {
        super.cull(cull, cullBox);
        return this;
    }

    public ParticleRendererBuilder cull(boolean cull) {
        return this.cull(cull, null);
    }

    public ParticleRendererBuilder orderInLayer(int order) {
        super.orderInLayer(order);
        return this;
    }

    public ParticleRendererBuilder vertexSortingMode(SortMode mode) {
        super.vertexSortingMode(mode);
        return this;
    }

    public enum Mode {
        Billboard,
        Horizontal,
        Vertical,
        VerticalBillboard,
        Model
    }
}
