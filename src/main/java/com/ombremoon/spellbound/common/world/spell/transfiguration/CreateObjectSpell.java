package com.ombremoon.spellbound.common.world.spell.transfiguration;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AnimatedSpell;
import com.ombremoon.spellbound.common.magic.api.Imbuement;
import com.ombremoon.spellbound.common.magic.api.RadialSpell;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreateObjectSpell extends AnimatedSpell implements RadialSpell {
    private static final EquipmentSlot[] ARMOR_SLOTS = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static Builder<CreateObjectSpell> createCreateObjectBuilder() {
        return createSimpleSpellBuilder(CreateObjectSpell.class)
                .castCondition((context, createObjectSpell) -> {
                    LivingEntity caster = context.getCaster();
                    if (createObjectSpell.disableChoiceOnRecast(context, createObjectSpell)) {
                        return false;
                    } else {
                        if (context.isChoice(SBSkills.ADVENTURER)) {
                            return createObjectSpell.hasEmptyArmorSlots(caster);
                        } else {
                            return caster.getMainHandItem().isEmpty();
                        }
                    }
                })
                .duration(2400);
    }
    private int imbuedSlot;

    public CreateObjectSpell() {
        super(SBSpells.CREATE_OBJECT.get(), createCreateObjectBuilder());
    }

    @Override
    public void registerSkillTooltips() {

    }

    @Override
    protected void onSpellStart(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            boolean addOneTier = context.hasSkill(SBSkills.ARTISANS_TOUCH);
            boolean addTwoTiers = context.hasSkill(SBSkills.MASTERWORK_GEAR);
            if (context.isChoice(SBSkills.ADVENTURER) && this.hasEmptyArmorSlots(caster)) {
                List<ItemStack> armorSet = new ArrayList<>();
                this.createArmor(armorSet, Items.DIAMOND_HELMET, Items.IRON_HELMET, Items.LEATHER_HELMET, addOneTier, addTwoTiers);
                this.createArmor(armorSet, Items.DIAMOND_CHESTPLATE, Items.IRON_CHESTPLATE, Items.LEATHER_CHESTPLATE, addOneTier, addTwoTiers);
                this.createArmor(armorSet, Items.DIAMOND_LEGGINGS, Items.IRON_LEGGINGS, Items.LEATHER_LEGGINGS, addOneTier, addTwoTiers);
                this.createArmor(armorSet, Items.DIAMOND_BOOTS, Items.IRON_BOOTS, Items.LEATHER_BOOTS, addOneTier, addTwoTiers);
                    armorSet.forEach(itemStack -> {
                        if (context.hasSkill(SBSkills.MYSTIC_TOOLSMITH)) {
                            this.enchantItem(level, itemStack);
                        }
                    });

                Imbuement imbuement = Imbuement.create(this);
                for (int i = 0; i < ARMOR_SLOTS.length; i++) {
                    EquipmentSlot slot = ARMOR_SLOTS[i];
                    ItemStack stack = armorSet.get(i);
                    this.giveSpellItem(stack, slot, imbuement);
                }
            } else {
                ItemStack grantedItem = addTwoTiers ? Items.DIAMOND_PICKAXE.getDefaultInstance() : addOneTier ? Items.IRON_PICKAXE.getDefaultInstance() : Items.STONE_PICKAXE.getDefaultInstance();
                if (context.isChoice(SBSkills.LUMBERJACK)) {
                    grantedItem = addTwoTiers ? Items.DIAMOND_AXE.getDefaultInstance() : addOneTier ? Items.IRON_AXE.getDefaultInstance() : Items.STONE_AXE.getDefaultInstance();
                } else if (context.isChoice(SBSkills.EXCAVATOR)) {
                    grantedItem = addTwoTiers ? Items.DIAMOND_SHOVEL.getDefaultInstance() : addOneTier ? Items.IRON_SHOVEL.getDefaultInstance() : Items.STONE_SHOVEL.getDefaultInstance();
                } else if (context.isChoice(SBSkills.HARVESTER)) {
                    grantedItem = addTwoTiers ? Items.DIAMOND_HOE.getDefaultInstance() : addOneTier ? Items.IRON_HOE.getDefaultInstance() : Items.STONE_HOE.getDefaultInstance();
                } else if (context.isChoice(SBSkills.KNIGHT)) {
                    grantedItem = addTwoTiers ? Items.DIAMOND_SWORD.getDefaultInstance() : addOneTier ? Items.IRON_SWORD.getDefaultInstance() : Items.STONE_SWORD.getDefaultInstance();
                } else if (context.isChoice(SBSkills.SCOUT)) {
                    grantedItem = Items.SPYGLASS.getDefaultInstance();
                }

                if (context.hasSkill(SBSkills.MYSTIC_TOOLSMITH) && !context.isChoice(SBSkills.SCOUT)) {
                    this.enchantItem(level, grantedItem);
                }

                if (this.giveSpellItem(grantedItem) && caster instanceof Player player) {
                    this.imbuedSlot = player.getInventory().selected;
                }
            }
        }
    }

    @Override
    protected void onSpellStop(SpellContext context) {
        LivingEntity caster = context.getCaster();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            if (this.isChoice(SBSkills.ADVENTURER)) {
                for (EquipmentSlot slot : ARMOR_SLOTS) {
                    this.removeSpellItem(caster, slot);
                }
            } else {
                this.removeSpellItem(caster, this.imbuedSlot);
            }
        }
    }

    @Override
    protected int getDuration(SpellContext context) {
        return context.hasSkill(SBSkills.LASTING_PROVISIONS) ? 6000 : super.getDuration(context);
    }

    @Override
    public int getSpellNumberCap(SpellContext context) {
        int level = this.level();
        if (level == 5) {
            return 3;
        } else if (level >= 3) {
            return 2;
        }

        return 1;
    }

    private void enchantItem(Level level, ItemStack itemStack) {
        Optional<HolderSet.Named<Enchantment>> optional = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.IN_ENCHANTING_TABLE);
        if (optional.isPresent()) {
            List<EnchantmentInstance> enchants = new ArrayList<>();
            RandomSource random = level.random;
            int enchantLevel = random.nextInt(3);
            int enchantCost = EnchantmentHelper.getEnchantmentCost(random, enchantLevel, 15, itemStack);
            List<EnchantmentInstance> instances = EnchantmentHelper.selectEnchantment(random, itemStack, enchantCost, optional.get().stream());
            enchants.add(instances.get(random.nextInt(instances.size())));
            itemStack.getItem().applyEnchantments(itemStack, enchants);
        }
    }

    private void createArmor(List<ItemStack> items, Item tierTwoItem, Item tierOneItem, Item defaultItem, boolean addOneTier, boolean addTwoTiers) {
        ItemStack armorItem = addTwoTiers ? tierTwoItem.getDefaultInstance() : addOneTier ? tierOneItem.getDefaultInstance() : defaultItem.getDefaultInstance();
        items.add(armorItem);
    }

    private boolean hasEmptyArmorSlots(LivingEntity caster) {
        for (ItemStack stack : caster.getArmorSlots()) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean inTestingPhase() {
        return true;
    }
}
