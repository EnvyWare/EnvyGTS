package com.envyful.gts.forge.api.player;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.neoforge.player.attribute.ManagedForgeAttribute;
import com.envyful.api.player.attribute.adapter.SelfAttributeAdapter;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.CollectionItem;
import com.envyful.gts.forge.api.GTSDatabase;
import com.envyful.gts.forge.api.Sale;
import com.envyful.gts.forge.api.TradeOffer;
import com.envyful.gts.forge.api.item.TradeItem;
import com.envyful.gts.forge.api.money.InstantPurchaseMoney;
import com.envyful.gts.forge.api.trade.Trade;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GTSAttribute extends ManagedForgeAttribute<EnvyGTSForge> implements SelfAttributeAdapter {

    private List<CollectionItem> collections = new ArrayList<>();

    protected double currentPrice = -1;
    protected double currentMinPrice = -1;
    protected PlayerSettings settings = new PlayerSettings();
    protected String name;

    public GTSAttribute(UUID id) {
        super(id, EnvyGTSForge.getInstance());
    }

    public List<Trade> getOwnedTrades() {
        return EnvyGTSForge.getTradeService().userListings(this.parent);
    }

    public List<CollectionItem> getCollections() {
        return List.copyOf(this.collections);
    }

    public CollectionItem getCollectionItem(UUID offerId) {
        for (var item : this.collections) {
            if (item.offer().id().equals(offerId)) {
                return item;
            }
        }

        return null;
    }

    public void removeCollectionItem(CollectionItem item) {
        this.collections.remove(item);

        EnvyGTSForge.getDSLContext().deleteFrom(GTSDatabase.COLLECTIONS)
                .where(GTSDatabase.COLLECTIONS_OFFER_ID.eq(item.offer().id().toString()))
                .executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }

    public void addCollectionItem(CollectionItem item) {
        this.collections.add(item);
        item.record();
    }

    public boolean hasReachedMaximumTrades() {
        var ownedTrades = this.getOwnedTrades();

        return ownedTrades.size() >= EnvyGTSForge.getConfig().getMaxListingsPerUser();
    }

    public double getCurrentPrice() {
        return this.currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getCurrentMinPrice() {
        return this.currentMinPrice;
    }

    public void setCurrentMinPrice(double currentMinPrice) {
        this.currentMinPrice = currentMinPrice;
    }

    public PlayerSettings getSettings() {
        return this.settings;
    }

    @Override
    public void setParent(ForgeEnvyPlayer parent) {
        super.setParent(parent);

        this.name = parent.getName();
        if (!this.collections.isEmpty()) {
            this.parent.message(EnvyGTSForge.getLocale().getMessages().getItemsToClaim());
        }
    }

    @Override
    public void load() {
        var results = EnvyGTSForge.getDSLContext()
                .select(
                        GTSDatabase.COLLECTIONS_PLAYER,
                        GTSDatabase.TRADES_OFFER_ID,
                        GTSDatabase.SALES_SALE_ID,

                        GTSDatabase.TRADES_SELLER_UUID,
                        GTSDatabase.TRADES_SELLER_NAME,
                        GTSDatabase.TRADES_CREATION_TIME,
                        GTSDatabase.TRADES_EXPIRY_TIME,
                        GTSDatabase.TRADES_PRICE,

                        GTSDatabase.TRADE_ITEMS_TYPE,
                        GTSDatabase.TRADE_ITEMS_DATA,

                        GTSDatabase.SALES_BUYER_UUID,
                        GTSDatabase.SALES_BUYER_NAME,
                        GTSDatabase.SALES_PURCHASE_TIME,
                        GTSDatabase.SALES_PURCHASE_PRICE
                )
                .from(GTSDatabase.COLLECTIONS)
                .join(GTSDatabase.TRADES)
                .on(GTSDatabase.COLLECTIONS_OFFER_ID
                        .eq(GTSDatabase.TRADES_OFFER_ID))
                .leftJoin(GTSDatabase.TRADE_ITEMS)
                .on(GTSDatabase.TRADE_ITEMS_OFFER_ID
                        .eq(GTSDatabase.TRADES_OFFER_ID))
                .leftJoin(GTSDatabase.SALES)
                .on(GTSDatabase.COLLECTIONS_SALE_ID
                        .eq(GTSDatabase.SALES_SALE_ID))
                .where(GTSDatabase.COLLECTIONS_PLAYER.eq(this.id.toString()))
                .fetchArray();

        try {
            for (var result : results) {
                this.collections.add(new CollectionItem(
                        new TradeOffer(
                                UUID.fromString(result.get(GTSDatabase.TRADES_OFFER_ID)),
                                new PlayerInfo(
                                        UUID.fromString(result.get(GTSDatabase.TRADES_SELLER_UUID)),
                                        result.get(GTSDatabase.TRADES_SELLER_NAME)
                                ),
                                Instant.ofEpochMilli(result.get(GTSDatabase.TRADES_CREATION_TIME)),
                                Instant.ofEpochMilli(result.get(GTSDatabase.TRADES_EXPIRY_TIME)),
                                TradeItem.deserialize(result.get(GTSDatabase.TRADE_ITEMS_TYPE), result.get(GTSDatabase.TRADE_ITEMS_DATA)),
                                new InstantPurchaseMoney(result.get(GTSDatabase.TRADES_PRICE))
                        ),
                        result.get(GTSDatabase.SALES_SALE_ID) == null ? null : new Sale(
                                UUID.fromString(result.get(GTSDatabase.SALES_SALE_ID)),
                                UUID.fromString(result.get(GTSDatabase.TRADES_OFFER_ID)),
                                new PlayerInfo(
                                        UUID.fromString(result.get(GTSDatabase.SALES_BUYER_UUID)),
                                        result.get(GTSDatabase.SALES_BUYER_NAME)
                                ),
                                Instant.ofEpochMilli(result.get(GTSDatabase.SALES_PURCHASE_TIME)),
                                result.get(GTSDatabase.SALES_PURCHASE_PRICE)
                        )
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load GTS collections for player " + this.id, e);
        }
    }

    @Override
    public void save() {

    }
}
