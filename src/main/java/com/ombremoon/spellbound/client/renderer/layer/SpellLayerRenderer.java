package com.ombremoon.spellbound.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ombremoon.spellbound.client.event.ClientEventFactory;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

public class SpellLayerRenderer<T extends AbstractSpell> extends HumanoidModel implements GeoRenderer<T> {
    protected final GeoRenderLayersContainer<T> renderLayers = new GeoRenderLayersContainer<>(this);
    protected final GeoModel<T> model;

    protected T animatable;
    protected HumanoidModel<?> baseModel;
    protected float scaleWidth = 1;
    protected float scaleHeight = 1;

    protected Matrix4f entityRenderTranslations = new Matrix4f();
    protected Matrix4f modelRenderTranslations = new Matrix4f();

    protected BakedGeoModel lastModel = null;
    protected GeoBone head = null;
    protected GeoBone body = null;
    protected GeoBone vfx = null;
    protected GeoBone rightArm = null;
    protected GeoBone leftArm = null;
    protected GeoBone rightLeg = null;
    protected GeoBone leftLeg = null;
    protected GeoBone rightBoot = null;
    protected GeoBone leftBoot = null;

    protected LivingEntity currentEntity = null;
    protected MultiBufferSource bufferSource = null;
    protected float partialTick;
    protected float limbSwing;
    protected float limbSwingAmount;
    protected float netHeadYaw;
    protected float headPitch;

    public SpellLayerRenderer(GeoModel<T> model) {
        super(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        this.model = model;
        this.young = false;
    }

    @Override
    public GeoModel<T> getGeoModel() {
        return this.model;
    }

    @Override
    public T getAnimatable() {
        return this.animatable;
    }

    public LivingEntity getCurrentEntity() {
        return this.currentEntity;
    }

    @Override
    public long getInstanceId(T animatable) {
        return animatable.hashCode();
    }

    @Override
    public @Nullable RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.itemEntityTranslucentCull(texture);
    }

    @Override
    public List<GeoRenderLayer<T>> getRenderLayers() {
        return this.renderLayers.getRenderLayers();
    }

    public SpellLayerRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
        this.renderLayers.addLayer(renderLayer);

        return this;
    }

    public SpellLayerRenderer<T> withScale(float scale) {
        return withScale(scale, scale);
    }

    public SpellLayerRenderer<T> withScale(float scaleWidth, float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;

        return this;
    }

    public GeoBone getHeadBone(GeoModel<T> model) {
        return model.getBone("head").orElse(null);
    }

    public GeoBone getBodyBone(GeoModel<T> model) {
        return model.getBone("body").orElse(null);
    }

    public GeoBone getRightArmBone(GeoModel<T> model) {
        return model.getBone("rightArm").orElse(null);
    }

    public GeoBone getLeftArmBone(GeoModel<T> model) {
        return model.getBone("leftArm").orElse(null);
    }

    public GeoBone getRightLegBone(GeoModel<T> model) {
        return model.getBone("rightLeg").orElse(null);
    }

    public GeoBone getLeftLegBone(GeoModel<T> model) {
        return model.getBone("leftLeg").orElse(null);
    }

    public GeoBone getRightBootBone(GeoModel<T> model) {
        return model.getBone("rightBoot").orElse(null);
    }

    public GeoBone getLeftBootBone(GeoModel<T> model) {
        return model.getBone("leftBoot").orElse(null);
    }

    public GeoBone getVFXBone(GeoModel<T> model) {
        return model.getBone("vfx").orElse(null);
    }

    /*public ResourceLocation getTextureLocation(AbstractSpell spell) {
        return GeoRenderer.super.getTextureLocation(spell);
    }*/

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        this.entityRenderTranslations = new Matrix4f(poseStack.last().pose());

        applyBaseModel(this.baseModel);
        grabRelevantBones(model);
        applyBaseTransformations(this.baseModel);
        scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource bufferSource =  mc.levelRenderer.renderBuffers.bufferSource();

        if (mc.levelRenderer.shouldShowEntityOutlines() && mc.shouldEntityAppearGlowing(this.currentEntity))
            bufferSource =  mc.levelRenderer.renderBuffers.outlineBufferSource();

        float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(true);
        RenderType renderType = getRenderType(this.animatable, getTextureLocation(this.animatable), bufferSource, partialTick);
        buffer = bufferSource.getBuffer(renderType);

        defaultRender(poseStack, this.animatable, bufferSource, null, buffer,
                0, partialTick, packedLight);

        this.animatable = null;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.pushPose();
        poseStack.translate(0, 24 / 16f, 0);
        poseStack.scale(-1, -1, 1);

        if (!isReRender) {
            AnimationState<T> animationState = new AnimationState<>(animatable, 0, 0, partialTick, false);
            long instanceId = getInstanceId(animatable);
            GeoModel<T> currentModel = getGeoModel();

            animationState.setData(DataTickets.TICK, animatable.getTick(this.currentEntity));
            animationState.setData(AbstractSpell.DATA_TICKET, this.animatable);
            animationState.setData(DataTickets.ENTITY, this.currentEntity);
            currentModel.addAdditionalStateData(animatable, instanceId, animationState::setData);
            currentModel.handleAnimations(animatable, instanceId, animationState, partialTick);
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        if (buffer != null)
            GeoRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
                    packedLight
                    , packedOverlay, colour);

        poseStack.popPose();
    }

    @Override
    public void doPostRenderCleanup() {
        this.baseModel = null;
        this.currentEntity = null;
        this.animatable = null;
        this.bufferSource = null;
        this.partialTick = 0;
        this.limbSwing = 0;
        this.limbSwingAmount = 0;
        this.netHeadYaw = 0;
        this.headPitch = 0;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());

            bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
            bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations));
        }

        GeoRenderer.super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        if (this.lastModel == bakedModel)
            return;

        GeoModel<T> model = getGeoModel();
        this.lastModel = bakedModel;
        this.head = getHeadBone(model);
        this.body = getBodyBone(model);
        this.rightArm = getRightArmBone(model);
        this.leftArm = getLeftArmBone(model);
        this.rightLeg = getRightLegBone(model);
        this.leftLeg = getLeftLegBone(model);
        this.rightBoot = getRightBootBone(model);
        this.leftBoot = getLeftBootBone(model);
        this.vfx = getVFXBone(model);
    }

    public void prepForRender(@Nullable LivingEntity entity, AbstractSpell spell, @Nullable HumanoidModel<?> baseModel, MultiBufferSource bufferSource,
                              float partialTick, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch) {
        if (entity == null|| baseModel == null)
            return;

        this.baseModel = baseModel;
        this.currentEntity = entity;
        this.animatable = (T) spell;
        this.bufferSource = bufferSource;
        this.partialTick = partialTick;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
    }

    protected void applyBaseModel(HumanoidModel<?> baseModel) {
        this.young = baseModel.young;
        this.crouching = baseModel.crouching;
        this.riding = baseModel.riding;
        this.rightArmPose = baseModel.rightArmPose;
        this.leftArmPose = baseModel.leftArmPose;
    }

    protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
        if (this.head != null) {
            ModelPart headPart = baseModel.head;

            RenderUtil.matchModelPartRot(headPart, this.head);
            this.head.updatePosition(headPart.x, -headPart.y, headPart.z);
        }

        if (this.body != null) {
            ModelPart bodyPart = baseModel.body;

            RenderUtil.matchModelPartRot(bodyPart, this.body);
            this.body.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
        }

        if (this.vfx != null) {
            ModelPart bodyPart = baseModel.body;

            RenderUtil.matchModelPartRot(bodyPart, this.vfx);
            this.vfx.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
        }

        if (this.rightArm != null) {
            ModelPart rightArmPart = baseModel.rightArm;

            RenderUtil.matchModelPartRot(rightArmPart, this.rightArm);
            this.rightArm.updatePosition(rightArmPart.x + 5, 2 - rightArmPart.y, rightArmPart.z);
        }

        if (this.leftArm != null) {
            ModelPart leftArmPart = baseModel.leftArm;

            RenderUtil.matchModelPartRot(leftArmPart, this.leftArm);
            this.leftArm.updatePosition(leftArmPart.x - 5f, 2f - leftArmPart.y, leftArmPart.z);
        }

        if (this.rightLeg != null) {
            ModelPart rightLegPart = baseModel.rightLeg;

            RenderUtil.matchModelPartRot(rightLegPart, this.rightLeg);
            this.rightLeg.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);

            if (this.rightBoot != null) {
                RenderUtil.matchModelPartRot(rightLegPart, this.rightBoot);
                this.rightBoot.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);
            }
        }

        if (this.leftLeg != null) {
            ModelPart leftLegPart = baseModel.leftLeg;

            RenderUtil.matchModelPartRot(leftLegPart, this.leftLeg);
            this.leftLeg.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);

            if (this.leftBoot != null) {
                RenderUtil.matchModelPartRot(leftLegPart, this.leftBoot);
                this.leftBoot.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);
            }
        }
    }

    @Override
    public void setAllVisible(boolean visible) {
        super.setAllVisible(visible);

        setBoneVisible(this.head, visible);
        setBoneVisible(this.body, visible);
        setBoneVisible(this.vfx, visible);
        setBoneVisible(this.rightArm, visible);
        setBoneVisible(this.leftArm, visible);
        setBoneVisible(this.rightLeg, visible);
        setBoneVisible(this.leftLeg, visible);
        setBoneVisible(this.rightBoot, visible);
        setBoneVisible(this.leftBoot, visible);
    }

    protected void setBoneVisible(@Nullable GeoBone bone, boolean visible) {
        if (bone == null)
            return;

        bone.setHidden(!visible);
    }

    @Override
    public void updateAnimatedTextureFrame(T animatable) {
        if (this.currentEntity != null)
            AnimatableTexture.setAndUpdate(getTextureLocation(animatable));
    }

    @Override
    public void fireCompileRenderLayersEvent() {
        ClientEventFactory.fireSpellLayerCompileRenderLayers(this);
    }

    @Override
    public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return ClientEventFactory.fireSpellLayerPreRender(this, poseStack, model, bufferSource, partialTick, packedLight);
    }

    @Override
    public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        ClientEventFactory.fireSpellLayerPostRender(this, poseStack, model, bufferSource, partialTick, packedLight);
    }
}
