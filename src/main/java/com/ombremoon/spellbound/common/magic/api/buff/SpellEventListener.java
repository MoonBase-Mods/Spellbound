package com.ombremoon.spellbound.common.magic.api.buff;

import com.ombremoon.spellbound.common.magic.SpellContext;
import com.ombremoon.spellbound.common.magic.api.AbstractSpell;
import com.ombremoon.spellbound.common.magic.api.events.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Utility class used for listening to events from the {@link AbstractSpell}'s class. This allows spells to easily interact with player events without the need to clutter a whole class filled with events for different spells.
 */
public class SpellEventListener {
    public final Map<IEvent<?>, List<EventInstance<? extends SpellEvent>>> events = new Object2ObjectOpenHashMap<>();
    private final LivingEntity listener;

    public SpellEventListener(LivingEntity listener) {
        this.listener = listener;
    }

    /**
     * Checks if the player is currently listening to an event
     * @param event The event
     * @param location The event resource location
     * @return Whether the event listener is present
     */
    public boolean hasListener(IEvent<?> event, ResourceLocation location) {
        if (this.events.containsKey(event)) {
            for (var instance : this.events.get(event)) {
                if (instance.location().equals(location)) return true;
            }
        }
        return false;
    }

    /**
     * Adds an event listener to the player. Should be called from {@link AbstractSpell#onSpellStart(SpellContext)}.
     * @param event The event
     * @param location The event resource location
     * @param consumer The action to take place when the event is fired
     * @param <T> The spells event class
     */
    public <T extends SpellEvent> void addListener(IEvent<T> event, ResourceLocation location, Consumer<T> consumer) {
        if (checkSide(event)) return;
        var list = refreshListeners(event, location, consumer);
        this.events.put(event, list);
    }

    /**
     * Removes an event listener from the player. If there are multiple event listeners with the same id, will remove the first listener in the list. Should be called from {@link AbstractSpell#onSpellStop(SpellContext)}.
     * @param event The event
     * @param location The event resource location
     */
    public void removeListener(IEvent<?> event, ResourceLocation location) {
        if (hasListener(event, location)) {
            var instances = this.events.get(event);
            for (var instance : instances) {
                if (instance.location().equals(location)) {
                    instances.remove(instance);
                    if (!instances.isEmpty()) {
                        this.events.replace(event, instances);
                    } else {
                        this.events.remove(event);
                    }
                    break;
                }
            }
        }
    }

    public void removeListener(ResourceLocation location) {
        for (var entry : this.events.entrySet()) {
            if (this.hasListener(entry.getKey(), location)) {
                this.removeListener(entry.getKey(), location);
                break;
            }
        }
    }

    /**
     * Checks if the event is being fired from the correct side.
     * @param event The event
     * @return Whether the event is being fired on the proper side
     */
    private boolean checkSide(IEvent<?> event) {
        return this.listener.level().isClientSide != event.isClientSide();
    }

    /**
     * Adds an event instance to the list of events.
     * @param event The event
     * @param instance The event instance
     * @return The list of event instances for the specific event
     * @param <T> The spells event class
     */
    private <T extends SpellEvent> List<EventInstance<? extends SpellEvent>> refreshListeners(IEvent<T> event, EventInstance<T> instance) {
        var list = this.events.get(event);
        if (list == null) list = new ObjectArrayList<>();
        list.add(instance);
        return list;
    }

    private <T extends SpellEvent> List<EventInstance<? extends SpellEvent>> refreshListeners(IEvent<T> event, ResourceLocation location, Consumer<T> consumer) {
        return refreshListeners(event, new EventInstance<>(event, location, consumer));
    }

    /**
     * Accepts the event consumer given the event is on the proper side and isn't cancelled/
     * @param event The event
     * @param spellEvent The spells event instance
     * @return If the event was actually fired
     * @param <T> The spells event class
     */
    @SuppressWarnings("unchecked")
    public <T extends SpellEvent> boolean fireEvent(IEvent<?> event, T spellEvent) {
        if (checkSide(event)) return false;

        var instances = this.events.get(event);
        boolean flag = false;
        if (instances != null) {
            for (var instance : instances) {
                var consumer = (Consumer<T>) instance.spellConsumer();
                consumer.accept(spellEvent);
                flag |= spellEvent.isCancelled();
            }
        }
        return flag;
    }

    public static class Events<T extends SpellEvent> implements IEvent<T> {
        public static Events<CastSpellEvent> CAST_SPELL = new Events<>(false);
        public static Events<JumpEvent> JUMP = new Events<>(false);
        public static Events<PlayerAttackEvent> ATTACK = new Events<>(false);
        public static Events<DamageEvent.Post> POST_DAMAGE = new Events<>(false);
        public static Events<DamageEvent.Pre> PRE_DAMAGE = new Events<>(false);
        public static Events<DealtDamageEvent.Post> DEALT_DAMAGE_POST = new Events<>(false);
        public static Events<DealtDamageEvent.Pre> DEALT_DAMAGE_PRE = new Events<>(false);
        public static Events<EffectApplicableEvent> EFFECT_APPLICABLE = new Events<>(false);
        public static Events<LivingBlockEvent> BLOCK = new Events<>(false);
        public static Events<DeathEvent> DEATH = new Events<>(false);
        public static Events<DeathEvent> ENTITY_KILL = new Events<>(false);
        public static Events<BlockItemDropsEvent> BLOCK_DROPS = new Events<>(false);
        public static Events<MouseInputEvent.Pre> PRE_MOUSE_INPUT = new Events<>(true);
        public static Events<MouseInputEvent.Post> POST_MOUSE_INPUT = new Events<>(true);

        private final boolean isClientSide;

        Events(boolean isClientSide) {
            this.isClientSide = isClientSide;
        }

        @Override
        public boolean isClientSide() {
            return this.isClientSide;
        }
    }

    public interface IEvent<T extends SpellEvent> {
        boolean isClientSide();
    }

    public record EventInstance<T extends SpellEvent>(IEvent<T> event, ResourceLocation location, Consumer<T> spellConsumer) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else {
                return obj instanceof EventInstance<?> eventInstance && this.location.equals(eventInstance.location);
            }
        }
    }
}
