package com.ombremoon.spellbound.client.photon.data;

import com.lowdragmc.photon.client.gameobject.emitter.data.shape.Box;
import com.lowdragmc.photon.client.gameobject.emitter.data.shape.Mesh;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class ShapeBuilder extends ConfigDataBuilder {

    public static class Dot extends ShapeBuilder {

        public static Dot create() {
            return new Dot();
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "dot");
            return tag;
        }
    }

    public static class Cube extends ShapeBuilder {

        public static Cube create() {
            return new Cube();
        }

        public Cube emitFrom(Box.Type emitFrom) {
            this.configData.putString("emitFrom", emitFrom.name());
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "box");
            return tag;
        }
    }

    public static class Sphere extends ShapeBuilder {

        public static Sphere create() {
            return new Sphere();
        }

        public Sphere radius(float radius) {
            this.configData.putFloat("radius", Mth.clamp(radius, 0, 1000));
            return this;
        }

        public Sphere radiusThickness(float radiusThickness) {
            this.configData.putFloat("radiusThickness", Mth.clamp(radiusThickness, 0, 1));
            return this;
        }

        public Sphere arc(float arc) {
            this.configData.putFloat("arc", Mth.clamp(arc, 0, 360));
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "sphere");
            return tag;
        }
    }

    public static class Circle extends ShapeBuilder {

        public static Circle create() {
            return new Circle();
        }

        public Circle radius(float radius) {
            this.configData.putFloat("radius", Mth.clamp(radius, 0, 1000));
            return this;
        }

        public Circle radiusThickness(float radiusThickness) {
            this.configData.putFloat("radiusThickness", Mth.clamp(radiusThickness, 0, 1));
            return this;
        }

        public Circle arc(float arc) {
            this.configData.putFloat("arc", Mth.clamp(arc, 0, 360));
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "circle");
            return tag;
        }
    }

    public static class Cylinder extends ShapeBuilder {

        public static Cylinder create() {
            return new Cylinder();
        }

        public Cylinder radius(float radius) {
            this.configData.putFloat("radius", Mth.clamp(radius, 0, 1000));
            return this;
        }

        public Cylinder radiusThickness(float radiusThickness) {
            this.configData.putFloat("radiusThickness", Mth.clamp(radiusThickness, 0, 1));
            return this;
        }

        public Cylinder arc(float arc) {
            this.configData.putFloat("arc", Mth.clamp(arc, 0, 360));
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "cylinder");
            return tag;
        }
    }

    public static class Cone extends ShapeBuilder {

        public static Cone create() {
            return new Cone();
        }

        public Cone angle(float angle) {
            this.configData.putFloat("angle", Mth.clamp(angle, 0, 90));
            return this;
        }

        public Cone radius(float radius) {
            this.configData.putFloat("radius", Mth.clamp(radius, 0, 1000));
            return this;
        }

        public Cone radiusThickness(float radiusThickness) {
            this.configData.putFloat("radiusThickness", Mth.clamp(radiusThickness, 0, 1));
            return this;
        }

        public Cone arc(float arc) {
            this.configData.putFloat("arc", Mth.clamp(arc, 0, 360));
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "cone");
            return tag;
        }
    }

    public static class Model extends ShapeBuilder {

        public static Model create() {
            return new Model();
        }

        public Model type(Mesh.Type type) {
            this.configData.putString("type", type.name());
            return this;
        }

        public Model meshData(ResourceLocation modelLocation) {
            CompoundTag tag = new CompoundTag();
            tag.putString("modelLocation", modelLocation.toString());
            this.configData.put("meshData", tag);
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "mesh");
            return tag;
        }
    }

    public static class Function extends ShapeBuilder {

        public static Function create() {
            return new Function();
        }

        public Function x(String x) {
            this.configData.putString("x", x);
            return this;
        }

        public Function y(String y) {
            this.configData.putString("y", y);
            return this;
        }

        public Function z(String z) {
            this.configData.putString("z", z);
            return this;
        }

        public Function speedX(String speedX) {
            this.configData.putString("speedX", speedX);
            return this;
        }

        public Function speedY(String speedY) {
            this.configData.putString("speedY", speedY);
            return this;
        }

        public Function speedZ(String speedZ) {
            this.configData.putString("speedZ", speedZ);
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "function");
            return tag;
        }
    }
}
