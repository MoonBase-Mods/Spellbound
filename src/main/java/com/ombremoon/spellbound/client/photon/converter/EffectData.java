package com.ombremoon.spellbound.client.photon.converter;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.main.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class EffectData {
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectData> STREAM_CODEC = EffectTypes.STREAM_CODEC.dispatch(
            EffectData::getType, EffectType::streamCodec
    );
    protected final ResourceLocation location;
    protected final List<ConfigBuilder<?>> configs = new ArrayList<>();

    public EffectData(ResourceLocation location) {
        this.location = location;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public List<ConfigBuilder<?>> getConfigs() {
        return List.copyOf(this.configs);
    }

    public abstract void toNetwork(RegistryFriendlyByteBuf buffer);

    public abstract EffectType<?> getType();

    public static class Block extends EffectData {
        public final BlockPos blockPos;
        public Vec3 offset = Vec3.ZERO;
        public Vec3 rotation = Vec3.ZERO;
        public Vec3 scale = new Vec3(1, 1, 1);
        public int delay = 0;
        public boolean forcedDeath = false;
        public boolean allowMulti = false;
        public boolean checkState = false;

        public Block(ResourceLocation location, BlockPos blockPos) {
            super(location);
            this.blockPos = blockPos;
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

        public Block defineConfig(ConfigBuilder<?> config) {
            this.configs.add(config);
            return this;
        }

        @Override
        public EffectType<?> getType() {
            return EffectTypes.BLOCK;
        }

        @Override
        public void toNetwork(RegistryFriendlyByteBuf buffer) {
            buffer.writeResourceLocation(this.location);
            buffer.writeBlockPos(this.blockPos);
            buffer.writeVec3(this.offset);
            buffer.writeVec3(this.rotation);
            buffer.writeVec3(this.scale);
            buffer.writeVarInt(this.delay);
            buffer.writeBoolean(this.forcedDeath);
            buffer.writeBoolean(this.allowMulti);
            buffer.writeBoolean(this.checkState);

            buffer.writeVarInt(this.configs.size());
            for (var config : this.configs) {
                buffer.writeUtf(config.emitterName);
                buffer.writeNbt(config.config);
            }
        }

        public static Block fromNetwork(RegistryFriendlyByteBuf buffer) {
            ResourceLocation id = buffer.readResourceLocation();
            BlockPos blockPos = buffer.readBlockPos();
            Vec3 offset = buffer.readVec3();
            Vec3 rotation = buffer.readVec3();
            Vec3 scale = buffer.readVec3();
            int delay = buffer.readVarInt();
            boolean forcedDeath = buffer.readBoolean();
            boolean allowMulti = buffer.readBoolean();
            boolean checkState = buffer.readBoolean();

            List<ConfigBuilder<?>> list = new ArrayList<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                String name = buffer.readUtf();
                CompoundTag tag = buffer.readNbt();
                var builder = ConfigBuilder.Particle.of(name, buffer.registryAccess());
                builder.config.merge(tag);
                list.add(builder);
            }

            Block block = new Block(id, blockPos)
                    .setOffset(offset.x, offset.y, offset.z)
                    .setRotation(rotation.x, rotation.y, rotation.z)
                    .setScale(scale.x, scale.y, scale.z)
                    .setDelay(delay)
                    .setForcedDeath(forcedDeath)
                    .setAllowMulti(allowMulti)
                    .setCheckState(checkState);

            for (var config : list) {
                block.defineConfig(config);
            }

            return block;
        }
    }

    public static class Entity extends EffectData {
        public final int entityId;
        public final EntityEffectExecutor.AutoRotate rotate;
        public Vec3 effectPos = Vec3.ZERO;
        public Vec3 offset = Vec3.ZERO;
        public Vec3 rotation = Vec3.ZERO;
        public Vec3 scale = new Vec3(1, 1, 1);
        public int delay = 0;
        public boolean forcedDeath = false;
        public boolean allowMulti = false;

        public Entity(ResourceLocation location, int entityId, EntityEffectExecutor.AutoRotate rotate) {
            super(location);
            this.entityId = entityId;
            this.rotate = rotate;
        }

        public static Entity of(ResourceLocation effect, int entityId, EntityEffectExecutor.AutoRotate rotate) {
            return new Entity(effect, entityId, rotate);
        }

        public Entity setPos(double x, double y, double z) {
            this.effectPos = new Vec3(x, y, z);
            return this;
        }

        public Entity setPos(Vec3 pos) {
            this.effectPos = pos;
            return this;
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

        public Entity defineConfig(ConfigBuilder<?> config) {
            this.configs.add(config);
            return this;
        }

        @Override
        public void toNetwork(RegistryFriendlyByteBuf buffer) {
            buffer.writeResourceLocation(this.location);
            buffer.writeVarInt(this.entityId);
            buffer.writeEnum(this.rotate);
            buffer.writeVec3(this.offset);
            buffer.writeVec3(this.rotation);
            buffer.writeVec3(this.scale);
            buffer.writeVarInt(this.delay);
            buffer.writeBoolean(this.forcedDeath);
            buffer.writeBoolean(this.allowMulti);

            buffer.writeVarInt(this.configs.size());
            for (var config : this.configs) {
                buffer.writeUtf(config.emitterName);
                buffer.writeNbt(config.config);
            }
        }

        @Override
        public EffectType<?> getType() {
            return EffectTypes.ENTITY;
        }

        public static Entity fromNetwork(RegistryFriendlyByteBuf buffer) {
            ResourceLocation id = buffer.readResourceLocation();
            int entityId = buffer.readVarInt();
            EntityEffectExecutor.AutoRotate rotate = buffer.readEnum(EntityEffectExecutor.AutoRotate.class);
            Vec3 offset = buffer.readVec3();
            Vec3 rotation = buffer.readVec3();
            Vec3 scale = buffer.readVec3();
            int delay = buffer.readVarInt();
            boolean forcedDeath = buffer.readBoolean();
            boolean allowMulti = buffer.readBoolean();

            List<ConfigBuilder<?>> list = new ArrayList<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                String name = buffer.readUtf();
                CompoundTag tag = buffer.readNbt();
                var builder = ConfigBuilder.Particle.of(name, buffer.registryAccess());
                builder.config.merge(tag);
                list.add(builder);
            }

            Entity entity = new Entity(id, entityId, rotate)
                    .setOffset(offset.x, offset.y, offset.z)
                    .setRotation(rotation.x, rotation.y, rotation.z)
                    .setScale(scale.x, scale.y, scale.z)
                    .setDelay(delay)
                    .setForcedDeath(forcedDeath)
                    .setAllowMulti(allowMulti);

            for (var config : list) {
                entity.defineConfig(config);
            }

            return entity;
        }
    }

    public static class DeadEntity extends EffectData {
        public final int entityId;
        public final EntityEffectExecutor.AutoRotate rotate;
        public Vec3 effectPos = Vec3.ZERO;
        public Vec3 offset = Vec3.ZERO;
        public Vec3 rotation = Vec3.ZERO;
        public Vec3 scale = new Vec3(1, 1, 1);
        public int delay = 0;
        public boolean forcedDeath = false;
        public boolean allowMulti = false;

        public DeadEntity(ResourceLocation location, int entityId, EntityEffectExecutor.AutoRotate rotate) {
            super(location);
            this.entityId = entityId;
            this.rotate = rotate;
        }

        public static DeadEntity of(ResourceLocation effect, int entityId, EntityEffectExecutor.AutoRotate rotate) {
            return new DeadEntity(effect, entityId, rotate);
        }

        public DeadEntity setPos(double x, double y, double z) {
            this.effectPos = new Vec3(x, y, z);
            return this;
        }

        public DeadEntity setPos(Vec3 pos) {
            this.effectPos = pos;
            return this;
        }

        public DeadEntity setOffset(double x, double y, double z) {
            this.offset = new Vec3(x, y, z);
            return this;
        }

        public DeadEntity setRotation(double x, double y, double z) {
            this.rotation = new Vec3(x, y, z);
            return this;
        }

        public DeadEntity setScale(double x, double y, double z) {
            this.scale = new Vec3(x, y, z);
            return this;
        }

        public DeadEntity setDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public DeadEntity setForcedDeath(boolean forcedDeath) {
            this.forcedDeath = forcedDeath;
            return this;
        }

        public DeadEntity setAllowMulti(boolean allowMulti) {
            this.allowMulti = allowMulti;
            return this;
        }

        public DeadEntity defineConfig(ConfigBuilder<?> config) {
            this.configs.add(config);
            return this;
        }

        @Override
        public void toNetwork(RegistryFriendlyByteBuf buffer) {
            buffer.writeResourceLocation(this.location);
            buffer.writeVarInt(this.entityId);
            buffer.writeEnum(this.rotate);
            buffer.writeVec3(this.offset);
            buffer.writeVec3(this.rotation);
            buffer.writeVec3(this.scale);
            buffer.writeVarInt(this.delay);
            buffer.writeBoolean(this.forcedDeath);
            buffer.writeBoolean(this.allowMulti);

            buffer.writeVarInt(this.configs.size());
            for (var config : this.configs) {
                buffer.writeUtf(config.emitterName);
                buffer.writeNbt(config.config);
            }
        }

        @Override
        public EffectType<?> getType() {
            return EffectTypes.ENTITY_DEATH;
        }

        public static DeadEntity fromNetwork(RegistryFriendlyByteBuf buffer) {
            ResourceLocation id = buffer.readResourceLocation();
            int entityId = buffer.readVarInt();
            EntityEffectExecutor.AutoRotate rotate = buffer.readEnum(EntityEffectExecutor.AutoRotate.class);
            Vec3 offset = buffer.readVec3();
            Vec3 rotation = buffer.readVec3();
            Vec3 scale = buffer.readVec3();
            int delay = buffer.readVarInt();
            boolean forcedDeath = buffer.readBoolean();
            boolean allowMulti = buffer.readBoolean();

            List<ConfigBuilder<?>> list = new ArrayList<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                String name = buffer.readUtf();
                CompoundTag tag = buffer.readNbt();
                var builder = ConfigBuilder.Particle.of(name, buffer.registryAccess());
                builder.config.merge(tag);
                list.add(builder);
            }

            DeadEntity deadEntity = new DeadEntity(id, entityId, rotate)
                    .setOffset(offset.x, offset.y, offset.z)
                    .setRotation(rotation.x, rotation.y, rotation.z)
                    .setScale(scale.x, scale.y, scale.z)
                    .setDelay(delay)
                    .setForcedDeath(forcedDeath)
                    .setAllowMulti(allowMulti);

            for (var config : list) {
                deadEntity.defineConfig(config);
            }

            return deadEntity;
        }
    }

    public static class StaticEntity extends EffectData {
        public final int entityId;
        public final EntityEffectExecutor.AutoRotate rotate;
        public Vec3 effectPos = Vec3.ZERO;
        public Vec3 offset = Vec3.ZERO;
        public Vec3 rotation = Vec3.ZERO;
        public Vec3 scale = new Vec3(1, 1, 1);
        public int delay = 0;
        public boolean forcedDeath = false;
        public boolean allowMulti = false;

        public StaticEntity(ResourceLocation location, int entityId, EntityEffectExecutor.AutoRotate rotate) {
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

        public StaticEntity defineConfig(ConfigBuilder<?> config) {
            this.configs.add(config);
            return this;
        }

        @Override
        public void toNetwork(RegistryFriendlyByteBuf buffer) {
            buffer.writeResourceLocation(this.location);
            buffer.writeVarInt(this.entityId);
            buffer.writeEnum(this.rotate);
            buffer.writeVec3(this.effectPos);
            buffer.writeVec3(this.offset);
            buffer.writeVec3(this.rotation);
            buffer.writeVec3(this.scale);
            buffer.writeVarInt(this.delay);
            buffer.writeBoolean(this.forcedDeath);
            buffer.writeBoolean(this.allowMulti);

            buffer.writeVarInt(this.configs.size());
            for (var config : this.configs) {
                buffer.writeUtf(config.emitterName);
                buffer.writeNbt(config.config);
            }
        }

        @Override
        public EffectType<?> getType() {
            return EffectTypes.STATIC_ENTITY;
        }

        public static StaticEntity fromNetwork(RegistryFriendlyByteBuf buffer) {
            ResourceLocation id = buffer.readResourceLocation();
            int entityId = buffer.readVarInt();
            EntityEffectExecutor.AutoRotate rotate = buffer.readEnum(EntityEffectExecutor.AutoRotate.class);
            Vec3 effectPos = buffer.readVec3();
            Vec3 offset = buffer.readVec3();
            Vec3 rotation = buffer.readVec3();
            Vec3 scale = buffer.readVec3();
            int delay = buffer.readVarInt();
            boolean forcedDeath = buffer.readBoolean();
            boolean allowMulti = buffer.readBoolean();

            List<ConfigBuilder<?>> list = new ArrayList<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                String name = buffer.readUtf();
                CompoundTag tag = buffer.readNbt();
                var builder = ConfigBuilder.Particle.of(name, buffer.registryAccess());
                builder.config.merge(tag);
                list.add(builder);
            }

            StaticEntity staticEntity = new StaticEntity(id, entityId, rotate)
                    .setPos(effectPos)
                    .setOffset(offset.x, offset.y, offset.z)
                    .setRotation(rotation.x, rotation.y, rotation.z)
                    .setScale(scale.x, scale.y, scale.z)
                    .setDelay(delay)
                    .setForcedDeath(forcedDeath)
                    .setAllowMulti(allowMulti);

            for (var config : list) {
                staticEntity.defineConfig(config);
            }

            return staticEntity;
        }
    }
}
