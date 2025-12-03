package com.ombremoon.spellbound.common.magic.acquisition.bosses;

import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.main.CommonClass;

public class BossFights {

    public static final EntityBasedBossFight.Builder WILD_MUSHROOM = EntityBasedBossFight.newBuilder()
            .spell(CommonClass.customLocation("wild_mushroom"))
            .withBoss(SBEntities.GIANT_MUSHROOM)
            .scanTo(-100, 70, 100)
            .spawnAt(-36, 65, 23);
}
