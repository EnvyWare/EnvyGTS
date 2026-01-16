package com.envyful.gts.forge.api.service;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.Sale;
import com.envyful.gts.forge.api.TradeOffer;
import com.envyful.gts.forge.api.item.TradeItem;
import com.envyful.gts.forge.api.money.InstantPurchaseMoney;
import com.envyful.gts.forge.api.player.PlayerInfo;
import com.envyful.gts.forge.api.trade.ActiveTrade;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.api.GTSDatabase;

import java.time.Instant;
import java.util.UUID;

public class jOOQTradeService extends CachedTradeService {

    public jOOQTradeService() {
        super();

        var records = EnvyGTSForge.getDSLContext()
                .select(
                        // Trades
                        GTSDatabase.TRADES_OFFER_ID,
                        GTSDatabase.TRADES_SELLER_UUID,
                        GTSDatabase.TRADES_SELLER_NAME,
                        GTSDatabase.TRADES_CREATION_TIME,
                        GTSDatabase.TRADES_EXPIRY_TIME,
                        GTSDatabase.TRADES_PRICE,

                        // Trade items
                        GTSDatabase.TRADE_ITEMS_TYPE,
                        GTSDatabase.TRADE_ITEMS_DATA
                )
                .from(GTSDatabase.TRADES)
                .join(GTSDatabase.TRADE_ITEMS)
                .on(GTSDatabase.TRADE_ITEMS_OFFER_ID
                        .eq(GTSDatabase.TRADES_OFFER_ID))
                .andNotExists(
                        EnvyGTSForge.getDSLContext().selectOne()
                                .from(GTSDatabase.TRADE_OUTCOMES)
                                .where(GTSDatabase.TRADE_OUTCOMES_OFFER_ID
                                        .eq(GTSDatabase.TRADES_OFFER_ID))
                )
                .fetch();

        try {
            for (var record : records) {
                var tradeId = UUID.fromString(record.get(GTSDatabase.TRADES_OFFER_ID));
                this.activeListings.put(tradeId, new ActiveTrade(
                        new TradeOffer(
                                tradeId,
                                new PlayerInfo(
                                        UUID.fromString(record.get(GTSDatabase.TRADES_SELLER_UUID)),
                                        record.get(GTSDatabase.TRADES_SELLER_NAME)
                                ),
                                Instant.ofEpochMilli(record.get(GTSDatabase.TRADES_CREATION_TIME)),
                                Instant.ofEpochMilli(record.get(GTSDatabase.TRADES_EXPIRY_TIME)),
                                TradeItem.deserialize(
                                        record.get(GTSDatabase.TRADE_ITEMS_TYPE),
                                        record.get(GTSDatabase.TRADE_ITEMS_DATA)
                                ),
                                new InstantPurchaseMoney(record.get(GTSDatabase.TRADES_PRICE))
                        )
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load active trades from database", e);
        }
    }

    @Override
    public void addListing(Trade trade) {
        super.addListing(trade);

        UtilConcurrency.runAsync(() -> {
            EnvyGTSForge.getDSLContext()
                    .insertInto(GTSDatabase.TRADES)
                    .set(GTSDatabase.TRADES_OFFER_ID, trade.offer().id().toString())
                    .set(GTSDatabase.TRADES_SELLER_UUID, trade.offer().seller().uniqueId().toString())
                    .set(GTSDatabase.TRADES_SELLER_NAME, trade.offer().seller().name())
                    .set(GTSDatabase.TRADES_CREATION_TIME, trade.offer().creationTime().toEpochMilli())
                    .set(GTSDatabase.TRADES_EXPIRY_TIME, trade.offer().expiryTime().toEpochMilli())
                    .set(GTSDatabase.TRADES_PRICE, trade.offer().price().getPrice())
                    .execute();

            EnvyGTSForge.getDSLContext()
                    .insertInto(GTSDatabase.TRADE_ITEMS)
                    .set(GTSDatabase.TRADE_ITEMS_OFFER_ID, trade.offer().id().toString())
                    .set(GTSDatabase.TRADE_ITEMS_TYPE, trade.offer().item().id())
                    .set(GTSDatabase.TRADE_ITEMS_DATA, trade.offer().item().serialize())
                    .execute();
        });
    }

    @Override
    protected void onTradeExpire(Trade trade) {
        super.onTradeExpire(trade);

        EnvyGTSForge.getDSLContext()
                .insertInto(GTSDatabase.TRADE_OUTCOMES)
                .set(GTSDatabase.TRADES_OFFER_ID, trade.offer().id().toString())
                .set(GTSDatabase.TRADE_OUTCOMES_TYPE, "EXPIRED")
                .set(GTSDatabase.TRADE_OUTCOMES_TIME, System.currentTimeMillis())
                .executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }

    @Override
    public void addSale(Sale sale) {
        super.addSale(sale);

        EnvyGTSForge.getDSLContext()
                .insertInto(GTSDatabase.SALES)
                .set(GTSDatabase.SALES_SALE_ID, sale.saleId().toString())
                .set(GTSDatabase.TRADES_OFFER_ID, sale.offerId().toString())
                .set(GTSDatabase.SALES_BUYER_UUID, sale.buyer().uniqueId().toString())
                .set(GTSDatabase.SALES_BUYER_NAME, sale.buyer().name())
                .set(GTSDatabase.SALES_PURCHASE_TIME, sale.time().toEpochMilli())
                .set(GTSDatabase.SALES_PURCHASE_PRICE, sale.purchasePrice())
                .executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);

        EnvyGTSForge.getDSLContext()
                .insertInto(GTSDatabase.TRADE_OUTCOMES)
                .set(GTSDatabase.TRADES_OFFER_ID, sale.offerId().toString())
                .set(GTSDatabase.TRADE_OUTCOMES_TYPE, "SOLD")
                .set(GTSDatabase.TRADE_OUTCOMES_TIME, System.currentTimeMillis())
                .executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }

    @Override
    public void adminRemoveListing(Trade trade) {
        super.adminRemoveListing(trade);

        EnvyGTSForge.getDSLContext()
                .insertInto(GTSDatabase.TRADE_OUTCOMES)
                .set(GTSDatabase.TRADES_OFFER_ID, trade.offer().id().toString())
                .set(GTSDatabase.TRADE_OUTCOMES_TYPE, "ADMIN_REMOVED")
                .set(GTSDatabase.TRADE_OUTCOMES_TIME, System.currentTimeMillis())
                .executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }

    @Override
    public void ownerRemoveListing(Trade trade) {
        super.ownerRemoveListing(trade);

        EnvyGTSForge.getDSLContext()
                .insertInto(GTSDatabase.TRADE_OUTCOMES)
                .set(GTSDatabase.TRADES_OFFER_ID, trade.offer().id().toString())
                .set(GTSDatabase.TRADE_OUTCOMES_TYPE, "OWNER_REMOVED")
                .set(GTSDatabase.TRADE_OUTCOMES_TIME, System.currentTimeMillis())
                .executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }
}
