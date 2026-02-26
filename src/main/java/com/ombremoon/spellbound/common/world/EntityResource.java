package com.ombremoon.spellbound.common.world;

import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBData;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public enum EntityResource implements StringRepresentable {
    HEALTH("health",
            (entity, amount, operation) -> entity.setHealth((float) Math.clamp(operation.apply(entity.getHealth(), amount), 0, entity.getMaxHealth())),
            entity -> entity.getHealth() >= entity.getMaxHealth()
    ),
    MANA("mana",
            (entity, amount, operation) -> {
                Double mana = entity.getData(SBData.MANA);
                entity.setData(SBData.MANA, Math.clamp(operation.apply(mana, amount), 0, entity.getAttributeValue(SBAttributes.MAX_MANA)));
            },
            entity -> entity.getData(SBData.MANA) >= entity.getAttributeValue(SBAttributes.MAX_MANA)
    ),
    EXPERIENCE("experience",
            (entity, amount, operation) -> {
                if (entity instanceof Player player) {
                    switch (operation) {
                        case ADD -> player.giveExperienceLevels((int) Math.max(amount, 0));
                        case SET -> player.experienceLevel = (int) Math.max(operation.apply(player.experienceLevel, amount), 0);
                        default -> player.giveExperienceLevels((int) operation.apply(player.experienceLevel, amount));
                    }
                }
            },
            entity -> false
    ),
    HUNGER("hunger",
            (entity, amount, operation) -> {
                if (entity instanceof Player player) {
                    player.getFoodData().setFoodLevel((int) Math.max(operation.apply(player.getFoodData().getFoodLevel(), amount), 0));
                }
            },
            livingEntity -> livingEntity instanceof Player player && !player.getFoodData().needsFood()
    ),
    SATURATION("saturation",
            (entity, amount, operation) -> {
                if (entity instanceof Player player) {
                    player.getFoodData().setSaturation((float) Math.clamp(operation.apply(player.getFoodData().getSaturationLevel(), amount), 0, player.getFoodData().getFoodLevel()));
                }
            },
            livingEntity -> livingEntity instanceof Player player && player.getFoodData().getSaturationLevel() >= player.getFoodData().getFoodLevel()

    ),
    AIR("air",
            (entity, amount, operation) -> entity.setAirSupply((int) Math.clamp(operation.apply(entity.getAirSupply(), amount), 0, entity.getMaxAirSupply())),
            entity -> entity.getAirSupply() >= entity.getMaxAirSupply()
    );

    public static final StringRepresentable.EnumCodec<EntityResource> CODEC = StringRepresentable.fromEnum(EntityResource::values);
    private final String name;
    private final ResourceConsumer consumeResource;
    private final Predicate<LivingEntity> isFull;

    EntityResource(String name, ResourceConsumer consumeResource, Predicate<LivingEntity> isFull) {
        this.name = name;
        this.consumeResource = consumeResource;
        this.isFull = isFull;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public void consume(LivingEntity entity, double amount, Operation operation) {
        this.consumeResource.consume(entity, amount, operation);
    }

    public boolean isFull(LivingEntity entity) {
        return this.isFull.test(entity);
    }

    public enum Operation implements StringRepresentable {
        ADD("add", Double::sum),
        ADD_MULTIPLIED_TOTAL("add_multiplied_total", (a, b) -> a + (a * b)),
        SET("set", (a, b) -> b);

        public static final StringRepresentable.EnumCodec<Operation> CODEC = StringRepresentable.fromEnum(Operation::values);
        private final String name;
        private final BiFunction<Double, Double, Double> operation;

        Operation(String name, BiFunction<Double, Double, Double> operation) {
            this.name = name;
            this.operation = operation;
        }

        @Override
        public String getSerializedName() {
               return this.name;
        }

        public double apply(double a, double b) {
            return this.operation.apply(a, b);
        }
    }

    @FunctionalInterface
    public interface ResourceConsumer {
        void consume(LivingEntity entity, double amount, Operation operation);
    }
}
