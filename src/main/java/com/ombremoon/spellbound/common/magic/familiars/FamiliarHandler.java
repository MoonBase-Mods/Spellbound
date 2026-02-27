package com.ombremoon.spellbound.common.magic.familiars;

import com.ombremoon.spellbound.common.init.SBFamiliars;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.SpellMastery;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.skills.FamiliarAffinity;
import com.ombremoon.spellbound.common.world.entity.living.familiars.SBFamiliarEntity;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

public class FamiliarHandler implements INBTSerializable<CompoundTag> {
    private static final float LEVEL_ONE_XP = 100;
    private static Map<SpellMastery, List<FamiliarHolder<?, ?>>> MASTERY_SORTED_FAMILIARS = new HashMap<>();

    public static List<FamiliarHolder<?, ?>> getMasterySortedFamiliars(SpellMastery mastery) {
        return MASTERY_SORTED_FAMILIARS.getOrDefault(mastery, new ArrayList<>());
    }

    private boolean isInitialised;
    private SpellHandler spellHandler;
    private Level level;
    private LivingEntity owner;
    private Set<FamiliarHolder<?, ?>> ownedFamiliars = new HashSet<>();
    private Map<FamiliarHolder<?, ?>, Integer> familiarRebirths = new HashMap<>();
    private Map<FamiliarHolder<?, ?>, Float> familiarBond = new HashMap<>();
    private FamiliarHolder<?, ?> selectedFamiliar = null;
    private LivingEntity summonedEntity = null;
    private Familiar<?> summonedFamiliar = null;
    private Map<FamiliarAffinity, Integer> skillCooldowns = new HashMap<>();
    private int familiarHpPercent = 1;

    public LivingEntity getOwner() {
        return this.owner;
    }

    public SpellHandler getSpellHandler() {
        return spellHandler;
    }

    public Level getLevel() {
        return level;
    }

    /**
     * Adds a familiar to its mastery group
     * @param familiar The familiar to categorise
     * @param reqMastery Required mastery to unlock
     */
    public static void registerFamiliarMastery(FamiliarHolder<?, ?> familiar, SpellMastery reqMastery) {
        List<FamiliarHolder<?, ?>> familiars = MASTERY_SORTED_FAMILIARS.getOrDefault(reqMastery, new ArrayList<>());
        familiars.add(familiar);
        MASTERY_SORTED_FAMILIARS.put(reqMastery, familiars);
    }

    /**
     * Initialises the handler
     * @param owner The familiar summoner
     */
    public void init(LivingEntity owner, SpellHandler spellHandler) {
        this.level = owner.level();
        this.spellHandler = spellHandler;
        this.owner = owner;

        var skills = spellHandler.getSkillHolder();
        for (int i = 0; i <= skills.getMastery(SpellPath.SUMMONS).ordinal(); i++) {
            unlockFamiliars(SpellMastery.values()[i]);
        }

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

        if (this.summonedEntity != null) {
            BlockPos pos = this.summonedEntity.blockPosition();
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
        if (level.isClientSide()) return;
        if ((this.summonedEntity != null && this.summonedFamiliar != null) || this.selectedFamiliar == null) return;

        this.summonedEntity = this.selectedFamiliar.getEntity().create(level);
        this.summonedFamiliar = this.selectedFamiliar.getBuilder().create(getLevelForFamiliar(selectedFamiliar), getRebirths(selectedFamiliar));

        if (summonedEntity instanceof SBFamiliarEntity familairEntity) {
            familairEntity.markFamiliar();
            familairEntity.setIdle(true);
            familairEntity.setFamiliar(this.summonedFamiliar);
            summonedEntity.setPos(owner.position()
                    .subtract(familairEntity.getVehicleAttachmentPoint(owner))
                    .add(0, owner.getBbHeight(), 0));
        }
        else summonedEntity.setPos(pos.getCenter().add(0, 1, 0));

        summonedFamiliar.setOwner(this.owner);
        summonedFamiliar.refreshAttributes(this);
        if (summonedEntity.getMaxHealth() * familiarHpPercent <= 2) {
            this.summonedEntity = null;
            this.summonedFamiliar = null;
            return;
        }


        summonedEntity.setHealth(summonedEntity.getMaxHealth() * familiarHpPercent);

        SpellUtil.setOwner(summonedEntity, this.owner);
        summonedFamiliar.onSpawn(this, summonedEntity instanceof SBFamiliarEntity ? owner.blockPosition() : pos);
        level.addFreshEntity(summonedEntity);
        if (this.owner instanceof ServerPlayer serverPlayer)
            PayloadHandler.syncFamiliarHandler(serverPlayer);

    }

    public void loadFamiliar(int id) {
        Entity entity = this.level.getEntity(id);
        if (!(entity instanceof LivingEntity fam)) return;

        this.summonedEntity = fam;
        this.summonedFamiliar = this.selectedFamiliar.getBuilder().create(getLevelForFamiliar(selectedFamiliar), getRebirths(selectedFamiliar));

        if (fam instanceof SBFamiliarEntity familairEntity)
            familairEntity.setFamiliar(this.summonedFamiliar);
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
                this.summonedFamiliar.onAffinityOffCooldown(this, entry.getKey());
            }
        }

        if (this.summonedFamiliar != null)
            this.summonedFamiliar.tick(this, this.summonedEntity.tickCount);
    }

    /**
     * Gets the currently summoned familiar
     * @return Familiar that is currently in use, null if non active
     */
    public Familiar<?> getActiveFamiliar() {
        return this.summonedFamiliar;
    }

    /**
     * Gets the entity of the currently summoned familiar
     * @return The entity of the familiar, null if non active
     */
    public LivingEntity getActiveEntity() {
        return this.summonedEntity;
    }

    /**
     * Checks if there is a current familiar deployed
     * @return true if familiar active, false otherwise
     */
    public boolean hasActiveFamiliar() {
        if (this.getActiveEntity() != null && getActiveEntity().isRemoved()) {
            this.discardFamiliar();
        }
        return this.summonedFamiliar != null;
    }

    /**
     * Removes the currently active familiar
     */
    public void discardFamiliar() {
        if (this.summonedFamiliar == null || this.summonedEntity == null) return;

        this.summonedFamiliar.onRemove(this, this.summonedEntity.blockPosition());
        this.summonedEntity.discard();
        this.summonedFamiliar = null;
        this.summonedEntity = null;
    }

    /**
     * Unlocks all familiars available at a given mastery
     * @param mastery The mastery to unlock
     */
    public void unlockFamiliars(SpellMastery mastery) {
        this.ownedFamiliars.addAll(MASTERY_SORTED_FAMILIARS.get(mastery));
    }

    /**
     * Gets all currently unlocked familiars
     * @return set of all unlocked familiar holders
     */
    public Set<FamiliarHolder<?, ?>> getUnlockedFamiliars() {
        return ownedFamiliars;
    }

    /**
     * Rebirths the given familiar resetting bond to 0
     * @param familiar familiar to rebirth
     * @return false if not ready for rebirth, true otherwise
     */
    public boolean rebirthFamiliar(FamiliarHolder<?, ?> familiar) {
        if (getLevelForFamiliar(familiar) < familiar.getMaxLevel()) return false;

        int rebirths = familiarRebirths.getOrDefault(familiar, 0) + 1;
        familiarRebirths.put(familiar, rebirths);
        familiarBond.put(familiar, 0F);
        if (this.summonedFamiliar != null) {
            this.summonedFamiliar.rebirths = rebirths;
            this.summonedFamiliar.onRebirth(this, rebirths);
        }
        return true;
    }

    /**
     * gets current number of rebirths
     * @param familiarHolder familiar to get rebirths of
     * @return number of rebirths
     */
    public int getRebirths(FamiliarHolder<?, ?> familiarHolder) {
        return this.familiarRebirths.getOrDefault(familiarHolder, 0);
    }

    /**
     * Gets current bond for a familiar
     * @param familiar familiar to get bond for
     * @return bond of familiar
     */
    public int getLevelForFamiliar(FamiliarHolder<?, ?> familiar) {
        return (int) Math.floor(Math.sqrt(this.familiarBond.getOrDefault(familiar, 0f) / LEVEL_ONE_XP));
    }

    public float progressToNextLevel(FamiliarHolder<?, ?> familiar) {
        int level = getLevelForFamiliar(familiar);
        if (level == familiar.getMaxLevel()) return 0.08f;

        float currentLevelXP = LEVEL_ONE_XP * (level * level);
        float nextLevelXP = LEVEL_ONE_XP * ((level+1) * (level+1));
        float xpBetween = nextLevelXP - currentLevelXP;

        return (this.familiarBond.getOrDefault(familiar, 0f) - currentLevelXP) / xpBetween;
    }

    /**
     * Returns max xp a familiar can get
     * @param familiar familiar to check max xp for
     * @return max amount of xp
     */
    public float getMaxXPForFamiliar(FamiliarHolder<?, ?> familiar) {
        return LEVEL_ONE_XP * (familiar.getMaxLevel() * familiar.getMaxLevel());
    }

    public void awardBond(FamiliarHolder<?, ?> familiar, float xp) {
        float currentBond = this.familiarBond.getOrDefault(familiar, 0f);
        float maxXp = getMaxXPForFamiliar(familiar);

        float newBond = currentBond + xp;
        if (currentBond == maxXp) return;
        if (newBond > maxXp) newBond = maxXp;

        if (this.summonedFamiliar != null) {
            int level = getLevelForFamiliar(familiar);
            this.familiarBond.put(familiar, newBond);
            int newLevel = getLevelForFamiliar(familiar);

            if (newLevel != level) {
                this.summonedFamiliar.bond = newLevel;
                this.summonedFamiliar.onBondUp(this, level, newLevel);
            }
        } else
            this.familiarBond.put(familiar, newBond);

    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        ListTag familiars = new ListTag();
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

        for (var familiar : ownedFamiliars) {
            var entryTag = new CompoundTag();
            entryTag.putString("familiar", familiar.getIdentifier().toString());
            familiars.add(entryTag);
        }

        tag.put("familiars", familiars);
        tag.put("rebirths", rebirths);
        tag.put("bond", bond);
        if (summonedEntity != null)
            tag.putInt("entity", summonedEntity.getId());
        if (this.selectedFamiliar != null)
            tag.putString("selected", selectedFamiliar.getIdentifier().toString());

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        ListTag rebirthTag = compoundTag.getList("rebirths", 10);
        ListTag bondTag = compoundTag.getList("bond", 10);
        ListTag familiars = compoundTag.getList("familiars", 10);

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

        for (int i =0; i < familiars.size(); i++) {
            this.ownedFamiliars.add(
                    SBFamiliars.REGISTRY.get(ResourceLocation.parse(familiars.getCompound(i).getString("familiar")))
            );
        }

        String familiarId = compoundTag.getString("selected");
        this.selectedFamiliar = familiarId.isEmpty() ? null : SBFamiliars.REGISTRY.get(ResourceLocation.parse(familiarId));

        int entityId = compoundTag.getInt("entity");
        if (entityId != 0) loadFamiliar(entityId);
    }
}
