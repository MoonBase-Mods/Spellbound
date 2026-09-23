package com.ombremoon.spellbound.common.magic.skills;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;

import java.util.Iterator;
import java.util.Map;

public class SkillCooldowns {
    private final Map<SkillProvider, Instance> cooldowns = new Object2ObjectOpenHashMap<>();
    private int tickCount;

    public boolean isOnCooldown(SkillProvider skill) {
        return this.getCooldownPercent(skill, 0.0F) > 0.0F;
    }

    public float getCooldownPercent(SkillProvider skill, float partialTicks) {
        Instance instance = this.cooldowns.get(skill);
        if (instance != null) {
            float f = instance.endTime - instance.startTime;
            float f1 = instance.endTime - (this.tickCount + partialTicks);
            return Mth.clamp(f1 / f, 0.0F, 1.0F);
        } else {
            return 0.0F;
        }
    }

    public void tick() {
        this.tickCount++;
        if (!this.cooldowns.isEmpty()) {
            this.cooldowns.entrySet().removeIf(entry -> entry.getValue().endTime <= this.tickCount);
        }
    }

    public void addCooldown(SkillProvider skill, int ticks) {
        if (!this.isOnCooldown(skill))
            this.cooldowns.put(skill, new Instance(this.tickCount, this.tickCount + ticks));
    }

    record Instance(int startTime, int endTime) {}
}
