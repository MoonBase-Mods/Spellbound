package com.ombremoon.spellbound.common.events;

import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class ModBusEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        player(EntityType.PLAYER, event);
        magicEntity(EntityType.COD, 2, event);
        magicEntity(EntityType.SALMON, 2, event);
        magicEntity(EntityType.PUFFERFISH, 2, event);
        magicEntity(EntityType.TROPICAL_FISH, 2, event);
        magicEntity(EntityType.TADPOLE, 2, event);
        magicEntity(EntityType.CHICKEN, 5, event);
        magicEntity(EntityType.COW, 5, event);
        magicEntity(EntityType.PIG, 5, event);
        magicEntity(EntityType.SHEEP, 5, event);
        magicEntity(EntityType.RABBIT, 5, event);
        magicEntity(EntityType.BEE, 5, event);
        magicEntity(EntityType.FROG, 5, event);
        magicEntity(EntityType.ARMADILLO, 10, event);
        magicEntity(EntityType.GOAT, 10, event);
        magicEntity(EntityType.SQUID, 10, event);
        magicEntity(EntityType.HORSE, 10, event);
        magicEntity(EntityType.CAMEL, 10, event);
        magicEntity(EntityType.LLAMA, 10, event);
        magicEntity(EntityType.DONKEY, 10, event);
        magicEntity(EntityType.MULE, 10, event);
        magicEntity(EntityType.TRADER_LLAMA, 10, event);
        magicEntity(EntityType.WOLF, 15, event);
        magicEntity(EntityType.CAT, 15, event);
        magicEntity(EntityType.PARROT, 15, event);
        magicEntity(EntityType.FOX, 15, event);
        magicEntity(EntityType.OCELOT, 15, event);
        magicEntity(EntityType.PANDA, 15, event);
        magicEntity(EntityType.POLAR_BEAR, 15, event);
        magicEntity(EntityType.DOLPHIN, 15, event);
        magicEntity(EntityType.AXOLOTL, 15, event);
        magicEntity(EntityType.MOOSHROOM, 25, event);
        magicEntity(EntityType.BAT, 5, event);
        magicEntity(EntityType.SILVERFISH, 5, event);
        magicEntity(EntityType.ENDERMITE, 5, event);
        magicEntity(EntityType.ZOMBIE, 0, event);
        magicEntity(EntityType.ZOMBIE_VILLAGER, 0, event);
        magicEntity(EntityType.ZOMBIE_HORSE, 0, event);
        magicEntity(EntityType.ZOGLIN, 0, event);
        magicEntity(EntityType.HUSK, 0, event);
        magicEntity(EntityType.DROWNED, 0, event);
        magicEntity(EntityType.ZOMBIFIED_PIGLIN, 5, event);
        magicEntity(EntityType.SKELETON, 10, event);
        magicEntity(EntityType.STRAY, 10, event);
        magicEntity(EntityType.BOGGED, 10, event);
        magicEntity(EntityType.SKELETON_HORSE, 15, event);
        magicEntity(EntityType.WITHER_SKELETON, 25, event);
        magicEntity(EntityType.VILLAGER, 25, event);
        magicEntity(EntityType.PILLAGER, 15, event);
        magicEntity(EntityType.VINDICATOR, 15, event);
        magicEntity(EntityType.PIGLIN, 15, event);
        magicEntity(EntityType.PIGLIN_BRUTE, 15, event);
        magicEntity(EntityType.SPIDER, 15, event);
        magicEntity(EntityType.CAVE_SPIDER, 20, event);
        magicEntity(EntityType.HOGLIN, 25, event);
        magicEntity(EntityType.RAVAGER, 30, event);
        magicEntity(EntityType.SNIFFER, 30, event);
        magicEntity(EntityType.CREEPER, 50, event);
        magicEntity(EntityType.GLOW_SQUID, 40, event);
        magicEntity(EntityType.STRIDER, 40, event);
        magicEntity(EntityType.SLIME, 40, event);
        magicEntity(EntityType.MAGMA_CUBE, 40, event);
        magicEntity(EntityType.SNOW_GOLEM, 30, event);
        magicEntity(EntityType.IRON_GOLEM, 60, event);
        magicEntity(EntityType.BLAZE, 80, event);
        magicEntity(EntityType.BREEZE, 80, event);
        magicEntity(EntityType.PHANTOM, 80, event);
        magicEntity(EntityType.ALLAY, 80, event);
        magicEntity(EntityType.VEX, 100, event);
        magicEntity(EntityType.ENDERMAN, 100, event);
        magicEntity(EntityType.SHULKER, 80, event);
        magicEntity(EntityType.WANDERING_TRADER, 60, event);
        magicEntity(EntityType.WITCH, 150, event);
        magicEntity(EntityType.EVOKER, 200, event);
        magicEntity(EntityType.ILLUSIONER, 180, event);
        magicEntity(EntityType.GHAST, 150, event);
        magicEntity(EntityType.GUARDIAN, 120, event);
        magicEntity(EntityType.ELDER_GUARDIAN, 250, event);
        magicEntity(EntityType.WARDEN, 300, event);
        magicEntity(EntityType.WITHER, 800, event);
        magicEntity(EntityType.ENDER_DRAGON, 1000, event);
    }

    private static void defaultEntity(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event) {
        event.add(entityType, SBAttributes.ATTACK_POWER);
        event.add(entityType, SBAttributes.MAGIC_RESIST);
        event.add(entityType, SBAttributes.FIRE_SPELL_RESIST);
        event.add(entityType, SBAttributes.FROST_SPELL_RESIST);
        event.add(entityType, SBAttributes.SHOCK_SPELL_RESIST);
    }

    private static void magicEntity(EntityType<? extends LivingEntity> entityType, float mana, EntityAttributeModificationEvent event) {
        defaultEntity(entityType, event);
        event.add(entityType, SBAttributes.MAX_MANA, mana);
        event.add(entityType, SBAttributes.MANA_REGEN);
    }

    private static void player(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event) {
        magicEntity(entityType, 100, event);
        event.add(entityType, SBAttributes.CAST_RANGE);
        event.add(entityType, SBAttributes.CAST_SPEED);
    }

    @SubscribeEvent
    public static void onEntityAttributeRegister(EntityAttributeCreationEvent event) {
        SBEntities.SUPPLIERS.forEach(register -> event.put(register.entityTypeSupplier().get(), register.attributeSupplier().get().build()));
    }
}
