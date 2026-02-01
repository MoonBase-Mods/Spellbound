package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.skills.FamiliarAffinity;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class SBAffinities {
    public static final Map<ResourceLocation, FamiliarAffinity> REGISTRY = new HashMap<>();

    //Owl
    public static final FamiliarAffinity NIGHTS_EYE = createAffinity("nights_eye", 0);
    public static final FamiliarAffinity STEEL_FEATHERS = createAffinity("steel_feathers", 1);
    public static final FamiliarAffinity PHANTOM_SHIELD = createAffinity("phantom_shield", 2);
    public static final FamiliarAffinity OWL_VISION = createAffinity("owl_vision", 3);
    public static final FamiliarAffinity CLOUDLESS_SPEED = createAffinity("cloudless_speed", 4);
    public static final FamiliarAffinity TWISTED_HEAD = createAffinity("twisted_head", 600, 5);

    //Frog
    public static final FamiliarAffinity SPECTRAL_HOPS = createAffinity("spectral_hops", 0);
    public static final FamiliarAffinity SUBMERGED = createAffinity("submerged", 1200, 1);
    public static final FamiliarAffinity MAGMA_DIGESTION = createAffinity("magma_digestion", 2);
    public static final FamiliarAffinity ELONGATED_TONGUE = createAffinity("elongated_tongue", 3);
    public static final FamiliarAffinity MURKY_HABITAT = createAffinity("murky_habitat", 4);
    public static final FamiliarAffinity SLIMEY_EXPULSION = createAffinity("slimey_expuslion", 5);

    private static FamiliarAffinity createAffinity(String name, int requiredBond) {
        return createAffinity(name, 0, requiredBond);
    }

    private static FamiliarAffinity createAffinity(String name, int cooldown, int requiredBond) {
        return new FamiliarAffinity(CommonClass.customLocation(name), cooldown, requiredBond);
    }

    public static void register() {}
}
