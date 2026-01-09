package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.client.renderer.types.MageArmorRenderer;
import com.ombremoon.spellbound.common.init.SBArmorMaterials;
import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MageArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected ItemAttributeModifiers attributeModifiers;
    private boolean hasSetBonus;
    private static final Map<EquipmentSlot, Double> SPELL_RESISTANCES = new HashMap<>();
    private static final Map<Holder<ArmorMaterial>, Holder<MobEffect>> SET_BONUS = new HashMap<>();

    public static void armorAttributeInit() {
        SPELL_RESISTANCES.put(EquipmentSlot.HEAD, 0.03);
        SPELL_RESISTANCES.put(EquipmentSlot.CHEST, 0.07);
        SPELL_RESISTANCES.put(EquipmentSlot.LEGS, 0.07);
        SPELL_RESISTANCES.put(EquipmentSlot.FEET, 0.03);

        SET_BONUS.put(SBArmorMaterials.PYROMANCER, SBEffects.PYROMANCER);
        SET_BONUS.put(SBArmorMaterials.STORMWEAVER, SBEffects.STORMWEAVER);
        SET_BONUS.put(SBArmorMaterials.CRYOMANCER, SBEffects.CRYOMANCER);
        SET_BONUS.put(SBArmorMaterials.CREATIONIST, SBEffects.TRANSFIG);
    }

    public MageArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
        buildModifiers();
        this.hasSetBonus = SET_BONUS.containsKey(material);
    }

    protected void buildModifiers() {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        int i = material.value().getDefense(type);
        float f = material.value().toughness();
        EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(type.getSlot());
        ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("armor." + type.getName());
        builder.add(Attributes.ARMOR, new AttributeModifier(resourcelocation, i, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup);
        builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resourcelocation, f, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup);
        float f1 = material.value().knockbackResistance();
        if (f1 > 0.0F) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(resourcelocation, f1, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup);
        }

        if (this.material.is(SBArmorMaterials.CRYOMANCER)) {
            builder.add(
                    SBAttributes.FROST_SPELL_RESIST, modifier(), EquipmentSlotGroup.bySlot(type.getSlot())
            );
        } else if (this.material.is(SBArmorMaterials.PYROMANCER)) {
            builder.add(
                    SBAttributes.FIRE_SPELL_RESIST, modifier(), EquipmentSlotGroup.bySlot(type.getSlot())
            );
        } else if (this.material.is(SBArmorMaterials.STORMWEAVER)) {
            builder.add(
                    SBAttributes.SHOCK_SPELL_RESIST, modifier(), EquipmentSlotGroup.bySlot(type.getSlot())
            );
        }

        this.attributeModifiers = builder.build();
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return this.attributeModifiers;
    }

    private AttributeModifier modifier(double amount) {
        return new AttributeModifier(CommonClass.customLocation("armor." + this.type.getName()), amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    private AttributeModifier modifier() {
        return modifier(SPELL_RESISTANCES.get(this.type.getSlot()));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (this.getEquipmentSlot() != EquipmentSlot.HEAD) return;
        if (!hasSetBonus) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;
        for (ItemStack armorItem : livingEntity.getArmorSlots()) {
            if (!(armorItem.getItem() instanceof MageArmorItem mageArmor) || !mageArmor.getMaterial().is(this.getMaterial())) {
                if (livingEntity.hasEffect(SET_BONUS.get(this.material)))
                    livingEntity.removeEffect(SET_BONUS.get(this.material));

                return;
            }
        }

        if (!livingEntity.hasEffect(SET_BONUS.get(this.material)))
            livingEntity.addEffect(new MobEffectInstance(SET_BONUS.get(this.material), -1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (this.material.is(SBArmorMaterials.CREATIONIST)) {
            tooltipComponents.add(Component.translatable("spellbound.transfiguration_armor.buff").withStyle(ChatFormatting.BLUE));
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @Nullable <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if (this.renderer == null)
                    this.renderer = new MageArmorRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
