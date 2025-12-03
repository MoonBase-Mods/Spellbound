package com.ombremoon.spellbound.common.world.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBLootFunctions;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.world.item.SpellTomeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Supplier;

public class SetSpellFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetSpellFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance)
                    .and(SBSpells.REGISTRY.byNameCodec().fieldOf("spellType").forGetter(function -> function.spellType)
                    ).apply(instance, SetSpellFunction::new)
    );
    private final SpellType<?> spellType;

    private SetSpellFunction(List<LootItemCondition> predicates, SpellType<?> spellType) {
        super(predicates);
        this.spellType = spellType;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return SBLootFunctions.SET_SPELL.get();
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        itemStack.set(SBData.SPELL, this.spellType);
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> setSpell(Supplier<? extends SpellType<?>> spellType) {
        return simpleBuilder(lootItemConditions -> new SetSpellFunction(lootItemConditions, spellType.get()));
    }
}
