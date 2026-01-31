package com.ombremoon.spellbound.common.magic.familiars;

import com.ombremoon.spellbound.common.init.SBFamiliars;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.skills.FamiliarAffinity;
import com.ombremoon.spellbound.common.world.entity.living.familiars.SBFamiliarEntity;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

public class FamiliarHandler implements INBTSerializable<CompoundTag> {
    private static final float LEVEL_ONE_XP = 100;
    private static Map<SpellMastery, Set<FamiliarHolder<?, ?>>> MASTERY_SORTED_FAMILIARS = new HashMap<>();

    private boolean isInitialised;
    private Level level;
    private LivingEntity owner;
    private Map<FamiliarHolder<?, ?>, Integer> familiarRebirths = new HashMap<>();
    private Map<FamiliarHolder<?, ?>, Float> familiarBond = new HashMap<>();
    private FamiliarHolder<?, ?> selectedFamiliar = null;
    private FamiliarContext summonedContext = null;
    private Map<FamiliarAffinity, Integer> skillCooldowns = new HashMap<>();
    private int familiarHpPercent = 1;

    /**
     * Adds a familiar to its mastery group
     * @param familiar The familiar to categorise
     * @param reqMastery Required mastery to unlock
     */
    public static void registerFamiliarMastery(FamiliarHolder<?, ?> familiar, SpellMastery reqMastery) {
        Set<FamiliarHolder<?, ?>> familiars = MASTERY_SORTED_FAMILIARS.getOrDefault(reqMastery, new HashSet<>());
        familiars.add(familiar);
        MASTERY_SORTED_FAMILIARS.put(reqMastery, familiars);
    }

    /**
     * Initialises the handler
     * @param owner The familiar summoner
     */
    public void init(LivingEntity owner) {
        this.level = owner.level();
        this.owner = owner;
        unlockFamiliars(SpellUtil.getSkills(owner).getMaster(SpellPath.SUMMONS));

        this.isInitialised = true;
    }

    /**
     * Checks if the handler has been initialised
     * @return true if initialised, false otherwise
     */
    public boolean isInitialised() {
        return isInitialised;
    }

    /**
     * Gets the currently selected familiar (may not be actively summoned)
     * @return The selected familiar holder
     */
    @Nullable
    public FamiliarHolder<?, ?> getSelectedFamiliar() {
        return selectedFamiliar;
    }

    /**
     * Sets a new familiar as the one currently selected, will redeploy the familiar if one is already active
     * @param holder The familiar to select
     * @return true if the familiar was unlocked and selected, false otherwise
     */
    public boolean selectFamiliar(FamiliarHolder<?, ?> holder) {
        if (!this.familiarBond.containsKey(holder)) return false;

        if (this.summonedContext != null) {
            BlockPos pos = this.summonedContext.getEntity().blockPosition();
            this.discardFamiliar();
            this.familiarHpPercent = 1;
            this.selectedFamiliar = holder;
            this.summonFamiliar(pos);
            return true;
        }

        this.familiarHpPercent = 1;
        this.selectedFamiliar = holder;
        return true;
    }

    /**
     * Summons the selected familiar, Will spawn at the given block pos, if entity extends {@link SBFamiliarEntity} it will spawn riding summoner
     * @param pos The spawn pos
     */
    public void summonFamiliar(BlockPos pos) {
        createContext();
        if (this.summonedContext == null) return;

        LivingEntity entity = summonedContext.getEntity();

        if (entity instanceof SBFamiliarEntity familairEntity) {
            entity.setPos(owner.position()
                    .subtract(familairEntity.getVehicleAttachmentPoint(owner))
                    .add(0, owner.getBbHeight(), 0));
            familairEntity.setIdle(true);
        }
        else entity.setPos(pos.getCenter().add(0, 1, 0));

        summonedContext.getFamiliar().refreshAttributes(summonedContext);
        if (summonedContext.getEntity().getMaxHealth() * familiarHpPercent <= 2) {
            summonedContext = null;
            return;
        }

        summonedContext.getEntity().setHealth(summonedContext.getEntity().getMaxHealth() * familiarHpPercent);

        SpellUtil.setOwner(entity, this.owner);
        level.addFreshEntity(entity);
        summonedContext.getFamiliar().onSpawn(summonedContext, entity instanceof SBFamiliarEntity ? owner.blockPosition() : pos);
    }

    /**
     * Creates familiar context ready for spawning
     */
    private void createContext() {
        if (this.selectedFamiliar == null) return;

        this.summonedContext = new FamiliarContext(
                this.level,
                this.owner,
                this.selectedFamiliar,
                this.selectedFamiliar.getBuilder().create(),
                this.selectedFamiliar.getEntity().create(this.level),
                this,
                SpellUtil.getSpellHandler(this.owner),
                SpellUtil.getSkills(this.owner),
                this.getLevelForFamiliar(this.selectedFamiliar),
                this.getRebirths(this.selectedFamiliar)
        );

    }

    /**
     * Puts an affinity on cooldown
     * @param affinity affinity to put on CD
     */
    public void putSkillOnCooldown(FamiliarAffinity affinity) {
        this.skillCooldowns.put(affinity, owner.tickCount + affinity.getCooldown());
    }

    /**
     * Checks if affinity is on cooldown
     * @param affinity Affinity to check
     * @return false if on cooldown, true otherwise
     */
    public boolean isSkillReady(FamiliarAffinity affinity) {
        return !this.skillCooldowns.containsKey(affinity);
    }

    /**
     * Removes skills from cooldown once time has passed. Also ticks familiar
     */
    public void tick() {
        for (var entry : skillCooldowns.entrySet()) {
            if (entry.getValue() >= owner.tickCount) {
                skillCooldowns.remove(entry.getKey());
                this.summonedContext.getFamiliar().onAffinityOffCooldown(this.summonedContext, entry.getKey());
            }
        }

        if (this.summonedContext != null)
            this.summonedContext.tick();
    }

    /**
     * Gets the currently summoned familiar
     * @return Familiar that is currently in use, null if non active
     */
    public Familiar<?> getActiveFamiliar() {
        return this.summonedContext == null ? null : this.summonedContext.getFamiliar();
    }

    /**
     * Gets the entity of the currently summoned familiar
     * @return The entity of the familiar, null if non active
     */
    public LivingEntity getActiveEntity() {
        return this.summonedContext == null ? null : this.summonedContext.getEntity();
    }

    /**
     * Gets the familiar holder of currently active familiar (if this doesnt match {@link #getSelectedFamiliar()} i will cry)
     * @return The familiar holder for current familiar
     */
    public FamiliarHolder<?, ?> getActivatedFamiliarHolder() {
        return this.summonedContext == null || selectedFamiliar == null ? null : selectedFamiliar;
    }

    /**
     * Checks if there is a current familiar deployed
     * @return true if familiar active, false otherwise
     */
    public boolean hasActiveFamiliar() {
        return this.summonedContext != null;
    }

    /**
     * Removes the currently active familiar
     */
    public void discardFamiliar() {
        if (this.selectedFamiliar == null || this.summonedContext == null) return;

        LivingEntity summon = summonedContext.getEntity();
        summonedContext.getFamiliar().onRemove(summonedContext, summon.blockPosition());
        summon.discard();
        this.summonedContext = null;
    }

    /**
     * Unlocks all familiars available at a given mastery
     * @param mastery The mastery to unlock
     */
    public void unlockFamiliars(SpellMastery mastery) {
        for (int i = 0; i <= mastery.ordinal(); i++) {
            for (FamiliarHolder<?, ?> familiar : MASTERY_SORTED_FAMILIARS.get(mastery)) {
                if (this.familiarBond.get(familiar) != null) continue;
                this.familiarBond.put(familiar, 0F);
                this.familiarRebirths.put(familiar, 0);
            }
        }
    }

    /**
     * Gets all currently unlocked familiars
     * @return set of all unlocked familiar holders
     */
    public Set<FamiliarHolder<?, ?>> getUnlockedFamiliars() {
        return familiarBond.keySet();
    }

    /**
     * Rebirths the given familiar resetting bond to 0
     * @param familiar familiar to rebirth
     * @return false if not ready for rebirth, true otherwise
     */
    public boolean rebirthFamiliar(FamiliarHolder<?, ?> familiar) {
        if (getLevelForFamiliar(familiar) < familiar.getMaxLevel()) return false;

        int rebirths = familiarRebirths.get(familiar) + 1;
        familiarRebirths.put(familiar, rebirths);
        familiarBond.put(familiar, 0F);
        if (this.summonedContext != null) this.summonedContext.rebirth(rebirths);
        return true;
    }

    /**
     * gets current number of rebirths
     * @param familiarHolder familiar to get rebirths of
     * @return number of rebirths
     */
    public int getRebirths(FamiliarHolder<?, ?> familiarHolder) {
        return this.familiarRebirths.get(familiarHolder);
    }

    /**
     * Gets current bond for a familiar
     * @param familiar familiar to get bond for
     * @return bond of familiar
     */
    public int getLevelForFamiliar(FamiliarHolder<?, ?> familiar) {
        return (int) Math.floor(Math.sqrt(this.familiarBond.get(familiar) / LEVEL_ONE_XP));
    }

    /**
     * Returns max xp a familiar can get
     * @param familiar familiar to check max xp for
     * @return max amount of xp
     */
    public float getMaxXPForFamiliar(FamiliarHolder<?, ?> familiar) {
        return LEVEL_ONE_XP * (familiar.getMaxLevel() * familiar.getMaxLevel());
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        ListTag rebirths = new ListTag();
        ListTag bond = new ListTag();

        for (var entry : familiarRebirths.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("familiar", entry.getKey().getIdentifier().toString());
            entryTag.putInt("rebirths", entry.getValue());
            rebirths.add(entryTag);
        }

        for (var entry : familiarBond.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("familiar", entry.getKey().getIdentifier().toString());
            entryTag.putFloat("bond", entry.getValue());
            bond.add(entryTag);
        }

        tag.put("rebirths", rebirths);
        tag.put("bond", bond);
        if (this.selectedFamiliar != null)
            tag.putString("selected", selectedFamiliar.getIdentifier().toString());

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        ListTag rebirthTag = compoundTag.getList("rebirths", 10);
        ListTag bondTag = compoundTag.getList("bond", 10);

        for (int i = 0; i < rebirthTag.size(); i++) {
            CompoundTag tag = rebirthTag.getCompound(i);
            this.familiarRebirths.put(
                    SBFamiliars.REGISTRY.get(ResourceLocation.parse(tag.getString("familiar"))),
                    tag.getInt("rebirths"));
        }

        for (int i = 0; i < bondTag.size(); i++) {
            CompoundTag tag = bondTag.getCompound(i);
            this.familiarBond.put(
                    SBFamiliars.REGISTRY.get(ResourceLocation.parse(tag.getString("familiar"))),
                    tag.getFloat("bond"));
        }

        String familiarId = compoundTag.getString("selected");
        this.selectedFamiliar = familiarId.isEmpty() ? null : SBFamiliars.REGISTRY.get(ResourceLocation.parse(familiarId));
    }

    public void awardBond(FamiliarHolder<?, ?> familiar, float xp) {
        float currentBond = this.familiarBond.get(familiar);
        float maxXp = getMaxXPForFamiliar(familiar);

        float newBond = currentBond + xp;
        if (currentBond == maxXp) return;
        if (newBond > maxXp) newBond = maxXp;

        if (this.summonedContext != null) {
            int level = getLevelForFamiliar(familiar);
            this.familiarBond.put(familiar, newBond);
            int newLevel = getLevelForFamiliar(familiar);

            if (newLevel != level)
                this.summonedContext.levelUp(level, newLevel);
        } else
            this.familiarBond.put(familiar, newBond);

    }
}
