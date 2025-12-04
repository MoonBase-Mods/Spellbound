package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookPage;
import com.ombremoon.spellbound.datagen.provider.GuideBookProvider;
import com.ombremoon.spellbound.datagen.provider.guide_builders.PageBuilder;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ModGuideBookProvider extends GuideBookProvider {
    //Books
    private static final ResourceLocation GRIMOIRE = loc("grimoire_of_annihilation");

    //Pages
    private static final ResourceLocation RUIN_P1 = loc("sb_ruin_v1_p1");
    private static final ResourceLocation RUIN_P2 = loc("sb_ruin_v1_p2");
    private static final ResourceLocation RUIN_P3 = loc("sb_ruin_v1_p3");
    private static final ResourceLocation SOLAR_RAY = loc("solar_ray_page");

    public ModGuideBookProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, BiConsumer<ResourceLocation, GuideBookPage> writer) {
        PageBuilder
                .forBook(GRIMOIRE)
                .addElements(
                        PageBuilder.Text
                                .of("item.spellbound.grimoire_of_annihilation")
                                .position(PAGE_TWO_START + 13, 20)
                                .build(),
                        PageBuilder.Text
                                .of("guide.ruin.v1_p1.description")
                                .position(PAGE_TWO_START, 50)
                                .build()
                ).save(writer, RUIN_P1);


        PageBuilder
                .forBook(GRIMOIRE)
                .setPreviousPage(RUIN_P1)
                .addElements(
                        PageBuilder.Image
                                .of(CommonClass.customLocation("textures/gui/books/images/ruin_portal.png"))
                                .setDimensions(140, 74)
                                .build(),
                        PageBuilder.Text
                                .of("guide.ruin.v1_p2.ruin_portal")
                                .position(0, 80)
                                .build(),
                        PageBuilder.Text
                                .of("guide.ruin.v1_p2.keystone")
                                .position(PAGE_TWO_START, 5)
                                .build(),
                        PageBuilder.Recipe
                                .of(ResourceLocation.withDefaultNamespace("anvil"))
                                .gridName(PageBuilder.Recipe.SpellboundGrids.GRIMOIRE)
                                .position(195, 125)
                                .build()
                ).save(writer, RUIN_P2);

        PageBuilder
                .forBook(GRIMOIRE)
                .setPreviousPage(RUIN_P2)
                .addElements(
                        PageBuilder.Text
                                .of("guide.ruin.v1_p3.keystones")
                                .build(),
                        PageBuilder.Image
                                .of(loc("textures/gui/books/images/broker_tower.png"))
                                .setDimensions(150, 87)
                                .position(PAGE_TWO_START, 0)
                                .build(),
                        PageBuilder.Text
                                .of("guide.ruin.v1_p3.spell_broker")
                                .position(PAGE_TWO_START, 95)
                                .build()
                ).save(writer, RUIN_P3);

        PageBuilder
                .forBook(GRIMOIRE)
                .setPreviousPage(RUIN_P3)
                .addElements(
                        PageBuilder.Text
                                .of("guide.ruin.solar_ray.title")
                                .build(),
                        PageBuilder.EntityRenderer
                                .of(SBEntities.SOLAR_RAY.get())
                                .position(20, 40).build(),
                        PageBuilder.Text
                                .of("guide.ruin.solar_ray.spell_lore")
                                .position(0, 60)
                                .build(),
                        PageBuilder.SpellInfo
                                .of(SBSpells.SOLAR_RAY.get())
                                .position(PAGE_TWO_START, 65)
                                .build()
                ).save(writer, SOLAR_RAY);
    }

    private static ResourceLocation loc(String path) {
        return CommonClass.customLocation(path);
    }
}
