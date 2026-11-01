package com.ombremoon.spellbound.client.photon.data;

import net.minecraft.nbt.ListTag;

public class ShapeSettingBuilder extends ConfigDataBuilder {

    public static ShapeSettingBuilder of() {
        return new ShapeSettingBuilder();
    }

    public ShapeSettingBuilder shape(ShapeBuilder shape) {
        this.configData.put("shape", shape.build());
        return this;
    }

    public ShapeSettingBuilder position(NumberFunctionBuilder x, NumberFunctionBuilder y, NumberFunctionBuilder z) {
        ListTag listTag = new ListTag();
        listTag.add(x.build());
        listTag.add(y.build());
        listTag.add(z.build());
        this.configData.put("position", listTag);
        return this;
    }

    public ShapeSettingBuilder rotation(NumberFunctionBuilder x, NumberFunctionBuilder y, NumberFunctionBuilder z) {
        ListTag listTag = new ListTag();
        listTag.add(x.build());
        listTag.add(y.build());
        listTag.add(z.build());
        this.configData.put("rotation", listTag);
        return this;
    }

    public ShapeSettingBuilder scale(NumberFunctionBuilder x, NumberFunctionBuilder y, NumberFunctionBuilder z) {
        ListTag listTag = new ListTag();
        listTag.add(x.build());
        listTag.add(y.build());
        listTag.add(z.build());
        this.configData.put("scale", listTag);
        return this;
    }
}
