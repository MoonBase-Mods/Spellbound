package com.ombremoon.spellbound.client.photon.data;

import com.lowdragmc.lowdraglib2.math.GradientColor;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.curve.ECBCurves;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;

public abstract class NumberFunctionBuilder extends ConfigDataBuilder {

    public static class Constant extends NumberFunctionBuilder {

        protected Constant() {
        }

        public static Constant of() {
            return new Constant();
        }

        public Constant number(Number number) {
            CompoundTag tag = new CompoundTag();
            tag.put("number", NbtOps.INSTANCE.createNumeric(number));
            this.configData.put("data", tag);
            this.configData.putString("type", "constant");
            return this;
        }
    }

    public static class RandomConstant extends NumberFunctionBuilder {

        protected RandomConstant() {
        }

        public static RandomConstant of() {
            return new RandomConstant();
        }

        public RandomConstant random(Number min, Number max) {
            CompoundTag tag = new CompoundTag();
            tag.put("a", NbtOps.INSTANCE.createNumeric(min));
            tag.put("b", NbtOps.INSTANCE.createNumeric(max));
            this.configData.put("data", tag);
            this.configData.putString("type", "random_constant");
            return this;
        }
    }

    public static class Curve extends NumberFunctionBuilder {
        protected final HolderLookup.Provider registries;

        protected Curve(HolderLookup.Provider registries) {
            this.registries = registries;
            this.configData.putFloat("max", Float.MAX_VALUE);
            this.configData.putFloat("min", Float.MIN_VALUE);
        }

        public static Curve of(HolderLookup.Provider registries) {
            return new Curve(registries);
        }

        public Curve curveLimit(float min, float max) {
            this.configData.putFloat("min", min);
            this.configData.putFloat("max", max);
            return this;
        }

        public Curve bounds(float lower, float upper) {
            this.configData.putFloat("lower", lower);
            this.configData.putFloat("upper", upper);
            return this;
        }

        public Curve axes(String xAxis, String yAxis) {
            this.configData.putString("xAxis", xAxis);
            this.configData.putString("yAxis", yAxis);
            return this;
        }

        public Curve lockControlPoint(boolean lockControlPoint) {
            this.configData.putBoolean("lockControlPoint", lockControlPoint);
            return this;
        }

        public Curve curves(float... data) {
            ECBCurves curves = new ECBCurves(data);
            this.configData.put("curves", curves.serializeNBT(this.registries));
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "curve");
            return tag;
        }
    }

    public static class RandomCurve extends NumberFunctionBuilder {
        protected final HolderLookup.Provider registries;

        protected RandomCurve(HolderLookup.Provider registries) {
            this.registries = registries;
            this.configData.putFloat("max", Float.MAX_VALUE);
            this.configData.putFloat("min", Float.MIN_VALUE);
        }

        public static RandomCurve of(HolderLookup.Provider registries) {
            return new RandomCurve(registries);
        }

        public RandomCurve curveLimit(float min, float max) {
            this.configData.putFloat("min", min);
            this.configData.putFloat("max", max);
            return this;
        }

        public RandomCurve bounds(float lower, float upper) {
            this.configData.putFloat("lower", lower);
            this.configData.putFloat("upper", upper);
            return this;
        }

        public RandomCurve axes(String xAxis, String yAxis) {
            this.configData.putString("xAxis", xAxis);
            this.configData.putString("yAxis", yAxis);
            return this;
        }

        public RandomCurve lockControlPoint(boolean lockControlPoint) {
            this.configData.putBoolean("lockControlPoint", lockControlPoint);
            return this;
        }

        public RandomCurve curves0(float... data) {
            ECBCurves curves = new ECBCurves(data);
            this.configData.put("curves0", curves.serializeNBT(this.registries));
            return this;
        }

        public RandomCurve curves1(float... data) {
            ECBCurves curves = new ECBCurves(data);
            this.configData.put("curves1", curves.serializeNBT(this.registries));
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "random_curve");
            return tag;
        }
    }

    public static class Gradient extends NumberFunctionBuilder {
        protected final HolderLookup.Provider registries;

        protected Gradient(HolderLookup.Provider registries) {
            this.registries = registries;
        }

        public static Gradient of(HolderLookup.Provider registries) {
            return new Gradient(registries);
        }

        public Gradient color(int... colors) {
            CompoundTag tag = new CompoundTag();
            GradientColor color = new GradientColor(colors);
            tag.put("gradientColor", color.serializeNBT(this.registries));
            this.configData.put("data", tag);
            this.configData.putString("type", "gradient");
            return this;
        }
    }

    public static class RandomGradient extends NumberFunctionBuilder {
        protected final HolderLookup.Provider registries;

        protected RandomGradient(HolderLookup.Provider registries) {
            this.registries = registries;
        }

        public static RandomGradient of(HolderLookup.Provider registries) {
            return new RandomGradient(registries);
        }

        public RandomGradient colors0(int... colors) {
            GradientColor color = new GradientColor(colors);
            this.configData.put("gradientColor0", color.serializeNBT(this.registries));
            return this;
        }

        public RandomGradient colors1(int... colors) {
            GradientColor color = new GradientColor(colors);
            this.configData.put("gradientColor1", color.serializeNBT(this.registries));
            return this;
        }

        public RandomGradient colors(GradientColor firstColor, GradientColor secondColor) {
            this.configData.put("gradientColor0", firstColor.serializeNBT(this.registries));
            this.configData.put("gradientColor1", secondColor.serializeNBT(this.registries));
            return this;
        }

        @Override
        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.put("data", super.build());
            tag.putString("type", "random_gradient");
            return tag;
        }
    }

    public static class NumberFunction3 extends NumberFunctionBuilder {
        protected final HolderLookup.Provider registries;

        protected NumberFunction3(HolderLookup.Provider registries) {
            this.registries = registries;
        }

        public static NumberFunction3 of(HolderLookup.Provider registries) {
            return new NumberFunction3(registries);
        }

        public NumberFunction3 function(NumberFunctionBuilder xFunction, NumberFunctionBuilder yFunction, NumberFunctionBuilder zFunction) {
            ListTag listTag = new ListTag();
            listTag.add(xFunction.build());
            listTag.add(yFunction.build());
            listTag.add(zFunction.build());
            return this;
        }

    }
}
