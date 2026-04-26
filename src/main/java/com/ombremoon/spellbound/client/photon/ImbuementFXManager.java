package com.ombremoon.spellbound.client.photon;

import com.lowdragmc.photon.client.fx.FXHelper;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.magic.api.ImbuementSpell;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class ImbuementFXManager {
    private static final Map<Key, ImbuementFX> ACTIVE = new HashMap<>();

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.isPaused()) {
            stopAll();
            return;
        }

        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof LivingEntity living) {
                visitHand(living, InteractionHand.MAIN_HAND);
                visitHand(living, InteractionHand.OFF_HAND);
            } else if (entity instanceof ItemEntity itemEntity) {
                visitItemEntity(itemEntity);
            }
        }

        Iterator<Map.Entry<Key, ImbuementFX>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Key, ImbuementFX> entry = it.next();
            ImbuementFX fx = entry.getValue();
            if (!fx.isValid()) {
                fx.stop();
                it.remove();
            }
        }
    }

    public static void stopAll() {
        for (ImbuementFX fx : ACTIVE.values()) {
            fx.stop();
        }
        ACTIVE.clear();
    }

    private static ResourceLocation resolveFXLocation(ItemStack stack) {
        if (stack.isEmpty()) return null;
        ResourceLocation override = stack.get(SBData.IMBUEMENT_FX_OVERRIDE.get());
        if (override != null) return override;
        SpellType<?> imbuement = stack.get(SBData.IMBUEMENT.get());
        if (imbuement == null) return null;
        var instance = imbuement.createSpell();
        return instance instanceof ImbuementSpell s ? s.getImbuementFX() : null;
    }

    private static void visitHand(LivingEntity living, InteractionHand hand) {
        ItemStack stack = living.getItemInHand(hand);
        ResourceLocation fxLoc = resolveFXLocation(stack);
        Key key = Key.living(living.getId(), hand);

        if (fxLoc == null) return;
        ImbuementFX existing = ACTIVE.get(key);
        if (existing != null && Objects.equals(existing.fx.getFxLocation(), fxLoc)) return;
        if (existing != null) {
            existing.stop();
            ACTIVE.remove(key);
        }

        int entityId = living.getId();
        Function<Float, Vec3> positionSupplier = partialTicks -> heldItemPosition(living, hand, partialTicks);
        BooleanSupplier validitySupplier = () -> {
            Entity e = Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.getEntity(entityId);
            if (!(e instanceof LivingEntity le) || !le.isAlive()) return false;
            ResourceLocation cur = resolveFXLocation(le.getItemInHand(hand));
            return Objects.equals(cur, fxLoc);
        };
        startFX(fxLoc, living.level(), positionSupplier, validitySupplier, key);
    }

    private static void visitItemEntity(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        ResourceLocation fxLoc = resolveFXLocation(stack);
        Key key = Key.item(itemEntity.getId());

        if (fxLoc == null) return;
        ImbuementFX existing = ACTIVE.get(key);
        if (existing != null && Objects.equals(existing.fx.getFxLocation(), fxLoc)) return;
        if (existing != null) {
            existing.stop();
            ACTIVE.remove(key);
        }

        int entityId = itemEntity.getId();
        Function<Float, Vec3> positionSupplier = partialTicks -> {
            Entity e = Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.getEntity(entityId);
            return e == null ? itemEntity.position() : e.getPosition(partialTicks).add(0, 0.25, 0);
        };
        BooleanSupplier validitySupplier = () -> {
            Entity e = Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.getEntity(entityId);
            if (!(e instanceof ItemEntity ie) || !ie.isAlive()) return false;
            ResourceLocation cur = resolveFXLocation(ie.getItem());
            return Objects.equals(cur, fxLoc);
        };
        startFX(fxLoc, itemEntity.level(), positionSupplier, validitySupplier, key);
    }

    private static void startFX(ResourceLocation fxLoc, net.minecraft.world.level.Level level,
                                Function<Float, Vec3> positionSupplier, BooleanSupplier validitySupplier, Key key) {
        var fx = FXHelper.getFX(fxLoc);
        if (fx == null) return;
        ImbuementFX effect = new ImbuementFX(fx, level, positionSupplier, validitySupplier);
        effect.start();
        ACTIVE.put(key, effect);
    }

    private static Vec3 heldItemPosition(LivingEntity living, InteractionHand hand, float partialTicks) {
        Vec3 base = living.getPosition(partialTicks);
        float yawRad = (float) Math.toRadians(lerp(partialTicks, living.yBodyRotO, living.yBodyRot));
        float pitchRad = (float) Math.toRadians(lerp(partialTicks, living.xRotO, living.getXRot()));

        double sinYaw = Math.sin(yawRad);
        double cosYaw = Math.cos(yawRad);
        double sinPitch = Math.sin(pitchRad);
        double cosPitch = Math.cos(pitchRad);

        double rightX = -cosYaw;
        double rightZ = -sinYaw;
        double forwardX = -sinYaw * cosPitch;
        double forwardY = -sinPitch;
        double forwardZ = cosYaw * cosPitch;

        boolean rightSide = isRightSide(living, hand);
        double sideMul = rightSide ? 0.4 : -0.4;
        double forwardMul = 0.45;
        double upMul = living.getEyeHeight() - 0.3;

        return new Vec3(
                base.x + rightX * sideMul + forwardX * forwardMul,
                base.y + upMul + forwardY * forwardMul,
                base.z + rightZ * sideMul + forwardZ * forwardMul
        );
    }

    private static boolean isRightSide(LivingEntity living, InteractionHand hand) {
        HumanoidArm mainArm = living instanceof Player p ? p.getMainArm() : HumanoidArm.RIGHT;
        boolean mainIsRight = mainArm == HumanoidArm.RIGHT;
        return hand == InteractionHand.MAIN_HAND ? mainIsRight : !mainIsRight;
    }

    private static float lerp(float t, float a, float b) {
        return a + (b - a) * t;
    }

    private record Key(int kind, int entityId, int hand) {
        static Key living(int id, InteractionHand h) { return new Key(0, id, h.ordinal()); }
        static Key item(int id) { return new Key(1, id, 0); }
    }
}
