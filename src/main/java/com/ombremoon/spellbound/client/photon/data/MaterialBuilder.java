package com.ombremoon.spellbound.client.photon.data;

import com.lowdragmc.lowdraglib2.editor.resource.IResourcePath;
import com.lowdragmc.photon.client.gameobject.emitter.data.material.TextureMaterial;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4f;

public abstract class MaterialBuilder extends ConfigDataBuilder {

    public static class Texture extends MaterialBuilder {

        public static Texture of() {
            return new Texture();
        }

        public Texture texture(ResourceLocation texture) {
            this.configData.putString("texture", texture.toString());
            return this;
        }

        public Texture discardThreshold(float discardThreshold) {
            this.configData.putFloat("discardThreshold", discardThreshold);
            return this;
        }

        public Texture hdr(float r, float g, float b, float a) {
            ListTag listTag = new ListTag();
            listTag.add(FloatTag.valueOf(r));
            listTag.add(FloatTag.valueOf(g));
            listTag.add(FloatTag.valueOf(b));
            listTag.add(FloatTag.valueOf(a));
            this.configData.put("hdr", listTag);
            return this;
        }

        public Texture hdr(Vector4f hdr) {
            return this.hdr(hdr.x, hdr.y, hdr.z, hdr.w);
        }

        public Texture hdrMode(TextureMaterial.HDRMode mode) {
            this.configData.putString("hdrMode", mode.name());
            return this;
        }

        public Texture pixelArt(boolean pixelArt, int bits) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("_enable", pixelArt);

            if (pixelArt) {
                tag.putInt("bits", bits);
            }

            this.configData.put("pixelArt", tag);
            return this;
        }

        public Texture pixelArt(boolean pixelArt) {
            return this.pixelArt(pixelArt, 8);
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "texture");
            return tag;
        }
    }

    public static class UIResource extends MaterialBuilder {

        public static UIResource of() {
            return new UIResource();
        }

        public UIResource resourcePath(IResourcePath resourcePath) {
            this.configData.putString("resourcePath", resourcePath.getPathWithType());
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "ui_resource_material");
            return tag;
        }
    }

    public static class Sprite extends MaterialBuilder {

        public static Sprite of() {
            return new Sprite();
        }

        public Sprite spriteLocation(ResourceLocation spriteLocation) {
            this.configData.putString("spriteLocation", spriteLocation.toString());
            return this;
        }

        public Sprite discardThreshold(float discardThreshold) {
            this.configData.putFloat("discardThreshold", discardThreshold);
            return this;
        }

        public Sprite hdr(float r, float g, float b, float a) {
            ListTag listTag = new ListTag();
            listTag.add(FloatTag.valueOf(r));
            listTag.add(FloatTag.valueOf(g));
            listTag.add(FloatTag.valueOf(b));
            listTag.add(FloatTag.valueOf(a));
            this.configData.put("hdr", listTag);
            return this;
        }

        public Sprite hdr(Vector4f hdr) {
            return this.hdr(hdr.x, hdr.y, hdr.z, hdr.w);
        }

        public Sprite hdrMode(TextureMaterial.HDRMode mode) {
            this.configData.putString("hdrMode", mode.name());
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "sprite");
            return tag;
        }
    }

    public static class Shader extends MaterialBuilder {

        public static Shader of() {
            return new Shader();
        }

        public Shader shaderLocation(ResourceLocation shaderLocation) {
            this.configData.putString("shaderLocation", shaderLocation.toString());
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "custom_shader");
            return tag;
        }
    }
}
