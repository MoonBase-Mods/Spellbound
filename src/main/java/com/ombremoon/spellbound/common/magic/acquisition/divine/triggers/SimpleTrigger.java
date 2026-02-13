package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionTrigger;
import com.ombremoon.spellbound.common.magic.acquisition.divine.PlayerSpellActions;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public abstract class SimpleTrigger<T extends SimpleTrigger.Instance> implements ActionTrigger<T> {
    private final Map<PlayerSpellActions, Set<ActionTrigger.Listener<T>>> players = Maps.newIdentityHashMap();

    @Override
    public void addPlayerListener(PlayerSpellActions playerActions, Listener<T> listener) {
        this.players.computeIfAbsent(playerActions, playerSpellActions -> Sets.newHashSet()).add(listener);
    }

    @Override
    public void removePlayerListener(PlayerSpellActions playerActions, Listener<T> listener) {
        var set = this.players.get(playerActions);
        if (set != null) {
            set.remove(listener);
            if (set.isEmpty())
                this.players.remove(playerActions);
        }
    }

    @Override
    public void removePlayerListener(PlayerSpellActions playerActions) {
        this.players.remove(playerActions);
    }

    protected void trigger(ServerPlayer player, Predicate<T> testTrigger) {
        var handler = SpellUtil.getSpellHandler(player);
        PlayerSpellActions divineActions = handler.getSpellActions();
        var set = this.players.get(divineActions);
        if (set != null && !set.isEmpty()) {
            LootContext context = EntityPredicate.createContext(player, player);
            List<ActionTrigger.Listener<T>> list = null;

            for (var listener : set) {
                T t = listener.trigger();
                if (testTrigger.test(t)) {
                    var optional = t.player();
                    if (optional.isEmpty() || optional.get().matches(context)) {
                        if (list == null)
                            list = Lists.newArrayList();

                        list.add(listener);
                    }
                }
            }

            if (list != null) {
                for (var listener : list) {
                    listener.run(divineActions);
                }
            }
        }
    }

    public interface Instance extends CriterionTriggerInstance {
        @Override
        default void validate(CriterionValidator validator) {
            validator.validateEntity(this.player(), ".player");
        }

        Optional<ContextAwarePredicate> player();
    }
}
