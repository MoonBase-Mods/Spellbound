package com.ombremoon.spellbound.util.portal;

import com.ombremoon.spellbound.common.init.SBTags;
import com.ombremoon.spellbound.common.world.entity.PortalEntity;
import com.ombremoon.spellbound.networking.PayloadHandler;
import com.ombremoon.spellbound.util.EntityUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.function.Consumer;

public class PortalMap<T extends PortalEntity<?>> extends Int2ObjectOpenHashMap<PortalInfo> {

    public void createOrShiftPortal(T portal, int spawnTicks) {
        this.createOrShiftPortal(portal, 2, spawnTicks, spawnedPortal -> {});
    }

    public void createOrShiftPortal(T portal, int maxPortals, int spawnTicks) {
        this.createOrShiftPortal(portal, maxPortals, spawnTicks, spawnedPortal -> {});
    }

    public <E extends Entity> void createOrShiftPortal(T portal, int maxPortals, int spawnTicks, Consumer<E> onShift) {
        if (this.size() >= maxPortals) {
            this.shiftPortals(portal.level(), portal.getId(), portal.position(), onShift);
        } else {
            PortalInfo info = new PortalInfo(this.size(), portal.position());
            this.put(portal.getId(), info);
        }
        portal.setStartTick(spawnTicks);
    }

    public boolean attemptTeleport(Entity entity, T portal) {
        if (!EntityUtil.isBoss(entity)) {
            T adjacentRift = this.getAdjacentPortal(portal, portal.level());
            if (adjacentRift != null) {
                Vec3 position = adjacentRift.position();
                if (!portal.isOnCooldown(entity)) {
                    adjacentRift.addCooldown(entity);
                    if (entity instanceof Projectile)
                        position = position.add(0, 1, 0);

                    entity.teleportTo(position.x, position.y, position.z);
                    if (entity instanceof Player teleportedPlayer)
                        PayloadHandler.setRotation(teleportedPlayer, teleportedPlayer.getXRot(), adjacentRift.getYRot());

                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private <E extends Entity> void shiftPortals(Level level, int portalID, Vec3 position, Consumer<E> onShift) {
        for (var entry : this.entrySet()) {
            var info = entry.getValue();
            if (info.id() == 0) {
                E entity = (E) level.getEntity(entry.getKey());
                if (entity != null) {
                    onShift.accept(entity);
                    entity.discard();
                }

                this.remove(entry.getKey());
            } else {
                PortalInfo newInfo = new PortalInfo(info.id() - 1, info.position());
                this.replace(entry.getKey(), newInfo);
            }
        }
        PortalInfo info = new PortalInfo(this.size(), position);
        this.put(portalID, info);
    }

    @SuppressWarnings("unchecked")
    public T getAdjacentPortal(T portal, Level level) {
        int activePortals = this.size();
        if (activePortals < 2) return null;

        int id = portal.getId();
        PortalInfo info = this.get(id);
        if (info != null) {
            int portalId = info.id() + 1;
            if (portalId >= activePortals) portalId = 0;
            for (var entry : this.entrySet()) {
                if (portalId == entry.getValue().id())
                    return (T) level.getEntity(entry.getKey());
            }
        }
        return null;
    }

    public int getPreviousPortal() {
        int i = 0;
        for (var entry : this.entrySet()) {
            if (entry.getValue().id() > i)
                i = entry.getValue().id();
        }
        return getPortalFromID(i);
    }

    private int getPortalFromID(int id) {
        for (var entry : this.entrySet()) {
            if (entry.getValue().id() == id)
                return entry.getKey();
        }
        return 0;
    }

    public void serialize(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();
        for (var entry : this.entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("PortalEntityId", entry.getKey());
            nbt.putInt("PortalId", entry.getValue().id());

            Vec3 vec3 = entry.getValue().position();
            nbt.putIntArray("PortalPosition", List.of((int)vec3.x(), (int)vec3.y(), (int)vec3.z()));
            listTag.add(nbt);
        }
        compoundTag.put("PortalInfo", listTag);
    }

    public void deserialize(CompoundTag compoundTag) {
        if (compoundTag.contains("PortalInfo", 9)) {
            ListTag listTag = compoundTag.getList("PortalInfo", 10);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag nbt = listTag.getCompound(i);
                int entityId = nbt.getInt("PortalEntityId");
                int portalId = nbt.getInt("PortalId");
                var posArray = nbt.getIntArray("PortalPosition");
                this.put(entityId, new PortalInfo(portalId, new Vec3(posArray[0], posArray[1], posArray[2])));
            }
        }
    }
}
