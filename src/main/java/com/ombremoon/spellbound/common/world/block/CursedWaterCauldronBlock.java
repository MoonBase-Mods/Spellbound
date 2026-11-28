package com.ombremoon.spellbound.common.world.block;

import com.mojang.serialization.MapCodec;
import com.ombremoon.spellbound.common.DamageInstance;
import com.ombremoon.spellbound.common.init.*;
import com.ombremoon.spellbound.common.magic.api.Imbuement;
import com.ombremoon.spellbound.common.world.item.PhialItem;
import com.ombremoon.spellbound.common.world.spell.divine.SmiteSpell;
import com.ombremoon.spellbound.main.CommonClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
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

public class CursedWaterCauldronBlock extends LayeredCauldronBlock {
    public static final MapCodec<LayeredCauldronBlock> CODEC = simpleCodec(CursedWaterCauldronBlock::new);
    private static final CauldronInteraction.InteractionMap CURSED_WATER = CauldronInteraction.newInteractionMap("spellbound:cursed_water");
    private static final CauldronInteraction FILL_CURSED_WATER = (state, level, pos, player, hand, stack) -> CauldronInteraction.emptyBucket(
            level,
            pos,
            player,
            hand,
            stack,
            SBBlocks.CURSED_WATER_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3),
            SoundEvents.BUCKET_EMPTY
    );

    @Override
    public MapCodec<LayeredCauldronBlock> codec() {
        return CODEC;
    }

    public CursedWaterCauldronBlock(Properties properties) {
        super(Biome.Precipitation.NONE, CURSED_WATER, properties);
    }

    public static void createInteractions() {
        var emptyMap = CauldronInteraction.EMPTY.map();
        emptyMap.put(SBItems.CURSED_WATER_BUCKET.get(), FILL_CURSED_WATER);

        var map = CURSED_WATER.map();
        CauldronInteraction.addDefaultInteractions(map);
        map.put(
                Items.BUCKET,
                (state, level, pos, player, hand, stack) -> {
                    if (player instanceof ServerPlayer serverPlayer) {
                        SBTriggers.USE_HOLY_CAULDRON.get().trigger(serverPlayer, stack, PhialItem.PhialType.CURSED);
                    }

                    return CauldronInteraction.fillBucket(
                            state,
                            level,
                            pos,
                            player,
                            hand,
                            stack,
                            new ItemStack(SBItems.CURSED_WATER_BUCKET.get()),
                            blockState -> blockState.getValue(LayeredCauldronBlock.LEVEL) == 3,
                            SoundEvents.BUCKET_FILL
                    );
                }
        );
        map.put(
                Items.GLASS_BOTTLE,
                (state, level, pos, player, hand, stack) -> {
                    if (!level.isClientSide) {
                        if (player instanceof ServerPlayer serverPlayer) {
                            SBTriggers.USE_HOLY_CAULDRON.get().trigger(serverPlayer, stack, PhialItem.PhialType.CURSED);
                        }

                        Item item = stack.getItem();
                        player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(SBItems.CURSED_PHIAL.get())));
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
                SBItems.CURSED_PHIAL.get(),
                (state, level, pos, player, hand, stack) -> {
                    if (state.getValue(LayeredCauldronBlock.LEVEL) == 3) {
                        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                    } else {
                        if (!level.isClientSide) {
                            if (player instanceof ServerPlayer serverPlayer) {
                                SBTriggers.USE_HOLY_CAULDRON.get().trigger(serverPlayer, stack, PhialItem.PhialType.CURSED);
                            }

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

        var weapons = BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.WEAPON_ENCHANTABLE);
        for (var weapon : weapons) {
            map.put(
                    weapon.value(),
                    (state, level, pos, player, hand, stack) -> {
                        Imbuement imbuement = stack.get(SBData.IMBUEMENT);
                        if (imbuement != null && !imbuement.canImbueStack(stack)) {
                            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                        } else {
                            if (!level.isClientSide) {
                                if (player instanceof ServerPlayer serverPlayer) {
                                    SBTriggers.USE_HOLY_CAULDRON.get().trigger(serverPlayer, stack, PhialItem.PhialType.CURSED);
                                }

                                imbuement = Imbuement.create()
                                        .addDamage(DamageInstance.of(SBDamageTypes.SB_GENERIC, 3F))
                                        .addCharges(24)
                                        .addGlint(SmiteSpell.BLACK_BLADE);
                                stack.set(SBData.IMBUEMENT, imbuement);
                                player.awardStat(SBStats.WEAPONS_IMBUED.get());
                                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
                            }

                            return ItemInteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
            );
        }
    }
}
