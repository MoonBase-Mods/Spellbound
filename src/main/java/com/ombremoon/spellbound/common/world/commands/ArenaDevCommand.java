package com.ombremoon.spellbound.common.world.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.ombremoon.spellbound.common.init.SBBlocks;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.acquisition.bosses.ArenaSavedData;
import com.ombremoon.spellbound.common.magic.acquisition.deception.PuzzleDungeonData;
import com.ombremoon.spellbound.common.world.SpellDimensionData;
import com.ombremoon.spellbound.common.world.block.SummonStoneBlock;
import com.ombremoon.spellbound.common.world.block.entity.SummonPortalBlockEntity;
import com.ombremoon.spellbound.common.world.dimension.DimensionCreator;
import com.ombremoon.spellbound.common.world.dimension.DynamicDimensionFactory;
import com.ombremoon.spellbound.networking.PayloadHandler;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

public class ArenaDevCommand {

    public ArenaDevCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("spell_dimension")
                .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("arena")
                                .then(Commands.literal("spawn")
                                        .executes(ctx -> spawnPortal(ctx.getSource())))
                                .then(Commands.literal("remove")
                                        .executes(ctx -> removeArena(ctx.getSource())))
                                .then(Commands.literal("reset")
                                        .executes(ctx -> resetAll(ctx.getSource())))
                                .then(Commands.literal("bounds")
                                        .executes(ctx -> toggleArenaBounds(ctx.getSource()))))
                        .then(Commands.literal("deception")
                                /*.then(Commands.literal("spawn")
                                        .then(Commands.argument("spells", ResourceArgument.resource(context, SBSpells.SPELL_TYPE_REGISTRY_KEY))
                                                .executes(ctx -> spawnPuzzleDungeon(ctx.getSource(),
                                                        ResourceArgument.getResource(ctx, "spells", SBSpells.SPELL_TYPE_REGISTRY_KEY))))
                                )*/
                                .then(Commands.literal("bounds")
                                        .executes(ctx -> toggleDungeonBounds(ctx.getSource())))));
    }

    private int spawnPortal(CommandSourceStack source) {
        if (!source.isPlayer()) return 0;
        ServerPlayer player = source.getPlayer();
        ServerLevel level = source.getLevel();

        if (ArenaSavedData.isArena(level)) {
            source.sendFailure(Component.literal("Cannot spawn portal inside an arena"));
            return 0;
        }

        BlockPos playerPos = player.blockPosition();
        BlockPos centerPos = playerPos.offset(2, 0, 2);

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos pos = centerPos.offset(x, 0, z);
                boolean isCorner = (Math.abs(x) == 2 && Math.abs(z) == 2);
                boolean isEdge = (Math.abs(x) == 2 || Math.abs(z) == 2) && !isCorner;
                boolean isCenter = x == 0 && z == 0;

                if (isEdge) {
                    level.setBlock(pos, SBBlocks.SUMMON_STONE.get().defaultBlockState()
                            .setValue(SummonStoneBlock.POWERED, true), 3);
                } else if (isCenter) {
                    level.setBlock(pos, SBBlocks.WILD_MUSHROOM_SUMMON_STONE.get().defaultBlockState(), 3);
                }
            }
        }

        source.sendSuccess(() -> Component.literal("Portal structure spawned. Use Magic Essence on the center stone to activate."), true);
        return 1;
    }

    private int removeArena(CommandSourceStack source) {
        if (!source.isPlayer()) return 0;
        ServerPlayer player = source.getPlayer();
        ServerLevel level = source.getLevel();

        if (ArenaSavedData.isArena(level)) {
            ArenaSavedData arenaData = ArenaSavedData.get(level);
            arenaData.destroyDimension(level);
            source.sendSuccess(() -> Component.literal("Arena destroyed and portal removed"), true);
            return 1;
        }

        BlockPos playerPos = player.blockPosition();
        int radius = 10;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    if (level.getBlockState(pos).is(SBBlocks.SUMMON_PORTAL.get())) {
                        BlockEntity be = level.getBlockEntity(pos);
                        if (be instanceof SummonPortalBlockEntity summonBE) {
                            int arenaId = summonBE.getArenaID();
                            ArenaSavedData data = ArenaSavedData.get(level);
                            ResourceKey<Level> levelKey = data.getOrCreateKey(level.getServer(), arenaId);
                            ServerLevel arena = level.getServer().getLevel(levelKey);
                            if (arena != null) {
                                ArenaSavedData arenaData = ArenaSavedData.get(arena);
                                arenaData.destroyDimension(arena);
                                source.sendSuccess(() -> Component.literal("Found and destroyed arena " + arenaId), true);
                                return 1;
                            }
                        }
                    }
                }
            }
        }

        source.sendFailure(Component.literal("No portal found nearby"));
        return 0;
    }

    private int resetAll(CommandSourceStack source) {
        if (!source.isPlayer()) return 0;
        ServerPlayer player = source.getPlayer();
        ServerLevel level = source.getLevel();

        if (ArenaSavedData.isArena(level)) {
            source.sendFailure(Component.literal("Leave the arena first"));
            return 0;
        }

        BlockPos playerPos = player.blockPosition();
        int radius = 10;
        int removed = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    var state = level.getBlockState(pos);
                    if (state.is(SBBlocks.SUMMON_PORTAL.get()) || state.is(SBBlocks.SUMMON_STONE.get()) || state.is(SBBlocks.WILD_MUSHROOM_SUMMON_STONE.get())) {
                        if (state.is(SBBlocks.SUMMON_PORTAL.get())) {
                            BlockEntity be = level.getBlockEntity(pos);
                            if (be instanceof SummonPortalBlockEntity summonBE) {
                                int arenaId = summonBE.getArenaID();
                                ArenaSavedData data = ArenaSavedData.get(level);
                                ResourceKey<Level> levelKey = data.getOrCreateKey(level.getServer(), arenaId);
                                ServerLevel arena = level.getServer().getLevel(levelKey);
                                if (arena != null) {
                                    DimensionCreator.get().markDimensionForUnregistration(level.getServer(), arena.dimension());
                                }
                            }
                        }
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        removed++;
                    }
                }
            }
        }

        int finalRemoved = removed;
        source.sendSuccess(() -> Component.literal("Reset complete. Removed " + finalRemoved + " blocks and cleared arena data."), true);
        return 1;
    }

    private static boolean arenaBoundsEnabled = false;

    private int toggleArenaBounds(CommandSourceStack source) {
        if (!source.isPlayer()) return 0;
        ServerPlayer player = source.getPlayer();
        ServerLevel level = source.getLevel();

        arenaBoundsEnabled = !arenaBoundsEnabled;

        if (!arenaBoundsEnabled) {
            PayloadHandler.sendDimensionDebugDisable(player);
            source.sendSuccess(() -> Component.literal("Arena bounds rendering disabled"), true);
            return 1;
        }

        if (!SpellDimensionData.isStatic(level)) {
            PayloadHandler.sendDimensionDebugDisable(player);
            source.sendFailure(Component.literal("Not in a static dimension"));
            arenaBoundsEnabled = false;
            return 0;
        }

        ArenaSavedData data = ArenaSavedData.get(level);
        BoundingBox bounds = data.getStructureBounds();

        if (bounds == null) {
            PayloadHandler.sendDimensionDebugDisable(player);
            source.sendFailure(Component.literal("Structure bounds not available"));
            arenaBoundsEnabled = false;
            return 0;
        }

        var bossFight = data.getCurrentBossFight();
        Vec3 spawnOffset = Vec3.ZERO;
        if (bossFight != null) {
            var spawnData = bossFight.getBossFight().getSpawnData();
            spawnOffset = spawnData.playerOffset();
        }

        BlockPos origin = DynamicDimensionFactory.ORIGIN;
        BlockPos spawnPos = origin.offset((int) spawnOffset.x, (int) spawnOffset.y, (int) spawnOffset.z);

        PayloadHandler.sendDimensionDebug(player, true, bounds, spawnPos, origin);
        source.sendSuccess(() -> Component.literal("Arena bounds rendering enabled. Red=bounds, Green=spawn, Blue=origin, Purple=center"), true);
        return 1;
    }

    private static boolean dungeonBoundsEnabled = false;

    private int toggleDungeonBounds(CommandSourceStack source) {
        if (!source.isPlayer()) return 0;
        ServerPlayer player = source.getPlayer();
        ServerLevel level = source.getLevel();

        dungeonBoundsEnabled = !dungeonBoundsEnabled;

        if (!dungeonBoundsEnabled) {
            PayloadHandler.sendDimensionDebugDisable(player);
            source.sendSuccess(() -> Component.literal("Dungeon bounds rendering disabled"), true);
            return 1;
        }

        if (!SpellDimensionData.isStatic(level)) {
            PayloadHandler.sendDimensionDebugDisable(player);
            source.sendFailure(Component.literal("Not in a static dimension"));
            dungeonBoundsEnabled = false;
            return 0;
        }

        PuzzleDungeonData data = PuzzleDungeonData.get(level);
        BoundingBox bounds = data.getStructureBounds();

        if (bounds == null) {
            PayloadHandler.sendDimensionDebugDisable(player);
            source.sendFailure(Component.literal("Structure bounds not available"));
            dungeonBoundsEnabled = false;
            return 0;
        }

        var puzzle = data.getCurrentDungeon();
        Vec3 spawnOffset = puzzle != null ? puzzle.spawnData().playerOffset() : Vec3.ZERO;
        BlockPos origin = bounds.getCenter();
        BlockPos spawnPos = origin.offset((int) spawnOffset.x, (int) spawnOffset.y, (int) spawnOffset.z);

        PayloadHandler.sendDimensionDebug(player, true, bounds, spawnPos, origin);
        source.sendSuccess(() -> Component.literal("Dungeon bounds rendering enabled. Red=bounds, Green=spawn, Blue=origin"), true);
        return 1;
    }
}
