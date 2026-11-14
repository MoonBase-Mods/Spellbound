package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.SBEntities;
import com.ombremoon.spellbound.common.magic.effects.EffectHolder;
import com.ombremoon.spellbound.common.world.block.RuneBlock;
import com.ombremoon.spellbound.common.world.block.entity.RuneBlockEntity;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.world.entity.spell.CursedRune;
import com.ombremoon.spellbound.util.SpellUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class ChalkItem extends BlockItem {

    public ChalkItem(Properties properties) {
        super(SBBlocks.RUNE.get(), properties.durability(59));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        List<EffectHolder> effects = stack.get(SBData.RUNE_EFFECTS);
        if (effects != null && !effects.isEmpty()) {
            if (!level.isClientSide) {
                CursedRune rune = SBEntities.CURSED_RUNE.get().create(level);
                SpellUtil.setOwner(rune, player);
                rune.setPos(context.getClickLocation());
                rune.setRuneEffects(effects);
                level.addFreshEntity(rune);
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            InteractionResult interactionresult = this.place(new BlockPlaceContext(context));
            if (!interactionresult.consumesAction() && context.getItemInHand().has(DataComponents.FOOD)) {
                InteractionResult interactionresult1 = super.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
                return interactionresult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : interactionresult1;
            } else {
                return interactionresult;
            }
        }
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if (!this.getBlock().isEnabled(context.getLevel().enabledFeatures())) {
            return InteractionResult.FAIL;
        } else if (!context.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockplacecontext = this.updatePlacementContext(context);
            if (blockplacecontext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockstate = this.getPlacementState(blockplacecontext);
                if (blockstate == null) {
                    return InteractionResult.FAIL;
                } else if (!this.placeBlock(blockplacecontext, blockstate)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockpos = blockplacecontext.getClickedPos();
                    Level level = blockplacecontext.getLevel();
                    Player player = blockplacecontext.getPlayer();
                    ItemStack itemstack = blockplacecontext.getItemInHand();
                    BlockState blockstate1 = level.getBlockState(blockpos);
                    if (blockstate1.is(blockstate.getBlock())) {
                        blockstate1 = this.updateBlockStateFromTag(blockpos, level, itemstack, blockstate1);
                        this.updateCustomBlockEntityTag(blockpos, level, player, itemstack, blockstate1);
                        updateBlockEntityComponents(level, blockpos, itemstack);
                        blockstate1.getBlock().setPlacedBy(level, blockpos, blockstate1, player, itemstack);
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
                        }
                    }

                    BlockEntity blockEntity = level.getBlockEntity(blockpos);
                    if (blockEntity instanceof RuneBlockEntity runeBlock) {
                        DyedItemColor color = itemstack.get(DataComponents.DYED_COLOR);
                        if (color != null) {
                            runeBlock.setData(SBData.RUNE_COLOR, color.rgb());
                        }
                    }

                    SoundType soundtype = blockstate1.getSoundType(level, blockpos, context.getPlayer());
                    level.playSound(
                            player,
                            blockpos,
                            this.getPlaceSound(blockstate1, level, blockpos, context.getPlayer()),
                            SoundSource.BLOCKS,
                            (soundtype.getVolume() + 1.0F) / 2.0F,
                            soundtype.getPitch() * 0.8F
                    );
                    level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(player, blockstate1));
                    itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.empty());
        List<EffectHolder> effects = stack.get(SBData.RUNE_EFFECTS);
        if (effects != null && !effects.isEmpty()) {
            if (tooltipFlag.hasShiftDown()) {
                for (EffectHolder effect : effects) {
                    if (effect.name().isPresent()) {
                        ResourceLocation name = effect.name().get();
                        tooltipComponents.add(Component.literal("- ").append(Component.translatable("rune." + name.getNamespace() + "." + name.getPath())));
                    }
                }
            } else {
                tooltipComponents.add(Component.translatable("tooltip.spellbound.holdshift"));
            }
        }
    }

    private static void updateBlockEntityComponents(Level level, BlockPos poa, ItemStack stack) {
        BlockEntity blockentity = level.getBlockEntity(poa);
        if (blockentity != null) {
            blockentity.applyComponentsFromItemStack(stack);
            blockentity.setChanged();
        }
    }

    private BlockState updateBlockStateFromTag(BlockPos pos, Level level, ItemStack stack, BlockState state) {
        BlockItemStateProperties blockitemstateproperties = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
        if (blockitemstateproperties.isEmpty()) {
            return state;
        } else {
            BlockState blockstate = blockitemstateproperties.apply(state);
            if (blockstate != state) {
                level.setBlock(pos, blockstate, 2);
            }

            return blockstate;
        }
    }

    @Override
    public Block getBlock() {
        return SBBlocks.RUNE.get();
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        int type = this.getRuneType(stack);
        BlockState blockState = this.getBlock().defaultBlockState().setValue(RuneBlock.RUNE_TYPE, type);
        return this.canPlace(context, blockState) ? blockState : null;
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    private int getRuneType(ItemStack stack) {
        int[] runes = getOrCreateRunes(stack);
        var type = stack.get(SBData.RUNE_INDEX);

        if (type == null || type >= 26) {
            shuffle(runes);
            type = 0;
        }

        int rune = runes[type];
        stack.set(SBData.RUNE_INDEX, type + 1);
        return rune;
    }

    private static int[] getOrCreateRunes(ItemStack stack) {
        var list = stack.get(SBData.RUNES);
        if (list == null) {
            int[] newRunes = IntStream.rangeClosed(1, 26).toArray();
            shuffle(newRunes);
            stack.set(SBData.RUNES, new IntArrayList(newRunes));
            return newRunes;
        }

        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    private static void shuffle(int[] array) {
        RandomSource random = RandomSource.create();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
