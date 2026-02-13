package com.ombremoon.spellbound.main;

import com.ombremoon.spellbound.client.AnimationHelper;
import com.ombremoon.spellbound.client.gui.guide.GuideTooltipRenderer;
import com.ombremoon.spellbound.client.gui.guide.elements.*;
import com.ombremoon.spellbound.client.gui.guide.renderers.*;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleConfiguration;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleDefinition;
import com.ombremoon.spellbound.common.magic.acquisition.divine.SpellAction;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.util.SpellUtil;
import com.zigythebird.playeranim.animation.PlayerAnimResources;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.RawAnimation;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractFadeModifier;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

//TODO: General - Discuss with Duck about bobHurt in GameRenderer
// bobHurt stuff?

//Change rituals to check one display per tick (startup time min depends on tier {tier 1  = min(4)})
//Add favorites button to spell select screen

//Catalysts
//Ruin - Doubles Status Build Up
//Transfig - Increases Range
//Summons - Have 1 extra summon/Summons killed by the staff fill a dormant Soul Shard
//Light Divine - Gain Judgement From Casting
//Dark Divine - Lost Judgement From Casting
//Deception - More Potency/Reduced Mana Cost in Darkness

//Armors
//Ruin - Resistances
//Transfig - Increases Duration
//Summons - Tankier Summons
//Light Divine - Resistance to Undead and Dark Magic
//Dark Divine - Reduces Nearby Light Magic Efficacy
//Deception - Grants Dodge Chance

//Functional Blocks
//Resonance Pillar - Upgrade and swap familiars
//Chroma Table - Customization options
// - Respec and skill point transfer

//Items
//Shard Pouch - Carry a bunch of shards

@Mod(Constants.MOD_ID)
public class Spellbound {

    public Spellbound(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
            event.dataPackRegistry(Keys.RITUAL, TransfigurationRitual.DIRECT_CODEC, TransfigurationRitual.DIRECT_CODEC);
            event.dataPackRegistry(Keys.MULTIBLOCKS, Multiblock.CODEC);
            event.dataPackRegistry(Keys.GUIDE_BOOK, GuideBookPage.CODEC, GuideBookPage.CODEC);
            event.dataPackRegistry(Keys.DIVINE_ACTION, SpellAction.CODEC, SpellAction.CODEC);
            event.dataPackRegistry(Keys.PUZZLE_CONFIG, PuzzleConfiguration.CODEC, PuzzleConfiguration.CODEC);
        });
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerRegistry);
        CommonClass.init(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void registerRegistry(NewRegistryEvent event) {
        event.register(SBSpells.REGISTRY);
        event.register(SBSkills.REGISTRY);
        event.register(SBDataTypes.REGISTRY);
        event.register(SBTriggers.REGISTRY);
        event.register(SBMultiblockSerializers.REGISTRY);
        event.register(SBRitualEffects.REGISTRY);
        event.register(SBPageElements.REGISTRY);
        event.register(SBBossFights.REGISTRY);
    }
}