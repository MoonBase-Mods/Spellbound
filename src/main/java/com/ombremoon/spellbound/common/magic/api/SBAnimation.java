package com.ombremoon.spellbound.common.magic.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.magic.api.combat.AnimProperties;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record SBAnimation(ResourceLocation animation, Type type, boolean stationary, boolean canPlayAnotherAnimation, AnimProperties properties) {
    public static final ResourceLocation ACTION_ANIMATION = CommonClass.customLocation("action");
    public static final ResourceLocation MOVEMENT_ANIMATION = CommonClass.customLocation("movement");

    public static final Codec<SBAnimation> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("animation").forGetter(SBAnimation::animation),
                    Type.CODEC.fieldOf("type").forGetter(SBAnimation::type),
                    Codec.BOOL.fieldOf("stationary").forGetter(SBAnimation::stationary),
                    Codec.BOOL.fieldOf("canPlayAnotherAnimation").forGetter(SBAnimation::canPlayAnotherAnimation),
                    AnimProperties.CODEC.fieldOf("properties").forGetter(SBAnimation::properties)
            ).apply(instance, SBAnimation::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SBAnimation> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, SBAnimation::animation,
            NeoForgeStreamCodecs.enumCodec(Type.class), SBAnimation::type,
            ByteBufCodecs.BOOL, SBAnimation::stationary,
            ByteBufCodecs.BOOL, SBAnimation::canPlayAnotherAnimation,
            AnimProperties.STREAM_CODEC, SBAnimation::properties,
            SBAnimation::new
    );

    //AnimProperties
    //isMotionTracking

    public SBAnimation(String animation, Type type, boolean stationary) {
        this(CommonClass.customLocation(animation), type, stationary, true, AnimProperties.EMPTY);
    }

    public SBAnimation(String animation, Type type, boolean stationary, boolean canPlayAnotherAnimation) {
        this(CommonClass.customLocation(animation), type, stationary, canPlayAnotherAnimation, AnimProperties.EMPTY);
    }

    public SBAnimation(ResourceLocation animation, Type type, boolean stationary, boolean canPlayAnotherAnimation) {
        this(animation, type, stationary, canPlayAnotherAnimation, AnimProperties.EMPTY);
    }

    public enum Type implements StringRepresentable {
        CAST("cast", true, ACTION_ANIMATION),
        CHANNEL("channel", true, ACTION_ANIMATION),
        MOVEMENT("movement", false, MOVEMENT_ANIMATION);

        public static final StringRepresentableCodec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        private final String name;
        private final boolean stationary;
        private final ResourceLocation animationLayer;

        Type(String name, boolean stationary, ResourceLocation animationLayer) {
            this.name = name;
            this.stationary = stationary;
            this.animationLayer = animationLayer;
        }

        public boolean isStationary() {
            return stationary;
        }

        public ResourceLocation getAnimationLayer() {
            return this.animationLayer;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
