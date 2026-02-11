package com.ombremoon.spellbound.common.magic.acquisition.divine;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Map;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class DivineActionManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = Constants.LOG;
    private static final Gson GSON = new GsonBuilder().create();
    private static Map<ResourceLocation, ActionHolder> ACTIONS = Map.of();
    private final HolderLookup.Provider registries;

    public DivineActionManager(HolderLookup.Provider registries) {
        super(GSON, "divine_action");
        this.registries = registries;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        RegistryOps<JsonElement> registryOps = this.makeConditionalOps();
        ImmutableMap.Builder<ResourceLocation, ActionHolder> builder = ImmutableMap.builder();
        object.forEach((location, jsonElement) -> {
            try {
                SpellAction action = ICondition.getWithWithConditionsCodec(SpellAction.CONDITIONAL_CODEC, registryOps, jsonElement).orElse(null);
                if (action == null) {
                    LOGGER.debug("Skipping loading divine action {} as its conditions were not met", location);
                    return;
                }
                this.validate(location, action);
                builder.put(location, new ActionHolder(location, action));
            } catch (Exception e) {
                LOGGER.error("Parsing error loading custom divine action {}: {}", location, e.getMessage());
            }
        });
        ACTIONS = builder.buildOrThrow();
        LOGGER.info("Loaded {} divine actions", ACTIONS.size());
    }

    private void validate(ResourceLocation location, SpellAction action) {
        ProblemReporter.Collector problemreporter$collector = new ProblemReporter.Collector();
        action.validate(problemreporter$collector, this.registries.asGetterLookup());
        problemreporter$collector.getReport().ifPresent(p_344260_ -> LOGGER.warn("Found validation problems in action {}: \n{}", location, p_344260_));
    }

    @Nullable
    public static ActionHolder get(ResourceLocation location) {
        return ACTIONS.get(location);
    }

    public static Collection<ActionHolder> getAllActions() {
        return ACTIONS.values();
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        DivineActionManager manager = new DivineActionManager(event.getRegistryAccess());
        event.addListener(manager);
    }
}
