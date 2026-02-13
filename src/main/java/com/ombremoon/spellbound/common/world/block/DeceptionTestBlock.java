package com.ombremoon.spellbound.common.world.block;

import com.ombremoon.spellbound.common.init.SBData;
import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.ArenaSavedData;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleConfiguration;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleDungeonData;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import com.ombremoon.spellbound.common.world.dimension.DynamicDimensionFactory;
import com.ombremoon.spellbound.main.Keys;
import com.ombremoon.spellbound.mixin.ConnectionAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DeceptionTestBlock extends Block {
    public DeceptionTestBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(SBItems.DUNGEON_KEY.get())) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            } else {
                this.sendToDungeon(stack, state, level, pos, player);
                return ItemInteractionResult.CONSUME;
            }
        }

        return ItemInteractionResult.FAIL;
    }

    private void sendToDungeon(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player) {
        MinecraftServer server = level.getServer();
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        PuzzleDungeonData data = PuzzleDungeonData.get(overworld);
        int dungeonId = data.incrementId();
        for (var serverPlayer : server.getPlayerList().getPlayers()) {
            ((ConnectionAccessor) serverPlayer.connection.getConnection()).invokeFlush();
        }
        ResourceKey<Level> levelKey = data.getOrCreateKey(server, dungeonId);
        ServerLevel dungeon = DynamicDimensionFactory.getOrCreateDimension(server, levelKey);
//        SpellType<?> spell = stack.get(SBData.DUNGEON_SPELL);
        SpellType<?> spell = SBSpells.FLICKER.get();
        if (dungeon != null/* && spell != null*/) {
            PuzzleDungeonData dungeonData = PuzzleDungeonData.get(dungeon);
            ResourceKey<PuzzleConfiguration> configKey = ResourceKey.create(Keys.PUZZLE_CONFIG, spell.location());
            PuzzleConfiguration config = level.registryAccess().registryOrThrow(Keys.PUZZLE_CONFIG).getOrThrow(configKey);
            dungeonData.initializeDungeon(player, configKey, config);
        }

        if (!player.getAbilities().instabuild)
            stack.shrink(1);
    }
}
