package com.ombremoon.spellbound.common.magic.api.buff;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.magic.skills.SkillProvider;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.SpellUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.apache.logging.log4j.util.TriConsumer;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

@SuppressWarnings("unchecked")
public record SkillBuff<T>(SkillProvider skill, BuffCategory category, BuffObject<T> buffObject, T object) {
    private static final Logger LOGGER = Constants.LOG;
    private static final Map<String, BuffObject<?>> REGISTERED_OBJECTS = Maps.newHashMap();
    public static final StreamCodec<RegistryFriendlyByteBuf, SkillBuff<?>> STREAM_CODEC = StreamCodec.ofMember(
            SkillBuff::toNetwork, SkillBuff::fromNetwork
    );

    public static final BuffObject<MobEffectInstance> MOB_EFFECT = registerBuffObject(
            "mob_effect",
            (source, livingEntity, effectInstance) -> livingEntity.addEffect(effectInstance, source),
            (livingEntity, mobEffectInstance) -> livingEntity.removeEffect(mobEffectInstance.getEffect()),
            (effectInstance, effectInstance2) -> effectInstance.is(effectInstance2.getEffect()),
            MobEffectInstance.CODEC,
            MobEffectInstance.STREAM_CODEC);

    public static final BuffObject<ModifierData> ATTRIBUTE_MODIFIER = registerBuffObject(
            "attribute_modifier",
            (source, livingEntity, modifierData) -> {
                var instance = livingEntity.getAttribute(modifierData.attribute());
                if (instance != null && !instance.hasModifier(modifierData.attributeModifier().id()))
                    instance.addTransientModifier(modifierData.attributeModifier());
            },
            (livingEntity, modifierData) -> {
                var instance = livingEntity.getAttribute(modifierData.attribute());
                if (instance != null && instance.hasModifier(modifierData.attributeModifier().id()))
                    instance.removeModifier(modifierData.attributeModifier());
            },
            (data, data1) -> data.attributeModifier().is(data1.attributeModifier().id()),
            ModifierData.CODEC,
            ModifierData.STREAM_CODEC);

    public static final BuffObject<SpellModifier> SPELL_MODIFIER = registerBuffObject(
            "spell_modifier",
            (source, livingEntity, spellModifier) -> {
                var skills = SpellUtil.getSkills(livingEntity);
                skills.addModifier(spellModifier);
            },
            (livingEntity, spellModifier) -> {
                var skills = SpellUtil.getSkills(livingEntity);
                skills.removeModifier(spellModifier);
            },
            SpellModifier::equals,
            SpellModifier.CODEC,
            SpellModifier.STREAM_CODEC);

    public static final BuffObject<ResourceLocation> EVENT = registerBuffObject(
            "event",
            (source, livingEntity, resourceLocation) -> {
            },
            (livingEntity, resourceLocation) -> {
                var handler = SpellUtil.getSpellHandler(livingEntity);
                handler.getListener().removeListener(resourceLocation);
            },
            ResourceLocation::equals,
            ResourceLocation.CODEC,
            ResourceLocation.STREAM_CODEC);

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("SkillProviderType", this.skill.getType().name());
        tag.putString("Skill", this.skill.location().toString());
        BuffCategory.CODEC
                .encodeStart(NbtOps.INSTANCE, this.category)
                .resultOrPartial(LOGGER::error)
                .ifPresent(nbt -> tag.put("Category", nbt));
        BuffObject.CODEC
                .encodeStart(NbtOps.INSTANCE, this.buffObject)
                .resultOrPartial(LOGGER::error)
                .ifPresent(nbt -> tag.put("BuffObject", nbt));
        if (this.object != null) {
            this.buffObject.objectCodec
                    .encodeStart(NbtOps.INSTANCE, this.object)
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(nbt -> tag.put("Buff", nbt));
        }
        return tag;
    }

    public static <T> SkillBuff<T> load(CompoundTag tag) {
        Skill skill = SkillProvider.getFromId(
                SkillProvider.Type.valueOf(tag.getString("SkillProviderType")),
                ResourceLocation.parse(tag.getString("Skill"))
        );
        BuffCategory category = BuffCategory.CODEC
                .parse(new Dynamic<>(NbtOps.INSTANCE, tag.get("Category")))
                .resultOrPartial(LOGGER::error)
                .orElse(null);
        BuffObject<T> buffObject = (BuffObject<T>) BuffObject.CODEC
                .parse(new Dynamic<>(NbtOps.INSTANCE, tag.get("BuffObject")))
                .resultOrPartial(LOGGER::error)
                .orElse(null);
        T object = null;
        if (buffObject != null) {
            object = buffObject.objectCodec
                    .parse(new Dynamic<>(NbtOps.INSTANCE, tag.get("Buff")))
                    .resultOrPartial(LOGGER::error)
                    .orElse(null);
        }
        return new SkillBuff<>(skill, category, buffObject, object);
    }

    private void toNetwork(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(this.skill.getType().name());
        this.skill.encode(buf);
        NeoForgeStreamCodecs.enumCodec(BuffCategory.class).encode(buf, this.category);
        BuffObject.STREAM_CODEC.encode(buf, this.buffObject);
        this.buffObject.objectStreamCodec.encode(buf, this.object);
    }

    private static <T> SkillBuff<T> fromNetwork(RegistryFriendlyByteBuf buf) {
        SkillProvider skill = SkillProvider.decode(SkillProvider.Type.valueOf(buf.readUtf()), buf);
        BuffCategory category = NeoForgeStreamCodecs.enumCodec(BuffCategory.class).decode(buf);
        BuffObject<T> buffObject = (BuffObject<T>) BuffObject.STREAM_CODEC.decode(buf);
        T object = buffObject.objectStreamCodec.decode(buf);
        return new SkillBuff<>(skill, category, buffObject, object);
    }

    private static <T> BuffObject<T> registerBuffObject(String name,
                                                        TriConsumer<Entity, LivingEntity, T> addObject,
                                                        BiConsumer<LivingEntity, T> removeObject,
                                                        BiPredicate<T, T> equalCondition,
                                                        Codec<T> objectCodec,
                                                        StreamCodec<? super RegistryFriendlyByteBuf, T> objectStreamCodec) {
        BuffObject<T> object = new BuffObject<>(name, addObject, removeObject, equalCondition, objectCodec, objectStreamCodec);
        REGISTERED_OBJECTS.put(name, object);
        return object;
    }

    public void addBuff(Entity source, LivingEntity livingEntity) {
        if (this.buffObject != null)
            this.buffObject.addObject().accept(source, livingEntity, this.object);
    }

    public void removeBuff(LivingEntity livingEntity) {
        if (this.buffObject != null)
            this.buffObject.removeObject().accept(livingEntity, this.object);
    }

    public boolean isSkill(SkillProvider skill) {
        return this.skill.equals(skill);
    }

    public boolean isBeneficial() {
        return this.category == BuffCategory.BENEFICIAL;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof SkillBuff<?> skillBuff)) {
            return false;
        } else {
            return this.isSkill(skillBuff.skill) && this.isType(skillBuff) && this.buffObject.equalCondition.test(this.object, (T) skillBuff.object);
        }
    }

    private boolean isType(SkillBuff<?> buff) {
        return this.object.getClass().equals(buff.object.getClass());
    }

    @Override
    public String toString() {
        return "SkillBuff: [" + this.skill + ", Type: " + this.buffObject.name + ", Category: " + this.category + ", Buff: " + this.object + "]";
    }

    public record BuffObject<T>(
            String name,
            TriConsumer<Entity, LivingEntity, T> addObject,
            BiConsumer<LivingEntity, T> removeObject,
            BiPredicate<T, T> equalCondition,
            Codec<T> objectCodec,
            StreamCodec<? super RegistryFriendlyByteBuf, T> objectStreamCodec) {

        public static final Codec<BuffObject<?>> CODEC = Codec.STRING
                .comapFlatMap(
                        string -> {
                            var object = parseUnchecked(string);
                            return object != null ? DataResult.success(object) : DataResult.error(() -> "Failed to parse buff object " + string);
                        },
                        BuffObject::name
                );
        public static final StreamCodec<ByteBuf, BuffObject<?>> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
                .map(BuffObject::parseUnchecked, BuffObject::name);

        @SuppressWarnings("unchecked")
        private static <T> BuffObject<T> parseUnchecked(String name) {
            return (BuffObject<T>) REGISTERED_OBJECTS.get(name);
        }
    }
}