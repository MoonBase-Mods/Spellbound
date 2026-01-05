package com.ombremoon.spellbound.main;

import com.ombremoon.spellbound.common.magic.acquisition.divine.DivineAction;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;

public class Keys {
    public static final ResourceKey<Registry<TransfigurationRitual>> RITUAL = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("transfiguration_ritual"));
    public static final ResourceKey<Registry<DivineAction>> DIVINE_ACTION = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("divine_action"));
    public static final ResourceKey<Registry<Multiblock>> MULTIBLOCKS = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("multiblocks"));
    public static final ResourceKey<Registry<GuideBookPage>> GUIDE_BOOK = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("guide_books"));

    public static ResourceKey<Biome> EMPTY_BIOME = ResourceKey.create(Registries.BIOME, CommonClass.customLocation("empty"));

    public static final GameRules.Key<GameRules.BooleanValue> CONSUME_MANA = GameRules.register("spellboundConsumeMana", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
}
