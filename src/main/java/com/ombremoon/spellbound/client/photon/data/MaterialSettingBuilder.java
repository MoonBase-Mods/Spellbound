package com.ombremoon.spellbound.client.photon.data;

public class MaterialSettingBuilder extends ConfigDataBuilder {

    public static MaterialSettingBuilder of() {
        return new MaterialSettingBuilder();
    }

    public MaterialSettingBuilder material(MaterialBuilder material) {
        this.configData.put("material", material.build());
        return this;
    }

    public MaterialSettingBuilder blendMode(BlendBuilder blend) {
        this.configData.put("blendMode", blend.build());
        return this;
    }

    public MaterialSettingBuilder cull(boolean cull) {
        this.configData.putBoolean("cull", cull);
        return this;
    }

    public MaterialSettingBuilder depthTest(boolean depthTest) {
        this.configData.putBoolean("depthTest", depthTest);
        return this;
    }

    public MaterialSettingBuilder depthMask(boolean depthMask) {
        this.configData.putBoolean("depthMask", depthMask);
        return this;
    }
}
