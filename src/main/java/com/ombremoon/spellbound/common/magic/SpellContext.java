package com.ombremoon.spellbound.common.magic;

import com.ombremoon.spellbound.common.magic.api.SpellAnimation;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SpellContext {
    private final SpellType<?> spellType;
    private final LivingEntity caster;
    private final Level level;
    private final BlockPos blockPos;
    private final ItemStack rightHandItem;
    private final ItemStack leftHandItem;
    private final SpellHandler spellHandler;
    private final SkillHolder skillHolder;
    private final boolean isRecast;

    public SpellContext(SpellType<?> spellType, LivingEntity caster, boolean isRecast) {
        this(spellType, caster, caster.level(), caster.getOnPos(), isRecast);
    }

    public SpellContext(SpellType<?> spellType, LivingEntity caster, Level level, BlockPos blockPos, boolean isRecast) {
        this(spellType, caster, level, blockPos, caster.getMainHandItem(), caster.getOffhandItem(), isRecast);
    }

    protected SpellContext(SpellType<?> spellType, LivingEntity caster, Level level, BlockPos blockPos, ItemStack rightHandItem, ItemStack leftHandItem, boolean isRecast) {
        this.spellType = spellType;
        this.caster = caster;
        this.level = level;
        this.blockPos = blockPos;
        this.rightHandItem = rightHandItem;
        this.leftHandItem = leftHandItem;
        this.spellHandler = SpellUtil.getSpellHandler(caster);
        this.skillHolder = SpellUtil.getSkills(caster);
        this.isRecast = isRecast;
    }

    public static SpellContext simple(SpellType<?> spellType, LivingEntity caster) {
        return new SpellContext(spellType, caster, false);
    }

    public SpellHandler getSpellHandler() {
        return this.spellHandler;
    }

    public SkillHolder getSkills() {
        return skillHolder;
    }

    public boolean isRecast() {
        return this.isRecast;
    }

    public LivingEntity getCaster() {
        return this.caster;
    }

    public Level getLevel() {
        return this.level;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public ItemStack getMainHandItem() {
        return this.rightHandItem;
    }

    public ItemStack getOffHandItem() {
        return this.leftHandItem;
    }

    public @Nullable Entity getTarget() {
        return this.spellHandler.getTargetEntity();
    }

    public float getRotation() {
        return this.caster.getYRot();
    }

    public boolean hasSkill(Holder<Skill> skill) {
        return this.skillHolder.hasSkill(skill);
    }

    public boolean hasSkillReady(Holder<Skill> skill) {
        return this.skillHolder.hasSkillReady(skill);
    }

    public boolean hasCatalyst(Item catalyst) {
        return this.caster.isHolding(catalyst);
    }

    public ItemStack getCatalyst(Item catalyst) {
        return this.getMainHandItem().is(catalyst) ? this.getMainHandItem() : this.getOffHandItem().is(catalyst) ? this.getOffHandItem() : ItemStack.EMPTY;
    }

    public void useCatalyst(Item catalyst) {
        this.getCatalyst(catalyst).shrink(1);
    }

    public int getActiveSpells() {
        return this.spellHandler.getActiveSpells(this.spellType).size();
    }

    public boolean hasActiveSpells(int amount) {
        return this.getActiveSpells() >= amount;
    }

    public int getSpellLevel() {
        return this.skillHolder.getSpellLevel(this.spellType);
    }

    public int getPathLevel() {
        return getPathLevel(this.spellType.getIdentifiablePath());
    }

    public int getPathLevel(SpellPath path) {
        return this.skillHolder.getPathLevel(path);
    }

    public boolean canCastWithLevel() {
        return this.getActiveSpells() <= this.getSpellLevel();
    }

    public boolean isChoice(Skill skill) {
        return this.skillHolder.getChoice(this.spellType).equals(skill);
    }

    public boolean isChoice(Holder<Skill> skill) {
        return this.isChoice(skill.value());
    }

    public boolean hasSkillBuff(Holder<Skill> skill) {
        return this.spellHandler.hasSkillBuff(skill.value());
    }

    public SpellAnimation quickOrSimpleCast(boolean isInstant) {
        String animation = isInstant ? "instant_cast" : "simple_cast";
        return new SpellAnimation(animation, SpellAnimation.Type.CAST, true);
    }
}
