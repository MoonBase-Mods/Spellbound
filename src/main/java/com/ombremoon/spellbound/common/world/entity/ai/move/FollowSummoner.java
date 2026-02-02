package com.ombremoon.spellbound.common.world.entity.ai.move;

import com.ombremoon.spellbound.common.world.entity.SmartSpellEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowEntity;
import net.tslat.smartbrainlib.util.BrainUtils;

public class FollowSummoner<E extends SmartSpellEntity<?>> extends FollowEntity<E, Entity> {
    protected Entity owner = null;

    public FollowSummoner() {
        following(this::getOwner);
        teleportToTargetAfter((livingEntity, entity) -> BrainUtils.getTargetOfEntity(livingEntity) != null ? 100.0 : 12.0);
        startCondition(entity -> entity.getSummoner() instanceof Player /*or instanceof SpellCaster*/ && BrainUtils.getTargetOfEntity(entity) == null);
    }

    protected Entity getOwner(E entity) {
        if (this.owner != null && (this.owner.isRemoved() || !this.owner.getUUID().equals(entity.getSummoner().getUUID())))
            this.owner = null;

        if (this.owner == null)
            this.owner = entity.getSummoner();

        return this.owner;
    }
}
