package com.ombremoon.spellbound.common.magic.acquisition.divine;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleDungeonData;
import com.ombremoon.spellbound.common.world.block.DivineShrineBlock;
import com.ombremoon.spellbound.common.events.custom.MobEffectEvent;
import com.ombremoon.spellbound.common.events.custom.MobInteractEvent;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBTags;
import com.ombremoon.spellbound.common.magic.acquisition.guides.page_scraps.PageScrapManager;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.util.Loggable;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.LinkedHashMap;
import java.util.Map;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class PlayerSpellActions implements Loggable {
    private final Map<ActionHolder, ActionProgress> progress = new LinkedHashMap<>();
    private final Map<ActionHolder, Integer> recentActions = new Object2IntOpenHashMap<>();
    private final ServerPlayer player;
    private int undeadKilled = 0;
    private int undeadKillTick;
    private int villagersKilled = 0;
    private int villagerKillTick;
    private final Multimap<BlockPos, Block> shrineDecorations = ArrayListMultimap.create();
    private Entity healTarget;

    public PlayerSpellActions(ServerPlayer player) {
        this.player = player;
        this.registerListeners();
    }

    public void stopListening() {
        for (var trigger : SBTriggers.REGISTRY) {
            trigger.removePlayerListener(this);
        }
    }

    public void reload() {
        this.stopListening();
        this.progress.clear();
        this.recentActions.clear();
        this.registerListeners();
    }

    private void registerListeners() {
        for (ActionHolder actionHolder : DivineActionManager.getAllActions()) {
            this.registerListeners(actionHolder);
        }
        for (ActionHolder action : PageScrapManager.getAllActions()) {
            this.registerListeners(action);
        }
    }

    private boolean isPageScrap(ActionHolder holder) {
        return PageScrapManager.getAllActions().contains(holder);
    }

    public void award(ActionHolder action, String key) {
        var progress = this.getOrStartProgress(action);
        boolean flag2 = progress.isDone();
        this.refreshRecentActions();
        if (this.recentlyPerformedAction(action))
            return;

        if (progress.grantProgress(key)) {
            this.unregisterListeners(action);
            if (!flag2 && progress.isDone()) {
                action.value().rewards().grant(this.player);
                if (!this.isPageScrap(action))
                    this.recentActions.put(action, this.player.tickCount + action.value().cooldown());

                ServerLevel level = (ServerLevel) this.player.level();
                if (PuzzleDungeonData.isDungeon(level)) {
                    PuzzleDungeonData puzzle = PuzzleDungeonData.get(level);
                    if (puzzle.shouldCompleteObjective(action))
                        puzzle.completeObjective(action);
                }

                for (String s : progress.getCompletedCriteria()) {
                    this.revoke(action, s);
                }
            }
        }
    }

    private void refreshRecentActions() {
        for (var entry : this.recentActions.entrySet()) {
            if (this.player.tickCount > entry.getValue())
                this.recentActions.remove(entry.getKey());
        }
    }

    public boolean revoke(ActionHolder action, String key) {
        boolean flag = false;
        var progress = this.getOrStartProgress(action);
        if (progress.revokeProgress(key)) {
            this.registerListeners(action);
            flag = true;
        }
        return flag;
    }

    public boolean recentlyPerformedAction(ActionHolder action) {
        return this.recentActions.containsKey(action) && this.player.tickCount < this.recentActions.get(action);
    }

    public void resetProgress(ActionHolder action) {
        var progress = this.getOrStartProgress(action);
        this.unregisterListeners(action);
        action.value().rewards().grant(this.player);
        this.recentActions.put(action, this.player.tickCount + 600);
        for (String s : progress.getCompletedCriteria()) {
            this.revoke(action, s);
        }
    }

    public void registerListeners(ActionHolder action) {
        ActionProgress progress = this.getOrStartProgress(action);
        if (!progress.isDone()) {
            for (var entry : action.value().criteria().entrySet()) {
                var criterionProgress = progress.getCriterion(entry.getKey());
                if (criterionProgress != null && !criterionProgress.isDone())
                    this.registerListener(action, entry.getKey(), entry.getValue());
            }
        }
    }

    private <T extends CriterionTriggerInstance> void registerListener(ActionHolder action, String key, ActionCriterion<T> criterion) {
        criterion.trigger().addPlayerListener(this, new ActionTrigger.Listener<>(criterion.triggerInstance(), action, key));
    }

    public void unregisterListeners(ActionHolder action) {
        var progress = this.getOrStartProgress(action);

        for (var entry : action.value().criteria().entrySet()) {
            var criterionProgress = progress.getCriterion(entry.getKey());
            if (criterionProgress != null && (criterionProgress.isDone() || progress.isDone()))
                this.removeListener(action, entry.getKey(), entry.getValue());
        }
    }

    private <T extends CriterionTriggerInstance> void removeListener(ActionHolder action, String key, ActionCriterion<T> criterion) {
        criterion.trigger().removePlayerListener(this, new ActionTrigger.Listener<>(criterion.triggerInstance(), action, key));
    }

    public ActionProgress getOrStartProgress(ActionHolder action) {
        var progress = this.progress.get(action);
        if (progress == null) {
            progress = new ActionProgress();
            this.startProgress(action, progress);
        }
        return progress;
    }

    private void startProgress(ActionHolder action, ActionProgress progress) {
        progress.update(action.value().requirements());
        this.progress.put(action, progress);
    }

    @SubscribeEvent
    public static void onDataReload(OnDatapackSyncEvent event) {
        for (Player player : event.getRelevantPlayers().toList()) {
            var handler = SpellUtil.getSpellHandler(player);
            var actions = handler.getSpellActions();
            actions.reload();
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        LevelAccessor accessor = event.getLevel();
        Entity entity = event.getEntity();
        BlockState state = event.getPlacedBlock();
        BlockPos blockPos = event.getPos();
        if (accessor instanceof Level level && !level.isClientSide && entity instanceof ServerPlayer player && state.is(BlockTags.FLOWERS)) {
            var actions = SpellUtil.getSpellHandler(player).getSpellActions();
            var shrine = DivineShrineBlock.getNearestShrine(level, blockPos);
            if (shrine != null) {
                BlockPos shrinePos = shrine.getFirst();
                var decorations = actions.shrineDecorations.get(shrinePos);
                if (decorations.isEmpty() || !decorations.contains(state.getBlock())) {
                    actions.shrineDecorations.put(shrinePos, state.getBlock());
                    decorations = actions.shrineDecorations.get(shrinePos);
                }

                if (decorations.size() >= 18) {
                    SBTriggers.DECORATED_SHRINE.get().trigger(player);
                    actions.shrineDecorations.removeAll(shrinePos);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        LevelAccessor accessor = event.getLevel();
        Player player = event.getPlayer();
        BlockState state = event.getState();
        BlockPos blockPos = event.getPos();
        if (accessor instanceof Level level && !level.isClientSide) {
            var actions = SpellUtil.getSpellHandler(player).getSpellActions();
            if (state.is(BlockTags.FLOWERS)) {
                var shrine = DivineShrineBlock.getNearestShrine(level, blockPos);
                if (shrine != null) {
                    BlockPos shrinePos = shrine.getFirst();
                    var decorations = actions.shrineDecorations.get(shrinePos);
                    if (!decorations.isEmpty())
                        actions.shrineDecorations.removeAll(shrinePos);
                }
            } else if (state.is(SBTags.Blocks.DIVINE_SHRINE) && actions.shrineDecorations.containsKey(blockPos)) {
                actions.shrineDecorations.removeAll(blockPos);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        DamageSource source = event.getSource();
        if (livingEntity.level().isClientSide)
            return;

        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            PlayerSpellActions actions = SpellUtil.getSpellHandler(player).getSpellActions();
            SBTriggers.PLAYER_KILL.get().trigger(player, livingEntity, source, 0);

            if (livingEntity.getType().is(EntityTypeTags.UNDEAD)) {
                if (player.tickCount > actions.undeadKillTick + 200 || actions.undeadKilled > 10) {
                    actions.undeadKilled = 1;
                } else {
                    actions.undeadKilled++;
                }

                actions.undeadKillTick = player.tickCount;
                SBTriggers.KILL_UNDEAD.get().trigger(player, livingEntity, source, actions.undeadKilled);
            }

            if (livingEntity instanceof Villager) {
                if (player.tickCount > actions.villagerKillTick + 200 || actions.villagersKilled > 5) {
                    actions.villagersKilled = 1;
                } else {
                    actions.villagersKilled++;
                }

                actions.villagerKillTick = player.tickCount;
                SBTriggers.KILL_VILLAGER.get().trigger(player, livingEntity, source, actions.villagersKilled);
            }
        }
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        Entity entity = event.getEffectSource();
        MobEffectInstance instance = event.getEffectInstance();
        if (!level.isClientSide && entity instanceof Player player && instance != null && instance.getEffect().is(SBTags.MobEffects.HEALING))
            livingEntity.setData(SBData.EFFECT_HEAL_TARGET, player.getId());
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        MobEffectInstance instance = event.getEffectInstance();
        if (!level.isClientSide && instance != null && instance.getEffect().is(SBTags.MobEffects.HEALING))
            livingEntity.removeData(SBData.EFFECT_HEAL_TARGET);
    }

    @SubscribeEvent
    public static void onEntityInteract(MobInteractEvent.Pre event) {
        Player player = event.getEntity();
        Level level = player.level();
        Mob mob = event.getMob();
        if (!level.isClientSide) {
            mob.setData(SBData.INTERACT_HEAL_TARGET, player.getId());
        }
    }

    @SubscribeEvent
    public static void onEntityHeal(LivingHealEvent event) {
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        if (!level.isClientSide) {
            float f = livingEntity.getHealth();
            float f1 = Mth.clamp(f + event.getAmount(), 0, livingEntity.getMaxHealth());
            boolean flag = f < livingEntity.getMaxHealth() && f1 >= livingEntity.getMaxHealth();
            Entity entity = level.getEntity(livingEntity.getData(SBData.INTERACT_HEAL_TARGET));
            if (entity instanceof ServerPlayer player) {
                SBTriggers.ENTITY_HEALED.get().trigger(player, livingEntity, f, f1);
                if (flag)
                    SBTriggers.HEAL_TO_FULL.get().trigger(player, livingEntity, f, f1);
            }

            Entity entity1 = level.getEntity(livingEntity.getData(SBData.EFFECT_HEAL_TARGET));
            if (entity1 instanceof ServerPlayer player) {
                SBTriggers.ENTITY_HEALED.get().trigger(player, livingEntity, f, f1);
                if (flag)
                    SBTriggers.HEAL_TO_FULL.get().trigger(player, livingEntity, f, f1);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(MobInteractEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();
        Mob mob = event.getMob();
        if (!level.isClientSide)
            mob.setData(SBData.INTERACT_HEAL_TARGET, 0);
    }
}
