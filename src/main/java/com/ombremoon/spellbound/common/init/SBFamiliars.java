package com.ombremoon.spellbound.common.init;

import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.familiars.Familiar;
import com.ombremoon.spellbound.common.magic.familiars.FamiliarHandler;
import com.ombremoon.spellbound.common.magic.skills.FamiliarAffinity;
import com.ombremoon.spellbound.common.magic.familiars.FamiliarHolder;
import com.ombremoon.spellbound.common.world.entity.living.familiars.FrogEntity;
import com.ombremoon.spellbound.common.world.familiars.FrogFamiliar;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SBFamiliars {
    public static final Map<ResourceLocation, FamiliarHolder<?, ?>> REGISTRY = new HashMap<>();

    public static final FamiliarHolder<FrogEntity, FrogFamiliar> FROG = createFamiliar(
            "frog", SBEntities.FROG, SpellMastery.NOVICE, FrogFamiliar::new,
            SBAffinities.SPECTRAL_HOPS, SBAffinities.WARTFUL_SKIN, SBAffinities.MAGMA_DIGESTION,
            SBAffinities.ELONGATED_TONGUE, SBAffinities.MURKY_HABITAT, SBAffinities.SLIMEY_EXPULSION);

    private static <E extends LivingEntity, F extends Familiar<E>> FamiliarHolder<E, F> createFamiliar(String name, Supplier<EntityType<E>> entity, SpellMastery reqMastery, FamiliarHolder.FamiliarBuilder<F> constructor, FamiliarAffinity... affinities) {
        var holder = new FamiliarHolder<E, F>(CommonClass.customLocation(name), entity, reqMastery, constructor, 5, affinities);
        REGISTRY.put(CommonClass.customLocation(name), holder);
        return holder;
    }

    public static void register() {}
}
