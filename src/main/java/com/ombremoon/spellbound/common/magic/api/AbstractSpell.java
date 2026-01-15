package com.ombremoon.spellbound.common.magic.api;

import com.ombremoon.spellbound.client.CameraEngine;
import com.ombremoon.spellbound.client.KeyBinds;
import com.ombremoon.spellbound.client.particle.EffectCache;
import com.ombremoon.spellbound.client.renderer.layer.GenericSpellLayer;
import com.ombremoon.spellbound.client.renderer.layer.SpellLayerModel;
import com.ombremoon.spellbound.client.renderer.layer.SpellLayerRenderer;
import com.ombremoon.spellbound.client.particle.FXEmitter;
import com.ombremoon.spellbound.common.world.effect.SBEffect;
import com.ombremoon.spellbound.common.world.entity.ISpellEntity;
import com.ombremoon.spellbound.common.events.EventFactory;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.api.buff.*;
import com.ombremoon.spellbound.common.magic.api.buff.events.SpellEvent;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.magic.sync.SpellDataHolder;
import com.ombremoon.spellbound.common.magic.sync.SpellDataKey;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.common.world.item.MageArmorItem;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.Loggable;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.tslat.smartbrainlib.util.RandomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The main class used to create spells. Spells exist on both the client and server and must be handled as such. In general, spells should extend {@link AnimatedSpell} unless you don't want the player to have an animation while casting the spells.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSpell implements GeoAnimatable, SpellDataHolder, FXEmitter, Loggable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final DataTicket<AbstractSpell> DATA_TICKET = new DataTicket<>("abstract_spell", AbstractSpell.class);
    protected static final SpellDataKey<BlockPos> CAST_POS = SyncedSpellData.registerDataKey(AbstractSpell.class, SBDataTypes.BLOCK_POS.get());
    protected static final float SPELL_LEVEL_DAMAGE_MODIFIER = 0.25F;
    protected static final float PATH_LEVEL_DAMAGE_MODIFIER = 0.5F;
    protected static final float PATH_MANA_MODIFIER = 0.3F;
    protected static final float SUB_PATH_MANA_MODIFIER = 0.2F;
    protected static final float MANA_COST_MODIFIER = 0.15F;
    protected static final float HURT_XP_MODIFIER = 0.01F;
    protected static final float HEAL_MODIFIER = 0.25F;
    private final SpellType<?> spellType;
    private final int manaCost;
    private final int duration;
    private final float baseDamage;
    private final float xpModifier;
    private final int castTime;
    private final BiPredicate<SpellContext, AbstractSpell> castPredicate;
    private final CastType castType;
    private final SoundEvent castSound;
    private final boolean fullRecast;
    private final Predicate<SpellContext> skipEndOnRecast;
    private final boolean hasLayer;
    private final Predicate<SpellContext> negativeScaling;
    private final int updateInterval;
    protected final SyncedSpellData spellData;
    private final EffectCache effectCache = new EffectCache();
    private Level level;
    private LivingEntity caster;
    private BlockPos blockPos;
    private String nameId;
    private String descriptionId;
    private SpellContext context;
    private SpellContext castContext;
    private boolean isRecast;
    public int tickCount = 0;
    public boolean isInactive = false;
    public boolean init = false;
    public int castId = 0;

    /**
     * <p>
     * Creates a static instance of a spells builder.
     * </p>
     * See:
     * <ul>
     *     <li>{@code AnimatedSpell.Builder}</li>
     *     <li>{@code ChanneledSpell.Builder}</li>
     *     <li>{@code SummonSpell.Builder}</li>
     * </ul>
     * @return The spells builder
     */
    public static <T extends AbstractSpell> Builder<T> createBuilder() {
        return new Builder<>();
    }

    public AbstractSpell(SpellType<?> spellType, Builder<? extends AbstractSpell> builder) {
        this.spellType = spellType;
        this.manaCost = builder.manaCost;
        this.duration = builder.duration;
        this.baseDamage = builder.baseDamage;
        this.xpModifier = builder.xpModifier;
        this.castTime = builder.castTime;
        this.castPredicate = (BiPredicate<SpellContext, AbstractSpell>) builder.castPredicate;
        this.castType = builder.castType;
        this.castSound = builder.castSound;
        this.fullRecast = builder.fullRecast;
        this.skipEndOnRecast = builder.skipEndOnRecast;
        this.hasLayer = builder.hasLayer;
        this.negativeScaling = builder.negativeScaling;
        this.updateInterval = builder.updateInterval;
        SyncedSpellData.Builder dataBuilder = new SyncedSpellData.Builder(this);
        dataBuilder.define(CAST_POS, BlockPos.ZERO);
        this.defineSpellData(dataBuilder);
        this.spellData = dataBuilder.build();
    }

    /**
     * Returns the spells type of the spells.
     * @return The spells type
     */
    public SpellType<?> spellType() {
        return this.spellType;
    }

    public SpellMastery getSpellMastery() {
        return this.spellType().getMastery();
    }

    public float getManaCost() {
        return getManaCost(this.caster);
    }

    /**
     * Returns the total mana cost to cast the spells.
     * @param caster The living entity casting the spells
     * @return The mana cost
     */
    public float getManaCost(LivingEntity caster) {
        var skills = SpellUtil.getSkills(caster);
        float subPathRedux = this.getPath() != SpellPath.RUIN ? 0.0F : SUB_PATH_MANA_MODIFIER * ((float) skills.getPathLevel(this.getSubPath()) / 100);
        float pathRedux = Math.max(0.3F, 1 - (PATH_MANA_MODIFIER * ((float) skills.getPathLevel(this.getPath()) / 100)) - subPathRedux);
        return (this.manaCost * (1 + MANA_COST_MODIFIER * skills.getSpellLevel(spellType()))) * pathRedux * getModifier(ModifierType.MANA, caster);
    }

    /**
     * Returns the total duration the spells will remain active.
     * @return The spells duration
     */
    public int getDuration() {
        float duration =this.getDuration(this.context) * getModifier(ModifierType.DURATION);
        duration *= this.getTransfigurationArmorBuff(this.context);
        return Mth.floor(duration);
    }

    protected int getDuration(SpellContext context) {
        return this.duration;
    }

    public float getBaseDamage() {
        return this.baseDamage;
    }

    public float getCastChance() {
        return getModifier(ModifierType.CAST_CHANCE);
    }

    /**
     * Returns the sound played when the spells cast is complete.
     * @return The cast sound
     */
    protected SoundEvent getCastSound() {
        return this.castSound;
    }

    /**
     * Returns the type of cast the spells takes. Instant spells will cast instantly after spells cast, charged spells have the capability to be charged after spells cast, and channeled spells must be channeled to keep the spells active.
     * @return The cast type
     */
    public CastType getCastType(SpellContext context) {
        return this.castType;
    }

    public CastType getCastType() {
        SpellContext context = this.isCasting() ? this.castContext : this.context;
        return this.getCastType(context);
    }

    /**
     * Returns the amount of time it takes to cast the spells.
     * @return The cast time
     */
    public int getCastTime(SpellContext context) {
        return this.castTime;
    }

    public int getCastTime() {
        return this.getCastTime(this.castContext);
    }

    public boolean isCasting() {
        return !this.init && this.context == null;
    }

    /**
     * Returns the resource location of the current path's path type.
     * @return The spells type's resource location
     */
    public ResourceLocation location() {
        return SBSpells.REGISTRY.getKey(this.spellType);
    }

    /**
     * Returns the name id for GUI text.
     * @return The name id
     */
    public String getNameId() {
        return this.getOrCreateNameId();
    }

    /**
     * Returns the name id for GUI text, or creates one if it doesn't exist.
     * @return The name id
     */
    protected String getOrCreateNameId() {
        if (this.nameId == null) {
            this.nameId = Util.makeDescriptionId("spells", this.location());
        }
        return this.nameId;
    }

    /**
     * Returns the description id for language data gen.
     * @return The description id
     */
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    /**
     * Gets the description id for language data gen, or creates one if it doesn't exist.
     * @return The description id
     */
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("spells.description", this.location());
        }
        return this.descriptionId;
    }

    /**
     * Returns the texture location of the spells used in the casting overlay.
     * @return The spells texture resource location
     */
    public ResourceLocation getTexture() {
        ResourceLocation name = this.location();
        return CommonClass.customLocation("textures/gui/spells/" + name.getPath() + ".png");
    }

    /**
     * Returns a component of the spells name.
     * @return The spells name
     */
    public MutableComponent getName() {
        return Component.translatable(this.getNameId());
    }

    /**
     * Returns a component of the spells description.
     * @return The spells description
     */
    public MutableComponent getDescription() {
        return Component.translatable(this.getDescriptionId());
    }

    /**
     * Returns the spells type from the spells registry given a resource location.
     * @param resourceLocation The spells type resource location
     * @return The spells type
     */
    public static SpellType<?> getSpellByName(ResourceLocation resourceLocation) {
        return SBSpells.REGISTRY.get(resourceLocation);
    }

    /**
     * Returns the spells pathTexture of the spells.
     * @return The spells pathTexture
     */
    public SpellPath getPath() {
        return this.spellType().getPath();
    }

    /**
     * Returns the sub pathTexture of the spells if present.
     * @return The spells sub pathTexture
     */
    public SpellPath getSubPath() {
        if (this.spellType.getPath() != SpellPath.RUIN || this.spellType.getPath().isSubPath())
            return null;

        return this.spellType().getSubPath();
    }

    /**
     * Returns the {@link SpellContext} for the path
     * @return The casting specific spells context
     */
    public final SpellContext getContext() {
        return this.context;
    }

    /**
     * Returns a temporary {@link SpellContext} for the spells. Specifically used for casting mechanics.
     * @return The casting specific spells context
     */
    public SpellContext getCastContext() {
        return this.castContext;
    }

    /**
     * Sets a temporary {@link SpellContext} for the spells. Specifically used for casting mechanics.
     * @param context The casting specific spells context
     */
    public void setCastContext(SpellContext context) {
        this.castContext = context;
    }

    /**
     * The specific cast id for this spells type within the caster's active spells. Useful for getting a specific instance(s) of a spells.
     * @return The spells's cast id
     */
    public int getId() {
        return this.castId;
    }

    public int level() {
        return this.context.getSkills().getSpellLevel(this.spellType);
    }

    /**
     * Defines the default data values of the synced data keys.
     * @param builder The synced spells data builder
     */
    protected void defineSpellData(SyncedSpellData.Builder builder) {

    }

    /**
     * Returns the synced spells data. Is used to get/set spells data on server that is synced with the client every update interval tick. Default data values must be defined before they can be manipulated.
     * @return The synced spells data
     */
    public SyncedSpellData getSpellData() {
        return this.spellData;
    }

    /**
     * Saves data on recast spells.
     * @param compoundTag The save data tag
     * @return A compound tag with new save data
     */
    public @UnknownNullability CompoundTag saveData(CompoundTag compoundTag) {
        return compoundTag;
    }

    /**
     * Loads recast data from the previously cast spells.
     * @param nbt The saved data tag
     */
    public void loadData(CompoundTag nbt) {

    }

    /**
     * Spell ticking logic. Should not be overridden. Override {@link AbstractSpell#onSpellTick(SpellContext)} for ticking functionality.
     */
    public final void tick() {
        tickCount++;
//        endSpell();
        if (init) {
            this.startSpell();
        } else if (!isInactive) {
            if (this.shouldTickSpellEffect(this.context)) {
                this.onSpellTick(this.context);
            }
            if (!this.level.isClientSide && this.getCastType() == CastType.CHANNEL && !context.getSpellHandler().inCastMode()) {
                this.endSpell();
            }
            if (!this.level.isClientSide && this.getCastType() != CastType.CHANNEL && tickCount >= getDuration()) {
                this.endSpell();
            }
        }

        if (!this.level.isClientSide) {
            if (this.spellData.isDirty() || this.tickCount % this.updateInterval == 0)
                this.sendDirtySpellData();
        } else {
            this.handleFXRemoval();
        }
    }

    /**
     * Triggers the spells to start ticking.
     */
    private void startSpell() {
        this.init = false;
        this.onSpellStart(this.context);
        if (this.isRecast)
            this.onSpellRecast(this.context);
    }

    /**
     * Ends the spells. Can be called to end the spells early.
     */
    public final void endSpell() {
        this.onSpellStop(this.context);
        this.init = false;
        this.isInactive = true;
        this.tickCount = 0;
        this.handleFXRemoval();
        if (!level.isClientSide)
            PayloadHandler.endSpell(this.caster, spellType(), this.castId);
    }

    /**
     * Called every tick while a spells is active.
     * @param context The spells context
     */
    protected void onSpellTick(SpellContext context) {
//        endSpell();
    }

    /**
     * Called when a spells starts.
     * @param context The spells context
     */
    protected abstract void onSpellStart(SpellContext context);

    /**
     * Called when a spells is recast.
     * @param context The spells context
     */
    protected void onSpellRecast(SpellContext context) {

    }

    /**
     * Called when a spells ends.
     * @param context The spells context
     */
    protected abstract void onSpellStop(SpellContext context);

    //TODO: CHECK
    /**
     * Called at the start of casting.
     * @param context The casting specific spells context
     */
    public void onCastStart(SpellContext context) {
        if (!context.getLevel().isClientSide) {
            this.spellData.set(CAST_POS, context.getBlockPos());
        }
    }

    /**
     * Called when a cast condition isn't met or if the cast key is released before the cast duration.
     * @param context The casting specific spells context
     */
    public void onCastReset(SpellContext context) {
        if (context.getLevel().isClientSide) {
            KeyBinds.getSpellCastMapping().setDown(false);
        }
    }

    public void resetCast(SpellHandler handler) {
        this.resetCast(handler, this.castContext);
    }

    public void resetCast(SpellHandler handler, SpellContext context) {
        handler.castTick = 0;
        PayloadHandler.castReset(this.spellType(), this.isRecast);
        this.onCastReset(context);
    }

    public boolean isStationaryCast(SpellContext castContext) {
        return true;
    }

    /**
     * Called when spells data is synced from the server to the client
     * @param newData The list of data values being synced
     */
    @Override
    public void onSpellDataUpdated(List<SyncedSpellData.DataValue<?>> newData) {
    }

    /**
     * Called when spells data is synced from the server to the client
     * @param dataKey The specific spells data key being synced
     */
    @Override
    public void onSpellDataUpdated(SpellDataKey<?> dataKey) {
    }

    /**
     * Sets the amount of ticks the path will continue to tick
     * @param ticks The amount of ticks left for the path
     */
    public void setRemainingTicks(int ticks) {
        this.tickCount = Mth.clamp(this.getDuration() - ticks, 0, this.getDuration());
        if (!this.level.isClientSide) PayloadHandler.setSpellTicks(this.caster, this.spellType, this.castId, this.tickCount);
    }

    public int getRemainingTime() {
        return this.getDuration() - this.tickCount;
    }

    /**
     * Checks if the spells should tick. Can be used to start/stop spells effects in certain conditions
     * @param context The spells context
     * @return Whether the spells should tick
     */
    protected boolean shouldTickSpellEffect(SpellContext context) {
        return true;
    }

    /**
     * Clears all harmful effects from the caster
     */
    public void cleanseCaster() {
        cleanseCaster(0);
    }

    /**
     * Clears a given number of harmful effects from the caster
     */
    public void cleanseCaster(int effects) {
        cleanseCaster(effects, MobEffectCategory.HARMFUL);
    }

    /**
     * Clears a given number of effects from the caster
     * @param effectsToRemove Number of effects to remove, 0 will remove all effect of a given category
     * @param category The type of effects to remove
     */
    public void cleanseCaster(int effectsToRemove, MobEffectCategory category) {
        cleanse(this.caster, effectsToRemove, category);
    }

    /**
     * Clears a given number of effects from the target
     * @param cleanseTarget Target to be cleansed
     * @param effectsToRemove Number of effects to remove, 0 will remove all effect of a given category
     * @param category The type of effects to remove
     */
    public void cleanse(LivingEntity cleanseTarget, int effectsToRemove, MobEffectCategory category) {
        Collection<MobEffectInstance> effects = cleanseTarget.getActiveEffects();
        if (effectsToRemove == 0) {
            effects = List.copyOf(effects);
            for (MobEffectInstance instance : effects) {
                if (instance.getEffect().value().getCategory() == category) {
                    cleanseTarget.removeEffect(instance.getEffect());
                }
            }
        } else {
            List<Holder<MobEffect>> harmfulEffects = new ArrayList<>();
            for (MobEffectInstance instance : effects) {
                if (instance.getEffect().value().getCategory() == category)
                    harmfulEffects.add(instance.getEffect());
            }

            if (harmfulEffects.isEmpty())
                return;

            effectsToRemove = Math.min(effectsToRemove, harmfulEffects.size());
            for (int i = 0; i < effectsToRemove; i++) {
                Holder<MobEffect> removed = harmfulEffects.get(cleanseTarget.getRandom().nextInt(0, harmfulEffects.size()));
                cleanseTarget.removeEffect(removed);
                harmfulEffects.remove(removed);
            }
        }
    }

    /**
     * Hurts the target entity, taking path level, potency, and magic resistance into account. Suitable for modded damage types.
     * @param ownerEntity The damage causing entity
     * @param targetEntity The hurt entity
     * @param damageType The damage type
     * @param hurtAmount The amount of damage the entity takes
     * @return Whether the entity takes damage or not
     */
    private boolean hurt(LivingEntity ownerEntity, Entity attackEntity, LivingEntity targetEntity, ResourceKey<DamageType> damageType, float hurtAmount) {
        if (!SpellUtil.CAN_ATTACK_ENTITY.test(ownerEntity, targetEntity))
            return false;

        float damageAfterResistance = this.getDamageAfterResistances(ownerEntity, targetEntity, damageType, hurtAmount);
        boolean flag = targetEntity.hurt(SpellUtil.damageSource(ownerEntity.level(), damageType, ownerEntity, attackEntity), damageAfterResistance);
        if (flag && !ownerEntity.level().isClientSide) {
            targetEntity.setLastHurtByMob(attackEntity instanceof LivingEntity livingEntity ? livingEntity : ownerEntity);
            ownerEntity.setLastHurtMob(targetEntity);
            this.awardXp(this.calculateHurtXP(damageAfterResistance));
            if (this.hasRuinBuff(this.context))
                damageAfterResistance *= 2.0F;

            this.incrementEffect(targetEntity, damageType, damageAfterResistance);
        }
        return flag;
    }

    public boolean hurt(Entity attackEntity, LivingEntity targetEntity, ResourceKey<DamageType> damageType, float hurtAmount) {
        return hurt(this.caster, attackEntity, targetEntity, damageType, hurtAmount);
    }

    public boolean hurt(Entity attackEntity, LivingEntity targetEntity, DamageSource source, float hurtAmount) {
        return hurt(this.caster, attackEntity, targetEntity, source.typeHolder().getKey(), hurtAmount);
    }

    public boolean hurt(LivingEntity targetEntity, DamageSource source, float hurtAmount) {
        return hurt(this.caster, targetEntity, source.typeHolder().getKey(), hurtAmount);
    }

    /**
     * Hurts the target entity. The damage type is determined by the sub-pathTexture of the path.
     * @param targetEntity The hurt entity
     * @param hurtAmount The amount of damage the entity takes
     * @return Whether the entity takes damage or not
     */
    public boolean hurt(Entity attackEntity, LivingEntity targetEntity, float hurtAmount) {
        SpellPath subPath = this.getSubPath();
        if (subPath == null)
            return hurt(attackEntity, targetEntity, SBDamageTypes.SB_GENERIC, hurtAmount);

        var effect = subPath.getEffect();
        if (effect == null)
            return false;

        return hurt(attackEntity, targetEntity, effect.getDamageType(), hurtAmount);
    }

    public boolean hurt(LivingEntity targetEntity, float hurtAmount) {
        return hurt(this.caster, targetEntity, hurtAmount);
    }

    /**
     * Hurts the target entity by the pre-defined base damage of the path. The damage type is determined by the sub-pathTexture of the path.
     * @param targetEntity The hurt entity
     * @return Whether the entity takes damage or not
     */
    public boolean hurt(Entity attackEntity, LivingEntity targetEntity) {
        return hurt(attackEntity, targetEntity, this.baseDamage);
    }

    public boolean hurt(LivingEntity targetEntity) {
        return hurt(targetEntity, targetEntity);
    }

    /**
     * Calculates damage based on path level, pathTexture level, potency, and judgement (if Divine)
     * @param ownerEntity The damage causing entity
     * @param amount The damage amount
     * @return The damage taking all modifiers into account
     */
    public float getModifiedDamage(LivingEntity ownerEntity, float amount) {
        var skills = SpellUtil.getSkills(ownerEntity);
        var effects = SpellUtil.getSpellEffects(this.caster);
        SpellPath path = this.spellType().getIdentifiablePath();
        float levelDamage = amount * (1.0F + SPELL_LEVEL_DAMAGE_MODIFIER * skills.getSpellLevel(spellType()));
        float judgementFactor = this.getPath() == SpellPath.DIVINE ? effects.getJudgementFactor(this.negativeScaling.test(this.context)) : 1.0F;
        levelDamage *= 1 + PATH_LEVEL_DAMAGE_MODIFIER * ((float) skills.getPathLevel(path) / 100) * (1.0F + judgementFactor);
        return potency(ownerEntity, levelDamage);
    }

    public float getModifiedDamage() {
        return this.getModifiedDamage(this.caster, this.baseDamage);
    }

    /**
     * Calculates the final damage dealt after taking path level, pathTexture level, potency, and magic resistance into account
     * @param ownerEntity The damage causing entity
     * @param targetEntity The hurt entity
     * @param damageAmount The damage amount
     * @return The total damage taken
     */
    private float getDamageAfterResistances(LivingEntity ownerEntity, LivingEntity targetEntity, ResourceKey<DamageType> damageType, float damageAmount) {
        var effects = SpellUtil.getSpellEffects(targetEntity);
        float f = (float) (this.getModifiedDamage(ownerEntity, damageAmount) * (1.0F - effects.getMagicResistance()));
        var effect = effects.getEffectFromDamageType(damageType);
        return effect != null ? f * (1.0F - effect.getEntityResistance(targetEntity)) : f;
    }

    /**
     * Calculates the amount of XP gained from hurting an entity
     * @param amount The damage amount
     * @return The modified xp value
     */
    private float calculateHurtXP(float amount) {
        return amount * xpModifier * (1.0F + HURT_XP_MODIFIER * (this.getManaCost() / this.manaCost));
    }

    /**
     * Increments the effect build up for ruin spells (or other registered damage types)
     * @param targetEntity The hurt entity
     * @param damageType The damage type
     * @param amount The amount of damage dealt
     */
    private void incrementEffect(LivingEntity targetEntity, ResourceKey<DamageType> damageType, float amount) {
        var effects = SpellUtil.getSpellEffects(targetEntity);
        effects.incrementRuinEffects(damageType, amount);
    }

    /**
     * Heals the target, scaling with path level, potency, and judgement.
     * Will not work if caster's judgement does not correspond with the type of heal path
     * @param healEntity The entity being healed
     * @param amount The base heal amount
     */
    protected void heal(LivingEntity healEntity, float amount) {
        var skills = this.context.getSkills();
        var effects = SpellUtil.getSpellEffects(this.caster);
        float judgementFactor = this.getPath() == SpellPath.DIVINE ? effects.getJudgementFactor(this.negativeScaling.test(this.context)) : 1.0F;
        float healAmount = potency(amount * (1 + HEAL_MODIFIER * skills.getSpellLevel(spellType())) * judgementFactor);
        double health = healEntity.getHealth();
        healEntity.heal(healAmount);

        if (!this.level.isClientSide && this.caster instanceof ServerPlayer player)
            SBTriggers.ENTITY_HEALED.get().trigger(player, healEntity, health, healEntity.getHealth());
    }

    public boolean consumeMana(LivingEntity targetEntity, float amount) {
        return SpellUtil.getSpellHandler(targetEntity).consumeMana(amount);
    }

    protected boolean hasRuinBuff(SpellContext context) {
        return this.hasFireStaffBuff(context) || this.hasFrostStaffBuff(context) || this.hasShockStaffBuff(context);
    }

    protected boolean hasFireStaffBuff(SpellContext context) {
        ItemStack staff = context.getCatalyst(SBItems.FIRE_STAFF.get());
        return this.getSubPath() == SpellPath.FIRE && !staff.isEmpty();
    }

    protected boolean hasFrostStaffBuff(SpellContext context) {
        ItemStack staff = context.getCatalyst(SBItems.ICE_STAFF.get());
        return this.getSubPath() == SpellPath.FROST && !staff.isEmpty();
    }

    protected boolean hasShockStaffBuff(SpellContext context) {
        ItemStack staff = context.getCatalyst(SBItems.SHOCK_STAFF.get());
        return this.getSubPath() == SpellPath.SHOCK && !staff.isEmpty();
    }

    protected boolean hasTransfigurationStaffBuff(SpellContext context) {
        ItemStack staff = context.getCatalyst(SBItems.CREATIONIST_STAFF.get());
        return this.getPath() == SpellPath.TRANSFIGURATION && !staff.isEmpty();
    }

    protected boolean hasSummonStaffBuff(SpellContext context) {
        ItemStack staff = context.getCatalyst(SBItems.SHOCK_STAFF.get());
        return this.getPath() == SpellPath.SUMMONS && !staff.isEmpty();
    }

    protected float getTransfigurationArmorBuff(SpellContext context) {
        float i = 1.0F;
        if (this.getPath() != SpellPath.TRANSFIGURATION)
            return i;

        LivingEntity caster = context.getCaster();
        for (ItemStack stack : caster.getArmorSlots()) {
            if (stack.getItem() instanceof MageArmorItem item && item.getMaterial() == SBArmorMaterials.CREATIONIST) {
                i += 0.125F;
            }
        }

        return i;
    }

    /**
     * Adds a {@link SkillBuff} to a living entity for a specified amount of ticks.
     * @param livingEntity The living entity
     * @param skill The skill that activates the buff
     * @param buffCategory Whether it's a good, bad, or neutral buff
     * @param buffObject The type of buff: Mob Effect, Attribute Modifier, Spell Modifier, or Event Listener
     * @param skillObject The actual buff being applied to the entity
     * @param duration The length in ticks the buff persists
     * @param <T> The buff
     */
    public <T> void addSkillBuff(LivingEntity livingEntity, Holder<Skill> skill, BuffCategory buffCategory, SkillBuff.BuffObject<T> buffObject, T skillObject, int duration) {
        if (livingEntity.level().isClientSide || checkForCounterMagic(livingEntity) && buffCategory == BuffCategory.HARMFUL) return;
        SkillBuff<T> skillBuff = new SkillBuff<>(skill.value(), buffCategory, buffObject, skillObject);
        var handler = SpellUtil.getSpellHandler(livingEntity);
        handler.addSkillBuff(skillBuff, this.caster, duration);
    }

    public <T> void addSkillBuff(LivingEntity livingEntity, Holder<Skill> skill, BuffCategory buffCategory, SkillBuff.BuffObject<T> buffObject, T skillObject) {
        this.addSkillBuff(livingEntity, skill, buffCategory, buffObject, skillObject, -1);
    }


    /**
     * Adds an {@link SkillBuff#EVENT Event Skill Buff} to a living entity for a specified amount of ticks.
     * @param livingEntity The living entity
     * @param skill The skill that activates the buff
     * @param category Whether it's a good, bad, or neutral buff
     * @param event The type of event to listen to
     * @param location The id of the listener
     * @param consumer The action that takes places when the event is fired
     * @param duration The length in ticks the buff persists
     * @param <T> The buff
     */
    public <T extends SpellEvent> void addEventBuff(LivingEntity livingEntity, Holder<Skill> skill, BuffCategory category, SpellEventListener.IEvent<T> event, ResourceLocation location, Consumer<T> consumer, int duration) {
        this.addSkillBuff(livingEntity, skill, category, SkillBuff.EVENT, location, duration);
        var listener = SpellUtil.getSpellHandler(livingEntity).getListener();
        if (!listener.hasListener(event, location))
            listener.addListener(event, location, consumer);
    }

    public <T extends SpellEvent> void addEventBuff(LivingEntity livingEntity, Holder<Skill> skill, BuffCategory category, SpellEventListener.IEvent<T> event, ResourceLocation location, Consumer<T> consumer) {
        this.addEventBuff(livingEntity, skill, category, event, location, consumer, -1);
    }

    public void removeSkillBuff(LivingEntity livingEntity, Holder<Skill> skill) {
        this.removeSkillBuff(livingEntity, skill.value());
    }

    public void removeSkillBuff(LivingEntity livingEntity, Skill skill) {
        var handler = SpellUtil.getSpellHandler(livingEntity);
        var buffs = handler.getBuffs().stream().filter(skillBuff -> skillBuff.isSkill(skill)).toList();
        this.removeSkillBuff(livingEntity, skill, buffs.size());
    }

    private void removeSkillBuff(LivingEntity livingEntity, Skill skill, int iterations) {
        if (livingEntity.level().isClientSide)
            return;

        for (int i = 0; i < iterations; i++) {
            var handler = SpellUtil.getSpellHandler(livingEntity);
            var optional = handler.getSkillBuff(skill);
            if (optional.isPresent()) {
                SkillBuff<?> skillBuff = optional.get();
                handler.removeSkillBuff(skillBuff);
            }
        }
    }

    /**
     * Returns the modified potency of the spells
     * @param initialAmount The initial amount of the spells damage/effect
     * @return The modified damage/effect amount
     */
    protected float potency(LivingEntity livingEntity, float initialAmount) {
        return initialAmount * getModifier(ModifierType.POTENCY, livingEntity);
    }

    protected float potency(float initialAmount) {
        return potency(this.caster, initialAmount);
    }

    /**
     * Returns the modifier amount for the caster of the spells specifically.
     * @param modifierType The type of modifier
     * @return The modifier amount
     */
    private float getModifier(ModifierType modifierType) {
        return getModifier(modifierType, this.caster);
    }

    /**
     * Returns the total modifier amount of a given {@link SpellModifier}.
     * @param modifierType The type of modifier
     * @param livingEntity The living entity
     * @return The modifier amount
     */
    public float getModifier(ModifierType modifierType, LivingEntity livingEntity) {
        var skills = SpellUtil.getSkills(livingEntity);
        float f = 1;
        for (var modifier : skills.getModifiers()) {
            if (modifierType.equals(modifier.modifierType()) && modifier.spellPredicate().test(spellType()))
                f *= modifier.modifier();
        }
        for (MobEffectInstance instance : livingEntity.getActiveEffects()) {
            if (!(instance.getEffect().value() instanceof SBEffect sbEffect)) continue;
            for (var modifier : sbEffect.getSpellModifiers()) {
                if (modifierType.equals(modifier.modifierType()) && modifier.spellPredicate().test(spellType()))
                    f *= modifier.modifier();
            }
        }
        return f;
    }

    public <T extends Entity> T summonEntity(SpellContext context, EntityType<T> entityType) {
        return this.summonEntity(context, entityType, entity -> {});
    }

    public <T extends Entity> T summonEntity(SpellContext context, EntityType<T> entityType, Consumer<T> extraData) {
        return this.summonEntity(context, entityType, SpellUtil.getCastRange(this.caster), extraData);
    }

    public <T extends Entity> T summonEntity(SpellContext context, EntityType<T> entityType, double range, Consumer<T> extraData) {
        BlockPos blockPos = this.getSpawnPos(range);
        if (blockPos == null) return null;
        while (!context.getLevel().getBlockState(blockPos.below()).isSolid()) {
            blockPos = blockPos.below();
        }

        Vec3 spawnPos = Vec3.atBottomCenterOf(blockPos);
        return this.summonEntity(context, entityType, spawnPos, extraData);
    }

    public <T extends Entity> T summonEntity(SpellContext context, EntityType<T> entityType, Vec3 spawnPos) {
        return this.summonEntity(context, entityType, spawnPos, entity -> {});
    }

    /**
     * Spawns the desired entity as a summon to a given location
     * @param context The context of the spells
     * @param entityType The entity being created
     * @param spawnPos The spawn position
     * @param extraData Callback to initialize extra data on the entity
     * @return The summoned entity
     * @param <T> The type of entity
     */
    public <T extends Entity> T summonEntity(SpellContext context, EntityType<T> entityType, Vec3 spawnPos, Consumer<T> extraData) {
        ServerLevel level = (ServerLevel) context.getLevel();
        LivingEntity caster = context.getCaster();

        T summon = entityType.create(level);
        if (summon != null) {
            SpellUtil.setOwner(summon, caster);
            SpellUtil.setSpell(summon, this);
            summon.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            summon.setYRot(caster.getYRot());
            extraData.accept(summon);
            if (summon instanceof Mob mob)
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.MOB_SUMMONED, null);

            level.addFreshEntity(summon);
            return summon;
        }
        return null;
    }

    protected <T extends Projectile> T shootProjectile(SpellContext context, EntityType<T> entityType, float velocity, float inaccuracy) {
        return this.shootProjectile(context, entityType, new Vec3(caster.getX(), caster.getEyeY() - 0.1F, caster.getZ()), caster.getXRot(), caster.getYRot(), velocity, inaccuracy, projectile -> {});
    }

    protected <T extends Projectile> T shootProjectile(SpellContext context, EntityType<T> entityType, float velocity, float inaccuracy, Consumer<T> extraData) {
        return this.shootProjectile(context, entityType, new Vec3(caster.getX(), caster.getEyeY() - 0.1F, caster.getZ()), caster.getXRot(), caster.getYRot(), velocity, inaccuracy, extraData);
    }

    protected <T extends Projectile> T shootProjectile(SpellContext context, EntityType<T> entityType, float x, float y, float velocity, float inaccuracy, Consumer<T> extraData) {
        return this.shootProjectile(context, entityType, new Vec3(caster.getX(), caster.getEyeY() - 0.1F, caster.getZ()), x, y, velocity, inaccuracy, extraData);
    }

    protected <T extends Projectile> T shootProjectile(SpellContext context, EntityType<T> entityType, Vec3 spawnPos, float x, float y, float velocity, float inaccuracy, Consumer<T> extraData) {
        return this.summonEntity(context, entityType, spawnPos, projectile -> {
            projectile.shootFromRotation(caster, x, y, 0.0F, velocity, inaccuracy);
            extraData.accept(projectile);
        });
    }

    public void onEntityTick(ISpellEntity<?> spellEntity, SpellContext context) {

    }

    public void onProjectileHitEntity(ISpellEntity<?> spellEntity, SpellContext context, EntityHitResult result) {

    }

    public void onProjectileHitBlock(ISpellEntity<?> spellEntity, SpellContext context, BlockHitResult result) {

    }

    /**
     * Gets the position for the entity to spawn
     * @return the BlockPos of a valid spawn position, null if none found
     */
    protected BlockPos getSpawnPos(double range) {
        if (this.hasTransfigurationStaffBuff(this.context))
            range *= 1.5F;

        BlockHitResult hitResult = this.getTargetBlock(range);
        if (hitResult.getType() == HitResult.Type.MISS) return null;
        if (hitResult.getDirection() == Direction.DOWN) return null;

        return hitResult.getBlockPos().above();
    }

    protected boolean hasValidSpawnPos() {
        float range = SpellUtil.getCastRange(this.caster);
        return hasValidSpawnPos(range);
    }

    /**
     * Checks if the spawn position in a given range is a valid block
     * @param range The spawn range
     * @return Whether the spawn pos is null
     */
    protected boolean hasValidSpawnPos(double range) {
        return this.getSpawnPos(range) != null;
    }

    /**
     * Checks if the caster of the spells has an attribute modifier
     * @param attribute The attribute to check if modified
     * @param modifier ResourceLocation of the AttributeModifier
     * @return true if the modifier is present, false otherwise
     */
    public boolean hasAttributeModifier(LivingEntity livingEntity, Holder<Attribute> attribute, ResourceLocation modifier) {
        return livingEntity.getAttribute(attribute).hasModifier(modifier);
    }

    /**
     * Checks whether the entity is the caster of this path.
     * @param entity The entity
     * @return If the living entity is the caster
     */
    public boolean isCaster(@NotNull Entity entity) {
        return entity.is(this.caster);
    }

    /**
     * Checks if the damage dealt was spells damage.
     * @param damageSource The source of damage
     * @return Whether the damage source has the {@link SBTags.DamageTypes Spell Damage} tag
     */
    public static boolean isSpellDamage(@NotNull DamageSource damageSource) {
        return damageSource.is(SBTags.DamageTypes.SPELL_DAMAGE);
    }

    /**
     * Checks if the damage dealt was physical damage.
     * @param damageSource The source of damage
     * @return Whether the damage source has the {@link SBTags.DamageTypes Physical Damage} tag
     */
    public static boolean isPhysicalDamage(@NotNull DamageSource damageSource) {
        return damageSource.is(SBTags.DamageTypes.PHYSICAL_DAMAGE);
    }

    public boolean checkForCounterMagic(LivingEntity targetEntity) {
        return targetEntity.hasEffect(SBEffects.COUNTER_MAGIC) || targetEntity instanceof Player player && player.isCreative();
    }

    /**
     * Returns a number from 0 to 1 based on the amount of time a spells was charged.
     * @param charge The amount of time the spells has been charged
     * @param totalCharge The amount of time need for the spells to reach maximum charge
     * @return A proportion of the charge to total charge
     */
    public static float getPowerForTime(int charge, float totalCharge) {
        float f = (float) charge / totalCharge;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    /**
     * Adds a cooldown to skills.
     * @param skill The skill to go on cooldown
     * @param ticks The amount of ticks the cooldown will last
     */
    public void addCooldown(Holder<Skill> skill, int ticks) {
        if (this.caster instanceof Player player && player.isCreative())
            return;

        this.context.getSkills().getCooldowns().addCooldown(skill, ticks);

        if (!this.caster.level().isClientSide && this.caster instanceof ServerPlayer player)
            PayloadHandler.updateCooldowns(player, skill, ticks);
    }

    protected void shakeScreen(Player player) {
        shakeScreen(player, 10);
    }

    protected void shakeScreen(Player player, int duration) {
        shakeScreen(player, duration, 1);
    }

    protected void shakeScreen(Player player, int duration, float intensity) {
        shakeScreen(player, duration, intensity, 0.25F);
    }

    protected void shakeScreen(Player player, int duration, float intensity, float maxOffset) {
        shakeScreen(player, duration, intensity, maxOffset, 10);
    }

    /**
     * Shakes a player entity's screen. <b><u>MUST</u></b> be called from the client.
     * @param player The player that receives screen shake
     * @param duration The duration of the screen shake in ticks
     * @param intensity The intensity of the screen shake (max. 10.0F)
     * @param maxOffset The maximum offset from the center the screen will move from while shaking (max. 0.5F)
     * @param freq The speed of which the screen will shake
     */
    protected void shakeScreen(Player player, int duration, float intensity, float maxOffset, int freq) {
        if (player.level().isClientSide())
            CameraEngine.getEngine(player).shakeScreen(player.getRandom().nextInt(), duration, intensity, maxOffset, freq);
    }

    /**
     * Plays an animation for the player. This is called server-side for all players to see the animation
     * @param player The player performing the animation
     * @param animationName The animation pathTexture location
     */
    protected void playAnimation(Player player, String animationName) {
        if (!player.level().isClientSide) {
            var handler = SpellUtil.getSpellHandler(player);
            handler.playAnimation(player, animationName, SpellUtil.getCastSpeed(player));
        }
    }

    protected void stopAnimation(Player player, String animationName) {
        if (!player.level().isClientSide)
            PayloadHandler.handleAnimation(player, animationName, 0.0F, true);
    }

    protected void playMovementAnimation(Player player, String animationName, @Nullable String fallbackAnimation) {
        var caster = SpellUtil.getSpellHandler(player);
        if (caster.isMoving() && !caster.movementDirty) {
            caster.movementDirty = true;
            playAnimation(player, animationName);
        } else if (!caster.isMoving() && caster.movementDirty) {
            caster.movementDirty = false;
            if (fallbackAnimation != null) {
                playAnimation(player, fallbackAnimation);
            } else {
                stopAnimation(player, animationName);
            }
        }
    }

    public boolean shouldRender(SpellContext context) {
        return true;
    }

    /**
     * Returns a {@link BlockHitResult} of where the caster is looking within a certain range.
     * @param range The hit result range
     * @return The block hit result
     */
    public BlockHitResult getTargetBlock(double range) {
        return this.level.clip(setupRayTraceContext(this.caster, range, ClipContext.Fluid.NONE));
    }

    protected @Nullable Entity getTargetEntity() {
        return getTargetEntity(SpellUtil.getCastRange(this.caster));
    }

    protected @Nullable Entity getTargetEntity(double range) {
        return getTargetEntity(this.caster, range);
    }

    public @Nullable Entity getTargetEntity(LivingEntity livingEntity, double range) {
        return getTargetEntity(livingEntity, livingEntity.getEyePosition(1.0F), range);
    }

    /**
     * Returns an entity the camera entity is looking at within a certain distance.
     * @param livingEntity The camera entity
     * @param startPosition The position the camera entity is starting
     * @param range The maximum range of the ray cast
     * @return An entity
     */
    private @Nullable Entity getTargetEntity(LivingEntity livingEntity, Vec3 startPosition, double range) {
        Vec3 lookVec = livingEntity.getViewVector(1.0F);
        Vec3 maxLength = startPosition.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        AABB aabb = livingEntity.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(2.0);

        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(livingEntity, startPosition, maxLength, aabb, EntitySelector.NO_CREATIVE_OR_SPECTATOR, range * range);
        BlockHitResult blockHitResult = livingEntity.level().clip(setupRayTraceContext(livingEntity, range, ClipContext.Fluid.NONE));

        if (hitResult == null)
            return null;

        Entity targetEntity = hitResult.getEntity();
        if (!blockHitResult.getType().equals(BlockHitResult.Type.MISS)) {
            double blockDistance = blockHitResult.getLocation().distanceTo(startPosition);
            if (blockDistance > targetEntity.distanceTo(livingEntity)) {
                return targetEntity;
            }
        } else {
            return targetEntity;
        }
        return null;
    }

    /**
     * Prepares a {@link ClipContext} for ray cast results.
     * @param livingEntity The camera entity
     * @param distance The maximum distance of the ray cast
     * @param fluidContext The type of fluid context to determine if the ray cast should account for fluids
     * @return The clip context of the camera entity
     */
    protected static ClipContext setupRayTraceContext(LivingEntity livingEntity, double distance, ClipContext.Fluid fluidContext) {
        float pitch = livingEntity.getXRot();
        float yaw = livingEntity.getYRot();
        Vec3 fromPos = livingEntity.getEyePosition(1.0F);
        float float_3 = Mth.cos(-yaw * 0.017453292F - 3.1415927F);
        float float_4 = Mth.sin(-yaw * 0.017453292F - 3.1415927F);
        float float_5 = -Mth.cos(-pitch * 0.017453292F);
        float xComponent = float_4 * float_5;
        float yComponent = Mth.sin(-pitch * 0.017453292F);
        float zComponent = float_3 * float_5;
        Vec3 toPos = fromPos.add((double) xComponent * distance, (double) yComponent * distance,
                (double) zComponent * distance);
        return new ClipContext(fromPos, toPos, ClipContext.Block.OUTLINE, fluidContext, livingEntity);
    }

    protected void createSurroundingParticles(Entity entity, ParticleOptions particle, double scale) {
        double d0 = entity.getRandom().nextGaussian() * 0.02;
        double d1 = entity.getRandom().nextGaussian() * 0.02;
        double d2 = entity.getRandom().nextGaussian() * 0.02;
        entity.level().addParticle(
                particle,
                entity.getRandomX(scale),
                entity.getRandomY(),
                entity.getRandomZ(scale),
                d0,
                d1,
                d2);
    }

    protected void createSurroundingServerParticles(Entity entity, ParticleOptions particle, double scale) {
        double d0 = entity.getRandom().nextGaussian() * 0.02;
        double d1 = entity.getRandom().nextGaussian() * 0.02;
        double d2 = entity.getRandom().nextGaussian() * 0.02;
        this.createServerParticles(
                particle,
                entity.getRandomX(scale),
                entity.getRandomY(),
                entity.getRandomZ(scale),
                d0,
                d1,
                d2);
    }

    protected void createServerParticles(ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        if (!this.level.isClientSide)
            PayloadHandler.createParticles(this.caster, particleData, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public void awardXp(float amount) {
        this.context.getSkills().awardSpellXp(spellType(), amount);
    }

    /**
     * Sends manipulated {@link SyncedSpellData} from the server to the client. Will only be called once every update interval tick.
     */
    private void sendDirtySpellData() {
        List<SyncedSpellData.DataValue<?>> list = this.spellData.packDirty();
        if (list != null) {
            LivingEntity caster = !this.isInactive ? this.castContext.getCaster() : this.caster;
            PayloadHandler.setSpellData(caster, spellType(), this.castId, list);
        }
    }

    /**
     * Will allow a spells to be recast without calling the end methods of the previously cast spells.
     * @return Whether the spells should skip the end methods on recast.
     */
    public boolean skipEndOnRecast(SpellContext context) {
        return this.skipEndOnRecast.test(context);
    }

    /**
     * Checks if the {@link GenericSpellLayer} should render a vfx layer when the spells is active.
     * @return Whether the spells has a render layer
     */
    public boolean hasLayer() {
        return this.hasLayer;
    }

    public void castSpell(LivingEntity caster) {
        castSpell(caster, caster.level(), caster.getOnPos(), this.getTargetEntity(caster, SpellUtil.getCastRange(caster)));
    }

    /**
     * Initializes spells data before activation. Will only activate the spells upon a successful cast condition.
     * @param caster The casting living entity
     * @param level The current level
     * @param blockPos The block position the caster is in when the cast timer ends
     * @param target The target entity of the caster
     */
    public void castSpell(LivingEntity caster, Level level, BlockPos blockPos, @Nullable Entity target) {
        if (!level.isClientSide) {
            this.initNoCast(caster, level, blockPos, target);

            var handler = SpellUtil.getSpellHandler(caster);
            boolean incrementId = true;
            CompoundTag nbt = new CompoundTag();
            if (this.isRecast) {
                AbstractSpell prevSpell;

                if (this.fullRecast) {
                    prevSpell = handler.getActiveSpells(spellType()).stream().findFirst().orElse(null);
                } else {
                    prevSpell = this.getPreviouslyCastSpell();
                }

                if (prevSpell != null) {
                    if (this.fullRecast) {
                        if (!prevSpell.skipEndOnRecast(prevSpell.context) && !prevSpell.equals(this))
                            prevSpell.endSpell();

                        this.castId = 1;
                    } else {
                        this.castId = prevSpell.castId + 1;
                    }

                    incrementId = false;
                    nbt = prevSpell.saveData(new CompoundTag());
                    this.loadData(nbt);
                }
            }

            if (incrementId)
                this.castId++;

            if (!SpellUtil.canCastSpell(caster, this) || !(this.castPredicate.test(this.context, this) && RandomUtil.percentChance(getCastChance()))) {
                onCastReset(this.context);
                CompoundTag initTag = this.initTag(this.isRecast, true);
                PayloadHandler.updateSpells(caster, this.spellType, this.castId, initTag, nbt);
                return;
            }

            int spellCap = this.context.getSpellLevel() + 1;
            if (this.hasSummonStaffBuff(this.context))
                spellCap++;

            int spellsActive = this.context.getActiveSpells();
            if (!this.fullRecast && spellsActive >= spellCap) {
                this.endFirstSpell();
            }

            CompoundTag initTag = this.initTag(this.isRecast, false);
            PayloadHandler.updateSpells(caster, this.spellType, this.castId, initTag, nbt);
            awardXp(this.manaCost * this.xpModifier);
            if (caster instanceof Player player) {
                player.awardStat(SBStats.SPELLS_CAST.get());
            }

            handler.setCurrentlyCastingSpell(null);
            activateSpell();
            EventFactory.onSpellCast(caster, this, this.context);

            this.init = true;
        }
    }

    /**
     * Initializes spells data on the client. Will reset if cast condition failed on the server.
     * @param caster The casting living entity
     * @param level The current level
     * @param blockPos The block position the caster is in when the cast timer ends
     * @param isRecast Whether the path was already active upon casting
     * @param castId The specific numeric id for the path instance
     * @param forceReset Whether the path failed casting on the server
     */
    public void clientCastSpell(LivingEntity caster, Level level, BlockPos blockPos, int castId, CompoundTag spellData, boolean isRecast, boolean forceReset) {
        this.level = level;
        this.caster = caster;
        this.blockPos = blockPos;

        this.isRecast = isRecast;
        this.context = new SpellContext(this.spellType(), this.caster, this.level, this.blockPos, this.getTargetEntity(), isRecast);

        if (forceReset) {
            onCastReset(this.context);
            return;
        }

        this.castId = castId;
        this.loadData(spellData);

        this.context.getSpellHandler().setCurrentlyCastingSpell(null);
        activateSpell();
        EventFactory.onSpellCast(caster, this, this.context);

        this.init = true;
    }

    /**
     * Initializes spells data before activation. Will not activate the path.
     * @param caster The casting living entity
     * @param level The current level
     * @param blockPos The block position the caster is in when the cast timer ends
     * @param target The target entity of the caster
     */
    protected void initNoCast(LivingEntity caster, Level level, BlockPos blockPos, Entity target) {
        this.caster = caster;
        this.level = level;
        this.blockPos = blockPos;

        var handler = SpellUtil.getSpellHandler(caster);
        var list = handler.getActiveSpells(spellType());
        if (!list.isEmpty())
            this.isRecast = true;

        this.context = new SpellContext(this.spellType(), this.caster, this.level, this.blockPos, target, this.isRecast);
    }

    protected void initNoCast(LivingEntity caster) {
        this.initNoCast(caster, caster.level(), caster.getOnPos(), this.getTargetEntity(caster, SpellUtil.getCastRange(caster)));
    }

    /**
     * Returns the spells cast prior to this one of the same spells type. Necessary for saving/loading data on recast spells.
     * @return The previously cast spells
     */
    protected AbstractSpell getPreviouslyCastSpell() {
        var handler = this.context.getSpellHandler();
        var spells = handler.getActiveSpells(spellType());
        AbstractSpell spell = this;
        for (AbstractSpell abstractSpell : spells) {
            if (abstractSpell.castId > spell.castId)
                spell = abstractSpell;
        }
        return spell;
    }

    private void endFirstSpell() {
        var handler = this.context.getSpellHandler();
        Optional<AbstractSpell> firstSpell = handler.getActiveSpells(spellType()).stream().min(Comparator.comparingInt(AbstractSpell::getId));
        firstSpell.ifPresent(AbstractSpell::endSpell);
    }

    protected CompoundTag initTag(boolean isRecast, boolean forceReset) {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("isRecast", isRecast);
        nbt.putBoolean("forceReset", forceReset);
        return nbt;
    }

    /**
     * Called when the spells actually activates. That is after mana consumption and cast condition have already been taken into account.
     */
    private void activateSpell() {
        var handler = this.context.getSpellHandler();
        if (this.fullRecast && this.isRecast) {
            handler.recastSpell(this);
        } else {
            handler.activateSpell(this);
        }

        if (!this.level.isClientSide) {
            float mana = getManaCost();
            handler.consumeMana(mana, true);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /**
     * Registers the controllers for the spells vfx layer. Layers must be geckolib models that use the {@link SpellLayerModel} and {@link SpellLayerRenderer}.
     * @param controllers The object to register your controller instances to
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public double getTick(Object object) {
        return this.tickCount;
    }

    @Override
    public EffectCache getFXCache() {
        return this.effectCache;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractSpell spell && spell.spellType() == this.spellType() && spell.castId == this.castId;
    }

    @Override
    public int hashCode() {
        int i = this.spellType.hashCode();
        i = 31 * i + this.castId;
        i = 31 * i + this.blockPos.hashCode();
        i = 31 * i + (this.caster != null ? this.caster.hashCode() : 0);
        return 31 * i + (this.isRecast ? 1 : 0);
    }

    /**
     * Base builder class for all spells builders. Should always be instantiated statically using the helper method from its parent class.
     * @param <T> Type extends AbstractSpell to give access to private fields in a static context
     */
    public static class Builder<T extends AbstractSpell> {
        protected int duration = 10;
        protected int manaCost;
        protected float baseDamage;
        protected int castTime = 10;
        protected float xpModifier = 0.2F;
        protected BiPredicate<SpellContext, T> castPredicate = (context, abstractSpell) -> true;
        protected CastType castType = CastType.INSTANT;
        protected SoundEvent castSound;
        protected boolean fullRecast;
        protected Predicate<SpellContext> skipEndOnRecast = context -> false;
        protected boolean hasLayer;
        protected Predicate<SpellContext> negativeScaling = context -> false;
        protected int updateInterval = 3;

        /**
         * Sets the mana cost of the spells.
         * @param manaCost The mana cost
         * @return The spells builder
         */
        public Builder<T> manaCost(int manaCost) {
            this.manaCost = manaCost;
            return this;
        }

        /**
         * Sets the duration of the spells.
         * @param duration The duration
         * @return The spells builder
         */
        public Builder<T> duration(int duration) {
            this.duration = duration;
            return this;
        }

        /**
         * Sets the base damage of the path (if applicable). Should only be used for Ruin spells.
         * @param baseDamage The duration
         * @return The spells builder
         */
        public Builder<T> baseDamage(float baseDamage) {
            this.baseDamage = baseDamage;
            return this;
        }

        /**
         * Sets the amount of xp scaled from the path's mana cost gained by the caster.
         * @param modifier The scaling amount
         * @return The spells builder
         */
        public Builder<T> xpModifier(float modifier) {
            this.xpModifier = modifier;
            return this;
        }

        /**
         * Sets the amount of time it takes to cast the spells.
         * @param castTime The cast time
         * @return The spells builder
         */
        public Builder<T> castTime(int castTime) {
            this.castTime = castTime;
            return this;
        }

        /**
         * Sets the cast type of the spells.
         * @param castType The cast type
         * @return The spells builder
         */
        public Builder<T> castType(CastType castType) {
            this.castType = castType;
            return this;
        }

        /**
         * Sets the sound played on a successful cast.
         * @param castSound The cast sound
         * @return The spells builder
         */
        public Builder<T> castSound(SoundEvent castSound) {
            this.castSound = castSound;
            return this;
        }

        /**
         * Sets the necessary condition for a spells to successfully be cast.
         * @param castCondition The cast predicate
         * @return The spells builder
         */
        public Builder<T> castCondition(BiPredicate<SpellContext, T> castCondition) {
            this.castPredicate = castCondition;
            return this;
        }

        /**
         * Used to append conditions of spells whose parents already have cast conditions.
         * @param castCondition The appended cast predicate
         * @return The spells builder
         */
        public Builder<T> additionalCondition(BiPredicate<SpellContext, T> castCondition) {
            this.castPredicate = this.castPredicate.and(castCondition);
            return this;
        }

        /**
         * Allows the spells to replace the previously cast spells of the same type in the active spells list. Will save data on recast.
         * @return The spells builder
         */
        public Builder<T> fullRecast() {
            this.fullRecast = true;
            return this;
        }

        /**
         * Allows the spells to skip the endSpell method. Mainly used for recasting.
         * @return The spells builder
         */
        public Builder<T> skipEndOnRecast(Predicate<SpellContext> skipIf) {
            this.skipEndOnRecast = skipIf;
            return this;
        }

        public Builder<T> skipEndOnRecast() {
            this.skipEndOnRecast = context -> true;
            return this;
        }

        /**
         * Must be set to render the layer set by this path.
         * @return The spells builder
         */
        public Builder<T> hasLayer() {
            this.hasLayer = true;
            return this;
        }

        /**
         * Determines if a path should scale with negative judgement. Should only be used for Divine spells.
         * @return The spells builder
         */
        public Builder<T> negativeScaling(Predicate<SpellContext> negativeScaling) {
            this.negativeScaling = negativeScaling;
            return this;
        }

        public Builder<T> negativeScaling() {
            this.negativeScaling = context -> true;
            return this;
        }

        /**
         * Sets the update interval for when spells data should be synced from the server to the client.
         * @param updateInterval The update interval
         * @return The spells builder
         */
        public Builder<T> updateInterval(int updateInterval) {
            this.updateInterval = updateInterval;
            return this;
        }
    }

    public enum CastType {
        INSTANT, CHANNEL
    }
}
