package com.ombremoon.spellbound.main;

import com.ombremoon.spellbound.client.gui.guide.*;
import com.ombremoon.spellbound.client.gui.guide.elements.TransfigurationRitualElement;
import com.ombremoon.spellbound.client.gui.guide.renderers.*;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.client.gui.guide.elements.*;
import com.ombremoon.spellbound.common.magic.acquisition.divine.DivineAction;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.TransfigurationRitual;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

//TODO: General - Discuss with Duck about bobHurt in GameRenderer
//TODO: General - Discuss with Duck about Followers

//Add judgement requirement for divine spell acquisition
//Add recent action cooldown for divine spell acquisition
//Change rituals to check one display per tick (startup time min depends on tier {tier 1  = min(4)})

//Alpha Bugs to Fix
//Dimension system
//Ritual Lag
//Loot Tables
//Shadow Gate Light Level
//Minor Book Changes

//Catalysts
//Ruin - Doubles Status Build Up
//Transfig - Rituals have double outpu
//Summons - Have 1 extra summon
//Light Divine - Gain Judgement From Casting
//Dark Divine - Lost Judgement From Casting
//Deception - More Potency/Reduced Mana Cost in Darkness

@Mod(Constants.MOD_ID)
public class Spellbound {

    public Spellbound(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
            event.dataPackRegistry(Keys.RITUAL, TransfigurationRitual.DIRECT_CODEC, TransfigurationRitual.DIRECT_CODEC);
            event.dataPackRegistry(Keys.MULTIBLOCKS, Multiblock.CODEC);
            event.dataPackRegistry(Keys.GUIDE_BOOK, GuideBookPage.CODEC, GuideBookPage.CODEC);
            event.dataPackRegistry(Keys.DIVINE_ACTION, DivineAction.CODEC, DivineAction.CODEC);
        });
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerRegistry);
        CommonClass.init(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void clientSetup(final FMLClientSetupEvent event) {
        registerElementRenderers();

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(CommonClass.customLocation("animation"), 42, Spellbound::registerPlayerAnimation);

        for (SpellPath spellPath : SpellPath.values()) {
            if (!spellPath.isSubPath()) {
                ItemProperties.register(SBItems.SPELL_TOME.get(), CommonClass.customLocation(spellPath.getSerializedName()), (stack, level, entity, seed) -> {
                    SpellType<?> spellType = stack.get(SBData.SPELL);
                    if (spellType != null) {
                        SpellPath spellPath1 = spellType.getPath();
                        return spellPath == spellPath1 ? 1.0F : 0.0F;
                    }

                    return 0.0F;
                });
            }
        }

        ItemProperties.register(SBItems.RITUAL_TALISMAN.get(), CommonClass.customLocation("rings"), (stack, level, entity, seed) -> {
            if (stack.is(SBItems.RITUAL_TALISMAN.get())) {
                Integer rings = stack.get(SBData.TALISMAN_RINGS.get());
                if (rings != null) {
                    return rings == 3 ? 1.0F : rings == 2 ? 0.5F : 0.0F;
                }
            }

            return 0.0F;
        });
    }

    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        return new ModifierLayer<>();
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

    private void registerElementRenderers() {
        ElementRenderDispatcher.register(GuideEntityElement.class, new GuideEntityRenderer());
        ElementRenderDispatcher.register(GuideImageElement.class, new GuideImageRenderer());
        ElementRenderDispatcher.register(GuideStaticItemElement.class, new GuideStaticItemRenderer());
        ElementRenderDispatcher.register(GuideItemListElement.class, new GuideItemListRenderer());
        ElementRenderDispatcher.register(GuideRecipeElement.class, new GuideRecipeRenderer());
        ElementRenderDispatcher.register(GuideSpellInfoElement.class, new GuideSpellInfoRenderer());
        ElementRenderDispatcher.register(GuideTextElement.class, new GuideTextRenderer());
        ElementRenderDispatcher.register(GuideTextListElement.class, new GuideTextListRenderer());
        ElementRenderDispatcher.register(GuideItemElement.class, new GuideItemRenderer());
        ElementRenderDispatcher.register(GuideTooltipElement.class, new GuideTooltipRenderer());
        ElementRenderDispatcher.register(GuideSpellBorderElement.class, new GuideSpellBorderRenderer());
        ElementRenderDispatcher.register(TransfigurationRitualElement.class, new GuideRitualRenderer());
        ElementRenderDispatcher.register(GuideEquipmentElement.class, new GuideEquipmentRenderer());
    }
}