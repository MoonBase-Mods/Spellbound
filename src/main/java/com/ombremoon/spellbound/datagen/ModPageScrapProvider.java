package com.ombremoon.spellbound.datagen;

import com.ombremoon.spellbound.common.init.SBPageScraps;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionHolder;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionRewards;
import com.ombremoon.spellbound.common.magic.acquisition.divine.DivineAction;
import com.ombremoon.spellbound.common.magic.acquisition.guides.triggers.LearnSpellTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.guides.triggers.SpellPredicate;
import com.ombremoon.spellbound.datagen.provider.PageScrapProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModPageScrapProvider extends PageScrapProvider {
    public ModPageScrapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<ActionHolder> writer) {
        DivineAction.Builder.divineAction()
                .addCriterion("learn_spell",
                        LearnSpellTrigger.Instance.learnSpell(
                                SpellPredicate.spell(SBSpells.SOLAR_RAY.get())))
                .rewards(ActionRewards.Builder.bookScrap(SBPageScraps.UNLOCKED_SOLAR_RAY))
                .save(writer, SBPageScraps.UNLOCKED_SOLAR_RAY);
    }

    @Override
    public String getName() {
        return "Page Scraps";
    }
}
