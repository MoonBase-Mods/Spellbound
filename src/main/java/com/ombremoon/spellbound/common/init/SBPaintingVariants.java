package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

import java.util.ArrayList;
import java.util.List;

public interface SBPaintingVariants {
    //also in modtagprovider
    List<ResourceKey<PaintingVariant>> PAINTINGS = new ArrayList<>();
    ResourceKey<PaintingVariant> DECEPTION = create("deception");
    ResourceKey<PaintingVariant> DIVINE = create("divine");
    ResourceKey<PaintingVariant> FIRE = create("fire");
    ResourceKey<PaintingVariant> FISH = create("fish");
    ResourceKey<PaintingVariant> FROG = create("frog");
    ResourceKey<PaintingVariant> NUN = create("nun");
    ResourceKey<PaintingVariant> RUIN = create("ruin");
    ResourceKey<PaintingVariant> SNOWY_LAMPION = create("snowy_lampion");
    ResourceKey<PaintingVariant> STRIX = create("strix");
    ResourceKey<PaintingVariant> SUMMON = create("summon");
    ResourceKey<PaintingVariant> TRANSFIG = create("transfig");
    ResourceKey<PaintingVariant> VALKYR = create("valkyr");
    ResourceKey<PaintingVariant> VALKYR2 = create("valkyr2");
    ResourceKey<PaintingVariant> VILLAGE = create("village");
    ResourceKey<PaintingVariant> WITCH = create("witch");
    ResourceKey<PaintingVariant> WITCH2 = create("witch2");
    ResourceKey<PaintingVariant> FENRIR = create("fenrir");


    static void bootstrap(BootstrapContext<PaintingVariant> context) {
        register(context, DECEPTION, 1, 1);
        register(context, DIVINE, 1, 1);
        register(context, FIRE, 1, 1);
        register(context, FISH, 1, 1);
        register(context, FROG, 1, 1);
        register(context, NUN, 1, 2);
        register(context, RUIN, 1, 1);
        register(context, SNOWY_LAMPION, 1, 2);
        register(context, STRIX, 2, 2);
        register(context, SUMMON, 1, 1);
        register(context, TRANSFIG, 1, 1);
        register(context, VALKYR, 2, 2);
        register(context, VALKYR2, 2, 2);
        register(context, VILLAGE, 2, 2);
        register(context, WITCH, 1, 2);
        register(context, WITCH2, 1, 1);
        register(context, FENRIR, 2,2);
    }

    private static void register(BootstrapContext<PaintingVariant> context, ResourceKey<PaintingVariant> key, int width, int height) {
        context.register(key, new PaintingVariant(width, height, key.location()));
        PAINTINGS.add(key);
    }

    private static ResourceKey<PaintingVariant> create(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, CommonClass.customLocation(name));
    }
}