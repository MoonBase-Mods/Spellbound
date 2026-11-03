package com.ombremoon.spellbound.common.world.block;

import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class HolyWaterCauldronBlock extends LayeredCauldronBlock {
    public static final MapCodec<LayeredCauldronBlock> CODEC = simpleCodec(HolyWaterCauldronBlock::new);
    private static final CauldronInteraction.InteractionMap HOLY_WATER = CauldronInteraction.newInteractionMap("spellbound:holy_water");
    private static final CauldronInteraction FILL_HOLY_WATER = (state, level, pos, player, hand, stack) -> CauldronInteraction.emptyBucket(
            level,
            pos,
            player,
            hand,
            stack,
            SBBlocks.HOLY_WATER_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3),
            SoundEvents.BUCKET_EMPTY
    );

    @Override
    public MapCodec<LayeredCauldronBlock> codec() {
        return CODEC;
    }

    public HolyWaterCauldronBlock(Properties properties) {
        super(Biome.Precipitation.NONE, HOLY_WATER, properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!entity.getType().is(EntityTypeTags.UNDEAD)) {
            super.entityInside(state, level, pos, entity);
        } else if (this.isEntityInsideContent(state, pos, entity)) {
            this.hurtUndead(entity);
        }
    }

    private void hurtUndead(Entity entity) {
        entity.igniteForSeconds(7.5F);
        if (entity.hurt(entity.damageSources().inFire(), 2.0F)) {
            entity.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + entity.getRandom().nextFloat() * 0.4F);
        }
    }

    public static void createInteractions() {
        var emptyMap = CauldronInteraction.EMPTY.map();
        emptyMap.put(SBItems.HOLY_WATER_BUCKET.get(), FILL_HOLY_WATER);

        var map = HOLY_WATER.map();
        CauldronInteraction.addDefaultInteractions(map);
        map.put(
                Items.BUCKET,
                (state, level, pos, player, hand, stack) -> CauldronInteraction.fillBucket(
                        state,
                        level,
                        pos,
                        player,
                        hand,
                        stack,
                        new ItemStack(SBItems.HOLY_WATER_BUCKET.get()),
                        blockState -> blockState.getValue(LayeredCauldronBlock.LEVEL) == 3,
                        SoundEvents.BUCKET_FILL
                )
        );
        map.put(
                Items.GLASS_BOTTLE,
                (state, level, pos, player, hand, stack) -> {
                    if (!level.isClientSide) {
                        Item item = stack.getItem();
                        player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(SBItems.DIVINE_PHIAL.get())));
                        player.awardStat(Stats.USE_CAULDRON);
                        player.awardStat(Stats.ITEM_USED.get(item));
                        LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
                    }

                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
        );
        map.put(
                SBItems.DIVINE_PHIAL.get(),
                (state, level, pos, player, hand, stack) -> {
                    if (state.getValue(LayeredCauldronBlock.LEVEL) == 3) {
                        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                    } else {
                        if (!level.isClientSide) {
                            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                            player.awardStat(Stats.USE_CAULDRON);
                            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                            level.setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
                            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
                        }

                        return ItemInteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
        );
        map.put(
                Items.LEATHER,
                (state, level, pos, player, hand, stack) -> {
                    if (!level.isClientSide) {
                        Item item = stack.getItem();
                        player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(SBItems.BLESSED_BANDAGES.get())));
                        player.awardStat(Stats.USE_CAULDRON);
                        player.awardStat(Stats.ITEM_USED.get(item));
                        LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
                    }

                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
        );
    }
}
