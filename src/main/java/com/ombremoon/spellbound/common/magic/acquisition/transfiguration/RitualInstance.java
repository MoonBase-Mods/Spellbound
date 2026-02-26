package com.ombremoon.spellbound.common.magic.acquisition.transfiguration;

import com.mojang.serialization.Dynamic;
import com.ombremoon.spellbound.common.magic.SpellPath;
import com.ombremoon.spellbound.common.magic.effects.EffectHolder;
import com.ombremoon.spellbound.common.magic.effects.MagicEffectInstance;
import com.ombremoon.spellbound.common.magic.effects.RangeProvider;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class RitualInstance {
    private static final Logger LOGGER = Constants.LOG;
    private final Holder<TransfigurationRitual> ritualHolder;
    private final UUID ownerID;
    private final BlockPos blockPos;
    private final Multiblock.MultiblockPattern pattern;
    private final int tier;
    private final List<MagicEffectInstance> magicEffects = new ObjectArrayList<>();
    private boolean active;
    public int startTicks;
    public int ticks;

    public RitualInstance(Holder<TransfigurationRitual> ritualHolder, UUID ownerID, BlockPos blockPos, Multiblock.MultiblockPattern pattern, int tier) {
        this.ritualHolder = ritualHolder;
        this.ownerID = ownerID;
        this.blockPos = blockPos;
        this.pattern = pattern;
        this.tier = tier;

        TransfigurationRitual ritual = ritualHolder.value();
        for (EffectHolder effect : ritual.effects()) {
            this.magicEffects.add(new MagicEffectInstance(effect));
        }
    }

    public void tick(ServerLevel level) {
        Player player = level.getPlayerByUUID(this.ownerID);
        if (player == null)
            return;

        TransfigurationRitual ritual = ritualHolder.value();
        TransfigurationMultiblock multiblock = (TransfigurationMultiblock) pattern.multiblock();
        if (this.ticks >= ritual.definition().duration()) {
            this.active = false;
            multiblock.clearMultiblock(player, level, pattern);
            var skills = SpellUtil.getSkills(player);
            skills.awardPathXP(SpellPath.TRANSFIGURATION, ritual.definition().pathXP());
            return;
        }

        if (this.startTicks < ritual.getStartupTime()) {
            this.startTicks++;
        } else {
            for (MagicEffectInstance effect : this.magicEffects) {
                List<LivingEntity> list = new ObjectArrayList<>();
                list.add(player);
                Optional<RangeProvider> optional = effect.getEffect().range();
                if (optional.isPresent()) {
                    RangeProvider range = optional.get();
                    if (range.affectsEntities()) {
                        float radius = range.maxRadius();
                        float height = range.maxHeight();
                        AABB aabb = AABB.ofSize(Vec3.atCenterOf(blockPos), radius, height, radius);
                        list.addAll(level.getEntitiesOfClass(LivingEntity.class, aabb));
                    }
                }

                for (LivingEntity entity : list) {
                    if (this.startTicks == ritual.getStartupTime() || !effect.hasSource()) {
                        effect.initializeEffect(player);
                    }

                    effect.attemptToActivateOrRemoveEffect(level, ritual.definition().tier(), entity, this.blockPos, this.pattern);
                }
            }
            this.ticks++;
        }
    }

    public void doPostAttackEffects(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        Entity source = event.getSource().getEntity();
        Level level = target.level();
        if (!level.isClientSide) {
            for (MagicEffectInstance effect : this.magicEffects) {
                effect.doPostAttackEffects(target, source, this.tier, this.blockPos, this.pattern, event.getSource());
            }
        }
    }

    public void doPreAttackEffects(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        Entity source = event.getSource().getEntity();
        Level level = target.level();
        if (!level.isClientSide) {
            for (MagicEffectInstance effect : this.magicEffects) {
                effect.doPreAttackEffects(target, source, this.tier, this.blockPos, this.pattern, event.getContainer());
            }
        }
    }

    public void doPreDamageEffects(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        Entity source = event.getSource().getEntity();
        Level level = target.level();
        if (!level.isClientSide) {
            for (MagicEffectInstance effect : this.magicEffects) {
                effect.doPreDamageEffects(target, source, this.tier, this.blockPos, this.pattern, event.getContainer());
            }
        }
    }

/*    public void doBlockBreakEffects(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = player.level();
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            for (MagicEffectInstance effect : this.magicEffects) {
                EffectHolder holder = effect.getEffect();
                if (holder.hasComponent(SBData.HIT_BLOCK) && holder.withinValidEffectRange(player, this.blockPos)) {
                    holder.effect().onActivated(serverLevel, this.tier, serverLevel.getEntity(this.ownerID), player, this.blockPos, this.pattern);
                }
            }
        }
    }*/

    public void toggleRitual() {
        this.active = !this.active;
    }

    public boolean isActive() {
        return this.active;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        TransfigurationRitual.CODEC
                .encodeStart(NbtOps.INSTANCE, this.ritualHolder)
                .resultOrPartial(LOGGER::error)
                .ifPresent(nbt -> tag.put("Ritual", nbt));
        tag.putUUID("Owner", this.ownerID);
        tag.putInt("X", blockPos.getX());
        tag.putInt("Y", blockPos.getY());
        tag.putInt("Z", blockPos.getZ());
        tag.put("Pattern", this.pattern.save());
        tag.putInt("Tier", this.tier);
        tag.putBoolean("Active", this.active);
        tag.putInt("Ticks", this.ticks);
        return tag;
    }

    public static RitualInstance load(CompoundTag tag) {
        if (tag.contains("Ritual", 10)) {
            var optional = TransfigurationRitual.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, tag.get("Ritual"))).resultOrPartial(LOGGER::error);
            if (optional.isPresent()) {
                UUID owner = tag.getUUID("Owner");
                BlockPos blockPos = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
                Multiblock.MultiblockPattern pattern = Multiblock.MultiblockPattern.load(tag);
                int tier = tag.getInt("Tier");
                RitualInstance instance = new RitualInstance(optional.orElseThrow(), owner, blockPos, pattern, tier);
                instance.active = tag.getBoolean("Active");
                instance.ticks = tag.getInt("Ticks");
                return instance;
            }
        }

        return null;
    }
}
