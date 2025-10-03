package com.ombremoon.spellbound.common.content.entity;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBMemoryTypes;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    @Override
    public void tick() {
        super.tick();
        if (this.getOwner() instanceof LivingEntity livingEntity && this.tickCount < 5 && (this.handler == null || this.skills == null)) {
            this.handler = SpellUtil.getSpellHandler(livingEntity);
            this.skills = SpellUtil.getSkills(livingEntity);
        }

        if (!this.level().isClientSide) {
            if ((this.wasSummoned() && !this.hasOwner()) || (this.isInitialized() && (this.spell == null ||  this.spell.isInactive)))
                discard();
        }
    }

    @Override
    public boolean isInitialized() {
        return this.handler != null;
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
