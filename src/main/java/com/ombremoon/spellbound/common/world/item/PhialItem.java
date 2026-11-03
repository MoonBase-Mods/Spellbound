package com.ombremoon.spellbound.common.world.item;

import com.ombremoon.spellbound.common.init.SBItems;
import com.ombremoon.spellbound.common.world.entity.projectile.ThrownPhial;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.stats.Stats;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.util.Color;

import java.util.function.IntFunction;
import java.util.function.Supplier;

public class PhialItem extends Item implements ProjectileItem {
    private final PhialType type;

    public PhialItem(PhialType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public PhialType getType() {
        return this.type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            ThrownPhial phial = new ThrownPhial(player, this.type, level);
            phial.setItem(itemStack);
            phial.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            level.addFreshEntity(phial);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, player);
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        ThrownPhial phial = new ThrownPhial(pos.x(), pos.y(), pos.z(), this.type, level);
        phial.setItem(stack);
        return phial;
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder()
                .uncertainty(ProjectileItem.DispenseConfig.DEFAULT.uncertainty() * 0.5F)
                .power(ProjectileItem.DispenseConfig.DEFAULT.power() * 1.25F)
                .build();
    }

    public enum PhialType {
        DIVINE(0, SBItems.DIVINE_PHIAL, Color.YELLOW.argbInt()),
        CURSED(1, SBItems.CURSED_PHIAL, Color.MAGENTA.darker(2.5F).argbInt()),
        ECLIPSE(2, SBItems.TEAR_OF_THE_ECLIPSE, Color.BLACK.argbInt(), Color.WHITE.argbInt());

        public static final IntFunction<PhialType> BY_ID = ByIdMap.continuous(PhialType::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, PhialType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, PhialType::id);
        private final int id;
        private final Supplier<Item> item;
        private final int[] colors;

        PhialType(int id, Supplier<Item> item, int... colors) {
            this.id = id;
            this.item = item;
            this.colors = colors;
        }

        public int id() {
            return this.id;
        }

        public Supplier<Item> getItem() {
            return this.item;
        }

        public int[] getColors() {
            return this.colors;
        }
    }
}
