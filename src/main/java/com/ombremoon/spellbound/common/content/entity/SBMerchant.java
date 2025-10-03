package com.ombremoon.spellbound.common.content.entity;

import com.google.common.collect.Lists;
import com.mojang.serialization.DataResult;
import com.ombremoon.spellbound.main.Constants;
import com.ombremoon.spellbound.common.content.world.SBTrades;
import com.ombremoon.spellbound.common.events.custom.ModifySpellTradesEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public abstract class SBMerchant extends PathfinderMob implements Merchant, SmartBrainOwner<SBMerchant> {
    @Nullable
    private Player tradingPlayer;
    public MerchantOffers trades = new MerchantOffers();

    protected SBMerchant(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    //Just initiate trading
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && !player.isSecondaryUseActive() && hand == InteractionHand.MAIN_HAND && !this.isTrading()) {
            this.startTrading(player);
        }
        return super.mobInteract(player, hand);
    }

    /**
     * The level of the merchant for deciding trades to get
     * @return the level of the merchant, default 1
     */
    public int getMerchantLevel() {
        return 1;
    }

    /**
     * The number of trades that the villager can have when first created.
     * To add additional trades upon level ups call SBMerchant#addTrades
     * @return number of available trades
     */
    public int numberOfTrades() {
        return 5;
    }

    /**
     * Type of merchant, used to determine trades
     * @return Type of this merchant
     */
    protected abstract SBMerchantType getMerchantType();

    /**
     * Gets all available trades or populates list if none available
     * @return Available trade offers
     */
    @Override
    public MerchantOffers getOffers() {
        if (this.trades == null || this.trades.isEmpty()) {
            this.trades = new MerchantOffers();
            addTrades(numberOfTrades());
        }

        return this.trades;
    }

    /**
     * Adds more trades to the current MerchantOffers for this entity randomly based on traders level
     * @param numberOfTrades the number of trades to add
     */
    private void addTrades(int numberOfTrades) {
        Int2ObjectMap<MerchantOffer[]> map = SBTrades.TRADES.get(getMerchantType());
        if (map != null && !map.isEmpty()) {
            MerchantOffer[] offers = map.get(getMerchantLevel());
            if (offers != null) {
                List<MerchantOffer> availableOffers = Lists.newArrayList(offers);
                int maxTrades = Math.min(numberOfTrades, availableOffers.size());
                for (int i = 0; i < maxTrades; i++) {
                    MerchantOffer newTrade = availableOffers.remove(this.random.nextInt(availableOffers.size()));
                    if (newTrade != null) {
                        this.trades.add(newTrade);
                    }
                }
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        postTradesEvent();
        if (!this.level().isClientSide) {
            MerchantOffers merchantoffers = this.getOffers();
            if (!merchantoffers.isEmpty()) {
                compound.put("Offers", (Tag)MerchantOffers.CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), merchantoffers).getOrThrow());
            }
        }
    }

    /**
     * Sends the SpellBoundTradesEvent to any event subscribers and updates list of possible trades
     */
    private void postTradesEvent() {
        Int2ObjectMap<MerchantOffer[]> trades = SBTrades.TRADES.getOrDefault(getMerchantType(), new Int2ObjectOpenHashMap<>());
        Int2ObjectMap<List<MerchantOffer>> mutableTrades = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i < 6; i++) {
            mutableTrades.put(i, NonNullList.create());
        }
        trades.int2ObjectEntrySet().forEach(e -> {
            Arrays.stream(e.getValue()).forEach(mutableTrades.get(e.getIntKey())::add);
        });
        NeoForge.EVENT_BUS.post(new ModifySpellTradesEvent(getMerchantType(), mutableTrades));
        Int2ObjectMap<MerchantOffer[]> newTrades = new Int2ObjectOpenHashMap<>();
        mutableTrades.int2ObjectEntrySet().forEach(e -> newTrades.put(e.getIntKey(), e.getValue().toArray(new MerchantOffer[0])));
        SBTrades.TRADES.put(getMerchantType(), newTrades);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Offers")) {
            DataResult var10000 = MerchantOffers.CODEC.parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), compound.get("Offers"));
            Logger var10002 = Constants.LOG;
            Objects.requireNonNull(var10002);
            var10000.resultOrPartial(Util.prefix("Failed to load offers: ", var10002::warn)).ifPresent((p_323775_) -> {
                this.trades = (MerchantOffers) p_323775_;
            });
        }
    }

    /**
     * Check for if the merchant is currently trading with a player
     * @return true if actively trading, false otherwise
     */
    private boolean isTrading() {
        return tradingPlayer != null;
    }

    /**
     * Opens the trading menu for a given player and sets them as the current user trading with the merchant
     * @param player the player to trade with
     */
    private void startTrading(Player player) {
        this.setTradingPlayer(player);
        this.openTradingScreen(player, this.getDisplayName(), 1);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        this.setTradingPlayer(null);
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        this.tradingPlayer = player;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    /**
     * Used for syncing serverside offers to clientside
     * @param merchantOffers serverside offers
     */
    @Override
    public void overrideOffers(MerchantOffers merchantOffers) {
        this.trades = merchantOffers;
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int i) {
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide;
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        tickBrain(this);
    }
}
