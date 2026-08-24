package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.world.sound.SpellboundSounds;
import com.ombremoon.spellbound.util.SpellUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class SpiritWhistleItem extends Item {
    public SpiritWhistleItem(Properties properties) {
        super(properties.durability(100));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        summon(player, player.blockPosition());
        return super.use(level, player, usedHand);
    }

    private void summon(Player player, BlockPos pos) {
        var handler = SpellUtil.getFamiliarHandler(player);
        Level level = player.level();
        float volume = 0.01F + level.random.nextFloat() * 0.05F;
        float pitch = 0.8F + level.random.nextFloat() * 0.2F;

        if (handler.hasActiveFamiliar()) {
            level.playSound(null, player.blockPosition(), SpellboundSounds.WHISTLE_OFF.get(),
                    SoundSource.PLAYERS, volume, pitch);
            handler.discardFamiliar();
            return;
        }
        else{
            level.playSound(null, player.blockPosition(), SpellboundSounds.WHISTLE_ON.get(),
                    SoundSource.PLAYERS, volume, pitch);
        }

        handler.summonFamiliar(pos);
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }
}
