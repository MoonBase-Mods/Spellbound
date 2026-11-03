package com.ombremoon.spellbound.client.photon.data;

import com.lowdragmc.photon.client.gameobject.emitter.data.RendererSetting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class RendererBuilder extends ConfigDataBuilder {

    public static RendererBuilder of() {
        return new RendererBuilder();
    }

    public RendererBuilder materials(MaterialSettingBuilder... materials) {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (MaterialSettingBuilder material : materials) {
            listTag.add(material.build());
        }

        tag.put("payload", listTag);
        tag.putInt("uid", listTag.size());
        this.configData.put("materials", tag);
        return this;
    }

    public RendererBuilder layer(RendererSetting.Layer layer) {
        this.configData.putString("layer", layer.name());
        return this;
    }

    public RendererBuilder cull(boolean cull, @Nullable AABB cullBox) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("_enable", cull);

        if (cull && cullBox != null) {
            CompoundTag boxTag = new CompoundTag();
            ListTag max = new ListTag();
            max.add(DoubleTag.valueOf(cullBox.maxX));
            max.add(DoubleTag.valueOf(cullBox.maxY));
            max.add(DoubleTag.valueOf(cullBox.maxZ));
            boxTag.put("max", max);

            ListTag min = new ListTag();
            min.add(DoubleTag.valueOf(cullBox.minX));
            min.add(DoubleTag.valueOf(cullBox.minY));
            min.add(DoubleTag.valueOf(cullBox.minZ));
            boxTag.put("min", min);

            tag.put("cullBox", boxTag);
        }

        this.configData.put("cull", tag);
        return this;
    }

    public RendererBuilder cull(boolean cull) {
        return this.cull(cull, null);
    }

    public RendererBuilder orderInLayer(int order) {
        this.configData.putInt("orderInLayer", order);
        return this;
    }

    public RendererBuilder vertexSortingMode(SortMode mode) {
        this.configData.putString("vertexSortingMode", mode.name());
        return this;
    }

    public enum SortMode {
        NONE,
        DISTANCE
    }
}
