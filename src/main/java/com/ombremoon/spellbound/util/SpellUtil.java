package com.ombremoon.spellbound.util;

import com.ombremoon.spellbound.common.init.SBAttributes;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBEffects;
import com.ombremoon.spellbound.common.init.SBMemoryTypes;
import com.ombremoon.spellbound.common.magic.EffectManager;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.skills.SkillHolder;
import com.ombremoon.spellbound.common.world.SpellDamageSource;
import com.ombremoon.spellbound.common.world.entity.ISpellEntity;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import com.ombremoon.spellbound.networking.PayloadHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;

public class SpellUtil {
    public static final BiPredicate<Entity, LivingEntity> CAN_ATTACK_ENTITY = (entity, livingEntity) -> !livingEntity.isAlliedTo(entity) && !livingEntity.is(entity) && !livingEntity.hasEffect(SBEffects.COUNTER_MAGIC) && !(livingEntity instanceof OwnableEntity ownable && ownable.getOwner() == (entity));
    public static final BiPredicate<Entity, LivingEntity> IS_ALLIED = (entity, livingEntity) -> entity != null && livingEntity.isAlliedTo(entity) || (livingEntity instanceof OwnableEntity ownable && ownable.getOwner() == entity);

    public static SpellDamageSource spellDamageSource(Level level, ResourceKey<DamageType> damageType, AbstractSpell spell, Entity ownerEntity, Entity attackEntity) {
        return new SpellDamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType), spell, attackEntity, ownerEntity);
    }

    public static DamageSource damageSource(Level level, ResourceKey<DamageType> damageType, Entity ownerEntity, Entity attackEntity) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType), attackEntity, ownerEntity);
    }

    public static SpellHandler getSpellHandler(LivingEntity livingEntity) {
        var handler = livingEntity.getData(SBData.SPELL_HANDLER);
        if (!handler.isInitialized())
            handler.initData(livingEntity);
        
        return handler;
    }

    public static SkillHolder getSkills(LivingEntity livingEntity) {
        return livingEntity.getData(SBData.SKILL_HOLDER);
    }

    public static EffectManager getSpellEffects(LivingEntity livingEntity) {
        return livingEntity.getData(SBData.STATUS_EFFECTS);
    }

    public static CompoundTag storeSpell(SpellType<?> spellType) {
        CompoundTag compoundTag = new CompoundTag();
        return storeSpell(compoundTag, spellType);
    }

    private static CompoundTag storeSpell(CompoundTag compoundTag, SpellType<?> spellType) {
        compoundTag.putString("Spell", spellType.location().toString());
        return compoundTag;
    }

    public static ResourceLocation getSpellId(CompoundTag compoundTag, String tagKey) {
        return ResourceLocation.tryParse(compoundTag.getString(tagKey));
    }

    public static boolean canCastSpell(LivingEntity livingEntity, AbstractSpell spell) {
        if (livingEntity instanceof Player player && player.getAbilities().instabuild) return true;
        if (EffectManager.isSilenced(livingEntity)) return false;

        var handler = getSpellHandler(livingEntity);
        return handler.inCastMode() && handler.consumeMana(spell.getManaCost(livingEntity), false);
    }

    public static <T extends SpellType<?>> void cycle(SpellHandler handler, T activeSpell) {
        var spellType = findNextSpellInList(handler.getEquippedSpells(), activeSpell);
        if (spellType != activeSpell) {
            handler.setSelectedSpell(spellType);
        }
    }

    private static <T> T findNextSpellInList(Collection<T> spellList, T currentSpell) {
        Iterator<T> iterator = spellList.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().equals(currentSpell)) {
                if (iterator.hasNext()) {
                    return iterator.next();
                }
                return spellList.iterator().next();
            }
        }

        return spellList.isEmpty() ? currentSpell : spellList.iterator().next();
    }

    /**
     * Sets the spells data attachment of a given entity to the given spells
     * @param entity The summon/SpellEntity to set the spells of
     * @param spell The spells
     */
    public static void setSpell(@NotNull Entity entity, @NotNull AbstractSpell spell) {
        if (entity instanceof ISpellEntity<?> spellEntity) {
            spellEntity.setSpell(spell);
        } else {
            entity.setData(SBData.SPELL_TYPE, spell.location());
            entity.setData(SBData.SPELL_ID, spell.getId());
        }
    }

    public static float getCastRange(LivingEntity caster) {
        return caster.getAttribute(SBAttributes.CAST_RANGE) != null ? (float) caster.getAttributeValue(SBAttributes.CAST_RANGE) : 10.0F;
    }

    public static float getCastSpeed(LivingEntity caster) {
        return caster.getAttribute(SBAttributes.CAST_SPEED) != null ? (float) caster.getAttributeValue(SBAttributes.CAST_SPEED) : 1.0F;
    }

    /**
     * Sets the owner data attachment of a given entity to the given owner
     * @param entity The summon/SpellEntity to set the owner of
     * @param owner The owner of the summon
     */
    public static void setOwner(@NotNull Entity entity, @NotNull LivingEntity owner) {
        if (entity instanceof ISpellEntity<?> spellEntity) {
            spellEntity.setOwner(owner);
        } else {
            entity.setData(SBData.OWNER_ID, owner.getId());
        }

        if (entity instanceof LivingEntity livingEntity)
            BrainUtils.setMemory(livingEntity, SBMemoryTypes.SUMMON_OWNER.get(), owner);
    }

    /**
     * Gets the owner of a given entity, assuming that it is a valid summon
     * @param entity The entity to get the owner of
     * @return The Player that owns the entity, null if unowned
     */
    @Nullable
    public static Entity getOwner(@NotNull Entity entity) {
        if (entity instanceof ISpellEntity<?> spellEntity) {
            return spellEntity.getOwner();
        }

        return entity.level().getEntity(entity.getData(SBData.OWNER_ID));
    }

    /**
     * Gets the target of the current entity
     * @param entity The entity to get the target of
     * @return the current target, null if no current target
     * @apiNote Should only be used for summons
     */
    @Nullable
    public static LivingEntity getTarget(@NotNull LivingEntity entity) {
        if (entity instanceof SBLivingEntity) {
            return BrainUtils.getTargetOfEntity(entity);
        } else if (entity instanceof Mob mob) {
            return mob.getTarget();
        } else {
            Entity target = entity.level().getEntity(entity.getData(SBData.TARGET_ID));
            return target instanceof LivingEntity livingEntity ? livingEntity : null;
        }
    }

    /**
     * Sets the target for a given mob both through vanilla targeting and summon targeting systems.
     * @param summon The summon to set the target of
     * @param target The new target for the summon
     */
    public static void setTarget(@NotNull LivingEntity summon, @NotNull LivingEntity target) {
        if (summon instanceof SBLivingEntity livingEntity) {
            BrainUtils.setTargetOfEntity(livingEntity, target);
        } else if (summon instanceof Mob mob) {
            mob.setTarget(target);
        } else {
            summon.setData(SBData.TARGET_ID, target.getId());
        }
    }

    /**
     * Gets the ids of every currently active summon created by a given caster
     * @param caster The living entity to get the summons of
     * @return Set of the ID of every summon summoned by the given caster
     */
    @NotNull
    public static Set<Integer> getAllSummonIds(@NotNull LivingEntity caster) {
        SpellHandler handler = SpellUtil.getSpellHandler(caster);
        return handler.getSummons();
    }

    /**
     * Checks if a given entity is a summon.
     * @param summon The summon to check the owner of
     * @return true if owner, false if no owner/not the owner
     */
    public static boolean isSummon(@NotNull Entity summon) {
        return getOwner(summon) != null;
    }

    /**
     * Checks if a given entity is a summon of a given living entity.
     * @param summon The summon to check the owner of
     * @param owner The player to check if they are the owner
     * @return true if owner, false if no owner/not the owner
     */
    public static boolean isSummonOf(@NotNull Entity summon, @NotNull LivingEntity owner) {
        Entity entity = getOwner(summon);
        if (entity == null) return false;
        return entity.is(owner);
    }

    public static void grantScrap(Player player, ResourceLocation scrap) {
        List<ResourceLocation> bookScraps = new ArrayList<>(player.getData(SBData.BOOK_SCRAPS));
//        if (bookScraps.contains(scrap)) return;

        bookScraps.add(scrap);
        player.sendSystemMessage(Component.literal("Book scrap acquired"));
        player.setData(SBData.BOOK_SCRAPS, bookScraps);

        if (player instanceof ServerPlayer serverPlayer) {
            PayloadHandler.sendScrapToast(serverPlayer, scrap);
        }
    }

    public static boolean hasScrap(Player player, ResourceLocation scrap) {
        return player.getData(SBData.BOOK_SCRAPS).contains(scrap);
    }
}
