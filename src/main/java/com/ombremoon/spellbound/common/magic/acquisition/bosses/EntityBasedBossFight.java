package com.ombremoon.spellbound.common.magic.acquisition.bosses;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBBossFights;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.DataComponentStorage;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualHelper;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.world.entity.SBLivingEntity;
import com.ombremoon.spellbound.common.world.item.SpellTomeItem;
import com.ombremoon.spellbound.main.Keys;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class EntityBasedBossFight extends BossFight {
    public static final Codec<EntityBasedBossFight> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BossSpawn.CODEC.listOf().fieldOf("bosses").forGetter(bossFight -> bossFight.bosses),
                    SBSpells.REGISTRY.byNameCodec().fieldOf("path").forGetter(bossFight -> bossFight.spell),
                    Vec3.CODEC.fieldOf("playerSpawnOffset").forGetter(bossFight -> bossFight.playerSpawnOffset),
                    DimensionData.CODEC.fieldOf("dimensionData").forGetter(bossFight -> bossFight.dimensionData)
            ).apply(instance, EntityBasedBossFight::new)
    );

    private final List<BossSpawn> bosses;

    public EntityBasedBossFight(List<BossSpawn> bosses, SpellType<?> spell, Vec3 playerSpawnOffset, DimensionData dimensionData) {
        super(spell, playerSpawnOffset, dimensionData);
        this.bosses = bosses;
    }

    @Override
    public BossFightInstance<?, ?> createFight(ServerLevel level) {
        return new Instance(this);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Instance extends BossFightInstance<EntityBasedBossFight, Instance> {
        public static final MapCodec<Instance> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        EntityBasedBossFight.CODEC.fieldOf("bossFight").forGetter(inst -> inst.bossFight)
                ).apply(instance, Instance::new)
        );
        final List<Integer> bosses = new ObjectArrayList<>();

        Instance(EntityBasedBossFight bossFight) {
            super(bossFight);
        }

        @Override
        public boolean initializeWinCondition(ServerLevel level, EntityBasedBossFight bossFight) {
            for (BossSpawn boss : bossFight.bosses) {
                Vec3 offset = boss.spawnOffset;
                Entity entity = boss.boss.create(level);
                if (entity != null) {
                    entity.setPos(Vec3.atBottomCenterOf(ORIGIN.offset((int) offset.x, (int) offset.y, (int) offset.z)));
                    level.addFreshEntity(entity);
                    this.bosses.add(entity.getId());
                }
            }

            return this.bosses.size() == bossFight.bosses.size();
        }

        @Override
        public void tickFight(ServerLevel level, EntityBasedBossFight bossFight) {
            this.bosses.removeIf(id -> {
                Entity entity = level.getEntity(id);
                return entity == null || entity.isRemoved();
            });

            if (this.bosses.isEmpty())
                this.defeatedBoss = true;
        }

        @Override
        public boolean winCondition(ServerLevel level, EntityBasedBossFight bossFight) {
            return this.bosses.isEmpty();
        }

        @Override
        public void endFight(ServerLevel level, EntityBasedBossFight bossFight) {
            if (this.defeatedBoss && bossFight.spell != null) {
                Vec3 spawnOffset = bossFight.playerSpawnOffset;
                BlockPos spawnPos = ORIGIN.offset((int) spawnOffset.x, (int) spawnOffset.y, (int) spawnOffset.z);
                RitualHelper.createItem(
                        level,
                        spawnPos.above(2),
                        SpellTomeItem.createWithSpell(bossFight.spell),
                        DataComponentStorage.optionalOf(
                                new TypedDataComponent<>(SBData.BOSS_PICKUP.get(), true)
                        ));
            }
        }

        @Override
        public MapCodec<Instance> codec() {
            return SBBossFights.DEFAULT.get();
        }

        public List<Entity> getBosses(ServerLevel level) {
            return this.bosses.stream().map(level::getEntity).filter(Objects::nonNull).toList();
        }

        @Override
        public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
            nbt.putIntArray("Bosses", this.bosses);
            return nbt;
        }

        @Override
        public void load(CompoundTag nbt) {
            if (nbt.contains("Bosses", 11)) {
                for (int i : nbt.getIntArray("Bosses")) {
                    this.bosses.add(i);
                }
            }
        }
    }

    public static class Builder implements BossFightBuilder<EntityBasedBossFight> {
        private final List<BossSpawnSupplier> bosses = new ObjectArrayList<>();
        private ResourceLocation spell;
        private Vec3 playerSpawnOffset = Vec3.ZERO;

        public Builder spell(ResourceLocation spell) {
            this.spell = spell;
            return this;
        }

        public Builder withBoss(Supplier<? extends EntityType<?>> entity, int x, int y, int z) {
            this.bosses.add(new BossSpawnSupplier(entity, new Vec3(x, y, z)));
            return this;
        }

        public Builder spawnPlayerAt(int x, int y, int z) {
            this.playerSpawnOffset = new Vec3(x, y, z);
            return this;
        }

        @Override
        public EntityBasedBossFight build() {
            List<BossSpawn> bosses = this.bosses.stream().map(supplier -> {
                EntityType<?> type = supplier.boss().get();
                return new BossSpawn(type, supplier.spawnOffset());
            }).toList();
            SpellType<?> spell = SBSpells.REGISTRY.get(this.spell);
            return new EntityBasedBossFight(
                    bosses,
                    spell,
                    this.playerSpawnOffset,
                    new DimensionData(Keys.EMPTY_BIOME, DimensionData.Weather.CLEAR));
        }
    }

    record BossSpawn(EntityType<?> boss, Vec3 spawnOffset) {
        private static final Codec<BossSpawn> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("boss").forGetter(BossSpawn::boss),
                        Vec3.CODEC.fieldOf("offset").forGetter(BossSpawn::spawnOffset)
                ).apply(instance, BossSpawn::new)
        );
    }

    record BossSpawnSupplier(Supplier<? extends EntityType<?>> boss, Vec3 spawnOffset) {
    }
}
