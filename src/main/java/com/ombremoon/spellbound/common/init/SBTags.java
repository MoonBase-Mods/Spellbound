package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class SBTags {
    public static class DamageTypes {
        public static final TagKey<DamageType> SPELL_DAMAGE = tag("spell_damage");
        public static final TagKey<DamageType> PHYSICAL_DAMAGE = tag("physical_damage");

        private static TagKey<DamageType> tag(String name) {
            return TagKey.create(Registries.DAMAGE_TYPE, CommonClass.customLocation(name));
        }
    }

    public static class EntityTypes {

        private static TagKey<EntityType<?>> tag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, CommonClass.customLocation(name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> ARCANTHUS_GROWTH_BLOCKS = tag("arcanthus_growth_blocks");
        public static final TagKey<Block> RITUAL_COMPATIBLE = tag("ritual_compatible");
        public static final TagKey<Block> DIVINE_SHRINE = tag("divine_shrine");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, CommonClass.customLocation(name));
        }
    }

    public static class Items {


        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, CommonClass.customLocation(name));
        }
    }

    public static class MobEffects {
        public static final TagKey<MobEffect> HEALING = tag("healing");

        private static TagKey<MobEffect> tag(String name) {
            return TagKey.create(Registries.MOB_EFFECT, CommonClass.customLocation(name));
        }
    }
}
