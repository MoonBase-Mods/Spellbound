package com.ombremoon.spellbound.common.magic.api;

import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Wrapper class for {@link AbstractSpell}'s. Spell types should be registered like any other {@link DeferredRegister}
 * @see SBSpells
 * @param <S> AbstractSpell
 */
public class SpellType<S extends AbstractSpell> {
    private final SpellFactory<S> factory;
    private final SpellPath path;
    @Nullable
    private final SpellPath subPath;
    private final SpellMastery mastery;
    private final Set<Holder<Skill>> availableSkills;

    public SpellType(SpellPath path, @Nullable SpellPath subPath, SpellMastery mastery, Set<Holder<Skill>> skills, SpellFactory<S> factory) {
        this.path = path;
        this.subPath = subPath;
        this.mastery = mastery;
        this.availableSkills = skills;
        this.factory = factory;

        if (this.getRootSkill() == null) throw new IllegalStateException(this + " does not contain a root skill");
    }

    public ResourceLocation location() {
        return SBSpells.REGISTRY.getKey(this);
    }

    public SpellPath getPath() {
        return this.path;
    }

    public @Nullable SpellPath getSubPath() {
        return this.subPath;
    }

    public SpellPath getIdentifiablePath() {
        return this.getSubPath() != null ? this.getSubPath() : this.getPath();
    }

    public SpellMastery getMastery() {
        return this.mastery;
    }

    public ResourceLocation getTexture() {
        return this.createSpell().getTexture();
    }

    public Skill getRootSkill() {
        return this.availableSkills.isEmpty() ? null : this.getSkills().getFirst().getRoot();
    }

    public List<Skill> getSkills() {
        return this.availableSkills.stream().map(Holder::value).collect(Collectors.toList());
    }

    public <T extends AbstractSpell> boolean is(Supplier<SpellType<T>> spellType) {
        return this == spellType.get();
    }

    public boolean is(ResourceLocation spellType) {
        return this.location().equals(spellType);
    }

    public S createSpell() {
        return this.factory.create();
    }

    public S createSpellWithData(LivingEntity caster) {
        S spell = createSpell();
        spell.initNoCast(caster);
        return spell;
    }

    public interface SpellFactory<S extends AbstractSpell> {
        S create();
    }

    @Override
    public String toString() {
        return this.location().toString();
    }

    @Override
    public int hashCode() {
        return this.location().hashCode();
    }

    public static class Builder<T extends AbstractSpell> {
        private final SpellFactory<T> factory;
        private SpellPath path;
        private SpellPath subPath;
        private SpellMastery mastery;
        private final Set<Holder<Skill>> availableSkills = new ObjectOpenHashSet<>();

        public Builder(SpellFactory<T> factory) {
            this.factory = factory;
        }

        public Builder<T> setPath(SpellPath path, SpellPath subPath) {
            this.path = path;

            if (!subPath.isSubPath()) throw new IllegalArgumentException("Second argument must be a subpath");
            this.subPath = subPath;
            return this;
        }

        public Builder<T> setPath(SpellPath path) {
            this.path = path;
            return this;
        }

        public Builder<T> setMastery(SpellMastery mastery) {
            this.mastery = mastery;
            return this;
        }

        @SafeVarargs
        public final Builder<T> skills(Holder<Skill>... skills) {
            this.availableSkills.addAll(Arrays.stream(skills).toList());
            return this;
        }

        public SpellType<T> build() {
            return new SpellType<>(path, subPath, mastery, availableSkills, factory);
        }
    }
}
