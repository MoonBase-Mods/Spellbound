package com.ombremoon.spellbound.client.photon;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import com.ombremoon.spellbound.common.world.entity.spell.Fireball;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class FireballEffect extends EntityEffectExecutor {
    public FireballEffect(FX fx, Level level, Fireball fireball) {
        super(fx, level, fireball, AutoRotate.LOOK);
    }

    @Override
    public void updateFXObjectFrame(IFXObject fxObject, float partialTicks) {
        super.updateFXObjectFrame(fxObject, partialTicks);
        if (runtime != null && fxObject == runtime.root) {
            var fireball = (Fireball) entity;
            float size = fireball.getSize();
            float scale = size * 0.5F + 0.5F;
            runtime.root.updateScale(new Vector3f(scale, scale, scale));
        }
    }
}
