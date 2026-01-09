package com.ombremoon.spellbound.common.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ombremoon.spellbound.common.world.entity.SBMerchantType;
import com.ombremoon.spellbound.common.world.item.SpellTomeItem;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class SBTrades {
    public static final Map<SBMerchantType, Int2ObjectMap<MerchantOffer[]>> TRADES = Util.make(Maps.newHashMap(), map -> {
        map.put(SBMerchantType.SPELL_BROKER, toIntMap(ImmutableMap.of(1, new MerchantOffer[]{
                spellTrade(8, Items.LIGHTNING_ROD, 4, SBSpells.STORMSTRIKE.get()),
                spellTrade(8, Items.COPPER_TRAPDOOR, 4, SBSpells.ELECTRIC_CHARGE.get()),
                spellTrade(48, Items.BLUE_ICE, 16, SBSpells.SHATTERING_CRYSTAL.get()),
                spellTrade(48, Items.MAGMA_CREAM, 32, SBSpells.SOLAR_RAY.get()),
                spellTrade(64, Items.LIGHTNING_ROD, 32, SBSpells.STORM_RIFT.get()),
                spellTrade(32, Items.LEAD, 2, SBSpells.SHADOWBOND.get()),
                spellTrade(32, Items.MILK_BUCKET, 1, SBSpells.PURGE_MAGIC.get())
        }, 2, new MerchantOffer[]{
                spellTrade(8, Items.LIGHTNING_ROD, 4, SBSpells.STORMSTRIKE.get()),
                spellTrade(8, Items.COPPER_TRAPDOOR, 4, SBSpells.ELECTRIC_CHARGE.get()),
                spellTrade(48, Items.BLUE_ICE, 16, SBSpells.SHATTERING_CRYSTAL.get()),
                spellTrade(48, Items.MAGMA_CREAM, 32, SBSpells.SOLAR_RAY.get()),
                spellTrade(64, Items.LIGHTNING_ROD, 32, SBSpells.STORM_RIFT.get()),
                spellTrade(32, Items.LEAD, 2, SBSpells.SHADOWBOND.get()),
                spellTrade(32, Items.MILK_BUCKET, 1, SBSpells.PURGE_MAGIC.get())
        })));
    });

    private static MerchantOffer spellTrade(int arcanthusCost, ItemLike item, int count, SpellType<?> spell) {
        return makeOffer(SBBlocks.ARCANTHUS.get().asItem(), arcanthusCost, item, count, SpellTomeItem.createWithSpell(spell), 999, 0, 0f);
    }

    private static MerchantOffer makeOffer(@NotNull ItemLike item, int count, @NotNull ItemStack result, int maxUses, int xp, float multiplier) {
        return makeOffer(item, count, null, null, result, maxUses, xp, multiplier);
    }

    private static MerchantOffer makeOffer(@NotNull ItemLike item1, int count1, @Nullable ItemLike item2, @Nullable Integer count2, @NotNull ItemStack result, int maxUses, int xp, float multiplier) {
        ItemCost costA = new ItemCost(item1, count1);
        Optional<ItemCost> costB = Optional.ofNullable(item2 == null || count2 == null ? null : new ItemCost(item2, count2));
        return new MerchantOffer(
                costA,
                costB,
                result,
                maxUses,
                xp,
                multiplier
        );
    }

    private static Int2ObjectMap<MerchantOffer[]> toIntMap(ImmutableMap<Integer, MerchantOffer[]> map) {
        return new Int2ObjectOpenHashMap(map);
    }
}
