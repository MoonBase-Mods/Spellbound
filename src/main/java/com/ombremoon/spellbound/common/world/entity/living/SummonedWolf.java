package com.ombremoon.spellbound.common.world.entity.living;

import com.ombremoon.spellbound.client.photon.EffectCache;
import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBSkills;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.world.entity.ISpellEntity;
import com.ombremoon.spellbound.common.world.entity.ai.goal.FollowSummonerGoal;
import com.ombremoon.spellbound.common.world.entity.ai.goal.HurtSummonerGoal;
import com.ombremoon.spellbound.common.world.entity.ai.goal.SummonerAttackGoal;
import com.ombremoon.spellbound.common.world.spell.summon.SummonWolfPackSpell;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

public abstract class SummonedWolf extends Wolf implements ISpellEntity<SummonWolfPackSpell> {
    private static final EntityDataAccessor<Integer> SPELL_ID = SynchedEntityData.defineId(SummonedWolf.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> PACK_LEADER = SynchedEntityData.defineId(SummonedWolf.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PACK_LEADER_ID = SynchedEntityData.defineId(SummonedWolf.class, EntityDataSerializers.INT);
    private final EffectCache cache = new EffectCache();
    private SummonWolfPackSpell spell;
    protected SpellHandler handler;
    protected SkillHolder skills;
    private boolean isSpellCast;

    public SummonedWolf(EntityType<? extends Wolf> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPELL_ID, -1);
        builder.define(PACK_LEADER, false);
        builder.define(PACK_LEADER_ID, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TamableAnimal.TamableAnimalPanicGoal(1.5, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(1, new FollowSummonerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtSummonerGoal(this));
        this.targetSelector.addGoal(2, new SummonerAttackGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    public void tryToTame(Player player) {

    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if (this.isPackLeader()) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttributeBaseValue(Attributes.MAX_HEALTH) * 1.5);
        }

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        if (this.isPackLeader()) {
            return super.getDefaultDimensions(pose).scale(1.05F);
        }

        return super.getDefaultDimensions(pose);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (PACK_LEADER.equals(key)) {
            this.refreshDimensions();
        }
    }

    @Override
    public void setSpell(@NotNull AbstractSpell spell) {
        if (spell instanceof SummonWolfPackSpell wolfPackSpell) {
            this.spell = wolfPackSpell;
            this.setSpellId(spell.getId());
            this.isSpellCast = true;
        }
    }

    @Override
    public SummonWolfPackSpell getOrCreateSpell() {
        if (this.spell == null) {
            SpellType<SummonWolfPackSpell> spellType = SBSpells.SUMMON_WOLF_PACK.get();
            if (this.handler != null) {
                this.spell = this.handler.getSpell(spellType, this.getSpellId());
                if (this.spell == null && this.getSummoner() instanceof LivingEntity caster) {
                    this.spell = spellType.createSpellWithData(caster);
                }
            }
        }

        return this.spell;
    }

    public int getSpellId(){
        return this.entityData.get(SPELL_ID);
    }

    public void setSpellId(int id) {
        this.entityData.set(SPELL_ID, id);
    }

    @Override
    public Entity getSummoner() {
        return SpellUtil.getOwner(this);
    }

    @Override
    public boolean isSpellCast() {
        return this.isSpellCast;
    }

    public boolean isPackLeader() {
        return this.entityData.get(PACK_LEADER);
    }

    public void setPackLeader() {
        this.entityData.set(PACK_LEADER, true);
    }

    public int getPackLeaderId() {
        return this.entityData.get(PACK_LEADER_ID);
    }

    public void setPackLeaderId(int id) {
        this.entityData.set(PACK_LEADER_ID, id);
    }

    @Override
    public EffectCache getFXCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }
}
