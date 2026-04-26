package com.ombremoon.spellbound.client.photon;

import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class ImbuementFX extends FXEffectExecutor {
    private final Function<Float, Vec3> positionSupplier;
    private final BooleanSupplier validitySupplier;

    public ImbuementFX(FX fx, Level level, Function<Float, Vec3> positionSupplier, BooleanSupplier validitySupplier) {
        super(fx, level);
        this.positionSupplier = positionSupplier;
        this.validitySupplier = validitySupplier;
    }

    public boolean isValid() {
        return validitySupplier.getAsBoolean();
    }

    @Override
    public void updateFXObjectTick(IFXObject fxObject) {
        if (runtime != null && fxObject == runtime.root) {
            if (!validitySupplier.getAsBoolean()) {
                runtime.destroy(forcedDeath);
            }
        }
    }

    @Override
    public void updateFXObjectFrame(IFXObject fxObject, float partialTicks) {
        if (runtime == null || fxObject != runtime.root) return;
        if (!validitySupplier.getAsBoolean()) return;

        Vec3 pos = positionSupplier.apply(partialTicks);
        runtime.root.updatePos(new Vector3f(
                (float) (pos.x + offset.x),
                (float) (pos.y + offset.y),
                (float) (pos.z + offset.z)
        ));
    }

    @Override
    public void start() {
        if (!validitySupplier.getAsBoolean()) return;

        this.runtime = fx.createRuntime();
        var root = this.runtime.getRoot();
        Vec3 pos = positionSupplier.apply(1.0F);
        root.updatePos(new Vector3f(
                (float) (pos.x + offset.x),
                (float) (pos.y + offset.y),
                (float) (pos.z + offset.z)
        ));
        root.updateRotation(rotation);
        root.updateScale(scale);
        this.runtime.emmit(this, delay);
    }

    public void stop() {
        if (runtime != null) {
            runtime.destroy(forcedDeath);
        }
    }
}
