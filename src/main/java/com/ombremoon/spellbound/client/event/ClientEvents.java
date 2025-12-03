package com.ombremoon.spellbound.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.spellbound.client.KeyBinds;
import com.ombremoon.spellbound.client.gui.CastModeOverlay;
import com.ombremoon.spellbound.client.gui.SpellSelectScreen;
import com.ombremoon.spellbound.client.particle.CircleAroundPositionParticle;
import com.ombremoon.spellbound.client.particle.GenericParticle;
import com.ombremoon.spellbound.client.particle.SparkParticle;
import com.ombremoon.spellbound.client.renderer.blockentity.*;
import com.ombremoon.spellbound.client.renderer.entity.LivingShadowRenderer;
import com.ombremoon.spellbound.client.renderer.entity.PlaceholderRenderer;
import com.ombremoon.spellbound.client.renderer.entity.SpellBrokerRenderer;
import com.ombremoon.spellbound.client.renderer.entity.projectile.MushroomProjectileRenderer;
import com.ombremoon.spellbound.client.renderer.entity.spell.*;
import com.ombremoon.spellbound.client.renderer.layer.FrozenLayer;
import com.ombremoon.spellbound.client.renderer.layer.GenericSpellLayer;
import com.ombremoon.spellbound.client.renderer.layer.SpellCastRenderLayer;
import com.ombremoon.spellbound.client.renderer.types.EmissiveOutlineSpellRenderer;
import com.ombremoon.spellbound.client.renderer.types.EmissiveSpellProjectileRenderer;
import com.ombremoon.spellbound.client.renderer.types.GenericLivingEntityRenderer;
import com.ombremoon.spellbound.client.renderer.types.GenericSpellRenderer;
import com.ombremoon.spellbound.client.shader.SBShaders;
import com.ombremoon.spellbound.common.world.block.entity.RuneBlockEntity;
import com.ombremoon.spellbound.common.world.weather.ClientHailstormData;
import com.ombremoon.spellbound.common.world.weather.HailstormSavedData;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.SpellHandler;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.buff.SpellEventListener;
import com.ombremoon.spellbound.common.magic.api.buff.events.MouseInputEvent;
import com.ombremoon.spellbound.main.CommonClass;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.joml.Matrix4f;
import software.bernie.geckolib.util.Color;

public class ClientEvents {

    @EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinds.SWITCH_MODE_BINDING);
            event.register(KeyBinds.SELECT_SPELL_BINDING);
            event.register(KeyBinds.CYCLE_SPELL_BINDING);
        }

        @SubscribeEvent
        public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(SBParticles.SLUDGE.get(), GenericParticle.SludgeProvider::new);
            event.registerSpriteSet(SBParticles.MUSHROOM_SPORE.get(), GenericParticle.MushroomSpore::new);
            event.registerSpriteSet(SBParticles.SPARK.get(), SparkParticle.Provider::new);
            event.registerSpriteSet(SBParticles.GOLD_HEART.get(), HeartParticle.Provider::new);
            event.registerSpriteSet(SBParticles.TEST.get(), CircleAroundPositionParticle.Provider::new);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SBEntities.TEST_DUMMY.get(), GenericLivingEntityRenderer::new);

            event.registerEntityRenderer(SBEntities.MUSHROOM.get(), GenericSpellRenderer::new);
            event.registerEntityRenderer(SBEntities.SHADOW_GATE.get(), ShadowGateRenderer::new);
            event.registerEntityRenderer(SBEntities.SOLAR_RAY.get(), SolarRayRenderer::new);
            event.registerEntityRenderer(SBEntities.SHATTERING_CRYSTAL.get(), ShatteringCrystalRenderer::new);
            event.registerEntityRenderer(SBEntities.ICE_SHRAPNEL.get(), EmissiveSpellProjectileRenderer::new);
            event.registerEntityRenderer(SBEntities.ICE_MIST.get(), EmissiveOutlineSpellRenderer::new);
            event.registerEntityRenderer(SBEntities.STORMSTRIKE_BOLT.get(), EmissiveSpellProjectileRenderer::new);
            event.registerEntityRenderer(SBEntities.STORM_RIFT.get(), StormRiftRenderer::new);
            event.registerEntityRenderer(SBEntities.STORM_CLOUD.get(), StormCloudRenderer::new);
            event.registerEntityRenderer(SBEntities.STORM_BOLT.get(), StormBoltRenderer::new);
//            event.registerEntityRenderer(SBEntities.CYCLONE.get(), CycloneRenderer::new);
            event.registerEntityRenderer(SBEntities.HAIL.get(), HailRenderer::new);
            event.registerEntityRenderer(SBEntities.HEALING_BLOSSOM.get(), HealingBlossomRenderer::new);

            event.registerEntityRenderer(SBEntities.SPELL_BROKER.get(), SpellBrokerRenderer::new);
            event.registerEntityRenderer(SBEntities.VALKYR.get(), GenericLivingEntityRenderer::new);
            event.registerEntityRenderer(SBEntities.MINI_MUSHROOM.get(), MiniMushroomRenderer::new);
            event.registerEntityRenderer(SBEntities.GIANT_MUSHROOM.get(), GiantMushroomRenderer::new);
            event.registerEntityRenderer(SBEntities.LIVING_SHADOW.get(), LivingShadowRenderer::new);
            event.registerEntityRenderer(SBEntities.DUNGEON_SHADOW.get(), GenericLivingEntityRenderer::new);

            event.registerEntityRenderer(SBEntities.MUSHROOM_PROJECTILE.get(), MushroomProjectileRenderer::new);

            event.registerBlockEntityRenderer(SBBlockEntities.VALKY_STATUE.get(), ValkyrStatueRenderer::new);
            event.registerBlockEntityRenderer(SBBlockEntities.RUNE.get(), RuneBlockRenderer::new);
            event.registerBlockEntityRenderer(SBBlockEntities.SUMMON_PORTAL.get(), SummonPortalRenderer::new);
            event.registerBlockEntityRenderer(SBBlockEntities.TRANSFIGURATION_DISPLAY.get(), TransfigurationDisplayRenderer::new);
        }

        @SubscribeEvent
        public static void registerEntityLayers(EntityRenderersEvent.AddLayers event) {
            for (final var skin : event.getSkins()) {
                final LivingEntityRenderer<Player, PlayerModel<Player>> playerRenderer = event.getSkin(skin);
                if (playerRenderer == null)
                    continue;

                playerRenderer.addLayer(new GenericSpellLayer<>(playerRenderer));
                playerRenderer.addLayer(new SpellCastRenderLayer<>(playerRenderer));
                playerRenderer.addLayer(new FrozenLayer<>(playerRenderer));
            }

            for (var entity : event.getEntityTypes()) {
                if (entity != EntityType.PLAYER) {
                    var renderer = event.getRenderer(entity);
                    if (renderer instanceof LivingEntityRenderer<?, ?>) {
                        LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> livingRenderer = (LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>) renderer;
                        livingRenderer.addLayer(new FrozenLayer<>(livingRenderer));
                    }
                }
            }
        }

        @SubscribeEvent
        public static void registerGuisOverlays(RegisterGuiLayersEvent event) {
            event.registerAboveAll(CommonClass.customLocation("selected_spell_overlay"),
                    CastModeOverlay::new);

        }

        @SubscribeEvent
        public static void onRegisterItemColorHandlers(RegisterColorHandlersEvent.Item event) {
            event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : DyedItemColor.getOrDefault(stack, Color.WHITE.argbInt()), SBItems.CHALK.get());
        }

        @SubscribeEvent
        public static void onRegisterBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
            event.register((state, level, pos, tintIndex) ->  level != null && pos != null && level.getBlockEntity(pos) instanceof RuneBlockEntity runeBlock ? runeBlock.getData(SBData.RUNE_COLOR.get()) : -1, SBBlocks.RUNE.get());
        }
    }

    @EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player != null) {
                SpellHandler handler = SpellUtil.getSpellHandler(player);
                if (KeyBinds.SWITCH_MODE_BINDING.consumeClick()) {
                    handler.switchMode();
                    player.displayClientMessage(Component.literal("Switched to " + (handler.inCastMode() ? "Cast mode" : "Normal mode")), true);
                    PayloadHandler.switchMode();
                }
                if (handler.inCastMode()) {
                    if (KeyBinds.SELECT_SPELL_BINDING.consumeClick()) {
                        Screen screen = minecraft.screen;
                        if (!handler.getEquippedSpells().isEmpty()) {
                            minecraft.setScreen(new SpellSelectScreen());
                        } else {
                            minecraft.setScreen(screen);
                        }
                    }
                    if (KeyBinds.CYCLE_SPELL_BINDING.consumeClick()) {
                        if (handler.castTick > 0) {
                            AbstractSpell spell = handler.getCurrentlyCastSpell();
                            spell.resetCast(handler);
                        }

                        KeyBinds.getSpellCastMapping().setDown(false);
                        SpellUtil.cycle(handler, handler.getSelectedSpell());
                        PayloadHandler.setSpell(handler.getSelectedSpell());
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onMouseInputPre(InputEvent.MouseButton.Pre event) {
            Player player = Minecraft.getInstance().player;
            if (player != null)
                SpellUtil.getSpellHandler(player).getListener().fireEvent(SpellEventListener.Events.PRE_MOUSE_INPUT, new MouseInputEvent.Pre(player, event));
        }

        @SubscribeEvent
        public static void onMouseInputPost(InputEvent.MouseButton.Post event) {
            Player player = Minecraft.getInstance().player;
            if (player != null)
                SpellUtil.getSpellHandler(player).getListener().fireEvent(SpellEventListener.Events.POST_MOUSE_INPUT, new MouseInputEvent.Post(player, event));
        }

        @SubscribeEvent
        public static void onMovementInput(MovementInputUpdateEvent event) {
            var handler = SpellUtil.getSpellHandler(event.getEntity());
            if (handler.isStationary()) {
                event.getInput().leftImpulse = 0;
                event.getInput().forwardImpulse = 0;
                event.getInput().jumping = false;
            } else if (event.getEntity().tickCount % 10 == 0) {
                float forward = handler.forwardImpulse;
                float left = handler.leftImpulse;
                handler.forwardImpulse = event.getInput().forwardImpulse;
                handler.leftImpulse = event.getInput().leftImpulse;
                if (forward != handler.forwardImpulse || left != handler.leftImpulse) {
                    PayloadHandler.updateMovement(event.getInput().forwardImpulse, event.getInput().leftImpulse);
                }
            }
        }

        @SubscribeEvent
        public static void onRenderLevel(RenderLevelStageEvent event) {
            RenderLevelStageEvent.Stage stage = event.getStage();
            PoseStack poseStack = event.getPoseStack();
            Camera camera = event.getCamera();
            Matrix4f projectionMatrix = event.getProjectionMatrix();
            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            Frustum frustum = event.getFrustum();
            DeltaTracker partialTick = event.getPartialTick();

            if (stage == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
                ClientHailstormData data = (ClientHailstormData) HailstormSavedData.get(level);
//                data.renderHailstorm(event);
            }

            if (stage == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
                SBShaders.setupPoseStack(event.getPoseStack());
                SBShaders.processShaders();
            }

            if (stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
                ExtendedBlockPreviewRenderer.renderMultiblockPreviews(event.getPartialTick(), minecraft, level, camera, poseStack);
            }

        }

        @SubscribeEvent
        public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
            ClientHailstormData data = (ClientHailstormData) HailstormSavedData.get(Minecraft.getInstance().level);
//            data.renderHailstormFog(event);
        }

        @SubscribeEvent
        public static void onSpellZoom(ComputeFovModifierEvent event) {
            Player player = event.getPlayer();
            var handler = SpellUtil.getSpellHandler(player);
            event.setNewFovModifier(handler.getZoom());
        }
    }
}
