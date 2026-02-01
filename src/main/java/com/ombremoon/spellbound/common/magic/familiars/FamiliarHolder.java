package com.ombremoon.spellbound.common.magic.familiars;

import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.skills.FamiliarAffinity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FamiliarHolder<T extends LivingEntity, F extends Familiar<T>> {
    private final ResourceLocation identifier;
    private final Supplier<EntityType<T>> entity;
    private final SpellMastery mastery;
    private final int maxLevel;
    private final FamiliarBuilder<F> builder;
    private final List<FamiliarAffinity> affinities;

    public FamiliarHolder(ResourceLocation identifier, Supplier<EntityType<T>> entity, SpellMastery mastery, FamiliarBuilder<F> builder, FamiliarAffinity... affinities) {
        this(identifier, entity, mastery, builder, 5, affinities);
    }

    public FamiliarHolder(ResourceLocation identifier, Supplier<EntityType<T>> entity, SpellMastery mastery, FamiliarBuilder<F> builder, int maxLevel, FamiliarAffinity... affinities) {
        this.identifier = identifier;
        this.entity = entity;
        this.mastery = mastery;
        this.maxLevel = maxLevel;
        this.builder = builder;
        this.affinities = Arrays.stream(affinities).toList();
        FamiliarHandler.registerFamiliarMastery(this, mastery);
    }

    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public SpellMastery getMastery() {
        return mastery;
    }

    public EntityType<T> getEntity() {
        return entity.get();
    }

    public FamiliarBuilder<F> getBuilder() {
        return builder;
    }

    public List<FamiliarAffinity> getAffinities() {
        return affinities;
    }

    @FunctionalInterface
    public interface FamiliarBuilder<F extends Familiar<?>> {
        F create();
    }
}
