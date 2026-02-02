package com.ombremoon.spellbound.common.magic.familiars;

import com.google.common.collect.Multimap;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;

public class FamiliarContext {
    private int tickCount;
    private LivingEntity entity;
    private FamiliarHolder<?, ?> familiarHolder;
    private Familiar<?> familiar;
    private LivingEntity owner;
    private Level level;
    private int bond;
    private int rebirths;
    private FamiliarHandler handler;
    private SpellHandler spellHandler;
    private SkillHolder skillHolder;

    public FamiliarContext(Level level, LivingEntity owner, FamiliarHolder<?, ?> familiarHolder, Familiar<?> familiar, LivingEntity entity, FamiliarHandler familiarHandler, SpellHandler spellHandler, SkillHolder skillHolder, int bond, int rebirths) {
        this.tickCount = 0;
        this.entity = entity;
        this.familiarHolder = familiarHolder;
        this.familiar = familiar;
        this.owner = owner;
        this.level = level;
        this.handler = familiarHandler;
        this.spellHandler = spellHandler;
        this.skillHolder = skillHolder;
        this.bond = bond;
        this.rebirths = rebirths;
    }

    protected Familiar<?> getFamiliar() {
        return familiar;
    }

    protected void tick() {
        this.tickCount++;
        if (this.familiar.shouldTick(this, tickCount)) this.familiar.tick(this);
    }

    protected void levelUp(int oldLevel, int newLevel) {
        this.bond = newLevel;
        this.familiar.onBondUp(this, oldLevel, newLevel);
    }

    protected void rebirth(int rebirths) {
        this.rebirths = rebirths;
        this.familiar.onRebirth(this, rebirths);
    }

    public int getTickCount() {
        return tickCount;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public FamiliarHolder<?, ?> getHolder() {
        return familiarHolder;
    }

    public Level getLevel() {
        return level;
    }

    public FamiliarHandler getHandler() {
        return handler;
    }

    public SpellHandler getSpellHandler() {
        return spellHandler;
    }

    public SkillHolder getSkillHolder() {
        return skillHolder;
    }

    public int getRebirths() {
        return rebirths;
    }

    public int getBond() {
        return bond;
    }
}
