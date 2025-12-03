package com.ombremoon.spellbound.common.world.multiblock;

import com.google.common.collect.Sets;
import com.ombremoon.spellbound.main.Keys;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MultiblockProvider implements DataProvider {
    protected final PackOutput.PathProvider multiblockPathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public MultiblockProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.multiblockPathProvider = output.createRegistryElementsPathProvider(Keys.MULTIBLOCKS);
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(provider -> this.run(output, provider));
    }

    protected CompletableFuture<?> run(final CachedOutput output, final HolderLookup.Provider registries) {
        final Set<ResourceLocation> set = Sets.newHashSet();
        final List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildMultiblocks(
                (location, multiblock) -> {
                    if (!set.add(location)) {
                        throw new IllegalStateException("Duplicate multiblock "+ location);
                    } else {
                        list.add(DataProvider.saveStable(output, registries, Multiblock.CODEC, multiblock, MultiblockProvider.this.multiblockPathProvider.json(location)));
                    }
                }, registries
        );
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    protected void buildMultiblocks(MultiblockOutput multiblockOutput, HolderLookup.Provider registries) {
        buildMultiblocks(multiblockOutput);
    }

    protected void buildMultiblocks(MultiblockOutput multiblockOutput) {}

    @Override
    public String getName() {
        return "Multiblocks";
    }
}
