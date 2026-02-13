package com.ombremoon.spellbound.common.magic.acquisition.deception;

import com.mojang.serialization.Dynamic;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.DataComponentStorage;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualHelper;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.world.StructureHolderData;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionHolder;
import com.ombremoon.spellbound.common.magic.acquisition.divine.PlayerSpellActions;
import com.ombremoon.spellbound.common.magic.acquisition.divine.SpellAction;
import com.ombremoon.spellbound.common.world.dimension.DynamicDimensionFactory;
import com.ombremoon.spellbound.common.world.item.SpellTomeItem;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.main.Keys;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PuzzleDungeonData extends SavedData implements StructureHolderData {
    public static final Logger LOGGER = Constants.LOG;

    //Global
    private final Map<Integer, UUID> dungeonMap = new Int2ObjectOpenHashMap<>();
    private int dungeonId;

    //Dungeon Levels
    private UUID owner;
    private boolean spawnedDungeon;
    private ResourceKey<PuzzleConfiguration> configuration;
    private PuzzleDefinition currentDungeon;
    private final List<ActionHolder> objectives = new ArrayList<>();
    private final List<ActionHolder> resetConditions = new ArrayList<>();
    private boolean puzzleStarted;
    private boolean puzzleCompleted;
    private int dungeonIndex;
    @Nullable
    private BoundingBox dungeonBounds;

    public static PuzzleDungeonData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(PuzzleDungeonData::create, PuzzleDungeonData::load), "_puzzle_dungeon");
    }

    private PuzzleDungeonData() {}

    private static PuzzleDungeonData create() {
        return new PuzzleDungeonData();
    }

    public int incrementId() {
        this.dungeonId++;
        this.setDirty();
        return this.dungeonId;
    }

    public UUID getOrCreateUuid(MinecraftServer server, int dungeonId) {
        if (!this.dungeonMap.containsKey(dungeonId)) {
            UUID uuid;
            ResourceLocation dimension;
            ResourceKey<Level> levelKey;
            do {
                uuid = UUID.randomUUID();
                dimension = CommonClass.customLocation(uuid.toString());
                levelKey = ResourceKey.create(Registries.DIMENSION, dimension);
            } while (server.levelKeys().contains(levelKey));
            this.dungeonMap.put(dungeonId, uuid);
            this.setDirty();
        }
        return this.dungeonMap.get(dungeonId);
    }

    public ResourceKey<Level> getOrCreateKey(MinecraftServer server, int dungeonId) {
        UUID uuid = getOrCreateUuid(server, dungeonId);
        ResourceLocation dimension = CommonClass.customLocation(uuid + "_dungeon");
        return ResourceKey.create(Registries.DIMENSION, dimension);
    }

    public static boolean isDungeon(Level level) {
        return level.dimension().location().getPath().endsWith("_dungeon");
    }

    public static boolean isDungeonEmpty(ServerLevel level) {
        return isDungeon(level) && level.getPlayers(player -> !player.isSpectator()).isEmpty();
    }

    public static boolean hasRule(ServerLevel level, ResourceLocation rule) {
        PuzzleDungeonData data = PuzzleDungeonData.get(level);
        return isDungeon(level) && data.currentDungeon!= null && data.currentDungeon.hasRule(rule);
    }

    public void spawnDungeon(ServerLevel level) {
        ResourceLocation structureLoc = this.configuration.location();
        var optional = this.currentDungeon.alternativeConfigs();
        if (optional.isPresent()) {
            List<ResourceLocation> configs = optional.get();
            structureLoc = configs.get(level.random.nextInt(configs.size()));
        }

        if (DynamicDimensionFactory.spawnDungeon(level, structureLoc)) {
            this.spawnedDungeon = true;
            this.setDirty();
        }
    }

    public void spawnInDungeon(ServerLevel level, Player player) {
        if (!this.spawnedDungeon) {
            spawnDungeon(level);
        }

        if (this.currentDungeon != null && this.spawnedDungeon) {
            DynamicDimensionFactory.spawnInDungeon(level, player, this.currentDungeon, this);
            if (!this.puzzleStarted) {
                this.puzzleStarted = true;
                this.setDirty();
            }

            if (checkFlightRule(level, player)) {
                player.setData(SBData.NO_FLY_DUNGEON, true);
                PayloadHandler.updateAbilities((ServerPlayer) player);
            }
        }
    }

    public void initializeDungeon(Player player, ResourceKey<PuzzleConfiguration> configKey, PuzzleConfiguration config) {
        this.initializeDungeonInternal(player, configKey, config, 0);
    }

    private void initializeDungeonInternal(Player player, ResourceKey<PuzzleConfiguration> configKey, PuzzleConfiguration config, int puzzleIndex) {
        this.configuration = configKey;
        this.currentDungeon = config.puzzles().get(puzzleIndex);
        this.owner = player.getUUID();
        this.dungeonIndex = puzzleIndex;

        var handler = SpellUtil.getSpellHandler(player);
        PlayerSpellActions actions = handler.getSpellActions();
        int i = 0;
        String nameSpace = this.currentDungeon.puzzleId().getNamespace();
        String path = this.currentDungeon.puzzleId().getPath();
        for (SpellAction action : this.currentDungeon.objectives()) {
            ActionHolder actionHolder = new ActionHolder(ResourceLocation.fromNamespaceAndPath(nameSpace, path + "_" + i), action);
            actions.registerListeners(actionHolder);
            this.objectives.add(actionHolder);
            i++;
        }

        int j = 0;
        for (SpellAction action : this.currentDungeon.resetConditions()) {
            ActionHolder actionHolder = new ActionHolder(ResourceLocation.fromNamespaceAndPath(nameSpace, path + "_reset_" + j), action);
            actions.registerListeners(actionHolder);
            this.resetConditions.add(actionHolder);
            j++;
        }
        this.setDirty();
    }

    public boolean proceedToNextDungeon(ServerLevel level) {
        PuzzleConfiguration config = level.registryAccess().registryOrThrow(Keys.PUZZLE_CONFIG).getOrThrow(this.configuration);
        return this.dungeonIndex + 1 < config.getPuzzleCount();
    }

    public void completeObjective(ActionHolder action) {
        this.objectives.remove(action);
    }

    public boolean shouldCompleteObjective(ActionHolder action) {
        return this.objectives.contains(action);
    }

    public void handleDungeonLogic(ServerLevel level) {
        Player player = level.getPlayerByUUID(this.owner);
        if (player == null)
            return;

        if (this.objectives.isEmpty()) {
            this.puzzleCompleted = true;
            if (this.proceedToNextDungeon(level)) {
                this.sendToNextDungeon(level, player);
                this.destroyDimension(level);
            } else {
                this.spawnSpellTome(level, player);
            }

            if (hasRule(level, DungeonRules.NO_FLYING)) {
//                player.
            }

            this.setDirty();
            return;
        }

        if (player.tickCount % 5 == 0) {
            PlayerSpellActions actions = SpellUtil.getSpellHandler(player).getSpellActions();
            for (ActionHolder action : this.resetConditions) {
                if (actions.recentlyPerformedAction(action)) {
                    this.resetDungeon(level);
                }
            }
        }
    }

    public boolean checkFlightRule(ServerLevel level, Player player) {
        if (hasRule(level, DungeonRules.NO_FLYING)) {
            player.getAbilities().mayfly = false;
            return true;
        }
        return false;
    }

    private void resetDungeon(ServerLevel level) {

    }

    private void sendToNextDungeon(ServerLevel level, Player player) {
        MinecraftServer server = level.getServer();
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        PuzzleDungeonData data = PuzzleDungeonData.get(overworld);
        int dungeonId = data.incrementId();
        ResourceKey<Level> levelKey = data.getOrCreateKey(server, dungeonId);
        ServerLevel dungeon = DynamicDimensionFactory.getOrCreateDimension(server, levelKey);
        if (dungeon != null) {
            PuzzleDungeonData dungeonData = PuzzleDungeonData.get(dungeon);
            PuzzleConfiguration config = level.registryAccess().registryOrThrow(Keys.PUZZLE_CONFIG).getOrThrow(this.configuration);
            dungeonData.initializeDungeonInternal(player, this.configuration, config, this.dungeonIndex + 1);
        }
    }

    private void spawnSpellTome(ServerLevel level, Player player) {
        Vec3 spawnOffset = this.currentDungeon.spawnData().spellOffset();
        BlockPos center = this.getStructureCenter();
        if (center != null) {
            BlockPos spawnPos = center.offset((int) spawnOffset.x, (int) spawnOffset.y, (int) spawnOffset.z);
            SpellType<?> spell = SBSpells.REGISTRY.get(this.configuration.location());
            RitualHelper.createItem(
                    level,
                    spawnPos,
                    SpellTomeItem.createWithSpell(spell),
                    DataComponentStorage.optionalOf(
                            new TypedDataComponent<>(SBData.SPECIAL_PICKUP.get(), true)
                    ));
            //Spawn fx
        }
    }

    public PuzzleDefinition getCurrentDungeon() {
        return this.currentDungeon;
    }

    public boolean spawnedDungeon() {
        return this.spawnedDungeon;
    }

    public boolean hasPuzzleStarted() {
        return this.puzzleStarted;
    }

    public boolean isPuzzleCompleted() {
        return this.puzzleCompleted;
    }

    @Override
    public @Nullable BoundingBox getStructureBounds() {
        return this.dungeonBounds;
    }

    @Override
    public void setStructureBounds(@Nullable BoundingBox bounds) {
        this.dungeonBounds = bounds;
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag dungeonMapTag = new ListTag();
        for (var entry : dungeonMap.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt("DungeonId", entry.getKey());
            entryTag.putUUID("DungeonUUID", entry.getValue());
            dungeonMapTag.add(entryTag);
        }
        tag.put("Dungeons", dungeonMapTag);
        tag.putInt("CurrentDungeonId", this.dungeonId);

        if (this.spawnedDungeon) {
            ListTag objectivesTag = new ListTag();
            ListTag resetConditionsTag = new ListTag();

            tag.putUUID("Owner", this.owner);
            tag.putBoolean("SpawnedDungeon", true);
            if (this.configuration != null) {
                ResourceKey.codec(Keys.PUZZLE_CONFIG)
                        .encodeStart(NbtOps.INSTANCE, this.configuration)
                        .resultOrPartial(LOGGER::error)
                        .ifPresent(nbt -> tag.put("Configuration", nbt));
            }
            if (this.currentDungeon != null) {
                PuzzleDefinition.CODEC
                        .encodeStart(NbtOps.INSTANCE, this.currentDungeon)
                        .resultOrPartial(LOGGER::error)
                        .ifPresent(nbt -> tag.put("Dungeon", nbt));
            }
            for (ActionHolder actionHolder : this.objectives) {
                objectivesTag.add(actionHolder.serializeNBT());
            }
            tag.put("Objectives", objectivesTag);

            for (ActionHolder actionHolder : this.resetConditions) {
                resetConditionsTag.add(actionHolder.serializeNBT());
            }
            tag.put("ResetConditions", resetConditionsTag);
            tag.putBoolean("PuzzleStarted", this.puzzleStarted);
            tag.putBoolean("PuzzleCompleted", this.puzzleCompleted);
            tag.putInt("DungeonIndex", this.dungeonIndex);
        }

        return tag;
    }

    public void load(CompoundTag nbt) {
        this.dungeonMap.clear();
        final ListTag listTag = nbt.getList("Dungeons", 10);
        for (int i = 0, l = listTag.size(); i < l; i++) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int id = compoundTag.getInt("DungeonId");
            UUID uuid = compoundTag.getUUID("DungeonUUID");
            this.dungeonMap.put(id, uuid);
        }
        this.dungeonId = nbt.getInt("CurrentDungeonId");

        if (nbt.getBoolean("SpawnedDungeon")) {
            this.owner = nbt.getUUID("Owner");
            this.spawnedDungeon = true;
            if (nbt.contains("Configuration", 10)) {
                ResourceKey.codec(Keys.PUZZLE_CONFIG)
                        .parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("Configuration")))
                        .resultOrPartial(LOGGER::error)
                        .ifPresent(configuration -> this.configuration = configuration);
            }
            if (nbt.contains("Dungeon", 10)) {
                PuzzleDefinition.CODEC
                        .parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("Dungeon")))
                        .resultOrPartial(LOGGER::error)
                        .ifPresent(configuration -> this.currentDungeon = configuration);
            }

            final ListTag objectivesTag = nbt.getList("Objectives", 10);
            for (int i = 0, l = objectivesTag.size(); i < l; i++) {
                ActionHolder actionHolder = ActionHolder.deserializeNBT(objectivesTag.getCompound(i));
                this.objectives.add(actionHolder);
            }

            final ListTag resetConditionsTag = nbt.getList("ResetConditions", 10);
            for (int i = 0, l = resetConditionsTag.size(); i < l; i++) {
                ActionHolder actionHolder = ActionHolder.deserializeNBT(resetConditionsTag.getCompound(i));
                this.resetConditions.add(actionHolder);
            }

            this.puzzleStarted = nbt.getBoolean("PuzzleStarted");
            this.puzzleCompleted = nbt.getBoolean("PuzzleCompleted");
            this.dungeonIndex = nbt.getInt("DungeonIndex");
        }
    }

    public static PuzzleDungeonData load(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        PuzzleDungeonData data = create();
        data.load(nbt);
        return data;
    }
}
