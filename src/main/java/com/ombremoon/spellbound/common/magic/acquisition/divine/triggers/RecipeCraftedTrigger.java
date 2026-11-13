package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class RecipeCraftedTrigger extends SimpleTrigger<RecipeCraftedTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ResourceLocation recipeId, List<ItemStack> items) {
        this.trigger(player, instance -> instance.matches(recipeId, items));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, ResourceLocation recipeId, List<ItemPredicate> ingredients) implements Instance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                                ResourceLocation.CODEC.fieldOf("recipe_id").forGetter(TriggerInstance::recipeId),
                                ItemPredicate.CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(TriggerInstance::ingredients)
                        )
                        .apply(instance, TriggerInstance::new)
        );

        public static ActionCriterion<TriggerInstance> craftedItem(ResourceLocation recipeId, List<ItemPredicate.Builder> ingredients) {
            return SBTriggers.RECIPE_CRAFTED.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), recipeId, ingredients.stream().map(ItemPredicate.Builder::build).toList()));
        }

        public static ActionCriterion<TriggerInstance> craftedItem(ResourceLocation recipeId) {
            return SBTriggers.RECIPE_CRAFTED.get().createCriterion(new TriggerInstance(Optional.empty(), recipeId, List.of()));
        }

        boolean matches(ResourceLocation recipeId, List<ItemStack> items) {
            if (!recipeId.equals(this.recipeId)) {
                return false;
            } else {
                List<ItemStack> list = new ArrayList<>(items);

                for (ItemPredicate itempredicate : this.ingredients) {
                    boolean flag = false;
                    Iterator<ItemStack> iterator = list.iterator();

                    while (iterator.hasNext()) {
                        if (itempredicate.test(iterator.next())) {
                            iterator.remove();
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }

                return true;
            }
        }
    }
}
