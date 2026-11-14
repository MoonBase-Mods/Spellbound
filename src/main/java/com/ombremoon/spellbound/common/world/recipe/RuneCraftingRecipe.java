package com.ombremoon.spellbound.common.world.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBRecipes;
import com.ombremoon.spellbound.common.magic.effects.EffectHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;

import java.util.List;

public class RuneCraftingRecipe implements Recipe<RuneCraftingInput> {
    final EffectHolder effect;
    final NonNullList<Ingredient> items;

    public RuneCraftingRecipe(EffectHolder effect, NonNullList<Ingredient> items) {
        this.effect = effect;
        this.items = items;
    }

    @Override
    public boolean matches(RuneCraftingInput input, Level level) {
        if (input.ingredientCount() != this.items.size()) {
            return false;
        } else {
            var nonEmptyItems = new java.util.ArrayList<ItemStack>(input.ingredientCount());
            for (var item : input.items())
                if (!item.isEmpty())
                    nonEmptyItems.add(item);
            return RecipeMatcher.findMatches(nonEmptyItems, this.items) != null;
        }
    }

    @Override
    public ItemStack assemble(RuneCraftingInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    public ItemStack assemble(HolderLookup.Provider registries, ResourceLocation name) {
        ItemStack chalk = this.getResultItem(registries);
        EffectHolder namedEffect = this.effect.named(name);
        chalk.set(SBData.RUNE_EFFECTS, List.of(namedEffect));
        return chalk;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(SBItems.CHALK.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SBRecipes.RUNE_CRAFTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SBRecipes.RUNE_RECIPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.items;
    }

    public EffectHolder getEffect() {
        return this.effect;
    }

    public static class Serializer implements RecipeSerializer<RuneCraftingRecipe> {
        private static final MapCodec<RuneCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        EffectHolder.CODEC.fieldOf("effect").forGetter(RuneCraftingRecipe::getEffect),
                        Ingredient.CODEC_NONEMPTY
                                .listOf()
                                .fieldOf("ingredients")
                                .flatXmap(
                                        ingredients -> {
                                            Ingredient[] aingredient = ingredients.toArray(Ingredient[]::new); // Neo skip the empty check and immediately create the array.
                                            if (aingredient.length == 0) {
                                                return DataResult.error(() -> "No ingredients for rune crafting recipe");
                                            } else {
                                                return aingredient.length > 4
                                                        ? DataResult.error(() -> "Too many ingredients for rune crafting recipe. The maximum is: %s".formatted(4))
                                                        : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                                            }
                                        },
                                        DataResult::success
                                )
                                .forGetter(RuneCraftingRecipe::getIngredients)
                ).apply(instance, RuneCraftingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, RuneCraftingRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork
        );

        @Override
        public MapCodec<RuneCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RuneCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static RuneCraftingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
            nonnulllist.replaceAll(p_319735_ -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            EffectHolder effect = EffectHolder.STREAM_CODEC.decode(buffer);
            return new RuneCraftingRecipe(effect, nonnulllist);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, RuneCraftingRecipe recipe) {
            buffer.writeVarInt(recipe.items.size());

            for (Ingredient ingredient : recipe.items) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }

            EffectHolder.STREAM_CODEC.encode(buffer, recipe.effect);
        }
    }
}
