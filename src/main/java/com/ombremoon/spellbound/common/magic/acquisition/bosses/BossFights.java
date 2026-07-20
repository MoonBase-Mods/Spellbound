package com.ombremoon.spellbound.common.magic.acquisition.bosses;

import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.world.phys.Vec3;

public class BossFights {

    public static final EntityBasedBossFight.Builder WILD_MUSHROOM = EntityBasedBossFight.newBuilder()
            .spell(CommonClass.customLocation("wild_mushroom"))
            .withBoss(SBEntities.GIANT_MUSHROOM, 35, 72, 35)
            .spawnData(
                    DynamicLevelSpawnData.Builder.create()
                            .playerOffset(new Vec3(8, 64, 35))
                            .playerRotation(-90F)
                            .spellOffset(new Vec3(35, 72, 35))
            );
}
