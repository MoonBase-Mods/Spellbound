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
    ResourceKey<PaintingVariant> ON_FIRE = create("on_fire");
    ResourceKey<PaintingVariant> UNDER_THE_SEA = create("under_the_sea");
    ResourceKey<PaintingVariant> PARALLEL_DECISIONS = create("parallel_decisions");
    ResourceKey<PaintingVariant> BLESSED_IS_SHE = create("blessed_is_she");
    ResourceKey<PaintingVariant> RUIN = create("ruin");
    ResourceKey<PaintingVariant> GAZES_IN_WHITE = create("gazes_in_white");
    ResourceKey<PaintingVariant> GHOST_FEATHER = create("ghost_feather");
    ResourceKey<PaintingVariant> SUMMON = create("summon");
    ResourceKey<PaintingVariant> TRANSFIG = create("transfig");
    ResourceKey<PaintingVariant> VALKYR = create("valkyr");
    ResourceKey<PaintingVariant> JUSTICE_FROM_ABOVE = create("justice_from_above");
    ResourceKey<PaintingVariant> INSIDE_MEMORIES = create("inside_memories");
    ResourceKey<PaintingVariant> FORGOTTEN_MEMORIES = create("forgotten_memories");
    ResourceKey<PaintingVariant> WITCH_HOMEWORKS = create("witch_homeworks");
    ResourceKey<PaintingVariant> A_WOLF_LAMENT = create("a_wolf_lament");

    //ResourceKey<PaintingVariant> x = create("");
    //You know I love you Ombre, and you love me for all these paintings no one asked for

    //Lmao, at least someone is doing it -Ombre
    ResourceKey<PaintingVariant> LA_SENTENZA = create("la_sentenza");
    ResourceKey<PaintingVariant> SHARED_DREAMS = create("shared_dreams");

    ResourceKey<PaintingVariant> ANOTHER_BEGINNING = create("another_beginning");
    ResourceKey<PaintingVariant> ANOTHER_STORY = create("another_story");
    ResourceKey<PaintingVariant> ANOTHER_STORY_DOWN = create("another_story_down");
    ResourceKey<PaintingVariant> BENEATH_EMOTIONS = create("beneath_emotions");
    ResourceKey<PaintingVariant> BENEATH_EMOTIONS_DOWN = create("beneath_emotions_down");
    ResourceKey<PaintingVariant> BENEATH_ILLUSIONS = create("beneath_illusions");
    ResourceKey<PaintingVariant> SCRAPS_FROM_AFAR1 = create("scraps_from_afar1");
    ResourceKey<PaintingVariant> SCRAPS_FROM_AFAR2 = create("scraps_from_afar2");
    ResourceKey<PaintingVariant> SCRAPS_FROM_AFAR3 = create("scraps_from_afar3");
    ResourceKey<PaintingVariant> SCRAPS_FROM_AFAR4 = create("scraps_from_afar4");
    ResourceKey<PaintingVariant> FALSE_STEPS = create("false_steps");
    ResourceKey<PaintingVariant> FLOATING_POINT = create("floating_point");
    ResourceKey<PaintingVariant> KNUCKLE_VELVET = create("knuckle_velvet");
    ResourceKey<PaintingVariant> MONDADLER = create("mondadler");
    ResourceKey<PaintingVariant> LOST_HOME = create("lost_home");
    ResourceKey<PaintingVariant> MOUNTAIN_TOP = create("mountain_top");
    ResourceKey<PaintingVariant> PEAK = create("peak");
    ResourceKey<PaintingVariant> REMNANT = create("remnant");
    ResourceKey<PaintingVariant> ROOM = create("room");
    ResourceKey<PaintingVariant> UNKNOWN_SKY = create("unknown_sky");
    ResourceKey<PaintingVariant> UNYIELDING_PAST = create("unyielding_past");




    static void bootstrap(BootstrapContext<PaintingVariant> context) {
        register(context, DECEPTION, 1, 1);
        register(context, DIVINE, 1, 1);
        register(context, ON_FIRE, 1, 1);
        register(context, UNDER_THE_SEA, 1, 1);
        register(context, PARALLEL_DECISIONS, 1, 1);
        register(context, BLESSED_IS_SHE, 1, 2);
        register(context, RUIN, 1, 1);
        register(context, GAZES_IN_WHITE, 1, 2);
        register(context, GHOST_FEATHER, 2, 2);
        register(context, SUMMON, 1, 1);
        register(context, TRANSFIG, 1, 1);
        register(context, VALKYR, 2, 2);
        register(context, JUSTICE_FROM_ABOVE, 2, 2);
        register(context, INSIDE_MEMORIES, 2, 2);
        register(context, FORGOTTEN_MEMORIES, 1, 2);
        register(context, WITCH_HOMEWORKS, 1, 1);
        register(context, A_WOLF_LAMENT, 2,2);

        register(context, LA_SENTENZA, 2, 2);
        register(context, SHARED_DREAMS, 2, 3);
        register(context, ANOTHER_BEGINNING, 2, 2);
        register(context, ANOTHER_STORY, 2, 2);
        register(context, ANOTHER_STORY_DOWN, 2, 2);
        register(context, BENEATH_ILLUSIONS, 1, 2);
        register(context, BENEATH_EMOTIONS, 1, 2);
        register(context, BENEATH_EMOTIONS_DOWN, 1, 2);
        register(context, SCRAPS_FROM_AFAR1, 2,1);
        register(context, SCRAPS_FROM_AFAR2, 2,1);
        register(context, SCRAPS_FROM_AFAR3, 2,1);
        register(context, SCRAPS_FROM_AFAR4, 2,1);
        register(context, FALSE_STEPS, 1, 1);
        register(context,FLOATING_POINT, 4,4);
        register(context,KNUCKLE_VELVET,3,2);
        register(context,MONDADLER, 3,2);
        register(context,LOST_HOME, 2, 1);
        register(context,MOUNTAIN_TOP,2,1);
        register(context,PEAK, 1, 1);
        register(context, REMNANT, 1, 2);
        register(context,ROOM, 2,1);
        register(context,UNKNOWN_SKY, 2, 1);
        register(context, UNYIELDING_PAST, 2, 2);





    }

    private static void register(BootstrapContext<PaintingVariant> context, ResourceKey<PaintingVariant> key, int width, int height) {
        context.register(key, new PaintingVariant(width, height, key.location()));
        PAINTINGS.add(key);
    }

    private static ResourceKey<PaintingVariant> create(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, CommonClass.customLocation(name));
    }
}