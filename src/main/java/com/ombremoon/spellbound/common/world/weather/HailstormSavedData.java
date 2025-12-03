package com.ombremoon.spellbound.common.world.weather;

import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.Loggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HailstormSavedData extends SavedData implements HailstormData, Loggable {
    private static ClientHailstormData clientData;
    private boolean isHailing;
    private int hailTime;
    private float oHailLevel;
    private float hailLevel;

    private HailstormSavedData() {

    }

    public static HailstormData get(LevelAccessor level) {
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = level.getServer().getLevel(Level.OVERWORLD);
            DimensionDataStorage storage = serverLevel.getDataStorage();
            return storage.computeIfAbsent(new Factory<>(HailstormSavedData::create, HailstormSavedData::load), "_hailstorm");
        } else {
            return getOrCreateClientData();
        }
    }

    private static HailstormSavedData create() {
        return new HailstormSavedData();
    }

    private static ClientHailstormData getOrCreateClientData() {
        if (clientData == null)
            clientData = new ClientHailstormData();

        return clientData;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        nbt.putBoolean("Hailing", this.isHailing);
        nbt.putInt("HailTime", this.hailTime);
        nbt.putFloat("InitialHailLevel", this.oHailLevel);
        nbt.putFloat("HailLevel", this.hailLevel);
        log("Successfully saved hailstorm data.");
        return nbt;
    }

    public static HailstormSavedData load(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        HailstormSavedData data = create();
        if (nbt.contains("Hailing", 99)) {
            data.isHailing = nbt.getBoolean("Hailing");
        }
        if (nbt.contains("HailTime", 99)) {
            data.hailTime = nbt.getInt("HailTime");
        }
        if (nbt.contains("InitialHailLevel", 99)) {
            data.oHailLevel = nbt.getFloat("InitialHailLevel");
        }
        if (nbt.contains("HailLevel", 99)) {
            data.hailLevel = nbt.getFloat("HailLevel");
        }
        Constants.LOG.info("Hailstorm data loaded successfully.");
        return data;
    }

    public int getHailTime() {
        return this.hailTime;
    }

    public void setHailTime(int hailTIme) {
        this.hailTime = hailTIme;
        this.setDirty();
    }

    public void toggleHailing(ServerLevel level, int hailTime) {
        ServerLevelData data = (ServerLevelData) level.getLevelData();
        this.setHailTime(hailTime);
        this.setHailing(true);
        data.setClearWeatherTime(0);
        data.setRainTime(0);
        data.setThunderTime(0);
        data.setRaining(false);
        data.setThundering(false);
    }

    /*public boolean chunkHasCyclone(ServerLevel level, BlockPos pos) {
        List<Cyclone> cycloneList = this.getCyclonesInChunk(level, pos);
        return !cycloneList.isEmpty();
    }*/

    public BlockPos findLightningTargetAround(ServerLevel level, BlockPos pos) {
        BlockPos blockpos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);
        AABB aabb = AABB.encapsulatingFullBlocks(blockpos, new BlockPos(blockpos.atY(level.getMaxBuildHeight()))).inflate(3);
        List<LivingEntity> cycloneList = level.getEntitiesOfClass(LivingEntity.class, aabb, cyclone -> cyclone != null && cyclone.isAlive() && level.canSeeSky(cyclone.blockPosition()));
        if (!cycloneList.isEmpty()) {
            return cycloneList.get(level.random.nextInt(cycloneList.size())).blockPosition();
        } else {
            if (blockpos.getY() == level.getMinBuildHeight() - 1) {
                blockpos = blockpos.above(2);
            }

            return blockpos;
        }
    }

    /*private List<Cyclone> getCyclonesInChunk(ServerLevel level, BlockPos pos) {
        BlockPos blockpos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);
        AABB aabb = AABB.encapsulatingFullBlocks(blockpos, new BlockPos(blockpos.atY(level.getMaxBuildHeight()))).inflate(10.0);
        return level.getEntitiesOfClass(Cyclone.class, aabb, cyclone -> cyclone != null && level.canSeeSky(cyclone.blockPosition()));
    }*/

/*    public void tickHailLevel(ServerLevel level) {
        this.oHailLevel = this.hailLevel;
        if (this.isHailing()) {
            this.hailLevel += 0.01F;
        } else {
            this.hailLevel -= 0.01F;
        }

        this.hailLevel = Mth.clamp(this.hailLevel, 0.0F, 1.0F);

        if (this.oHailLevel != this.hailLevel)
            PayloadHandler.changeHailLevel(level, this.hailLevel);


        this.setDirty();
    }*/

    public boolean isHailingAt(Level level, BlockPos pos) {
        if (!this.isHailing()) {
            return false;
        } else if (!level.canSeeSky(pos)) {
            return false;
        } else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        } else {
            Biome biome = level.getBiome(pos).value();
            return biome.getPrecipitationAt(pos) == Biome.Precipitation.RAIN;
        }
    }

    @Override
    public boolean isHailing() {
        return this.isHailing;
    }

    @Override
    public void setHailing(boolean hailing) {
        this.isHailing = hailing;
        this.setDirty();
    }

    @Override
    public float getHailLevel(float delta) {
        return Mth.lerp(delta, this.oHailLevel, this.hailLevel);
    }

    @Override
    public void setHailLevel(float strength) {
        float f = Mth.clamp(strength, 0.0F, 1.0F);
        this.oHailLevel = f;
        this.hailLevel = f;
        this.setDirty();
    }

    @Override
    public void prepareHail() {
        if (this.isHailing)
            this.hailLevel = 1.0F;

        this.setDirty();
    }
}
