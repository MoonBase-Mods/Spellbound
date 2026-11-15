package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.world.block.ManaSiphonBlock;
import com.ombremoon.spellbound.common.world.block.entity.*;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SBBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final Supplier<BlockEntityType<ManaSiphonBlockEntity>> MANA_SIPHON = BLOCK_ENTITY_TYPE.register("mana_siphon", () -> BlockEntityType.Builder.of(ManaSiphonBlockEntity::new, SBBlocks.MANA_SIPHON.get()).build(null));
    public static final Supplier<BlockEntityType<IceSheetBlockEntity>> ICE_SHEET = BLOCK_ENTITY_TYPE.register("ice_sheet", () -> BlockEntityType.Builder.of(IceSheetBlockEntity::new, SBBlocks.ICE_SHEET.get()).build(null));
    public static final Supplier<BlockEntityType<SummonPortalBlockEntity>> SUMMON_PORTAL = BLOCK_ENTITY_TYPE.register("summon_portal", () -> BlockEntityType.Builder.of(SummonPortalBlockEntity::new, SBBlocks.SUMMON_PORTAL.get()).build(null));
    public static final Supplier<BlockEntityType<RuneBlockEntity>> RUNE = BLOCK_ENTITY_TYPE.register("rune", () -> BlockEntityType.Builder.of(RuneBlockEntity::new, SBBlocks.RUNE.get()).build(null));
    public static final Supplier<BlockEntityType<TransfigurationDisplayBlockEntity>> TRANSFIGURATION_DISPLAY = BLOCK_ENTITY_TYPE.register("transfiguration_display", () -> BlockEntityType.Builder.of(TransfigurationDisplayBlockEntity::new, SBBlocks.TRANSFIGURATION_DISPLAY.get()).build(null));
    public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL = BLOCK_ENTITY_TYPE.register("pedestal", () -> BlockEntityType.Builder.of(PedestalBlockEntity::new, SBBlocks.TRANSFIGURATION_PEDESTAL.get()).build(null));
    public static final Supplier<BlockEntityType<ValkyrBlockEntity>> VALKY_STATUE = BLOCK_ENTITY_TYPE.register("valkyr_statue", () -> BlockEntityType.Builder.of(ValkyrBlockEntity::new, SBBlocks.VALKYR_STATUE.get()).build(null));
    public static final Supplier<BlockEntityType<DarkAltarBlockEntity>> DARK_ALTAR = BLOCK_ENTITY_TYPE.register("dark_altar", () -> BlockEntityType.Builder.of(DarkAltarBlockEntity::new, SBBlocks.DARK_ALTAR.get()).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPE.register(modEventBus);
    }
}
