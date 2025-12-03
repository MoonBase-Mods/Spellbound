package com.ombremoon.spellbound.common.magic.acquisition.transfiguration;

import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RitualSavedData extends SavedData {
    protected static final Logger LOGGER = Constants.LOG;
    public final List<RitualInstance> ACTIVE_RITUALS = new ArrayList<>();

    public static RitualSavedData get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            DimensionDataStorage storage = serverLevel.getDataStorage();
            return storage.computeIfAbsent(new Factory<>(RitualSavedData::create, RitualSavedData::load), "_ritual");
        } else {
            throw new RuntimeException("Cannot retrieve server data from the client.");
        }
    }

    private static RitualSavedData create() {
        return new RitualSavedData();
    }

    public static void addRitual(Level level, RitualInstance instance) {
        if (!level.isClientSide) {
            RitualSavedData data = get(level);
            instance.toggleRitual();
            data.ACTIVE_RITUALS.add(instance);
            data.setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag ritualList = new ListTag();

        for (RitualInstance ritual : ACTIVE_RITUALS) {
            ritualList.add(ritual.save());
        }

        tag.put("Transfiguration Rituals", ritualList);

        LOGGER.info("Successfully saved rituals");
        return tag;
    }

    public static RitualSavedData load(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        RitualSavedData data = new RitualSavedData();
        ListTag ritualList = nbt.getList("Transfiguration Rituals", 10);
        for (int i = 0; i < ritualList.size(); i++) {
            CompoundTag compoundTag = ritualList.getCompound(i);
            RitualInstance instance = RitualInstance.load(compoundTag);

            if (instance != null) {
                data.ACTIVE_RITUALS.add(instance);
            } else {
                LOGGER.error("Invalid ritual found");
            }
        }
        LOGGER.info("Rituals loaded successfully");
        return data;
    }
}
