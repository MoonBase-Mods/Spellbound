package com.ombremoon.spellbound.common.magic.api.combat;

import com.ombremoon.spellbound.common.magic.api.Imbuement;
import com.ombremoon.spellbound.common.magic.api.SpellAnimation;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlayerAttackController {
    private Player player;
    private final Map<AnimationRule, Boolean> rules = new Object2ObjectArrayMap<>();
    private int comboCounter;

    public boolean canExecuteAttack() {
        return !this.player.isSpectator() && this.checkAnimationRule(AnimationRule.CAN_ATTACK);
    }

    public void attack() {
        Imbuement imbuement = Imbuement.getPriorityImbuement(this.player);
        if (this.player.isPassenger()) {

        } else {
            /*Optional<AttackMoveset> optional = imbuement.moveset();
            if (optional.isEmpty()) {
                //Default player attack
                return;
            }

            AttackMoveset moveset = optional.get();
            int size = moveset.size();
            SpellAnimation animation = moveset.getAnimation(Math.abs(comboCounter) % size);
            this.comboCounter = (this.comboCounter + 1) % size;

            if (animation != null) {
                //Play attack animation
            }*/
        }
    }

    public boolean checkAnimationRule(AnimationRule rule) {
        return this.rules.getOrDefault(rule, rule.defaultRule());
    }
}
