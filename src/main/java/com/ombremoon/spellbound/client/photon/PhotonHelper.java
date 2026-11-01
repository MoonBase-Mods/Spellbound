package com.ombremoon.spellbound.client.photon;

import com.lowdragmc.photon.client.gameobject.emitter.data.*;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleConfig;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleRendererSetting;
import com.ombremoon.spellbound.client.photon.converter.ConfigBuilder;
import net.minecraft.core.HolderLookup;

public class PhotonHelper {

    public static ParticleEmitter createParticle(ParticleConfig config, ConfigBuilder<ParticleConfig> modifiedConfig, HolderLookup.Provider registries) {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.setName(modifiedConfig.getEmitterName());
        ParticleConfig newConfig = modifiedConfig.createConfig(registries, config.serializeNBT(registries));
        copyParticleConfig(emitter.config, newConfig);
        return emitter;
    }

    private static void copyParticleConfig(ParticleConfig copyTo, ParticleConfig copyFrom) {
        copyTo.setDuration(copyFrom.getDuration());
        copyTo.setLooping(copyFrom.isLooping());
        copyTo.setPrewarm(copyFrom.getPrewarm());
        copyTo.setStartDelay(copyFrom.getStartDelay());
        copyTo.setStartLifetime(copyFrom.getStartLifetime());
        copyTo.setStartSpeed(copyFrom.getStartSpeed());
        copyTo.setStartSize(copyFrom.getStartSize());
        copyTo.setStartRotation(copyFrom.getStartRotation());
        copyTo.setStartColor(copyFrom.getStartColor());
        copyTo.setSimulationSpace(copyFrom.getSimulationSpace());
        copyTo.setMaxParticles(copyFrom.getMaxParticles());
        copyTo.setParallelUpdate(copyFrom.isParallelUpdate());
        copyTo.setParallelRendering(copyFrom.isParallelRendering());

        copyEmissionSettings(copyTo.emission, copyFrom.emission);
        copyShapeSettings(copyTo.shape, copyFrom.shape);
        copyParticleRendererSettings(copyTo.renderer, copyFrom.renderer);
        copyPhysicsSettings(copyTo.physics, copyFrom.physics);
        copyLightOverLifetimeSettings(copyTo.lights, copyFrom.lights);
        copyVelocityOverLifetimeSettings(copyTo.velocityOverLifetime, copyFrom.velocityOverLifetime);
        copyInheritVelocitySettings(copyTo.inheritVelocity, copyFrom.inheritVelocity);
        copyLifetimeByEmitterSpeedSettings(copyTo.lifetimeByEmitterSpeed, copyFrom.lifetimeByEmitterSpeed);
        copyForceOverLifetimeSettings(copyTo.forceOverLifetime, copyFrom.forceOverLifetime);
        copyColorOverLifetimeSettings(copyTo.colorOverLifetime, copyFrom.colorOverLifetime);
        copyColorBySpeedSettings(copyTo.colorBySpeed, copyFrom.colorBySpeed);
        copySizeOverLifetimeSettings(copyTo.sizeOverLifetime, copyFrom.sizeOverLifetime);
        copySizeBySpeedSettings(copyTo.sizeBySpeed, copyFrom.sizeBySpeed);
        copyRotationOverLifetimeSettings(copyTo.rotationOverLifetime, copyFrom.rotationOverLifetime);
        copyRotationBySpeedSettings(copyTo.rotationBySpeed, copyFrom.rotationBySpeed);
        copyNoiseSettings(copyTo.noise, copyFrom.noise);
        copyUVAnimationSettings(copyTo.uvAnimation, copyFrom.uvAnimation);
        copyTrailsSettings(copyTo.trails, copyFrom.trails);
        copySubEmittersSettings(copyTo.subEmitters, copyFrom.subEmitters);
    }

    private static void copyEmissionSettings(EmissionSetting copyTo, EmissionSetting copyFrom) {
        copyTo.setEmissionRate(copyFrom.getEmissionRate());
        copyTo.setDistanceRate(copyFrom.getDistanceRate());
        copyTo.setEmissionMode(copyFrom.getEmissionMode());
        copyTo.setBursts(copyFrom.getBursts());
    }

    private static void copyShapeSettings(ShapeSetting copyTo, ShapeSetting copyFrom) {
        copyTo.setShape(copyFrom.getShape());
        copyTo.setPosition(copyFrom.getPosition());
        copyTo.setRotation(copyFrom.getRotation());
        copyTo.setScale(copyFrom.getScale());
    }

    private static void copyRendererSettings(RendererSetting copyTo, RendererSetting copyFrom) {
        copyTo.setMaterials(copyFrom.getMaterials());
        copyTo.setLayer(copyFrom.getLayer());

        copyToggleGroup(copyTo.getCull(), copyFrom.getCull());
        copyTo.getCull().setCullBox(copyFrom.getCull().getCullBox());

        copyTo.setOrderInLayer(copyFrom.getOrderInLayer());
        copyTo.setVertexSortingMode(copyFrom.getVertexSortingMode());
    }

    private static void copyPhysicsSettings(PhysicsSetting copyTo, PhysicsSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setHasCollision(copyFrom.isHasCollision());
        copyTo.setRemovedWhenCollided(copyFrom.isRemovedWhenCollided());
        copyTo.setFriction(copyFrom.getFriction());
        copyTo.setCollidedFriction(copyFrom.getCollidedFriction());
        copyTo.setGravity(copyFrom.getGravity());
        copyTo.setBounceChance(copyFrom.getBounceChance());
        copyTo.setBounceRate(copyFrom.getBounceRate());
        copyTo.setBounceSpreadRate(copyFrom.getBounceSpreadRate());
    }

    private static void copyLightOverLifetimeSettings(LightOverLifetimeSetting copyTo, LightOverLifetimeSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setSkyLight(copyFrom.getSkyLight());
        copyTo.setBlockLight(copyFrom.getBlockLight());
    }

    private static void copyVelocityOverLifetimeSettings(VelocityOverLifetimeSetting copyTo, VelocityOverLifetimeSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setLinear(copyFrom.getLinear());
        copyTo.setOrbitalMode(copyFrom.getOrbitalMode());
        copyTo.setOrbital(copyFrom.getOrbital());
        copyTo.setOffset(copyFrom.getOffset());
        copyTo.setRadial(copyFrom.getRadial());
        copyTo.setSpeedModifier(copyFrom.getSpeedModifier());
    }

    private static void copyInheritVelocitySettings(InheritVelocitySetting copyTo, InheritVelocitySetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setMode(copyFrom.getMode());
        copyTo.setMultiply(copyFrom.getMultiply());
    }

    private static void copyLifetimeByEmitterSpeedSettings(LifetimeByEmitterSpeedSetting copyTo, LifetimeByEmitterSpeedSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setMultiplier(copyFrom.getMultiplier());
        copyTo.setSpeedRange(copyFrom.getSpeedRange());
    }

    private static void copyForceOverLifetimeSettings(ForceOverLifetimeSetting copyTo, ForceOverLifetimeSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setForce(copyFrom.getForce());
        copyTo.setSimulationSpace(copyFrom.getSimulationSpace());
    }

    private static void copyColorOverLifetimeSettings(ColorOverLifetimeSetting copyTo, ColorOverLifetimeSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setColor(copyFrom.getColor());
    }

    private static void copyColorBySpeedSettings(ColorBySpeedSetting copyTo, ColorBySpeedSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setColor(copyFrom.getColor());
        copyTo.setSpeedRange(copyFrom.getSpeedRange());
    }

    private static void copySizeOverLifetimeSettings(SizeOverLifetimeSetting copyTo, SizeOverLifetimeSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setSize(copyFrom.getSize());
    }

    private static void copySizeBySpeedSettings(SizeBySpeedSetting copyTo, SizeBySpeedSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setSize(copyFrom.getSize());
        copyTo.setSpeedRange(copyFrom.getSpeedRange());
    }

    private static void copyRotationOverLifetimeSettings(RotationOverLifetimeSetting copyTo, RotationOverLifetimeSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setRoll(copyFrom.getRoll());
        copyTo.setPitch(copyFrom.getPitch());
        copyTo.setYaw(copyFrom.getYaw());
    }

    private static void copyRotationBySpeedSettings(RotationBySpeedSetting copyTo, RotationBySpeedSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setRoll(copyFrom.getRoll());
        copyTo.setPitch(copyFrom.getPitch());
        copyTo.setYaw(copyFrom.getYaw());
        copyTo.setSpeedRange(copyFrom.getSpeedRange());
    }

    private static void copyNoiseSettings(NoiseSetting copyTo, NoiseSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setFrequency(copyFrom.getFrequency());
        copyTo.setQuality(copyFrom.getQuality());

        copyToggleGroup(copyTo.getRemap(), copyFrom.getRemap());
        copyTo.getRemap().setRemapCurve(copyFrom.getRemap().getRemapCurve());

        copyTo.setPosition(copyFrom.getPosition());
        copyTo.setRotation(copyFrom.getRotation());
        copyTo.setSize(copyFrom.getSize());
    }

    private static void copyUVAnimationSettings(UVAnimationSetting copyTo, UVAnimationSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setTiles(copyFrom.getTiles());
        copyTo.setAnimation(copyFrom.getAnimation());
        copyTo.setFrameOverTime(copyFrom.getFrameOverTime());
        copyTo.setStartFrame(copyFrom.getStartFrame());
        copyTo.setCycle(copyFrom.getCycle());
    }

    private static void copyTrailsSettings(TrailsSetting copyTo, TrailsSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setRatio(copyFrom.getRatio());
        copyTo.setLifetime(copyFrom.getLifetime());
        copyTo.setDieWithParticles(copyFrom.isDieWithParticles());
        copyTo.setSizeAffectsWidth(copyFrom.isSizeAffectsWidth());
        copyTo.setSizeAffectsLifetime(copyFrom.isSizeAffectsLifetime());
        copyTo.setInheritParticleColor(copyFrom.isInheritParticleColor());
        copyTo.setColorOverLifetime(copyFrom.getColorOverLifetime());
        copyTo.setTrailType(copyFrom.getTrailType());
    }

    private static void copySubEmittersSettings(SubEmittersSetting copyTo, SubEmittersSetting copyFrom) {
        copyToggleGroup(copyTo, copyFrom);
        copyTo.setEmitters(copyFrom.getEmitters());
    }

    private static void copyParticleRendererSettings(ParticleRendererSetting copyTo, ParticleRendererSetting copyFrom) {
        copyRendererSettings(copyTo, copyFrom);
        copyTo.setRenderMode(copyFrom.getRenderMode());
        copyTo.setModel(copyFrom.getModel());
        copyTo.setShade(copyFrom.isShade());
        copyTo.setUseBlockUV(copyFrom.isUseBlockUV());
        copyTo.setModelPivot(copyFrom.getModelPivot());
        copyTo.setUseGPUInstance(copyFrom.isUseGPUInstance());
    }

    private static void copyToggleGroup(ToggleGroup copyTo, ToggleGroup copyFrom) {
        copyTo.setEnable(copyFrom.isEnable());
    }
}
