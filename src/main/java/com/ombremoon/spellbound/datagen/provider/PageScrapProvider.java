package com.ombremoon.spellbound.datagen.provider;

import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionHolder;
import com.ombremoon.spellbound.common.magic.acquisition.divine.SpellAction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class PageScrapProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public PageScrapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "page_scraps");
        this.registries = registries;
    }

    public abstract void generate(HolderLookup.Provider registries, Consumer<ActionHolder> writer);

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<ActionHolder> consumer = actionHolder -> {
                if (!set.add(actionHolder.id())) {
                    throw new IllegalStateException("Duplicate page scrap " + actionHolder.id());
                } else {
                    Path path = this.pathProvider.json(actionHolder.id());
                    list.add(DataProvider.saveStable(output, provider, SpellAction.CODEC, actionHolder.value(), path));
                }
            };
            this.generate(provider, consumer);

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Page Scraps";
    }
}
