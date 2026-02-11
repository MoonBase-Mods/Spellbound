package com.ombremoon.spellbound.main;

import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleConfiguration;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleDefinition;
import com.ombremoon.spellbound.common.magic.acquisition.divine.SpellAction;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;

public class Keys {
    public static final ResourceKey<Registry<TransfigurationRitual>> RITUAL = ResourceKey.createRegistryKey(CommonClass.customLocation("transfiguration_ritual"));
    public static final ResourceKey<Registry<SpellAction>> DIVINE_ACTION = ResourceKey.createRegistryKey(CommonClass.customLocation("divine_action"));
    public static final ResourceKey<Registry<Multiblock>> MULTIBLOCKS = ResourceKey.createRegistryKey(CommonClass.customLocation("multiblocks"));
    public static final ResourceKey<Registry<GuideBookPage>> GUIDE_BOOK = ResourceKey.createRegistryKey(CommonClass.customLocation("guide_books"));
    public static final ResourceKey<Registry<PuzzleConfiguration>> PUZZLE_CONFIG = ResourceKey.createRegistryKey(CommonClass.customLocation("puzzle_config"));

    public static ResourceKey<Biome> EMPTY_BIOME = ResourceKey.create(Registries.BIOME, CommonClass.customLocation("empty"));

    public static final GameRules.Key<GameRules.BooleanValue> CONSUME_MANA = GameRules.register("spellboundConsumeMana", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
}
