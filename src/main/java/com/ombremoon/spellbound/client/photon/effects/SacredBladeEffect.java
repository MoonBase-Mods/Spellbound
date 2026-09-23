package com.ombremoon.spellbound.client.photon.effects;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.common.world.entity.projectile.SacredBlade;
import com.ombremoon.spellbound.common.world.entity.spell.Fireball;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class SacredBladeEffect extends EntityEffectExecutor {
    public SacredBladeEffect(FX fx, Level level, SacredBlade sacredBlade) {
        super(fx, level, sacredBlade, AutoRotate.LOOK);
    }

    @Override
    public void updateFXObjectFrame(IFXObject fxObject, float partialTicks) {
        super.updateFXObjectFrame(fxObject, partialTicks);
        if (runtime != null && fxObject == runtime.root) {
            var sacredBlade = (SacredBlade) entity;
            float size = sacredBlade.getSize();
            runtime.root.updateScale(new Vector3f(size, 1, size));
        }
    }

    public static class SacredBladeBuilder extends EffectBuilder<SacredBladeEffect> {
        public static final ResourceLocation LOCATION = CommonClass.customLocation("smite_projectle");
        private final int entityId;

        SacredBladeBuilder(ResourceLocation location, int entityId) {
            super(location);
            this.entityId = entityId;
        }

        public static SacredBladeBuilder of(int entityId) {
            return new SacredBladeBuilder(LOCATION, entityId);
        }

        @Override
        public SacredBladeEffect build() {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                var fx = FXHelper.getFX(this.location);
                if (fx != null) {
                    var entity = level.getEntity(this.entityId);
                    if (entity instanceof SacredBlade sacredBlade) {
                        var effect = new SacredBladeEffect(fx, level, sacredBlade);
                            effect.setRotation(180, 180, 0);
                            effect.setOffset(0.5, -0.25, 0);
                        return effect;
                    }
                }
            }

            return null;
        }
    }
}
