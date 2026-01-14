package com.ombremoon.spellbound.common.world.dimension;

import com.ombremoon.spellbound.common.magic.acquisition.bosses.ArenaSavedData;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.BossFight;
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

    public static ServerLevel getOrCreateDimension(MinecraftServer server, ResourceKey<Level> levelKey) {
        return DimensionCreator.get().getOrCreateLevel(server, levelKey, () -> createLevel(server));
    }

    private static LevelStem createLevel(MinecraftServer server) {
        ChunkGenerator newChunkGenerator = new EmptyChunkGenerator(server);
        Holder<DimensionType> typeHolder = server.overworld().dimensionTypeRegistration();
        return new LevelStem(typeHolder, newChunkGenerator);
    }

    public static void spawnInArena(ServerLevel level, Entity entity, BossFight bossFight) {
        BlockPos blockPos = BossFight.ORIGIN;
        level.getChunkAt(blockPos);

        Vec3 spawnOffset = bossFight.getPlayerSpawnOffset();
        int randomOffsetX = level.random.nextInt(3);
        int randomOffsetZ = level.random.nextInt(3);
        BlockPos offsetPos = blockPos.offset((int) spawnOffset.x + randomOffsetX, (int) spawnOffset.y, (int) spawnOffset.z + randomOffsetZ);
        Vec3 targetVec = Vec3.atBottomCenterOf(offsetPos);

        sendToDimension(entity, level, targetVec);
    }

    public static boolean spawnArena(ServerLevel level, ResourceLocation spell) {
        BlockPos origin = BossFight.ORIGIN;
        level.getChunkAt(origin);
        return spawnArena(level, origin, spell);
    }

    private static boolean spawnArena(ServerLevel level, BlockPos origin, ResourceLocation spell) {
        Structure structure = getArena(level, spell).value();
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
            LOGGER.debug("[Arena Spawn] Structure valid | BoundingBox: min({}, {}, {}) max({}, {}, {})",
                    boundingBox.minX(), boundingBox.minY(), boundingBox.minZ(),
                    boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ());

            ArenaSavedData arenaData = ArenaSavedData.get(level);
            arenaData.setArenaBounds(boundingBox);

            ChunkPos chunkPos = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.minX()), SectionPos.blockToSectionCoord(boundingBox.minZ()));
            ChunkPos chunkPos1 = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.maxX()), SectionPos.blockToSectionCoord(boundingBox.maxZ()));

            LOGGER.debug("[Arena Spawn] Placing in chunks from {} to {}", chunkPos, chunkPos1);

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

            LOGGER.debug("[Arena Spawn] Arena generation complete");
            return true;
        }

        LOGGER.warn("[Arena Spawn] Structure generation FAILED - StructureStart invalid | Spell: {}", spell);
        return false;
    }

    private static Holder.Reference<Structure> getArena(ServerLevel level, ResourceLocation spell) {
        ResourceKey<Structure> resourceKey = ResourceKey.create(Registries.STRUCTURE, spell);
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
