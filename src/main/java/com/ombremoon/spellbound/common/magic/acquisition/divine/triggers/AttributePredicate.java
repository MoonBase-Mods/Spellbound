package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBEntitySubPredicates;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record AttributePredicate(
        Optional<Holder<Attribute>> attribute,
        MinMaxBounds.Doubles percent
) implements EntitySubPredicate {
    public static final MapCodec<AttributePredicate> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Attribute.CODEC.optionalFieldOf("attribute").forGetter(AttributePredicate::attribute),
                    MinMaxBounds.Doubles.CODEC.optionalFieldOf("percent", MinMaxBounds.Doubles.ANY).forGetter(AttributePredicate::percent)
            ).apply(instance, AttributePredicate::new)
    );

    public static AttributePredicate withAttribute(Holder<Attribute> attribute, MinMaxBounds.Doubles percent) {
        return new AttributePredicate(Optional.of(attribute), percent);
    }

    public static AttributePredicate withAttribute(Holder<Attribute> attribute) {
        return withAttribute(attribute, MinMaxBounds.Doubles.ANY);
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
        if (!(entity instanceof LivingEntity living)) {
            return false;
        } else if (this.attribute.isPresent()) {
            AttributeInstance instance = living.getAttribute(this.attribute().get());
            if (instance == null) {
                return false;
            }

            return this.percent.matches(instance.getValue() / instance.getBaseValue());
        }

        return false;
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return SBEntitySubPredicates.ATTRIBUTE.get();
    }
}
