package com.ombremoon.spellbound.common.magic.acquisition.deception;

import com.mojang.serialization.Dynamic;
import com.ombremoon.spellbound.common.world.dimension.DynamicDimensionFactory;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.main.Keys;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;

public class PuzzleDungeonData extends SavedData {
    public static final Logger LOGGER = Constants.LOG;

    //Global
    private final Map<Integer, UUID> dungeonMap = new Int2ObjectOpenHashMap<>();
    private int dungeonId;

    //Dungeon Levels
    private boolean spawnedDungeon;
    private ResourceKey<PuzzleConfiguration> configuration;
    private PuzzleDefinition currentDungeon;
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

    public void spawnDungeon(ServerLevel level) {
        /*if (DynamicDimensionFactory.spawnSpellStructure(level, this.currentDungeon)) {
            this.spawnedDungeon = true;
            this.setDirty();
        }*/
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
        tag.putBoolean("SpawnedDungeon", this.spawnedDungeon);
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
        this.spawnedDungeon = nbt.getBoolean("SpawnedDungeon");
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
    }

    public static PuzzleDungeonData load(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        PuzzleDungeonData data = create();
        data.load(nbt);
        return data;
    }
}
