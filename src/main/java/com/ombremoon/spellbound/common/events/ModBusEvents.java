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
    }

    private static void defaultEntity(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event) {
        event.add(entityType, SBAttributes.ATTACK_POWER);
        event.add(entityType, SBAttributes.MAGIC_RESIST);
        event.add(entityType, SBAttributes.FIRE_SPELL_RESIST);
        event.add(entityType, SBAttributes.FROST_SPELL_RESIST);
        event.add(entityType, SBAttributes.SHOCK_SPELL_RESIST);
    }

    private static void magicEntity(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event) {
        defaultEntity(entityType, event);
        event.add(entityType, SBAttributes.MAX_MANA);
        event.add(entityType, SBAttributes.MANA_REGEN);
    }

    private static void player(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event) {
        magicEntity(entityType, event);
        event.add(entityType, SBAttributes.CAST_RANGE);
        event.add(entityType, SBAttributes.CAST_SPEED);
    }

    @SubscribeEvent
    public static void onEntityAttributeRegister(EntityAttributeCreationEvent event) {
        SBEntities.SUPPLIERS.forEach(register -> event.put(register.entityTypeSupplier().get(), register.attributeSupplier().get().build()));
    }
}
