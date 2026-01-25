package com.ombremoon.spellbound.networking.clientbound;

import com.ombremoon.spellbound.client.AnimationHelper;
import com.ombremoon.spellbound.client.gui.toasts.SpellboundToasts;
import com.ombremoon.spellbound.client.renderer.ArenaDebugRenderer;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.world.multiblock.MultiblockManager;
import com.ombremoon.spellbound.common.world.weather.HailstormData;
import com.ombremoon.spellbound.common.world.weather.HailstormSavedData;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.networking.serverbound.ChargeOrChannelPayload;
import com.ombremoon.spellbound.util.RenderUtil;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class ClientPayloadHandler {

    public static void handleAnimation(HandleAnimationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player().level().getPlayerByUUID(UUID.fromString(payload.playerId()));
            if (player instanceof AbstractClientPlayer clientPlayer) {
                if (payload.stopAnimation()) {
                    AnimationHelper.stopAnimation(clientPlayer, payload.animation());
                } else {
                    AnimationHelper.playAnimation(clientPlayer, payload.animation(), payload.castSpeed());
                }
            }
        });
    }

    public static void handleEndSpell(EndSpellPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            Entity entity = level.getEntity(payload.entityId());
            if (entity instanceof LivingEntity livingEntity) {
                var handler = SpellUtil.getSpellHandler(livingEntity);
                AbstractSpell spell = handler.getSpell(payload.spellType(), payload.castId());
                if (spell != null)
                    spell.endSpell();
            }
        });
    }

    public static void handleClientChargeOrChannel(final ChargeOrChannelPayload payload, final IPayloadContext context) {
        var handler = SpellUtil.getSpellHandler(context.player());
        boolean charging = payload.isChargingOrChannelling();
        handler.setChargingOrChannelling(charging);
    }

    public static void handleClientUpdateSpells(UpdateSpellsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            Entity entity = level.getEntity(payload.entityId());
            if (entity instanceof LivingEntity livingEntity) {
                AbstractSpell spell = payload.spellType().createSpell();
                if (spell != null) {
                    CompoundTag nbt = payload.initTag();
                    spell.clientCastSpell(livingEntity, level, livingEntity.getOnPos(), payload.castId(), payload.spellData(), nbt);
                }
            }
        });
    }

    public static void handleClientUpdateSpellTicks(UpdateSpellTicksPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            Entity entity = level.getEntity(payload.entityId());
            if (entity instanceof LivingEntity livingEntity) {
                var handler = SpellUtil.getSpellHandler(livingEntity);
                AbstractSpell spell = handler.getSpell(payload.spellType(), payload.castId());
                if (spell != null)
                    spell.tickCount = payload.ticks();
            }
        });
    }

    public static void handleClientUpdateSkillBuff(UpdateSkillBuffPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            Entity entity = level.getEntity(payload.entityId());
            if (entity instanceof LivingEntity livingEntity) {
                var handler = SpellUtil.getSpellHandler(livingEntity);
                if (!payload.removeBuff()) {
                    handler.forceAddBuff(payload.skillBuff(), payload.duration());
                } else {
                    handler.removeSkillBuff(payload.skillBuff());
                }
            }
        });
    }

    public static void handleClientUpdateCooldowns(UpdateCooldownsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            Entity entity = level.getEntity(payload.entityId());
            if (entity instanceof LivingEntity livingEntity) {
                var skills = SpellUtil.getSkills(livingEntity);
                skills.getCooldowns().addCooldown(payload.skill(), payload.duration());
            }
        });
    }

    public static void handleClientSpellSync(SyncSpellHandlerPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();

            SpellHandler handler = SpellUtil.getSpellHandler(context.player());
            handler.deserializeNBT(level.registryAccess(), payload.tag());
            if (handler.caster == null) handler.caster = context.player();
        });
    }

    public static void handleClientManaSync(SyncManaPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().setData(SBData.MANA, payload.mana());
        });
    }

    public static void handleClientSkillSync(SyncSkillPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();

            var holder = SpellUtil.getSkills(context.player());
            holder.deserializeNBT(level.registryAccess(), payload.tag());
        });
    }

    public static void handleClientSetSpellData(SetSpellDataPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            Entity entity = level.getEntity(payload.entityId());
            if (entity instanceof LivingEntity livingEntity) {
                var handler = SpellUtil.getSpellHandler(livingEntity);
                AbstractSpell spell = handler.castTick > 0 ? handler.getCurrentlyCastSpell() : handler.getSpell(payload.spellType(), payload.id());
                if (spell != null)
                    spell.getSpellData().assignValues(payload.packedItems());
            }
        });
    }

    public static void handleClientUpdateTree(UpdateTreePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().getData(SBData.UPGRADE_TREE).update(payload);
        });
    }

    public static void handleClientSetRotation(SetRotationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(payload.entityId());
            if (entity != null) {
                entity.setXRot(payload.xRot());
                entity.setYRot(payload.yRot());
                entity.setYHeadRot(payload.yRot());
                entity.setYBodyRot(payload.yRot());
            }
        });
    }

    public static void handleCreateParticles(CreateParticlesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            level.addParticle(payload.particle(), payload.x(), payload.y(), payload.z(), payload.xSpeed(), payload.ySpeed(), payload.zSpeed());
        });
    }

    public static void handleUpdateGlowEffect(UpdateGlowEffectPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            var handler = SpellUtil.getSpellHandler(context.player());
            Entity entity = level.getEntity(payload.entityId());
            if (entity instanceof LivingEntity livingEntity) {
                if (payload.remove()) {
                    handler.addGlowEffect(livingEntity);
                } else {
                    handler.removeGlowEffect(livingEntity);
                }
            }
        });
    }

    public static void handleChangeHailLevel(ChangeHailLevelPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            HailstormData data = HailstormSavedData.get(level);
            data.setHailLevel(payload.hailLevel());
        });
    }

    public static void handleScrapToasts(ScrapToastPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            RenderUtil.sendScrapToast(payload.scrap());
        });
    }

    public static void handlePathLevelUpToast(PathLevelUpToastPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            RenderUtil.sendLevelUpToast(payload.level(), payload.toast(), null, payload.toast().getPath());
        });
    }

    public static void handleSpellLevelUpToast(SpellLevelUpToastPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            RenderUtil.sendLevelUpToast(payload.level(), payload.toast(), payload.spellType(), null);
        });
    }

    public static void handleUpdateMultiblocks(UpdateMultiblocksPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            MultiblockManager multiblockManager = MultiblockManager.getInstance(context.player().level());
            multiblockManager.updateMultiblocks(payload.multiblocks());
            Constants.LOG.info("Loaded {} multiblocks on the client", payload.multiblocks().size());
        });
    }

    public static void handleUpdateDimensions(UpdateDimensionsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            @SuppressWarnings("resource")
            var player = context.player();

            if (player instanceof LocalPlayer localPlayer) {
                final Set<ResourceKey<Level>> dimensionList = localPlayer.connection.levels();
                Consumer<ResourceKey<Level>> keyConsumer = payload.add() ? dimensionList::add : dimensionList::remove;
                payload.keys().forEach(keyConsumer);
            }
        });
    }

    public static void handGuideBooks(SendGuideBooksPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            GuideBookManager.registerGuideBooks(payload.pages(), payload.pageIndex());
        });
    }

    public static void handleArenaDebug(ArenaDebugPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ArenaDebugRenderer.setEnabled(payload.enabled());
            if (payload.enabled()) {
                AABB bounds = new AABB(payload.minX(), payload.minY(), payload.minZ(), payload.maxX(), payload.maxY(), payload.maxZ());
                ArenaDebugRenderer.setArenaBounds(bounds);
                ArenaDebugRenderer.setSpawnPos(payload.spawnPos());
                ArenaDebugRenderer.setOriginPos(payload.originPos());
            } else {
                ArenaDebugRenderer.clear();
            }
        });
    }
}
