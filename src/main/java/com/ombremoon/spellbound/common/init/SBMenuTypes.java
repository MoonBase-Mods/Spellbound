package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.world.inventory.RiddleTradeMenu;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SBMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Constants.MOD_ID);

    public static final Supplier<MenuType<RiddleTradeMenu>> RIDDLE_TRADES = register("riddle_trades", RiddleTradeMenu::new);

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> register(String id, MenuType.MenuSupplier<T> factory) {
        return MENU_TYPES.register(id, () -> new MenuType<>(factory, FeatureFlags.VANILLA_SET));
    }

    public static void register(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }
}
