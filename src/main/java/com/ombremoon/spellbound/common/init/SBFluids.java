package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.main.Constants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class SBFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Constants.MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Constants.MOD_ID);

    public static final Supplier<FluidType> HOLY_WATER_TYPE = FLUID_TYPES.register(
            "holy_water",
            location -> new FluidType(FluidType.Properties.create()
                    .descriptionId(Util.makeDescriptionId("fluid", location))
                    .fallDistanceModifier(0F)
                    .canExtinguish(true)
                    .canConvertToSource(true)
                    .supportsBoating(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                    .canHydrate(true)) {
                @Override
                public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
                    if (reader instanceof Level level) {
                        return level.getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION);
                    }

                    return super.canConvertToSource(state, reader, pos);
                }
            });


    public static final Supplier<FlowingFluid> HOLY_WATER = registerFluid("holy_water", () -> new BaseFlowingFluid.Source(SBFluids.HOLY_WATER_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_HOLY_WATER = registerFluid("flowing_holy_water", () -> new BaseFlowingFluid.Flowing(SBFluids.HOLY_WATER_PROPERTIES));

    public static final BaseFlowingFluid.Properties HOLY_WATER_PROPERTIES = new BaseFlowingFluid.Properties(HOLY_WATER_TYPE, HOLY_WATER, FLOWING_HOLY_WATER)
            .bucket(SBItems.HOLY_WATER_BUCKET)
            .block(SBBlocks.HOLY_WATER)
            .explosionResistance(100);

    public static <T extends Fluid> Supplier<T> registerFluid(String name, Supplier<T> fluid) {
        return FLUIDS.register(name, fluid);
    }

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
    }
}
