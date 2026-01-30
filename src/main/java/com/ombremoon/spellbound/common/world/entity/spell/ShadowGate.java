package com.ombremoon.spellbound.common.world.entity.spell;

import com.ombremoon.sentinellib.api.box.SentinelBox;
import com.ombremoon.sentinellib.api.compat.GeoBoneOBBSentinelBox;
import com.ombremoon.sentinellib.api.compat.GeoSentinel;
import com.ombremoon.sentinellib.api.compat.ServerGeoModel;
import com.ombremoon.sentinellib.common.BoxInstanceManager;
import com.ombremoon.spellbound.common.world.entity.PortalEntity;
import com.ombremoon.spellbound.common.world.spell.transfiguration.ShadowGateSpell;
import com.ombremoon.spellbound.main.CommonClass;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;

public class ShadowGate extends PortalEntity<ShadowGateSpell> implements GeoSentinel<ShadowGate> {
    private static final EntityDataAccessor<Boolean> SHIFTED = SynchedEntityData.defineId(ShadowGate.class, EntityDataSerializers.BOOLEAN);
    public static final GeoBoneOBBSentinelBox ROOT = GeoBoneOBBSentinelBox.Builder.of("gate")
            .sizeAndOffset(0.5F, 1, 0, 0.5F, 0)
            .noDuration(Entity::isRemoved)
            .build();
    private final BoxInstanceManager manager = new BoxInstanceManager(this);
    private final ShadowGateSentinelModel model = new ShadowGateSentinelModel();

    public ShadowGate(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.triggerAllSentinelBoxes();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHIFTED, false);
    }

    @Override
    public void tick() {
        this.tickBoxes();
        super.tick();
        if (!this.level().isClientSide) {
            log(this.getSentinelModel().getBone("gate").get().getWorldPosition());
            if (this.tickCount % 81 == 0) {
                this.triggerAnim("Test", "test");
            }
        }
    }

    public void shift() {
        this.entityData.set(SHIFTED, true);
    }

    public boolean isShifted() {
        return this.entityData.get(SHIFTED);
    }

    @Override
    public int getPortalCooldown() {
        return 20;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER, 0, this::genericController));
        controllers.add(new AnimationController<>(this, "Test", 0, state -> PlayState.STOP)
                .triggerableAnim("test", RawAnimation.begin().thenPlay("test")));
    }

    @Override
    public ServerGeoModel<ShadowGate> getSentinelModel() {
        return this.model;
    }

    @Override
    public BoxInstanceManager getBoxManager() {
        return this.manager;
    }

    @Override
    public List<SentinelBox> getSentinelBoxes() {
        return ObjectArrayList.of(ROOT);
    }

    public static class ShadowGateSentinelModel extends ServerGeoModel<ShadowGate> {

        @Override
        public ResourceLocation getModelResource(ShadowGate animatable) {
            return CommonClass.customLocation("geo_sentinel/entity/shadow_gate/shadow_gate.json");
        }

        @Override
        public ResourceLocation getAnimationResource(ShadowGate animatable) {
            return CommonClass.customLocation("sentinel_anim/entity/shadow_gate/shadow_gate.json");
        }
    }
}
