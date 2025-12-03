package com.ombremoon.spellbound.common.world.entity;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBMemoryTypes;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public abstract class SmartSpellEntity<T extends AbstractSpell> extends SBLivingEntity implements ISpellEntity<T> {
    private static final EntityDataAccessor<String> SPELL_TYPE = SynchedEntityData.defineId(SmartSpellEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> SPELL_ID = SynchedEntityData.defineId(SmartSpellEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected T spell;
    protected SpellHandler handler;
    protected SkillHolder skills;
    private boolean isSpellCast;

    protected SmartSpellEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPELL_TYPE, "");
        builder.define(SPELL_ID, -1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isSpellCast", this.isSpellCast);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.isSpellCast = compound.getBoolean("isSpellCast");
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    protected Predicate<LivingEntity> summonAttackPredicate() {
        if (this.getBrain() == null) return livingEntity -> false; //This is needed, ignore intellij
        Entity target = BrainUtils.getMemory(this, MemoryModuleType.HURT_BY_ENTITY);
        return livingEntity -> !isOwner(livingEntity) && ((target != null &&  target.is(livingEntity)) || isOwnersTarget(livingEntity));
    }

    protected boolean wasSummoned() {
        return BrainUtils.hasMemory(this, SBMemoryTypes.SUMMON_OWNER.get());
    }

    //Each level above novice, add 20% phy resistance
    //Novice - mid tier
    //Apprentice - Needs netherite
    //Adept - Magic puzzle mechanic
    //Expert - Requires spell damage
    //Master - Soulslike
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if ((this.wasSummoned() && !this.hasOwner()) || (this.isSpellCast() && (this.spell == null ||  this.spell.isInactive)))
                discard();
        }
    }

    public boolean isAttacking() {
        Brain<?> brain = this.getBrain();
        return BrainUtils.hasMemory(brain, MemoryModuleType.ATTACK_COOLING_DOWN);
    }

    protected void startAttack(int cooldownTicks) {
        BrainUtils.setForgettableMemory(this, MemoryModuleType.ATTACK_COOLING_DOWN, true, cooldownTicks);
    }

    public boolean hurtTarget(LivingEntity target, float hurtAmount) {
        boolean flag;
        if (this.spell != null) {
            flag = this.spell.hurt(this, target, hurtAmount);
        } else {
            if (target.isAlliedTo(this))
                return false;

            DamageSource source = this.damageSources().mobAttack(this);
            flag = target.hurt(source, hurtAmount);
        }

        return this.checkHurt(target, flag);
    }

    public boolean hurtTarget(LivingEntity target, DamageSource source, float hurtAmount) {
        boolean flag;
        if (this.spell != null) {
            flag = this.spell.hurt(this, target, source, hurtAmount);
        } else {
            if (target.isAlliedTo(this))
                return false;

            flag = target.hurt(source, hurtAmount);
        }

        return this.checkHurt(target, flag);
    }

    private boolean checkHurt(LivingEntity target, boolean hurt) {
        if (hurt) {
            this.setLastHurtMob(target);
            return true;
        }

        return false;
    }

    @Override
    protected float getDamageAfterArmorAbsorb(DamageSource damageSource, float damageAmount) {
        if (this.isBoss() && !AbstractSpell.isSpellDamage(damageSource)) {
            int i = (this.getBossLevel().ordinal() + 1) * 5;
            int j = 25 - i;
            float f = damageAmount * (float)j;
            damageAmount = Math.max(f / 25.0F, 0.0F);
        }
        return super.getDamageAfterArmorAbsorb(damageSource, damageAmount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(Tags.DamageTypes.IS_PHYSICAL) && this.getBossLevel().ordinal() > 2 || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isSpellCast() {
        return this.isSpellCast;
    }

    @Override
    public void onAddedToLevel() {
        if (this.getOwner() instanceof Player player /*or instanceof SpellCaster*/) {
            this.handler = SpellUtil.getSpellHandler(player);
            this.skills = SpellUtil.getSkills(player);
        }
        super.onAddedToLevel();
    }

    public T getSpell() {
        if (this.spell == null) {
            SpellType<T> spellType = this.getSpellType();
            if (this.handler != null && spellType != null)
                this.spell = this.handler.getSpell(spellType, this.getSpellId());
        }

        return this.spell;
    }

    public void setSpell(@NotNull AbstractSpell spell) {
        this.spell = (T) spell;
        this.setSpellType(spell.spellType());
        this.setSpellId(spell.getId());
        this.isSpellCast = true;
    }

    public SpellType<T> getSpellType(){
        return (SpellType<T>) SBSpells.REGISTRY.get(ResourceLocation.tryParse(this.entityData.get(SPELL_TYPE)));
    }

    public void setSpellType(SpellType<?> spellType) {
        this.entityData.set(SPELL_TYPE, spellType.location().toString());
    }

    public int getSpellId(){
        return this.entityData.get(SPELL_ID);
    }

    public void setSpellId(int id) {
        this.entityData.set(SPELL_ID, id);
    }

    protected boolean isOwner(LivingEntity entity) {
        Entity owner = this.getOwner();
        return owner != null && owner.is(entity);
    }

    public boolean hasOwner() {
        Entity owner = this.getOwner();
        return owner != null && owner.isAlive();
    }

    protected boolean isOwnersTarget(LivingEntity entity) {
        if (!this.hasData(SBData.TARGET_ID)) return false;
        return entity.getId() == this.getData(SBData.TARGET_ID);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new SmoothGroundNavigation(this, this.level());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public EntityType<?> entityType() {
        return this.getType();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
