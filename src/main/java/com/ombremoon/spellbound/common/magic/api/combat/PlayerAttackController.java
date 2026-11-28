package com.ombremoon.spellbound.common.magic.api.combat;

import com.ombremoon.spellbound.common.magic.api.Imbuement;
import com.ombremoon.spellbound.common.magic.api.SBAnimation;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class PlayerAttackController {
    private Player player;
    private int comboCounter;

//    public boolean canExecuteAttack() {
//        PlayerAnimData animData;
//        return !this.player.isSpectator() && animData.checkAnimationRule(AnimationRule.CAN_ATTACK);
//    }

    public void attack() {
        Imbuement imbuement = Imbuement.getPriorityImbuement(this.player);
        if (this.player.isPassenger()) {

        } else {
            Optional<AttackMoveset> optional = imbuement.moveset();
            if (optional.isEmpty()) {
                //Default player attack
                return;
            }

            AttackMoveset moveset = optional.get();
            int size = moveset.size();
            SBAnimation animation = moveset.getAnimation(Math.abs(comboCounter) % size);
            this.comboCounter = (this.comboCounter + 1) % size;

            if (animation != null) {
                //Play attack animation
            }
        }
    }
}
