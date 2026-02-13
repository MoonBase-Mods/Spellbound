package com.ombremoon.spellbound.common.world.dimension;

import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleDefinition;
import com.ombremoon.spellbound.common.world.StructureHolderData;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.ArenaSavedData;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.BossFight;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleDungeonData;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

/**
 * @author Commoble, used with permission.
 * https://gist.github.com/Commoble/7db2ef25f94952a4d2e2b7e3d4be53e0
 */
public class DynamicDimensionFactory {
    private static final Logger LOGGER = Constants.LOG;
    public static final BlockPos ORIGIN = new BlockPos(0, 64, 0);

    public static ServerLevel getOrCreateDimension(MinecraftServer server, ResourceKey<Level> levelKey) {
        return DimensionCreator.get().getOrCreateLevel(server, levelKey, () -> createLevel(server));
    }

    private static LevelStem createLevel(MinecraftServer server) {
        ChunkGenerator newChunkGenerator = new EmptyChunkGenerator(server);
        Holder<DimensionType> typeHolder = server.overworld().dimensionTypeRegistration();
        return new LevelStem(typeHolder, newChunkGenerator);
    }

    public static void spawnInArena(ServerLevel level, Entity entity, BossFight bossFight) {
        BlockPos blockPos = ORIGIN;
        level.getChunkAt(blockPos);

        Vec3 spawnOffset = bossFight.getPlayerSpawnOffset();
        int randomOffsetX = level.random.nextInt(3);
        int randomOffsetZ = level.random.nextInt(3);
        BlockPos offsetPos = blockPos.offset((int) spawnOffset.x + randomOffsetX, (int) spawnOffset.y, (int) spawnOffset.z + randomOffsetZ);
        Vec3 targetVec = Vec3.atBottomCenterOf(offsetPos);

        sendToDimension(entity, level, targetVec);
    }

    public static void spawnInDungeon(ServerLevel level, Player player, PuzzleDefinition puzzle, StructureHolderData structure) {
        BlockPos center = structure.getStructureCenter();
        if (center != null) {
            level.getChunkAt(center);
            Vec3 spawnOffset = puzzle.spawnData().playerOffset();
            BlockPos offsetPos = center.offset((int) spawnOffset.x, (int) spawnOffset.y, (int) spawnOffset.z);
            Vec3 targetVec = Vec3.atBottomCenterOf(offsetPos);
            sendToDimension(player, level, targetVec);
        }
    }

    public static boolean spawnArena(ServerLevel level, ResourceLocation structureLoc) {
        return spawnSpellStructure(level, structureLoc, ArenaSavedData.get(level));
    }

    public static boolean spawnDungeon(ServerLevel level, ResourceLocation structureLoc) {
        return spawnSpellStructure(level, structureLoc, PuzzleDungeonData.get(level));
    }

    public static boolean spawnSpellStructure(ServerLevel level, ResourceLocation structureLoc, StructureHolderData dimension) {
        BlockPos origin = ORIGIN;
        level.getChunkAt(origin);
        return spawnSpellStructure(level, origin, structureLoc, dimension);
    }

    private static boolean spawnSpellStructure(ServerLevel level, BlockPos origin, ResourceLocation structureLoc, StructureHolderData dimension) {
        Structure structure = getSpellStructure(level, structureLoc).value();
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        StructureStart start = structure.generate(
                level.registryAccess(),
                generator,
                generator.getBiomeSource(),
                level.getChunkSource().randomState(),
                level.getStructureManager(),
                level.getSeed(),
                new ChunkPos(origin),
                0,
                level,
                holder -> true
        );
        if (start.isValid()) {
            BoundingBox boundingBox = start.getBoundingBox();
            LOGGER.debug("[Spell Structure Spawn] Structure valid | BoundingBox: min({}, {}, {}) max({}, {}, {})",
                    boundingBox.minX(), boundingBox.minY(), boundingBox.minZ(),
                    boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ());

            dimension.setStructureBounds(boundingBox);

            ChunkPos chunkPos = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.minX()), SectionPos.blockToSectionCoord(boundingBox.minZ()));
            ChunkPos chunkPos1 = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.maxX()), SectionPos.blockToSectionCoord(boundingBox.maxZ()));

            LOGGER.debug("[Spell Structure Spawn] Placing in chunks from {} to {}", chunkPos, chunkPos1);

            ChunkPos.rangeClosed(chunkPos, chunkPos1)
                    .forEach(pos -> start.placeInChunk(
                            level,
                            level.structureManager(),
                            generator,
                            level.getRandom(),
                            new BoundingBox(
                                    pos.getMinBlockX(),
                                    level.getMinBuildHeight(),
                                    pos.getMinBlockZ(),
                                    pos.getMaxBlockX(),
                                    level.getMaxBuildHeight(),
                                    pos.getMaxBlockZ()
                            ),
                            pos
                    ));

            LOGGER.debug("[Spell Structure Spawn] Spell structure generation complete");
            return true;
        }

        LOGGER.warn("[Spell Structure Spawn] Structure generation FAILED - StructureStart invalid | Structure Location: {}", structureLoc);
        return false;
    }

    private static Holder.Reference<Structure> getSpellStructure(ServerLevel level, ResourceLocation structure) {
        ResourceKey<Structure> resourceKey = ResourceKey.create(Registries.STRUCTURE, structure);
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        return registry.getHolder(resourceKey).orElseGet(() -> registry.getHolderOrThrow(ResourceKey.create(Registries.STRUCTURE, CommonClass.customLocation("broker_tower"))));
    }

    private static void sendToDimension(Entity entity, ServerLevel level, Vec3 targetVec) {
        level.getChunk(new BlockPos(Mth.floor(targetVec.x), Mth.floor(targetVec.y), Mth.floor(targetVec.z)));
        float f = entity.getYRot();
        var transition = new DimensionTransition(level, targetVec, entity.getDeltaMovement(), f, entity.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET));
        entity.changeDimension(transition);
        entity.portalCooldown = 20;
    }
}
