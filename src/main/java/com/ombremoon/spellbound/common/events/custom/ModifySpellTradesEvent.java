package com.ombremoon.spellbound.common.events.custom;

import com.ombremoon.spellbound.common.world.entity.SBMerchantType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.Event;

import java.util.List;

public class ModifySpellTradesEvent extends Event {
    protected final SBMerchantType type;
    protected final Int2ObjectMap<List<MerchantOffer>> trades;

    public ModifySpellTradesEvent(SBMerchantType type, Int2ObjectMap<List<MerchantOffer>> trades) {
        this.type = type;
        this.trades = trades;
    }

    public SBMerchantType getType() {
        return type;
    }

    public Int2ObjectMap<List<MerchantOffer>> getTrades() {
        return trades;
    }
}
