package com.ombremoon.spellbound.networking;

import com.ombremoon.spellbound.client.gui.toasts.SpellboundToasts;
import com.ombremoon.spellbound.common.magic.acquisition.guides.GuideBookManager;
import com.ombremoon.spellbound.common.world.multiblock.MultiblockHolder;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.magic.api.buff.SkillBuff;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import com.ombremoon.spellbound.common.magic.sync.SyncedSpellData;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.networking.clientbound.*;
import com.ombremoon.spellbound.networking.serverbound.*;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class PayloadHandler {

    public static void sendGuideBooks(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, GuideBookManager.getClientboundPayload());
    }

    public static void sendPathLevelUp(ServerPlayer player, int level, SpellboundToasts toast) {
        PacketDistributor.sendToPlayer(player, new PathLevelUpToastPayload(level, toast));
    }

    public static void sendSpellLevelUp(ServerPlayer player, int level, SpellboundToasts toast, SpellType<?> spellType) {
        PacketDistributor.sendToPlayer(player, new SpellLevelUpToastPayload(level, toast, spellType));
    }

    public static void switchMode() {
        PacketDistributor.sendToServer(new SwitchModePayload());
    }

    public static void setSpell(SpellType<?> spellType) {
        PacketDistributor.sendToServer(new SetSpellPayload(spellType));
    }

    public static void equipSpell(SpellType<?> spellType, boolean equip) {
        PacketDistributor.sendToServer(new EquipSpellPayload(spellType, equip));
    }

    public static void castSpell(int charges) {
        PacketDistributor.sendToServer(new CastSpellPayload(charges));
    }

    public static void setCastingSpell(SpellType<?> spellType, SpellContext context) {
        PacketDistributor.sendToServer(new SetCastingSpellPayload(spellType, context.getTarget() == null ? 0 : context.getTarget().getId(), context.isRecast()));
    }

    public static void castStart() {
        PacketDistributor.sendToServer(new CastStartPayload());
    }

    public static void castReset(SpellType<?> spellType, boolean recast) {
        PacketDistributor.sendToServer(new CastResetPayload(spellType, recast));
    }

    public static void stopChannel(SpellType<?> spellType) {
        PacketDistributor.sendToServer(new StopChanneledSpellPayload(spellType));
    }

    public static void updateChoice(SpellType<?> spellType, Skill skill) {
        PacketDistributor.sendToServer(new UpdateChoicePayload(spellType, skill));
    }

    public static void setChargeOrChannel(boolean isChargingOrChanneling) {
        PacketDistributor.sendToServer(new ChargeOrChannelPayload(isChargingOrChanneling));
    }

    public static void unlockSkill(Skill skill) {
        PacketDistributor.sendToServer(new UnlockSkillPayload(skill));
    }

    public static void updateMovement(float forwardImpulse, float leftImpulse) {
        PacketDistributor.sendToServer(new PlayerMovementPayload(PlayerMovementPayload.Movement.MOVE, forwardImpulse, leftImpulse, 0));
    }

    public static void updateRotation(float yRot) {
        PacketDistributor.sendToServer(new PlayerMovementPayload(PlayerMovementPayload.Movement.ROTATE, 0, 0, yRot));
    }

    public static void handleAnimation(Player player, ResourceLocation animation, float animationSpeed, boolean stopAnimation) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new HandleAnimationPayload(player.getUUID().toString(), animation, animationSpeed, stopAnimation));
    }

    public static void updateSpells(LivingEntity entity, SpellType<?> spellType, int castId,CompoundTag initTag, @Nullable CompoundTag spellData) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new UpdateSpellsPayload(entity.getId(), spellType, castId, initTag, spellData));
    }

    //Does this need to go to everyone?
    public static void setSpellTicks(LivingEntity entity, SpellType<?> spellType, int castId, int ticks) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new UpdateSpellTicksPayload(entity.getId(), spellType, castId, ticks));
    }

    public static void updateSkillBuff(ServerPlayer player, SkillBuff<?> skillBuff, int duration, boolean removeBuff) {
        PacketDistributor.sendToPlayer(player, new UpdateSkillBuffPayload(player.getId(), skillBuff, duration, removeBuff));
    }

    public static void updateCooldowns(ServerPlayer player, Holder<Skill> skill, int duration) {
        PacketDistributor.sendToPlayer(player, new UpdateCooldownsPayload(player.getId(), skill, duration));
    }

    public static void setChargeOrChannel(Player player, boolean isChargingOrChanneling) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, new ChargeOrChannelPayload(isChargingOrChanneling));
    }

    //Does this need to go to everyone?
    public static void endSpell(LivingEntity entity, SpellType<?> spellType, int castId) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new EndSpellPayload(entity.getId(), spellType, castId));
    }

    public static void syncHandlerToClient(Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncSpellHandlerPayload(SpellUtil.getSpellHandler(player).serializeNBT(player.level().registryAccess())));
    }

    public static void syncSkillsToClient(Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncSkillPayload(SpellUtil.getSkills(player).serializeNBT(player.level().registryAccess())));
    }

    public static void setSpellData(LivingEntity entity, SpellType<?> spellType, int id, List<SyncedSpellData.DataValue<?>> packedItems) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new SetSpellDataPayload(entity.getId(), spellType, id, packedItems));
    }

    public static void syncMana(Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncManaPayload(player.getData(SBData.MANA)));
    }

    public static void updateTree(Player player, boolean reset, List<Skill> added, Set<ResourceLocation> removed) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, new UpdateTreePayload(reset, added, removed));
    }

    public static void setRotation(Entity entity, float xRot, float yRot) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new SetRotationPayload(entity.getId(), xRot, yRot));
    }

    public static void createParticles(LivingEntity entity, ParticleOptions particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new CreateParticlesPayload(particle, x, y, z, xSpeed, ySpeed, zSpeed));
    }

    public static void updateGlowEffect(Player player, int entityId, boolean remove) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, new UpdateGlowEffectPayload(entityId, remove));
    }

    public static void updateDimensions(MinecraftServer server, Set<ResourceKey<Level>> keys, boolean add) {
        sendToAll(server, new UpdateDimensionsPayload(keys, add));
    }

    public static void updateMultiblocks(MinecraftServer server, List<MultiblockHolder<?>> multiblocks) {
        sendToAll(server, new UpdateMultiblocksPayload(multiblocks));
    }

    public static void sendScrapToast(ServerPlayer player, ResourceLocation scrap) {
        PacketDistributor.sendToPlayer(player, new ScrapToastPayload(scrap));
    }

    public static void sendArenaDebug(ServerPlayer player, boolean enabled, BoundingBox bounds, BlockPos spawnPos, BlockPos origin) {
        PacketDistributor.sendToPlayer(player, new ArenaDebugPayload(
                enabled,
                bounds.minX(), bounds.minY(), bounds.minZ(),
                bounds.maxX(), bounds.maxY(), bounds.maxZ(),
                spawnPos, origin
        ));
    }

    public static void sendArenaDebugDisable(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new ArenaDebugPayload(
                false, 0, 0, 0, 0, 0, 0, BlockPos.ZERO, BlockPos.ZERO
        ));
    }

/*    public static void changeHailLevel(ServerLevel level, float hailLevel) {
        PacketDistributor.sendToPlayersInDimension(level, new ChangeHailLevelPayload(hailLevel));
    }*/

    public static <PACKET extends CustomPacketPayload> void sendToAll(MinecraftServer server, PACKET packet) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.connection.hasChannel(packet)) {
                PacketDistributor.sendToPlayer(player, packet);
            }
        }
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Constants.MOD_ID).optional();
        registrar.playToClient(
                HandleAnimationPayload.TYPE,
                HandleAnimationPayload.STREAM_CODEC,
                ClientPayloadHandler::handleAnimation
        );
        registrar.playToClient(
                EndSpellPayload.TYPE,
                EndSpellPayload.STREAM_CODEC,
                ClientPayloadHandler::handleEndSpell
        );
        registrar.playToClient(
                UpdateSpellsPayload.TYPE,
                UpdateSpellsPayload.STREAM_CODEC,
                ClientPayloadHandler::handleClientUpdateSpells
        );
        registrar.playToClient(
                UpdateSpellTicksPayload.TYPE,
                UpdateSpellTicksPayload.STREAM_CODEC,
                ClientPayloadHandler::handleClientUpdateSpellTicks
        );
        registrar.playToClient(
                UpdateSkillBuffPayload.TYPE,
                UpdateSkillBuffPayload.STREAM_CODEC,
                ClientPayloadHandler::handleClientUpdateSkillBuff
        );
        registrar.playToClient(
                UpdateCooldownsPayload.TYPE,
                UpdateCooldownsPayload.STREAM_CODEC,
                ClientPayloadHandler::handleClientUpdateCooldowns
        );
        registrar.playToClient(
                SyncSpellHandlerPayload.TYPE,
                SyncSpellHandlerPayload.STREAM_CODEC,
                ClientPayloadHandler::handleClientSpellSync
        );
        registrar.playToClient(
                SyncSkillPayload.TYPE,
                SyncSkillPayload.STREAM_CODEC,
                ClientPayloadHandler::handleClientSkillSync
        );
        registrar.playToClient(
                SetSpellDataPayload.TYPE,
                SetSpellDataPayload.STREAM_CODEC,
                ClientPayloadHandler::handleClientSetSpellData
        );
        registrar.playToClient(
                SyncManaPayload.TYPE,
                SyncManaPayload.STREAM_CODEC,
                ClientPayloadHandler::handleClientManaSync
        );
        registrar.playToClient(
                UpdateTreePayload.TYPE,
                UpdateTreePayload.STREAM_CODEC,
                ClientPayloadHandler::handleClientUpdateTree
        );
        registrar.playToClient(
                SetRotationPayload.TYPE,
                SetRotationPayload.STREAM_CODEC,
                ClientPayloadHandler::handleClientSetRotation
        );
        registrar.playToClient(
                CreateParticlesPayload.TYPE,
                CreateParticlesPayload.STREAM_CODEC,
                ClientPayloadHandler::handleCreateParticles
        );
        registrar.playToClient(
                UpdateGlowEffectPayload.TYPE,
                UpdateGlowEffectPayload.STREAM_CODEC,
                ClientPayloadHandler::handleUpdateGlowEffect
        );
        registrar.playToClient(
                ChangeHailLevelPayload.TYPE,
                ChangeHailLevelPayload.STREAM_CODEC,
                ClientPayloadHandler::handleChangeHailLevel
        );
        registrar.playToClient(
                UpdateDimensionsPayload.TYPE,
                UpdateDimensionsPayload.STREAM_CODEC,
                ClientPayloadHandler::handleUpdateDimensions
        );
        registrar.playToClient(
                UpdateMultiblocksPayload.TYPE,
                UpdateMultiblocksPayload.STREAM_CODEC,
                ClientPayloadHandler::handleUpdateMultiblocks
        );

        registrar.playToServer(
                SwitchModePayload.TYPE,
                SwitchModePayload.STREAM_CODEC,
                ServerPayloadHandler::handleNetworkSwitchMode
        );
        registrar.playToServer(
                CastSpellPayload.TYPE,
                CastSpellPayload.STREAM_CODEC,
                ServerPayloadHandler::handleNetworkCastSpell
        );
        registrar.playToServer(
                SetSpellPayload.TYPE,
                SetSpellPayload.STREAM_CODEC,
                ServerPayloadHandler::handleNetworkSetSpell
        );
        registrar.playToServer(
                EquipSpellPayload.TYPE,
                EquipSpellPayload.STREAM_CODEC,
                ServerPayloadHandler::handleNetworkEquipSpell
        );
        registrar.playToServer(
                SetCastingSpellPayload.TYPE,
                SetCastingSpellPayload.STREAM_CODEC,
                ServerPayloadHandler::handleNetworkSetCastSpell
        );
        registrar.playToServer(
                CastStartPayload.TYPE,
                CastStartPayload.STREAM_CODEC,
                ServerPayloadHandler::handleNetworkCastStart
        );
        registrar.playToServer(
                CastResetPayload.TYPE,
                CastResetPayload.STREAM_CODEC,
                ServerPayloadHandler::handleNetworkCastReset
        );
        registrar.playToServer(
                StopChanneledSpellPayload.TYPE,
                StopChanneledSpellPayload.STREAM_CODEC,
                ServerPayloadHandler::handleNetworkStopChanneledSpell
        );
        registrar.playToServer(
                UpdateChoicePayload.TYPE,
                UpdateChoicePayload.STREAM_CODEC,
                ServerPayloadHandler::handleNetworkUpdateChoice
        );
        registrar.playToServer(
                UnlockSkillPayload.TYPE,
                UnlockSkillPayload.STREAM_CODEC,
                ServerPayloadHandler::handleNetworkUnlockSkill
        );
        registrar.playToServer(
                PlayerMovementPayload.TYPE,
                PlayerMovementPayload.STREAM_CODEC,
                ServerPayloadHandler::handleNetworkPlayerMovement
        );

        registrar.playBidirectional(
                ChargeOrChannelPayload.TYPE,
                ChargeOrChannelPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleClientChargeOrChannel,
                        ServerPayloadHandler::handleNetworkChargeOrChannel
                )
        );

        registrar.playToClient(
                ScrapToastPayload.TYPE,
                ScrapToastPayload.STREAM_CODEC,
                ClientPayloadHandler::handleScrapToasts
        );

        registrar.playToClient(
                PathLevelUpToastPayload.TYPE,
                PathLevelUpToastPayload.STREAM_CODEC,
                ClientPayloadHandler::handlePathLevelUpToast
        );

        registrar.playToClient(
                SpellLevelUpToastPayload.TYPE,
                SpellLevelUpToastPayload.STREAM_CODEC,
                ClientPayloadHandler::handleSpellLevelUpToast
        );

        registrar.playToClient(
                SendGuideBooksPayload.TYPE,
                SendGuideBooksPayload.STREAM_CODEC,
                ClientPayloadHandler::handGuideBooks
        );
        registrar.playToClient(
                ArenaDebugPayload.TYPE,
                ArenaDebugPayload.STREAM_CODEC,
                ClientPayloadHandler::handleArenaDebug
        );
    }
}
