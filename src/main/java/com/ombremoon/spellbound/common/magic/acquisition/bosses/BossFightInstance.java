package com.ombremoon.spellbound.common.magic.acquisition.bosses;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.init.SBBossFights;
import com.ombremoon.spellbound.main.Constants;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.Map;
import java.util.function.Function;

public abstract class BossFightInstance<T extends BossFight, S extends BossFightInstance<T, S>> {
    public static final Codec<BossFightInstance<?, ?>> CODEC = SBBossFights.REGISTRY
            .byNameCodec()
            .dispatch(BossFightInstance::codec, Function.identity());

    protected final T bossFight;
    protected boolean defeatedBoss;
    private int initializeAttempts;
    private boolean initialized;

    public BossFightInstance(T bossFight) {
        this.bossFight = bossFight;
    }

    public abstract boolean initializeWinCondition(ServerLevel level, T bossFight);

    public abstract boolean winCondition(ServerLevel level, T bossFight);

    public abstract void endFight(ServerLevel level, T bossFight);

    public abstract MapCodec<S> codec();

    public abstract CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries);

    public abstract void load(CompoundTag nbt);

    public boolean isInitialized() {
        return this.initialized;
    }

    private boolean tryStart(ServerLevel level) {
        if (this.initializeWinCondition(level, this.bossFight)) {
            this.initialized = true;
            return true;
        }

        return false;
    }

    public void start(ServerLevel level) {
        if (!this.tryStart(level)) {
            this.initializeAttempts = 20;
        }
    }

    public void handleBossFightLogic(ServerLevel level) {
        this.tickFight(level);
    }

    //If buggy, just use EntityJoinLevel event
    private void tickFight(ServerLevel level) {
        if (!this.initialized && !this.tryStart(level)) {
            this.initializeAttempts--;
            if (this.initializeAttempts <= 0) {
                this.endFight(level, true);
            }
            return;
        }

        this.tickFight(level, bossFight);
        boolean flag = ArenaSavedData.isArenaEmpty(level);
        if (flag || this.winCondition(level, bossFight)) {
            this.endFight(level, flag);
        }
    }

    protected void tickFight(ServerLevel level, T bossFight) {
    }

    private void endFight(ServerLevel level, boolean destroyPortal) {
        this.endFight(level, this.bossFight);

        ArenaSavedData data = ArenaSavedData.get(level);
        data.endFight();

        if (destroyPortal)
            data.destroyPortal(level);
    }

    public T getBossFight() {
        return this.bossFight;
    }
}
