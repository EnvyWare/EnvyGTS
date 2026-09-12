package com.envyful.gts.forge.api.service;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.RemovalInfo;
import com.envyful.gts.forge.api.Sale;
import com.envyful.gts.forge.api.TradeOffer;
import com.envyful.gts.forge.api.item.TradeItem;
import com.envyful.gts.forge.api.money.InstantPurchaseMoney;
import com.envyful.gts.forge.api.player.PlayerInfo;
import com.envyful.gts.forge.api.trade.ActiveTrade;
import com.envyful.gts.forge.api.trade.ExpiredTrade;
import com.envyful.gts.forge.api.trade.RemovedTrade;
import com.envyful.gts.forge.api.trade.SoldTrade;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.api.trade.TradeHistory;
import com.envyful.gts.forge.api.trade.TradeHistoryItemType;
import com.envyful.gts.forge.api.GTSDatabase;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.DSL;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class jOOQTradeService extends CachedTradeService {

    private static final PlayerInfo UNKNOWN_PLAYER = new PlayerInfo(new UUID(0L, 0L), "Unknown");

    private static final int MAX_HISTORY_RESULTS = 1000;

    private static final List<SelectFieldOrAsterisk> HISTORY_FIELDS = List.of(
            GTSDatabase.TRADES_OFFER_ID,
            GTSDatabase.TRADES_SELLER_UUID,
            GTSDatabase.TRADES_SELLER_NAME,
            GTSDatabase.TRADES_CREATION_TIME,
            GTSDatabase.TRADES_EXPIRY_TIME,
            GTSDatabase.TRADES_PRICE,
            GTSDatabase.TRADE_ITEMS_TYPE,
            GTSDatabase.TRADE_ITEMS_DATA,
            GTSDatabase.TRADE_OUTCOMES_TYPE,
            GTSDatabase.TRADE_OUTCOMES_TIME,
            GTSDatabase.SALES_SALE_ID,
            GTSDatabase.SALES_OFFER_ID,
            GTSDatabase.SALES_BUYER_UUID,
            GTSDatabase.SALES_BUYER_NAME,
            GTSDatabase.SALES_PURCHASE_TIME,
            GTSDatabase.SALES_PURCHASE_PRICE
    );

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
    public TradeHistory historicalListings() {
        return this.deserializeHistory(this.historyQuery(DSL.noCondition())
                .orderBy(GTSDatabase.TRADE_OUTCOMES_TIME.desc())
                .maxRows(MAX_HISTORY_RESULTS)
                .fetch());
    }

    @Override
    public TradeHistory historicalListings(ForgeEnvyPlayer player) {
        return this.historicalListings(player.getUniqueId().toString());
    }

    @Override
    public TradeHistory historicalListings(String playerQuery) {
        var exactMatch = this.involvedListings(playerQuery, false);

        return exactMatch.trades().isEmpty() ? this.involvedListings(playerQuery, true) : exactMatch;
    }

    private TradeHistory involvedListings(String playerQuery, boolean ignoreNameCase) {
        var uniqueId = playerQuery.toLowerCase(Locale.ROOT);

        var seller = GTSDatabase.TRADES_SELLER_UUID.eq(uniqueId)
                .or(ignoreNameCase ?
                        GTSDatabase.TRADES_SELLER_NAME.equalIgnoreCase(playerQuery) :
                        GTSDatabase.TRADES_SELLER_NAME.eq(playerQuery));

        var buyer = GTSDatabase.SALES_BUYER_UUID.eq(uniqueId)
                .or(ignoreNameCase ?
                        GTSDatabase.SALES_BUYER_NAME.equalIgnoreCase(playerQuery) :
                        GTSDatabase.SALES_BUYER_NAME.eq(playerQuery));

        return this.deserializeHistory(this.historyQuery(seller)
                .union(this.historyQuery(buyer))
                .orderBy(GTSDatabase.TRADE_OUTCOMES_TIME.desc())
                .maxRows(MAX_HISTORY_RESULTS)
                .fetch());
    }

    @Override
    public TradeHistory highestPrices(Instant since, TradeHistoryItemType itemType) {
        var condition = GTSDatabase.TRADE_OUTCOMES_TYPE.eq("SOLD")
                .and(GTSDatabase.SALES_SALE_ID.isNotNull())
                .and(GTSDatabase.SALES_PURCHASE_TIME.ge(since.toEpochMilli()));

        var tradeItemId = itemType.getTradeItemId();

        if (tradeItemId.isPresent()) {
            condition = condition.and(GTSDatabase.TRADE_ITEMS_TYPE.eq(tradeItemId.get()));
        }

        return this.deserializeHistory(this.historyQuery(condition)
                .orderBy(GTSDatabase.SALES_PURCHASE_PRICE.desc(), GTSDatabase.SALES_PURCHASE_TIME.desc())
                .maxRows(MAX_HISTORY_RESULTS)
                .fetch());
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

    private SelectConditionStep<Record> historyQuery(Condition condition) {
        return EnvyGTSForge.getDSLContext()
                .select(HISTORY_FIELDS)
                .from(GTSDatabase.TRADES)
                .join(GTSDatabase.TRADE_ITEMS)
                .on(GTSDatabase.TRADE_ITEMS_OFFER_ID.eq(GTSDatabase.TRADES_OFFER_ID))
                .join(GTSDatabase.TRADE_OUTCOMES)
                .on(GTSDatabase.TRADE_OUTCOMES_OFFER_ID.eq(GTSDatabase.TRADES_OFFER_ID))
                .leftJoin(GTSDatabase.SALES)
                .on(GTSDatabase.SALES_OFFER_ID.eq(GTSDatabase.TRADES_OFFER_ID))
                .where(condition);
    }

    private TradeHistory deserializeHistory(Result<Record> records) {
        var history = new ArrayList<Trade>();
        var failed = 0;

        for (var record : records) {
            try {
                history.add(this.deserializeHistoricalTrade(record));
            } catch (Exception e) {
                ++failed;
                EnvyGTSForge.getLogger().error("Failed to read historical GTS trade {} from the database",
                        record.get(GTSDatabase.TRADES_OFFER_ID), e);
            }
        }

        if (failed > 0) {
            EnvyGTSForge.getLogger().error("{} of the {} historical GTS trades read could not be deserialized and have been omitted",
                    failed, records.size());
        }

        return new TradeHistory(List.copyOf(history), failed);
    }

    private Trade deserializeHistoricalTrade(Record record) throws CommandSyntaxException {
        var tradeId = UUID.fromString(record.get(GTSDatabase.TRADES_OFFER_ID));
        var offer = new TradeOffer(
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
        );

        var outcomeType = record.get(GTSDatabase.TRADE_OUTCOMES_TYPE);
        var outcomeTime = Instant.ofEpochMilli(record.get(GTSDatabase.TRADE_OUTCOMES_TIME));
        var sale = this.deserializeSale(record);

        return switch (outcomeType.toUpperCase(Locale.ROOT)) {
            case "SOLD" -> sale == null ?
                    new RemovedTrade(offer, new RemovalInfo(UNKNOWN_PLAYER, outcomeTime, "SOLD")) :
                    new SoldTrade(offer, sale);
            case "EXPIRED" -> new ExpiredTrade(offer, outcomeTime);
            default -> new RemovedTrade(offer, new RemovalInfo(UNKNOWN_PLAYER, outcomeTime, outcomeType));
        };
    }

    private Sale deserializeSale(Record record) {
        var saleId = record.get(GTSDatabase.SALES_SALE_ID);

        if (saleId == null) {
            return null;
        }

        return new Sale(
                UUID.fromString(saleId),
                UUID.fromString(record.get(GTSDatabase.SALES_OFFER_ID)),
                new PlayerInfo(
                        UUID.fromString(record.get(GTSDatabase.SALES_BUYER_UUID)),
                        record.get(GTSDatabase.SALES_BUYER_NAME)
                ),
                Instant.ofEpochMilli(record.get(GTSDatabase.SALES_PURCHASE_TIME)),
                record.get(GTSDatabase.SALES_PURCHASE_PRICE)
        );
    }
}
