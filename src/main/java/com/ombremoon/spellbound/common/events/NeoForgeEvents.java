package com.ombremoon.spellbound.common.events;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import com.ombremoon.sentinellib.common.event.RegisterPlayerSentinelBoxEvent;
import com.ombremoon.spellbound.client.event.SpellCastEvents;
import com.ombremoon.spellbound.common.events.custom.SpellCastEvent;
import com.ombremoon.spellbound.common.world.commands.ArenaDevCommand;
import com.ombremoon.spellbound.common.world.commands.LearnSkillsCommand;
import com.ombremoon.spellbound.common.world.commands.LearnSpellCommand;
import com.ombremoon.spellbound.common.world.commands.SpellboundCommand;
import com.ombremoon.spellbound.common.world.entity.ISpellEntity;
import com.ombremoon.spellbound.common.world.spell.ruin.fire.FlameJetSpell;
import com.ombremoon.spellbound.common.world.spell.ruin.fire.SolarRaySpell;
import com.ombremoon.spellbound.common.world.effect.SBEffect;
import com.ombremoon.spellbound.common.world.effect.SBEffectInstance;
import com.ombremoon.spellbound.common.world.weather.HailstormData;
import com.ombremoon.spellbound.common.world.weather.HailstormSavedData;
import com.ombremoon.spellbound.common.world.multiblock.MultiblockManager;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.EffectManager;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.ArenaSavedData;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualSavedData;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.api.SummonSpell;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.events.*;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.server.command.ConfigCommand;

import java.util.List;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class NeoForgeEvents {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext context = event.getBuildContext();

        new ArenaDevCommand(dispatcher, context);
        new LearnSkillsCommand(dispatcher, context);
        new LearnSpellCommand(dispatcher, context);
        new SpellboundCommand(dispatcher, context);

        ConfigCommand.register(dispatcher);
    }

    @SubscribeEvent
    public static void registerSentinelBox(RegisterPlayerSentinelBoxEvent event) {
        event.addEntry(SolarRaySpell.SOLAR_RAY);
        event.addEntry(SolarRaySpell.SOLAR_RAY_EXTENDED);
        event.addEntry(SolarRaySpell.OVERHEAT);
        event.addEntry(SolarRaySpell.SOLAR_BURST_FRONT);
        event.addEntry(SolarRaySpell.SOLAR_BURST_END);
        event.addEntry(SolarRaySpell.SOLAR_BURST_END_EXTENDED);
        FlameJetSpell.registerBoxes(event);
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            Level level = livingEntity.level();
            var handler = SpellUtil.getSpellHandler(livingEntity);
            handler.initData(livingEntity);

            if (livingEntity instanceof Player player) {
                if (!level.isClientSide) {
                    handler.sync();
                    handler.giveBook();

                    //PayloadHandler.setScraps((ServerPlayer) player, player.getData(SBData.BOOK_SCRAPS));

                    var holder = SpellUtil.getSkills(player);
                    holder.sync();

                    var tree = player.getData(SBData.UPGRADE_TREE);
                    tree.refreshTree(player);

                    ArenaSavedData data = ArenaSavedData.get((ServerLevel) level);
                    data.closeCachedArenas(player);

                    if (player instanceof ServerPlayer serverPlayer) {
                        PayloadHandler.sendGuideBooks(serverPlayer);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLeaveWorld(EntityLeaveLevelEvent event) {
        Level level = event.getLevel();
        if (event.getEntity() instanceof Player player && !level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            var handler = SpellUtil.getSpellHandler(player);
            handler.endSpells();

            if (ArenaSavedData.isArena(serverLevel)) {
                ArenaSavedData data = ArenaSavedData.get(serverLevel);
                if (handler.isArenaOwner(data.getCurrentId())) {
                    data.destroyPortal(serverLevel);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Level level = event.getEntity().level();
        Player player = event.getEntity();
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            var handler = SpellUtil.getSpellHandler(player);
            handler.endSpells();

            if (ArenaSavedData.isArena(serverLevel)) {
                ArenaSavedData data = ArenaSavedData.get(serverLevel);
                if (handler.isArenaOwner(data.getCurrentId())) {
                    data.destroyPortal(serverLevel);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPostEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            var handler = SpellUtil.getSpellHandler(entity);
            handler.tick();

            EffectManager status = entity.getData(SBData.STATUS_EFFECTS);
            if (status.isInitialised())
                status.tick(entity.tickCount);

            if (entity instanceof Player player) {
                if (player.tickCount % 20 == 0) {
                    double mana = handler.getMana();
                    double maxMana = handler.getMaxMana();
                    if (mana < maxMana) {
                        double regen = handler.getManaRegen();
                        handler.awardMana((float) regen);
                    }
                }

                if (player.level().isClientSide)
                    SpellCastEvents.chargeOrChannelSpell(event);
            }

            if (entity instanceof Mob mob) {
                if (handler.isStationary()) {
                    mob.getNavigation().stop();
                }

                if (mob.getTarget() != null && mob.getTarget().hasEffect(SBEffects.MAGI_INVISIBILITY)) {
                    mob.setTarget(null);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        LivingEntity livingEntity = event.getEntity();
        if (event.getEffect().value() instanceof SBEffect effect && event.getEffectInstance() != null)
            effect.onEffectRemoved(livingEntity, event.getEffectInstance().getAmplifier());

        if (event.getEffectInstance() instanceof SBEffectInstance effectInstance && effectInstance.willGlow()) {
            LivingEntity entity = effectInstance.getCauseEntity();
            if (entity instanceof ServerPlayer player)
                PayloadHandler.updateGlowEffect(player, livingEntity.getId(), true);
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        LivingEntity livingEntity = event.getEntity();
        if (event.getEffectInstance() != null && event.getEffectInstance().getEffect().value() instanceof SBEffect effect)
            effect.onEffectRemoved(livingEntity, event.getEffectInstance().getAmplifier());

        if (event.getEffectInstance() instanceof SBEffectInstance effectInstance && effectInstance.willGlow()) {
            LivingEntity entity = effectInstance.getCauseEntity();
            if (entity instanceof ServerPlayer player)
                PayloadHandler.updateGlowEffect(player, livingEntity.getId(), true);
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        HailstormData data = HailstormSavedData.get(event.getLevel());
        data.prepareHail();
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Pre event) {
        Level level = event.getLevel();
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            RitualSavedData ritualData = RitualSavedData.get(serverLevel);
            if (!ritualData.ACTIVE_RITUALS.isEmpty()) {
                ritualData.ACTIVE_RITUALS.removeIf(ritualInstance -> !ritualInstance.isActive());
                ritualData.ACTIVE_RITUALS.forEach(instance -> instance.tick(serverLevel));
                ritualData.setDirty();
            }

            if (ArenaSavedData.isArena(serverLevel)) {
                ArenaSavedData arenaData = ArenaSavedData.get(serverLevel);
                var bossFight = arenaData.getCurrentBossFight();
                if (!arenaData.spawnedArena()) {
                    arenaData.spawnArena(serverLevel);
                } else if (bossFight != null && arenaData.hasFightStarted()) {
                    arenaData.handleBossFightLogic(serverLevel);
                } else if (arenaData.hasFightStarted() && ArenaSavedData.isArenaEmpty(serverLevel)) {
                    arenaData.destroyPortal(serverLevel);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onSpellPickUp(ItemEntityPickupEvent.Post event) {
        Player player = event.getPlayer();
        Level level = player.level();
        ItemStack itemStack = event.getOriginalStack();
        ItemStack newStack = event.getCurrentStack();
        Boolean bool = itemStack.get(SBData.BOSS_PICKUP);
        if (!level.isClientSide && ArenaSavedData.isArena(level) && bool != null && bool) {
            ServerLevel serverLevel = (ServerLevel) level;
            newStack.set(SBData.BOSS_PICKUP, false);
            itemStack.set(SBData.BOSS_PICKUP, false);

            ArenaSavedData data = ArenaSavedData.get(serverLevel);
            data.destroyPortal(serverLevel);
        }
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        var players = event.getRelevantPlayers().toList();
        if (!players.isEmpty())
            PayloadHandler.updateMultiblocks(players.getFirst().server, MultiblockManager.getMultiblocks());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        player.setData(SBData.MANA, player.getAttribute(SBAttributes.MAX_MANA).getValue());
        PayloadHandler.syncMana(player);
    }

    @SubscribeEvent
    public static void onWorldEnd(ServerStoppingEvent event) {
        List<ServerPlayer> players = event.getServer().getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            SpellUtil.getSpellHandler(player).endSpells();
        }
    }

    @SubscribeEvent
    public static void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        SpellUtil.getSpellHandler(event.getEntity()).endSpells();
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(SBEffects.PERMAFROST))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().getType() == EntityType.PIGLIN_BRUTE) {
            LogUtils.getLogger().debug(String.valueOf(SpellUtil.getSpellEffects(event.getEntity()).getBuildUp(EffectManager.Effect.FIRE)));
        }

        LivingEntity livingEntity = event.getEntity();
        if (livingEntity.level().isClientSide)
            return;

        if (SpellUtil.isSummon(livingEntity)) {
            Entity entity = SpellUtil.getOwner(livingEntity);
            if (!(entity instanceof LivingEntity owner))
                return;

            var handler = SpellUtil.getSpellHandler(owner);
            AbstractSpell spell;
            if (livingEntity instanceof ISpellEntity<?> spellEntity) {
                spell = spellEntity.getSpell();
            } else {
                SpellType<?> spellType = SBSpells.REGISTRY.get(livingEntity.getData(SBData.SPELL_TYPE));
                int id = livingEntity.getData(SBData.SPELL_ID);
                spell = handler.getSpell(spellType, id);
            }

            if (spell instanceof SummonSpell summonSpell) {
                summonSpell.onMobRemoved(livingEntity, spell.getContext(), Entity.RemovalReason.KILLED);
                summonSpell.removeSummon(livingEntity);
            }
        }

        if (event.getSource().getEntity() instanceof LivingEntity sourceEntity)
            SpellUtil.getSpellHandler(sourceEntity).getListener().fireEvent(SpellEventListener.Events.ENTITY_KILL, new DeathEvent(sourceEntity, event));
    }

    @SubscribeEvent
    public static void onLivingCastSpell(SpellCastEvent event) {
        if (event.getEntity().level().isClientSide) return;

        SpellUtil.getSpellHandler(event.getEntity()).getListener().fireEvent(SpellEventListener.Events.CAST_SPELL, new CastSpellEvent(event.getEntity(), event));
    }

    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;

        LivingEntity target = event.getNewAboutToBeSetTarget();
        if (target != null && target.hasEffect(SBEffects.MAGI_INVISIBILITY)) {
            event.setNewAboutToBeSetTarget(null);
            return;
        }

        if (target == null)
            return;

        Entity owner = SpellUtil.getOwner(entity);
        if (SpellUtil.isSummonOf(entity, target) || owner != null && SpellUtil.IS_ALLIED.test(owner, target)) {
            event.setNewAboutToBeSetTarget(null);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(AttackEntityEvent event) {
        if (event.getEntity().level().isClientSide) return;

        SpellUtil.getSpellHandler(event.getEntity()).getListener().fireEvent(SpellEventListener.Events.ATTACK, new PlayerAttackEvent(event.getEntity(), event));
    }

    @SubscribeEvent
    public static void onLivingBlock(LivingShieldBlockEvent event) {
        if (event.getEntity().level().isClientSide) return;

        SpellUtil.getSpellHandler(event.getEntity()).getListener().fireEvent(SpellEventListener.Events.BLOCK, new LivingBlockEvent(event.getEntity(), event));
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (SpellUtil.isSummon(livingEntity)) {
            Entity owner = SpellUtil.getOwner(livingEntity);
            DamageSource source = event.getSource();
            Entity attacker = source.getEntity();
            if (attacker != null && SpellUtil.isSummonOf(attacker, owner)) {
                event.setCanceled(true);
            }

            AbstractSpell spell = SpellUtil.getSpell(livingEntity);
            if (spell instanceof SummonSpell summonSpell) {
                summonSpell.onMobIncomingHurt(spell.getContext(), event);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (event.getEntity().level().isClientSide)
            return;

        LivingEntity livingEntity = event.getEntity();
        var handler = SpellUtil.getSpellHandler(livingEntity);
        handler.getListener().fireEvent(SpellEventListener.Events.POST_DAMAGE, new DamageEvent.Post(livingEntity, event));

        DamageSource source = event.getSource();
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity attacker && SpellUtil.isSummon(attacker)) {
            AbstractSpell spell = SpellUtil.getSpell(attacker);
            if (spell instanceof SummonSpell summonSpell) {
                summonSpell.onMobPostDamage(spell.getContext(), event);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity.level().isClientSide)
            return;

        var handler = SpellUtil.getSpellHandler(livingEntity);
        handler.getListener().fireEvent(SpellEventListener.Events.PRE_DAMAGE, new DamageEvent.Pre(livingEntity, event));

        if (event.getSource().is(SBDamageTypes.RUIN_FIRE))
            livingEntity.igniteForSeconds(3.0F);

        if (livingEntity.hasEffect(SBEffects.SLEEP))
            livingEntity.removeEffect(SBEffects.SLEEP);

        if (livingEntity.hasEffect(SBEffects.PERMAFROST))
            event.setNewDamage(event.getOriginalDamage() * 1.15F);

        DamageSource source = event.getSource();
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity attacker && SpellUtil.isSummon(attacker)) {
            AbstractSpell spell = SpellUtil.getSpell(attacker);
            if (spell instanceof SummonSpell summonSpell) {
                summonSpell.onMobPreDamage(spell.getContext(), event);
            }
        } else if (SpellUtil.isSummon(livingEntity)) {
            AbstractSpell spell = SpellUtil.getSpell(livingEntity);
            if (spell instanceof SummonSpell summonSpell) {
                summonSpell.onMobPreHurt(spell.getContext(), event);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity().level().isClientSide) return;

        SpellUtil.getSpellHandler(event.getEntity()).getListener().fireEvent(SpellEventListener.Events.JUMP, new JumpEvent(event.getEntity(), event));

    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide)
            return;

        SpellUtil.getSpellHandler(entity).getListener().fireEvent(SpellEventListener.Events.EFFECT_APPLICABLE, new EffectApplicableEvent(entity, event));
    }
}
