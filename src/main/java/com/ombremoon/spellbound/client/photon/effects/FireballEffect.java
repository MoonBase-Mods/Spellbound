package com.ombremoon.spellbound.client.photon.effects;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import com.ombremoon.spellbound.client.photon.EffectBuilder;
import com.ombremoon.spellbound.common.world.entity.spell.Fireball;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
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

    public static class FireballBuilder extends EffectBuilder<FireballEffect> {
        public static final ResourceLocation LOCATION = CommonClass.customLocation("fireball");
        private final int entityId;

        FireballBuilder(ResourceLocation location, int entityId) {
            super(location);
            this.entityId = entityId;
        }

        public static FireballBuilder of(int entityId) {
            return new FireballBuilder(LOCATION, entityId);
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
