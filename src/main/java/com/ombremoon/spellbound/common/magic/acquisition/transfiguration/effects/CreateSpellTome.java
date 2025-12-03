package com.ombremoon.spellbound.common.magic.acquisition.transfiguration.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ombremoon.spellbound.common.world.item.SpellTomeItem;
import com.ombremoon.spellbound.common.world.multiblock.Multiblock;
import com.ombremoon.spellbound.common.world.multiblock.type.TransfigurationMultiblock;
import com.ombremoon.spellbound.common.init.SBSpells;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualEffect;
import com.ombremoon.spellbound.common.magic.acquisition.transfiguration.RitualHelper;
import com.ombremoon.spellbound.common.magic.api.SpellType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public record CreateSpellTome(SpellType<?> spell, int tier) implements RitualEffect {
    public static final MapCodec<CreateSpellTome> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    SBSpells.REGISTRY.byNameCodec().fieldOf("spell").forGetter(CreateSpellTome::spell),
                    ExtraCodecs.intRange(1, 3).fieldOf("tier").forGetter(CreateSpellTome::tier)
            ).apply(instance, CreateSpellTome::new)
    );

    @Override
    public void onActivated(ServerLevel level, int tier, Player player, BlockPos centerPos, Multiblock.MultiblockPattern pattern) {
        ItemStack spellTome = SpellTomeItem.createWithSpell(this.spell);
        Vec3 pos = centerPos.getBottomCenter();
        RitualHelper.createItem(level, pos, spellTome);
    }

    @Override
    public boolean isValid(TransfigurationMultiblock multiblock) {
        return this.tier == multiblock.getRings();
    }

    @Override
    public MapCodec<? extends RitualEffect> codec() {
        return CODEC;
    }
}
