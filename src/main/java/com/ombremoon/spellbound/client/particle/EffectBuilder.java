package com.ombremoon.spellbound.client.particle;

import com.lowdragmc.photon.client.fx.BlockEffectExecutor;
import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.ombremoon.spellbound.client.photon.FireballEffect;
import com.ombremoon.spellbound.client.photon.StaticEntityEffect;
import com.ombremoon.spellbound.common.world.entity.spell.Fireball;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class EffectBuilder<T extends FXEffectExecutor> {
    protected final ResourceLocation location;

    public EffectBuilder(ResourceLocation location) {
        this.location = location;
    }

    public abstract T build();

    public void buildAndStart() {
        var effect = this.build();
        if (effect != null)
            effect.start();
    }

    public static class Block extends EffectBuilder<BlockEffectExecutor> {
        private final BlockPos blockPos;
        private Vec3 offset = Vec3.ZERO;
        private Vec3 rotation = Vec3.ZERO;
        private Vec3 scale = new Vec3(1, 1, 1);
        private int delay;
        private boolean forcedDeath;
        private boolean allowMulti;
        private boolean checkState;

        Block(ResourceLocation location, BlockPos blockPos) {
            super(location);
            this.blockPos = blockPos;
        }

        @Override
        public BlockEffectExecutor build() {
            Level level = Minecraft.getInstance().level;
            if (level != null && level.isLoaded(this.blockPos)) {
                var fx = FXHelper.getFX(this.location);
                if (fx != null) {
                    var effect = new BlockEffectExecutor(fx, level, blockPos);
                    var offset = this.offset;
                    var rotation = this.rotation;
                    var scale = this.scale;
                    effect.setOffset(offset.x, offset.y, offset.z);
                    effect.setRotation(rotation.x, rotation.y, rotation.z);
                    effect.setScale(scale.x, scale.y, scale.z);
                    effect.setDelay(this.delay);
                    effect.setForcedDeath(this.forcedDeath);
                    effect.setAllowMulti(this.allowMulti);
                    effect.setCheckState(this.checkState);
                    return effect;
                }
            }

            return null;
        }

        public static Block of(ResourceLocation effect, BlockPos blockPos) {
            return new Block(effect, blockPos);
        }

        public Block setOffset(double x, double y, double z) {
            this.offset = new Vec3(x, y, z);
            return this;
        }

        public Block setRotation(double x, double y, double z) {
            this.rotation = new Vec3(x, y, z);
            return this;
        }

        public Block setScale(double x, double y, double z) {
            this.scale = new Vec3(x, y, z);
            return this;
        }

        public Block setDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public Block setForcedDeath(boolean forcedDeath) {
            this.forcedDeath = forcedDeath;
            return this;
        }

        public Block setAllowMulti(boolean allowMulti) {
            this.allowMulti = allowMulti;
            return this;
        }

        public Block setCheckState(boolean checkState) {
            this.checkState = checkState;
            return this;
        }
    }

    public static class Entity extends EffectBuilder<EntityEffectExecutor> {
        private final int entityId;
        private final EntityEffectExecutor.AutoRotate rotate;
        private Vec3 offset = Vec3.ZERO;
        private Vec3 rotation = Vec3.ZERO;
        private Vec3 scale = new Vec3(1, 1, 1);
        private int delay;
        private boolean forcedDeath;
        private boolean allowMulti;

        Entity(ResourceLocation location, int entityId, EntityEffectExecutor.AutoRotate rotate) {
            super(location);
            this.entityId = entityId;
            this.rotate = rotate;
        }

        public static Entity of(ResourceLocation effect, int entityId, EntityEffectExecutor.AutoRotate rotate) {
            return new Entity(effect, entityId, rotate);
        }

        public Entity setOffset(double x, double y, double z) {
            this.offset = new Vec3(x, y, z);
            return this;
        }

        public Entity setRotation(double x, double y, double z) {
            this.rotation = new Vec3(x, y, z);
            return this;
        }

        public Entity setScale(double x, double y, double z) {
            this.scale = new Vec3(x, y, z);
            return this;
        }

        public Entity setDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public Entity setForcedDeath(boolean forcedDeath) {
            this.forcedDeath = forcedDeath;
            return this;
        }

        public Entity setAllowMulti(boolean allowMulti) {
            this.allowMulti = allowMulti;
            return this;
        }

        @Override
        public EntityEffectExecutor build() {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                var fx = FXHelper.getFX(this.location);
                if (fx != null) {
                    var entity = level.getEntity(this.entityId);
                    if (entity != null) {
                        var effect = new EntityEffectExecutor(fx, level, entity, this.rotate);
                        var offset = this.offset;
                        var rotation = this.rotation;
                        var scale = this.scale;
                        effect.setOffset(offset.x, offset.y, offset.z);
                        effect.setRotation(rotation.x, rotation.y, rotation.z);
                        effect.setScale(scale.x, scale.y, scale.z);
                        effect.setDelay(this.delay);
                        effect.setForcedDeath(this.forcedDeath);
                        effect.setAllowMulti(this.allowMulti);
                        return effect;
                    }
                }
            }

            return null;
        }
    }

    public static class StaticEntity extends EffectBuilder<StaticEntityEffect> {
        private final int entityId;
        private final EntityEffectExecutor.AutoRotate rotate;
        private Vec3 effectPos = Vec3.ZERO;
        private Vec3 offset = Vec3.ZERO;
        private Vec3 rotation = Vec3.ZERO;
        private Vec3 scale = new Vec3(1, 1, 1);
        private int delay;
        private boolean forcedDeath;
        private boolean allowMulti;

        StaticEntity(ResourceLocation location, int entityId, EntityEffectExecutor.AutoRotate rotate) {
            super(location);
            this.entityId = entityId;
            this.rotate = rotate;
        }

        public static StaticEntity of(ResourceLocation effect, int entityId, EntityEffectExecutor.AutoRotate rotate) {
            return new StaticEntity(effect, entityId, rotate);
        }

        public StaticEntity setPos(double x, double y, double z) {
            this.effectPos = new Vec3(x, y, z);
            return this;
        }

        public StaticEntity setPos(Vec3 pos) {
            this.effectPos = pos;
            return this;
        }

        public StaticEntity setOffset(double x, double y, double z) {
            this.offset = new Vec3(x, y, z);
            return this;
        }

        public StaticEntity setRotation(double x, double y, double z) {
            this.rotation = new Vec3(x, y, z);
            return this;
        }

        public StaticEntity setScale(double x, double y, double z) {
            this.scale = new Vec3(x, y, z);
            return this;
        }

        public StaticEntity setDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public StaticEntity setForcedDeath(boolean forcedDeath) {
            this.forcedDeath = forcedDeath;
            return this;
        }

        public StaticEntity setAllowMulti(boolean allowMulti) {
            this.allowMulti = allowMulti;
            return this;
        }

        @Override
        public StaticEntityEffect build() {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                var fx = FXHelper.getFX(this.location);
                if (fx != null) {
                    var entity = level.getEntity(this.entityId);
                    if (entity != null) {
                        var effect = new StaticEntityEffect(fx, level, entity, this.rotate);
                        var offset = this.offset;
                        var rotation = this.rotation;
                        var scale = this.scale;
                        effect.setOffset(offset.x, offset.y, offset.z);
                        effect.setPos(this.effectPos.x, this.effectPos.y, this.effectPos.z);
                        effect.setRotation(rotation.x, rotation.y, rotation.z);
                        effect.setScale(scale.x, scale.y, scale.z);
                        effect.setDelay(this.delay);
                        effect.setForcedDeath(this.forcedDeath);
                        effect.setAllowMulti(this.allowMulti);
                        return effect;
                    }
                }
            }

            return null;
        }
    }

    public static class FireballBuilder extends EffectBuilder<FireballEffect> {
        private final int entityId;

        FireballBuilder(ResourceLocation location, int entityId) {
            super(location);
            this.entityId = entityId;
        }

        public static FireballBuilder of(int entityId) {
            return new FireballBuilder(CommonClass.customLocation("fireball"), entityId);
        }

        @Override
        public FireballEffect build() {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                var fx = FXHelper.getFX(this.location);
                if (fx != null) {
                    var entity = level.getEntity(this.entityId);
                    if (entity instanceof Fireball fireball) {
                        var effect = new FireballEffect(fx, level, fireball);
                        effect.setOffset(0, -0.5, 0);
                        effect.setRotation(180, 180, 0);
                        effect.setScale(0.5F, 0.5F, 0.5F);
                        return effect;
                    }
                }
            }

            return null;
        }
    }
}
