package com.ombremoon.spellbound.common.magic.api;

import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.skills.Skill;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

public interface ChargeableSpell {

    int maxCharges();

    boolean canCharge(SpellContext context);
}
