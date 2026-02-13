package com.ombremoon.spellbound.common.magic.acquisition.bosses;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Dynamic;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.world.StructureHolderData;
import com.ombremoon.spellbound.common.world.block.entity.SummonPortalBlockEntity;
import com.ombremoon.spellbound.common.world.dimension.DynamicDimensionFactory;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;

public class ArenaSavedData extends SavedData implements StructureHolderData {
    public static final Logger LOGGER = Constants.LOG;

    //Global
    private final Map<Integer, UUID> arenaMap = new Int2ObjectOpenHashMap<>();
    private int arenaId;
    private final Multimap<UUID, Integer> closedArenas = ArrayListMultimap.create();

    //Arena levels
    private final PortalCache portalCache = new PortalCache();
    private boolean spawnedArena;
    private boolean fightStarted;
    private ResourceLocation spellLocation;
    private BossFightInstance<?, ?> currentBossFight;
    @Nullable
    private BoundingBox arenaBounds;

    public static ArenaSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(ArenaSavedData::create, ArenaSavedData::load), "_static_arena");
    }

    private ArenaSavedData() {}

    private static ArenaSavedData create() {
        return new ArenaSavedData();
    }

    public int incrementId() {
        this.arenaId++;
        this.setDirty();
        return this.arenaId;
    }

    public int getCurrentId() {
        return this.portalCache.getArenaID();
    }

    public UUID getOrCreateUuid(MinecraftServer server, int arenaId) {
        if (!this.arenaMap.containsKey(arenaId)) {
            UUID uuid;
            ResourceLocation dimension;
            ResourceKey<Level> levelKey;
            do {
                uuid = UUID.randomUUID();
                dimension = CommonClass.customLocation(uuid.toString());
                levelKey = ResourceKey.create(Registries.DIMENSION, dimension);
            } while (server.levelKeys().contains(levelKey));
            this.arenaMap.put(arenaId, uuid);
            this.setDirty();
        }
        return this.arenaMap.get(arenaId);
    }

    public ResourceKey<Level> getOrCreateKey(MinecraftServer server, int arenaId) {
        UUID uuid = getOrCreateUuid(server, arenaId);
        ResourceLocation dimension = CommonClass.customLocation(uuid + "_arena");
        return ResourceKey.create(Registries.DIMENSION, dimension);
    }

    public static boolean isArena(Level level) {
        return level.dimension().location().getPath().endsWith("_arena");
    }

    public static boolean isArenaEmpty(ServerLevel level) {
        return isArena(level) && level.getPlayers(player -> !player.isSpectator()).isEmpty();
    }

    public void spawnArena(ServerLevel level) {
        if (DynamicDimensionFactory.spawnArena(level, this.spellLocation)) {
            this.spawnedArena = true;
            this.setDirty();
            notifyPortalReady(level.getServer());
        }
    }

    private void notifyPortalReady(MinecraftServer server) {
        ResourceKey<Level> portalLevelKey = this.portalCache.getPortalLevel();
        BlockPos portalPos = this.portalCache.getPortalPos();
        if (portalLevelKey == null || portalPos == null) return;

        ServerLevel portalLevel = server.getLevel(portalLevelKey);
        if (portalLevel == null) return;

        BlockPos basePos = portalPos.offset(-4, 0, -4);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                BlockPos blockPos = basePos.offset(i, 0, j);
                if (portalLevel.getBlockState(blockPos).is(SBBlocks.SUMMON_PORTAL.get())) {
                    BlockEntity blockEntity = portalLevel.getBlockEntity(blockPos);
                    if (blockEntity instanceof SummonPortalBlockEntity summonBlockEntity) {
                        summonBlockEntity.setArenaReady(true);
                    }
                }
            }
        }
    }

    public void spawnInArena(ServerLevel level, Entity entity) {
        if (!this.spawnedArena) {
            spawnArena(level);
        }

        if (this.currentBossFight != null && this.spawnedArena) {
            DynamicDimensionFactory.spawnInArena(level, entity, this.currentBossFight.getBossFight());
            if (!this.fightStarted) {
                this.currentBossFight.start(level);
                this.fightStarted = true;
                this.setDirty();
            }
        }
    }

    public boolean spawnedArena() {
        return this.spawnedArena;
    }

    public boolean hasFightStarted() {
        return this.fightStarted;
    }

    public void handleBossFightLogic(ServerLevel level) {
        if (this.currentBossFight != null)
            this.currentBossFight.handleBossFightLogic(level);
    }

    public void endFight() {
        this.currentBossFight = null;
        this.setDirty();
    }

    public void initializeArena(ServerLevel level, Player player, int arenaId, BlockPos portalPos, ResourceKey<Level> portalLevel, ResourceLocation spellLocation, BossFight bossFight) {
        this.spellLocation = spellLocation;
        this.currentBossFight = bossFight.createFight(level);
        this.portalCache.loadCache(player, arenaId, portalPos, portalLevel);
        this.setDirty();
    }

    @Override
    public void destroyDimension(ServerLevel level) {
        StructureHolderData.super.destroyDimension(level);
        ServerLevel portalLevel = level.getServer().getLevel(this.portalCache.getPortalLevel());
        if (portalLevel != null)
            this.portalCache.destroyPortal(portalLevel);
    }

    public void cacheClosedArena(UUID owner, int id) {
        this.closedArenas.put(owner, id);
        this.setDirty();
    }

    public void closeCachedArenas(Player player) {
        for (var entry : this.closedArenas.asMap().entrySet()) {
            var arenas = entry.getValue();
            arenas.removeIf(id -> {
                var handler = SpellUtil.getSpellHandler(player);
                if (entry.getKey().equals(player.getUUID())) {
                    handler.closeArena(id);
                    this.setDirty();
                    return true;
                }
                return false;
            });

            if (arenas.isEmpty()) {
                this.closedArenas.removeAll(entry.getKey());
                this.setDirty();
            }
        }
    }

    public PortalCache getPortalCache() {
        return this.portalCache;
    }

    public BossFightInstance<?, ?> getCurrentBossFight() {
        return this.currentBossFight;
    }

    @Override
    public @Nullable BoundingBox getStructureBounds() {
        return this.arenaBounds;
    }

    @Override
    public void setStructureBounds(@Nullable BoundingBox bounds) {
        this.arenaBounds = bounds;
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag arenaMapTag = new ListTag();
        for (var entry : arenaMap.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt("ArenaId", entry.getKey());
            entryTag.putUUID("ArenaUUID", entry.getValue());
            arenaMapTag.add(entryTag);
        }
        tag.put("Arenas", arenaMapTag);

        ListTag closedArenaTag = new ListTag();
        for (var entry : closedArenas.asMap().entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("ArenaOwner", entry.getKey());
            entryTag.putIntArray("OpenArenas", entry.getValue().stream().toList());
            closedArenaTag.add(entryTag);
        }
        tag.put("ClosedArenas", closedArenaTag);
        tag.putInt("CurrentArenaId", this.arenaId);

        tag.putBoolean("SpawnedArena", this.spawnedArena);
        tag.putBoolean("FightStarted", this.fightStarted);
        tag.put("PortalCache", this.portalCache.serializeNBT());
        if (this.spellLocation != null) {
            ResourceLocation.CODEC
                    .encodeStart(NbtOps.INSTANCE, this.spellLocation)
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(nbt -> tag.put("ArenaSpell", nbt));
        }

        if (this.currentBossFight != null) {
            BossFightInstance.CODEC
                    .encodeStart(NbtOps.INSTANCE, this.currentBossFight)
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(nbt -> {
                        tag.put("CurrentBossFight", nbt);
                        this.currentBossFight.save(tag, registries);
                    });
        }
        return tag;
    }

    public void load(CompoundTag nbt) {
        this.arenaMap.clear();
        final ListTag listTag = nbt.getList("Arenas", 10);
        for (int i = 0, l = listTag.size(); i < l; i++) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int id = compoundTag.getInt("ArenaId");
            UUID uuid = compoundTag.getUUID("ArenaUUID");
            this.arenaMap.put(id, uuid);
        }
        final ListTag openArenaTag = nbt.getList("ArenasToClose", 10);
        for (int i = 0, l = openArenaTag.size(); i < l; i++) {
            CompoundTag compoundTag = openArenaTag.getCompound(i);
            UUID uuid = compoundTag.getUUID("ArenaOwner");
            for (int id : compoundTag.getIntArray("ClosedArenas")) {
                this.closedArenas.put(uuid, id);
            }
        }
        this.arenaId = nbt.getInt("CurrentArenaId");

        this.spawnedArena = nbt.getBoolean("SpawnedArena");
        this.fightStarted = nbt.getBoolean("FightStarted");
        this.portalCache.deserializeNBT(nbt.getCompound("PortalCache"));
        if (nbt.contains("ArenaSpell", 10)) {
            ResourceLocation.CODEC
                    .parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("ArenaSpell")))
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(location -> this.spellLocation = location);
        }

        if (nbt.contains("CurrentBossFight", 10)) {
            BossFightInstance.CODEC
                    .parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("CurrentBossFight")))
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(bossFightInstance -> {
                        this.currentBossFight = bossFightInstance;
                        bossFightInstance.load(nbt);
                    });
        }
    }

    public static ArenaSavedData load(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        ArenaSavedData data = create();
        data.load(nbt);
        return data;
    }
}
