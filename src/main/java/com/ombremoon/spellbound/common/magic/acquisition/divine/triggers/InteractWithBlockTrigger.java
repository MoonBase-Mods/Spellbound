package com.ombremoon.spellbound.common.magic.acquisition.divine.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.init.SBTriggers;
import com.ombremoon.spellbound.common.magic.acquisition.divine.ActionCriterion;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Optional;

public class InteractWithBlockTrigger extends SimpleTrigger<InteractWithBlockTrigger.Instance> {
    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, BlockPos pos, ItemStack stack) {
        ServerLevel serverlevel = player.serverLevel();
        BlockState blockstate = serverlevel.getBlockState(pos);
        LootParams lootparams = new LootParams.Builder(serverlevel)
                .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.BLOCK_STATE, blockstate)
                .withParameter(LootContextParams.TOOL, stack)
                .create(LootContextParamSets.ADVANCEMENT_LOCATION);
        LootContext lootcontext = new LootContext.Builder(lootparams).create(Optional.empty());
        BlockInWorld block = new BlockInWorld(serverlevel, pos, false);
        this.trigger(player, instance -> instance.matches(lootcontext, block));
    }

    public record Instance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> location, Optional<BlockPredicate> block) implements SimpleTrigger.Instance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(InteractWithBlockTrigger.Instance::player),
                        ContextAwarePredicate.CODEC.optionalFieldOf("location").forGetter(InteractWithBlockTrigger.Instance::location),
                        BlockPredicate.CODEC.optionalFieldOf("block").forGetter(InteractWithBlockTrigger.Instance::block)
                    )
                    .apply(instance, InteractWithBlockTrigger.Instance::new)
        );

        public static ActionCriterion<Instance> interactedWithBlock(BlockPredicate blockPredicate) {
            return SBTriggers.INTERACT_WITH_BLOCK.get().createCriterion(new Instance(Optional.empty(), Optional.empty(), Optional.of(blockPredicate)));
        }

        public boolean matches(LootContext context, BlockInWorld block) {
            return (this.location.isEmpty() || this.location.get().matches(context)) && (this.block.isEmpty() || this.block.get().matches(block));
        }

        @Override
        public void validate(CriterionValidator validator) {
            SimpleTrigger.Instance.super.validate(validator);
            this.location.ifPresent(p_320455_ -> validator.validate(p_320455_, LootContextParamSets.ADVANCEMENT_LOCATION, ".location"));
        }
    }
}
